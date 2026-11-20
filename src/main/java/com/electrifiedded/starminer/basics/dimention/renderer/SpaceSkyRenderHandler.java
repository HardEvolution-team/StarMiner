package com.electrifiedded.starminer.basics.dimention.renderer;

import java.util.Random;
import com.electrifiedded.starminer.basics.dimention.MapFromSky;
import com.electrifiedded.starminer.basics.dimention.WorldProviderSpace;
import net.minecraft.block.material.MapColor;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.WorldClient;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.GLAllocation;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.texture.DynamicTexture;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.entity.Entity;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraftforge.client.IRenderHandler;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.apache.logging.log4j.LogManager;
import org.lwjgl.opengl.GL11;

@SideOnly(Side.CLIENT)
public class SpaceSkyRenderHandler extends IRenderHandler {
   private static final ResourceLocation SUN = new ResourceLocation("textures/environment/sun.png");
   private static final ResourceLocation MOON_PHASES = new ResourceLocation("textures/environment/moon_phases.png");
   private static final ResourceLocation CLOUDS = new ResourceLocation("textures/environment/clouds.png");
   private DynamicTexture skyMapTexture;
   private ResourceLocation skyMap;
   private int[] intArray;
   private int starList = -1;
   private int skyList = -1;
   private int skyList2 = -1;
   private int renderGroundCallList = -1;

   private void ensureLists() {
      if (this.starList != -1) {
         return;
      }

      Tessellator tes = Tessellator.getInstance();
      BufferBuilder buf = tes.getBuffer();
      this.starList = GLAllocation.generateDisplayLists(3);
      this.skyList = this.starList + 1;
      this.skyList2 = this.starList + 2;
      GL11.glNewList(this.starList, GL11.GL_COMPILE);
      this.renderStars(buf);
      tes.draw();
      GL11.glEndList();
      GL11.glNewList(this.skyList, GL11.GL_COMPILE);
      this.renderDome(buf, 16.0F, false);
      tes.draw();
      GL11.glEndList();
      GL11.glNewList(this.skyList2, GL11.GL_COMPILE);
      this.renderDome(buf, -16.0F, true);
      tes.draw();
      GL11.glEndList();
   }

   @Override
   public void render(float partialTicks, WorldClient world, Minecraft mc) {
      if (this.skyMap == null) {
         this.skyMapTexture = new DynamicTexture(128, 128);
         this.skyMap = mc.getTextureManager().getDynamicTextureLocation("starminer_skymap", this.skyMapTexture);
         this.intArray = this.skyMapTexture.getTextureData();
      }

      this.ensureLists();
      Tessellator tes = Tessellator.getInstance();
      BufferBuilder buf = tes.getBuffer();
      GlStateManager.disableTexture2D();
      float f1 = 0.0F;
      float f2 = 0.0F;
      float f3 = 0.0F;
      GlStateManager.depthMask(false);
      GlStateManager.enableFog();
      GlStateManager.color(f1, f2, f3);
      GlStateManager.pushMatrix();
      GlStateManager.translate(0.0F, -10.0F, 0.0F);
      GlStateManager.callList(this.skyList);
      GlStateManager.popMatrix();
      GlStateManager.disableFog();
      GlStateManager.disableAlpha();
      GlStateManager.enableBlend();
      GlStateManager.tryBlendFuncSeparate(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA,
         GlStateManager.SourceFactor.ONE, GlStateManager.DestFactor.ZERO);
      RenderHelper.disableStandardItemLighting();
      float[] sunrise = world.provider.calcSunriseSunsetColors(world.getCelestialAngle(partialTicks), partialTicks);
      if (sunrise != null) {
         GlStateManager.disableTexture2D();
         GlStateManager.shadeModel(7425);
         GlStateManager.pushMatrix();
         GlStateManager.rotate(90.0F, 1.0F, 0.0F, 0.0F);
         GlStateManager.rotate(MathHelper.sin(world.getCelestialAngleRadians(partialTicks)) < 0.0F ? 180.0F : 0.0F, 0.0F, 0.0F, 1.0F);
         GlStateManager.rotate(90.0F, 0.0F, 0.0F, 1.0F);
         float r = sunrise[0];
         float g = sunrise[1];
         float b = sunrise[2];
         buf.begin(6, DefaultVertexFormats.POSITION_COLOR);
         buf.pos(0.0, 100.0, 0.0).color(r, g, b, sunrise[3]).endVertex();
         for (int j = 0; j <= 16; j++) {
            float f11 = j * (float)Math.PI * 2.0F / 16.0F;
            float f12 = MathHelper.sin(f11);
            float f13 = MathHelper.cos(f11);
            buf.pos(f12 * 120.0F, f13 * 120.0F, -f13 * 40.0F * sunrise[3]).color(sunrise[0], sunrise[1], sunrise[2], 0.0F).endVertex();
         }

         tes.draw();
         GlStateManager.popMatrix();
         GlStateManager.shadeModel(7424);
      }

      GlStateManager.enableTexture2D();
      GlStateManager.tryBlendFuncSeparate(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE,
         GlStateManager.SourceFactor.ONE, GlStateManager.DestFactor.ZERO);
      GlStateManager.pushMatrix();
      GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
      GlStateManager.rotate(-90.0F, 0.0F, 1.0F, 0.0F);
      GlStateManager.rotate(world.getCelestialAngle(partialTicks) * 360.0F, 1.0F, 0.0F, 0.0F);
      float size = 30.0F;
      mc.getTextureManager().bindTexture(SUN);
      buf.begin(7, DefaultVertexFormats.POSITION_TEX);
      buf.pos(-size, 100.0, -size).tex(0.0, 0.0).endVertex();
      buf.pos(size, 100.0, -size).tex(1.0, 0.0).endVertex();
      buf.pos(size, 100.0, size).tex(1.0, 1.0).endVertex();
      buf.pos(-size, 100.0, size).tex(0.0, 1.0).endVertex();
      tes.draw();
      size = 20.0F;
      mc.getTextureManager().bindTexture(MOON_PHASES);
      int phase = world.getMoonPhase();
      int l = phase % 4;
      int i1 = phase / 4 % 2;
      float u0 = (l + 0) / 4.0F;
      float v0 = (i1 + 0) / 2.0F;
      float u1 = (l + 1) / 4.0F;
      float v1 = (i1 + 1) / 2.0F;
      buf.begin(7, DefaultVertexFormats.POSITION_TEX);
      buf.pos(-size, -100.0, size).tex(u1, v1).endVertex();
      buf.pos(size, -100.0, size).tex(u0, v1).endVertex();
      buf.pos(size, -100.0, -size).tex(u0, v0).endVertex();
      buf.pos(-size, -100.0, -size).tex(u1, v0).endVertex();
      tes.draw();
      GlStateManager.disableTexture2D();
      GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
      GlStateManager.callList(this.starList);
      GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
      GlStateManager.disableBlend();
      GlStateManager.enableAlpha();
      GlStateManager.enableFog();
      GlStateManager.popMatrix();
      GlStateManager.disableTexture2D();
      float ca = world.getCelestialAngle(partialTicks);
      float ca2 = MathHelper.clamp(MathHelper.cos(ca * (float)Math.PI * 2.0F) * 2.0F + 0.5F, 0.0F, 1.0F);
      GlStateManager.color(0.3F * ca2, 0.3F * ca2, 1.0F * ca2);
      GlStateManager.pushMatrix();
      GlStateManager.translate(0.0F, 8.0F, 0.0F);
      GlStateManager.callList(this.skyList2);
      GlStateManager.popMatrix();
      GlStateManager.enableTexture2D();
      GlStateManager.pushMatrix();
      this.renderGround(partialTicks, world, mc);
      GlStateManager.popMatrix();
      GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
      GlStateManager.disableBlend();
      GlStateManager.depthMask(true);
   }

   private void uploadSkyMap() {
      byte[] colors = MapFromSky.skyMapclientData;
      for (int i = 0; i < 16384; i++) {
         byte b0 = colors[i];
         if (b0 / 4 == 0) {
            this.intArray[i] = (i + i / 128 & 1) * 8 + 16 << 24;
         } else {
            int j = MapColor.COLORS[b0 / 4].colorValue;
            int k = b0 & 3;
            int shade = 220;
            if (k == 2) {
               shade = 255;
            }

            if (k == 0) {
               shade = 180;
            }

            int r = (j >> 16 & 0xFF) * shade / 255;
            int g = (j >> 8 & 0xFF) * shade / 255;
            int b = (j & 0xFF) * shade / 255;
            this.intArray[i] = 0xFF000000 | r << 16 | g << 8 | b;
         }
      }

      this.skyMapTexture.updateDynamicTexture();
   }

   private void renderGround(float partialTicks, WorldClient world, Minecraft mc) {
      Entity view = mc.getRenderViewEntity();
      if (view == null) {
         return;
      }

      GlStateManager.disableCull();
      final int cell = 32;
      int range = 256 / cell;
      Tessellator tes = Tessellator.getInstance();
      BufferBuilder buf = tes.getBuffer();
      GlStateManager.enableBlend();
      GlStateManager.tryBlendFuncSeparate(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA,
         GlStateManager.SourceFactor.ONE, GlStateManager.DestFactor.ZERO);
      Vec3d cloudColor = world.getCloudColour(partialTicks);
      float cr = (float)cloudColor.x;
      float cg = (float)cloudColor.y;
      float cb = (float)cloudColor.z;
      GlStateManager.depthMask(false);
      float f5 = 0.001953125F;
      double d0 = WorldProviderSpace.cloudTickCounter + partialTicks;
      float f8 = -22.0F;
      GlStateManager.color(cr, cg, cb, 0.8F);
      if (MapFromSky.hasSkyMapImageData && MapFromSky.skyMapclientData != null) {
         if (this.renderGroundCallList == -1 || MapFromSky.doRecompileSkyMapList) {
            LogManager.getLogger("starminer").info("Compiling gllist for ground texture..");
            this.uploadSkyMap();
            if (this.renderGroundCallList != -1 && GL11.glIsList(this.renderGroundCallList)) {
               GLAllocation.deleteDisplayLists(this.renderGroundCallList);
            }

            MapFromSky.doRecompileSkyMapList = false;
            this.renderGroundCallList = GLAllocation.generateDisplayLists(1);
            GL11.glNewList(this.renderGroundCallList, GL11.GL_COMPILE);
            int ii = range * 8;
            buf.begin(7, DefaultVertexFormats.POSITION_TEX);
            double ff5 = f5 * 4.0F;
            for (int l = -cell * ii; l < cell * ii; l += cell) {
               for (int i1 = -cell * ii; i1 < cell * ii; i1 += cell) {
                  buf.pos(l + 0, f8, i1 + cell).tex((l + 0) * ff5 + 0.5, (i1 + cell) * ff5 + 0.5).endVertex();
                  buf.pos(l + cell, f8, i1 + cell).tex((l + cell) * ff5 + 0.5, (i1 + cell) * ff5 + 0.5).endVertex();
                  buf.pos(l + cell, f8, i1 + 0).tex((l + cell) * ff5 + 0.5, (i1 + 0) * ff5 + 0.5).endVertex();
                  buf.pos(l + 0, f8, i1 + 0).tex((l + 0) * ff5 + 0.5, (i1 + 0) * ff5 + 0.5).endVertex();
               }
            }

            tes.draw();
            GL11.glEndList();
         }

         mc.getTextureManager().bindTexture(this.skyMap);
         GlStateManager.callList(this.renderGroundCallList);
      }

      float f9 = (float)(d0 * 3.0E-5);
      float f10 = 0.0F;
      mc.getTextureManager().bindTexture(CLOUDS);
      buf.begin(7, DefaultVertexFormats.POSITION_TEX);
      for (int l = -cell * range; l < cell * range; l += cell) {
         for (int i1 = -cell * range; i1 < cell * range; i1 += cell) {
            buf.pos(l + 0, f8, i1 + cell).tex((l + 0) * f5 + f9, (i1 + cell) * f5 + f10).endVertex();
            buf.pos(l + cell, f8, i1 + cell).tex((l + cell) * f5 + f9, (i1 + cell) * f5 + f10).endVertex();
            buf.pos(l + cell, f8, i1 + 0).tex((l + cell) * f5 + f9, (i1 + 0) * f5 + f10).endVertex();
            buf.pos(l + 0, f8, i1 + 0).tex((l + 0) * f5 + f9, (i1 + 0) * f5 + f10).endVertex();
         }
      }

      tes.draw();
      GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
      GlStateManager.disableBlend();
      GlStateManager.enableCull();
      GlStateManager.depthMask(true);
   }

   private void renderDome(BufferBuilder buf, float posY, boolean reverseX) {
      buf.begin(7, DefaultVertexFormats.POSITION);
      for (int k = -384; k <= 384; k += 64) {
         for (int l = -384; l <= 384; l += 64) {
            float f = k;
            float f1 = k + 64;
            if (reverseX) {
               f1 = k;
               f = k + 64;
            }

            buf.pos(f, posY, l).endVertex();
            buf.pos(f1, posY, l).endVertex();
            buf.pos(f1, posY, l + 64).endVertex();
            buf.pos(f, posY, l + 64).endVertex();
         }
      }
   }

   private void renderStars(BufferBuilder buf) {
      Random random = new Random(10842L);
      buf.begin(7, DefaultVertexFormats.POSITION);
      for (int i = 0; i < 3000; i++) {
         double d0 = random.nextFloat() * 2.0F - 1.0F;
         double d1 = random.nextFloat() * 2.0F - 1.0F;
         double d2 = random.nextFloat() * 2.0F - 1.0F;
         double d3 = 0.15F + random.nextFloat() * 0.1F;
         double d4 = d0 * d0 + d1 * d1 + d2 * d2;
         if (d4 < 1.0 && d4 > 0.01) {
            d4 = 1.0 / Math.sqrt(d4);
            d0 *= d4;
            d1 *= d4;
            d2 *= d4;
            double d5 = d0 * 100.0;
            double d6 = d1 * 100.0;
            double d7 = d2 * 100.0;
            double d8 = Math.atan2(d0, d2);
            double d9 = Math.sin(d8);
            double d10 = Math.cos(d8);
            double d11 = Math.atan2(Math.sqrt(d0 * d0 + d2 * d2), d1);
            double d12 = Math.sin(d11);
            double d13 = Math.cos(d11);
            double d14 = random.nextDouble() * Math.PI * 2.0;
            double d15 = Math.sin(d14);
            double d16 = Math.cos(d14);
            for (int j = 0; j < 4; j++) {
               double d17 = 0.0;
               double d18 = ((j & 2) - 1) * d3;
               double d19 = ((j + 1 & 2) - 1) * d3;
               double d20 = d18 * d16 - d19 * d15;
               double d21 = d19 * d16 + d18 * d15;
               double d22 = d20 * d12 + d17 * d13;
               double d23 = d17 * d12 - d20 * d13;
               double d24 = d23 * d9 - d21 * d10;
               double d25 = d21 * d9 + d23 * d10;
               buf.pos(d5 + d24, d6 + d22, d7 + d25).endVertex();
            }
         }
      }
   }
}
