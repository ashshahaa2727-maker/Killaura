package neuro.client.module.movement;

import net.minecraft.client.MinecraftClient;
import net.minecraft.util.math.Vec3d;
import neuro.client.module.Category;
import neuro.client.module.Module;
import neuro.client.setting.ModeSetting;

import java.util.List;

public class JesusModule extends Module {
    private final ModeSetting mode = new ModeSetting("Mode", "Solid Dolphin",
            List.of("Solid Dolphin", "Bounce Hop", "Matrix Water"));

    public JesusModule() {
        super("Jesus", "Хождение по воде и лаве как по твёрдым блокам", Category.MOVEMENT);
        this.addSettings(mode);
    }

    @Override
    public void onTick(MinecraftClient client) {
        if (client.player != null && (client.player.isTouchingWater() || client.player.isInLava())) {
            Vec3d v = client.player.getVelocity();
            client.player.setVelocity(v.x, 0.11, v.z);
        }
    }
}
