package com.electrifiedded.starminer.core.common;

import com.electrifiedded.starminer.api.GravityDirection;
import com.electrifiedded.starminer.core.entity.ExtendedPropertyGravity;
import com.electrifiedded.starminer.core.packet.SMCorePacketSender;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraftforge.event.entity.item.ItemTossEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.PlayerEvent.PlayerLoggedInEvent;

public class SMCommonEventHandler {
   @SubscribeEvent
   public void handlePlayerLoggedIn(PlayerLoggedInEvent event) {
      try {
         if (event.player instanceof EntityPlayerMP) {
            ExtendedPropertyGravity gProp = ExtendedPropertyGravity.getExtendedPropertyGravity(event.player);
            if (gProp != null && gProp.isAttracted) {
               SMCorePacketSender.sendAttractedChangePacketToPlayer(
                  (EntityPlayerMP)event.player, true, true, gProp.attractedPosX, gProp.attractedPosY, gProp.attractedPosZ
               );
            }
         }
      } catch (Exception e) {
         e.printStackTrace();
      }
   }

   @SubscribeEvent
   public void handlePlayerTossedItem(ItemTossEvent event) {
      ExtendedPropertyGravity gravity = ExtendedPropertyGravity.getExtendedPropertyGravity(event.getPlayer());
      if (gravity != null && gravity.gravityDirection != GravityDirection.upTOdown_YN) {
         EntityItem entityitem = event.getEntityItem();
         gravity.setGravityFixedPlayerShootVec(event.getPlayer(), entityitem, 1.0F);
         entityitem.motionX *= 0.2;
         entityitem.motionY *= 0.2;
         entityitem.motionZ *= 0.2;
      }
   }
}
