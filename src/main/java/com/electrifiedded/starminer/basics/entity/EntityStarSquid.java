package com.electrifiedded.starminer.basics.entity;

import com.electrifiedded.starminer.api.Gravity;
import com.electrifiedded.starminer.api.IZeroGravityWorldProvider;
import net.minecraft.entity.Entity;
import net.minecraft.entity.MoverType;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.passive.EntityAmbientCreature;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.DamageSource;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;

public class EntityStarSquid extends EntityAmbientCreature {
   public float squidPitch;
   public float prevSquidPitch;
   public float squidYaw;
   public float prevSquidYaw;
   public float squidRotation;
   public float prevSquidRotation;
   public float tentacleAngle;
   public float prevTentacleAngle;
   private float randomMotionSpeed;
   private float rotationVelocity;
   private float rotateSpeed;
   private float randomMotionVecX;
   private float randomMotionVecY;
   private float randomMotionVecZ;
   private boolean isBazookaSquid = false;

   public EntityStarSquid(World world) {
      super(world);
      this.setSize(0.95F, 0.95F);
      this.rotationVelocity = 1.0F / (this.rand.nextFloat() + 1.0F) * 0.2F;
      this.isImmuneToFire = true;
   }

   public EntityStarSquid(World world, boolean isBazookaSquid) {
      this(world);
      this.isBazookaSquid = isBazookaSquid;
   }

   @Override
   protected void applyEntityAttributes() {
      super.applyEntityAttributes();
      this.getEntityAttribute(SharedMonsterAttributes.MAX_HEALTH).setBaseValue(10.0);
   }

   @Override
   protected SoundEvent getAmbientSound() {
      return null;
   }

   @Override
   protected SoundEvent getHurtSound(DamageSource src) {
      return null;
   }

   @Override
   protected SoundEvent getDeathSound() {
      return null;
   }

   @Override
   protected float getSoundVolume() {
      return 0.4F;
   }

   @Override
   protected boolean canTriggerWalking() {
      return false;
   }

   @Override
   protected void dropFewItems(boolean wasRecentlyHit, int lootingModifier) {
      int j = this.rand.nextInt(3 + lootingModifier) + 1;
      for (int k = 0; k < j; k++) {
         this.entityDropItem(new ItemStack(Items.GUNPOWDER, 1, 0), 0.0F);
      }
   }

   @Override
   public double getMountedYOffset() {
      return this.height * 0.5;
   }

   @Override
   public boolean shouldRiderSit() {
      return false;
   }

   @Override
   public void onLivingUpdate() {
      if (this.isBazookaSquid) {
         Entity rider = this.getControllingPassenger();
         if (rider == null && !this.getPassengers().isEmpty()) {
            rider = this.getPassengers().get(0);
         }

         if (rider != null) {
            rider.fallDistance = 0.0F;
            if (!this.world.isRemote) {
               Gravity gravity = Gravity.getGravityProp(rider);
               if (gravity != null) {
                  gravity.setResistInOpaqueBlockDamegeTick(20);
               }
            }
         }

         super.onLivingUpdate();
         if (!this.world.isRemote) {
            if (this.collided || !this.isBeingRidden()) {
               this.removePassengers();
               this.setDead();
               return;
            }

            if (!(this.world.provider instanceof IZeroGravityWorldProvider)) {
               this.motionX *= 0.987F;
               this.motionY *= 0.987F;
               this.motionZ *= 0.987F;
               double totalSpeed = Math.sqrt(this.motionX * this.motionX + this.motionY * this.motionY + this.motionZ * this.motionZ);
               if (totalSpeed < 0.6) {
                  this.motionY -= 0.4;
               }
            }
         }

         this.prevSquidPitch = this.squidPitch;
         this.prevSquidYaw = this.squidYaw;
         this.prevSquidRotation = this.squidRotation;
         this.prevTentacleAngle = this.tentacleAngle;
         this.squidRotation = this.squidRotation + this.rotationVelocity;
         if (this.squidRotation > (float)(Math.PI * 2)) {
            this.squidRotation -= (float)(Math.PI * 2);
            if (this.rand.nextInt(10) == 0) {
               this.rotationVelocity = 1.0F / (this.rand.nextFloat() + 1.0F) * 0.2F;
            }
         }

         float f = MathHelper.sqrt(this.motionX * this.motionX + this.motionZ * this.motionZ);
         this.renderYawOffset = this.renderYawOffset + (-((float)Math.atan2(this.motionX, this.motionZ)) * 180.0F / (float)Math.PI - this.renderYawOffset) * 0.1F;
         this.rotationYaw = this.renderYawOffset;
         this.squidPitch = this.squidPitch + (-((float)Math.atan2(f, this.motionY)) * 180.0F / (float)Math.PI - this.squidPitch) * 0.5F;
      } else {
         super.onLivingUpdate();
         this.prevSquidPitch = this.squidPitch;
         this.prevSquidYaw = this.squidYaw;
         this.prevSquidRotation = this.squidRotation;
         this.prevTentacleAngle = this.tentacleAngle;
         this.squidRotation = this.squidRotation + this.rotationVelocity;
         if (this.squidRotation > (float)(Math.PI * 2)) {
            this.squidRotation -= (float)(Math.PI * 2);
            if (this.rand.nextInt(10) == 0) {
               this.rotationVelocity = 1.0F / (this.rand.nextFloat() + 1.0F) * 0.2F;
            }
         }

         if (this.squidRotation < (float)Math.PI) {
            float f = this.squidRotation / (float)Math.PI;
            this.tentacleAngle = MathHelper.sin(f * f * (float)Math.PI) * (float)Math.PI * 0.25F;
            if (f > 0.75) {
               this.randomMotionSpeed = 1.0F;
               this.rotateSpeed = 1.0F;
            } else {
               this.rotateSpeed *= 0.8F;
            }
         } else {
            this.tentacleAngle = 0.0F;
            this.randomMotionSpeed *= 0.9F;
            this.rotateSpeed *= 0.99F;
         }

         if (!this.world.isRemote) {
            this.motionX = this.randomMotionVecX * this.randomMotionSpeed;
            this.motionY = this.randomMotionVecY * this.randomMotionSpeed;
            this.motionZ = this.randomMotionVecZ * this.randomMotionSpeed;
         }

         float f = MathHelper.sqrt(this.motionX * this.motionX + this.motionZ * this.motionZ);
         this.renderYawOffset = this.renderYawOffset + (-((float)Math.atan2(this.motionX, this.motionZ)) * 180.0F / (float)Math.PI - this.renderYawOffset) * 0.1F;
         this.rotationYaw = this.renderYawOffset;
         this.squidYaw = this.squidYaw + (float)Math.PI * this.rotateSpeed * 1.5F;
         this.squidPitch = this.squidPitch + (-((float)Math.atan2(f, this.motionY)) * 180.0F / (float)Math.PI - this.squidPitch) * 0.1F;
      }
   }

   @Override
   public void onDeath(DamageSource cause) {
      for (Entity e : this.getPassengers()) {
         e.fallDistance = 0.0F;
      }

      super.onDeath(cause);
   }

   @Override
   public void fall(float distance, float damageMultiplier) {
   }

   @Override
   public void travel(float strafe, float vertical, float forward) {
      this.move(MoverType.SELF, this.motionX, this.motionY, this.motionZ);
   }

   @Override
   protected void updateAITasks() {
      if (this.isBazookaSquid) {
         super.updateAITasks();
      } else {
         this.idleTime++;
         if (this.idleTime > 100) {
            this.randomMotionVecX = this.randomMotionVecY = this.randomMotionVecZ = 0.0F;
         } else if (this.rand.nextInt(50) == 0 || !this.inWater || this.randomMotionVecX == 0.0F && this.randomMotionVecY == 0.0F && this.randomMotionVecZ == 0.0F) {
            float f = this.rand.nextFloat() * (float)Math.PI * 2.0F;
            this.randomMotionVecX = MathHelper.cos(f) * 0.2F;
            this.randomMotionVecY = -0.1F + this.rand.nextFloat() * 0.2F;
            this.randomMotionVecZ = MathHelper.sin(f) * 0.2F;
         }

         this.despawnEntity();
      }
   }

   @Override
   public boolean getCanSpawnHere() {
      return this.isBazookaSquid || this.world.checkNoEntityCollision(this.getEntityBoundingBox());
   }

   @Override
   public void writeEntityToNBT(NBTTagCompound tag) {
      super.writeEntityToNBT(tag);
      tag.setBoolean("isBazookaSquid", this.isBazookaSquid);
   }

   @Override
   public void readEntityFromNBT(NBTTagCompound tag) {
      super.readEntityFromNBT(tag);
      this.isBazookaSquid = tag.getBoolean("isBazookaSquid");
   }
}
