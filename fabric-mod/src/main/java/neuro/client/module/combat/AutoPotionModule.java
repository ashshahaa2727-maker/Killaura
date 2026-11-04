package neuro.client.module.combat;

import neuro.client.module.Category;
import neuro.client.module.Module;
import neuro.client.setting.BooleanSetting;
import neuro.client.setting.SliderSetting;

public class AutoPotionModule extends Module {
    private final SliderSetting healHp = new SliderSetting("Heal HP", 10.0, 4.0, 18.0, 1.0);
    private final BooleanSetting strength = new BooleanSetting("Auto Strength", true);
    private final BooleanSetting speed = new BooleanSetting("Auto Speed", true);

    public AutoPotionModule() {
        super("AutoPotion", "Авто-бафф взрывных зелий под ноги", Category.COMBAT);
        this.addSettings(healHp, strength, speed);
    }
}
