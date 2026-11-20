package com.electrifiedded.starminer.basics.item;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ActionResult;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumHand;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;

public class ItemGravityContoler extends Item {
   public static final String G_REVERSE_NBT_TAG = "stmn.g_reverse";

   public ItemGravityContoler() {
      this.setMaxStackSize(1);
      this.addPropertyOverride(new ResourceLocation("starminer", "reversed"), (stack, world, entity) -> isReversed(stack) ? 1.0F : 0.0F);
   }

   public static boolean isReversed(ItemStack stack) {
      return stack.hasTagCompound() && stack.getTagCompound().getBoolean(G_REVERSE_NBT_TAG);
   }

   @Override
   public ActionResult<ItemStack> onItemRightClick(World world, EntityPlayer player, EnumHand hand) {
      ItemStack itemStack = player.getHeldItem(hand);
      NBTTagCompound tag = itemStack.getTagCompound();
      if (tag == null) {
         tag = new NBTTagCompound();
         itemStack.setTagCompound(tag);
      }

      tag.setBoolean(G_REVERSE_NBT_TAG, !tag.getBoolean(G_REVERSE_NBT_TAG));
      return new ActionResult<>(EnumActionResult.SUCCESS, itemStack);
   }
}
