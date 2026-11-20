package com.electrifiedded.starminer.core.common;

import com.electrifiedded.starminer.api.GravityDirection;
import com.electrifiedded.starminer.core.entity.ExtendedPropertyGravity;
import com.electrifiedded.starminer.core.entity.GravityLiving;
import net.minecraft.client.entity.EntityOtherPlayerMP;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class CoreProxyClient extends CoreProxy {
   @Override
   public boolean isOtherPlayer(EntityPlayer player) {
      return player instanceof EntityOtherPlayerMP;
   }

   @Override
   public void setFlyMovementByGravity(EntityPlayer entityPlayer) {
      if (entityPlayer instanceof EntityPlayerSP) {
         EntityPlayerSP sp = (EntityPlayerSP)entityPlayer;
         if (sp.capabilities.isFlying) {
            GravityDirection g = ExtendedPropertyGravity.getGravityDirection(sp);
            if (g == GravityDirection.upTOdown_YN) return;
            double amount = sp.capabilities.getFlySpeed() * 3.0F;
            double delta = 0.0;
            if (sp.movementInput.sneak) delta -= amount;
            if (sp.movementInput.jump) delta += amount;
            if (delta != 0.0) {
               sp.motionY -= delta;
               GravityLiving.addLocalUp(sp, g, delta);
            }
         }
      }
   }
}
