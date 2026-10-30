package neuro.client.setting;

import java.util.List;

public class ModeSetting extends Setting<String> {
    private final List<String> modes;

    public ModeSetting(String name, String defaultValue, List<String> modes) {
        super(name, defaultValue);
        this.modes = modes;
    }

    public List<String> getModes() {
        return modes;
    }

    public void cycle() {
        int idx = modes.indexOf(getValue());
        int next = (idx + 1) % modes.size();
        setValue(modes.get(next));
    }
}
