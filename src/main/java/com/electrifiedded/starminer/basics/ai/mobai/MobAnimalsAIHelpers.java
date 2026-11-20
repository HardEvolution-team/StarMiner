package com.electrifiedded.starminer.basics.ai.mobai;

import java.util.HashSet;
import java.util.Set;
import com.electrifiedded.starminer.basics.ai.EntityAIBegGEx;
import com.electrifiedded.starminer.basics.ai.EntityAIEatSpaceEtherGEx;
import com.electrifiedded.starminer.basics.ai.EntityAILeapAtTargetGEx;
import com.electrifiedded.starminer.basics.ai.EntityAILookAtTradePlayerGEx;
import com.electrifiedded.starminer.basics.ai.EntityAILookIdleGEx;
import com.electrifiedded.starminer.basics.ai.EntityAIMateGEx;
import com.electrifiedded.starminer.basics.ai.EntityAIVillagerMateGEx;
import com.electrifiedded.starminer.core.ai.AbstractGravityAIHelper;
import com.electrifiedded.starminer.core.ai.EntityAIWatchClosest2GEx;
import com.electrifiedded.starminer.core.ai.EntityAIWatchClosestGEx;
import net.minecraft.entity.EntityCreature;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.ai.EntityAIAttackMelee;
import net.minecraft.entity.ai.EntityAIAvoidEntity;
import net.minecraft.entity.ai.EntityAIFollowOwner;
import net.minecraft.entity.ai.EntityAIFollowParent;
import net.minecraft.entity.ai.EntityAIHurtByTarget;
import net.minecraft.entity.ai.EntityAIOcelotAttack;
import net.minecraft.entity.ai.EntityAIOwnerHurtByTarget;
import net.minecraft.entity.ai.EntityAIOwnerHurtTarget;
import net.minecraft.entity.ai.EntityAIPanic;
import net.minecraft.entity.ai.EntityAIPlay;
import net.minecraft.entity.ai.EntityAIRunAroundLikeCrazy;
import net.minecraft.entity.ai.EntityAISwimming;
import net.minecraft.entity.ai.EntityAITargetNonTamed;
import net.minecraft.entity.ai.EntityAITempt;
import net.minecraft.entity.ai.EntityAITradePlayer;
import net.minecraft.entity.ai.EntityAIWander;
import net.minecraft.entity.monster.EntityZombie;
import net.minecraft.entity.passive.AbstractHorse;
import net.minecraft.entity.passive.EntityAnimal;
import net.minecraft.entity.passive.EntityChicken;
import net.minecraft.entity.passive.EntityCow;
import net.minecraft.entity.passive.EntityDonkey;
import net.minecraft.entity.passive.EntityHorse;
import net.minecraft.entity.passive.EntityMooshroom;
import net.minecraft.entity.passive.EntityMule;
import net.minecraft.entity.passive.EntityOcelot;
import net.minecraft.entity.passive.EntityPig;
import net.minecraft.entity.passive.EntitySheep;
import net.minecraft.entity.passive.EntityTameable;
import net.minecraft.entity.passive.EntityVillager;
import net.minecraft.entity.passive.EntityWolf;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.item.Item;

public class MobAnimalsAIHelpers {
   private static final Set<Item> CHICKEN_FOOD = new HashSet<>();
   private static final Set<Item> PIG_FOOD = new HashSet<>();

   static {
      CHICKEN_FOOD.add(Items.WHEAT_SEEDS);
      CHICKEN_FOOD.add(Items.MELON_SEEDS);
      CHICKEN_FOOD.add(Items.PUMPKIN_SEEDS);
      CHICKEN_FOOD.add(Items.BEETROOT_SEEDS);
      PIG_FOOD.add(Items.CARROT);
      PIG_FOOD.add(Items.POTATO);
      PIG_FOOD.add(Items.BEETROOT);
   }

   public static void register() {
      AbstractGravityAIHelper.gAiConstructMap.put(EntityVillager.class, VillagerGravityAIHelper.class);
      AbstractGravityAIHelper.gAiConstructMap.put(EntityChicken.class, ChickenGravityAIHelper.class);
      AbstractGravityAIHelper.gAiConstructMap.put(EntityCow.class, CowGravityAIHelper.class);
      AbstractGravityAIHelper.gAiConstructMap.put(EntityMooshroom.class, CowGravityAIHelper.class);
      AbstractGravityAIHelper.gAiConstructMap.put(EntityPig.class, PigGravityAIHelper.class);
      AbstractGravityAIHelper.gAiConstructMap.put(EntitySheep.class, SheepGravityAIHelper.class);
      AbstractGravityAIHelper.gAiConstructMap.put(EntityHorse.class, HorseGravityAIHelper.class);
      AbstractGravityAIHelper.gAiConstructMap.put(EntityDonkey.class, HorseGravityAIHelper.class);
      AbstractGravityAIHelper.gAiConstructMap.put(EntityMule.class, HorseGravityAIHelper.class);
      AbstractGravityAIHelper.gAiConstructMap.put(EntityOcelot.class, OcelotGravityAIHelper.class);
      AbstractGravityAIHelper.gAiConstructMap.put(EntityWolf.class, WolfGravityAIHelper.class);
   }

   public static class ChickenGravityAIHelper extends AbstractGravityAIHelper {
      @Override
      public void initAI(EntityLiving entityLiving) {
         super.initAI(entityLiving);
         this.gTasks.addTask(0, new EntityAISwimming(entityLiving));
         this.gTasks.addTask(1, new EntityAIPanic((EntityCreature)entityLiving, 1.4));
         this.gTasks.addTask(2, new EntityAIMateGEx((EntityAnimal)entityLiving, 1.0));
         this.gTasks.addTask(3, new EntityAITempt((EntityCreature)entityLiving, 1.0, false, CHICKEN_FOOD));
         this.gTasks.addTask(4, new EntityAIFollowParent((EntityAnimal)entityLiving, 1.1));
         this.gTasks.addTask(5, new EntityAIWander((EntityCreature)entityLiving, 1.0));
         this.gTasks.addTask(6, new EntityAIWatchClosestGEx(entityLiving, EntityPlayer.class, 6.0F));
         this.gTasks.addTask(7, new EntityAILookIdleGEx(entityLiving));
      }
   }

   public static class CowGravityAIHelper extends AbstractGravityAIHelper {
      @Override
      public void initAI(EntityLiving entityLiving) {
         super.initAI(entityLiving);
         this.gTasks.addTask(0, new EntityAISwimming(entityLiving));
         this.gTasks.addTask(1, new EntityAIPanic((EntityCreature)entityLiving, 2.0));
         this.gTasks.addTask(2, new EntityAIMateGEx((EntityAnimal)entityLiving, 1.0));
         this.gTasks.addTask(3, new EntityAITempt((EntityCreature)entityLiving, 1.25, Items.WHEAT, false));
         this.gTasks.addTask(4, new EntityAIFollowParent((EntityAnimal)entityLiving, 1.25));
         this.gTasks.addTask(5, new EntityAIWander((EntityCreature)entityLiving, 1.0));
         this.gTasks.addTask(6, new EntityAIWatchClosestGEx(entityLiving, EntityPlayer.class, 6.0F));
         this.gTasks.addTask(7, new EntityAILookIdleGEx(entityLiving));
      }
   }

   public static class HorseGravityAIHelper extends AbstractGravityAIHelper {
      @Override
      public void initAI(EntityLiving entityLiving) {
         super.initAI(entityLiving);
         this.gTasks.addTask(0, new EntityAISwimming(entityLiving));
         this.gTasks.addTask(1, new EntityAIPanic((EntityCreature)entityLiving, 1.2));
         this.gTasks.addTask(1, new EntityAIRunAroundLikeCrazy((AbstractHorse)entityLiving, 1.2));
         this.gTasks.addTask(2, new EntityAIMateGEx((EntityAnimal)entityLiving, 1.0, AbstractHorse.class));
         this.gTasks.addTask(4, new EntityAIFollowParent((EntityAnimal)entityLiving, 1.0));
         this.gTasks.addTask(6, new EntityAIWander((EntityCreature)entityLiving, 0.7));
         this.gTasks.addTask(7, new EntityAIWatchClosestGEx(entityLiving, EntityPlayer.class, 6.0F));
         this.gTasks.addTask(8, new EntityAILookIdleGEx(entityLiving));
      }
   }

   public static class OcelotGravityAIHelper extends AbstractGravityAIHelper {
      @Override
      public void initAI(EntityLiving entityLiving) {
         super.initAI(entityLiving);
         this.gTasks.addTask(1, new EntityAISwimming(entityLiving));
         this.gTasks.addTask(2, ((EntityTameable)entityLiving).getAISit());
         this.gTasks.addTask(3, new EntityAITempt((EntityCreature)entityLiving, 0.6, Items.FISH, true));
         this.gTasks.addTask(4, new EntityAIAvoidEntity<>((EntityCreature)entityLiving, EntityPlayer.class, 16.0F, 0.8, 1.33));
         this.gTasks.addTask(5, new EntityAIFollowOwner((EntityTameable)entityLiving, 1.0, 10.0F, 5.0F));
         this.gTasks.addTask(7, new EntityAILeapAtTargetGEx(entityLiving, 0.3F));
         this.gTasks.addTask(8, new EntityAIOcelotAttack(entityLiving));
         this.gTasks.addTask(9, new EntityAIMateGEx((EntityAnimal)entityLiving, 0.8));
         this.gTasks.addTask(10, new EntityAIWander((EntityCreature)entityLiving, 0.8));
         this.gTasks.addTask(11, new EntityAIWatchClosestGEx(entityLiving, EntityPlayer.class, 10.0F));
         this.gTargetTasks.addTask(1, new EntityAITargetNonTamed<>((EntityTameable)entityLiving, EntityChicken.class, false, null));
      }
   }

   public static class PigGravityAIHelper extends AbstractGravityAIHelper {
      @Override
      public void initAI(EntityLiving entityLiving) {
         super.initAI(entityLiving);
         this.gTasks.addTask(0, new EntityAISwimming(entityLiving));
         this.gTasks.addTask(1, new EntityAIPanic((EntityCreature)entityLiving, 1.25));
         this.gTasks.addTask(3, new EntityAIMateGEx((EntityAnimal)entityLiving, 1.0));
         this.gTasks.addTask(4, new EntityAITempt((EntityCreature)entityLiving, 1.2, Items.CARROT_ON_A_STICK, false));
         this.gTasks.addTask(4, new EntityAITempt((EntityCreature)entityLiving, 1.2, false, PIG_FOOD));
         this.gTasks.addTask(5, new EntityAIFollowParent((EntityAnimal)entityLiving, 1.1));
         this.gTasks.addTask(6, new EntityAIWander((EntityCreature)entityLiving, 1.0));
         this.gTasks.addTask(7, new EntityAIWatchClosestGEx(entityLiving, EntityPlayer.class, 6.0F));
         this.gTasks.addTask(8, new EntityAILookIdleGEx(entityLiving));
      }
   }

   public static class SheepGravityAIHelper extends AbstractGravityAIHelper {
      @Override
      public void initAI(EntityLiving entityLiving) {
         super.initAI(entityLiving);
         this.gTasks.addTask(0, new EntityAISwimming(entityLiving));
         this.gTasks.addTask(1, new EntityAIPanic((EntityCreature)entityLiving, 1.25));
         this.gTasks.addTask(2, new EntityAIMateGEx((EntityAnimal)entityLiving, 1.0));
         this.gTasks.addTask(3, new EntityAITempt((EntityCreature)entityLiving, 1.1, Items.WHEAT, false));
         this.gTasks.addTask(4, new EntityAIFollowParent((EntityAnimal)entityLiving, 1.1));
         this.gTasks.addTask(5, new EntityAIEatSpaceEtherGEx((EntitySheep)entityLiving));
         this.gTasks.addTask(6, new EntityAIWander((EntityCreature)entityLiving, 1.0));
         this.gTasks.addTask(7, new EntityAIWatchClosestGEx(entityLiving, EntityPlayer.class, 6.0F));
         this.gTasks.addTask(8, new EntityAILookIdleGEx(entityLiving));
      }
   }

   public static class VillagerGravityAIHelper extends AbstractGravityAIHelper {
      @Override
      public void initAI(EntityLiving entityLiving) {
         super.initAI(entityLiving);
         this.gTasks.addTask(0, new EntityAISwimming(entityLiving));
         this.gTasks.addTask(1, new EntityAIAvoidEntity<>((EntityCreature)entityLiving, EntityZombie.class, 8.0F, 0.6, 0.6));
         this.gTasks.addTask(1, new EntityAITradePlayer((EntityVillager)entityLiving));
         this.gTasks.addTask(1, new EntityAILookAtTradePlayerGEx((EntityVillager)entityLiving));
         this.gTasks.addTask(6, new EntityAIVillagerMateGEx((EntityVillager)entityLiving));
         this.gTasks.addTask(8, new EntityAIPlay((EntityVillager)entityLiving, 0.32));
         this.gTasks.addTask(9, new EntityAIWatchClosest2GEx(entityLiving, EntityPlayer.class, 3.0F, 1.0F));
         this.gTasks.addTask(9, new EntityAIWatchClosest2GEx(entityLiving, EntityVillager.class, 5.0F, 0.02F));
         this.gTasks.addTask(9, new EntityAIWander((EntityCreature)entityLiving, 0.6));
         this.gTasks.addTask(10, new EntityAIWatchClosestGEx(entityLiving, EntityLiving.class, 8.0F));
      }
   }

   public static class WolfGravityAIHelper extends AbstractGravityAIHelper {
      @Override
      public void initAI(EntityLiving entityLiving) {
         super.initAI(entityLiving);
         this.gTasks.addTask(1, new EntityAISwimming(entityLiving));
         this.gTasks.addTask(2, ((EntityTameable)entityLiving).getAISit());
         this.gTasks.addTask(3, new EntityAILeapAtTargetGEx(entityLiving, 0.4F));
         this.gTasks.addTask(4, new EntityAIAttackMelee((EntityCreature)entityLiving, 1.0, true));
         this.gTasks.addTask(5, new EntityAIFollowOwner((EntityTameable)entityLiving, 1.0, 10.0F, 2.0F));
         this.gTasks.addTask(6, new EntityAIMateGEx((EntityAnimal)entityLiving, 1.0));
         this.gTasks.addTask(7, new EntityAIWander((EntityCreature)entityLiving, 1.0));
         this.gTasks.addTask(8, new EntityAIBegGEx((EntityWolf)entityLiving, 8.0F));
         this.gTasks.addTask(9, new EntityAIWatchClosestGEx(entityLiving, EntityPlayer.class, 8.0F));
         this.gTasks.addTask(9, new EntityAILookIdleGEx(entityLiving));
         this.gTargetTasks.addTask(1, new EntityAIOwnerHurtByTarget((EntityTameable)entityLiving));
         this.gTargetTasks.addTask(2, new EntityAIOwnerHurtTarget((EntityTameable)entityLiving));
         this.gTargetTasks.addTask(3, new EntityAIHurtByTarget((EntityCreature)entityLiving, true));
         this.gTargetTasks.addTask(4, new EntityAITargetNonTamed<>((EntityTameable)entityLiving, EntitySheep.class, false, null));
      }
   }
}
