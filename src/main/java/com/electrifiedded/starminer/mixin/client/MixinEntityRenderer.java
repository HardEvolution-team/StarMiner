package com.electrifiedded.starminer.mixin.client;

import java.nio.FloatBuffer;
import com.electrifiedded.starminer.api.GravityDirection;
import com.electrifiedded.starminer.core.entity.ExtendedPropertyGravity;
import com.electrifiedded.starminer.core.entity.GravityLiving;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.ActiveRenderInfo;
import net.minecraft.client.renderer.EntityRenderer;
import net.minecraft.client.renderer.GLAllocation;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.Vec3d;
import net.minecraftforge.client.event.EntityViewRenderEvent;
import net.minecraftforge.common.MinecraftForge;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(EntityRenderer.class)
public abstract class MixinEntityRenderer {
   @Shadow
   @Final
   private Minecraft mc;
   @Shadow
   private float thirdPersonDistancePrev;
   @Shadow
   private boolean cloudFog;

   @Unique
   private static final FloatBuffer STARMINER$MATRIX = GLAllocation.createDirectFloatBuffer(16);

   @Inject(method = "orientCamera", at = @At("HEAD"), cancellable = true)
   private void starminer$orientCamera(float partialTicks, CallbackInfo ci) {
      Entity entity = this.mc.getRenderViewEntity();
      if (!(entity instanceof EntityLivingBase) || !GravityLiving.isGravitized(entity)) return;
      if (((EntityLivingBase)entity).isPlayerSleeping()) return;
      ExtendedPropertyGravity gp = GravityLiving.gravity(entity);
      GravityDirection g = gp.gravityDirection;
      boolean turning = gp.turnRate < 1.0F;
      if (g == GravityDirection.upTOdown_YN && !turning) return;
      ci.cancel();

      double rx = entity.prevPosX + (entity.posX - entity.prevPosX) * partialTicks;
      double ry = entity.prevPosY + (entity.posY - entity.prevPosY) * partialTicks;
      double rz = entity.prevPosZ + (entity.posZ - entity.prevPosZ) * partialTicks;
      Vec3d eye = g == GravityDirection.upTOdown_YN
         ? new Vec3d(rx, ry + entity.getEyeHeight(), rz)
         : GravityLiving.eyeAt(entity, g, rx, ry, rz);

      if (this.mc.gameSettings.thirdPersonView > 0) {
         double d3 = this.thirdPersonDistancePrev + (4.0F - this.thirdPersonDistancePrev) * partialTicks;
         if (this.mc.gameSettings.debugCamEnable) {
            GlStateManager.translate(0.0F, 0.0F, (float)(-d3));
         } else {
            float f1 = entity.rotationYaw;
            float f2 = entity.rotationPitch;
            if (this.mc.gameSettings.thirdPersonView == 2) {
               f2 += 180.0F;
            }

            double lx = -MathHelper.sin(f1 * (float)(Math.PI / 180.0)) * MathHelper.cos(f2 * (float)(Math.PI / 180.0)) * d3;
            double lz = MathHelper.cos(f1 * (float)(Math.PI / 180.0)) * MathHelper.cos(f2 * (float)(Math.PI / 180.0)) * d3;
            double ly = -MathHelper.sin(f2 * (float)(Math.PI / 180.0)) * d3;
            double d4 = g.toWorldX(lx, ly, lz);
            double d6 = g.toWorldY(lx, ly, lz);
            double d5 = g.toWorldZ(lx, ly, lz);
            for (int i = 0; i < 8; i++) {
               float f3 = ((i & 1) * 2 - 1) * 0.1F;
               float f4 = ((i >> 1 & 1) * 2 - 1) * 0.1F;
               float f5 = ((i >> 2 & 1) * 2 - 1) * 0.1F;
               RayTraceResult hit = this.mc.world.rayTraceBlocks(
                  new Vec3d(eye.x + f3, eye.y + f4, eye.z + f5), new Vec3d(eye.x - d4 + f3 + f5, eye.y - d6 + f4, eye.z - d5 + f5)
               );
               if (hit != null) {
                  double d7 = hit.hitVec.distanceTo(eye);
                  if (d7 < d3) {
                     d3 = d7;
                  }
               }
            }

            if (this.mc.gameSettings.thirdPersonView == 2) {
               GlStateManager.rotate(180.0F, 0.0F, 1.0F, 0.0F);
            }

            GlStateManager.rotate(entity.rotationPitch - f2, 1.0F, 0.0F, 0.0F);
            GlStateManager.rotate(entity.rotationYaw - f1, 0.0F, 1.0F, 0.0F);
            GlStateManager.translate(0.0F, 0.0F, (float)(-d3));
            GlStateManager.rotate(f1 - entity.rotationYaw, 0.0F, 1.0F, 0.0F);
            GlStateManager.rotate(f2 - entity.rotationPitch, 1.0F, 0.0F, 0.0F);
         }
      } else {
         GlStateManager.translate(0.0F, 0.0F, 0.05F);
      }

      if (!this.mc.gameSettings.debugCamEnable) {
         float yaw = entity.prevRotationYaw + (entity.rotationYaw - entity.prevRotationYaw) * partialTicks + 180.0F;
         float pitch = entity.prevRotationPitch + (entity.rotationPitch - entity.prevRotationPitch) * partialTicks;
         IBlockState state = ActiveRenderInfo.getBlockStateAtEntityViewpoint(this.mc.world, entity, partialTicks);
         EntityViewRenderEvent.CameraSetup event = new EntityViewRenderEvent.CameraSetup((EntityRenderer)(Object)this, entity, state, partialTicks, yaw, pitch, 0.0F);
         MinecraftForge.EVENT_BUS.post(event);
         GlStateManager.rotate(event.getRoll(), 0.0F, 0.0F, 1.0F);
         GlStateManager.rotate(event.getPitch(), 1.0F, 0.0F, 0.0F);
         GlStateManager.rotate(event.getYaw(), 0.0F, 1.0F, 0.0F);
      }

      if (g != GravityDirection.upTOdown_YN) {
         double[] m = g.matrixRotationD;
         STARMINER$MATRIX.clear();
         STARMINER$MATRIX.put(new float[]{
            (float)m[0], (float)m[3], (float)m[6], 0.0F,
            (float)m[1], (float)m[4], (float)m[7], 0.0F,
            (float)m[2], (float)m[5], (float)m[8], 0.0F,
            0.0F, 0.0F, 0.0F, 1.0F
         });
         STARMINER$MATRIX.flip();
         GlStateManager.multMatrix(STARMINER$MATRIX);
      }

      if (turning) {
         GlStateManager.rotate(
            90.0F * (gp.prevTurnRate + (gp.turnRate - gp.prevTurnRate) * partialTicks), gp.onChangeRoatDirX, gp.onChangeRoatDirY, gp.onChangeRoatDirZ
         );
      }

      GlStateManager.translate((float)(rx - eye.x), (float)(ry - eye.y), (float)(rz - eye.z));
      this.cloudFog = this.mc.renderGlobal.hasCloudFog(eye.x, eye.y, eye.z, partialTicks);
   }
}
