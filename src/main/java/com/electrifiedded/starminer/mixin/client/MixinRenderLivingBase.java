package com.electrifiedded.starminer.mixin.client;

import com.electrifiedded.starminer.api.GravityDirection;
import com.electrifiedded.starminer.core.entity.GravityLiving;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.entity.RenderLivingBase;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.monster.EntitySilverfish;
import net.minecraft.entity.monster.EntitySpider;
import net.minecraft.util.math.MathHelper;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(RenderLivingBase.class)
public abstract class MixinRenderLivingBase {
   @Inject(method = "applyRotations", at = @At("HEAD"), cancellable = true)
   private void starminer$applyRotations(EntityLivingBase entity, float ageInTicks, float rotationYaw, float partialTicks, CallbackInfo ci) {
      if (!GravityLiving.isAbnormal(entity)) return;
      ci.cancel();
      GravityDirection g = GravityLiving.getDir(entity);
      float w = entity.width / 2.0F;
      switch (g) {
         case southTOnorth_ZN:
            GlStateManager.translate(0.0F, w, -w);
            GlStateManager.rotate(180.0F - rotationYaw, 0.0F, 0.0F, 1.0F);
            GlStateManager.rotate(90.0F, 1.0F, 0.0F, 0.0F);
            break;
         case northTOsouth_ZP:
            GlStateManager.translate(0.0F, w, w);
            GlStateManager.rotate(180.0F + rotationYaw, 0.0F, 0.0F, 1.0F);
            GlStateManager.rotate(-90.0F, 1.0F, 0.0F, 0.0F);
            break;
         case westTOeast_XP:
            GlStateManager.translate(w, w, 0.0F);
            GlStateManager.rotate(180.0F + rotationYaw, 1.0F, 0.0F, 0.0F);
            GlStateManager.rotate(90.0F, 0.0F, 0.0F, 1.0F);
            break;
         case eastTOwest_XN:
            GlStateManager.translate(-w, w, 0.0F);
            GlStateManager.rotate(180.0F - rotationYaw, 1.0F, 0.0F, 0.0F);
            GlStateManager.rotate(-90.0F, 0.0F, 0.0F, 1.0F);
            break;
         case downTOup_YP:
            GlStateManager.translate(0.0F, 2.0F * w, 0.0F);
            GlStateManager.rotate(180.0F + rotationYaw, 0.0F, 1.0F, 0.0F);
            GlStateManager.rotate(180.0F, 1.0F, 0.0F, 0.0F);
            break;
         default:
            GlStateManager.rotate(180.0F - rotationYaw, 0.0F, 1.0F, 0.0F);
      }

      if (entity.deathTime > 0) {
         float f3 = (entity.deathTime + partialTicks - 1.0F) / 20.0F * 1.6F;
         f3 = MathHelper.sqrt(f3);
         if (f3 > 1.0F) {
            f3 = 1.0F;
         }

         GlStateManager.rotate(f3 * (entity instanceof EntitySpider || entity instanceof EntitySilverfish ? 180.0F : 90.0F), 0.0F, 0.0F, 1.0F);
      }
   }
}
