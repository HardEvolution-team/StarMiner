package com.electrifiedded.starminer.basics.client;

import com.electrifiedded.starminer.api.Gravity;
import com.electrifiedded.starminer.basics.entity.EntityGProjectile;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.entity.RenderArrow;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.Vec3d;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class RenderGProjectile extends RenderArrow<EntityGProjectile> {
   private static final ResourceLocation ARROW_TEXTURES = new ResourceLocation("textures/entity/projectiles/arrow.png");

   public RenderGProjectile(RenderManager manager) {
      super(manager);
   }

   @Override
   protected ResourceLocation getEntityTexture(EntityGProjectile entity) {
      return ARROW_TEXTURES;
   }

   @Override
   public void doRender(EntityGProjectile entity, double x, double y, double z, float entityYaw, float partialTicks) {
      super.doRender(entity, x, y, z, entityYaw, partialTicks);
      if (entity.getGProjectileType() == EntityGProjectile.GProjectileType.gRappleHook) {
         this.renderRope(entity, x, y, z, partialTicks);
      }
   }

   private void renderRope(EntityGProjectile hook, double x, double y, double z, float partialTicks) {
      Entity shooter = hook.getShooter();
      if (!(shooter instanceof EntityPlayer)) return;
      EntityPlayer player = (EntityPlayer)shooter;
      Gravity gravity = Gravity.getGravityProp(player);
      if (gravity == null) return;

      Vec3d eye = gravity.getGravityFixedPlayerEyePoz(player, partialTicks);
      float pitch = player.prevRotationPitch + (player.rotationPitch - player.prevRotationPitch) * partialTicks;
      float yaw = player.prevRotationYaw + (player.rotationYaw - player.prevRotationYaw) * partialTicks;
      Vec3d look = gravity.getGravityFixedLook(pitch + 10.0F, yaw + 25.0F);
      Vec3d down = gravity.getGravityFixedLook(pitch + 90.0F, yaw);
      double gunX = eye.x + look.x * 0.4 + down.x * 0.12;
      double gunY = eye.y + look.y * 0.4 + down.y * 0.12;
      double gunZ = eye.z + look.z * 0.4 + down.z * 0.12;

      double hx = hook.prevPosX + (hook.posX - hook.prevPosX) * partialTicks;
      double hy = hook.prevPosY + (hook.posY - hook.prevPosY) * partialTicks;
      double hz = hook.prevPosZ + (hook.posZ - hook.prevPosZ) * partialTicks;
      double dx = gunX - hx;
      double dy = gunY - hy;
      double dz = gunZ - hz;

      GlStateManager.disableTexture2D();
      GlStateManager.disableLighting();
      GlStateManager.disableCull();
      Tessellator tes = Tessellator.getInstance();
      BufferBuilder buf = tes.getBuffer();
      for (int pass = 0; pass < 2; pass++) {
         buf.begin(5, DefaultVertexFormats.POSITION_COLOR);
         for (int i = 0; i <= 24; i++) {
            float r = i % 2 == 0 ? 0.5F : 0.35F;
            float g = i % 2 == 0 ? 0.4F : 0.28F;
            float b = i % 2 == 0 ? 0.3F : 0.21F;
            float f = i / 24.0F;
            double px = x + dx * f;
            double py = y + dy * f;
            double pz = z + dz * f;
            if (pass == 0) {
               buf.pos(px, py, pz).color(r, g, b, 1.0F).endVertex();
               buf.pos(px + 0.025, py + 0.025, pz).color(r, g, b, 1.0F).endVertex();
            } else {
               buf.pos(px, py + 0.025, pz).color(r, g, b, 1.0F).endVertex();
               buf.pos(px + 0.025, py, pz + 0.025).color(r, g, b, 1.0F).endVertex();
            }
         }
         tes.draw();
      }
      GlStateManager.enableLighting();
      GlStateManager.enableTexture2D();
      GlStateManager.enableCull();
   }
}
