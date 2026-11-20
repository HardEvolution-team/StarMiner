package com.electrifiedded.starminer.api;

import java.util.List;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.world.Teleporter;

public interface IMountTeleportHandler {
   boolean handleSpaceTeleport(IMountTeleportHandler.IEntityTransfer var1, Teleporter var2, int var3, double var4, double var6, double var8, List var10);

   public interface IEntityTransfer {
      Entity travelEntityToDimension(Entity var1, int var2, Teleporter var3);

      void travelPlayerToDimension(EntityPlayer var1, int var2, Teleporter var3);
   }
}
