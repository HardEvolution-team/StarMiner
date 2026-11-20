package com.electrifiedded.starminer.api;

import java.util.ArrayList;
import net.minecraft.entity.player.EntityPlayer;

public interface IRotateSleepingViewHandler {
   ArrayList<IRotateSleepingViewHandler> handlerList = new ArrayList<>();

   boolean rotateSleepingFPView();

   boolean rotateTPPlayerSleeping(EntityPlayer var1);
}
