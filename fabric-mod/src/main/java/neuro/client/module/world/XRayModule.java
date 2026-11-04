package neuro.client.module.world;

import neuro.client.module.Category;
import neuro.client.module.Module;
import neuro.client.setting.SliderSetting;

public class XRayModule extends Module {
    private final SliderSetting radius = new SliderSetting("Scan Radius", 32.0, 12.0, 64.0, 4.0);

    public XRayModule() {
        super("XRay", "Подсветка незерита, алмазов и спавнеров", Category.WORLD);
        this.addSettings(radius);
    }
}
