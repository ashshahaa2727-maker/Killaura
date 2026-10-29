package pyrock.classes.aura;



import rockstar.client.setting.*;
import rockstar.client.internal.script.*;
import java.util.ArrayList;
import jep.python.PyCallable;
import net.minecraft.entity.Entity;
import pyrock.classes.aura.PyRotationMode;
import rockstar.client.internal.script.ScriptInternal083;
import rockstar.client.setting.ModeSetting;
import rockstar.client.RockstarClient;
import rockstar.modules.combat.AuraModule;

public class PyAura {
    private AuraModule aura() {
        return RockstarClient.getInstance().getModuleManager().getModule(AuraModule.class);
    }

    private ModeSetting rotationMode() {
        return this.aura().internalMethod01895();
    }

    public PyRotationMode addRotation(String string, PyCallable pyCallable, PyCallable pyCallable2, PyCallable pyCallable3, PyCallable pyCallable4, PyCallable pyCallable5) {
        ModeSetting typedValue170 = this.rotationMode();
        ScriptInternal083.internalMethod06468(typedValue170);
        PyRotationMode pyRotationMode = new PyRotationMode(typedValue170, string, pyCallable, pyCallable2, pyCallable3, pyCallable4, pyCallable5);
        ScriptInternal083.internalMethod06874(typedValue170, pyRotationMode);
        return pyRotationMode;
    }

    public void selectRotation(String string) {
        ModeSetting typedValue170 = this.rotationMode();
        for (ModeSetting.InternalType0088 nestedValue0042 : typedValue170.internalMethod06723()) {
            if (!nestedValue0042.getName().equals(string)) continue;
            typedValue170.internalMethod03917(nestedValue0042);
            return;
        }
    }

    public void removeRotation(String string) {
        ModeSetting typedValue170 = this.rotationMode();
        ArrayList<PyRotationMode> arrayList = new ArrayList<PyRotationMode>();
        for (ModeSetting.InternalType0088 nestedValue0042 : typedValue170.internalMethod06723()) {
            if (!(nestedValue0042 instanceof PyRotationMode)) continue;
            PyRotationMode pyRotationMode = (PyRotationMode)nestedValue0042;
            if (!nestedValue0042.getName().equals(string) || !ScriptInternal083.internalMethod06875(typedValue170, nestedValue0042)) continue;
            arrayList.add(pyRotationMode);
        }
        for (PyRotationMode pyRotationMode : arrayList) {
            this.removeRotation(pyRotationMode);
        }
    }

    public void removeRotation(PyRotationMode pyRotationMode) {
        if (pyRotationMode == null) {
            return;
        }
        ModeSetting typedValue170 = this.rotationMode();
        if (!ScriptInternal083.internalMethod06875(typedValue170, pyRotationMode)) {
            return;
        }
        boolean bl = typedValue170.internalMethod07418() == pyRotationMode;
        typedValue170.internalMethod06723().remove(pyRotationMode);
        ScriptInternal083.internalMethod04950(typedValue170, pyRotationMode);
        if (bl) {
            typedValue170.internalMethod03917(typedValue170.internalMethod06723().isEmpty() ? null : typedValue170.internalMethod06723().getFirst());
        }
    }

    public boolean hasRotation(String string) {
        for (ModeSetting.InternalType0088 nestedValue0042 : this.rotationMode().internalMethod06723()) {
            if (!nestedValue0042.getName().equals(string)) continue;
            return true;
        }
        return false;
    }

    public String currentRotation() {
        ModeSetting.InternalType0088 nestedValue0042 = this.rotationMode().internalMethod07418();
        return nestedValue0042 == null ? null : nestedValue0042.getName();
    }

    public boolean isEnabled() {
        return this.aura().isEnabled();
    }

    public void setEnabled(boolean bl) {
        this.aura().setEnabled(bl, false);
    }

    public Entity target() {
        return RockstarClient.getInstance().internalMethod04463().internalMethod04526();
    }

    public float attackDistance() {
        return this.aura().internalMethod05151().internalMethod08576();
    }
}
