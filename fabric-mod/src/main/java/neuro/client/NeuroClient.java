package neuro.client;

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
import neuro.client.module.combat.FakePlayerModule;
import neuro.client.module.render.HudModule;
import neuro.client.render.shader.ShaderManager;
import org.lwjgl.glfw.GLFW;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Comparator;
import java.util.List;
import java.util.concurrent.CompletableFuture;

@Environment(EnvType.CLIENT)
@SuppressWarnings("deprecation")
public final class NeuroClient implements ClientModInitializer {
    public static final String MOD_ID = "neuro";
    public static final String MOD_NAME = "Neuro Client";
    public static final String MOD_VERSION = "1.0.0 (1.21.4)";
    public static final Logger LOGGER = LoggerFactory.getLogger(MOD_NAME);

    private static NeuroClient instance;

    private KeyBinding clickGuiKeyBinding;
    private KeyBinding fakePlayerKeyBinding;
    private ModuleManager moduleManager;
    private ShaderManager shaderManager;
    private NeuroAuraEngine neuroEngine;
    private NeuroClicks neuroClicks;
    private NeuroDatasetRecorder datasetRecorder;
    private NeuroTrainerManager trainerManager;
    private NeuroCommandManager commandManager;

    /**
     * Быстрый побитовый ARGB без использования java.awt.Color
     * (исключает зависание AWT Toolkit на логотипе Mojang Studios на телефонах PojavLauncher/Zalith и ПК).
     */
    public static int argb(int a, int r, int g, int b) {
        return ((a & 0xFF) << 24) | ((r & 0xFF) << 16) | ((g & 0xFF) << 8) | (b & 0xFF);
    }

    @Override
    public void onInitializeClient() {
        instance = this;
        LOGGER.info("[Neuro] Быстрая неблокирующая инициализация клиента под Fabric 1.21.4 (Java 21)...");

        this.clickGuiKeyBinding = KeyBindingHelper.registerKeyBinding(new KeyBinding(
                "key.neuro.open_clickgui",
                InputUtil.Type.KEYSYM,
                GLFW.GLFW_KEY_RIGHT_SHIFT,
                "category.neuro.general"
        ));

        this.fakePlayerKeyBinding = KeyBindingHelper.registerKeyBinding(new KeyBinding(
                "key.neuro.fake_player",
                InputUtil.Type.KEYSYM,
                GLFW.GLFW_KEY_P,
                "category.neuro.general"
        ));

        this.neuroEngine = new NeuroAuraEngine();
        this.neuroClicks = new NeuroClicks();
        this.datasetRecorder = new NeuroDatasetRecorder();
        this.trainerManager = new NeuroTrainerManager();
        this.commandManager = new NeuroCommandManager();
        this.moduleManager = new ModuleManager();
        this.shaderManager = new ShaderManager();

        this.commandManager.register();
        this.shaderManager.initShaders();

        // Асинхронная загрузка нейросетевых матриц, чтобы не задерживать экран загрузки Mojang Studios
        CompletableFuture.runAsync(() -> {
            try {
                this.neuroEngine.loadModelFromResources();
                this.neuroClicks.loadFromResources();
                this.trainerManager.extractTrainerScripts();
            } catch (Throwable t) {
                LOGGER.warn("[Neuro] Фоновая инициализация ресурсов: {}", t.getMessage());
            }
        });

        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            if (client.player == null) return;

            while (this.clickGuiKeyBinding.wasPressed()) {
                if (client.currentScreen == null) {
                    client.setScreen(new ModernClickGuiScreen());
                } else if (client.currentScreen instanceof ModernClickGuiScreen) {
                    client.setScreen(null);
                }
            }

            while (this.fakePlayerKeyBinding.wasPressed()) {
                FakePlayerModule fp = this.moduleManager.getModule(FakePlayerModule.class);
                if (fp != null) {
                    fp.toggle();
                }
            }

            this.moduleManager.onTick(client);
        });

        HudRenderCallback.EVENT.register((drawContext, tickCounter) -> {
            MinecraftClient client = MinecraftClient.getInstance();
            if (client.player == null || client.options.hudHidden) return;
            renderHud(client, drawContext);
        });

        LOGGER.info("[Neuro] Загружен 51 модуль! [Правый Shift / .gui] — меню | [P / .fp] — Фейк-Плеер | [.neuro help] — обучение ИИ.");
    }

    private void renderHud(MinecraftClient client, DrawContext context) {
        HudModule hud = this.moduleManager.getModule(HudModule.class);
        boolean showHud = hud == null || hud.isEnabled();

        if (showHud && (hud == null || hud.isShowWatermark())) {
            String modelTag = this.neuroEngine != null ? this.neuroEngine.getLoadedModelName() : "default.json";
            FakePlayerModule fp = this.moduleManager.getModule(FakePlayerModule.class);
            String fpTag = (fp != null && fp.isEnabled()) ? " | FP: ON" : "";
            String watermark = "✦ NEURO 1.21.4 | " + client.getCurrentFps() + " FPS | AI: " + modelTag + fpTag;
            int wmWidth = client.textRenderer.getWidth(watermark) + 14;
            context.fill(8, 8, 8 + wmWidth, 26, argb(225, 13, 17, 30));
            context.drawBorder(8, 8, wmWidth, 18, 0xFF8B5CF6);
            context.drawText(client.textRenderer, watermark, 15, 13, 0xFFFFFFFF, true);
        }

        int statusY = 30;
        if (this.datasetRecorder != null && this.datasetRecorder.isRecording()) {
            String recText = "● REC [" + this.datasetRecorder.getActiveFileName() + "]: "
                    + this.datasetRecorder.getRecordedTicks() + " тиков (эпизодов: "
                    + this.datasetRecorder.getValidEpisodes() + ")";
            int recW = client.textRenderer.getWidth(recText) + 14;
            context.fill(8, statusY, 8 + recW, statusY + 18, argb(230, 45, 12, 22));
            context.drawBorder(8, statusY, recW, 18, 0xFFEF4444);
            context.drawText(client.textRenderer, recText, 15, statusY + 5, 0xFFFCA5A5, true);
            statusY += 22;
        }

        if (this.trainerManager != null && this.trainerManager.isTraining()) {
            String trText = "⚡ TRAIN: " + this.trainerManager.getLastStatus();
            int trW = client.textRenderer.getWidth(trText) + 14;
            context.fill(8, statusY, 8 + trW, statusY + 18, argb(230, 14, 36, 28));
            context.drawBorder(8, statusY, trW, 18, 0xFF10B981);
            context.drawText(client.textRenderer, trText, 15, statusY + 5, 0xFF6EE7B7, true);
            statusY += 22;
        }

        int screenWidth = client.getWindow().getScaledWidth();
        int screenHeight = client.getWindow().getScaledHeight();

        // Coords & BPS in bottom-left
        if (showHud && (hud == null || hud.isShowCoords())) {
            double dx = client.player.getX() - client.player.prevX;
            double dz = client.player.getZ() - client.player.prevZ;
            double bps = Math.hypot(dx, dz) * 20.0;
            String coords = String.format("XYZ: %.0f, %.0f, %.0f | %.1f BPS",
                    client.player.getX(), client.player.getY(), client.player.getZ(), bps);
            int cw = client.textRenderer.getWidth(coords) + 12;
            context.fill(8, screenHeight - 24, 8 + cw, screenHeight - 8, argb(210, 13, 17, 30));
            context.drawBorder(8, screenHeight - 24, cw, 16, 0xFF6366F1);
            context.drawText(client.textRenderer, coords, 14, screenHeight - 20, 0xFFCBD5E1, true);
        }

        // Sorted ArrayList in top-right
        if (showHud && (hud == null || hud.isShowArrayList())) {
            List<Module> enabled = this.moduleManager.getEnabledModules();
            enabled.sort(Comparator.comparingInt((Module m) -> client.textRenderer.getWidth(m.getName())).reversed());
            int yOffset = 8;
            for (Module mod : enabled) {
                String name = mod.getName();
                int tw = client.textRenderer.getWidth(name) + 12;
                int x = screenWidth - tw - 8;
                context.fill(x, yOffset, x + tw, yOffset + 15, argb(210, 13, 17, 30));
                context.fill(x + tw - 2, yOffset, x + tw, yOffset + 15, 0xFF8B5CF6);
                context.drawText(client.textRenderer, name, x + 5, yOffset + 4, 0xFFE2E8F0, true);
                yOffset += 16;
            }
        }

        // TargetHUD (показывает цель Aura ИЛИ активного FakePlayer)
        if (showHud && (hud == null || hud.isShowTargetHud())) {
            AuraModule aura = this.moduleManager.getModule(AuraModule.class);
            FakePlayerModule fpMod = this.moduleManager.getModule(FakePlayerModule.class);
            LivingEntity target = (aura != null && aura.isEnabled() && aura.getTargetEntity() != null)
                    ? aura.getTargetEntity()
                    : (fpMod != null && fpMod.isEnabled() ? fpMod.getFakePlayer() : null);

            if (target != null && target.isAlive()) {
                int thX = screenWidth / 2 + 18;
                int thY = screenHeight / 2 + 14;
                int thW = 168;
                int thH = 48;
                boolean hurt = target.hurtTime > 0;
                context.fill(thX, thY, thX + thW, thY + thH, argb(230, hurt ? 38 : 13, 17, 30));
                context.drawBorder(thX, thY, thW, thH, hurt ? 0xFFEF4444 : 0xFF8B5CF6);

                String tName = target == (fpMod != null ? fpMod.getFakePlayer() : null)
                        ? "NeuroBot [FakePlayer]"
                        : target.getName().getString();
                float hp = Math.max(0.0f, target.getHealth());
                float abs = Math.max(0.0f, target.getAbsorptionAmount());
                float maxHp = Math.max(1.0f, target.getMaxHealth());

                context.drawText(client.textRenderer,
                        tName + " (" + String.format("%.1f", hp + abs) + "❤)",
                        thX + 8, thY + 6, 0xFFFFFFFF, true);

                String prof = aura != null ? aura.getClicksProfile() : "strong";
                String subInfo = String.format("Dist: %.1fm | Hurt: %d | %s",
                        client.player.distanceTo(target), target.hurtTime, prof);
                context.drawText(client.textRenderer, subInfo, thX + 8, thY + 19, 0xFFA78BFA, false);

                int barW = (int) ((thW - 16) * Math.min(1.0f, hp / maxHp));
                context.fill(thX + 8, thY + 34, thX + thW - 8, thY + 41, 0xFF1E293B);
                context.fill(thX + 8, thY + 34, thX + 8 + barW, thY + 41, hurt ? 0xFFEF4444 : 0xFF8B5CF6);
            }
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
