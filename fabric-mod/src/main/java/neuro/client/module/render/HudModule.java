package neuro.client.module.render;

import neuro.client.module.Category;
import neuro.client.module.Module;
import neuro.client.setting.BooleanSetting;

public class HudModule extends Module {
    private final BooleanSetting watermark = new BooleanSetting("Watermark", true);
    private final BooleanSetting arrayList = new BooleanSetting("ArrayList", true);
    private final BooleanSetting targetHud = new BooleanSetting("TargetHUD", true);
    private final BooleanSetting coords = new BooleanSetting("Coords & BPS", true);

    public HudModule() {
        super("HUD", "Ватермарка, список активных модулей, TargetHUD и координаты", Category.RENDER);
        this.setEnabled(true);
        this.addSettings(watermark, arrayList, targetHud, coords);
    }

    public boolean isShowWatermark() { return watermark.getValue(); }
    public boolean isShowArrayList() { return arrayList.getValue(); }
    public boolean isShowTargetHud() { return targetHud.getValue(); }
    public boolean isShowCoords() { return coords.getValue(); }
}
