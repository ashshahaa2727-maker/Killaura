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
import neuro.client.module.Category;
import neuro.client.module.Module;
import neuro.client.setting.*;

import java.util.Comparator;
import java.util.List;

public class AuraModule extends Module {
    private final ModeSetting rotationMode = new ModeSetting("Rotation Mode", "Neuro GRU AI",
            List.of("Neuro GRU AI", "Matrix Prediction", "GrimAC Strict", "Dynamic Smooth", "Packet Legit"));
    private final SliderSetting attackDistance = new SliderSetting("Attack Distance", 3.6, 2.0, 6.0, 0.1);
    private final SliderSetting wallDistance = new SliderSetting("Wall Distance", 1.8, 0.0, 4.5, 0.1);
    private final SliderSetting preAimLatency = new SliderSetting("Pre-Aim Latency", 80, 0, 300, 10);
    private final BooleanSetting raycastCheck = new BooleanSetting("Raycast Check", true);
    private final BooleanSetting autoShieldBreaker = new BooleanSetting("Auto Shield Breaker", true);
    private final BooleanSetting silentRotation = new BooleanSetting("Silent Rotation", false);

    private LivingEntity targetEntity = null;

    public AuraModule() {
        super("Aura", "Интеллектуальная киллаура с нейросетевой наводкой GRU", Category.COMBAT);
        this.setEnabled(true);
        this.addSettings(rotationMode, attackDistance, wallDistance, preAimLatency,
                raycastCheck, autoShieldBreaker, silentRotation);
    }

    @Override
    public void onTick(MinecraftClient client) {
        if (client.player == null || client.world == null || client.interactionManager == null) {
            this.targetEntity = null;
            return;
        }

        double maxDist = this.attackDistance.getValue();
        Box searchBox = client.player.getBoundingBox().expand(maxDist, maxDist, maxDist);
        List<LivingEntity> potentialTargets = client.world.getEntitiesByClass(
                LivingEntity.class, searchBox,
                entity -> entity != client.player && entity.isAlive() && !entity.isInvisible()
        );

        this.targetEntity = potentialTargets.stream()
                .min(Comparator.comparingDouble(e -> e.squaredDistanceTo(client.player)))
                .orElse(null);

        if (this.targetEntity == null) return;

        double dist = client.player.distanceTo(this.targetEntity);
        if (dist > maxDist) {
            this.targetEntity = null;
            return;
        }

        if (this.raycastCheck.getValue() && !canSeeEntity(client, this.targetEntity)) {
            if (dist > this.wallDistance.getValue()) return;
        }

        boolean attackedThisTick = false;
        float cooldown = client.player.getAttackCooldownProgress(0.5f);
        if (cooldown >= 1.0f) {
            client.interactionManager.attackEntity(client.player, this.targetEntity);
            client.player.swingHand(Hand.MAIN_HAND);
            attackedThisTick = true;
        }

        if ("Neuro GRU AI".equals(this.rotationMode.getValue())) {
            NeuroAuraEngine engine = NeuroClient.getInstance().getNeuroEngine();
            float[] stepAngles = engine.computeStep(client.player, this.targetEntity, attackedThisTick);
            if (!this.silentRotation.getValue()) {
                client.player.setYaw(client.player.getYaw() + stepAngles[0]);
                client.player.setPitch(MathHelper.clamp(client.player.getPitch() + stepAngles[1], -90.0f, 90.0f));
            }
        } else {
            float[] targetRot = calculateRotations(client, this.targetEntity);
            if (!this.silentRotation.getValue()) {
                float diffYaw = MathHelper.wrapDegrees(targetRot[0] - client.player.getYaw());
                float diffPitch = targetRot[1] - client.player.getPitch();
                client.player.setYaw(client.player.getYaw() + diffYaw * 0.65f);
                client.player.setPitch(MathHelper.clamp(client.player.getPitch() + diffPitch * 0.65f, -90.0f, 90.0f));
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
}
