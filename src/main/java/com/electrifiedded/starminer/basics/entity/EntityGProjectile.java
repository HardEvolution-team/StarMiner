package com.electrifiedded.starminer.basics.entity;

import com.electrifiedded.starminer.api.Gravity;
import com.electrifiedded.starminer.api.GravityDirection;
import com.electrifiedded.starminer.api.IZeroGravityWorldProvider;
import com.electrifiedded.starminer.basics.SMModContainer;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.entity.projectile.EntityArrow;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.world.World;

public class EntityGProjectile extends EntityArrow {
   private static final DataParameter<Integer> TYPE = EntityDataManager.createKey(EntityGProjectile.class, DataSerializers.VARINT);
   private static final DataParameter<Integer> SHOOTER = EntityDataManager.createKey(EntityGProjectile.class, DataSerializers.VARINT);
   private int hitBlockSide = 1;

   public EntityGProjectile(World world) {
      super(world);
      this.setSize(0.5F, 0.5F);
   }

   public EntityGProjectile(World world, EntityPlayer shooter, float shootSpeed, GProjectileType type) {
      super(world, shooter);
      this.setSize(0.5F, 0.5F);
      this.setGProjectileType(type);
      this.dataManager.set(SHOOTER, shooter.getEntityId());
      this.pickupStatus = type == GProjectileType.gArrow ? EntityArrow.PickupStatus.ALLOWED : EntityArrow.PickupStatus.DISALLOWED;
      Gravity gravity = Gravity.getGravityProp(shooter);
      if (gravity != null) {
         gravity.setGravityFixedPlayerShootVec(shooter, this, 1.0F);
      }

      this.shoot(this.motionX, this.motionY, this.motionZ, shootSpeed * 1.5F, 1.0F);
      this.updateNoGravity();
   }

   @Override
   protected void entityInit() {
      super.entityInit();
      this.dataManager.register(TYPE, 0);
      this.dataManager.register(SHOOTER, 0);
      this.ignoreFrustumCheck = true;
   }

   public void setGProjectileType(GProjectileType type) {
      this.dataManager.set(TYPE, type.ordinal());
      this.updateNoGravity();
   }

   public GProjectileType getGProjectileType() {
      int i = this.dataManager.get(TYPE);
      return GProjectileType.values()[i >= 0 && i < GProjectileType.values().length ? i : 0];
   }

   private void updateNoGravity() {
      this.setNoGravity(this.getGProjectileType() == GProjectileType.gRappleHook || this.world != null && this.world.provider instanceof IZeroGravityWorldProvider);
   }

   public Entity getShooter() {
      if (this.shootingEntity != null) {
         return this.shootingEntity;
      }

      int id = this.dataManager.get(SHOOTER);
      return id != 0 ? this.world.getEntityByID(id) : null;
   }

   @Override
   public void onUpdate() {
      if (this.getGProjectileType() == GProjectileType.gRappleHook) {
         Entity shooter = this.getShooter();
         if (!this.world.isRemote) {
            if (!(shooter instanceof EntityPlayerMP)) {
               this.setDead();
               return;
            }

            shooter.fallDistance = 0.0F;
            Gravity gravity = Gravity.getGravityProp(shooter);
            if (gravity != null) {
               gravity.acceptExceptionalGravityTick = 45;
            }

            if (shooter.isDead || !shooter.isEntityAlive() || shooter.isSneaking() || this.getDistanceSq(shooter) > 3000.0) {
               this.setDead();
               return;
            }
         } else if (this.inGround && shooter != null) {
            this.pull(shooter);
         }
      }

      super.onUpdate();
   }

   private void pull(Entity entityToPull) {
      entityToPull.fallDistance = 0.0F;
      Gravity gravity = Gravity.getGravityProp(entityToPull);
      if (gravity == null) return;
      double distance = entityToPull.getDistance(this.posX, this.posY + 1.0, this.posZ);
      if (distance <= 0.0) return;
      double distX = this.posX - entityToPull.posX;
      double distY = this.posY + 1.0 - entityToPull.posY;
      double distZ = this.posZ - entityToPull.posZ;
      double motionForX = distX / distance * 0.05;
      double motionForY = distY / distance * 0.05;
      double motionForZ = distZ / distance * 0.05;
      double acl = 4.0;
      boolean xNearCheck = false;
      boolean yNearCheck = false;
      boolean zNearCheck = false;
      GravityDirection againstWall;
      switch (this.hitBlockSide) {
         case 0:
            againstWall = GravityDirection.downTOup_YP;
            motionForX *= acl;
            motionForZ *= acl;
            yNearCheck = true;
            break;
         case 2:
            againstWall = GravityDirection.northTOsouth_ZP;
            motionForX *= acl;
            motionForY *= acl;
            zNearCheck = true;
            break;
         case 3:
            againstWall = GravityDirection.southTOnorth_ZN;
            motionForX *= acl;
            motionForY *= acl;
            zNearCheck = true;
            break;
         case 4:
            againstWall = GravityDirection.westTOeast_XP;
            motionForY *= acl;
            motionForZ *= acl;
            xNearCheck = true;
            break;
         case 5:
            againstWall = GravityDirection.eastTOwest_XN;
            motionForY *= acl;
            motionForZ *= acl;
            xNearCheck = true;
            break;
         default:
            againstWall = GravityDirection.upTOdown_YN;
            motionForX *= acl;
            motionForZ *= acl;
            yNearCheck = true;
      }

      entityToPull.motionX += motionForX;
      entityToPull.motionY += motionForY;
      entityToPull.motionZ += motionForZ;
      if (Math.abs(distX) < 1.0) {
         entityToPull.motionX *= Math.abs(distX) / 50.0;
         if (distX > 0.2) {
            entityToPull.motionX += distX / Math.abs(distX) * 0.05;
         }

         xNearCheck = true;
      }

      if (Math.abs(distY) < 1.0) {
         entityToPull.motionY *= Math.abs(distY) / 50.0;
         if (distY > 0.2) {
            entityToPull.motionY += distY / Math.abs(distY) * 0.05;
         }

         yNearCheck = true;
      }

      if (Math.abs(distZ) < 1.0) {
         entityToPull.motionZ *= Math.abs(distZ) / 50.0;
         if (distZ > 0.2) {
            entityToPull.motionZ += distZ / Math.abs(distZ) * 0.05;
         }

         zNearCheck = true;
      }

      if ((!xNearCheck || !yNearCheck || !zNearCheck) && !(distance < 4.0)) {
         gravity.setTemporaryZeroGravity(2);
      } else {
         gravity.setTemporaryGravityDirection(againstWall, 15);
      }
   }

   @Override
   protected void onHit(RayTraceResult result) {
      if (result.typeOfHit == RayTraceResult.Type.BLOCK && result.sideHit != null) {
         this.hitBlockSide = result.sideHit.getIndex();
      } else if (result.entityHit != null && this.getGProjectileType() == GProjectileType.gRappleHook) {
         this.setDead();
         return;
      }

      super.onHit(result);
   }

   @Override
   protected ItemStack getArrowStack() {
      return this.getGProjectileType() == GProjectileType.gArrow ? new ItemStack(SMModContainer.GArrowItem) : ItemStack.EMPTY;
   }

   @Override
   public void writeEntityToNBT(NBTTagCompound tag) {
      super.writeEntityToNBT(tag);
      tag.setShort("gpType", (short)this.getGProjectileType().ordinal());
      tag.setByte("hitSide", (byte)this.hitBlockSide);
   }

   @Override
   public void readEntityFromNBT(NBTTagCompound tag) {
      super.readEntityFromNBT(tag);
      this.setGProjectileType(GProjectileType.values()[Math.max(0, Math.min(1, tag.getShort("gpType")))]);
      this.hitBlockSide = tag.getByte("hitSide");
   }

   public enum GProjectileType {
      gArrow,
      gRappleHook;
   }
}
