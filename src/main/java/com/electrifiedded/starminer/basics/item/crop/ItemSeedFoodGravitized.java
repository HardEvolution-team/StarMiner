package com.electrifiedded.starminer.basics.item.crop;

import net.minecraft.block.Block;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemFood;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class ItemSeedFoodGravitized extends ItemFood {
   private final Block crop;

   public ItemSeedFoodGravitized(Block crop, int heal, float saturation) {
      super(heal, saturation, false);
      this.crop = crop;
   }

   @Override
   public EnumActionResult onItemUse(EntityPlayer player, World world, BlockPos pos, EnumHand hand, EnumFacing facing, float hitX, float hitY, float hitZ) {
      return GravitizedSeedHelper.plant(this.crop, player, world, pos, hand, facing);
   }
}
