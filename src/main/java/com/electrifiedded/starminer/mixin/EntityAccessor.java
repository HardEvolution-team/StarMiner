package com.electrifiedded.starminer.mixin;

import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.math.BlockPos;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;
import org.spongepowered.asm.mixin.gen.Invoker;

@Mixin(Entity.class)
public interface EntityAccessor {
   @Accessor("isInWeb")
   boolean starminer$isInWeb();

   @Accessor("isInWeb")
   void starminer$setInWeb(boolean v);

   @Accessor("fire")
   int starminer$getFire();

   @Accessor("fire")
   void starminer$setFire(int v);

   @Accessor("nextStepDistance")
   int starminer$getNextStepDistance();

   @Accessor("nextStepDistance")
   void starminer$setNextStepDistance(int v);

   @Accessor("nextFlap")
   float starminer$getNextFlap();

   @Accessor("nextFlap")
   void starminer$setNextFlap(float v);

   @Invoker("playStepSound")
   void starminer$playStepSound(BlockPos pos, Block block);

   @Invoker("doBlockCollisions")
   void starminer$doBlockCollisions();

   @Invoker("dealFireDamage")
   void starminer$dealFireDamage(int amount);

   @Invoker("getFireImmuneTicks")
   int starminer$getFireImmuneTicks();

   @Invoker("canTriggerWalking")
   boolean starminer$canTriggerWalking();

   @Invoker("getSwimSound")
   SoundEvent starminer$getSwimSound();

   @Invoker("updateFallState")
   void starminer$updateFallState(double y, boolean onGround, IBlockState state, BlockPos pos);

   @Invoker("makeFlySound")
   boolean starminer$makeFlySound();

   @Invoker("playFlySound")
   float starminer$playFlySound(float f);
}
