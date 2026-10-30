package neuro.client.module.render;

import net.minecraft.client.MinecraftClient;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import neuro.client.module.Category;
import neuro.client.module.Module;
import neuro.client.setting.SliderSetting;

public class FullBrightModule extends Module {
    private final SliderSetting gamma = new SliderSetting("Gamma", 16.0, 1.0, 25.0, 1.0);

    public FullBrightModule() {
        super("FullBright", "Максимальное освещение в пещерах и ночью", Category.RENDER);
        this.setEnabled(true);
        this.addSettings(gamma);
    }

    @Override
    public void onTick(MinecraftClient client) {
        if (client.player != null) {
            client.player.addStatusEffect(new StatusEffectInstance(StatusEffects.NIGHT_VISION, 400, 0, false, false, false));
        }
    }

    @Override
    public void onDisable() {
        MinecraftClient client = MinecraftClient.getInstance();
        if (client.player != null) {
            client.player.removeStatusEffect(StatusEffects.NIGHT_VISION);
        }
    }
}
