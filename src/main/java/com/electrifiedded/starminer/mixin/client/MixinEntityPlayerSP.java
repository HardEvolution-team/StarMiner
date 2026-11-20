package com.electrifiedded.starminer.mixin.client;

import com.electrifiedded.starminer.core.SMCore;
import com.electrifiedded.starminer.core.entity.GravityLiving;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.util.math.AxisAlignedBB;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(EntityPlayerSP.class)
public abstract class MixinEntityPlayerSP {
   @Inject(method = "onLivingUpdate", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/entity/EntityPlayerSP;isRidingHorse()Z"))
   private void starminer$flyByGravity(CallbackInfo ci) {
      SMCore.proxy.setFlyMovementByGravity((EntityPlayerSP)(Object)this);
   }

   @Redirect(method = "onUpdateWalkingPlayer", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/entity/EntityPlayerSP;getEntityBoundingBox()Lnet/minecraft/util/math/AxisAlignedBB;"))
   private AxisAlignedBB starminer$reportedBox(EntityPlayerSP self) {
      AxisAlignedBB bb = self.getEntityBoundingBox();
      if (GravityLiving.isAbnormal(self)) {
         return bb.offset(0.0, self.posY - bb.minY, 0.0);
      }
      return bb;
   }

   /** Vanilla probes world-up points at minY+0.5 and shoves along world X/Z: wrong frame under abnormal gravity. */
   @Inject(method = "pushOutOfBlocks", at = @At("HEAD"), cancellable = true)
   private void starminer$noWorldPush(double x, double y, double z, CallbackInfoReturnable<Boolean> cir) {
      if (GravityLiving.isAbnormal((EntityPlayerSP)(Object)this)) {
         cir.setReturnValue(false);
      }
   }

   @Inject(method = "updateAutoJump", at = @At("HEAD"), cancellable = true)
   private void starminer$noAutoJump(float dx, float dz, CallbackInfo ci) {
      if (GravityLiving.isAbnormal((EntityPlayerSP)(Object)this)) {
         ci.cancel();
      }
   }
}
