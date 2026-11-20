package com.electrifiedded.starminer.basics.item;

import com.electrifiedded.starminer.api.Gravity;
import com.electrifiedded.starminer.basics.entity.EntityFallingBlockEx;
import com.electrifiedded.starminer.basics.tileentity.AllowedBlockDictionary;
import net.minecraft.block.Block;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.init.SoundEvents;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ActionResult;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumHand;
import net.minecraft.util.NonNullList;
import net.minecraft.util.SoundCategory;
import net.minecraft.world.World;

public class ItemBlockGun extends Item {
   public static final String[] SUB_NAMES = new String[]{"short", "normal", "long", "spread"};
   private static final double[] SUB_EXCEED_RANGES = new double[]{2.0, 4.0, 6.0, 0.0};

   public ItemBlockGun() {
      this.setMaxStackSize(1);
      this.setMaxDamage(0);
      this.setHasSubtypes(true);
      this.setFull3D();
   }

   @SuppressWarnings("deprecation")
   @Override
   public ActionResult<ItemStack> onItemRightClick(World world, EntityPlayer player, EnumHand hand) {
      ItemStack gun = player.getHeldItem(hand);
      Gravity gravity = Gravity.getGravityProp(player);
      if (gravity == null) {
         return new ActionResult<>(EnumActionResult.PASS, gun);
      }

      boolean infinity = player.capabilities.isCreativeMode;
      int type = gun.getMetadata();
      if (type < 0 || type >= SUB_NAMES.length) {
         type = 0;
      }

      boolean doSpread = type == 3;
      int spreadBlockCount = doSpread ? 5 : 1;
      NonNullList<ItemStack> mainInv = player.inventory.mainInventory;
      int count = 0;
      for (int i = 0; i < mainInv.size(); i++) {
         ItemStack ammo = mainInv.get(i);
         if (!ammo.isEmpty() && ammo.getItem() instanceof ItemBlock) {
            Block block = Block.getBlockFromItem(ammo.getItem());
            if (block != Blocks.AIR && AllowedBlockDictionary.isAllowed(block)) {
               while (count++ < spreadBlockCount) {
                  if (count == 1) {
                     world.playSound(null, player.posX, player.posY, player.posZ, SoundEvents.ENTITY_ARROW_SHOOT, SoundCategory.PLAYERS, 0.5F, 0.4F / (itemRand.nextFloat() * 0.4F + 0.8F));
                  }

                  EntityFallingBlockEx blockEntity = new EntityFallingBlockEx(world, player.posX, player.posY, player.posZ,
                     block.getStateFromMeta(ammo.getItem().getMetadata(ammo.getMetadata())));
                  gravity.setGravityFixedPlayerShootVec(player, blockEntity, 1.0F);
                  blockEntity.setExceedRange(SUB_EXCEED_RANGES[type]);
                  if (doSpread) {
                     blockEntity.motionX += itemRand.nextDouble() * 0.8 - itemRand.nextDouble() * 0.8;
                     blockEntity.motionY += itemRand.nextDouble() * 0.8 - itemRand.nextDouble() * 0.8;
                     blockEntity.motionZ += itemRand.nextDouble() * 0.8 - itemRand.nextDouble() * 0.8;
                  }

                  blockEntity.prevPosX = blockEntity.spawnPosX = blockEntity.posX;
                  blockEntity.prevPosY = blockEntity.spawnPosY = blockEntity.posY;
                  blockEntity.prevPosZ = blockEntity.spawnPosZ = blockEntity.posZ;
                  if (!infinity) {
                     ammo.shrink(1);
                  }

                  if (!world.isRemote) {
                     world.spawnEntity(blockEntity);
                  }

                  if (ammo.isEmpty()) {
                     return new ActionResult<>(EnumActionResult.SUCCESS, gun);
                  }
               }

               return new ActionResult<>(EnumActionResult.SUCCESS, gun);
            }
         }
      }

      return new ActionResult<>(EnumActionResult.PASS, gun);
   }

   @Override
   public String getTranslationKey(ItemStack itemStack) {
      int i = itemStack.getMetadata();
      if (i < 0 || i >= SUB_NAMES.length) {
         i = 0;
      }

      return super.getTranslationKey() + "." + SUB_NAMES[i];
   }

   @Override
   public int getItemEnchantability() {
      return 0;
   }

   @Override
   public void getSubItems(CreativeTabs tab, NonNullList<ItemStack> items) {
      if (this.isInCreativeTab(tab)) {
         for (int i = 0; i < SUB_NAMES.length; i++) {
            items.add(new ItemStack(this, 1, i));
         }
      }
   }
}
