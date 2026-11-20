package com.electrifiedded.starminer.mixin;

import com.electrifiedded.starminer.core.entity.GravityLiving;
import net.minecraft.entity.EntityLiving;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(EntityLiving.class)
public abstract class MixinEntityLiving {
   @Inject(method = "updateEntityActionState", at = @At("HEAD"), cancellable = true)
   private void starminer$gravityAI(CallbackInfo ci) {
      EntityLiving self = (EntityLiving)(Object)this;
      if (!self.world.isRemote && GravityLiving.updateAITasksForGravity(self)) {
         ci.cancel();
      }
   }
}
