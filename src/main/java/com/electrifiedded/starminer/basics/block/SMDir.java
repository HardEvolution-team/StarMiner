package com.electrifiedded.starminer.basics.block;

import net.minecraft.block.properties.PropertyInteger;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.AxisAlignedBB;

public final class SMDir {
   public static final PropertyInteger DIR = PropertyInteger.create("dir", 0, 5);
   public static final EnumFacing[] ATTACH = {EnumFacing.DOWN, EnumFacing.UP, EnumFacing.WEST, EnumFacing.EAST, EnumFacing.NORTH, EnumFacing.SOUTH};
   private static final int[] SIDE_TO_META = {1, 0, 5, 4, 3, 2};

   private SMDir() {
   }

   public static int metaFromClickedSide(EnumFacing side) {
      return SIDE_TO_META[side.getIndex()];
   }

   public static int metaFromAttach(EnumFacing attach) {
      for (int i = 0; i < 6; i++) {
         if (ATTACH[i] == attach) return i;
      }
      return 0;
   }

   public static AxisAlignedBB slab(int dir, double t) {
      double hi = 1.0 - t;
      switch (dir) {
         case 1: return new AxisAlignedBB(0.0, hi, 0.0, 1.0, 1.0, 1.0);
         case 2: return new AxisAlignedBB(0.0, 0.0, 0.0, t, 1.0, 1.0);
         case 3: return new AxisAlignedBB(hi, 0.0, 0.0, 1.0, 1.0, 1.0);
         case 4: return new AxisAlignedBB(0.0, 0.0, 0.0, 1.0, 1.0, t);
         case 5: return new AxisAlignedBB(0.0, 0.0, hi, 1.0, 1.0, 1.0);
         default: return new AxisAlignedBB(0.0, 0.0, 0.0, 1.0, t, 1.0);
      }
   }
}
