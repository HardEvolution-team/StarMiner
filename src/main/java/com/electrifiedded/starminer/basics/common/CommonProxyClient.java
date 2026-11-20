package com.electrifiedded.starminer.basics.common;

import com.electrifiedded.starminer.basics.SMModContainer;
import com.electrifiedded.starminer.basics.gui.GuiStarCore;
import com.electrifiedded.starminer.basics.tileentity.TileEntityGravityGenerator;
import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.I18n;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class CommonProxyClient extends CommonProxy {
   @Override
   public void preInit() {
      super.preInit();
      com.electrifiedded.starminer.basics.Port1Registry.registerClient();
      com.electrifiedded.starminer.basics.Port3Registry.registerClient();
      com.electrifiedded.starminer.basics.Port2Registry.registerClient();
      net.minecraftforge.fml.client.registry.RenderingRegistry.registerEntityRenderingHandler(
         com.electrifiedded.starminer.basics.entity.EntityStarSquid.class, com.electrifiedded.starminer.basics.client.RenderStarSquid::new);
      net.minecraftforge.fml.client.registry.RenderingRegistry.registerEntityRenderingHandler(
         com.electrifiedded.starminer.basics.entity.EntityGProjectile.class, com.electrifiedded.starminer.basics.client.RenderGProjectile::new);
      net.minecraftforge.fml.client.registry.RenderingRegistry.registerEntityRenderingHandler(
         com.electrifiedded.starminer.basics.entity.EntityFallingBlockEx.class, com.electrifiedded.starminer.basics.client.RenderFallingBlockEx::new);
      net.minecraftforge.fml.client.registry.ClientRegistry.bindTileEntitySpecialRenderer(
         com.electrifiedded.starminer.basics.tileentity.TileEntityNavigator.class, new com.electrifiedded.starminer.basics.client.TileEntityNavigatorRenderer());
   }

   @Override
   public Object getClientGuiElement(int id, EntityPlayer player, World world, int x, int y, int z) {
      if (id == SMModContainer.guiStarCoreId) {
         TileEntity te = world.getTileEntity(new BlockPos(x, y, z));
         if (te instanceof TileEntityGravityGenerator) {
            return new GuiStarCore(player, (TileEntityGravityGenerator)te);
         }
      }
      return null;
   }

   @Override
   public void showGrappleGunGuideMessage() {
      Minecraft mc = Minecraft.getMinecraft();
      mc.ingameGUI.setOverlayMessage(I18n.format("item.g_rapplegun.keyguide", mc.gameSettings.keyBindSneak.getDisplayName()), false);
   }
}
