package com.electrifiedded.starminer.basics.ai.mobai;

import com.electrifiedded.starminer.basics.ai.EntityAILookIdleGEx;
import com.electrifiedded.starminer.core.ai.AbstractGravityAIHelper;
import com.electrifiedded.starminer.core.ai.EntityAIWatchClosestGEx;
import net.minecraft.entity.EntityCreature;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.ai.EntityAIAttackMelee;
import net.minecraft.entity.ai.EntityAIAvoidEntity;
import net.minecraft.entity.ai.EntityAICreeperSwell;
import net.minecraft.entity.ai.EntityAIHurtByTarget;
import net.minecraft.entity.ai.EntityAINearestAttackableTarget;
import net.minecraft.entity.ai.EntityAISwimming;
import net.minecraft.entity.ai.EntityAIWander;
import net.minecraft.entity.monster.EntityCreeper;
import net.minecraft.entity.passive.EntityOcelot;
import net.minecraft.entity.player.EntityPlayer;

public class MobMonstersAIHelpers {
   public static void register() {
      AbstractGravityAIHelper.gAiConstructMap.put(EntityCreeper.class, CreeperGravityAIHelper.class);
   }

   public static class CreeperGravityAIHelper extends AbstractGravityAIHelper {
      @Override
      public void initAI(EntityLiving entityLiving) {
         super.initAI(entityLiving);
         this.gTasks.addTask(1, new EntityAISwimming(entityLiving));
         this.gTasks.addTask(2, new EntityAICreeperSwell((EntityCreeper)entityLiving));
         this.gTasks.addTask(3, new EntityAIAvoidEntity<>((EntityCreature)entityLiving, EntityOcelot.class, 6.0F, 1.0, 1.2));
         this.gTasks.addTask(4, new EntityAIAttackMelee((EntityCreature)entityLiving, 1.0, false));
         this.gTasks.addTask(5, new EntityAIWander((EntityCreature)entityLiving, 0.8));
         this.gTasks.addTask(6, new EntityAIWatchClosestGEx(entityLiving, EntityPlayer.class, 8.0F));
         this.gTasks.addTask(6, new EntityAILookIdleGEx(entityLiving));
         this.gTargetTasks.addTask(1, new EntityAINearestAttackableTarget<>((EntityCreature)entityLiving, EntityPlayer.class, true));
         this.gTargetTasks.addTask(2, new EntityAIHurtByTarget((EntityCreature)entityLiving, false));
      }
   }
}
