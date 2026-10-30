package neuro.client.module;

import net.minecraft.client.MinecraftClient;
import neuro.client.module.combat.*;
import neuro.client.module.movement.*;
import neuro.client.module.player.*;
import neuro.client.module.render.*;
import neuro.client.module.world.*;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class ModuleManager {
    private final List<Module> modules = new ArrayList<>();

    public ModuleManager() {
        // Combat
        modules.add(new AuraModule());
        modules.add(new VelocityModule());
        modules.add(new AutoTotemModule());
        modules.add(new CriticalsModule());

        // Movement
        modules.add(new FlightModule());
        modules.add(new SpeedModule());
        modules.add(new SprintModule());
        modules.add(new NoSlowModule());

        // Render
        modules.add(new GlowEspModule());
        modules.add(new CustomSkyModule());
        modules.add(new FullBrightModule());

        // Player
        modules.add(new AutoArmorModule());
        modules.add(new InvMoveModule());

        // World
        modules.add(new ScaffoldModule());
        modules.add(new FastPlaceModule());
    }

    public void onTick(MinecraftClient client) {
        for (Module module : modules) {
            if (module.isEnabled()) {
                module.onTick(client);
            }
        }
    }

    public <T extends Module> T getModule(Class<T> clazz) {
        for (Module m : modules) {
            if (clazz.isInstance(m)) {
                return clazz.cast(m);
            }
        }
        return null;
    }

    public List<Module> getModules() {
        return modules;
    }

    public List<Module> getModulesByCategory(Category category) {
        return modules.stream()
                .filter(m -> m.getCategory() == category)
                .collect(Collectors.toList());
    }

    public List<Module> getEnabledModules() {
        return modules.stream()
                .filter(Module::isEnabled)
                .collect(Collectors.toList());
    }
}
