package com.electrifiedded.starminer.basics.ai;

import java.util.List;
import java.util.Random;
import net.minecraft.advancements.CriteriaTriggers;
import net.minecraft.entity.EntityAgeable;
import net.minecraft.entity.item.EntityXPOrb;
import net.minecraft.entity.passive.EntityAnimal;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.stats.StatList;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.world.World;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.living.BabyEntitySpawnEvent;

public class EntityAIMateGEx extends AbstractEntityAIMateGEx {
   private final EntityAnimal animal;
   private final Class<? extends EntityAnimal> mateClass;
   private final World world;
   private EntityAnimal targetMate;
   private int spawnBabyDelay;
   private final double moveSpeed;

   public EntityAIMateGEx(EntityAnimal animal, double speed) {
      this(animal, speed, animal.getClass());
   }

   public EntityAIMateGEx(EntityAnimal animal, double speed, Class<? extends EntityAnimal> mateClass) {
      this.animal = animal;
      this.world = animal.world;
      this.mateClass = mateClass;
      this.moveSpeed = speed;
      this.setMutexBits(3);
   }

   @Override
   public boolean shouldExecute() {
      if (!this.animal.isInLove()) {
         return false;
      }

      this.targetMate = this.getNearbyMate();
      return this.targetMate != null;
   }

   @Override
   public boolean shouldContinueExecuting() {
      return this.targetMate.isEntityAlive() && this.targetMate.isInLove() && this.spawnBabyDelay < 60;
   }

   @Override
   public void resetTask() {
      this.targetMate = null;
      this.spawnBabyDelay = 0;
   }

   @Override
   public void updateTask() {
      this.animal.getLookHelper().setLookPositionWithEntity(this.targetMate, 10.0F, this.animal.getVerticalFaceSpeed());
      this.animal.getNavigator().tryMoveToEntityLiving(this.targetMate, this.moveSpeed);
      this.spawnBabyDelay++;
      if (this.spawnBabyDelay >= 60 && this.animal.getDistanceSq(this.targetMate) < 9.0) {
         this.spawnBaby();
      }
   }

   private EntityAnimal getNearbyMate() {
      List<? extends EntityAnimal> list = this.world.getEntitiesWithinAABB(this.mateClass, this.animal.getEntityBoundingBox().grow(8.0));
      double d0 = Double.MAX_VALUE;
      EntityAnimal found = null;
      for (EntityAnimal other : list) {
         if (this.animal.canMateWith(other) && this.animal.getDistanceSq(other) < d0) {
            found = other;
            d0 = this.animal.getDistanceSq(other);
         }
      }

      return found;
   }

   private void spawnBaby() {
      EntityAgeable baby = this.animal.createChild(this.targetMate);
      BabyEntitySpawnEvent event = new BabyEntitySpawnEvent(this.animal, this.targetMate, baby);
      boolean cancelled = MinecraftForge.EVENT_BUS.post(event);
      baby = event.getChild();
      if (cancelled) {
         this.animal.setGrowingAge(6000);
         this.targetMate.setGrowingAge(6000);
         this.animal.resetInLove();
         this.targetMate.resetInLove();
         return;
      }

      if (baby != null) {
         EntityPlayerMP player = this.animal.getLoveCause();
         if (player == null && this.targetMate.getLoveCause() != null) {
            player = this.targetMate.getLoveCause();
         }

         if (player != null) {
            player.addStat(StatList.ANIMALS_BRED);
            CriteriaTriggers.BRED_ANIMALS.trigger(player, this.animal, this.targetMate, baby);
         }

         this.animal.setGrowingAge(6000);
         this.targetMate.setGrowingAge(6000);
         this.animal.resetInLove();
         this.targetMate.resetInLove();
         baby.setGrowingAge(-24000);
         baby.setLocationAndAngles(this.animal.posX, this.animal.posY, this.animal.posZ, 0.0F, 0.0F);
         if (baby instanceof net.minecraft.entity.EntityLiving) {
            this.passGravityStateToChild(this.animal, (net.minecraft.entity.EntityLiving)baby);
         }

         this.world.spawnEntity(baby);
         Random random = this.animal.getRNG();
         for (int i = 0; i < 7; i++) {
            double d0 = random.nextGaussian() * 0.02;
            double d1 = random.nextGaussian() * 0.02;
            double d2 = random.nextGaussian() * 0.02;
            double d3 = random.nextDouble() * this.animal.width * 2.0 - this.animal.width;
            double d4 = 0.5 + random.nextDouble() * this.animal.height;
            double d5 = random.nextDouble() * this.animal.width * 2.0 - this.animal.width;
            this.world.spawnParticle(EnumParticleTypes.HEART, this.animal.posX + d3, this.animal.posY + d4, this.animal.posZ + d5, d0, d1, d2);
         }

         if (this.world.getGameRules().getBoolean("doMobLoot")) {
            this.world.spawnEntity(new EntityXPOrb(this.world, this.animal.posX, this.animal.posY, this.animal.posZ, random.nextInt(7) + 1));
         }
      }
   }
}
