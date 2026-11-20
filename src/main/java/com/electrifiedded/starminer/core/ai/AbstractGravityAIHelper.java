package com.electrifiedded.starminer.core.ai;

import java.util.HashMap;
import com.electrifiedded.starminer.core.ai.helper.GEntityJumpHelper;
import com.electrifiedded.starminer.core.ai.helper.GEntityLookHelper;
import com.electrifiedded.starminer.core.ai.helper.GEntityMoveHelper;
import com.electrifiedded.starminer.core.ai.path.GPathNavigate;
import com.electrifiedded.starminer.mixin.EntityLivingAccessor;
import net.minecraft.entity.EntityCreature;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.ai.EntityAITasks;
import net.minecraft.entity.ai.EntityAIWander;
import net.minecraft.entity.ai.EntityJumpHelper;
import net.minecraft.entity.ai.EntityLookHelper;
import net.minecraft.entity.ai.EntityMoveHelper;
import net.minecraft.entity.ai.EntitySenses;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.pathfinding.PathNavigate;

public abstract class AbstractGravityAIHelper {
   public static final HashMap<Class<? extends EntityLiving>, Class<? extends AbstractGravityAIHelper>> gAiConstructMap = new HashMap<>();
   public EntityLookHelper lookHelperOrg;
   public EntityMoveHelper moveHelperOrg;
   public EntityJumpHelper jumpHelperOrg;
   public PathNavigate navigatorOrg;
   public EntitySenses sensesOrg;
   public GEntityLookHelper gLookHelper;
   public GEntityMoveHelper gMoveHelper;
   public GEntityJumpHelper gJumpHelper;
   public GPathNavigate gNavigator;
   public EntitySenses gSenses;
   public EntityAITasks gTasks;
   public EntityAITasks gTargetTasks;

   public void initAI(EntityLiving entityLiving) {
      this.lookHelperOrg = entityLiving.getLookHelper();
      this.moveHelperOrg = entityLiving.getMoveHelper();
      this.jumpHelperOrg = entityLiving.getJumpHelper();
      this.navigatorOrg = entityLiving.getNavigator();
      this.sensesOrg = entityLiving.getEntitySenses();
      this.gLookHelper = new GEntityLookHelper(entityLiving);
      this.gMoveHelper = new GEntityMoveHelper(entityLiving);
      this.gJumpHelper = new GEntityJumpHelper(entityLiving);
      this.gNavigator = new GPathNavigate(entityLiving, entityLiving.world);
      this.gSenses = new EntitySenses(entityLiving);
      this.gTasks = new EntityAITasks(entityLiving.world != null ? entityLiving.world.profiler : null);
      this.gTargetTasks = new EntityAITasks(entityLiving.world != null ? entityLiving.world.profiler : null);
   }

   public static void injectGAIHelper(AbstractGravityAIHelper gAIHelper, EntityLiving entityLiving) {
      if (entityLiving.getNavigator() != gAIHelper.gNavigator) {
         EntityLivingAccessor acc = (EntityLivingAccessor)entityLiving;
         acc.starminer$setLookHelper(gAIHelper.gLookHelper);
         acc.starminer$setMoveHelper(gAIHelper.gMoveHelper);
         acc.starminer$setJumpHelper(gAIHelper.gJumpHelper);
         acc.starminer$setNavigator(gAIHelper.gNavigator);
         acc.starminer$setSenses(gAIHelper.gSenses);
      }
   }

   public static void resetOrgAIHelper(AbstractGravityAIHelper gAIHelper, EntityLiving entityLiving) {
      if (entityLiving.getNavigator() == gAIHelper.gNavigator) {
         EntityLivingAccessor acc = (EntityLivingAccessor)entityLiving;
         acc.starminer$setLookHelper(gAIHelper.lookHelperOrg);
         acc.starminer$setMoveHelper(gAIHelper.moveHelperOrg);
         acc.starminer$setJumpHelper(gAIHelper.jumpHelperOrg);
         acc.starminer$setNavigator(gAIHelper.navigatorOrg);
         acc.starminer$setSenses(gAIHelper.sensesOrg);
      }
   }

   public static class DefaultGravityAIHelper extends AbstractGravityAIHelper {
      @Override
      public void initAI(EntityLiving entityLiving) {
         super.initAI(entityLiving);
         this.gTasks.addTask(9, new EntityAIWatchClosest2GEx(entityLiving, EntityPlayer.class, 3.0F, 1.0F));
         if (entityLiving instanceof EntityCreature) {
            this.gTasks.addTask(9, new EntityAIWander((EntityCreature)entityLiving, 0.6));
         }
         this.gTasks.addTask(10, new EntityAIWatchClosestGEx(entityLiving, EntityLiving.class, 8.0F));
      }
   }
}
