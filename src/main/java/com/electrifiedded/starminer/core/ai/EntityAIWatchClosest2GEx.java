package com.electrifiedded.starminer.core.ai;

import net.minecraft.entity.EntityLiving;

public class EntityAIWatchClosest2GEx extends EntityAIWatchClosestGEx {
   public EntityAIWatchClosest2GEx(EntityLiving entityLiving, Class<? extends net.minecraft.entity.Entity> clazz, float maxDistanceForPlayer, float randomValueThresHold) {
      super(entityLiving, clazz, maxDistanceForPlayer, randomValueThresHold);
      this.setMutexBits(3);
   }
}
