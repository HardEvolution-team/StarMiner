package com.electrifiedded.starminer.basics.ai;

import com.electrifiedded.starminer.api.Gravity;
import com.electrifiedded.starminer.api.GravityDirection;
import com.electrifiedded.starminer.core.entity.GravityLiving;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.ai.EntityAIBase;
import net.minecraft.util.math.Vec3d;

public class EntityAILookIdleGEx extends EntityAIBase {
   private final EntityLiving idleEntity;
   private double lookA;
   private double lookB;
   private int idleTime;

   public EntityAILookIdleGEx(EntityLiving entityLiving) {
      this.idleEntity = entityLiving;
      this.setMutexBits(3);
   }

   @Override
   public boolean shouldExecute() {
      return this.idleEntity.getRNG().nextFloat() < 0.02F;
   }

   @Override
   public boolean shouldContinueExecuting() {
      return this.idleTime >= 0;
   }

   @Override
   public void startExecuting() {
      double d0 = (Math.PI * 2) * this.idleEntity.getRNG().nextDouble();
      this.lookA = Math.cos(d0);
      this.lookB = Math.sin(d0);
      this.idleTime = 20 + this.idleEntity.getRNG().nextInt(20);
   }

   @Override
   public void updateTask() {
      this.idleTime--;
      GravityDirection g = Gravity.getGravityDirection(this.idleEntity);
      Vec3d eye = GravityLiving.eyeFromBB(this.idleEntity);
      double lookX = eye.x + g.toWorldX(this.lookA, 0.0, this.lookB);
      double lookY = eye.y + g.toWorldY(this.lookA, 0.0, this.lookB);
      double lookZ = eye.z + g.toWorldZ(this.lookA, 0.0, this.lookB);
      this.idleEntity.getLookHelper().setLookPosition(lookX, lookY, lookZ, 10.0F, this.idleEntity.getVerticalFaceSpeed());
   }
}
