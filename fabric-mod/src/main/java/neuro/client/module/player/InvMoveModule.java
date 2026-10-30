package neuro.client.module.player;

import neuro.client.module.Category;
import neuro.client.module.Module;
import neuro.client.setting.BooleanSetting;

public class InvMoveModule extends Module {
    private final BooleanSetting sprint = new BooleanSetting("Allow Sprint", true);

    public InvMoveModule() {
        super("InvMove", "Ходьба и прыжки при открытом инвентаре", Category.PLAYER);
        this.setEnabled(true);
        this.addSettings(sprint);
    }
}
