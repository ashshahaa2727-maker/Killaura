package neuro.client.module.movement;

import net.minecraft.client.MinecraftClient;
import net.minecraft.util.math.Vec3d;
import neuro.client.module.Category;
import neuro.client.module.Module;
import neuro.client.setting.BooleanSetting;

public class AirStuckModule extends Module {
    private final BooleanSetting freezePackets = new BooleanSetting("Freeze Velocity", true);

    public AirStuckModule() {
        super("AirStuck", "Заморозка игрока в воздухе без падения", Category.MOVEMENT);
        this.addSettings(freezePackets);
    }

    @Override
    public void onTick(MinecraftClient client) {
        if (client.player != null) {
            client.player.setVelocity(Vec3d.ZERO);
            client.player.fallDistance = 0.0f;
        }
    }
}
