package neuro.client.module.movement;

import net.minecraft.client.MinecraftClient;
import net.minecraft.entity.LivingEntity;
import neuro.client.NeuroClient;
import neuro.client.module.Category;
import neuro.client.module.Module;
import neuro.client.module.combat.AuraModule;
import neuro.client.setting.BooleanSetting;
import neuro.client.setting.SliderSetting;

public class TargetStrafeModule extends Module {
    private final SliderSetting radius = new SliderSetting("Radius", 2.4, 1.0, 4.5, 0.1);
    private final SliderSetting speed = new SliderSetting("Strafe Speed", 0.24, 0.10, 0.55, 0.02);
    private final BooleanSetting autoJump = new BooleanSetting("Auto Jump", true);
    private double orbitAngle = 0.0;

    public TargetStrafeModule() {
        super("TargetStrafe", "Кружение вокруг цели Киллауры и FakePlayer", Category.MOVEMENT);
        this.addSettings(radius, speed, autoJump);
    }

    @Override
    public void onTick(MinecraftClient client) {
        if (client.player == null) return;
        AuraModule aura = NeuroClient.getInstance().getModuleManager().getModule(AuraModule.class);
        if (aura == null || !aura.isEnabled() || aura.getTargetEntity() == null) return;
        LivingEntity target = aura.getTargetEntity();

        if (autoJump.getValue() && client.player.isOnGround()) {
            client.player.jump();
        }
        this.orbitAngle += speed.getValue();
        double tx = target.getX() + Math.cos(this.orbitAngle) * radius.getValue();
        double tz = target.getZ() + Math.sin(this.orbitAngle) * radius.getValue();
        double vx = (tx - client.player.getX()) * 0.25;
        double vz = (tz - client.player.getZ()) * 0.25;
        client.player.setVelocity(vx, client.player.getVelocity().y, vz);
    }
}
