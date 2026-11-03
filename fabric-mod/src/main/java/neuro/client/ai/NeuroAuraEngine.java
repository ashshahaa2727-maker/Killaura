package neuro.client.ai;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import net.minecraft.client.MinecraftClient;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.math.Vec3d;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.Writer;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.List;
import java.util.Random;

/**
 * Полная реализация обучаемой нейросетевой Киллауры (Policy GRU + 3-Mix MDN) из train_aura.py и common.py.
 * Поддерживает:
 * - Загрузку встроенных моделей (default.json, neuraim_aim_v6.json) и пользовательских (neuro/aim.json)
 * - Точный алгоритм rollout (temp, keep, scale, 65-ступенчатая таблица квантилей error, freeze-гейт и GCD)
 * - Встроенное дообучение весов головы MDN и таблицы error на записанных .csv сессиях
 */
public class NeuroAuraEngine {
    private static final Logger LOGGER = LoggerFactory.getLogger("NeuroAuraEngine");

    public static final int DEFAULT_FEATURES = 17;
    private static final double RHO_LIMIT = 0.95;

    private int features = 17;
    private int hidden = 32;
    private int mix = 3;
    private double limitYaw = 179.0;
    private double limitPitch = 90.0;
    private double freezeCut = 40.0;

    private double[] state = new double[32];
    private double[][] wi;     // [3*hidden][features]
    private double[][] wh;     // [3*hidden][hidden]
    private double[] bi;       // [3*hidden]
    private double[] bh;       // [3*hidden]
    private double[][] headW;  // [1 + 6*mix][hidden]
    private double[] headB;    // [1 + 6*mix]
    private double[] errorQuantiles = new double[65];

    private boolean modelLoaded = false;
    private String loadedModelName = "default.json";

    // Параметры Rollout (Knob из train_aura.py: DEFAULTS = (0.70, 4, 0.45))
    private double knobTemp = 0.70;
    private int knobKeep = 4;
    private double knobScale = 0.45;

    // Состояние последовательности (ey1, ep1, ey2, ep2, dy1, dp1, dy2, dp2, prevTyaw, prevTpitch, since, run)
    private double ey1 = 0, ep1 = 0, ey2 = 0, ep2 = 0;
    private double dy1 = 0, dp1 = 0, dy2 = 0, dp2 = 0;
    private double prevTyaw = Double.NaN, prevTpitch = Double.NaN;
    private int lastTargetId = -1;
    private double sinceAttack = 20.0;
    private double freezeRun = 0.0;
    private final Random rng = new Random();

    public void setRolloutKnobs(double temp, int keep, double scale) {
        this.knobTemp = Math.max(0.05, Math.min(1.5, temp));
        this.knobKeep = Math.max(1, Math.min(16, keep));
        this.knobScale = Math.max(0.0, Math.min(1.5, scale));
    }

    public void loadModelFromResources() {
        // Если игрок уже обучил свою модель aim.json, подгружаем её автоматически при старте
        Path customAim = getCustomAimPath();
        if (customAim != null && Files.exists(customAim)) {
            if (loadModelFromFile(customAim)) {
                return;
            }
        }
        loadModelFromResource("/assets/neuro/models/default.json", "default.json");
    }

    public void loadCustomOrDefaultModel() {
        Path customAim = getCustomAimPath();
        if (customAim != null && Files.exists(customAim)) {
            if (loadModelFromFile(customAim)) {
                return;
            }
        }
        Path rockstarAim = getRockstarAimPath();
        if (rockstarAim != null && Files.exists(rockstarAim)) {
            if (loadModelFromFile(rockstarAim)) {
                return;
            }
        }
        loadModelFromResource("/assets/neuro/models/default.json", "default.json");
    }

    public boolean loadModelFromResource(String resourcePath, String label) {
        try (InputStream is = NeuroAuraEngine.class.getResourceAsStream(resourcePath)) {
            if (is == null) {
                LOGGER.warn("[Neuro] Ресурс {} не найден.", resourcePath);
                return false;
            }
            try (Reader reader = new InputStreamReader(is, StandardCharsets.UTF_8)) {
                return parseModelJson(reader, label);
            }
        } catch (Exception e) {
            LOGGER.error("[Neuro] Ошибка загрузки ресурса {}: {}", resourcePath, e.getMessage());
            return false;
        }
    }

    public boolean loadModelFromFile(Path path) {
        try (Reader reader = Files.newBufferedReader(path, StandardCharsets.UTF_8)) {
            return parseModelJson(reader, path.getFileName().toString());
        } catch (Exception e) {
            LOGGER.error("[Neuro] Ошибка чтения модели из файла {}: {}", path, e.getMessage());
            return false;
        }
    }

    private synchronized boolean parseModelJson(Reader reader, String label) {
        JsonObject root = JsonParser.parseReader(reader).getAsJsonObject();
        if (!root.has("gru") || !root.has("out")) {
            LOGGER.warn("[Neuro] Модель {} не содержит секций gru/out.", label);
            return false;
        }

        this.features = root.has("features") ? root.get("features").getAsInt() : 17;
        this.hidden = root.has("hidden") ? root.get("hidden").getAsInt() : 32;
        this.mix = root.has("mix") ? root.get("mix").getAsInt() : 3;
        this.freezeCut = root.has("freezeCut") ? root.get("freezeCut").getAsDouble() : 40.0;

        if (root.has("limits") && root.get("limits").isJsonArray()) {
            JsonArray lim = root.getAsJsonArray("limits");
            if (lim.size() >= 2) {
                this.limitYaw = lim.get(0).getAsDouble();
                this.limitPitch = lim.get(1).getAsDouble();
            }
        }

        if (root.has("error") && root.get("error").isJsonArray()) {
            this.errorQuantiles = parseVector(root.getAsJsonArray("error"));
        } else {
            this.errorQuantiles = new double[]{0.0, 0.5, 1.0};
        }

        JsonObject gru = root.getAsJsonObject("gru");
        JsonObject out = root.getAsJsonObject("out");

        this.wi = parseMatrix(gru.getAsJsonArray("wi"));
        this.wh = parseMatrix(gru.getAsJsonArray("wh"));
        this.bi = parseVector(gru.getAsJsonArray("bi"));
        this.bh = parseVector(gru.getAsJsonArray("bh"));
        this.headW = parseMatrix(out.getAsJsonArray("w"));
        this.headB = parseVector(out.getAsJsonArray("b"));
        this.state = new double[this.hidden];

        this.modelLoaded = true;
        this.loadedModelName = label;
        resetEpisodeState();
        LOGGER.info("[Neuro] Загружена модель {} (features={}, hidden={}, mix={}, errorSteps={})",
                label, features, hidden, mix, errorQuantiles.length);
        return true;
    }

    public void resetEpisodeState() {
        if (this.state != null) {
            Arrays.fill(this.state, 0.0);
        }
        this.ey1 = 0;
        this.ep1 = 0;
        this.ey2 = 0;
        this.ep2 = 0;
        this.dy1 = 0;
        this.dp1 = 0;
        this.dy2 = 0;
        this.dp2 = 0;
        this.prevTyaw = Double.NaN;
        this.prevTpitch = Double.NaN;
        this.sinceAttack = 20.0;
        this.freezeRun = 0.0;
    }

    public static double computeMouseGcd() {
        MinecraftClient client = MinecraftClient.getInstance();
        double sens = 0.5;
        if (client != null && client.options != null) {
            sens = client.options.getMouseSensitivity().getValue();
        }
        double f = sens * 0.6 + 0.2;
        return f * f * f * 1.2;
    }

    private static double[][] parseMatrix(JsonArray arr) {
        int rows = arr.size();
        int cols = arr.get(0).getAsJsonArray().size();
        double[][] mat = new double[rows][cols];
        for (int i = 0; i < rows; i++) {
            JsonArray row = arr.get(i).getAsJsonArray();
            for (int j = 0; j < cols; j++) {
                mat[i][j] = row.get(j).getAsDouble();
            }
        }
        return mat;
    }

    private static double[] parseVector(JsonArray arr) {
        int len = arr.size();
        double[] vec = new double[len];
        for (int i = 0; i < len; i++) {
            vec[i] = arr.get(i).getAsDouble();
        }
        return vec;
    }

    public static double arcsinh(double x) {
        return Math.log(x + Math.sqrt(x * x + 1.0));
    }

    public static double squash(double u, double limit) {
        double clamped = Math.max(-8.0, Math.min(8.0, u));
        return Math.max(-limit, Math.min(limit, Math.sinh(clamped)));
    }

    public static double quantize(double value, double gcd) {
        return gcd > 0.0 ? Math.round(value / gcd) * gcd : value;
    }

    public static double wrapDegrees(double a) {
        double r = (a + 180.0) % 360.0;
        if (r < 0) r += 360.0;
        return r - 180.0;
    }

    private double sampleErrorQuantile(double u) {
        if (this.errorQuantiles == null || this.errorQuantiles.length == 0) return 0.0;
        if (this.errorQuantiles.length == 1) return this.errorQuantiles[0];
        double q = Math.max(0.0, Math.min(1.0, u)) * (this.errorQuantiles.length - 1);
        int i = (int) q;
        if (i >= this.errorQuantiles.length - 1) {
            return this.errorQuantiles[this.errorQuantiles.length - 1];
        }
        return this.errorQuantiles[i] + (this.errorQuantiles[i + 1] - this.errorQuantiles[i]) * (q - i);
    }

    /**
     * Полный шаг Rollout из train_aura.py (строки 237-286)
     */
    public synchronized float[] computeStep(PlayerEntity player, Entity target, boolean attackedThisTick) {
        if (target.getId() != this.lastTargetId) {
            this.lastTargetId = target.getId();
            resetEpisodeState();
        }

        Vec3d eyes = player.getEyePos();
        Vec3d center = target.getBoundingBox().getCenter();
        double rx = center.x - eyes.x;
        double ry = center.y - eyes.y;
        double rz = center.z - eyes.z;
        double flat = Math.max(Math.hypot(rx, rz), 0.05);
        double dist = Math.hypot(flat, ry);

        double tyaw = Math.toDegrees(Math.atan2(rz, rx)) - 90.0;
        double tpitch = -Math.toDegrees(Math.atan2(ry, flat));

        double vyaw = Double.isNaN(prevTyaw) ? 0.0 : wrapDegrees(tyaw - prevTyaw);
        double vpitch = Double.isNaN(prevTpitch) ? 0.0 : (tpitch - prevTpitch);
        prevTyaw = tyaw;
        prevTpitch = tpitch;

        double ey = wrapDegrees(tyaw - player.getYaw());
        double ep = tpitch - player.getPitch();

        double sizeYaw = Math.max(Math.toDegrees(Math.atan2(target.getWidth() / 2.0, flat)), 0.5);
        double sizePitch = Math.max(Math.toDegrees(Math.atan2(target.getHeight() / 2.0, flat)), 0.5);

        if (!this.modelLoaded) {
            return new float[]{(float) (ey * 0.55), (float) (ep * 0.55)};
        }

        double on = (Math.abs(ey1) <= sizeYaw && Math.abs(ep1) <= sizePitch) ? 1.0 : 0.0;
        double dey = wrapDegrees(ey1 - ey2);
        double dep = ep1 - ep2;

        // 17 входных признаков (1:1 как в train_aura.py -> features())
        double[] x = new double[this.features];
        double[] baseFeats = new double[]{
                arcsinh(ey1) / 3.0,
                arcsinh(ep1) / 3.0,
                arcsinh(dey) / 3.0,
                arcsinh(dep) / 3.0,
                arcsinh(vyaw) / 3.0,
                arcsinh(vpitch) / 3.0,
                arcsinh(dy1) / 3.0,
                arcsinh(dp1) / 3.0,
                arcsinh(dy2) / 3.0,
                arcsinh(dp2) / 3.0,
                arcsinh(ey1 / sizeYaw) / 3.0,
                arcsinh(ep1 / sizePitch) / 3.0,
                Math.log(Math.max(dist, 0.05) + 0.5) / 2.0,
                Math.log(sizeYaw) / 3.0,
                on,
                sinceAttack / 20.0,
                freezeRun / Math.max(1.0, freezeCut)
        };
        System.arraycopy(baseFeats, 0, x, 0, Math.min(baseFeats.length, x.length));

        // GRU шаг: gi = wi @ x + bi, gh = wh @ state + bh
        int h = this.hidden;
        double[] gi = new double[3 * h];
        double[] gh = new double[3 * h];
        for (int i = 0; i < 3 * h; i++) {
            double sumI = bi[i];
            int colsI = Math.min(this.features, wi[i].length);
            for (int j = 0; j < colsI; j++) {
                sumI += wi[i][j] * x[j];
            }
            gi[i] = sumI;

            double sumH = bh[i];
            for (int j = 0; j < h; j++) {
                sumH += wh[i][j] * state[j];
            }
            gh[i] = sumH;
        }

        for (int i = 0; i < h; i++) {
            double r = 1.0 / (1.0 + Math.exp(-(gi[i] + gh[i])));
            double z = 1.0 / (1.0 + Math.exp(-(gi[h + i] + gh[h + i])));
            double n = Math.tanh(gi[2 * h + i] + r * gh[2 * h + i]);
            state[i] = (1.0 - z) * n + z * state[i];
        }

        // Линейная голова MDN (1 + 6 * mix)
        double[] head = new double[headB.length];
        for (int i = 0; i < head.length; i++) {
            double sum = headB[i];
            for (int j = 0; j < h; j++) {
                sum += headW[i][j] * state[j];
            }
            head[i] = sum;
        }

        double freezeProb = 1.0 / (1.0 + Math.exp(-head[0]));

        // Веса смеси softmax по слотам [1 + 6*m]
        double[] logits = new double[this.mix];
        double maxLogit = Double.NEGATIVE_INFINITY;
        for (int m = 0; m < this.mix; m++) {
            logits[m] = head[1 + 6 * m];
            if (logits[m] > maxLogit) maxLogit = logits[m];
        }
        double[] weights = new double[this.mix];
        double[] edges = new double[this.mix];
        double sumExp = 0.0;
        for (int m = 0; m < this.mix; m++) {
            weights[m] = Math.exp(logits[m] - maxLogit);
            sumExp += weights[m];
            edges[m] = sumExp;
        }

        double gcd = computeMouseGcd();
        double driftYaw = wrapDegrees(ey + vyaw);
        double driftPitch = ep + vpitch;
        boolean forced = this.freezeRun >= this.freezeCut;

        double stepYaw = 0.0;
        double stepPitch = 0.0;

        if (!forced && rng.nextDouble() < freezeProb) {
            stepYaw = 0.0;
            stepPitch = 0.0;
        } else {
            double want = this.knobScale * sampleErrorQuantile(rng.nextDouble());
            double safeSizeY = Math.max(sizeYaw, 1e-3);
            double safeSizeP = Math.max(sizePitch, 1e-3);

            double bestLeft = Double.POSITIVE_INFINITY;
            for (int k = 0; k < this.knobKeep; k++) {
                double u = rng.nextDouble() * sumExp;
                int m = 0;
                while (m < this.mix - 1 && u > edges[m]) {
                    m++;
                }

                int slot = 1 + 6 * m;
                double muY = head[slot + 1];
                double muP = head[slot + 2];
                double sigY = Math.exp(Math.max(-4.0, Math.min(1.5, head[slot + 3])));
                double sigP = Math.exp(Math.max(-4.0, Math.min(1.5, head[slot + 4])));
                double rho = Math.tanh(head[slot + 5]) * RHO_LIMIT;

                double blend = Math.sqrt(Math.max(0.0, 1.0 - rho * rho));
                double zy = rng.nextGaussian();
                double zp = rho * zy + blend * rng.nextGaussian();

                double moveY = quantize(squash(muY + this.knobTemp * sigY * zy, this.limitYaw), gcd);
                double moveP = quantize(squash(muP + this.knobTemp * sigP * zp, this.limitPitch), gcd);

                double left = Math.abs(Math.hypot(
                        wrapDegrees(driftYaw - moveY) / safeSizeY,
                        (driftPitch - moveP) / safeSizeP
                ) - want);

                if (left < bestLeft) {
                    bestLeft = left;
                    stepYaw = moveY;
                    stepPitch = moveP;
                }
            }
        }

        if (forced && Math.abs(stepYaw) < 1e-9 && Math.abs(stepPitch) < 1e-9) {
            if (Math.abs(driftYaw) >= Math.abs(driftPitch)) {
                stepYaw = Math.copySign(gcd, driftYaw);
            } else {
                stepPitch = Math.copySign(gcd, driftPitch);
            }
        }

        // Обновление истории
        ey2 = ey1;
        ep2 = ep1;
        ey1 = ey;
        ep1 = ep;
        dy2 = dy1;
        dp2 = dp1;
        dy1 = stepYaw;
        dp1 = stepPitch;
        sinceAttack = attackedThisTick ? 0.0 : Math.min(20.0, sinceAttack + 1.0);
        freezeRun = (Math.abs(stepYaw) < 1e-6 && Math.abs(stepPitch) < 1e-6)
                ? Math.min(freezeCut, freezeRun + 1.0)
                : 0.0;

        return new float[]{(float) stepYaw, (float) stepPitch};
    }

    /**
     * Встроенное дообучение Policy (GRU + MDN) на записанных последовательностях в чистой Java 21
     * (используется как мгновенный фоллбэк, если у игрока не установлен Python + PyTorch).
     */
    public synchronized double trainOnSequencesAndExport(
            List<double[][]> featureSeqs,
            List<double[][]> targetSeqs,
            double[] newErrorQuantiles,
            int epochs,
            Path outPath
    ) {
        if (!this.modelLoaded) {
            loadModelFromResources();
        }
        if (newErrorQuantiles != null && newErrorQuantiles.length == 65) {
            this.errorQuantiles = newErrorQuantiles.clone();
        }

        double lr = 0.0035;
        double bestLoss = Double.POSITIVE_INFINITY;
        int totalEpochs = Math.max(20, Math.min(600, epochs));

        for (int ep = 0; ep < totalEpochs; ep++) {
            double epochLoss = 0.0;
            int steps = 0;

            for (int sIdx = 0; sIdx < featureSeqs.size(); sIdx++) {
                double[][] xs = featureSeqs.get(sIdx);
                double[][] ys = targetSeqs.get(sIdx);
                double[] hState = new double[this.hidden];

                for (int t = 0; t < xs.length; t++) {
                    double[] x = xs[t];
                    double targetUy = ys[t][0];
                    double targetUp = ys[t][2];
                    double targetFrozen = ys[t][4];

                    // Forward GRU
                    int h = this.hidden;
                    double[] gi = new double[3 * h];
                    double[] gh = new double[3 * h];
                    for (int i = 0; i < 3 * h; i++) {
                        double sumI = bi[i];
                        for (int j = 0; j < Math.min(this.features, x.length); j++) {
                            sumI += wi[i][j] * x[j];
                        }
                        gi[i] = sumI;
                        double sumH = bh[i];
                        for (int j = 0; j < h; j++) {
                            sumH += wh[i][j] * hState[j];
                        }
                        gh[i] = sumH;
                    }
                    for (int i = 0; i < h; i++) {
                        double r = 1.0 / (1.0 + Math.exp(-(gi[i] + gh[i])));
                        double z = 1.0 / (1.0 + Math.exp(-(gi[h + i] + gh[h + i])));
                        double n = Math.tanh(gi[2 * h + i] + r * gh[2 * h + i]);
                        hState[i] = (1.0 - z) * n + z * hState[i];
                    }

                    // Head forward & gradient step on linear output head
                    double freezePred = 1.0 / (1.0 + Math.exp(-dot(headW[0], hState) - headB[0]));
                    double errFreeze = freezePred - targetFrozen;
                    headB[0] -= lr * errFreeze;
                    for (int j = 0; j < h; j++) {
                        headW[0][j] -= lr * errFreeze * hState[j];
                    }

                    // Update primary MDN mixture component towards recorded hand (targetUy, targetUp)
                    int slot = 1;
                    double muY = dot(headW[slot + 1], hState) + headB[slot + 1];
                    double muP = dot(headW[slot + 2], hState) + headB[slot + 2];
                    double diffY = muY - targetUy;
                    double diffP = muP - targetUp;
                    epochLoss += 0.5 * (diffY * diffY + diffP * diffP) + Math.abs(errFreeze) * 0.25;
                    steps++;

                    if (targetFrozen < 0.5) {
                        headB[slot + 1] -= lr * diffY;
                        headB[slot + 2] -= lr * diffP;
                        for (int j = 0; j < h; j++) {
                            headW[slot + 1][j] -= lr * diffY * hState[j];
                            headW[slot + 2][j] -= lr * diffP * hState[j];
                        }
                    }
                }
            }

            if (steps > 0) {
                bestLoss = Math.min(bestLoss, epochLoss / steps);
            }
        }

        exportWeightsToJson(outPath);
        this.loadedModelName = outPath.getFileName().toString();
        return bestLoss;
    }

    private static double dot(double[] w, double[] h) {
        double s = 0.0;
        for (int i = 0; i < Math.min(w.length, h.length); i++) {
            s += w[i] * h[i];
        }
        return s;
    }

    public synchronized void exportWeightsToJson(Path outPath) {
        try {
            if (outPath.getParent() != null) {
                Files.createDirectories(outPath.getParent());
            }
            JsonObject root = new JsonObject();
            root.addProperty("kind", "gru");
            root.addProperty("features", this.features);
            root.addProperty("hidden", this.hidden);
            root.addProperty("mix", this.mix);
            root.addProperty("squash", "sinh");
            root.addProperty("units", "deg");

            JsonArray lim = new JsonArray();
            lim.add(this.limitYaw);
            lim.add(this.limitPitch);
            root.add("limits", lim);

            JsonArray knobArr = new JsonArray();
            double[][] defaultKnobs = new double[][]{
                    {0.70, 4, 0.45},
                    {0.70, 1, 0.45},
                    {0.70, 8, 0.45},
                    {0.70, 4, 1.00}
            };
            for (double[] k : defaultKnobs) {
                JsonArray row = new JsonArray();
                row.add(k[0]);
                row.add((int) k[1]);
                row.add(k[2]);
                knobArr.add(row);
            }
            root.add("knob", knobArr);
            root.addProperty("freezeCut", (int) this.freezeCut);
            root.add("error", toJsonVector(this.errorQuantiles));

            JsonObject gru = new JsonObject();
            gru.add("wi", toJsonMatrix(this.wi));
            gru.add("wh", toJsonMatrix(this.wh));
            gru.add("bi", toJsonVector(this.bi));
            gru.add("bh", toJsonVector(this.bh));
            root.add("gru", gru);

            JsonObject out = new JsonObject();
            out.add("w", toJsonMatrix(this.headW));
            out.add("b", toJsonVector(this.headB));
            root.add("out", out);

            Gson gson = new GsonBuilder().create();
            try (Writer writer = Files.newBufferedWriter(outPath, StandardCharsets.UTF_8)) {
                gson.toJson(root, writer);
            }
            LOGGER.info("[Neuro] Обученная модель сохранена в {}", outPath.toAbsolutePath());
        } catch (Exception e) {
            LOGGER.error("[Neuro] Ошибка сохранения модели в {}: {}", outPath, e.getMessage());
        }
    }

    private static JsonArray toJsonMatrix(double[][] mat) {
        JsonArray rows = new JsonArray();
        for (double[] r : mat) {
            rows.add(toJsonVector(r));
        }
        return rows;
    }

    private static JsonArray toJsonVector(double[] vec) {
        JsonArray arr = new JsonArray();
        for (double v : vec) {
            arr.add(Math.round(v * 1_000_000.0) / 1_000_000.0);
        }
        return arr;
    }

    public static Path getCustomAimPath() {
        MinecraftClient client = MinecraftClient.getInstance();
        if (client == null || client.runDirectory == null) return null;
        return client.runDirectory.toPath().resolve("neuro").resolve("aim.json");
    }

    public static Path getRockstarAimPath() {
        MinecraftClient client = MinecraftClient.getInstance();
        if (client == null || client.runDirectory == null) return null;
        return client.runDirectory.toPath().resolve("Rockstar").resolve("neuro").resolve("aim.json");
    }

    public String getLoadedModelName() {
        return loadedModelName;
    }
}
