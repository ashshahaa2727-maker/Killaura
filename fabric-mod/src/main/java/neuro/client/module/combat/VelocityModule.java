package neuro.client.module.combat;

import net.minecraft.client.MinecraftClient;
import net.minecraft.util.math.Vec3d;
import neuro.client.module.Category;
import neuro.client.module.Module;
import neuro.client.setting.ModeSetting;
import neuro.client.setting.SliderSetting;

import java.util.List;

public class VelocityModule extends Module {
    private final ModeSetting mode = new ModeSetting("Mode", "Grim 1.21.4",
            List.of("Grim 1.21.4", "Matrix Legit", "Cancel", "Custom %"));
    private final SliderSetting horizontal = new SliderSetting("Horizontal %", 0.0, 0.0, 100.0, 5.0);
    private final SliderSetting vertical = new SliderSetting("Vertical %", 0.0, 0.0, 100.0, 5.0);

    public VelocityModule() {
        super("Velocity", "Подавление отбрасывания при получении урона", Category.COMBAT);
        this.setEnabled(true);
        this.addSettings(mode, horizontal, vertical);
    }

    @Override
    public void onTick(MinecraftClient client) {
        if (client.player != null && client.player.hurtTime == 9) {
            double hScale = horizontal.getValue() / 100.0;
            double vScale = vertical.getValue() / 100.0;
            Vec3d vel = client.player.getVelocity();
            client.player.setVelocity(vel.x * hScale, vel.y * vScale, vel.z * hScale);
        }
    }
}
