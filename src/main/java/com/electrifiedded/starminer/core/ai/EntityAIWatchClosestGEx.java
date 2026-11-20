package com.electrifiedded.starminer.core.ai;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.ai.EntityAIBase;
import net.minecraft.entity.player.EntityPlayer;

public class EntityAIWatchClosestGEx extends EntityAIBase {
   private EntityLiving theWatcher;
   protected Entity closestEntity;
   private float maxDistanceForPlayer;
   private int lookTime;
   private float randomValueThresHold;
   private Class<? extends Entity> watchedClass;

   public EntityAIWatchClosestGEx(EntityLiving p_i1631_1_, Class<? extends Entity> clazz, float maxDistanceForPlayer) {
      this.theWatcher = p_i1631_1_;
      this.watchedClass = clazz;
      this.maxDistanceForPlayer = maxDistanceForPlayer;
      this.randomValueThresHold = 0.02F;
      this.setMutexBits(2);
   }

   public EntityAIWatchClosestGEx(EntityLiving p_i1632_1_, Class<? extends Entity> clazz, float maxDistanceForPlayer, float randomValueThresHold) {
      this.theWatcher = p_i1632_1_;
      this.watchedClass = clazz;
      this.maxDistanceForPlayer = maxDistanceForPlayer;
      this.randomValueThresHold = randomValueThresHold;
      this.setMutexBits(2);
   }

   public boolean shouldExecute() {
      if (this.theWatcher.getRNG().nextFloat() >= this.randomValueThresHold) {
         return false;
      } else {
         if (this.theWatcher.getAttackTarget() != null) {
            this.closestEntity = this.theWatcher.getAttackTarget();
         }

         if (this.watchedClass == EntityPlayer.class) {
            this.closestEntity = this.theWatcher.world.getClosestPlayerToEntity(this.theWatcher, this.maxDistanceForPlayer);
         } else {
            this.closestEntity = this.theWatcher.world.findNearestEntityWithinAABB(
                  this.watchedClass, this.theWatcher.getEntityBoundingBox().grow(this.maxDistanceForPlayer, 3.0, this.maxDistanceForPlayer), this.theWatcher
               );
         }

         return this.closestEntity != null;
      }
   }

   public boolean shouldContinueExecuting() {
      return !this.closestEntity.isEntityAlive()
         ? false
         : (this.theWatcher.getDistanceSq(this.closestEntity) > this.maxDistanceForPlayer * this.maxDistanceForPlayer ? false : this.lookTime > 0);
   }

   public void startExecuting() {
      this.lookTime = 40 + this.theWatcher.getRNG().nextInt(40);
   }

   public void resetTask() {
      this.closestEntity = null;
   }

   public void updateTask() {
      this.theWatcher.getLookHelper().setLookPositionWithEntity(this.closestEntity, 10.0F, this.theWatcher.getVerticalFaceSpeed());
      this.lookTime--;
   }
}
