package com.electrifiedded.starminer.basics.entity;

import io.netty.buffer.ByteBuf;
import net.minecraft.block.Block;
import net.minecraft.block.BlockFalling;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.crash.CrashReportCategory;
import net.minecraft.entity.Entity;
import net.minecraft.entity.MoverType;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.registry.IEntityAdditionalSpawnData;

public class EntityFallingBlockEx extends Entity implements IEntityAdditionalSpawnData {
   private IBlockState state = Blocks.AIR.getDefaultState();
   public int fallTime;
   public double exceedRange = 3.0;
   public double spawnPosX;
   public double spawnPosY;
   public double spawnPosZ;
   public boolean shouldDropItem = true;

   public EntityFallingBlockEx(World world) {
      super(world);
      this.setSize(0.2F, 0.2F);
   }

   public EntityFallingBlockEx(World world, double x, double y, double z, IBlockState state) {
      this(world);
      this.state = state;
      this.preventEntitySpawning = true;
      this.setPosition(x, y, z);
      this.motionX = 0.0;
      this.motionY = 0.0;
      this.motionZ = 0.0;
      this.spawnPosX = this.prevPosX = x;
      this.spawnPosY = this.prevPosY = y;
      this.spawnPosZ = this.prevPosZ = z;
   }

   public IBlockState getBlockState() {
      return this.state;
   }

   public Block getBlock() {
      return this.state.getBlock();
   }

   public void setExceedRange(double distance) {
      this.exceedRange = distance;
      if (this.exceedRange == 0.0) {
         this.exceedRange = 5.0 + this.world.rand.nextDouble() * 5.0;
      }
   }

   @Override
   protected boolean canTriggerWalking() {
      return false;
   }

   @Override
   protected void entityInit() {
   }

   @Override
   public boolean canBeCollidedWith() {
      return !this.isDead;
   }

   @Override
   public void onUpdate() {
      if (this.state.getMaterial() == Material.AIR) {
         this.setDead();
         return;
      }

      this.prevPosX = this.posX;
      this.prevPosY = this.posY;
      this.prevPosZ = this.posZ;
      this.fallTime++;
      this.move(MoverType.SELF, this.motionX, this.motionY, this.motionZ);
      this.motionX *= 0.98F;
      this.motionY *= 0.98F;
      this.motionZ *= 0.98F;
      if (!this.world.isRemote) {
         BlockPos pos = new BlockPos(this);
         double farFromSpawn = Math.sqrt(this.getDistanceSq(this.spawnPosX, this.spawnPosY, this.spawnPosZ));
         if (farFromSpawn > this.exceedRange || this.onGround || this.collidedHorizontally || this.collidedVertically || this.fallTime > 600) {
            this.motionX *= 0.7F;
            this.motionZ *= 0.7F;
            this.motionY *= -0.5;
            if (this.world.getBlockState(pos).getBlock() != Blocks.PISTON_EXTENSION) {
               this.setDead();
               if (this.world.mayPlace(this.state.getBlock(), pos, true, net.minecraft.util.EnumFacing.UP, null) && this.world.setBlockState(pos, this.state, 3)) {
                  if (this.state.getBlock() instanceof BlockFalling) {
                     ((BlockFalling)this.state.getBlock()).onEndFalling(this.world, pos, this.state, this.state);
                  }
               } else if (this.shouldDropItem) {
                  this.entityDropItem(new ItemStack(this.state.getBlock(), 1, this.state.getBlock().damageDropped(this.state)), 0.0F);
               }
            }
         }
      }
   }

   @SuppressWarnings("deprecation")
   @Override
   protected void writeEntityToNBT(NBTTagCompound tag) {
      tag.setDouble("exceedR", this.exceedRange);
      tag.setDouble("spwPosX", this.spawnPosX);
      tag.setDouble("spwPosY", this.spawnPosY);
      tag.setDouble("spwPosZ", this.spawnPosZ);
      tag.setInteger("BlockState", Block.getStateId(this.state));
      tag.setInteger("Time", this.fallTime);
      tag.setBoolean("DropItem", this.shouldDropItem);
   }

   @SuppressWarnings("deprecation")
   @Override
   protected void readEntityFromNBT(NBTTagCompound tag) {
      this.exceedRange = tag.getDouble("exceedR");
      this.spawnPosX = tag.getDouble("spwPosX");
      this.spawnPosY = tag.getDouble("spwPosY");
      this.spawnPosZ = tag.getDouble("spwPosZ");
      this.state = Block.getStateById(tag.getInteger("BlockState"));
      this.fallTime = tag.getInteger("Time");
      if (tag.hasKey("DropItem", 99)) {
         this.shouldDropItem = tag.getBoolean("DropItem");
      }
   }

   @Override
   public void addEntityCrashInfo(CrashReportCategory category) {
      super.addEntityCrashInfo(category);
      category.addCrashSection("Immitating block", String.valueOf(this.state));
   }

   @Override
   public boolean canRenderOnFire() {
      return false;
   }

   @SuppressWarnings("deprecation")
   @Override
   public void writeSpawnData(ByteBuf buffer) {
      buffer.writeDouble(this.exceedRange);
      buffer.writeDouble(this.spawnPosX);
      buffer.writeDouble(this.spawnPosY);
      buffer.writeDouble(this.spawnPosZ);
      buffer.writeInt(Block.getStateId(this.state));
      buffer.writeInt(this.fallTime);
      buffer.writeBoolean(this.shouldDropItem);
   }

   @SuppressWarnings("deprecation")
   @Override
   public void readSpawnData(ByteBuf data) {
      this.exceedRange = data.readDouble();
      this.spawnPosX = data.readDouble();
      this.spawnPosY = data.readDouble();
      this.spawnPosZ = data.readDouble();
      this.state = Block.getStateById(data.readInt());
      this.fallTime = data.readInt();
      this.shouldDropItem = data.readBoolean();
   }
}
