package neuro.client.ai;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.Random;

/**
 * Автономный математический движок GRU + Mixture Density Network (MDN) для Minecraft 1.21.4.
 * Напрямую парсит обученные веса из /assets/neuro/models/default.json (без зависимости от Python/JEP).
 * Реализует точную формулу Policy(features=17, hidden=32, mix=3) с функцией сжатия sinh.
 */
public class NeuroAuraEngine {
    private static final Logger LOGGER = LoggerFactory.getLogger("NeuroAuraEngine");

    public static final int FEATURES = 17;
    public static final int HIDDEN = 32;
    public static final int MIX = 3;
    private static final double RHO_LIMIT = 0.95;
    private static final double MAX_RUN = 40.0;

    private final double[] state = new double[HIDDEN];
    private double[][] wi;     // [96][17]
    private double[][] wh;     // [96][32]
    private double[] bi;       // [96]
    private double[] bh;       // [96]
    private double[][] headW;  // [19][32]
    private double[] headB;    // [19]
    private boolean modelLoaded = false;

    // История для 17 признаков (ey1, ep1, ey2, ep2, dy1, dp1, dy2, dp2, tyawPrev, tpitchPrev, since, run)
    private double ey1 = 0, ep1 = 0, ey2 = 0, ep2 = 0;
    private double dy1 = 0, dp1 = 0, dy2 = 0, dp2 = 0;
    private double prevTyaw = Double.NaN, prevTpitch = Double.NaN;
    private double sinceAttack = 20.0;
    private double freezeRun = 0.0;
    private final Random rng = new Random();

    public void loadModelFromResources() {
        try (InputStream is = NeuroAuraEngine.class.getResourceAsStream("/assets/neuro/models/default.json")) {
            if (is == null) {
                LOGGER.warn("[Neuro] Файл default.json не найден в ресурсах, используется резервный профиль.");
                return;
            }
            JsonObject root = JsonParser.parseReader(new InputStreamReader(is, StandardCharsets.UTF_8)).getAsJsonObject();
            if (!root.has("gru") || !root.has("out")) {
                LOGGER.warn("[Neuro] Файл default.json не содержит секций gru/out.");
                return;
            }
            JsonObject gru = root.getAsJsonObject("gru");
            JsonObject out = root.getAsJsonObject("out");

            this.wi = parseMatrix(gru.getAsJsonArray("wi"));
            this.wh = parseMatrix(gru.getAsJsonArray("wh"));
            this.bi = parseVector(gru.getAsJsonArray("bi"));
            this.bh = parseVector(gru.getAsJsonArray("bh"));
            this.headW = parseMatrix(out.getAsJsonArray("w"));
            this.headB = parseVector(out.getAsJsonArray("b"));

            this.modelLoaded = true;
            LOGGER.info("[Neuro] Веса GRU нейросети успешно загружены (wi={}x{}, wh={}x{}, out={}x{})!",
                    wi.length, wi[0].length, wh.length, wh[0].length, headW.length, headW[0].length);
        } catch (Exception e) {
            LOGGER.error("[Neuro] Ошибка чтения весов GRU из default.json: {}", e.getMessage());
        }
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

    private static double arcsinh(double x) {
        return Math.log(x + Math.sqrt(x * x + 1.0));
    }

    private static double squash(double u, double limit) {
        double clamped = Math.max(-8.0, Math.min(8.0, u));
        return Math.max(-limit, Math.min(limit, Math.sinh(clamped)));
    }

    private static double wrapDegrees(double a) {
        double r = (a + 180.0) % 360.0;
        if (r < 0) r += 360.0;
        return r - 180.0;
    }

    /**
     * Выполняет точный шаг GRU + MDN (17 признаков -> 32 скрытых состояния -> 3 гауссианы)
     */
    public float[] computeStep(PlayerEntity player, Entity target, boolean attackedThisTick) {
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

        // 17 входных признаков (1:1 как в train_aura.py)
        double[] x = new double[]{
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
                freezeRun / MAX_RUN
        };

        // Вычисление GRU: gi = wi @ x + bi, gh = wh @ state + bh
        int h = HIDDEN;
        double[] gi = new double[3 * h];
        double[] gh = new double[3 * h];
        for (int i = 0; i < 3 * h; i++) {
            double sumI = bi[i];
            for (int j = 0; j < FEATURES; j++) {
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

        // Вычисление линейной головы MDN (19 выходов)
        double[] head = new double[1 + 6 * MIX];
        for (int i = 0; i < head.length; i++) {
            double sum = headB[i];
            for (int j = 0; j < h; j++) {
                sum += headW[i][j] * state[j];
            }
            head[i] = sum;
        }

        // Выбор компоненты смеси с наибольшим весом
        int bestMix = 0;
        double bestLogit = Double.NEGATIVE_INFINITY;
        for (int m = 0; m < MIX; m++) {
            double logit = head[1 + 6 * m];
            if (logit > bestLogit) {
                bestLogit = logit;
                bestMix = m;
            }
        }

        int slot = 1 + 6 * bestMix;
        double muYaw = head[slot + 1];
        double muPitch = head[slot + 2];
        double sigmaYaw = Math.exp(Math.max(-4.0, Math.min(1.5, head[slot + 3])));
        double sigmaPitch = Math.exp(Math.max(-4.0, Math.min(1.5, head[slot + 4])));
        double rho = Math.tanh(head[slot + 5]) * RHO_LIMIT;

        double temp = 0.35;
        double zy = rng.nextGaussian();
        double zp = rho * zy + Math.sqrt(Math.max(0.0, 1.0 - rho * rho)) * rng.nextGaussian();

        double stepYaw = squash(muYaw + temp * sigmaYaw * zy, 179.0);
        double stepPitch = squash(muPitch + temp * sigmaPitch * zp, 90.0);

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
        freezeRun = (Math.abs(stepYaw) < 1e-6 && Math.abs(stepPitch) < 1e-6) ? Math.min(MAX_RUN, freezeRun + 1.0) : 0.0;

        return new float[]{(float) stepYaw, (float) stepPitch};
    }

    public float[] predictRotations(PlayerEntity player, Entity target) {
        return computeStep(player, target, false);
    }
}
