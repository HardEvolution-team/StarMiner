package com.electrifiedded.starminer.basics.block.gravitized;

import java.util.Random;
import net.minecraft.block.BlockLeaves;
import net.minecraft.block.BlockLog;
import net.minecraft.block.BlockNewLeaf;
import net.minecraft.block.BlockNewLog;
import net.minecraft.block.BlockOldLeaf;
import net.minecraft.block.BlockOldLog;
import net.minecraft.block.BlockPlanks;
import net.minecraft.block.IGrowable;
import net.minecraft.block.properties.PropertyInteger;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.NonNullList;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class BlockSaplingGravitized extends BlockBushGravitized implements IGrowable {
   public static final PropertyInteger TYPE = PropertyInteger.create("type", 0, 5);
   public static final PropertyInteger STAGE = PropertyInteger.create("stage", 0, 1);
   public static final String[] NAMES = {"oak", "spruce", "birch", "jungle", "acacia", "roofed_oak"};

   public BlockSaplingGravitized() {
      super(0.4, 0.8);
      this.setTickRandomly(true);
      this.setDefaultState(this.blockState.getBaseState().withProperty(TYPE, 0).withProperty(STAGE, 0).withProperty(FACING, EnumFacing.UP));
   }

   @Override
   protected BlockStateContainer createBlockState() {
      return new BlockStateContainer(this, TYPE, STAGE, FACING);
   }

   @Override
   public IBlockState getStateFromMeta(int meta) {
      return this.getDefaultState().withProperty(TYPE, Math.min(meta & 7, 5)).withProperty(STAGE, (meta & 8) >> 3);
   }

   @Override
   public int getMetaFromState(IBlockState state) {
      return state.getValue(TYPE) | state.getValue(STAGE) << 3;
   }

   @Override
   public int damageDropped(IBlockState state) {
      return state.getValue(TYPE);
   }

   @Override
   public void getSubBlocks(CreativeTabs tab, NonNullList<ItemStack> items) {
      for (int i = 0; i < NAMES.length; i++) {
         items.add(new ItemStack(this, 1, i));
      }
   }

   @Override
   public void updateTick(World world, BlockPos pos, IBlockState state, Random rand) {
      if (!world.isRemote) {
         super.updateTick(world, pos, state, rand);
         if (world.getBlockState(pos).getBlock() == this && world.getLightFromNeighbors(pos.offset(GravityPlants.getGrowFacing(world, pos))) >= 9 && rand.nextInt(7) == 0) {
            this.grow(world, rand, pos, state);
         }
      }
   }

   @Override
   public boolean canGrow(World world, BlockPos pos, IBlockState state, boolean isClient) {
      return true;
   }

   @Override
   public boolean canUseBonemeal(World world, Random rand, BlockPos pos, IBlockState state) {
      return world.rand.nextFloat() < 0.45;
   }

   @Override
   public void grow(World world, Random rand, BlockPos pos, IBlockState state) {
      if (state.getValue(STAGE) == 0) {
         world.setBlockState(pos, state.withProperty(STAGE, 1), 4);
      } else {
         this.generateTree(world, pos, state, rand);
      }
   }

   private static IBlockState logState(int type, EnumFacing.Axis axis) {
      BlockLog.EnumAxis a = BlockLog.EnumAxis.fromFacingAxis(axis);
      if (type < 4) {
         return Blocks.LOG.getDefaultState().withProperty(BlockOldLog.VARIANT, BlockPlanks.EnumType.byMetadata(type)).withProperty(BlockLog.LOG_AXIS, a);
      }
      return Blocks.LOG2.getDefaultState().withProperty(BlockNewLog.VARIANT, BlockPlanks.EnumType.byMetadata(type)).withProperty(BlockLog.LOG_AXIS, a);
   }

   private static IBlockState leafState(int type) {
      if (type < 4) {
         return Blocks.LEAVES.getDefaultState().withProperty(BlockOldLeaf.VARIANT, BlockPlanks.EnumType.byMetadata(type)).withProperty(BlockLeaves.CHECK_DECAY, false);
      }
      return Blocks.LEAVES2.getDefaultState().withProperty(BlockNewLeaf.VARIANT, BlockPlanks.EnumType.byMetadata(type)).withProperty(BlockLeaves.CHECK_DECAY, false);
   }

   public void generateTree(World world, BlockPos pos, IBlockState state, Random rand) {
      int type = state.getValue(TYPE);
      EnumFacing up = GravityPlants.getGrowFacing(world, pos);
      int height = (type == 3 ? 7 : type == 1 ? 6 : 4) + rand.nextInt(3);
      for (int i = 1; i <= height; i++) {
         BlockPos p = pos.offset(up, i);
         IBlockState s = world.getBlockState(p);
         if (!s.getBlock().isReplaceable(world, p) && !s.getBlock().isLeaves(s, world, p)) {
            return;
         }
      }

      IBlockState log = logState(type, up.getAxis());
      IBlockState leaves = leafState(type);
      EnumFacing[] side = sidesOf(up);
      world.setBlockState(pos, log, 3);
      for (int i = 1; i < height; i++) {
         world.setBlockState(pos.offset(up, i), log, 3);
      }

      for (int layer = height - 3; layer <= height; layer++) {
         int r = layer >= height - 1 ? 1 : 2;
         BlockPos center = pos.offset(up, layer);
         for (int a = -r; a <= r; a++) {
            for (int b = -r; b <= r; b++) {
               if (Math.abs(a) == r && Math.abs(b) == r && (rand.nextInt(2) == 0 || layer == height)) {
                  continue;
               }

               BlockPos p = center.offset(side[0], a).offset(side[1], b);
               IBlockState s = world.getBlockState(p);
               if (s.getBlock().isAir(s, world, p) || s.getBlock().isLeaves(s, world, p) || s.getBlock().isReplaceable(world, p)) {
                  world.setBlockState(p, leaves, 3);
               }
            }
         }
      }
   }

   private static EnumFacing[] sidesOf(EnumFacing up) {
      switch (up.getAxis()) {
         case X: return new EnumFacing[]{EnumFacing.UP, EnumFacing.SOUTH};
         case Z: return new EnumFacing[]{EnumFacing.UP, EnumFacing.EAST};
         default: return new EnumFacing[]{EnumFacing.EAST, EnumFacing.SOUTH};
      }
   }

   @Override
   protected String[] getPlantIconNames() {
      return new String[]{"minecraft:sapling_oak", "minecraft:sapling_spruce", "minecraft:sapling_birch", "minecraft:sapling_jungle", "minecraft:sapling_acacia",
         "minecraft:sapling_roofed_oak"};
   }

   @Override
   protected int iconIndex(int meta) {
      return meta & 7;
   }
}
