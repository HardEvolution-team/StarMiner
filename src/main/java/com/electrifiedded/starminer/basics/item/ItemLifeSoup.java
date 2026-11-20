package com.electrifiedded.starminer.basics.item;

import com.electrifiedded.starminer.basics.SMModContainer;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.NonNullList;

public class ItemLifeSoup extends Item {
   public static final int LIFESOUPTYPE_ANIMAL = 0;
   public static final int LIFESOUPTYPE_DIRTY = 1;
   public static final int LIFESOUPTYPE_VILLAGER = 2;
   public static final String[] SUB_NAMES = new String[]{"animal", "dirty", "villager"};

   public ItemLifeSoup() {
      this.setMaxStackSize(64);
      this.setMaxDamage(0);
      this.setHasSubtypes(true);
   }

   @Override
   public String getTranslationKey(ItemStack itemStack) {
      int i = itemStack.getMetadata();
      if (i < 0 || i >= SUB_NAMES.length) {
         i = 0;
      }

      return super.getTranslationKey() + "." + SUB_NAMES[i];
   }

   @Override
   public void getSubItems(CreativeTabs tab, NonNullList<ItemStack> items) {
      if (this.isInCreativeTab(tab)) {
         for (int i = 0; i < SUB_NAMES.length; i++) {
            items.add(new ItemStack(this, 1, i));
         }
      }
   }

   public static boolean isStackLifeSeedAnimal(ItemStack itemStack) {
      return !itemStack.isEmpty() && itemStack.getItem() == SMModContainer.LifeSoupItem && itemStack.getMetadata() == 0;
   }

   public static boolean isStackLifeSeedAnimalDirty(ItemStack itemStack) {
      return !itemStack.isEmpty() && itemStack.getItem() == SMModContainer.LifeSoupItem && itemStack.getMetadata() == 1;
   }

   public static boolean isStackLifeSeedVillager(ItemStack itemStack) {
      return !itemStack.isEmpty() && itemStack.getItem() == SMModContainer.LifeSoupItem && itemStack.getMetadata() == 2;
   }
}
