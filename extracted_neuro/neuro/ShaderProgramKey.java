package rockstar.client.compat;

import com.mojang.blaze3d.vertex.VertexFormat;
import java.util.Objects;
import rockstar.client.render.compat.LegacyShaderProgram;
import net.minecraft.util.Identifier;

/** Description and lazy instance for a legacy Rockstar core shader. */
public final class ShaderProgramKey {
    private final Identifier id;
    private final VertexFormat format;
    private final boolean builtin;
    private ShaderProgram program;

    public ShaderProgramKey(Identifier id, VertexFormat format, Object ignoredDefines) {
        this(id, format, false);
    }

    private ShaderProgramKey(Identifier id, VertexFormat format, boolean builtin) {
        this.id = Objects.requireNonNull(id);
        this.format = Objects.requireNonNull(format);
        this.builtin = builtin;
    }

    static ShaderProgramKey builtin(String path, VertexFormat format) {
        return new ShaderProgramKey(Identifier.ofVanilla(path), format, true);
    }

    public ShaderProgram program() {
        if (program == null) {
            program = builtin
                ? new ShaderProgram(null)
                : new ShaderProgram(new LegacyShaderProgram(id, format));
        }
        return program;
    }

    public Identifier id() {
        return id;
    }

    public VertexFormat format() {
        return format;
    }
}

