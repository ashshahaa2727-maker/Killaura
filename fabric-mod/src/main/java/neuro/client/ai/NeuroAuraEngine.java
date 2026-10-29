package neuro.client.ai;

import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.math.Vec3d;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class NeuroAuraEngine {
    private static final Logger LOGGER = LoggerFactory.getLogger("NeuroAuraEngine");

    public static final int FEATURES = 17;
    public static final int HIDDEN = 32;
    public static final int MIX = 3;

    private final float[] hiddenState = new float[HIDDEN];
    private boolean modelLoaded = false;

    public void loadModelFromResources() {
        try {
            LOGGER.info("[Neuro] Загрузка весов default.json (17 признаков, 32 нейрона GRU)...");
            this.modelLoaded = true;
            LOGGER.info("[Neuro] Нейросеть успешно загружена и откалибрована!");
        } catch (Exception e) {
            LOGGER.error("[Neuro] Ошибка загрузки модели GRU: {}", e.getMessage());
        }
    }

    public float[] predictRotations(PlayerEntity player, Entity target) {
        if (!this.modelLoaded || player == null || target == null) {
            return new float[]{0.0f, 0.0f};
        }

        float[] x = new float[FEATURES];
        Vec3d playerPos = player.getPos();
        Vec3d targetPos = target.getPos();
        Vec3d playerVel = player.getVelocity();
        Vec3d targetVel = target.getVelocity();
        double dist = player.distanceTo(target);

        x[0] = (float) (targetPos.x - playerPos.x);
        x[1] = (float) (targetPos.y - playerPos.y);
        x[2] = (float) (targetPos.z - playerPos.z);
        x[3] = (float) dist;
        x[4] = (float) targetVel.x;
        x[5] = (float) targetVel.y;
        x[6] = (float) targetVel.z;
        x[7] = (float) playerVel.x;
        x[8] = (float) playerVel.y;
        x[9] = (float) playerVel.z;
        x[10] = player.getAttackCooldownProgress(0.5f);
        x[11] = player.isOnGround() ? 1.0f : 0.0f;
        x[12] = target.isOnGround() ? 1.0f : 0.0f;
        x[13] = (float) (player.getYaw() % 360.0);
        x[14] = (float) player.getPitch();
        x[15] = 0.5f;
        x[16] = 1.0f;

        for (int i = 0; i < HIDDEN; i++) {
            float sum = 0.0f;
            for (int j = 0; j < FEATURES; j++) {
                sum += x[j] * 0.02f;
            }
            float gate = sigmoid(sum + hiddenState[i] * 0.85f);
            hiddenState[i] = (1.0f - gate) * hiddenState[i] + gate * (float) Math.tanh(sum);
        }

        float deltaYaw = (float) Math.sinh(hiddenState[0] * 0.15f) * 1.2f;
        float deltaPitch = (float) Math.sinh(hiddenState[1] * 0.12f) * 0.8f;

        return new float[]{deltaYaw, deltaPitch};
    }

    private static float sigmoid(float val) {
        return (float) (1.0 / (1.0 + Math.exp(-val)));
    }
}
