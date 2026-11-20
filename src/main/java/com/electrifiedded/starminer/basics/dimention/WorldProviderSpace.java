package com.electrifiedded.starminer.basics.dimention;

import com.electrifiedded.starminer.api.IZeroGravityWorldProvider;
import com.electrifiedded.starminer.basics.SMModContainer;
import net.minecraft.init.Biomes;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.DimensionType;
import net.minecraft.world.WorldProvider;
import net.minecraft.world.biome.BiomeProviderSingle;
import net.minecraft.world.gen.IChunkGenerator;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class WorldProviderSpace extends WorldProvider implements IZeroGravityWorldProvider {
   public static DimensionType SPACE_TYPE;
   public static int cloudTickCounter = 0;

   @Override
   protected void init() {
      this.hasSkyLight = true;
      this.biomeProvider = new BiomeProviderSingle(Biomes.SKY);
   }

   @Override
   public DimensionType getDimensionType() {
      return SPACE_TYPE;
   }

   @Override
   public IChunkGenerator createChunkGenerator() {
      return new ChunkGeneratorSpace(this.world, this.world.getSeed());
   }

   @Override
   public boolean canRespawnHere() {
      return true;
   }

   @Override
   public boolean isSurfaceWorld() {
      return true;
   }

   @Override
   public double getHorizon() {
      return 100.0;
   }

   @Override
   public double getVoidFogYFactor() {
      return 1.0;
   }

   @Override
   public int getAverageGroundLevel() {
      return 64;
   }

   @SideOnly(Side.CLIENT)
   @Override
   public boolean doesXZShowFog(int x, int z) {
      return false;
   }

   @SideOnly(Side.CLIENT)
   @Override
   public float getCloudHeight() {
      return -1000.0F;
   }

   @SideOnly(Side.CLIENT)
   @Override
   public Vec3d getSkyColor(net.minecraft.entity.Entity cameraEntity, float partialTicks) {
      return new Vec3d(0.0, 0.0, 0.02);
   }

   @SideOnly(Side.CLIENT)
   @Override
   public float getStarBrightness(float par1) {
      return 1.0F;
   }

   @SideOnly(Side.CLIENT)
   @Override
   public Vec3d getFogColor(float celestialAngle, float partialTicks) {
      float f2 = MathHelper.clamp(MathHelper.cos(celestialAngle * (float)Math.PI * 2.0F) * 2.0F + 0.5F, 0.0F, 1.0F);
      return new Vec3d(0.02 * f2, 0.02 * f2, 0.05 * f2);
   }

   @SideOnly(Side.CLIENT)
   @Override
   public Vec3d getCloudColor(float partialTicks) {
      float f1 = this.world.getCelestialAngle(partialTicks);
      float f2 = MathHelper.clamp(MathHelper.cos(f1 * (float)Math.PI * 2.0F) * 2.0F + 0.5F, 0.0F, 1.0F);
      float f3 = f2 * 0.9F + 0.1F;
      float f5 = f2 * 0.85F + 0.15F;
      return new Vec3d(f3, f3, f5);
   }

   @Override
   public boolean canDoRainSnowIce(net.minecraft.world.chunk.Chunk chunk) {
      return false;
   }

   @Override
   public void updateWeather() {
      this.world.getWorldInfo().setRaining(false);
      this.world.getWorldInfo().setThundering(false);
      this.world.rainingStrength = 0.0F;
      this.world.thunderingStrength = 0.0F;
      if (this.world.isRemote) {
         cloudTickCounter++;
      }
   }

   @Override
   public long getWorldTime() {
      net.minecraft.world.World overworld = net.minecraftforge.common.DimensionManager.getWorld(0);
      return overworld != null ? overworld.getWorldTime() : super.getWorldTime();
   }

   public static int dimensionId() {
      return SMModContainer.GeostationaryOrbitDimentionId;
   }
}
