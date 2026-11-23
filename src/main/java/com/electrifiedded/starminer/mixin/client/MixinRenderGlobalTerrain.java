package com.electrifiedded.starminer.mixin.client;

import com.electrifiedded.starminer.api.GravityDirection;
import com.electrifiedded.starminer.core.entity.GravityLiving;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.RenderGlobal;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.BlockPos;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

/** Skipped by StarMinerMixinPlugin when Celeritas (which overwrites setupTerrain) is installed. */
@Mixin(RenderGlobal.class)
public abstract class MixinRenderGlobalTerrain {
   @Redirect(method = "setupTerrain", at = @At(value = "NEW", target = "(DDD)Lnet/minecraft/util/math/BlockPos;", ordinal = 0), require = 0)
   private BlockPos starminer$cameraBlock(double x, double y, double z) {
      Entity view = Minecraft.getMinecraft().getRenderViewEntity();
      if (view != null && GravityLiving.isAbnormal(view)) {
         GravityDirection g = GravityLiving.getDir(view);
         double baseY = y - view.getEyeHeight();
         return new BlockPos(GravityLiving.eyeAt(view, g, x, baseY, z));
      }
      return new BlockPos(x, y, z);
   }
}
