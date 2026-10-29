package neuro.client;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.util.InputUtil;
import neuro.client.ai.NeuroAuraEngine;
import neuro.client.gui.ModernClickGuiScreen;
import neuro.client.module.ModuleManager;
import neuro.client.render.shader.ShaderManager;
import org.lwjgl.glfw.GLFW;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Главный клиентский инициализатор для Minecraft Fabric 1.21.4.
 * Разработано с соблюдением стандартов чистого кода, современного API 1.21.4
 * и архитектуры компонентов.
 */
@Environment(EnvType.CLIENT)
public final class NeuroClient implements ClientModInitializer {
    public static final String MOD_ID = "neuro";
    public static final String MOD_NAME = "Neuro Client";
    public static final String MOD_VERSION = "1.0.0 (1.21.4)";
    public static final Logger LOGGER = LoggerFactory.getLogger(MOD_NAME);

    private static NeuroClient instance;

    // Клавиша открытия ClickGUI - Правый Shift (GLFW_KEY_RIGHT_SHIFT)
    private KeyBinding clickGuiKeyBinding;
    private ModuleManager moduleManager;
    private ShaderManager shaderManager;
    private NeuroAuraEngine neuroEngine;

    @Override
    public void onInitializeClient() {
        instance = this;
        LOGGER.info("[Neuro] Инициализация мода под Fabric 1.21.4 (Java 21)...");

        // Регистрация клавиши открытия меню: ПРАВЫЙ ШИФТ (Right Shift)
        this.clickGuiKeyBinding = KeyBindingHelper.registerKeyBinding(new KeyBinding(
                "key.neuro.open_clickgui",
                InputUtil.Type.KEYSYM,
                GLFW.GLFW_KEY_RIGHT_SHIFT, // Правый Shift
                "category.neuro.general"
        ));

        // Инициализация сервисов и модулей
        this.moduleManager = new ModuleManager();
        this.shaderManager = new ShaderManager();
        this.neuroEngine = new NeuroAuraEngine();

        // Загрузка весов обученной GRU нейросети из assets/neuro/models/default.json
        this.neuroEngine.loadModelFromResources();

        // Тик-листенер для перехвата нажатия Правого Шифта
        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            if (client.player == null) return;

            while (this.clickGuiKeyBinding.wasPressed()) {
                if (client.currentScreen == null) {
                    client.setScreen(new ModernClickGuiScreen());
                }
            }

            // Обновление состояния модулей каждый тик
            this.moduleManager.onTick(client);
        });

        LOGGER.info("[Neuro] Успешно инициализирован! Нажмите [Правый Shift] в игре для открытия меню.");
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
