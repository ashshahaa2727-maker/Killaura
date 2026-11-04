package neuro.client.module.movement;

import net.minecraft.client.MinecraftClient;
import neuro.client.module.Category;
import neuro.client.module.Module;
import neuro.client.setting.ModeSetting;
import neuro.client.setting.SliderSetting;

import java.util.List;

public class FlightModule extends Module {
    private final ModeSetting mode = new ModeSetting("Fly Mode", "Grim Disabler",
            List.of("Grim Disabler", "Matrix Boost", "Vanilla Creative", "Glide Slow", "AirWalk"));
    private final SliderSetting speed = new SliderSetting("Horizontal Speed", 1.6, 0.5, 5.0, 0.1);
    private final SliderSetting vSpeed = new SliderSetting("Vertical Speed", 0.8, 0.2, 3.0, 0.1);

    public FlightModule() {
        super("Flight", "Позволяет летать в режиме выживания", Category.MOVEMENT);
        this.addSettings(mode, speed, vSpeed);
    }

    @Override
    public void onTick(MinecraftClient client) {
        if (client.player != null) {
            client.player.getAbilities().flying = true;
            client.player.getAbilities().setFlySpeed((float) (speed.getValue() * 0.05));
        }
    }

    @Override
    public void onDisable() {
        MinecraftClient client = MinecraftClient.getInstance();
        if (client.player != null && !client.player.isCreative()) {
            client.player.getAbilities().flying = false;
            client.player.getAbilities().setFlySpeed(0.05f);
        }
    }
}
