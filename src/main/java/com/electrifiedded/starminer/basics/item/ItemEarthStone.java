package com.electrifiedded.starminer.basics.item;

import java.util.List;
import javax.annotation.Nullable;
import com.electrifiedded.starminer.api.Gravity;
import com.electrifiedded.starminer.api.GravityDirection;
import com.electrifiedded.starminer.api.IZeroGravityWorldProvider;
import net.minecraft.client.resources.I18n;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class ItemEarthStone extends Item {
   private static final int HEAVYSTONE_AFFECT_TICK = 15;

   public ItemEarthStone() {
      this.setMaxStackSize(1);
   }

   @Override
   public void onUpdate(ItemStack itemStack, World world, Entity entity, int slot, boolean selected) {
      if (world.provider instanceof IZeroGravityWorldProvider && entity instanceof EntityPlayer) {
         Gravity gravity = Gravity.getGravityProp(entity);
         if (gravity != null && gravity.isZeroGravity()) {
            entity.fallDistance = 0.0F;
            if (world.isRemote) {
               gravity.setTemporaryGravityDirection(GravityDirection.upTOdown_YN, HEAVYSTONE_AFFECT_TICK);
            } else {
               gravity.acceptExceptionalGravityTick = 45;
            }
         }
      }
   }

   @SideOnly(Side.CLIENT)
   @Override
   public void addInformation(ItemStack stack, @Nullable World worldIn, List<String> list, ITooltipFlag flagIn) {
      list.add(I18n.format("starInfo.creativeonly"));
   }
}
