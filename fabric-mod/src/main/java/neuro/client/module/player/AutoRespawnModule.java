package neuro.client.module.player;

import net.minecraft.client.MinecraftClient;
import neuro.client.module.Category;
import neuro.client.module.Module;
import neuro.client.setting.BooleanSetting;

public class AutoRespawnModule extends Module {
    private final BooleanSetting autoHome = new BooleanSetting("Auto /home", false);

    public AutoRespawnModule() {
        super("AutoRespawn", "Мгновенное возрождение после смерти", Category.PLAYER);
        this.setEnabled(true);
        this.addSettings(autoHome);
    }

    @Override
    public void onTick(MinecraftClient client) {
        if (client.player != null && client.player.isDead()) {
            client.player.requestRespawn();
            client.setScreen(null);
        }
    }
}
