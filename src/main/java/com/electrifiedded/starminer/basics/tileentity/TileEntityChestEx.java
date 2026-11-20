package com.electrifiedded.starminer.basics.tileentity;

import com.electrifiedded.starminer.api.GravityDirection;
import com.electrifiedded.starminer.basics.block.BlockChestEx;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.init.SoundEvents;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.ContainerChest;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.InventoryLargeChest;
import net.minecraft.inventory.ItemStackHelper;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.play.server.SPacketUpdateTileEntity;
import net.minecraft.tileentity.TileEntityLockable;
import net.minecraft.util.ITickable;
import net.minecraft.util.NonNullList;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;

public class TileEntityChestEx extends TileEntityLockable implements ITickable {
   private NonNullList<ItemStack> chestContents = NonNullList.withSize(27, ItemStack.EMPTY);
   public float lidAngle;
   public float prevLidAngle;
   public int numPlayersUsing;
   private int ticksSinceSync;
   private int cachedChestType = -1;
   private String customName;
   public static int IS_adjacentChestZNeg = 0;
   public static int IS_adjacentChestXPos = 1;
   public static int IS_adjacentChestXNeg = 2;
   public static int IS_adjacentChestZPos = 3;
   public static int IS_adjacentChestNone = 4;
   public boolean isSubBlock = false;
   public int relatedBlockX;
   public int relatedBlockY;
   public int relatedBlockZ;
   private GravityDirection gravityDirection = GravityDirection.upTOdown_YN;
   public int[] conv = new int[3];

   public int getAdjacentChestTo() {
      GravityDirection direOpp = GravityDirection.turnWayForNormal(this.gravityDirection);
      direOpp.rotateXYZAt(this.conv, this.relatedBlockX, this.relatedBlockY, this.relatedBlockZ, this.pos.getX(), this.pos.getY(), this.pos.getZ());
      int xDiff = this.conv[0] - this.pos.getX();
      int zDiff = this.conv[2] - this.pos.getZ();
      if (xDiff < 0) {
         return IS_adjacentChestXNeg;
      } else if (xDiff > 0) {
         return IS_adjacentChestXPos;
      } else if (zDiff < 0) {
         return IS_adjacentChestZNeg;
      } else {
         return zDiff > 0 ? IS_adjacentChestZPos : IS_adjacentChestNone;
      }
   }

   @Override
   public int getSizeInventory() {
      return 27;
   }

   @Override
   public boolean isEmpty() {
      for (ItemStack s : this.chestContents) {
         if (!s.isEmpty()) {
            return false;
         }
      }
      return true;
   }

   @Override
   public ItemStack getStackInSlot(int index) {
      return this.chestContents.get(index);
   }

   @Override
   public ItemStack decrStackSize(int index, int count) {
      ItemStack s = ItemStackHelper.getAndSplit(this.chestContents, index, count);
      if (!s.isEmpty()) {
         this.markDirty();
      }
      return s;
   }

   @Override
   public ItemStack removeStackFromSlot(int index) {
      return ItemStackHelper.getAndRemove(this.chestContents, index);
   }

   @Override
   public void setInventorySlotContents(int index, ItemStack stack) {
      this.chestContents.set(index, stack);
      if (stack.getCount() > this.getInventoryStackLimit()) {
         stack.setCount(this.getInventoryStackLimit());
      }
      this.markDirty();
   }

   @Override
   public String getName() {
      return this.hasCustomName() ? this.customName : "container.chest";
   }

   @Override
   public boolean hasCustomName() {
      return this.customName != null && this.customName.length() > 0;
   }

   public void setCustomName(String name) {
      this.customName = name;
   }

   @Override
   public void readFromNBT(NBTTagCompound compound) {
      super.readFromNBT(compound);
      this.chestContents = NonNullList.withSize(this.getSizeInventory(), ItemStack.EMPTY);
      ItemStackHelper.loadAllItems(compound, this.chestContents);
      if (compound.hasKey("CustomName", 8)) {
         this.customName = compound.getString("CustomName");
      }
      this.readOriginalNBTData(compound);
   }

   public void readOriginalNBTData(NBTTagCompound compound) {
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
      ItemStackHelper.saveAllItems(compound, this.chestContents);
      if (this.hasCustomName()) {
         compound.setString("CustomName", this.customName);
      }
      this.writeOriginalNBTData(compound);
      return compound;
   }

   public void writeOriginalNBTData(NBTTagCompound compound) {
      compound.setInteger("gDir", this.gravityDirection.ordinal());
      compound.setInteger("relatedBlockX", this.relatedBlockX);
      compound.setInteger("relatedBlockY", this.relatedBlockY);
      compound.setInteger("relatedBlockZ", this.relatedBlockZ);
      compound.setBoolean("isSubBlock", this.isSubBlock);
   }

   @Override
   public int getInventoryStackLimit() {
      return 64;
   }

   @Override
   public boolean isUsableByPlayer(EntityPlayer player) {
      return this.world.getTileEntity(this.pos) == this
         && player.getDistanceSq(this.pos.getX() + 0.5, this.pos.getY() + 0.5, this.pos.getZ() + 0.5) <= 64.0;
   }

   @Override
   public void update() {
      int adjacentChestInt = this.getAdjacentChestTo();
      boolean dontPlaySound = adjacentChestInt == IS_adjacentChestZNeg || adjacentChestInt == IS_adjacentChestXNeg;
      int x = this.pos.getX();
      int y = this.pos.getY();
      int z = this.pos.getZ();
      this.ticksSinceSync++;
      if (!this.world.isRemote && this.numPlayersUsing != 0 && (this.ticksSinceSync + x + y + z) % 200 == 0) {
         this.numPlayersUsing = 0;
         float f = 5.0F;
         for (EntityPlayer entityplayer : this.world.getEntitiesWithinAABB(
            EntityPlayer.class, new AxisAlignedBB(x - f, y - f, z - f, x + 1 + f, y + 1 + f, z + 1 + f))) {
            if (entityplayer.openContainer instanceof ContainerChest) {
               IInventory iinventory = ((ContainerChest)entityplayer.openContainer).getLowerChestInventory();
               if (iinventory == this || iinventory instanceof InventoryLargeChest && ((InventoryLargeChest)iinventory).isPartOfLargeChest(this)) {
                  this.numPlayersUsing++;
               }
            }
         }
      }

      this.prevLidAngle = this.lidAngle;
      float f = 0.1F;
      if (this.numPlayersUsing > 0 && this.lidAngle == 0.0F && !dontPlaySound) {
         this.world.playSound(null, x + 0.5, y + 0.5, z + 0.5, SoundEvents.BLOCK_CHEST_OPEN, SoundCategory.BLOCKS, 0.5F,
            this.world.rand.nextFloat() * 0.1F + 0.9F);
      }

      if (this.numPlayersUsing == 0 && this.lidAngle > 0.0F || this.numPlayersUsing > 0 && this.lidAngle < 1.0F) {
         float f1 = this.lidAngle;
         if (this.numPlayersUsing > 0) {
            this.lidAngle += f;
         } else {
            this.lidAngle -= f;
         }

         if (this.lidAngle > 1.0F) {
            this.lidAngle = 1.0F;
         }

         float f2 = 0.5F;
         if (this.lidAngle < f2 && f1 >= f2 && !dontPlaySound) {
            this.world.playSound(null, x + 0.5, y + 0.5, z + 0.5, SoundEvents.BLOCK_CHEST_CLOSE, SoundCategory.BLOCKS, 0.5F,
               this.world.rand.nextFloat() * 0.1F + 0.9F);
         }

         if (this.lidAngle < 0.0F) {
            this.lidAngle = 0.0F;
         }
      }
   }

   @Override
   public boolean receiveClientEvent(int id, int type) {
      if (id == 1) {
         this.numPlayersUsing = type;
         return true;
      }
      return super.receiveClientEvent(id, type);
   }

   @Override
   public void openInventory(EntityPlayer player) {
      if (!player.isSpectator()) {
         if (this.numPlayersUsing < 0) {
            this.numPlayersUsing = 0;
         }
         this.numPlayersUsing++;
         this.world.addBlockEvent(this.pos, this.getBlockType(), 1, this.numPlayersUsing);
         this.world.notifyNeighborsOfStateChange(this.pos, this.getBlockType(), false);
         this.world.notifyNeighborsOfStateChange(this.pos.down(), this.getBlockType(), false);
      }
   }

   @Override
   public void closeInventory(EntityPlayer player) {
      if (!player.isSpectator() && this.getBlockType() instanceof BlockChestEx) {
         this.numPlayersUsing--;
         this.world.addBlockEvent(this.pos, this.getBlockType(), 1, this.numPlayersUsing);
         this.world.notifyNeighborsOfStateChange(this.pos, this.getBlockType(), false);
         this.world.notifyNeighborsOfStateChange(this.pos.down(), this.getBlockType(), false);
      }
   }

   @Override
   public boolean isItemValidForSlot(int index, ItemStack stack) {
      return true;
   }

   public int getChestType() {
      if (this.cachedChestType == -1) {
         if (this.world == null || !(this.getBlockType() instanceof BlockChestEx)) {
            return 0;
         }
         this.cachedChestType = ((BlockChestEx)this.getBlockType()).chestType;
      }
      return this.cachedChestType;
   }

   @Override
   public void onDataPacket(NetworkManager net, SPacketUpdateTileEntity pkt) {
      this.readOriginalNBTData(pkt.getNbtCompound());
   }

   @Override
   public SPacketUpdateTileEntity getUpdatePacket() {
      return new SPacketUpdateTileEntity(this.pos, 0, this.getUpdateTag());
   }

   @Override
   public NBTTagCompound getUpdateTag() {
      NBTTagCompound tag = super.writeToNBT(new NBTTagCompound());
      this.writeOriginalNBTData(tag);
      return tag;
   }

   @Override
   public void handleUpdateTag(NBTTagCompound tag) {
      this.readOriginalNBTData(tag);
   }

   public GravityDirection getGravityDirection() {
      return this.gravityDirection;
   }

   public void setGravityDirection(GravityDirection gDir) {
      this.gravityDirection = gDir;
   }

   public boolean hasRelated() {
      return this.relatedBlockX != this.pos.getX() || this.relatedBlockY != this.pos.getY() || this.relatedBlockZ != this.pos.getZ();
   }

   public BlockPos getRelatedPos() {
      return new BlockPos(this.relatedBlockX, this.relatedBlockY, this.relatedBlockZ);
   }

   @Override
   public String getGuiID() {
      return "minecraft:chest";
   }

   @Override
   public Container createContainer(InventoryPlayer playerInventory, EntityPlayer playerIn) {
      return new ContainerChest(playerInventory, this, playerIn);
   }

   @Override
   public int getField(int id) {
      return 0;
   }

   @Override
   public void setField(int id, int value) {
   }

   @Override
   public int getFieldCount() {
      return 0;
   }

   @Override
   public void clear() {
      this.chestContents.clear();
   }
}
