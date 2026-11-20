package com.electrifiedded.starminer;

import com.electrifiedded.archaiclib.original.OriginalModAssets;
import com.electrifiedded.starminer.basics.SMModContainer;
import com.electrifiedded.starminer.basics.common.CommonProxy;
import com.electrifiedded.starminer.core.SMCore;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventHandler;
import net.minecraftforge.fml.common.SidedProxy;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;

@Mod(modid = Reference.MOD_ID, name = Reference.MOD_NAME, version = Reference.VERSION, dependencies = "required-after:archaiclib")
public class StarMiner {
   @Mod.Instance(Reference.MOD_ID)
   public static StarMiner instance;

   @SidedProxy(clientSide = "com.electrifiedded.starminer.basics.common.CommonProxyClient", serverSide = "com.electrifiedded.starminer.basics.common.CommonProxy")
   public static CommonProxy proxy;

   public static final String ORIGINAL_NAME = "starminer1710-0.9.9.jar - Starminer 0.9.9 for 1.7.10 (by ARUBE)";
   public static final String ORIGINAL_URL = "https://mcarchive.net/mods/starminer";

   public StarMiner() {
      // textures come from the player's own copy of the original jar (archaic_originals/), never bundled
      OriginalModAssets.alias(Reference.MOD_ID, "textures/items/starmark.png", "textures/blocks/starmark.png");
      OriginalModAssets.hide(Reference.MOD_ID, "lang/en_us.lang");
      OriginalModAssets.hide(Reference.MOD_ID, "lang/ja_jp.lang");
   }

   @EventHandler
   public void preInit(FMLPreInitializationEvent event) {
      OriginalModAssets.require(Reference.MOD_ID, "modJ_StarMiner", ORIGINAL_NAME, ORIGINAL_URL, Reference.MOD_ID);
      SMCore.preInit(event.getModConfigurationDirectory());
      SMModContainer.proxy = proxy;
      SMModContainer.preInit(event.getModConfigurationDirectory());
      proxy.preInit();
   }

   @EventHandler
   public void init(FMLInitializationEvent event) {
      SMCore.init();
      SMModContainer.init();
      proxy.init();
   }
}
