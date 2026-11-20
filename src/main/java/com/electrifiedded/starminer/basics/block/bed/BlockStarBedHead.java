package com.electrifiedded.starminer.basics.block.bed;

import javax.annotation.Nullable;
import com.electrifiedded.starminer.basics.Port2Registry;
import net.minecraft.block.Block;
import net.minecraft.block.properties.PropertyBool;
import net.minecraft.block.properties.PropertyInteger;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;

public class BlockStarBedHead extends BlockStarBedBase {
   public static final double HEIGHT_D = StarBedUtil.HEIGHT_D;
   public static final PropertyInteger GRAV = PropertyInteger.create("grav", 0, 5);
   public static final PropertyBool OCCUPIED = PropertyBool.create("occupied");
   public static final PropertyInteger HEAD_CONN = PropertyInteger.create("conn", 0, 6);

   public BlockStarBedHead() {
      this.setDefaultState(this.blockState.getBaseState().withProperty(GRAV, 0).withProperty(OCCUPIED, false).withProperty(HEAD_CONN, 6));
   }

   @Override
   protected BlockStateContainer createBlockState() {
      return new BlockStateContainer(this, GRAV, OCCUPIED, HEAD_CONN);
   }

   @Override
   public IBlockState getStateFromMeta(int meta) {
      return this.getDefaultState().withProperty(GRAV, Math.min(meta & 7, 5)).withProperty(OCCUPIED, (meta & 8) != 0);
   }

   @Override
   public int getMetaFromState(IBlockState state) {
      return state.getValue(GRAV) | (state.getValue(OCCUPIED) ? 8 : 0);
   }

   @Override
   public IBlockState getActualState(IBlockState state, IBlockAccess worldIn, BlockPos pos) {
      int c = StarBedUtil.headConnectionDirection(worldIn, pos);
      return state.withProperty(HEAD_CONN, c < 0 ? 6 : c);
   }

   @Override
   protected boolean isHead() {
      return true;
   }

   @Override
   public int getGravityDirection(IBlockAccess world, BlockPos pos, IBlockState state) {
      return state.getBlock() == this ? state.getValue(GRAV) : -1;
   }

   public int getConnectionDirection(IBlockAccess world, BlockPos pos) {
      return StarBedUtil.headConnectionDirection(world, pos);
   }

   @Override
   protected BlockPos getHeadPos(IBlockAccess world, BlockPos pos, IBlockState state) {
      return pos;
   }

   @Override
   public int getArchaicRenderKey(IBlockState state) {
      int c = state.getValue(HEAD_CONN);
      return StarBedModel.key(state.getValue(GRAV), c == 6 ? -1 : c);
   }

   @Override
   public void neighborChanged(IBlockState state, World world, BlockPos pos, Block blockIn, BlockPos fromPos) {
      if (StarBedUtil.headNeighborBody(world, pos) < 0) {
         world.setBlockToAir(pos);
      }
   }

   @Nullable
   @Override
   public BlockPos getBedSpawnPosition(IBlockState state, IBlockAccess world, BlockPos pos, @Nullable EntityPlayer player) {
      return StarBedUtil.getNearestEmptyPos(world, pos);
   }

   public static boolean isBedOccupiedHead(int meta) {
      return (meta & 8) != 0;
   }

   public static void setBedOccupiedHead(IBlockAccess world, BlockPos pos, boolean occupied) {
      if (world instanceof World) {
         IBlockState s = world.getBlockState(pos);
         if (s.getBlock() == Port2Registry.StarBedHeadBlock) {
            ((World)world).setBlockState(pos, s.withProperty(OCCUPIED, occupied), 4);
         }
      }
   }

   @Override
   public void setBedOccupied(IBlockAccess world, BlockPos pos, EntityPlayer player, boolean occupied) {
      setBedOccupiedHead(world, pos, occupied);
   }

   @Override
   public void onBlockHarvested(World world, BlockPos pos, IBlockState state, EntityPlayer player) {
      if (player.capabilities.isCreativeMode && StarBedUtil.headNeighborBody(world, pos) < 0) {
         world.setBlockToAir(pos);
      }
   }

   public static void setPositionForStarBedSleepingPlayer(EntityPlayer player, BlockPos headPos, int gravDir, int connDir) {
      double setPad = 1.3;
      double up = 0.1875;
      int[] h = StarBedUtil.CHECKNEIGHBOR_LIST[connDir];
      double x = headPos.getX() + 0.5 - setPad * h[0];
      double y = headPos.getY() + 0.5 - setPad * h[1];
      double z = headPos.getZ() + 0.5 - setPad * h[2];
      net.minecraft.util.EnumFacing ground = com.electrifiedded.starminer.basics.block.SMDir.ATTACH[gravDir];
      x -= up * ground.getXOffset();
      y -= up * ground.getYOffset();
      z -= up * ground.getZOffset();
      player.setPositionAndRotation(x, y, z, 0.0F, 0.0F);
   }
}
