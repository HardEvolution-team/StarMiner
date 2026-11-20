package com.electrifiedded.starminer.basics.ai;

import com.electrifiedded.starminer.core.ai.EntityAIWatchClosestGEx;
import net.minecraft.entity.passive.EntityVillager;
import net.minecraft.entity.player.EntityPlayer;

public class EntityAILookAtTradePlayerGEx extends EntityAIWatchClosestGEx {
   private final EntityVillager merchant;

   public EntityAILookAtTradePlayerGEx(EntityVillager villager) {
      super(villager, EntityPlayer.class, 8.0F);
      this.merchant = villager;
   }

   @Override
   public boolean shouldExecute() {
      if (this.merchant.isTrading()) {
         this.closestEntity = this.merchant.getCustomer();
         return true;
      }

      return false;
   }
}
