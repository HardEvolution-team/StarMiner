package com.electrifiedded.starminer.basics.dimention;

import com.electrifiedded.starminer.basics.Config;
import com.electrifiedded.starminer.basics.SMModContainer;
import com.electrifiedded.starminer.basics.tileentity.TileEntityGravityGenerator;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.world.World;
import net.minecraftforge.common.util.ITeleporter;

public class DimentionTeleportHandler {
   public boolean onUpdateEntityEnd(EntityPlayerMP player) {
      if (player.isRiding() || player.isBeingRidden()) {
         return false;
      }

      if (player.dimension == SMModContainer.GeostationaryOrbitDimentionId) {
         if (player.posY < -64.0) {
            transfer(player, 0, 288.0);
            return true;
         }

         if (player.posY > 384.0) {
            player.motionY = Math.min(player.motionY, -0.1);
         }
      } else if (player.dimension == 0 && player.posY > Config.TELEPORT_GROUND_POSY
         && (Config.ticketFreeForTeleport || TileEntityGravityGenerator.isGravityReverse(player, true))) {
         transfer(player, SMModContainer.GeostationaryOrbitDimentionId, Config.TELEPORT_SPACE_POSY);
         return true;
      }

      return false;
   }

   private static void transfer(EntityPlayerMP player, int dim, double y) {
      final double x = player.posX;
      final double z = player.posZ;
      player.changeDimension(dim, new ITeleporter() {
         @Override
         public void placeEntity(World world, Entity entity, float yaw) {
            entity.setLocationAndAngles(x, y, z, entity.rotationYaw, entity.rotationPitch);
            entity.motionX = 0.0;
            entity.motionY = 0.0;
            entity.motionZ = 0.0;
            entity.fallDistance = 0.0F;
         }

         @Override
         public boolean isVanilla() {
            return false;
         }
      });
      player.connection.setPlayerLocation(x, y, z, player.rotationYaw, player.rotationPitch);
   }
}
