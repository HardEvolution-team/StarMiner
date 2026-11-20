package com.electrifiedded.starminer.basics.structure;

import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.chunk.ChunkPrimer;
import net.minecraft.world.gen.structure.StructureBoundingBox;

public abstract class StarAccess {
   public final StructureBoundingBox window;

   protected StarAccess(StructureBoundingBox window) {
      this.window = window;
   }

   public boolean inside(int x, int y, int z) {
      return y >= 0 && y <= 255 && x >= this.window.minX && x <= this.window.maxX && z >= this.window.minZ && z <= this.window.maxZ;
   }

   public abstract IBlockState get(int x, int y, int z);

   public abstract void set(int x, int y, int z, IBlockState state);

   public boolean isAir(int x, int y, int z) {
      return this.get(x, y, z).getBlock() == Blocks.AIR;
   }

   public static final class WorldAccess extends StarAccess {
      private final World world;

      public WorldAccess(World world, StructureBoundingBox window) {
         super(window);
         this.world = world;
      }

      @Override
      public IBlockState get(int x, int y, int z) {
         return this.inside(x, y, z) ? this.world.getBlockState(new BlockPos(x, y, z)) : Blocks.AIR.getDefaultState();
      }

      @Override
      public void set(int x, int y, int z, IBlockState state) {
         if (this.inside(x, y, z)) {
            this.world.setBlockState(new BlockPos(x, y, z), state, 2);
         }
      }
   }

   public static final class PrimerAccess extends StarAccess {
      private final ChunkPrimer primer;

      public PrimerAccess(ChunkPrimer primer, int chunkX, int chunkZ) {
         super(new StructureBoundingBox(chunkX << 4, chunkZ << 4, (chunkX << 4) + 15, (chunkZ << 4) + 15));
         this.primer = primer;
      }

      @Override
      public IBlockState get(int x, int y, int z) {
         return this.inside(x, y, z) ? this.primer.getBlockState(x & 15, y, z & 15) : Blocks.AIR.getDefaultState();
      }

      @Override
      public void set(int x, int y, int z, IBlockState state) {
         if (this.inside(x, y, z)) {
            this.primer.setBlockState(x & 15, y, z & 15, state);
         }
      }
   }
}
