package com.electrifiedded.starminer.basics.block.gravitized;

import java.util.Random;
import net.minecraft.block.properties.PropertyInteger;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.NonNullList;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.common.IShearable;

public class BlockTallGrassGravitized extends BlockBushGravitized implements IShearable {
   public static final PropertyInteger TYPE = PropertyInteger.create("type", 0, 2);
   public static final String[] NAMES = {"shrub", "grass", "fern"};

   public BlockTallGrassGravitized() {
      super(0.4, 0.8);
      this.setDefaultState(this.blockState.getBaseState().withProperty(TYPE, 1).withProperty(FACING, EnumFacing.UP));
   }

   @Override
   protected BlockStateContainer createBlockState() {
      return new BlockStateContainer(this, TYPE, FACING);
   }

   @Override
   public IBlockState getStateFromMeta(int meta) {
      return this.getDefaultState().withProperty(TYPE, Math.min(meta & 3, 2));
   }

   @Override
   public int getMetaFromState(IBlockState state) {
      return state.getValue(TYPE);
   }

   @Override
   public boolean isReplaceable(IBlockAccess worldIn, BlockPos pos) {
      return true;
   }

   @Override
   public Item getItemDropped(IBlockState state, Random rand, int fortune) {
      return rand.nextInt(8) == 0 ? Items.WHEAT_SEEDS : Items.AIR;
   }

   @Override
   public void getSubBlocks(CreativeTabs tab, NonNullList<ItemStack> items) {
      for (int i = 0; i < NAMES.length; i++) {
         items.add(new ItemStack(this, 1, i));
      }
   }

   @Override
   public boolean isShearable(ItemStack item, IBlockAccess world, BlockPos pos) {
      return true;
   }

   @Override
   public java.util.List<ItemStack> onSheared(ItemStack item, IBlockAccess world, BlockPos pos, int fortune) {
      java.util.List<ItemStack> ret = new java.util.ArrayList<>();
      ret.add(new ItemStack(this, 1, world.getBlockState(pos).getValue(TYPE)));
      return ret;
   }

   @Override
   public boolean canBlockStay(World world, BlockPos pos, IBlockState state) {
      return GravityPlants.canBlockStay(world, pos, state, this);
   }

   @Override
   protected String[] getPlantIconNames() {
      return new String[]{"minecraft:deadbush", "minecraft:tallgrass", "minecraft:fern"};
   }

   @Override
   public boolean hasArchaicTint() {
      return true;
   }

   @Override
   public int colorMultiplier(net.minecraft.world.IBlockAccess world, BlockPos pos) {
      return net.minecraft.world.biome.BiomeColorHelper.getGrassColorAtPos(world, pos);
   }

   @Override
   public int getRenderColor(int meta) {
      return net.minecraft.world.ColorizerGrass.getGrassColor(0.5, 1.0);
   }
}
