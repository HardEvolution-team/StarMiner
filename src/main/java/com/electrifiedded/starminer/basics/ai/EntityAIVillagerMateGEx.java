package com.electrifiedded.starminer.basics.ai;

import com.electrifiedded.starminer.api.Gravity;
import com.electrifiedded.starminer.basics.tileentity.TileEntityGravityGenerator;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityAgeable;
import net.minecraft.entity.passive.EntityVillager;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.living.BabyEntitySpawnEvent;

public class EntityAIVillagerMateGEx extends AbstractEntityAIMateGEx {
   private final EntityVillager villager;
   private EntityVillager mate;
   private final World world;
   private int matingTimeout;

   public EntityAIVillagerMateGEx(EntityVillager villager) {
      this.villager = villager;
      this.world = villager.world;
      this.setMutexBits(3);
   }

   private TileEntityGravityGenerator getCore() {
      Gravity gravity = Gravity.getGravityProp(this.villager);
      if (gravity == null || !gravity.isAttracted) {
         return null;
      }

      TileEntity te = this.world.getTileEntity(new BlockPos(gravity.attractedPosX, gravity.attractedPosY, gravity.attractedPosZ));
      return te instanceof TileEntityGravityGenerator ? (TileEntityGravityGenerator)te : null;
   }

   private boolean canAttractedVillagerMate() {
      TileEntityGravityGenerator core = this.getCore();
      return core != null && core.isEntityMateableNow(this.villager);
   }

   @Override
   public boolean shouldExecute() {
      if (this.villager.getGrowingAge() != 0) {
         return false;
      } else if (this.villager.getRNG().nextInt(60) != 0) {
         return false;
      } else if (!this.canAttractedVillagerMate()) {
         return false;
      } else {
         Entity entity = this.world.findNearestEntityWithinAABB(EntityVillager.class, this.villager.getEntityBoundingBox().grow(8.0, 3.0, 8.0), this.villager);
         if (entity == null) {
            return false;
         }

         this.mate = (EntityVillager)entity;
         return this.mate.getGrowingAge() == 0;
      }
   }

   @Override
   public void startExecuting() {
      this.matingTimeout = 600;
      this.villager.setMating(true);
   }

   @Override
   public void resetTask() {
      this.mate = null;
      this.villager.setMating(false);
   }

   @Override
   public boolean shouldContinueExecuting() {
      return this.matingTimeout >= 0 && this.canAttractedVillagerMate() && this.villager.getGrowingAge() == 0;
   }

   @Override
   public void updateTask() {
      this.matingTimeout--;
      this.villager.getLookHelper().setLookPositionWithEntity(this.mate, 10.0F, 30.0F);
      if (this.villager.getDistanceSq(this.mate) > 2.25) {
         this.villager.getNavigator().tryMoveToEntityLiving(this.mate, 0.25);
      } else if (this.matingTimeout == 0 && this.mate.isMating()) {
         this.giveBirth();
      }

      if (this.villager.getRNG().nextInt(35) == 0) {
         this.world.setEntityState(this.villager, (byte)12);
      }
   }

   private void giveBirth() {
      TileEntityGravityGenerator core = this.getCore();
      if (core == null) {
         return;
      }

      core.notifyMate(this.villager);
      EntityAgeable baby = this.villager.createChild(this.mate);
      this.mate.setGrowingAge(1000);
      this.villager.setGrowingAge(1000);
      BabyEntitySpawnEvent event = new BabyEntitySpawnEvent(this.villager, this.mate, baby);
      if (MinecraftForge.EVENT_BUS.post(event) || event.getChild() == null) {
         return;
      }

      baby = event.getChild();
      baby.setGrowingAge(-24000);
      baby.setLocationAndAngles(this.villager.posX, this.villager.posY, this.villager.posZ, 0.0F, 0.0F);
      if (baby instanceof net.minecraft.entity.EntityLiving) {
         this.passGravityStateToChild(this.villager, (net.minecraft.entity.EntityLiving)baby);
      }

      this.world.spawnEntity(baby);
      this.world.setEntityState(baby, (byte)12);
   }
}
