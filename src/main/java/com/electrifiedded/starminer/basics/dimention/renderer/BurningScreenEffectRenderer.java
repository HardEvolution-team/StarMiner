package com.electrifiedded.starminer.basics.dimention.renderer;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class BurningScreenEffectRenderer {
   @SubscribeEvent
   public void onOverlay(RenderGameOverlayEvent.Pre event) {
      if (event.getType() == RenderGameOverlayEvent.ElementType.HELMET && Minecraft.getMinecraft().player != null) {
         renderBurningScreen(event.getResolution().getScaledWidth(), event.getResolution().getScaledHeight(), event.getPartialTicks());
      }
   }

   public static void renderBurningScreen(int screenWidth, int screenHeight, float partialTicks) {
      double eyePosY = Minecraft.getMinecraft().player.getPositionVector().y;
      if (eyePosY < -20.0) {
         float redRate = -((float)(eyePosY + 20.0)) / 48.0F;
         if (redRate > 0.7F) {
            redRate = 0.7F;
         }

         drawRect(screenWidth, screenHeight, redRate);
      }
   }

   private static void drawRect(int width, int height, float redRate) {
      GlStateManager.disableDepth();
      GlStateManager.disableAlpha();
      GlStateManager.enableBlend();
      GlStateManager.disableTexture2D();
      GlStateManager.tryBlendFuncSeparate(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA,
         GlStateManager.SourceFactor.ONE, GlStateManager.DestFactor.ZERO);
      Tessellator tes = Tessellator.getInstance();
      BufferBuilder buf = tes.getBuffer();
      buf.begin(7, DefaultVertexFormats.POSITION_COLOR);
      buf.pos(0.0, height, 0.0).color(redRate, 0.0F, 0.0F, redRate).endVertex();
      buf.pos(width, height, 0.0).color(redRate, 0.0F, 0.0F, redRate).endVertex();
      buf.pos(width, 0.0, 0.0).color(redRate, 0.0F, 0.0F, redRate).endVertex();
      buf.pos(0.0, 0.0, 0.0).color(redRate, 0.0F, 0.0F, redRate).endVertex();
      tes.draw();
      GlStateManager.enableTexture2D();
      GlStateManager.disableBlend();
      GlStateManager.enableAlpha();
      GlStateManager.enableDepth();
      GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
   }
}
