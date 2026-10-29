package pyrock.classes.aura;






import rockstar.client.util.*;
import rockstar.client.setting.*;
import rockstar.client.rotation.*;
import rockstar.client.internal.script.*;
import rockstar.client.internal.rotation.*;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.List;
import java.util.Map;
import jep.python.PyCallable;
import jep.python.PyObject;
import net.minecraft.entity.LivingEntity;
import net.minecraft.text.Text;
import pyrock.classes.PyRotations;
import rockstar.client.internal.script.ScriptInternal083;
import rockstar.client.internal.script.ScriptInternal085;
import rockstar.client.setting.ModeSetting;
import rockstar.client.RockstarClient;
import rockstar.client.util.ClientMessages;
import rockstar.client.rotation.RotationBehavior;
import rockstar.client.rotation.Rotation;
import rockstar.client.rotation.RotationManager;
import rockstar.client.rotation.RotationUtils;
import rockstar.client.rotation.RotationPriority;
import rockstar.client.internal.rotation.RotationInternal005;

public class PyRotationMode extends RotationInternal005 {
   private final PyCallable rotateFn;
   private final PyCallable attackFn;
   private final PyCallable canAttackFn;
   private final PyCallable targetNullFn;
   private final PyCallable updateFn;
   private final ScriptInternal083 owner;
   private boolean errored;

   public PyRotationMode(ModeSetting localValue1, String localValue2, PyCallable localValue3, PyCallable localValue4, PyCallable localValue5, PyCallable localValue6, PyCallable localValue7) {
      super(localValue1, localValue2);
      this.rotateFn = localValue3;
      this.attackFn = localValue4;
      this.canAttackFn = localValue5;
      this.targetNullFn = localValue6;
      this.updateFn = localValue7;
      this.owner = ScriptInternal083.internalMethod00581();
   }

   @Override
   public void rotate(RotationManager localValue1, float localValue2, boolean localValue3, boolean localValue4, RotationBehavior localValue5, LivingEntity localValue6) {
      if (this.rotateFn != null && !this.errored && ScriptInternal085.internalMethod08724()) {
         try (AutoCloseable localValue7 = ScriptInternal083.internalMethod02561(this.owner)) {
            Object localValue8 = this.rotateFn.call(new Object[]{localValue1, localValue2, localValue3, localValue4, localValue5, localValue6});
            PyRotationMode.InternalType0452 localValue9 = this.parseRotationRequest(localValue8, localValue5);
            if (localValue9 != null) {
               localValue1.internalMethod01596(
                  localValue9.rotation(), localValue9.moveCorrection(), localValue9.yawSpeed(), localValue9.pitchSpeed(), localValue9.returnSpeed(), localValue9.priority(), localValue9.correctGcd()
               );
            }
         } catch (Exception localValue12) {
            this.fail("rotate", localValue12);
            this.applyDefault(localValue1, localValue5, localValue6);
         }
      } else {
         this.applyDefault(localValue1, localValue5, localValue6);
      }
   }

   private void applyDefault(RotationManager localValue1, RotationBehavior localValue2, LivingEntity localValue3) {
      if (localValue3 != null) {
         Rotation localValue4 = RotationUtils.internalMethod04766(localValue3, this.aura());
         localValue1.internalMethod00418(localValue4, localValue2, 180.0F, 180.0F, 180.0F, RotationPriority.internalField1010);
      }
   }

   private PyRotationMode.InternalType0452 parseRotationRequest(Object localValue1, RotationBehavior localValue2) {
      if (localValue1 == null) {
         return null;
      } else if (localValue1 instanceof Rotation localValue11) {
         return this.request(localValue11, localValue2);
      } else if (localValue1 instanceof List localValue10) {
         return this.request(this.rotationFromList(localValue10), localValue2);
      } else if (localValue1 instanceof Map localValue9) {
         return this.requestFromMap(localValue9, localValue2);
      } else if (localValue1 instanceof PyObject localValue8) {
         PyRotationMode.InternalType0452 localValue4;
         try {
            localValue4 = this.requestFromPyObject(localValue8, localValue2);
         } finally {
            this.closeQuietly(localValue8);
         }

         return localValue4;
      } else {
         Rotation localValue3 = this.rotationFromObject(localValue1);
         if (localValue3 != null) {
            return this.request(localValue3, localValue2);
         } else {
            throw new IllegalArgumentException("rotate returned unsupported value: " + localValue1.getClass().getName());
         }
      }
   }

   private PyRotationMode.InternalType0452 requestFromPyObject(PyObject localValue1, RotationBehavior localValue2) {
      Rotation localValue3 = this.pyAs(localValue1, Rotation.class);
      if (localValue3 != null) {
         return this.request(localValue3, localValue2);
      } else {
         List localValue4 = this.pyAs(localValue1, List.class);
         if (localValue4 != null) {
            return this.request(this.rotationFromList(localValue4), localValue2);
         } else {
            Map localValue5 = this.pyAs(localValue1, Map.class);
            if (localValue5 != null) {
               return this.requestFromMap(localValue5, localValue2);
            } else {
               Object localValue6 = this.pyAttr(localValue1, "yaw");
               Object localValue7 = this.pyAttr(localValue1, "pitch");
               if (localValue6 != null && localValue7 != null) {
                  return this.request(new Rotation(this.asFloat(localValue6, "yaw"), this.asFloat(localValue7, "pitch")), localValue2);
               } else {
                  throw new IllegalArgumentException("rotate returned unsupported python object: " + localValue1);
               }
            }
         }
      }
   }

   private PyRotationMode.InternalType0452 requestFromMap(Map<?, ?> localValue1, RotationBehavior localValue2) {
      Rotation localValue3 = null;
      Object localValue4 = this.first(localValue1, "rotation", "rot");
      if (localValue4 != null) {
         PyRotationMode.InternalType0452 localValue5 = this.parseRotationRequest(localValue4, localValue2);
         if (localValue5 != null) {
            localValue3 = localValue5.rotation();
         }
      }

      if (localValue3 == null) {
         localValue3 = new Rotation(this.asFloat(this.first(localValue1, "yaw", "x"), "yaw"), this.asFloat(this.first(localValue1, "pitch", "y"), "pitch"));
      }

      RotationBehavior localValue11 = this.parseCorrection(this.first(localValue1, "correction", "moveCorrection", "move_correction"), localValue2);
      RotationPriority localValue6 = this.parsePriority(this.first(localValue1, "priority", "prio"), RotationPriority.internalField1010);
      float localValue7 = this.asFloat(this.first(localValue1, "yawSpeed", "yaw_speed"), 180.0F);
      float localValue8 = this.asFloat(this.first(localValue1, "pitchSpeed", "pitch_speed"), 180.0F);
      float localValue9 = this.asFloat(this.first(localValue1, "returnSpeed", "return_speed"), 180.0F);
      boolean localValue10 = this.parseCorrectGcd(localValue1);
      return new PyRotationMode.InternalType0452(localValue3, localValue11, localValue7, localValue8, localValue9, localValue6, localValue10);
   }

   private Rotation rotationFromList(List<?> localValue1) {
      if (localValue1.size() < 2) {
         throw new IllegalArgumentException("rotation list must contain yaw and pitch");
      } else {
         return new Rotation(this.asFloat(localValue1.get(0), "yaw"), this.asFloat(localValue1.get(1), "pitch"));
      }
   }

   private Rotation rotationFromObject(Object localValue1) {
      Object localValue2 = this.property(localValue1, "yaw", "getYaw");
      Object localValue3 = this.property(localValue1, "pitch", "getPitch");
      return localValue2 != null && localValue3 != null ? new Rotation(this.asFloat(localValue2, "yaw"), this.asFloat(localValue3, "pitch")) : null;
   }

   private PyRotationMode.InternalType0452 request(Rotation localValue1, RotationBehavior localValue2) {
      return new PyRotationMode.InternalType0452(localValue1, localValue2, 180.0F, 180.0F, 180.0F, RotationPriority.internalField1010, true);
   }

   private Object first(Map<?, ?> localValue1, String... localValue2) {
      for (String localValue6 : localValue2) {
         if (localValue1.containsKey(localValue6)) {
            return localValue1.get(localValue6);
         }
      }

      return null;
   }

   private Object pyAttr(PyObject localValue1, String localValue2) {
      try {
         return localValue1.getAttr(localValue2);
      } catch (Exception localValue4) {
         return null;
      }
   }

   private <T> T pyAs(PyObject localValue1, Class<T> localValue2) {
      try {
         return (T)localValue1.as(localValue2);
      } catch (Exception localValue4) {
         return null;
      }
   }

   private Object property(Object localValue1, String localValue2, String localValue3) {
      try {
         Method localValue9 = localValue1.getClass().getMethod(localValue3);
         return localValue9.invoke(localValue1);
      } catch (Exception localValue7) {
         try {
            Method localValue8 = localValue1.getClass().getMethod(localValue2);
            return localValue8.invoke(localValue1);
         } catch (Exception localValue6) {
            try {
               Field localValue4 = localValue1.getClass().getField(localValue2);
               return localValue4.get(localValue1);
            } catch (Exception localValue5) {
               return null;
            }
         }
      }
   }

   private RotationBehavior parseCorrection(Object localValue1, RotationBehavior localValue2) {
      if (localValue1 == null) {
         return localValue2;
      } else if (localValue1 instanceof RotationBehavior localValue5) {
         return localValue5;
      } else {
         if (localValue1 instanceof PyObject localValue3) {
            RotationBehavior localValue4 = this.pyAs(localValue3, RotationBehavior.class);
            if (localValue4 != null) {
               return localValue4;
            }
         }

         return PyRotations.parseCorrection(String.valueOf(localValue1));
      }
   }

   private RotationPriority parsePriority(Object localValue1, RotationPriority localValue2) {
      if (localValue1 == null) {
         return localValue2;
      } else if (localValue1 instanceof RotationPriority localValue5) {
         return localValue5;
      } else {
         if (localValue1 instanceof PyObject localValue3) {
            RotationPriority localValue4 = this.pyAs(localValue3, RotationPriority.class);
            if (localValue4 != null) {
               return localValue4;
            }
         }

         return PyRotations.parsePriority(String.valueOf(localValue1));
      }
   }

   private float asFloat(Object localValue1, String localValue2) {
      if (localValue1 instanceof Number localValue10) {
         return localValue10.floatValue();
      } else {
         if (localValue1 instanceof PyObject localValue3) {
            Number localValue4 = this.pyAs(localValue3, Number.class);
            if (localValue4 != null) {
               return localValue4.floatValue();
            }

            Double localValue5 = this.pyAs(localValue3, Double.class);
            if (localValue5 != null) {
               return localValue5.floatValue();
            }

            Float localValue6 = this.pyAs(localValue3, Float.class);
            if (localValue6 != null) {
               return localValue6;
            }

            Integer localValue7 = this.pyAs(localValue3, Integer.class);
            if (localValue7 != null) {
               return localValue7.floatValue();
            }

            String localValue8 = this.pyAs(localValue3, String.class);
            if (localValue8 != null) {
               return Float.parseFloat(localValue8);
            }
         }

         if (localValue1 instanceof String localValue9) {
            return Float.parseFloat(localValue9);
         } else if (localValue1 == null) {
            throw new IllegalArgumentException(localValue2 + " is required");
         } else {
            throw new IllegalArgumentException(localValue2 + " must be a number");
         }
      }
   }

   private float asFloat(Object localValue1, float localValue2) {
      return localValue1 == null ? localValue2 : this.asFloat(localValue1, "rotation option");
   }

   private boolean parseCorrectGcd(Map<?, ?> localValue1) {
      Object localValue2 = this.first(localValue1, "rawGcd", "raw_gcd");
      if (localValue2 != null) {
         return !this.asBoolean(localValue2);
      } else {
         Object localValue3 = this.first(localValue1, "correctGcd", "correct_gcd", "vanillaGcd", "vanilla_gcd", "handlerGcd", "handler_gcd");
         return localValue3 == null || this.asBoolean(localValue3);
      }
   }

   private boolean asBoolean(Object localValue1) {
      if (localValue1 instanceof Boolean localValue5) {
         return localValue5;
      } else {
         if (localValue1 instanceof PyObject localValue2) {
            Boolean localValue3 = this.pyAs(localValue2, Boolean.class);
            if (localValue3 != null) {
               return localValue3;
            }

            String localValue4 = this.pyAs(localValue2, String.class);
            if (localValue4 != null) {
               return Boolean.parseBoolean(localValue4);
            }
         }

         return Boolean.parseBoolean(String.valueOf(localValue1));
      }
   }

   private void closeQuietly(PyObject localValue1) {
      try {
         localValue1.close();
      } catch (Exception localValue3) {
      }
   }

   @Override
   public void attack() {
      this.invoke(this.attackFn, "attack");
   }

   @Override
   public void targetNull() {
      this.invoke(this.targetNullFn, "target_null");
   }

   @Override
   public void update() {
      this.invoke(this.updateFn, "update");
   }

   @Override
   public boolean canAttack() {
      if (this.canAttackFn != null && !this.errored && ScriptInternal085.internalMethod08724()) {
         try {
            boolean localValue7;
            try (AutoCloseable localValue1 = ScriptInternal083.internalMethod02561(this.owner)) {
               localValue7 = !(this.canAttackFn.call(new Object[0]) instanceof Boolean localValue3 && !localValue3);
            }

            return localValue7;
         } catch (Exception localValue6) {
            this.fail("can_attack", localValue6);
            return true;
         }
      } else {
         return true;
      }
   }

   private void invoke(PyCallable localValue1, String localValue2) {
      if (localValue1 != null && !this.errored && ScriptInternal085.internalMethod08724()) {
         try (AutoCloseable localValue3 = ScriptInternal083.internalMethod02561(this.owner)) {
            localValue1.call(new Object[0]);
         } catch (Exception localValue8) {
            this.fail(localValue2, localValue8);
         }
      }
   }

   private void fail(String localValue1, Exception localValue2) {
      this.errored = true;
      String localValue3 = localValue2.getMessage() == null ? localValue2.getClass().getSimpleName() : localValue2.getMessage();
      ClientMessages.internalMethod09025(Text.of("[rotation:" + this.getName() + "] " + localValue1 + ": " + localValue3));
      RockstarClient.internalField0572
         .error(
            "[PyRotation] \u043e\u0448\u0438\u0431\u043a\u0430 \u0432 '"
               + this.getName()
               + "' ("
               + localValue1
               + "), \u043e\u0442\u043a\u0430\u0442 \u043d\u0430 \u0441\u0442\u0430\u043d\u0434\u0430\u0440\u0442\u043d\u0443\u044e \u043d\u0430\u0432\u043e\u0434\u043a\u0443",
            localValue2
         );
   }

   static final class InternalType0452 {
      private final Rotation rotation;
      private final RotationBehavior moveCorrection;
      private final float yawSpeed;
      private final float pitchSpeed;
      private final float returnSpeed;
      private final RotationPriority priority;
      private final boolean correctGcd;

      InternalType0452(Rotation localValue1, RotationBehavior localValue2, float localValue3, float localValue4, float localValue5, RotationPriority localValue6, boolean localValue7) {
         this.rotation = localValue1;
         this.moveCorrection = localValue2;
         this.yawSpeed = localValue3;
         this.pitchSpeed = localValue4;
         this.returnSpeed = localValue5;
         this.priority = localValue6;
         this.correctGcd = localValue7;
      }

      @Override
      public final String toString() {
         return "InternalType0452[rotation=" + this.rotation() + ", moveCorrection=" + this.moveCorrection() + ", yawSpeed=" + this.yawSpeed() + ", pitchSpeed=" + this.pitchSpeed() + ", returnSpeed=" + this.returnSpeed() + ", priority=" + this.priority() + ", correctGcd=" + this.correctGcd() + "]";
      }

      @Override
      public final int hashCode() {
         int result = 0;
         result = 31 * result + java.util.Objects.hashCode(this.rotation());
         result = 31 * result + java.util.Objects.hashCode(this.moveCorrection());
         result = 31 * result + java.util.Objects.hashCode(this.yawSpeed());
         result = 31 * result + java.util.Objects.hashCode(this.pitchSpeed());
         result = 31 * result + java.util.Objects.hashCode(this.returnSpeed());
         result = 31 * result + java.util.Objects.hashCode(this.priority());
         result = 31 * result + java.util.Objects.hashCode(this.correctGcd());
         return result;
      }

      @Override
      public final boolean equals(Object localValue1) {
         if (this == localValue1) return true;
         if (localValue1 == null || getClass() != localValue1.getClass()) return false;
         PyRotationMode.InternalType0452 other = (PyRotationMode.InternalType0452) localValue1;
         return java.util.Objects.equals(this.rotation(), other.rotation())
            && java.util.Objects.equals(this.moveCorrection(), other.moveCorrection())
            && java.util.Objects.equals(this.yawSpeed(), other.yawSpeed())
            && java.util.Objects.equals(this.pitchSpeed(), other.pitchSpeed())
            && java.util.Objects.equals(this.returnSpeed(), other.returnSpeed())
            && java.util.Objects.equals(this.priority(), other.priority())
            && java.util.Objects.equals(this.correctGcd(), other.correctGcd());
      }

      public Rotation rotation() {
         return this.rotation;
      }

      public RotationBehavior moveCorrection() {
         return this.moveCorrection;
      }

      public float yawSpeed() {
         return this.yawSpeed;
      }

      public float pitchSpeed() {
         return this.pitchSpeed;
      }

      public float returnSpeed() {
         return this.returnSpeed;
      }

      public RotationPriority priority() {
         return this.priority;
      }

      public boolean correctGcd() {
         return this.correctGcd;
      }
   }
}
