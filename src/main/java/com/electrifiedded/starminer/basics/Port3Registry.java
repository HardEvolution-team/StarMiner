package com.electrifiedded.starminer.basics;

import com.electrifiedded.archaiclib.legacy.registry.LegacyGameRegistry;
import com.electrifiedded.archaiclib.legacy.registry.LegacyRecipes;
import com.electrifiedded.starminer.basics.block.BlockChestEx;
import com.electrifiedded.starminer.basics.block.BlockRotator;
import com.electrifiedded.starminer.basics.item.block.ItemBlockForRotator;
import com.electrifiedded.starminer.basics.tileentity.TileEntityBlockRotator;
import com.electrifiedded.starminer.basics.tileentity.TileEntityChestEx;
import net.minecraft.block.Block;
import net.minecraft.init.Blocks;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.common.registry.GameRegistry;

public class Port3Registry {
   public static Block ChestExBlock;
   public static Block BlockRotatorBlock;

   public static void registerCommon() {
      ChestExBlock = register(new BlockChestEx(0), "chestgravitized");
      BlockRotatorBlock = register(new BlockRotator(), "blockrotator");
      GameRegistry.registerTileEntity(TileEntityChestEx.class, new ResourceLocation("starminer", "chestex"));
      GameRegistry.registerTileEntity(TileEntityBlockRotator.class, new ResourceLocation("starminer", "blockrotator"));
   }

   private static Block register(Block block, String name) {
      Item item = block instanceof BlockRotator ? new ItemBlockForRotator(block) : new ItemBlock(block);
      block.setTranslationKey(name);
      block.setCreativeTab(SMModContainer.starMinerTab);
      LegacyGameRegistry.registerBlock(block, item, name);
      return block;
   }

   public static void addRecipes() {
      LegacyRecipes.addRecipe(new ItemStack(ChestExBlock), " X ", "YYY", 'X', Blocks.CHEST, 'Y', SMModContainer.GravityWallBlock);
      LegacyRecipes.addShapelessRecipe(new ItemStack(BlockRotatorBlock, 1), new ItemStack(Blocks.GLASS, 1), new ItemStack(SMModContainer.StardustItem, 1));
   }

   public static void registerClient() {
      Client.run();
   }

   private static class Client {
      static void run() {
      net.minecraftforge.fml.client.registry.ClientRegistry.bindTileEntitySpecialRenderer(
         TileEntityChestEx.class, new com.electrifiedded.starminer.basics.tileentity.render.TileEntityChestRendererEx());
      net.minecraftforge.fml.client.registry.ClientRegistry.bindTileEntitySpecialRenderer(
         TileEntityBlockRotator.class, new com.electrifiedded.starminer.basics.tileentity.render.TileEnityRenderBlockRotator());
      }
   }
}
