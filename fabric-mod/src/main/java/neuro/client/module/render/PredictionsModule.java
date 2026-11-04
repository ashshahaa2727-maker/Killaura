package neuro.client.module.render;

import neuro.client.module.Category;
import neuro.client.module.Module;
import neuro.client.setting.BooleanSetting;

public class PredictionsModule extends Module {
    private final BooleanSetting pearls = new BooleanSetting("Ender Pearls", true);
    private final BooleanSetting arrows = new BooleanSetting("Arrows & Tridents", true);

    public PredictionsModule() {
        super("Predictions", "Предсказание траектории Эндер-пёрлов и стрел", Category.RENDER);
        this.setEnabled(true);
        this.addSettings(pearls, arrows);
    }
}
