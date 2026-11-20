package com.electrifiedded.starminer.basics.entity.fx;

import com.electrifiedded.starminer.basics.client.BillboardHelper;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.entity.Entity;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class EntityCometTailFX extends Particle {
   private final float initialParticleScale;

   public EntityCometTailFX(World world, double posX, double posY, double posZ) {
      this(world, posX, posY, posZ, 1.0F);
   }

   public EntityCometTailFX(World world, double posX, double posY, double posZ, float scale) {
      super(world, posX, posY, posZ, 0.0, 0.0, 0.0);
      this.particleRed = this.particleGreen = this.particleBlue = (float)(Math.random() * 0.3F + 0.6F);
      this.particleScale *= 0.75F;
      this.particleScale *= scale;
      this.initialParticleScale = this.particleScale;
      this.particleMaxAge = (int)(18.0 / (Math.random() * 0.8 + 0.6));
      this.particleMaxAge = (int)(this.particleMaxAge * scale);
      this.canCollide = false;
      this.setParticleTextureIndex(65);
      this.onUpdate();
   }

   public void setMotion(double x, double y, double z) {
      this.motionX = x;
      this.motionY = y;
      this.motionZ = z;
   }

   @Override
   public void renderParticle(BufferBuilder buffer, Entity entityIn, float partialTicks, float rotX, float rotZ, float rotYZ, float rotXY, float rotXZ) {
      float agedScale = (this.particleAge + partialTicks) / this.particleMaxAge * 32.0F;
      if (agedScale < 0.0F) {
         agedScale = 0.0F;
      }

      if (agedScale > 1.0F) {
         agedScale = 1.0F;
      }

      this.particleScale = this.initialParticleScale * agedScale;
      BillboardHelper.compute(partialTicks);
      float rotationX = BillboardHelper.rotationX;
      float rotationZ = BillboardHelper.rotationZ;
      float rotationYZ = BillboardHelper.rotationYZ;
      float rotationXY = BillboardHelper.rotationXY;
      float rotationXZ = BillboardHelper.rotationXZ;
      float texUMN = this.particleTextureIndexX / 16.0F;
      float texUMX = texUMN + 0.0624375F;
      float texVMN = this.particleTextureIndexY / 16.0F;
      float texVMX = texVMN + 0.0624375F;
      float scale = 0.1F * this.particleScale;
      if (this.particleTexture != null) {
         texUMN = this.particleTexture.getMinU();
         texUMX = this.particleTexture.getMaxU();
         texVMN = this.particleTexture.getMinV();
         texVMX = this.particleTexture.getMaxV();
      }

      float fX = (float)(this.prevPosX + (this.posX - this.prevPosX) * partialTicks - interpPosX);
      float fY = (float)(this.prevPosY + (this.posY - this.prevPosY) * partialTicks - interpPosY);
      float fZ = (float)(this.prevPosZ + (this.posZ - this.prevPosZ) * partialTicks - interpPosZ);
      int light = 240 << 16 | 240;
      int l1 = light >> 16 & 65535;
      int l2 = light & 65535;
      buffer.pos(fX - rotationX * scale - rotationYZ * scale, fY - rotationXZ * scale, fZ - rotationZ * scale - rotationXY * scale)
         .tex(texUMX, texVMX).color(this.particleRed, this.particleGreen, this.particleBlue, this.particleAlpha).lightmap(l1, l2).endVertex();
      buffer.pos(fX - rotationX * scale + rotationYZ * scale, fY + rotationXZ * scale, fZ - rotationZ * scale + rotationXY * scale)
         .tex(texUMX, texVMN).color(this.particleRed, this.particleGreen, this.particleBlue, this.particleAlpha).lightmap(l1, l2).endVertex();
      buffer.pos(fX + rotationX * scale + rotationYZ * scale, fY + rotationXZ * scale, fZ + rotationZ * scale + rotationXY * scale)
         .tex(texUMN, texVMN).color(this.particleRed, this.particleGreen, this.particleBlue, this.particleAlpha).lightmap(l1, l2).endVertex();
      buffer.pos(fX + rotationX * scale - rotationYZ * scale, fY - rotationXZ * scale, fZ + rotationZ * scale - rotationXY * scale)
         .tex(texUMN, texVMX).color(this.particleRed, this.particleGreen, this.particleBlue, this.particleAlpha).lightmap(l1, l2).endVertex();
   }

   @Override
   public void onUpdate() {
      this.prevPosX = this.posX;
      this.prevPosY = this.posY;
      this.prevPosZ = this.posZ;
      if (this.particleAge++ >= this.particleMaxAge) {
         this.setExpired();
      }

      this.move(this.motionX, this.motionY, this.motionZ);
      this.motionX *= 0.65;
      this.motionY *= 0.65;
      this.motionZ *= 0.65;
   }
}
