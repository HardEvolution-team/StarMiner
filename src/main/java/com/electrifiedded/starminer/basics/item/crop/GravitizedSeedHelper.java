package com.electrifiedded.starminer.basics.item.crop;

import com.electrifiedded.starminer.basics.block.gravitized.BlockBushGravitized;
import com.electrifiedded.starminer.basics.block.gravitized.GravityPlants;
import net.minecraft.block.Block;
import net.minecraft.block.SoundType;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

final class GravitizedSeedHelper {
   private GravitizedSeedHelper() {
   }

   static EnumActionResult plant(Block crop, EntityPlayer player, World world, BlockPos pos, EnumHand hand, EnumFacing facing) {
      ItemStack stack = player.getHeldItem(hand);
      BlockPos target = pos.offset(facing);
      if (!player.canPlayerEdit(target, facing, stack) || !world.isAirBlock(target) || !(crop instanceof BlockBushGravitized)) {
         return EnumActionResult.FAIL;
      }

      IBlockState support = world.getBlockState(pos);
      IBlockState cropState = crop.getDefaultState();
      if (!((BlockBushGravitized)crop).allowPlantOn(support, cropState)) {
         return EnumActionResult.FAIL;
      }

      if (!world.isRemote) {
         world.setBlockState(target, cropState, 3);
         GravityPlants.markForPlacement(world, target, facing);
      }

      SoundType sound = SoundType.PLANT;
      world.playSound(player, target, sound.getPlaceSound(), SoundCategory.BLOCKS, (sound.getVolume() + 1.0F) / 2.0F, sound.getPitch() * 0.8F);
      if (!player.capabilities.isCreativeMode) {
         stack.shrink(1);
      }

      return EnumActionResult.SUCCESS;
   }
}
