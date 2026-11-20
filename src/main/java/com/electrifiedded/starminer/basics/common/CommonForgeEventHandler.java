package com.electrifiedded.starminer.basics.common;

import com.electrifiedded.archaiclib.legacy.entity.LegacyInventory;
import com.electrifiedded.starminer.basics.Config;
import com.electrifiedded.starminer.basics.SMModContainer;
import com.electrifiedded.starminer.basics.block.BlockDirtGrassEx;
import com.electrifiedded.starminer.basics.dimention.DimentionTeleportHandler;
import com.electrifiedded.starminer.basics.entity.EntityGProjectile;
import com.electrifiedded.starminer.basics.structure.MapGenStar;
import net.minecraft.block.SoundType;
import net.minecraft.block.state.IBlockState;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.init.Enchantments;
import net.minecraft.init.SoundEvents;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ActionResult;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.world.World;
import java.util.Map;
import java.util.WeakHashMap;
import net.minecraft.world.WorldProviderSurface;
import net.minecraftforge.event.entity.living.LivingEvent.LivingUpdateEvent;
import net.minecraftforge.event.entity.player.ArrowLooseEvent;
import net.minecraftforge.event.entity.player.ArrowNockEvent;
import net.minecraftforge.event.entity.player.UseHoeEvent;
import net.minecraftforge.event.terraingen.PopulateChunkEvent;
import net.minecraftforge.fml.common.eventhandler.Event.Result;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public class CommonForgeEventHandler {
   public final DimentionTeleportHandler teleportHandler = new DimentionTeleportHandler();

   private final Map<World, MapGenStar> overworldStars = new WeakHashMap<>();

   @SubscribeEvent
   public void handleLivingUpdate(LivingUpdateEvent event) {
      if (event.getEntityLiving() instanceof EntityPlayerMP && !event.getEntityLiving().world.isRemote
         && this.teleportHandler.onUpdateEntityEnd((EntityPlayerMP)event.getEntityLiving())) {
         event.setCanceled(true);
      }
   }

   @SubscribeEvent
   public void handlePopulateChunk(PopulateChunkEvent.Post event) {
      World world = event.getWorld();
      if (Config.generateStars && world.provider instanceof WorldProviderSurface) {
         MapGenStar gen = this.overworldStars.computeIfAbsent(world, w -> new MapGenStar(16, 8));
         gen.generate(world, event.getChunkX(), event.getChunkZ(), null);
         gen.generateStructure(world, event.getRand(), new ChunkPos(event.getChunkX(), event.getChunkZ()));
      }
   }

   @SubscribeEvent
   public void handleUseHoe(UseHoeEvent event) {
      World world = event.getWorld();
      BlockPos pos = event.getPos();
      IBlockState state = world.getBlockState(pos);
      if (state.getBlock() == SMModContainer.DirtGrassExBlock && !state.getValue(BlockDirtGrassEx.TILLED)) {
         SoundType st = SoundType.GROUND;
         world.playSound(null, pos, SoundEvents.ITEM_HOE_TILL, SoundCategory.BLOCKS, (st.getVolume() + 1.0F) / 2.0F, st.getPitch() * 0.8F);
         if (!world.isRemote) {
            world.setBlockState(pos, state.withProperty(BlockDirtGrassEx.TILLED, true), 2);
         }
         event.setResult(Result.ALLOW);
      }
   }

   private static boolean hasGArrow(EntityPlayer player) {
      return LegacyInventory.hasItem(player.inventory, SMModContainer.GArrowItem);
   }

   @SubscribeEvent
   public void handleArrowNock(ArrowNockEvent event) {
      EntityPlayer player = event.getEntityPlayer();
      if (hasGArrow(player)) {
         player.setActiveHand(event.getHand());
         event.setAction(new ActionResult<>(EnumActionResult.SUCCESS, event.getBow()));
      }
   }

   @SubscribeEvent
   public void handleArrowLoose(ArrowLooseEvent event) {
      EntityPlayer shooter = event.getEntityPlayer();
      if (!hasGArrow(shooter)) {
         return;
      }
      World world = shooter.world;
      ItemStack bow = event.getBow();
      boolean infinity = shooter.capabilities.isCreativeMode || EnchantmentHelper.getEnchantmentLevel(Enchantments.INFINITY, bow) > 0;
      float f = event.getCharge() / 20.0F;
      f = (f * f + f * 2.0F) / 3.0F;
      if (f < 0.1F) {
         event.setCanceled(true);
         return;
      }
      if (f > 1.0F) {
         f = 1.0F;
      }

      if (!world.isRemote) {
         EntityGProjectile arrow = new EntityGProjectile(world, shooter, f * 2.0F, EntityGProjectile.GProjectileType.gArrow);
         if (f == 1.0F) {
            arrow.setIsCritical(true);
         }
         int power = EnchantmentHelper.getEnchantmentLevel(Enchantments.POWER, bow);
         if (power > 0) {
            arrow.setDamage(arrow.getDamage() + power * 0.5 + 0.5);
         }
         int punch = EnchantmentHelper.getEnchantmentLevel(Enchantments.PUNCH, bow);
         if (punch > 0) {
            arrow.setKnockbackStrength(punch);
         }
         if (EnchantmentHelper.getEnchantmentLevel(Enchantments.FLAME, bow) > 0) {
            arrow.setFire(100);
         }
         if (infinity) {
            arrow.pickupStatus = EntityGProjectile.PickupStatus.CREATIVE_ONLY;
         }
         world.spawnEntity(arrow);
      }

      bow.damageItem(1, shooter);
      world.playSound(null, shooter.posX, shooter.posY, shooter.posZ, SoundEvents.ENTITY_ARROW_SHOOT, SoundCategory.PLAYERS, 1.0F,
         1.0F / (world.rand.nextFloat() * 0.4F + 1.2F) + f * 0.5F);
      if (!infinity) {
         LegacyInventory.consumeInventoryItem(shooter.inventory, SMModContainer.GArrowItem);
      }
      event.setCanceled(true);
   }
}
