package com.electrifiedded.starminer.basics.packet;

import com.electrifiedded.archaiclib.v1710.api.network.AbstractPacket;
import com.electrifiedded.starminer.basics.dimention.MapFromSky;
import io.netty.buffer.ByteBuf;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraftforge.fml.relauncher.Side;

public class PacketSkyMap extends AbstractPacket {
   private byte[] colors;

   public PacketSkyMap() {
   }

   public PacketSkyMap(byte[] colors) {
      this.colors = colors;
   }

   @Override
   public void writeTo(ByteBuf buf, Side side) {
      buf.writeShort(this.colors.length);
      buf.writeBytes(this.colors);
   }

   @Override
   public void readFrom(ByteBuf buf, Side side) {
      int length = buf.readUnsignedShort();
      this.colors = new byte[length];
      buf.readBytes(this.colors);
   }

   @Override
   public void execute(Side side, EntityPlayer player) {
      if (side != Side.CLIENT || this.colors == null || this.colors.length != MapFromSky.SIZE * MapFromSky.SIZE) return;
      if (MapFromSky.skyMapclientData != null) {
         MapFromSky.doRecompileSkyMapList = true;
      }

      MapFromSky.skyMapclientData = this.colors;
      MapFromSky.hasSkyMapImageData = true;
   }
}
