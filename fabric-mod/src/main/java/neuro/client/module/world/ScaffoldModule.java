package neuro.client.module.world;

import neuro.client.module.Category;
import neuro.client.module.Module;
import neuro.client.setting.BooleanSetting;
import neuro.client.setting.ModeSetting;
import neuro.client.setting.SliderSetting;

import java.util.List;

public class ScaffoldModule extends Module {
    private final ModeSetting mode = new ModeSetting("Place Mode", "Snap 1.21.4",
            List.of("Snap 1.21.4", "Legit Eagle", "Tower Fast", "Hypixel Fast"));
    private final SliderSetting delay = new SliderSetting("Place Delay (ms)", 25.0, 0.0, 120.0, 5.0);
    private final BooleanSetting safeWalk = new BooleanSetting("SafeWalk", true);

    public ScaffoldModule() {
        super("Scaffold", "Автоматическая постройка моста под ногами", Category.WORLD);
        this.addSettings(mode, delay, safeWalk);
    }
}
