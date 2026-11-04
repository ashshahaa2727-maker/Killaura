package neuro.client.module.movement;

import net.minecraft.client.MinecraftClient;
import net.minecraft.entity.LivingEntity;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import neuro.client.NeuroClient;
import neuro.client.module.Category;
import neuro.client.module.Module;
import neuro.client.module.combat.AuraModule;
import neuro.client.setting.BooleanSetting;
import neuro.client.setting.SliderSetting;

public class ElytraTargetModule extends Module {
    private final SliderSetting fireworkDelay = new SliderSetting("Firework Delay (s)", 1.4, 0.4, 4.0, 0.1);
    private final BooleanSetting autoFirework = new BooleanSetting("Auto Firework", true);

    public ElytraTargetModule() {
        super("ElytraTarget", "Погоня за целью Киллауры на элитрах", Category.MOVEMENT);
        this.addSettings(fireworkDelay, autoFirework);
    }

    @Override
    public void onTick(MinecraftClient client) {
        if (client.player == null || !client.player.isGliding()) return;
        AuraModule aura = NeuroClient.getInstance().getModuleManager().getModule(AuraModule.class);
        if (aura == null || aura.getTargetEntity() == null) return;
        LivingEntity target = aura.getTargetEntity();
        Vec3d diff = target.getBoundingBox().getCenter().subtract(client.player.getEyePos());
        float yaw = (float) Math.toDegrees(Math.atan2(diff.z, diff.x)) - 90.0f;
        float pitch = (float) -Math.toDegrees(Math.atan2(diff.y, Math.hypot(diff.x, diff.z)));
        client.player.setYaw(MathHelper.wrapDegrees(yaw));
        client.player.setPitch(MathHelper.clamp(pitch, -89.0f, 89.0f));
    }
}
