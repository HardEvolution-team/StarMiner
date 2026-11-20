package com.electrifiedded.starminer.basics.entity;

import com.electrifiedded.starminer.basics.SMModContainer;
import com.electrifiedded.starminer.basics.entity.fx.EntityCometTailFX;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.MoverType;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.passive.EntityAmbientCreature;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.util.DamageSource;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class EntityComet extends EntityAmbientCreature {
   private static final DataParameter<Byte> RED_ONE = EntityDataManager.createKey(EntityComet.class, DataSerializers.BYTE);
   private boolean isResting = false;
   private int scaleWaveAngle;

   public EntityComet(World world) {
      super(world);
      this.setSize(0.95F, 0.95F);
      this.isImmuneToFire = true;
      this.ignoreFrustumCheck = true;
   }

   @Override
   protected void entityInit() {
      super.entityInit();
      this.dataManager.register(RED_ONE, (byte)(this.world.rand.nextInt(12) == 0 ? 1 : 0));
   }

   @Override
   protected void applyEntityAttributes() {
      super.applyEntityAttributes();
      this.getEntityAttribute(SharedMonsterAttributes.MAX_HEALTH).setBaseValue(10.0);
   }

   @Override
   @SideOnly(Side.CLIENT)
   public boolean isInRangeToRenderDist(double distance) {
      return distance < 128.0 * 128.0 * 4.0;
   }

   public boolean isRedOne() {
      return this.dataManager.get(RED_ONE) != 0;
   }

   @Override
   protected SoundEvent getAmbientSound() {
      return null;
   }

   @Override
   protected SoundEvent getHurtSound(DamageSource source) {
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
      if (this.isRedOne()) {
         this.entityDropItem(new ItemStack(SMModContainer.LifeSoupItem, 32, 0), 0.0F);
      } else {
         int j = this.rand.nextInt(3 + lootingModifier) + 1;
         for (int k = 0; k < j; k++) {
            this.entityDropItem(new ItemStack(SMModContainer.LifeSoupItem, 2 + this.rand.nextInt(2), 0), 0.0F);
         }
      }
   }

   @Override
   public boolean canBePushed() {
      return false;
   }

   @Override
   public void onLivingUpdate() {
      super.onLivingUpdate();
      double movedX = this.posX - this.prevPosX;
      double movedY = this.posY - this.prevPosY;
      double movedZ = this.posZ - this.prevPosZ;
      double movedXZ = movedX * movedX + movedZ * movedZ;
      float xz = MathHelper.sqrt(movedXZ);
      float xyz = MathHelper.sqrt(movedXZ + movedY * movedY);
      this.rotationYaw = (float)(-90.0 + Math.atan2(movedZ, movedX) * 180.0 / Math.PI);
      this.rotationPitch = -((float)(Math.atan2(movedY, xz) * 180.0 / Math.PI));
      if (this.world.isRemote) {
         if (xyz >= 0.2F) {
            this.scaleWaveAngle = 0;
            this.spawnCometTail();
         } else {
            this.scaleWaveAngle++;
         }
      }
   }

   @SideOnly(Side.CLIENT)
   public void spawnCometTail() {
      float scale = 2.0F;
      AxisAlignedBB bb = this.getEntityBoundingBox();
      double fxPosX = (bb.minX + bb.maxX) / 2.0;
      double fxPosY = (bb.minY + bb.maxY) / 2.0;
      double fxPosZ = (bb.minZ + bb.maxZ) / 2.0;
      float yaw = this.rotationYaw;
      float pitch = this.rotationPitch;
      float f1 = MathHelper.cos(-yaw * (float)(Math.PI / 180.0) - (float)Math.PI);
      float f2 = MathHelper.sin(-yaw * (float)(Math.PI / 180.0) - (float)Math.PI);
      float f3 = -MathHelper.cos(-pitch * (float)(Math.PI / 180.0));
      float f4 = MathHelper.sin(-pitch * (float)(Math.PI / 180.0));
      double ax = f2 * f3;
      double ay = f4;
      double az = f1 * f3;
      EntityCometTailFX fx = new EntityCometTailFX(this.world, fxPosX, fxPosY, fxPosZ, scale);
      fx.setMotion(-ax, -ay, -az);
      Minecraft.getMinecraft().effectRenderer.addEffect(fx);
      double fixX = -ax / 2.0;
      double fixY = -ay / 2.0;
      double fixZ = -az / 2.0;
      pitch = (this.rotationPitch + 90.0F + 180.0F) % 180.0F;
      f1 = MathHelper.cos(-yaw * (float)(Math.PI / 180.0) - (float)Math.PI);
      f2 = MathHelper.sin(-yaw * (float)(Math.PI / 180.0) - (float)Math.PI);
      f3 = -MathHelper.cos(-pitch * (float)(Math.PI / 180.0));
      f4 = MathHelper.sin(-pitch * (float)(Math.PI / 180.0));
      double bx = f2 * f3;
      double by = f4;
      double bz = f1 * f3;
      fx = new EntityCometTailFX(this.world, fxPosX, fxPosY, fxPosZ, scale);
      fx.setMotion(bx + fixX, by + fixY, bz + fixZ);
      Minecraft.getMinecraft().effectRenderer.addEffect(fx);
      fx = new EntityCometTailFX(this.world, fxPosX, fxPosY, fxPosZ, scale);
      fx.setMotion(-bx + fixX, -by + fixX, -bz + fixX);
      Minecraft.getMinecraft().effectRenderer.addEffect(fx);
      double motX = ay * bz - az * by;
      double motY = az * bx - ax * bz;
      double motZ = ax * by - ay * bx;
      fx = new EntityCometTailFX(this.world, fxPosX, fxPosY, fxPosZ, scale);
      fx.setMotion(motX + fixX, motY + fixY, motZ + fixZ);
      Minecraft.getMinecraft().effectRenderer.addEffect(fx);
      motX = by * az - bz * ay;
      motY = bz * ax - bx * az;
      motZ = bx * ay - by * ax;
      fx = new EntityCometTailFX(this.world, fxPosX, fxPosY, fxPosZ, scale);
      fx.setMotion(motX + fixX, motY + fixY, motZ + fixZ);
      Minecraft.getMinecraft().effectRenderer.addEffect(fx);
   }

   @Override
   public void travel(float strafe, float vertical, float forward) {
      this.move(MoverType.SELF, this.motionX, this.motionY, this.motionZ);
   }

   @Override
   public boolean attackEntityFrom(DamageSource source, float amount) {
      if (!this.world.isRemote) {
         this.isResting = false;
      }

      return super.attackEntityFrom(source, amount);
   }

   @Override
   protected void updateAITasks() {
      if (this.idleTime > 2000) {
         this.setDead();
      }

      if (this.isResting) {
         this.motionX *= 0.9;
         this.motionY *= 0.9;
         this.motionZ *= 0.9;
         int restChange = 180;
         if (this.isRedOne()) {
            restChange = 60;
         }

         if (this.rand.nextInt(restChange) == 0) {
            this.isResting = false;
         }
      } else {
         int wayChange = 60;
         double speed = 0.2;
         if (this.isRedOne()) {
            wayChange = 20;
            speed = 0.6;
         }

         if (this.rand.nextInt(wayChange) == 0 || this.collided) {
            double angleRad = this.rand.nextDouble() * Math.PI * 2.0;
            double angleRad2 = this.rand.nextDouble() * Math.PI * 2.0;
            double cos = Math.cos(angleRad2);
            speed += this.rand.nextDouble() * 0.8;
            this.motionX = Math.sin(angleRad2) * speed;
            this.motionY = Math.cos(angleRad) * cos * speed;
            this.motionZ = Math.sin(angleRad) * cos * speed;
         }

         if (this.rand.nextInt(180) == 0) {
            this.isResting = true;
         }
      }

      if (this.posY <= 5.0) {
         this.motionY = Math.abs(this.motionY);
      } else if (this.posY >= 250.0) {
         this.motionY = -Math.abs(this.motionY);
      }

      this.despawnEntity();
   }

   public float getScaleNow() {
      return MathHelper.cos((float)(this.scaleWaveAngle * 2 / 180.0 * Math.PI)) * 0.25F + 1.0F;
   }

   @Override
   public void fall(float distance, float damageMultiplier) {
   }

   @Override
   public boolean getCanSpawnHere() {
      BlockPos pos = new BlockPos(MathHelper.floor(this.posX), MathHelper.floor(this.getEntityBoundingBox().minY), MathHelper.floor(this.posZ));
      return this.world.canSeeSky(pos)
         && this.world.getEntitiesWithinAABB(EntityComet.class, this.getEntityBoundingBox().grow(64.0, 64.0, 64.0)).isEmpty();
   }

   @Override
   public void writeEntityToNBT(NBTTagCompound tag) {
      super.writeEntityToNBT(tag);
      tag.setBoolean("isResting", this.isResting);
      tag.setByte("isRedOne", this.dataManager.get(RED_ONE));
   }

   @Override
   public void readEntityFromNBT(NBTTagCompound tag) {
      super.readEntityFromNBT(tag);
      this.isResting = tag.getBoolean("isResting");
      this.dataManager.set(RED_ONE, tag.getByte("isRedOne"));
   }
}
