package com.electrifiedded.starminer.basics.block.gravitized;

import com.electrifiedded.archaiclib.v1710.api.block.IArchaicRenderBlock;
import com.electrifiedded.archaiclib.v1710.api.client.IIcon;
import com.electrifiedded.archaiclib.v1710.api.client.IIconRegister;

import net.minecraft.block.properties.PropertyInteger;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.NonNullList;

public class BlockFlowerGravitized extends BlockBushGravitized {
   public static final PropertyInteger TYPE = PropertyInteger.create("type", 0, 8);
   public static final String[] YELLOW_NAMES = {"dandelion"};
   public static final String[] RED_NAMES = {"poppy", "blueOrchid", "allium", "houstonia", "tulipRed", "tulipOrange", "tulipWhite", "tulipPink", "oxeyeDaisy"};
   private final boolean red;

   public BlockFlowerGravitized(int colorType) {
      super();
      this.red = colorType == 1;
      this.setDefaultState(this.blockState.getBaseState().withProperty(TYPE, 0).withProperty(FACING, EnumFacing.UP));
   }

   public String[] getNames() {
      return this.red ? RED_NAMES : YELLOW_NAMES;
   }

   @Override
   protected BlockStateContainer createBlockState() {
      return new BlockStateContainer(this, TYPE, FACING);
   }

   @Override
   public IBlockState getStateFromMeta(int meta) {
      return this.getDefaultState().withProperty(TYPE, Math.min(meta & 15, this.getNames().length - 1));
   }

   @Override
   public int getMetaFromState(IBlockState state) {
      return state.getValue(TYPE);
   }

   @Override
   public int damageDropped(IBlockState state) {
      return state.getValue(TYPE);
   }

   @Override
   public void getSubBlocks(CreativeTabs tab, NonNullList<ItemStack> items) {
      for (int i = 0; i < this.getNames().length; i++) {
         items.add(new ItemStack(this, 1, i));
      }
   }

   @Override
   protected String[] getPlantIconNames() {
      return this.red
         ? new String[]{"minecraft:flower_rose", "minecraft:flower_blue_orchid", "minecraft:flower_allium", "minecraft:flower_houstonia", "minecraft:flower_tulip_red",
            "minecraft:flower_tulip_orange", "minecraft:flower_tulip_white", "minecraft:flower_tulip_pink", "minecraft:flower_oxeye_daisy"}
         : new String[]{"minecraft:flower_dandelion"};
   }
}
