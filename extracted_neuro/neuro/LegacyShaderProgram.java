package rockstar.client.render.compat;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.mojang.blaze3d.buffers.GpuBufferSlice;
import com.mojang.blaze3d.buffers.GpuBuffer;
import com.mojang.blaze3d.buffers.Std140Builder;
import com.mojang.blaze3d.buffers.Std140SizeCalculator;
import com.mojang.blaze3d.pipeline.BlendFunction;
import com.mojang.blaze3d.platform.DepthTestFunction;
import com.mojang.blaze3d.pipeline.RenderPipeline;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.VertexFormat;
import java.io.InputStreamReader;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import net.minecraft.client.gl.RenderPipelines;
import net.minecraft.client.gl.DynamicUniformStorage;
import net.minecraft.client.gl.UniformType;
import net.minecraft.util.Identifier;
import org.joml.Matrix4fc;

/** Adapts Rockstar's legacy core shaders to the explicit 1.21.11 pipeline API. */
public class LegacyShaderProgram {
   public static final List<Runnable> shaderRendererCoreItems = new ArrayList<>();
   private static final List<LegacyShaderProgram> INSTANCES = new ArrayList<>();
   private final Map<String, UniformValue> uniforms = new LinkedHashMap<>();
   private final RenderPipeline pipeline;
   private final RenderPipeline.Snippet pipelineSnippet;
   private final Map<PipelineVariant, RenderPipeline> pipelineVariants = new LinkedHashMap<>();
   private final int uniformBufferSize;
   private final UniformSnapshot[] uniformSnapshots;
   private UniformSnapshot lastUniformSnapshot;
   private DynamicUniformStorage<UniformSnapshot> uniformStorage;
   private boolean dedicatedUniformBuffer;
   private boolean gui;

   public LegacyShaderProgram(Identifier identifier, VertexFormat vertexFormat) {
      this(identifier, vertexFormat, VertexFormat.DrawMode.QUADS, true, loadDefinition(identifier));
   }

   public LegacyShaderProgram(Identifier identifier, VertexFormat vertexFormat, VertexFormat.DrawMode drawMode) {
      this(identifier, vertexFormat, drawMode, true, loadDefinition(identifier));
   }

   public LegacyShaderProgram(Identifier identifier, VertexFormat vertexFormat, VertexFormat.DrawMode drawMode, boolean translucent) {
      this(identifier, vertexFormat, drawMode, translucent, loadDefinition(identifier));
   }

   public LegacyShaderProgram(
      Identifier identifier,
      VertexFormat vertexFormat,
      Identifier vertexShader,
      Identifier fragmentShader,
      List<String> samplers,
      UniformSpec... uniformSpecs
   ) {
      this(
         identifier,
         vertexFormat,
         VertexFormat.DrawMode.QUADS,
         true,
         new ShaderDefinition(
            vertexShader,
            fragmentShader,
            List.copyOf(samplers),
            java.util.Arrays.stream(uniformSpecs)
               .map(spec -> new UniformValue(spec.name(), spec.count(), spec.integer(), new float[spec.count()]))
               .toList(),
            false,
            false
         )
      );
   }

   private LegacyShaderProgram(
      Identifier identifier,
      VertexFormat vertexFormat,
      VertexFormat.DrawMode drawMode,
      boolean translucent,
      ShaderDefinition definition
   ) {
      Std140SizeCalculator size = new Std140SizeCalculator();
      for (UniformValue value : definition.uniforms) {
         uniforms.put(value.name, value);
         value.addSize(size);
      }
      uniformBufferSize = (size.get() + 15) & ~15;
      int componentCount = 0;
      for (UniformValue value : uniforms.values()) {
         componentCount += value.count;
      }
      uniformSnapshots = new UniformSnapshot[]{
         new UniformSnapshot(this, new float[componentCount]),
         new UniformSnapshot(this, new float[componentCount])
      };

      RenderPipeline.Snippet standardUniforms = definition.usesLighting
         ? RenderPipelines.TRANSFORMS_PROJECTION_FOG_LIGHTING_SNIPPET
         : definition.usesFog
            ? RenderPipelines.TRANSFORMS_PROJECTION_FOG_SNIPPET
            : RenderPipelines.TRANSFORMS_AND_PROJECTION_SNIPPET;
      RenderPipeline.Builder builder = RenderPipeline.builder(standardUniforms)
         .withLocation(Identifier.of(identifier.getNamespace(), "pipeline/ported/" + identifier.getPath().replace("/data", "")))
         .withVertexShader(definition.vertexShader)
         .withFragmentShader(definition.fragmentShader)
         .withUniform("RockstarData", UniformType.UNIFORM_BUFFER)
         .withDepthTestFunction(DepthTestFunction.NO_DEPTH_TEST)
         .withDepthWrite(false)
         .withCull(false)
         .withVertexFormat(vertexFormat, drawMode);
      if (translucent) {
         builder.withBlend(BlendFunction.TRANSLUCENT);
      }
      for (String sampler : definition.samplers) {
         builder.withSampler(sampler);
      }
      pipelineSnippet = builder.buildSnippet();
      pipeline = RenderPipelines.register(builder.build());
      INSTANCES.add(this);
      shaderRendererCoreItems.add(this::method01430);
      method01430();
   }

   public record UniformSpec(String name, int count, boolean integer) {
      public UniformSpec(String name, int count) {
         this(name, count, false);
      }
   }

   public void method00688() {
      ImmediateRenderer.useShader(this);
   }

   public void bind() {
      method00688();
   }

   protected void method01430() {
   }

   public UniformValue method00099(String name) {
      UniformValue value = uniforms.get(name);
      if (value == null) {
         throw new IllegalArgumentException("Unknown shader uniform " + name + " in " + pipeline.getLocation());
      }
      return value;
   }

   public UniformValue uniform(String name) {
      return method00099(name);
   }

   public UniformValue uniformOrNull(String name) {
      return uniforms.get(name);
   }

   public RenderPipeline pipeline() {
      return pipeline;
   }

   RenderPipeline pipeline(LegacyRenderState state, VertexFormat.DrawMode mode) {
      return pipeline(state, mode, false);
   }

   public RenderPipeline worldLayerPipeline(boolean translucent, boolean polygonOffset) {
      return worldLayerPipeline(translucent, polygonOffset, true);
   }

   public RenderPipeline worldLayerPipeline(boolean translucent, boolean polygonOffset, boolean cull) {
      return pipeline(new LegacyRenderState(translucent ? BlendFunction.TRANSLUCENT : null,
         DepthTestFunction.LEQUAL_DEPTH_TEST, true, cull, true, true), pipeline.getVertexFormatMode(), polygonOffset);
   }

   private RenderPipeline pipeline(LegacyRenderState state, VertexFormat.DrawMode mode, boolean polygonOffset) {
      return pipelineVariants.computeIfAbsent(new PipelineVariant(state, mode, polygonOffset), key ->
         RenderPipelines.register(key.state().apply(RenderPipeline.builder(pipelineSnippet))
            .withLocation(pipeline.getLocation().withSuffixedPath("/state_" + pipelineVariants.size()))
            .withDepthBias(key.polygonOffset() ? -1.0F : 0.0F, key.polygonOffset() ? -10.0F : 0.0F)
            .withVertexFormat(pipeline.getVertexFormat(), key.mode()).build()));
   }

   private record PipelineVariant(LegacyRenderState state, VertexFormat.DrawMode mode, boolean polygonOffset) { }

   /**
    * Keeps animated or state-heavy shaders isolated from the shared deferred
    * uniform ring. Their values must remain immutable until the GPU executes
    * the queued draw, otherwise alternating frames can observe another draw's
    * data and visibly flicker.
    */
   public LegacyShaderProgram useDedicatedUniformBuffer() {
      dedicatedUniformBuffer = true;
      return this;
   }

   public LegacyShaderProgram worldSpace() {
      gui = false;
      return this;
   }

   boolean isGui() {
      return gui;
   }

   boolean usesDedicatedUniformBuffer() {
      return dedicatedUniformBuffer;
   }

   GpuBufferSlice createUniformSlice() {
      if (uniformStorage == null) {
         uniformStorage = new DynamicUniformStorage<>("Rockstar shader uniforms", Math.max(uniformBufferSize, 16), 4);
      }
      UniformSnapshot snapshot = this.lastUniformSnapshot == this.uniformSnapshots[0]
         ? this.uniformSnapshots[1]
         : this.uniformSnapshots[0];
      float[] components = snapshot.components;
      int offset = 0;
      for (UniformValue value : uniforms.values()) {
         System.arraycopy(value.values, 0, components, offset, value.count);
         offset += value.count;
      }
      GpuBufferSlice slice = uniformStorage.write(snapshot);
      if (this.lastUniformSnapshot == null || !this.lastUniformSnapshot.equals(snapshot)) {
         this.lastUniformSnapshot = snapshot;
      }
      return slice;
   }

   GpuBuffer createUniformBuffer() {
      ByteBuffer bytes = ByteBuffer.allocateDirect(Math.max(uniformBufferSize, 16)).order(ByteOrder.nativeOrder());
      Std140Builder writer = Std140Builder.intoBuffer(bytes);
      for (UniformValue value : uniforms.values()) {
         value.write(writer);
      }
      writer.align(16);
      return RenderSystem.getDevice().createBuffer(() -> "Rockstar direct shader uniforms", GpuBuffer.USAGE_UNIFORM, writer.get());
   }

   public static void rotateUniformBuffers() {
      for (LegacyShaderProgram instance : INSTANCES) {
         if (instance.uniformStorage != null) {
            instance.uniformStorage.clear();
            instance.lastUniformSnapshot = null;
         }
      }
   }

   public static void method01338() {
      shaderRendererCoreItems.forEach(Runnable::run);
   }

   private static ShaderDefinition loadDefinition(Identifier id) {
      String shaderPath = id.getPath();

      String path = "/assets/" + id.getNamespace() + "/shaders/" + shaderPath + ".json";
      try (var stream = LegacyShaderProgram.class.getResourceAsStream(path)) {
         if (stream == null) {
            throw new IllegalStateException("Missing shader definition " + path);
         }
         JsonObject root = JsonParser.parseReader(new InputStreamReader(stream, StandardCharsets.UTF_8)).getAsJsonObject();
         Identifier vertex = resolveShaderId(id, root.get("vertex").getAsString());
         Identifier fragment = resolveShaderId(id, root.get("fragment").getAsString());
         List<String> samplers = new ArrayList<>();
         if (root.has("samplers")) {
            for (var element : root.getAsJsonArray("samplers")) {
               samplers.add(element.getAsJsonObject().get("name").getAsString());
            }
         }
         List<UniformValue> uniforms = new ArrayList<>();
         boolean usesFog = shaderImports(vertex, "minecraft:fog.glsl") || shaderImports(fragment, "minecraft:fog.glsl");
         boolean usesLighting = shaderImports(vertex, "minecraft:light.glsl") || shaderImports(fragment, "minecraft:light.glsl");
         for (var element : root.getAsJsonArray("uniforms")) {
            JsonObject uniform = element.getAsJsonObject();
            String name = uniform.get("name").getAsString();
            if (name.equals("FogStart") || name.equals("FogEnd") || name.equals("FogColor") || name.equals("FogShape")) {
               usesFog = true;
               continue;
            }
            if (name.equals("Light0_Direction") || name.equals("Light1_Direction")) {
               usesFog = true;
               usesLighting = true;
               continue;
            }
            if (name.equals("ModelViewMat") || name.equals("ProjMat") || name.equals("ColorModulator")) {
               continue;
            }
            int count = uniform.get("count").getAsInt();
            boolean integer = uniform.get("type").getAsString().equals("int");
            float[] values = new float[count];
            JsonArray defaults = uniform.getAsJsonArray("values");
            for (int i = 0; i < Math.min(count, defaults.size()); i++) {
               values[i] = defaults.get(i).getAsFloat();
            }
            uniforms.add(new UniformValue(name, count, integer, values));
         }
         return new ShaderDefinition(vertex, fragment, samplers, uniforms, usesFog, usesLighting);
      } catch (Exception exception) {
         throw new IllegalStateException("Unable to load shader " + id, exception);
      }
   }

   private static Identifier resolveShaderId(Identifier definitionId, String value) {
      if (value.indexOf(':') >= 0) {
         return Identifier.of(value);
      }
      String path = definitionId.getPath();
      int slash = path.lastIndexOf('/');
      String parent = slash < 0 ? "" : path.substring(0, slash + 1);
      return Identifier.of(definitionId.getNamespace(), parent + value);
   }

   private static boolean shaderImports(Identifier shader, String include) {
      String base = "/assets/" + shader.getNamespace() + "/shaders/" + shader.getPath();
      for (String extension : List.of(".vsh", ".fsh")) {
         try (var stream = LegacyShaderProgram.class.getResourceAsStream(base + extension)) {
            if (stream != null && new String(stream.readAllBytes(), StandardCharsets.UTF_8).contains(include)) {
               return true;
            }
         } catch (Exception exception) {
            throw new IllegalStateException("Unable to inspect shader " + shader, exception);
         }
      }
      return false;
   }

   public static final class UniformValue {
      private final String name;
      private final int count;
      private final boolean integer;
      private final float[] values;

      private UniformValue(String name, int count, boolean integer, float[] values) {
         this.name = name;
         this.count = count;
         this.integer = integer;
         this.values = values;
      }

      public void set(float x) {
         if (values.length > 0) values[0] = x;
      }

      public void set(float x, float y) {
         if (values.length > 0) values[0] = x;
         if (values.length > 1) values[1] = y;
      }

      public void set(float x, float y, float z) {
         if (values.length > 0) values[0] = x;
         if (values.length > 1) values[1] = y;
         if (values.length > 2) values[2] = z;
      }

      public void set(float x, float y, float z, float w) {
         if (values.length > 0) values[0] = x;
         if (values.length > 1) values[1] = y;
         if (values.length > 2) values[2] = z;
         if (values.length > 3) values[3] = w;
      }

      public void set(int x) {
         if (values.length > 0) values[0] = x;
      }

      public void set(Matrix4fc matrix) {
         if (values.length != 16) {
            throw new IllegalStateException("Uniform " + name + " is not a mat4");
         }
         matrix.get(values);
      }

      private void addSize(Std140SizeCalculator size) {
         if (integer) size.putInt();
         else if (count == 1) size.putFloat();
         else if (count == 2) size.putVec2();
         else if (count == 3) size.putVec3();
         else if (count == 16) size.putMat4f();
         else size.putVec4();
      }

      private void write(Std140Builder writer) {
         if (integer) writer.putInt((int)values[0]);
         else if (count == 1) writer.putFloat(values[0]);
         else if (count == 2) writer.putVec2(values[0], values[1]);
         else if (count == 3) writer.putVec3(values[0], values[1], values[2]);
         else if (count == 16) {
            for (int column = 0; column < 4; column++) {
               int offset = column * 4;
               writer.putVec4(values[offset], values[offset + 1], values[offset + 2], values[offset + 3]);
            }
         }
         else writer.putVec4(values[0], values[1], values[2], values[3]);
      }
   }

   private static final class UniformSnapshot implements DynamicUniformStorage.Uploadable {
      private final LegacyShaderProgram owner;
      private final float[] components;

      private UniformSnapshot(LegacyShaderProgram owner, float[] components) {
         this.owner = owner;
         this.components = components;
      }

      @Override
      public void write(ByteBuffer buffer) {
         Std140Builder writer = Std140Builder.intoBuffer(buffer);
         int offset = 0;
         for (UniformValue value : this.owner.uniforms.values()) {
            if (value.integer) writer.putInt((int)this.components[offset]);
            else if (value.count == 1) writer.putFloat(this.components[offset]);
            else if (value.count == 2) writer.putVec2(this.components[offset], this.components[offset + 1]);
            else if (value.count == 3) writer.putVec3(this.components[offset], this.components[offset + 1], this.components[offset + 2]);
            else if (value.count == 16) {
               for (int column = 0; column < 4; column++) {
                  int matrixOffset = offset + column * 4;
                  writer.putVec4(
                     this.components[matrixOffset],
                     this.components[matrixOffset + 1],
                     this.components[matrixOffset + 2],
                     this.components[matrixOffset + 3]
                  );
               }
            }
            else writer.putVec4(this.components[offset], this.components[offset + 1], this.components[offset + 2], this.components[offset + 3]);
            offset += value.count;
         }
         writer.align(16);
      }

      @Override
      public boolean equals(Object other) {
         return this == other
            || other instanceof UniformSnapshot snapshot
               && this.owner == snapshot.owner
               && Arrays.equals(this.components, snapshot.components);
      }

      @Override
      public int hashCode() {
         return 31 * System.identityHashCode(this.owner) + Arrays.hashCode(this.components);
      }
   }

   private record ShaderDefinition(
      Identifier vertexShader,
      Identifier fragmentShader,
      List<String> samplers,
      List<UniformValue> uniforms,
      boolean usesFog,
      boolean usesLighting
   ) {
   }
}
