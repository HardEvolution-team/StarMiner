package com.electrifiedded.starminer.basics.dimention;

import java.util.Random;
import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;
import net.minecraft.world.chunk.ChunkPrimer;

public class MapGenAsteroidBelt {
   private static final int RANGE = 8;
   private final Random rand = new Random();

   public void generate(World world, int xChunkPos, int zChunkPos, ChunkPrimer primer) {
      this.rand.setSeed(world.getSeed());
      long l = this.rand.nextLong();
      long i1 = this.rand.nextLong();
      for (int workChunkX = xChunkPos - RANGE; workChunkX <= xChunkPos + RANGE; workChunkX++) {
         for (int workChunkZ = zChunkPos - RANGE; workChunkZ <= zChunkPos + RANGE; workChunkZ++) {
            long l1 = workChunkX * l;
            long i2 = workChunkZ * i1;
            this.rand.setSeed(l1 ^ i2 ^ world.getSeed());
            this.recursiveGenerate(workChunkX, workChunkZ, xChunkPos, zChunkPos, primer);
         }
      }
   }

   private void recursiveGenerate(int workChunkX, int workChunkZ, int xChunkPos, int zChunkPos, ChunkPrimer primer) {
      int count = this.rand.nextInt(this.rand.nextInt(this.rand.nextInt(4) + 1) + 1);
      if (this.rand.nextInt(15) != 0) {
         count = 0;
      }

      for (int j1 = 0; j1 < count; j1++) {
         double workPosX = workChunkX * 16 + this.rand.nextInt(16);
         double workPosY = this.rand.nextInt(this.rand.nextInt(230) + 15);
         double workPosZ = workChunkZ * 16 + this.rand.nextInt(16);
         float yaw = this.rand.nextFloat() * (float)Math.PI * 2.0F;
         float pitch = (this.rand.nextFloat() - 0.5F) * 2.0F / 8.0F;
         float size = this.rand.nextFloat() * 2.0F + this.rand.nextFloat();
         this.generateNode(this.rand.nextLong(), xChunkPos, zChunkPos, primer, workPosX, workPosY, workPosZ, size, yaw, pitch);
      }
   }

   private void generateNode(long seed, int xChunkPos, int zChunkPos, ChunkPrimer primer, double workPosX, double workPosY, double workPosZ,
      float size, float yaw, float pitch) {
      double chunkCenterX = xChunkPos * 16 + 8;
      double chunkCenterZ = zChunkPos * 16 + 8;
      float f3 = 0.0F;
      float f4 = 0.0F;
      Random random = new Random(seed);
      int total = RANGE * 16 * 4 - 16;
      total -= random.nextInt(total / 4);
      random.nextInt(total / 2);
      boolean steady = random.nextInt(6) == 0;
      IBlockState ice = Blocks.PACKED_ICE.getDefaultState();
      IBlockState dirt = Blocks.DIRT.getDefaultState();
      IBlockState cobble = Blocks.COBBLESTONE.getDefaultState();
      for (int step = 0; step < total; step++) {
         double d6 = 16.0 + MathHelper.sin(step * (float)Math.PI / total) * size * 1.0F;
         double d7 = d6;
         float cosPitch = MathHelper.cos(pitch);
         float sinPitch = MathHelper.sin(pitch);
         workPosX += MathHelper.cos(yaw) * cosPitch;
         workPosY += sinPitch;
         workPosZ += MathHelper.sin(yaw) * cosPitch;
         if (steady) {
            pitch *= 0.92F;
         } else {
            pitch *= 0.7F;
         }

         pitch += f4 * 0.1F;
         yaw += f3 * 0.1F;
         f4 *= 0.9F;
         f3 *= 0.75F;
         f4 += (random.nextFloat() - random.nextFloat()) * random.nextFloat() * 2.0F;
         f3 += (random.nextFloat() - random.nextFloat()) * random.nextFloat() * 2.0F;
         if (random.nextInt(4) != 0) {
            double d8 = workPosX - chunkCenterX;
            double d9 = workPosZ - chunkCenterZ;
            double d10 = total - step;
            double d11 = size + 2.0F + 16.0F;
            if (d8 * d8 + d9 * d9 - d10 * d10 > d11 * d11) {
               return;
            }

            if (workPosX >= chunkCenterX - 16.0 - d6 * 2.0
               && workPosZ >= chunkCenterZ - 16.0 - d6 * 2.0
               && workPosX <= chunkCenterX + 16.0 + d6 * 2.0
               && workPosZ <= chunkCenterZ + 16.0 + d6 * 2.0) {
               int xMin = Math.max(0, MathHelper.floor(workPosX - d6) - xChunkPos * 16 - 1);
               int xMax = Math.min(16, MathHelper.floor(workPosX + d6) - xChunkPos * 16 + 1);
               int yMin = Math.max(1, MathHelper.floor(workPosY - d7) - 1);
               int yMax = Math.min(240, MathHelper.floor(workPosY + d7) + 1);
               int zMin = Math.max(0, MathHelper.floor(workPosZ - d6) - zChunkPos * 16 - 1);
               int zMax = Math.min(16, MathHelper.floor(workPosZ + d6) - zChunkPos * 16 + 1);
               for (int x = xMin; x < xMax; x++) {
                  double d12 = (x + xChunkPos * 16 + 0.5 - workPosX) / d6;
                  for (int z = zMin; z < zMax; z++) {
                     double d13 = (z + zChunkPos * 16 + 0.5 - workPosZ) / d6;
                     if (d12 * d12 + d13 * d13 < 1.0) {
                        for (int y = yMax - 1; y >= yMin; y--) {
                           double d14 = (y + 0.5 - workPosY) / d7;
                           if (d14 > -0.7 && d12 * d12 + d14 * d14 + d13 * d13 < 1.0) {
                              int randVal = random.nextInt(32768);
                              if (randVal == 1) {
                                 primer.setBlockState(x, y, z, ice);
                              } else if (randVal <= 4) {
                                 primer.setBlockState(x, y, z, dirt);
                              } else if (randVal <= 8) {
                                 primer.setBlockState(x, y, z, cobble);
                              }
                           }
                        }
                     }
                  }
               }
            }
         }
      }
   }
}
