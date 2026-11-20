package com.electrifiedded.starminer.basics.block;

import com.electrifiedded.archaiclib.v1710.api.block.IArchaicRenderBlock;
import com.electrifiedded.archaiclib.v1710.api.client.IIcon;
import com.electrifiedded.archaiclib.v1710.api.client.IIconRegister;

import com.electrifiedded.starminer.basics.Config;
import com.electrifiedded.starminer.basics.SMModContainer;
import com.electrifiedded.starminer.basics.entity.EntityStarSquid;
import com.electrifiedded.starminer.basics.tileentity.TileEntityNavigator;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.BlockFaceShape;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.SoundEvents;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.BlockRenderLayer;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class BlockManBazooka extends Block implements IArchaicRenderBlock {
   private static final AxisAlignedBB[] BOXES = new AxisAlignedBB[6];

   static {
      for (int i = 0; i < 6; i++) {
         BOXES[i] = SMDir.slab(i, 0.2);
      }
   }

   public BlockManBazooka() {
      super(Material.ROCK);
      this.setHardness(2.0F);
      this.setResistance(50.0F);
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
      return BlockRenderLayer.CUTOUT;
   }

   @Override
   public BlockFaceShape getBlockFaceShape(IBlockAccess worldIn, IBlockState state, BlockPos pos, EnumFacing face) {
      return SMDir.ATTACH[state.getValue(SMDir.DIR)] == face ? BlockFaceShape.SOLID : BlockFaceShape.UNDEFINED;
   }

   @Override
   public AxisAlignedBB getCollisionBoundingBox(IBlockState state, IBlockAccess worldIn, BlockPos pos) {
      return BOXES[state.getValue(SMDir.DIR)];
   }

   @Override
   public boolean onBlockActivated(World world, BlockPos pos, IBlockState state, EntityPlayer player, EnumHand hand, EnumFacing facing, float hitX, float hitY, float hitZ) {
      int dir = state.getValue(SMDir.DIR);
      Vec3d shootPoint = getSpawnPoint(world, pos, dir);
      if (shootPoint == null) {
         return true;
      }

      if (!world.isRemote) {
         EntityStarSquid mob = new EntityStarSquid(world, true);
         mob.setLocationAndAngles(shootPoint.x, shootPoint.y, shootPoint.z, player.rotationYaw, player.rotationPitch);
         TileEntityNavigator teNavi = null;
         search:
         for (BlockPos p : BlockPos.getAllInBoxMutable(pos.add(-1, -1, -1), pos.add(1, 1, 1))) {
            IBlockState s = world.getBlockState(p);
            if (s.getBlock() == SMModContainer.NavigatorBlock && ((BlockNavigator)SMModContainer.NavigatorBlock).isOn(s)) {
               TileEntity te = world.getTileEntity(p);
               if (te instanceof TileEntityNavigator) {
                  teNavi = (TileEntityNavigator)te;
                  break search;
               }
            }
         }

         if (teNavi != null) {
            mob.motionX = teNavi.lookX * Config.bazookaStartSpeed;
            mob.motionY = teNavi.lookY * Config.bazookaStartSpeed;
            mob.motionZ = teNavi.lookZ * Config.bazookaStartSpeed;
         } else {
            EnumFacing out = SMDir.ATTACH[dir].getOpposite();
            mob.motionX = out.getXOffset() * 1.3;
            mob.motionY = out.getYOffset() * 1.3;
            mob.motionZ = out.getZOffset() * 1.3;
         }

         world.spawnEntity(mob);
         player.startRiding(mob, true);
      } else {
         world.playSound(player, pos, SoundEvents.ENTITY_FIREWORK_LARGE_BLAST, SoundCategory.BLOCKS, 0.8F, 1.2F);
      }

      return true;
   }

   private static Vec3d getSpawnPoint(World world, BlockPos pos, int dir) {
      EnumFacing out = SMDir.ATTACH[dir].getOpposite();
      for (int i = 5; i >= 1; i--) {
         BlockPos p = pos.offset(out, i);
         if (world.isAirBlock(p)) {
            return new Vec3d(p.getX() + 0.5, p.getY() + 0.5, p.getZ() + 0.5);
         }
      }

      return null;
   }

   @SideOnly(Side.CLIENT)
   private IIcon icon;
   @SideOnly(Side.CLIENT)
   private IIcon iconCauldron;
   @SideOnly(Side.CLIENT)
   private IIcon iconTnt;
   @SideOnly(Side.CLIENT)
   private IIcon iconIron;
   @SideOnly(Side.CLIENT)
   private IIcon iconStone;

   @SideOnly(Side.CLIENT)
   @Override
   public void registerBlockIcons(IIconRegister reg) {
      this.icon = reg.registerIcon("starminer:dokan");
      this.iconCauldron = reg.registerIcon("minecraft:blocks/cauldron_inner");
      this.iconTnt = reg.registerIcon("minecraft:blocks/tnt_side");
      this.iconIron = reg.registerIcon("minecraft:blocks/nether_brick");
      this.iconStone = reg.registerIcon("minecraft:blocks/stonebrick");
   }

   @SideOnly(Side.CLIENT)
   @Override
   public IIcon getIcon(int side, int key) {
      return this.iconStone;
   }

   @SideOnly(Side.CLIENT)
   @Override
   public java.util.List<com.electrifiedded.archaiclib.v1710.api.block.ArchaicRenderPart> getArchaicRenderParts(int key) {
      java.util.List<com.electrifiedded.archaiclib.v1710.api.block.ArchaicRenderPart> parts = new java.util.ArrayList<>();
      double mainHight = 1.2;
      double tNTHight = 0.4;
      double sleeveHight = 0.1;
      double bodyHight = mainHight - sleeveHight;
      double marginLw = 0.1;
      double marginHi = 1.0 - marginLw;
      double inbound = 0.1;
      double lo = marginLw + inbound;
      double hi = marginHi - inbound;
      double[][] side = {
         {hi, marginHi, lo, marginHi}, {hi, marginLw, lo, marginLw},
         {marginLw, hi, marginLw, lo}, {marginHi, hi, marginHi, lo},
         {lo, marginHi, marginLw, hi}, {marginHi, hi, hi, marginHi},
         {lo, marginLw, marginLw, lo}, {marginHi, lo, hi, marginLw}
      };
      double[][] bands = {{tNTHight, bodyHight}, {0.0, tNTHight}, {bodyHight, mainHight}};
      com.electrifiedded.archaiclib.v1710.api.client.IIcon[] bandIcons = {this.iconCauldron, this.iconTnt, this.iconIron};
      float[][] bandV = {{16.0F, 0.0F}, {2.0F, 4.0F}, {2.0F, 4.0F}};
      for (double[] s : side) {
         for (int b = 0; b < 3; b++) {
            double y0 = bands[b][0];
            double y1 = bands[b][1];
            float vb = bandV[b][0];
            float vt = bandV[b][1];
            parts.add(com.electrifiedded.archaiclib.v1710.api.block.ArchaicRenderPart.quad(bandIcons[b],
               new double[]{s[0], y0, s[1], 16.0, vb},
               new double[]{s[0], y1, s[1], 16.0, vt},
               new double[]{s[2], y1, s[3], 0.0, vt},
               new double[]{s[2], y0, s[3], 0.0, vb}).doubleSided());
         }
      }
      double lw = 0.2;
      double tntSize = 0.35;
      parts.add(com.electrifiedded.archaiclib.v1710.api.block.ArchaicRenderPart.box(0.0, 0.0, 0.0, 1.0, lw, 1.0, this.iconStone));
      parts.add(com.electrifiedded.archaiclib.v1710.api.block.ArchaicRenderPart.box(
         0.5 - tntSize, 0.05, 0.5 - tntSize, 0.5 + tntSize, 0.05 + tntSize, 0.5 + tntSize, this.iconTnt));
      return parts;
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
