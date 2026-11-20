package com.electrifiedded.starminer.api;

import com.electrifiedded.starminer.core.entity.IGravityHolder;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.math.Vec3d;

public abstract class Gravity {
   public static final String EXTENDED_PROP_GRAVITY_KEY = "starminer.Gravity";
   public GravityDirection gravityDirection = GravityDirection.upTOdown_YN;
   public GravityDirection gravityDirectionNext = GravityDirection.upTOdown_YN;
   public boolean isAttracted = false;
   public int attractedPosX = 0;
   public int attractedPosY = 0;
   public int attractedPosZ = 0;
   public int attractUpdateTickCount = 0;
   public int acceptExceptionalGravityTick = 0;

   public static Gravity getGravityProp(Entity entity) {
      return entity instanceof IGravityHolder ? ((IGravityHolder)entity).starminer$getGravity() : null;
   }

   public static final GravityDirection getGravityDirection(Entity entity) {
      Gravity gp = entity == null ? null : getGravityProp(entity);
      return gp == null ? GravityDirection.upTOdown_YN : gp.gravityDirection;
   }

   public static boolean isEntityZeroGravity(Entity entity) {
      if (entity == null) {
         return false;
      } else {
         Gravity gp = getGravityProp(entity);
         return gp != null && gp.isZeroGravity();
      }
   }

   public abstract boolean isZeroGravity();

   public abstract boolean isAttracted();

   public abstract void setAttractedBy(IAttractableTileEntity var1);

   public abstract boolean isAttractedBy(IAttractableTileEntity var1);

   public abstract void loseAttractedBy();

   public abstract void setTemporaryGravityDirection(GravityDirection var1, int var2);

   public abstract void setTemporaryZeroGravity(int var1);

   public abstract Vec3d getGravityFixedLook(float var1, float var2);

   public abstract Vec3d getGravityFixedPlayerEyePoz(EntityPlayer var1, float var2);

   public abstract void setGravityFixedPlayerShootVec(EntityPlayer var1, Entity var2, float var3);

   public abstract void setResistInOpaqueBlockDamegeTick(int var1);
}
