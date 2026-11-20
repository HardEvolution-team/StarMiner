package com.electrifiedded.starminer.basics.block;

import com.electrifiedded.archaiclib.v1710.api.block.IArchaicRenderBlock;
import com.electrifiedded.archaiclib.v1710.api.client.IIcon;
import com.electrifiedded.archaiclib.v1710.api.client.IIconRegister;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;

public class BlockOuterCore extends Block implements IArchaicRenderBlock {
   public BlockOuterCore() {
      super(Material.ROCK);
      this.setHardness(4.0F);
      this.setResistance(2000.0F);
      this.setLightLevel(1.0F);
   }

   @net.minecraftforge.fml.relauncher.SideOnly(net.minecraftforge.fml.relauncher.Side.CLIENT)
   private IIcon icon;

   @net.minecraftforge.fml.relauncher.SideOnly(net.minecraftforge.fml.relauncher.Side.CLIENT)
   @Override
   public void registerBlockIcons(IIconRegister reg) {
      this.icon = reg.registerIcon("starminer:outer_core");
   }

   @net.minecraftforge.fml.relauncher.SideOnly(net.minecraftforge.fml.relauncher.Side.CLIENT)
   @Override
   public IIcon getIcon(int side, int key) {
      return this.icon;
   }
}
