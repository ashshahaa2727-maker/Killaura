package neuro.client.module.render;

import neuro.client.module.Category;
import neuro.client.module.Module;
import neuro.client.setting.ModeSetting;
import neuro.client.setting.SliderSetting;

import java.util.List;

public class CustomSkyModule extends Module {
    private final ModeSetting style = new ModeSetting("Sky Shader", "Nebula Cosmic",
            List.of("Nebula Cosmic", "Galaxy Space", "Sunset Radiant", "Caustic Waves"));
    private final SliderSetting stars = new SliderSetting("Star Density", 1.3, 0.2, 3.0, 0.1);

    public CustomSkyModule() {
        super("Custom Sky", "Замена неба на процедурные космические туманности", Category.RENDER);
        this.setEnabled(true);
        this.addSettings(style, stars);
    }
}
