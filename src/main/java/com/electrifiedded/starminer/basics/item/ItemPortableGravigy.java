package com.electrifiedded.starminer.basics.item;

import net.minecraft.entity.Entity;
import net.minecraft.entity.MoverType;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.item.EntityXPOrb;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ActionResult;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumHand;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.world.World;

public class ItemPortableGravigy extends Item {
   private static final double G_EXPAND = 16.0;
   public static final String ENABLEPGRV_NBT_TAG = "stmn.p_grav";

   public ItemPortableGravigy() {
      this.setMaxStackSize(1);
      this.addPropertyOverride(new ResourceLocation("starminer", "enabled"), (stack, world, entity) -> isItemEnabledPortableG(stack) ? 1.0F : 0.0F);
   }

   @Override
   public void onUpdate(ItemStack itemStack, World world, Entity entity, int slot, boolean selected) {
      if (entity instanceof EntityPlayer && isItemEnabledPortableG(itemStack)) {
         EntityPlayer player = (EntityPlayer)entity;
         for (int i = 0; i < player.inventory.mainInventory.size() && i < slot; i++) {
            if (isItemEnabledPortableG(player.inventory.mainInventory.get(i))) {
               return;
            }
         }

         AxisAlignedBB bb = player.getEntityBoundingBox();
         double cx = (bb.maxX + bb.minX) / 2.0;
         double cy = (bb.maxY + bb.minY) / 2.0;
         double cz = (bb.maxZ + bb.minZ) / 2.0;
         pullInRange(world, cx, cy, cz);
      }
   }

   public static boolean isItemEnabledPortableG(ItemStack itemStack) {
      return !itemStack.isEmpty() && itemStack.getItem() instanceof ItemPortableGravigy && itemStack.hasTagCompound()
         && itemStack.getTagCompound().getBoolean(ENABLEPGRV_NBT_TAG);
   }

   private static void pullInRange(World world, double cx, double cy, double cz) {
      AxisAlignedBB area = new AxisAlignedBB(cx - 0.5, cy - 0.5, cz - 0.5, cx + 0.5, cy + 0.5, cz + 0.5).grow(G_EXPAND);
      for (EntityItem entityItem : world.getEntitiesWithinAABB(EntityItem.class, area)) {
         if (!isItemEnabledPortableG(entityItem.getItem())) {
            entityItem.motionX = (cx - entityItem.posX) / 16.0;
            entityItem.motionY = (cy - entityItem.posY) / 16.0;
            entityItem.motionZ = (cz - entityItem.posZ) / 16.0;
         }
      }

      for (EntityXPOrb orb : world.getEntitiesWithinAABB(EntityXPOrb.class, area)) {
         orb.motionX = (cx - orb.posX) / 16.0;
         orb.motionY = (cy - orb.posY) / 16.0;
         orb.motionZ = (cz - orb.posZ) / 16.0;
      }
   }

   @Override
   public boolean onEntityItemUpdate(EntityItem entityItem) {
      if (!isItemEnabledPortableG(entityItem.getItem())) {
         return false;
      }

      AxisAlignedBB bb = entityItem.getEntityBoundingBox();
      pullInRange(entityItem.world, (bb.maxX + bb.minX) / 2.0, (bb.maxY + bb.minY) / 2.0 + 1.0, (bb.maxZ + bb.minZ) / 2.0);
      entityItem.motionX *= 0.7;
      entityItem.motionY *= 0.7;
      entityItem.motionZ *= 0.7;
      if (entityItem.collided) {
         entityItem.motionX *= 0.5;
         entityItem.motionY *= 0.5;
         entityItem.motionZ *= 0.5;
      }

      if (!entityItem.hasNoGravity()) {
         entityItem.motionY += 0.04F;
      }

      return false;
   }

   @Override
   public ActionResult<ItemStack> onItemRightClick(World world, EntityPlayer player, EnumHand hand) {
      ItemStack itemStack = player.getHeldItem(hand);
      NBTTagCompound tag = itemStack.getTagCompound();
      if (tag == null) {
         tag = new NBTTagCompound();
         itemStack.setTagCompound(tag);
      }

      tag.setBoolean(ENABLEPGRV_NBT_TAG, !tag.getBoolean(ENABLEPGRV_NBT_TAG));
      return new ActionResult<>(EnumActionResult.SUCCESS, itemStack);
   }
}
