package neuro.client.module.player;

import neuro.client.module.Category;
import neuro.client.module.Module;
import neuro.client.setting.SliderSetting;

public class AutoArmorModule extends Module {
    private final SliderSetting delay = new SliderSetting("Equip Delay", 65.0, 0.0, 200.0, 5.0);

    public AutoArmorModule() {
        super("AutoArmor", "Автоматическое надевание лучшей брони", Category.PLAYER);
        this.setEnabled(true);
        this.addSettings(delay);
    }
}
