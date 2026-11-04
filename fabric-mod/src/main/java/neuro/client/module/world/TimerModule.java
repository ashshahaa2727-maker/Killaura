package neuro.client.module.world;

import neuro.client.module.Category;
import neuro.client.module.Module;
import neuro.client.setting.SliderSetting;

public class TimerModule extends Module {
    private final SliderSetting speed = new SliderSetting("Speed Multiplier", 1.4, 0.5, 4.0, 0.1);

    public TimerModule() {
        super("Timer", "Изменяет скорость хода игровых тиков клиента", Category.WORLD);
        this.addSettings(speed);
    }
}
