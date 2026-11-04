package neuro.client.module.movement;

import net.minecraft.client.MinecraftClient;
import net.minecraft.util.math.Vec3d;
import neuro.client.module.Category;
import neuro.client.module.Module;
import neuro.client.setting.SliderSetting;

public class SpiderModule extends Module {
    private final SliderSetting climbSpeed = new SliderSetting("Climb Speed", 0.42, 0.20, 1.00, 0.02);

    public SpiderModule() {
        super("Spider", "Позволяет взбираться по вертикальным стенам", Category.MOVEMENT);
        this.addSettings(climbSpeed);
    }

    @Override
    public void onTick(MinecraftClient client) {
        if (client.player != null && client.player.horizontalCollision) {
            Vec3d v = client.player.getVelocity();
            client.player.setVelocity(v.x, climbSpeed.getValue(), v.z);
        }
    }
}
