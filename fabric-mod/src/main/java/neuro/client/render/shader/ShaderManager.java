package neuro.client.render.shader;

import net.minecraft.util.Identifier;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.Map;

public class ShaderManager {
    private static final Logger LOGGER = LoggerFactory.getLogger("ShaderManager");
    private final Map<String, Identifier> shaderRegistry = new HashMap<>();

    public void initShaders() {
        LOGGER.info("[Neuro] Регистрация шейдеров 1.21.4 (UI Universal, Squircle, Glow, Sky Nebula)...");
        shaderRegistry.put("ui_universal", Identifier.of("neuro", "shaders/core/ui_universal"));
        shaderRegistry.put("liquidglass", Identifier.of("neuro", "shaders/core/liquidglass"));
        shaderRegistry.put("glow", Identifier.of("neuro", "shaders/core/glow"));
        shaderRegistry.put("sky_nebula", Identifier.of("neuro", "shaders/core/sky_nebula"));
    }

    public Identifier getShaderId(String name) {
        return shaderRegistry.get(name);
    }
}
