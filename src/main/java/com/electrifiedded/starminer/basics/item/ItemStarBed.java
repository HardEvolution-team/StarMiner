package com.electrifiedded.starminer.basics.item;

import com.electrifiedded.starminer.api.Gravity;
import com.electrifiedded.starminer.api.GravityDirection;
import com.electrifiedded.starminer.basics.Port2Registry;
import com.electrifiedded.starminer.basics.block.bed.BlockStarBedBody;
import com.electrifiedded.starminer.basics.block.bed.BlockStarBedHead;
import com.electrifiedded.starminer.basics.block.bed.StarBedUtil;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

public class ItemStarBed extends Item {
   public ItemStarBed() {
      this.setMaxStackSize(1);
   }

   @Override
   public EnumActionResult onItemUse(EntityPlayer player, World world, BlockPos pos, EnumHand hand, EnumFacing facing, float hitX, float hitY, float hitZ) {
      if (world.isRemote) {
         return EnumActionResult.SUCCESS;
      }

      ItemStack stack = player.getHeldItem(hand);
      Gravity gravity = Gravity.getGravityProp(player);
      GravityDirection gd = gravity == null ? GravityDirection.upTOdown_YN : gravity.gravityDirection;
      Vec3d look = player.getLook(1.0F);
      int gravityDir;
      int connectDir;
      switch (gd) {
         case northTOsouth_ZP:
            gravityDir = 5;
            connectDir = Math.abs(look.x) > Math.abs(look.y) ? (look.x > 0.0 ? 2 : 3) : (look.y > 0.0 ? 0 : 1);
            break;
         case southTOnorth_ZN:
            gravityDir = 4;
            connectDir = Math.abs(look.x) > Math.abs(look.y) ? (look.x > 0.0 ? 2 : 3) : (look.y > 0.0 ? 0 : 1);
            break;
         case westTOeast_XP:
            gravityDir = 3;
            connectDir = Math.abs(look.z) > Math.abs(look.y) ? (look.z > 0.0 ? 4 : 5) : (look.y > 0.0 ? 0 : 1);
            break;
         case eastTOwest_XN:
            gravityDir = 2;
            connectDir = Math.abs(look.z) > Math.abs(look.y) ? (look.z > 0.0 ? 4 : 5) : (look.y > 0.0 ? 0 : 1);
            break;
         case downTOup_YP:
            gravityDir = 1;
            connectDir = Math.abs(look.z) > Math.abs(look.x) ? (look.z > 0.0 ? 4 : 5) : (look.x > 0.0 ? 2 : 3);
            break;
         default:
            gravityDir = 0;
            connectDir = Math.abs(look.z) > Math.abs(look.x) ? (look.z > 0.0 ? 4 : 5) : (look.x > 0.0 ? 2 : 3);
      }

      EnumFacing ground = com.electrifiedded.starminer.basics.block.SMDir.ATTACH[gravityDir];
      EnumFacing needSide = ground.getOpposite();
      if (facing != needSide) {
         return EnumActionResult.FAIL;
      }

      BlockPos bodyPos = pos.offset(needSide);
      BlockPos headPos = StarBedUtil.offset(bodyPos, connectDir);
      if (!player.canPlayerEdit(bodyPos, facing, stack) || !player.canPlayerEdit(headPos, facing, stack)) {
         return EnumActionResult.FAIL;
      }

      if (world.isAirBlock(bodyPos)
         && world.isAirBlock(headPos)
         && world.isSideSolid(bodyPos.offset(ground), needSide, false)
         && world.isSideSolid(headPos.offset(ground), needSide, false)) {
         IBlockState body = Port2Registry.StarBedBodyBlock.getDefaultState().withProperty(BlockStarBedBody.CONN, connectDir);
         world.setBlockState(bodyPos, body, 3);
         if (world.getBlockState(bodyPos).getBlock() == Port2Registry.StarBedBodyBlock) {
            IBlockState head = Port2Registry.StarBedHeadBlock.getDefaultState().withProperty(BlockStarBedHead.GRAV, gravityDir);
            world.setBlockState(headPos, head, 3);
         }

         stack.shrink(1);
         return EnumActionResult.SUCCESS;
      }

      return EnumActionResult.FAIL;
   }
}
