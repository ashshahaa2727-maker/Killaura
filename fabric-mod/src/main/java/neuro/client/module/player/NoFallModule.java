package neuro.client.module.player;

import net.minecraft.client.MinecraftClient;
import neuro.client.module.Category;
import neuro.client.module.Module;
import neuro.client.setting.ModeSetting;

import java.util.List;

public class NoFallModule extends Module {
    private final ModeSetting mode = new ModeSetting("Mode", "Grim GroundSpoof",
            List.of("Grim GroundSpoof", "Packet OnGround", "Water Bucket Clutch"));

    public NoFallModule() {
        super("NoFall", "Отключение урона от падения", Category.PLAYER);
        this.setEnabled(true);
        this.addSettings(mode);
    }

    @Override
    public void onTick(MinecraftClient client) {
        if (client.player != null && client.player.fallDistance > 2.8f) {
            client.player.fallDistance = 0.0f;
        }
    }
}
