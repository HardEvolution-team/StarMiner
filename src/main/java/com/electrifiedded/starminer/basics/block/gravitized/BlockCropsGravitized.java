package com.electrifiedded.starminer.basics.block.gravitized;

import java.util.Random;
import com.electrifiedded.starminer.basics.SMModContainer;
import net.minecraft.block.Block;
import net.minecraft.block.IGrowable;
import net.minecraft.block.properties.PropertyInteger;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.NonNullList;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;

public class BlockCropsGravitized extends BlockBushGravitized implements IGrowable {
   public static final PropertyInteger AGE = PropertyInteger.create("age", 0, 7);
   private final int kind;

   public BlockCropsGravitized(int kind) {
      super(0.5, 0.25);
      this.kind = kind;
      this.setTickRandomly(true);
      this.setDefaultState(this.blockState.getBaseState().withProperty(AGE, 0).withProperty(FACING, net.minecraft.util.EnumFacing.UP));
   }

   public BlockCropsGravitized() {
      this(0);
   }

   @Override
   protected BlockStateContainer createBlockState() {
      return new BlockStateContainer(this, AGE, FACING);
   }

   @Override
   public IBlockState getStateFromMeta(int meta) {
      return this.getDefaultState().withProperty(AGE, meta & 7);
   }

   @Override
   public int getMetaFromState(IBlockState state) {
      return state.getValue(AGE);
   }

   @Override
   public boolean allowPlantOn(IBlockState support, IBlockState plantState) {
      Block block = support.getBlock();
      return block == Blocks.FARMLAND
         || block == SMModContainer.DirtGrassExBlock && support.getValue(com.electrifiedded.starminer.basics.block.BlockDirtGrassEx.TILLED);
   }

   @Override
   public void updateTick(World world, BlockPos pos, IBlockState state, Random rand) {
      this.checkAndDropBlock(world, pos, state);
      if (world.getBlockState(pos).getBlock() != this) return;
      if (world.getLightFromNeighbors(pos.offset(GravityPlants.getGrowFacing(world, pos))) >= 7) {
         int l = state.getValue(AGE);
         if (l < 7 && rand.nextInt((int)(25.0F / 3.0F) + 1) == 0) {
            world.setBlockState(pos, state.withProperty(AGE, l + 1), 2);
         }
      }
   }

   @Override
   public void getDrops(NonNullList<ItemStack> drops, IBlockAccess world, BlockPos pos, IBlockState state, int fortune) {
      int age = state.getValue(AGE);
      Random rand = world instanceof World ? ((World)world).rand : RANDOM;
      drops.add(new ItemStack(age >= 7 ? this.getCrop() : this.getSeed()));
      if (age >= 7) {
         for (int n = 0; n < 3 + fortune; n++) {
            if (rand.nextInt(15) <= age) {
               drops.add(new ItemStack(this.getSeed()));
            }
         }
      }
   }

   @Override
   public Item getItemDropped(IBlockState state, Random rand, int fortune) {
      return state.getValue(AGE) >= 7 ? this.getCrop() : this.getSeed();
   }

   protected Item getSeed() {
      switch (this.kind) {
         case 1: return SMModContainer.CarrotGravizedItem;
         case 2: return SMModContainer.PotatoGravizedItem;
         default: return SMModContainer.SeedGravizedItem;
      }
   }

   protected Item getCrop() {
      switch (this.kind) {
         case 1: return SMModContainer.CarrotGravizedItem;
         case 2: return SMModContainer.PotatoGravizedItem;
         default: return Items.WHEAT;
      }
   }

   @Override
   public ItemStack getPickBlock(IBlockState state, net.minecraft.util.math.RayTraceResult target, World world, BlockPos pos, net.minecraft.entity.player.EntityPlayer player) {
      return new ItemStack(this.getSeed());
   }

   @Override
   public boolean canGrow(World world, BlockPos pos, IBlockState state, boolean isClient) {
      return state.getValue(AGE) < 7;
   }

   @Override
   public boolean canUseBonemeal(World world, Random rand, BlockPos pos, IBlockState state) {
      return true;
   }

   @Override
   public void grow(World world, Random rand, BlockPos pos, IBlockState state) {
      int l = Math.min(7, state.getValue(AGE) + MathHelper.getInt(world.rand, 2, 5));
      world.setBlockState(pos, state.withProperty(AGE, l), 2);
   }

   @Override
   public int getArchaicRenderType(int key) {
      return RENDER_CROPS;
   }

   @Override
   protected String[] getPlantIconNames() {
      String[] n = new String[8];
      int[] stage = {0, 0, 1, 1, 2, 2, 2, 3};
      for (int i = 0; i < 8; i++) {
         n[i] = this.kind == 0 ? "minecraft:wheat_stage_" + i : (this.kind == 1 ? "minecraft:carrots_stage_" : "minecraft:potatoes_stage_") + stage[i];
      }
      return n;
   }
}
