package neuro.client.ai;

import net.minecraft.client.MinecraftClient;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.util.math.Vec3d;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Locale;

/**
 * Записывает датасет движений мыши и боев игрока в точном формате train_aura.py:
 * Заголовок: t,clean,has,tid,gcd,yaw,pitch,dyaw,dpitch,rx,ry,rz,bw,bh,dist,on,atk
 * Сохраняет одновременно в <runDir>/neuro/data/ и <runDir>/Rockstar/neuro/data/ для 100% совместимости.
 */
public class NeuroDatasetRecorder {
    private static final Logger LOGGER = LoggerFactory.getLogger("NeuroDatasetRecorder");
    public static final String CSV_HEADER = "t,clean,has,tid,gcd,yaw,pitch,dyaw,dpitch,rx,ry,rz,bw,bh,dist,on,atk";

    private boolean recording = false;
    private String activeFileName = "";
    private BufferedWriter primaryWriter = null;
    private BufferedWriter mirrorWriter = null;

    private int tickCounter = 0;
    private int recordedTicks = 0;
    private int currentEpisodeLen = 0;
    private int validEpisodes = 0;
    private float prevYaw = Float.NaN;
    private float prevPitch = Float.NaN;

    public synchronized boolean startRecording(String sessionPrefix) {
        if (this.recording) {
            stopRecording();
        }

        MinecraftClient client = MinecraftClient.getInstance();
        if (client == null || client.runDirectory == null) return false;

        String cleanPrefix = (sessionPrefix == null || sessionPrefix.isBlank())
                ? "pvp"
                : sessionPrefix.replaceAll("[^a-zA-Z0-9_-]", "");
        String stamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("MMdd_HHmmss"));
        this.activeFileName = cleanPrefix + "_" + stamp + ".csv";

        try {
            Path dir1 = client.runDirectory.toPath().resolve("neuro").resolve("data");
            Path dir2 = client.runDirectory.toPath().resolve("Rockstar").resolve("neuro").resolve("data");
            Files.createDirectories(dir1);
            Files.createDirectories(dir2);

            Path file1 = dir1.resolve(this.activeFileName);
            Path file2 = dir2.resolve(this.activeFileName);

            this.primaryWriter = Files.newBufferedWriter(file1, StandardCharsets.UTF_8,
                    StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING, StandardOpenOption.WRITE);
            this.mirrorWriter = Files.newBufferedWriter(file2, StandardCharsets.UTF_8,
                    StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING, StandardOpenOption.WRITE);

            this.primaryWriter.write(CSV_HEADER);
            this.primaryWriter.newLine();
            this.mirrorWriter.write(CSV_HEADER);
            this.mirrorWriter.newLine();

            this.recording = true;
            this.tickCounter = 0;
            this.recordedTicks = 0;
            this.currentEpisodeLen = 0;
            this.validEpisodes = 0;
            this.prevYaw = Float.NaN;
            this.prevPitch = Float.NaN;

            sendChat(client, "§a[Neuro] §fЗапись боя начата: §d" + this.activeFileName
                    + " §7(бейте цель вручную для чистого датасета, минимум 24 тика подряд).");
            return true;
        } catch (IOException e) {
            LOGGER.error("[NeuroDatasetRecorder] Не удалось открыть файл для записи: {}", e.getMessage());
            return false;
        }
    }

    public synchronized void stopRecording() {
        if (!this.recording) return;
        this.recording = false;

        try {
            if (this.primaryWriter != null) {
                this.primaryWriter.flush();
                this.primaryWriter.close();
            }
            if (this.mirrorWriter != null) {
                this.mirrorWriter.flush();
                this.mirrorWriter.close();
            }
        } catch (IOException e) {
            LOGGER.warn("[NeuroDatasetRecorder] Ошибка закрытия CSV: {}", e.getMessage());
        } finally {
            this.primaryWriter = null;
            this.mirrorWriter = null;
        }

        MinecraftClient client = MinecraftClient.getInstance();
        sendChat(client, "§a[Neuro] §fЗапись сохранена: §d" + this.activeFileName
                + " §7(тиков: §f" + this.recordedTicks + "§7, эпизодов: §f" + this.validEpisodes
                + "§7). Запустите обучение: §d.neuro train");
    }

    public synchronized void onTick(MinecraftClient client, Entity target, boolean attacked, boolean cleanHand) {
        if (!this.recording || client.player == null) return;
        PlayerEntity player = client.player;

        this.tickCounter++;
        float yaw = player.getYaw();
        float pitch = player.getPitch();

        double dyaw = Float.isNaN(this.prevYaw) ? 0.0 : NeuroAuraEngine.wrapDegrees(yaw - this.prevYaw);
        double dpitch = Float.isNaN(this.prevPitch) ? 0.0 : (pitch - this.prevPitch);
        this.prevYaw = yaw;
        this.prevPitch = pitch;

        boolean hasTarget = target != null && target.isAlive();
        int tid = hasTarget ? target.getId() : 0;
        double gcd = NeuroAuraEngine.computeMouseGcd();

        double rx = 0.0, ry = 0.0, rz = 0.0, bw = 0.6, bh = 1.8, dist = 3.0, on = 0.0;
        if (hasTarget) {
            Vec3d eyes = player.getEyePos();
            Vec3d center = target.getBoundingBox().getCenter();
            rx = center.x - eyes.x;
            ry = center.y - eyes.y;
            rz = center.z - eyes.z;
            bw = target.getWidth();
            bh = target.getHeight();
            double flat = Math.max(Math.hypot(rx, rz), 0.05);
            dist = Math.hypot(flat, ry);

            double tyaw = Math.toDegrees(Math.atan2(rz, rx)) - 90.0;
            double tpitch = -Math.toDegrees(Math.atan2(ry, flat));
            double ey = NeuroAuraEngine.wrapDegrees(tyaw - yaw);
            double ep = tpitch - pitch;
            double sizeYaw = Math.max(Math.toDegrees(Math.atan2(bw / 2.0, flat)), 0.5);
            double sizePitch = Math.max(Math.toDegrees(Math.atan2(bh / 2.0, flat)), 0.5);
            on = (Math.abs(ey) <= sizeYaw && Math.abs(ep) <= sizePitch) ? 1.0 : 0.0;
        }

        if (hasTarget && cleanHand) {
            this.currentEpisodeLen++;
            if (this.currentEpisodeLen == 24) {
                this.validEpisodes++;
            }
        } else {
            this.currentEpisodeLen = 0;
        }

        String line = String.format(Locale.US,
                "%d,%d,%d,%d,%.6f,%.5f,%.5f,%.5f,%.5f,%.5f,%.5f,%.5f,%.4f,%.4f,%.5f,%.1f,%.1f",
                this.tickCounter,
                cleanHand ? 1 : 0,
                hasTarget ? 1 : 0,
                tid,
                gcd,
                yaw,
                pitch,
                dyaw,
                dpitch,
                rx,
                ry,
                rz,
                bw,
                bh,
                dist,
                on,
                attacked ? 1.0 : 0.0
        );

        try {
            if (this.primaryWriter != null) {
                this.primaryWriter.write(line);
                this.primaryWriter.newLine();
            }
            if (this.mirrorWriter != null) {
                this.mirrorWriter.write(line);
                this.mirrorWriter.newLine();
            }
            this.recordedTicks++;
            if (this.recordedTicks % 128 == 0) {
                if (this.primaryWriter != null) this.primaryWriter.flush();
                if (this.mirrorWriter != null) this.mirrorWriter.flush();
            }
        } catch (IOException e) {
            LOGGER.warn("[NeuroDatasetRecorder] Ошибка записи строки: {}", e.getMessage());
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

    public boolean isRecording() {
        return recording;
    }

    public int getRecordedTicks() {
        return recordedTicks;
    }

    public int getValidEpisodes() {
        return validEpisodes;
    }

    public String getActiveFileName() {
        return activeFileName;
    }
}
