package com.electrifiedded.starminer.basics.common;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.List;
import com.electrifiedded.starminer.api.Gravity;
import com.electrifiedded.starminer.api.GravityDirection;
import com.electrifiedded.starminer.basics.Port2Registry;
import com.electrifiedded.starminer.basics.block.bed.BlockStarBedHead;
import net.minecraft.entity.Entity;
import net.minecraft.entity.monster.EntityMob;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayer.SleepResult;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.event.entity.player.PlayerSleepInBedEvent;
import net.minecraftforge.event.ForgeEventFactory;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public class StarBedEvents {
   private static Field fieldSleeping;
   private static Field fieldSleepTimer;
   private static Method methodSetSize;
   private static Method methodSpawnShoulder;

   @SubscribeEvent
   public void handlePlayerSleepInBed(PlayerSleepInBedEvent event) {
      EntityPlayer player = event.getEntityPlayer();
      BlockPos pos = event.getPos();
      if (pos == null || !player.world.isBlockLoaded(pos)) {
         return;
      }

      if (player.world.getBlockState(pos).getBlock() == Port2Registry.StarBedHeadBlock) {
         event.setResult(this.sleepInBedAt(player, pos));
      }
   }

   private SleepResult sleepInBedAt(EntityPlayer player, BlockPos pos) {
      BlockStarBedHead block = (BlockStarBedHead)Port2Registry.StarBedHeadBlock;
      int gravDir = player.world.getBlockState(pos).getValue(BlockStarBedHead.GRAV);
      int connDir = block.getConnectionDirection(player.world, pos);
      if (connDir == -1) {
         return SleepResult.OTHER_PROBLEM;
      }

      if (!player.world.isRemote) {
         Gravity gravity = Gravity.getGravityProp(player);
         GravityDirection gd = gravity == null ? GravityDirection.upTOdown_YN : gravity.gravityDirection;
         int need;
         switch (gd) {
            case northTOsouth_ZP:
               need = 5;
               break;
            case southTOnorth_ZN:
               need = 4;
               break;
            case westTOeast_XP:
               need = 3;
               break;
            case eastTOwest_XN:
               need = 2;
               break;
            case downTOup_YP:
               need = 1;
               break;
            default:
               need = 0;
         }

         if (gravDir != need) {
            return SleepResult.OTHER_PROBLEM;
         }

         if (player.isPlayerSleeping() || !player.isEntityAlive()) {
            return SleepResult.OTHER_PROBLEM;
         }

         if (!player.world.provider.isSurfaceWorld()) {
            return SleepResult.NOT_POSSIBLE_HERE;
         }

         if (!ForgeEventFactory.fireSleepingTimeCheck(player, pos)) {
            return SleepResult.NOT_POSSIBLE_NOW;
         }

         if (Math.abs(player.posX - pos.getX()) > 3.0 || Math.abs(player.posY - pos.getY()) > 3.0 || Math.abs(player.posZ - pos.getZ()) > 3.0) {
            return SleepResult.TOO_FAR_AWAY;
         }

         double d0 = 8.0;
         double d1 = 5.0;
         List<EntityMob> list = player.world.getEntitiesWithinAABB(
            EntityMob.class,
            new AxisAlignedBB(pos.getX() - d0, pos.getY() - d1, pos.getZ() - d0, pos.getX() + d0, pos.getY() + d1, pos.getZ() + d0),
            mob -> mob.isPreventingPlayerRest(player)
         );
         if (!list.isEmpty()) {
            return SleepResult.NOT_SAFE;
         }
      }

      if (player.isRiding()) {
         player.dismountRidingEntity();
      }

      invoke(player, "spawnShoulderEntities", "func_192030_dh");
      setSize(player, 0.2F, 0.2F);
      BlockStarBedHead.setPositionForStarBedSleepingPlayer(player, pos, gravDir, connDir);
      setField(player, "sleeping", "field_71083_bS", true);
      setField(player, "sleepTimer", "field_71076_b", 0);
      player.bedLocation = pos;
      player.motionX = player.motionY = player.motionZ = 0.0;
      if (!player.world.isRemote) {
         player.world.updateAllPlayersSleepingFlag();
      }

      return SleepResult.OK;
   }

   private static void invoke(EntityPlayer player, String dev, String srg) {
      try {
         if (methodSpawnShoulder == null) {
            methodSpawnShoulder = find(EntityPlayer.class, dev, srg);
         }

         methodSpawnShoulder.invoke(player);
      } catch (Exception e) {
         e.printStackTrace();
      }
   }

   private static void setSize(EntityPlayer player, float w, float h) {
      try {
         if (methodSetSize == null) {
            methodSetSize = find(Entity.class, "setSize", "func_70105_a", float.class, float.class);
         }

         methodSetSize.invoke(player, w, h);
      } catch (Exception e) {
         e.printStackTrace();
      }
   }

   private static Method find(Class<?> c, String dev, String srg, Class<?>... args) throws NoSuchMethodException {
      for (String n : new String[]{dev, srg}) {
         try {
            Method m = c.getDeclaredMethod(n, args);
            m.setAccessible(true);
            return m;
         } catch (NoSuchMethodException ignored) {
         }
      }

      throw new NoSuchMethodException(dev);
   }

   private static void setField(EntityPlayer player, String dev, String srg, Object value) {
      try {
         boolean timer = value instanceof Integer;
         Field f = timer ? fieldSleepTimer : fieldSleeping;
         if (f == null) {
            for (String n : new String[]{dev, srg}) {
               try {
                  f = EntityPlayer.class.getDeclaredField(n);
                  break;
               } catch (NoSuchFieldException ignored) {
               }
            }

            f.setAccessible(true);
            if (timer) {
               fieldSleepTimer = f;
            } else {
               fieldSleeping = f;
            }
         }

         f.set(player, value);
      } catch (Exception e) {
         e.printStackTrace();
      }
   }
}
