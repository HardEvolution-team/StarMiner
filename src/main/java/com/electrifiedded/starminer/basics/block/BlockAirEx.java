package com.electrifiedded.starminer.basics.block;

import com.electrifiedded.archaiclib.v1710.api.block.IArchaicRenderBlock;
import com.electrifiedded.archaiclib.v1710.api.client.IIcon;
import com.electrifiedded.archaiclib.v1710.api.client.IIconRegister;

import java.util.Random;
import javax.annotation.Nullable;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.BlockFaceShape;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.item.Item;
import net.minecraft.util.EnumBlockRenderType;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;

public class BlockAirEx extends Block implements IArchaicRenderBlock {
   public BlockAirEx() {
      super(Material.AIR);
      this.setDefaultState(this.blockState.getBaseState().withProperty(SMDir.DIR, 0));
   }

   @Override
   protected BlockStateContainer createBlockState() {
      return new BlockStateContainer(this, SMDir.DIR);
   }

   @Override
   public IBlockState getStateFromMeta(int meta) {
      return this.getDefaultState().withProperty(SMDir.DIR, Math.min(meta, 5));
   }

   @Override
   public int getMetaFromState(IBlockState state) {
      return state.getValue(SMDir.DIR);
   }

   @Override
   public EnumBlockRenderType getRenderType(IBlockState state) {
      return EnumBlockRenderType.INVISIBLE;
   }

   @Nullable
   @Override
   public AxisAlignedBB getCollisionBoundingBox(IBlockState blockState, IBlockAccess worldIn, BlockPos pos) {
      return NULL_AABB;
   }

   @Override
   public boolean isOpaqueCube(IBlockState state) {
      return false;
   }

   @Override
   public boolean isFullCube(IBlockState state) {
      return false;
   }

   @Override
   public boolean canCollideCheck(IBlockState state, boolean hitIfLiquid) {
      return false;
   }

   @Override
   public void dropBlockAsItemWithChance(World worldIn, BlockPos pos, IBlockState state, float chance, int fortune) {
   }

   @Override
   public Item getItemDropped(IBlockState state, Random rand, int fortune) {
      return net.minecraft.init.Items.AIR;
   }

   @Override
   public boolean isReplaceable(IBlockAccess worldIn, BlockPos pos) {
      return true;
   }

   @Override
   public boolean isAir(IBlockState state, IBlockAccess world, BlockPos pos) {
      return true;
   }

   @Override
   public BlockFaceShape getBlockFaceShape(IBlockAccess worldIn, IBlockState state, BlockPos pos, EnumFacing face) {
      return BlockFaceShape.UNDEFINED;
   }
}
