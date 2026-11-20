package com.electrifiedded.starminer.mixin;

import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.ai.EntityJumpHelper;
import net.minecraft.entity.ai.EntityLookHelper;
import net.minecraft.entity.ai.EntityMoveHelper;
import net.minecraft.entity.ai.EntitySenses;
import net.minecraft.pathfinding.PathNavigate;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.gen.Accessor;
import org.spongepowered.asm.mixin.gen.Invoker;

@Mixin(EntityLiving.class)
public interface EntityLivingAccessor {
   @Mutable
   @Accessor("lookHelper")
   void starminer$setLookHelper(EntityLookHelper v);

   @Accessor("moveHelper")
   void starminer$setMoveHelper(EntityMoveHelper v);

   @Accessor("jumpHelper")
   void starminer$setJumpHelper(EntityJumpHelper v);

   @Accessor("navigator")
   void starminer$setNavigator(PathNavigate v);

   @Mutable
   @Accessor("senses")
   void starminer$setSenses(EntitySenses v);

   @Invoker("despawnEntity")
   void starminer$despawnEntity();

   @Invoker("updateAITasks")
   void starminer$updateAITasks();
}
