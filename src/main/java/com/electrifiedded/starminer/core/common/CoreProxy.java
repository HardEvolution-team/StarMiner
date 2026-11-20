package com.electrifiedded.starminer.core.common;

import com.electrifiedded.starminer.core.CoreConfig;
import com.electrifiedded.starminer.core.entity.ExtendedPropertyGravity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.util.text.TextComponentString;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class CoreProxy {
   private static final Logger LOGGER = LogManager.getLogger("StarMiner");

   public boolean isOtherPlayer(EntityPlayer player) {
      return true;
   }

   public void setFlyMovementByGravity(EntityPlayer entityPlayer) {
   }

   public void warnOrKickIllegalGravity(EntityPlayer player, ExtendedPropertyGravity gravity) {
      if (!(player instanceof EntityPlayerMP) || player.world.isRemote) return;
      if (CoreConfig.showUnsynchronizedWarning) {
         LOGGER.warn("{} had gravity unsynchronization long time!", player.getName());
      }

      if (CoreConfig.unsynchronizedWarnToKick != 0 && gravity.unsynchronizedWarnCount >= CoreConfig.unsynchronizedWarnToKick) {
         LOGGER.warn("{} was kicked for gravity unsynchronization for far long time!", player.getName());
         ((EntityPlayerMP)player).connection.disconnect(new TextComponentString("You had too long time gravity unsynchronized with server!!!"));
      }
   }
}
