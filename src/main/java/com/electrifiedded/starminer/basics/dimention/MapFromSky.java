package com.electrifiedded.starminer.basics.dimention;

import java.util.HashMap;
import java.util.Map;
import net.minecraft.block.material.MapColor;
import net.minecraft.init.Biomes;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.biome.Biome;

public class MapFromSky {
   public static final int SIZE = 128;
   public static final int SCALE = 128;
   public static boolean hasSkyMapImageData = false;
   public static byte[] mapDataFromSky;
   public static byte[] skyMapclientData;
   public static boolean doRecompileSkyMapList = false;

   private static final Map<Biome, MapColor> COLORS = new HashMap<>();

   static {
      put(MapColor.SAND, Biomes.BEACH, Biomes.DESERT, Biomes.DESERT_HILLS);
      put(MapColor.STONE, Biomes.STONE_BEACH, Biomes.EXTREME_HILLS, Biomes.EXTREME_HILLS_EDGE, Biomes.EXTREME_HILLS_WITH_TREES);
      put(MapColor.WOOD, Biomes.FOREST, Biomes.FOREST_HILLS, Biomes.BIRCH_FOREST, Biomes.BIRCH_FOREST_HILLS, Biomes.ROOFED_FOREST);
      put(MapColor.ICE, Biomes.FROZEN_OCEAN, Biomes.FROZEN_RIVER);
      put(MapColor.SNOW, Biomes.ICE_MOUNTAINS, Biomes.ICE_PLAINS, Biomes.COLD_BEACH, Biomes.COLD_TAIGA, Biomes.COLD_TAIGA_HILLS,
         Biomes.REDWOOD_TAIGA, Biomes.REDWOOD_TAIGA_HILLS, Biomes.TAIGA, Biomes.TAIGA_HILLS);
      put(MapColor.FOLIAGE, Biomes.JUNGLE, Biomes.JUNGLE_HILLS, Biomes.JUNGLE_EDGE);
      put(MapColor.DIRT, Biomes.MUSHROOM_ISLAND, Biomes.MUSHROOM_ISLAND_SHORE);
      put(MapColor.GRASS, Biomes.SAVANNA, Biomes.SAVANNA_PLATEAU, Biomes.PLAINS);
      put(MapColor.TNT, Biomes.MESA, Biomes.MESA_ROCK, Biomes.MESA_CLEAR_ROCK);
      put(MapColor.WATER, Biomes.OCEAN, Biomes.DEEP_OCEAN, Biomes.RIVER);
      put(MapColor.CLAY, Biomes.SWAMPLAND);
   }

   private static void put(MapColor color, Biome... biomes) {
      for (Biome biome : biomes) {
         COLORS.put(biome, color);
      }
   }

   public static void createMapDataFromSky(World world) {
      byte[] colors = new byte[SIZE * SIZE];
      for (int px = 0; px < SIZE; px++) {
         for (int pz = 0; pz < SIZE; pz++) {
            Biome biome = world.getBiomeProvider().getBiome(new BlockPos((px - SIZE / 2) * SCALE, 0, (pz - SIZE / 2) * SCALE), Biomes.OCEAN);
            MapColor color = COLORS.get(biome);
            if (color == null) {
               int base = Biome.getIdForBiome(biome) & 127;
               Biome baseBiome = Biome.getBiomeForId(base);
               color = baseBiome == null ? null : COLORS.get(baseBiome);
            }

            if (color == null) {
               color = MapColor.WATER;
            }

            colors[px + pz * SIZE] = (byte)(color.colorIndex * 4 + 1);
         }
      }

      mapDataFromSky = colors;
   }
}
