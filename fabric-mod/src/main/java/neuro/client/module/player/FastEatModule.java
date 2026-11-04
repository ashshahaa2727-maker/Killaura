package neuro.client.module.player;

import neuro.client.module.Category;
import neuro.client.module.Module;
import neuro.client.setting.ModeSetting;
import neuro.client.setting.SliderSetting;

import java.util.List;

public class FastEatModule extends Module {
    private final ModeSetting mode = new ModeSetting("Mode", "Packet Spam 1.21.4",
            List.of("Packet Spam 1.21.4", "Timer 2.0x", "Delayed Legit"));
    private final SliderSetting packets = new SliderSetting("Packets / Tick", 18.0, 5.0, 35.0, 1.0);

    public FastEatModule() {
        super("FastEat", "Быстрое поедание еды и питьё зелий", Category.PLAYER);
        this.addSettings(mode, packets);
    }
}
