package com.electrifiedded.starminer.basics.item;

import com.electrifiedded.archaiclib.legacy.entity.LegacyInventory;
import com.electrifiedded.starminer.basics.SMModContainer;
import com.electrifiedded.starminer.basics.entity.EntityGProjectile;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Enchantments;
import net.minecraft.init.SoundEvents;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ActionResult;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumHand;
import net.minecraft.util.SoundCategory;
import net.minecraft.world.World;

public class ItemGrappleGun extends Item {
   public static final String G_RAPPLE_NBT_TAG = "stmn.g_rapple";

   public ItemGrappleGun() {
      this.setMaxStackSize(1);
      this.setMaxDamage(512);
      this.setFull3D();
   }

   @Override
   public ActionResult<ItemStack> onItemRightClick(World world, EntityPlayer player, EnumHand hand) {
      ItemStack stack = player.getHeldItem(hand);
      if (LegacyInventory.hasItem(player.inventory, SMModContainer.GHookItem) || player.capabilities.isCreativeMode) {
         boolean infinity = player.capabilities.isCreativeMode || EnchantmentHelper.getEnchantmentLevel(Enchantments.INFINITY, stack) > 0;
         world.playSound(null, player.posX, player.posY, player.posZ, SoundEvents.ENTITY_ARROW_SHOOT, SoundCategory.PLAYERS, 0.5F, 0.4F / (itemRand.nextFloat() * 0.4F + 0.8F));
         stack.damageItem(1, player);
         if (!infinity) {
            LegacyInventory.consumeInventoryItem(player.inventory, SMModContainer.GHookItem);
         }

         if (!world.isRemote) {
            Entity grappleHook = new EntityGProjectile(world, player, 2.5F, EntityGProjectile.GProjectileType.gRappleHook);
            world.spawnEntity(grappleHook);
            NBTTagCompound tag = stack.getTagCompound();
            if (tag == null) {
               tag = new NBTTagCompound();
               stack.setTagCompound(tag);
            }

            if (tag.hasKey(G_RAPPLE_NBT_TAG)) {
               Entity oldGrapple = world.getEntityByID(tag.getInteger(G_RAPPLE_NBT_TAG));
               if (oldGrapple instanceof EntityGProjectile) {
                  oldGrapple.setDead();
               }
            }

            tag.setInteger(G_RAPPLE_NBT_TAG, grappleHook.getEntityId());
         } else {
            SMModContainer.proxy.showGrappleGunGuideMessage();
         }

         return new ActionResult<>(EnumActionResult.SUCCESS, stack);
      }

      return new ActionResult<>(EnumActionResult.PASS, stack);
   }

   @Override
   public int getItemEnchantability() {
      return 0;
   }
}
