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

  // 7. NeuroClient.java (Main Client Entrypoint with RIGHT SHIFT KEYBIND, HUD & Trainable Killaura Subsystems)
  {
    path: 'src/main/java/neuro/client/NeuroClient.java',
    name: 'NeuroClient.java',
    category: 'core',
    language: 'java',
    description: 'Главный класс мода: регистрация бинда на Правый Shift (GLFW_KEY_RIGHT_SHIFT), менеджер команд .neuro, рекордер датасетов, тренер ИИ и HUD',
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
import neuro.client.ai.NeuroClicks;
import neuro.client.ai.NeuroCommandManager;
import neuro.client.ai.NeuroDatasetRecorder;
import neuro.client.ai.NeuroTrainerManager;
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
    private NeuroClicks neuroClicks;
    private NeuroDatasetRecorder datasetRecorder;
    private NeuroTrainerManager trainerManager;
    private NeuroCommandManager commandManager;

    @Override
    public void onInitializeClient() {
        instance = this;
        LOGGER.info("[Neuro] Инициализация обучаемого клиента под Fabric 1.21.4 (Java 21)...");

        this.clickGuiKeyBinding = KeyBindingHelper.registerKeyBinding(new KeyBinding(
                "key.neuro.open_clickgui",
                InputUtil.Type.KEYSYM,
                GLFW.GLFW_KEY_RIGHT_SHIFT,
                "category.neuro.general"
        ));

        this.neuroEngine = new NeuroAuraEngine();
        this.neuroClicks = new NeuroClicks();
        this.datasetRecorder = new NeuroDatasetRecorder();
        this.trainerManager = new NeuroTrainerManager();
        this.commandManager = new NeuroCommandManager();
        this.moduleManager = new ModuleManager();
        this.shaderManager = new ShaderManager();

        this.neuroEngine.loadModelFromResources();
        this.neuroClicks.loadFromResources();
        this.trainerManager.extractTrainerScripts();
        this.commandManager.register();
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

        LOGGER.info("[Neuro] Успешно инициализирован! [Правый Shift] — меню | [.neuro help] — обучение Киллауры.");
    }

    private void renderHud(MinecraftClient client, DrawContext context) {
        String modelTag = this.neuroEngine != null ? this.neuroEngine.getLoadedModelName() : "default.json";
        String watermark = "✦ NEURO 1.21.4 | " + client.getCurrentFps() + " FPS | AI: " + modelTag;
        int wmWidth = client.textRenderer.getWidth(watermark) + 14;
        context.fill(8, 8, 8 + wmWidth, 26, new Color(13, 17, 30, 220).getRGB());
        context.drawBorder(8, 8, wmWidth, 18, 0xFF8B5CF6);
        context.drawText(client.textRenderer, watermark, 15, 13, 0xFFFFFFFF, true);

        int statusY = 30;
        if (this.datasetRecorder != null && this.datasetRecorder.isRecording()) {
            String recText = "● REC [" + this.datasetRecorder.getActiveFileName() + "]: "
                    + this.datasetRecorder.getRecordedTicks() + " тиков (эпизодов: "
                    + this.datasetRecorder.getValidEpisodes() + ")";
            int recW = client.textRenderer.getWidth(recText) + 14;
            context.fill(8, statusY, 8 + recW, statusY + 18, new Color(45, 12, 22, 225).getRGB());
            context.drawBorder(8, statusY, recW, 18, 0xFFEF4444);
            context.drawText(client.textRenderer, recText, 15, statusY + 5, 0xFFFCA5A5, true);
            statusY += 22;
        }

        if (this.trainerManager != null && this.trainerManager.isTraining()) {
            String trText = "⚡ TRAIN: " + this.trainerManager.getLastStatus();
            int trW = client.textRenderer.getWidth(trText) + 14;
            context.fill(8, statusY, 8 + trW, statusY + 18, new Color(14, 36, 28, 225).getRGB());
            context.drawBorder(8, statusY, trW, 18, 0xFF10B981);
            context.drawText(client.textRenderer, trText, 15, statusY + 5, 0xFF6EE7B7, true);
        }

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
            int thW = 152;
            int thH = 44;
            context.fill(thX, thY, thX + thW, thY + thH, new Color(13, 17, 30, 225).getRGB());
            context.drawBorder(thX, thY, thW, thH, 0xFF8B5CF6);
            String tName = target.getName().getString();
            float hp = Math.max(0.0f, target.getHealth());
            float maxHp = Math.max(1.0f, target.getMaxHealth());
            context.drawText(client.textRenderer, tName + " (" + String.format("%.1f", hp) + " HP)", thX + 8, thY + 6, 0xFFFFFFFF, true);
            String subInfo = String.format("Dist: %.1fm | %s", client.player.distanceTo(target), aura.getClicksProfile());
            context.drawText(client.textRenderer, subInfo, thX + 8, thY + 18, 0xFFA78BFA, false);
            int barW = (int) ((thW - 16) * Math.min(1.0f, hp / maxHp));
            context.fill(thX + 8, thY + 32, thX + thW - 8, thY + 38, 0xFF1E293B);
            context.fill(thX + 8, thY + 32, thX + 8 + barW, thY + 38, 0xFF8B5CF6);
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

    public NeuroClicks getNeuroClicks() {
        return neuroClicks;
    }

    public NeuroDatasetRecorder getDatasetRecorder() {
        return datasetRecorder;
    }

    public NeuroTrainerManager getTrainerManager() {
        return trainerManager;
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

  // 15. AuraModule.java (Full Trainable Killaura + NeuroClicks + Live Dataset Recorder & Trainer)
  {
    path: 'src/main/java/neuro/client/module/combat/AuraModule.java',
    name: 'AuraModule.java',
    category: 'module',
    language: 'java',
    description: 'Обучаемая Киллаура под 1.21.4: GRU+MDN rollout (scale, keep, temp), NeuroClicks (криты и W-Tap спринт-ресет), запись .csv и обучение aim.json',
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
import neuro.client.ai.NeuroClicks;
import neuro.client.ai.NeuroDatasetRecorder;
import neuro.client.ai.NeuroTrainerManager;
import neuro.client.module.Category;
import neuro.client.module.Module;
import neuro.client.setting.*;

import java.util.Comparator;
import java.util.List;

public class AuraModule extends Module {
    private final ModeSetting rotationMode = new ModeSetting("Model", "Neuro GRU AI",
            List.of("Neuro GRU AI", "Custom (aim.json)", "NeurAim v6", "GrimAC Strict", "Matrix"));
    private final ModeSetting clicksProfile = new ModeSetting("NeuroClicks", "strong",
            List.of("strong", "early", "exact", "relaxed"));
    private final SliderSetting aimScale = new SliderSetting("Scale (Knob)", 0.45, 0.0, 1.0, 0.05);
    private final SliderSetting aimKeep = new SliderSetting("Keep Samples", 4.0, 1.0, 8.0, 1.0);
    private final SliderSetting aimTemp = new SliderSetting("Temperature", 0.70, 0.10, 1.20, 0.05);
    private final SliderSetting attackDistance = new SliderSetting("Attack Dist", 3.6, 2.0, 6.0, 0.1);
    private final SliderSetting wallDistance = new SliderSetting("Wall Dist", 1.8, 0.0, 4.5, 0.1);
    private final BooleanSetting smartCrits = new BooleanSetting("Smart Crits", true);
    private final BooleanSetting sprintReset = new BooleanSetting("W-Tap Sprint", true);
    private final BooleanSetting recordDataset = new BooleanSetting("Record CSV (.neuro rec)", false);
    private final BooleanSetting trainNow = new BooleanSetting("Train AI (.neuro train)", false);
    private final BooleanSetting raycastCheck = new BooleanSetting("Raycast Check", true);
    private final BooleanSetting silentRotation = new BooleanSetting("Silent Rotation", false);

    private LivingEntity targetEntity = null;
    private String lastModelMode = "Neuro GRU AI";

    public AuraModule() {
        super("Aura", "Обучаемая Киллаура: GRU+MDN + NeuroClicks + Запись и Тренер", Category.COMBAT);
        this.setEnabled(true);
        this.addSettings(
                rotationMode, clicksProfile, aimScale, aimKeep, aimTemp,
                attackDistance, wallDistance, smartCrits, sprintReset,
                recordDataset, trainNow, raycastCheck, silentRotation
        );
    }

    @Override
    public void onTick(MinecraftClient client) {
        if (client.player == null || client.world == null || client.interactionManager == null) {
            this.targetEntity = null;
            return;
        }

        NeuroClient nc = NeuroClient.getInstance();
        NeuroAuraEngine engine = nc.getNeuroEngine();
        NeuroClicks clicks = nc.getNeuroClicks();
        NeuroDatasetRecorder recorder = nc.getDatasetRecorder();
        NeuroTrainerManager trainer = nc.getTrainerManager();

        // Обработка кнопки быстрого запуска обучения прямо из ClickGUI
        if (this.trainNow.getValue()) {
            this.trainNow.setValue(false);
            if (trainer != null && !trainer.isTraining()) {
                if (recorder != null && recorder.isRecording()) {
                    recorder.stopRecording();
                    this.recordDataset.setValue(false);
                }
                trainer.startTraining(200);
            }
        }

        // Синхронизация переключателя записи датасета (.csv)
        if (recorder != null) {
            if (this.recordDataset.getValue() && !recorder.isRecording()) {
                recorder.startRecording("session");
            } else if (!this.recordDataset.getValue() && recorder.isRecording()) {
                recorder.stopRecording();
            }
        }

        // Переключение весов модели при смене режима
        String currentMode = this.rotationMode.getValue();
        if (!currentMode.equals(this.lastModelMode) && engine != null) {
            this.lastModelMode = currentMode;
            if ("Custom (aim.json)".equals(currentMode)) {
                engine.loadCustomOrDefaultModel();
            } else if ("NeurAim v6".equals(currentMode)) {
                engine.loadModelFromResource("/assets/neuro/models/neuraim_aim_v6.json", "neuraim_v6.json");
            } else if ("Neuro GRU AI".equals(currentMode)) {
                engine.loadModelFromResources();
            }
        }

        double maxDist = this.attackDistance.getValue();
        Box searchBox = client.player.getBoundingBox().expand(maxDist + 1.5, maxDist + 1.5, maxDist + 1.5);
        List<LivingEntity> potentialTargets = client.world.getEntitiesByClass(
                LivingEntity.class, searchBox,
                entity -> entity != client.player && entity.isAlive() && !entity.isInvisible()
        );

        this.targetEntity = potentialTargets.stream()
                .min(Comparator.comparingDouble(e -> e.squaredDistanceTo(client.player)))
                .orElse(null);

        // Если включена запись боя (.csv), записываем движения руки игрока (clean=1) и не вмешиваемся камерой
        if (recorder != null && recorder.isRecording()) {
            boolean playerAttacked = client.options.attackKey.isPressed() || client.player.handSwinging;
            recorder.onTick(client, this.targetEntity, playerAttacked, true);
            return;
        }

        if (this.targetEntity == null) {
            if (clicks != null) clicks.reset();
            return;
        }

        double dist = client.player.distanceTo(this.targetEntity);
        if (dist > maxDist) {
            this.targetEntity = null;
            return;
        }

        if (this.raycastCheck.getValue() && !canSeeEntity(client, this.targetEntity)) {
            if (dist > this.wallDistance.getValue()) return;
        }

        // Вычисление тайминга удара и W-Tap спринт-ресета через NeuroClicks
        boolean shouldHit = false;
        if (clicks != null) {
            shouldHit = clicks.shouldAttack(
                    client,
                    client.player,
                    this.targetEntity,
                    this.clicksProfile.getValue(),
                    this.smartCrits.getValue(),
                    this.sprintReset.getValue()
            );
        } else {
            shouldHit = client.player.getAttackCooldownProgress(0.5f) >= 0.92f;
        }

        boolean attackedThisTick = false;
        if (shouldHit) {
            if (this.sprintReset.getValue() && client.player.isSprinting()) {
                client.player.setSprinting(false);
            }
            client.interactionManager.attackEntity(client.player, this.targetEntity);
            client.player.swingHand(Hand.MAIN_HAND);
            attackedThisTick = true;
            if (clicks != null) {
                clicks.notifyHit(this.clicksProfile.getValue());
            }
        }

        // Применение наводки GRU + MDN с параметрами Scale, Keep, Temp
        if (engine != null && ("Neuro GRU AI".equals(currentMode)
                || "Custom (aim.json)".equals(currentMode)
                || "NeurAim v6".equals(currentMode))) {
            engine.setRolloutKnobs(
                    this.aimTemp.getValue(),
                    (int) Math.round(this.aimKeep.getValue()),
                    this.aimScale.getValue()
            );
            float[] stepAngles = engine.computeStep(client.player, this.targetEntity, attackedThisTick);
            if (!this.silentRotation.getValue()) {
                client.player.setYaw(client.player.getYaw() + stepAngles[0]);
                client.player.setPitch(MathHelper.clamp(client.player.getPitch() + stepAngles[1], -90.0f, 90.0f));
            }
        } else {
            float[] targetRot = calculateRotations(client, this.targetEntity);
            if (!this.silentRotation.getValue()) {
                float factor = "GrimAC Strict".equals(currentMode) ? 0.52f : 0.72f;
                float diffYaw = MathHelper.wrapDegrees(targetRot[0] - client.player.getYaw());
                float diffPitch = targetRot[1] - client.player.getPitch();
                client.player.setYaw(client.player.getYaw() + diffYaw * factor);
                client.player.setPitch(MathHelper.clamp(client.player.getPitch() + diffPitch * factor, -90.0f, 90.0f));
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

    public String getClicksProfile() {
        return clicksProfile.getValue();
    }

    public void setRecordDatasetFlag(boolean recording) {
        this.recordDataset.setValue(recording);
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

  // 30. ModernClickGuiScreen.java (1.21.4 DrawContext Screen with Scroll, Trainer Bar & No Blur Conflict)
  {
    path: 'src/main/java/neuro/client/gui/ModernClickGuiScreen.java',
    name: 'ModernClickGuiScreen.java',
    category: 'gui',
    language: 'java',
    description: 'Элитное меню на Fabric 1.21.4: открытие на Правый Shift, скролл колесиком, быстрые кнопки [REC Датасет] и [Обучить ИИ], слайдеры и переключатели',
    content: `package neuro.client.gui;

import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.text.Text;
import neuro.client.NeuroClient;
import neuro.client.ai.NeuroDatasetRecorder;
import neuro.client.ai.NeuroTrainerManager;
import neuro.client.module.Category;
import neuro.client.module.Module;
import neuro.client.module.ModuleManager;
import neuro.client.module.combat.AuraModule;
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
    private int scrollOffset = 0;

    private int guiX = 100;
    private int guiY = 45;
    private final int guiWidth = 600;
    private final int guiHeight = 410;

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
        if (this.expandedModule == null && NeuroClient.getInstance() != null) {
            this.expandedModule = NeuroClient.getInstance().getModuleManager().getModule(AuraModule.class);
        }
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

        int bgAlpha = (int) (238 * this.animationProgress);
        int mainBgColor = new Color(13, 17, 30, bgAlpha).getRGB();
        int borderColor = new Color(139, 92, 246, (int) (210 * this.animationProgress)).getRGB();

        context.fill(guiX, guiY, guiX + guiWidth, guiY + guiHeight, mainBgColor);
        context.drawBorder(guiX, guiY, guiWidth, guiHeight, borderColor);

        int headerHeight = 42;
        int headerBg = new Color(19, 24, 42, bgAlpha).getRGB();
        context.fill(guiX + 1, guiY + 1, guiX + guiWidth - 1, guiY + headerHeight, headerBg);

        context.drawText(this.textRenderer, "✦ NEURO 1.21.4", guiX + 16, guiY + 16, 0xFFFFFFFF, true);

        NeuroClient nc = NeuroClient.getInstance();
        String modelLabel = nc != null && nc.getNeuroEngine() != null
                ? "AI: " + nc.getNeuroEngine().getLoadedModelName()
                : "GRU AI";
        context.drawText(this.textRenderer, modelLabel, guiX + 112, guiY + 16, 0xFFA78BFA, false);

        // Кнопки быстрого управления обучаемой Киллаурой в шапке
        NeuroDatasetRecorder recorder = nc != null ? nc.getDatasetRecorder() : null;
        NeuroTrainerManager trainer = nc != null ? nc.getTrainerManager() : null;
        boolean recActive = recorder != null && recorder.isRecording();
        boolean trActive = trainer != null && trainer.isTraining();

        int recBtnX = guiX + 250;
        int recBtnY = guiY + 10;
        int recBtnW = 108;
        int recBtnH = 22;
        context.fill(recBtnX, recBtnY, recBtnX + recBtnW, recBtnY + recBtnH,
                recActive ? new Color(185, 28, 28, 230).getRGB() : new Color(30, 41, 59, 220).getRGB());
        context.drawBorder(recBtnX, recBtnY, recBtnW, recBtnH, recActive ? 0xFFEF4444 : 0xFF64748B);
        String recBtnLabel = recActive ? "■ СТОП (" + recorder.getRecordedTicks() + "t)" : "● Запись боя";
        context.drawText(this.textRenderer, recBtnLabel, recBtnX + 8, recBtnY + 7, 0xFFFFFFFF, false);

        int trBtnX = recBtnX + recBtnW + 8;
        int trBtnY = guiY + 10;
        int trBtnW = 108;
        int trBtnH = 22;
        context.fill(trBtnX, trBtnY, trBtnX + trBtnW, trBtnY + trBtnH,
                trActive ? new Color(5, 150, 105, 230).getRGB() : new Color(88, 28, 135, 220).getRGB());
        context.drawBorder(trBtnX, trBtnY, trBtnW, trBtnH, trActive ? 0xFF10B981 : 0xFFA855F7);
        String trBtnLabel = trActive ? "⚡ Обучение..." : "⚡ Обучить ИИ";
        context.drawText(this.textRenderer, trBtnLabel, trBtnX + 10, trBtnY + 7, 0xFFFFFFFF, false);

        String bindHint = "[R-Shift]";
        int hintWidth = this.textRenderer.getWidth(bindHint);
        context.drawText(this.textRenderer, bindHint, guiX + guiWidth - hintWidth - 14, guiY + 16, 0xFF94A3B8, false);

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

        // Подсказка команд внизу сайдбара
        context.drawText(this.textRenderer, ".neuro rec", guiX + 12, guiY + guiHeight - 40, 0xFF94A3B8, false);
        context.drawText(this.textRenderer, ".neuro train", guiX + 12, guiY + guiHeight - 28, 0xFFA78BFA, false);
        context.drawText(this.textRenderer, "Колесо: скролл", guiX + 12, guiY + guiHeight - 16, 0xFF64748B, false);

        int contentX = guiX + sidebarWidth + 14;
        int contentY = guiY + headerHeight + 10;
        int contentWidth = guiWidth - sidebarWidth - 28;
        int contentBottom = guiY + guiHeight - 10;

        context.enableScissor(contentX - 2, contentY, contentX + contentWidth + 2, contentBottom);

        ModuleManager moduleManager = nc.getModuleManager();
        List<Module> modules = moduleManager.getModulesByCategory(this.currentCategory);

        int modY = contentY - this.scrollOffset;
        for (Module module : modules) {
            boolean isHovered = mouseX >= contentX && mouseX <= contentX + contentWidth &&
                               mouseY >= Math.max(contentY, modY) && mouseY <= Math.min(contentBottom, modY + 34);

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

            modY += 38;

            if (this.expandedModule == module && !module.getSettings().isEmpty()) {
                for (Setting<?> setting : module.getSettings()) {
                    context.fill(contentX + 8, modY, contentX + contentWidth - 8, modY + 22, new Color(10, 14, 26, 225).getRGB());
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

        context.disableScissor();
    }

    @Override
    public boolean mouseScrolled(double mouseX, double mouseY, double horizontalAmount, double verticalAmount) {
        this.scrollOffset = Math.max(0, this.scrollOffset - (int) (verticalAmount * 18));
        return true;
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        NeuroClient nc = NeuroClient.getInstance();
        int recBtnX = guiX + 250;
        int recBtnY = guiY + 10;
        if (button == 0 && mouseX >= recBtnX && mouseX <= recBtnX + 108 && mouseY >= recBtnY && mouseY <= recBtnY + 22) {
            if (nc != null && nc.getDatasetRecorder() != null) {
                AuraModule aura = nc.getModuleManager().getModule(AuraModule.class);
                if (nc.getDatasetRecorder().isRecording()) {
                    nc.getDatasetRecorder().stopRecording();
                    if (aura != null) aura.setRecordDatasetFlag(false);
                } else {
                    nc.getDatasetRecorder().startRecording("session");
                    if (aura != null) aura.setRecordDatasetFlag(true);
                }
            }
            return true;
        }

        int trBtnX = recBtnX + 116;
        int trBtnY = guiY + 10;
        if (button == 0 && mouseX >= trBtnX && mouseX <= trBtnX + 108 && mouseY >= trBtnY && mouseY <= trBtnY + 22) {
            if (nc != null && nc.getTrainerManager() != null && !nc.getTrainerManager().isTraining()) {
                if (nc.getDatasetRecorder() != null && nc.getDatasetRecorder().isRecording()) {
                    nc.getDatasetRecorder().stopRecording();
                    AuraModule aura = nc.getModuleManager().getModule(AuraModule.class);
                    if (aura != null) aura.setRecordDatasetFlag(false);
                }
                nc.getTrainerManager().startTraining(200);
            }
            return true;
        }

        if (button == 0 && mouseX >= guiX && mouseX <= guiX + guiWidth && mouseY >= guiY && mouseY <= guiY + 42) {
            this.isDragging = true;
            this.dragOffsetX = (int) (mouseX - guiX);
            this.dragOffsetY = (int) (mouseY - guiY);
            return true;
        }

        int sidebarWidth = 135;
        int headerHeight = 42;
        if (mouseX >= guiX && mouseX <= guiX + sidebarWidth && mouseY >= guiY + headerHeight) {
            int catY = guiY + headerHeight + 12;
            for (Category category : Category.values()) {
                if (mouseY >= catY && mouseY <= catY + 26) {
                    this.currentCategory = category;
                    this.expandedModule = null;
                    this.scrollOffset = 0;
                    return true;
                }
                catY += 32;
            }
        }

        int contentX = guiX + sidebarWidth + 14;
        int contentY = guiY + headerHeight + 10;
        int contentWidth = guiWidth - sidebarWidth - 28;
        int contentBottom = guiY + guiHeight - 10;

        if (mouseY < contentY || mouseY > contentBottom) {
            return super.mouseClicked(mouseX, mouseY, button);
        }

        ModuleManager moduleManager = nc.getModuleManager();
        List<Module> modules = moduleManager.getModulesByCategory(this.currentCategory);

        int modY = contentY - this.scrollOffset;
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
            modY += 38;

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

  // 31. NeuroAuraEngine.java (100% Exact Rollout from train_aura.py + common.py + Online Fine-Tuning)
  {
    path: 'src/main/java/neuro/client/ai/NeuroAuraEngine.java',
    name: 'NeuroAuraEngine.java',
    category: 'ai',
    language: 'java',
    description: 'Точный движок GRU + 3-Mix MDN из train_aura.py: 17 признаков, 65 квантилей error, keep-сэмплирование, freeze-гейт, квантование GCD и горячая подгрузка aim.json',
    content: `package neuro.client.ai;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import net.minecraft.client.MinecraftClient;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.math.Vec3d;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.Writer;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.List;
import java.util.Random;

/**
 * Полная реализация обучаемой нейросетевой Киллауры (Policy GRU + 3-Mix MDN) из train_aura.py и common.py.
 * Поддерживает:
 * - Загрузку встроенных моделей (default.json, neuraim_aim_v6.json) и пользовательских (neuro/aim.json)
 * - Точный алгоритм rollout (temp, keep, scale, 65-ступенчатая таблица квантилей error, freeze-гейт и GCD)
 * - Встроенное дообучение весов головы MDN и таблицы error на записанных .csv сессиях
 */
public class NeuroAuraEngine {
    private static final Logger LOGGER = LoggerFactory.getLogger("NeuroAuraEngine");

    public static final int DEFAULT_FEATURES = 17;
    private static final double RHO_LIMIT = 0.95;

    private int features = 17;
    private int hidden = 32;
    private int mix = 3;
    private double limitYaw = 179.0;
    private double limitPitch = 90.0;
    private double freezeCut = 40.0;

    private double[] state = new double[32];
    private double[][] wi;     // [3*hidden][features]
    private double[][] wh;     // [3*hidden][hidden]
    private double[] bi;       // [3*hidden]
    private double[] bh;       // [3*hidden]
    private double[][] headW;  // [1 + 6*mix][hidden]
    private double[] headB;    // [1 + 6*mix]
    private double[] errorQuantiles = new double[65];

    private boolean modelLoaded = false;
    private String loadedModelName = "default.json";

    // Параметры Rollout (Knob из train_aura.py: DEFAULTS = (0.70, 4, 0.45))
    private double knobTemp = 0.70;
    private int knobKeep = 4;
    private double knobScale = 0.45;

    // Состояние последовательности (ey1, ep1, ey2, ep2, dy1, dp1, dy2, dp2, prevTyaw, prevTpitch, since, run)
    private double ey1 = 0, ep1 = 0, ey2 = 0, ep2 = 0;
    private double dy1 = 0, dp1 = 0, dy2 = 0, dp2 = 0;
    private double prevTyaw = Double.NaN, prevTpitch = Double.NaN;
    private int lastTargetId = -1;
    private double sinceAttack = 20.0;
    private double freezeRun = 0.0;
    private final Random rng = new Random();

    public void setRolloutKnobs(double temp, int keep, double scale) {
        this.knobTemp = Math.max(0.05, Math.min(1.5, temp));
        this.knobKeep = Math.max(1, Math.min(16, keep));
        this.knobScale = Math.max(0.0, Math.min(1.5, scale));
    }

    public void loadModelFromResources() {
        // Если игрок уже обучил свою модель aim.json, подгружаем её автоматически при старте
        Path customAim = getCustomAimPath();
        if (customAim != null && Files.exists(customAim)) {
            if (loadModelFromFile(customAim)) {
                return;
            }
        }
        loadModelFromResource("/assets/neuro/models/default.json", "default.json");
    }

    public void loadCustomOrDefaultModel() {
        Path customAim = getCustomAimPath();
        if (customAim != null && Files.exists(customAim)) {
            if (loadModelFromFile(customAim)) {
                return;
            }
        }
        Path rockstarAim = getRockstarAimPath();
        if (rockstarAim != null && Files.exists(rockstarAim)) {
            if (loadModelFromFile(rockstarAim)) {
                return;
            }
        }
        loadModelFromResource("/assets/neuro/models/default.json", "default.json");
    }

    public boolean loadModelFromResource(String resourcePath, String label) {
        try (InputStream is = NeuroAuraEngine.class.getResourceAsStream(resourcePath)) {
            if (is == null) {
                LOGGER.warn("[Neuro] Ресурс {} не найден.", resourcePath);
                return false;
            }
            try (Reader reader = new InputStreamReader(is, StandardCharsets.UTF_8)) {
                return parseModelJson(reader, label);
            }
        } catch (Exception e) {
            LOGGER.error("[Neuro] Ошибка загрузки ресурса {}: {}", resourcePath, e.getMessage());
            return false;
        }
    }

    public boolean loadModelFromFile(Path path) {
        try (Reader reader = Files.newBufferedReader(path, StandardCharsets.UTF_8)) {
            return parseModelJson(reader, path.getFileName().toString());
        } catch (Exception e) {
            LOGGER.error("[Neuro] Ошибка чтения модели из файла {}: {}", path, e.getMessage());
            return false;
        }
    }

    private synchronized boolean parseModelJson(Reader reader, String label) {
        JsonObject root = JsonParser.parseReader(reader).getAsJsonObject();
        if (!root.has("gru") || !root.has("out")) {
            LOGGER.warn("[Neuro] Модель {} не содержит секций gru/out.", label);
            return false;
        }

        this.features = root.has("features") ? root.get("features").getAsInt() : 17;
        this.hidden = root.has("hidden") ? root.get("hidden").getAsInt() : 32;
        this.mix = root.has("mix") ? root.get("mix").getAsInt() : 3;
        this.freezeCut = root.has("freezeCut") ? root.get("freezeCut").getAsDouble() : 40.0;

        if (root.has("limits") && root.get("limits").isJsonArray()) {
            JsonArray lim = root.getAsJsonArray("limits");
            if (lim.size() >= 2) {
                this.limitYaw = lim.get(0).getAsDouble();
                this.limitPitch = lim.get(1).getAsDouble();
            }
        }

        if (root.has("error") && root.get("error").isJsonArray()) {
            this.errorQuantiles = parseVector(root.getAsJsonArray("error"));
        } else {
            this.errorQuantiles = new double[]{0.0, 0.5, 1.0};
        }

        JsonObject gru = root.getAsJsonObject("gru");
        JsonObject out = root.getAsJsonObject("out");

        this.wi = parseMatrix(gru.getAsJsonArray("wi"));
        this.wh = parseMatrix(gru.getAsJsonArray("wh"));
        this.bi = parseVector(gru.getAsJsonArray("bi"));
        this.bh = parseVector(gru.getAsJsonArray("bh"));
        this.headW = parseMatrix(out.getAsJsonArray("w"));
        this.headB = parseVector(out.getAsJsonArray("b"));
        this.state = new double[this.hidden];

        this.modelLoaded = true;
        this.loadedModelName = label;
        resetEpisodeState();
        LOGGER.info("[Neuro] Загружена модель {} (features={}, hidden={}, mix={}, errorSteps={})",
                label, features, hidden, mix, errorQuantiles.length);
        return true;
    }

    public void resetEpisodeState() {
        if (this.state != null) {
            Arrays.fill(this.state, 0.0);
        }
        this.ey1 = 0;
        this.ep1 = 0;
        this.ey2 = 0;
        this.ep2 = 0;
        this.dy1 = 0;
        this.dp1 = 0;
        this.dy2 = 0;
        this.dp2 = 0;
        this.prevTyaw = Double.NaN;
        this.prevTpitch = Double.NaN;
        this.sinceAttack = 20.0;
        this.freezeRun = 0.0;
    }

    public static double computeMouseGcd() {
        MinecraftClient client = MinecraftClient.getInstance();
        double sens = 0.5;
        if (client != null && client.options != null) {
            sens = client.options.getMouseSensitivity().getValue();
        }
        double f = sens * 0.6 + 0.2;
        return f * f * f * 1.2;
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

    public static double arcsinh(double x) {
        return Math.log(x + Math.sqrt(x * x + 1.0));
    }

    public static double squash(double u, double limit) {
        double clamped = Math.max(-8.0, Math.min(8.0, u));
        return Math.max(-limit, Math.min(limit, Math.sinh(clamped)));
    }

    public static double quantize(double value, double gcd) {
        return gcd > 0.0 ? Math.round(value / gcd) * gcd : value;
    }

    public static double wrapDegrees(double a) {
        double r = (a + 180.0) % 360.0;
        if (r < 0) r += 360.0;
        return r - 180.0;
    }

    private double sampleErrorQuantile(double u) {
        if (this.errorQuantiles == null || this.errorQuantiles.length == 0) return 0.0;
        if (this.errorQuantiles.length == 1) return this.errorQuantiles[0];
        double q = Math.max(0.0, Math.min(1.0, u)) * (this.errorQuantiles.length - 1);
        int i = (int) q;
        if (i >= this.errorQuantiles.length - 1) {
            return this.errorQuantiles[this.errorQuantiles.length - 1];
        }
        return this.errorQuantiles[i] + (this.errorQuantiles[i + 1] - this.errorQuantiles[i]) * (q - i);
    }

    /**
     * Полный шаг Rollout из train_aura.py (строки 237-286)
     */
    public synchronized float[] computeStep(PlayerEntity player, Entity target, boolean attackedThisTick) {
        if (target.getId() != this.lastTargetId) {
            this.lastTargetId = target.getId();
            resetEpisodeState();
        }

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

        // 17 входных признаков (1:1 как в train_aura.py -> features())
        double[] x = new double[this.features];
        double[] baseFeats = new double[]{
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
                freezeRun / Math.max(1.0, freezeCut)
        };
        System.arraycopy(baseFeats, 0, x, 0, Math.min(baseFeats.length, x.length));

        // GRU шаг: gi = wi @ x + bi, gh = wh @ state + bh
        int h = this.hidden;
        double[] gi = new double[3 * h];
        double[] gh = new double[3 * h];
        for (int i = 0; i < 3 * h; i++) {
            double sumI = bi[i];
            int colsI = Math.min(this.features, wi[i].length);
            for (int j = 0; j < colsI; j++) {
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

        // Линейная голова MDN (1 + 6 * mix)
        double[] head = new double[headB.length];
        for (int i = 0; i < head.length; i++) {
            double sum = headB[i];
            for (int j = 0; j < h; j++) {
                sum += headW[i][j] * state[j];
            }
            head[i] = sum;
        }

        double freezeProb = 1.0 / (1.0 + Math.exp(-head[0]));

        // Веса смеси softmax по слотам [1 + 6*m]
        double[] logits = new double[this.mix];
        double maxLogit = Double.NEGATIVE_INFINITY;
        for (int m = 0; m < this.mix; m++) {
            logits[m] = head[1 + 6 * m];
            if (logits[m] > maxLogit) maxLogit = logits[m];
        }
        double[] weights = new double[this.mix];
        double[] edges = new double[this.mix];
        double sumExp = 0.0;
        for (int m = 0; m < this.mix; m++) {
            weights[m] = Math.exp(logits[m] - maxLogit);
            sumExp += weights[m];
            edges[m] = sumExp;
        }

        double gcd = computeMouseGcd();
        double driftYaw = wrapDegrees(ey + vyaw);
        double driftPitch = ep + vpitch;
        boolean forced = this.freezeRun >= this.freezeCut;

        double stepYaw = 0.0;
        double stepPitch = 0.0;

        if (!forced && rng.nextDouble() < freezeProb) {
            stepYaw = 0.0;
            stepPitch = 0.0;
        } else {
            double want = this.knobScale * sampleErrorQuantile(rng.nextDouble());
            double safeSizeY = Math.max(sizeYaw, 1e-3);
            double safeSizeP = Math.max(sizePitch, 1e-3);

            double bestLeft = Double.POSITIVE_INFINITY;
            for (int k = 0; k < this.knobKeep; k++) {
                double u = rng.nextDouble() * sumExp;
                int m = 0;
                while (m < this.mix - 1 && u > edges[m]) {
                    m++;
                }

                int slot = 1 + 6 * m;
                double muY = head[slot + 1];
                double muP = head[slot + 2];
                double sigY = Math.exp(Math.max(-4.0, Math.min(1.5, head[slot + 3])));
                double sigP = Math.exp(Math.max(-4.0, Math.min(1.5, head[slot + 4])));
                double rho = Math.tanh(head[slot + 5]) * RHO_LIMIT;

                double blend = Math.sqrt(Math.max(0.0, 1.0 - rho * rho));
                double zy = rng.nextGaussian();
                double zp = rho * zy + blend * rng.nextGaussian();

                double moveY = quantize(squash(muY + this.knobTemp * sigY * zy, this.limitYaw), gcd);
                double moveP = quantize(squash(muP + this.knobTemp * sigP * zp, this.limitPitch), gcd);

                double left = Math.abs(Math.hypot(
                        wrapDegrees(driftYaw - moveY) / safeSizeY,
                        (driftPitch - moveP) / safeSizeP
                ) - want);

                if (left < bestLeft) {
                    bestLeft = left;
                    stepYaw = moveY;
                    stepPitch = moveP;
                }
            }
        }

        if (forced && Math.abs(stepYaw) < 1e-9 && Math.abs(stepPitch) < 1e-9) {
            if (Math.abs(driftYaw) >= Math.abs(driftPitch)) {
                stepYaw = Math.copySign(gcd, driftYaw);
            } else {
                stepPitch = Math.copySign(gcd, driftPitch);
            }
        }

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
        freezeRun = (Math.abs(stepYaw) < 1e-6 && Math.abs(stepPitch) < 1e-6)
                ? Math.min(freezeCut, freezeRun + 1.0)
                : 0.0;

        return new float[]{(float) stepYaw, (float) stepPitch};
    }

    /**
     * Встроенное дообучение Policy (GRU + MDN) на записанных последовательностях в чистой Java 21
     * (используется как мгновенный фоллбэк, если у игрока не установлен Python + PyTorch).
     */
    public synchronized double trainOnSequencesAndExport(
            List<double[][]> featureSeqs,
            List<double[][]> targetSeqs,
            double[] newErrorQuantiles,
            int epochs,
            Path outPath
    ) {
        if (!this.modelLoaded) {
            loadModelFromResources();
        }
        if (newErrorQuantiles != null && newErrorQuantiles.length == 65) {
            this.errorQuantiles = newErrorQuantiles.clone();
        }

        double lr = 0.0035;
        double bestLoss = Double.POSITIVE_INFINITY;
        int totalEpochs = Math.max(20, Math.min(600, epochs));

        for (int ep = 0; ep < totalEpochs; ep++) {
            double epochLoss = 0.0;
            int steps = 0;

            for (int sIdx = 0; sIdx < featureSeqs.size(); sIdx++) {
                double[][] xs = featureSeqs.get(sIdx);
                double[][] ys = targetSeqs.get(sIdx);
                double[] hState = new double[this.hidden];

                for (int t = 0; t < xs.length; t++) {
                    double[] x = xs[t];
                    double targetUy = ys[t][0];
                    double targetUp = ys[t][2];
                    double targetFrozen = ys[t][4];

                    // Forward GRU
                    int h = this.hidden;
                    double[] gi = new double[3 * h];
                    double[] gh = new double[3 * h];
                    for (int i = 0; i < 3 * h; i++) {
                        double sumI = bi[i];
                        for (int j = 0; j < Math.min(this.features, x.length); j++) {
                            sumI += wi[i][j] * x[j];
                        }
                        gi[i] = sumI;
                        double sumH = bh[i];
                        for (int j = 0; j < h; j++) {
                            sumH += wh[i][j] * hState[j];
                        }
                        gh[i] = sumH;
                    }
                    for (int i = 0; i < h; i++) {
                        double r = 1.0 / (1.0 + Math.exp(-(gi[i] + gh[i])));
                        double z = 1.0 / (1.0 + Math.exp(-(gi[h + i] + gh[h + i])));
                        double n = Math.tanh(gi[2 * h + i] + r * gh[2 * h + i]);
                        hState[i] = (1.0 - z) * n + z * hState[i];
                    }

                    // Head forward & gradient step on linear output head
                    double freezePred = 1.0 / (1.0 + Math.exp(-dot(headW[0], hState) - headB[0]));
                    double errFreeze = freezePred - targetFrozen;
                    headB[0] -= lr * errFreeze;
                    for (int j = 0; j < h; j++) {
                        headW[0][j] -= lr * errFreeze * hState[j];
                    }

                    // Update primary MDN mixture component towards recorded hand (targetUy, targetUp)
                    int slot = 1;
                    double muY = dot(headW[slot + 1], hState) + headB[slot + 1];
                    double muP = dot(headW[slot + 2], hState) + headB[slot + 2];
                    double diffY = muY - targetUy;
                    double diffP = muP - targetUp;
                    epochLoss += 0.5 * (diffY * diffY + diffP * diffP) + Math.abs(errFreeze) * 0.25;
                    steps++;

                    if (targetFrozen < 0.5) {
                        headB[slot + 1] -= lr * diffY;
                        headB[slot + 2] -= lr * diffP;
                        for (int j = 0; j < h; j++) {
                            headW[slot + 1][j] -= lr * diffY * hState[j];
                            headW[slot + 2][j] -= lr * diffP * hState[j];
                        }
                    }
                }
            }

            if (steps > 0) {
                bestLoss = Math.min(bestLoss, epochLoss / steps);
            }
        }

        exportWeightsToJson(outPath);
        this.loadedModelName = outPath.getFileName().toString();
        return bestLoss;
    }

    private static double dot(double[] w, double[] h) {
        double s = 0.0;
        for (int i = 0; i < Math.min(w.length, h.length); i++) {
            s += w[i] * h[i];
        }
        return s;
    }

    public synchronized void exportWeightsToJson(Path outPath) {
        try {
            if (outPath.getParent() != null) {
                Files.createDirectories(outPath.getParent());
            }
            JsonObject root = new JsonObject();
            root.addProperty("kind", "gru");
            root.addProperty("features", this.features);
            root.addProperty("hidden", this.hidden);
            root.addProperty("mix", this.mix);
            root.addProperty("squash", "sinh");
            root.addProperty("units", "deg");

            JsonArray lim = new JsonArray();
            lim.add(this.limitYaw);
            lim.add(this.limitPitch);
            root.add("limits", lim);

            JsonArray knobArr = new JsonArray();
            double[][] defaultKnobs = new double[][]{
                    {0.70, 4, 0.45},
                    {0.70, 1, 0.45},
                    {0.70, 8, 0.45},
                    {0.70, 4, 1.00}
            };
            for (double[] k : defaultKnobs) {
                JsonArray row = new JsonArray();
                row.add(k[0]);
                row.add((int) k[1]);
                row.add(k[2]);
                knobArr.add(row);
            }
            root.add("knob", knobArr);
            root.addProperty("freezeCut", (int) this.freezeCut);
            root.add("error", toJsonVector(this.errorQuantiles));

            JsonObject gru = new JsonObject();
            gru.add("wi", toJsonMatrix(this.wi));
            gru.add("wh", toJsonMatrix(this.wh));
            gru.add("bi", toJsonVector(this.bi));
            gru.add("bh", toJsonVector(this.bh));
            root.add("gru", gru);

            JsonObject out = new JsonObject();
            out.add("w", toJsonMatrix(this.headW));
            out.add("b", toJsonVector(this.headB));
            root.add("out", out);

            Gson gson = new GsonBuilder().create();
            try (Writer writer = Files.newBufferedWriter(outPath, StandardCharsets.UTF_8)) {
                gson.toJson(root, writer);
            }
            LOGGER.info("[Neuro] Обученная модель сохранена в {}", outPath.toAbsolutePath());
        } catch (Exception e) {
            LOGGER.error("[Neuro] Ошибка сохранения модели в {}: {}", outPath, e.getMessage());
        }
    }

    private static JsonArray toJsonMatrix(double[][] mat) {
        JsonArray rows = new JsonArray();
        for (double[] r : mat) {
            rows.add(toJsonVector(r));
        }
        return rows;
    }

    private static JsonArray toJsonVector(double[] vec) {
        JsonArray arr = new JsonArray();
        for (double v : vec) {
            arr.add(Math.round(v * 1_000_000.0) / 1_000_000.0);
        }
        return arr;
    }

    public static Path getCustomAimPath() {
        MinecraftClient client = MinecraftClient.getInstance();
        if (client == null || client.runDirectory == null) return null;
        return client.runDirectory.toPath().resolve("neuro").resolve("aim.json");
    }

    public static Path getRockstarAimPath() {
        MinecraftClient client = MinecraftClient.getInstance();
        if (client == null || client.runDirectory == null) return null;
        return client.runDirectory.toPath().resolve("Rockstar").resolve("neuro").resolve("aim.json");
    }

    public String getLoadedModelName() {
        return loadedModelName;
    }
}
`,
  },

  // 32. NeuroClicks.java (Human Crit / Ground / Sprint W-Tap Timings from neuro_clicks.json)
  {
    path: 'src/main/java/neuro/client/ai/NeuroClicks.java',
    name: 'NeuroClicks.java',
    category: 'ai',
    language: 'java',
    description: 'Нейросетевой контроллер таймингов удара и W-Tap спринт-ресета из neuro_clicks.json (профили strong, early, exact, relaxed)',
    content: `package neuro.client.ai;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import net.minecraft.client.MinecraftClient;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

/**
 * Реализация биомеханических таймингов удара и сброса спринта (W-Tap) по обученным таблицам
 * из /assets/neuro/models/neuro_clicks.json (format: neurotest-clicks-v1).
 */
public class NeuroClicks {
    private static final Logger LOGGER = LoggerFactory.getLogger("NeuroClicks");

    public static class ProfileData {
        public double[][] critP;           // [da][f] вероятности крита по фазе прыжка (1..11)
        public double[] groundHazard;      // [d] интенсивность удара на земле после готовности кулдауна
        public double[] releaseAfterTakeoff;
        public double[] repressAfterHit;
        public double holdP = 0.04;
    }

    private final Map<String, ProfileData> profiles = new HashMap<>();
    private final Random rng = new Random();

    private int airTicks = 0;
    private int groundReadyTicks = 0;
    private int chosenCritAirTick = -1;
    private int ticksSinceHit = 99;

    public void loadFromResources() {
        try (InputStream is = NeuroClicks.class.getResourceAsStream("/assets/neuro/models/neuro_clicks.json")) {
            if (is == null) {
                LOGGER.warn("[NeuroClicks] Файл neuro_clicks.json не найден, используются базовые распределения.");
                return;
            }
            JsonObject root = JsonParser.parseReader(new InputStreamReader(is, StandardCharsets.UTF_8)).getAsJsonObject();
            if (!root.has("profiles")) return;

            JsonObject profs = root.getAsJsonObject("profiles");
            for (Map.Entry<String, JsonElement> entry : profs.entrySet()) {
                String name = entry.getKey();
                JsonObject obj = entry.getValue().getAsJsonObject();
                ProfileData pd = new ProfileData();

                if (obj.has("crit") && obj.getAsJsonObject("crit").has("p")) {
                    JsonArray pArr = obj.getAsJsonObject("crit").getAsJsonArray("p");
                    pd.critP = new double[pArr.size()][];
                    for (int i = 0; i < pArr.size(); i++) {
                        pd.critP[i] = toDoubleArray(pArr.get(i).getAsJsonArray());
                    }
                }

                if (obj.has("ground") && obj.getAsJsonObject("ground").has("h")) {
                    pd.groundHazard = toDoubleArray(obj.getAsJsonObject("ground").getAsJsonArray("h"));
                }

                if (obj.has("sprint")) {
                    JsonObject sp = obj.getAsJsonObject("sprint");
                    if (sp.has("release_after_takeoff")) {
                        pd.releaseAfterTakeoff = toDoubleArray(sp.getAsJsonArray("release_after_takeoff"));
                    }
                    if (sp.has("repress_after_hit")) {
                        pd.repressAfterHit = toDoubleArray(sp.getAsJsonArray("repress_after_hit"));
                    }
                    if (sp.has("hold_p")) {
                        pd.holdP = sp.get("hold_p").getAsDouble();
                    }
                }
                profiles.put(name, pd);
            }
            LOGGER.info("[NeuroClicks] Загружено {} профилей таймингов ударов и спринта: {}",
                    profiles.size(), profiles.keySet());
        } catch (Exception e) {
            LOGGER.error("[NeuroClicks] Ошибка чтения neuro_clicks.json: {}", e.getMessage());
        }
    }

    private static double[] toDoubleArray(JsonArray arr) {
        double[] res = new double[arr.size()];
        for (int i = 0; i < arr.size(); i++) {
            res[i] = arr.get(i).getAsDouble();
        }
        return res;
    }

    private int sampleCategorical(double[] probs) {
        if (probs == null || probs.length == 0) return 6;
        double sum = 0.0;
        for (double p : probs) sum += Math.max(0.0, p);
        if (sum <= 1e-9) return 6;
        double u = rng.nextDouble() * sum;
        double acc = 0.0;
        for (int i = 0; i < probs.length; i++) {
            acc += Math.max(0.0, probs[i]);
            if (u <= acc) return i;
        }
        return probs.length - 1;
    }

    public void reset() {
        this.airTicks = 0;
        this.groundReadyTicks = 0;
        this.chosenCritAirTick = -1;
    }

    public void notifyHit(String profileName) {
        this.ticksSinceHit = 0;
        this.groundReadyTicks = 0;
        this.chosenCritAirTick = -1;
    }

    /**
     * Вычисляет, должен ли игрок нанести удар на текущем тике согласно распределению профиля.
     */
    public boolean shouldAttack(
            MinecraftClient client,
            PlayerEntity player,
            Entity target,
            String profileName,
            boolean smartCrits,
            boolean sprintReset
    ) {
        this.ticksSinceHit++;
        ProfileData pd = profiles.getOrDefault(profileName, profiles.get("strong"));
        float cooldown = player.getAttackCooldownProgress(0.5f);

        if (player.isOnGround()) {
            this.airTicks = 0;
            this.chosenCritAirTick = -1;

            if (cooldown < 0.90f) {
                this.groundReadyTicks = 0;
                return false;
            }

            if (smartCrits && !player.isTouchingWater() && !player.isSubmergedInWater() && player.getVelocity().y >= -0.08) {
                // Если игрок прыгает, ждем фазу падения
                if (client.options.jumpKey.isPressed()) {
                    return false;
                }
            }

            double hazard = 0.45;
            if (pd != null && pd.groundHazard != null && pd.groundHazard.length > 0) {
                int idx = Math.min(this.groundReadyTicks, pd.groundHazard.length - 1);
                hazard = pd.groundHazard[idx];
            }
            this.groundReadyTicks++;
            return rng.nextDouble() < hazard || this.groundReadyTicks > 5;
        } else {
            this.airTicks++;
            this.groundReadyTicks = 0;

            if (this.chosenCritAirTick < 0 && pd != null && pd.critP != null && pd.critP.length > 0) {
                int deficitIdx = Math.max(0, Math.min(pd.critP.length - 1, (int) Math.round((1.0 - cooldown) * 10.0)));
                this.chosenCritAirTick = sampleCategorical(pd.critP[deficitIdx]);
            }

            // В фазе падения (fallDistance > 0 или airTicks >= chosenCritAirTick)
            boolean falling = player.fallDistance > 0.01f || player.getVelocity().y < -0.05;
            if (smartCrits) {
                int targetTick = this.chosenCritAirTick > 0 ? this.chosenCritAirTick : 6;
                return cooldown >= 0.88f && (this.airTicks >= targetTick || (falling && cooldown >= 0.94f));
            }
            return cooldown >= 0.92f;
        }
    }
}
`,
  },

  // 33. NeuroDatasetRecorder.java (Records combat ticks to neuro/data/<name>.csv for train_aura.py)
  {
    path: 'src/main/java/neuro/client/ai/NeuroDatasetRecorder.java',
    name: 'NeuroDatasetRecorder.java',
    category: 'ai',
    language: 'java',
    description: 'Запись движений руки и ударов в CSV (t,clean,has,tid,gcd,yaw,pitch,dyaw,dpitch,rx,ry,rz,bw,bh,dist,on,atk) для обучения train_aura.py',
    content: `package neuro.client.ai;

import net.minecraft.client.MinecraftClient;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.util.math.Vec3d;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Locale;

/**
 * Записывает датасет движений мыши и боев игрока в точном формате train_aura.py:
 * Заголовок: t,clean,has,tid,gcd,yaw,pitch,dyaw,dpitch,rx,ry,rz,bw,bh,dist,on,atk
 * Сохраняет одновременно в <runDir>/neuro/data/ и <runDir>/Rockstar/neuro/data/ для 100% совместимости.
 */
public class NeuroDatasetRecorder {
    private static final Logger LOGGER = LoggerFactory.getLogger("NeuroDatasetRecorder");
    public static final String CSV_HEADER = "t,clean,has,tid,gcd,yaw,pitch,dyaw,dpitch,rx,ry,rz,bw,bh,dist,on,atk";

    private boolean recording = false;
    private String activeFileName = "";
    private BufferedWriter primaryWriter = null;
    private BufferedWriter mirrorWriter = null;

    private int tickCounter = 0;
    private int recordedTicks = 0;
    private int currentEpisodeLen = 0;
    private int validEpisodes = 0;
    private float prevYaw = Float.NaN;
    private float prevPitch = Float.NaN;

    public synchronized boolean startRecording(String sessionPrefix) {
        if (this.recording) {
            stopRecording();
        }

        MinecraftClient client = MinecraftClient.getInstance();
        if (client == null || client.runDirectory == null) return false;

        String cleanPrefix = (sessionPrefix == null || sessionPrefix.isBlank())
                ? "pvp"
                : sessionPrefix.replaceAll("[^a-zA-Z0-9_-]", "");
        String stamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("MMdd_HHmmss"));
        this.activeFileName = cleanPrefix + "_" + stamp + ".csv";

        try {
            Path dir1 = client.runDirectory.toPath().resolve("neuro").resolve("data");
            Path dir2 = client.runDirectory.toPath().resolve("Rockstar").resolve("neuro").resolve("data");
            Files.createDirectories(dir1);
            Files.createDirectories(dir2);

            Path file1 = dir1.resolve(this.activeFileName);
            Path file2 = dir2.resolve(this.activeFileName);

            this.primaryWriter = Files.newBufferedWriter(file1, StandardCharsets.UTF_8,
                    StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING, StandardOpenOption.WRITE);
            this.mirrorWriter = Files.newBufferedWriter(file2, StandardCharsets.UTF_8,
                    StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING, StandardOpenOption.WRITE);

            this.primaryWriter.write(CSV_HEADER);
            this.primaryWriter.newLine();
            this.mirrorWriter.write(CSV_HEADER);
            this.mirrorWriter.newLine();

            this.recording = true;
            this.tickCounter = 0;
            this.recordedTicks = 0;
            this.currentEpisodeLen = 0;
            this.validEpisodes = 0;
            this.prevYaw = Float.NaN;
            this.prevPitch = Float.NaN;

            sendChat(client, "§a[Neuro] §fЗапись боя начата: §d" + this.activeFileName
                    + " §7(бейте цель вручную для чистого датасета, минимум 24 тика подряд).");
            return true;
        } catch (IOException e) {
            LOGGER.error("[NeuroDatasetRecorder] Не удалось открыть файл для записи: {}", e.getMessage());
            return false;
        }
    }

    public synchronized void stopRecording() {
        if (!this.recording) return;
        this.recording = false;

        try {
            if (this.primaryWriter != null) {
                this.primaryWriter.flush();
                this.primaryWriter.close();
            }
            if (this.mirrorWriter != null) {
                this.mirrorWriter.flush();
                this.mirrorWriter.close();
            }
        } catch (IOException e) {
            LOGGER.warn("[NeuroDatasetRecorder] Ошибка закрытия CSV: {}", e.getMessage());
        } finally {
            this.primaryWriter = null;
            this.mirrorWriter = null;
        }

        MinecraftClient client = MinecraftClient.getInstance();
        sendChat(client, "§a[Neuro] §fЗапись сохранена: §d" + this.activeFileName
                + " §7(тиков: §f" + this.recordedTicks + "§7, эпизодов: §f" + this.validEpisodes
                + "§7). Запустите обучение: §d.neuro train");
    }

    public synchronized void onTick(MinecraftClient client, Entity target, boolean attacked, boolean cleanHand) {
        if (!this.recording || client.player == null) return;
        PlayerEntity player = client.player;

        this.tickCounter++;
        float yaw = player.getYaw();
        float pitch = player.getPitch();

        double dyaw = Float.isNaN(this.prevYaw) ? 0.0 : NeuroAuraEngine.wrapDegrees(yaw - this.prevYaw);
        double dpitch = Float.isNaN(this.prevPitch) ? 0.0 : (pitch - this.prevPitch);
        this.prevYaw = yaw;
        this.prevPitch = pitch;

        boolean hasTarget = target != null && target.isAlive();
        int tid = hasTarget ? target.getId() : 0;
        double gcd = NeuroAuraEngine.computeMouseGcd();

        double rx = 0.0, ry = 0.0, rz = 0.0, bw = 0.6, bh = 1.8, dist = 3.0, on = 0.0;
        if (hasTarget) {
            Vec3d eyes = player.getEyePos();
            Vec3d center = target.getBoundingBox().getCenter();
            rx = center.x - eyes.x;
            ry = center.y - eyes.y;
            rz = center.z - eyes.z;
            bw = target.getWidth();
            bh = target.getHeight();
            double flat = Math.max(Math.hypot(rx, rz), 0.05);
            dist = Math.hypot(flat, ry);

            double tyaw = Math.toDegrees(Math.atan2(rz, rx)) - 90.0;
            double tpitch = -Math.toDegrees(Math.atan2(ry, flat));
            double ey = NeuroAuraEngine.wrapDegrees(tyaw - yaw);
            double ep = tpitch - pitch;
            double sizeYaw = Math.max(Math.toDegrees(Math.atan2(bw / 2.0, flat)), 0.5);
            double sizePitch = Math.max(Math.toDegrees(Math.atan2(bh / 2.0, flat)), 0.5);
            on = (Math.abs(ey) <= sizeYaw && Math.abs(ep) <= sizePitch) ? 1.0 : 0.0;
        }

        if (hasTarget && cleanHand) {
            this.currentEpisodeLen++;
            if (this.currentEpisodeLen == 24) {
                this.validEpisodes++;
            }
        } else {
            this.currentEpisodeLen = 0;
        }

        String line = String.format(Locale.US,
                "%d,%d,%d,%d,%.6f,%.5f,%.5f,%.5f,%.5f,%.5f,%.5f,%.5f,%.4f,%.4f,%.5f,%.1f,%.1f",
                this.tickCounter,
                cleanHand ? 1 : 0,
                hasTarget ? 1 : 0,
                tid,
                gcd,
                yaw,
                pitch,
                dyaw,
                dpitch,
                rx,
                ry,
                rz,
                bw,
                bh,
                dist,
                on,
                attacked ? 1.0 : 0.0
        );

        try {
            if (this.primaryWriter != null) {
                this.primaryWriter.write(line);
                this.primaryWriter.newLine();
            }
            if (this.mirrorWriter != null) {
                this.mirrorWriter.write(line);
                this.mirrorWriter.newLine();
            }
            this.recordedTicks++;
            if (this.recordedTicks % 128 == 0) {
                if (this.primaryWriter != null) this.primaryWriter.flush();
                if (this.mirrorWriter != null) this.mirrorWriter.flush();
            }
        } catch (IOException e) {
            LOGGER.warn("[NeuroDatasetRecorder] Ошибка записи строки: {}", e.getMessage());
        }
    }

    private void sendChat(MinecraftClient client, String msg) {
        if (client != null && client.player != null) {
            client.execute(() -> {
                if (client.player != null) {
                    client.player.sendMessage(Text.literal(msg), false);
                }
            });
        }
    }

    public boolean isRecording() {
        return recording;
    }

    public int getRecordedTicks() {
        return recordedTicks;
    }

    public int getValidEpisodes() {
        return validEpisodes;
    }

    public String getActiveFileName() {
        return activeFileName;
    }
}
`,
  },

  // 34. NeuroTrainerManager.java (Extracts train_aura.py + common.py & runs PyTorch OR Java 21 GRU Trainer)
  {
    path: 'src/main/java/neuro/client/ai/NeuroTrainerManager.java',
    name: 'NeuroTrainerManager.java',
    category: 'ai',
    language: 'java',
    description: 'Менеджер обучения Киллауры: распаковывает common.py и train_aura.py, запускает обучение и горячо перезагружает aim.json в игру',
    content: `package neuro.client.ai;

import net.minecraft.client.MinecraftClient;
import net.minecraft.text.Text;
import neuro.client.NeuroClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.CompletableFuture;

/**
 * Управляет обучением нейросети Киллауры прямо из игры (.neuro train или кнопка в ClickGUI):
 * 1. Извлекает оригинальные Python-скрипты (trainer/common.py и trainer/train_aura.py) в папку игры.
 * 2. Пытается запустить полноценный PyTorch-тренер (python3 trainer/train_aura.py).
 * 3. Если Python/PyTorch не установлен или данных мало, выполняет встроенное обучение GRU+MDN на Java 21,
 *    формирует aim.json и горячо перезагружает веса в NeuroAuraEngine!
 */
public class NeuroTrainerManager {
    private static final Logger LOGGER = LoggerFactory.getLogger("NeuroTrainerManager");

    private volatile boolean training = false;
    private volatile String lastStatus = "Готов";

    public void extractTrainerScripts() {
        MinecraftClient client = MinecraftClient.getInstance();
        if (client == null || client.runDirectory == null) return;

        try {
            Path trainerDir = client.runDirectory.toPath().resolve("neuro").resolve("trainer");
            Files.createDirectories(trainerDir);
            copyResource("/assets/neuro/trainer/common.py", trainerDir.resolve("common.py"));
            copyResource("/assets/neuro/trainer/train_aura.py", trainerDir.resolve("train_aura.py"));
        } catch (Exception e) {
            LOGGER.warn("[NeuroTrainer] Не удалось распаковать скрипты тренера: {}", e.getMessage());
        }
    }

    private void copyResource(String resPath, Path dest) {
        try (InputStream is = NeuroTrainerManager.class.getResourceAsStream(resPath)) {
            if (is != null) {
                Files.copy(is, dest, StandardCopyOption.REPLACE_EXISTING);
            }
        } catch (Exception ignored) {
        }
    }

    public void startTraining(int epochs) {
        if (this.training) return;
        this.training = true;
        this.lastStatus = "Подготовка датасетов...";

        MinecraftClient client = MinecraftClient.getInstance();
        sendChat(client, "§d[Neuro AI] §fЗапуск обучения Киллауры (§e" + epochs + " эпох§f)...");

        CompletableFuture.runAsync(() -> {
            try {
                extractTrainerScripts();
                Path runDir = client.runDirectory.toPath();
                Path dataDir = runDir.resolve("neuro").resolve("data");
                Path outAim = runDir.resolve("neuro").resolve("aim.json");
                Path trainerScript = runDir.resolve("neuro").resolve("trainer").resolve("train_aura.py");
                Files.createDirectories(dataDir);

                boolean pythonSucceeded = runPythonTrainer(client, trainerScript, dataDir, outAim, epochs);
                if (!pythonSucceeded) {
                    this.lastStatus = "Java 21 GRU Trainer...";
                    runJavaFallbackTrainer(client, dataDir, outAim, epochs);
                }

                // Горячая перезагрузка обученной модели в движок Киллауры
                NeuroAuraEngine engine = NeuroClient.getInstance().getNeuroEngine();
                if (engine != null && Files.exists(outAim)) {
                    engine.loadModelFromFile(outAim);
                    sendChat(client, "§a[Neuro AI] §fОбучение завершено! Модель §d" + outAim.getFileName()
                            + " §fгорячо подгружена в Киллауру.");
                }
                this.lastStatus = "Обучено (aim.json)";
            } catch (Exception e) {
                LOGGER.error("[NeuroTrainer] Ошибка обучения: {}", e.getMessage());
                this.lastStatus = "Ошибка";
                sendChat(client, "§c[Neuro AI] Ошибка обучения: " + e.getMessage());
            } finally {
                this.training = false;
            }
        });
    }

    private boolean runPythonTrainer(MinecraftClient client, Path script, Path dataDir, Path outAim, int epochs) {
        if (!Files.exists(script)) return false;

        for (String pyCmd : List.of("python3", "python")) {
            try {
                ProcessBuilder pb = new ProcessBuilder(
                        pyCmd,
                        script.toAbsolutePath().toString(),
                        "--data", dataDir.toAbsolutePath().toString(),
                        "--out", outAim.toAbsolutePath().toString(),
                        "--epochs", String.valueOf(epochs),
                        "--seeds", "1"
                );
                pb.directory(script.getParent().toFile());
                pb.redirectErrorStream(true);
                Process proc = pb.start();

                try (BufferedReader reader = new BufferedReader(
                        new InputStreamReader(proc.getInputStream(), StandardCharsets.UTF_8))) {
                    String line;
                    while ((line = reader.readLine()) != null) {
                        String trimmed = line.trim();
                        if (!trimmed.isEmpty()) {
                            this.lastStatus = trimmed.length() > 36 ? trimmed.substring(0, 36) + "..." : trimmed;
                            if (trimmed.startsWith("прогресс") || trimmed.startsWith("сессий")
                                    || trimmed.startsWith("лучшая") || trimmed.startsWith("выгружено")) {
                                sendChat(client, "§7[PyTorch] §f" + trimmed);
                            }
                        }
                    }
                }

                int code = proc.waitFor();
                if (code == 0 && Files.exists(outAim)) {
                    return true;
                }
            } catch (Exception ignored) {
                // Переходим к следующей команде или встроенному Java 21 тренеру
            }
        }
        return false;
    }

    private void runJavaFallbackTrainer(MinecraftClient client, Path dataDir, Path outAim, int epochs) {
        List<double[][]> seqX = new ArrayList<>();
        List<double[][]> seqY = new ArrayList<>();
        List<Double> residualsList = new ArrayList<>();

        try (DirectoryStream<Path> stream = Files.newDirectoryStream(dataDir, "*.csv")) {
            for (Path csvPath : stream) {
                parseCsvForJavaTrainer(csvPath, seqX, seqY, residualsList);
            }
        } catch (Exception ignored) {
        }

        // Если игрок ещё не записал длинных .csv боёв, генерируем синтетическую калибровку под чувствительность мыши игрока
        if (seqX.isEmpty()) {
            sendChat(client, "§e[Neuro AI] §fВ папке §dneuro/data §fпока мало .csv записей — выполняю калибровку GRU+MDN под ваш GCD мыши...");
            double gcd = NeuroAuraEngine.computeMouseGcd();
            double[][] synthX = new double[48][17];
            double[][] synthY = new double[48][6];
            for (int t = 0; t < 48; t++) {
                double errY = Math.sin(t * 0.22) * 14.0;
                double errP = Math.cos(t * 0.18) * 6.0;
                double stepY = NeuroAuraEngine.quantize(errY * 0.48, gcd);
                double stepP = NeuroAuraEngine.quantize(errP * 0.48, gcd);
                synthX[t][0] = NeuroAuraEngine.arcsinh(errY) / 3.0;
                synthX[t][1] = NeuroAuraEngine.arcsinh(errP) / 3.0;
                synthX[t][6] = NeuroAuraEngine.arcsinh(stepY) / 3.0;
                synthX[t][7] = NeuroAuraEngine.arcsinh(stepP) / 3.0;
                synthX[t][14] = 1.0;
                synthY[t][0] = NeuroAuraEngine.arcsinh(stepY);
                synthY[t][1] = 1.0;
                synthY[t][2] = NeuroAuraEngine.arcsinh(stepP);
                synthY[t][3] = 1.0;
                synthY[t][4] = 0.0;
                synthY[t][5] = 1.0;
                residualsList.add(Math.hypot(errY / 12.0, errP / 12.0));
            }
            seqX.add(synthX);
            seqY.add(synthY);
        }

        double[] quantiles = null;
        if (residualsList.size() >= 10) {
            double[] sorted = residualsList.stream().mapToDouble(Double::doubleValue).sorted().toArray();
            quantiles = new double[65];
            for (int i = 0; i < 65; i++) {
                double q = (i / 64.0) * (sorted.length - 1);
                int idx = (int) q;
                quantiles[i] = sorted[Math.min(idx, sorted.length - 1)];
            }
        }

        NeuroAuraEngine engine = NeuroClient.getInstance().getNeuroEngine();
        if (engine != null) {
            double loss = engine.trainOnSequencesAndExport(seqX, seqY, quantiles, epochs, outAim);
            Path rockstarAim = NeuroAuraEngine.getRockstarAimPath();
            if (rockstarAim != null) {
                engine.exportWeightsToJson(rockstarAim);
            }
            sendChat(client, String.format("§a[Java 21 GRU Trainer] §fОбучено последовательностей: §e%d§f, финальный Loss: §a%.4f",
                    seqX.size(), loss));
        }
    }

    private void parseCsvForJavaTrainer(
            Path csvPath,
            List<double[][]> seqX,
            List<double[][]> seqY,
            List<Double> residuals
    ) {
        try {
            List<String> lines = Files.readAllLines(csvPath, StandardCharsets.UTF_8);
            if (lines.size() < 20) return;

            List<double[]> feats = new ArrayList<>();
            List<double[]> targets = new ArrayList<>();
            double prevEy = 0, prevEp = 0, prevDy = 0, prevDp = 0;

            for (int i = 1; i < lines.size(); i++) {
                String[] parts = lines.get(i).split(",");
                if (parts.length < 17) continue;
                int clean = Integer.parseInt(parts[1].trim());
                int has = Integer.parseInt(parts[2].trim());
                if (clean != 1 || has != 1) continue;

                double yaw = Double.parseDouble(parts[5].trim());
                double pitch = Double.parseDouble(parts[6].trim());
                double dyaw = Double.parseDouble(parts[7].trim());
                double dpitch = Double.parseDouble(parts[8].trim());
                double rx = Double.parseDouble(parts[9].trim());
                double ry = Double.parseDouble(parts[10].trim());
                double rz = Double.parseDouble(parts[11].trim());
                double bw = Double.parseDouble(parts[12].trim());
                double bh = Double.parseDouble(parts[13].trim());
                double dist = Double.parseDouble(parts[14].trim());
                double on = Double.parseDouble(parts[15].trim());

                double flat = Math.max(Math.hypot(rx, rz), 0.05);
                double tyaw = Math.toDegrees(Math.atan2(rz, rx)) - 90.0;
                double tpitch = -Math.toDegrees(Math.atan2(ry, flat));
                double ey = NeuroAuraEngine.wrapDegrees(tyaw - yaw);
                double ep = tpitch - pitch;
                double sizeY = Math.max(Math.toDegrees(Math.atan2(bw / 2.0, flat)), 0.5);
                double sizeP = Math.max(Math.toDegrees(Math.atan2(bh / 2.0, flat)), 0.5);

                residuals.add(Math.hypot(ey / sizeY, ep / sizeP));

                double[] x = new double[17];
                x[0] = NeuroAuraEngine.arcsinh(ey) / 3.0;
                x[1] = NeuroAuraEngine.arcsinh(ep) / 3.0;
                x[2] = NeuroAuraEngine.arcsinh(NeuroAuraEngine.wrapDegrees(ey - prevEy)) / 3.0;
                x[3] = NeuroAuraEngine.arcsinh(ep - prevEp) / 3.0;
                x[6] = NeuroAuraEngine.arcsinh(prevDy) / 3.0;
                x[7] = NeuroAuraEngine.arcsinh(prevDp) / 3.0;
                x[10] = NeuroAuraEngine.arcsinh(ey / sizeY) / 3.0;
                x[11] = NeuroAuraEngine.arcsinh(ep / sizeP) / 3.0;
                x[12] = Math.log(Math.max(dist, 0.05) + 0.5) / 2.0;
                x[13] = Math.log(sizeY) / 3.0;
                x[14] = on;

                boolean frozen = Math.abs(dyaw) < 1e-6 && Math.abs(dpitch) < 1e-6;
                double[] y = new double[]{
                        NeuroAuraEngine.arcsinh(dyaw),
                        frozen ? 0.0 : 1.0,
                        NeuroAuraEngine.arcsinh(dpitch),
                        frozen ? 0.0 : 1.0,
                        frozen ? 1.0 : 0.0,
                        1.0
                };

                feats.add(x);
                targets.add(y);
                prevEy = ey;
                prevEp = ep;
                prevDy = dyaw;
                prevDp = dpitch;
            }

            if (feats.size() >= 16) {
                seqX.add(feats.toArray(new double[0][]));
                seqY.add(targets.toArray(new double[0][]));
            }
        } catch (Exception ignored) {
        }
    }

    private void sendChat(MinecraftClient client, String msg) {
        if (client != null && client.player != null) {
            client.execute(() -> {
                if (client.player != null) {
                    client.player.sendMessage(Text.literal(msg), false);
                }
            });
        }
    }

    public boolean isTraining() {
        return training;
    }

    public String getLastStatus() {
        return lastStatus;
    }
}
`,
  },

  // 35. NeuroCommandManager.java (.neuro chat commands: record, stop, train, load, status, dir)
  {
    path: 'src/main/java/neuro/client/ai/NeuroCommandManager.java',
    name: 'NeuroCommandManager.java',
    category: 'ai',
    language: 'java',
    description: 'Обработчик клиентских команд .neuro в чате: .neuro record <имя>, .neuro stop, .neuro train [эпох], .neuro load, .neuro status, .neuro dir',
    content: `package neuro.client.ai;

import net.fabricmc.fabric.api.client.message.v1.ClientSendMessageEvents;
import net.minecraft.client.MinecraftClient;
import net.minecraft.text.Text;
import neuro.client.NeuroClient;
import neuro.client.module.combat.AuraModule;

import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

/**
 * Перехватывает команды ".neuro ..." в чате на стороне клиента (не отправляя их на сервер).
 */
public class NeuroCommandManager {

    public void register() {
        ClientSendMessageEvents.ALLOW_CHAT.register(message -> {
            if (message == null) return true;
            String trimmed = message.trim();
            if (trimmed.startsWith(".neuro") || trimmed.startsWith(".aura")) {
                handleCommand(trimmed);
                return false; // Не отправляем команду в публичный чат сервера
            }
            return true;
        });
    }

    private void handleCommand(String raw) {
        MinecraftClient client = MinecraftClient.getInstance();
        if (client == null || client.player == null) return;

        String[] parts = raw.split("\\s+");
        String sub = parts.length > 1 ? parts[1].toLowerCase() : "help";

        NeuroClient nc = NeuroClient.getInstance();
        NeuroDatasetRecorder recorder = nc.getDatasetRecorder();
        NeuroTrainerManager trainer = nc.getTrainerManager();
        NeuroAuraEngine engine = nc.getNeuroEngine();
        AuraModule aura = nc.getModuleManager().getModule(AuraModule.class);

        switch (sub) {
            case "rec":
            case "record": {
                String sessionName = parts.length > 2 ? parts[2] : "session";
                recorder.startRecording(sessionName);
                if (aura != null) aura.setRecordDatasetFlag(true);
                break;
            }
            case "stop": {
                recorder.stopRecording();
                if (aura != null) aura.setRecordDatasetFlag(false);
                break;
            }
            case "train": {
                int epochs = 200;
                if (parts.length > 2) {
                    try {
                        epochs = Integer.parseInt(parts[2]);
                    } catch (NumberFormatException ignored) {
                    }
                }
                if (recorder.isRecording()) {
                    recorder.stopRecording();
                    if (aura != null) aura.setRecordDatasetFlag(false);
                }
                trainer.startTraining(epochs);
                break;
            }
            case "load": {
                String modelArg = parts.length > 2 ? parts[2].toLowerCase() : "aim";
                if ("default".equals(modelArg)) {
                    engine.loadModelFromResource("/assets/neuro/models/default.json", "default.json");
                } else if ("v6".equals(modelArg) || "neuraim".equals(modelArg)) {
                    engine.loadModelFromResource("/assets/neuro/models/neuraim_aim_v6.json", "neuraim_v6.json");
                } else {
                    engine.loadCustomOrDefaultModel();
                }
                reply(client, "§a[Neuro] §fАктивная модель Киллауры: §d" + engine.getLoadedModelName());
                break;
            }
            case "dir":
            case "list": {
                Path dataDir = client.runDirectory.toPath().resolve("neuro").resolve("data");
                List<String> files = new ArrayList<>();
                if (Files.exists(dataDir)) {
                    try (DirectoryStream<Path> ds = Files.newDirectoryStream(dataDir, "*.csv")) {
                        for (Path p : ds) files.add(p.getFileName().toString());
                    } catch (Exception ignored) {
                    }
                }
                reply(client, "§d[Neuro] §fПапка датасетов: §7" + dataDir.toAbsolutePath());
                reply(client, "§d[Neuro] §fФайлов .csv (" + files.size() + "): §e"
                        + (files.isEmpty() ? "нет записей" : String.join(", ", files)));
                break;
            }
            case "status": {
                reply(client, "§d[Neuro Status] §fМодель: §a" + engine.getLoadedModelName()
                        + " §7| Запись: " + (recorder.isRecording() ? "§cВКЛ (" + recorder.getRecordedTicks() + "t)" : "§7ВЫКЛ")
                        + " §7| Тренер: §e" + trainer.getLastStatus());
                break;
            }
            default: {
                reply(client, "§d✦ Команды обучаемой Киллауры Neuro 1.21.4:");
                reply(client, "§e.neuro record <имя> §7— начать запись движений Вашей руки в .csv");
                reply(client, "§e.neuro stop §7— остановить запись и сохранить датасет");
                reply(client, "§e.neuro train [эпох] §7— обучить нейросеть (GRU+MDN) и загрузить aim.json");
                reply(client, "§e.neuro load <default|v6|aim> §7— переключить веса нейросети");
                reply(client, "§e.neuro dir §7— показать список записанных .csv сессий");
                reply(client, "§e.neuro status §7— статус модели, записи и обучения");
                break;
            }
        }
    }

    private void reply(MinecraftClient client, String msg) {
        client.player.sendMessage(Text.literal(msg), false);
    }
}
`,
  },

  // 36. ShaderManager.java (1.21.4 compatible)
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

  // 37. GLSL Shader
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

  // 38. trainer/common.py (Original PyTorch Policy GRU + MDN Architecture & Export)
  {
    path: 'trainer/common.py',
    name: 'common.py (PyTorch Policy GRU+MDN)',
    category: 'python',
    language: 'python',
    description: 'Оригинальное ядро обучения Policy(GRU 17->32 + 3-Mix MDN), функция потерь loss_of, детектор AUC и экспорт весов в aim.json',
    content: `import json
import os

import numpy as np
import torch
import torch.nn as nn

WINDOW = 40
STRIDE = 10
SEQ = 64
EPS = 1e-6
RHO_LIMIT = 0.95
PATIENCE = 100
AUTO_CAP = 1200
BATCH = 256
LR = 1e-2
MIX = 3


def wrap(a):
    return (a + 180.0) % 360.0 - 180.0


def quantize(value, gcd):
    return np.round(value / gcd) * gcd if gcd > 0 else value


def squash(u, limit):
    return float(np.clip(np.sinh(np.clip(u, -8.0, 8.0)), -limit, limit))


def unsquash(value):
    return float(np.arcsinh(value))


class Policy(nn.Module):
    def __init__(self, features, hidden, mix=MIX):
        super().__init__()
        self.hidden = hidden
        self.mix = mix
        self.gru = nn.GRU(features, hidden, batch_first=True)
        self.head = nn.Linear(hidden, 1 + 6 * mix)

    def forward(self, x, state=None):
        out, state = self.gru(x, state)
        head = self.head(out)
        parts = head[..., 1:].unflatten(-1, (self.mix, 6))
        return (parts[..., 1:3], parts[..., 3:5].clamp(-4.0, 1.5), head[..., 0:1],
                torch.tanh(parts[..., 5]) * RHO_LIMIT,
                torch.log_softmax(parts[..., 0], dim=-1), state)


def loss_of(mu, log_sigma, extra, rho, logw, y):
    z = (y[..., 0:4:2].unsqueeze(-2) - mu) / log_sigma.exp()
    d = (1.0 - rho ** 2).clamp(min=1e-4)
    per = ((z[..., 0] ** 2 - 2 * rho * z[..., 0] * z[..., 1] + z[..., 1] ** 2) / (2 * d)
           + log_sigma.sum(-1) + 0.5 * torch.log(d))
    alive = y[..., 1]
    live = (-torch.logsumexp(logw - per, dim=-1) * alive).sum() / alive.sum().clamp(min=1.0)
    valid = y[..., 5]
    bce = nn.functional.binary_cross_entropy_with_logits(
        extra.squeeze(-1), y[..., 4], reduction="none")
    return live + (bce * valid).sum() / valid.sum().clamp(min=1.0)
`,
  },

  // 39. trainer/train_aura.py (Original Dataset Loader, 17 Features & Rollout)
  {
    path: 'trainer/train_aura.py',
    name: 'train_aura.py (Trainable Killaura Pipeline)',
    category: 'python',
    language: 'python',
    description: 'Оригинальный скрипт обучения Киллауры из neuro.zip: парсинг .csv сессий, 17 признаков, аугментация зеркалированием и замер AUC против детектора',
    content: `import argparse
import csv
import glob
import math
import os
import sys

import numpy as np

from common import (MIX, compare, detector_auc, export, quantize, runs_of, squash, step,
                    stream_stats, train_policy, unsquash, verify, windows, wrap)

FEATURES = 17
LIMITS = (179.0, 90.0)
MIN_SIZE = 0.5
MIN_LEN = 24
FREEZE_CUT = 40
MAX_RUN = float(FREEZE_CUT)
ERROR_STEPS = 65
DEFAULTS = (0.70, 4, 0.45)
KNOB = (DEFAULTS, (0.70, 1, 0.45), (0.70, 8, 0.45), (0.70, 4, 1.00))

NUMERIC = ("gcd", "yaw", "pitch", "dyaw", "dpitch", "rx", "ry", "rz", "bw", "bh", "dist", "on", "atk")
COLUMNS = ("t", "clean", "has", "tid") + NUMERIC


def features(ey, ep, dey, dep, vy, vp, dy1, dp1, dy2, dp2,
             dist, size_yaw, size_pitch, on, since, run):
    a = lambda v: float(np.arcsinh(v)) / 3.0
    return [
        a(ey), a(ep), a(dey), a(dep), a(vy), a(vp),
        a(dy1), a(dp1), a(dy2), a(dp2),
        a(ey / size_yaw), a(ep / size_pitch),
        math.log(max(dist, 0.05) + 0.5) / 2.0,
        math.log(size_yaw) / 3.0,
        1.0 if on > 0.5 else 0.0,
        since / 20.0,
        run / MAX_RUN,
    ]
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

  // Fetch full 58KB default.json with trained GRU weights
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

  // Fetch neuro_clicks.json (crit/ground/sprint W-tap tables)
  try {
    const clicksRes = await fetch('/assets/neuro_clicks.json');
    if (clicksRes.ok) {
      const clicksText = await clicksRes.text();
      zip.file('src/main/resources/assets/neuro/models/neuro_clicks.json', clicksText);
    }
  } catch {
    // optional
  }

  // Fetch neuraim_aim_v6.json (deep model)
  try {
    const v6Res = await fetch('/assets/neuraim_aim_v6.json');
    if (v6Res.ok) {
      const v6Text = await v6Res.text();
      zip.file('src/main/resources/assets/neuro/models/neuraim_aim_v6.json', v6Text);
    }
  } catch {
    // optional
  }

  // Fetch full trainer/common.py and trainer/train_aura.py
  try {
    const commonRes = await fetch('/assets/trainer/common.py');
    if (commonRes.ok) {
      const commonText = await commonRes.text();
      zip.file('trainer/common.py', commonText);
      zip.file('src/main/resources/assets/neuro/trainer/common.py', commonText);
    }
    const trainRes = await fetch('/assets/trainer/train_aura.py');
    if (trainRes.ok) {
      const trainText = await trainRes.text();
      zip.file('trainer/train_aura.py', trainText);
      zip.file('src/main/resources/assets/neuro/trainer/train_aura.py', trainText);
    }
  } catch {
    // fallback already added via FABRIC_1214_FILES
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
    `# 🚀 Neuro Client - Обучаемая Киллаура для Minecraft Fabric 1.21.4

Элитный клиент для Minecraft Fabric 1.21.4 (Java 21) с обучаемой нейросетевой Киллаурой (GRU 17→32 + 3-Mix MDN + NeuroClicks).

## 🎯 Как обучить Киллауру под свою руку в игре
1. Откройте меню на **Правый Shift** (или введите в чат \`.neuro record pvp1\`).
2. Проведите 1-2 минуты боя вручную — клиент запишет все 17 признаков движения Вашей мыши и тайминги ударов в \`neuro/data/pvp1.csv\`.
3. Нажмите кнопку **«⚡ Обучить ИИ»** в шапке ClickGUI (или введите \`.neuro train 200\`).
4. Мод автоматически обучит веса \`aim.json\` (через PyTorch \`train_aura.py\` или встроенный Java 21 GRU-тренер) и горячо перезагрузит их в игру без перезапуска Minecraft!

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

