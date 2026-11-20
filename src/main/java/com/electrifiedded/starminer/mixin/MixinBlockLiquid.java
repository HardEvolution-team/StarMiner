package com.electrifiedded.starminer.mixin;

import com.electrifiedded.starminer.api.IZeroGravityWorldProvider;
import net.minecraft.block.BlockLiquid;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(BlockLiquid.class)
public abstract class MixinBlockLiquid {
   @Inject(method = "onBlockAdded", at = @At("HEAD"), cancellable = true)
   private void starminer$freezeInSpace(World world, BlockPos pos, IBlockState state, CallbackInfo ci) {
      if (world.provider instanceof IZeroGravityWorldProvider) {
         if (state.getMaterial() == Material.WATER) {
            world.setBlockState(pos, Blocks.PACKED_ICE.getDefaultState(), 2);
            ci.cancel();
         } else if (state.getMaterial() == Material.LAVA) {
            world.setBlockState(pos, Blocks.OBSIDIAN.getDefaultState(), 2);
            ci.cancel();
         }
      }
   }
}
