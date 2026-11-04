package neuro.client.setting;

import java.util.ArrayList;
import java.util.List;

public class MultiSelectSetting extends Setting<List<String>> {
    private final List<String> options;

    public MultiSelectSetting(String name, List<String> options, List<String> defaultSelected) {
        super(name, new ArrayList<>(defaultSelected));
        this.options = List.copyOf(options);
    }

    public List<String> getOptions() {
        return options;
    }

    public boolean isSelected(String option) {
        return getValue().contains(option);
    }

    public void toggleOption(String option) {
        List<String> current = new ArrayList<>(getValue());
        if (current.contains(option)) {
            current.remove(option);
        } else if (options.contains(option)) {
            current.add(option);
        }
        setValue(current);
    }

    public String getSummary() {
        List<String> cur = getValue();
        if (cur.isEmpty()) return "None";
        return String.join(", ", cur);
    }
}
