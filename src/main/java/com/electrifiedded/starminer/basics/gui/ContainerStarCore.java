package com.electrifiedded.starminer.basics.gui;

import com.electrifiedded.starminer.basics.SMModContainer;
import com.electrifiedded.starminer.basics.tileentity.TileEntityGravityGenerator;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class ContainerStarCore extends Container {
   public TileEntityGravityGenerator tileEntityGrav;
   public EntityPlayer entityplayer;
   private final World world;
   private final BlockPos pos;

   public ContainerStarCore(EntityPlayer player, TileEntityGravityGenerator te) {
      this.entityplayer = player;
      InventoryPlayer inv = player.inventory;
      this.tileEntityGrav = te;
      this.world = te.getWorld();
      this.pos = te.getPos();
      int i = 37;

      for (int j = 0; j < 3; j++) {
         for (int k = 0; k < 9; k++) {
            this.addSlotToContainer(new Slot(te, k + j * 9, 8 + k * 18, 72 + j * 18));
         }
      }

      this.addSlotToContainer(new Slot(te, 27, 142, 36));

      for (int j = 0; j < 3; j++) {
         for (int k = 0; k < 9; k++) {
            this.addSlotToContainer(new Slot(inv, k + j * 9 + 9, 8 + k * 18, 103 + j * 18 + i));
         }
      }

      for (int k = 0; k < 9; k++) {
         this.addSlotToContainer(new Slot(inv, k, 8 + k * 18, 161 + i));
      }
   }

   @Override
   public boolean canInteractWith(EntityPlayer player) {
      return this.world.getBlockState(this.pos).getBlock() == SMModContainer.GravityCoreBlock
         && player.getDistanceSq(this.pos.getX() + 0.5, this.pos.getY() + 0.5, this.pos.getZ() + 0.5) < 4096.0;
   }

   @Override
   public ItemStack transferStackInSlot(EntityPlayer player, int index) {
      ItemStack itemstack = ItemStack.EMPTY;
      Slot slot = this.inventorySlots.get(index);
      if (slot != null && slot.getHasStack()) {
         ItemStack itemstack1 = slot.getStack();
         itemstack = itemstack1.copy();
         if (index < 28) {
            if (!this.mergeItemStack(itemstack1, 28, this.inventorySlots.size(), true)) {
               return ItemStack.EMPTY;
            }
         } else if (!this.mergeItemStack(itemstack1, 0, 27, false)) {
            return ItemStack.EMPTY;
         }

         if (itemstack1.isEmpty()) {
            slot.putStack(ItemStack.EMPTY);
         } else {
            slot.onSlotChanged();
         }
      }

      return itemstack;
   }

   @Override
   public boolean enchantItem(EntityPlayer player, int id) {
      this.receiveButtonAction(id);
      return true;
   }

   public void receiveButtonAction(int id) {
      if (this.world.isRemote || this.tileEntityGrav == null || this.world.getTileEntity(this.pos) != this.tileEntityGrav) {
         return;
      }

      boolean resetWorkState = false;
      switch (id) {
         case 1:
            this.tileEntityGrav.gravityRange++;
            break;
         case 2:
            this.tileEntityGrav.gravityRange--;
            break;
         case 3:
            this.tileEntityGrav.gravityRange += 5.0;
            break;
         case 4:
            this.tileEntityGrav.gravityRange -= 5.0;
            break;
         case 5:
            this.tileEntityGrav.starRad++;
            resetWorkState = true;
            break;
         case 6:
            this.tileEntityGrav.starRad--;
            resetWorkState = true;
            break;
         case 7:
            this.tileEntityGrav.starRad += 5.0;
            resetWorkState = true;
            break;
         case 8:
            this.tileEntityGrav.starRad -= 5.0;
            resetWorkState = true;
            break;
         case 9:
            this.tileEntityGrav.type = (this.tileEntityGrav.type + 1) % TileEntityGravityGenerator.TYPE_NUM;
            resetWorkState = true;
            break;
         case 10:
            this.tileEntityGrav.useBufferArea = !this.tileEntityGrav.useBufferArea;
      }

      this.tileEntityGrav.fixInValidRange();
      if (resetWorkState) {
         this.tileEntityGrav.resetWorkState();
      }

      this.tileEntityGrav.sync();
   }
}
