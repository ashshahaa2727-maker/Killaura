package neuro.client.module.combat;

import net.minecraft.client.MinecraftClient;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.RaycastContext;
import neuro.client.NeuroClient;
import neuro.client.ai.NeuroAuraEngine;
import neuro.client.ai.NeuroClicks;
import neuro.client.ai.NeuroDatasetRecorder;
import neuro.client.ai.NeuroTrainerManager;
import neuro.client.module.Category;
import neuro.client.module.Module;
import neuro.client.setting.*;

import java.util.Comparator;
import java.util.List;

public class AuraModule extends Module {
    private final ModeSetting rotationMode = new ModeSetting("Model", "Neuro GRU AI",
            List.of("Neuro GRU AI", "Custom (aim.json)", "NeurAim v6", "GrimAC Strict", "Matrix"));
    private final ModeSetting clicksProfile = new ModeSetting("NeuroClicks", "strong",
            List.of("strong", "early", "exact", "relaxed"));
    private final SliderSetting aimScale = new SliderSetting("Scale (Knob)", 0.45, 0.0, 1.0, 0.05);
    private final SliderSetting aimKeep = new SliderSetting("Keep Samples", 4.0, 1.0, 8.0, 1.0);
    private final SliderSetting aimTemp = new SliderSetting("Temperature", 0.70, 0.10, 1.20, 0.05);
    private final SliderSetting attackDistance = new SliderSetting("Attack Dist", 3.6, 2.0, 6.0, 0.1);
    private final SliderSetting wallDistance = new SliderSetting("Wall Dist", 1.8, 0.0, 4.5, 0.1);
    private final BooleanSetting smartCrits = new BooleanSetting("Smart Crits", true);
    private final BooleanSetting sprintReset = new BooleanSetting("W-Tap Sprint", true);
    private final BooleanSetting recordDataset = new BooleanSetting("Record CSV (.neuro rec)", false);
    private final BooleanSetting trainNow = new BooleanSetting("Train AI (.neuro train)", false);
    private final BooleanSetting raycastCheck = new BooleanSetting("Raycast Check", true);
    private final BooleanSetting silentRotation = new BooleanSetting("Silent Rotation", false);

    private LivingEntity targetEntity = null;
    private String lastModelMode = "Neuro GRU AI";

    public AuraModule() {
        super("Aura", "Обучаемая Киллаура: GRU+MDN + NeuroClicks + Запись и Тренер", Category.COMBAT);
        this.setEnabled(true);
        this.addSettings(
                rotationMode, clicksProfile, aimScale, aimKeep, aimTemp,
                attackDistance, wallDistance, smartCrits, sprintReset,
                recordDataset, trainNow, raycastCheck, silentRotation
        );
    }

    @Override
    public void onTick(MinecraftClient client) {
        if (client.player == null || client.world == null || client.interactionManager == null) {
            this.targetEntity = null;
            return;
        }

        NeuroClient nc = NeuroClient.getInstance();
        NeuroAuraEngine engine = nc.getNeuroEngine();
        NeuroClicks clicks = nc.getNeuroClicks();
        NeuroDatasetRecorder recorder = nc.getDatasetRecorder();
        NeuroTrainerManager trainer = nc.getTrainerManager();

        // Обработка кнопки быстрого запуска обучения прямо из ClickGUI
        if (this.trainNow.getValue()) {
            this.trainNow.setValue(false);
            if (trainer != null && !trainer.isTraining()) {
                if (recorder != null && recorder.isRecording()) {
                    recorder.stopRecording();
                    this.recordDataset.setValue(false);
                }
                trainer.startTraining(200);
            }
        }

        // Синхронизация переключателя записи датасета (.csv)
        if (recorder != null) {
            if (this.recordDataset.getValue() && !recorder.isRecording()) {
                recorder.startRecording("session");
            } else if (!this.recordDataset.getValue() && recorder.isRecording()) {
                recorder.stopRecording();
            }
        }

        // Переключение весов модели при смене режима
        String currentMode = this.rotationMode.getValue();
        if (!currentMode.equals(this.lastModelMode) && engine != null) {
            this.lastModelMode = currentMode;
            if ("Custom (aim.json)".equals(currentMode)) {
                engine.loadCustomOrDefaultModel();
            } else if ("NeurAim v6".equals(currentMode)) {
                engine.loadModelFromResource("/neuro_data/models/neuraim_aim_v6.json", "neuraim_v6.json");
            } else if ("Neuro GRU AI".equals(currentMode)) {
                engine.loadModelFromResources();
            }
        }

        double maxDist = this.attackDistance.getValue();
        Box searchBox = client.player.getBoundingBox().expand(maxDist + 1.5, maxDist + 1.5, maxDist + 1.5);
        List<LivingEntity> potentialTargets = client.world.getEntitiesByClass(
                LivingEntity.class, searchBox,
                entity -> entity != client.player && entity.isAlive() && !entity.isInvisible()
        );

        this.targetEntity = potentialTargets.stream()
                .min(Comparator.comparingDouble(e -> e.squaredDistanceTo(client.player)))
                .orElse(null);

        // Если включена запись боя (.csv), записываем движения руки игрока (clean=1) и не вмешиваемся камерой
        if (recorder != null && recorder.isRecording()) {
            boolean playerAttacked = client.options.attackKey.isPressed() || client.player.handSwinging;
            recorder.onTick(client, this.targetEntity, playerAttacked, true);
            return;
        }

        if (this.targetEntity == null) {
            if (clicks != null) clicks.reset();
            return;
        }

        double dist = client.player.distanceTo(this.targetEntity);
        if (dist > maxDist) {
            this.targetEntity = null;
            return;
        }

        if (this.raycastCheck.getValue() && !canSeeEntity(client, this.targetEntity)) {
            if (dist > this.wallDistance.getValue()) return;
        }

        // Вычисление тайминга удара и W-Tap спринт-ресета через NeuroClicks
        boolean shouldHit = false;
        if (clicks != null) {
            shouldHit = clicks.shouldAttack(
                    client,
                    client.player,
                    this.targetEntity,
                    this.clicksProfile.getValue(),
                    this.smartCrits.getValue(),
                    this.sprintReset.getValue()
            );
        } else {
            shouldHit = client.player.getAttackCooldownProgress(0.5f) >= 0.92f;
        }

        boolean attackedThisTick = false;
        if (shouldHit) {
            if (this.sprintReset.getValue() && client.player.isSprinting()) {
                client.player.setSprinting(false);
            }
            client.interactionManager.attackEntity(client.player, this.targetEntity);
            client.player.swingHand(Hand.MAIN_HAND);
            attackedThisTick = true;
            if (clicks != null) {
                clicks.notifyHit(this.clicksProfile.getValue());
            }
        }

        // Применение наводки GRU + MDN с параметрами Scale, Keep, Temp
        if (engine != null && ("Neuro GRU AI".equals(currentMode)
                || "Custom (aim.json)".equals(currentMode)
                || "NeurAim v6".equals(currentMode))) {
            engine.setRolloutKnobs(
                    this.aimTemp.getValue(),
                    (int) Math.round(this.aimKeep.getValue()),
                    this.aimScale.getValue()
            );
            float[] stepAngles = engine.computeStep(client.player, this.targetEntity, attackedThisTick);
            if (!this.silentRotation.getValue()) {
                client.player.setYaw(client.player.getYaw() + stepAngles[0]);
                client.player.setPitch(MathHelper.clamp(client.player.getPitch() + stepAngles[1], -90.0f, 90.0f));
            }
        } else {
            float[] targetRot = calculateRotations(client, this.targetEntity);
            if (!this.silentRotation.getValue()) {
                float factor = "GrimAC Strict".equals(currentMode) ? 0.52f : 0.72f;
                float diffYaw = MathHelper.wrapDegrees(targetRot[0] - client.player.getYaw());
                float diffPitch = targetRot[1] - client.player.getPitch();
                client.player.setYaw(client.player.getYaw() + diffYaw * factor);
                client.player.setPitch(MathHelper.clamp(client.player.getPitch() + diffPitch * factor, -90.0f, 90.0f));
            }
        }
    }

    private boolean canSeeEntity(MinecraftClient client, Entity target) {
        Vec3d eyePos = client.player.getEyePos();
        Vec3d targetCenter = target.getBoundingBox().getCenter();
        RaycastContext context = new RaycastContext(
                eyePos, targetCenter,
                RaycastContext.ShapeType.COLLIDER,
                RaycastContext.FluidHandling.NONE,
                client.player
        );
        BlockHitResult hit = client.world.raycast(context);
        return hit.getType() == HitResult.Type.MISS;
    }

    private float[] calculateRotations(MinecraftClient client, Entity target) {
        Vec3d eyes = client.player.getEyePos();
        Vec3d targetCenter = target.getBoundingBox().getCenter();

        double diffX = targetCenter.x - eyes.x;
        double diffY = targetCenter.y - eyes.y;
        double diffZ = targetCenter.z - eyes.z;
        double diffXZ = Math.sqrt(diffX * diffX + diffZ * diffZ);

        float yaw = (float) Math.toDegrees(Math.atan2(diffZ, diffX)) - 90.0f;
        float pitch = (float) -Math.toDegrees(Math.atan2(diffY, diffXZ));

        return new float[]{MathHelper.wrapDegrees(yaw), MathHelper.clamp(pitch, -90.0f, 90.0f)};
    }

    public LivingEntity getTargetEntity() {
        return targetEntity;
    }

    public String getClicksProfile() {
        return clicksProfile.getValue();
    }

    public void setRecordDatasetFlag(boolean recording) {
        this.recordDataset.setValue(recording);
    }
}
