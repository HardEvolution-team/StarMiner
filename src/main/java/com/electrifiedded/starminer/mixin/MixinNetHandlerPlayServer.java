package com.electrifiedded.starminer.mixin;

import com.electrifiedded.starminer.core.entity.ExtendedPropertyGravity;
import com.electrifiedded.starminer.core.entity.GravityLiving;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.network.NetHandlerPlayServer;
import net.minecraft.server.management.PlayerInteractionManager;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(NetHandlerPlayServer.class)
public abstract class MixinNetHandlerPlayServer {
   @Shadow
   public EntityPlayerMP player;
   @Shadow
   private boolean floating;
   @Shadow
   private int floatingTickCount;

   @Unique
   private static boolean starminer$gravityPlayer(EntityPlayerMP p) {
      return p != null && p.world != null && (ExtendedPropertyGravity.isEntityAbnormalGravity(p) || ExtendedPropertyGravity.isAttracted(p));
   }

   @Inject(method = "update", at = @At("HEAD"))
   private void starminer$noFloatKick(CallbackInfo ci) {
      if (this.player != null && this.player.world != null && (GravityLiving.isOutSpace(this.player) || starminer$gravityPlayer(this.player))) {
         this.floating = false;
         this.floatingTickCount = 0;
      }
   }

   /**
    * "moved wrongly": vanilla zeroes the vertical (world Y) error, but under sideways gravity the
    * fall/jump error lands in X/Z, and the server box may still be in the previous gravity frame for a
    * tick after a direction change. Both made the server rubber-band the player ("freezing").
    * Treat gravitized players like creative players for this check.
    */
   @Redirect(method = "processPlayer", at = @At(value = "INVOKE", target = "Lnet/minecraft/server/management/PlayerInteractionManager;isCreative()Z"))
   private boolean starminer$skipWrongMove(PlayerInteractionManager mgr) {
      return mgr.isCreative() || starminer$gravityPlayer(this.player);
   }

   /** Third isPlayerSleeping() in processPlayer guards the "inside a block after move" rollback. */
   @Redirect(method = "processPlayer", at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/player/EntityPlayerMP;isPlayerSleeping()Z", ordinal = 2))
   private boolean starminer$skipCollisionRollback(EntityPlayerMP p) {
      return p.isPlayerSleeping() || starminer$gravityPlayer(p);
   }

   /** Vanilla infers a jump from world +Y motion; wrong (and spurious for upside-down) under other gravity. */
   @Redirect(method = "processPlayer", at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/player/EntityPlayerMP;jump()V"))
   private void starminer$gravityJump(EntityPlayerMP p) {
      if (!ExtendedPropertyGravity.isEntityAbnormalGravity(p)) {
         p.jump();
      }
   }
}
