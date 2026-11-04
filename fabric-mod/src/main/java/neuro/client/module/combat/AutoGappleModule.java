package neuro.client.module.combat;

import net.minecraft.client.MinecraftClient;
import net.minecraft.item.Items;
import neuro.client.module.Category;
import neuro.client.module.Module;
import neuro.client.setting.SliderSetting;

public class AutoGappleModule extends Module {
    private final SliderSetting eatHealth = new SliderSetting("Eat Health", 14.0, 4.0, 20.0, 0.5);
    private boolean eating = false;

    public AutoGappleModule() {
        super("AutoGapple", "Авто-поедание золотого яблока в левой руке по HP", Category.COMBAT);
        this.addSettings(eatHealth);
    }

    @Override
    public void onTick(MinecraftClient client) {
        if (client.player == null) return;
        boolean hasGapple = client.player.getOffHandStack().isOf(Items.GOLDEN_APPLE)
                || client.player.getOffHandStack().isOf(Items.ENCHANTED_GOLDEN_APPLE);
        if (hasGapple && client.player.getHealth() <= eatHealth.getValue()) {
            client.options.useKey.setPressed(true);
            eating = true;
        } else if (eating) {
            client.options.useKey.setPressed(false);
            eating = false;
        }
    }
}
