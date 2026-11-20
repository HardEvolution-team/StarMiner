package com.electrifiedded.starminer.basics.entity;

import net.minecraft.entity.Entity;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.play.server.SPacketSetPassengers;
import net.minecraft.world.World;
import net.minecraft.world.WorldServer;

public class EntityReRideAttacher extends Entity {
   private int entityLimit = 150;
   private Entity toRideEntity;
   private Entity toBeRiddenEntity;

   public EntityReRideAttacher(World world) {
      super(world);
      this.isImmuneToFire = true;
      this.noClip = true;
   }

   public void setToRideEntityAtServer(Entity entity) {
      this.toRideEntity = entity;
      this.setLocationAndAngles(entity.posX, entity.posY, entity.posZ, entity.rotationYaw, entity.rotationPitch);
   }

   public void setToBeRiddenEntityAtServer(Entity entity) {
      this.toBeRiddenEntity = entity;
   }

   @Override
   protected void entityInit() {
      this.setInvisible(true);
   }

   @Override
   public boolean canBeCollidedWith() {
      return false;
   }

   @Override
   public boolean isEntityInvulnerable(net.minecraft.util.DamageSource source) {
      return true;
   }

   @Override
   public boolean isPushedByWater() {
      return false;
   }

   @Override
   public void onUpdate() {
      super.onUpdate();
      if (--this.entityLimit > 0) {
         if (!this.world.isRemote) {
            if (this.toRideEntity == null || this.toBeRiddenEntity == null) {
               this.setDead();
            } else if (this.toRideEntity.getRidingEntity() != this.toBeRiddenEntity) {
               this.setDead();
            } else if (this.entityLimit % 5 == 0 && this.world instanceof WorldServer) {
               ((WorldServer)this.world).getEntityTracker().sendToTracking(this.toBeRiddenEntity, new SPacketSetPassengers(this.toBeRiddenEntity));
            }
         }
      } else {
         this.setDead();
      }
   }

   @Override
   protected void readEntityFromNBT(NBTTagCompound tag) {
   }

   @Override
   protected void writeEntityToNBT(NBTTagCompound tag) {
   }
}
