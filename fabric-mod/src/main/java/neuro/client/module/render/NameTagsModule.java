package neuro.client.module.render;

import neuro.client.module.Category;
import neuro.client.module.Module;
import neuro.client.setting.BooleanSetting;
import neuro.client.setting.SliderSetting;

public class NameTagsModule extends Module {
    private final SliderSetting scale = new SliderSetting("Tag Scale", 1.2, 0.6, 2.5, 0.1);
    private final BooleanSetting showArmor = new BooleanSetting("Show Armor & Hand", true);

    public NameTagsModule() {
        super("NameTags", "Крупные плашки с бронёй, тотемами и HP над игроками", Category.RENDER);
        this.setEnabled(true);
        this.addSettings(scale, showArmor);
    }
}
