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
        // COMBAT (12 модулей)
        modules.add(new AuraModule());
        modules.add(new FakePlayerModule());
        modules.add(new VelocityModule());
        modules.add(new AutoTotemModule());
        modules.add(new CriticalsModule());
        modules.add(new TriggerBotModule());
        modules.add(new HitBoxesModule());
        modules.add(new AutoMaceModule());
        modules.add(new AutoCrystalModule());
        modules.add(new AutoGappleModule());
        modules.add(new AutoPotionModule());
        modules.add(new AntiBotModule());

        // MOVEMENT (11 модулей)
        modules.add(new FlightModule());
        modules.add(new SpeedModule());
        modules.add(new SprintModule());
        modules.add(new NoSlowModule());
        modules.add(new ElytraTargetModule());
        modules.add(new TargetStrafeModule());
        modules.add(new StrafeModule());
        modules.add(new JesusModule());
        modules.add(new SpiderModule());
        modules.add(new AirStuckModule());
        modules.add(new NoPushModule());

        // RENDER (11 модулей)
        modules.add(new HudModule());
        modules.add(new TargetEspModule());
        modules.add(new GlowEspModule());
        modules.add(new NameTagsModule());
        modules.add(new CustomSkyModule());
        modules.add(new FullBrightModule());
        modules.add(new JumpCirclesModule());
        modules.add(new TrailsModule());
        modules.add(new PredictionsModule());
        modules.add(new ViewModelModule());
        modules.add(new ChinaHatModule());

        // PLAYER (9 модулей)
        modules.add(new AutoArmorModule());
        modules.add(new InvMoveModule());
        modules.add(new ChestStealerModule());
        modules.add(new FastEatModule());
        modules.add(new FreeCamModule());
        modules.add(new AutoToolModule());
        modules.add(new NoFallModule());
        modules.add(new AutoRespawnModule());
        modules.add(new MiddleClickPearlModule());

        // WORLD (8 модулей)
        modules.add(new ScaffoldModule());
        modules.add(new FastPlaceModule());
        modules.add(new TimerModule());
        modules.add(new FastBreakModule());
        modules.add(new XRayModule());
        modules.add(new AutoFarmModule());
        modules.add(new NukerModule());
        modules.add(new WeatherControlModule());
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
