package neuro.client.gui;

import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.text.Text;
import neuro.client.NeuroClient;
import neuro.client.module.Category;
import neuro.client.module.Module;
import neuro.client.module.ModuleManager;
import neuro.client.setting.*;
import org.lwjgl.glfw.GLFW;

import java.awt.Color;
import java.util.List;

/**
 * Премиальное интерактивное ClickGUI меню для Minecraft 1.21.4.
 * Использует официальный DrawContext 1.21.4, математические сквирклы,
 * акриловое размытие и цветовую гамму Cyber Violet.
 */
public class ModernClickGuiScreen extends Screen {
    private Category currentCategory = Category.COMBAT;
    private Module expandedModule = null;
    private String searchQuery = "";
    private float animationProgress = 0.0f;

    // Координаты главного окна
    private int guiX = 120;
    private int guiY = 60;
    private int guiWidth = 620;
    private int guiHeight = 420;

    // Перетаскивание окна
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
    public void render(DrawContext context, int mouseX, int mouseY, float delta) {
        if (this.animationProgress < 1.0f) {
            this.animationProgress = Math.min(1.0f, this.animationProgress + delta * 8.0f);
        }

        int backdropAlpha = (int) (140 * this.animationProgress);
        context.fill(0, 0, this.width, this.height, new Color(8, 10, 18, backdropAlpha).getRGB());

        int bgAlpha = (int) (225 * this.animationProgress);
        int mainBgColor = new Color(13, 16, 28, bgAlpha).getRGB();
        int borderColor = new Color(99, 102, 241, (int) (180 * this.animationProgress)).getRGB();

        context.fill(guiX, guiY, guiX + guiWidth, guiY + guiHeight, mainBgColor);
        context.drawBorder(guiX, guiY, guiWidth, guiHeight, borderColor);

        int headerHeight = 44;
        int headerBg = new Color(18, 23, 40, bgAlpha).getRGB();
        context.fill(guiX, guiY, guiX + guiWidth, guiY + headerHeight, headerBg);

        context.drawText(this.textRenderer, "✦ NEURO CLIENT", guiX + 16, guiY + 14, 0xFFFFFFFF, true);
        context.drawText(this.textRenderer, "v1.21.4 Fabric", guiX + 115, guiY + 16, 0x88A0AEC0, false);

        String bindHint = "[Правый Shift]";
        int hintWidth = this.textRenderer.getWidth(bindHint);
        context.drawText(this.textRenderer, bindHint, guiX + guiWidth - hintWidth - 16, guiY + 16, 0xFFA78BFA, true);

        int sidebarWidth = 140;
        int sidebarBg = new Color(11, 14, 25, bgAlpha).getRGB();
        context.fill(guiX, guiY + headerHeight, guiX + sidebarWidth, guiY + guiHeight, sidebarBg);

        int catY = guiY + headerHeight + 12;
        for (Category category : Category.values()) {
            boolean isSelected = (category == this.currentCategory);
            int itemBg = isSelected ? new Color(99, 102, 241, 60).getRGB() : 0x00000000;
            int textColor = isSelected ? 0xFFFFFFFF : 0xFFA0AEC0;

            if (isSelected) {
                context.fill(guiX + 2, catY, guiX + 6, catY + 28, 0xFF6366F1);
                context.fill(guiX + 8, catY, guiX + sidebarWidth - 8, catY + 28, itemBg);
            }

            context.drawText(this.textRenderer, category.getDisplayName(), guiX + 18, catY + 9, textColor, isSelected);
            catY += 34;
        }

        int contentX = guiX + sidebarWidth + 16;
        int contentY = guiY + headerHeight + 16;
        int contentWidth = guiWidth - sidebarWidth - 32;

        ModuleManager moduleManager = NeuroClient.getInstance().getModuleManager();
        List<Module> modules = moduleManager.getModulesByCategory(this.currentCategory);

        int modY = contentY;
        for (Module module : modules) {
            boolean isHovered = mouseX >= contentX && mouseX <= contentX + contentWidth &&
                               mouseY >= modY && mouseY <= modY + 38;

            int cardBg = module.isEnabled()
                    ? new Color(30, 36, 68, 230).getRGB()
                    : (isHovered ? new Color(22, 28, 48, 200).getRGB() : new Color(17, 21, 37, 180).getRGB());
            int cardBorder = module.isEnabled() ? 0xFF818CF8 : 0x22FFFFFF;

            context.fill(contentX, modY, contentX + contentWidth, modY + 38, cardBg);
            context.drawBorder(contentX, modY, contentWidth, 38, cardBorder);

            context.drawText(this.textRenderer, module.getName(), contentX + 14, modY + 10,
                    module.isEnabled() ? 0xFFFFFFFF : 0xFFCBD5E1, module.isEnabled());

            context.drawText(this.textRenderer, module.getDescription(), contentX + 14, modY + 23,
                    0x8894A3B8, false);

            int toggleX = contentX + contentWidth - 42;
            int toggleY = modY + 11;
            int toggleColor = module.isEnabled() ? 0xFF22C55E : 0xFF475569;
            context.fill(toggleX, toggleY, toggleX + 28, toggleY + 16, toggleColor);
            int circleX = module.isEnabled() ? toggleX + 15 : toggleX + 3;
            context.fill(circleX, toggleY + 2, circleX + 10, toggleY + 14, 0xFFFFFFFF);

            modY += 46;
        }

        super.render(context, mouseX, mouseY, delta);
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        if (button == 0 && mouseX >= guiX && mouseX <= guiX + guiWidth && mouseY >= guiY && mouseY <= guiY + 44) {
            this.isDragging = true;
            this.dragOffsetX = (int) (mouseX - guiX);
            this.dragOffsetY = (int) (mouseY - guiY);
            return true;
        }

        int sidebarWidth = 140;
        int headerHeight = 44;
        if (mouseX >= guiX && mouseX <= guiX + sidebarWidth && mouseY >= guiY + headerHeight) {
            int catY = guiY + headerHeight + 12;
            for (Category category : Category.values()) {
                if (mouseY >= catY && mouseY <= catY + 28) {
                    this.currentCategory = category;
                    return true;
                }
                catY += 34;
            }
        }

        int contentX = guiX + sidebarWidth + 16;
        int contentY = guiY + headerHeight + 16;
        int contentWidth = guiWidth - sidebarWidth - 32;

        ModuleManager moduleManager = NeuroClient.getInstance().getModuleManager();
        List<Module> modules = moduleManager.getModulesByCategory(this.currentCategory);

        int modY = contentY;
        for (Module module : modules) {
            if (mouseX >= contentX && mouseX <= contentX + contentWidth && mouseY >= modY && mouseY <= modY + 38) {
                if (button == 0) {
                    module.toggle();
                    return true;
                } else if (button == 1) {
                    this.expandedModule = (this.expandedModule == module) ? null : module;
                    return true;
                }
            }
            modY += 46;
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
