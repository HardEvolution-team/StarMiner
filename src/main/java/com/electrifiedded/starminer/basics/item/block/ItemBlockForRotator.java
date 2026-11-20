package com.electrifiedded.starminer.basics.item.block;

import com.electrifiedded.starminer.api.Gravity;
import com.electrifiedded.starminer.api.GravityDirection;
import com.electrifiedded.starminer.basics.Config;
import com.electrifiedded.starminer.basics.block.BlockRotator;
import com.electrifiedded.starminer.basics.tileentity.TileEntityBlockRotator;
import net.minecraft.block.Block;
import net.minecraft.block.BlockDoor;
import net.minecraft.block.BlockDoublePlant;
import net.minecraft.block.ITileEntityProvider;
import net.minecraft.block.SoundType;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemBlockSpecial;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.NonNullList;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

public class ItemBlockForRotator extends ItemBlock {
   public ItemBlockForRotator(Block block) {
      super(block);
   }

   @Override
   public EnumActionResult onItemUse(EntityPlayer player, World world, BlockPos pos, EnumHand hand, EnumFacing facing, float hitX, float hitY, float hitZ) {
      ItemStack itemStack = player.getHeldItem(hand);
      IBlockState clicked = world.getBlockState(pos);
      if (!clicked.getBlock().isReplaceable(world, pos)) {
         pos = pos.offset(facing);
      }

      if (itemStack.isEmpty()) {
         return EnumActionResult.FAIL;
      }
      if (!player.canPlayerEdit(pos, facing, itemStack)) {
         return EnumActionResult.FAIL;
      }
      if (pos.getY() == 255 && this.block.getDefaultState().getMaterial().isSolid()) {
         return EnumActionResult.FAIL;
      }
      if (!world.mayPlace(this.block, pos, false, facing, player)) {
         return EnumActionResult.FAIL;
      }

      GravityDirection gDir = Gravity.getGravityDirection(player);
      NonNullList<ItemStack> mainInv = player.inventory.mainInventory;
      Block blockToInclude = null;
      ItemStack targetStack = ItemStack.EMPTY;
      for (int slotNum = 0; slotNum < 9; slotNum++) {
         ItemStack s = mainInv.get(slotNum);
         if (s.isEmpty() || s == itemStack) {
            continue;
         }
         Block candidate = this.convertItemToBlock(s.getItem());
         if (candidate != null
            && candidate != Blocks.AIR
            && !candidate.hasTileEntity(candidate.getDefaultState())
            && !(candidate instanceof ITileEntityProvider)
            && !(candidate instanceof BlockRotator)
            && !(candidate instanceof BlockDoor)
            && !(candidate instanceof BlockDoublePlant)
            && (!Config.enableFakeRotatorOnlyVannilaBlock || this.isVannilaBlock(candidate))) {
            blockToInclude = candidate;
            targetStack = s;
            break;
         }
      }

      if (blockToInclude == null) {
         return EnumActionResult.FAIL;
      }

      int itemMeta = targetStack.getMetadata();
      EnumFacing convSide = EnumFacing.byIndex(gDir.forgeSideRot[facing.getIndex()]);
      GravityDirection dirOpposite = GravityDirection.turnWayForNormal(gDir);
      float[] conv = dirOpposite.rotateXYZAt(new float[3], hitX, hitY, hitZ, 0.5F, 0.5F, 0.5F);
      Vec3d look = dirOpposite.rotateVec3(player.getLookVec());
      float rotationPitchSaved = player.rotationPitch;
      float rotationYawSaved = player.rotationYaw;
      boolean placed = false;
      try {
         player.rotationPitch = (float)(-Math.asin(look.y) * (180.0 / Math.PI));
         player.rotationYaw = (float)(-90.0 + Math.atan2(look.z, look.x) * 180.0 / Math.PI);
         IBlockState storedState = blockToInclude.getStateForPlacement(world, pos, convSide, conv[0], conv[1], conv[2], itemMeta, player, hand);
         IBlockState rotatorState = ((BlockRotator)this.block).getDefaultState()
            .withProperty(BlockRotator.META, blockToInclude.getMetaFromState(storedState));
         if (world.setBlockState(pos, rotatorState, 3)) {
            TileEntityBlockRotator te = BlockRotator.getTileEntityBlockRotator(world, pos);
            if (te != null) {
               te.setStoredBlock(blockToInclude);
               te.setStoredItem(targetStack.getItem());
               te.setItemMetadata(targetStack.getMetadata());
               te.setGravityDirection(gDir);
               te.isSubBlock = false;
               te.relatedBlockX = pos.getX();
               te.relatedBlockY = pos.getY();
               te.relatedBlockZ = pos.getZ();
               te.markDirty();
               world.notifyBlockUpdate(pos, rotatorState, rotatorState, 3);
               world.checkLight(pos);
               placed = true;
            }
         }
      } catch (Exception e) {
         e.printStackTrace();
      } finally {
         player.rotationPitch = rotationPitchSaved;
         player.rotationYaw = rotationYawSaved;
      }

      if (placed) {
         SoundType st = this.block.getSoundType();
         world.playSound(player, pos, st.getPlaceSound(), SoundCategory.BLOCKS, (st.getVolume() + 1.0F) / 2.0F, st.getPitch() * 0.8F);
         itemStack.shrink(1);
         if (!player.capabilities.isCreativeMode) {
            targetStack.shrink(1);
         }
      }
      return EnumActionResult.SUCCESS;
   }

   private boolean isVannilaBlock(Block block) {
      return block.getClass().getName().startsWith("net.minecraft");
   }

   private Block convertItemToBlock(Item item) {
      if (item instanceof ItemBlockSpecial) {
         return ((ItemBlockSpecial)item).getBlock();
      }
      if (item instanceof ItemBlock) {
         return Block.getBlockFromItem(item);
      }
      return null;
   }
}
