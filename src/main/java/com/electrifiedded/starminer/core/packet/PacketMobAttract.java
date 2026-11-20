package com.electrifiedded.starminer.core.packet;

import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraftforge.fml.relauncher.Side;

public class PacketMobAttract extends PacketAttract {
   public PacketMobAttract() {
   }

   public PacketMobAttract(int entityId, boolean immidiate, boolean attracted, int x, int y, int z) {
      super(entityId, immidiate, attracted, x, y, z);
   }

   @Override
   public void execute(Side side, EntityPlayer player) {
      if (side != Side.CLIENT || player == null || player.world == null) return;
      Entity entity = player.world.getEntityByID(this.entityId);
      if (entity != null) {
         this.apply(entity, false);
      }
   }
}
