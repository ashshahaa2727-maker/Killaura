package neuro.client.module.movement;

import neuro.client.module.Category;
import neuro.client.module.Module;
import neuro.client.setting.BooleanSetting;

public class NoPushModule extends Module {
    private final BooleanSetting players = new BooleanSetting("Ignore Players", true);
    private final BooleanSetting water = new BooleanSetting("Ignore Water", true);
    private final BooleanSetting blocks = new BooleanSetting("Ignore Blocks", true);

    public NoPushModule() {
        super("NoPush", "Отключение выталкивания игроками, водой и блоками", Category.MOVEMENT);
        this.setEnabled(true);
        this.addSettings(players, water, blocks);
    }
}
