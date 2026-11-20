package com.electrifiedded.starminer.basics.tileentity.render;

import com.electrifiedded.starminer.api.GravityDirection;
import com.electrifiedded.starminer.basics.block.BlockChestEx;
import com.electrifiedded.starminer.basics.client.GravityGL;
import com.electrifiedded.starminer.basics.tileentity.TileEntityChestEx;
import java.util.Calendar;
import net.minecraft.client.model.ModelChest;
import net.minecraft.client.model.ModelLargeChest;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class TileEntityChestRendererEx extends TileEntitySpecialRenderer<TileEntityChestEx> {
   private static final ResourceLocation textureTrappedDouble = new ResourceLocation("textures/entity/chest/trapped_double.png");
   private static final ResourceLocation textureChristmasDouble = new ResourceLocation("textures/entity/chest/christmas_double.png");
   private static final ResourceLocation textureNormalDouble = new ResourceLocation("textures/entity/chest/normal_double.png");
   private static final ResourceLocation textureTrapped = new ResourceLocation("textures/entity/chest/trapped.png");
   private static final ResourceLocation textureChristmas = new ResourceLocation("textures/entity/chest/christmas.png");
   private static final ResourceLocation textureNormal = new ResourceLocation("textures/entity/chest/normal.png");
   private final ModelChest modelChest = new ModelChest();
   private final ModelChest modelLargeChest = new ModelLargeChest();
   private boolean isChristams;

   public TileEntityChestRendererEx() {
      Calendar calendar = Calendar.getInstance();
      if (calendar.get(Calendar.MONTH) + 1 == 12 && calendar.get(Calendar.DATE) >= 24 && calendar.get(Calendar.DATE) <= 26) {
         this.isChristams = true;
      }
   }

   @Override
   public void render(TileEntityChestEx te, double posX, double posY, double posZ, float partialTick, int destroyStage, float alpha) {
      GravityDirection gDir = te.getGravityDirection();
      int meta = 0;
      if (te.hasWorld()) {
         meta = te.getBlockMetadata();
         if (te.getBlockType() instanceof BlockChestEx) {
            meta = te.getWorld().getBlockState(te.getPos()).getValue(BlockChestEx.FACING).getIndex();
         }
      }

      int adjacentChestInt = te.getAdjacentChestTo();
      if (adjacentChestInt == TileEntityChestEx.IS_adjacentChestZNeg || adjacentChestInt == TileEntityChestEx.IS_adjacentChestXNeg) {
         return;
      }

      ModelChest modelchest;
      if (adjacentChestInt == TileEntityChestEx.IS_adjacentChestNone) {
         modelchest = this.modelChest;
         if (destroyStage >= 0) {
            this.bindTexture(DESTROY_STAGES[destroyStage]);
            GlStateManager.matrixMode(5890);
            GlStateManager.pushMatrix();
            GlStateManager.scale(4.0F, 4.0F, 1.0F);
            GlStateManager.translate(0.0625F, 0.0625F, 0.0625F);
            GlStateManager.matrixMode(5888);
         } else if (te.getChestType() == 1) {
            this.bindTexture(textureTrapped);
         } else if (this.isChristams) {
            this.bindTexture(textureChristmas);
         } else {
            this.bindTexture(textureNormal);
         }
      } else {
         modelchest = this.modelLargeChest;
         if (destroyStage >= 0) {
            this.bindTexture(DESTROY_STAGES[destroyStage]);
            GlStateManager.matrixMode(5890);
            GlStateManager.pushMatrix();
            GlStateManager.scale(8.0F, 4.0F, 1.0F);
            GlStateManager.translate(0.0625F, 0.0625F, 0.0625F);
            GlStateManager.matrixMode(5888);
         } else if (te.getChestType() == 1) {
            this.bindTexture(textureTrappedDouble);
         } else if (this.isChristams) {
            this.bindTexture(textureChristmasDouble);
         } else {
            this.bindTexture(textureNormalDouble);
         }
      }

      GlStateManager.pushMatrix();
      GlStateManager.enableRescaleNormal();
      GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
      GlStateManager.translate((float)posX, (float)posY, (float)posZ);
      GravityGL.rotateAboutBlockCenter(gDir);
      GlStateManager.translate(0.0F, 1.0F, 1.0F);
      GlStateManager.scale(1.0F, -1.0F, -1.0F);
      GlStateManager.translate(0.5F, 0.5F, 0.5F);
      int rotateAroundY = 0;
      if (meta == 2) {
         rotateAroundY = 180;
      }
      if (meta == 3) {
         rotateAroundY = 0;
      }
      if (meta == 4) {
         rotateAroundY = 90;
      }
      if (meta == 5) {
         rotateAroundY = -90;
      }

      if (meta == 2 && adjacentChestInt == TileEntityChestEx.IS_adjacentChestXPos) {
         GlStateManager.translate(1.0F, 0.0F, 0.0F);
      }
      if (meta == 5 && adjacentChestInt == TileEntityChestEx.IS_adjacentChestZPos) {
         GlStateManager.translate(0.0F, 0.0F, -1.0F);
      }

      GlStateManager.rotate(rotateAroundY, 0.0F, 1.0F, 0.0F);
      GlStateManager.translate(-0.5F, -0.5F, -0.5F);
      float f1 = te.prevLidAngle + (te.lidAngle - te.prevLidAngle) * partialTick;
      f1 = 1.0F - f1;
      f1 = 1.0F - f1 * f1 * f1;
      modelchest.chestLid.rotateAngleX = -(f1 * (float)Math.PI / 2.0F);
      modelchest.renderAll();
      GlStateManager.disableRescaleNormal();
      GlStateManager.popMatrix();
      GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
      if (destroyStage >= 0) {
         GlStateManager.matrixMode(5890);
         GlStateManager.popMatrix();
         GlStateManager.matrixMode(5888);
      }
   }
}
