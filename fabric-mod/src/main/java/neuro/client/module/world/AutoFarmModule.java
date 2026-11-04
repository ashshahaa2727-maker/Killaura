package neuro.client.module.world;

import neuro.client.module.Category;
import neuro.client.module.Module;
import neuro.client.setting.SliderSetting;

public class AutoFarmModule extends Module {
    private final SliderSetting reach = new SliderSetting("Reach", 4.0, 2.0, 5.0, 0.5);

    public AutoFarmModule() {
        super("AutoFarm", "Автоматический сбор и посадка урожая", Category.WORLD);
        this.addSettings(reach);
    }
}
