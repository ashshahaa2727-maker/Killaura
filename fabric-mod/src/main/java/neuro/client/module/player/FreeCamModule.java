package neuro.client.module.player;

import neuro.client.module.Category;
import neuro.client.module.Module;
import neuro.client.setting.SliderSetting;

public class FreeCamModule extends Module {
    private final SliderSetting speed = new SliderSetting("Camera Speed", 1.5, 0.5, 4.0, 0.1);

    public FreeCamModule() {
        super("FreeCam", "Свободная призрачная камера сквозь стены", Category.PLAYER);
        this.addSettings(speed);
    }
}
