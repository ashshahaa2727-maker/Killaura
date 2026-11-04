package neuro.client.module.combat;

import neuro.client.module.Category;
import neuro.client.module.Module;
import neuro.client.setting.BooleanSetting;
import neuro.client.setting.SliderSetting;

public class HitBoxesModule extends Module {
    private final SliderSetting expandSize = new SliderSetting("Expand Size", 0.35, 0.05, 1.50, 0.05);
    private final BooleanSetting includeFakePlayer = new BooleanSetting("Include FakePlayer", true);

    public HitBoxesModule() {
        super("HitBoxes", "Расширение хитбоксов игроков и мобов", Category.COMBAT);
        this.addSettings(expandSize, includeFakePlayer);
    }

    public double getExpandSize() {
        return expandSize.getValue();
    }
}
