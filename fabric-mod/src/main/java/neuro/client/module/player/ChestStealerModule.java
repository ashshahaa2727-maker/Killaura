package neuro.client.module.player;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.ingame.GenericContainerScreen;
import net.minecraft.screen.GenericContainerScreenHandler;
import net.minecraft.screen.slot.SlotActionType;
import neuro.client.module.Category;
import neuro.client.module.Module;
import neuro.client.setting.BooleanSetting;
import neuro.client.setting.SliderSetting;

public class ChestStealerModule extends Module {
    private final SliderSetting delay = new SliderSetting("Steal Delay (ms)", 45.0, 0.0, 200.0, 5.0);
    private final BooleanSetting autoClose = new BooleanSetting("Auto Close", true);
    private long lastSteal = 0L;

    public ChestStealerModule() {
        super("ChestStealer", "Авто-лут сундуков и аирдропов (Мистиков)", Category.PLAYER);
        this.addSettings(delay, autoClose);
    }

    @Override
    public void onTick(MinecraftClient client) {
        if (client.player == null || client.interactionManager == null) return;
        if (!(client.currentScreen instanceof GenericContainerScreen screen)) return;
        if (System.currentTimeMillis() - this.lastSteal < delay.getValue()) return;

        GenericContainerScreenHandler handler = screen.getScreenHandler();
        int rows = handler.getRows() * 9;
        for (int i = 0; i < rows; i++) {
            if (!handler.getSlot(i).getStack().isEmpty()) {
                client.interactionManager.clickSlot(handler.syncId, i, 0, SlotActionType.QUICK_MOVE, client.player);
                this.lastSteal = System.currentTimeMillis();
                return;
            }
        }
        if (autoClose.getValue()) {
            client.player.closeHandledScreen();
        }
    }
}
