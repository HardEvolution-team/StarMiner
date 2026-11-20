package com.electrifiedded.starminer.basics.block;

import com.electrifiedded.archaiclib.v1710.api.block.IArchaicRenderBlock;
import com.electrifiedded.archaiclib.v1710.api.client.IIcon;
import com.electrifiedded.archaiclib.v1710.api.client.IIconRegister;

import com.electrifiedded.starminer.StarMiner;
import com.electrifiedded.starminer.basics.SMModContainer;
import com.electrifiedded.starminer.basics.tileentity.TileEntityGravityGenerator;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.InventoryHelper;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.BlockRenderLayer;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class BlockGravityCore extends Block implements IArchaicRenderBlock {
   public BlockGravityCore() {
      super(Material.ROCK);
      this.setHardness(1.0F);
   }

   @Override
   public boolean hasTileEntity(IBlockState state) {
      return true;
   }

   @Override
   public TileEntity createTileEntity(World world, IBlockState state) {
      return new TileEntityGravityGenerator();
   }

   @Override
   public boolean isOpaqueCube(IBlockState state) {
      return false;
   }

   @Override
   public boolean isFullCube(IBlockState state) {
      return false;
   }

   @SideOnly(Side.CLIENT)
   @Override
   public BlockRenderLayer getRenderLayer() {
      return BlockRenderLayer.CUTOUT;
   }

   @Override
   public boolean onBlockActivated(World world, BlockPos pos, IBlockState state, EntityPlayer player, EnumHand hand, EnumFacing facing, float hitX, float hitY, float hitZ) {
      if (!world.isRemote) {
         player.openGui(StarMiner.instance, SMModContainer.guiStarCoreId, world, pos.getX(), pos.getY(), pos.getZ());
      }

      return true;
   }

   @Override
   public void breakBlock(World world, BlockPos pos, IBlockState state) {
      TileEntity te = world.getTileEntity(pos);
      if (te instanceof TileEntityGravityGenerator) {
         InventoryHelper.dropInventoryItems(world, pos, (TileEntityGravityGenerator)te);
         world.updateComparatorOutputLevel(pos, this);
      }

      super.breakBlock(world, pos, state);
   }

   @SideOnly(Side.CLIENT)
   private IIcon icon;
   @SideOnly(Side.CLIENT)
   private IIcon lava;

   @SideOnly(Side.CLIENT)
   @Override
   public void registerBlockIcons(IIconRegister reg) {
      this.icon = reg.registerIcon("starminer:g_core");
      this.lava = reg.registerIcon("minecraft:blocks/lava_still");
   }

   @SideOnly(Side.CLIENT)
   @Override
   public IIcon getIcon(int side, int key) {
      return this.icon;
   }

   @SideOnly(Side.CLIENT)
   @Override
   public java.util.List<com.electrifiedded.archaiclib.v1710.api.block.ArchaicRenderPart> getArchaicRenderParts(int key) {
      java.util.List<com.electrifiedded.archaiclib.v1710.api.block.ArchaicRenderPart> parts = new java.util.ArrayList<>();
      double lb = 0.40625;
      double hb = 0.59375;
      for (int k = 0; k < 8; k++) {
         double x = (k & 1) != 0 ? hb : 0.0;
         double y = (k & 2) != 0 ? hb : 0.0;
         double z = (k & 4) != 0 ? hb : 0.0;
         parts.add(com.electrifiedded.archaiclib.v1710.api.block.ArchaicRenderPart.box(x, y, z, x + lb, y + lb, z + lb, this.icon));
      }
      if (key >= 0) {
         parts.add(com.electrifiedded.archaiclib.v1710.api.block.ArchaicRenderPart.box(0.1875, 0.1875, 0.1875, 0.8125, 0.875, 0.8125, this.lava)
            .color(0.2F, 0.3F, 1.0F));
      }
      return parts;
   }
}
