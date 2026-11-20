package com.electrifiedded.starminer.basics.tileentity.render;

import com.electrifiedded.starminer.api.GravityDirection;
import com.electrifiedded.starminer.basics.block.BlockRotator;
import com.electrifiedded.starminer.basics.client.GravityGL;
import com.electrifiedded.starminer.basics.dummy.DummyRotatedBlockAccess;
import com.electrifiedded.starminer.basics.item.block.ItemBlockForRotator;
import com.electrifiedded.starminer.basics.tileentity.TileEntityBlockRotator;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.BlockRendererDispatcher;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.BlockRenderLayer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.client.ForgeHooksClient;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class TileEnityRenderBlockRotator extends TileEntitySpecialRenderer<TileEntityBlockRotator> {
   private static final String CAGE_TEXTURE = "starminer:blocks/rotator_yellow";

   @Override
   public void render(TileEntityBlockRotator te, double x, double y, double z, float partialTick, int destroyStage, float alpha) {
      World world = te.getWorld();
      if (world == null) {
         return;
      }
      BlockPos pos = te.getPos();
      IBlockState rotatorState = world.getBlockState(pos);
      if (!(rotatorState.getBlock() instanceof BlockRotator)) {
         return;
      }
      IBlockState stored = BlockRotator.getStoredState(world, pos, rotatorState);
      boolean holding = isHoldingRotator();
      if (stored == null && !holding) {
         return;
      }

      GravityDirection gDir = te.getGravityDirection();
      Minecraft mc = Minecraft.getMinecraft();
      this.bindTexture(TextureMap.LOCATION_BLOCKS_TEXTURE);
      RenderHelper.disableStandardItemLighting();
      GlStateManager.pushMatrix();
      GlStateManager.translate((float)x, (float)y, (float)z);
      GravityGL.rotateAboutBlockCenter(gDir);
      GlStateManager.enableBlend();
      GlStateManager.blendFunc(770, 771);
      GlStateManager.enableAlpha();
      GlStateManager.enableCull();
      GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
      GlStateManager.shadeModel(Minecraft.isAmbientOcclusionEnabled() ? 7425 : 7424);

      if (stored != null) {
         DummyRotatedBlockAccess access = new DummyRotatedBlockAccess(world, gDir, pos, stored);
         IBlockState actual = stored.getActualState(access, pos);
         BlockRendererDispatcher dispatcher = mc.getBlockRendererDispatcher();
         Tessellator tess = Tessellator.getInstance();
         BufferBuilder buf = tess.getBuffer();
         buf.begin(7, DefaultVertexFormats.BLOCK);
         buf.setTranslation(-pos.getX(), -pos.getY(), -pos.getZ());
         try {
            for (BlockRenderLayer layer : BlockRenderLayer.values()) {
               if (actual.getBlock().canRenderInLayer(actual, layer)) {
                  ForgeHooksClient.setRenderLayer(layer);
                  dispatcher.renderBlock(actual, pos, access, buf);
               }
            }
         } catch (Exception e) {
         } finally {
            ForgeHooksClient.setRenderLayer(null);
            buf.setTranslation(0.0, 0.0, 0.0);
         }
         tess.draw();
      }

      if (holding) {
         this.drawCage(mc);
      }

      GlStateManager.disableBlend();
      GlStateManager.popMatrix();
      RenderHelper.enableStandardItemLighting();
   }

   private void drawCage(Minecraft mc) {
      TextureAtlasSprite s = mc.getTextureMapBlocks().getAtlasSprite(CAGE_TEXTURE);
      float a = -BlockRotator.EXPAND_BND_SIZE;
      float b = 1.0F + BlockRotator.EXPAND_BND_SIZE;
      double u0 = s.getMinU(), u1 = s.getMaxU(), v0 = s.getMinV(), v1 = s.getMaxV();
      GlStateManager.disableLighting();
      GlStateManager.disableCull();
      BufferBuilder buf = Tessellator.getInstance().getBuffer();
      buf.begin(7, DefaultVertexFormats.POSITION_TEX);
      quad(buf, a, a, a, b, a, a, b, a, b, a, a, b, u0, u1, v0, v1);
      quad(buf, a, b, a, a, b, b, b, b, b, b, b, a, u0, u1, v0, v1);
      quad(buf, a, a, a, a, b, a, b, b, a, b, a, a, u0, u1, v0, v1);
      quad(buf, a, a, b, b, a, b, b, b, b, a, b, b, u0, u1, v0, v1);
      quad(buf, a, a, a, a, a, b, a, b, b, a, b, a, u0, u1, v0, v1);
      quad(buf, b, a, a, b, b, a, b, b, b, b, a, b, u0, u1, v0, v1);
      Tessellator.getInstance().draw();
      GlStateManager.enableCull();
      GlStateManager.enableLighting();
   }

   private static void quad(BufferBuilder buf, float x1, float y1, float z1, float x2, float y2, float z2,
                            float x3, float y3, float z3, float x4, float y4, float z4,
                            double u0, double u1, double v0, double v1) {
      buf.pos(x1, y1, z1).tex(u0, v1).endVertex();
      buf.pos(x2, y2, z2).tex(u0, v0).endVertex();
      buf.pos(x3, y3, z3).tex(u1, v0).endVertex();
      buf.pos(x4, y4, z4).tex(u1, v1).endVertex();
   }

   private static boolean isHoldingRotator() {
      EntityPlayer p = Minecraft.getMinecraft().player;
      return p != null && (p.getHeldItemMainhand().getItem() instanceof ItemBlockForRotator
         || p.getHeldItemOffhand().getItem() instanceof ItemBlockForRotator);
   }
}
