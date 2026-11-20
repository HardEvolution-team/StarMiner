package com.electrifiedded.starminer.basics.block;

import com.electrifiedded.archaiclib.v1710.api.block.IArchaicRenderBlock;
import com.electrifiedded.archaiclib.v1710.api.client.IIcon;
import com.electrifiedded.archaiclib.v1710.api.client.IIconRegister;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.util.BlockRenderLayer;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class BlockInnerCore extends Block implements IArchaicRenderBlock {
   public BlockInnerCore() {
      super(Material.ROCK);
      this.setHardness(45.0F);
      this.setResistance(2000.0F);
      this.setLightLevel(1.0F);
   }

   @Override
   public boolean isOpaqueCube(IBlockState state) {
      return false;
   }

   @SideOnly(Side.CLIENT)
   @Override
   public BlockRenderLayer getRenderLayer() {
      return BlockRenderLayer.CUTOUT;
   }

   @net.minecraftforge.fml.relauncher.SideOnly(net.minecraftforge.fml.relauncher.Side.CLIENT)
   private IIcon icon;

   @net.minecraftforge.fml.relauncher.SideOnly(net.minecraftforge.fml.relauncher.Side.CLIENT)
   @Override
   public void registerBlockIcons(IIconRegister reg) {
      this.icon = reg.registerIcon("starminer:inner_core");
   }

   @net.minecraftforge.fml.relauncher.SideOnly(net.minecraftforge.fml.relauncher.Side.CLIENT)
   @Override
   public IIcon getIcon(int side, int key) {
      return this.icon;
   }
}
