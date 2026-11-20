package com.electrifiedded.starminer.basics.block.bed;

import javax.annotation.Nullable;
import com.electrifiedded.starminer.basics.Port2Registry;
import net.minecraft.block.Block;
import net.minecraft.block.properties.PropertyInteger;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;

public class BlockStarBedBody extends BlockStarBedBase {
   public static final double HEIGHT_D = StarBedUtil.HEIGHT_D;
   public static final PropertyInteger CONN = PropertyInteger.create("conn", 0, 5);
   public static final PropertyInteger BODY_GRAV = PropertyInteger.create("grav", 0, 6);

   public BlockStarBedBody() {
      this.setDefaultState(this.blockState.getBaseState().withProperty(CONN, 0).withProperty(BODY_GRAV, 6));
   }

   @Override
   protected BlockStateContainer createBlockState() {
      return new BlockStateContainer(this, CONN, BODY_GRAV);
   }

   @Override
   public IBlockState getStateFromMeta(int meta) {
      return this.getDefaultState().withProperty(CONN, Math.min(meta & 7, 5));
   }

   @Override
   public int getMetaFromState(IBlockState state) {
      return state.getValue(CONN);
   }

   @Override
   public IBlockState getActualState(IBlockState state, IBlockAccess worldIn, BlockPos pos) {
      int g = StarBedUtil.bodyGravityDirection(worldIn, pos, state.getValue(CONN));
      return state.withProperty(BODY_GRAV, g < 0 ? 6 : g);
   }

   @Override
   protected boolean isHead() {
      return false;
   }

   @Override
   public int getGravityDirection(IBlockAccess world, BlockPos pos, IBlockState state) {
      return state.getBlock() == this ? StarBedUtil.bodyGravityDirection(world, pos, state.getValue(CONN)) : -1;
   }

   @Override
   protected BlockPos getHeadPos(IBlockAccess world, BlockPos pos, IBlockState state) {
      return StarBedUtil.offset(pos, state.getValue(CONN));
   }

   @Override
   public int getArchaicRenderKey(IBlockState state) {
      int g = state.getValue(BODY_GRAV);
      return StarBedModel.key(g == 6 ? -1 : g, state.getValue(CONN));
   }

   @Override
   public void neighborChanged(IBlockState state, World world, BlockPos pos, Block blockIn, BlockPos fromPos) {
      if (world.getBlockState(StarBedUtil.offset(pos, state.getValue(CONN))).getBlock() != Port2Registry.StarBedHeadBlock) {
         world.setBlockToAir(pos);
      }
   }

   @Nullable
   @Override
   public BlockPos getBedSpawnPosition(IBlockState state, IBlockAccess world, BlockPos pos, @Nullable EntityPlayer player) {
      return StarBedUtil.getNearestEmptyPos(world, pos);
   }

   @Override
   public void setBedOccupied(IBlockAccess world, BlockPos pos, EntityPlayer player, boolean occupied) {
      BlockPos hp = StarBedUtil.offset(pos, world.getBlockState(pos).getValue(CONN));
      if (world.getBlockState(hp).getBlock() == Port2Registry.StarBedHeadBlock) {
         BlockStarBedHead.setBedOccupiedHead(world, hp, occupied);
      }
   }
}
