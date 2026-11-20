package com.electrifiedded.starminer.basics.block;

import com.electrifiedded.archaiclib.v1710.api.block.IArchaicRenderBlock;
import com.electrifiedded.archaiclib.v1710.api.client.IIcon;
import com.electrifiedded.archaiclib.v1710.api.client.IIconRegister;

import com.electrifiedded.starminer.api.Gravity;
import com.electrifiedded.starminer.api.GravityDirection;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.BlockFaceShape;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.BlockRenderLayer;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class BlockGravityWall extends Block implements IArchaicRenderBlock {
   public static final double HEIGHT_D = 0.0625;
   private static final int GWALL_AFFECT_TICK = 15;
   private static final AxisAlignedBB[] BOXES = new AxisAlignedBB[6];

   static {
      for (int i = 0; i < 6; i++) {
         BOXES[i] = SMDir.slab(i, HEIGHT_D);
      }
   }

   public BlockGravityWall() {
      super(Material.ROCK);
      this.setHardness(9.0F);
      this.setResistance(2.0F);
      this.setDefaultState(this.blockState.getBaseState().withProperty(SMDir.DIR, 0));
   }

   @Override
   protected BlockStateContainer createBlockState() {
      return new BlockStateContainer(this, SMDir.DIR);
   }

   @Override
   public IBlockState getStateFromMeta(int meta) {
      return this.getDefaultState().withProperty(SMDir.DIR, Math.min(meta & 7, 5));
   }

   @Override
   public int getMetaFromState(IBlockState state) {
      return state.getValue(SMDir.DIR);
   }

   @Override
   public int damageDropped(IBlockState state) {
      return 0;
   }

   @Override
   public IBlockState getStateForPlacement(World world, BlockPos pos, EnumFacing facing, float hitX, float hitY, float hitZ, int meta, EntityLivingBase placer) {
      return this.getDefaultState().withProperty(SMDir.DIR, SMDir.metaFromClickedSide(facing));
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
      return BlockRenderLayer.TRANSLUCENT;
   }

   @Override
   public AxisAlignedBB getBoundingBox(IBlockState state, IBlockAccess source, BlockPos pos) {
      return BOXES[state.getValue(SMDir.DIR)];
   }

   @Override
   public BlockFaceShape getBlockFaceShape(IBlockAccess worldIn, IBlockState state, BlockPos pos, EnumFacing face) {
      return SMDir.ATTACH[state.getValue(SMDir.DIR)] == face ? BlockFaceShape.SOLID : BlockFaceShape.UNDEFINED;
   }

   @SideOnly(Side.CLIENT)
   @Override
   public boolean shouldSideBeRendered(IBlockState blockState, IBlockAccess blockAccess, BlockPos pos, EnumFacing side) {
      IBlockState other = blockAccess.getBlockState(pos.offset(side));
      if (other.getBlock() == this && other.getValue(SMDir.DIR).equals(blockState.getValue(SMDir.DIR))
         && SMDir.ATTACH[blockState.getValue(SMDir.DIR)].getAxis() != side.getAxis()) {
         return false;
      }
      return super.shouldSideBeRendered(blockState, blockAccess, pos, side);
   }

   @Override
   public void onEntityCollision(World world, BlockPos pos, IBlockState state, Entity entity) {
      if (entity instanceof EntityPlayer) {
         Gravity gravity = Gravity.getGravityProp(entity);
         entity.fallDistance = 0.0F;
         if (gravity != null) {
            if (world.isRemote) {
               gravity.setTemporaryGravityDirection(GravityDirection.fromDownFacing(SMDir.ATTACH[state.getValue(SMDir.DIR)]), GWALL_AFFECT_TICK);
            } else {
               gravity.acceptExceptionalGravityTick = 45;
            }
         }
      }
   }

   @net.minecraftforge.fml.relauncher.SideOnly(net.minecraftforge.fml.relauncher.Side.CLIENT)
   private IIcon icon;

   @net.minecraftforge.fml.relauncher.SideOnly(net.minecraftforge.fml.relauncher.Side.CLIENT)
   @Override
   public void registerBlockIcons(IIconRegister reg) {
      this.icon = reg.registerIcon("starminer:g_wall");
   }

   @net.minecraftforge.fml.relauncher.SideOnly(net.minecraftforge.fml.relauncher.Side.CLIENT)
   @Override
   public IIcon getIcon(int side, int key) {
      return this.icon;
   }

   @Override
   public net.minecraft.util.math.AxisAlignedBB getArchaicRenderBounds(int key) {
      return SMDir.slab(0, HEIGHT_D);
   }

   @Override
   public net.minecraft.util.EnumFacing getArchaicRenderUp(int key) {
      return SMDir.ATTACH[Math.max(0, Math.min(5, key))].getOpposite();
   }

   @Override
   public int getArchaicItemRenderKey(int meta) {
      return 0;
   }
}
