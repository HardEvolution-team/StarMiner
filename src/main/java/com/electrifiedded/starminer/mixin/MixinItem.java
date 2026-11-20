package com.electrifiedded.starminer.mixin;

import com.electrifiedded.starminer.core.entity.GravityLiving;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(Item.class)
public abstract class MixinItem {
   @Inject(method = "rayTrace", at = @At("HEAD"), cancellable = true)
   private void starminer$rayTrace(World world, EntityPlayer player, boolean useLiquids, CallbackInfoReturnable<RayTraceResult> cir) {
      if (GravityLiving.isAbnormal(player)) {
         Vec3d eye = GravityLiving.getEyePosition(player, GravityLiving.getDir(player), 1.0F);
         Vec3d look = player.getLook(1.0F);
         double reach = player.getEntityAttribute(EntityPlayer.REACH_DISTANCE).getAttributeValue();
         Vec3d end = eye.add(look.x * reach, look.y * reach, look.z * reach);
         cir.setReturnValue(world.rayTraceBlocks(eye, end, useLiquids, !useLiquids, false));
      }
   }
}
