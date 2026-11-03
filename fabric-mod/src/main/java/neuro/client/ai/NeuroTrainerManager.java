package neuro.client.ai;

import net.minecraft.client.MinecraftClient;
import net.minecraft.text.Text;
import neuro.client.NeuroClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.CompletableFuture;

/**
 * Управляет обучением нейросети Киллауры прямо из игры (.neuro train или кнопка в ClickGUI):
 * 1. Извлекает оригинальные Python-скрипты (trainer/common.py и trainer/train_aura.py) в папку игры.
 * 2. Пытается запустить полноценный PyTorch-тренер (python3 trainer/train_aura.py).
 * 3. Если Python/PyTorch не установлен или данных мало, выполняет встроенное обучение GRU+MDN на Java 21,
 *    формирует aim.json и горячо перезагружает веса в NeuroAuraEngine!
 */
public class NeuroTrainerManager {
    private static final Logger LOGGER = LoggerFactory.getLogger("NeuroTrainerManager");

    private volatile boolean training = false;
    private volatile String lastStatus = "Готов";

    public void extractTrainerScripts() {
        MinecraftClient client = MinecraftClient.getInstance();
        if (client == null || client.runDirectory == null) return;

        try {
            Path trainerDir = client.runDirectory.toPath().resolve("neuro").resolve("trainer");
            Files.createDirectories(trainerDir);
            copyResource("/assets/neuro/trainer/common.py", trainerDir.resolve("common.py"));
            copyResource("/assets/neuro/trainer/train_aura.py", trainerDir.resolve("train_aura.py"));
        } catch (Exception e) {
            LOGGER.warn("[NeuroTrainer] Не удалось распаковать скрипты тренера: {}", e.getMessage());
        }
    }

    private void copyResource(String resPath, Path dest) {
        try (InputStream is = NeuroTrainerManager.class.getResourceAsStream(resPath)) {
            if (is != null) {
                Files.copy(is, dest, StandardCopyOption.REPLACE_EXISTING);
            }
        } catch (Exception ignored) {
        }
    }

    public void startTraining(int epochs) {
        if (this.training) return;
        this.training = true;
        this.lastStatus = "Подготовка датасетов...";

        MinecraftClient client = MinecraftClient.getInstance();
        sendChat(client, "§d[Neuro AI] §fЗапуск обучения Киллауры (§e" + epochs + " эпох§f)...");

        CompletableFuture.runAsync(() -> {
            try {
                extractTrainerScripts();
                Path runDir = client.runDirectory.toPath();
                Path dataDir = runDir.resolve("neuro").resolve("data");
                Path outAim = runDir.resolve("neuro").resolve("aim.json");
                Path trainerScript = runDir.resolve("neuro").resolve("trainer").resolve("train_aura.py");
                Files.createDirectories(dataDir);

                boolean pythonSucceeded = runPythonTrainer(client, trainerScript, dataDir, outAim, epochs);
                if (!pythonSucceeded) {
                    this.lastStatus = "Java 21 GRU Trainer...";
                    runJavaFallbackTrainer(client, dataDir, outAim, epochs);
                }

                // Горячая перезагрузка обученной модели в движок Киллауры
                NeuroAuraEngine engine = NeuroClient.getInstance().getNeuroEngine();
                if (engine != null && Files.exists(outAim)) {
                    engine.loadModelFromFile(outAim);
                    sendChat(client, "§a[Neuro AI] §fОбучение завершено! Модель §d" + outAim.getFileName()
                            + " §fгорячо подгружена в Киллауру.");
                }
                this.lastStatus = "Обучено (aim.json)";
            } catch (Exception e) {
                LOGGER.error("[NeuroTrainer] Ошибка обучения: {}", e.getMessage());
                this.lastStatus = "Ошибка";
                sendChat(client, "§c[Neuro AI] Ошибка обучения: " + e.getMessage());
            } finally {
                this.training = false;
            }
        });
    }

    private boolean runPythonTrainer(MinecraftClient client, Path script, Path dataDir, Path outAim, int epochs) {
        if (!Files.exists(script)) return false;

        for (String pyCmd : List.of("python3", "python")) {
            try {
                ProcessBuilder pb = new ProcessBuilder(
                        pyCmd,
                        script.toAbsolutePath().toString(),
                        "--data", dataDir.toAbsolutePath().toString(),
                        "--out", outAim.toAbsolutePath().toString(),
                        "--epochs", String.valueOf(epochs),
                        "--seeds", "1"
                );
                pb.directory(script.getParent().toFile());
                pb.redirectErrorStream(true);
                Process proc = pb.start();

                try (BufferedReader reader = new BufferedReader(
                        new InputStreamReader(proc.getInputStream(), StandardCharsets.UTF_8))) {
                    String line;
                    while ((line = reader.readLine()) != null) {
                        String trimmed = line.trim();
                        if (!trimmed.isEmpty()) {
                            this.lastStatus = trimmed.length() > 36 ? trimmed.substring(0, 36) + "..." : trimmed;
                            if (trimmed.startsWith("прогресс") || trimmed.startsWith("сессий")
                                    || trimmed.startsWith("лучшая") || trimmed.startsWith("выгружено")) {
                                sendChat(client, "§7[PyTorch] §f" + trimmed);
                            }
                        }
                    }
                }

                int code = proc.waitFor();
                if (code == 0 && Files.exists(outAim)) {
                    return true;
                }
            } catch (Exception ignored) {
                // Переходим к следующей команде или встроенному Java 21 тренеру
            }
        }
        return false;
    }

    private void runJavaFallbackTrainer(MinecraftClient client, Path dataDir, Path outAim, int epochs) {
        List<double[][]> seqX = new ArrayList<>();
        List<double[][]> seqY = new ArrayList<>();
        List<Double> residualsList = new ArrayList<>();

        try (DirectoryStream<Path> stream = Files.newDirectoryStream(dataDir, "*.csv")) {
            for (Path csvPath : stream) {
                parseCsvForJavaTrainer(csvPath, seqX, seqY, residualsList);
            }
        } catch (Exception ignored) {
        }

        // Если игрок ещё не записал длинных .csv боёв, генерируем синтетическую калибровку под чувствительность мыши игрока
        if (seqX.isEmpty()) {
            sendChat(client, "§e[Neuro AI] §fВ папке §dneuro/data §fпока мало .csv записей — выполняю калибровку GRU+MDN под ваш GCD мыши...");
            double gcd = NeuroAuraEngine.computeMouseGcd();
            double[][] synthX = new double[48][17];
            double[][] synthY = new double[48][6];
            for (int t = 0; t < 48; t++) {
                double errY = Math.sin(t * 0.22) * 14.0;
                double errP = Math.cos(t * 0.18) * 6.0;
                double stepY = NeuroAuraEngine.quantize(errY * 0.48, gcd);
                double stepP = NeuroAuraEngine.quantize(errP * 0.48, gcd);
                synthX[t][0] = NeuroAuraEngine.arcsinh(errY) / 3.0;
                synthX[t][1] = NeuroAuraEngine.arcsinh(errP) / 3.0;
                synthX[t][6] = NeuroAuraEngine.arcsinh(stepY) / 3.0;
                synthX[t][7] = NeuroAuraEngine.arcsinh(stepP) / 3.0;
                synthX[t][14] = 1.0;
                synthY[t][0] = NeuroAuraEngine.arcsinh(stepY);
                synthY[t][1] = 1.0;
                synthY[t][2] = NeuroAuraEngine.arcsinh(stepP);
                synthY[t][3] = 1.0;
                synthY[t][4] = 0.0;
                synthY[t][5] = 1.0;
                residualsList.add(Math.hypot(errY / 12.0, errP / 12.0));
            }
            seqX.add(synthX);
            seqY.add(synthY);
        }

        double[] quantiles = null;
        if (residualsList.size() >= 10) {
            double[] sorted = residualsList.stream().mapToDouble(Double::doubleValue).sorted().toArray();
            quantiles = new double[65];
            for (int i = 0; i < 65; i++) {
                double q = (i / 64.0) * (sorted.length - 1);
                int idx = (int) q;
                quantiles[i] = sorted[Math.min(idx, sorted.length - 1)];
            }
        }

        NeuroAuraEngine engine = NeuroClient.getInstance().getNeuroEngine();
        if (engine != null) {
            double loss = engine.trainOnSequencesAndExport(seqX, seqY, quantiles, epochs, outAim);
            Path rockstarAim = NeuroAuraEngine.getRockstarAimPath();
            if (rockstarAim != null) {
                engine.exportWeightsToJson(rockstarAim);
            }
            sendChat(client, String.format("§a[Java 21 GRU Trainer] §fОбучено последовательностей: §e%d§f, финальный Loss: §a%.4f",
                    seqX.size(), loss));
        }
    }

    private void parseCsvForJavaTrainer(
            Path csvPath,
            List<double[][]> seqX,
            List<double[][]> seqY,
            List<Double> residuals
    ) {
        try {
            List<String> lines = Files.readAllLines(csvPath, StandardCharsets.UTF_8);
            if (lines.size() < 20) return;

            List<double[]> feats = new ArrayList<>();
            List<double[]> targets = new ArrayList<>();
            double prevEy = 0, prevEp = 0, prevDy = 0, prevDp = 0;

            for (int i = 1; i < lines.size(); i++) {
                String[] parts = lines.get(i).split(",");
                if (parts.length < 17) continue;
                int clean = Integer.parseInt(parts[1].trim());
                int has = Integer.parseInt(parts[2].trim());
                if (clean != 1 || has != 1) continue;

                double yaw = Double.parseDouble(parts[5].trim());
                double pitch = Double.parseDouble(parts[6].trim());
                double dyaw = Double.parseDouble(parts[7].trim());
                double dpitch = Double.parseDouble(parts[8].trim());
                double rx = Double.parseDouble(parts[9].trim());
                double ry = Double.parseDouble(parts[10].trim());
                double rz = Double.parseDouble(parts[11].trim());
                double bw = Double.parseDouble(parts[12].trim());
                double bh = Double.parseDouble(parts[13].trim());
                double dist = Double.parseDouble(parts[14].trim());
                double on = Double.parseDouble(parts[15].trim());

                double flat = Math.max(Math.hypot(rx, rz), 0.05);
                double tyaw = Math.toDegrees(Math.atan2(rz, rx)) - 90.0;
                double tpitch = -Math.toDegrees(Math.atan2(ry, flat));
                double ey = NeuroAuraEngine.wrapDegrees(tyaw - yaw);
                double ep = tpitch - pitch;
                double sizeY = Math.max(Math.toDegrees(Math.atan2(bw / 2.0, flat)), 0.5);
                double sizeP = Math.max(Math.toDegrees(Math.atan2(bh / 2.0, flat)), 0.5);

                residuals.add(Math.hypot(ey / sizeY, ep / sizeP));

                double[] x = new double[17];
                x[0] = NeuroAuraEngine.arcsinh(ey) / 3.0;
                x[1] = NeuroAuraEngine.arcsinh(ep) / 3.0;
                x[2] = NeuroAuraEngine.arcsinh(NeuroAuraEngine.wrapDegrees(ey - prevEy)) / 3.0;
                x[3] = NeuroAuraEngine.arcsinh(ep - prevEp) / 3.0;
                x[6] = NeuroAuraEngine.arcsinh(prevDy) / 3.0;
                x[7] = NeuroAuraEngine.arcsinh(prevDp) / 3.0;
                x[10] = NeuroAuraEngine.arcsinh(ey / sizeY) / 3.0;
                x[11] = NeuroAuraEngine.arcsinh(ep / sizeP) / 3.0;
                x[12] = Math.log(Math.max(dist, 0.05) + 0.5) / 2.0;
                x[13] = Math.log(sizeY) / 3.0;
                x[14] = on;

                boolean frozen = Math.abs(dyaw) < 1e-6 && Math.abs(dpitch) < 1e-6;
                double[] y = new double[]{
                        NeuroAuraEngine.arcsinh(dyaw),
                        frozen ? 0.0 : 1.0,
                        NeuroAuraEngine.arcsinh(dpitch),
                        frozen ? 0.0 : 1.0,
                        frozen ? 1.0 : 0.0,
                        1.0
                };

                feats.add(x);
                targets.add(y);
                prevEy = ey;
                prevEp = ep;
                prevDy = dyaw;
                prevDp = dpitch;
            }

            if (feats.size() >= 16) {
                seqX.add(feats.toArray(new double[0][]));
                seqY.add(targets.toArray(new double[0][]));
            }
        } catch (Exception ignored) {
        }
    }

    private void sendChat(MinecraftClient client, String msg) {
        if (client != null && client.player != null) {
            client.execute(() -> {
                if (client.player != null) {
                    client.player.sendMessage(Text.literal(msg), false);
                }
            });
        }
    }

    public boolean isTraining() {
        return training;
    }

    public String getLastStatus() {
        return lastStatus;
    }
}
