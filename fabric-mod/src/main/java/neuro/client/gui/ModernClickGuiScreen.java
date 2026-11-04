package neuro.client.gui;

import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.text.Text;
import neuro.client.NeuroClient;
import neuro.client.ai.NeuroDatasetRecorder;
import neuro.client.ai.NeuroTrainerManager;
import neuro.client.module.Category;
import neuro.client.module.Module;
import neuro.client.module.ModuleManager;
import neuro.client.module.combat.AuraModule;
import neuro.client.module.combat.FakePlayerModule;
import neuro.client.setting.BooleanSetting;
import neuro.client.setting.ModeSetting;
import neuro.client.setting.MultiSelectSetting;
import neuro.client.setting.Setting;
import neuro.client.setting.SliderSetting;
import org.lwjgl.glfw.GLFW;

import java.util.List;

public class ModernClickGuiScreen extends Screen {
    private Category currentCategory = Category.COMBAT;
    private Module expandedModule = null;
    private float animationProgress = 0.0f;
    private int scrollOffset = 0;

    private int guiX = 80;
    private int guiY = 30;
    private final int guiWidth = 630;
    private final int guiHeight = 430;

    private boolean isDragging = false;
    private int dragOffsetX = 0;
    private int dragOffsetY = 0;
    private SliderSetting activeSlider = null;

    public ModernClickGuiScreen() {
        super(Text.literal("Neuro ClickGUI 1.21.4"));
    }

    @Override
    protected void init() {
        super.init();
        this.animationProgress = 0.0f;
        this.guiX = Math.max(8, (this.width - this.guiWidth) / 2);
        this.guiY = Math.max(8, (this.height - this.guiHeight) / 2);
        if (this.expandedModule == null && NeuroClient.getInstance() != null) {
            this.expandedModule = NeuroClient.getInstance().getModuleManager().getModule(AuraModule.class);
        }
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
        context.fill(0, 0, this.width, this.height, NeuroClient.argb(backdropAlpha, 7, 9, 16));

        int bgAlpha = (int) (240 * this.animationProgress);
        int mainBgColor = NeuroClient.argb(bgAlpha, 13, 17, 30);
        int borderColor = NeuroClient.argb((int) (215 * this.animationProgress), 139, 92, 246);

        context.fill(guiX, guiY, guiX + guiWidth, guiY + guiHeight, mainBgColor);
        context.drawBorder(guiX, guiY, guiWidth, guiHeight, borderColor);

        int headerHeight = 42;
        int headerBg = NeuroClient.argb(bgAlpha, 19, 24, 42);
        context.fill(guiX + 1, guiY + 1, guiX + guiWidth - 1, guiY + headerHeight, headerBg);

        context.drawText(this.textRenderer, "✦ NEURO 1.21.4", guiX + 14, guiY + 16, 0xFFFFFFFF, true);

        NeuroClient nc = NeuroClient.getInstance();
        String modelLabel = nc != null && nc.getNeuroEngine() != null
                ? "AI: " + nc.getNeuroEngine().getLoadedModelName()
                : "GRU AI";
        context.drawText(this.textRenderer, modelLabel, guiX + 106, guiY + 16, 0xFFA78BFA, false);

        // 3 кнопки быстрого управления в шапке: [👤 Фейк-Плеер], [● Запись боя], [⚡ Обучить ИИ]
        FakePlayerModule fpMod = nc != null ? nc.getModuleManager().getModule(FakePlayerModule.class) : null;
        NeuroDatasetRecorder recorder = nc != null ? nc.getDatasetRecorder() : null;
        NeuroTrainerManager trainer = nc != null ? nc.getTrainerManager() : null;
        boolean fpActive = fpMod != null && fpMod.isEnabled();
        boolean recActive = recorder != null && recorder.isRecording();
        boolean trActive = trainer != null && trainer.isTraining();

        int fpBtnX = guiX + 215;
        int fpBtnY = guiY + 10;
        int fpBtnW = 104;
        int fpBtnH = 22;
        context.fill(fpBtnX, fpBtnY, fpBtnX + fpBtnW, fpBtnY + fpBtnH,
                fpActive ? NeuroClient.argb(235, 16, 185, 129) : NeuroClient.argb(220, 30, 41, 59));
        context.drawBorder(fpBtnX, fpBtnY, fpBtnW, fpBtnH, fpActive ? 0xFF34D399 : 0xFF64748B);
        String fpBtnLabel = fpActive ? "👤 Бот: ВКЛ" : "👤 Фейк-Плеер";
        context.drawText(this.textRenderer, fpBtnLabel, fpBtnX + 8, fpBtnY + 7, 0xFFFFFFFF, false);

        int recBtnX = fpBtnX + fpBtnW + 6;
        int recBtnY = guiY + 10;
        int recBtnW = 104;
        int recBtnH = 22;
        context.fill(recBtnX, recBtnY, recBtnX + recBtnW, recBtnY + recBtnH,
                recActive ? NeuroClient.argb(235, 185, 28, 28) : NeuroClient.argb(220, 30, 41, 59));
        context.drawBorder(recBtnX, recBtnY, recBtnW, recBtnH, recActive ? 0xFFEF4444 : 0xFF64748B);
        String recBtnLabel = recActive ? "■ СТОП (" + recorder.getRecordedTicks() + "t)" : "● Запись боя";
        context.drawText(this.textRenderer, recBtnLabel, recBtnX + 8, recBtnY + 7, 0xFFFFFFFF, false);

        int trBtnX = recBtnX + recBtnW + 6;
        int trBtnY = guiY + 10;
        int trBtnW = 104;
        int trBtnH = 22;
        context.fill(trBtnX, trBtnY, trBtnX + trBtnW, trBtnY + trBtnH,
                trActive ? NeuroClient.argb(235, 5, 150, 105) : NeuroClient.argb(220, 88, 28, 135));
        context.drawBorder(trBtnX, trBtnY, trBtnW, trBtnH, trActive ? 0xFF10B981 : 0xFFA855F7);
        String trBtnLabel = trActive ? "⚡ Обучение..." : "⚡ Обучить ИИ";
        context.drawText(this.textRenderer, trBtnLabel, trBtnX + 8, trBtnY + 7, 0xFFFFFFFF, false);

        String bindHint = "[R-Shift / .gui]";
        int hintWidth = this.textRenderer.getWidth(bindHint);
        context.drawText(this.textRenderer, bindHint, guiX + guiWidth - hintWidth - 10, guiY + 16, 0xFF94A3B8, false);

        int sidebarWidth = 142;
        int sidebarBg = NeuroClient.argb(bgAlpha, 10, 13, 24);
        context.fill(guiX + 1, guiY + headerHeight, guiX + sidebarWidth, guiY + guiHeight - 1, sidebarBg);

        ModuleManager moduleManager = nc.getModuleManager();
        int catY = guiY + headerHeight + 12;
        for (Category category : Category.values()) {
            boolean isSelected = (category == this.currentCategory);
            int textColor = isSelected ? 0xFFFFFFFF : 0xFF94A3B8;
            int count = moduleManager.getModulesByCategory(category).size();

            if (isSelected) {
                context.fill(guiX + 4, catY, guiX + 7, catY + 26, 0xFF8B5CF6);
                context.fill(guiX + 8, catY, guiX + sidebarWidth - 6, catY + 26, NeuroClient.argb(55, 139, 92, 246));
            }

            context.drawText(this.textRenderer, category.getDisplayName() + " (" + count + ")", guiX + 14, catY + 9, textColor, isSelected);
            catY += 32;
        }

        // Подсказки внизу сайдбара
        context.drawText(this.textRenderer, ".fp — ФейкПлеер", guiX + 10, guiY + guiHeight - 52, 0xFF34D399, false);
        context.drawText(this.textRenderer, ".neuro rec", guiX + 10, guiY + guiHeight - 40, 0xFF94A3B8, false);
        context.drawText(this.textRenderer, ".neuro train", guiX + 10, guiY + guiHeight - 28, 0xFFA78BFA, false);
        context.drawText(this.textRenderer, "ПКМ: настройки", guiX + 10, guiY + guiHeight - 16, 0xFF64748B, false);

        int contentX = guiX + sidebarWidth + 12;
        int contentY = guiY + headerHeight + 10;
        int contentWidth = guiWidth - sidebarWidth - 24;
        int contentBottom = guiY + guiHeight - 10;

        context.enableScissor(contentX - 2, contentY, contentX + contentWidth + 2, contentBottom);

        List<Module> modules = moduleManager.getModulesByCategory(this.currentCategory);

        int modY = contentY - this.scrollOffset;
        for (Module module : modules) {
            boolean isHovered = mouseX >= contentX && mouseX <= contentX + contentWidth &&
                               mouseY >= Math.max(contentY, modY) && mouseY <= Math.min(contentBottom, modY + 34);

            int cardBg = module.isEnabled()
                    ? NeuroClient.argb(235, 32, 38, 72)
                    : (isHovered ? NeuroClient.argb(210, 24, 30, 52) : NeuroClient.argb(195, 17, 22, 38));
            int cardBorder = module.isEnabled() ? 0xFF8B5CF6 : 0x33FFFFFF;

            context.fill(contentX, modY, contentX + contentWidth, modY + 34, cardBg);
            context.drawBorder(contentX, modY, contentWidth, 34, cardBorder);

            String title = module.getName() + "  §7(" + module.getSettings().size() + " настр. — ПКМ)";
            context.drawText(this.textRenderer, title, contentX + 12, modY + 7,
                    module.isEnabled() ? 0xFFFFFFFF : 0xFFCBD5E1, module.isEnabled());
            context.drawText(this.textRenderer, module.getDescription(), contentX + 12, modY + 19,
                    0xFF94A3B8, false);

            int toggleX = contentX + contentWidth - 38;
            int toggleY = modY + 10;
            int toggleColor = module.isEnabled() ? 0xFF8B5CF6 : 0xFF334155;
            context.fill(toggleX, toggleY, toggleX + 26, toggleY + 14, toggleColor);
            int knobX = module.isEnabled() ? toggleX + 14 : toggleX + 2;
            context.fill(knobX, toggleY + 2, knobX + 10, toggleY + 12, 0xFFFFFFFF);

            modY += 38;

            if (this.expandedModule == module && !module.getSettings().isEmpty()) {
                for (Setting<?> setting : module.getSettings()) {
                    if (setting instanceof MultiSelectSetting multi) {
                        context.fill(contentX + 8, modY, contentX + contentWidth - 8, modY + 36, NeuroClient.argb(230, 10, 14, 26));
                        context.drawText(this.textRenderer, multi.getName() + ":", contentX + 14, modY + 5, 0xFFA78BFA, false);
                        int chipX = contentX + 14;
                        int chipY = modY + 17;
                        for (String opt : multi.getOptions()) {
                            boolean sel = multi.isSelected(opt);
                            int cw = this.textRenderer.getWidth(opt) + 10;
                            context.fill(chipX, chipY, chipX + cw, chipY + 14,
                                    sel ? NeuroClient.argb(230, 124, 58, 237) : NeuroClient.argb(200, 30, 41, 59));
                            context.drawText(this.textRenderer, opt, chipX + 5, chipY + 3, sel ? 0xFFFFFFFF : 0xFF94A3B8, false);
                            chipX += cw + 4;
                        }
                        modY += 38;
                    } else {
                        context.fill(contentX + 8, modY, contentX + contentWidth - 8, modY + 22, NeuroClient.argb(225, 10, 14, 26));
                        if (setting instanceof SliderSetting slider) {
                            double ratio = (slider.getValue() - slider.getMin()) / (slider.getMax() - slider.getMin());
                            int fillW = (int) ((contentWidth - 16) * Math.max(0.0, Math.min(1.0, ratio)));
                            context.fill(contentX + 8, modY + 19, contentX + 8 + fillW, modY + 22, 0xFF8B5CF6);
                        }
                        String label = setting.getName() + ": " + setting.getValue();
                        context.drawText(this.textRenderer, label, contentX + 16, modY + 6, 0xFFC4B5FD, false);
                        modY += 24;
                    }
                }
                modY += 4;
            }
        }

        context.disableScissor();
    }

    @Override
    public boolean mouseScrolled(double mouseX, double mouseY, double horizontalAmount, double verticalAmount) {
        this.scrollOffset = Math.max(0, this.scrollOffset - (int) (verticalAmount * 22));
        return true;
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        NeuroClient nc = NeuroClient.getInstance();

        int fpBtnX = guiX + 215;
        int fpBtnY = guiY + 10;
        if (button == 0 && mouseX >= fpBtnX && mouseX <= fpBtnX + 104 && mouseY >= fpBtnY && mouseY <= fpBtnY + 22) {
            if (nc != null) {
                FakePlayerModule fpMod = nc.getModuleManager().getModule(FakePlayerModule.class);
                if (fpMod != null) fpMod.toggle();
            }
            return true;
        }

        int recBtnX = fpBtnX + 110;
        int recBtnY = guiY + 10;
        if (button == 0 && mouseX >= recBtnX && mouseX <= recBtnX + 104 && mouseY >= recBtnY && mouseY <= recBtnY + 22) {
            if (nc != null && nc.getDatasetRecorder() != null) {
                AuraModule aura = nc.getModuleManager().getModule(AuraModule.class);
                if (nc.getDatasetRecorder().isRecording()) {
                    nc.getDatasetRecorder().stopRecording();
                    if (aura != null) aura.setRecordDatasetFlag(false);
                } else {
                    nc.getDatasetRecorder().startRecording("session");
                    if (aura != null) aura.setRecordDatasetFlag(true);
                }
            }
            return true;
        }

        int trBtnX = recBtnX + 110;
        int trBtnY = guiY + 10;
        if (button == 0 && mouseX >= trBtnX && mouseX <= trBtnX + 104 && mouseY >= trBtnY && mouseY <= trBtnY + 22) {
            if (nc != null && nc.getTrainerManager() != null && !nc.getTrainerManager().isTraining()) {
                if (nc.getDatasetRecorder() != null && nc.getDatasetRecorder().isRecording()) {
                    nc.getDatasetRecorder().stopRecording();
                    AuraModule aura = nc.getModuleManager().getModule(AuraModule.class);
                    if (aura != null) aura.setRecordDatasetFlag(false);
                }
                nc.getTrainerManager().startTraining(200);
            }
            return true;
        }

        if (button == 0 && mouseX >= guiX && mouseX <= guiX + guiWidth && mouseY >= guiY && mouseY <= guiY + 42) {
            this.isDragging = true;
            this.dragOffsetX = (int) (mouseX - guiX);
            this.dragOffsetY = (int) (mouseY - guiY);
            return true;
        }

        int sidebarWidth = 142;
        int headerHeight = 42;
        if (mouseX >= guiX && mouseX <= guiX + sidebarWidth && mouseY >= guiY + headerHeight) {
            int catY = guiY + headerHeight + 12;
            for (Category category : Category.values()) {
                if (mouseY >= catY && mouseY <= catY + 26) {
                    this.currentCategory = category;
                    this.expandedModule = null;
                    this.scrollOffset = 0;
                    return true;
                }
                catY += 32;
            }
        }

        int contentX = guiX + sidebarWidth + 12;
        int contentY = guiY + headerHeight + 10;
        int contentWidth = guiWidth - sidebarWidth - 24;
        int contentBottom = guiY + guiHeight - 10;

        if (mouseY < contentY || mouseY > contentBottom) {
            return super.mouseClicked(mouseX, mouseY, button);
        }

        ModuleManager moduleManager = nc.getModuleManager();
        List<Module> modules = moduleManager.getModulesByCategory(this.currentCategory);

        int modY = contentY - this.scrollOffset;
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
            modY += 38;

            if (this.expandedModule == module && !module.getSettings().isEmpty()) {
                for (Setting<?> setting : module.getSettings()) {
                    if (setting instanceof MultiSelectSetting multi) {
                        if (mouseX >= contentX + 8 && mouseX <= contentX + contentWidth - 8 &&
                            mouseY >= modY && mouseY <= modY + 36) {
                            int chipX = contentX + 14;
                            int chipY = modY + 17;
                            for (String opt : multi.getOptions()) {
                                int cw = this.textRenderer.getWidth(opt) + 10;
                                if (mouseX >= chipX && mouseX <= chipX + cw && mouseY >= chipY && mouseY <= chipY + 14) {
                                    multi.toggleOption(opt);
                                    return true;
                                }
                                chipX += cw + 4;
                            }
                        }
                        modY += 38;
                    } else {
                        if (mouseX >= contentX + 8 && mouseX <= contentX + contentWidth - 8 &&
                            mouseY >= modY && mouseY <= modY + 22) {
                            if (setting instanceof BooleanSetting boolSetting) {
                                boolSetting.toggle();
                            } else if (setting instanceof ModeSetting modeSetting) {
                                modeSetting.cycle();
                            } else if (setting instanceof SliderSetting sliderSetting) {
                                this.activeSlider = sliderSetting;
                                double ratio = (mouseX - (contentX + 8)) / (double) (contentWidth - 16);
                                double val = sliderSetting.getMin() + ratio * (sliderSetting.getMax() - sliderSetting.getMin());
                                sliderSetting.setValue(val);
                            }
                            return true;
                        }
                        modY += 24;
                    }
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
            this.activeSlider = null;
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
        if (this.activeSlider != null) {
            int sidebarWidth = 142;
            int contentX = guiX + sidebarWidth + 12;
            int contentWidth = guiWidth - sidebarWidth - 24;
            double ratio = (mouseX - (contentX + 8)) / (double) (contentWidth - 16);
            double val = this.activeSlider.getMin() + ratio * (this.activeSlider.getMax() - this.activeSlider.getMin());
            this.activeSlider.setValue(val);
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
