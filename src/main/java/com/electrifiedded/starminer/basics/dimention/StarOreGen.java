package com.electrifiedded.starminer.basics.dimention;

import java.util.Random;
import com.electrifiedded.starminer.basics.SMModContainer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.WorldProviderSurface;
import net.minecraft.world.gen.feature.WorldGenMinable;
import net.minecraftforge.event.terraingen.OreGenEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public class StarOreGen {
   private final Random rand = new Random();
   private WorldGenMinable genOuterCore;
   private WorldGenMinable genInnerCore;

   @SubscribeEvent
   public void onOreGenPost(OreGenEvent.Post event) {
      World world = event.getWorld();
      if (!(world.provider instanceof WorldProviderSurface)) {
         return;
      }

      if (this.genOuterCore == null) {
         this.genOuterCore = new WorldGenMinable(SMModContainer.OuterCoreBlock.getDefaultState(), 8);
         this.genInnerCore = new WorldGenMinable(SMModContainer.InnerCoreBlock.getDefaultState(), 4);
      }

      BlockPos origin = event.getPos();
      this.rand.setSeed(world.getSeed());
      long i1 = this.rand.nextLong() / 2L * 2L + 1L;
      long j1 = this.rand.nextLong() / 2L * 2L + 1L;
      this.rand.setSeed(origin.getX() * i1 + origin.getZ() * j1 ^ world.getSeed());
      for (int k = 0; k < 6; k++) {
         this.genOuterCore.generate(world, this.rand, origin.add(this.rand.nextInt(16), this.rand.nextInt(32), this.rand.nextInt(16)));
      }

      for (int k = 0; k < 2; k++) {
         this.genInnerCore.generate(world, this.rand, origin.add(this.rand.nextInt(16), this.rand.nextInt(16), this.rand.nextInt(16)));
      }
   }
}
