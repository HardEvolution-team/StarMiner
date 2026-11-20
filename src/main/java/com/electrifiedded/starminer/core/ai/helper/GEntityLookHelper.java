package com.electrifiedded.starminer.core.ai.helper;

import com.electrifiedded.starminer.api.GravityDirection;
import com.electrifiedded.starminer.core.entity.GravityLiving;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.ai.EntityLookHelper;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;

public class GEntityLookHelper extends EntityLookHelper {
   private final EntityLiving living;
   private float deltaLookYaw;
   private float deltaLookPitch;
   private boolean looking;
   private double lookX;
   private double lookY;
   private double lookZ;

   public GEntityLookHelper(EntityLiving entityLiving) {
      super(entityLiving);
      this.living = entityLiving;
   }

   @Override
   public void setLookPositionWithEntity(Entity lookingAt, float deltaYaw, float deltaPitch) {
      Vec3d p;
      if (lookingAt instanceof EntityLivingBase) {
         p = GravityLiving.eyeFromBB(lookingAt);
      } else {
         AxisAlignedBB bb = lookingAt.getEntityBoundingBox();
         p = new Vec3d((bb.minX + bb.maxX) / 2.0, (bb.minY + bb.maxY) / 2.0, (bb.minZ + bb.maxZ) / 2.0);
      }
      this.setLookPosition(p.x, p.y, p.z, deltaYaw, deltaPitch);
   }

   @Override
   public void setLookPosition(double x, double y, double z, float deltaYaw, float deltaPitch) {
      this.lookX = x;
      this.lookY = y;
      this.lookZ = z;
      this.deltaLookYaw = deltaYaw;
      this.deltaLookPitch = deltaPitch;
      this.looking = true;
   }

   @Override
   public void onUpdateLook() {
      this.living.rotationPitch = 0.0F;
      if (this.looking) {
         this.looking = false;
         GravityDirection g = GravityLiving.getDir(this.living);
         Vec3d eye = GravityLiving.eyeFromBB(this.living);
         double wx = this.lookX - eye.x;
         double wy = this.lookY - eye.y;
         double wz = this.lookZ - eye.z;
         double lx = g.toLocalX(wx, wy, wz);
         double ly = g.toLocalY(wx, wy, wz);
         double lz = g.toLocalZ(wx, wy, wz);
         double d3 = MathHelper.sqrt(lx * lx + lz * lz);
         float f = (float)(MathHelper.atan2(lz, lx) * (180.0 / Math.PI)) - 90.0F;
         float f1 = (float)(-(MathHelper.atan2(ly, d3) * (180.0 / Math.PI)));
         this.living.rotationPitch = this.rotate(this.living.rotationPitch, f1, this.deltaLookPitch);
         this.living.rotationYawHead = this.rotate(this.living.rotationYawHead, f, this.deltaLookYaw);
      } else {
         this.living.rotationYawHead = this.rotate(this.living.rotationYawHead, this.living.renderYawOffset, 10.0F);
      }

      float f2 = MathHelper.wrapDegrees(this.living.rotationYawHead - this.living.renderYawOffset);
      if (!this.living.getNavigator().noPath()) {
         if (f2 < -75.0F) {
            this.living.rotationYawHead = this.living.renderYawOffset - 75.0F;
         }

         if (f2 > 75.0F) {
            this.living.rotationYawHead = this.living.renderYawOffset + 75.0F;
         }
      }
   }

   private float rotate(float current, float target, float maxDelta) {
      float f = MathHelper.wrapDegrees(target - current);
      if (f > maxDelta) {
         f = maxDelta;
      }

      if (f < -maxDelta) {
         f = -maxDelta;
      }

      return current + f;
   }

   @Override
   public boolean getIsLooking() {
      return this.looking;
   }

   @Override
   public double getLookPosX() {
      return this.lookX;
   }

   @Override
   public double getLookPosY() {
      return this.lookY;
   }

   @Override
   public double getLookPosZ() {
      return this.lookZ;
   }
}
