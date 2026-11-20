package com.electrifiedded.starminer.basics.common;

import com.electrifiedded.starminer.api.IRotateSleepingViewHandler;
import com.electrifiedded.starminer.basics.Port2Registry;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraftforge.client.event.EntityViewRenderEvent;
import net.minecraftforge.client.event.RenderPlayerEvent;
import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class StarBedClientEvents {
   private final java.util.Set<EntityPlayer> pushed = java.util.Collections.newSetFromMap(new java.util.IdentityHashMap<>());

   private static boolean inStarBed(EntityPlayer player) {
      return player.isPlayerSleeping() && player.bedLocation != null && player.world.getBlockState(player.bedLocation).getBlock() == Port2Registry.StarBedHeadBlock;
   }

   @SubscribeEvent
   public void onCameraSetup(EntityViewRenderEvent.CameraSetup event) {
      Entity entity = event.getEntity();
      if (!(entity instanceof EntityPlayer) || !inStarBed((EntityPlayer)entity)) {
         return;
      }

      GlStateManager.rotate(event.getPitch(), 1.0F, 0.0F, 0.0F);
      GlStateManager.rotate(event.getYaw(), 0.0F, 1.0F, 0.0F);
      for (IRotateSleepingViewHandler handler : IRotateSleepingViewHandler.handlerList) {
         if (handler.rotateSleepingFPView()) {
            break;
         }
      }

      event.setYaw(0.0F);
      event.setPitch(0.0F);
      event.setRoll(0.0F);
   }

   @SubscribeEvent(priority = EventPriority.LOWEST)
   public void onRenderPlayerPre(RenderPlayerEvent.Pre event) {
      EntityPlayer player = event.getEntityPlayer();
      if (!inStarBed(player)) {
         return;
      }

      GlStateManager.pushMatrix();
      GlStateManager.translate((float)event.getX(), (float)event.getY(), (float)event.getZ());
      boolean done = false;
      for (IRotateSleepingViewHandler handler : IRotateSleepingViewHandler.handlerList) {
         if (handler.rotateTPPlayerSleeping(player)) {
            done = true;
            break;
         }
      }

      GlStateManager.translate((float)-event.getX(), (float)-event.getY(), (float)-event.getZ());
      if (done) {
         this.pushed.add(player);
      } else {
         GlStateManager.popMatrix();
      }
   }

   @SubscribeEvent(priority = EventPriority.LOWEST)
   public void onRenderPlayerPost(RenderPlayerEvent.Post event) {
      if (this.pushed.remove(event.getEntityPlayer())) {
         GlStateManager.popMatrix();
      }
   }
}
