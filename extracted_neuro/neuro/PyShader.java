package pyrock.utility.render;




import rockstar.client.ui.*;
import rockstar.client.render.*;
import rockstar.client.internal.config.*;
import com.mojang.blaze3d.opengl.GlStateManager;
import com.mojang.blaze3d.platform.DestFactor;
import com.mojang.blaze3d.platform.SourceFactor;
import com.mojang.blaze3d.systems.ProjectionType;
import rockstar.client.compat.RenderSystem;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import net.minecraft.client.gl.Framebuffer;
import rockstar.client.compat.ShaderProgramKeys;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.texture.AbstractTexture;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.Vec3d;
import org.joml.Matrix4f;
import org.joml.Matrix4fStack;
import org.joml.Quaternionf;
import org.joml.Vector3f;
import org.joml.Vector4f;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL15;
import org.lwjgl.opengl.GL20;
import pyrock.events.render.Render3DEvent;
import rockstar.client.RockstarClient;
import rockstar.client.internal.config.ConfigInternal033;
import rockstar.client.MinecraftClientAccess;
import rockstar.client.ui.WindowAccess;
import rockstar.client.render.ManagedFramebuffer;
import rockstar.client.render.RenderPipeline;
import rockstar.client.render.UiBatchRenderer;

public final class PyShader implements AutoCloseable, MinecraftClientAccess, WindowAccess {
   public static final int MAX_PER_SCRIPT = 32;
   private static final int MAX_SOURCE_LENGTH = 262144;
   private static final Matrix4f IDENTITY = new Matrix4f();
   private static final long START = System.nanoTime();
   private static final int POSITION_UV = 5;
   private static final int POSITION_UV_COLOR = 9;
   private static final int MAX_VERTICES = 262144;
   private static int meshVao;
   private static int meshVbo;
   private static ManagedFramebuffer scratch;
   private static String header;
   private static String vertexHeader;
   private static final Pattern IMPORT = Pattern.compile("(?m)^[ \\t]*#moj_import[ \\t]*<rockstar:([\\w.]+)>[ \\t]*$");
   private final String name;
   private final String vertexSource;
   private final String fragmentSource;
   private final Map<String, Integer> locations = new HashMap<>();
   private final Map<String, float[]> floats = new LinkedHashMap<>();
   private final Map<String, int[]> ints = new LinkedHashMap<>();
   private final Map<String, Matrix4f> matrices = new LinkedHashMap<>();
   private final Map<Integer, Object> textures = new LinkedHashMap<>();
   private int programId;
   private boolean disposed;

   public PyShader(String localValue1, String localValue2, String localValue3) {
      if (localValue3 != null && !localValue3.isBlank()) {
         if (localValue3.length() <= 262144 && (localValue2 == null || localValue2.length() <= 262144)) {
            if (!RenderSystem.isOnRenderThread()) {
               throw new IllegalStateException(
                  "\u0448\u0435\u0439\u0434\u0435\u0440 \u043a\u043e\u043c\u043f\u0438\u043b\u0438\u0440\u0443\u0435\u0442\u0441\u044f \u0442\u043e\u043b\u044c\u043a\u043e \u043d\u0430 \u0433\u043b\u0430\u0432\u043d\u043e\u043c \u043f\u043e\u0442\u043e\u043a\u0435 \u0438\u0433\u0440\u044b"
               );
            } else {
               this.name = localValue1 != null && !localValue1.isBlank() ? localValue1 : "shader";
               this.vertexSource = localValue2 != null && !localValue2.isBlank() ? localValue2 : defaultVertex();
               this.fragmentSource = localValue3;
               guardEndlessLoop(this.vertexSource);
               guardEndlessLoop(this.fragmentSource);
               this.programId = this.link(this.prepare(this.vertexSource, false), this.prepare(this.fragmentSource, true));
            }
         } else {
            throw new IllegalArgumentException("shader source is too large");
         }
      } else {
         throw new IllegalArgumentException("shader needs a fragment source");
      }
   }

   public String name() {
      return this.name;
   }

   public String vertexSource() {
      return this.vertexSource;
   }

   public String fragmentSource() {
      return this.fragmentSource;
   }

   public boolean valid() {
      return !this.disposed && this.programId != 0;
   }

   public PyShader set(String localValue1, float localValue2) {
      this.floats.put(localValue1, new float[]{localValue2});
      return this;
   }

   public PyShader set(String localValue1, float localValue2, float localValue3) {
      this.floats.put(localValue1, new float[]{localValue2, localValue3});
      return this;
   }

   public PyShader set(String localValue1, float localValue2, float localValue3, float localValue4) {
      this.floats.put(localValue1, new float[]{localValue2, localValue3, localValue4});
      return this;
   }

   public PyShader set(String localValue1, float localValue2, float localValue3, float localValue4, float localValue5) {
      this.floats.put(localValue1, new float[]{localValue2, localValue3, localValue4, localValue5});
      return this;
   }

   public PyShader setInt(String localValue1, int localValue2) {
      this.ints.put(localValue1, new int[]{localValue2});
      return this;
   }

   public PyShader setMatrix(String localValue1, Matrix4f localValue2) {
      if (localValue2 != null) {
         this.matrices.put(localValue1, new Matrix4f(localValue2));
      }

      return this;
   }

   public PyShader setColor(String localValue1, ColorRGBA localValue2) {
      return localValue2 == null ? this : this.set(localValue1, localValue2.getRed() / 255.0F, localValue2.getGreen() / 255.0F, localValue2.getBlue() / 255.0F, localValue2.getAlpha() / 255.0F);
   }

   public PyShader texture(int localValue1, Object localValue2) {
      if (localValue1 >= 0 && localValue1 <= 7) {
         if (localValue2 == null) {
            this.textures.remove(localValue1);
         } else {
            this.textures.put(localValue1, localValue2);
         }

         return this;
      } else {
         throw new IllegalArgumentException("texture unit out of range: " + localValue1);
      }
   }

   public void rect(DrawContext localValue1, float localValue2, float localValue3, float localValue4, float localValue5) {
      if (this.valid() && !(localValue4 <= 0.0F) && !(localValue5 <= 0.0F)) {
         UiBatchRenderer.internalMethod02576();
         Matrix4f localValue6 = localValue1 == null ? IDENTITY : rockstar.client.render.GuiMatrixCompat.toMatrix4f(localValue1.getMatrices());
         float[] localValue7 = this.quad(localValue6, localValue2, localValue3, localValue4, localValue5, 0.0F, 0.0F, 1.0F, 1.0F);
         RenderSystem.enableBlend();
         RenderSystem.defaultBlendFunc();
         this.bind();
         this.builtin("Size", localValue4, localValue5);
         this.builtin("ModelViewMat", RenderSystem.getModelViewMatrix());
         this.builtin("ProjMat", RenderSystem.getProjectionMatrix());
         this.applyUniforms();
         drawQuad(localValue7);
         this.unbind();
         RenderSystem.disableBlend();
      }
   }

   public void fullscreen(Render3DEvent localValue1) {
      if (this.valid()) {
         Framebuffer localValue2 = internalField0149.getFramebuffer();
         if (localValue2 != null) {
            int localValue3 = internalField0267.getScaledWidth();
            int localValue4 = internalField0267.getScaledHeight();
            if (localValue3 > 0 && localValue4 > 0) {
               if (scratch == null) {
                  scratch = new ManagedFramebuffer(false).internalMethod06013();
               }

               RenderSystem.disableBlend();
               RenderSystem.depthMask(false);
               RenderSystem.backupProjectionMatrix();
               RenderSystem.setProjectionMatrix(new Matrix4f().setOrtho(0.0F, localValue3, localValue4, 0.0F, 1000.0F, 21000.0F), ProjectionType.ORTHOGRAPHIC);
               Matrix4fStack localValue5 = RenderSystem.getModelViewStack();
               localValue5.pushMatrix();
               localValue5.identity().translate(0.0F, 0.0F, -11000.0F);

               try {
                  scratch.internalMethod02227(false);
                  this.bind();
                  this.builtin("Size", localValue3, localValue4);
                  this.builtin("ModelViewMat", RenderSystem.getModelViewMatrix());
                  this.builtin("ProjMat", RenderSystem.getProjectionMatrix());
                  if (localValue1 != null) {
                     this.builtin("InvViewProj", new Matrix4f(localValue1.getProjectionMatrix()).mul(localValue1.getPositionMatrix()).invert());
                     if (localValue1.getCamera() != null) {
                        Vector3f localValue6 = localValue1.getCamera().getCameraPos().toVector3f();
                        this.builtin("CamPos", localValue6.x, localValue6.y, localValue6.z);
                     }
                  }

                  this.bindTexture(0, rockstar.client.render.FramebufferCompat.glId(localValue2.getColorAttachment()));
                  if (localValue2.getDepthAttachment() != null) {
                     this.bindTexture(1, rockstar.client.render.FramebufferCompat.glId(localValue2.getDepthAttachment()));
                  }

                  this.applyUniforms();
                  drawQuad(this.quad(IDENTITY, 0.0F, 0.0F, localValue3, localValue4, 0.0F, 1.0F, 1.0F, 0.0F));
                  this.unbind();
                  scratch.internalMethod03248();
                  RenderSystem.setShader(ShaderProgramKeys.POSITION_TEX_COLOR);
                  RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
                  RenderSystem.setShaderTexture(0, scratch.getColorAttachmentView());
                  RenderPipeline.internalMethod01737(0.0F, 0.0F, localValue3, localValue4);
                  RenderSystem.setShaderTexture(0, 0);
               } finally {
                  localValue5.popMatrix();
                  RenderSystem.restoreProjectionMatrix();
                  RenderSystem.depthMask(true);
                  RenderSystem.enableBlend();
                  RenderSystem.defaultBlendFunc();
               }
            }
         }
      }
   }

   public void quad3d(Render3DEvent localValue1, double localValue2, double localValue4, double localValue6, float localValue8, float localValue9, String localValue10, boolean localValue11, boolean localValue12) {
      if (this.valid() && localValue1 != null && localValue1.getCamera() != null && !(localValue8 <= 0.0F) && !(localValue9 <= 0.0F)) {
         Vector3f localValue13 = localValue1.getCamera().getCameraPos().toVector3f().negate().add((float)localValue2, (float)localValue4, (float)localValue6);
         Vector3f localValue14 = new Vector3f();
         Vector3f localValue15 = new Vector3f();
         this.axes(localValue1, localValue10, localValue14, localValue15);
         localValue14.mul(localValue8 * 0.5F);
         localValue15.mul(localValue9 * 0.5F);
         Matrix4f localValue16 = localValue1.getMatrices().peek().getPositionMatrix();
         float[] localValue17 = new float[20];
         this.corner(localValue17, 0, localValue16, localValue13, localValue14, localValue15, -1.0F, 1.0F, 0.0F, 0.0F);
         this.corner(localValue17, 5, localValue16, localValue13, localValue14, localValue15, -1.0F, -1.0F, 0.0F, 1.0F);
         this.corner(localValue17, 10, localValue16, localValue13, localValue14, localValue15, 1.0F, 1.0F, 1.0F, 0.0F);
         this.corner(localValue17, 15, localValue16, localValue13, localValue14, localValue15, 1.0F, -1.0F, 1.0F, 1.0F);
         RenderSystem.enableBlend();
         if (localValue11) {
            RenderSystem.blendFunc(SourceFactor.SRC_ALPHA, DestFactor.ONE);
         } else {
            RenderSystem.defaultBlendFunc();
         }

         RenderSystem.disableCull();
         RenderSystem.depthMask(false);
         if (localValue12) {
            RenderSystem.enableDepthTest();
         } else {
            RenderSystem.disableDepthTest();
         }

         this.bind();
         this.builtin("Size", localValue8, localValue9);
         this.builtin("ModelViewMat", RenderSystem.getModelViewMatrix());
         this.builtin("ProjMat", RenderSystem.getProjectionMatrix());
         this.builtin("InvViewProj", new Matrix4f(localValue1.getProjectionMatrix()).mul(localValue1.getPositionMatrix()).invert());
         this.applyUniforms();
         drawQuad(localValue17);
         this.unbind();
         RenderSystem.depthMask(true);
         RenderSystem.enableDepthTest();
         RenderSystem.enableCull();
         RenderSystem.defaultBlendFunc();
         RenderSystem.disableBlend();
      }
   }

   public void mesh3d(
      Render3DEvent localValue1, Object localValue2, double localValue3, double localValue5, double localValue7, String localValue9, boolean localValue10, boolean localValue11, boolean localValue12, boolean localValue13
   ) {
      if (this.valid() && localValue1 != null && localValue1.getCamera() != null) {
         float[] localValue14 = floats(localValue2);
         int localValue15 = localValue10 ? 9 : 5;
         if (localValue14.length >= localValue15) {
            if (localValue14.length / localValue15 > 262144) {
               throw new IllegalArgumentException(
                  "\u0432 \u043c\u0435\u0448\u0435 \u0431\u043e\u043b\u044c\u0448\u0435 262144 \u0432\u0435\u0440\u0448\u0438\u043d"
               );
            } else {
               Vec3d localValue16 = localValue1.getCamera().getCameraPos();
               Matrix4f localValue17 = new Matrix4f(RenderSystem.getModelViewMatrix())
                  .mul(localValue1.getMatrices().peek().getPositionMatrix())
                  .translate((float)(localValue3 - localValue16.x), (float)(localValue5 - localValue16.y), (float)(localValue7 - localValue16.z));
               RenderSystem.enableBlend();
               if (localValue11) {
                  RenderSystem.blendFunc(SourceFactor.SRC_ALPHA, DestFactor.ONE);
               } else {
                  RenderSystem.defaultBlendFunc();
               }

               if (localValue13) {
                  RenderSystem.enableCull();
               } else {
                  RenderSystem.disableCull();
               }

               RenderSystem.depthMask(false);
               if (localValue12) {
                  RenderSystem.enableDepthTest();
               } else {
                  RenderSystem.disableDepthTest();
               }

               this.bind();
               this.builtin("Size", 1.0F, 1.0F);
               this.builtin("ModelViewMat", localValue17);
               this.builtin("ProjMat", RenderSystem.getProjectionMatrix());
               this.builtin("InvViewProj", new Matrix4f(localValue1.getProjectionMatrix()).mul(localValue1.getPositionMatrix()).invert());
               this.applyUniforms();
               draw(localValue14, localValue15, primitive(localValue9));
               this.unbind();
               RenderSystem.depthMask(true);
               RenderSystem.enableDepthTest();
               RenderSystem.enableCull();
               RenderSystem.defaultBlendFunc();
               RenderSystem.disableBlend();
            }
         }
      }
   }

   public void mesh2d(DrawContext localValue1, Object localValue2, String localValue3, boolean localValue4) {
      if (this.valid()) {
         float[] localValue5 = floats(localValue2);
         int localValue6 = localValue4 ? 9 : 5;
         if (localValue5.length >= localValue6) {
            if (localValue5.length / localValue6 > 262144) {
               throw new IllegalArgumentException(
                  "\u0432 \u043c\u0435\u0448\u0435 \u0431\u043e\u043b\u044c\u0448\u0435 262144 \u0432\u0435\u0440\u0448\u0438\u043d"
               );
            } else {
               UiBatchRenderer.internalMethod02576();
               Matrix4f localValue7 = new Matrix4f(RenderSystem.getModelViewMatrix());
               if (localValue1 != null) {
                  localValue7.mul(rockstar.client.render.GuiMatrixCompat.toMatrix4f(localValue1.getMatrices()));
               }

               RenderSystem.enableBlend();
               RenderSystem.defaultBlendFunc();
               this.bind();
               this.builtin("Size", internalField0267.getScaledWidth(), internalField0267.getScaledHeight());
               this.builtin("ModelViewMat", localValue7);
               this.builtin("ProjMat", RenderSystem.getProjectionMatrix());
               this.applyUniforms();
               draw(localValue5, localValue6, primitive(localValue3));
               this.unbind();
               RenderSystem.disableBlend();
            }
         }
      }
   }

   @Override
   public void close() {
      this.dispose();
   }

   public void dispose() {
      if (!this.disposed) {
         this.disposed = true;
         int localValue1 = this.programId;
         this.programId = 0;
         if (localValue1 != 0) {
            if (RenderSystem.isOnRenderThread()) {
               GlStateManager.glDeleteProgram(localValue1);
            } else {
               internalField0149.execute(() -> GlStateManager.glDeleteProgram(localValue1));
            }
         }
      }
   }

   private void axes(Render3DEvent localValue1, String localValue2, Vector3f localValue3, Vector3f localValue4) {
      String localValue5 = localValue2 == null ? "billboard" : localValue2.toLowerCase();
      switch (localValue5) {
         case "ground":
         case "floor":
         case "flat":
            localValue3.set(1.0F, 0.0F, 0.0F);
            localValue4.set(0.0F, 0.0F, 1.0F);
            break;
         case "wall":
         case "upright":
         case "yaw":
            float localValue9 = (float)Math.toRadians(localValue1.getCamera().getYaw());
            localValue3.set(-((float)Math.cos(localValue9)), 0.0F, -((float)Math.sin(localValue9)));
            localValue4.set(0.0F, 1.0F, 0.0F);
            break;
         default:
            Quaternionf localValue8 = localValue1.getCamera().getRotation();
            localValue3.set(1.0F, 0.0F, 0.0F).rotate(localValue8);
            localValue4.set(0.0F, 1.0F, 0.0F).rotate(localValue8);
      }
   }

   private void corner(float[] localValue1, int localValue2, Matrix4f localValue3, Vector3f localValue4, Vector3f localValue5, Vector3f localValue6, float localValue7, float localValue8, float localValue9, float localValue10) {
      Vector4f localValue11 = new Vector4f(
            localValue4.x + localValue5.x * localValue7 + localValue6.x * localValue8, localValue4.y + localValue5.y * localValue7 + localValue6.y * localValue8, localValue4.z + localValue5.z * localValue7 + localValue6.z * localValue8, 1.0F
         )
         .mul(localValue3);
      localValue1[localValue2] = localValue11.x;
      localValue1[localValue2 + 1] = localValue11.y;
      localValue1[localValue2 + 2] = localValue11.z;
      localValue1[localValue2 + 3] = localValue9;
      localValue1[localValue2 + 4] = localValue10;
   }

   private float[] quad(Matrix4f localValue1, float localValue2, float localValue3, float localValue4, float localValue5, float localValue6, float localValue7, float localValue8, float localValue9) {
      float[] localValue10 = new float[20];
      this.point(localValue10, 0, localValue1, localValue2, localValue3, localValue6, localValue7);
      this.point(localValue10, 5, localValue1, localValue2, localValue3 + localValue5, localValue6, localValue9);
      this.point(localValue10, 10, localValue1, localValue2 + localValue4, localValue3, localValue8, localValue7);
      this.point(localValue10, 15, localValue1, localValue2 + localValue4, localValue3 + localValue5, localValue8, localValue9);
      return localValue10;
   }

   private void point(float[] localValue1, int localValue2, Matrix4f localValue3, float localValue4, float localValue5, float localValue6, float localValue7) {
      Vector4f localValue8 = new Vector4f(localValue4, localValue5, 0.0F, 1.0F).mul(localValue3);
      localValue1[localValue2] = localValue8.x;
      localValue1[localValue2 + 1] = localValue8.y;
      localValue1[localValue2 + 2] = localValue8.z;
      localValue1[localValue2 + 3] = localValue6;
      localValue1[localValue2 + 4] = localValue7;
   }

   private void bind() {
      GlStateManager._glUseProgram(this.programId);
      this.builtin("Time", (float)((System.nanoTime() - START) / 1.0E9 % 3600.0));
      this.builtin("Resolution", internalField0267.getScaledWidth(), internalField0267.getScaledHeight());
      this.builtin("GuiScale", (float)internalField0267.getScaleFactor());
      this.builtin(
         "MousePos",
         (float)(internalField0149.mouse.getX() * internalField0267.getScaledWidth() / Math.max(1, internalField0267.getWidth())),
         (float)(internalField0149.mouse.getY() * internalField0267.getScaledHeight() / Math.max(1, internalField0267.getHeight()))
      );
   }

   private void unbind() {
      GlStateManager._glBindVertexArray(0);
      GlStateManager._glUseProgram(0);
      GlStateManager._activeTexture(33984);
   }

   private void applyUniforms() {
      for (Entry localValue2 : this.textures.entrySet()) {
         this.bindTexture((Integer)localValue2.getKey(), this.glId(localValue2.getValue()));
      }

      for (Entry localValue8 : this.floats.entrySet()) {
         int localValue3 = this.location((String)localValue8.getKey());
         if (localValue3 >= 0) {
            float[] localValue4 = (float[])localValue8.getValue();
            switch (localValue4.length) {
               case 1:
                  GL20.glUniform1f(localValue3, localValue4[0]);
                  break;
               case 2:
                  GL20.glUniform2f(localValue3, localValue4[0], localValue4[1]);
                  break;
               case 3:
                  GL20.glUniform3f(localValue3, localValue4[0], localValue4[1], localValue4[2]);
                  break;
               default:
                  GL20.glUniform4f(localValue3, localValue4[0], localValue4[1], localValue4[2], localValue4[3]);
            }
         }
      }

      for (Entry localValue9 : this.ints.entrySet()) {
         int localValue11 = this.location((String)localValue9.getKey());
         if (localValue11 >= 0) {
            GL20.glUniform1i(localValue11, ((int[])localValue9.getValue())[0]);
         }
      }

      for (Entry localValue10 : this.matrices.entrySet()) {
         int localValue12 = this.location((String)localValue10.getKey());
         if (localValue12 >= 0) {
            GL20.glUniformMatrix4fv(localValue12, false, ((Matrix4f)localValue10.getValue()).get(new float[16]));
         }
      }
   }

   private void bindTexture(int localValue1, int localValue2) {
      if (localValue2 > 0) {
         GlStateManager._activeTexture(33984 + localValue1);
         GlStateManager._bindTexture(localValue2);
         int localValue3 = this.location("Sampler" + localValue1);
         if (localValue3 >= 0) {
            GL20.glUniform1i(localValue3, localValue1);
         }

         GlStateManager._activeTexture(33984);
      }
   }

   private int glId(Object localValue1) {
      if (localValue1 instanceof Integer localValue5) {
         return localValue5;
      } else if (localValue1 instanceof PyDynamicTexture localValue4) {
         return this.glId(localValue4.identifier());
      } else if (localValue1 instanceof Identifier localValue2) {
         AbstractTexture localValue3 = internalField0149.getTextureManager().getTexture(localValue2);
         return localValue3 == null ? 0 : rockstar.client.render.FramebufferCompat.glId(localValue3.getGlTexture());
      } else {
         return 0;
      }
   }

   private void builtin(String localValue1, float localValue2) {
      int localValue3 = this.location(localValue1);
      if (localValue3 >= 0) {
         GL20.glUniform1f(localValue3, localValue2);
      }
   }

   private void builtin(String localValue1, float localValue2, float localValue3) {
      int localValue4 = this.location(localValue1);
      if (localValue4 >= 0) {
         GL20.glUniform2f(localValue4, localValue2, localValue3);
      }
   }

   private void builtin(String localValue1, float localValue2, float localValue3, float localValue4) {
      int localValue5 = this.location(localValue1);
      if (localValue5 >= 0) {
         GL20.glUniform3f(localValue5, localValue2, localValue3, localValue4);
      }
   }

   private void builtin(String localValue1, Matrix4f localValue2) {
      int localValue3 = this.location(localValue1);
      if (localValue3 >= 0) {
         GL20.glUniformMatrix4fv(localValue3, false, localValue2.get(new float[16]));
      }
   }

   private int location(String localValue1) {
      return this.locations.computeIfAbsent(localValue1, localValue1x -> GlStateManager._glGetUniformLocation(this.programId, localValue1x));
   }

   private static void drawQuad(float[] localValue0) {
      draw(localValue0, 5, 5);
   }

   private static void draw(float[] localValue0, int localValue1, int localValue2) {
      int localValue3 = localValue0.length / localValue1;
      if (localValue3 > 0) {
         if (!RenderSystem.isOnRenderThread()) {
            throw new IllegalStateException(
               "\u0448\u0435\u0439\u0434\u0435\u0440 \u0440\u0438\u0441\u0443\u0435\u0442\u0441\u044f \u0442\u043e\u043b\u044c\u043a\u043e \u043d\u0430 \u0433\u043b\u0430\u0432\u043d\u043e\u043c \u043f\u043e\u0442\u043e\u043a\u0435 \u0438\u0433\u0440\u044b"
            );
         } else {
            if (meshVao == 0) {
               meshVao = GlStateManager._glGenVertexArrays();
               meshVbo = GlStateManager._glGenBuffers();
            }

            GlStateManager._glBindVertexArray(meshVao);
            GlStateManager._glBindBuffer(34962, meshVbo);
            GL15.glBufferData(34962, localValue0, 35048);
            int localValue4 = localValue1 * 4;
            GL20.glEnableVertexAttribArray(0);
            GL20.glVertexAttribPointer(0, 3, 5126, false, localValue4, 0L);
            GL20.glEnableVertexAttribArray(1);
            GL20.glVertexAttribPointer(1, 2, 5126, false, localValue4, 12L);
            if (localValue1 >= 9) {
               GL20.glEnableVertexAttribArray(2);
               GL20.glVertexAttribPointer(2, 4, 5126, false, localValue4, 20L);
            } else {
               GL20.glDisableVertexAttribArray(2);
               GL20.glVertexAttrib4f(2, 255.0F, 255.0F, 255.0F, 255.0F);
            }

            GL11.glDrawArrays(localValue2, 0, localValue3);
            GlStateManager._glBindBuffer(34962, 0);
         }
      }
   }

   private static int primitive(String localValue0) {
      String localValue1 = localValue0 == null ? "triangles" : localValue0.toLowerCase();

      return switch (localValue1) {
         case "triangle_strip", "strip" -> 5;
         case "triangle_fan", "fan" -> 6;
         case "lines" -> 1;
         case "line_strip" -> 3;
         case "line_loop" -> 2;
         case "points" -> 0;
         case "triangles", "tris" -> 4;
         default -> throw new IllegalArgumentException(
            "\u043d\u0435\u0438\u0437\u0432\u0435\u0441\u0442\u043d\u044b\u0439 \u0440\u0435\u0436\u0438\u043c \u043f\u0440\u0438\u043c\u0438\u0442\u0438\u0432\u0430: "
               + localValue0
         );
      };
   }

   private static float[] floats(Object localValue0) {
      if (localValue0 == null) {
         return new float[0];
      } else if (localValue0 instanceof float[] localValue8) {
         return localValue8;
      } else if (localValue0 instanceof byte[] localValue7) {
         FloatBuffer localValue10 = ByteBuffer.wrap(localValue7).order(ByteOrder.LITTLE_ENDIAN).asFloatBuffer();
         float[] localValue12 = new float[localValue10.remaining()];
         localValue10.get(localValue12);
         return localValue12;
      } else if (!(localValue0 instanceof double[] localValue1)) {
         if (localValue0 instanceof List localValue6) {
            float[] localValue9 = new float[localValue6.size()];

            for (int localValue11 = 0; localValue11 < localValue9.length; localValue11++) {
               if (!(localValue6.get(localValue11) instanceof Number localValue5)) {
                  throw new IllegalArgumentException(
                     "\u0432\u0435\u0440\u0448\u0438\u043d\u044b \u043c\u0435\u0448\u0430 \u044d\u0442\u043e \u0447\u0438\u0441\u043b\u0430"
                  );
               }

               localValue9[localValue11] = localValue5.floatValue();
            }

            return localValue9;
         } else {
            throw new IllegalArgumentException(
               "\u0432\u0435\u0440\u0448\u0438\u043d\u044b \u043c\u0435\u0448\u0430: \u043f\u043b\u043e\u0441\u043a\u0438\u0439 \u0441\u043f\u0438\u0441\u043e\u043a \u0447\u0438\u0441\u0435\u043b \u0438\u043b\u0438 array('f').tobytes()"
            );
         }
      } else {
         float[] localValue2 = new float[localValue1.length];

         for (int localValue3 = 0; localValue3 < localValue1.length; localValue3++) {
            localValue2[localValue3] = (float)localValue1[localValue3];
         }

         return localValue2;
      }
   }

   private int link(String localValue1, String localValue2) {
      int localValue3 = this.compile(35633, localValue1, "vertex");

      int localValue4;
      try {
         localValue4 = this.compile(35632, localValue2, "fragment");
      } catch (RuntimeException localValue7) {
         GlStateManager.glDeleteShader(localValue3);
         throw localValue7;
      }

      int localValue5 = GlStateManager.glCreateProgram();
      GlStateManager.glAttachShader(localValue5, localValue3);
      GlStateManager.glAttachShader(localValue5, localValue4);
      GlStateManager._glBindAttribLocation(localValue5, 0, "Position");
      GlStateManager._glBindAttribLocation(localValue5, 1, "UV");
      GlStateManager.glLinkProgram(localValue5);
      GlStateManager.glDeleteShader(localValue3);
      GlStateManager.glDeleteShader(localValue4);
      if (GlStateManager.glGetProgrami(localValue5, 35714) == 0) {
         String localValue6 = GlStateManager.glGetProgramInfoLog(localValue5, 4096);
         GlStateManager.glDeleteProgram(localValue5);
         throw new RuntimeException(
            "\u0448\u0435\u0439\u0434\u0435\u0440 "
               + this.name
               + " \u043d\u0435 \u0441\u043b\u0438\u043d\u043a\u043e\u0432\u0430\u043b\u0441\u044f: "
               + localValue6.trim()
         );
      } else {
         return localValue5;
      }
   }

   private int compile(int localValue1, String localValue2, String localValue3) {
      int localValue4 = GlStateManager.glCreateShader(localValue1);
      GlStateManager.glShaderSource(localValue4, localValue2);
      GlStateManager.glCompileShader(localValue4);
      if (GlStateManager.glGetShaderi(localValue4, 35713) == 0) {
         String localValue5 = GlStateManager.glGetShaderInfoLog(localValue4, 4096);
         GlStateManager.glDeleteShader(localValue4);
         throw new RuntimeException(
            "\u0448\u0435\u0439\u0434\u0435\u0440 "
               + this.name
               + " ("
               + localValue3
               + ") \u043d\u0435 \u0441\u043e\u0431\u0440\u0430\u043b\u0441\u044f: "
               + localValue5.trim()
         );
      } else {
         return localValue4;
      }
   }

   private String prepare(String localValue1, boolean localValue2) {
      return hasVersion(localValue1) ? localValue1 : (localValue2 ? this.fragmentHeader() : "#version 150\n") + "\n#line 0\n" + localValue1;
   }

   private boolean ownVertex() {
      return this.vertexSource.equals(defaultVertex());
   }

   private static boolean hasVersion(String localValue0) {
      int localValue1 = 0;
      int localValue2 = localValue0.length();

      while (localValue1 < localValue2) {
         char localValue3 = localValue0.charAt(localValue1);
         if (Character.isWhitespace(localValue3)) {
            localValue1++;
         } else if (localValue3 != '/' || localValue1 + 1 >= localValue2 || localValue0.charAt(localValue1 + 1) != '/') {
            if (localValue3 != '/' || localValue1 + 1 >= localValue2 || localValue0.charAt(localValue1 + 1) != '*') {
               return localValue0.startsWith("#version", localValue1);
            }

            int localValue5 = localValue0.indexOf("*/", localValue1 + 2);
            if (localValue5 < 0) {
               return false;
            }

            localValue1 = localValue5 + 2;
         } else {
            int localValue4 = localValue0.indexOf(10, localValue1);
            if (localValue4 < 0) {
               return false;
            }

            localValue1 = localValue4 + 1;
         }
      }

      return false;
   }

   private String fragmentHeader() {
      if (header == null) {
         header = resource("shaders/scripts/header.fsh") + "\n" + glsl("shaders/include/common.glsl");
      }

      return this.ownVertex() ? header + "\nin vec4 VertexColor;\n" : header;
   }

   private static String glsl(String localValue0) {
      Matcher localValue1 = IMPORT.matcher(resource(localValue0));
      StringBuilder localValue2 = new StringBuilder();

      while (localValue1.find()) {
         localValue1.appendReplacement(localValue2, Matcher.quoteReplacement(glsl("shaders/include/" + localValue1.group(1))));
      }

      return localValue1.appendTail(localValue2).toString();
   }

   private static String defaultVertex() {
      if (vertexHeader == null) {
         vertexHeader = resource("shaders/scripts/vertex.vsh");
      }

      return vertexHeader;
   }

   private static String resource(String localValue0) {
      try {
         return ConfigInternal033.internalMethod04705(RockstarClient.id(localValue0));
      } catch (Throwable localValue2) {
         throw new IllegalStateException(
            "\u043d\u0435 \u0447\u0438\u0442\u0430\u0435\u0442\u0441\u044f \u0440\u0435\u0441\u0443\u0440\u0441 \u0448\u0435\u0439\u0434\u0435\u0440\u0430 "
               + localValue0,
            localValue2
         );
      }
   }

   private static void guardEndlessLoop(String localValue0) {
      String localValue1 = localValue0.replaceAll("\\s+", "");
      if (localValue1.contains("while(true)") || localValue1.contains("for(;;)")) {
         throw new IllegalArgumentException(
            "\u0431\u0435\u0441\u043a\u043e\u043d\u0435\u0447\u043d\u044b\u0439 \u0446\u0438\u043a\u043b \u0432 \u0448\u0435\u0439\u0434\u0435\u0440\u0435 \u043f\u043e\u0432\u0435\u0441\u0438\u0442 \u0432\u0438\u0434\u0435\u043e\u0434\u0440\u0430\u0439\u0432\u0435\u0440: \u0434\u0430\u0439\u0442\u0435 \u0441\u0447\u0451\u0442\u0447\u0438\u043a\u0443 \u0446\u0438\u043a\u043b\u0430 \u043f\u0440\u0435\u0434\u0435\u043b"
         );
      }
   }
}
