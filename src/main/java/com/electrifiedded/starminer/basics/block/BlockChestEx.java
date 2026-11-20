package com.electrifiedded.starminer.basics.block;

import com.electrifiedded.archaiclib.v1710.api.block.ArchaicRenderPart;
import com.electrifiedded.archaiclib.v1710.api.block.IArchaicRenderBlock;
import com.electrifiedded.archaiclib.v1710.api.client.IIcon;
import com.electrifiedded.archaiclib.v1710.api.client.IIconRegister;
import com.electrifiedded.starminer.api.Gravity;
import com.electrifiedded.starminer.api.GravityDirection;
import com.electrifiedded.starminer.basics.tileentity.TileEntityChestEx;
import java.util.Collections;
import java.util.List;
import net.minecraft.block.Block;
import net.minecraft.block.BlockContainer;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.PropertyDirection;
import net.minecraft.block.state.BlockFaceShape;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.InventoryHelper;
import net.minecraft.inventory.InventoryLargeChest;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumBlockRenderType;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.ILockableContainer;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class BlockChestEx extends BlockContainer implements IArchaicRenderBlock {
   public static final PropertyDirection FACING = PropertyDirection.create("facing", EnumFacing.Plane.HORIZONTAL);
   private static final AxisAlignedBB DEFAULT_BOX = new AxisAlignedBB(0.0625, 0.0, 0.0625, 0.9375, 0.875, 0.9375);
   public final int chestType;

   public BlockChestEx(int type) {
      super(Material.WOOD);
      this.chestType = type;
      this.setHardness(2.5F);
      this.setSoundType(SoundType.WOOD);
      this.setDefaultState(this.blockState.getBaseState().withProperty(FACING, EnumFacing.NORTH));
   }

   @Override
   protected BlockStateContainer createBlockState() {
      return new BlockStateContainer(this, FACING);
   }

   @Override
   public IBlockState getStateFromMeta(int meta) {
      EnumFacing f = EnumFacing.byIndex(meta);
      if (f.getAxis() == EnumFacing.Axis.Y) {
         f = EnumFacing.NORTH;
      }
      return this.getDefaultState().withProperty(FACING, f);
   }

   @Override
   public int getMetaFromState(IBlockState state) {
      return state.getValue(FACING).getIndex();
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
   public EnumBlockRenderType getRenderType(IBlockState state) {
      return EnumBlockRenderType.INVISIBLE;
   }

   @Override
   public BlockFaceShape getBlockFaceShape(IBlockAccess worldIn, IBlockState state, BlockPos pos, EnumFacing face) {
      return BlockFaceShape.UNDEFINED;
   }

   @Override
   public AxisAlignedBB getBoundingBox(IBlockState state, IBlockAccess world, BlockPos pos) {
      TileEntityChestEx te = this.getTileEntityChestEx(world, pos);
      if (te == null) {
         return DEFAULT_BOX;
      }
      GravityDirection gDir = te.getGravityDirection();
      GravityDirection dirOpposite = GravityDirection.turnWayForNormal(gDir);
      AxisAlignedBB aabb = DEFAULT_BOX;
      int px = pos.getX();
      int py = pos.getY();
      int pz = pos.getZ();
      if (te.hasRelated()) {
         int[] conv = new int[3];
         dirOpposite.rotateXYZAt(conv, te.relatedBlockX, te.relatedBlockY, te.relatedBlockZ, px, py, pz);
         if (this.conpareXYZ(conv, px, py, pz - 1)) {
            aabb = new AxisAlignedBB(0.0625, 0.0, 0.0, 0.9375, 0.875, 0.9375);
         } else if (this.conpareXYZ(conv, px, py, pz + 1)) {
            aabb = new AxisAlignedBB(0.0625, 0.0, 0.0625, 0.9375, 0.875, 1.0);
         } else if (this.conpareXYZ(conv, px - 1, py, pz)) {
            aabb = new AxisAlignedBB(0.0, 0.0, 0.0625, 0.9375, 0.875, 0.9375);
         } else if (this.conpareXYZ(conv, px + 1, py, pz)) {
            aabb = new AxisAlignedBB(0.0625, 0.0, 0.0625, 1.0, 0.875, 0.9375);
         }
      }
      return GravityDirection.rotateAABBAt(gDir, aabb, 0.5, 0.5, 0.5);
   }

   @Override
   public void onBlockPlacedBy(World world, BlockPos pos, IBlockState state, EntityLivingBase placer, ItemStack itemStack) {
      GravityDirection gDir = Gravity.getGravityDirection(placer);
      int parX = pos.getX();
      int parY = pos.getY();
      int parZ = pos.getZ();
      int[] conv = new int[3];
      gDir.rotateXYZAt(conv, parX, parY, parZ - 1, parX, parY, parZ);
      Block blockNorth = world.getBlockState(new BlockPos(conv[0], conv[1], conv[2])).getBlock();
      boolean canConbBlockNorth = this.canConbineWith(gDir, world, blockNorth, new BlockPos(conv[0], conv[1], conv[2]));
      gDir.rotateXYZAt(conv, parX, parY, parZ + 1, parX, parY, parZ);
      Block blockSouth = world.getBlockState(new BlockPos(conv[0], conv[1], conv[2])).getBlock();
      boolean canConbBlockSouth = this.canConbineWith(gDir, world, blockSouth, new BlockPos(conv[0], conv[1], conv[2]));
      gDir.rotateXYZAt(conv, parX - 1, parY, parZ, parX, parY, parZ);
      Block blockWest = world.getBlockState(new BlockPos(conv[0], conv[1], conv[2])).getBlock();
      boolean canConbBlockWest = this.canConbineWith(gDir, world, blockWest, new BlockPos(conv[0], conv[1], conv[2]));
      gDir.rotateXYZAt(conv, parX + 1, parY, parZ, parX, parY, parZ);
      Block blockEast = world.getBlockState(new BlockPos(conv[0], conv[1], conv[2])).getBlock();
      boolean canConbBlockEast = this.canConbineWith(gDir, world, blockEast, new BlockPos(conv[0], conv[1], conv[2]));
      int direction4 = this.getGravityFixedDirectionFromEntity(placer);
      int meta = 2;
      if (direction4 == 1) {
         meta = 5;
      }
      if (direction4 == 2) {
         meta = 3;
      }
      if (direction4 == 3) {
         meta = 4;
      }

      if (!canConbBlockNorth && !canConbBlockSouth && !canConbBlockWest && !canConbBlockEast) {
         this.setRelatedWithThis(world, pos, pos, gDir, false);
         this.setMeta(world, pos, meta);
      } else {
         if (canConbBlockNorth) {
            if (meta == 2 || meta == 3) {
               meta = blockWest == Blocks.AIR ? 4 : 5;
            }
            gDir.rotateXYZAt(conv, parX, parY, parZ - 1, parX, parY, parZ);
         } else if (canConbBlockSouth) {
            if (meta == 2 || meta == 3) {
               meta = blockEast == Blocks.AIR ? 5 : 4;
            }
            gDir.rotateXYZAt(conv, parX, parY, parZ + 1, parX, parY, parZ);
         } else if (canConbBlockWest) {
            if (meta == 5 || meta == 4) {
               meta = blockNorth == Blocks.AIR ? 2 : 3;
            }
            gDir.rotateXYZAt(conv, parX - 1, parY, parZ, parX, parY, parZ);
         } else {
            if (meta == 5 || meta == 4) {
               meta = blockSouth == Blocks.AIR ? 3 : 2;
            }
            gDir.rotateXYZAt(conv, parX + 1, parY, parZ, parX, parY, parZ);
         }

         BlockPos other = new BlockPos(conv[0], conv[1], conv[2]);
         this.setRelatedWithThis(world, other, pos, gDir, false);
         this.setRelatedWithThis(world, pos, other, gDir, true);
         this.setMeta(world, other, meta);
         this.setMeta(world, pos, meta);
      }

      if (itemStack.hasDisplayName()) {
         TileEntityChestEx te = this.getTileEntityChestEx(world, pos);
         if (te != null) {
            te.setCustomName(itemStack.getDisplayName());
         }
      }
   }

   private void setMeta(World world, BlockPos pos, int meta) {
      world.setBlockState(pos, this.getStateFromMeta(meta), 3);
   }

   private void setRelatedWithThis(IBlockAccess world, BlockPos target, BlockPos related, GravityDirection gDir, boolean isSubBlock) {
      TileEntityChestEx te = this.getTileEntityChestEx(world, target);
      if (te == null) {
         return;
      }
      te.relatedBlockX = related.getX();
      te.relatedBlockY = related.getY();
      te.relatedBlockZ = related.getZ();
      te.isSubBlock = isSubBlock;
      te.setGravityDirection(gDir);
      te.markDirty();
   }

   @Override
   public boolean canPlaceBlockAt(World worldIn, BlockPos pos) {
      return true;
   }

   @Override
   public void neighborChanged(IBlockState state, World world, BlockPos pos, Block block, BlockPos fromPos) {
      TileEntityChestEx te = this.getTileEntityChestEx(world, pos);
      if (te != null) {
         BlockPos rp = te.getRelatedPos();
         Block relatedBlock = world.getBlockState(rp).getBlock();
         if (relatedBlock != Blocks.AIR) {
            TileEntityChestEx relatedTe = this.getTileEntityChestEx(world, rp);
            if (relatedTe == null || relatedTe.relatedBlockX != pos.getX() || relatedTe.relatedBlockY != pos.getY() || relatedTe.relatedBlockZ != pos.getZ()) {
               this.detach(world, pos, te, state);
            }
         } else {
            this.detach(world, pos, te, state);
         }
      }
   }

   private void detach(World world, BlockPos pos, TileEntityChestEx te, IBlockState state) {
      if (te.relatedBlockX == pos.getX() && te.relatedBlockY == pos.getY() && te.relatedBlockZ == pos.getZ() && !te.isSubBlock) {
         return;
      }
      te.relatedBlockX = pos.getX();
      te.relatedBlockY = pos.getY();
      te.relatedBlockZ = pos.getZ();
      te.isSubBlock = false;
      te.markDirty();
      world.notifyBlockUpdate(pos, state, state, 3);
   }

   @Override
   public void breakBlock(World world, BlockPos pos, IBlockState state) {
      TileEntity te = world.getTileEntity(pos);
      if (te instanceof TileEntityChestEx) {
         InventoryHelper.dropInventoryItems(world, pos, (TileEntityChestEx)te);
         world.updateComparatorOutputLevel(pos, this);
      }
      super.breakBlock(world, pos, state);
   }

   @Override
   public boolean onBlockActivated(World world, BlockPos pos, IBlockState state, EntityPlayer player, EnumHand hand, EnumFacing facing, float hitX, float hitY, float hitZ) {
      if (world.isRemote) {
         return true;
      }
      ILockableContainer inv = this.conInventory(world, pos);
      if (inv != null) {
         player.displayGUIChest(inv);
      }
      return true;
   }

   public ILockableContainer conInventory(World world, BlockPos pos) {
      TileEntityChestEx te = this.getTileEntityChestEx(world, pos);
      if (te == null) {
         return null;
      }
      BlockPos rp = te.getRelatedPos();
      if (!te.hasRelated() || world.getBlockState(rp).getBlock() != this) {
         return te;
      }
      TileEntityChestEx other = this.getTileEntityChestEx(world, rp);
      if (other == null) {
         return te;
      }
      return te.isSubBlock
         ? new InventoryLargeChest("container.chestDouble", te, other)
         : new InventoryLargeChest("container.chestDouble", other, te);
   }

   @Override
   public TileEntity createNewTileEntity(World worldIn, int meta) {
      return new TileEntityChestEx();
   }

   @Override
   public boolean canProvidePower(IBlockState state) {
      return this.chestType == 1;
   }

   @Override
   public int getWeakPower(IBlockState blockState, IBlockAccess world, BlockPos pos, EnumFacing side) {
      if (!this.canProvidePower(blockState)) {
         return 0;
      }
      TileEntityChestEx te = this.getTileEntityChestEx(world, pos);
      return te == null ? 0 : MathHelper.clamp(te.numPlayersUsing, 0, 15);
   }

   @Override
   public int getStrongPower(IBlockState blockState, IBlockAccess world, BlockPos pos, EnumFacing side) {
      return side == EnumFacing.UP ? this.getWeakPower(blockState, world, pos, side) : 0;
   }

   @Override
   public boolean hasComparatorInputOverride(IBlockState state) {
      return true;
   }

   @Override
   public int getComparatorInputOverride(IBlockState state, World world, BlockPos pos) {
      ILockableContainer inv = this.conInventory(world, pos);
      return inv == null ? 0 : Container.calcRedstoneFromInventory(inv);
   }

   private TileEntityChestEx getTileEntityChestEx(IBlockAccess world, BlockPos pos) {
      TileEntity te = world.getTileEntity(pos);
      return te instanceof TileEntityChestEx ? (TileEntityChestEx)te : null;
   }

   private boolean conpareXYZ(int[] conv, int parX, int parY, int parZ) {
      return conv[0] == parX && conv[1] == parY && conv[2] == parZ;
   }

   private int getGravityFixedDirectionFromEntity(EntityLivingBase entityLivingBase) {
      GravityDirection gDir = Gravity.getGravityDirection(entityLivingBase);
      Vec3d look = entityLivingBase.getLookVec();
      look = GravityDirection.turnWayForNormal(gDir).rotateVec3(look);
      double yaw = -90.0 + Math.atan2(look.z, look.x) * 180.0 / Math.PI;
      return MathHelper.floor(yaw * 4.0 / 360.0 + 0.5) & 3;
   }

   public boolean canConbineWith(GravityDirection gDir, IBlockAccess world, Block block, BlockPos target) {
      if (block != this) {
         return false;
      }
      TileEntityChestEx te = this.getTileEntityChestEx(world, target);
      return te != null && !te.hasRelated() && te.getGravityDirection() == gDir;
   }

   public boolean isConbinedFrom(GravityDirection gDir, IBlockAccess world, Block block, BlockPos target, BlockPos self) {
      if (block != this) {
         return false;
      }
      TileEntityChestEx te = this.getTileEntityChestEx(world, target);
      return te != null && te.hasRelated() && te.getGravityDirection() == gDir
         && te.relatedBlockX == self.getX() && te.relatedBlockY == self.getY() && te.relatedBlockZ == self.getZ();
   }

   @SideOnly(Side.CLIENT)
   private IIcon icon;

   @SideOnly(Side.CLIENT)
   @Override
   public void registerBlockIcons(IIconRegister reg) {
      this.icon = reg.registerIcon("minecraft:blocks/planks_oak");
   }

   @SideOnly(Side.CLIENT)
   @Override
   public IIcon getIcon(int side, int key) {
      return this.icon;
   }

   @SideOnly(Side.CLIENT)
   @Override
   public List<ArchaicRenderPart> getArchaicRenderParts(int key) {
      if (key == 1) {
         return Collections.singletonList(ArchaicRenderPart.box(DEFAULT_BOX, this.icon).renderAllFaces());
      }
      return Collections.emptyList();
   }

   @Override
   public int getArchaicItemRenderKey(int meta) {
      return 1;
   }
}
