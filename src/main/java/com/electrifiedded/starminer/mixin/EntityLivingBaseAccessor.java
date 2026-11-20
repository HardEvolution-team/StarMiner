package com.electrifiedded.starminer.mixin;

import net.minecraft.entity.EntityLivingBase;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;
import org.spongepowered.asm.mixin.gen.Invoker;

@Mixin(EntityLivingBase.class)
public interface EntityLivingBaseAccessor {
   @Accessor("isJumping")
   boolean starminer$isJumping();

   @Accessor("idleTime")
   int starminer$getIdleTime();

   @Accessor("idleTime")
   void starminer$setIdleTime(int v);

   @Invoker("getWaterSlowDown")
   float starminer$getWaterSlowDown();

   @Invoker("getJumpUpwardsMotion")
   float starminer$getJumpUpwardsMotion();
}
