package neuro.client.module.render;

import neuro.client.module.Category;
import neuro.client.module.Module;
import neuro.client.setting.SliderSetting;

public class JumpCirclesModule extends Module {
    private final SliderSetting radius = new SliderSetting("Max Radius", 2.2, 1.0, 4.0, 0.2);

    public JumpCirclesModule() {
        super("JumpCircles", "Неоновые волны под ногами при прыжках", Category.RENDER);
        this.setEnabled(true);
        this.addSettings(radius);
    }
}
