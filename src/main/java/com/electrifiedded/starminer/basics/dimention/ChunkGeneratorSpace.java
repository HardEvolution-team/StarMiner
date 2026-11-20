package com.electrifiedded.starminer.basics.dimention;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import javax.annotation.Nullable;
import com.electrifiedded.starminer.basics.structure.MapGenStar;
import net.minecraft.entity.EnumCreatureType;
import net.minecraft.init.Biomes;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.world.World;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.chunk.ChunkPrimer;
import net.minecraft.world.gen.IChunkGenerator;

public class ChunkGeneratorSpace implements IChunkGenerator {
   private final World world;
   private final Random rand;
   private final MapGenAsteroidBelt asteroidBelt = new MapGenAsteroidBelt();
   private final MapGenStar stars = new MapGenStar(15, 4);
   private final List<Biome.SpawnListEntry> spawnableCreatures = new ArrayList<>();

   public ChunkGeneratorSpace(World world, long seed) {
      this.world = world;
      this.rand = new Random(seed);
      this.spawnableCreatures.add(new Biome.SpawnListEntry(com.electrifiedded.starminer.basics.entity.EntityComet.class, 10, 1, 1));
   }

   @Override
   public Chunk generateChunk(int x, int z) {
      this.rand.setSeed(x * 341873128712L + z * 132897987541L);
      ChunkPrimer primer = new ChunkPrimer();
      this.asteroidBelt.generate(this.world, x, z, primer);
      this.stars.generate(this.world, x, z, null);
      this.stars.generateStructuresImmidiateInChunk(this.world, this.rand, x, z, primer);
      Chunk chunk = new Chunk(this.world, primer, x, z);
      byte[] biomes = chunk.getBiomeArray();
      byte id = (byte)Biome.getIdForBiome(Biomes.SKY);
      for (int i = 0; i < biomes.length; i++) {
         biomes[i] = id;
      }

      chunk.generateSkylightMap();
      return chunk;
   }

   @Override
   public void populate(int x, int z) {
      this.stars.generateStructure(this.world, this.rand, new ChunkPos(x, z));
   }

   @Override
   public boolean generateStructures(Chunk chunkIn, int x, int z) {
      return false;
   }

   @Override
   public List<Biome.SpawnListEntry> getPossibleCreatures(EnumCreatureType creatureType, BlockPos pos) {
      if (creatureType == EnumCreatureType.CREATURE) {
         return this.spawnableCreatures;
      }

      return new ArrayList<>();
   }

   @Nullable
   @Override
   public BlockPos getNearestStructurePos(World worldIn, String structureName, BlockPos position, boolean findUnexplored) {
      return null;
   }

   @Override
   public void recreateStructures(Chunk chunkIn, int x, int z) {
      this.stars.generate(this.world, x, z, null);
   }

   @Override
   public boolean isInsideStructure(World worldIn, String structureName, BlockPos pos) {
      return false;
   }
}
