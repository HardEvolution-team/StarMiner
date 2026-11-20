package com.electrifiedded.starminer.basics.item.block;

import net.minecraft.block.Block;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;

public class ItemPlantG extends ItemBlock {
   private final String[] names;

   public ItemPlantG(Block block, String[] names) {
      super(block);
      this.names = names;
      this.setHasSubtypes(true);
      this.setMaxDamage(0);
   }

   @Override
   public int getMetadata(int damage) {
      return damage;
   }

   @Override
   public String getTranslationKey(ItemStack stack) {
      int i = stack.getMetadata();
      if (i < 0 || i >= this.names.length) {
         i = 0;
      }

      return super.getTranslationKey() + "." + this.names[i];
   }
}
