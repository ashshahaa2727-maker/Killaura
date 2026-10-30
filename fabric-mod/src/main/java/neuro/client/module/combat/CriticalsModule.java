package neuro.client.module.combat;

import neuro.client.module.Category;
import neuro.client.module.Module;
import neuro.client.setting.ModeSetting;

import java.util.List;

public class CriticalsModule extends Module {
    private final ModeSetting mode = new ModeSetting("Crit Mode", "Grim Fall",
            List.of("Grim Fall", "Packet Hop", "Strict 1.21.4"));

    public CriticalsModule() {
        super("Criticals", "Каждый удар наносит критический урон", Category.COMBAT);
        this.addSettings(mode);
    }
}
