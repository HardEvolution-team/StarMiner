package com.electrifiedded.starminer.basics.block.bed;

import com.electrifiedded.starminer.basics.Port2Registry;
import com.electrifiedded.starminer.basics.SMModContainer;
import com.electrifiedded.starminer.basics.block.SMDir;
import net.minecraft.block.state.IBlockState;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;

public final class StarBedUtil {
   public static final int[][] CHECKNEIGHBOR_LIST = new int[][]{{0, 1, 0, 0}, {0, -1, 0, 1}, {1, 0, 0, 2}, {-1, 0, 0, 3}, {0, 0, 1, 4}, {0, 0, -1, 5}};
   public static final int[] OPPOSITE_CNV = new int[]{1, 0, 3, 2, 5, 4};
   public static final double HEIGHT_D = 0.5625;

   private StarBedUtil() {
   }

   public static BlockPos offset(BlockPos pos, int dirIdx) {
      int[] o = CHECKNEIGHBOR_LIST[dirIdx];
      return pos.add(o[0], o[1], o[2]);
   }

   public static net.minecraft.util.math.AxisAlignedBB boxFor(int dir) {
      if (dir < 0 || dir > 5) {
         return net.minecraft.block.Block.FULL_BLOCK_AABB;
      }
      return SMDir.slab(dir, HEIGHT_D);
   }

   public static int headNeighborBody(IBlockAccess world, BlockPos pos) {
      for (int i = 0; i < 6; i++) {
         BlockPos n = offset(pos, i);
         IBlockState s = world.getBlockState(n);
         if (s.getBlock() == Port2Registry.StarBedBodyBlock && s.getValue(BlockStarBedBody.CONN) == OPPOSITE_CNV[i]) {
            return i;
         }
      }

      return -1;
   }

   public static int headConnectionDirection(IBlockAccess world, BlockPos headPos) {
      int rel = headNeighborBody(world, headPos);
      return rel < 0 ? -1 : world.getBlockState(offset(headPos, rel)).getValue(BlockStarBedBody.CONN);
   }

   public static int bodyGravityDirection(IBlockAccess world, BlockPos bodyPos, int conn) {
      BlockPos hp = offset(bodyPos, conn);
      IBlockState s = world.getBlockState(hp);
      return s.getBlock() == Port2Registry.StarBedHeadBlock ? s.getValue(BlockStarBedHead.GRAV) : -1;
   }

   private static final int[] CHECK_HEIGHT_LIST = new int[]{0, -1, 1, 2};

   public static boolean isAirBlockOrGravityWall(IBlockAccess world, BlockPos pos) {
      return world.isAirBlock(pos) || world.getBlockState(pos).getBlock() == SMModContainer.GravityWallBlock;
   }

   public static BlockPos getNearestEmptyPos(IBlockAccess world, BlockPos headPos) {
      IBlockState hs = world.getBlockState(headPos);
      if (hs.getBlock() != Port2Registry.StarBedHeadBlock) {
         return null;
      }

      int gravDir = hs.getValue(BlockStarBedHead.GRAV);
      int parX = headPos.getX();
      int parY = headPos.getY();
      int parZ = headPos.getZ();
      for (int height : CHECK_HEIGHT_LIST) {
         for (int r = 1; r <= 3; r++) {
            for (int a = -r; a <= r; a++) {
               for (int b = -r; b <= r; b++) {
                  BlockPos solid;
                  BlockPos feet;
                  BlockPos head;
                  EnumFacing face;
                  switch (gravDir) {
                     case 0:
                        solid = new BlockPos(parX + a, parY - 1 + height, parZ + b);
                        feet = new BlockPos(parX + a, parY + height, parZ + b);
                        head = new BlockPos(parX + a, parY + 1 + height, parZ + b);
                        face = EnumFacing.UP;
                        break;
                     case 1:
                        solid = new BlockPos(parX + a, parY + 1 - height, parZ + b);
                        feet = new BlockPos(parX + a, parY - height, parZ + b);
                        head = new BlockPos(parX + a, parY - 1 - height, parZ + b);
                        face = EnumFacing.DOWN;
                        break;
                     case 2:
                        solid = new BlockPos(parX - 1 + height, parY + b, parZ + a);
                        feet = new BlockPos(parX + height, parY + b, parZ + a);
                        head = new BlockPos(parX + 1 + height, parY + b, parZ + a);
                        face = EnumFacing.EAST;
                        break;
                     case 3:
                        solid = new BlockPos(parX + 1 - height, parY + b, parZ + a);
                        feet = new BlockPos(parX - height, parY + b, parZ + a);
                        head = new BlockPos(parX - 1 - height, parY + b, parZ + a);
                        face = EnumFacing.WEST;
                        break;
                     case 4:
                        solid = new BlockPos(parX + a, parY + b, parZ - 1 + height);
                        feet = new BlockPos(parX + a, parY + b, parZ + height);
                        head = new BlockPos(parX + a, parY + b, parZ + 1 + height);
                        face = EnumFacing.SOUTH;
                        break;
                     default:
                        solid = new BlockPos(parX + a, parY + b, parZ + 1 - height);
                        feet = new BlockPos(parX + a, parY + b, parZ - height);
                        head = new BlockPos(parX + a, parY + b, parZ - 1 - height);
                        face = EnumFacing.NORTH;
                  }

                  if (world.isSideSolid(solid, face, false) && isAirBlockOrGravityWall(world, feet) && world.isAirBlock(head)) {
                     return head;
                  }
               }
            }
         }
      }

      return null;
   }
}
