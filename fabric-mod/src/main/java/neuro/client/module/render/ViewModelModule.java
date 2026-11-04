package neuro.client.module.render;

import neuro.client.module.Category;
import neuro.client.module.Module;
import neuro.client.setting.ModeSetting;
import neuro.client.setting.SliderSetting;

import java.util.List;

public class ViewModelModule extends Module {
    private final ModeSetting swingMode = new ModeSetting("Swing Mode", "Smooth Spin 360",
            List.of("Smooth Spin 360", "BlockHit 1.7", "Glide Down", "Punch Fast"));
    private final SliderSetting scale = new SliderSetting("Hand Scale", 0.85, 0.40, 1.50, 0.05);

    public ViewModelModule() {
        super("ViewModel", "Позиция рук и плавная анимация взмаха меча", Category.RENDER);
        this.setEnabled(true);
        this.addSettings(swingMode, scale);
    }
}
