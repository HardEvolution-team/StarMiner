package com.electrifiedded.starminer.core.packet;

import com.electrifiedded.archaiclib.v1710.api.network.ArchaicChannel;
import com.electrifiedded.starminer.api.GravityDirection;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraftforge.fml.common.network.NetworkRegistry.TargetPoint;

public class SMCorePacketSender {
   public static ArchaicChannel coreChannel;

   public static void init() {
      coreChannel = new ArchaicChannel("StarminerCore", PacketAttract.class, PacketGravityState.class, PacketMobAttract.class);
   }

   public static void sendAttractedChangePacketToPlayer(
      EntityPlayerMP entityPlayer, boolean attractedState, boolean immidiate, int attractedX, int attractedY, int attractedZ
   ) {
      if (coreChannel == null || entityPlayer.connection == null) return;
      coreChannel.sendToPlayer(new PacketAttract(entityPlayer.getEntityId(), immidiate, attractedState, attractedX, attractedY, attractedZ), entityPlayer);
   }

   public static void sendGravityStatePacketToServer(Entity entity, GravityDirection newGravityDirection) {
      if (coreChannel == null) return;
      coreChannel.sendToServer(new PacketGravityState(entity.getEntityId(), newGravityDirection.ordinal()));
   }

   public static void sendGravityStateToAllAround(Entity entity, GravityDirection dir) {
      if (coreChannel == null) return;
      coreChannel.sendToAllAround(new PacketGravityState(entity.getEntityId(), dir.ordinal()),
         new TargetPoint(entity.world.provider.getDimension(), entity.posX, entity.posY, entity.posZ, 50.0));
   }

   public static void sendMobAttractedChangePacketToAllAround(
      Entity entity, boolean attractedState, boolean immidiate, int attractedX, int attractedY, int attractedZ
   ) {
      if (coreChannel == null || entity.world == null || entity.world.isRemote) return;
      coreChannel.sendToAllAround(new PacketMobAttract(entity.getEntityId(), immidiate, attractedState, attractedX, attractedY, attractedZ),
         new TargetPoint(entity.world.provider.getDimension(), entity.posX, entity.posY, entity.posZ, 50.0));
   }
}
