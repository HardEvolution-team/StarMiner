package com.electrifiedded.starminer.basics.item;

import com.electrifiedded.starminer.api.Gravity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ActionResult;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

public class ItemSolarWindFan extends Item {
   private static final String STMN_INSTANTFLAG = "stmn;instantflag";

   public ItemSolarWindFan() {
      this.setMaxDamage(500);
      this.setMaxStackSize(1);
   }

   @Override
   public boolean onEntitySwing(EntityLivingBase entityLiving, ItemStack itemStack) {
      if (!entityLiving.isSneaking() && Gravity.isEntityZeroGravity(entityLiving)) {
         Vec3d look = entityLiving.getLookVec();
         NBTTagCompound tag = itemStack.getTagCompound();
         if (tag == null) {
            itemStack.setTagCompound(tag = new NBTTagCompound());
         }

         if (tag.getBoolean(STMN_INSTANTFLAG)) {
            double div = 3.0;
            entityLiving.motionX -= entityLiving.motionX / div;
            if (Math.abs(entityLiving.motionX) < 0.03) {
               entityLiving.motionX = 0.0;
            }

            entityLiving.motionY -= entityLiving.motionY / div;
            if (Math.abs(entityLiving.motionY) < 0.03) {
               entityLiving.motionY = 0.0;
            }

            entityLiving.motionZ -= entityLiving.motionZ / div;
            if (Math.abs(entityLiving.motionZ) < 0.03) {
               entityLiving.motionZ = 0.0;
            }
         } else {
            double speed = 0.06;
            double min = -1.25;
            double max = 1.25;
            if (look.x > 0.0 && max >= entityLiving.motionX || look.x < 0.0 && min <= entityLiving.motionX) {
               entityLiving.motionX += look.x * speed;
            }

            if (look.y > 0.0 && max >= entityLiving.motionY || look.y < 0.0 && min <= entityLiving.motionY) {
               entityLiving.motionY += look.y * speed;
            }

            if (look.z > 0.0 && max >= entityLiving.motionZ || look.z < 0.0 && min <= entityLiving.motionZ) {
               entityLiving.motionZ += look.z * speed;
            }
         }

         itemStack.damageItem(1, entityLiving);
      }

      return false;
   }

   @Override
   public ActionResult<ItemStack> onItemRightClick(World world, EntityPlayer player, EnumHand hand) {
      ItemStack itemStack = player.getHeldItem(hand);
      NBTTagCompound tag = itemStack.getTagCompound();
      if (tag == null) {
         itemStack.setTagCompound(tag = new NBTTagCompound());
      }

      tag.setBoolean(STMN_INSTANTFLAG, true);
      this.onEntitySwing(player, itemStack);
      player.swingArm(hand);
      tag.removeTag(STMN_INSTANTFLAG);
      return new ActionResult<>(EnumActionResult.SUCCESS, itemStack);
   }
}
