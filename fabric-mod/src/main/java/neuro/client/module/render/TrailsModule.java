package neuro.client.module.render;

import neuro.client.module.Category;
import neuro.client.module.Module;
import neuro.client.setting.SliderSetting;

public class TrailsModule extends Module {
    private final SliderSetting duration = new SliderSetting("Trail Duration", 25.0, 5.0, 60.0, 5.0);

    public TrailsModule() {
        super("Trails", "Градиентный шлейф за спиной игрока", Category.RENDER);
        this.setEnabled(true);
        this.addSettings(duration);
    }
}
