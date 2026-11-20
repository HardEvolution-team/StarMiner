package com.electrifiedded.starminer.basics.client;

import com.electrifiedded.starminer.basics.entity.EntityComet;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.entity.Render;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class RenderComet extends Render<EntityComet> {
   private static final ResourceLocation RES_COMET = new ResourceLocation("starminer:textures/entity/comet.png");
   private static final ResourceLocation RES_SOUP = new ResourceLocation("starminer:textures/items/lifesoup_a.png");

   public RenderComet(RenderManager manager) {
      super(manager);
   }

   @Override
   public void doRender(EntityComet comet, double x, double y, double z, float entityYaw, float partialTicks) {
      BillboardHelper.compute(partialTicks);
      float fX = (float)x;
      float fY = (float)y + 0.5F;
      float fZ = (float)z;
      OpenGlHelper.setLightmapTextureCoords(OpenGlHelper.lightmapTexUnit, 240.0F, 240.0F);
      GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
      GlStateManager.disableLighting();
      GlStateManager.enableBlend();
      GlStateManager.tryBlendFuncSeparate(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA,
         GlStateManager.SourceFactor.ONE, GlStateManager.DestFactor.ZERO);
      this.bindTexture(RES_SOUP);
      this.quad(fX + fX / 1000.0F, fY + fY / 1000.0F, fZ + fZ / 1000.0F, 1.0F, 0.3F, 0.3F, 0.3F, 0.8F);
      this.bindTexture(RES_COMET);
      float scale = comet.getScaleNow() * 3.0F;
      if (comet.isRedOne()) {
         this.quad(fX, fY, fZ, scale, 1.0F, 0.2F, 0.2F, 0.8F);
      } else {
         this.quad(fX, fY, fZ, scale, 0.6F, 0.8F, 1.0F, 0.8F);
      }

      GlStateManager.disableBlend();
      GlStateManager.enableLighting();
      RenderHelper.enableStandardItemLighting();
   }

   private void quad(float fX, float fY, float fZ, float scale, float r, float g, float b, float a) {
      float rotationX = BillboardHelper.rotationX;
      float rotationZ = BillboardHelper.rotationZ;
      float rotationYZ = BillboardHelper.rotationYZ;
      float rotationXY = BillboardHelper.rotationXY;
      float rotationXZ = BillboardHelper.rotationXZ;
      Tessellator tes = Tessellator.getInstance();
      BufferBuilder buf = tes.getBuffer();
      buf.begin(7, DefaultVertexFormats.POSITION_TEX_COLOR);
      buf.pos(fX - rotationX * scale - rotationYZ * scale, fY - rotationXZ * scale, fZ - rotationZ * scale - rotationXY * scale).tex(1.0, 1.0).color(r, g, b, a).endVertex();
      buf.pos(fX - rotationX * scale + rotationYZ * scale, fY + rotationXZ * scale, fZ - rotationZ * scale + rotationXY * scale).tex(1.0, 0.0).color(r, g, b, a).endVertex();
      buf.pos(fX + rotationX * scale + rotationYZ * scale, fY + rotationXZ * scale, fZ + rotationZ * scale + rotationXY * scale).tex(0.0, 0.0).color(r, g, b, a).endVertex();
      buf.pos(fX + rotationX * scale - rotationYZ * scale, fY - rotationXZ * scale, fZ + rotationZ * scale - rotationXY * scale).tex(0.0, 1.0).color(r, g, b, a).endVertex();
      tes.draw();
   }

   @Override
   protected ResourceLocation getEntityTexture(EntityComet entity) {
      return RES_COMET;
   }
}
