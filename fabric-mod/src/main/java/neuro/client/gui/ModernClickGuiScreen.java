package neuro.client.gui;

import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.text.Text;
import neuro.client.NeuroClient;
import neuro.client.module.Category;
import neuro.client.module.Module;
import neuro.client.module.ModuleManager;
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

    private int guiX = 100;
    private int guiY = 50;
    private final int guiWidth = 580;
    private final int guiHeight = 390;

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

        int bgAlpha = (int) (235 * this.animationProgress);
        int mainBgColor = new Color(13, 17, 30, bgAlpha).getRGB();
        int borderColor = new Color(139, 92, 246, (int) (200 * this.animationProgress)).getRGB();

        context.fill(guiX, guiY, guiX + guiWidth, guiY + guiHeight, mainBgColor);
        context.drawBorder(guiX, guiY, guiWidth, guiHeight, borderColor);

        int headerHeight = 40;
        int headerBg = new Color(19, 24, 42, bgAlpha).getRGB();
        context.fill(guiX + 1, guiY + 1, guiX + guiWidth - 1, guiY + headerHeight, headerBg);

        context.drawText(this.textRenderer, "✦ NEURO CLIENT", guiX + 16, guiY + 15, 0xFFFFFFFF, true);
        context.drawText(this.textRenderer, "Fabric 1.21.4 | GRU AI", guiX + 120, guiY + 16, 0xFFA78BFA, false);

        String bindHint = "[Right Shift] ЛКМ: Вкл | ПКМ: Настройки";
        int hintWidth = this.textRenderer.getWidth(bindHint);
        context.drawText(this.textRenderer, bindHint, guiX + guiWidth - hintWidth - 16, guiY + 16, 0xFF94A3B8, false);

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

        int contentX = guiX + sidebarWidth + 14;
        int contentY = guiY + headerHeight + 12;
        int contentWidth = guiWidth - sidebarWidth - 28;

        ModuleManager moduleManager = NeuroClient.getInstance().getModuleManager();
        List<Module> modules = moduleManager.getModulesByCategory(this.currentCategory);

        int modY = contentY;
        for (Module module : modules) {
            if (modY + 36 > guiY + guiHeight - 10) break;

            boolean isHovered = mouseX >= contentX && mouseX <= contentX + contentWidth &&
                               mouseY >= modY && mouseY <= modY + 34;

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

            modY += 40;

            if (this.expandedModule == module && !module.getSettings().isEmpty()) {
                for (Setting<?> setting : module.getSettings()) {
                    if (modY + 24 > guiY + guiHeight - 10) break;
                    context.fill(contentX + 8, modY, contentX + contentWidth - 8, modY + 22, new Color(10, 14, 26, 220).getRGB());
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
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        if (button == 0 && mouseX >= guiX && mouseX <= guiX + guiWidth && mouseY >= guiY && mouseY <= guiY + 40) {
            this.isDragging = true;
            this.dragOffsetX = (int) (mouseX - guiX);
            this.dragOffsetY = (int) (mouseY - guiY);
            return true;
        }

        int sidebarWidth = 135;
        int headerHeight = 40;
        if (mouseX >= guiX && mouseX <= guiX + sidebarWidth && mouseY >= guiY + headerHeight) {
            int catY = guiY + headerHeight + 12;
            for (Category category : Category.values()) {
                if (mouseY >= catY && mouseY <= catY + 26) {
                    this.currentCategory = category;
                    this.expandedModule = null;
                    return true;
                }
                catY += 32;
            }
        }

        int contentX = guiX + sidebarWidth + 14;
        int contentY = guiY + headerHeight + 12;
        int contentWidth = guiWidth - sidebarWidth - 28;

        ModuleManager moduleManager = NeuroClient.getInstance().getModuleManager();
        List<Module> modules = moduleManager.getModulesByCategory(this.currentCategory);

        int modY = contentY;
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
            modY += 40;

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
