package com.electrifiedded.starminer.basics.item;

import java.util.List;
import javax.annotation.Nullable;
import com.electrifiedded.starminer.StarMiner;
import com.electrifiedded.starminer.basics.SMModContainer;
import com.electrifiedded.starminer.basics.tileentity.TileEntityGravityGenerator;
import net.minecraft.client.resources.I18n;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ActionResult;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class ItemStarContoler extends Item {
   public static final double DISTANCE_MAX = 64.0;
   public static final String TARGETX_NBT_TAG = "stmn.tX";
   public static final String TARGETY_NBT_TAG = "stmn.tY";
   public static final String TARGETZ_NBT_TAG = "stmn.tZ";

   public ItemStarContoler() {
      this.setMaxStackSize(1);
   }

   @Override
   public EnumActionResult onItemUseFirst(EntityPlayer player, World world, BlockPos pos, EnumFacing side, float hitX, float hitY, float hitZ, EnumHand hand) {
      TileEntity te = world.getTileEntity(pos);
      if (te instanceof TileEntityGravityGenerator) {
         if (!world.isRemote) {
            ItemStack itemStack = player.getHeldItem(hand);
            NBTTagCompound tag = itemStack.getTagCompound();
            if (tag == null) {
               tag = new NBTTagCompound();
               itemStack.setTagCompound(tag);
            }

            if (!tag.hasKey(TARGETX_NBT_TAG)) {
               tag.setInteger(TARGETX_NBT_TAG, pos.getX());
               tag.setInteger(TARGETY_NBT_TAG, pos.getY());
               tag.setInteger(TARGETZ_NBT_TAG, pos.getZ());
            }
         }

         return EnumActionResult.SUCCESS;
      }

      return EnumActionResult.PASS;
   }

   @SideOnly(Side.CLIENT)
   @Override
   public void addInformation(ItemStack itemStack, @Nullable World worldIn, List<String> list, ITooltipFlag flagIn) {
      NBTTagCompound tag = itemStack.getTagCompound();
      if (tag != null && tag.hasKey(TARGETX_NBT_TAG)) {
         list.add(I18n.format("starInfo.sconInfoON", tag.getInteger(TARGETX_NBT_TAG), tag.getInteger(TARGETY_NBT_TAG), tag.getInteger(TARGETZ_NBT_TAG)));
      } else {
         list.add(I18n.format("starInfo.sconInfoOFF"));
      }
   }

   @Override
   public ActionResult<ItemStack> onItemRightClick(World world, EntityPlayer player, EnumHand hand) {
      ItemStack itemStack = player.getHeldItem(hand);
      if (!world.isRemote) {
         NBTTagCompound tag = itemStack.getTagCompound();
         if (tag != null && tag.hasKey(TARGETX_NBT_TAG)) {
            int tx = tag.getInteger(TARGETX_NBT_TAG);
            int ty = tag.getInteger(TARGETY_NBT_TAG);
            int tz = tag.getInteger(TARGETZ_NBT_TAG);
            if (player.getDistance(tx + 0.5, ty + 0.5, tz + 0.5) <= DISTANCE_MAX) {
               player.openGui(StarMiner.instance, SMModContainer.guiStarCoreId, world, tx, ty, tz);
            }
         }
      }

      return new ActionResult<>(EnumActionResult.SUCCESS, itemStack);
   }
}
