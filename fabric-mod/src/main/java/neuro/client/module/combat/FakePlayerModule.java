package neuro.client.module.combat;

import com.mojang.authlib.GameProfile;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.OtherClientPlayerEntity;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.text.Text;
import net.minecraft.util.Hand;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import neuro.client.module.Category;
import neuro.client.module.Module;
import neuro.client.setting.BooleanSetting;
import neuro.client.setting.ModeSetting;
import neuro.client.setting.SliderSetting;

import java.util.List;
import java.util.UUID;

public class FakePlayerModule extends Module {
    private final ModeSetting moveMode = new ModeSetting("Bot Behavior", "Strafe & Jump (PvP Bot)",
            List.of("Strafe & Jump (PvP Bot)", "Circle Around", "Static Dummy", "Mirror Player", "Run & Heal"));
    private final ModeSetting equipment = new ModeSetting("Equipment", "Netherite Max + Totem",
            List.of("Netherite Max + Totem", "Diamond PvP", "Shield + Axe", "Naked Target"));
    private final BooleanSetting infiniteTotems = new BooleanSetting("Infinite Totems & Regen", true);
    private final BooleanSetting simulateKnockback = new BooleanSetting("Simulate Knockback", true);
    private final SliderSetting strafeSpeed = new SliderSetting("Bot Strafe Speed", 1.0, 0.2, 2.2, 0.1);

    private OtherClientPlayerEntity fakePlayer = null;
    private Vec3d spawnOrigin = null;
    private int botTicks = 0;
    private int totemsPopped = 0;
    private int lastManualHitTick = -99;

    public FakePlayerModule() {
        super("FakePlayer", "Спавнит живого PvP-бота для теста и обучения Киллауры (.fp)", Category.COMBAT);
        this.addSettings(moveMode, equipment, infiniteTotems, simulateKnockback, strafeSpeed);
    }

    @Override
    public void onEnable() {
        MinecraftClient client = MinecraftClient.getInstance();
        if (client == null || client.player == null || client.world == null) return;
        spawnFakePlayer(client);
    }

    @Override
    public void onDisable() {
        despawnFakePlayer();
    }

    public void spawnFakePlayer(MinecraftClient client) {
        despawnFakePlayer();
        if (client.player == null || client.world == null) return;

        GameProfile profile = new GameProfile(UUID.fromString("66666666-6666-4666-8666-666666666601"), "NeuroBot");
        this.fakePlayer = new OtherClientPlayerEntity(client.world, profile);
        this.fakePlayer.setId(-202642);

        Vec3d look = client.player.getRotationVec(1.0f);
        double sx = client.player.getX() + look.x * 2.6;
        double sy = client.player.getY();
        double sz = client.player.getZ() + look.z * 2.6;
        this.spawnOrigin = new Vec3d(sx, sy, sz);

        this.fakePlayer.refreshPositionAndAngles(sx, sy, sz, MathHelper.wrapDegrees(client.player.getYaw() + 180.0f), 0.0f);
        this.fakePlayer.setHeadYaw(this.fakePlayer.getYaw());
        this.fakePlayer.setBodyYaw(this.fakePlayer.getYaw());
        this.fakePlayer.setHealth(20.0f);
        this.fakePlayer.setAbsorptionAmount(16.0f);

        applyEquipment(this.fakePlayer);
        updateBotNametag();

        client.world.addEntity(this.fakePlayer);
        this.botTicks = 0;
        this.totemsPopped = 0;

        client.player.sendMessage(Text.literal(
                "§d[FakePlayer] §aФейк-Плеер §fNeuroBot §aзаспавнен! §7Режим: §e" + moveMode.getValue()
                        + " §7| Запись датасета: §d.neuro rec"), false);
    }

    private void applyEquipment(OtherClientPlayerEntity bot) {
        String eq = equipment.getValue();
        bot.equipStack(EquipmentSlot.HEAD, ItemStack.EMPTY);
        bot.equipStack(EquipmentSlot.CHEST, ItemStack.EMPTY);
        bot.equipStack(EquipmentSlot.LEGS, ItemStack.EMPTY);
        bot.equipStack(EquipmentSlot.FEET, ItemStack.EMPTY);
        bot.equipStack(EquipmentSlot.MAINHAND, ItemStack.EMPTY);
        bot.equipStack(EquipmentSlot.OFFHAND, ItemStack.EMPTY);

        if ("Netherite Max + Totem".equals(eq)) {
            bot.equipStack(EquipmentSlot.HEAD, new ItemStack(Items.NETHERITE_HELMET));
            bot.equipStack(EquipmentSlot.CHEST, new ItemStack(Items.NETHERITE_CHESTPLATE));
            bot.equipStack(EquipmentSlot.LEGS, new ItemStack(Items.NETHERITE_LEGGINGS));
            bot.equipStack(EquipmentSlot.FEET, new ItemStack(Items.NETHERITE_BOOTS));
            bot.equipStack(EquipmentSlot.MAINHAND, new ItemStack(Items.NETHERITE_SWORD));
            bot.equipStack(EquipmentSlot.OFFHAND, new ItemStack(Items.TOTEM_OF_UNDYING));
        } else if ("Diamond PvP".equals(eq)) {
            bot.equipStack(EquipmentSlot.HEAD, new ItemStack(Items.DIAMOND_HELMET));
            bot.equipStack(EquipmentSlot.CHEST, new ItemStack(Items.DIAMOND_CHESTPLATE));
            bot.equipStack(EquipmentSlot.LEGS, new ItemStack(Items.DIAMOND_LEGGINGS));
            bot.equipStack(EquipmentSlot.FEET, new ItemStack(Items.DIAMOND_BOOTS));
            bot.equipStack(EquipmentSlot.MAINHAND, new ItemStack(Items.DIAMOND_SWORD));
            bot.equipStack(EquipmentSlot.OFFHAND, new ItemStack(Items.GOLDEN_APPLE, 64));
        } else if ("Shield + Axe".equals(eq)) {
            bot.equipStack(EquipmentSlot.HEAD, new ItemStack(Items.NETHERITE_HELMET));
            bot.equipStack(EquipmentSlot.CHEST, new ItemStack(Items.NETHERITE_CHESTPLATE));
            bot.equipStack(EquipmentSlot.LEGS, new ItemStack(Items.NETHERITE_LEGGINGS));
            bot.equipStack(EquipmentSlot.FEET, new ItemStack(Items.NETHERITE_BOOTS));
            bot.equipStack(EquipmentSlot.MAINHAND, new ItemStack(Items.NETHERITE_AXE));
            bot.equipStack(EquipmentSlot.OFFHAND, new ItemStack(Items.SHIELD));
        }
    }

    public void despawnFakePlayer() {
        if (this.fakePlayer != null) {
            this.fakePlayer.discard();
            this.fakePlayer = null;
        }
    }

    @Override
    public void onTick(MinecraftClient client) {
        if (client.player == null || client.world == null) return;
        if (this.fakePlayer == null || this.fakePlayer.getWorld() != client.world || !this.fakePlayer.isAlive()) {
            spawnFakePlayer(client);
            if (this.fakePlayer == null) return;
        }

        this.botTicks++;
        applyEquipment(this.fakePlayer);

        if (this.fakePlayer.hurtTime > 0) {
            this.fakePlayer.hurtTime--;
        }

        // Регенерация здоровья
        if (this.infiniteTotems.getValue() && this.botTicks % 15 == 0 && this.fakePlayer.getHealth() < 20.0f) {
            this.fakePlayer.setHealth(Math.min(20.0f, this.fakePlayer.getHealth() + 1.0f));
        }

        // Детект ручного удара игрока (например, во время записи .neuro record с выключенной Аурой)
        if ((client.options.attackKey.isPressed() || client.player.handSwinging)
                && this.botTicks - this.lastManualHitTick >= 6
                && client.player.distanceTo(this.fakePlayer) <= 4.2f) {
            this.lastManualHitTick = this.botTicks;
            onHitByPlayer(client.player, 5.5f);
        }

        // Поворот бота лицом к игроку
        double dx = client.player.getX() - this.fakePlayer.getX();
        double dy = client.player.getEyeY() - this.fakePlayer.getEyeY();
        double dz = client.player.getZ() - this.fakePlayer.getZ();
        float yawToPlayer = (float) Math.toDegrees(Math.atan2(dz, dx)) - 90.0f;
        float pitchToPlayer = (float) -Math.toDegrees(Math.atan2(dy, Math.hypot(dx, dz)));

        String behavior = this.moveMode.getValue();
        double spd = this.strafeSpeed.getValue() * 0.14;

        this.fakePlayer.prevX = this.fakePlayer.getX();
        this.fakePlayer.prevY = this.fakePlayer.getY();
        this.fakePlayer.prevZ = this.fakePlayer.getZ();
        this.fakePlayer.prevYaw = this.fakePlayer.getYaw();
        this.fakePlayer.prevPitch = this.fakePlayer.getPitch();

        Vec3d vel = this.fakePlayer.getVelocity();
        double nextVx = vel.x * 0.78;
        double nextVy = vel.y;
        double nextVz = vel.z * 0.78;

        double groundY = this.spawnOrigin != null ? this.spawnOrigin.y : client.player.getY();

        if ("Strafe & Jump (PvP Bot)".equals(behavior) && this.spawnOrigin != null) {
            double phase = this.botTicks * 0.085 * this.strafeSpeed.getValue();
            double targetX = this.spawnOrigin.x + Math.sin(phase) * 2.2;
            double targetZ = this.spawnOrigin.z + Math.cos(phase * 0.7) * 1.6;
            nextVx += (targetX - this.fakePlayer.getX()) * 0.18;
            nextVz += (targetZ - this.fakePlayer.getZ()) * 0.18;
            if (this.fakePlayer.getY() <= groundY + 0.02 && this.botTicks % 18 == 0) {
                nextVy = 0.42;
                this.fakePlayer.swingHand(Hand.MAIN_HAND);
            }
        } else if ("Circle Around".equals(behavior)) {
            double angle = this.botTicks * 0.075 * this.strafeSpeed.getValue();
            double targetX = client.player.getX() + Math.cos(angle) * 2.7;
            double targetZ = client.player.getZ() + Math.sin(angle) * 2.7;
            nextVx = (targetX - this.fakePlayer.getX()) * 0.35;
            nextVz = (targetZ - this.fakePlayer.getZ()) * 0.35;
            groundY = client.player.getY();
        } else if ("Mirror Player".equals(behavior)) {
            nextVx = client.player.getVelocity().x;
            nextVz = client.player.getVelocity().z;
            yawToPlayer = client.player.getYaw();
            pitchToPlayer = client.player.getPitch();
        } else if ("Run & Heal".equals(behavior) && this.spawnOrigin != null) {
            double phase = this.botTicks * 0.06 * this.strafeSpeed.getValue();
            double targetX = this.spawnOrigin.x + Math.cos(phase) * 3.5;
            double targetZ = this.spawnOrigin.z + Math.sin(phase) * 3.5;
            nextVx += (targetX - this.fakePlayer.getX()) * 0.15 + spd * 0.2;
            nextVz += (targetZ - this.fakePlayer.getZ()) * 0.15;
        }

        // Гравитация и опора на землю
        nextVy -= 0.08;
        double newX = this.fakePlayer.getX() + nextVx;
        double newY = this.fakePlayer.getY() + nextVy;
        double newZ = this.fakePlayer.getZ() + nextVz;
        if (newY <= groundY) {
            newY = groundY;
            nextVy = 0.0;
            this.fakePlayer.setOnGround(true);
        } else {
            this.fakePlayer.setOnGround(false);
        }

        this.fakePlayer.setVelocity(nextVx, nextVy, nextVz);
        this.fakePlayer.setPosition(newX, newY, newZ);
        this.fakePlayer.setYaw(yawToPlayer);
        this.fakePlayer.setPitch(pitchToPlayer);
        this.fakePlayer.setHeadYaw(yawToPlayer);
        this.fakePlayer.setBodyYaw(yawToPlayer);

        updateBotNametag();
    }

    public void onHitByPlayer(PlayerEntity attacker, float baseDamage) {
        if (this.fakePlayer == null || attacker == null) return;
        if (this.fakePlayer.hurtTime > 4) return;

        boolean isCrit = attacker.fallDistance > 0.05f && !attacker.isOnGround();
        boolean isMace = attacker.getMainHandStack().isOf(Items.MACE) && attacker.fallDistance > 1.5f;
        float dmg = baseDamage * (isMace ? 2.6f : (isCrit ? 1.5f : 1.0f));

        this.fakePlayer.hurtTime = 10;
        this.fakePlayer.maxHurtTime = 10;

        if (this.simulateKnockback.getValue()) {
            double dx = this.fakePlayer.getX() - attacker.getX();
            double dz = this.fakePlayer.getZ() - attacker.getZ();
            double len = Math.max(0.01, Math.hypot(dx, dz));
            double kbStrength = attacker.isSprinting() ? 0.52 : 0.36;
            this.fakePlayer.setVelocity(
                    (dx / len) * kbStrength,
                    0.36,
                    (dz / len) * kbStrength
            );
        }

        float abs = this.fakePlayer.getAbsorptionAmount();
        if (abs > 0) {
            float absorbed = Math.min(abs, dmg);
            this.fakePlayer.setAbsorptionAmount(abs - absorbed);
            dmg -= absorbed;
        }
        float newHp = this.fakePlayer.getHealth() - dmg;
        if (newHp <= 1.0f && this.infiniteTotems.getValue()) {
            this.totemsPopped++;
            this.fakePlayer.setHealth(16.0f);
            this.fakePlayer.setAbsorptionAmount(8.0f);
            attacker.sendMessage(Text.literal(
                    "§d[FakePlayer] §e⚡ Снесён тотем у §fNeuroBot §7(всего тотемов: §6" + this.totemsPopped + "§7)"), true);
        } else {
            this.fakePlayer.setHealth(Math.max(1.0f, newHp));
        }
        updateBotNametag();
    }

    private void updateBotNametag() {
        if (this.fakePlayer == null) return;
        float totalHp = this.fakePlayer.getHealth() + this.fakePlayer.getAbsorptionAmount();
        this.fakePlayer.setCustomName(Text.literal(
                String.format("§d§lNeuroBot §7[§a%.1f❤ §7| §6%d Totems§7]", totalHp, this.totemsPopped)));
        this.fakePlayer.setCustomNameVisible(true);
    }

    public OtherClientPlayerEntity getFakePlayer() {
        return fakePlayer;
    }
}
