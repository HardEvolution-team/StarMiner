package com.electrifiedded.starminer.basics.block.gravitized;

import com.electrifiedded.starminer.basics.SMModContainer;
import com.electrifiedded.starminer.basics.block.SMDir;
import net.minecraft.block.state.IBlockState;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;

public final class GravityPlants {
   public static final EnumFacing[] GROW = {EnumFacing.UP, EnumFacing.DOWN, EnumFacing.EAST, EnumFacing.WEST, EnumFacing.SOUTH, EnumFacing.NORTH};

   private GravityPlants() {
   }

   public static int dirOf(EnumFacing growFacing) {
      for (int i = 0; i < 6; i++) {
         if (GROW[i] == growFacing) return i;
      }
      return 0;
   }

   public static boolean isMarker(IBlockAccess world, BlockPos pos, int dir) {
      IBlockState s = world.getBlockState(pos);
      return s.getBlock() == SMModContainer.AirExBlock && s.getValue(SMDir.DIR) == dir;
   }

   public static int getPlantGravityDirection(IBlockAccess world, BlockPos pos) {
      for (int i = 0; i < 6; i++) {
         if (isMarker(world, pos.offset(GROW[i]), i)) {
            return i;
         }
      }
      return -1;
   }

   public static EnumFacing getGrowFacing(IBlockAccess world, BlockPos pos) {
      int dir = getPlantGravityDirection(world, pos);
      return dir < 0 ? EnumFacing.UP : GROW[dir];
   }

   public static void setMarker(World world, BlockPos pos, int dir, int flags) {
      world.setBlockState(pos, SMModContainer.AirExBlock.getDefaultState().withProperty(SMDir.DIR, dir), flags);
   }

   public static void markForPlacement(World world, BlockPos plantPos, EnumFacing growFacing) {
      BlockPos m = plantPos.offset(growFacing);
      if (world.isAirBlock(m)) {
         setMarker(world, m, dirOf(growFacing), 3);
      }
   }

   public static boolean isStayableAtOppositeSide(World world, BlockPos pos, int dir, IBlockState plantState, IGravitizedPlants plant) {
      if (dir < 0 || dir > 5) return false;
      return plant.allowPlantOn(world.getBlockState(pos.offset(GROW[dir].getOpposite())), plantState);
   }

   public static boolean tryStayable(World world, BlockPos pos, IBlockState plantState, IGravitizedPlants plant) {
      for (int dir = 0; dir < 6; dir++) {
         if (plant.allowPlantOn(world.getBlockState(pos.offset(GROW[dir].getOpposite())), plantState)) {
            BlockPos m = pos.offset(GROW[dir]);
            if (world.getBlockState(m).getBlock() == net.minecraft.init.Blocks.AIR) {
               setMarker(world, m, dir, 2);
               return true;
            }
         }
      }
      return false;
   }

   public static boolean canBlockStay(World world, BlockPos pos, IBlockState state, IGravitizedPlants plant) {
      int dir = getPlantGravityDirection(world, pos);
      if (dir == -1) {
         return tryStayable(world, pos, state, plant);
      }
      return isStayableAtOppositeSide(world, pos, dir, state, plant) || tryStayable(world, pos, state, plant);
   }

   public static AxisAlignedBB box(int dir, double w, double h) {
      double lo = 0.5 - w;
      double hi = 0.5 + w;
      double top = 1.0 - h;
      switch (dir) {
         case 1: return new AxisAlignedBB(lo, top, lo, hi, 1.0, hi);
         case 2: return new AxisAlignedBB(0.0, lo, lo, h, hi, hi);
         case 3: return new AxisAlignedBB(top, lo, lo, 1.0, hi, hi);
         case 4: return new AxisAlignedBB(lo, lo, 0.0, hi, hi, h);
         case 5: return new AxisAlignedBB(lo, lo, top, hi, hi, 1.0);
         default: return new AxisAlignedBB(lo, 0.0, lo, hi, h, hi);
      }
   }
}
