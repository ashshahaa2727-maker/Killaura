package neuro.client.module.combat;

import net.minecraft.client.MinecraftClient;
import net.minecraft.item.Items;
import net.minecraft.screen.slot.SlotActionType;
import neuro.client.module.Category;
import neuro.client.module.Module;
import neuro.client.setting.BooleanSetting;
import neuro.client.setting.SliderSetting;

public class AutoTotemModule extends Module {
    private final SliderSetting health = new SliderSetting("Health Threshold", 8.0, 2.0, 20.0, 0.5);
    private final BooleanSetting swapBack = new BooleanSetting("Swap Back", true);

    public AutoTotemModule() {
        super("AutoTotem", "Автоматический тотем в левую руку", Category.COMBAT);
        this.setEnabled(true);
        this.addSettings(health, swapBack);
    }

    @Override
    public void onTick(MinecraftClient client) {
        if (client.player == null || client.interactionManager == null) return;
        if (client.player.getOffHandStack().getItem() == Items.TOTEM_OF_UNDYING) return;

        if (client.player.getHealth() + client.player.getAbsorptionAmount() <= health.getValue()) {
            for (int i = 9; i < 36; i++) {
                if (client.player.getInventory().getStack(i).getItem() == Items.TOTEM_OF_UNDYING) {
                    client.interactionManager.clickSlot(client.player.playerScreenHandler.syncId, i, 40, SlotActionType.SWAP, client.player);
                    break;
                }
            }
        }
    }
}
