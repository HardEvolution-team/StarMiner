package com.electrifiedded.starminer.basics.block;

import com.electrifiedded.archaiclib.v1710.api.block.ArchaicRenderPart;
import com.electrifiedded.archaiclib.v1710.api.block.IArchaicRenderBlock;
import com.electrifiedded.archaiclib.v1710.api.client.IIcon;
import com.electrifiedded.archaiclib.v1710.api.client.IIconRegister;
import com.electrifiedded.starminer.api.Gravity;
import com.electrifiedded.starminer.api.GravityDirection;
import com.electrifiedded.starminer.basics.dummy.DummyRotatedBlockAccess;
import com.electrifiedded.starminer.basics.tileentity.TileEntityBlockRotator;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import javax.annotation.Nullable;
import net.minecraft.block.Block;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.PropertyInteger;
import net.minecraft.block.state.BlockFaceShape;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumBlockRenderType;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class BlockRotator extends Block implements IArchaicRenderBlock {
   public static final PropertyInteger META = PropertyInteger.create("meta", 0, 15);
   public static final float EXPAND_BND_SIZE = 0.01F;

   public BlockRotator() {
      super(Material.GLASS);
      this.setHardness(1.0F);
      this.setResistance(1.0F);
      this.setLightLevel(1.0F);
      this.setSoundType(SoundType.GLASS);
      this.setDefaultState(this.blockState.getBaseState().withProperty(META, 0));
   }

   @Override
   protected BlockStateContainer createBlockState() {
      return new BlockStateContainer(this, META);
   }

   @Override
   public IBlockState getStateFromMeta(int meta) {
      return this.getDefaultState().withProperty(META, meta & 15);
   }

   @Override
   public int getMetaFromState(IBlockState state) {
      return state.getValue(META);
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
      return new TileEntityBlockRotator();
   }

   @Override
   public EnumBlockRenderType getRenderType(IBlockState state) {
      return EnumBlockRenderType.INVISIBLE;
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

   @Override
   public ItemStack getPickBlock(IBlockState state, RayTraceResult target, World world, BlockPos pos, EntityPlayer player) {
      return new ItemStack(this);
   }

   @Nullable
   public static IBlockState getStoredState(IBlockAccess world, BlockPos pos, IBlockState rotatorState) {
      TileEntityBlockRotator te = getTileEntityBlockRotator(world, pos);
      if (te == null) {
         return null;
      }
      Block block = te.getStoredBlock();
      if (block == null || block == Blocks.AIR) {
         return null;
      }
      return block.getStateFromMeta(rotatorState.getValue(META));
   }

   @Override
   public void onBlockPlacedBy(World world, BlockPos pos, IBlockState state, EntityLivingBase placer, ItemStack itemStack) {
      if (placer instanceof EntityPlayer) {
         TileEntityBlockRotator te = getTileEntityBlockRotator(world, pos);
         if (te != null) {
            te.setGravityDirection(Gravity.getGravityDirection(placer));
         }
      }
   }

   @Override
   public void neighborChanged(IBlockState state, World world, BlockPos pos, Block block, BlockPos fromPos) {
      TileEntityBlockRotator te = getTileEntityBlockRotator(world, pos);
      if (te != null && te.hasRelated()) {
         BlockPos rp = new BlockPos(te.relatedBlockX, te.relatedBlockY, te.relatedBlockZ);
         if (world.getBlockState(rp).getBlock() instanceof BlockRotator) {
            TileEntityBlockRotator relatedTe = getTileEntityBlockRotator(world, rp);
            if (relatedTe != null && relatedTe.relatedBlockX == pos.getX() && relatedTe.relatedBlockY == pos.getY() && relatedTe.relatedBlockZ == pos.getZ()) {
               return;
            }
         }
         world.setBlockToAir(pos);
      }
   }

   @Override
   public void breakBlock(World world, BlockPos pos, IBlockState state) {
      if (!world.isRemote && world.getGameRules().getBoolean("doTileDrops") && !world.restoringBlockSnapshots) {
         TileEntityBlockRotator te = getTileEntityBlockRotator(world, pos);
         if (te != null && !te.isSubBlock) {
            Item storedItem = te.getStoredItem();
            if (storedItem != null && storedItem != net.minecraft.init.Items.AIR && storedItem != net.minecraft.init.Items.CAKE) {
               ItemStack itemStack = new ItemStack(storedItem, 1, te.getItemMetadata());
               float f = 0.7F;
               double d0 = world.rand.nextFloat() * f + (1.0F - f) * 0.5;
               double d1 = world.rand.nextFloat() * f + (1.0F - f) * 0.5;
               double d2 = world.rand.nextFloat() * f + (1.0F - f) * 0.5;
               EntityItem entityitem = new EntityItem(world, pos.getX() + d0, pos.getY() + d1, pos.getZ() + d2, itemStack);
               entityitem.setDefaultPickupDelay();
               world.spawnEntity(entityitem);
            }
         }
      }
      super.breakBlock(world, pos, state);
   }

   @Override
   public boolean onBlockActivated(World world, BlockPos pos, IBlockState state, EntityPlayer player, EnumHand hand, EnumFacing facing, float hitX, float hitY, float hitZ) {
      TileEntityBlockRotator te = getTileEntityBlockRotator(world, pos);
      IBlockState stored = getStoredState(world, pos, state);
      if (te == null || stored == null) {
         return false;
      }
      try {
         NBTTagCompound saved = te.writeToNBT(new NBTTagCompound());
         IBlockState actual = stored.getActualState(new DummyRotatedBlockAccess(world, te.getGravityDirection(), pos, stored), pos);
         boolean result = stored.getBlock().onBlockActivated(world, pos, actual, player, hand, facing, hitX, hitY, hitZ);
         IBlockState now = world.getBlockState(pos);
         if (now.getBlock() == stored.getBlock()) {
            world.setBlockState(pos, this.getDefaultState().withProperty(META, stored.getBlock().getMetaFromState(now)), 3);
            TileEntity newTe = world.getTileEntity(pos);
            if (newTe != null) {
               newTe.readFromNBT(saved);
               newTe.markDirty();
               world.notifyBlockUpdate(pos, now, now, 3);
            }
         }
         return result;
      } catch (Exception e) {
         e.printStackTrace();
      }
      return false;
   }

   @Override
   public int getLightValue(IBlockState state, IBlockAccess world, BlockPos pos) {
      TileEntityBlockRotator te = getTileEntityBlockRotator(world, pos);
      IBlockState stored = te == null ? null : getStoredState(world, pos, state);
      if (stored != null) {
         return stored.getLightValue(new DummyRotatedBlockAccess(world, te.getGravityDirection(), pos, stored), pos);
      }
      return super.getLightValue(state, world, pos);
   }

   @Override
   public AxisAlignedBB getBoundingBox(IBlockState state, IBlockAccess world, BlockPos pos) {
      TileEntityBlockRotator te = getTileEntityBlockRotator(world, pos);
      IBlockState stored = te == null ? null : getStoredState(world, pos, state);
      if (stored != null) {
         IBlockState actual = stored.getActualState(new DummyRotatedBlockAccess(world, te.getGravityDirection(), pos, stored), pos);
         return GravityDirection.rotateAABBAt(te.getGravityDirection(), actual.getBoundingBox(new DummyRotatedBlockAccess(world, te.getGravityDirection(), pos, stored), pos), 0.5, 0.5, 0.5);
      }
      return FULL_BLOCK_AABB;
   }

   @Nullable
   @Override
   public AxisAlignedBB getCollisionBoundingBox(IBlockState state, IBlockAccess world, BlockPos pos) {
      TileEntityBlockRotator te = getTileEntityBlockRotator(world, pos);
      IBlockState stored = te == null ? null : getStoredState(world, pos, state);
      if (stored != null) {
         DummyRotatedBlockAccess access = new DummyRotatedBlockAccess(world, te.getGravityDirection(), pos, stored);
         AxisAlignedBB bb = stored.getActualState(access, pos).getCollisionBoundingBox(access, pos);
         return bb == null ? null : GravityDirection.rotateAABBAt(te.getGravityDirection(), bb, 0.5, 0.5, 0.5);
      }
      return FULL_BLOCK_AABB;
   }

   @Override
   public void addCollisionBoxToList(IBlockState state, World world, BlockPos pos, AxisAlignedBB entityBox, List<AxisAlignedBB> collidingBoxes, @Nullable Entity entity, boolean isActualState) {
      TileEntityBlockRotator te = getTileEntityBlockRotator(world, pos);
      IBlockState stored = te == null ? null : getStoredState(world, pos, state);
      if (stored == null) {
         super.addCollisionBoxToList(state, world, pos, entityBox, collidingBoxes, entity, isActualState);
         return;
      }
      GravityDirection dir = te.getGravityDirection();
      DummyRotatedBlockAccess access = new DummyRotatedBlockAccess(world, dir, pos, stored);
      IBlockState actual = stored.getActualState(access, pos);
      AxisAlignedBB local = DummyRotatedBlockAccess.toLocalBox(dir, entityBox, pos);
      List<AxisAlignedBB> tmp = new ArrayList<>();
      stored.getBlock().addCollisionBoxToList(actual, world, pos, local, tmp, entity, true);
      for (AxisAlignedBB bb : tmp) {
         if (bb != null) {
            AxisAlignedBB rotated = GravityDirection.rotateAABBAt(dir, bb, pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5);
            if (entityBox.intersects(rotated)) {
               collidingBoxes.add(rotated);
            }
         }
      }
   }

   @Override
   public boolean isLadder(IBlockState state, IBlockAccess world, BlockPos pos, EntityLivingBase entity) {
      TileEntityBlockRotator te = getTileEntityBlockRotator(world, pos);
      IBlockState stored = te == null ? null : getStoredState(world, pos, state);
      if (stored != null) {
         return stored.getBlock().isLadder(stored, new DummyRotatedBlockAccess(world, te.getGravityDirection(), pos, stored), pos, entity);
      }
      return false;
   }

   public static TileEntityBlockRotator getTileEntityBlockRotator(IBlockAccess world, BlockPos pos) {
      TileEntity te = world.getTileEntity(pos);
      return te instanceof TileEntityBlockRotator ? (TileEntityBlockRotator)te : null;
   }

   @SideOnly(Side.CLIENT)
   private IIcon icon;

   @SideOnly(Side.CLIENT)
   @Override
   public void registerBlockIcons(IIconRegister reg) {
      this.icon = reg.registerIcon("starminer:blocks/rotator_yellow");
   }

   @SideOnly(Side.CLIENT)
   @Override
   public IIcon getIcon(int side, int key) {
      return this.icon;
   }

   @SideOnly(Side.CLIENT)
   @Override
   public List<ArchaicRenderPart> getArchaicRenderParts(int key) {
      if (key == 16) {
         return Collections.singletonList(ArchaicRenderPart.box(FULL_BLOCK_AABB, this.icon).renderAllFaces());
      }
      return Collections.emptyList();
   }

   @Override
   public int getArchaicItemRenderKey(int meta) {
      return 16;
   }
}
