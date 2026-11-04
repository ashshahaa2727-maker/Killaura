package neuro.client.module.player;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.ChatScreen;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.util.InputUtil;
import neuro.client.module.Category;
import neuro.client.module.Module;
import neuro.client.setting.BooleanSetting;

public class InvMoveModule extends Module {
    private final BooleanSetting sprint = new BooleanSetting("Allow Sprint", true);
    private final BooleanSetting jump = new BooleanSetting("Allow Jump", true);

    public InvMoveModule() {
        super("InvMove", "Ходьба и прыжки при открытом инвентаре и меню", Category.PLAYER);
        this.setEnabled(true);
        this.addSettings(sprint, jump);
    }

    @Override
    public void onTick(MinecraftClient client) {
        if (client.player == null || client.currentScreen == null || client.currentScreen instanceof ChatScreen) return;
        long handle = client.getWindow().getHandle();
        updateKey(handle, client.options.forwardKey);
        updateKey(handle, client.options.backKey);
        updateKey(handle, client.options.leftKey);
        updateKey(handle, client.options.rightKey);
        if (jump.getValue()) updateKey(handle, client.options.jumpKey);
        if (sprint.getValue()) updateKey(handle, client.options.sprintKey);
    }

    private void updateKey(long window, KeyBinding key) {
        int code = InputUtil.fromTranslationKey(key.getBoundKeyTranslationKey()).getCode();
        if (code > 0) {
            key.setPressed(InputUtil.isKeyPressed(window, code));
        }
    }
}
