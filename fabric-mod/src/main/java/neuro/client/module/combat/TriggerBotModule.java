package neuro.client.module.combat;

import net.minecraft.client.MinecraftClient;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.util.Hand;
import neuro.client.NeuroClient;
import neuro.client.module.Category;
import neuro.client.module.Module;
import neuro.client.setting.BooleanSetting;
import neuro.client.setting.SliderSetting;

public class TriggerBotModule extends Module {
    private final SliderSetting range = new SliderSetting("Range", 3.3, 2.5, 5.0, 0.1);
    private final BooleanSetting onlyCrits = new BooleanSetting("Only Crits", true);
    private final BooleanSetting smartCooldown = new BooleanSetting("Smart Cooldown", true);

    public TriggerBotModule() {
        super("TriggerBot", "Авто-удар при наведении прицела с умными критами", Category.COMBAT);
        this.addSettings(range, onlyCrits, smartCooldown);
    }

    @Override
    public void onTick(MinecraftClient client) {
        if (client.player == null || client.interactionManager == null) return;
        Entity targeted = client.targetedEntity;
        if (!(targeted instanceof LivingEntity living) || !living.isAlive()) return;
        if (client.player.distanceTo(living) > range.getValue()) return;

        if (smartCooldown.getValue() && client.player.getAttackCooldownProgress(0.5f) < 0.92f) return;
        if (onlyCrits.getValue() && !client.player.isOnGround() && client.player.fallDistance <= 0.01f) return;

        FakePlayerModule fp = NeuroClient.getInstance().getModuleManager().getModule(FakePlayerModule.class);
        if (fp != null && living == fp.getFakePlayer()) {
            fp.onHitByPlayer(client.player, 6.0f);
        } else {
            client.interactionManager.attackEntity(client.player, living);
        }
        client.player.swingHand(Hand.MAIN_HAND);
    }
}
