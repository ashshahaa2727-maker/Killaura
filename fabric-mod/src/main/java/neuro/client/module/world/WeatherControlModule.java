package neuro.client.module.world;

import neuro.client.module.Category;
import neuro.client.module.Module;
import neuro.client.setting.ModeSetting;

import java.util.List;

public class WeatherControlModule extends Module {
    private final ModeSetting timeOfDay = new ModeSetting("Time of Day", "Midnight (18000)",
            List.of("Midnight (18000)", "Sunset (13000)", "Noon (6000)", "Server Default"));

    public WeatherControlModule() {
        super("WeatherControl", "Принудительная ночь, закат и ясная погода", Category.WORLD);
        this.setEnabled(true);
        this.addSettings(timeOfDay);
    }
}
