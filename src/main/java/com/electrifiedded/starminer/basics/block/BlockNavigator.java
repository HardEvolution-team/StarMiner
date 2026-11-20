package com.electrifiedded.starminer.basics.block;

import com.electrifiedded.archaiclib.v1710.api.block.IArchaicRenderBlock;
import com.electrifiedded.archaiclib.v1710.api.client.IIcon;
import com.electrifiedded.archaiclib.v1710.api.client.IIconRegister;

import com.electrifiedded.starminer.basics.tileentity.TileEntityNavigator;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.PropertyBool;
import net.minecraft.block.state.BlockFaceShape;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.BlockRenderLayer;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class BlockNavigator extends Block implements IArchaicRenderBlock {
   public static final PropertyBool POWERED = PropertyBool.create("powered");
   private static final AxisAlignedBB BOX = new AxisAlignedBB(0.1875, 0.1875, 0.1875, 0.8125, 0.8125, 0.8125);

   public BlockNavigator() {
      super(Material.ROCK);
      this.setHardness(1.0F);
      this.setResistance(1.0F);
      this.setLightLevel(1.0F);
      this.setDefaultState(this.blockState.getBaseState().withProperty(POWERED, false));
   }

   @Override
   protected BlockStateContainer createBlockState() {
      return new BlockStateContainer(this, POWERED);
   }

   @Override
   public IBlockState getStateFromMeta(int meta) {
      return this.getDefaultState().withProperty(POWERED, (meta & 1) != 0);
   }

   @Override
   public int getMetaFromState(IBlockState state) {
      return state.getValue(POWERED) ? 1 : 0;
   }

   @Override
   public int damageDropped(IBlockState state) {
      return 0;
   }

   @Override
   public boolean hasTileEntity(IBlockState state) {
      return true;
   }

   @Override
   public TileEntity createTileEntity(World world, IBlockState state) {
      return new TileEntityNavigator();
   }

   public boolean isOn(IBlockState state) {
      return state.getBlock() == this && state.getValue(POWERED);
   }

   @Override
   public void neighborChanged(IBlockState state, World world, BlockPos pos, Block blockIn, BlockPos fromPos) {
      boolean isPowerOn = state.getValue(POWERED);
      boolean isHavingPower = world.isBlockPowered(pos);
      if (isPowerOn != isHavingPower) {
         world.setBlockState(pos, state.withProperty(POWERED, isHavingPower), 2);
      }
   }

   @Override
   public boolean onBlockActivated(World world, BlockPos pos, IBlockState state, EntityPlayer player, EnumHand hand, EnumFacing facing, float hitX, float hitY, float hitZ) {
      if (!doesPlayerHasTorchOnHand(player, hand)) {
         return false;
      }

      if (!world.isRemote) {
         TileEntity te = world.getTileEntity(pos);
         if (!(te instanceof TileEntityNavigator)) {
            return false;
         }

         ((TileEntityNavigator)te).activate();
      }

      return true;
   }

   @Override
   public AxisAlignedBB getBoundingBox(IBlockState state, IBlockAccess source, BlockPos pos) {
      return BOX;
   }

   @Override
   public boolean isOpaqueCube(IBlockState state) {
      return false;
   }

   @Override
   public boolean isFullCube(IBlockState state) {
      return false;
   }

   @Override
   public BlockFaceShape getBlockFaceShape(IBlockAccess worldIn, IBlockState state, BlockPos pos, EnumFacing face) {
      return BlockFaceShape.UNDEFINED;
   }

   @SideOnly(Side.CLIENT)
   @Override
   public BlockRenderLayer getRenderLayer() {
      return BlockRenderLayer.CUTOUT;
   }

   public static boolean doesPlayerHasTorchOnHand(EntityPlayer player, EnumHand hand) {
      ItemStack itemNow = player.getHeldItem(hand);
      return !itemNow.isEmpty() && itemNow.getItem() == Item.getItemFromBlock(Blocks.TORCH);
   }

   @SideOnly(Side.CLIENT)
   private IIcon icon;

   @SideOnly(Side.CLIENT)
   @Override
   public void registerBlockIcons(IIconRegister reg) {
      this.icon = reg.registerIcon("minecraft:blocks/beacon");
   }

   @SideOnly(Side.CLIENT)
   @Override
   public IIcon getIcon(int side, int key) {
      return this.icon;
   }

   @SideOnly(Side.CLIENT)
   @Override
   public java.util.List<com.electrifiedded.archaiclib.v1710.api.block.ArchaicRenderPart> getArchaicRenderParts(int key) {
      com.electrifiedded.archaiclib.v1710.api.block.ArchaicRenderPart p =
         com.electrifiedded.archaiclib.v1710.api.block.ArchaicRenderPart.box(BOX, this.icon).renderAllFaces();
      if ((key & 1) == 0) p.color(0.6F, 0.9F, 0.6F);
      return java.util.Collections.singletonList(p);
   }

   @Override
   public int getArchaicItemRenderKey(int meta) {
      return 1;
   }
}
