package com.electrifiedded.starminer.basics.structure;

import java.util.Random;
import com.electrifiedded.starminer.basics.SMModContainer;
import com.electrifiedded.starminer.basics.block.SMDir;
import com.electrifiedded.starminer.basics.block.gravitized.GravityPlants;
import com.electrifiedded.starminer.basics.block.gravitized.IGravitizedPlants;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;
import net.minecraft.util.EnumFacing;

public final class StarTrees {
   public static final int OAK = 0;
   public static final int SPRUCE = 1;
   public static final int BIRCH = 2;
   public static final int JUNGLE = 3;
   public static final int ACACIA = 4;
   public static final int ROOFED_OAK = 5;
   private static final int[] DIR_OFFSET_X = {0, -1, 0, 1};
   private static final int[] DIR_OFFSET_Z = {1, 0, -1, 0};

   private final StarAccess acc;
   private final Random rand;
   private final int dir;
   private int sx;
   private int sy;
   private int sz;
   private int tx;
   private int ty;
   private int tz;

   private StarTrees(StarAccess acc, Random rand, int dir) {
      this.acc = acc;
      this.rand = rand;
      this.dir = dir;
   }

   public static void generate(StarAccess acc, Random rand, int species, int minHeight, int dir, int x, int y, int z) {
      StarTrees t = new StarTrees(acc, rand, dir);
      t.sx = x;
      t.sy = y;
      t.sz = z;
      switch (species) {
         case SPRUCE:
            t.spruce(minHeight, x, y, z);
            break;
         case BIRCH:
            t.birch(minHeight, x, y, z);
            break;
         case ACACIA:
            t.acacia(x, y, z);
            break;
         case JUNGLE:
            t.broadleaf(minHeight, Blocks.LOG, Blocks.LEAVES, 3, x, y, z);
            break;
         case ROOFED_OAK:
            t.broadleaf(minHeight, Blocks.LOG2, Blocks.LEAVES2, 1, x, y, z);
            break;
         default:
            t.broadleaf(minHeight, Blocks.LOG, Blocks.LEAVES, 0, x, y, z);
      }
   }

   private void translate(int ax, int ay, int az) {
      switch (this.dir) {
         case 1:
            this.tx = ax;
            this.ty = this.sy - (ay - this.sy);
            this.tz = az;
            break;
         case 2:
            this.tx = this.sx + (ay - this.sy);
            this.ty = this.sy + (az - this.sz);
            this.tz = this.sz + (ax - this.sx);
            break;
         case 3:
            this.tx = this.sx - (ay - this.sy);
            this.ty = this.sy + (az - this.sz);
            this.tz = this.sz + (ax - this.sx);
            break;
         case 4:
            this.tx = this.sx + (az - this.sz);
            this.ty = this.sy + (ax - this.sx);
            this.tz = this.sz + (ay - this.sy);
            break;
         case 5:
            this.tx = this.sx + (az - this.sz);
            this.ty = this.sy + (ax - this.sx);
            this.tz = this.sz - (ay - this.sy);
            break;
         default:
            this.tx = ax;
            this.ty = ay;
            this.tz = az;
      }
   }

   private int woodMeta(int meta) {
      int type = meta & 3;
      switch (this.dir) {
         case 2:
         case 3:
            return type | 4;
         case 4:
         case 5:
            return type | 8;
         default:
            return meta;
      }
   }

   private void put(IBlockState state) {
      if (!this.acc.inside(this.tx, this.ty, this.tz)) return;
      IBlockState old = this.acc.get(this.tx, this.ty, this.tz);
      if (old.getBlock() == SMModContainer.AirExBlock) {
         EnumFacing grow = GravityPlants.GROW[Math.min(5, old.getValue(SMDir.DIR))];
         int px = this.tx - grow.getXOffset();
         int py = this.ty - grow.getYOffset();
         int pz = this.tz - grow.getZOffset();
         if (this.acc.inside(px, py, pz) && this.acc.get(px, py, pz).getBlock() instanceof IGravitizedPlants) {
            this.acc.set(px, py, pz, Blocks.AIR.getDefaultState());
         }
      }

      this.acc.set(this.tx, this.ty, this.tz, state);
   }

   @SuppressWarnings("deprecation")
   private void leaf(Block leaves, int meta) {
      this.put(leaves.getStateFromMeta(meta));
   }

   @SuppressWarnings("deprecation")
   private void log(Block log, int meta) {
      this.put(log.getStateFromMeta(this.woodMeta(meta)));
   }

   private void broadleaf(int minHeight, Block logBlock, Block leafBlock, int meta, int posX, int posY, int posZ) {
      int height = this.rand.nextInt(3) + minHeight;
      for (int y = posY - 3 + height; y <= posY + height; y++) {
         int k1 = y - (posY + height);
         int i2 = 1 - k1 / 2;
         for (int x = posX - i2; x <= posX + i2; x++) {
            int k2 = x - posX;
            for (int z = posZ - i2; z <= posZ + i2; z++) {
               int i3 = z - posZ;
               if (Math.abs(k2) != i2 || Math.abs(i3) != i2 || this.rand.nextInt(2) != 0 && k1 != 0) {
                  this.translate(x, y, z);
                  this.leaf(leafBlock, meta);
               }
            }
         }
      }

      for (int i = 0; i < height; i++) {
         this.translate(posX, posY + i, posZ);
         this.log(logBlock, meta);
      }
   }

   private void birch(int minHeight, int posX, int posY, int posZ) {
      int height = this.rand.nextInt(3) + minHeight;
      for (int y = posY - 3 + height; y <= posY + height; y++) {
         int k = y - (posY + height);
         int r = 1 - k / 2;
         for (int x = posX - r; x <= posX + r; x++) {
            int j2 = x - posX;
            for (int z = posZ - r; z <= posZ + r; z++) {
               int l2 = z - posZ;
               if (Math.abs(j2) != r || Math.abs(l2) != r || this.rand.nextInt(2) != 0 && k != 0) {
                  this.translate(x, y, z);
                  this.leaf(Blocks.LEAVES, 2);
               }
            }
         }
      }

      for (int i = 0; i < height; i++) {
         this.translate(posX, posY + i, posZ);
         this.log(Blocks.LOG, 2);
      }
   }

   private void spruce(int minHeight, int posX, int posY, int posZ) {
      int height = this.rand.nextInt(4) + minHeight;
      int i1 = 1 + this.rand.nextInt(2);
      int j1 = height - i1;
      int k1 = 2 + this.rand.nextInt(2);
      int k2 = this.rand.nextInt(2);
      int xCnt = 1;
      int b0 = 0;
      for (int j2 = 0; j2 <= j1; j2++) {
         int y = posY + height - j2;
         for (int x = posX - k2; x <= posX + k2; x++) {
            int k3 = x - posX;
            for (int z = posZ - k2; z <= posZ + k2; z++) {
               int i4 = z - posZ;
               if (Math.abs(k3) != k2 || Math.abs(i4) != k2 || k2 <= 0) {
                  this.translate(x, y, z);
                  this.leaf(Blocks.LEAVES, 1);
               }
            }
         }

         if (k2 >= xCnt) {
            k2 = b0;
            b0 = 1;
            if (++xCnt > k1) {
               xCnt = k1;
            }
         } else {
            k2++;
         }
      }

      int cut = this.rand.nextInt(3);
      for (int j3 = 0; j3 < height - cut; j3++) {
         this.translate(posX, posY + j3, posZ);
         this.log(Blocks.LOG, 1);
      }
   }

   private void acacia(int posX, int posY, int posZ) {
      int l = this.rand.nextInt(3) + this.rand.nextInt(3) + 5;
      int j3 = this.rand.nextInt(4);
      int xCnt = l - this.rand.nextInt(4) - 1;
      int k1 = 3 - this.rand.nextInt(3);
      int k3 = posX;
      int l1 = posZ;
      int top = 0;
      for (int j2 = 0; j2 < l; j2++) {
         int k2 = posY + j2;
         if (j2 >= xCnt && k1 > 0) {
            k3 += DIR_OFFSET_X[j3];
            l1 += DIR_OFFSET_Z[j3];
            k1--;
         }

         this.translate(k3, k2, l1);
         this.log(Blocks.LOG2, 0);
         top = k2;
      }

      for (int a = -1; a <= 1; a++) {
         for (int b = -1; b <= 1; b++) {
            this.translate(k3 + a, top + 1, l1 + b);
            this.leaf(Blocks.LEAVES2, 0);
         }
      }

      this.translate(k3 + 2, top + 1, l1);
      this.leaf(Blocks.LEAVES2, 0);
      this.translate(k3 - 2, top + 1, l1);
      this.leaf(Blocks.LEAVES2, 0);
      this.translate(k3, top + 1, l1 + 2);
      this.leaf(Blocks.LEAVES2, 0);
      this.translate(k3, top + 1, l1 - 2);
      this.leaf(Blocks.LEAVES2, 0);
      for (int a = -3; a <= 3; a++) {
         for (int b = -3; b <= 3; b++) {
            if (Math.abs(a) != 3 || Math.abs(b) != 3) {
               this.translate(k3 + a, top, l1 + b);
               this.leaf(Blocks.LEAVES2, 0);
            }
         }
      }

      k3 = posX;
      l1 = posZ;
      int d2 = this.rand.nextInt(4);
      if (d2 != j3) {
         int k2 = xCnt - this.rand.nextInt(2) - 1;
         int l3 = 1 + this.rand.nextInt(3);
         top = 0;
         for (int l2 = k2; l2 < l && l3 > 0; l3--) {
            if (l2 >= 1) {
               int i3 = posY + l2;
               k3 += DIR_OFFSET_X[d2];
               l1 += DIR_OFFSET_Z[d2];
               this.translate(k3, i3, l1);
               this.log(Blocks.LOG2, 0);
               top = i3;
            }

            l2++;
         }

         if (top > 0) {
            for (int a = -1; a <= 1; a++) {
               for (int b = -1; b <= 1; b++) {
                  this.translate(k3 + a, top + 1, l1 + b);
                  this.leaf(Blocks.LEAVES2, 0);
               }
            }

            for (int a = -2; a <= 2; a++) {
               for (int b = -2; b <= 2; b++) {
                  if (Math.abs(a) != 2 || Math.abs(b) != 2) {
                     this.translate(k3 + a, top, l1 + b);
                     this.leaf(Blocks.LEAVES2, 0);
                  }
               }
            }
         }
      }
   }
}
