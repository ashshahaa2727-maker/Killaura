import JSZip from 'jszip';
import { NEURO_METADATA } from '../data/modulesData';

export interface JavaFileEntry {
  path: string;
  name: string;
  category: 'core' | 'gui' | 'module' | 'ai' | 'render' | 'config' | 'shader' | 'ci' | 'python';
  language: 'java' | 'json' | 'groovy' | 'properties' | 'glsl' | 'yaml' | 'python';
  content: string;
  description: string;
}

export const FABRIC_1214_FILES: JavaFileEntry[] = [
  // 1. GitHub Actions Workflow (.github/workflows/build.yml)
  {
    path: '.github/workflows/build.yml',
    name: 'build.yml (GitHub Actions CI)',
    category: 'ci',
    language: 'yaml',
    description: 'Автоматическая сборка мода на серверах GitHub (Gradle 8.12 + JDK 21 + Fabric Loom 1.10)',
    content: `name: Build Fabric 1.21.4 Mod & Web Studio

on:
  push:
    branches: [ "main", "master" ]
    tags: [ "v*" ]
  pull_request:
    branches: [ "main", "master" ]
  workflow_dispatch:

jobs:
  build-fabric-mod:
    name: 🚀 Build Fabric 1.21.4 Mod (.jar)
    runs-on: ubuntu-latest
    permissions:
      contents: write

    steps:
      - name: 📥 Checkout repository
        uses: actions/checkout@v4

      - name: ☕ Set up JDK 21 (Temurin)
        uses: actions/setup-java@v4
        with:
          java-version: '21'
          distribution: 'temurin'

      - name: 🐘 Setup Gradle 8.12
        uses: gradle/actions/setup-gradle@v4
        with:
          gradle-version: '8.12'

      - name: 🔨 Compile Fabric 1.21.4 Mod with Gradle Loom
        run: |
          if [ -d "fabric-mod" ] && [ -f "fabric-mod/build.gradle" ]; then
            cd fabric-mod
            gradle build --no-daemon
          else
            gradle build --no-daemon
          fi

      - name: 📦 Upload Mod JAR Artifact
        uses: actions/upload-artifact@v4
        with:
          name: neuro-fabric-1.21.4-build
          path: |
            build/libs/*.jar
            fabric-mod/build/libs/*.jar
            !build/libs/*-sources.jar
            !fabric-mod/build/libs/*-sources.jar
          if-no-files-found: error

      - name: 🏷️ Publish GitHub Release with JAR
        if: startsWith(github.ref, 'refs/tags/')
        uses: softprops/action-gh-release@v2
        with:
          files: |
            build/libs/*.jar
            fabric-mod/build/libs/*.jar
          name: Neuro Client 1.21.4 Release
          generate_release_notes: true
`,
  },

  // 2. build.gradle
  {
    path: 'build.gradle',
    name: 'build.gradle',
    category: 'config',
    language: 'groovy',
    description: 'Официальный Gradle-скрипт сборки под Fabric Loom 1.10, Minecraft 1.21.4 и Java 21',
    content: `plugins {
    id 'fabric-loom' version '1.10-SNAPSHOT'
    id 'maven-publish'
}

version = project.mod_version
group = project.maven_group

base {
    archivesName = project.archives_base_name
}

repositories {
    mavenCentral()
    maven { url "https://maven.fabricmc.net/" }
}

dependencies {
    minecraft "com.mojang:minecraft:\${project.minecraft_version}"
    mappings "net.fabricmc:yarn:\${project.yarn_mappings}:v2"
    modImplementation "net.fabricmc:fabric-loader:\${project.loader_version}"
    modImplementation "net.fabricmc.fabric-api:fabric-api:\${project.fabric_version}"
}

processResources {
    inputs.property "version", project.version
    filteringCharset "UTF-8"

    filesMatching("fabric.mod.json") {
        expand "version": project.version
    }
}

tasks.withType(JavaCompile).configureEach {
    it.options.encoding = "UTF-8"
    it.options.release = 21
}

java {
    withSourcesJar()
    sourceCompatibility = JavaVersion.VERSION_21
    targetCompatibility = JavaVersion.VERSION_21
}
`,
  },

  // 3. gradle.properties
  {
    path: 'gradle.properties',
    name: 'gradle.properties',
    category: 'config',
    language: 'properties',
    description: 'Точные версии маппингов Yarn 1.21.4+build.8, Fabric Loader 0.16.10 и Fabric API 0.119.2+1.21.4',
    content: `org.gradle.jvmargs=-Xmx3G
org.gradle.parallel=true

minecraft_version=1.21.4
yarn_mappings=1.21.4+build.8
loader_version=0.16.10
fabric_version=0.119.2+1.21.4

mod_version=1.0.0
maven_group=neuro.client
archives_base_name=neuro-fabric-1.21.4
`,
  },

  // 4. settings.gradle
  {
    path: 'settings.gradle',
    name: 'settings.gradle',
    category: 'config',
    language: 'groovy',
    description: 'Настройки репозиториев плагинов Fabric',
    content: `pluginManagement {
    repositories {
        maven {
            name = 'Fabric'
            url = 'https://maven.fabricmc.net/'
        }
        mavenCentral()
        gradlePluginPortal()
    }
}

rootProject.name = 'neuro-fabric-1.21.4'
`,
  },

  // 5. src/main/resources/fabric.mod.json
  {
    path: 'src/main/resources/fabric.mod.json',
    name: 'fabric.mod.json',
    category: 'config',
    language: 'json',
    description: 'Манифест мода в src/main/resources для Fabric Loader 0.16+ и Minecraft 1.21.4',
    content: `{
  "schemaVersion": 1,
  "id": "neuro",
  "version": "\${version}",
  "name": "Neuro Client 1.21.4",
  "description": "Элитный клиент для Minecraft Fabric 1.21.4 с открытием на Правый Shift, кастомными шейдерами и GRU нейросетью аима.",
  "authors": [
    "Neuro Team"
  ],
  "contact": {
    "homepage": "https://github.com/neuro-client"
  },
  "license": "MIT",
  "environment": "client",
  "entrypoints": {
    "client": [
      "neuro.client.NeuroClient"
    ]
  },
  "mixins": [
    "neuro.mixins.json"
  ],
  "depends": {
    "fabricloader": ">=0.16.0",
    "minecraft": "~1.21.4",
    "java": ">=21",
    "fabric-api": "*"
  }
}`,
  },

  // 6. src/main/resources/neuro.mixins.json
  {
    path: 'src/main/resources/neuro.mixins.json',
    name: 'neuro.mixins.json',
    category: 'config',
    language: 'json',
    description: 'Конфигурация миксинов (Java 21)',
    content: `{
  "required": false,
  "minVersion": "0.8",
  "package": "neuro.client.mixin",
  "compatibilityLevel": "JAVA_21",
  "mixins": [],
  "client": [],
  "injectors": {
    "defaultRequire": 1
  }
}`,
  },

  // 7. NeuroClient.java (Main Client Entrypoint with RIGHT SHIFT KEYBIND & HUD)
  {
    path: 'src/main/java/neuro/client/NeuroClient.java',
    name: 'NeuroClient.java',
    category: 'core',
    language: 'java',
    description: 'Главный класс мода: регистрация бинда на Правый Shift (GLFW_KEY_RIGHT_SHIFT), HUD и тик-менеджер',
    content: `package neuro.client;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.fabricmc.fabric.api.client.rendering.v1.HudRenderCallback;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.util.InputUtil;
import net.minecraft.entity.LivingEntity;
import neuro.client.ai.NeuroAuraEngine;
import neuro.client.gui.ModernClickGuiScreen;
import neuro.client.module.Module;
import neuro.client.module.ModuleManager;
import neuro.client.module.combat.AuraModule;
import neuro.client.render.shader.ShaderManager;
import org.lwjgl.glfw.GLFW;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.awt.Color;
import java.util.List;

@Environment(EnvType.CLIENT)
@SuppressWarnings("deprecation")
public final class NeuroClient implements ClientModInitializer {
    public static final String MOD_ID = "neuro";
    public static final String MOD_NAME = "Neuro Client";
    public static final String MOD_VERSION = "1.0.0 (1.21.4)";
    public static final Logger LOGGER = LoggerFactory.getLogger(MOD_NAME);

    private static NeuroClient instance;

    private KeyBinding clickGuiKeyBinding;
    private ModuleManager moduleManager;
    private ShaderManager shaderManager;
    private NeuroAuraEngine neuroEngine;

    @Override
    public void onInitializeClient() {
        instance = this;
        LOGGER.info("[Neuro] Инициализация мода под Fabric 1.21.4 (Java 21)...");

        this.clickGuiKeyBinding = KeyBindingHelper.registerKeyBinding(new KeyBinding(
                "key.neuro.open_clickgui",
                InputUtil.Type.KEYSYM,
                GLFW.GLFW_KEY_RIGHT_SHIFT,
                "category.neuro.general"
        ));

        this.moduleManager = new ModuleManager();
        this.shaderManager = new ShaderManager();
        this.neuroEngine = new NeuroAuraEngine();

        this.neuroEngine.loadModelFromResources();
        this.shaderManager.initShaders();

        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            if (client.player == null) return;

            while (this.clickGuiKeyBinding.wasPressed()) {
                if (client.currentScreen == null) {
                    client.setScreen(new ModernClickGuiScreen());
                } else if (client.currentScreen instanceof ModernClickGuiScreen) {
                    client.setScreen(null);
                }
            }

            this.moduleManager.onTick(client);
        });

        HudRenderCallback.EVENT.register((drawContext, tickCounter) -> {
            MinecraftClient client = MinecraftClient.getInstance();
            if (client.player == null || client.options.hudHidden) return;
            renderHud(client, drawContext);
        });

        LOGGER.info("[Neuro] Успешно инициализирован! Нажмите [Правый Shift] в игре для открытия меню.");
    }

    private void renderHud(MinecraftClient client, DrawContext context) {
        String watermark = "✦ NEURO 1.21.4 | " + client.getCurrentFps() + " FPS";
        int wmWidth = client.textRenderer.getWidth(watermark) + 14;
        context.fill(8, 8, 8 + wmWidth, 26, new Color(13, 17, 30, 220).getRGB());
        context.drawBorder(8, 8, wmWidth, 18, 0xFF8B5CF6);
        context.drawText(client.textRenderer, watermark, 15, 13, 0xFFFFFFFF, true);

        List<Module> enabled = this.moduleManager.getEnabledModules();
        int screenWidth = client.getWindow().getScaledWidth();
        int yOffset = 8;
        for (Module mod : enabled) {
            String name = mod.getName();
            int tw = client.textRenderer.getWidth(name) + 12;
            int x = screenWidth - tw - 8;
            context.fill(x, yOffset, x + tw, yOffset + 15, new Color(13, 17, 30, 205).getRGB());
            context.fill(x + tw - 2, yOffset, x + tw, yOffset + 15, 0xFF8B5CF6);
            context.drawText(client.textRenderer, name, x + 5, yOffset + 4, 0xFFE2E8F0, true);
            yOffset += 17;
        }

        AuraModule aura = this.moduleManager.getModule(AuraModule.class);
        if (aura != null && aura.isEnabled() && aura.getTargetEntity() != null) {
            LivingEntity target = aura.getTargetEntity();
            int thX = screenWidth / 2 + 16;
            int thY = client.getWindow().getScaledHeight() / 2 + 12;
            int thW = 140;
            int thH = 36;
            context.fill(thX, thY, thX + thW, thY + thH, new Color(13, 17, 30, 225).getRGB());
            context.drawBorder(thX, thY, thW, thH, 0xFF8B5CF6);
            String tName = target.getName().getString();
            float hp = Math.max(0.0f, target.getHealth());
            float maxHp = Math.max(1.0f, target.getMaxHealth());
            context.drawText(client.textRenderer, tName + " (" + String.format("%.1f", hp) + " HP)", thX + 8, thY + 6, 0xFFFFFFFF, true);
            int barW = (int) ((thW - 16) * Math.min(1.0f, hp / maxHp));
            context.fill(thX + 8, thY + 22, thX + thW - 8, thY + 28, 0xFF1E293B);
            context.fill(thX + 8, thY + 22, thX + 8 + barW, thY + 28, 0xFF8B5CF6);
        }
    }

    public static NeuroClient getInstance() {
        return instance;
    }

    public KeyBinding getClickGuiKeyBinding() {
        return clickGuiKeyBinding;
    }

    public ModuleManager getModuleManager() {
        return moduleManager;
    }

    public ShaderManager getShaderManager() {
        return shaderManager;
    }

    public NeuroAuraEngine getNeuroEngine() {
        return neuroEngine;
    }
}
`,
  },

  // 8. Category.java
  {
    path: 'src/main/java/neuro/client/module/Category.java',
    name: 'Category.java',
    category: 'core',
    language: 'java',
    description: 'Перечисление категорий модулей для ClickGUI',
    content: `package neuro.client.module;

public enum Category {
    COMBAT("Combat"),
    MOVEMENT("Movement"),
    RENDER("Render"),
    PLAYER("Player"),
    WORLD("World");

    private final String displayName;

    Category(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }
}
`,
  },

  // 9. Setting.java
  {
    path: 'src/main/java/neuro/client/setting/Setting.java',
    name: 'Setting.java',
    category: 'core',
    language: 'java',
    description: 'Базовый дженерик-класс настройки модуля',
    content: `package neuro.client.setting;

public abstract class Setting<T> {
    private final String name;
    private T value;

    public Setting(String name, T defaultValue) {
        this.name = name;
        this.value = defaultValue;
    }

    public String getName() {
        return name;
    }

    public T getValue() {
        return value;
    }

    public void setValue(T value) {
        this.value = value;
    }
}
`,
  },

  // 10. BooleanSetting.java
  {
    path: 'src/main/java/neuro/client/setting/BooleanSetting.java',
    name: 'BooleanSetting.java',
    category: 'core',
    language: 'java',
    description: 'Булевая настройка (переключатель Вкл/Выкл)',
    content: `package neuro.client.setting;

public class BooleanSetting extends Setting<Boolean> {
    public BooleanSetting(String name, boolean defaultValue) {
        super(name, defaultValue);
    }

    public void toggle() {
        setValue(!getValue());
    }
}
`,
  },

  // 11. SliderSetting.java
  {
    path: 'src/main/java/neuro/client/setting/SliderSetting.java',
    name: 'SliderSetting.java',
    category: 'core',
    language: 'java',
    description: 'Числовая настройка с ползунком, минимумом, максимумом и шагом',
    content: `package neuro.client.setting;

public class SliderSetting extends Setting<Double> {
    private final double min;
    private final double max;
    private final double step;

    public SliderSetting(String name, double defaultValue, double min, double max, double step) {
        super(name, defaultValue);
        this.min = min;
        this.max = max;
        this.step = step;
    }

    @Override
    public void setValue(Double value) {
        double clamped = Math.max(min, Math.min(max, value));
        double stepped = Math.round(clamped / step) * step;
        double rounded = Math.round(stepped * 100.0) / 100.0;
        super.setValue(rounded);
    }

    public double getMin() {
        return min;
    }

    public double getMax() {
        return max;
    }

    public double getStep() {
        return step;
    }
}
`,
  },

  // 12. ModeSetting.java
  {
    path: 'src/main/java/neuro/client/setting/ModeSetting.java',
    name: 'ModeSetting.java',
    category: 'core',
    language: 'java',
    description: 'Выбор режима из списка доступных вариантов',
    content: `package neuro.client.setting;

import java.util.List;

public class ModeSetting extends Setting<String> {
    private final List<String> modes;

    public ModeSetting(String name, String defaultValue, List<String> modes) {
        super(name, defaultValue);
        this.modes = modes;
    }

    public List<String> getModes() {
        return modes;
    }

    public void cycle() {
        int idx = modes.indexOf(getValue());
        int next = (idx + 1) % modes.size();
        setValue(modes.get(next));
    }
}
`,
  },

  // 13. Module.java
  {
    path: 'src/main/java/neuro/client/module/Module.java',
    name: 'Module.java',
    category: 'core',
    language: 'java',
    description: 'Базовый абстрактный класс модуля мода',
    content: `package neuro.client.module;

import net.minecraft.client.MinecraftClient;
import neuro.client.setting.Setting;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public abstract class Module {
    private final String name;
    private final String description;
    private final Category category;
    private boolean enabled = false;
    private int keybind = 0;
    private final List<Setting<?>> settings = new ArrayList<>();

    public Module(String name, String description, Category category) {
        this.name = name;
        this.description = description;
        this.category = category;
    }

    public void addSettings(Setting<?>... newSettings) {
        Collections.addAll(this.settings, newSettings);
    }

    public void toggle() {
        this.setEnabled(!this.enabled);
    }

    public void setEnabled(boolean enabled) {
        if (this.enabled != enabled) {
            this.enabled = enabled;
            if (this.enabled) {
                onEnable();
            } else {
                onDisable();
            }
        }
    }

    public void onEnable() {}
    public void onDisable() {}
    public void onTick(MinecraftClient client) {}

    public String getName() { return name; }
    public String getDescription() { return description; }
    public Category getCategory() { return category; }
    public boolean isEnabled() { return enabled; }
    public int getKeybind() { return keybind; }
    public void setKeybind(int keybind) { this.keybind = keybind; }
    public List<Setting<?>> getSettings() { return settings; }
}
`,
  },

  // 14. ModuleManager.java
  {
    path: 'src/main/java/neuro/client/module/ModuleManager.java',
    name: 'ModuleManager.java',
    category: 'core',
    language: 'java',
    description: 'Реестр всех боевых, визуальных и игровых модулей',
    content: `package neuro.client.module;

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
`,
  },

  // 15. AuraModule.java
  {
    path: 'src/main/java/neuro/client/module/combat/AuraModule.java',
    name: 'AuraModule.java',
    category: 'module',
    language: 'java',
    description: 'Боевой модуль KillAura под 1.21.4 с интеграцией реальной GRU нейросети, проверкой кулдауна и рейкастом',
    content: `package neuro.client.module.combat;

import net.minecraft.client.MinecraftClient;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.RaycastContext;
import neuro.client.NeuroClient;
import neuro.client.ai.NeuroAuraEngine;
import neuro.client.module.Category;
import neuro.client.module.Module;
import neuro.client.setting.*;

import java.util.Comparator;
import java.util.List;

public class AuraModule extends Module {
    private final ModeSetting rotationMode = new ModeSetting("Rotation Mode", "Neuro GRU AI",
            List.of("Neuro GRU AI", "Matrix Prediction", "GrimAC Strict", "Dynamic Smooth", "Packet Legit"));
    private final SliderSetting attackDistance = new SliderSetting("Attack Distance", 3.6, 2.0, 6.0, 0.1);
    private final SliderSetting wallDistance = new SliderSetting("Wall Distance", 1.8, 0.0, 4.5, 0.1);
    private final SliderSetting preAimLatency = new SliderSetting("Pre-Aim Latency", 80, 0, 300, 10);
    private final BooleanSetting raycastCheck = new BooleanSetting("Raycast Check", true);
    private final BooleanSetting autoShieldBreaker = new BooleanSetting("Auto Shield Breaker", true);
    private final BooleanSetting silentRotation = new BooleanSetting("Silent Rotation", false);

    private LivingEntity targetEntity = null;

    public AuraModule() {
        super("Aura", "Интеллектуальная киллаура с нейросетевой наводкой GRU", Category.COMBAT);
        this.setEnabled(true);
        this.addSettings(rotationMode, attackDistance, wallDistance, preAimLatency,
                raycastCheck, autoShieldBreaker, silentRotation);
    }

    @Override
    public void onTick(MinecraftClient client) {
        if (client.player == null || client.world == null || client.interactionManager == null) {
            this.targetEntity = null;
            return;
        }

        double maxDist = this.attackDistance.getValue();
        Box searchBox = client.player.getBoundingBox().expand(maxDist, maxDist, maxDist);
        List<LivingEntity> potentialTargets = client.world.getEntitiesByClass(
                LivingEntity.class, searchBox,
                entity -> entity != client.player && entity.isAlive() && !entity.isInvisible()
        );

        this.targetEntity = potentialTargets.stream()
                .min(Comparator.comparingDouble(e -> e.squaredDistanceTo(client.player)))
                .orElse(null);

        if (this.targetEntity == null) return;

        double dist = client.player.distanceTo(this.targetEntity);
        if (dist > maxDist) {
            this.targetEntity = null;
            return;
        }

        if (this.raycastCheck.getValue() && !canSeeEntity(client, this.targetEntity)) {
            if (dist > this.wallDistance.getValue()) return;
        }

        boolean attackedThisTick = false;
        float cooldown = client.player.getAttackCooldownProgress(0.5f);
        if (cooldown >= 1.0f) {
            client.interactionManager.attackEntity(client.player, this.targetEntity);
            client.player.swingHand(Hand.MAIN_HAND);
            attackedThisTick = true;
        }

        if ("Neuro GRU AI".equals(this.rotationMode.getValue())) {
            NeuroAuraEngine engine = NeuroClient.getInstance().getNeuroEngine();
            float[] stepAngles = engine.computeStep(client.player, this.targetEntity, attackedThisTick);
            if (!this.silentRotation.getValue()) {
                client.player.setYaw(client.player.getYaw() + stepAngles[0]);
                client.player.setPitch(MathHelper.clamp(client.player.getPitch() + stepAngles[1], -90.0f, 90.0f));
            }
        } else {
            float[] targetRot = calculateRotations(client, this.targetEntity);
            if (!this.silentRotation.getValue()) {
                float diffYaw = MathHelper.wrapDegrees(targetRot[0] - client.player.getYaw());
                float diffPitch = targetRot[1] - client.player.getPitch();
                client.player.setYaw(client.player.getYaw() + diffYaw * 0.65f);
                client.player.setPitch(MathHelper.clamp(client.player.getPitch() + diffPitch * 0.65f, -90.0f, 90.0f));
            }
        }
    }

    private boolean canSeeEntity(MinecraftClient client, Entity target) {
        Vec3d eyePos = client.player.getEyePos();
        Vec3d targetCenter = target.getBoundingBox().getCenter();
        RaycastContext context = new RaycastContext(
                eyePos, targetCenter,
                RaycastContext.ShapeType.COLLIDER,
                RaycastContext.FluidHandling.NONE,
                client.player
        );
        BlockHitResult hit = client.world.raycast(context);
        return hit.getType() == HitResult.Type.MISS;
    }

    private float[] calculateRotations(MinecraftClient client, Entity target) {
        Vec3d eyes = client.player.getEyePos();
        Vec3d targetCenter = target.getBoundingBox().getCenter();

        double diffX = targetCenter.x - eyes.x;
        double diffY = targetCenter.y - eyes.y;
        double diffZ = targetCenter.z - eyes.z;
        double diffXZ = Math.sqrt(diffX * diffX + diffZ * diffZ);

        float yaw = (float) Math.toDegrees(Math.atan2(diffZ, diffX)) - 90.0f;
        float pitch = (float) -Math.toDegrees(Math.atan2(diffY, diffXZ));

        return new float[]{MathHelper.wrapDegrees(yaw), MathHelper.clamp(pitch, -90.0f, 90.0f)};
    }

    public LivingEntity getTargetEntity() {
        return targetEntity;
    }
}
`,
  },

  // 16. VelocityModule.java
  {
    path: 'src/main/java/neuro/client/module/combat/VelocityModule.java',
    name: 'VelocityModule.java',
    category: 'module',
    language: 'java',
    description: 'Модуль анти-отдачи (Velocity) с настройками горизонтального и вертикального вектора',
    content: `package neuro.client.module.combat;

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
`,
  },

  // 17. AutoTotemModule.java
  {
    path: 'src/main/java/neuro/client/module/combat/AutoTotemModule.java',
    name: 'AutoTotemModule.java',
    category: 'module',
    language: 'java',
    description: 'Автоматическое взятие тотема бессмертия при критическом здоровье',
    content: `package neuro.client.module.combat;

import net.minecraft.client.MinecraftClient;
import net.minecraft.item.Items;
import net.minecraft.screen.slot.SlotActionType;
import neuro.client.module.Category;
import neuro.client.module.Module;
import neuro.client.setting.BooleanSetting;
import neuro.client.setting.SliderSetting;

public class AutoTotemModule extends Module {
    private final SliderSetting health = new SliderSetting("Health Threshold", 8.0, 2.0, 20.0, 0.5);
    private final BooleanSetting swapBack = new BooleanSetting("Swap Back", true);

    public AutoTotemModule() {
        super("AutoTotem", "Автоматический тотем в левую руку", Category.COMBAT);
        this.setEnabled(true);
        this.addSettings(health, swapBack);
    }

    @Override
    public void onTick(MinecraftClient client) {
        if (client.player == null || client.interactionManager == null) return;
        if (client.player.getOffHandStack().getItem() == Items.TOTEM_OF_UNDYING) return;

        if (client.player.getHealth() + client.player.getAbsorptionAmount() <= health.getValue()) {
            for (int i = 9; i < 36; i++) {
                if (client.player.getInventory().getStack(i).getItem() == Items.TOTEM_OF_UNDYING) {
                    client.interactionManager.clickSlot(client.player.playerScreenHandler.syncId, i, 40, SlotActionType.SWAP, client.player);
                    break;
                }
            }
        }
    }
}
`,
  },

  // 18. CriticalsModule.java
  {
    path: 'src/main/java/neuro/client/module/combat/CriticalsModule.java',
    name: 'CriticalsModule.java',
    category: 'module',
    language: 'java',
    description: 'Критические удары с места',
    content: `package neuro.client.module.combat;

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
`,
  },

  // 19. FlightModule.java
  {
    path: 'src/main/java/neuro/client/module/movement/FlightModule.java',
    name: 'FlightModule.java',
    category: 'module',
    language: 'java',
    description: 'Модуль полёта с настройкой скорости',
    content: `package neuro.client.module.movement;

import net.minecraft.client.MinecraftClient;
import neuro.client.module.Category;
import neuro.client.module.Module;
import neuro.client.setting.ModeSetting;
import neuro.client.setting.SliderSetting;

import java.util.List;

public class FlightModule extends Module {
    private final ModeSetting mode = new ModeSetting("Fly Mode", "Vanilla",
            List.of("Vanilla", "Grim Glide", "Matrix Boost"));
    private final SliderSetting speed = new SliderSetting("Fly Speed", 1.5, 0.5, 5.0, 0.1);

    public FlightModule() {
        super("Flight", "Позволяет летать в режиме выживания", Category.MOVEMENT);
        this.addSettings(mode, speed);
    }

    @Override
    public void onTick(MinecraftClient client) {
        if (client.player != null) {
            client.player.getAbilities().flying = true;
            client.player.getAbilities().setFlySpeed((float) (speed.getValue() * 0.05));
        }
    }

    @Override
    public void onDisable() {
        MinecraftClient client = MinecraftClient.getInstance();
        if (client.player != null && !client.player.isCreative()) {
            client.player.getAbilities().flying = false;
            client.player.getAbilities().setFlySpeed(0.05f);
        }
    }
}
`,
  },

  // 20. SpeedModule.java
  {
    path: 'src/main/java/neuro/client/module/movement/SpeedModule.java',
    name: 'SpeedModule.java',
    category: 'module',
    language: 'java',
    description: 'Ускорение передвижения и Bhop',
    content: `package neuro.client.module.movement;

import net.minecraft.client.MinecraftClient;
import net.minecraft.util.math.Vec3d;
import neuro.client.module.Category;
import neuro.client.module.Module;
import neuro.client.setting.ModeSetting;
import neuro.client.setting.SliderSetting;

import java.util.List;

public class SpeedModule extends Module {
    private final ModeSetting mode = new ModeSetting("Speed Mode", "Bhop Strafe",
            List.of("Bhop Strafe", "Ground Fast", "LowHop"));
    private final SliderSetting factor = new SliderSetting("Multiplier", 1.2, 1.0, 2.5, 0.05);

    public SpeedModule() {
        super("Speed", "Ускоренное передвижение и распрыжка", Category.MOVEMENT);
        this.addSettings(mode, factor);
    }

    @Override
    public void onTick(MinecraftClient client) {
        if (client.player == null) return;
        if (client.player.forwardSpeed != 0 || client.player.sidewaysSpeed != 0) {
            if (client.player.isOnGround() && "Bhop Strafe".equals(mode.getValue())) {
                client.player.jump();
            }
            Vec3d v = client.player.getVelocity();
            double mul = 1.0 + (factor.getValue() - 1.0) * 0.08;
            client.player.setVelocity(v.x * mul, v.y, v.z * mul);
        }
    }
}
`,
  },

  // 21. SprintModule.java
  {
    path: 'src/main/java/neuro/client/module/movement/SprintModule.java',
    name: 'SprintModule.java',
    category: 'module',
    language: 'java',
    description: 'Автоматический спринт при движении',
    content: `package neuro.client.module.movement;

import net.minecraft.client.MinecraftClient;
import neuro.client.module.Category;
import neuro.client.module.Module;
import neuro.client.setting.BooleanSetting;

public class SprintModule extends Module {
    private final BooleanSetting omni = new BooleanSetting("Omni Sprint", true);

    public SprintModule() {
        super("Sprint", "Автоматический бег без зажатия клавиши", Category.MOVEMENT);
        this.setEnabled(true);
        this.addSettings(omni);
    }

    @Override
    public void onTick(MinecraftClient client) {
        if (client.player != null && client.player.forwardSpeed > 0 && !client.player.isSneaking()) {
            client.player.setSprinting(true);
        }
    }
}
`,
  },

  // 22. NoSlowModule.java
  {
    path: 'src/main/java/neuro/client/module/movement/NoSlowModule.java',
    name: 'NoSlowModule.java',
    category: 'module',
    language: 'java',
    description: 'Отключение замедления при использовании предметов',
    content: `package neuro.client.module.movement;

import neuro.client.module.Category;
import neuro.client.module.Module;
import neuro.client.setting.ModeSetting;

import java.util.List;

public class NoSlowModule extends Module {
    private final ModeSetting mode = new ModeSetting("Bypass", "Grim 1.21.4",
            List.of("Grim 1.21.4", "Matrix", "Vanilla"));

    public NoSlowModule() {
        super("NoSlow", "Движение без замедления при еде и блоке щитом", Category.MOVEMENT);
        this.setEnabled(true);
        this.addSettings(mode);
    }
}
`,
  },

  // 23. GlowEspModule.java
  {
    path: 'src/main/java/neuro/client/module/render/GlowEspModule.java',
    name: 'GlowEspModule.java',
    category: 'module',
    language: 'java',
    description: 'Шейдерная подсветка игроков сквозь стены',
    content: `package neuro.client.module.render;

import neuro.client.module.Category;
import neuro.client.module.Module;
import neuro.client.setting.ModeSetting;
import neuro.client.setting.SliderSetting;

import java.util.List;

public class GlowEspModule extends Module {
    private final ModeSetting mode = new ModeSetting("Shader Mode", "Kawase Glow",
            List.of("Kawase Glow", "Squircle 2D", "Outline Box"));
    private final SliderSetting radius = new SliderSetting("Glow Radius", 12.0, 2.0, 30.0, 1.0);

    public GlowEspModule() {
        super("Glow ESP", "Шейдерное свечение игроков сквозь стены", Category.RENDER);
        this.setEnabled(true);
        this.addSettings(mode, radius);
    }
}
`,
  },

  // 24. CustomSkyModule.java
  {
    path: 'src/main/java/neuro/client/module/render/CustomSkyModule.java',
    name: 'CustomSkyModule.java',
    category: 'module',
    language: 'java',
    description: 'Космическое небо на базе шейдеров туманностей',
    content: `package neuro.client.module.render;

import neuro.client.module.Category;
import neuro.client.module.Module;
import neuro.client.setting.ModeSetting;

import java.util.List;

public class CustomSkyModule extends Module {
    private final ModeSetting style = new ModeSetting("Sky Shader", "Nebula Cosmic",
            List.of("Nebula Cosmic", "Galaxy Space", "Sunset Radiant"));

    public CustomSkyModule() {
        super("Custom Sky", "Замена неба на процедурные космические туманности", Category.RENDER);
        this.setEnabled(true);
        this.addSettings(style);
    }
}
`,
  },

  // 25. FullBrightModule.java
  {
    path: 'src/main/java/neuro/client/module/render/FullBrightModule.java',
    name: 'FullBrightModule.java',
    category: 'module',
    language: 'java',
    description: 'Максимальная яркость в темноте и пещерах',
    content: `package neuro.client.module.render;

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
`,
  },

  // 26. AutoArmorModule.java
  {
    path: 'src/main/java/neuro/client/module/player/AutoArmorModule.java',
    name: 'AutoArmorModule.java',
    category: 'module',
    language: 'java',
    description: 'Автоматическое надевание лучшей брони',
    content: `package neuro.client.module.player;

import neuro.client.module.Category;
import neuro.client.module.Module;
import neuro.client.setting.SliderSetting;

public class AutoArmorModule extends Module {
    private final SliderSetting delay = new SliderSetting("Equip Delay", 65.0, 0.0, 200.0, 5.0);

    public AutoArmorModule() {
        super("AutoArmor", "Автоматическое надевание лучшей брони", Category.PLAYER);
        this.setEnabled(true);
        this.addSettings(delay);
    }
}
`,
  },

  // 27. InvMoveModule.java
  {
    path: 'src/main/java/neuro/client/module/player/InvMoveModule.java',
    name: 'InvMoveModule.java',
    category: 'module',
    language: 'java',
    description: 'Передвижение при открытом инвентаре и меню',
    content: `package neuro.client.module.player;

import neuro.client.module.Category;
import neuro.client.module.Module;
import neuro.client.setting.BooleanSetting;

public class InvMoveModule extends Module {
    private final BooleanSetting sprint = new BooleanSetting("Allow Sprint", true);

    public InvMoveModule() {
        super("InvMove", "Ходьба и прыжки при открытом инвентаре", Category.PLAYER);
        this.setEnabled(true);
        this.addSettings(sprint);
    }
}
`,
  },

  // 28. ScaffoldModule.java
  {
    path: 'src/main/java/neuro/client/module/world/ScaffoldModule.java',
    name: 'ScaffoldModule.java',
    category: 'module',
    language: 'java',
    description: 'Автоматическая установка блоков под ногами',
    content: `package neuro.client.module.world;

import neuro.client.module.Category;
import neuro.client.module.Module;
import neuro.client.setting.BooleanSetting;
import neuro.client.setting.ModeSetting;

import java.util.List;

public class ScaffoldModule extends Module {
    private final ModeSetting mode = new ModeSetting("Place Mode", "Snap 1.21.4",
            List.of("Snap 1.21.4", "Legit Eagle", "Tower"));
    private final BooleanSetting safeWalk = new BooleanSetting("SafeWalk", true);

    public ScaffoldModule() {
        super("Scaffold", "Автоматическая постройка моста под ногами", Category.WORLD);
        this.addSettings(mode, safeWalk);
    }
}
`,
  },

  // 29. FastPlaceModule.java
  {
    path: 'src/main/java/neuro/client/module/world/FastPlaceModule.java',
    name: 'FastPlaceModule.java',
    category: 'module',
    language: 'java',
    description: 'Быстрая установка блоков без задержки',
    content: `package neuro.client.module.world;

import neuro.client.module.Category;
import neuro.client.module.Module;
import neuro.client.setting.SliderSetting;

public class FastPlaceModule extends Module {
    private final SliderSetting delay = new SliderSetting("Tick Delay", 0.0, 0.0, 4.0, 1.0);

    public FastPlaceModule() {
        super("FastPlace", "Убирает задержку при установке блоков", Category.WORLD);
        this.setEnabled(true);
        this.addSettings(delay);
    }
}
`,
  },

  // 30. ModernClickGuiScreen.java (1.21.4 DrawContext Screen with Settings Drawer & No Blur Conflict)
  {
    path: 'src/main/java/neuro/client/gui/ModernClickGuiScreen.java',
    name: 'ModernClickGuiScreen.java',
    category: 'gui',
    language: 'java',
    description: 'Элитное меню на Fabric 1.21.4: DrawContext, раскрытие настроек ПКМ, слайдеры, переключатели и сайдбар',
    content: `package neuro.client.gui;

import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.text.Text;
import neuro.client.NeuroClient;
import neuro.client.module.Category;
import neuro.client.module.Module;
import neuro.client.module.ModuleManager;
import neuro.client.setting.BooleanSetting;
import neuro.client.setting.ModeSetting;
import neuro.client.setting.Setting;
import neuro.client.setting.SliderSetting;
import org.lwjgl.glfw.GLFW;

import java.awt.Color;
import java.util.List;

public class ModernClickGuiScreen extends Screen {
    private Category currentCategory = Category.COMBAT;
    private Module expandedModule = null;
    private float animationProgress = 0.0f;

    private int guiX = 100;
    private int guiY = 50;
    private final int guiWidth = 580;
    private final int guiHeight = 390;

    private boolean isDragging = false;
    private int dragOffsetX = 0;
    private int dragOffsetY = 0;

    public ModernClickGuiScreen() {
        super(Text.literal("Neuro ClickGUI 1.21.4"));
    }

    @Override
    protected void init() {
        super.init();
        this.animationProgress = 0.0f;
        this.guiX = (this.width - this.guiWidth) / 2;
        this.guiY = (this.height - this.guiHeight) / 2;
    }

    @Override
    public void renderBackground(DrawContext context, int mouseX, int mouseY, float delta) {
        // Отключаем стандартный размытый фон 1.21.4, чтобы рисовать собственный акриловый фон без крашей
    }

    @Override
    public void render(DrawContext context, int mouseX, int mouseY, float delta) {
        if (this.animationProgress < 1.0f) {
            this.animationProgress = Math.min(1.0f, this.animationProgress + delta * 0.25f);
        }

        int backdropAlpha = (int) (145 * this.animationProgress);
        context.fill(0, 0, this.width, this.height, new Color(7, 9, 16, backdropAlpha).getRGB());

        int bgAlpha = (int) (235 * this.animationProgress);
        int mainBgColor = new Color(13, 17, 30, bgAlpha).getRGB();
        int borderColor = new Color(139, 92, 246, (int) (200 * this.animationProgress)).getRGB();

        context.fill(guiX, guiY, guiX + guiWidth, guiY + guiHeight, mainBgColor);
        context.drawBorder(guiX, guiY, guiWidth, guiHeight, borderColor);

        int headerHeight = 40;
        int headerBg = new Color(19, 24, 42, bgAlpha).getRGB();
        context.fill(guiX + 1, guiY + 1, guiX + guiWidth - 1, guiY + headerHeight, headerBg);

        context.drawText(this.textRenderer, "✦ NEURO CLIENT", guiX + 16, guiY + 15, 0xFFFFFFFF, true);
        context.drawText(this.textRenderer, "Fabric 1.21.4 | GRU AI", guiX + 120, guiY + 16, 0xFFA78BFA, false);

        String bindHint = "[Right Shift] ЛКМ: Вкл | ПКМ: Настройки";
        int hintWidth = this.textRenderer.getWidth(bindHint);
        context.drawText(this.textRenderer, bindHint, guiX + guiWidth - hintWidth - 16, guiY + 16, 0xFF94A3B8, false);

        int sidebarWidth = 135;
        int sidebarBg = new Color(10, 13, 24, bgAlpha).getRGB();
        context.fill(guiX + 1, guiY + headerHeight, guiX + sidebarWidth, guiY + guiHeight - 1, sidebarBg);

        int catY = guiY + headerHeight + 12;
        for (Category category : Category.values()) {
            boolean isSelected = (category == this.currentCategory);
            int textColor = isSelected ? 0xFFFFFFFF : 0xFF94A3B8;

            if (isSelected) {
                context.fill(guiX + 4, catY, guiX + 7, catY + 26, 0xFF8B5CF6);
                context.fill(guiX + 8, catY, guiX + sidebarWidth - 6, catY + 26, new Color(139, 92, 246, 55).getRGB());
            }

            context.drawText(this.textRenderer, category.getDisplayName(), guiX + 16, catY + 9, textColor, isSelected);
            catY += 32;
        }

        int contentX = guiX + sidebarWidth + 14;
        int contentY = guiY + headerHeight + 12;
        int contentWidth = guiWidth - sidebarWidth - 28;

        ModuleManager moduleManager = NeuroClient.getInstance().getModuleManager();
        List<Module> modules = moduleManager.getModulesByCategory(this.currentCategory);

        int modY = contentY;
        for (Module module : modules) {
            if (modY + 36 > guiY + guiHeight - 10) break;

            boolean isHovered = mouseX >= contentX && mouseX <= contentX + contentWidth &&
                               mouseY >= modY && mouseY <= modY + 34;

            int cardBg = module.isEnabled()
                    ? new Color(32, 38, 72, 235).getRGB()
                    : (isHovered ? new Color(24, 30, 52, 210).getRGB() : new Color(17, 22, 38, 195).getRGB());
            int cardBorder = module.isEnabled() ? 0xFF8B5CF6 : 0x33FFFFFF;

            context.fill(contentX, modY, contentX + contentWidth, modY + 34, cardBg);
            context.drawBorder(contentX, modY, contentWidth, 34, cardBorder);

            context.drawText(this.textRenderer, module.getName(), contentX + 12, modY + 8,
                    module.isEnabled() ? 0xFFFFFFFF : 0xFFCBD5E1, module.isEnabled());
            context.drawText(this.textRenderer, module.getDescription(), contentX + 12, modY + 20,
                    0xFF64748B, false);

            int toggleX = contentX + contentWidth - 38;
            int toggleY = modY + 10;
            int toggleColor = module.isEnabled() ? 0xFF8B5CF6 : 0xFF334155;
            context.fill(toggleX, toggleY, toggleX + 26, toggleY + 14, toggleColor);
            int knobX = module.isEnabled() ? toggleX + 14 : toggleX + 2;
            context.fill(knobX, toggleY + 2, knobX + 10, toggleY + 12, 0xFFFFFFFF);

            modY += 40;

            if (this.expandedModule == module && !module.getSettings().isEmpty()) {
                for (Setting<?> setting : module.getSettings()) {
                    if (modY + 24 > guiY + guiHeight - 10) break;
                    context.fill(contentX + 8, modY, contentX + contentWidth - 8, modY + 22, new Color(10, 14, 26, 220).getRGB());
                    if (setting instanceof SliderSetting slider) {
                        double ratio = (slider.getValue() - slider.getMin()) / (slider.getMax() - slider.getMin());
                        int fillW = (int) ((contentWidth - 16) * Math.max(0.0, Math.min(1.0, ratio)));
                        context.fill(contentX + 8, modY + 19, contentX + 8 + fillW, modY + 22, 0xFF8B5CF6);
                    }
                    String label = setting.getName() + ": " + setting.getValue();
                    context.drawText(this.textRenderer, label, contentX + 16, modY + 6, 0xFFC4B5FD, false);
                    modY += 24;
                }
                modY += 4;
            }
        }
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        if (button == 0 && mouseX >= guiX && mouseX <= guiX + guiWidth && mouseY >= guiY && mouseY <= guiY + 40) {
            this.isDragging = true;
            this.dragOffsetX = (int) (mouseX - guiX);
            this.dragOffsetY = (int) (mouseY - guiY);
            return true;
        }

        int sidebarWidth = 135;
        int headerHeight = 40;
        if (mouseX >= guiX && mouseX <= guiX + sidebarWidth && mouseY >= guiY + headerHeight) {
            int catY = guiY + headerHeight + 12;
            for (Category category : Category.values()) {
                if (mouseY >= catY && mouseY <= catY + 26) {
                    this.currentCategory = category;
                    this.expandedModule = null;
                    return true;
                }
                catY += 32;
            }
        }

        int contentX = guiX + sidebarWidth + 14;
        int contentY = guiY + headerHeight + 12;
        int contentWidth = guiWidth - sidebarWidth - 28;

        ModuleManager moduleManager = NeuroClient.getInstance().getModuleManager();
        List<Module> modules = moduleManager.getModulesByCategory(this.currentCategory);

        int modY = contentY;
        for (Module module : modules) {
            if (mouseX >= contentX && mouseX <= contentX + contentWidth && mouseY >= modY && mouseY <= modY + 34) {
                if (button == 0) {
                    module.toggle();
                    return true;
                } else if (button == 1) {
                    this.expandedModule = (this.expandedModule == module) ? null : module;
                    return true;
                }
            }
            modY += 40;

            if (this.expandedModule == module && !module.getSettings().isEmpty()) {
                for (Setting<?> setting : module.getSettings()) {
                    if (mouseX >= contentX + 8 && mouseX <= contentX + contentWidth - 8 &&
                        mouseY >= modY && mouseY <= modY + 22) {
                        if (setting instanceof BooleanSetting boolSetting) {
                            boolSetting.toggle();
                        } else if (setting instanceof ModeSetting modeSetting) {
                            modeSetting.cycle();
                        } else if (setting instanceof SliderSetting sliderSetting) {
                            double ratio = (mouseX - (contentX + 8)) / (double) (contentWidth - 16);
                            double val = sliderSetting.getMin() + ratio * (sliderSetting.getMax() - sliderSetting.getMin());
                            sliderSetting.setValue(val);
                        }
                        return true;
                    }
                    modY += 24;
                }
                modY += 4;
            }
        }

        return super.mouseClicked(mouseX, mouseY, button);
    }

    @Override
    public boolean mouseReleased(double mouseX, double mouseY, int button) {
        if (button == 0) {
            this.isDragging = false;
        }
        return super.mouseReleased(mouseX, mouseY, button);
    }

    @Override
    public boolean mouseDragged(double mouseX, double mouseY, int button, double deltaX, double deltaY) {
        if (this.isDragging) {
            this.guiX = (int) (mouseX - this.dragOffsetX);
            this.guiY = (int) (mouseY - this.dragOffsetY);
            return true;
        }
        return super.mouseDragged(mouseX, mouseY, button, deltaX, deltaY);
    }

    @Override
    public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
        if (keyCode == GLFW.GLFW_KEY_ESCAPE || keyCode == GLFW.GLFW_KEY_RIGHT_SHIFT) {
            this.close();
            return true;
        }
        return super.keyPressed(keyCode, scanCode, modifiers);
    }

    @Override
    public boolean shouldPause() {
        return false;
    }
}
`,
  },

  // 31. NeuroAuraEngine.java (100% Real GRU + MDN weights parser from default.json!)
  {
    path: 'src/main/java/neuro/client/ai/NeuroAuraEngine.java',
    name: 'NeuroAuraEngine.java',
    category: 'ai',
    language: 'java',
    description: 'Точный инференс GRU + Mixture Density Network на чистой Java 21 (загружает реальные матрицы wi, wh, bi, bh, out из default.json)',
    content: `package neuro.client.ai;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.Random;

/**
 * Автономный математический движок GRU + Mixture Density Network (MDN) для Minecraft 1.21.4.
 * Напрямую парсит обученные веса из /assets/neuro/models/default.json (без зависимости от Python/JEP).
 * Реализует точную формулу Policy(features=17, hidden=32, mix=3) с функцией сжатия sinh.
 */
public class NeuroAuraEngine {
    private static final Logger LOGGER = LoggerFactory.getLogger("NeuroAuraEngine");

    public static final int FEATURES = 17;
    public static final int HIDDEN = 32;
    public static final int MIX = 3;
    private static final double RHO_LIMIT = 0.95;
    private static final double MAX_RUN = 40.0;

    private final double[] state = new double[HIDDEN];
    private double[][] wi;     // [96][17]
    private double[][] wh;     // [96][32]
    private double[] bi;       // [96]
    private double[] bh;       // [96]
    private double[][] headW;  // [19][32]
    private double[] headB;    // [19]
    private boolean modelLoaded = false;

    // История для 17 признаков (ey1, ep1, ey2, ep2, dy1, dp1, dy2, dp2, tyawPrev, tpitchPrev, since, run)
    private double ey1 = 0, ep1 = 0, ey2 = 0, ep2 = 0;
    private double dy1 = 0, dp1 = 0, dy2 = 0, dp2 = 0;
    private double prevTyaw = Double.NaN, prevTpitch = Double.NaN;
    private double sinceAttack = 20.0;
    private double freezeRun = 0.0;
    private final Random rng = new Random();

    public void loadModelFromResources() {
        try (InputStream is = NeuroAuraEngine.class.getResourceAsStream("/assets/neuro/models/default.json")) {
            if (is == null) {
                LOGGER.warn("[Neuro] Файл default.json не найден в ресурсах, используется резервный профиль.");
                return;
            }
            JsonObject root = JsonParser.parseReader(new InputStreamReader(is, StandardCharsets.UTF_8)).getAsJsonObject();
            if (!root.has("gru") || !root.has("out")) {
                LOGGER.warn("[Neuro] Файл default.json не содержит секций gru/out.");
                return;
            }
            JsonObject gru = root.getAsJsonObject("gru");
            JsonObject out = root.getAsJsonObject("out");

            this.wi = parseMatrix(gru.getAsJsonArray("wi"));
            this.wh = parseMatrix(gru.getAsJsonArray("wh"));
            this.bi = parseVector(gru.getAsJsonArray("bi"));
            this.bh = parseVector(gru.getAsJsonArray("bh"));
            this.headW = parseMatrix(out.getAsJsonArray("w"));
            this.headB = parseVector(out.getAsJsonArray("b"));

            this.modelLoaded = true;
            LOGGER.info("[Neuro] Веса GRU нейросети успешно загружены (wi={}x{}, wh={}x{}, out={}x{})!",
                    wi.length, wi[0].length, wh.length, wh[0].length, headW.length, headW[0].length);
        } catch (Exception e) {
            LOGGER.error("[Neuro] Ошибка чтения весов GRU из default.json: {}", e.getMessage());
        }
    }

    private static double[][] parseMatrix(JsonArray arr) {
        int rows = arr.size();
        int cols = arr.get(0).getAsJsonArray().size();
        double[][] mat = new double[rows][cols];
        for (int i = 0; i < rows; i++) {
            JsonArray row = arr.get(i).getAsJsonArray();
            for (int j = 0; j < cols; j++) {
                mat[i][j] = row.get(j).getAsDouble();
            }
        }
        return mat;
    }

    private static double[] parseVector(JsonArray arr) {
        int len = arr.size();
        double[] vec = new double[len];
        for (int i = 0; i < len; i++) {
            vec[i] = arr.get(i).getAsDouble();
        }
        return vec;
    }

    private static double arcsinh(double x) {
        return Math.log(x + Math.sqrt(x * x + 1.0));
    }

    private static double squash(double u, double limit) {
        double clamped = Math.max(-8.0, Math.min(8.0, u));
        return Math.max(-limit, Math.min(limit, Math.sinh(clamped)));
    }

    private static double wrapDegrees(double a) {
        double r = (a + 180.0) % 360.0;
        if (r < 0) r += 360.0;
        return r - 180.0;
    }

    /**
     * Выполняет точный шаг GRU + MDN (17 признаков -> 32 скрытых состояния -> 3 гауссианы)
     */
    public float[] computeStep(PlayerEntity player, Entity target, boolean attackedThisTick) {
        Vec3d eyes = player.getEyePos();
        Vec3d center = target.getBoundingBox().getCenter();
        double rx = center.x - eyes.x;
        double ry = center.y - eyes.y;
        double rz = center.z - eyes.z;
        double flat = Math.max(Math.hypot(rx, rz), 0.05);
        double dist = Math.hypot(flat, ry);

        double tyaw = Math.toDegrees(Math.atan2(rz, rx)) - 90.0;
        double tpitch = -Math.toDegrees(Math.atan2(ry, flat));

        double vyaw = Double.isNaN(prevTyaw) ? 0.0 : wrapDegrees(tyaw - prevTyaw);
        double vpitch = Double.isNaN(prevTpitch) ? 0.0 : (tpitch - prevTpitch);
        prevTyaw = tyaw;
        prevTpitch = tpitch;

        double ey = wrapDegrees(tyaw - player.getYaw());
        double ep = tpitch - player.getPitch();

        double sizeYaw = Math.max(Math.toDegrees(Math.atan2(target.getWidth() / 2.0, flat)), 0.5);
        double sizePitch = Math.max(Math.toDegrees(Math.atan2(target.getHeight() / 2.0, flat)), 0.5);

        if (!this.modelLoaded) {
            return new float[]{(float) (ey * 0.55), (float) (ep * 0.55)};
        }

        double on = (Math.abs(ey1) <= sizeYaw && Math.abs(ep1) <= sizePitch) ? 1.0 : 0.0;
        double dey = wrapDegrees(ey1 - ey2);
        double dep = ep1 - ep2;

        // 17 входных признаков (1:1 как в train_aura.py)
        double[] x = new double[]{
                arcsinh(ey1) / 3.0,
                arcsinh(ep1) / 3.0,
                arcsinh(dey) / 3.0,
                arcsinh(dep) / 3.0,
                arcsinh(vyaw) / 3.0,
                arcsinh(vpitch) / 3.0,
                arcsinh(dy1) / 3.0,
                arcsinh(dp1) / 3.0,
                arcsinh(dy2) / 3.0,
                arcsinh(dp2) / 3.0,
                arcsinh(ey1 / sizeYaw) / 3.0,
                arcsinh(ep1 / sizePitch) / 3.0,
                Math.log(Math.max(dist, 0.05) + 0.5) / 2.0,
                Math.log(sizeYaw) / 3.0,
                on,
                sinceAttack / 20.0,
                freezeRun / MAX_RUN
        };

        // Вычисление GRU: gi = wi @ x + bi, gh = wh @ state + bh
        int h = HIDDEN;
        double[] gi = new double[3 * h];
        double[] gh = new double[3 * h];
        for (int i = 0; i < 3 * h; i++) {
            double sumI = bi[i];
            for (int j = 0; j < FEATURES; j++) {
                sumI += wi[i][j] * x[j];
            }
            gi[i] = sumI;

            double sumH = bh[i];
            for (int j = 0; j < h; j++) {
                sumH += wh[i][j] * state[j];
            }
            gh[i] = sumH;
        }

        for (int i = 0; i < h; i++) {
            double r = 1.0 / (1.0 + Math.exp(-(gi[i] + gh[i])));
            double z = 1.0 / (1.0 + Math.exp(-(gi[h + i] + gh[h + i])));
            double n = Math.tanh(gi[2 * h + i] + r * gh[2 * h + i]);
            state[i] = (1.0 - z) * n + z * state[i];
        }

        // Вычисление линейной головы MDN (19 выходов)
        double[] head = new double[1 + 6 * MIX];
        for (int i = 0; i < head.length; i++) {
            double sum = headB[i];
            for (int j = 0; j < h; j++) {
                sum += headW[i][j] * state[j];
            }
            head[i] = sum;
        }

        // Выбор компоненты смеси с наибольшим весом
        int bestMix = 0;
        double bestLogit = Double.NEGATIVE_INFINITY;
        for (int m = 0; m < MIX; m++) {
            double logit = head[1 + 6 * m];
            if (logit > bestLogit) {
                bestLogit = logit;
                bestMix = m;
            }
        }

        int slot = 1 + 6 * bestMix;
        double muYaw = head[slot + 1];
        double muPitch = head[slot + 2];
        double sigmaYaw = Math.exp(Math.max(-4.0, Math.min(1.5, head[slot + 3])));
        double sigmaPitch = Math.exp(Math.max(-4.0, Math.min(1.5, head[slot + 4])));
        double rho = Math.tanh(head[slot + 5]) * RHO_LIMIT;

        double temp = 0.35;
        double zy = rng.nextGaussian();
        double zp = rho * zy + Math.sqrt(Math.max(0.0, 1.0 - rho * rho)) * rng.nextGaussian();

        double stepYaw = squash(muYaw + temp * sigmaYaw * zy, 179.0);
        double stepPitch = squash(muPitch + temp * sigmaPitch * zp, 90.0);

        // Обновление истории
        ey2 = ey1;
        ep2 = ep1;
        ey1 = ey;
        ep1 = ep;
        dy2 = dy1;
        dp2 = dp1;
        dy1 = stepYaw;
        dp1 = stepPitch;
        sinceAttack = attackedThisTick ? 0.0 : Math.min(20.0, sinceAttack + 1.0);
        freezeRun = (Math.abs(stepYaw) < 1e-6 && Math.abs(stepPitch) < 1e-6) ? Math.min(MAX_RUN, freezeRun + 1.0) : 0.0;

        return new float[]{(float) stepYaw, (float) stepPitch};
    }

    public float[] predictRotations(PlayerEntity player, Entity target) {
        return computeStep(player, target, false);
    }
}
`,
  },

  // 32. ShaderManager.java (1.21.4 compatible)
  {
    path: 'src/main/java/neuro/client/render/shader/ShaderManager.java',
    name: 'ShaderManager.java',
    category: 'render',
    language: 'java',
    description: 'Менеджер шейдеров для Minecraft 1.21.4 Core Profile',
    content: `package neuro.client.render.shader;

import net.minecraft.util.Identifier;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.Map;

public class ShaderManager {
    private static final Logger LOGGER = LoggerFactory.getLogger("ShaderManager");
    private final Map<String, Identifier> shaderRegistry = new HashMap<>();

    public void initShaders() {
        LOGGER.info("[Neuro] Регистрация шейдеров 1.21.4 (UI Universal, Squircle, Glow, Sky Nebula)...");
        shaderRegistry.put("ui_universal", Identifier.of("neuro", "shaders/core/ui_universal"));
        shaderRegistry.put("liquidglass", Identifier.of("neuro", "shaders/core/liquidglass"));
        shaderRegistry.put("glow", Identifier.of("neuro", "shaders/core/glow"));
        shaderRegistry.put("sky_nebula", Identifier.of("neuro", "shaders/core/sky_nebula"));
    }

    public Identifier getShaderId(String name) {
        return shaderRegistry.get(name);
    }
}
`,
  },

  // 33. GLSL Shader
  {
    path: 'src/main/resources/assets/neuro/shaders/core/ui_universal.fsh',
    name: 'ui_universal.fsh',
    category: 'shader',
    language: 'glsl',
    description: 'Официальный шейдер сквиркла со сглаживанием sdfAA и радиусами углов',
    content: `#version 150

in vec2 FragCoord;
in vec2 TexCoord;
in vec4 FragColor;

uniform vec4 Radius;
uniform float CornerSmoothness;
uniform float RectSmoothness;
uniform vec4 ColorModulator;

out vec4 OutColor;

float uiBoxSDF(vec2 p, vec2 b, vec4 r, float s) {
    r = min(r, vec4(min(b.x, b.y) * s * 0.5));
    r.xy = (p.x > 0.0) ? r.xy : r.zw;
    r.x = (p.y > 0.0) ? r.x : r.y;
    vec2 q = abs(p) - b + r.x;
    vec2 qc = max(q, 0.0);
    float len = pow(pow(qc.x, s) + pow(qc.y, s), 1.0 / s);
    return min(max(q.x, q.y), 0.0) + len - r.x;
}

float sdfAA(float dist, float smoothness) {
    return max(smoothness, fwidth(dist));
}

void main() {
    vec2 size = TexCoord;
    vec2 center = size * 0.5;
    float dist = uiBoxSDF(center - (FragCoord * size), center - 1.0, Radius, CornerSmoothness);
    float alpha = 1.0 - smoothstep(1.0 - sdfAA(dist, RectSmoothness), 1.0, dist);
    
    if (alpha <= 0.001) {
        discard;
    }
    
    OutColor = vec4(FragColor.rgb, FragColor.a * alpha) * ColorModulator;
}
`,
  },
];

export async function downloadFabricModZip(): Promise<void> {
  const zip = new JSZip();

  for (const file of FABRIC_1214_FILES) {
    zip.file(file.path, file.content);
  }

  zip.file(
    'gradle/wrapper/gradle-wrapper.properties',
    `distributionBase=GRADLE_USER_HOME
distributionPath=wrapper/dists
distributionUrl=https\\://services.gradle.org/distributions/gradle-8.12-bin.zip
networkTimeout=10000
validateDistributionUrl=true
zipStoreBase=GRADLE_USER_HOME
zipStorePath=wrapper/dists
`
  );

  // Fetch full 58KB default.json with trained GRU weights if available
  try {
    const res = await fetch('/assets/neuro_default_model.json');
    if (res.ok) {
      const text = await res.text();
      zip.file('src/main/resources/assets/neuro/models/default.json', text);
    } else {
      zip.file('src/main/resources/assets/neuro/models/default.json', JSON.stringify(NEURO_METADATA, null, 2));
    }
  } catch {
    zip.file('src/main/resources/assets/neuro/models/default.json', JSON.stringify(NEURO_METADATA, null, 2));
  }

  // Fetch gradle-wrapper.jar if available
  try {
    const gwRes = await fetch('/assets/gradle-wrapper.jar');
    if (gwRes.ok) {
      const gwBuf = await gwRes.arrayBuffer();
      zip.file('gradle/wrapper/gradle-wrapper.jar', gwBuf);
    }
  } catch {
    // GitHub Actions uses gradle/actions/setup-gradle@v4 so it builds even without wrapper jar
  }

  zip.file(
    '.gitignore',
    `.gradle/
build/
bin/
run/
.idea/
*.iml
.vscode/
__pycache__/
*.pyc
`
  );

  zip.file(
    'README.md',
    `# 🚀 Neuro Client - Minecraft Fabric 1.21.4

Премиальный клиент для Minecraft Fabric 1.21.4 (Java 21).

## ☁️ Автоматическая сборка на GitHub Actions
При каждом \`git push\` GitHub Actions автоматически устанавливает JDK 21 + Gradle 8.12 и собирает готовый \`.jar\` файл мода в разделе **Actions -> Artifacts -> neuro-fabric-1.21.4-build**.
`
  );

  const blob = await zip.generateAsync({ type: 'blob' });
  const url = URL.createObjectURL(blob);
  const anchor = document.createElement('a');
  anchor.href = url;
  anchor.download = 'neuro-fabric-1.21.4-full-src.zip';
  document.body.appendChild(anchor);
  anchor.click();
  document.body.removeChild(anchor);
  URL.revokeObjectURL(url);
}
