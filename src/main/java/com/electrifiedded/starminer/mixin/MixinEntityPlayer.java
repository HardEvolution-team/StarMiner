package com.electrifiedded.starminer.mixin;

import com.electrifiedded.starminer.api.GravityDirection;
import com.electrifiedded.starminer.core.entity.ExtendedPropertyGravity;
import com.electrifiedded.starminer.core.entity.GravityLiving;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.world.World;
import org.objectweb.asm.Opcodes;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(EntityPlayer.class)
public abstract class MixinEntityPlayer {
   @Redirect(method = "travel", at = @At(value = "FIELD", target = "Lnet/minecraft/entity/player/EntityPlayer;motionY:D", opcode = Opcodes.PUTFIELD))
   private void starminer$flyMotionY(EntityPlayer self, double value) {
      ExtendedPropertyGravity gp = GravityLiving.gravity(self);
      if (gp != null && (gp.gravityDirection != GravityDirection.upTOdown_YN || gp.isZeroGravity(GravityLiving.isOutSpace(self)))) {
         return;
      }
      self.motionY = value;
   }

   @Redirect(method = "updateSize", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/World;collidesWithAnyBlock(Lnet/minecraft/util/math/AxisAlignedBB;)Z"))
   private boolean starminer$sizeFits(World world, AxisAlignedBB upright) {
      EntityPlayer self = (EntityPlayer)(Object)this;
      if (GravityLiving.isAbnormal(self)) {
         return world.collidesWithAnyBlock(GravityLiving.resizedBox(self, GravityLiving.getDir(self), (float)(upright.maxX - upright.minX), (float)(upright.maxY - upright.minY)));
      }
      return world.collidesWithAnyBlock(upright);
   }
}
