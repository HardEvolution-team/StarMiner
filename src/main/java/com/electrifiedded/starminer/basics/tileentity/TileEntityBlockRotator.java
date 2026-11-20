package com.electrifiedded.starminer.basics.tileentity;

import com.electrifiedded.starminer.api.GravityDirection;
import net.minecraft.block.Block;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.play.server.SPacketUpdateTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class TileEntityBlockRotator extends TileEntity {
   private Block storedBlock = Blocks.AIR;
   private Item storedItem = Items.AIR;
   private int itemMetadata;
   public int relatedBlockX;
   public int relatedBlockY;
   public int relatedBlockZ;
   public boolean isSubBlock;
   private GravityDirection gravityDirection = GravityDirection.upTOdown_YN;

   public void setStoredBlock(Block block) {
      this.storedBlock = block;
   }

   public void setStoredItem(Item item) {
      this.storedItem = item;
   }

   public void setItemMetadata(int meta) {
      this.itemMetadata = meta;
   }

   public Block getStoredBlock() {
      return this.storedBlock;
   }

   public Item getStoredItem() {
      return this.storedItem;
   }

   public int getItemMetadata() {
      return this.itemMetadata;
   }

   public void setGravityDirection(GravityDirection gDir) {
      this.gravityDirection = gDir;
   }

   public GravityDirection getGravityDirection() {
      return this.gravityDirection;
   }

   public boolean hasRelated() {
      return this.relatedBlockX != this.pos.getX() || this.relatedBlockY != this.pos.getY() || this.relatedBlockZ != this.pos.getZ();
   }

   @SideOnly(Side.CLIENT)
   @Override
   public AxisAlignedBB getRenderBoundingBox() {
      return new AxisAlignedBB(this.pos.getX() - 1, this.pos.getY() - 1, this.pos.getZ() - 1, this.pos.getX() + 2, this.pos.getY() + 2, this.pos.getZ() + 2);
   }

   @Override
   public boolean shouldRefresh(net.minecraft.world.World world, net.minecraft.util.math.BlockPos pos, net.minecraft.block.state.IBlockState oldState, net.minecraft.block.state.IBlockState newState) {
      return oldState.getBlock() != newState.getBlock();
   }

   @Override
   public void readFromNBT(NBTTagCompound compound) {
      super.readFromNBT(compound);
      Block b = Block.REGISTRY.getObject(new ResourceLocation(compound.hasKey("block") ? compound.getString("block") : "minecraft:air"));
      this.storedBlock = b == null ? Blocks.AIR : b;
      Item i = Item.REGISTRY.getObject(new ResourceLocation(compound.hasKey("item") ? compound.getString("item") : "minecraft:air"));
      this.storedItem = i == null ? Items.AIR : i;
      this.itemMetadata = compound.getInteger("itemMetadata");
      int g = compound.getInteger("gDir");
      GravityDirection[] values = GravityDirection.values();
      this.gravityDirection = g >= 0 && g < values.length ? values[g] : GravityDirection.upTOdown_YN;
      this.relatedBlockX = compound.getInteger("relatedBlockX");
      this.relatedBlockY = compound.getInteger("relatedBlockY");
      this.relatedBlockZ = compound.getInteger("relatedBlockZ");
      this.isSubBlock = compound.getBoolean("isSubBlock");
   }

   @Override
   public NBTTagCompound writeToNBT(NBTTagCompound compound) {
      super.writeToNBT(compound);
      ResourceLocation bn = Block.REGISTRY.getNameForObject(this.storedBlock);
      ResourceLocation in = Item.REGISTRY.getNameForObject(this.storedItem);
      compound.setString("block", bn == null ? "minecraft:air" : bn.toString());
      compound.setString("item", in == null ? "minecraft:air" : in.toString());
      compound.setInteger("itemMetadata", this.itemMetadata);
      compound.setInteger("gDir", this.gravityDirection.ordinal());
      compound.setInteger("relatedBlockX", this.relatedBlockX);
      compound.setInteger("relatedBlockY", this.relatedBlockY);
      compound.setInteger("relatedBlockZ", this.relatedBlockZ);
      compound.setBoolean("isSubBlock", this.isSubBlock);
      return compound;
   }

   @Override
   public void onDataPacket(NetworkManager net, SPacketUpdateTileEntity pkt) {
      Block before = this.storedBlock;
      this.readFromNBT(pkt.getNbtCompound());
      if (this.world != null && this.storedBlock != before) {
         this.world.checkLight(this.pos);
      }
   }

   @Override
   public SPacketUpdateTileEntity getUpdatePacket() {
      return new SPacketUpdateTileEntity(this.pos, 0, this.getUpdateTag());
   }

   @Override
   public NBTTagCompound getUpdateTag() {
      return this.writeToNBT(new NBTTagCompound());
   }
}
