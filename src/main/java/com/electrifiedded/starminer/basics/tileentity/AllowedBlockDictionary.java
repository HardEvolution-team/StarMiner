package com.electrifiedded.starminer.basics.tileentity;

import java.util.HashMap;
import com.electrifiedded.starminer.basics.SMModContainer;
import net.minecraft.block.Block;
import net.minecraft.init.Blocks;


public class AllowedBlockDictionary {
   public static final HashMap<Block, Object> placableBlockMap = new HashMap<>();
   public static final Object ALLOWED = new Object();

   public static final boolean isAllowed(Block block) {
      return block == null ? false : placableBlockMap.containsKey(block);
   }

   static {
      placableBlockMap.put(SMModContainer.OuterCoreBlock, ALLOWED);
      placableBlockMap.put(SMModContainer.InnerCoreBlock, ALLOWED);
      placableBlockMap.put(SMModContainer.DirtGrassExBlock, ALLOWED);
      placableBlockMap.put(Blocks.STONE, ALLOWED);
      placableBlockMap.put(Blocks.DIRT, ALLOWED);
      placableBlockMap.put(Blocks.COBBLESTONE, ALLOWED);
      placableBlockMap.put(Blocks.PLANKS, ALLOWED);
      placableBlockMap.put(Blocks.BEDROCK, ALLOWED);
      placableBlockMap.put(Blocks.SAND, ALLOWED);
      placableBlockMap.put(Blocks.GRAVEL, ALLOWED);
      placableBlockMap.put(Blocks.GOLD_ORE, ALLOWED);
      placableBlockMap.put(Blocks.IRON_ORE, ALLOWED);
      placableBlockMap.put(Blocks.COAL_ORE, ALLOWED);
      placableBlockMap.put(Blocks.LOG, ALLOWED);
      placableBlockMap.put(Blocks.LOG2, ALLOWED);
      placableBlockMap.put(Blocks.LEAVES, ALLOWED);
      placableBlockMap.put(Blocks.LEAVES2, ALLOWED);
      placableBlockMap.put(Blocks.SPONGE, ALLOWED);
      placableBlockMap.put(Blocks.GLASS, ALLOWED);
      placableBlockMap.put(Blocks.LAPIS_ORE, ALLOWED);
      placableBlockMap.put(Blocks.LAPIS_BLOCK, ALLOWED);
      placableBlockMap.put(Blocks.SANDSTONE, ALLOWED);
      placableBlockMap.put(Blocks.WEB, ALLOWED);
      placableBlockMap.put(Blocks.WOOL, ALLOWED);
      placableBlockMap.put(Blocks.GOLD_BLOCK, ALLOWED);
      placableBlockMap.put(Blocks.IRON_BLOCK, ALLOWED);
      placableBlockMap.put(Blocks.BRICK_BLOCK, ALLOWED);
      placableBlockMap.put(Blocks.TNT, ALLOWED);
      placableBlockMap.put(Blocks.BOOKSHELF, ALLOWED);
      placableBlockMap.put(Blocks.MOSSY_COBBLESTONE, ALLOWED);
      placableBlockMap.put(Blocks.OBSIDIAN, ALLOWED);
      placableBlockMap.put(Blocks.DIAMOND_ORE, ALLOWED);
      placableBlockMap.put(Blocks.DIAMOND_BLOCK, ALLOWED);
      placableBlockMap.put(Blocks.REDSTONE_ORE, ALLOWED);
      placableBlockMap.put(Blocks.SNOW_LAYER, ALLOWED);
      placableBlockMap.put(Blocks.ICE, ALLOWED);
      placableBlockMap.put(Blocks.CLAY, ALLOWED);
      placableBlockMap.put(Blocks.PUMPKIN, ALLOWED);
      placableBlockMap.put(Blocks.NETHERRACK, ALLOWED);
      placableBlockMap.put(Blocks.SOUL_SAND, ALLOWED);
      placableBlockMap.put(Blocks.GLOWSTONE, ALLOWED);
      placableBlockMap.put(Blocks.LIT_PUMPKIN, ALLOWED);
      placableBlockMap.put(Blocks.CAKE, ALLOWED);
      placableBlockMap.put(Blocks.STONEBRICK, ALLOWED);
      placableBlockMap.put(Blocks.MELON_BLOCK, ALLOWED);
      placableBlockMap.put(Blocks.MYCELIUM, ALLOWED);
      placableBlockMap.put(Blocks.NETHER_BRICK, ALLOWED);
      placableBlockMap.put(Blocks.END_STONE, ALLOWED);
      placableBlockMap.put(Blocks.EMERALD_ORE, ALLOWED);
      placableBlockMap.put(Blocks.EMERALD_BLOCK, ALLOWED);
      placableBlockMap.put(Blocks.ANVIL, ALLOWED);
      placableBlockMap.put(Blocks.REDSTONE_BLOCK, ALLOWED);
      placableBlockMap.put(Blocks.QUARTZ_ORE, ALLOWED);
      placableBlockMap.put(Blocks.QUARTZ_BLOCK, ALLOWED);
      placableBlockMap.put(Blocks.STAINED_HARDENED_CLAY, ALLOWED);
      placableBlockMap.put(Blocks.HAY_BLOCK, ALLOWED);
      placableBlockMap.put(Blocks.HARDENED_CLAY, ALLOWED);
      placableBlockMap.put(Blocks.COAL_BLOCK, ALLOWED);
      placableBlockMap.put(Blocks.PACKED_ICE, ALLOWED);
      placableBlockMap.put(Blocks.STAINED_GLASS, ALLOWED);
   }
}
