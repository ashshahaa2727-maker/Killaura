package neuro.client.module.player;

import net.minecraft.client.MinecraftClient;
import net.minecraft.item.Items;
import net.minecraft.util.Hand;
import neuro.client.module.Category;
import neuro.client.module.Module;
import neuro.client.setting.BooleanSetting;
import org.lwjgl.glfw.GLFW;

public class MiddleClickPearlModule extends Module {
    private final BooleanSetting fromInventory = new BooleanSetting("From Inventory", true);
    private boolean wasPressed = false;

    public MiddleClickPearlModule() {
        super("MiddleClickPearl", "Бросок Эндер-пёрла по нажатию колёсика мыши", Category.PLAYER);
        this.setEnabled(true);
        this.addSettings(fromInventory);
    }

    @Override
    public void onTick(MinecraftClient client) {
        if (client.player == null || client.interactionManager == null || client.currentScreen != null) return;
        boolean pressed = GLFW.glfwGetMouseButton(client.getWindow().getHandle(), GLFW.GLFW_MOUSE_BUTTON_MIDDLE) == GLFW.GLFW_PRESS;
        if (pressed && !wasPressed) {
            for (int i = 0; i < 9; i++) {
                if (client.player.getInventory().getStack(i).isOf(Items.ENDER_PEARL)) {
                    int prev = client.player.getInventory().selectedSlot;
                    client.player.getInventory().selectedSlot = i;
                    client.interactionManager.interactItem(client.player, Hand.MAIN_HAND);
                    client.player.getInventory().selectedSlot = prev;
                    break;
                }
            }
        }
        wasPressed = pressed;
    }
}
