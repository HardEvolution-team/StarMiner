package com.electrifiedded.starminer.basics.dummy;

import com.electrifiedded.starminer.api.GravityDirection;
import net.minecraft.block.state.IBlockState;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.WorldType;
import net.minecraft.world.biome.Biome;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class DummyRotatedBlockAccess implements IBlockAccess {
   private final IBlockAccess world;
   private final GravityDirection dir;
   private final BlockPos center;
   private final IBlockState centerState;

   public DummyRotatedBlockAccess(IBlockAccess world, GravityDirection dir, BlockPos center, IBlockState centerState) {
      this.world = world;
      this.dir = dir;
      this.center = center;
      this.centerState = centerState;
   }

   public BlockPos toReal(BlockPos p) {
      int[] r = this.dir.rotateXYZAt(new int[3], p.getX(), p.getY(), p.getZ(), this.center.getX(), this.center.getY(), this.center.getZ());
      return new BlockPos(r[0], r[1], r[2]);
   }

   public EnumFacing toReal(EnumFacing f) {
      Vec3d v = this.dir.toWorld(new Vec3d(f.getXOffset(), f.getYOffset(), f.getZOffset()));
      return EnumFacing.getFacingFromVector((float)v.x, (float)v.y, (float)v.z);
   }

   @Override
   public TileEntity getTileEntity(BlockPos pos) {
      return pos.equals(this.center) ? null : this.world.getTileEntity(this.toReal(pos));
   }

   @SideOnly(Side.CLIENT)
   @Override
   public int getCombinedLight(BlockPos pos, int lightValue) {
      return this.world.getCombinedLight(this.toReal(pos), lightValue);
   }

   @Override
   public IBlockState getBlockState(BlockPos pos) {
      return pos.equals(this.center) ? this.centerState : this.world.getBlockState(this.toReal(pos));
   }

   @Override
   public boolean isAirBlock(BlockPos pos) {
      return this.getBlockState(pos).getBlock().isAir(this.getBlockState(pos), this, pos);
   }

   @SideOnly(Side.CLIENT)
   @Override
   public Biome getBiome(BlockPos pos) {
      return this.world.getBiome(this.toReal(pos));
   }

   @Override
   public int getStrongPower(BlockPos pos, EnumFacing direction) {
      return this.world.getStrongPower(this.toReal(pos), this.toReal(direction));
   }

   @SideOnly(Side.CLIENT)
   @Override
   public WorldType getWorldType() {
      return this.world.getWorldType();
   }

   @Override
   public boolean isSideSolid(BlockPos pos, EnumFacing side, boolean _default) {
      if (pos.equals(this.center)) {
         return this.centerState.isSideSolid(this, pos, side);
      }
      return this.world.isSideSolid(this.toReal(pos), this.toReal(side), _default);
   }

   public static AxisAlignedBB toLocalBox(GravityDirection dir, AxisAlignedBB bb, BlockPos center) {
      double cx = center.getX() + 0.5;
      double cy = center.getY() + 0.5;
      double cz = center.getZ() + 0.5;
      double ax = bb.minX - cx, ay = bb.minY - cy, az = bb.minZ - cz;
      double bx = bb.maxX - cx, by = bb.maxY - cy, bz = bb.maxZ - cz;
      return new AxisAlignedBB(
         dir.toLocalX(ax, ay, az) + cx, dir.toLocalY(ax, ay, az) + cy, dir.toLocalZ(ax, ay, az) + cz,
         dir.toLocalX(bx, by, bz) + cx, dir.toLocalY(bx, by, bz) + cy, dir.toLocalZ(bx, by, bz) + cz);
   }
}
