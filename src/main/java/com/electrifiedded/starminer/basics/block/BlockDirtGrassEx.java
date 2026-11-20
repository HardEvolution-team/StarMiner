package com.electrifiedded.starminer.basics.block;

import com.electrifiedded.archaiclib.v1710.api.block.IArchaicRenderBlock;
import com.electrifiedded.archaiclib.v1710.api.client.IIcon;
import com.electrifiedded.archaiclib.v1710.api.client.IIconRegister;

import net.minecraft.block.Block;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.PropertyBool;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;

public class BlockDirtGrassEx extends Block implements IArchaicRenderBlock {
   public static final PropertyBool TILLED = PropertyBool.create("tilled");

   public BlockDirtGrassEx() {
      super(Material.GROUND);
      this.setHardness(0.5F);
      this.setSoundType(SoundType.GROUND);
      this.setDefaultState(this.blockState.getBaseState().withProperty(TILLED, false));
   }

   @Override
   protected BlockStateContainer createBlockState() {
      return new BlockStateContainer(this, TILLED);
   }

   @Override
   public IBlockState getStateFromMeta(int meta) {
      return this.getDefaultState().withProperty(TILLED, (meta & 8) != 0);
   }

   @Override
   public int getMetaFromState(IBlockState state) {
      return state.getValue(TILLED) ? 8 : 0;
   }

   @Override
   public int damageDropped(IBlockState state) {
      return 0;
   }

   @Override
   public boolean isSideSolid(IBlockState base_state, IBlockAccess world, BlockPos pos, EnumFacing side) {
      return true;
   }

   @Override
   public boolean canSustainPlant(IBlockState state, IBlockAccess world, BlockPos pos, EnumFacing direction, net.minecraftforge.common.IPlantable plantable) {
      return true;
   }

   @net.minecraftforge.fml.relauncher.SideOnly(net.minecraftforge.fml.relauncher.Side.CLIENT)
   private IIcon dirt, farmland;

   @net.minecraftforge.fml.relauncher.SideOnly(net.minecraftforge.fml.relauncher.Side.CLIENT)
   @Override
   public void registerBlockIcons(IIconRegister reg) {
      this.dirt = reg.registerIcon("minecraft:dirt");
      this.farmland = reg.registerIcon("minecraft:farmland_dry");
   }

   @net.minecraftforge.fml.relauncher.SideOnly(net.minecraftforge.fml.relauncher.Side.CLIENT)
   @Override
   public IIcon getIcon(int side, int key) {
      return side == 1 && (key & 8) != 0 ? this.farmland : this.dirt;
   }
}
