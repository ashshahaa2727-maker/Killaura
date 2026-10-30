package neuro.client.module.render;

import neuro.client.module.Category;
import neuro.client.module.Module;
import neuro.client.setting.ModeSetting;
import neuro.client.setting.SliderSetting;

import java.util.List;

public class GlowEspModule extends Module {
    private final ModeSetting mode = new ModeSetting("Shader Mode", "Kawase Glow",
            List.of("Kawase Glow", "Squircle 2D", "Outline Box"));
    private final SliderSetting radius = new SliderSetting("Glow Radius", 12.0, 2.0, 30.0, 1.0);

    public GlowEspModule() {
        super("Glow ESP", "Шейдерное свечение игроков сквозь стены", Category.RENDER);
        this.setEnabled(true);
        this.addSettings(mode, radius);
    }
}
