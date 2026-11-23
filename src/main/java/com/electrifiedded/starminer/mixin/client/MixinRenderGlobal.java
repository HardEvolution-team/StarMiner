package com.electrifiedded.starminer.mixin.client;

import com.electrifiedded.starminer.api.GravityDirection;
import com.electrifiedded.starminer.core.entity.GravityLiving;
import net.minecraft.client.renderer.RenderGlobal;
import net.minecraft.entity.Entity;
import org.lwjgl.util.vector.Vector3f;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(RenderGlobal.class)
public abstract class MixinRenderGlobal {
   @Inject(method = "getViewVector", at = @At("RETURN"), cancellable = true)
   private void starminer$viewVector(Entity entity, double partialTicks, CallbackInfoReturnable<Vector3f> cir) {
      if (GravityLiving.isAbnormal(entity)) {
         GravityDirection g = GravityLiving.getDir(entity);
         Vector3f v = cir.getReturnValue();
         cir.setReturnValue(new Vector3f((float)g.toWorldX(v.x, v.y, v.z), (float)g.toWorldY(v.x, v.y, v.z), (float)g.toWorldZ(v.x, v.y, v.z)));
      }
   }
}
