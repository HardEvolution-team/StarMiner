package com.electrifiedded.starminer.basics.common;

import java.nio.FloatBuffer;
import com.electrifiedded.starminer.api.IRotateSleepingViewHandler;
import com.electrifiedded.starminer.basics.Port2Registry;
import com.electrifiedded.starminer.basics.block.SMDir;
import com.electrifiedded.starminer.basics.block.bed.BlockStarBedHead;
import com.electrifiedded.starminer.basics.block.bed.StarBedUtil;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GLAllocation;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class SleepingViewHandler implements IRotateSleepingViewHandler {
   private static final float CAMERA_FIX = 0.6675F;

   private static final FloatBuffer BUFFER = GLAllocation.createDirectFloatBuffer(16);

   private static BlockPos headOf(EntityPlayer player) {
      BlockPos bed = player.bedLocation;
      if (bed == null || player.world.getBlockState(bed).getBlock() != Port2Registry.StarBedHeadBlock) {
         return null;
      }

      return bed;
   }

   @Override
   public boolean rotateSleepingFPView() {
      EntityPlayer player = Minecraft.getMinecraft().player;
      if (player == null) {
         return false;
      }

      World world = player.world;
      BlockPos pos = headOf(player);
      if (pos == null) {
         return false;
      }

      int conDir = ((BlockStarBedHead)Port2Registry.StarBedHeadBlock).getConnectionDirection(world, pos);
      if (conDir == -1) {
         return true;
      }

      conDir = StarBedUtil.OPPOSITE_CNV[conDir];
      int gravDir = world.getBlockState(pos).getValue(BlockStarBedHead.GRAV);
      switch (gravDir) {
         case 0:
            switch (conDir) {
               case 2:
                  GlStateManager.rotate(90.0F, 0.0F, 1.0F, 0.0F);
                  break;
               case 3:
                  GlStateManager.rotate(-90.0F, 0.0F, 1.0F, 0.0F);
                  break;
               case 4:
                  GlStateManager.rotate(180.0F, 0.0F, 1.0F, 0.0F);
               default:
            }

            GlStateManager.translate(0.0F, -0.5F, 0.0F);
            break;
         case 1:
            GlStateManager.rotate(180.0F, 1.0F, 0.0F, 0.0F);
            switch (conDir) {
               case 2:
                  GlStateManager.rotate(-90.0F, 0.0F, 1.0F, 0.0F);
                  break;
               case 3:
                  GlStateManager.rotate(90.0F, 0.0F, 1.0F, 0.0F);
               case 4:
               default:
                  break;
               case 5:
                  GlStateManager.rotate(180.0F, 0.0F, 1.0F, 0.0F);
            }

            GlStateManager.translate(0.0F, 0.5F, 0.0F);
            break;
         case 2:
            GlStateManager.rotate(90.0F, 0.0F, 1.0F, 0.0F);
            GlStateManager.rotate(90.0F, 0.0F, 0.0F, 1.0F);
            switch (conDir) {
               case 0:
                  GlStateManager.rotate(-180.0F, 1.0F, 0.0F, 0.0F);
               case 1:
               case 2:
               case 3:
               default:
                  break;
               case 4:
                  GlStateManager.rotate(90.0F, 1.0F, 0.0F, 0.0F);
                  break;
               case 5:
                  GlStateManager.rotate(-90.0F, 1.0F, 0.0F, 0.0F);
            }

            GlStateManager.translate(-0.5F, 0.0F, 0.0F);
            break;
         case 3:
            GlStateManager.rotate(-90.0F, 0.0F, 1.0F, 0.0F);
            GlStateManager.rotate(-90.0F, 0.0F, 0.0F, 1.0F);
            switch (conDir) {
               case 0:
                  GlStateManager.rotate(-180.0F, 1.0F, 0.0F, 0.0F);
               case 1:
               case 2:
               case 3:
               default:
                  break;
               case 4:
                  GlStateManager.rotate(90.0F, 1.0F, 0.0F, 0.0F);
                  break;
               case 5:
                  GlStateManager.rotate(-90.0F, 1.0F, 0.0F, 0.0F);
            }

            GlStateManager.translate(0.5F, 0.0F, 0.0F);
            break;
         case 4:
            GlStateManager.rotate(180.0F, 0.0F, 1.0F, 0.0F);
            GlStateManager.rotate(-90.0F, 1.0F, 0.0F, 0.0F);
            switch (conDir) {
               case 0:
                  GlStateManager.rotate(-180.0F, 0.0F, 0.0F, 1.0F);
               case 1:
               default:
                  break;
               case 2:
                  GlStateManager.rotate(-90.0F, 0.0F, 0.0F, 1.0F);
                  break;
               case 3:
                  GlStateManager.rotate(90.0F, 0.0F, 0.0F, 1.0F);
            }

            GlStateManager.translate(0.0F, 0.0F, -0.5F);
            break;
         case 5:
            GlStateManager.rotate(90.0F, 1.0F, 0.0F, 0.0F);
            switch (conDir) {
               case 0:
                  GlStateManager.rotate(-180.0F, 0.0F, 0.0F, 1.0F);
               case 1:
               default:
                  break;
               case 2:
                  GlStateManager.rotate(-90.0F, 0.0F, 0.0F, 1.0F);
                  break;
               case 3:
                  GlStateManager.rotate(90.0F, 0.0F, 0.0F, 1.0F);
            }

            GlStateManager.translate(0.0F, 0.0F, 0.5F);
      }

      GlStateManager.translate(0.0F, CAMERA_FIX, 0.0F);
      return true;
   }

   @Override
   public boolean rotateTPPlayerSleeping(EntityPlayer player) {
      BlockPos pos = headOf(player);
      if (pos == null) {
         return false;
      }

      int conn = ((BlockStarBedHead)Port2Registry.StarBedHeadBlock).getConnectionDirection(player.world, pos);
      if (conn == -1) {
         return false;
      }

      int gravDir = player.world.getBlockState(pos).getValue(BlockStarBedHead.GRAV);
      EnumFacing ground = SMDir.ATTACH[gravDir];
      double[] u = {-ground.getXOffset(), -ground.getYOffset(), -ground.getZOffset()};
      int[] c = StarBedUtil.CHECKNEIGHBOR_LIST[conn];
      double[] h = {c[0], c[1], c[2]};
      double[] x = {u[1] * h[2] - u[2] * h[1], u[2] * h[0] - u[0] * h[2], u[0] * h[1] - u[1] * h[0]};
      double[] z = {-u[0], -u[1], -u[2]};
      double[][] wanted = new double[3][3];
      for (int r = 0; r < 3; r++) {
         wanted[r][0] = x[r];
         wanted[r][1] = h[r];
         wanted[r][2] = z[r];
      }

      double[][] vanilla = mul(mul(rot(1, player.getBedOrientationInDegrees()), rot(2, 90.0)), rot(1, 270.0));
      double[][] m = mul(wanted, transpose(vanilla));
      BUFFER.clear();
      for (int col = 0; col < 3; col++) {
         for (int row = 0; row < 3; row++) {
            BUFFER.put((float)m[row][col]);
         }

         BUFFER.put(0.0F);
      }

      BUFFER.put(0.0F).put(0.0F).put(0.0F).put(1.0F);
      BUFFER.flip();
      GlStateManager.multMatrix(BUFFER);
      return true;
   }

   private static double[][] rot(int axis, double deg) {
      double a = Math.toRadians(deg);
      double c = Math.cos(a);
      double s = Math.sin(a);
      switch (axis) {
         case 0:
            return new double[][]{{1, 0, 0}, {0, c, -s}, {0, s, c}};
         case 1:
            return new double[][]{{c, 0, s}, {0, 1, 0}, {-s, 0, c}};
         default:
            return new double[][]{{c, -s, 0}, {s, c, 0}, {0, 0, 1}};
      }
   }

   private static double[][] mul(double[][] a, double[][] b) {
      double[][] r = new double[3][3];
      for (int i = 0; i < 3; i++) {
         for (int j = 0; j < 3; j++) {
            for (int k = 0; k < 3; k++) {
               r[i][j] += a[i][k] * b[k][j];
            }
         }
      }

      return r;
   }

   private static double[][] transpose(double[][] a) {
      double[][] r = new double[3][3];
      for (int i = 0; i < 3; i++) {
         for (int j = 0; j < 3; j++) {
            r[i][j] = a[j][i];
         }
      }

      return r;
   }
}
