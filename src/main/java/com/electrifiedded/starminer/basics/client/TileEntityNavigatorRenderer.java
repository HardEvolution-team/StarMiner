package com.electrifiedded.starminer.basics.client;

import com.electrifiedded.starminer.basics.Config;
import com.electrifiedded.starminer.basics.tileentity.TileEntityNavigator;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class TileEntityNavigatorRenderer extends TileEntitySpecialRenderer<TileEntityNavigator> {
   @Override
   public void render(TileEntityNavigator te, double x, double y, double z, float partialTicks, int destroyStage, float alpha) {
      if (te.getWorld() == null) return;
      IBlockState state = te.getWorld().getBlockState(te.getPos());
      if ((state.getBlock().getMetaFromState(state) & 1) == 0) return;
      double lx = te.lookX;
      double ly = te.lookY;
      double lz = te.lookZ;
      if (lx == 0.0 && ly == 0.0 && lz == 0.0) return;

      float r = 0.6F;
      float g = 0.9F;
      float b = 0.6F;
      double len = Config.naviLaserLength;
      if (te.isActive()) {
         r = 1.0F;
         g = 1.0F;
         b = 0.0F;
         len = 100.0;
      }

      double cx = x + 0.5;
      double cy = y + 0.5;
      double cz = z + 0.5;
      double ex = cx + lx * len;
      double ey = cy + ly * len;
      double ez = cz + lz * len;
      double w = 0.05;

      GlStateManager.pushMatrix();
      GlStateManager.disableTexture2D();
      GlStateManager.disableLighting();
      GlStateManager.disableCull();
      OpenGlHelper.setLightmapTextureCoords(OpenGlHelper.lightmapTexUnit, 240.0F, 240.0F);
      Tessellator tes = Tessellator.getInstance();
      BufferBuilder buf = tes.getBuffer();
      buf.begin(7, DefaultVertexFormats.POSITION_COLOR);
      quad(buf, cx + w, cy, cz, ex + w, ey, ez, ex - w, ey, ez, cx - w, cy, cz, r, g, b);
      quad(buf, cx, cy, cz + w, ex, ey, ez + w, ex, ey, ez - w, cx, cy, cz - w, r, g, b);
      quad(buf, cx, cy + w, cz, ex, ey + w, ez, ex, ey - w, ez, cx, cy - w, cz, r, g, b);
      tes.draw();
      GlStateManager.enableCull();
      GlStateManager.enableLighting();
      GlStateManager.enableTexture2D();
      GlStateManager.popMatrix();
   }

   private static void quad(BufferBuilder buf, double x0, double y0, double z0, double x1, double y1, double z1,
                            double x2, double y2, double z2, double x3, double y3, double z3, float r, float g, float b) {
      buf.pos(x0, y0, z0).color(r, g, b, 1.0F).endVertex();
      buf.pos(x1, y1, z1).color(r, g, b, 1.0F).endVertex();
      buf.pos(x2, y2, z2).color(r, g, b, 1.0F).endVertex();
      buf.pos(x3, y3, z3).color(r, g, b, 1.0F).endVertex();
   }

   @Override
   public boolean isGlobalRenderer(TileEntityNavigator te) {
      return true;
   }
}
