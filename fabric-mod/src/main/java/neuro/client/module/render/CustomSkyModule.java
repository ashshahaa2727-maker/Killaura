package neuro.client.module.render;

import neuro.client.module.Category;
import neuro.client.module.Module;
import neuro.client.setting.ModeSetting;

import java.util.List;

public class CustomSkyModule extends Module {
    private final ModeSetting style = new ModeSetting("Sky Shader", "Nebula Cosmic",
            List.of("Nebula Cosmic", "Galaxy Space", "Sunset Radiant"));

    public CustomSkyModule() {
        super("Custom Sky", "Замена неба на процедурные космические туманности", Category.RENDER);
        this.setEnabled(true);
        this.addSettings(style);
    }
}
