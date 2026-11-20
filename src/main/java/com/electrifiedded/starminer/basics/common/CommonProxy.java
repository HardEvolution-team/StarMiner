package com.electrifiedded.starminer.basics.common;

import com.electrifiedded.starminer.basics.SMModContainer;
import com.electrifiedded.starminer.basics.gui.ContainerStarCore;
import com.electrifiedded.starminer.basics.tileentity.TileEntityGravityGenerator;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.network.IGuiHandler;

public class CommonProxy implements IGuiHandler {
   public void preInit() {
   }

   public void init() {
   }

   @Override
   public Object getServerGuiElement(int id, EntityPlayer player, World world, int x, int y, int z) {
      if (id == SMModContainer.guiStarCoreId) {
         TileEntity te = world.getTileEntity(new BlockPos(x, y, z));
         if (te instanceof TileEntityGravityGenerator) {
            return new ContainerStarCore(player, (TileEntityGravityGenerator)te);
         }
      }
      return null;
   }

   @Override
   public Object getClientGuiElement(int id, EntityPlayer player, World world, int x, int y, int z) {
      return null;
   }

   public void showGrappleGunGuideMessage() {
   }
}
