package com.electrifiedded.starminer.basics.block.gravitized;

import net.minecraft.block.state.IBlockState;

public interface IGravitizedPlants {
   boolean allowPlantOn(IBlockState support, IBlockState plantState);
}
