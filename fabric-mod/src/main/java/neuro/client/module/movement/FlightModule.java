package neuro.client.module.movement;

import net.minecraft.client.MinecraftClient;
import neuro.client.module.Category;
import neuro.client.module.Module;
import neuro.client.setting.ModeSetting;
import neuro.client.setting.SliderSetting;

import java.util.List;

public class FlightModule extends Module {
    private final ModeSetting mode = new ModeSetting("Fly Mode", "Vanilla",
            List.of("Vanilla", "Grim Glide", "Matrix Boost"));
    private final SliderSetting speed = new SliderSetting("Fly Speed", 1.5, 0.5, 5.0, 0.1);

    public FlightModule() {
        super("Flight", "Позволяет летать в режиме выживания", Category.MOVEMENT);
        this.addSettings(mode, speed);
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
