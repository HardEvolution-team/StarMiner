package com.electrifiedded.starminer.basics.structure;

import java.util.Random;
import net.minecraft.world.World;
import net.minecraft.world.gen.structure.StructureComponent;
import net.minecraft.world.gen.structure.StructureStart;

public class StructureStarStart extends StructureStart {
   public StructureStarStart() {
   }

   public StructureStarStart(World world, Random random, int chunkX, int chunkZ) {
      super(chunkX, chunkZ);
      this.components.add(new ComponentStar(0, random, (chunkX << 4) + 2, (chunkZ << 4) + 2, world));
      this.updateBoundingBox();
   }

   public void generateStructureImmidiate(StarAccess acc) {
      for (StructureComponent component : this.components) {
         if (component instanceof ComponentStar && component.getBoundingBox().intersectsWith(acc.window.minX, acc.window.minZ, acc.window.maxX, acc.window.maxZ)) {
            ((ComponentStar)component).generateIntoChunk(acc);
         }
      }
   }
}
