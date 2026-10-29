package rockstar.client.render;


import rockstar.client.*;
import rockstar.client.internal.script.*;
import lombok.Generated;
import net.minecraft.util.Identifier;
import rockstar.client.internal.script.ScriptInternal013;

public class ShaderPair {
    private final ScriptInternal013 internalField0439;
    private final ScriptInternal013 internalField0438;

    public ShaderPair(Identifier identifier, Identifier identifier2) {
        this.internalField0439 = new ScriptInternal013(identifier);
        this.internalField0438 = new ScriptInternal013(identifier2);
    }

    @Generated
    public ScriptInternal013 internalMethod04938() {
        return this.internalField0439;
    }

    @Generated
    public ScriptInternal013 internalMethod06188() {
        return this.internalField0438;
    }
}

