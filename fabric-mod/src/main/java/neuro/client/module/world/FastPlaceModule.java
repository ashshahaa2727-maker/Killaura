package neuro.client.module.world;

import neuro.client.module.Category;
import neuro.client.module.Module;
import neuro.client.setting.SliderSetting;

public class FastPlaceModule extends Module {
    private final SliderSetting delay = new SliderSetting("Tick Delay", 0.0, 0.0, 4.0, 1.0);

    public FastPlaceModule() {
        super("FastPlace", "Убирает задержку при установке блоков", Category.WORLD);
        this.setEnabled(true);
        this.addSettings(delay);
    }
}
