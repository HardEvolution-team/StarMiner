package com.electrifiedded.starminer.basics.gui;

import java.util.ArrayList;
import java.util.List;
import com.electrifiedded.starminer.basics.tileentity.TileEntityGravityGenerator;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.resources.I18n;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.client.config.GuiCheckBox;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class GuiStarCore extends GuiContainer {
   private static final ResourceLocation resource = new ResourceLocation("starminer:textures/gui/guistarcore.png");
   private static final String[] TYPENAMES = new String[]{"starInfo.type.sph", "starInfo.type.cub", "starInfo.type.xcyl", "starInfo.type.ycyl", "starInfo.type.zcyl"};
   private GuiButton buttonToggleType;
   private GuiCheckBox checkBoxUseBuffer;
   private final TileEntityGravityGenerator core;
   private final List<String> floatLabelList = new ArrayList<>();

   public GuiStarCore(EntityPlayer player, TileEntityGravityGenerator core) {
      super(new ContainerStarCore(player, core));
      this.core = core;
      this.allowUserInput = false;
      this.ySize = 222;
   }

   @Override
   public void initGui() {
      super.initGui();
      int xLeft = (this.width - this.xSize) / 2;
      int yTop = (this.height - this.ySize) / 2;
      int bx = xLeft + 8;
      int bw = 17;
      int bh = 20;
      int shift = bw + 1;
      int row1 = yTop + 8;
      int row2 = yTop + 55 - bh;
      this.buttonList.add(new GuiButton(8, bx, row1, bw, bh, "-5"));
      this.buttonList.add(new GuiButton(6, bx + shift, row1, bw, bh, "-1"));
      this.buttonList.add(new GuiButton(4, bx, row2, bw, bh, "-5"));
      this.buttonList.add(new GuiButton(2, bx + shift, row2, bw, bh, "-1"));
      bx = xLeft + 97;
      this.buttonList.add(new GuiButton(5, bx, row1, bw, bh, "+1"));
      this.buttonList.add(new GuiButton(7, bx + shift, row1, bw, bh, "+5"));
      this.buttonList.add(new GuiButton(1, bx, row2, bw, bh, "+1"));
      this.buttonList.add(new GuiButton(3, bx + shift, row2, bw, bh, "+5"));
      this.buttonList.add(this.buttonToggleType = new GuiButton(9, xLeft + 135, row1, 30, bh, I18n.format(TYPENAMES[this.core.type])));
      this.buttonList.add(this.checkBoxUseBuffer = new GuiCheckBox(10, xLeft + 145, yTop + 58, "", this.core.useBufferArea));
      if (this.floatLabelList.isEmpty()) {
         this.floatLabelList.add(I18n.format("starInfo.bufferzone"));
      }
   }

   @Override
   public void updateScreen() {
      super.updateScreen();
      this.buttonToggleType.displayString = I18n.format(TYPENAMES[Math.floorMod(this.core.type, TYPENAMES.length)]);
      boolean cyl = this.core.type == 2 || this.core.type == 3 || this.core.type == 4;
      this.checkBoxUseBuffer.visible = cyl;
      if (cyl) {
         this.checkBoxUseBuffer.setIsChecked(this.core.useBufferArea);
      }
   }

   @Override
   public void drawScreen(int mouseX, int mouseY, float partialTicks) {
      this.drawDefaultBackground();
      super.drawScreen(mouseX, mouseY, partialTicks);
      this.renderHoveredToolTip(mouseX, mouseY);
   }

   @Override
   protected void drawGuiContainerForegroundLayer(int mouseX, int mouseY) {
      this.fontRenderer.drawString(I18n.format("container.inventory"), 8, this.ySize - 94, 4210752);
      this.fontRenderer.drawString(I18n.format("starInfo.inventory"), 8, this.ySize - 162, 4210752);
      int centerX = 70;
      String s = I18n.format("starInfo.titleSSize");
      this.fontRenderer.drawString(s, centerX - this.fontRenderer.getStringWidth(s) / 2, this.ySize - 212, 15658734);
      s = I18n.format("starInfo.gSizeInfo", (int)this.core.starRad);
      this.fontRenderer.drawString(s, centerX - this.fontRenderer.getStringWidth(s) / 2, this.ySize - 202, 15658734);
      s = I18n.format("starInfo.titleGrange");
      this.fontRenderer.drawString(s, centerX - this.fontRenderer.getStringWidth(s) / 2, this.ySize - 187, 15658734);
      s = I18n.format("starInfo.gRangeInfo", (int)this.core.gravityRange);
      this.fontRenderer.drawString(s, centerX - this.fontRenderer.getStringWidth(s) / 2, this.ySize - 177, 15658734);
      if (this.checkBoxUseBuffer.visible && this.checkBoxUseBuffer.isMouseOver()) {
         int w = this.fontRenderer.getStringWidth(this.floatLabelList.get(0));
         this.drawHoveringText(this.floatLabelList, 145 - w / 2 - 3, this.ySize - 137);
      }
   }

   @Override
   protected void drawGuiContainerBackgroundLayer(float partialTicks, int mouseX, int mouseY) {
      GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
      this.mc.getTextureManager().bindTexture(resource);
      int k = (this.width - this.xSize) / 2;
      int l = (this.height - this.ySize) / 2;
      this.drawTexturedModalRect(k, l, 0, 0, this.xSize, this.ySize);
   }

   @Override
   protected void actionPerformed(GuiButton button) {
      this.mc.playerController.sendEnchantPacket(this.inventorySlots.windowId, button.id);
   }
}
