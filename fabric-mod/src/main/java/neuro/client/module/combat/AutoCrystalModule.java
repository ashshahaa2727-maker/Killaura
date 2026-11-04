package neuro.client.module.combat;

import net.minecraft.client.MinecraftClient;
import net.minecraft.entity.decoration.EndCrystalEntity;
import net.minecraft.util.Hand;
import net.minecraft.util.math.Box;
import neuro.client.module.Category;
import neuro.client.module.Module;
import neuro.client.setting.BooleanSetting;
import neuro.client.setting.SliderSetting;

import java.util.List;

public class AutoCrystalModule extends Module {
    private final SliderSetting breakRange = new SliderSetting("Break Range", 4.5, 2.5, 6.0, 0.1);
    private final BooleanSetting antiSuicide = new BooleanSetting("Anti Suicide", true);

    public AutoCrystalModule() {
        super("AutoCrystal", "Автоматическая установка и взрыв кристаллов Энда", Category.COMBAT);
        this.addSettings(breakRange, antiSuicide);
    }

    @Override
    public void onTick(MinecraftClient client) {
        if (client.player == null || client.world == null || client.interactionManager == null) return;
        if (antiSuicide.getValue() && client.player.getHealth() <= 6.0f) return;

        double r = breakRange.getValue();
        Box box = client.player.getBoundingBox().expand(r, r, r);
        List<EndCrystalEntity> crystals = client.world.getEntitiesByClass(EndCrystalEntity.class, box, EndCrystalEntity::isAlive);
        for (EndCrystalEntity c : crystals) {
            if (client.player.distanceTo(c) <= r) {
                client.interactionManager.attackEntity(client.player, c);
                client.player.swingHand(Hand.MAIN_HAND);
                break;
            }
        }
    }
}
