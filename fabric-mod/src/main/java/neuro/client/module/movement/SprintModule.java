package neuro.client.module.movement;

import net.minecraft.client.MinecraftClient;
import neuro.client.module.Category;
import neuro.client.module.Module;
import neuro.client.setting.BooleanSetting;

public class SprintModule extends Module {
    private final BooleanSetting omni = new BooleanSetting("Omni Sprint", true);

    public SprintModule() {
        super("Sprint", "Автоматический бег без зажатия клавиши", Category.MOVEMENT);
        this.setEnabled(true);
        this.addSettings(omni);
    }

    @Override
    public void onTick(MinecraftClient client) {
        if (client.player != null && client.player.forwardSpeed > 0 && !client.player.isSneaking()) {
            client.player.setSprinting(true);
        }
    }
}
