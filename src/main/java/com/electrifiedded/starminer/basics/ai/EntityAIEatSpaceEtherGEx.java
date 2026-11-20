package com.electrifiedded.starminer.basics.ai;

import net.minecraft.entity.ai.EntityAIBase;
import net.minecraft.entity.passive.EntitySheep;
import net.minecraft.world.World;

public class EntityAIEatSpaceEtherGEx extends EntityAIBase {
   private final EntitySheep sheep;
   private final World world;
   private int eatingTick;

   public EntityAIEatSpaceEtherGEx(EntitySheep sheep) {
      this.sheep = sheep;
      this.world = sheep.world;
      this.setMutexBits(7);
   }

   @Override
   public boolean shouldExecute() {
      return this.sheep.getSheared() && !this.sheep.isChild() && this.sheep.getRNG().nextInt(2000) == 0;
   }

   @Override
   public void startExecuting() {
      this.eatingTick = 40;
      this.world.setEntityState(this.sheep, (byte)10);
      this.sheep.getNavigator().clearPath();
   }

   @Override
   public void resetTask() {
      this.eatingTick = 0;
   }

   @Override
   public boolean shouldContinueExecuting() {
      return this.eatingTick > 0;
   }

   @Override
   public void updateTask() {
      this.eatingTick = Math.max(0, this.eatingTick - 1);
      if (this.eatingTick == 4) {
         this.sheep.eatGrassBonus();
      }
   }
}
