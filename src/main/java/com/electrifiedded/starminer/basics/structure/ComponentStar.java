package com.electrifiedded.starminer.basics.structure;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import com.electrifiedded.starminer.api.IZeroGravityWorldProvider;
import com.electrifiedded.starminer.basics.SMModContainer;
import com.electrifiedded.starminer.basics.block.SMDir;
import com.electrifiedded.starminer.basics.block.gravitized.BlockFlowerGravitized;
import com.electrifiedded.starminer.basics.block.gravitized.BlockTallGrassGravitized;
import com.electrifiedded.starminer.basics.tileentity.TileEntityGravityGenerator;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;
import net.minecraft.world.gen.structure.StructureBoundingBox;
import net.minecraft.world.gen.structure.StructureComponent;
import net.minecraft.world.gen.structure.template.TemplateManager;

public class ComponentStar extends StructureComponent {
   private int starRad;
   private int ringRad;
   private int centerY;
   private int starSeed;
   private final Random rand = new com.electrifiedded.archaiclib.util.FastRandom();

   private static final Block[] BEDROCK_BLOCKS = {Blocks.DIRT, Blocks.SANDSTONE, Blocks.COBBLESTONE, Blocks.STONE, Blocks.NETHERRACK, Blocks.SPONGE, Blocks.CLAY};
   private static final Block[] ORE_BLOCKS = {Blocks.GLOWSTONE, Blocks.COAL_ORE, Blocks.IRON_ORE, Blocks.REDSTONE_ORE, Blocks.GLOWSTONE, Blocks.LAPIS_ORE};
   private static final Block[] ORE_BLOCKS_RARE = {Blocks.GOLD_ORE, Blocks.EMERALD_ORE, Blocks.DIAMOND_ORE, Blocks.IRON_BLOCK, Blocks.LAPIS_BLOCK};
   private static final Block[] ORE_BLOCKS_S_RARE = {Blocks.GOLD_BLOCK, Blocks.DIAMOND_BLOCK, Blocks.EMERALD_BLOCK};
   private static final int[][] TREESET_LST = {
      {StarTrees.OAK}, {StarTrees.SPRUCE}, {StarTrees.BIRCH}, {StarTrees.JUNGLE}, {StarTrees.ACACIA}, {StarTrees.ROOFED_OAK},
      {StarTrees.OAK, StarTrees.SPRUCE, StarTrees.BIRCH, StarTrees.JUNGLE, StarTrees.ACACIA, StarTrees.ROOFED_OAK}
   };
   private static StarBiomeType[] STAR_BIOMES;

   public ComponentStar() {
   }

   public ComponentStar(int type, Random random, int x, int z, World world) {
      super(type);
      this.starSeed = random.nextInt();
      if (world.provider instanceof IZeroGravityWorldProvider) {
         if (random.nextInt(5) == 1) {
            this.starRad = 24 + random.nextInt(12);
            this.ringRad = this.getExpandBoundRad();
            this.centerY = 77 + random.nextInt(50);
         } else {
            this.starRad = 24 + random.nextInt(30);
            this.ringRad = 0;
            this.centerY = 57 + random.nextInt(70);
         }
      } else {
         this.starRad = 10 + random.nextInt(12);
         this.ringRad = 0;
         this.centerY = 100 + random.nextInt(80);
      }

      int bound = this.starRad + 2 + this.getExpandBoundRad();
      this.boundingBox = new StructureBoundingBox(x - bound, this.centerY - bound, z - bound, x + bound, this.centerY + bound, z + bound);
   }

   private int getExpandBoundRad() {
      return (int)(this.starRad * 1.2F);
   }

   private int centerX() {
      return this.starRad + 2 + this.getExpandBoundRad() + this.boundingBox.minX;
   }

   private int centerZ() {
      return this.starRad + 2 + this.getExpandBoundRad() + this.boundingBox.minZ;
   }

   private static IBlockState air() {
      return Blocks.AIR.getDefaultState();
   }

   private static IBlockState marker(int dir) {
      return SMModContainer.AirExBlock.getDefaultState().withProperty(SMDir.DIR, dir);
   }

   private static int outwardDir(double xFar, boolean xComp, double yFar, boolean yComp, double zFar, boolean zComp) {
      if (xFar > zFar && xFar > yFar) {
         return xComp ? 3 : 2;
      } else if (zFar >= xFar && zFar > yFar) {
         return zComp ? 5 : 4;
      } else {
         return yComp ? 1 : 0;
      }
   }


   @Override
   public boolean addComponentParts(World world, Random random, StructureBoundingBox chunkBoundingBox) {
      this.rand.setSeed(this.starSeed + this.boundingBox.minX + this.boundingBox.minZ);
      int cx = this.centerX();
      int cy = this.centerY;
      int cz = this.centerZ();
      StarAccess acc = new StarAccess.WorldAccess(world, chunkBoundingBox);
      if (!(world.provider instanceof IZeroGravityWorldProvider)) {
         IBlockState surface = BEDROCK_BLOCKS[this.rand.nextInt(BEDROCK_BLOCKS.length)].getDefaultState();
         int rad = this.starRad - 1;
         double radPow = rad * rad;
         double radSub3Pow = (rad - 3) * (rad - 3);
         double radSub4Pow = (rad - 4) * (rad - 4);
         for (int x = chunkBoundingBox.minX; x <= chunkBoundingBox.maxX; x++) {
            double xFar = x - cx;
            xFar *= xFar;
            if (xFar >= radPow) continue;
            for (int y = cy - rad; y <= cy + rad; y++) {
               if (y < 0 || y > 255) continue;
               double yFar = y - cy;
               yFar *= yFar;
               double xyFar = xFar + yFar;
               if (xyFar >= radPow) continue;
               for (int z = chunkBoundingBox.minZ; z <= chunkBoundingBox.maxZ; z++) {
                  double zFar = z - cz;
                  zFar *= zFar;
                  double distancePow = xyFar + zFar;
                  if (distancePow < radPow) {
                     if (distancePow > radSub3Pow) {
                        acc.set(x, y, z, surface);
                     } else if (distancePow > radSub4Pow) {
                        acc.set(x, y, z, this.oreOrBody(surface));
                     } else if (distancePow < 6.25) {
                        acc.set(x, y, z, SMModContainer.InnerCoreBlock.getDefaultState());
                     } else if (distancePow < 16.0) {
                        acc.set(x, y, z, SMModContainer.OuterCoreBlock.getDefaultState());
                     }
                  }
               }
            }
         }
      }

      if (chunkBoundingBox.isVecInside(new BlockPos(cx, cy, cz))) {
         BlockPos corePos = new BlockPos(cx, cy, cz);
         world.setBlockState(corePos, SMModContainer.GravityCoreBlock.getDefaultState(), 2);
         TileEntity te = world.getTileEntity(corePos);
         if (te instanceof TileEntityGravityGenerator) {
            TileEntityGravityGenerator g = (TileEntityGravityGenerator)te;
            g.starRad = this.starRad;
            g.gravityRange = this.starRad + 8;
            if (this.rand.nextInt(4) == 0) {
               g.setDirtyLifeSoup(4 + this.rand.nextInt(12));
            }

            g.resetWorkState();
            g.markDirty();
         }
      }

      return true;
   }

   private IBlockState oreOrBody(IBlockState body) {
      int randInt = this.rand.nextInt(700);
      if (randInt < 1) {
         return ORE_BLOCKS_S_RARE[this.rand.nextInt(ORE_BLOCKS_S_RARE.length)].getDefaultState();
      } else if (randInt < 8) {
         return ORE_BLOCKS_RARE[this.rand.nextInt(ORE_BLOCKS_RARE.length)].getDefaultState();
      } else if (randInt < 80) {
         return ORE_BLOCKS[this.rand.nextInt(ORE_BLOCKS.length)].getDefaultState();
      } else {
         return body;
      }
   }

   private IBlockState plantState(StarBiomeType biome) {
      BlockStateSet set = BlockStateSet.GRASS_SETS[biome.grassSet];
      return set.pick(this.rand);
   }

   public boolean generateIntoChunk(StarAccess acc) {
      StructureBoundingBox bb = acc.window;
      this.rand.setSeed(this.starSeed + this.boundingBox.minX + this.boundingBox.minZ);
      boolean makeDimples = this.rand.nextBoolean();
      StarBiomeType biome = STAR_BIOMES[this.rand.nextInt(STAR_BIOMES.length)];
      IBlockState surfaceB = BEDROCK_BLOCKS[this.rand.nextInt(BEDROCK_BLOCKS.length)].getDefaultState();
      IBlockState surfaceA = biome.surface.getDefaultState();
      int cx = this.centerX();
      int cy = this.centerY;
      int cz = this.centerZ();
      int rad = this.starRad;
      int yMin = cy - (rad + 2);
      int yMax = cy + rad + 2;
      double radPow = rad * rad;
      double radPls1Pow = (rad + 1) * (rad + 1);
      double radPls3Pow = (rad + 2) * (rad + 2);
      double radSub1Pow = (rad - 1) * (rad - 1);
      double radSub3Pow = (rad - 3) * (rad - 3);
      double radSub4Pow = (rad - 4) * (rad - 4);
      boolean plantGrass = biome.grassRate > 0;
      for (int x = bb.minX; x <= bb.maxX; x++) {
         double xFar = x - cx;
         boolean xComp = xFar < 0.0;
         xFar *= xFar;
         if (xFar > radPls3Pow) continue;
         for (int y = yMin; y <= yMax; y++) {
            if (y < 0 || y > 255) continue;
            double yFar = y - cy;
            boolean yComp = yFar < 0.0;
            yFar *= yFar;
            double xyFar = xFar + yFar;
            if (xyFar > radPls3Pow) continue;
            for (int z = bb.minZ; z <= bb.maxZ; z++) {
               double zFar = z - cz;
               boolean zComp = zFar < 0.0;
               zFar *= zFar;
               double distancePow = xyFar + zFar;
               if (distancePow < radPow) {
                  acc.set(x, y, z, air());
                  if (distancePow > radSub3Pow) {
                     acc.set(x, y, z, distancePow >= radSub1Pow ? surfaceA : surfaceB);
                  } else if (distancePow > radSub4Pow) {
                     acc.set(x, y, z, this.oreOrBody(surfaceB));
                  } else if (distancePow < 6.25) {
                     acc.set(x, y, z, SMModContainer.InnerCoreBlock.getDefaultState());
                  } else if (distancePow < 16.0) {
                     acc.set(x, y, z, SMModContainer.OuterCoreBlock.getDefaultState());
                  }
               } else if (distancePow < radPls1Pow) {
                  if (plantGrass
                     && this.rand.nextInt(100) <= biome.grassRate
                     && (!(Math.abs(xFar - zFar) <= radPow * 0.1) || !(yFar <= radPow * 0.5))
                     && (!(Math.abs(xFar - yFar) <= radPow * 0.1) || !(zFar <= radPow * 0.5))
                     && (!(Math.abs(yFar - zFar) <= radPow * 0.1) || !(xFar <= radPow * 0.5))) {
                     if (xFar > zFar && xFar > yFar) {
                        double fixedFar = xComp ? x + 1 - cx : x - 1 - cx;
                        distancePow = fixedFar * fixedFar + yFar + zFar;
                     } else if (zFar >= xFar && zFar > yFar) {
                        double fixedFar = zComp ? z + 1 - cz : z - 1 - cz;
                        distancePow = xFar + yFar + fixedFar * fixedFar;
                     } else {
                        double fixedFar = yComp ? y + 1 - cy : y - 1 - cy;
                        distancePow = xFar + fixedFar * fixedFar + zFar;
                     }

                     if (!(distancePow >= radPow) || !(distancePow < radPls1Pow)) {
                        acc.set(x, y, z, this.plantState(biome));
                     }
                  }
               } else if (distancePow <= radPls3Pow) {
                  if (acc.isAir(x, y, z)) {
                     acc.set(x, y, z, marker(outwardDir(xFar, xComp, yFar, yComp, zFar, zComp)));
                  }
               }
            }
         }
      }

      if (this.ringRad != 0) {
         this.makeRing(acc);
      }

      if (biome.treeRate > 0) {
         this.makeTrees(acc, biome);
      } else if (makeDimples) {
         this.makeDimples(acc, surfaceA);
      }

      return true;
   }

   private void makeRing(StarAccess acc) {
      this.rand.setSeed(this.starSeed);
      int ringRoatX = this.rand.nextInt(90);
      int ringRoatZ = this.rand.nextInt(90);
      int rad = this.starRad + this.getExpandBoundRad();
      int wa = (int)(this.starRad * 0.7F);
      int cx = this.centerX();
      int cy = this.centerY;
      int cz = this.centerZ();
      int prevPosX = 0;
      int prevPosY = 0;
      int prevPosZ = 0;
      int color = 0;
      int colorCnt = 0;
      for (int distance = rad - wa; distance <= rad; colorCnt++) {
         if ((colorCnt & 1) == 0) {
            color = this.rand.nextInt(16);
         }

         IBlockState glass = Blocks.STAINED_GLASS.getStateFromMeta(color);
         for (int angle = 0; angle < 360; angle++) {
            double dXYZ0 = Math.cos(angle * Math.PI / 180.0) * distance;
            double dXYZ1 = 0.0;
            double dXYZ2 = Math.sin(angle * Math.PI / 180.0) * distance;
            float f1 = MathHelper.cos(ringRoatX);
            float f2 = MathHelper.sin(ringRoatX);
            double d1 = dXYZ1 * f1 + dXYZ2 * f2;
            double d2 = dXYZ2 * f1 - dXYZ1 * f2;
            f1 = MathHelper.cos(ringRoatZ);
            f2 = MathHelper.sin(ringRoatZ);
            double d0 = dXYZ0 * f1 + d1 * f2;
            d1 = d1 * f1 - dXYZ0 * f2;
            int posX = cx + (int)d0;
            int posY = cy + (int)d1;
            int posZ = cz + (int)d2;
            if ((prevPosX != posX || prevPosY != posY || prevPosZ != posZ) && acc.inside(posX, posY, posZ)) {
               acc.set(posX, posY, posZ, glass);
            }

            prevPosX = posX;
            prevPosY = posY;
            prevPosZ = posZ;
         }

         distance++;
      }
   }

   private void makeTrees(StarAccess acc, StarBiomeType biome) {
      this.rand.setSeed(this.starSeed);
      int[] treeSets = TREESET_LST[this.rand.nextInt(TREESET_LST.length)];
      int cx = this.centerX();
      int cy = this.centerY;
      int cz = this.centerZ();
      int workDistance = this.starRad;
      int genCount = (int)((Math.PI * 4) * this.starRad * this.starRad * (biome.treeRate / 1000.0)) / 2;
      genCount += this.rand.nextInt(genCount);
      for (int i = 0; i < genCount; i++) {
         int angle = this.rand.nextInt(360);
         int ringRoatX = this.rand.nextInt(90);
         int ringRoatZ = this.rand.nextInt(90);
         int species = treeSets[this.rand.nextInt(treeSets.length)];
         double dXYZ0 = Math.cos(angle * Math.PI / 180.0) * workDistance;
         double dXYZ1 = 0.0;
         double dXYZ2 = Math.sin(angle * Math.PI / 180.0) * workDistance;
         float f1 = MathHelper.cos(ringRoatX);
         float f2 = MathHelper.sin(ringRoatX);
         double d1 = dXYZ1 * f1 + dXYZ2 * f2;
         double d2 = dXYZ2 * f1 - dXYZ1 * f2;
         f1 = MathHelper.cos(ringRoatZ);
         f2 = MathHelper.sin(ringRoatZ);
         double d0 = dXYZ0 * f1 + d1 * f2;
         d1 = d1 * f1 - dXYZ0 * f2;
         int posX = cx + (int)d0;
         int posY = cy + (int)d1;
         int posZ = cz + (int)d2;
         double xFar = posX - cx;
         boolean xComp = xFar < 0.0;
         xFar *= xFar;
         double yFar = posY - cy;
         boolean yComp = yFar < 0.0;
         yFar *= yFar;
         double zFar = posZ - cz;
         boolean zComp = zFar < 0.0;
         zFar *= zFar;
         int dir = outwardDir(xFar, xComp, yFar, yComp, zFar, zComp);
         int minTreeHeight = 4 + this.rand.nextInt(6);
         StarTrees.generate(acc, this.rand, species, minTreeHeight, dir, posX, posY, posZ);
      }
   }

   private void makeDimples(StarAccess acc, IBlockState surface) {
      this.rand.setSeed(this.starSeed);
      int cx = this.centerX();
      int cy = this.centerY;
      int cz = this.centerZ();
      int workDistance = this.starRad + 4;
      double starRadPow = this.starRad * this.starRad;
      double starRadPls2Pow = (this.starRad + 2) * (this.starRad + 2);
      int r = this.rand.nextInt(360);
      int ringRoatX = this.rand.nextInt(90);
      int ringRoatZ = this.rand.nextInt(90);
      int dimpleRad = this.starRad / 4 + this.rand.nextInt(this.starRad / 4);
      double dimpleRadPow = dimpleRad * dimpleRad;
      double dimpleRadSub1Pow = (dimpleRad - 2) * (dimpleRad - 2);
      double dXYZ0 = Math.cos(r * Math.PI / 180.0) * workDistance;
      double dXYZ1 = 0.0;
      double dXYZ2 = Math.sin(r * Math.PI / 180.0) * workDistance;
      float f1 = MathHelper.cos(ringRoatX);
      float f2 = MathHelper.sin(ringRoatX);
      double d1 = dXYZ1 * f1 + dXYZ2 * f2;
      double d2 = dXYZ2 * f1 - dXYZ1 * f2;
      f1 = MathHelper.cos(ringRoatZ);
      f2 = MathHelper.sin(ringRoatZ);
      double d0 = dXYZ0 * f1 + d1 * f2;
      d1 = d1 * f1 - dXYZ0 * f2;
      int dimpleCPosX = cx + (int)d0;
      int dimpleCPosY = cy + (int)d1;
      int dimpleCPosZ = cz + (int)d2;
      for (int x = dimpleCPosX - dimpleRad; x <= dimpleCPosX + dimpleRad; x++) {
         double xFarFromDimpleC = x - dimpleCPosX;
         xFarFromDimpleC *= xFarFromDimpleC;
         double xFarFromStarC = x - cx;
         xFarFromStarC *= xFarFromStarC;
         if (xFarFromDimpleC > dimpleRadPow || xFarFromStarC > starRadPls2Pow) continue;
         for (int y = dimpleCPosY - dimpleRad; y <= dimpleCPosY + dimpleRad; y++) {
            if (y < 0 || y > 255) continue;
            double yFarFromDimpleC = y - dimpleCPosY;
            yFarFromDimpleC *= yFarFromDimpleC;
            double xyFarFromDimpleC = xFarFromDimpleC + yFarFromDimpleC;
            double yFarFromStarC = y - cy;
            yFarFromStarC *= yFarFromStarC;
            double xyFarFromStarC = xFarFromStarC + yFarFromStarC;
            if (xyFarFromDimpleC > dimpleRadPow || xyFarFromStarC > starRadPls2Pow) continue;
            for (int z = dimpleCPosZ - dimpleRad; z <= dimpleCPosZ + dimpleRad; z++) {
               double zFarFromDimpleC = z - dimpleCPosZ;
               zFarFromDimpleC *= zFarFromDimpleC;
               double xyzFarFromDimpleC = zFarFromDimpleC + xyFarFromDimpleC;
               double zFarFromStarC = z - cz;
               zFarFromStarC *= zFarFromStarC;
               double xyzFarFromStarC = zFarFromStarC + xyFarFromStarC;
               if (xyzFarFromDimpleC > dimpleRadPow || xyzFarFromStarC > starRadPls2Pow) continue;
               if (xyzFarFromDimpleC >= dimpleRadSub1Pow) {
                  if (!(xyzFarFromStarC > starRadPow)) {
                     acc.set(x, y, z, surface);
                  }
               } else {
                  acc.set(x, y, z, air());
               }
            }
         }
      }
   }

   @Override
   protected void writeStructureToNBT(NBTTagCompound tag) {
      tag.setInteger("starRad", this.starRad);
      tag.setInteger("ringRad", this.ringRad);
      tag.setInteger("centerY", this.centerY);
      tag.setInteger("starSeed", this.starSeed);
   }

   @Override
   protected void readStructureFromNBT(NBTTagCompound tag, TemplateManager templateManager) {
      this.starRad = tag.getInteger("starRad");
      this.ringRad = tag.getInteger("ringRad");
      this.centerY = tag.getInteger("centerY");
      this.starSeed = tag.getInteger("starSeed");
   }

   private static final class BlockStateSet {
      static final IBlockState GRASS = SMModContainer.TallGrassGravitizedBlock.getDefaultState().withProperty(BlockTallGrassGravitized.TYPE, 1);
      static final IBlockState FERN = SMModContainer.TallGrassGravitizedBlock.getDefaultState().withProperty(BlockTallGrassGravitized.TYPE, 2);
      static final IBlockState DANDELION = SMModContainer.PlantYelGravitizedBlock.getDefaultState();
      static final BlockStateSet[] GRASS_SETS = makeSets();
      final IBlockState[] states;

      BlockStateSet(IBlockState... states) {
         this.states = states;
      }

      IBlockState pick(Random r) {
         return this.states[r.nextInt(this.states.length)];
      }

      private static IBlockState flower(int type) {
         return SMModContainer.PlantRedGravitizedBlock.getDefaultState().withProperty(BlockFlowerGravitized.TYPE, type);
      }

      private static BlockStateSet[] makeSets() {
         return new BlockStateSet[]{
            new BlockStateSet(GRASS, GRASS, GRASS, GRASS, GRASS, DANDELION, flower(0)),
            new BlockStateSet(
               GRASS, GRASS, GRASS, GRASS, GRASS, DANDELION, flower(0), flower(1), flower(2), flower(3), flower(4), flower(5), flower(6), flower(7), flower(8)
            ),
            new BlockStateSet(FERN),
            new BlockStateSet(GRASS)
         };
      }
   }

   private enum StarBiomeType {
      grassStar(SMModContainer.DirtGrassExBlock, 5, 50, 0, 0),
      grassStarRich(SMModContainer.DirtGrassExBlock, 2, 40, 1, 0),
      forestStar(SMModContainer.DirtGrassExBlock, 3, 15, 0, 20),
      stoneStar(Blocks.STONE, 1, 10, 2, 0),
      mossyStar(Blocks.MOSSY_COBBLESTONE, 1, 10, 2, 4),
      snowStar(Blocks.SNOW, 1, 20, 3, 4),
      iceStar(Blocks.PACKED_ICE, 1, 0, 0, 0),
      spongeStar(Blocks.SPONGE, 1, 0, 0, 0),
      clayStar(Blocks.CLAY, 1, 10, 3, 10),
      netherStar(Blocks.NETHERRACK, 1, 0, 0, 0),
      sandStoneStar(Blocks.SANDSTONE, 1, 0, 0, 0);

      final Block surface;
      final int frequency;
      final int grassRate;
      final int grassSet;
      final int treeRate;

      StarBiomeType(Block surface, int frequency, int grassRate, int grassSet, int treeRate) {
         this.surface = surface;
         this.frequency = frequency;
         this.grassRate = grassRate;
         this.grassSet = grassSet;
         this.treeRate = treeRate;
      }
   }

   public static void init() {
      List<StarBiomeType> list = new ArrayList<>();
      for (StarBiomeType t : StarBiomeType.values()) {
         for (int i = 0; i < t.frequency; i++) {
            list.add(t);
         }
      }

      STAR_BIOMES = list.toArray(new StarBiomeType[0]);
   }
}
