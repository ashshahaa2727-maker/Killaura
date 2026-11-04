package neuro.client.module.world;

import neuro.client.module.Category;
import neuro.client.module.Module;
import neuro.client.setting.SliderSetting;

public class NukerModule extends Module {
    private final SliderSetting radius = new SliderSetting("Radius", 3.0, 1.0, 5.0, 1.0);

    public NukerModule() {
        super("Nuker", "Авто-вскапывание блоков вокруг игрока", Category.WORLD);
        this.addSettings(radius);
    }
}
