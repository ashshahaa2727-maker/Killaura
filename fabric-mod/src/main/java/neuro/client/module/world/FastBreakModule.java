package neuro.client.module.world;

import neuro.client.module.Category;
import neuro.client.module.Module;
import neuro.client.setting.SliderSetting;

public class FastBreakModule extends Module {
    private final SliderSetting threshold = new SliderSetting("Break Threshold", 0.75, 0.50, 1.00, 0.05);

    public FastBreakModule() {
        super("FastBreak", "Ускоренное копание блоков без задержки", Category.WORLD);
        this.setEnabled(true);
        this.addSettings(threshold);
    }
}
