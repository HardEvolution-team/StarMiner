package com.electrifiedded.starminer.basics;

import com.electrifiedded.archaiclib.legacy.registry.LegacyGameRegistry;
import com.electrifiedded.archaiclib.legacy.registry.LegacyRecipes;
import com.electrifiedded.starminer.api.IRotateSleepingViewHandler;
import com.electrifiedded.starminer.basics.block.bed.BlockStarBedBody;
import com.electrifiedded.starminer.basics.block.bed.BlockStarBedHead;
import com.electrifiedded.starminer.basics.common.SleepingViewHandler;
import com.electrifiedded.starminer.basics.common.StarBedClientEvents;
import com.electrifiedded.starminer.basics.common.StarBedEvents;
import com.electrifiedded.starminer.basics.item.ItemStarBed;
import net.minecraft.block.Block;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraftforge.common.MinecraftForge;

public class Port2Registry {
   public static Block StarBedBodyBlock;
   public static Block StarBedHeadBlock;
   public static Item StarBedItem;

   public static void registerCommon() {
      StarBedBodyBlock = new BlockStarBedBody().setTranslationKey("starbedbody");
      StarBedHeadBlock = new BlockStarBedHead().setTranslationKey("starbedhead");
      LegacyGameRegistry.registerBlock(StarBedBodyBlock, null, "starbedbody");
      LegacyGameRegistry.registerBlock(StarBedHeadBlock, null, "starbedhead");
      StarBedItem = new ItemStarBed();
      StarBedItem.setTranslationKey("starbed");
      StarBedItem.setCreativeTab(SMModContainer.starMinerTab);
      LegacyGameRegistry.registerItem(StarBedItem, "starbed");
      LegacyGameRegistry.registerItemModel(StarBedItem, 0, "starminer:starbed", "inventory");
      LegacyRecipes.addRecipe(new ItemStack(StarBedItem), " X ", "YYY", 'X', Items.BED, 'Y', SMModContainer.GravityWallBlock);
      MinecraftForge.EVENT_BUS.register(new StarBedEvents());
   }

   public static void registerClient() {
      Client.run();
   }

   private static class Client {
      static void run() {
      IRotateSleepingViewHandler.handlerList.add(new SleepingViewHandler());
      MinecraftForge.EVENT_BUS.register(new StarBedClientEvents());
      }
   }
}
