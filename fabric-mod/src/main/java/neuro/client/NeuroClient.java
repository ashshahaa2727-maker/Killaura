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
