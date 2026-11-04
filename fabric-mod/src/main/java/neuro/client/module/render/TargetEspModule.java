package neuro.client.module.render;

import neuro.client.module.Category;
import neuro.client.module.Module;
import neuro.client.setting.ModeSetting;
import neuro.client.setting.SliderSetting;

import java.util.List;

public class TargetEspModule extends Module {
    private final ModeSetting style = new ModeSetting("Style", "Ghosts Orbit",
            List.of("Ghosts Orbit", "Jello Neon Ring", "TargetLens Square", "Soul Spiral"));
    private final SliderSetting speed = new SliderSetting("Orbit Speed", 1.5, 0.5, 3.5, 0.1);

    public TargetEspModule() {
        super("TargetESP", "Анимация захвата цели Киллауры (Призраки / Jello / TargetLens)", Category.RENDER);
        this.setEnabled(true);
        this.addSettings(style, speed);
    }
}
