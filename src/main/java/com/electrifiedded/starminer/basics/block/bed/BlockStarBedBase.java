package com.electrifiedded.starminer.basics.block.bed;

import java.util.List;
import java.util.Random;
import javax.annotation.Nullable;
import com.electrifiedded.archaiclib.v1710.api.block.ArchaicRenderPart;
import com.electrifiedded.archaiclib.v1710.api.block.IArchaicRenderBlock;
import com.electrifiedded.archaiclib.v1710.api.client.IIcon;
import com.electrifiedded.archaiclib.v1710.api.client.IIconRegister;
import com.electrifiedded.starminer.basics.Port2Registry;
import net.minecraft.block.Block;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.EnumPushReaction;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.BlockFaceShape;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Biomes;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.BlockRenderLayer;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public abstract class BlockStarBedBase extends Block implements IArchaicRenderBlock {
   protected BlockStarBedBase() {
      super(Material.CLOTH);
      this.setHardness(0.2F);
      this.setSoundType(SoundType.CLOTH);
      this.disableStats();
   }

   protected abstract boolean isHead();

   public abstract int getGravityDirection(IBlockAccess world, BlockPos pos, IBlockState state);

   protected abstract BlockPos getHeadPos(IBlockAccess world, BlockPos pos, IBlockState state);

   @Override
   public boolean isBed(IBlockState state, IBlockAccess world, BlockPos pos, @Nullable Entity player) {
      return true;
   }

   @Override
   public EnumFacing getBedDirection(IBlockState state, IBlockAccess world, BlockPos pos) {
      return EnumFacing.SOUTH;
   }

   @Override
   public boolean isBedFoot(IBlockAccess world, BlockPos pos) {
      return !this.isHead();
   }

   @Override
   public boolean onBlockActivated(World world, BlockPos pos, IBlockState state, EntityPlayer player, EnumHand hand, EnumFacing facing, float hitX, float hitY, float hitZ) {
      if (world.isRemote) {
         return true;
      }

      BlockPos hp = this.getHeadPos(world, pos, state);
      if (hp == null || world.getBlockState(hp).getBlock() != Port2Registry.StarBedHeadBlock) {
         return true;
      }

      IBlockState hs = world.getBlockState(hp);
      if (world.provider.canRespawnHere() && world.getBiome(hp) != Biomes.HELL) {
         if (hs.getValue(BlockStarBedHead.OCCUPIED)) {
            EntityPlayer other = null;
            for (EntityPlayer p : world.playerEntities) {
               if (p.isPlayerSleeping() && hp.equals(p.bedLocation)) {
                  other = p;
               }
            }

            if (other != null) {
               player.sendStatusMessage(new TextComponentTranslation("tile.bed.occupied"), true);
               return true;
            }

            BlockStarBedHead.setBedOccupiedHead(world, hp, false);
         }

         EntityPlayer.SleepResult res = player.trySleep(hp);
         if (res == EntityPlayer.SleepResult.OK) {
            BlockStarBedHead.setBedOccupiedHead(world, hp, true);
         } else if (res == EntityPlayer.SleepResult.NOT_POSSIBLE_NOW) {
            player.sendStatusMessage(new TextComponentTranslation("tile.bed.noSleep"), true);
         } else if (res == EntityPlayer.SleepResult.NOT_SAFE) {
            player.sendStatusMessage(new TextComponentTranslation("tile.bed.notSafe"), true);
         }

         return true;
      } else {
         world.setBlockToAir(hp);
         world.newExplosion(null, hp.getX() + 0.5, hp.getY() + 0.5, hp.getZ() + 0.5, 8.0F, true, true);
         return true;
      }
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
   public AxisAlignedBB getBoundingBox(IBlockState state, IBlockAccess source, BlockPos pos) {
      return StarBedUtil.boxFor(this.getGravityDirection(source, pos, state));
   }

   @Override
   public EnumPushReaction getPushReaction(IBlockState state) {
      return EnumPushReaction.DESTROY;
   }

   @Override
   public Item getItemDropped(IBlockState state, Random rand, int fortune) {
      return Port2Registry.StarBedItem;
   }

   @Override
   public int damageDropped(IBlockState state) {
      return 0;
   }

   @SideOnly(Side.CLIENT)
   @Override
   public ItemStack getItem(World worldIn, BlockPos pos, IBlockState state) {
      return new ItemStack(Port2Registry.StarBedItem);
   }

   @SideOnly(Side.CLIENT)
   @Override
   public BlockRenderLayer getRenderLayer() {
      return BlockRenderLayer.CUTOUT;
   }


   @SideOnly(Side.CLIENT)
   protected IIcon topIcon;
   @SideOnly(Side.CLIENT)
   protected IIcon endIcon;
   @SideOnly(Side.CLIENT)
   protected IIcon sideIcon;
   @SideOnly(Side.CLIENT)
   protected IIcon starIcon;
   @SideOnly(Side.CLIENT)
   protected IIcon planksIcon;

   @SideOnly(Side.CLIENT)
   @Override
   public void registerBlockIcons(IIconRegister reg) {
      String n = this.isHead() ? "starminer:starbed_head_" : "starminer:starbed_feet_";
      this.topIcon = reg.registerIcon(n + "top");
      this.endIcon = reg.registerIcon(n + "end");
      this.sideIcon = reg.registerIcon(n + "side");
      this.planksIcon = reg.registerIcon("minecraft:planks_oak");
      this.starIcon = this.isHead() ? null : reg.registerIcon("starminer:bedstar");
   }

   @SideOnly(Side.CLIENT)
   @Override
   public IIcon getIcon(int side, int key) {
      return this.topIcon;
   }

   @SideOnly(Side.CLIENT)
   @Override
   public List<ArchaicRenderPart> getArchaicRenderParts(int key) {
      return StarBedModel.build(key, this.isHead(), this.topIcon, this.endIcon, this.sideIcon, this.starIcon, this.planksIcon);
   }

   @Override
   public int getArchaicItemRenderKey(int meta) {
      return StarBedModel.KEY_NONE;
   }
}
