package com.electrifiedded.starminer.core.ai.helper;

import com.electrifiedded.starminer.api.GravityDirection;
import com.electrifiedded.starminer.core.entity.GravityLiving;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.EntityMoveHelper;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;

public class GEntityMoveHelper extends EntityMoveHelper {
   public GEntityMoveHelper(EntityLiving entityLiving) {
      super(entityLiving);
   }

   @Override
   public void onUpdateMoveHelper() {
      this.entity.setMoveForward(0.0F);
      if (this.action == EntityMoveHelper.Action.MOVE_TO) {
         this.action = EntityMoveHelper.Action.WAIT;
         GravityDirection g = GravityLiving.getDir(this.entity);
         Vec3d feet = g == GravityDirection.upTOdown_YN
            ? new Vec3d(this.entity.posX, this.entity.getEntityBoundingBox().minY, this.entity.posZ)
            : GravityLiving.feetCenter(this.entity.getEntityBoundingBox(), g);
         double wx = this.posX - feet.x;
         double wy = this.posY - feet.y;
         double wz = this.posZ - feet.z;
         double lx = g.toLocalX(wx, wy, wz);
         double ly = g.toLocalY(wx, wy, wz);
         double lz = g.toLocalZ(wx, wy, wz);
         double dist = lx * lx + ly * ly + lz * lz;
         if (dist < 2.5000003E-7F) {
            this.entity.setMoveForward(0.0F);
            return;
         }

         float f = (float)(MathHelper.atan2(lz, lx) * (180.0 / Math.PI)) - 90.0F;
         this.entity.rotationYaw = this.limitAngle(this.entity.rotationYaw, f, 90.0F);
         this.entity.setAIMoveSpeed((float)(this.speed * this.entity.getEntityAttribute(SharedMonsterAttributes.MOVEMENT_SPEED).getAttributeValue()));
         if (ly > this.entity.stepHeight && lx * lx + lz * lz < Math.max(1.0F, this.entity.width)) {
            this.entity.getJumpHelper().setJumping();
         } else if (this.entity.collidedHorizontally && this.entity.onGround) {
            this.entity.getJumpHelper().setJumping();
         }
      } else {
         this.action = EntityMoveHelper.Action.WAIT;
      }
   }
}
