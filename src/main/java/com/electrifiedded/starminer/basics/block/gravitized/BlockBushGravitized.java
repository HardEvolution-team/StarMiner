package com.electrifiedded.starminer.basics.block.gravitized;

import com.electrifiedded.archaiclib.v1710.api.block.IArchaicRenderBlock;
import com.electrifiedded.archaiclib.v1710.api.client.IIcon;
import com.electrifiedded.archaiclib.v1710.api.client.IIconRegister;

import com.electrifiedded.starminer.basics.SMModContainer;
import net.minecraft.block.Block;
import net.minecraft.block.BlockBush;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.PropertyDirection;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.init.Blocks;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;

public class BlockBushGravitized extends BlockBush implements IArchaicRenderBlock, IGravitizedPlants {
   public static final PropertyDirection FACING = PropertyDirection.create("facing");
   public double W_D = 0.2;
   public double HEIGHT_D = 0.6;

   public BlockBushGravitized(Material material, double wd, double heightD) {
      super(material);
      this.W_D = wd;
      this.HEIGHT_D = heightD;
      this.setHardness(0.0F);
      this.setSoundType(SoundType.PLANT);
   }

   protected BlockBushGravitized(double wd, double heightD) {
      this(Material.PLANTS, wd, heightD);
   }

   public BlockBushGravitized() {
      this(0.2, 0.6);
   }

   @Override
   public boolean allowPlantOn(IBlockState support, IBlockState plantState) {
      Block block = support.getBlock();
      return Blocks.DIRT == block || Blocks.GRASS == block || Blocks.FARMLAND == block || SMModContainer.DirtGrassExBlock == block;
   }

   @Override
   public IBlockState getActualState(IBlockState state, IBlockAccess worldIn, BlockPos pos) {
      return state.withProperty(FACING, GravityPlants.getGrowFacing(worldIn, pos));
   }

   @Override
   public boolean canPlaceBlockAt(World world, BlockPos pos) {
      return world.getBlockState(pos).getBlock().isReplaceable(world, pos);
   }

   @Override
   public boolean canPlaceBlockOnSide(World world, BlockPos pos, EnumFacing side) {
      return this.canPlaceBlockAt(world, pos) && this.allowPlantOn(world.getBlockState(pos.offset(side.getOpposite())), this.getDefaultState());
   }

   @Override
   public boolean canBlockStay(World world, BlockPos pos, IBlockState state) {
      return GravityPlants.canBlockStay(world, pos, state, this);
   }

   @Override
   public IBlockState getStateForPlacement(World world, BlockPos pos, EnumFacing facing, float hitX, float hitY, float hitZ, int meta, EntityLivingBase placer) {
      GravityPlants.markForPlacement(world, pos, facing);
      return this.getStateFromMeta(meta);
   }

   @Override
   public AxisAlignedBB getBoundingBox(IBlockState state, IBlockAccess source, BlockPos pos) {
      int dir = GravityPlants.getPlantGravityDirection(source, pos);
      return GravityPlants.box(Math.max(dir, 0), this.W_D, this.HEIGHT_D);
   }

   @Override
   public EnumOffsetType getOffsetType() {
      return EnumOffsetType.NONE;
   }

   @Override
   public int getArchaicRenderKey(IBlockState state) {
      return this.getMetaFromState(state) | (state.getValue(FACING).getIndex() + 1) << 4;
   }

   @Override
   public net.minecraft.util.EnumFacing getArchaicRenderUp(int key) {
      int f = key >> 4;
      return f == 0 ? net.minecraft.util.EnumFacing.UP : net.minecraft.util.EnumFacing.byIndex(f - 1);
   }

   @Override
   public int getArchaicRenderType(int key) {
      return RENDER_CROSS;
   }

   protected String[] getPlantIconNames() {
      return new String[]{"minecraft:deadbush"};
   }

   @net.minecraftforge.fml.relauncher.SideOnly(net.minecraftforge.fml.relauncher.Side.CLIENT)
   private IIcon[] icons;

   @net.minecraftforge.fml.relauncher.SideOnly(net.minecraftforge.fml.relauncher.Side.CLIENT)
   @Override
   public void registerBlockIcons(IIconRegister reg) {
      String[] n = this.getPlantIconNames();
      this.icons = new IIcon[n.length];
      for (int i = 0; i < n.length; i++) {
         this.icons[i] = reg.registerIcon(n[i]);
      }
   }

   @net.minecraftforge.fml.relauncher.SideOnly(net.minecraftforge.fml.relauncher.Side.CLIENT)
   @Override
   public IIcon getIcon(int side, int key) {
      int m = this.iconIndex(key & 15);
      return this.icons[m >= 0 && m < this.icons.length ? m : 0];
   }

   protected int iconIndex(int meta) {
      return meta;
   }
}
