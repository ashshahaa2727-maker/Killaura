package neuro.client.module.combat;

import net.minecraft.client.MinecraftClient;
import net.minecraft.item.Items;
import neuro.client.module.Category;
import neuro.client.module.Module;
import neuro.client.setting.BooleanSetting;
import neuro.client.setting.SliderSetting;

public class AutoMaceModule extends Module {
    private final SliderSetting minFall = new SliderSetting("Min Fall Height", 1.5, 1.0, 6.0, 0.5);
    private final BooleanSetting windChargeBoost = new BooleanSetting("Auto Wind Charge", true);
    private final BooleanSetting swapBack = new BooleanSetting("Swap Back Sword", true);

    public AutoMaceModule() {
        super("AutoMace", "Авто-свап на Булаву 1.21.4 при падении и комбо с Зарядом Ветра", Category.COMBAT);
        this.setEnabled(true);
        this.addSettings(minFall, windChargeBoost, swapBack);
    }

    @Override
    public void onTick(MinecraftClient client) {
        if (client.player == null) return;
        if (client.player.fallDistance >= minFall.getValue() && client.player.getVelocity().y < -0.08) {
            for (int i = 0; i < 9; i++) {
                if (client.player.getInventory().getStack(i).isOf(Items.MACE)) {
                    client.player.getInventory().selectedSlot = i;
                    break;
                }
            }
        }
    }
}
