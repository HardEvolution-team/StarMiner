package com.electrifiedded.starminer.mixin;

import com.electrifiedded.starminer.api.GravityDirection;
import com.electrifiedded.starminer.core.entity.ExtendedPropertyGravity;
import com.electrifiedded.starminer.core.entity.GravityLiving;
import com.electrifiedded.starminer.core.entity.IGravityHolder;
import net.minecraft.entity.Entity;
import net.minecraft.entity.MoverType;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.math.Vec3d;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(Entity.class)
public abstract class MixinEntity implements IGravityHolder {
   @Unique
   private ExtendedPropertyGravity starminer$gravity;

   @Override
   public ExtendedPropertyGravity starminer$getGravity() {
      if (this.starminer$gravity == null) {
         this.starminer$gravity = new ExtendedPropertyGravity((Entity)(Object)this);
      }
      return this.starminer$gravity;
   }

   @Unique
   private Entity starminer$self() {
      return (Entity)(Object)this;
   }

   @Inject(method = "writeToNBT", at = @At("RETURN"))
   private void starminer$writeGravity(NBTTagCompound compound, CallbackInfoReturnable<NBTTagCompound> cir) {
      if (GravityLiving.isGravitized(this.starminer$self())) {
         this.starminer$getGravity().saveNBTData(compound);
      }
   }

   @Inject(method = "readFromNBT", at = @At("TAIL"))
   private void starminer$readGravity(NBTTagCompound compound, CallbackInfo ci) {
      if (GravityLiving.isGravitized(this.starminer$self())) {
         this.starminer$getGravity().loadNBTData(compound);
      }
   }

   @Inject(method = "setPosition", at = @At("HEAD"), cancellable = true)
   private void starminer$setPosition(double x, double y, double z, CallbackInfo ci) {
      Entity self = this.starminer$self();
      if (GravityLiving.isAbnormal(self)) {
         GravityLiving.setPositionG(self, this.starminer$getGravity().gravityDirection, x, y, z);
         ci.cancel();
      }
   }

   @Inject(method = "resetPositionToBB", at = @At("HEAD"), cancellable = true)
   private void starminer$resetPositionToBB(CallbackInfo ci) {
      Entity self = this.starminer$self();
      if (GravityLiving.isAbnormal(self)) {
         GravityLiving.resetPositionToBBG(self, this.starminer$getGravity().gravityDirection);
         ci.cancel();
      }
   }

   @Inject(method = "move", at = @At("HEAD"), cancellable = true)
   private void starminer$move(MoverType type, double x, double y, double z, CallbackInfo ci) {
      Entity self = this.starminer$self();
      if (GravityLiving.isAbnormal(self)) {
         GravityLiving.move(self, this.starminer$getGravity(), type, x, y, z);
         ci.cancel();
      }
   }

   @Inject(method = "getPositionEyes", at = @At("HEAD"), cancellable = true)
   private void starminer$getPositionEyes(float partialTicks, CallbackInfoReturnable<Vec3d> cir) {
      Entity self = this.starminer$self();
      if (GravityLiving.isAbnormal(self)) {
         cir.setReturnValue(GravityLiving.getEyePosition(self, this.starminer$getGravity().gravityDirection, partialTicks));
      }
   }

   @Inject(method = "getVectorForRotation", at = @At("RETURN"), cancellable = true)
   private void starminer$lookVector(float pitch, float yaw, CallbackInfoReturnable<Vec3d> cir) {
      Entity self = this.starminer$self();
      if (GravityLiving.isAbnormal(self)) {
         cir.setReturnValue(this.starminer$getGravity().gravityDirection.toWorld(cir.getReturnValue()));
      }
   }

   @Inject(method = "isEntityInsideOpaqueBlock", at = @At("HEAD"), cancellable = true)
   private void starminer$insideOpaque(CallbackInfoReturnable<Boolean> cir) {
      Entity self = this.starminer$self();
      if (GravityLiving.isGravitized(self)) {
         ExtendedPropertyGravity gp = this.starminer$getGravity();
         if (gp.redistInOpaqueBlockDamageTick > 0) {
            cir.setReturnValue(false);
         } else if (gp.gravityDirection != GravityDirection.upTOdown_YN) {
            cir.setReturnValue(!self.noClip && GravityLiving.isInsideOpaqueBlock(self, gp.gravityDirection));
         }
      }
   }

   @Inject(method = "setSize", at = @At("HEAD"))
   private void starminer$beforeSetSize(float width, float height, CallbackInfo ci) {
      Entity self = this.starminer$self();
      if (GravityLiving.isAbnormal(self)) {
         GravityLiving.beforeSetSize(self, this.starminer$getGravity());
      }
   }

   @Inject(method = "setSize", at = @At("TAIL"))
   private void starminer$afterSetSize(float width, float height, CallbackInfo ci) {
      Entity self = this.starminer$self();
      if (GravityLiving.isAbnormal(self)) {
         GravityLiving.afterSetSize(self, this.starminer$getGravity());
      }
   }

   @Inject(method = "applyEntityCollision", at = @At("HEAD"), cancellable = true)
   private void starminer$applyEntityCollision(Entity target, CallbackInfo ci) {
      Entity self = this.starminer$self();
      if (GravityLiving.isGravitized(self) && GravityLiving.isAbnormal(target)) {
         GravityLiving.applyEntityCollision(self, target, GravityLiving.getDir(target));
         ci.cancel();
      }
   }

   @Inject(method = "outOfWorld", at = @At("HEAD"), cancellable = true)
   private void starminer$voidKillHeight(CallbackInfo ci) {
      if (((Entity)(Object)this).posY > -512.0) {
         ci.cancel();
      }
   }
}
