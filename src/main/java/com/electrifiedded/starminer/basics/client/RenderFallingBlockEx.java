package com.electrifiedded.starminer.basics.client;

import com.electrifiedded.starminer.basics.entity.EntityFallingBlockEx;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.BlockRendererDispatcher;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.entity.Render;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.util.EnumBlockRenderType;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class RenderFallingBlockEx extends Render<EntityFallingBlockEx> {
   public RenderFallingBlockEx(RenderManager manager) {
      super(manager);
      this.shadowSize = 0.5F;
   }

   @Override
   public void doRender(EntityFallingBlockEx entity, double x, double y, double z, float entityYaw, float partialTicks) {
      IBlockState state = entity.getBlockState();
      if (state == null || state.getRenderType() != EnumBlockRenderType.MODEL) return;
      World world = entity.world;
      BlockPos here = new BlockPos(entity.posX, entity.getEntityBoundingBox().maxY, entity.posZ);
      if (state == world.getBlockState(here)) return;

      this.bindTexture(TextureMap.LOCATION_BLOCKS_TEXTURE);
      GlStateManager.pushMatrix();
      GlStateManager.disableLighting();
      Tessellator tes = Tessellator.getInstance();
      BufferBuilder buf = tes.getBuffer();
      if (this.renderOutlines) {
         GlStateManager.enableColorMaterial();
         GlStateManager.enableOutlineMode(this.getTeamColor(entity));
      }

      buf.begin(7, DefaultVertexFormats.BLOCK);
      BlockPos pos = new BlockPos(entity.posX, entity.getEntityBoundingBox().minY, entity.posZ);
      double cy = (entity.getEntityBoundingBox().minY + entity.getEntityBoundingBox().maxY) / 2.0 - entity.posY;
      GlStateManager.translate((float)(x - pos.getX() - 0.5), (float)(y + cy - pos.getY() - 0.5), (float)(z - pos.getZ() - 0.5));
      BlockRendererDispatcher dispatcher = Minecraft.getMinecraft().getBlockRendererDispatcher();
      dispatcher.getBlockModelRenderer().renderModel(
         world, dispatcher.getModelForState(state), state, pos, buf, false, MathHelper.getPositionRandom(pos));
      tes.draw();

      if (this.renderOutlines) {
         GlStateManager.disableOutlineMode();
         GlStateManager.disableColorMaterial();
      }

      GlStateManager.enableLighting();
      GlStateManager.popMatrix();
      super.doRender(entity, x, y, z, entityYaw, partialTicks);
   }

   @Override
   protected ResourceLocation getEntityTexture(EntityFallingBlockEx entity) {
      return TextureMap.LOCATION_BLOCKS_TEXTURE;
   }
}
