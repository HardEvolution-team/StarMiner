package com.electrifiedded.starminer.basics;

import java.io.File;
import com.electrifiedded.archaiclib.legacy.registry.LegacyGameRegistry;
import com.electrifiedded.archaiclib.legacy.registry.LegacyRecipes;
import com.electrifiedded.archaiclib.v1710.api.item.ArchaicItem;
import com.electrifiedded.starminer.StarMiner;
import com.electrifiedded.starminer.basics.block.BlockAirEx;
import com.electrifiedded.starminer.basics.block.BlockDirtGrassEx;
import com.electrifiedded.starminer.basics.block.BlockGravityCore;
import com.electrifiedded.starminer.basics.block.BlockGravityWall;
import com.electrifiedded.starminer.basics.block.BlockInnerCore;
import com.electrifiedded.starminer.basics.block.BlockManBazooka;
import com.electrifiedded.starminer.basics.block.BlockNavigator;
import com.electrifiedded.starminer.basics.block.BlockOuterCore;
import com.electrifiedded.starminer.basics.block.gravitized.BlockCropsGravitized;
import com.electrifiedded.starminer.basics.block.gravitized.BlockFlowerGravitized;
import com.electrifiedded.starminer.basics.block.gravitized.BlockSaplingGravitized;
import com.electrifiedded.starminer.basics.block.gravitized.BlockTallGrassGravitized;
import com.electrifiedded.starminer.basics.common.CommonForgeEventHandler;
import com.electrifiedded.starminer.basics.common.CommonProxy;
import com.electrifiedded.starminer.basics.dimention.WorldProviderSpace;
import com.electrifiedded.starminer.basics.entity.EntityFallingBlockEx;
import com.electrifiedded.starminer.basics.entity.EntityGProjectile;
import com.electrifiedded.starminer.basics.entity.EntityStarSquid;
import com.electrifiedded.starminer.basics.item.ItemBlockGun;
import com.electrifiedded.starminer.basics.item.ItemEarthStone;
import com.electrifiedded.starminer.basics.item.ItemGrappleGun;
import com.electrifiedded.starminer.basics.item.ItemGravityContoler;
import com.electrifiedded.starminer.basics.item.ItemLifeSoup;
import com.electrifiedded.starminer.basics.item.ItemPortableGravigy;
import com.electrifiedded.starminer.basics.item.ItemSolarWindFan;
import com.electrifiedded.starminer.basics.item.ItemStarContoler;
import com.electrifiedded.starminer.basics.item.block.ItemPlantG;
import com.electrifiedded.starminer.basics.item.crop.ItemSeedFoodGravitized;
import com.electrifiedded.starminer.basics.item.crop.ItemSeedGravitized;
import com.electrifiedded.starminer.basics.tileentity.TileEntityGravityGenerator;
import com.electrifiedded.starminer.basics.tileentity.TileEntityNavigator;
import net.minecraft.block.Block;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.DimensionType;
import net.minecraft.world.gen.structure.MapGenStructureIO;
import com.electrifiedded.starminer.basics.structure.ComponentStar;
import com.electrifiedded.starminer.basics.structure.StructureStarStart;
import com.electrifiedded.starminer.basics.dimention.StarOreGen;
import net.minecraftforge.common.DimensionManager;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.fml.common.network.NetworkRegistry;
import net.minecraftforge.fml.common.registry.GameRegistry;
import net.minecraftforge.oredict.OreDictionary;

public class SMModContainer {
   public static CommonProxy proxy;
   public static int GeostationaryOrbitDimentionId = 88;
   public static int guiStarCoreId = 1243;
   public static Item GravityControllerItem;
   public static Item StarControlerItem;
   public static Item SolarWindFanItem;
   public static Item StardustItem;
   public static Item GrappleGunItem;
   public static Item BlockGunItem;
   public static Item GArrowItem;
   public static Item GHookItem;
   public static Item LifeSoupItem;
   public static Item PortableGravityItem;
   public static Item EarthStone;
   public static Item SeedGravizedItem;
   public static Item CarrotGravizedItem;
   public static Item PotatoGravizedItem;
   public static Block GravityCoreBlock;
   public static Block InnerCoreBlock;
   public static Block OuterCoreBlock;
   public static Block ManBazookaBlock;
   public static Block NavigatorBlock;
   public static Block GravityWallBlock;
   public static Block AirExBlock;
   public static Block DirtGrassExBlock;
   public static Block CropGravitizedBlock;
   public static Block PlantYelGravitizedBlock;
   public static Block PlantRedGravitizedBlock;
   public static Block SaplingGravitizedBlock;
   public static Block CarrotGravitizedBlock;
   public static Block PotatoGravitizedBlock;
   public static Block TallGrassGravitizedBlock;
   public static final CreativeTabs starMinerTab = new CreativeTabs("StarMiner") {
      @Override
      public ItemStack createIcon() {
         return new ItemStack(SMModContainer.GravityCoreBlock);
      }
   };

   public static void preInit(File configDir) {
      try {
         Configuration config = new Configuration(new File(configDir, "Starminer.cfg"));
         config.load();
         String s = Config.CONFIG_CAT_SERVERSIDE;
         config.addCustomCategoryComment(s, "Affects server side only.");
         Config.bazookaStartSpeed = config.get(s, "bazookaStartSpeed", 3.3).getDouble(3.3);
         Config.generateStars = config.get(s, "generateStars", true).getBoolean(true);
         Config.generateOres = config.get(s, "generateOres", false).getBoolean(false);
         Config.ticketFreeForTeleport = config.get(s, "ticketFreeForTeleport", false).getBoolean(false);
         Config.attractCheckTick = Math.max(1, config.get(s, "attractCheckTick", 8).getInt(8));
         Config.enableFakeRotatorOnlyVannilaBlock = config.get(s, "enableFakeRotatorOnlyVannilaBlock", true).getBoolean(true);
         Config.maxStarRad = config.get(s, "maxStarRad", 48).getInt(48);
         Config.maxGravityRad = config.get(s, "maxGravityRad", 54).getInt(54);
         GeostationaryOrbitDimentionId = config.get(s, "GSODimentionId_byte", 88).getInt(88);
         String c = Config.CONFIG_CAT_CLIENTSIDE;
         config.addCustomCategoryComment(c, "Affects client side only.");
         Config.naviLaserLength = config.get(c, "naviLaserLength", 7).getInt(7);
         if (config.hasChanged()) {
            config.save();
         }
      } catch (Exception e) {
         e.printStackTrace();
      }

      registerContent();
      MapGenStructureIO.registerStructure(StructureStarStart.class, "StarminerStarStart");
      MapGenStructureIO.registerStructureComponent(ComponentStar.class, "StarminerStar");
      ComponentStar.init();
      WorldProviderSpace.SPACE_TYPE = DimensionType.register("GeostationaryOrbit", "_starminer_space", GeostationaryOrbitDimentionId, WorldProviderSpace.class, false);
      DimensionManager.registerDimension(GeostationaryOrbitDimentionId, WorldProviderSpace.SPACE_TYPE);
   }

   private static <T extends Block> T block(T block, String name) {
      block.setTranslationKey(name);
      block.setCreativeTab(starMinerTab);
      LegacyGameRegistry.registerBlock(block, name);
      return block;
   }

   private static <T extends Block> T block(T block, Item item, String name) {
      block.setTranslationKey(name);
      block.setCreativeTab(starMinerTab);
      LegacyGameRegistry.registerBlock(block, item, name);
      return block;
   }

   private static <T extends Item> T item(T item, String name, String translationKey) {
      item.setTranslationKey(translationKey);
      item.setCreativeTab(starMinerTab);
      LegacyGameRegistry.registerItem(item, name);
      return item;
   }

   private static void registerContent() {
      GravityCoreBlock = block(new BlockGravityCore(), "gravitycore");
      InnerCoreBlock = block(new BlockInnerCore(), "innercore");
      OuterCoreBlock = block(new BlockOuterCore(), "outercore");
      ManBazookaBlock = block(new BlockManBazooka(), "manbazooka");
      NavigatorBlock = block(new BlockNavigator(), "navigator");
      GravityWallBlock = block(new BlockGravityWall(), "gravitywall");
      AirExBlock = new BlockAirEx().setTranslationKey("airex");
      LegacyGameRegistry.registerBlock(AirExBlock, null, "airex");
      DirtGrassExBlock = block(new BlockDirtGrassEx(), "dirtgrassex");
      CropGravitizedBlock = new BlockCropsGravitized(0).setTranslationKey("cropgravitized");
      LegacyGameRegistry.registerBlock(CropGravitizedBlock, null, "cropgravitized");
      CarrotGravitizedBlock = new BlockCropsGravitized(1).setTranslationKey("carrotgravitized");
      LegacyGameRegistry.registerBlock(CarrotGravitizedBlock, null, "carrotgravitized");
      PotatoGravitizedBlock = new BlockCropsGravitized(2).setTranslationKey("potatogravitized");
      LegacyGameRegistry.registerBlock(PotatoGravitizedBlock, null, "potatogravitized");
      SaplingGravitizedBlock = new BlockSaplingGravitized();
      block(SaplingGravitizedBlock, new ItemPlantG(SaplingGravitizedBlock, BlockSaplingGravitized.NAMES), "saplinggravitized");
      PlantYelGravitizedBlock = new BlockFlowerGravitized(0);
      block(PlantYelGravitizedBlock, new ItemPlantG(PlantYelGravitizedBlock, BlockFlowerGravitized.YELLOW_NAMES), "yflowergravitized");
      PlantRedGravitizedBlock = new BlockFlowerGravitized(1);
      block(PlantRedGravitizedBlock, new ItemPlantG(PlantRedGravitizedBlock, BlockFlowerGravitized.RED_NAMES), "rflowergravitized");
      TallGrassGravitizedBlock = new BlockTallGrassGravitized();
      block(TallGrassGravitizedBlock, new ItemPlantG(TallGrassGravitizedBlock, BlockTallGrassGravitized.NAMES), "tallgrassgravitized");
      OreDictionary.registerOre("blockGravityCore", GravityCoreBlock);
      OreDictionary.registerOre("blockInnerCore", InnerCoreBlock);
      OreDictionary.registerOre("blockOuterCore", OuterCoreBlock);
      OreDictionary.registerOre("blockManBazooka", ManBazookaBlock);
      OreDictionary.registerOre("blockNavigator", NavigatorBlock);
      OreDictionary.registerOre("blockGravityWall", GravityWallBlock);
      OreDictionary.registerOre("blockStarDirt", DirtGrassExBlock);
      GravityControllerItem = item(new ItemGravityContoler(), "gravitycontroller", "gravitycontroller");
      StarControlerItem = item(new ItemStarContoler(), "starcontroller", "starcontroller");
      SolarWindFanItem = item(new ItemSolarWindFan(), "solarwindfan", "solarwindFan");
      StardustItem = item(new Item(), "starcore_dust", "starcore_dust");
      GrappleGunItem = item(new ItemGrappleGun(), "g_rapplegun", "g_rapplegun");
      GArrowItem = item(new Item(), "g_arrow", "g_arrow");
      GHookItem = item(new Item(), "g_hook", "g_hook");
      BlockGunItem = item(new ItemBlockGun(), "blockgun", "blockgun");
      LifeSoupItem = item(new ItemLifeSoup(), "lifesoup", "lifesoup");
      PortableGravityItem = item(new ItemPortableGravigy(), "portablegravity", "portablegravity");
      EarthStone = item(new ItemEarthStone(), "earthstone", "earthstone");
      SeedGravizedItem = item(new ItemSeedGravitized(CropGravitizedBlock), "seedsgravitized", "seedsgravitized");
      CarrotGravizedItem = item(new ItemSeedFoodGravitized(CarrotGravitizedBlock, 3, 0.6F), "carrotgravitizeditem", "carrotgravitizeditem");
      PotatoGravizedItem = item(new ItemSeedFoodGravitized(PotatoGravitizedBlock, 1, 0.3F), "potatogravitizeditem", "potatogravitizeditem");
      OreDictionary.registerOre("gemStardust", StardustItem);
      GameRegistry.registerTileEntity(TileEntityGravityGenerator.class, new ResourceLocation("starminer", "gravitycore"));
      GameRegistry.registerTileEntity(TileEntityNavigator.class, new ResourceLocation("starminer", "navigator"));
      LegacyGameRegistry.registerModEntity(EntityFallingBlockEx.class, "FallingBlockEx", 564, StarMiner.instance, 30, 1, true);
      LegacyGameRegistry.registerModEntity(EntityStarSquid.class, "StarSquid", 565, StarMiner.instance, 80, 1, true);
      LegacyGameRegistry.registerModEntity(EntityGProjectile.class, "GProjectile", 566, StarMiner.instance, 80, 1, true);
      Port1Registry.registerCommon();
      Port3Registry.registerCommon();
      Port2Registry.registerCommon();
      registerItemModels();
   }

   private static void registerItemModels() {
      ArchaicItem.registerMetaTexture(StarControlerItem, 0, "scontroler");
      ArchaicItem.registerMetaTexture(SolarWindFanItem, 0, "uchiwa");
      ArchaicItem.registerMetaTexture(StardustItem, 0, "minecraft:glowstone_dust");
      ArchaicItem.registerMetaTexture(GrappleGunItem, 0, "minecraft:bow_pulling_2");
      ArchaicItem.registerMetaTexture(GArrowItem, 0, "minecraft:arrow");
      ArchaicItem.registerMetaTexture(GHookItem, 0, "g-rapplegun_arrow");
      ArchaicItem.registerMetaTexture(EarthStone, 0, "starmark");
      ArchaicItem.registerMetaTexture(SeedGravizedItem, 0, "minecraft:seeds_wheat");
      ArchaicItem.registerMetaTexture(CarrotGravizedItem, 0, "minecraft:carrot");
      ArchaicItem.registerMetaTexture(PotatoGravizedItem, 0, "minecraft:potato");
      String[] gun = {"blockgun_short", "blockgun_normal", "blockgun_long", "blockgun_spread"};
      for (int i = 0; i < gun.length; i++) {
         ArchaicItem.registerMetaTexture(BlockGunItem, i, gun[i]);
      }
      String[] soup = {"lifesoup_a", "lifesoup_v", "lifesoup_v"};
      for (int i = 0; i < soup.length; i++) {
         ArchaicItem.registerMetaTexture(LifeSoupItem, i, soup[i]);
      }
      LegacyGameRegistry.registerItemModel(GravityControllerItem, 0, "starminer:gravitycontroller", "inventory");
      LegacyGameRegistry.registerItemModel(PortableGravityItem, 0, "starminer:portablegravity", "inventory");
   }

   public static void init() {
      CommonForgeEventHandler handler = new CommonForgeEventHandler();
      MinecraftForge.EVENT_BUS.register(handler);
      if (Config.generateOres) {
         MinecraftForge.ORE_GEN_BUS.register(new StarOreGen());
      }

      NetworkRegistry.INSTANCE.registerGuiHandler(StarMiner.instance, proxy);
      addRecipes();
   }

   private static void addRecipes() {
      Port3Registry.addRecipes();
      LegacyRecipes.addRecipe(new ItemStack(OuterCoreBlock), "XZX", "ZYZ", "XZX", 'X', Blocks.SANDSTONE, 'Y', Items.LAVA_BUCKET, 'Z', Blocks.DIRT);
      LegacyRecipes.addRecipe(new ItemStack(OuterCoreBlock), "XXX", "XXX", "XXX", 'X', StardustItem);
      LegacyRecipes.addRecipe(new ItemStack(InnerCoreBlock), "ZXZ", "XYX", "ZXZ", 'X', Blocks.OBSIDIAN, 'Y', Blocks.IRON_BLOCK, 'Z', Blocks.GLASS);
      LegacyRecipes.addRecipe(new ItemStack(GravityCoreBlock), "XXX", "XYX", "XXX", 'X', OuterCoreBlock, 'Y', InnerCoreBlock);
      LegacyRecipes.addRecipe(new ItemStack(GravityWallBlock), "XXX", 'X', StardustItem);
      LegacyRecipes.addRecipe(new ItemStack(GravityControllerItem), "X  ", "YZZ", "X Z", 'X', OuterCoreBlock, 'Y', InnerCoreBlock, 'Z', Items.STICK);
      LegacyRecipes.addRecipe(new ItemStack(StarControlerItem), "XYX", "XXX", " Z ", 'X', Items.IRON_INGOT, 'Y', InnerCoreBlock, 'Z', GravityControllerItem);
      LegacyRecipes.addRecipe(new ItemStack(SolarWindFanItem), "XXY", "XZY", "YYW", 'X', OuterCoreBlock, 'Y', new ItemStack(Items.DYE, 1, 4), 'Z', InnerCoreBlock, 'W', Items.STICK);
      LegacyRecipes.addShapelessRecipe(new ItemStack(StardustItem, 9), new ItemStack(OuterCoreBlock, 1));
      LegacyRecipes.addRecipe(new ItemStack(BlockGunItem, 1, 0), " XX", "XXY", "  Z", 'X', Blocks.COBBLESTONE, 'Y', StardustItem, 'Z', GravityControllerItem);
      LegacyRecipes.addShapelessRecipe(new ItemStack(BlockGunItem, 1, 1), new ItemStack(BlockGunItem, 1, 0));
      LegacyRecipes.addShapelessRecipe(new ItemStack(BlockGunItem, 1, 2), new ItemStack(BlockGunItem, 1, 1));
      LegacyRecipes.addShapelessRecipe(new ItemStack(BlockGunItem, 1, 3), new ItemStack(BlockGunItem, 1, 2));
      LegacyRecipes.addShapelessRecipe(new ItemStack(BlockGunItem, 1, 0), new ItemStack(BlockGunItem, 1, 3));
      LegacyRecipes.addRecipe(new ItemStack(GrappleGunItem), " XX", "XXY", "  Z", 'X', Blocks.OBSIDIAN, 'Y', GravityCoreBlock, 'Z', GravityControllerItem);
      LegacyRecipes.addShapelessRecipe(new ItemStack(GArrowItem, 1), new ItemStack(Items.ARROW, 1), new ItemStack(StardustItem, 1));
      LegacyRecipes.addShapelessRecipe(new ItemStack(GHookItem, 1), new ItemStack(GArrowItem, 1), new ItemStack(Items.STRING, 1));
      LegacyRecipes.addRecipe(new ItemStack(NavigatorBlock, 2), "X", "Y", 'X', OuterCoreBlock, 'Y', Blocks.TORCH);
      LegacyRecipes.addRecipe(new ItemStack(ManBazookaBlock), "XTX", "XZX", "YWY", 'X', Blocks.COBBLESTONE_WALL, 'T', Blocks.PLANKS, 'Z', Items.GUNPOWDER, 'Y', Blocks.COBBLESTONE, 'W', Blocks.FURNACE);
      LegacyRecipes.addRecipe(new ItemStack(ManBazookaBlock), "XTX", "XZX", "YWY", 'X', Blocks.COBBLESTONE_WALL, 'T', Blocks.PLANKS, 'Z', InnerCoreBlock, 'Y', Blocks.COBBLESTONE, 'W', Blocks.FURNACE);
      LegacyRecipes.addRecipe(new ItemStack(PortableGravityItem), " X ", "XYX", " X ", 'X', Items.IRON_INGOT, 'Y', GravityCoreBlock);
      LegacyRecipes.addShapelessRecipe(new ItemStack(LifeSoupItem, 1, 1), new ItemStack(StardustItem, 1), new ItemStack(Items.WATER_BUCKET, 1));
      LegacyRecipes.addShapelessRecipe(new ItemStack(LifeSoupItem, 1, 2), new ItemStack(LifeSoupItem, 1, 0), new ItemStack(Items.BOOK, 1));
      LegacyRecipes.addShapelessRecipe(new ItemStack(DirtGrassExBlock, 1), new ItemStack(Blocks.DIRT, 1), new ItemStack(StardustItem, 1));
      LegacyRecipes.addShapelessRecipe(new ItemStack(SeedGravizedItem, 1), new ItemStack(Items.WHEAT_SEEDS, 1), new ItemStack(StardustItem, 1));
      LegacyRecipes.addShapelessRecipe(new ItemStack(CarrotGravizedItem, 1), new ItemStack(Items.CARROT, 1), new ItemStack(StardustItem, 1));
      LegacyRecipes.addShapelessRecipe(new ItemStack(PotatoGravizedItem, 1), new ItemStack(Items.POTATO, 1), new ItemStack(StardustItem, 1));
      LegacyRecipes.addShapelessRecipe(new ItemStack(PlantYelGravitizedBlock, 1, 0), new ItemStack(Blocks.YELLOW_FLOWER, 1, 0), new ItemStack(StardustItem, 1));
      for (int i = 0; i < 9; i++) {
         LegacyRecipes.addShapelessRecipe(new ItemStack(PlantRedGravitizedBlock, 1, i), new ItemStack(Blocks.RED_FLOWER, 1, i), new ItemStack(StardustItem, 1));
      }

      for (int i = 0; i < 6; i++) {
         LegacyRecipes.addShapelessRecipe(new ItemStack(SaplingGravitizedBlock, 1, i), new ItemStack(Blocks.SAPLING, 1, i), new ItemStack(StardustItem, 1));
      }
   }
}
