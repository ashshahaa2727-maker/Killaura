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
import neuro.client.render.shader.ShaderManager;
import org.lwjgl.glfw.GLFW;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

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

            this.moduleManager.onTick(client);
        });

        HudRenderCallback.EVENT.register((drawContext, tickCounter) -> {
            MinecraftClient client = MinecraftClient.getInstance();
            if (client.player == null || client.options.hudHidden) return;
            renderHud(client, drawContext);
        });

        LOGGER.info("[Neuro] Готово! [Правый Shift] или [.gui] в чате — меню | [.neuro help] — обучение Киллауры.");
    }

    private void renderHud(MinecraftClient client, DrawContext context) {
        String modelTag = this.neuroEngine != null ? this.neuroEngine.getLoadedModelName() : "default.json";
        String watermark = "✦ NEURO 1.21.4 | " + client.getCurrentFps() + " FPS | AI: " + modelTag;
        int wmWidth = client.textRenderer.getWidth(watermark) + 14;
        context.fill(8, 8, 8 + wmWidth, 26, argb(220, 13, 17, 30));
        context.drawBorder(8, 8, wmWidth, 18, 0xFF8B5CF6);
        context.drawText(client.textRenderer, watermark, 15, 13, 0xFFFFFFFF, true);

        int statusY = 30;
        if (this.datasetRecorder != null && this.datasetRecorder.isRecording()) {
            String recText = "● REC [" + this.datasetRecorder.getActiveFileName() + "]: "
                    + this.datasetRecorder.getRecordedTicks() + " тиков (эпизодов: "
                    + this.datasetRecorder.getValidEpisodes() + ")";
            int recW = client.textRenderer.getWidth(recText) + 14;
            context.fill(8, statusY, 8 + recW, statusY + 18, argb(225, 45, 12, 22));
            context.drawBorder(8, statusY, recW, 18, 0xFFEF4444);
            context.drawText(client.textRenderer, recText, 15, statusY + 5, 0xFFFCA5A5, true);
            statusY += 22;
        }

        if (this.trainerManager != null && this.trainerManager.isTraining()) {
            String trText = "⚡ TRAIN: " + this.trainerManager.getLastStatus();
            int trW = client.textRenderer.getWidth(trText) + 14;
            context.fill(8, statusY, 8 + trW, statusY + 18, argb(225, 14, 36, 28));
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
            context.fill(x, yOffset, x + tw, yOffset + 15, argb(205, 13, 17, 30));
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
            context.fill(thX, thY, thX + thW, thY + thH, argb(225, 13, 17, 30));
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
