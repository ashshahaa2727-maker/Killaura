package rockstar.client.compat;

import net.minecraft.client.render.VertexFormats;

/** Built-in shader selections used by the 1.21.4 immediate renderer. */
public final class ShaderProgramKeys {
    public static final ShaderProgramKey POSITION_COLOR = ShaderProgramKey.builtin("position_color", VertexFormats.POSITION_COLOR);
    public static final ShaderProgramKey POSITION_TEX_COLOR = ShaderProgramKey.builtin("position_tex_color", VertexFormats.POSITION_TEXTURE_COLOR);
    public static final ShaderProgramKey POSITION_COLOR_TEX_LIGHTMAP = ShaderProgramKey.builtin("position_color_tex_lightmap", VertexFormats.POSITION_COLOR_TEXTURE_LIGHT_NORMAL);
    public static final ShaderProgramKey RENDERTYPE_LINES = ShaderProgramKey.builtin("rendertype_lines", VertexFormats.POSITION_COLOR_NORMAL_LINE_WIDTH);
    public static final ShaderProgramKey BLIT_SCREEN = ShaderProgramKey.builtin("blit_screen", VertexFormats.POSITION_TEXTURE);

    private ShaderProgramKeys() {
    }
}


