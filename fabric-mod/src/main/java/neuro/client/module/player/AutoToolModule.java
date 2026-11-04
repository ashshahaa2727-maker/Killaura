package neuro.client.module.player;

import net.minecraft.block.BlockState;
import net.minecraft.client.MinecraftClient;
import net.minecraft.item.ItemStack;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.HitResult;
import neuro.client.module.Category;
import neuro.client.module.Module;
import neuro.client.setting.BooleanSetting;

public class AutoToolModule extends Module {
    private final BooleanSetting swapBack = new BooleanSetting("Swap Back", true);

    public AutoToolModule() {
        super("AutoTool", "Авто-выбор самого быстрого инструмента под блок", Category.PLAYER);
        this.setEnabled(true);
        this.addSettings(swapBack);
    }

    @Override
    public void onTick(MinecraftClient client) {
        if (client.player == null || client.world == null) return;
        if (!client.options.attackKey.isPressed()) return;
        if (!(client.crosshairTarget instanceof BlockHitResult bhr) || bhr.getType() != HitResult.Type.BLOCK) return;

        BlockState state = client.world.getBlockState(bhr.getBlockPos());
        float bestSpeed = 1.0f;
        int bestSlot = -1;
        for (int i = 0; i < 9; i++) {
            ItemStack stack = client.player.getInventory().getStack(i);
            float spd = stack.getMiningSpeedMultiplier(state);
            if (spd > bestSpeed) {
                bestSpeed = spd;
                bestSlot = i;
            }
        }
        if (bestSlot != -1) {
            client.player.getInventory().selectedSlot = bestSlot;
        }
    }
}
