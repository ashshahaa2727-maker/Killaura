package neuro.client.module.movement;

import neuro.client.module.Category;
import neuro.client.module.Module;
import neuro.client.setting.ModeSetting;
import neuro.client.setting.MultiSelectSetting;

import java.util.List;

public class NoSlowModule extends Module {
    private final ModeSetting mode = new ModeSetting("Bypass", "Grim 1.21.4",
            List.of("Grim 1.21.4", "FunTime", "Matrix", "Vanilla"));
    private final MultiSelectSetting items = new MultiSelectSetting("Items",
            List.of("Food", "Bows", "Shields", "Potions", "Crossbows"),
            List.of("Food", "Bows", "Shields", "Potions"));

    public NoSlowModule() {
        super("NoSlow", "Движение без замедления при еде и блоке щитом", Category.MOVEMENT);
        this.setEnabled(true);
        this.addSettings(mode, items);
    }
}
