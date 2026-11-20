package com.electrifiedded.starminer.basics;

import com.electrifiedded.archaiclib.legacy.registry.LegacyGameRegistry;
import com.electrifiedded.archaiclib.v1710.api.network.ArchaicChannel;
import com.electrifiedded.starminer.StarMiner;
import com.electrifiedded.starminer.basics.ai.mobai.MobAnimalsAIHelpers;
import com.electrifiedded.starminer.basics.ai.mobai.MobMonstersAIHelpers;
import com.electrifiedded.starminer.basics.dimention.MapFromSky;
import com.electrifiedded.starminer.basics.dimention.WorldProviderSpace;
import com.electrifiedded.starminer.basics.entity.EntityComet;
import com.electrifiedded.starminer.basics.entity.EntityReRideAttacher;
import com.electrifiedded.starminer.basics.packet.PacketSkyMap;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.world.WorldProviderSurface;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.world.WorldEvent;
import net.minecraftforge.fml.client.registry.RenderingRegistry;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.PlayerEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class Port1Registry {
   public static ArchaicChannel skyChannel;

   public static void registerCommon() {
      LegacyGameRegistry.registerModEntity(EntityReRideAttacher.class, "ReRideAttacher", 567, StarMiner.instance, 30, 1, true);
      LegacyGameRegistry.registerModEntity(EntityComet.class, "WanderingComet", 568, StarMiner.instance, 128, 1, true);
      MobAnimalsAIHelpers.register();
      MobMonstersAIHelpers.register();
      skyChannel = new ArchaicChannel("StarminerSky", PacketSkyMap.class);
      MinecraftForge.EVENT_BUS.register(new CommonEvents());
   }

   @SideOnly(Side.CLIENT)
   public static void registerClient() {
      Client.run();
   }

   private static class Client {
      static void run() {
      RenderingRegistry.registerEntityRenderingHandler(EntityComet.class, com.electrifiedded.starminer.basics.client.RenderComet::new);
      MinecraftForge.EVENT_BUS.register(new ClientEvents());
      MinecraftForge.EVENT_BUS.register(new com.electrifiedded.starminer.basics.dimention.renderer.BurningScreenEffectRenderer());
      }
   }

   public static class CommonEvents {
      @SubscribeEvent
      public void onWorldLoad(WorldEvent.Load event) {
         if (!event.getWorld().isRemote && event.getWorld().provider.getDimension() == 0 && event.getWorld().provider instanceof WorldProviderSurface) {
            MapFromSky.createMapDataFromSky(event.getWorld());
         }
      }

      @SubscribeEvent
      public void onPlayerLoggedIn(PlayerEvent.PlayerLoggedInEvent event) {
         if (event.player instanceof EntityPlayerMP && skyChannel != null && MapFromSky.mapDataFromSky != null) {
            skyChannel.sendToPlayer(new PacketSkyMap(MapFromSky.mapDataFromSky), (EntityPlayerMP)event.player);
         }
      }
   }

   @SideOnly(Side.CLIENT)
   public static class ClientEvents {
      @SubscribeEvent
      public void onWorldLoad(WorldEvent.Load event) {
         if (event.getWorld().isRemote && event.getWorld().provider instanceof WorldProviderSpace) {
            event.getWorld().provider.setSkyRenderer(new com.electrifiedded.starminer.basics.dimention.renderer.SpaceSkyRenderHandler());
            event.getWorld().provider.setCloudRenderer(new com.electrifiedded.starminer.basics.dimention.renderer.SpaceCloudRenderHandler());
         }
      }
   }
}
