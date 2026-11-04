package neuro.client.module.render;

import neuro.client.module.Category;
import neuro.client.module.Module;
import neuro.client.setting.SliderSetting;

public class ChinaHatModule extends Module {
    private final SliderSetting radius = new SliderSetting("Hat Radius", 0.65, 0.40, 1.20, 0.05);

    public ChinaHatModule() {
        super("ChinaHat", "Неоновая коническая шляпа над головой", Category.RENDER);
        this.setEnabled(true);
        this.addSettings(radius);
    }
}
