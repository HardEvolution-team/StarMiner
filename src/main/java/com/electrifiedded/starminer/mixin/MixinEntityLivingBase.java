package com.electrifiedded.starminer.mixin;

import com.electrifiedded.starminer.api.GravityDirection;
import com.electrifiedded.starminer.core.entity.ExtendedPropertyGravity;
import com.electrifiedded.starminer.core.entity.GravityLiving;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(EntityLivingBase.class)
public abstract class MixinEntityLivingBase {
   @Unique
   private static final DataParameter<Byte> STARMINER$GRAVITY = GravityLiving.setGravityKey(
      EntityDataManager.createKey(EntityLivingBase.class, DataSerializers.BYTE)
   );

   @Unique
   private EntityLivingBase starminer$self() {
      return (EntityLivingBase)(Object)this;
   }

   @Inject(method = "entityInit", at = @At("TAIL"))
   private void starminer$entityInit(CallbackInfo ci) {
      this.starminer$self().getDataManager().register(STARMINER$GRAVITY, (byte)(GravityDirection.upTOdown_YN.ordinal() + 1));
   }

   @Inject(method = "notifyDataManagerChange", at = @At("TAIL"))
   private void starminer$notifyDataManagerChange(DataParameter<?> key, CallbackInfo ci) {
      EntityLivingBase self = this.starminer$self();
      if (GravityLiving.isGravitized(self)) {
         GravityLiving.onDataManagerChange(self, key);
      }
   }

   @Inject(method = "onEntityUpdate", at = @At("HEAD"))
   private void starminer$onEntityUpdatePre(CallbackInfo ci) {
      EntityLivingBase self = this.starminer$self();
      if (GravityLiving.isGravitized(self)) {
         GravityLiving.onEntityUpdatePre(self);
      }
   }

   @Inject(method = "onEntityUpdate", at = @At("TAIL"))
   private void starminer$onEntityUpdatePost(CallbackInfo ci) {
      EntityLivingBase self = this.starminer$self();
      if (GravityLiving.isGravitized(self)) {
         GravityLiving.onEntityUpdatePost(self);
      }
   }

   @Inject(method = "travel", at = @At("HEAD"), cancellable = true)
   private void starminer$travel(float strafe, float vertical, float forward, CallbackInfo ci) {
      EntityLivingBase self = this.starminer$self();
      if (starminer$usesGravityTravel(self)) {
         GravityLiving.travel(self, GravityLiving.gravity(self), strafe, vertical, forward);
         ci.cancel();
      }
   }

   @Unique
   private static boolean starminer$usesGravityTravel(EntityLivingBase self) {
      if (!GravityLiving.isGravitized(self)) return false;
      ExtendedPropertyGravity gp = GravityLiving.gravity(self);
      return gp.gravityDirection != GravityDirection.upTOdown_YN || gp.isZeroGravity(GravityLiving.isOutSpace(self));
   }

   @Inject(method = "jump", at = @At("HEAD"), cancellable = true)
   private void starminer$jump(CallbackInfo ci) {
      EntityLivingBase self = this.starminer$self();
      if (GravityLiving.isAbnormal(self)) {
         GravityLiving.jump(self, GravityLiving.getDir(self));
         ci.cancel();
      }
   }

   @Inject(method = "handleJumpWater", at = @At("HEAD"), cancellable = true)
   private void starminer$handleJumpWater(CallbackInfo ci) {
      starminer$liquidJump(ci);
   }

   @Inject(method = "handleJumpLava", at = @At("HEAD"), cancellable = true)
   private void starminer$handleJumpLava(CallbackInfo ci) {
      starminer$liquidJump(ci);
   }

   @Unique
   private void starminer$liquidJump(CallbackInfo ci) {
      EntityLivingBase self = this.starminer$self();
      if (GravityLiving.isAbnormal(self)) {
         GravityLiving.addLocalUp(self, GravityLiving.getDir(self), 0.04F * self.getEntityAttribute(EntityLivingBase.SWIM_SPEED).getAttributeValue());
         ci.cancel();
      }
   }

   @Inject(method = "isOnLadder", at = @At("HEAD"), cancellable = true)
   private void starminer$isOnLadder(CallbackInfoReturnable<Boolean> cir) {
      EntityLivingBase self = this.starminer$self();
      if (GravityLiving.isAbnormal(self)) {
         cir.setReturnValue(GravityLiving.isOnLadder(self, GravityLiving.getDir(self)));
      }
   }

   @Inject(method = "updateFallState", at = @At("HEAD"), cancellable = true)
   private void starminer$updateFallState(double y, boolean onGroundIn, IBlockState state, BlockPos pos, CallbackInfo ci) {
      EntityLivingBase self = this.starminer$self();
      if (GravityLiving.isGravitized(self)) {
         ExtendedPropertyGravity gp = GravityLiving.gravity(self);
         if (gp.isAttracted || gp.isZeroGravity(GravityLiving.isOutSpace(self))) {
            self.fallDistance = 0.0F;
            ci.cancel();
         }
      }
   }

   @Inject(method = "swingArm", at = @At("TAIL"))
   private void starminer$swingArm(EnumHand hand, CallbackInfo ci) {
      EntityLivingBase self = this.starminer$self();
      if (GravityLiving.isGravitized(self)) {
         GravityLiving.onSwing(self);
      }
   }

   @Inject(method = "updateDistance", at = @At("HEAD"), cancellable = true)
   private void starminer$updateDistance(float p1, float p2, CallbackInfoReturnable<Float> cir) {
      EntityLivingBase self = this.starminer$self();
      if (GravityLiving.isAbnormal(self)) {
         float f3 = MathHelper.wrapDegrees(self.rotationYaw - self.renderYawOffset);
         boolean flag = f3 < -90.0F || f3 >= 90.0F;
         self.renderYawOffset = self.rotationYaw;
         cir.setReturnValue(flag ? -p2 : p2);
      }
   }

   @Inject(method = "canEntityBeSeen", at = @At("HEAD"), cancellable = true)
   private void starminer$canEntityBeSeen(Entity target, CallbackInfoReturnable<Boolean> cir) {
      EntityLivingBase self = this.starminer$self();
      if (GravityLiving.isAbnormal(self) || GravityLiving.isAbnormal(target)) {
         cir.setReturnValue(GravityLiving.canEntityBeSeen(self, target));
      }
   }

   @Inject(method = "moveRelative", at = @At("HEAD"), cancellable = true)
   private void starminer$moveRelative(float strafe, float up, float forward, float friction, CallbackInfo ci) {
      EntityLivingBase self = this.starminer$self();
      if (GravityLiving.isAbnormal(self)) {
         GravityLiving.moveRelative(self, GravityLiving.getDir(self), strafe, up, forward, friction);
         ci.cancel();
      }
   }
}
