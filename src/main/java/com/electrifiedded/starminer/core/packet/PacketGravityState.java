package com.electrifiedded.starminer.core.packet;

import com.electrifiedded.archaiclib.v1710.api.network.AbstractPacket;
import com.electrifiedded.starminer.api.GravityDirection;
import com.electrifiedded.starminer.core.entity.ExtendedPropertyGravity;
import com.electrifiedded.starminer.core.entity.GravityLiving;
import io.netty.buffer.ByteBuf;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraftforge.fml.relauncher.Side;

public class PacketGravityState extends AbstractPacket {
   private int entityId;
   private int dir;

   public PacketGravityState() {
   }

   public PacketGravityState(int entityId, int dir) {
      this.entityId = entityId;
      this.dir = dir;
   }

   @Override
   public void writeTo(ByteBuf buf, Side side) {
      buf.writeInt(this.entityId);
      buf.writeInt(this.dir);
   }

   @Override
   public void readFrom(ByteBuf buf, Side side) {
      this.entityId = buf.readInt();
      this.dir = buf.readInt();
   }

   @Override
   public void execute(Side side, EntityPlayer player) {
      if (player == null || this.dir < 0 || this.dir >= GravityDirection.values().length) return;
      GravityDirection newDir = GravityDirection.values()[this.dir];
      if (side == Side.SERVER) {
         if (player.getEntityId() != this.entityId) return;
         ExtendedPropertyGravity gravity = ExtendedPropertyGravity.getExtendedPropertyGravity(player);
         GravityDirection old = gravity.gravityDirection;
         gravity.gravityDirection = newDir;
         if (old != newDir) {
            player.setPosition(player.posX, player.posY, player.posZ);
            GravityLiving.fixOutFromOpaqueBlock(player, newDir);
            SMCorePacketSender.sendGravityStateToAllAround(player, newDir);
         }
      } else {
         if (player.getEntityId() == this.entityId || player.world == null) return;
         Entity entity = player.world.getEntityByID(this.entityId);
         ExtendedPropertyGravity gravity = ExtendedPropertyGravity.getExtendedPropertyGravity(entity);
         if (gravity == null) return;
         GravityDirection old = gravity.gravityDirection;
         gravity.gravityDirection = newDir;
         gravity.gravityDirectionNext = newDir;
         if (old != newDir) {
            entity.setPosition(entity.posX, entity.posY, entity.posZ);
         }
      }
   }
}
