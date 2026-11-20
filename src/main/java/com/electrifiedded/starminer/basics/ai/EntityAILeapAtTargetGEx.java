package com.electrifiedded.starminer.basics.ai;

import com.electrifiedded.starminer.api.Gravity;
import com.electrifiedded.starminer.api.GravityDirection;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.ai.EntityAIBase;

public class EntityAILeapAtTargetGEx extends EntityAIBase {
   private final EntityLiving leaper;
   private EntityLivingBase leapTarget;
   private final float leapMotionVertical;

   public EntityAILeapAtTargetGEx(EntityLiving leaper, float leapMotionVertical) {
      this.leaper = leaper;
      this.leapMotionVertical = leapMotionVertical;
      this.setMutexBits(5);
   }

   @Override
   public boolean shouldExecute() {
      this.leapTarget = this.leaper.getAttackTarget();
      if (this.leapTarget == null) {
         return false;
      }

      double d0 = this.leaper.getDistanceSq(this.leapTarget);
      return d0 >= 4.0 && d0 <= 16.0 && this.leaper.onGround && this.leaper.getRNG().nextInt(5) == 0;
   }

   @Override
   public boolean shouldContinueExecuting() {
      return !this.leaper.onGround;
   }

   @Override
   public void startExecuting() {
      GravityDirection g = Gravity.getGravityDirection(this.leaper);
      double dx = this.leapTarget.posX - this.leaper.posX;
      double dy = this.leapTarget.posY - this.leaper.posY;
      double dz = this.leapTarget.posZ - this.leaper.posZ;
      double ldx = g.toLocalX(dx, dy, dz);
      double ldz = g.toLocalZ(dx, dy, dz);
      double mx = g.toLocalX(this.leaper.motionX, this.leaper.motionY, this.leaper.motionZ);
      double mz = g.toLocalZ(this.leaper.motionX, this.leaper.motionY, this.leaper.motionZ);
      double dist = Math.sqrt(ldx * ldx + ldz * ldz);
      if (dist > 1.0E-6) {
         mx += ldx / dist * 0.5 * 0.8F + mx * 0.2F;
         mz += ldz / dist * 0.5 * 0.8F + mz * 0.2F;
      }

      this.leaper.motionX = g.toWorldX(mx, this.leapMotionVertical, mz);
      this.leaper.motionY = g.toWorldY(mx, this.leapMotionVertical, mz);
      this.leaper.motionZ = g.toWorldZ(mx, this.leapMotionVertical, mz);
   }
}
