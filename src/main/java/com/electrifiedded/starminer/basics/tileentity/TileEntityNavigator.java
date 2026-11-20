package com.electrifiedded.starminer.basics.tileentity;

import com.electrifiedded.starminer.basics.block.BlockNavigator;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.play.server.SPacketUpdateTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumHand;
import net.minecraft.util.ITickable;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.Vec3d;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class TileEntityNavigator extends TileEntity implements ITickable {
   private static final String LX_NBT_KEY = "stmn.lx";
   private static final String LY_NBT_KEY = "stmn.ly";
   private static final String LZ_NBT_KEY = "stmn.lz";
   private static final String ACT_NBT_KEY = "stmn.actvc";
   public int activeTickCount = 0;
   public float lookX = 0.0F;
   public float lookY = 0.0F;
   public float lookZ = 0.0F;

   public void activate() {
      this.activeTickCount = 100;
      this.sync();
   }

   public boolean isActive() {
      return this.activeTickCount > 0;
   }

   @Override
   public void update() {
      if (!this.world.isRemote) {
         if (this.activeTickCount > 0) {
            this.updateAngleByNearPlayer();
            this.activeTickCount--;
            if (this.activeTickCount <= 0 || this.world.getTotalWorldTime() % 10L == 0L) {
               this.sync();
            }
         }
      } else if (this.activeTickCount > 0) {
         this.activeTickCount--;
      }
   }

   private void sync() {
      this.markDirty();
      IBlockState state = this.world.getBlockState(this.pos);
      this.world.notifyBlockUpdate(this.pos, state, state, 3);
   }

   private void updateAngleByNearPlayer() {
      AxisAlignedBB bb = new AxisAlignedBB(this.pos).grow(2.0);
      bb = new AxisAlignedBB(bb.minX, bb.minY, bb.minZ, bb.maxX, this.world.getHeight(), bb.maxZ);
      for (EntityPlayer entityPlayer : this.world.getEntitiesWithinAABB(EntityPlayer.class, bb)) {
         if (!entityPlayer.isPlayerSleeping()
            && (BlockNavigator.doesPlayerHasTorchOnHand(entityPlayer, EnumHand.MAIN_HAND) || BlockNavigator.doesPlayerHasTorchOnHand(entityPlayer, EnumHand.OFF_HAND))) {
            Vec3d vec3 = entityPlayer.getLook(1.0F);
            this.lookX = (float)vec3.x;
            this.lookY = (float)vec3.y;
            this.lookZ = (float)vec3.z;
         }
      }
   }

   @Override
   public void readFromNBT(NBTTagCompound tag) {
      super.readFromNBT(tag);
      this.lookX = tag.getFloat(LX_NBT_KEY);
      this.lookY = tag.getFloat(LY_NBT_KEY);
      this.lookZ = tag.getFloat(LZ_NBT_KEY);
      this.activeTickCount = tag.getInteger(ACT_NBT_KEY);
   }

   @Override
   public NBTTagCompound writeToNBT(NBTTagCompound tag) {
      super.writeToNBT(tag);
      tag.setFloat(LX_NBT_KEY, this.lookX);
      tag.setFloat(LY_NBT_KEY, this.lookY);
      tag.setFloat(LZ_NBT_KEY, this.lookZ);
      tag.setInteger(ACT_NBT_KEY, this.activeTickCount);
      return tag;
   }

   @Override
   public NBTTagCompound getUpdateTag() {
      return this.writeToNBT(new NBTTagCompound());
   }

   @Override
   public SPacketUpdateTileEntity getUpdatePacket() {
      return new SPacketUpdateTileEntity(this.pos, 0, this.getUpdateTag());
   }

   @Override
   public void onDataPacket(NetworkManager net, SPacketUpdateTileEntity pkt) {
      this.readFromNBT(pkt.getNbtCompound());
   }

   @SideOnly(Side.CLIENT)
   @Override
   public AxisAlignedBB getRenderBoundingBox() {
      return INFINITE_EXTENT_AABB;
   }
}
