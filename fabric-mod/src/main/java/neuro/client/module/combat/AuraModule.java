package neuro.client.module.combat;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.ingame.InventoryScreen;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.entity.passive.AnimalEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.AxeItem;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.item.SwordItem;
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

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class AuraModule extends Module {
    private final ModeSetting rotationMode = new ModeSetting("Model", "Neuro GRU AI",
            List.of("Neuro GRU AI", "Custom (aim.json)", "NeurAim v6", "GrimAC Strict", "Matrix", "Snap"));
    private final ModeSetting clicksProfile = new ModeSetting("NeuroClicks", "strong",
            List.of("strong", "early", "exact", "relaxed"));
    private final SliderSetting aimScale = new SliderSetting("Scale (Knob)", 0.45, 0.0, 1.0, 0.05);
    private final SliderSetting aimKeep = new SliderSetting("Keep Samples", 4.0, 1.0, 8.0, 1.0);
    private final SliderSetting aimTemp = new SliderSetting("Temperature", 0.70, 0.10, 1.20, 0.05);

    private final SliderSetting attackDistance = new SliderSetting("Attack Dist", 3.6, 2.0, 6.0, 0.1);
    private final SliderSetting preAimDistance = new SliderSetting("Pre-Aim Dist", 4.8, 2.5, 8.0, 0.1);
    private final ModeSetting wallsMode = new ModeSetting("Walls Mode", "ReallyWorld",
            List.of("None", "All", "Doors", "ReallyWorld", "FunTime"));
    private final SliderSetting wallDistance = new SliderSetting("Wall Dist", 1.8, 0.0, 4.5, 0.1);

    private final MultiSelectSetting targets = new MultiSelectSetting("Targets",
            List.of("Players", "FakePlayer", "Mobs", "Animals", "Invisibles", "Naked Players", "Friends"),
            List.of("Players", "FakePlayer", "Mobs", "Invisibles", "Naked Players"));

    private final ModeSetting sorting = new ModeSetting("Sorting", "Distance",
            List.of("Distance", "Health", "FOV"));
    private final ModeSetting moveCorrection = new ModeSetting("Move Correction", "Silent",
            List.of("Silent", "Direct", "Targeted", "None"));
    private final ModeSetting styleAttack = new ModeSetting("Style Attack", "1.9",
            List.of("1.9", "1.8"));
    private final SliderSetting cpsLimiter = new SliderSetting("CPS Limiter", 12.0, 1.0, 20.0, 1.0);

    private final BooleanSetting onlyCrits = new BooleanSetting("Only Crits", true);
    private final BooleanSetting smartCrits = new BooleanSetting("Smart Crits", true);
    private final ModeSetting critCalc = new ModeSetting("Crit Calc", "New",
            List.of("Old", "New", "Air"));
    private final ModeSetting sprintReset = new ModeSetting("Sprint Reset", "Smart (NeuroClicks)",
            List.of("Smart (NeuroClicks)", "Normal (W-Tap)", "Packet", "None"));

    private final BooleanSetting autoMace = new BooleanSetting("Auto Mace 1.21.4", true);
    private final BooleanSetting shieldBreaker = new BooleanSetting("Shield Breaker", true);
    private final BooleanSetting onlyWeapon = new BooleanSetting("Only Weapon", false);
    private final BooleanSetting noHitInv = new BooleanSetting("No Hit In Inv", true);
    private final BooleanSetting raycastCheck = new BooleanSetting("RayTrace Check", true);
    private final BooleanSetting targetingEsp = new BooleanSetting("Targeting ESP", true);
    private final BooleanSetting silentRotation = new BooleanSetting("Silent Rotation", false);

    private final MultiSelectSetting utilities = new MultiSelectSetting("Utilities",
            List.of("Resolver", "UseHit", "Sync HurtTime", "Sync TPS", "No Teammates"),
            List.of("Resolver", "UseHit", "Sync TPS"));

    private final BooleanSetting recordDataset = new BooleanSetting("Record CSV (.neuro rec)", false);
    private final BooleanSetting trainNow = new BooleanSetting("Train AI (.neuro train)", false);

    private LivingEntity targetEntity = null;
    private String lastModelMode = "Neuro GRU AI";
    private long last18HitTime = 0L;
    private int savedSlotBeforeMace = -1;

    public AuraModule() {
        super("Aura", "Обучаемая Киллаура: GRU+MDN + NeuroClicks + AutoMace + FakePlayer", Category.COMBAT);
        this.setEnabled(true);
        this.addSettings(
                rotationMode, clicksProfile, aimScale, aimKeep, aimTemp,
                attackDistance, preAimDistance, wallsMode, wallDistance,
                targets, sorting, moveCorrection, styleAttack, cpsLimiter,
                onlyCrits, smartCrits, critCalc, sprintReset,
                autoMace, shieldBreaker, onlyWeapon, noHitInv,
                raycastCheck, targetingEsp, silentRotation, utilities,
                recordDataset, trainNow
        );
    }

    @Override
    public void onTick(MinecraftClient client) {
        if (client.player == null || client.world == null || client.interactionManager == null) {
            this.targetEntity = null;
            return;
        }

        if (this.noHitInv.getValue() && client.currentScreen instanceof InventoryScreen) {
            return;
        }

        if (this.onlyWeapon.getValue() && !isHoldingWeapon(client.player.getMainHandStack())) {
            this.targetEntity = null;
            return;
        }

        NeuroClient nc = NeuroClient.getInstance();
        NeuroAuraEngine engine = nc.getNeuroEngine();
        NeuroClicks clicks = nc.getNeuroClicks();
        NeuroDatasetRecorder recorder = nc.getDatasetRecorder();
        NeuroTrainerManager trainer = nc.getTrainerManager();
        FakePlayerModule fpMod = nc.getModuleManager().getModule(FakePlayerModule.class);

        // Кнопка быстрого запуска обучения из настроек
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

        double maxSearchDist = Math.max(this.attackDistance.getValue(), this.preAimDistance.getValue());
        Box searchBox = client.player.getBoundingBox().expand(maxSearchDist + 1.5, maxSearchDist + 1.5, maxSearchDist + 1.5);
        List<LivingEntity> candidates = new ArrayList<>(client.world.getEntitiesByClass(
                LivingEntity.class, searchBox,
                entity -> isValidTarget(client, entity, fpMod)
        ));

        // Явно добавляем FakePlayer, если он включен и находится в радиусе
        if (fpMod != null && fpMod.isEnabled() && fpMod.getFakePlayer() != null && targets.isSelected("FakePlayer")) {
            LivingEntity fp = fpMod.getFakePlayer();
            if (fp.isAlive() && client.player.distanceTo(fp) <= maxSearchDist && !candidates.contains(fp)) {
                candidates.add(fp);
            }
        }

        Comparator<LivingEntity> comp = switch (this.sorting.getValue()) {
            case "Health" -> Comparator.comparingDouble(e -> (double) (e.getHealth() + e.getAbsorptionAmount()));
            case "FOV" -> Comparator.comparingDouble(e -> Math.abs(MathHelper.wrapDegrees(calculateRotations(client, e)[0] - client.player.getYaw())));
            default -> Comparator.comparingDouble(e -> e.squaredDistanceTo(client.player));
        };

        this.targetEntity = candidates.stream().min(comp).orElse(null);

        // Если включена запись боя (.csv), записываем движения руки игрока (clean=1) и не вмешиваемся камерой
        if (recorder != null && recorder.isRecording()) {
            boolean playerAttacked = client.options.attackKey.isPressed() || client.player.handSwinging;
            recorder.onTick(client, this.targetEntity, playerAttacked, true);
            return;
        }

        if (this.targetEntity == null) {
            if (clicks != null) clicks.reset();
            restoreSlotAfterMace(client);
            return;
        }

        double dist = client.player.distanceTo(this.targetEntity);
        if (dist > maxSearchDist) {
            this.targetEntity = null;
            return;
        }

        boolean canSee = canSeeEntity(client, this.targetEntity);
        if (this.raycastCheck.getValue() && !canSee) {
            if ("None".equals(this.wallsMode.getValue()) || dist > this.wallDistance.getValue()) {
                return;
            }
        }

        // Auto Mace 1.21.4: свап на булаву при падении > 1.5 блока
        if (this.autoMace.getValue() && client.player.fallDistance > 1.5f && client.player.getVelocity().y < -0.05) {
            int maceSlot = findHotbarItem(client, Items.MACE);
            if (maceSlot != -1 && client.player.getInventory().selectedSlot != maceSlot) {
                this.savedSlotBeforeMace = client.player.getInventory().selectedSlot;
                client.player.getInventory().selectedSlot = maceSlot;
            }
        } else {
            restoreSlotAfterMace(client);
        }

        // Shield Breaker: если цель блокирует щитом, сбиваем топором
        if (this.shieldBreaker.getValue() && this.targetEntity.isBlocking()) {
            int axeSlot = findAxeInHotbar(client);
            if (axeSlot != -1) {
                int prev = client.player.getInventory().selectedSlot;
                client.player.getInventory().selectedSlot = axeSlot;
                client.interactionManager.attackEntity(client.player, this.targetEntity);
                client.player.swingHand(Hand.MAIN_HAND);
                client.player.getInventory().selectedSlot = prev;
            }
        }

        boolean withinAttackDist = dist <= this.attackDistance.getValue();
        boolean shouldHit = false;

        if (withinAttackDist) {
            if (utilities.isSelected("Sync HurtTime") && client.player.hurtTime > 4) {
                shouldHit = false;
            } else if ("1.8".equals(this.styleAttack.getValue())) {
                long delayMs = (long) (1000.0 / Math.max(1.0, this.cpsLimiter.getValue()));
                shouldHit = (System.currentTimeMillis() - this.last18HitTime) >= delayMs;
            } else if (clicks != null) {
                shouldHit = clicks.shouldAttack(
                        client,
                        client.player,
                        this.targetEntity,
                        this.clicksProfile.getValue(),
                        this.onlyCrits.getValue() && this.smartCrits.getValue(),
                        !"None".equals(this.sprintReset.getValue())
                );
            } else {
                shouldHit = client.player.getAttackCooldownProgress(0.5f) >= 0.92f;
            }
        }

        boolean attackedThisTick = false;
        if (shouldHit) {
            if (!"None".equals(this.sprintReset.getValue()) && client.player.isSprinting()) {
                client.player.setSprinting(false);
            }
            if (fpMod != null && this.targetEntity == fpMod.getFakePlayer()) {
                fpMod.onHitByPlayer(client.player, 6.5f);
            } else {
                client.interactionManager.attackEntity(client.player, this.targetEntity);
            }
            client.player.swingHand(Hand.MAIN_HAND);
            this.last18HitTime = System.currentTimeMillis();
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
                float factor = "Snap".equals(currentMode) ? 1.0f : ("GrimAC Strict".equals(currentMode) ? 0.52f : 0.72f);
                float diffYaw = MathHelper.wrapDegrees(targetRot[0] - client.player.getYaw());
                float diffPitch = targetRot[1] - client.player.getPitch();
                client.player.setYaw(client.player.getYaw() + diffYaw * factor);
                client.player.setPitch(MathHelper.clamp(client.player.getPitch() + diffPitch * factor, -90.0f, 90.0f));
            }
        }
    }

    private boolean isValidTarget(MinecraftClient client, LivingEntity entity, FakePlayerModule fpMod) {
        if (entity == client.player || !entity.isAlive()) return false;
        if (fpMod != null && entity == fpMod.getFakePlayer()) {
            return targets.isSelected("FakePlayer");
        }
        if (entity.isInvisible() && !targets.isSelected("Invisibles")) return false;
        if (entity instanceof PlayerEntity player) {
            AntiBotModule antiBot = NeuroClient.getInstance().getModuleManager().getModule(AntiBotModule.class);
            if (antiBot != null && antiBot.isEnabled() && antiBot.isBot(player)) return false;
            return targets.isSelected("Players");
        }
        if (entity instanceof AnimalEntity) return targets.isSelected("Animals");
        if (entity instanceof MobEntity) return targets.isSelected("Mobs");
        return true;
    }

    private boolean isHoldingWeapon(ItemStack stack) {
        if (stack == null || stack.isEmpty()) return false;
        return stack.getItem() instanceof SwordItem
                || stack.getItem() instanceof AxeItem
                || stack.isOf(Items.MACE)
                || stack.isOf(Items.TRIDENT);
    }

    private int findHotbarItem(MinecraftClient client, net.minecraft.item.Item item) {
        for (int i = 0; i < 9; i++) {
            if (client.player.getInventory().getStack(i).isOf(item)) return i;
        }
        return -1;
    }

    private int findAxeInHotbar(MinecraftClient client) {
        for (int i = 0; i < 9; i++) {
            if (client.player.getInventory().getStack(i).getItem() instanceof AxeItem) return i;
        }
        return -1;
    }

    private void restoreSlotAfterMace(MinecraftClient client) {
        if (this.savedSlotBeforeMace != -1 && client.player != null) {
            client.player.getInventory().selectedSlot = this.savedSlotBeforeMace;
            this.savedSlotBeforeMace = -1;
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
