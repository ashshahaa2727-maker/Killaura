package neuro.client.module.movement;

import neuro.client.module.Category;
import neuro.client.module.Module;
import neuro.client.setting.ModeSetting;

import java.util.List;

public class StrafeModule extends Module {
    private final ModeSetting mode = new ModeSetting("Mode", "Matrix Strict",
            List.of("Matrix Strict", "Grim Air", "Vanilla Full"));

    public StrafeModule() {
        super("Strafe", "Мгновенная смена направления движения в воздухе", Category.MOVEMENT);
        this.addSettings(mode);
    }
}
