package com.electrifiedded.starminer.basics.tileentity;

import com.electrifiedded.starminer.api.Gravity;
import com.electrifiedded.starminer.api.GravityDirection;
import com.electrifiedded.starminer.api.IAttractableTileEntity;
import com.electrifiedded.starminer.api.IZeroGravityWorldProvider;
import com.electrifiedded.starminer.basics.Config;
import com.electrifiedded.starminer.basics.SMModContainer;
import com.electrifiedded.starminer.basics.item.ItemLifeSoup;
import com.electrifiedded.starminer.core.entity.GravityLiving;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.monster.EntityCreeper;
import net.minecraft.entity.passive.EntityAmbientCreature;
import net.minecraft.entity.passive.EntityChicken;
import net.minecraft.entity.passive.EntityCow;
import net.minecraft.entity.passive.EntityHorse;
import net.minecraft.entity.passive.EntityMooshroom;
import net.minecraft.entity.passive.EntityOcelot;
import net.minecraft.entity.passive.EntityPig;
import net.minecraft.entity.passive.EntitySheep;
import net.minecraft.entity.passive.EntityVillager;
import net.minecraft.entity.passive.EntityWolf;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.inventory.ItemStackHelper;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.play.server.SPacketUpdateTileEntity;
import net.minecraft.tileentity.TileEntityLockable;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.util.ITickable;
import net.minecraft.util.NonNullList;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;

public class TileEntityGravityGenerator extends TileEntityLockable implements ITickable, IAttractableTileEntity {
   private static final String GRAVITY_NBT_KEY = "stmn.gravRange";
   private static final String STARRAD_NBT_KEY = "stmn.starRad";
   private static final String TYPE_NBT_KEY = "stmn.type";
   private static final String WORKX_NBT_KEY = "stmn.wkX";
   private static final String WORKY_NBT_KEY = "stmn.wkY";
   private static final String WORKZ_NBT_KEY = "stmn.wkZ";
   private static final String USEBUF_NBT_KEY = "stmn.usb";
   public static final int GTYPE_SPHERE = 0;
   public static final int GTYPE_SQUARE = 1;
   public static final int GTYPE_XCYLINDER = 2;
   public static final int GTYPE_YCYLINDER = 3;
   public static final int GTYPE_ZCYLINDER = 4;
   public static final int TYPE_NUM = 5;
   public double gravityRange = 0.0;
   public double starRad = 0.0;
   public int type = 0;
   public short workStateX = 0;
   public short workStateY = 0;
   public short workStateZ = 0;
   public boolean workFast = false;
   public boolean useBufferArea = true;
   private NonNullList<ItemStack> gCoreItemStacks = NonNullList.withSize(28, ItemStack.EMPTY);
   private static final Class<?>[] PURE_ANIMALS_SPAWN_LIST = new Class<?>[]{
      EntityVillager.class, EntityChicken.class, EntityCow.class, EntityMooshroom.class, EntityPig.class, EntitySheep.class, EntityHorse.class,
      EntityOcelot.class, EntityWolf.class
   };
   private static final Class<?>[] DIRTY_ANIMALS_SPAWN_LIST = new Class<?>[]{
      EntityVillager.class, EntityChicken.class, EntityCow.class, EntityMooshroom.class, EntityPig.class, EntitySheep.class, EntityHorse.class,
      EntityOcelot.class, EntityWolf.class, EntityCreeper.class, EntityCreeper.class, EntityCreeper.class, EntityCreeper.class, EntityCreeper.class,
      EntityCreeper.class, EntityCreeper.class, EntityCreeper.class, EntityCreeper.class
   };

   private int x() {
      return this.pos.getX();
   }

   private int y() {
      return this.pos.getY();
   }

   private int z() {
      return this.pos.getZ();
   }

   @Override
   public void update() {
      if (!this.world.isRemote) {
         long totalTime = this.world.getTotalWorldTime();
         if (totalTime % Config.attractCheckTick == 0L) {
            this.addEffectsToPlayersOnServer();
         }

         long workSpeed = this.workFast ? 5L : 40L;
         if (totalTime % workSpeed == 0L) {
            this.doTerraformWork();
         }

         if (totalTime % 100L == 0L) {
            this.doGravitizeWork();
            this.doAnimalMobSpawning();
         }
      }
   }

   private void addEffectsToPlayersOnServer() {
      double centerX = this.x() + 0.5;
      double centerY = this.y() + 0.5;
      double centerZ = this.z() + 0.5;
      AxisAlignedBB bb = new AxisAlignedBB(this.pos).grow(this.gravityRange);
      for (EntityLivingBase entityG : this.world.getEntitiesWithinAABB(EntityLivingBase.class, bb)) {
         if (!(entityG instanceof EntityAmbientCreature) && entityG.isNonBoss() && !entityG.isPlayerSleeping() && GravityLiving.isGravitized(entityG)) {
            Gravity gravity = Gravity.getGravityProp(entityG);
            if (gravity != null && inGravityRange(entityG, gravity.gravityDirection, centerX, centerY, centerZ, this.gravityRange, this.type)) {
               if (!gravity.isAttracted()) {
                  gravity.attractUpdateTickCount = 60;
                  gravity.setAttractedBy(this);
               } else if (gravity.isAttractedBy(this) && gravity.attractUpdateTickCount <= 0) {
                  gravity.attractUpdateTickCount = 60;
                  gravity.setAttractedBy(this);
               }
            }
         }
      }
   }

   private static double[] pivot(Entity entity) {
      GravityDirection g = Gravity.getGravityDirection(entity);
      AxisAlignedBB bb = entity.getEntityBoundingBox();
      double wh = entity.width / 2.0F;
      switch (g) {
         case northTOsouth_ZP:
            return new double[]{(bb.maxX + bb.minX) / 2.0, (bb.maxY + bb.minY) / 2.0, bb.maxZ - wh};
         case southTOnorth_ZN:
            return new double[]{(bb.maxX + bb.minX) / 2.0, (bb.maxY + bb.minY) / 2.0, bb.minZ + wh};
         case westTOeast_XP:
            return new double[]{bb.maxX - wh, (bb.maxY + bb.minY) / 2.0, (bb.maxZ + bb.minZ) / 2.0};
         case eastTOwest_XN:
            return new double[]{bb.minX + wh, (bb.maxY + bb.minY) / 2.0, (bb.maxZ + bb.minZ) / 2.0};
         case downTOup_YP:
            return new double[]{(bb.maxX + bb.minX) / 2.0, bb.maxY - wh, (bb.maxZ + bb.minZ) / 2.0};
         default:
            return new double[]{(bb.maxX + bb.minX) / 2.0, bb.minY + wh, (bb.maxZ + bb.minZ) / 2.0};
      }
   }

   public static boolean inGravityRange(Entity entity, GravityDirection gDirection, double cx, double cy, double cz, double range, int type) {
      double[] p = pivot(entity);
      double px = p[0];
      double py = p[1];
      double pz = p[2];
      switch (type) {
         case GTYPE_SQUARE:
            return px <= cx + range && px >= cx - range && py <= cy + range && py >= cy - range && pz <= cz + range && pz >= cz - range;
         case GTYPE_XCYLINDER:
            return px <= cx + range && px >= cx - range && Math.sqrt((cy - py) * (cy - py) + (cz - pz) * (cz - pz)) <= range;
         case GTYPE_YCYLINDER:
            return py <= cy + range && py >= cy - range && Math.sqrt((cx - px) * (cx - px) + (cz - pz) * (cz - pz)) <= range;
         case GTYPE_ZCYLINDER:
            return pz <= cz + range && pz >= cz - range && Math.sqrt((cx - px) * (cx - px) + (cy - py) * (cy - py)) <= range;
         default:
            return Math.sqrt((cx - px) * (cx - px) + (cy - py) * (cy - py) + (cz - pz) * (cz - pz)) <= range;
      }
   }

   @Override
   public boolean isStillInAttractedState(Entity entity) {
      return inGravityRange(entity, Gravity.getGravityDirection(entity), this.x() + 0.5, this.y() + 0.5, this.z() + 0.5, this.gravityRange + 2.0, this.type);
   }

   @Override
   public GravityDirection getCurrentGravity(Entity entity) {
      double[] p = pivot(entity);
      double xRel = p[0] - (this.x() + 0.5);
      double yRel = p[1] - (this.y() + 0.5);
      double zRel = p[2] - (this.z() + 0.5);
      return this.getGravityDirection(xRel, yRel, zRel, isGravityReverse(entity, false), Gravity.getGravityDirection(entity));
   }

   private GravityDirection getGravityDirection(double xRel, double yRel, double zRel, boolean reverse, GravityDirection gDirNow) {
      GravityDirection fresh = this.computeGravityDirection(xRel, yRel, zRel, reverse, gDirNow);
      if (this.type == GTYPE_SPHERE && gDirNow != null && fresh != gDirNow && gDirNow != GravityDirection.upTOdown_YN || this.type == GTYPE_SPHERE && gDirNow == GravityDirection.upTOdown_YN && fresh != gDirNow && this.isAttractedNow(xRel, yRel, zRel, reverse, gDirNow, fresh)) {
         if (this.keepsDirection(xRel, yRel, zRel, reverse, gDirNow, fresh)) {
            return gDirNow;
         }
      }

      return fresh;
   }

   private boolean isAttractedNow(double xRel, double yRel, double zRel, boolean reverse, GravityDirection cur, GravityDirection fresh) {
      return true;
   }

   private boolean keepsDirection(double xRel, double yRel, double zRel, boolean reverse, GravityDirection cur, GravityDirection fresh) {
      boolean valid;
      double curMag;
      switch (cur) {
         case westTOeast_XP:
            valid = xRel < 0.0 != reverse;
            curMag = Math.abs(xRel);
            break;
         case eastTOwest_XN:
            valid = !(xRel < 0.0 != reverse);
            curMag = Math.abs(xRel);
            break;
         case northTOsouth_ZP:
            valid = zRel < 0.0 != reverse;
            curMag = Math.abs(zRel);
            break;
         case southTOnorth_ZN:
            valid = !(zRel < 0.0 != reverse);
            curMag = Math.abs(zRel);
            break;
         case downTOup_YP:
            valid = yRel < 0.0 != reverse;
            curMag = Math.abs(yRel);
            break;
         default:
            valid = !(yRel < 0.0 != reverse);
            curMag = Math.abs(yRel);
      }

      double freshMag;
      switch (fresh) {
         case westTOeast_XP:
         case eastTOwest_XN:
            freshMag = Math.abs(xRel);
            break;
         case northTOsouth_ZP:
         case southTOnorth_ZN:
            freshMag = Math.abs(zRel);
            break;
         default:
            freshMag = Math.abs(yRel);
      }

      return valid && freshMag - curMag < 1.5;
   }

   private GravityDirection computeGravityDirection(double xRel, double yRel, double zRel, boolean reverse, GravityDirection gDirNow) {
      switch (this.type) {
         case GTYPE_XCYLINDER: {
            double axisDiff = Math.abs(zRel) - Math.abs(yRel);
            if (this.useBufferArea && gDirNow != null && Math.abs(axisDiff) < 1.5) {
               return gDirNow;
            }

            if (axisDiff > 0.0) {
               return zRel < 0.0 != reverse ? GravityDirection.southTOnorth_ZN : GravityDirection.northTOsouth_ZP;
            }

            return yRel < 0.0 != reverse ? GravityDirection.upTOdown_YN : GravityDirection.downTOup_YP;
         }
         case GTYPE_YCYLINDER: {
            double axisDiff = Math.abs(xRel) - Math.abs(zRel);
            if (this.useBufferArea && gDirNow != null && Math.abs(axisDiff) < 1.5) {
               return gDirNow;
            }

            if (axisDiff > 0.0) {
               return xRel < 0.0 != reverse ? GravityDirection.eastTOwest_XN : GravityDirection.westTOeast_XP;
            }

            return zRel < 0.0 != reverse ? GravityDirection.southTOnorth_ZN : GravityDirection.northTOsouth_ZP;
         }
         case GTYPE_ZCYLINDER: {
            double axisDiff = Math.abs(xRel) - Math.abs(yRel);
            if (this.useBufferArea && gDirNow != null && Math.abs(axisDiff) < 1.5) {
               return gDirNow;
            }

            if (axisDiff > 0.0) {
               return xRel < 0.0 != reverse ? GravityDirection.eastTOwest_XN : GravityDirection.westTOeast_XP;
            }

            return yRel < 0.0 != reverse ? GravityDirection.upTOdown_YN : GravityDirection.downTOup_YP;
         }
         default:
            if (Math.abs(xRel) > Math.abs(zRel) && Math.abs(xRel) > Math.abs(yRel)) {
               return xRel < 0.0 != reverse ? GravityDirection.westTOeast_XP : GravityDirection.eastTOwest_XN;
            } else if (Math.abs(zRel) >= Math.abs(xRel) && Math.abs(zRel) > Math.abs(yRel)) {
               return zRel < 0.0 != reverse ? GravityDirection.northTOsouth_ZP : GravityDirection.southTOnorth_ZN;
            } else {
               return yRel < 0.0 != reverse ? GravityDirection.downTOup_YP : GravityDirection.upTOdown_YN;
            }
      }
   }

   public static boolean isGravityReverse(Entity entity, boolean setReverseOff) {
      if (entity instanceof EntityPlayer) {
         EntityPlayer player = (EntityPlayer)entity;
         for (int i = 0; i < 9; i++) {
            ItemStack itemStack = player.inventory.mainInventory.get(i);
            if (!itemStack.isEmpty() && itemStack.getItem() == SMModContainer.GravityControllerItem) {
               if (itemStack.hasTagCompound()) {
                  NBTTagCompound tag = itemStack.getTagCompound();
                  boolean gcon = tag.getBoolean("stmn.g_reverse");
                  if (gcon && setReverseOff) {
                     tag.setBoolean("stmn.g_reverse", false);
                  }

                  return gcon;
               }

               return false;
            }
         }
      }

      return false;
   }

   private void doTerraformWork() {
      int itemStackIndexToUse = this.getItemStackForTerraform();
      if (itemStackIndexToUse == -1) {
         this.workFast = false;
      } else if (this.type == GTYPE_SQUARE) {
         this.workFast = false;
      } else if (this.workStateY >= -((int)this.starRad) && this.workStateY + this.y() >= 0) {
         this.workFast = true;
         if (this.calclulateNextWorkSpot()) {
            this.putBlock(itemStackIndexToUse);
         }
      } else {
         this.workFast = false;
      }
   }

   @SuppressWarnings("deprecation")
   private void putBlock(int itemStackIndexToUse) {
      ItemStack stack = this.gCoreItemStacks.get(itemStackIndexToUse);
      Block block = Block.getBlockFromItem(stack.getItem());
      IBlockState state = block.getStateFromMeta(stack.getItem().getMetadata(stack.getMetadata()));
      this.world.setBlockState(this.pos.add(this.workStateX, this.workStateY, this.workStateZ), state, 3);
      stack.shrink(1);
      this.markDirty();
   }

   public void resetWorkState() {
      int radPlsOne = (int)this.starRad + 1;
      this.workStateX = (short)(-radPlsOne);
      this.workStateZ = (short)(-radPlsOne);
      this.workStateY = (short)radPlsOne;
      if (this.workStateY + this.y() > 255) {
         this.workStateY = (short)(255 - this.y());
      }
   }

   private boolean calclulateNextWorkSpot() {
      int intRad = (int)this.starRad;
      int radPlsOne = intRad + 1;
      int radSub1 = intRad - 1;
      double radPow = intRad * intRad;
      double radSub1Pow = radSub1 * radSub1;
      int lookedCount = 0;

      do {
         if (++this.workStateX > radPlsOne) {
            this.workStateX = (short)(-radPlsOne);
            if (++this.workStateZ > radPlsOne) {
               this.workStateZ = (short)(-radPlsOne);
               if (--this.workStateY < -radPlsOne || this.workStateY + this.y() < 0) {
                  return false;
               }
            }
         }

         if (this.workStateX != 0 || this.workStateY != 0 || this.workStateZ != 0) {
            double a;
            double b;
            switch (this.type) {
               case GTYPE_XCYLINDER:
                  a = this.workStateY;
                  b = this.workStateZ;
                  break;
               case GTYPE_YCYLINDER:
                  a = this.workStateX;
                  b = this.workStateZ;
                  break;
               case GTYPE_ZCYLINDER:
                  a = this.workStateX;
                  b = this.workStateY;
                  break;
               default:
                  a = Math.sqrt((double)this.workStateX * this.workStateX + (double)this.workStateY * this.workStateY);
                  b = this.workStateZ;
            }

            double distancePow = a * a + b * b;
            if (distancePow < radPow && distancePow >= radSub1Pow) {
               return true;
            }
         }
      } while (++lookedCount <= 50);

      return false;
   }

   private int getItemStackForTerraform() {
      for (int i = 0; i < 27; i++) {
         ItemStack stack = this.gCoreItemStacks.get(i);
         if (!stack.isEmpty() && stack.getItem() instanceof ItemBlock) {
            Block block = Block.getBlockFromItem(stack.getItem());
            if (block != Blocks.AIR && AllowedBlockDictionary.isAllowed(block) && (!(this.world.provider instanceof IZeroGravityWorldProvider) || block != Blocks.ICE)) {
               return i;
            }
         }
      }

      return -1;
   }

   private void doGravitizeWork() {
      if (this.world.rand.nextInt(24) == 0) {
         int last = this.gCoreItemStacks.size() - 1;
         ItemStack itemStack = this.gCoreItemStacks.get(last);
         if (!itemStack.isEmpty()) {
            Item newItem = null;
            Item item = itemStack.getItem();
            if (item == Items.WHEAT_SEEDS) {
               newItem = SMModContainer.SeedGravizedItem;
            }

            if (item == Items.CARROT) {
               newItem = SMModContainer.CarrotGravizedItem;
            }

            if (item == Items.POTATO) {
               newItem = SMModContainer.PotatoGravizedItem;
            }

            Block block = Block.getBlockFromItem(item);
            if (block == Blocks.SAPLING) {
               newItem = Item.getItemFromBlock(SMModContainer.SaplingGravitizedBlock);
            }

            if (block == Blocks.YELLOW_FLOWER) {
               newItem = Item.getItemFromBlock(SMModContainer.PlantYelGravitizedBlock);
            }

            if (block == Blocks.RED_FLOWER) {
               newItem = Item.getItemFromBlock(SMModContainer.PlantRedGravitizedBlock);
            }

            if (newItem != null) {
               this.gCoreItemStacks.set(last, new ItemStack(newItem, itemStack.getCount(), itemStack.getMetadata()));
               this.markDirty();
            }
         }
      }
   }

   @Override
   public void readFromNBT(NBTTagCompound tag) {
      super.readFromNBT(tag);
      this.gravityRange = tag.getDouble(GRAVITY_NBT_KEY);
      this.type = tag.getInteger(TYPE_NBT_KEY);
      if (tag.hasKey(STARRAD_NBT_KEY)) {
         this.starRad = tag.getDouble(STARRAD_NBT_KEY);
      } else {
         this.starRad = this.gravityRange - 6.0;
      }

      if (this.starRad < 0.0) {
         this.starRad = 0.0;
      }

      if (tag.hasKey(WORKX_NBT_KEY)) {
         this.workStateX = tag.getShort(WORKX_NBT_KEY);
         this.workStateY = tag.getShort(WORKY_NBT_KEY);
         this.workStateZ = tag.getShort(WORKZ_NBT_KEY);
      } else if (this.pos != null) {
         this.resetWorkState();
      }

      this.useBufferArea = tag.getBoolean(USEBUF_NBT_KEY);
      if (tag.hasKey("Items")) {
         this.gCoreItemStacks = NonNullList.withSize(28, ItemStack.EMPTY);
         ItemStackHelper.loadAllItems(tag, this.gCoreItemStacks);
      }
   }

   @Override
   public NBTTagCompound writeToNBT(NBTTagCompound tag) {
      super.writeToNBT(tag);
      this.writeSettings(tag);
      ItemStackHelper.saveAllItems(tag, this.gCoreItemStacks);
      return tag;
   }

   private void writeSettings(NBTTagCompound tag) {
      tag.setDouble(GRAVITY_NBT_KEY, this.gravityRange);
      tag.setInteger(TYPE_NBT_KEY, this.type);
      tag.setDouble(STARRAD_NBT_KEY, this.starRad);
      tag.setShort(WORKX_NBT_KEY, this.workStateX);
      tag.setShort(WORKY_NBT_KEY, this.workStateY);
      tag.setShort(WORKZ_NBT_KEY, this.workStateZ);
      tag.setBoolean(USEBUF_NBT_KEY, this.useBufferArea);
   }

   @Override
   public NBTTagCompound getUpdateTag() {
      NBTTagCompound tag = super.writeToNBT(new NBTTagCompound());
      this.writeSettings(tag);
      return tag;
   }

   @Override
   public SPacketUpdateTileEntity getUpdatePacket() {
      return new SPacketUpdateTileEntity(this.pos, 0, this.getUpdateTag());
   }

   @Override
   public void onDataPacket(NetworkManager net, SPacketUpdateTileEntity pkt) {
      NBTTagCompound tag = pkt.getNbtCompound();
      this.gravityRange = tag.getDouble(GRAVITY_NBT_KEY);
      this.starRad = tag.getDouble(STARRAD_NBT_KEY);
      this.type = tag.getInteger(TYPE_NBT_KEY);
      this.useBufferArea = tag.getBoolean(USEBUF_NBT_KEY);
   }

   @Override
   public void handleUpdateTag(NBTTagCompound tag) {
      this.gravityRange = tag.getDouble(GRAVITY_NBT_KEY);
      this.starRad = tag.getDouble(STARRAD_NBT_KEY);
      this.type = tag.getInteger(TYPE_NBT_KEY);
      this.useBufferArea = tag.getBoolean(USEBUF_NBT_KEY);
   }

   public void sync() {
      this.markDirty();
      IBlockState state = this.world.getBlockState(this.pos);
      this.world.notifyBlockUpdate(this.pos, state, state, 3);
   }

   public void fixInValidRange() {
      if (this.gravityRange < 0.0) {
         this.gravityRange = 0.0;
      }

      if (this.gravityRange > Config.maxGravityRad) {
         this.gravityRange = Config.maxGravityRad;
      }

      if (this.starRad < 0.0) {
         this.starRad = 0.0;
      }

      if (this.starRad > Config.maxStarRad) {
         this.starRad = Config.maxStarRad;
      }
   }


   @Override
   public int getSizeInventory() {
      return this.gCoreItemStacks.size();
   }

   @Override
   public boolean isEmpty() {
      for (ItemStack s : this.gCoreItemStacks) {
         if (!s.isEmpty()) return false;
      }
      return true;
   }

   @Override
   public ItemStack getStackInSlot(int index) {
      return this.gCoreItemStacks.get(index);
   }

   @Override
   public ItemStack decrStackSize(int index, int count) {
      ItemStack r = ItemStackHelper.getAndSplit(this.gCoreItemStacks, index, count);
      if (!r.isEmpty()) this.markDirty();
      return r;
   }

   @Override
   public ItemStack removeStackFromSlot(int index) {
      return ItemStackHelper.getAndRemove(this.gCoreItemStacks, index);
   }

   @Override
   public void setInventorySlotContents(int index, ItemStack stack) {
      this.gCoreItemStacks.set(index, stack);
      if (stack.getCount() > this.getInventoryStackLimit()) {
         stack.setCount(this.getInventoryStackLimit());
      }

      this.markDirty();
   }

   @Override
   public String getName() {
      return "container.starcore";
   }

   @Override
   public boolean hasCustomName() {
      return false;
   }

   @Override
   public int getInventoryStackLimit() {
      return 64;
   }

   @Override
   public boolean isUsableByPlayer(EntityPlayer player) {
      return this.world.getTileEntity(this.pos) == this && player.getDistanceSq(this.x() + 0.5, this.y() + 0.5, this.z() + 0.5) < 4096.0;
   }

   @Override
   public void openInventory(EntityPlayer player) {
   }

   @Override
   public void closeInventory(EntityPlayer player) {
   }

   @Override
   public boolean isItemValidForSlot(int index, ItemStack stack) {
      return true;
   }

   @Override
   public int getField(int id) {
      return 0;
   }

   @Override
   public void setField(int id, int value) {
   }

   @Override
   public int getFieldCount() {
      return 0;
   }

   @Override
   public void clear() {
      this.gCoreItemStacks.clear();
   }

   @Override
   public Container createContainer(InventoryPlayer playerInventory, EntityPlayer playerIn) {
      return new com.electrifiedded.starminer.basics.gui.ContainerStarCore(playerIn, this);
   }

   @Override
   public String getGuiID() {
      return "starminer:starcore";
   }


   public boolean isEntityMateableNow(EntityLivingBase entityLivingBase) {
      return entityLivingBase instanceof EntityVillager && ItemLifeSoup.isStackLifeSeedVillager(this.gCoreItemStacks.get(this.gCoreItemStacks.size() - 1));
   }

   public void notifyMate(EntityLivingBase entityLivingBase) {
      if (entityLivingBase instanceof EntityVillager) {
         ItemStack itemStack = this.gCoreItemStacks.get(this.gCoreItemStacks.size() - 1);
         if (ItemLifeSoup.isStackLifeSeedVillager(itemStack)) {
            itemStack.shrink(1);
            this.markDirty();
         }
      }
   }

   private void doAnimalMobSpawning() {
      if (this.type == GTYPE_SQUARE || !this.world.getGameRules().getBoolean("doMobSpawning") || this.world.rand.nextInt(6) != 0) {
         return;
      }

      try {
         ItemStack itemStack = this.gCoreItemStacks.get(this.gCoreItemStacks.size() - 1);
         boolean isDirtySoup = ItemLifeSoup.isStackLifeSeedAnimalDirty(itemStack);
         if (!isDirtySoup && !ItemLifeSoup.isStackLifeSeedAnimal(itemStack)) {
            return;
         }

         java.util.Random rand = this.world.rand;
         double rad = this.starRad;
         double radSub1 = rad - 1.0;
         if (rad <= 3.0) {
            return;
         }

         double angleRad = rand.nextDouble() * Math.PI * 2.0;
         double relX;
         double relY;
         double relZ;
         switch (this.type) {
            case GTYPE_XCYLINDER:
               relX = rand.nextDouble() * rad * 2.0 - rad;
               relY = Math.sin(angleRad) * radSub1;
               relZ = Math.cos(angleRad) * radSub1;
               break;
            case GTYPE_YCYLINDER:
               relX = Math.sin(angleRad) * radSub1;
               relY = rand.nextDouble() * rad * 2.0 - rad;
               relZ = Math.cos(angleRad) * radSub1;
               break;
            case GTYPE_ZCYLINDER:
               relX = Math.sin(angleRad) * radSub1;
               relY = Math.cos(angleRad) * radSub1;
               relZ = rand.nextDouble() * rad * 2.0 - rad;
               break;
            default:
               double angleRad2 = rand.nextDouble() * Math.PI * 2.0;
               double cos = Math.cos(angleRad2);
               double radPls1 = rad + 1.0;
               relY = Math.sin(angleRad2) * radPls1;
               relX = Math.cos(angleRad) * cos * radPls1;
               relZ = Math.sin(angleRad) * cos * radPls1;
         }

         int checkY = (int)(relY + this.y() + 0.5);
         if (checkY <= 0 || checkY >= 255) {
            return;
         }

         GravityDirection gDir = this.getGravityDirection(relX, relY, relZ, false, null);
         BlockPos target = new BlockPos(MathHelper.floor(relX + this.x() + 0.5), checkY, MathHelper.floor(relZ + this.z() + 0.5));
         net.minecraft.util.EnumFacing down = gDir.getDownFacing();
         int fullAirLayerCount = 0;
         for (int i = -6; i <= 3; i++) {
            BlockPos layer = target.offset(down, i);
            if (layer.equals(this.pos)) {
               continue;
            }

            if (fullAirLayerCount > 2 && this.world.getBlockState(layer).getMaterial().isSolid()) {
               if (this.tryMobSpawnInPoint(gDir, layer.offset(down.getOpposite()), isDirtySoup)) {
                  itemStack.shrink(1);
                  this.markDirty();
               }

               return;
            }

            boolean blocked = false;
            for (BlockPos p : BlockPos.getAllInBoxMutable(layer.add(-1, -1, -1), layer.add(1, 1, 1))) {
               if (p.offset(down, 0).equals(p) && this.isOnLayer(p, layer, down) && this.world.getBlockState(p).getMaterial().blocksMovement()) {
                  blocked = true;
                  break;
               }
            }

            fullAirLayerCount = blocked ? 0 : fullAirLayerCount + 1;
         }
      } catch (Exception ignored) {
      }
   }

   private boolean isOnLayer(BlockPos p, BlockPos layer, net.minecraft.util.EnumFacing down) {
      switch (down.getAxis()) {
         case X: return p.getX() == layer.getX();
         case Y: return p.getY() == layer.getY();
         default: return p.getZ() == layer.getZ();
      }
   }

   private boolean tryMobSpawnInPoint(GravityDirection gDir, BlockPos p, boolean isDirtySoup) throws Exception {
      Class<?>[] targetList = isDirtySoup ? DIRTY_ANIMALS_SPAWN_LIST : PURE_ANIMALS_SPAWN_LIST;
      EntityLiving entityliving = (EntityLiving)targetList[this.world.rand.nextInt(targetList.length)].getConstructor(World.class).newInstance(this.world);
      entityliving.setLocationAndAngles(p.getX() + 0.5, p.getY() + 0.5, p.getZ() + 0.5, MathHelper.wrapDegrees(this.world.rand.nextFloat() * 360.0F), 0.0F);
      entityliving.rotationYawHead = entityliving.rotationYaw;
      entityliving.renderYawOffset = entityliving.rotationYaw;
      entityliving.onInitialSpawn(this.world.getDifficultyForLocation(p), null);
      GravityLiving.preSetSpawnGravity(entityliving, gDir, this.x(), this.y(), this.z());
      return this.world.spawnEntity(entityliving);
   }

   public void setDirtyLifeSoup(int itemCnt) {
      this.gCoreItemStacks.set(this.gCoreItemStacks.size() - 1, new ItemStack(SMModContainer.LifeSoupItem, itemCnt, 1));
   }
}
