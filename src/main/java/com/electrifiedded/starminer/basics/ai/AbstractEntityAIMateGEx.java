package com.electrifiedded.starminer.basics.ai;

import com.electrifiedded.starminer.api.Gravity;
import com.electrifiedded.starminer.core.entity.GravityLiving;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.ai.EntityAIBase;

public abstract class AbstractEntityAIMateGEx extends EntityAIBase {
   protected void passGravityStateToChild(EntityLiving parent, EntityLiving baby) {
      Gravity gravityParent = Gravity.getGravityProp(parent);
      if (gravityParent != null && GravityLiving.isGravitized(baby)) {
         GravityLiving.preSetSpawnGravity(baby, gravityParent.gravityDirection, gravityParent.attractedPosX, gravityParent.attractedPosY,
            gravityParent.attractedPosZ);
      }
   }
}
