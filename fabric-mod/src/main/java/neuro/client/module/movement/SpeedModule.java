package neuro.client.module.movement;

import net.minecraft.client.MinecraftClient;
import net.minecraft.util.math.Vec3d;
import neuro.client.module.Category;
import neuro.client.module.Module;
import neuro.client.setting.ModeSetting;
import neuro.client.setting.SliderSetting;

import java.util.List;

public class SpeedModule extends Module {
    private final ModeSetting mode = new ModeSetting("Speed Mode", "Bhop Strafe",
            List.of("Bhop Strafe", "Ground Fast", "LowHop"));
    private final SliderSetting factor = new SliderSetting("Multiplier", 1.2, 1.0, 2.5, 0.05);

    public SpeedModule() {
        super("Speed", "Ускоренное передвижение и распрыжка", Category.MOVEMENT);
        this.addSettings(mode, factor);
    }

    @Override
    public void onTick(MinecraftClient client) {
        if (client.player == null) return;
        if (client.player.forwardSpeed != 0 || client.player.sidewaysSpeed != 0) {
            if (client.player.isOnGround() && "Bhop Strafe".equals(mode.getValue())) {
                client.player.jump();
            }
            Vec3d v = client.player.getVelocity();
            double mul = 1.0 + (factor.getValue() - 1.0) * 0.08;
            client.player.setVelocity(v.x * mul, v.y, v.z * mul);
        }
    }
}
