package rockstar.modules.combat;

import rockstar.client.util.*;
import rockstar.client.setting.*;
import rockstar.client.server.*;
import rockstar.client.rotation.*;
import rockstar.client.module.*;
import rockstar.client.inventory.*;
import rockstar.client.i18n.*;
import rockstar.client.event.*;
import rockstar.client.internal.script.*;
import rockstar.client.internal.rotation.*;
import rockstar.client.internal.inventory.*;
import rockstar.client.internal.game.*;
import rockstar.client.internal.core.*;
import rockstar.client.*;
import rockstar.modules.movement.AirStuckModule;
import rockstar.client.module.Module;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import lombok.Generated;
import moscow.rockstar.mixin.accessors.ItemCooldownEntryAccessor;
import moscow.rockstar.mixin.accessors.ItemCooldownManagerAccessor;
import net.minecraft.block.Block;
import net.minecraft.block.DoorBlock;
import net.minecraft.block.TrapdoorBlock;
import net.minecraft.client.gui.screen.ingame.InventoryScreen;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Items;
import net.minecraft.item.consume.UseAction;
import net.minecraft.network.packet.c2s.play.PlayerActionC2SPacket;
import net.minecraft.network.packet.c2s.play.PlayerInteractItemC2SPacket;
import net.minecraft.network.packet.c2s.play.PlayerActionC2SPacket.Action;
import net.minecraft.util.Hand;
import net.minecraft.util.Identifier;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.HitResult.Type;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.RaycastContext;
import net.minecraft.world.RaycastContext.FluidHandling;
import net.minecraft.world.RaycastContext.ShapeType;
import pyrock.events.game.EntityJumpEvent;

@ModuleInfo(
   name = "Aura",
   category = ModuleCategory.COMBAT,
   internalMethod09633 = "modules.descriptions.aura"
)
public class AuraModule extends Module {
   private SliderSetting internalField0383;
   private SliderSetting internalField0382;
   private MultiSelectSetting internalField0675;
   private MultiSelectSetting.InternalType0091 internalField0245;
   private MultiSelectSetting.InternalType0091 internalField0244;
   private MultiSelectSetting.InternalType0091 internalField1075;
   private MultiSelectSetting.InternalType0091 internalField1074;
   private MultiSelectSetting.InternalType0091 internalField1073;
   private MultiSelectSetting.InternalType0091 internalField1072;
   private MultiSelectSetting.InternalType0091 internalField1491;
   private ModeSetting internalField0668;
   private ModeSetting.InternalType0088 internalField0237;
   private ModeSetting.InternalType0088 internalField0238;
   private ModeSetting.InternalType0088 internalField1066;
   private ModeSetting internalField0669;
   private ModeSetting.InternalType0088 internalField1067;
   private ModeSetting.InternalType0088 internalField1068;
   private ModeSetting internalField1272;
   private ModeSetting.InternalType0088 internalField1065;
   private ModeSetting.InternalType0088 internalField1480;
   private ModeSetting.InternalType0088 internalField1481;
   private ModeSetting internalField1269;
   private ModeSetting.InternalType0088 internalField1483;
   private ModeSetting.InternalType0088 internalField1485;
   private ModeSetting.InternalType0088 internalField1484;
   private ModeSetting.InternalType0088 internalField1482;
   private BooleanSetting internalField0650;
   private BooleanSetting internalField0651;
   private ModeSetting internalField1270;
   private ModeSetting.InternalType0088 internalField1479;
   private ModeSetting.InternalType0088 internalField1478;
   private RangeSetting internalField0672;
   private BooleanSetting internalField1261;
   private BooleanSetting internalField1263;
   private BooleanSetting internalField1262;
   private BooleanSetting internalField1264;
   private BooleanSetting internalField1587;
   private BooleanSetting internalField1590;
   private BooleanSetting internalField1594;
   private ModeSetting internalField1271;
   private ModeSetting.InternalType0088 internalField1749;
   private ModeSetting.InternalType0088 internalField1761;
   private ModeSetting.InternalType0088 internalField1751;
   private ModeSetting.InternalType0088 internalField1752;
   private ModeSetting.InternalType0088 internalField1753;
   private ModeSetting internalField1596;
   private ModeSetting.InternalType0088 internalField1754;
   private ModeSetting.InternalType0088 internalField1755;
   private ModeSetting.InternalType0088 internalField1750;
   private ModeSetting internalField1595;
   private ModeSetting.InternalType0088 internalField1756;
   private ModeSetting.InternalType0088 internalField1757;
   private ModeSetting.InternalType0088 internalField1758;
   private MultiSelectSetting internalField0674;
   private MultiSelectSetting.InternalType0091 internalField1488;
   private MultiSelectSetting.InternalType0091 internalField1490;
   private MultiSelectSetting.InternalType0091 internalField1489;
   private MultiSelectSetting.InternalType0091 internalField1493;
   private MultiSelectSetting.InternalType0091 internalField1487;
   private Stopwatch internalField0519;
   private long internalField0229;
   private float internalField0205;
   boolean internalField0277;
   boolean internalField0276;
   boolean internalField1099;
   int internalField0227;
   private final ScriptInternal039 internalField0306 = new ScriptInternal039();
   private RotationInternal008 internalField0308;
   private int internalField0228;
   private Rotation internalField0118;
   private boolean internalField1100;
   private boolean internalField1102;
   private int internalField1053 = -1;
   private boolean internalField1101;
   private static final float internalField0206 = 1.5F;
   private static final int internalField1055 = 4;
   private static final long internalField0230 = 200L;
   private float internalField1048 = MathUtils.internalMethod07919(0.0F, 1.0F);
   private long internalField1059;
   private final Map<String, Integer> internalField0543 = new LinkedHashMap<>();
   private final EventListener<EntityJumpEvent> internalField0157 = localValue1 -> {
      if (internalField0149.player == localValue1.getEntity()) {
         if (this.internalField1596.internalMethod06103(this.internalField1755)
            && internalField0149.player.isOnGround()
            && LegacyItemTypes.isSword(internalField0149.player.getMainHandStack())) {
            LivingEntity localValue2 = RockstarClient.getInstance().internalMethod04463().internalMethod01783();
            if (RotationInternal012.internalMethod02421(internalField0149.player).internalMethod01002(CombatUtils.internalMethod03105(localValue2), 40, true) > 10) {
               localValue1.cancel();
            }
         }
      }
   };

   public AuraModule() {
      this.internalMethod09753();
   }

   private void internalMethod09753() {
      this.internalField0669 = new ModeSetting(this, "modules.settings.aura.rotationMode");
      this.internalField1068 = new ModeSetting.InternalType0088(this.internalField0669, "modules.settings.aura.noRotation");
      this.internalField1067 = new RotationInternal006(this.internalField0669).select();
      new RotationInternal004(this.internalField0669);
      this.internalField0308 = new RotationInternal008(this.internalField0669);
      this.internalField1272 = new ModeSetting(
         this, "modules.settings.aura.returnMode", () -> this.internalField0669.internalMethod06103(this.internalField1068)
      );
      this.internalField1065 = new ModeSetting.InternalType0088(this.internalField1272, "modules.settings.aura.returnMode.none");
      this.internalField1480 = new ModeSetting.InternalType0088(this.internalField1272, "modules.settings.aura.returnMode.smooth").select();
      this.internalField1481 = new ModeSetting.InternalType0088(this.internalField1272, "modules.settings.aura.returnMode.camera");
      this.internalField0383 = new SliderSetting(this, "modules.settings.aura.attackDistance")
         .internalMethod05900(0.1F)
         .internalMethod02732(6.0F)
         .internalMethod08673(0.1F)
         .internalMethod08074(3.0F)
         .internalMethod05660(localValue0 -> " %s".formatted(LanguageManager.internalMethod07214("block")) + TextUtils.internalMethod05759(localValue0))
         .internalMethod04966(localValue1 -> {
            if (this.internalField0382 != null && this.internalField0382.internalMethod08576() < localValue1) {
               this.internalField0382.internalMethod08074(localValue1);
            }

            return localValue1;
         });
      this.internalField0382 = new SliderSetting(this, "modules.settings.aura.aimDistance")
         .internalMethod05900(0.1F)
         .internalMethod02732(9.0F)
         .internalMethod08673(0.1F)
         .internalMethod08074(3.0F)
         .internalMethod05660(localValue0 -> " %s".formatted(LanguageManager.internalMethod07214("block")) + TextUtils.internalMethod05759(localValue0))
         .internalMethod04966(localValue1 -> this.internalField0383 == null ? localValue1 : Math.max(this.internalField0383.internalMethod08576(), localValue1));
      this.internalField1261 = new BooleanSetting(this, "modules.settings.aura.onlyCrits").internalMethod06630();
      this.internalField1263 = new BooleanSetting(this, "modules.settings.aura.smart_criticals", () -> !this.internalField1261.internalMethod04496());
      this.internalField1271 = new ModeSetting(this, "modules.settings.aura.walls");
      this.internalField1749 = new ModeSetting.InternalType0088(this.internalField1271, "modules.settings.aura.walls.none").select();
      this.internalField1761 = new ModeSetting.InternalType0088(this.internalField1271, "modules.settings.aura.walls.all");
      this.internalField1751 = new ModeSetting.InternalType0088(this.internalField1271, "modules.settings.aura.walls.doors");
      this.internalField1752 = new ModeSetting.InternalType0088(this.internalField1271, "modules.settings.aura.walls.rw");
      this.internalField1753 = new ModeSetting.InternalType0088(this.internalField1271, "modules.settings.aura.walls.ft");
      this.internalField1262 = new BooleanSetting(this, "modules.settings.aura.rayTrace").internalMethod06630();
      this.internalField1590 = new BooleanSetting(this, "modules.settings.aura.targeting").internalMethod06630();
      this.internalField1264 = new BooleanSetting(this, "modules.settings.aura.onlyWeapon");
      this.internalField1587 = new BooleanSetting(this, "modules.settings.aura.auto_mace", "modules.settings.aura.auto_mace.description");
      this.internalField1594 = new BooleanSetting(this, "modules.settings.aura.no_hit_inv");
      this.internalField0675 = new MultiSelectSetting(this, "modules.settings.aura.targets");
      this.internalField0245 = new MultiSelectSetting.InternalType0091(this.internalField0675, "modules.settings.aura.targets.players").select();
      this.internalField0244 = new MultiSelectSetting.InternalType0091(this.internalField0675, "modules.settings.aura.targets.animals").select();
      this.internalField1075 = new MultiSelectSetting.InternalType0091(this.internalField0675, "modules.settings.aura.targets.mobs").select();
      this.internalField1074 = new MultiSelectSetting.InternalType0091(this.internalField0675, "modules.settings.aura.targets.invisibles").select();
      this.internalField1073 = new MultiSelectSetting.InternalType0091(this.internalField0675, "modules.settings.aura.targets.nakedPlayers").select();
      this.internalField1491 = new MultiSelectSetting.InternalType0091(this.internalField0675, "modules.settings.aura.targets.rockUsers");
      this.internalField1072 = new MultiSelectSetting.InternalType0091(this.internalField0675, "modules.settings.aura.targets.friends");
      this.internalField0668 = new ModeSetting(this, "modules.settings.aura.sorting");
      this.internalField0237 = new ModeSetting.InternalType0088(this.internalField0668, "modules.settings.aura.distanceSorting").select();
      this.internalField0238 = new ModeSetting.InternalType0088(this.internalField0668, "modules.settings.aura.healthSorting");
      this.internalField1066 = new ModeSetting.InternalType0088(this.internalField0668, "modules.settings.aura.fovSorting");
      this.internalField1269 = new ModeSetting(this, "modules.settings.aura.moveCorrectionMode");
      this.internalField1483 = new ModeSetting.InternalType0088(this.internalField1269, "modules.settings.aura.noMoveCorrection");
      this.internalField1485 = new ModeSetting.InternalType0088(this.internalField1269, "modules.settings.aura.directMoveCorrection");
      this.internalField1484 = new ModeSetting.InternalType0088(this.internalField1269, "modules.settings.aura.silentMoveCorrection").select();
      this.internalField1482 = new ModeSetting.InternalType0088(this.internalField1269, "modules.settings.aura.targeted_move_correction");
      this.internalField0650 = new BooleanSetting(
         this, "modules.settings.aura.force_targeted_ranged", () -> this.internalField1269.internalMethod06103(this.internalField1482)
      );
      this.internalField0651 = new BooleanSetting(
         this,
         "modules.settings.aura.force_behind_targeted",
         () -> !this.internalField0650.internalMethod04496() || this.internalField1269.internalMethod06103(this.internalField1482)
      );
      this.internalField1270 = new ModeSetting(this, "modules.settings.aura.styleAttack");
      this.internalField1479 = new ModeSetting.InternalType0088(this.internalField1270, "1.8");
      this.internalField1478 = new ModeSetting.InternalType0088(this.internalField1270, "1.9").select();
      this.internalField0672 = new RangeSetting(this, "modules.settings.aura.cps_limiter", this.internalField1478::isSelected)
         .internalMethod08834(1.0F)
         .internalMethod08219(20.0F)
         .internalMethod08853(1.0F)
         .internalMethod01407(8.0F)
         .internalMethod06328(12.0F);
      this.internalField1596 = new ModeSetting(this, "modules.settings.aura.crit_calc");
      this.internalField1754 = new ModeSetting.InternalType0088(this.internalField1596, "modules.settings.aura.crit_calc.old").select();
      this.internalField1755 = new ModeSetting.InternalType0088(this.internalField1596, "modules.settings.aura.crit_calc.new");
      this.internalField1750 = new ModeSetting.InternalType0088(this.internalField1596, "modules.settings.aura.crit_calc.air");
      this.internalField1595 = new ModeSetting(this, "modules.settings.aura.sprint_reset");
      this.internalField1756 = new ModeSetting.InternalType0088(this.internalField1595, "modules.settings.aura.sprint_reset.smart");
      this.internalField1757 = new ModeSetting.InternalType0088(this.internalField1595, "modules.settings.aura.sprint_reset.normal");
      this.internalField1758 = new ModeSetting.InternalType0088(this.internalField1595, "modules.settings.aura.sprint_reset.packet");
      this.internalField1756.select();
      this.internalField0674 = new MultiSelectSetting(this, "modules.settings.aura.utilities");
      this.internalField1488 = new MultiSelectSetting.InternalType0091(this.internalField0674, "modules.settings.aura.resolver");
      this.internalField1490 = new MultiSelectSetting.InternalType0091(this.internalField0674, "modules.settings.aura.useHit");
      this.internalField1487 = new MultiSelectSetting.InternalType0091(
         this.internalField0674, "modules.settings.aura.no_teammates_1_8", () -> !this.internalField1479.isSelected()
      );
      this.internalField1489 = new MultiSelectSetting.InternalType0091(this.internalField0674, "modules.settings.aura.sync");
      this.internalField1493 = new MultiSelectSetting.InternalType0091(this.internalField0674, "modules.settings.aura.sync_tps");
      this.internalField0519 = new Stopwatch();
   }

   public GameInternal023 internalMethod05948() {
      if (this.internalField1271 == null || this.internalField1271.internalMethod06103(this.internalField1749)) {
         return GameInternal023.internalField0305;
      } else if (this.internalField1271.internalMethod06103(this.internalField1761)) {
         return GameInternal023.internalField0304;
      } else if (this.internalField1271.internalMethod06103(this.internalField1751)) {
         return GameInternal023.internalField1119;
      } else if (this.internalField1271.internalMethod06103(this.internalField1753)) {
         return GameInternal023.internalField1117;
      } else {
         return this.internalField1271.internalMethod06103(this.internalField1752) ? GameInternal023.internalField1118 : GameInternal023.internalField0305;
      }
   }

   @Override
   public void internalMethod08229() {
      if (this.internalField0382.internalMethod08576() < this.internalField0383.internalMethod08576()) {
         this.internalField0382.internalMethod08074(this.internalField0383.internalMethod08576());
      }

      if (internalField0149.player != null) {
         if (this.internalField0669.internalMethod07418() instanceof RotationInternal005 localValue1) {
            localValue1.update();
         }

         ElytraTargetModule localValue10 = RockstarClient.getInstance().getModuleManager().getModule(ElytraTargetModule.class);
         boolean localValue11 = localValue10.isEnabled();
         float localValue3 = localValue11 ? localValue10.internalMethod08019().internalMethod08576() : Math.max(this.internalField0382.internalMethod08576(), this.internalMethod05568());
         InventoryInternal024.InternalType0309 localValue4 = new InventoryInternal024.InternalType0309()
            .internalMethod00547(this.internalField0245.isSelected())
            .internalMethod06455(!localValue11 && this.internalField0244.isSelected())
            .internalMethod08543(!localValue11 && this.internalField1075.isSelected())
            .internalMethod07990(this.internalField1074.isSelected())
            .internalMethod09114(this.internalField1073.isSelected())
            .internalMethod09525(this.internalField1072.isSelected())
            .internalMethod08126(this.internalField1491.isSelected())
            .internalMethod09220(this.internalField1487.isSelected() && this.internalField1479.isSelected())
            .internalMethod03468(localValue3);
         if (localValue11 || this.internalField0668.internalMethod06103(this.internalField0237)) {
            localValue4.internalMethod04109(InventoryInternal023.internalField0757);
         } else if (this.internalField0668.internalMethod06103(this.internalField0238)) {
            localValue4.internalMethod04109(InventoryInternal023.internalField0758);
         } else if (this.internalField0668.internalMethod06103(this.internalField1066)) {
            localValue4.internalMethod04109(InventoryInternal023.internalField1302);
         }

         InventoryInternal024 localValue5 = localValue4.internalMethod03528();
         LivingEntity localValue7 = RockstarClient.getInstance().internalMethod04463().internalMethod04526() instanceof LivingEntity localValue8 ? localValue8 : null;
         if (!this.internalField1590.internalMethod04496()
            || localValue7 == null
            || !localValue5.internalMethod05417(localValue7)
            || MathHelper.sqrt((float)internalField0149.player.squaredDistanceTo(RotationUtils.internalMethod01412(localValue7))) > localValue3
            || !internalField0149.world.hasEntity(localValue7)
            || !localValue7.isAlive()
            || AntiBotModule.internalMethod07596(localValue7)) {
            RockstarClient.getInstance().internalMethod04463().internalMethod05014(localValue5);
            localValue7 = RockstarClient.getInstance().internalMethod04463().internalMethod04526() instanceof LivingEntity localValue13 ? localValue13 : null;
         }

         if (localValue7 != null) {
            this.internalMethod08989(localValue7);
            this.internalField0276 = false;

            for (PlayerEntity localValue9 : internalField0149.world.getPlayers()) {
               if (internalField0149.player.distanceTo(localValue9) < 4.0F
                  && RockstarClient.getInstance().internalMethod03375().internalMethod00380(localValue9.getNameForScoreboard())) {
                  this.internalField0276 = true;
               }
            }

            this.internalMethod09151(localValue7);
            if (this.internalMethod09359()) {
               this.internalField0543
                  .merge("\u0441\u043f\u0440\u0438\u043d\u0442-\u0440\u0435\u0441\u0435\u0442 (\u0434\u043e \u0443\u0434\u0430\u0440\u0430)", 1, Integer::sum);
               return;
            }

            if (this.internalMethod05236(localValue7, true)) {
               if (this.internalMethod07802(localValue7)) {
                  this.internalField0543
                     .merge(
                        "\u0441\u043f\u0440\u0438\u043d\u0442-\u0440\u0435\u0441\u0435\u0442 (\u0432\u043c\u0435\u0441\u0442\u043e \u0443\u0434\u0430\u0440\u0430)",
                        1,
                        Integer::sum
                     );
                  return;
               }

               this.internalField0543.merge("\u2605 \u0423\u0414\u0410\u0420 \u0412\u042b\u041f\u041e\u041b\u041d\u0415\u041d", 1, Integer::sum);
               this.internalMethod05275(localValue7);
            }
         } else {
            CoreInternal060.internalField0006.internalMethod03169(this);
            this.internalMethod09756();
            if (this.internalField0669.internalMethod07418() instanceof RotationInternal005 localValue15) {
               this.internalField1100 = false;
               localValue15.targetNull();
            }
         }
      }
   }

   public boolean internalMethod05236(LivingEntity localValue1, boolean localValue2) {
      if (!this.internalMethod09754()) {
         return this.internalMethod07004("\u043a\u0443\u043b\u0434\u0430\u0443\u043d \u0430\u0443\u0440\u044b");
      } else if (AntiBotModule.internalMethod07596(localValue1)) {
         return this.internalMethod07004("antibot");
      } else if (this.internalField1270.internalMethod06103(this.internalField1479)
         && this.internalField1487.isSelected()
         && localValue1 instanceof PlayerEntity localValue3
         && InventoryInternal024.internalMethod05465(internalField0149.player, localValue3)) {
         return this.internalMethod07004("\u0441\u043e\u044e\u0437\u043d\u0438\u043a");
      } else {
         CriticalsModule localValue8 = RockstarClient.getInstance().getModuleManager().getModule(CriticalsModule.class);
         if (localValue8.internalMethod09260() && !localValue8.internalMethod09913()) {
            return this.internalMethod07004("\u043c\u043e\u0434\u0443\u043b\u044c Criticals");
         } else if (this.internalField1264.internalMethod04496() && !GameUtils.internalMethod08354()) {
            return this.internalMethod07004("\u043d\u0435 \u043e\u0440\u0443\u0436\u0438\u0435 \u0432 \u0440\u0443\u043a\u0435");
         } else if (this.internalMethod09958()) {
            return this.internalMethod07004("\u0438\u0441\u043f\u043e\u043b\u044c\u0437\u0443\u0435\u0442\u0441\u044f \u043f\u0440\u0435\u0434\u043c\u0435\u0442");
         } else if (this.internalField0669.internalMethod07418() instanceof RotationInternal005 localValue4 && !localValue4.canAttack()) {
            return this.internalMethod07004("\u0440\u0435\u0436\u0438\u043c \u043d\u0435 \u0445\u043e\u0447\u0435\u0442 \u0431\u0438\u0442\u044c");
         } else if (internalField0149.currentScreen instanceof InventoryScreen && this.internalField1594.internalMethod04496()) {
            return this.internalMethod07004("\u043e\u0442\u043a\u0440\u044b\u0442 \u0438\u043d\u0432\u0435\u043d\u0442\u0430\u0440\u044c");
         } else if (this.internalField1489.isSelected() && internalField0149.player.hurtTime > 0 && this.internalField0276) {
            return this.internalMethod07004("sync \u043f\u043e hurtTime");
         } else if (!this.internalMethod05276(localValue1)) {
            return this.internalMethod07004("\u0434\u0430\u043b\u0435\u043a\u043e (attackDistance)");
         } else {
            if (this.internalMethod07941(localValue1) && CombatUtils.internalMethod06028() != null) {
               if (!this.internalMethod10083()) {
                  return this.internalMethod07004(
                     "\u043e\u0436\u0438\u0434\u0430\u0435\u043c \u0432\u044b\u0441\u043e\u0442\u0443 \u0434\u043b\u044f \u0431\u0443\u043b\u0430\u0432\u044b"
                  );
               }

               if (ServerUtils.internalMethod01786(KnownServer.internalField0578) && !this.internalMethod10084()) {
                  return this.internalMethod07004(
                     "\u043e\u0436\u0438\u0434\u0430\u0435\u043c \u0441\u0432\u0430\u043f \u043d\u0430 \u0431\u0443\u043b\u0430\u0432\u0443"
                  );
               }
            }

            ElytraTargetModule localValue9 = RockstarClient.getInstance().getModuleManager().getModule(ElytraTargetModule.class);
            boolean localValue10 = localValue9.isEnabled() && internalField0149.player.isGliding() && internalField0149.player.getVelocity().length() < 6.0;
            if (localValue10) {
               return true;
            } else if (!(this.internalField0669.internalMethod07418() instanceof RotationInternal004 localValue6 && localValue6.internalMethod04412())
               && !MathUtils.internalMethod04490(
                  this.internalMethod05568(),
                  RockstarClient.getInstance().internalMethod02368().internalMethod09074().internalMethod00169(),
                  RockstarClient.getInstance().internalMethod02368().internalMethod09074().internalMethod00171(),
                  internalField0149.player,
                  localValue1,
                  this.internalMethod05948()
               )
               && this.internalField1262.internalMethod04496()
               && localValue2
               && (this.internalField0306.internalMethod03184() == null || this.internalField0306.internalMethod05984() <= 1)
               && !this.internalField1099) {
               return this.internalMethod07004("\u0440\u0435\u0439\u0442\u0440\u0435\u0439\u0441 \u043d\u0435 \u043f\u0440\u043e\u0445\u043e\u0434\u0438\u0442");
            } else if (this.internalMethod09352() && this.internalMethod09152(localValue1) && !CombatUtils.internalMethod06040(localValue1, true)) {
               return this.internalMethod07004("\u0436\u0434\u0451\u043c \u043a\u0440\u0438\u0442");
            } else {
               this.internalField0543
                  .merge("\u043f\u0440\u043e\u0432\u0435\u0440\u043a\u0438 \u043f\u0440\u043e\u0439\u0434\u0435\u043d\u044b", 1, Integer::sum);
               return true;
            }
         }
      }
   }

   private boolean internalMethod07004(String localValue1) {
      this.internalField0543.merge(localValue1, 1, Integer::sum);
      return false;
   }

   public boolean internalMethod04187(LivingEntity localValue1) {
      return this.internalMethod04291(localValue1, false);
   }

   public boolean internalMethod04291(LivingEntity localValue1, boolean localValue2) {
      CriticalsModule localValue3 = RockstarClient.getInstance().getModuleManager().getModule(CriticalsModule.class);
      if (localValue3.internalMethod09260() && !localValue3.internalMethod09913()) {
         return false;
      } else if (this.internalField1264.internalMethod04496() && !GameUtils.internalMethod08354()) {
         return false;
      } else if (this.internalMethod09958()) {
         return false;
      } else if (this.internalField0669.internalMethod07418() instanceof RotationInternal005 localValue4 && !localValue4.canAttack()) {
         return false;
      } else if (internalField0149.currentScreen instanceof InventoryScreen && this.internalField1594.internalMethod04496()) {
         return false;
      } else if (this.internalField1489.isSelected() && internalField0149.player.hurtTime > 0 && this.internalField0276) {
         return false;
      } else {
         if (localValue2) {
            if (internalField0149.player
                  .getEyePos()
                  .add(0.0, -1.0, 0.0)
                  .distanceTo(RotationUtils.internalMethod01839(localValue1, CombatUtils.internalMethod01237(localValue1, this.internalField1488.isSelected())))
               > this.internalMethod05568()) {
               return false;
            }
         } else if (!this.internalMethod05276(localValue1)) {
            return false;
         }

         return !this.internalMethod09352() || !this.internalMethod09152(localValue1) || CombatUtils.internalMethod03106(localValue1);
      }
   }

   private boolean internalMethod09152(LivingEntity localValue1) {
      float localValue2 = this.internalMethod04184(localValue1);
      return localValue2 <= localValue1.getHealth();
   }

   public boolean internalMethod09754() {
      if (internalField0149.player == null) {
         return false;
      } else if (internalField0149.player.isSubmergedInWater() && ServerUtils.internalMethod08700()) {
         return this.internalMethod09284();
      } else {
         float localValue1 = this.internalMethod08807();
         float localValue2 = this.internalField1493.isSelected() ? Math.min(1.0F, 0.8F * localValue1) : 0.8F;
         boolean localValue3 = internalField0149.player.getAttackCooldownProgress(0.0F) >= localValue2;
         return this.internalField1270.internalMethod06103(this.internalField1479)
            ? this.internalField0519.internalMethod02365(this.internalMethod05569())
            : localValue3 && this.internalField0519.internalMethod02365(Math.round(500.0F * localValue1));
      }
   }

   private boolean internalMethod09284() {
      if (internalField0149.player.getAttackCooldownProgress(0.0F) < 1.0F) {
         this.internalField1059 = 0L;
         return false;
      } else {
         if (this.internalField1059 == 0L) {
            this.internalField1059 = System.currentTimeMillis();
         }

         long localValue1 = Math.round(Math.clamp(this.internalField1048, 0.0F, 1.0F) * 200.0F);
         return System.currentTimeMillis() - this.internalField1059 >= localValue1;
      }
   }

   public float internalMethod04184(LivingEntity localValue1) {
      return 0.0F;
   }

   private void internalMethod04186(LivingEntity localValue1) {
      if (this.internalMethod05948() == GameInternal023.internalField1118 && internalField0149.player != null && internalField0149.world != null && localValue1 != null) {
         Rotation localValue2 = this.internalField0669.internalMethod07418() instanceof RotationInternal004 localValue3 && localValue3.internalMethod04412()
            ? localValue3.internalMethod04941()
            : RotationUtils.internalMethod04766(localValue1, this);
         List localValue7 = this.internalMethod01334(localValue1, localValue2);
         if (!localValue7.isEmpty()) {
            for (BlockHitResult localValue5 : (Iterable<BlockHitResult>)(Iterable<?>)localValue7) {
               Direction localValue6 = localValue5.getSide();
               internalField0149.player.networkHandler.sendPacket(new PlayerActionC2SPacket(Action.STOP_DESTROY_BLOCK, localValue5.getBlockPos(), localValue6));
               internalField0149.player.networkHandler.sendPacket(new PlayerActionC2SPacket(Action.START_DESTROY_BLOCK, localValue5.getBlockPos(), localValue6));
            }
         }
      }
   }

   private List<BlockHitResult> internalMethod01334(LivingEntity localValue1, Rotation localValue2) {
      ArrayList localValue3 = new ArrayList();
      if (localValue1 != null && internalField0149.player != null && internalField0149.world != null) {
         float localValue4 = internalField0149.getRenderTickCounter().getTickProgress(false);
         Vec3d localValue5 = internalField0149.player.getCameraPosVec(localValue4);
         Vec3d localValue6 = MathUtils.internalMethod06554(localValue2.internalMethod00171(), localValue2.internalMethod00169());
         double localValue7 = localValue6.lengthSquared();
         if (localValue7 < 1.0E-8) {
            return localValue3;
         } else {
            Vec3d localValue9 = localValue6.multiply(1.0 / Math.sqrt(localValue7));
            double localValue10 = this.internalMethod05568();
            Vec3d localValue12 = localValue5.add(localValue9.multiply(localValue10));
            double localValue13 = internalMethod06749(localValue5, localValue9, localValue12, localValue10, localValue1);
            LinkedHashSet localValue15 = new LinkedHashSet();
            localValue15.add(localValue5);
            Vec3d localValue16 = internalMethod01410(localValue9);
            if (localValue16.lengthSquared() > 1.0E-8) {
               localValue16 = localValue16.normalize().multiply(0.09);
               localValue15.add(localValue5.add(localValue16));
               localValue15.add(localValue5.subtract(localValue16));
            }

            ArrayList localValue17 = new ArrayList();
            HashSet localValue18 = new HashSet();

            for (Vec3d localValue20 : (Iterable<Vec3d>)(Iterable<?>)localValue15) {
               Vec3d localValue21 = localValue20.add(localValue9.multiply(localValue10));

               for (BlockHitResult localValue23 : this.internalMethod01923(localValue20, localValue21, localValue9, localValue5, localValue13)) {
                  if (localValue18.add(localValue23.getBlockPos())) {
                     localValue17.add(localValue23);
                  }
               }
            }

            localValue17.sort(Comparator.comparingDouble(localValue2x -> ((net.minecraft.entity.Entity)localValue2x).getEntityPos().subtract(localValue5).dotProduct(localValue9)));
            localValue3.addAll(localValue17);
            return localValue3;
         }
      } else {
         return localValue3;
      }
   }

   private static Vec3d internalMethod01410(Vec3d localValue0) {
      Vec3d localValue1 = new Vec3d(localValue0.x, 0.0, localValue0.z);
      if (localValue1.lengthSquared() < 1.0E-8) {
         return Vec3d.ZERO;
      } else {
         localValue1 = localValue1.normalize();
         return new Vec3d(-localValue1.z, 0.0, localValue1.x);
      }
   }

   private static double internalMethod06749(Vec3d localValue0, Vec3d localValue1, Vec3d localValue2, double localValue3, LivingEntity localValue5) {
      Box localValue6 = localValue5.getBoundingBox();
      Optional localValue7 = localValue6.raycast(localValue0, localValue2);
      if (localValue7.isPresent()) {
         return ((Vec3d)localValue7.get()).subtract(localValue0).dotProduct(localValue1);
      } else {
         double localValue8 = localValue5.getEyePos().subtract(localValue0).dotProduct(localValue1);
         return localValue8 > 0.0 && localValue8 <= localValue3 ? localValue8 : localValue3;
      }
   }

   private List<BlockHitResult> internalMethod01923(Vec3d localValue1, Vec3d localValue2, Vec3d localValue3, Vec3d localValue4, double localValue5) {
      ArrayList localValue7 = new ArrayList();
      Vec3d localValue8 = localValue1;
      HashSet localValue9 = new HashSet();
      double localValue10 = 1.0E-4;

      for (int localValue12 = 0; localValue12 < 40; localValue12++) {
         BlockHitResult localValue13 = internalField0149.world.raycast(new RaycastContext(localValue8, localValue2, ShapeType.COLLIDER, FluidHandling.NONE, internalField0149.player));
         if (localValue13.getType() != Type.BLOCK) {
            break;
         }

         BlockHitResult localValue14 = localValue13;
         double localValue15 = localValue14.getPos().subtract(localValue4).dotProduct(localValue3);
         if (localValue15 >= localValue5 - 1.0E-4) {
            break;
         }

         Block localValue17 = internalField0149.world.getBlockState(localValue14.getBlockPos()).getBlock();
         if (!(localValue17 instanceof DoorBlock) && !(localValue17 instanceof TrapdoorBlock)) {
            BlockPos localValue18 = localValue14.getBlockPos();
            if (!localValue9.add(localValue18)) {
               localValue8 = localValue14.getPos().add(localValue3.multiply(0.02));
            } else {
               localValue7.add(localValue14);
               localValue8 = localValue14.getPos().add(localValue3.multiply(0.01));
            }
         } else {
            localValue8 = localValue14.getPos().add(localValue3.multiply(0.01));
         }
      }

      return localValue7;
   }

   private void internalMethod05275(LivingEntity localValue1) {
      if (internalField0149.interactionManager != null && internalField0149.player != null) {
         this.internalMethod04186(localValue1);
         Hand localValue2 = null;
         this.internalField0277 = this.internalMethod10093();
         if (this.internalField0277) {
            localValue2 = internalField0149.player.getActiveHand();
            internalField0149.player.networkHandler.sendPacket(new PlayerActionC2SPacket(Action.RELEASE_USE_ITEM, BlockPos.ORIGIN, Direction.DOWN));
         }

         if (CombatUtils.internalMethod04183(localValue1) && CombatUtils.internalMethod07758(localValue1)) {
            CombatUtils.internalMethod07972(localValue1);
         }

         HotbarSlot localValue3 = this.internalMethod07941(localValue1) && this.internalMethod10083() ? CombatUtils.internalMethod06028() : null;
         if (localValue3 != null && !ServerUtils.internalMethod01786(KnownServer.internalField0578)) {
            GameInternal040.internalMethod07593(localValue3, () -> internalField0149.interactionManager.attackEntity(internalField0149.player, localValue1));
         } else {
            internalField0149.interactionManager.attackEntity(internalField0149.player, localValue1);
         }

         CoreInternal060.internalField0006.internalMethod03169(this);
         internalField0149.player.swingHand(Hand.MAIN_HAND);
         if (this.internalField0277 && localValue2 != null) {
            Hand localValue4 = localValue2;
            internalField0149.interactionManager
               .sendSequencedPacket(
                  internalField0149.world,
                  localValue1x -> new PlayerInteractItemC2SPacket(
                     localValue4,
                     localValue1x,
                     RockstarClient.getInstance().internalMethod02368().internalMethod09074().internalMethod00169(),
                     RockstarClient.getInstance().internalMethod02368().internalMethod09074().internalMethod00171()
                  )
               );
         }

         if (this.internalField0669.internalMethod07418() instanceof RotationInternal005 localValue6) {
            localValue6.attack();
         }

         this.internalField0118 = new Rotation(MathUtils.internalMethod05368(5.0, 20.0), MathUtils.internalMethod05368(5.0, 10.0));
         this.internalField0519.internalMethod00701();
         this.internalField1048 = MathUtils.internalMethod07919(0.0F, 1.0F);
         this.internalField1059 = 0L;
         this.internalField0228++;
         this.internalField0205 = this.internalField0228 % 9 == 0 ? 0.08F : 0.0F;
      }
   }

   private void internalMethod09151(LivingEntity localValue1) {
      if (!this.internalField1264.internalMethod04496() || GameUtils.internalMethod08354()) {
         boolean localValue3 = this.internalField0650.internalMethod04496()
            && localValue1 != null
            && this.internalMethod09302(localValue1)
            && !this.internalField1269.internalMethod06103(this.internalField1482);
         this.internalField1100 = localValue3;
         RotationBehavior localValue2;
         if (this.internalField1269.internalMethod06103(this.internalField1484)) {
            localValue2 = RotationBehavior.internalField1003;
         } else if (this.internalField1269.internalMethod06103(this.internalField1485)) {
            localValue2 = RotationBehavior.internalField0114;
         } else if (!this.internalField1269.internalMethod06103(this.internalField1482) && !localValue3) {
            localValue2 = RotationBehavior.internalField0115;
         } else {
            localValue2 = RotationBehavior.internalField1423;
         }

         RotationManager localValue4 = RockstarClient.getInstance().internalMethod02368();
         if (this.internalField0669.internalMethod06103(this.internalField1068)) {
            if (localValue2 == RotationBehavior.internalField1423 && localValue1 != null) {
               localValue4.internalMethod00418(
                  localValue4.internalMethod07496(), RotationBehavior.internalField1423, 180.0F, 180.0F, 180.0F, RotationPriority.internalField1010
               );
            }
         } else {
            if (this.internalField0669.internalMethod07418() instanceof RotationInternal005 localValue5) {
               RotationRequest localValue9 = localValue4.internalMethod07551();
               localValue5.rotate(localValue4, this.internalMethod05568(), this.internalMethod05948().internalMethod04282(), this.internalField1262.internalMethod04496(), localValue2, localValue1);
               RotationRequest localValue7 = localValue4.internalMethod07551();
               if (localValue7 != null && localValue7 != localValue9) {
                  localValue7.internalMethod03988(this.internalMethod05678());
                  localValue7.internalMethod01624(localValue5 instanceof RotationInternal008 localValue8 ? localValue8::internalMethod05104 : null);
               }
            }
         }
      }
   }

   private RotationResetMode internalMethod05678() {
      if (this.internalField1272.internalMethod06103(this.internalField1065)) {
         return RotationResetMode.internalField0117;
      } else {
         return this.internalField1272.internalMethod06103(this.internalField1481) ? RotationResetMode.internalField1005 : RotationResetMode.internalField0116;
      }
   }

   private boolean internalMethod09352() {
      return this.internalField1263 != null && this.internalField1263.internalMethod04496() && !this.internalField1750.isSelected()
         ? internalField0149.options != null && internalField0149.options.jumpKey.isPressed() || !internalField0149.player.isOnGround()
         : this.internalField1261.internalMethod04496();
   }

   private boolean internalMethod09359() {
      if (internalField0149.player == null) {
         return false;
      } else if (GameInternal034.internalMethod03032()) {
         return true;
      } else if (this.internalMethod09352() && GameInternal033.internalMethod05020(internalField0149.player)) {
         GameInternal034.internalMethod04017(internalField0149.player);
         return true;
      } else {
         return false;
      }
   }

   private boolean internalMethod07802(LivingEntity localValue1) {
      boolean localValue2 = this.internalField1595.internalMethod06103(this.internalField1757);
      boolean localValue3 = this.internalField1595.internalMethod06103(this.internalField1758);
      if (RockstarClient.getInstance().getModuleManager().getModule(KnockbackTweaksModule.class).isEnabled()) {
         return false;
      } else if ((localValue2 || localValue3) && internalField0149.player != null) {
         if (GameInternal034.internalMethod03032() || GameInternal034.internalMethod06977(internalField0149.player)) {
            return true;
         } else if (!internalField0149.player.isSprinting()) {
            GameInternal034.internalMethod04865(internalField0149.player);
            return false;
         } else {
            GameInternal034.internalMethod06906(internalField0149.player, () -> this.internalMethod07801(localValue1), localValue3);
            return true;
         }
      } else {
         return false;
      }
   }

   private void internalMethod07801(LivingEntity localValue1) {
      if (this.isEnabled()
         && internalField0149.player != null
         && internalField0149.interactionManager != null
         && localValue1 != null
         && !localValue1.isRemoved()
         && localValue1.isAlive()) {
         if (this.internalMethod05236(localValue1, true)) {
            this.internalMethod05275(localValue1);
         }
      }
   }

   public boolean internalMethod09757() {
      if (!this.internalField1595.internalMethod06103(this.internalField1756)) {
         return false;
      } else if (RockstarClient.getInstance().getModuleManager().getModule(KnockbackTweaksModule.class).isEnabled()) {
         return false;
      } else {
         LivingEntity localValue2 = RockstarClient.getInstance().internalMethod04463().internalMethod04526() instanceof LivingEntity localValue3 ? localValue3 : null;
         if (localValue2 == null || internalField0149.player == null) {
            return false;
         } else if (!this.internalField1270.internalMethod06103(this.internalField1479) && !internalField0149.player.isSubmergedInWater()) {
            CriticalsModule localValue5 = RockstarClient.getInstance().getModuleManager().getModule(CriticalsModule.class);
            boolean localValue4 = localValue5.internalMethod09260()
                  && (localValue5.internalMethod09914() && this.internalField0519.internalMethod02365(500L) || internalField0149.player.isOnGround())
               || !internalField0149.player.isOnGround()
                  && RotationInternal012.internalMethod02421(internalField0149.player)
                     .internalMethod04604(
                        CombatUtils.internalMethod03105(localValue2),
                        !ServerUtils.internalMethod01786(KnownServer.internalField0578)
                              && !ServerUtils.internalMethod01786(KnownServer.internalField1567)
                              && !ServerUtils.internalMethod08700()
                           ? 1
                           : MathUtils.internalField0858.nextInt(3)
                     );
            return this.internalMethod09352()
               && this.internalMethod09152(localValue2)
               && (
                  localValue4
                     || CombatUtils.internalMethod06040(localValue2, true)
                     || !this.internalField0519
                        .internalMethod02365(
                           !ServerUtils.internalMethod01786(KnownServer.internalField0579) && !ServerUtils.internalMethod08700()
                              ? 50L
                              : (long)MathUtils.internalMethod07919(50.0F, 150.0F)
                        )
               );
         } else {
            return false;
         }
      }
   }

   public float internalMethod05568() {
      float localValue1 = RockstarClient.getInstance().getModuleManager().getModule(AirStuckModule.class).internalMethod02232();
      return localValue1 > 0.0F ? localValue1 : this.internalField0383.internalMethod08576();
   }

   public boolean internalMethod05276(LivingEntity localValue1) {
      return this.internalField0306.internalMethod05984() > 1 && this.internalField1750.isSelected()
         ? this.internalField0306.internalMethod03184().internalMethod00677().distanceTo(localValue1.getEntityPos()) < 6.0
         : internalField0149.player
               .getEyePos()
               .distanceTo(RotationUtils.internalMethod01839(localValue1, CombatUtils.internalMethod01237(localValue1, this.internalField1488.isSelected())))
            <= this.internalMethod05568();
   }

   @Override
   public void onEnable() {
      if (this.internalField0669.internalMethod07418() instanceof RotationInternal005 localValue1) {
         localValue1.enabled();
      }

      if (this.internalField1750.isSelected()) {
         this.internalField0306.internalMethod05985();
      }

      this.internalField1102 = false;
      this.internalMethod09756();
      super.onEnable();
   }

   @Override
   public void onDisable() {
      CoreInternal060.internalField0006.internalMethod01775(this);
      this.internalMethod09756();
      RockstarClient.getInstance().internalMethod04463().internalMethod07889();
      if (internalField0149.player != null) {
         GameInternal034.internalMethod04865(internalField0149.player);
      }

      if (this.internalField0308 != null) {
         this.internalField0308.targetNull();
      }

      this.internalField0306.internalMethod05988();
      super.onDisable();
   }

   private void internalMethod08989(LivingEntity localValue1) {
      if (ServerUtils.internalMethod01786(KnownServer.internalField0578) && this.internalMethod08990(localValue1)) {
         if (!CoreInternal060.internalField0006.internalMethod03170(this) && !internalField0149.player.getMainHandStack().isOf(Items.MACE)) {
            HotbarSlot localValue2 = CombatUtils.internalMethod06028();
            if (localValue2 != null && CoreInternal060.internalField0006.internalMethod05917(this, localValue2)) {
               this.internalField1053 = internalField0149.player.age;
               this.internalField1101 = internalField0149.player.getItemCooldownManager().isCoolingDown(localValue2.internalMethod03427());
            }
         } else {
            if (internalField0149.player.getItemCooldownManager().isCoolingDown(Items.MACE.getDefaultStack())) {
               this.internalField1101 = true;
            }
         }
      } else {
         CoreInternal060.internalField0006.internalMethod03169(this);
         this.internalMethod09756();
      }
   }

   private boolean internalMethod08990(LivingEntity localValue1) {
      if (!this.internalField1587.internalMethod04496()
         || localValue1 == null
         || internalField0149.player == null
         || internalField0149.player.isOnGround()
         || internalField0149.player.isGliding()
         || internalField0149.player.hasStatusEffect(StatusEffects.SLOW_FALLING)) {
         return false;
      } else if (this.internalMethod07941(localValue1)) {
         return true;
      } else {
         HotbarSlot localValue2 = CombatUtils.internalMethod06028();
         if (localValue2 == null) {
            return false;
         } else {
            int localValue3 = this.internalMethod04185(localValue1);
            return localValue3 >= 0 && this.internalMethod07571(localValue2) >= localValue3 - 4;
         }
      }
   }

   private int internalMethod04185(LivingEntity localValue1) {
      double localValue2 = internalField0149.player.getY();
      double localValue4 = internalField0149.player.getVelocity().y;
      double localValue6 = internalField0149.player.fallDistance;
      double localValue7 = localValue1.getBoundingBox().maxY;

      for (int localValue9 = 1; localValue9 <= 40; localValue9++) {
         localValue2 += localValue4;
         if (localValue4 < 0.0) {
            localValue6 -= (float)localValue4;
         } else {
            localValue6 = 0.0F;
         }

         if (localValue4 < 0.0 && localValue6 + Math.max(0.0, localValue2 - localValue7) > 1.5) {
            return localValue9;
         }

         if (localValue4 < 0.0 && localValue2 < localValue1.getBoundingBox().minY - 2.0) {
            return -1;
         }

         localValue4 = (localValue4 - 0.08) * 0.98;
      }

      return -1;
   }

   private int internalMethod07571(HotbarSlot localValue1) {
      if (!internalField0149.player.getItemCooldownManager().isCoolingDown(localValue1.internalMethod03427())) {
         return 0;
      } else {
         ItemCooldownManagerAccessor localValue2 = (ItemCooldownManagerAccessor)(Object)internalField0149.player.getItemCooldownManager();
         Identifier localValue3 = localValue2.rockstar$getGroup(localValue1.internalMethod03427());
         Object localValue4 = localValue2.rockstar$getEntries().get(localValue3);
         return localValue4 == null ? 0 : Math.max(0, ((ItemCooldownEntryAccessor)(Object)localValue4).rockstar$getEndTick() - localValue2.rockstar$getTick());
      }
   }

   private void internalMethod09756() {
      this.internalField1053 = -1;
      this.internalField1101 = false;
   }

   private boolean internalMethod07941(LivingEntity localValue1) {
      if (this.internalField1587.internalMethod04496()
         && localValue1 != null
         && internalField0149.player != null
         && !internalField0149.player.isOnGround()
         && !internalField0149.player.isGliding()
         && !internalField0149.player.hasStatusEffect(StatusEffects.SLOW_FALLING)) {
         double localValue2 = Math.max(0.0, internalField0149.player.getY() - localValue1.getBoundingBox().maxY);
         return internalField0149.player.fallDistance + localValue2 > 1.5;
      } else {
         return false;
      }
   }

   private boolean internalMethod10083() {
      return internalField0149.player.fallDistance > 1.5F && internalField0149.player.getVelocity().y < 0.0;
   }

   private boolean internalMethod10084() {
      return internalField0149.player.getMainHandStack().isOf(Items.MACE)
            && !internalField0149.player.getItemCooldownManager().isCoolingDown(internalField0149.player.getMainHandStack())
         ? this.internalField1053 < 0 || this.internalField1101 || internalField0149.player.age - this.internalField1053 >= 4
         : false;
   }

   private long internalMethod05569() {
      int localValue1 = (int)this.internalField0672.internalMethod06919();
      int localValue2 = (int)this.internalField0672.internalMethod07967();
      if (localValue1 > localValue2) {
         int localValue3 = localValue1;
         localValue1 = localValue2;
         localValue2 = localValue3;
      }

      int localValue4 = MathUtils.internalField0858.nextInt(localValue1, localValue2 + 1);
      return Math.max(1L, 1000L / localValue4);
   }

   private float internalMethod08807() {
      if (!this.internalField1493.isSelected()) {
         return 1.0F;
      } else {
         float localValue1 = RockstarClient.getInstance().internalMethod06191().internalMethod00956();
         if (localValue1 <= 0.0F || Float.isNaN(localValue1)) {
            return 1.0F;
         } else {
            return localValue1 >= 19.0F ? 1.0F : Math.max(25.0F / localValue1, 1.0F);
         }
      }
   }

   private boolean internalMethod10092() {
      if (internalField0149.player != null && internalField0149.player.isUsingItem()) {
         UseAction localValue1 = internalField0149.player.getActiveItem().getItem().getUseAction(internalField0149.player.getActiveItem());
         return localValue1 == UseAction.EAT || localValue1 == UseAction.DRINK;
      } else {
         return false;
      }
   }

   private boolean internalMethod10093() {
      if (internalField0149.player != null && internalField0149.player.isUsingItem()) {
         UseAction localValue1 = internalField0149.player.getActiveItem().getItem().getUseAction(internalField0149.player.getActiveItem());
         return localValue1 == UseAction.BLOCK;
      } else {
         return false;
      }
   }

   private boolean internalMethod09958() {
      if (internalField0149.player == null || !internalField0149.player.isUsingItem()) {
         return false;
      } else if (!this.internalField1490.isSelected()) {
         return false;
      } else {
         return this.internalMethod10092() ? true : internalField0149.player.getActiveHand() == Hand.OFF_HAND && !this.internalMethod10093();
      }
   }

   private boolean internalMethod09302(LivingEntity localValue1) {
      return localValue1.getMainHandStack().isOf(Items.CROSSBOW)
         || localValue1.getOffHandStack().isOf(Items.CROSSBOW)
         || localValue1.getMainHandStack().isOf(Items.TRIDENT)
         || localValue1.getOffHandStack().isOf(Items.TRIDENT);
   }

   private long internalMethod05572() {
      if (this.internalField1270.internalMethod06103(this.internalField1479)) {
         int localValue1 = (int)Math.max(this.internalField0672.internalMethod06919(), this.internalField0672.internalMethod07967());
         return Math.max(1L, 1000L / Math.max(1, localValue1));
      } else {
         return Math.round(500.0F * this.internalMethod08807());
      }
   }

   @Generated
   public SliderSetting internalMethod05151() {
      return this.internalField0383;
   }

   @Generated
   public ModeSetting internalMethod01895() {
      return this.internalField0669;
   }

   @Generated
   public BooleanSetting internalMethod01838() {
      return this.internalField0650;
   }

   @Generated
   public BooleanSetting internalMethod02517() {
      return this.internalField0651;
   }

   @Generated
   public ModeSetting.InternalType0088 internalMethod06308() {
      return this.internalField1479;
   }

   @Generated
   public ModeSetting.InternalType0088 internalMethod06505() {
      return this.internalField1478;
   }

   @Generated
   public RangeSetting internalMethod01896() {
      return this.internalField0672;
   }

   @Generated
   public BooleanSetting internalMethod08177() {
      return this.internalField1261;
   }

   @Generated
   public BooleanSetting internalMethod08312() {
      return this.internalField1263;
   }

   @Generated
   public BooleanSetting internalMethod09123() {
      return this.internalField1262;
   }

   @Generated
   public BooleanSetting internalMethod07659() {
      return this.internalField1264;
   }

   @Generated
   public BooleanSetting internalMethod09772() {
      return this.internalField1587;
   }

   @Generated
   public BooleanSetting internalMethod09210() {
      return this.internalField1590;
   }

   @Generated
   public BooleanSetting internalMethod09637() {
      return this.internalField1594;
   }

   @Generated
   public ModeSetting.InternalType0088 internalMethod07687() {
      return this.internalField1754;
   }

   @Generated
   public ModeSetting.InternalType0088 internalMethod07769() {
      return this.internalField1755;
   }

   @Generated
   public ModeSetting.InternalType0088 internalMethod08873() {
      return this.internalField1750;
   }

   @Generated
   public MultiSelectSetting.InternalType0091 internalMethod06377() {
      return this.internalField1488;
   }

   @Generated
   public MultiSelectSetting.InternalType0091 internalMethod06586() {
      return this.internalField1490;
   }

   @Generated
   public MultiSelectSetting.InternalType0091 internalMethod07725() {
      return this.internalField1489;
   }

   @Generated
   public MultiSelectSetting.InternalType0091 internalMethod07783() {
      return this.internalField1493;
   }

   @Generated
   public MultiSelectSetting.InternalType0091 internalMethod08880() {
      return this.internalField1487;
   }

   @Generated
   public Stopwatch internalMethod00789() {
      return this.internalField0519;
   }

   @Generated
   public float internalMethod05571() {
      return this.internalField0205;
   }

   @Generated
   public ScriptInternal039 internalMethod05949() {
      return this.internalField0306;
   }

   @Generated
   public RotationInternal008 internalMethod05950() {
      return this.internalField0308;
   }

   @Generated
   public int internalMethod08808() {
      return this.internalField0228;
   }

   @Generated
   public Rotation internalMethod05679() {
      return this.internalField0118;
   }

   @Generated
   public void internalMethod03834(Rotation localValue1) {
      this.internalField0118 = localValue1;
   }

   @Generated
   public boolean internalMethod09280() {
      return this.internalField1100;
   }

   @Generated
   public Map<String, Integer> internalMethod05372() {
      return this.internalField0543;
   }
}
