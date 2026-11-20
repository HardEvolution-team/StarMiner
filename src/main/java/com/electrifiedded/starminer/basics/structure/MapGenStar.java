package com.electrifiedded.starminer.basics.structure;

import java.util.Random;
import javax.annotation.Nullable;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.chunk.ChunkPrimer;
import net.minecraft.world.gen.structure.MapGenStructure;
import net.minecraft.world.gen.structure.StructureBoundingBox;
import net.minecraft.world.gen.structure.StructureStart;

public class MapGenStar extends MapGenStructure {
   public int maxDistanceBetweenStars = 16;
   public int minDistanceBetweenStars = 8;

   public MapGenStar() {
   }

   public MapGenStar(int maxDistance, int minDistance) {
      this.maxDistanceBetweenStars = maxDistance;
      this.minDistanceBetweenStars = minDistance;
   }

   public void generateStructuresImmidiateInChunk(World world, Random random, int chunkX, int chunkZ, ChunkPrimer primer) {
      StarAccess acc = new StarAccess.PrimerAccess(primer, chunkX, chunkZ);
      int k = chunkX << 4;
      int l = chunkZ << 4;
      for (StructureStart start : this.structureMap.values()) {
         if (start instanceof StructureStarStart && start.isSizeableStructure() && start.getBoundingBox().intersectsWith(k, l, k + 15, l + 15)) {
            ((StructureStarStart)start).generateStructureImmidiate(acc);
         }
      }
   }

   @Override
   public String getStructureName() {
      return "StarminerStar";
   }

   @Nullable
   @Override
   public BlockPos getNearestStructurePos(World worldIn, BlockPos pos, boolean findUnexplored) {
      return null;
   }

   @Override
   protected boolean canSpawnStructureAtCoords(int chunkX, int chunkZ) {
      int k = chunkX;
      int l = chunkZ;
      if (chunkX < 0) {
         chunkX -= this.maxDistanceBetweenStars - 1;
      }

      if (chunkZ < 0) {
         chunkZ -= this.maxDistanceBetweenStars - 1;
      }

      int i1 = chunkX / this.maxDistanceBetweenStars;
      int j1 = chunkZ / this.maxDistanceBetweenStars;
      Random random = this.world.setRandomSeed(i1, j1, 58939324);
      i1 *= this.maxDistanceBetweenStars;
      j1 *= this.maxDistanceBetweenStars;
      i1 += random.nextInt(this.maxDistanceBetweenStars - this.minDistanceBetweenStars);
      j1 += random.nextInt(this.maxDistanceBetweenStars - this.minDistanceBetweenStars);
      return k == i1 && l == j1;
   }

   @Override
   protected StructureStart getStructureStart(int chunkX, int chunkZ) {
      return new StructureStarStart(this.world, this.rand, chunkX, chunkZ);
   }

   public static StructureBoundingBox populateWindow(int chunkX, int chunkZ) {
      int x = (chunkX << 4) + 8;
      int z = (chunkZ << 4) + 8;
      return new StructureBoundingBox(x, z, x + 15, z + 15);
   }
}
