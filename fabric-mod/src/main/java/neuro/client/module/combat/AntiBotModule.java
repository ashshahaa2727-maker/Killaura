package neuro.client.module.combat;

import net.minecraft.entity.player.PlayerEntity;
import neuro.client.NeuroClient;
import neuro.client.module.Category;
import neuro.client.module.Module;
import neuro.client.setting.ModeSetting;

import java.util.List;

public class AntiBotModule extends Module {
    private final ModeSetting mode = new ModeSetting("Check Mode", "Matrix + Grim UUID",
            List.of("Matrix + Grim UUID", "ReallyWorld TabList", "FunTime ArmorCheck"));

    public AntiBotModule() {
        super("AntiBot", "Игнорирование проверочных ботов античита", Category.COMBAT);
        this.setEnabled(true);
        this.addSettings(mode);
    }

    public boolean isBot(PlayerEntity player) {
        FakePlayerModule fp = NeuroClient.getInstance().getModuleManager().getModule(FakePlayerModule.class);
        if (fp != null && player == fp.getFakePlayer()) {
            return false; // Никогда не блокируем наш тренировочный FakePlayer!
        }
        return player.getUuid() == null;
    }
}
