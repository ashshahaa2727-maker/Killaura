package rockstar.client.compat;

import rockstar.client.render.compat.ImmediateRenderer;
import rockstar.client.render.compat.LegacyShaderProgram;

/** Legacy shader facade backed by an explicit 1.21.11 render pipeline. */
public final class ShaderProgram {
    private final LegacyShaderProgram delegate;

    ShaderProgram(LegacyShaderProgram delegate) {
        this.delegate = delegate;
    }

    void bind() {
        if (delegate == null) {
            ImmediateRenderer.clearShader();
        } else {
            delegate.bind();
        }
    }

    public GlUniform getUniform(String name) {
        if (delegate == null) {
            return null;
        }
        LegacyShaderProgram.UniformValue uniform = delegate.uniformOrNull(name);
        return uniform == null ? null : new GlUniform(uniform);
    }

    public void addSamplerTexture(String name, int glId) {
        com.mojang.blaze3d.textures.GpuTextureView view = rockstar.client.render.TextureCompat.viewOfGlId(glId);
        if (view == null) {
            if ("Sampler0".equals(name)) ImmediateRenderer.clearTexture();
            else ImmediateRenderer.clearTexture(name);
        } else if ("Sampler0".equals(name)) {
            ImmediateRenderer.setTexture(view);
        } else {
            ImmediateRenderer.setTexture(name, view, com.mojang.blaze3d.textures.FilterMode.LINEAR);
        }
    }

    public LegacyShaderProgram delegate() {
        return delegate;
    }

    public com.mojang.blaze3d.pipeline.RenderPipeline pipeline() {
        return delegate == null ? null : delegate.pipeline();
    }
}
