package com.electrifiedded.starminer.basics.ai;

import net.minecraft.entity.ai.EntityAIBase;
import net.minecraft.entity.passive.EntityWolf;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;

public class EntityAIBegGEx extends EntityAIBase {
   private final EntityWolf wolf;
   private EntityPlayer player;
   private final World world;
   private final float minPlayerDistance;
   private int begTick;

   public EntityAIBegGEx(EntityWolf wolf, float minDistance) {
      this.wolf = wolf;
      this.world = wolf.world;
      this.minPlayerDistance = minDistance;
      this.setMutexBits(2);
   }

   @Override
   public boolean shouldExecute() {
      this.player = this.world.getClosestPlayerToEntity(this.wolf, this.minPlayerDistance);
      return this.player != null && this.hasTemptationItemInHand(this.player);
   }

   @Override
   public boolean shouldContinueExecuting() {
      if (!this.player.isEntityAlive()) {
         return false;
      }

      return this.wolf.getDistanceSq(this.player) <= this.minPlayerDistance * this.minPlayerDistance
         && this.begTick > 0 && this.hasTemptationItemInHand(this.player);
   }

   @Override
   public void startExecuting() {
      this.wolf.setBegging(true);
      this.begTick = 40 + this.wolf.getRNG().nextInt(40);
   }

   @Override
   public void resetTask() {
      this.wolf.setBegging(false);
      this.player = null;
   }

   @Override
   public void updateTask() {
      this.wolf.getLookHelper().setLookPositionWithEntity(this.player, 10.0F, this.wolf.getVerticalFaceSpeed());
      this.begTick--;
   }

   private boolean hasTemptationItemInHand(EntityPlayer p) {
      for (net.minecraft.util.EnumHand hand : net.minecraft.util.EnumHand.values()) {
         ItemStack stack = p.getHeldItem(hand);
         if (!stack.isEmpty()) {
            if (!this.wolf.isTamed() && stack.getItem() == Items.BONE) {
               return true;
            }

            if (this.wolf.isBreedingItem(stack)) {
               return true;
            }
         }
      }

      return false;
   }
}
