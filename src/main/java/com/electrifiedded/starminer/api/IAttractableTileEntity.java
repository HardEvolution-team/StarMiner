package com.electrifiedded.starminer.api;

import net.minecraft.entity.Entity;

public interface IAttractableTileEntity {
   GravityDirection getCurrentGravity(Entity var1);

   boolean isStillInAttractedState(Entity var1);
}
