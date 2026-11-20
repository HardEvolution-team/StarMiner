package com.electrifiedded.starminer.mixin;

import com.electrifiedded.starminer.api.IZeroGravityWorldProvider;
import net.minecraft.entity.item.EntityItem;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.Constant;
import org.spongepowered.asm.mixin.injection.ModifyConstant;

@Mixin(EntityItem.class)
public abstract class MixinEntityItem {
   @ModifyConstant(method = "onUpdate", constant = @Constant(doubleValue = 0.03999999910593033))
   private double starminer$itemGravity(double original) {
      EntityItem self = (EntityItem)(Object)this;
      return self.world.provider instanceof IZeroGravityWorldProvider ? 0.0 : original;
   }
}
