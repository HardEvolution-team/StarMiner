package com.electrifiedded.starminer.basics.client;

import com.electrifiedded.starminer.basics.entity.EntityStarSquid;
import net.minecraft.client.model.ModelSquid;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.entity.RenderLiving;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class RenderStarSquid extends RenderLiving<EntityStarSquid> {
   private static final ResourceLocation SQUID_TEXTURES = new ResourceLocation("textures/entity/squid.png");

   public RenderStarSquid(RenderManager manager) {
      super(manager, new ModelSquid(), 0.7F);
   }

   @Override
   protected ResourceLocation getEntityTexture(EntityStarSquid entity) {
      return SQUID_TEXTURES;
   }

   @Override
   protected void applyRotations(EntityStarSquid squid, float ageInTicks, float rotationYaw, float partialTicks) {
      float pitch = squid.prevSquidPitch + (squid.squidPitch - squid.prevSquidPitch) * partialTicks;
      float yaw = squid.prevSquidYaw + (squid.squidYaw - squid.prevSquidYaw) * partialTicks;
      GlStateManager.translate(0.0F, 0.5F, 0.0F);
      GlStateManager.rotate(180.0F - rotationYaw, 0.0F, 1.0F, 0.0F);
      GlStateManager.rotate(pitch, 1.0F, 0.0F, 0.0F);
      GlStateManager.rotate(yaw, 0.0F, 1.0F, 0.0F);
      GlStateManager.translate(0.0F, -1.2F, 0.0F);
   }

   @Override
   protected float handleRotationFloat(EntityStarSquid squid, float partialTicks) {
      return squid.prevTentacleAngle + (squid.tentacleAngle - squid.prevTentacleAngle) * partialTicks;
   }
}
