package com.electrifiedded.starminer.core.packet;

import com.electrifiedded.archaiclib.v1710.api.network.AbstractPacket;
import com.electrifiedded.starminer.core.entity.ExtendedPropertyGravity;
import io.netty.buffer.ByteBuf;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraftforge.fml.relauncher.Side;

public class PacketAttract extends AbstractPacket {
   protected int entityId;
   protected boolean immidiate;
   protected boolean attracted;
   protected int x;
   protected int y;
   protected int z;

   public PacketAttract() {
   }

   public PacketAttract(int entityId, boolean immidiate, boolean attracted, int x, int y, int z) {
      this.entityId = entityId;
      this.immidiate = immidiate;
      this.attracted = attracted;
      this.x = x;
      this.y = y;
      this.z = z;
   }

   @Override
   public void writeTo(ByteBuf buf, Side side) {
      buf.writeInt(this.entityId);
      buf.writeBoolean(this.immidiate);
      buf.writeBoolean(this.attracted);
      buf.writeInt(this.x);
      buf.writeInt(this.y);
      buf.writeInt(this.z);
   }

   @Override
   public void readFrom(ByteBuf buf, Side side) {
      this.entityId = buf.readInt();
      this.immidiate = buf.readBoolean();
      this.attracted = buf.readBoolean();
      this.x = buf.readInt();
      this.y = buf.readInt();
      this.z = buf.readInt();
   }

   @Override
   public void execute(Side side, EntityPlayer player) {
      if (side != Side.CLIENT || player == null || player.getEntityId() != this.entityId) return;
      this.apply(player, true);
   }

   protected void apply(Entity entity, boolean setImmidiate) {
      ExtendedPropertyGravity gravity = ExtendedPropertyGravity.getExtendedPropertyGravity(entity);
      if (gravity == null) return;
      if (this.attracted) {
         gravity.isAttracted = true;
         gravity.attractedPosX = this.x;
         gravity.attractedPosY = this.y;
         gravity.attractedPosZ = this.z;
         if (setImmidiate) {
            gravity.changeGravityImmidiate = this.immidiate;
         }
      } else {
         gravity.isAttracted = false;
         gravity.attractedPosX = 0;
         gravity.attractedPosY = 0;
         gravity.attractedPosZ = 0;
      }
   }
}
