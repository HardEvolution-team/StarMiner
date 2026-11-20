package com.electrifiedded.starminer.core.ai.helper;

import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.ai.EntityJumpHelper;

public class GEntityJumpHelper extends EntityJumpHelper {
   private final EntityLiving living;
   private boolean jumpingG;

   public GEntityJumpHelper(EntityLiving entityLiving) {
      super(entityLiving);
      this.living = entityLiving;
   }

   @Override
   public void setJumping() {
      this.jumpingG = true;
   }

   @Override
   public void doJump() {
      this.living.setJumping(this.jumpingG);
      this.jumpingG = false;
   }
}
