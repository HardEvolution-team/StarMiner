package com.electrifiedded.starminer.core.entity;

import java.lang.ref.WeakReference;
import com.electrifiedded.starminer.api.Gravity;
import com.electrifiedded.starminer.api.GravityDirection;
import com.electrifiedded.starminer.api.IAttractableTileEntity;
import com.electrifiedded.starminer.api.IZeroGravityWorldProvider;
import com.electrifiedded.starminer.core.ai.AbstractGravityAIHelper;
import com.electrifiedded.starminer.core.packet.SMCorePacketSender;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

public class ExtendedPropertyGravity extends Gravity {
   private static final String NBT_TAG_KEY = "stmn.gravity";
   private static final float TURN_SPEED_START = 0.05F;
   private WeakReference<Entity> entityWeakRef;
   public int temporatyZeroGTick;
   public int keepGravityDirTick;
   public int normalGravityEffectRedistTick = 0;
   public float turnRate = 100.0F;
   public float prevTurnRate = 100.0F;
   public float turnSpeed = 0.0F;
   public float onChangeRoatDirX = 0.0F;
   public float onChangeRoatDirY = 0.0F;
   public float onChangeRoatDirZ = 0.0F;
   public float onChangeTurnYaw = 0.0F;
   public int illegalGStateTickCount = 0;
   public int unsynchronizedWarnCount = 0;
   public boolean changeGravityImmidiate;

   public int gravityUpdateTick = 0;
   public int redistInOpaqueBlockDamageTick = 0;
   public int commonInstanceFlag = 0;
   public boolean hasInitServerPlayer = false;
   public boolean hasInitClientGravity = false;
   public boolean collidedGravityHorizontally;
   public AbstractGravityAIHelper gAIHelper;
   public boolean noGAIHelper = false;
   public double savedFootPos = Double.NaN;

   public ExtendedPropertyGravity(Entity entity) {
      this.entityWeakRef = new WeakReference<>(entity);
   }

   public Entity getEntity() {
      return this.entityWeakRef.get();
   }

   public void saveNBTData(NBTTagCompound compound) {
      NBTTagCompound myNBT = new NBTTagCompound();
      myNBT.setFloat("turnRate", this.turnRate);
      myNBT.setFloat("turnSpeed", this.turnSpeed);
      myNBT.setFloat("onChangeRoatDirX", this.onChangeRoatDirX);
      myNBT.setFloat("onChangeRoatDirY", this.onChangeRoatDirY);
      myNBT.setFloat("onChangeRoatDirZ", this.onChangeRoatDirZ);
      myNBT.setFloat("onChangeTurnYaw", this.onChangeTurnYaw);
      myNBT.setBoolean("isAttracted", this.isAttracted);
      myNBT.setInteger("attractedPosX", this.attractedPosX);
      myNBT.setInteger("attractedPosY", this.attractedPosY);
      myNBT.setInteger("attractedPosZ", this.attractedPosZ);
      myNBT.setInteger("gdir", this.gravityDirection.ordinal());
      compound.setTag(NBT_TAG_KEY, myNBT);
   }

   public void loadNBTData(NBTTagCompound compound) {
      if (compound.hasKey(NBT_TAG_KEY)) {
         NBTTagCompound myNBT = compound.getCompoundTag(NBT_TAG_KEY);
         this.turnRate = myNBT.getFloat("turnRate");
         this.turnSpeed = myNBT.getFloat("turnSpeed");
         this.onChangeRoatDirX = myNBT.getFloat("onChangeRoatDirX");
         this.onChangeRoatDirY = myNBT.getFloat("onChangeRoatDirY");
         this.onChangeRoatDirZ = myNBT.getFloat("onChangeRoatDirZ");
         this.onChangeTurnYaw = myNBT.getFloat("onChangeTurnYaw");
         this.isAttracted = myNBT.getBoolean("isAttracted");
         this.attractedPosX = myNBT.getInteger("attractedPosX");
         this.attractedPosY = myNBT.getInteger("attractedPosY");
         this.attractedPosZ = myNBT.getInteger("attractedPosZ");
         if (myNBT.hasKey("gdir")) {
            int gdir = myNBT.getInteger("gdir");
            if (gdir >= 0 && gdir < GravityDirection.values().length) {
               this.gravityDirectionNext = this.gravityDirection = GravityDirection.values()[gdir];
               Entity entity = this.entityWeakRef.get();
               if (GravityLiving.isGravitized(entity)) {
                  entity.setPosition(entity.posX, entity.posY, entity.posZ);
                  GravityLiving.hideInEntityGDirToDataManager((EntityLivingBase)entity, this.gravityDirection);
               }
            }
         }
      }
   }

   @Override
   public boolean isZeroGravity() {
      Entity entity = this.entityWeakRef.get();
      return entity != null && entity.world != null && this.isZeroGravity(entity.world.provider instanceof IZeroGravityWorldProvider);
   }

   @Override
   public boolean isAttracted() {
      return this.isAttracted;
   }

   @Override
   public void setAttractedBy(IAttractableTileEntity tileEntity) {
      if (tileEntity instanceof TileEntity) {
         BlockPos pos = ((TileEntity)tileEntity).getPos();
         Entity entity = this.entityWeakRef.get();
         if (entity != null) {
            this.isAttracted = true;
            this.attractedPosX = pos.getX();
            this.attractedPosY = pos.getY();
            this.attractedPosZ = pos.getZ();
            if (entity instanceof EntityPlayerMP) {
               SMCorePacketSender.sendAttractedChangePacketToPlayer((EntityPlayerMP)entity, true, false, pos.getX(), pos.getY(), pos.getZ());
            } else {
               SMCorePacketSender.sendMobAttractedChangePacketToAllAround(entity, true, false, pos.getX(), pos.getY(), pos.getZ());
            }
         }
      }
   }

   @Override
   public boolean isAttractedBy(IAttractableTileEntity tileEntity) {
      if (this.isAttracted && tileEntity instanceof TileEntity) {
         BlockPos pos = ((TileEntity)tileEntity).getPos();
         return pos.getX() == this.attractedPosX && pos.getY() == this.attractedPosY && pos.getZ() == this.attractedPosZ;
      }
      return false;
   }

   @Override
   public void loseAttractedBy() {
      Entity entity = this.entityWeakRef.get();
      if (entity != null) {
         this.isAttracted = false;
         if (entity instanceof EntityPlayerMP) {
            SMCorePacketSender.sendAttractedChangePacketToPlayer((EntityPlayerMP)entity, false, false, 0, 0, 0);
         } else {
            SMCorePacketSender.sendMobAttractedChangePacketToAllAround(entity, false, false, 0, 0, 0);
         }
      }
   }

   @Override
   public Vec3d getGravityFixedLook(float pitch, float yaw) {
      float f = MathHelper.cos(-yaw * (float) (Math.PI / 180.0) - (float) Math.PI);
      float f1 = MathHelper.sin(-yaw * (float) (Math.PI / 180.0) - (float) Math.PI);
      float f2 = -MathHelper.cos(-pitch * (float) (Math.PI / 180.0));
      float f3 = MathHelper.sin(-pitch * (float) (Math.PI / 180.0));
      return this.gravityDirection.toWorld(new Vec3d(f1 * f2, f3, f * f2));
   }

   @Override
   public Vec3d getGravityFixedPlayerEyePoz(EntityPlayer player, float partialTick) {
      return GravityLiving.getEyePosition(player, this.gravityDirection, partialTick);
   }

   @Override
   public void setGravityFixedPlayerShootVec(EntityPlayer shooter, Entity projectileEntity, float partialTick) {
      Vec3d eye = this.getGravityFixedPlayerEyePoz(shooter, partialTick);
      Vec3d look = shooter.getLook(partialTick);
      projectileEntity.setPosition(eye.x + look.x, eye.y + look.y, eye.z + look.z);
      projectileEntity.motionX = look.x;
      projectileEntity.motionY = look.y;
      projectileEntity.motionZ = look.z;
   }

   @Override
   public void setResistInOpaqueBlockDamegeTick(int tick) {
      this.redistInOpaqueBlockDamageTick = 20;
   }

   public void updateGravityDirectionState(Entity entity) {
      GravityDirection gravityDirNew = this.gravityDirection;
      if (this.normalGravityEffectRedistTick <= 0) {
         if (!(this.turnRate < 1.0F)) {
            if (!this.isAttracted) {
               gravityDirNew = GravityDirection.upTOdown_YN;
            } else {
               TileEntity te = entity.world.getTileEntity(new BlockPos(this.attractedPosX, this.attractedPosY, this.attractedPosZ));
               if (!(te instanceof IAttractableTileEntity)) {
                  return;
               }

               gravityDirNew = ((IAttractableTileEntity)te).getCurrentGravity(entity);
            }

            if (this.gravityDirection != gravityDirNew) {
               if (this.changeGravityImmidiate) {
                  this.gravityDirection = gravityDirNew;
                  this.changeGravityImmidiate = false;
               } else {
                  this.changeGravityDirection(gravityDirNew);
               }

               this.keepGravityDirTick = 15;
            }
         }
      }
   }

   @Override
   public void setTemporaryZeroGravity(int tick) {
      this.temporatyZeroGTick = tick;
   }

   @Override
   public void setTemporaryGravityDirection(GravityDirection nextGravityDirection, int tick) {
      GravityDirection old = this.gravityDirection;
      if (this.keepGravityDirTick <= 0 && this.turnRate >= 1.0F) {
         this.changeGravityDirection(nextGravityDirection);
      }

      this.normalGravityEffectRedistTick = tick;
      if (old != this.gravityDirectionNext) {
         this.keepGravityDirTick = 30;
      }
   }

   public void changeGravityDirection(GravityDirection nextGravityDirection) {
      if (this.gravityDirection != nextGravityDirection) {
         this.prevTurnRate = this.turnRate = 0.0F;
         this.turnSpeed = 0.05F;
         this.onChangeRoatDirX = 0.0F;
         this.onChangeRoatDirY = 0.0F;
         this.onChangeRoatDirZ = 0.0F;
         this.onChangeTurnYaw = 0.0F;
         label66:
         switch (this.gravityDirection) {
            case southTOnorth_ZN:
               switch (nextGravityDirection) {
                  case southTOnorth_ZN:
                  default:
                     break label66;
                  case northTOsouth_ZP:
                     this.onChangeRoatDirX = -2.0F;
                     break label66;
                  case westTOeast_XP:
                     this.onChangeRoatDirY = 1.0F;
                     this.onChangeTurnYaw = 90.0F;
                     break label66;
                  case eastTOwest_XN:
                     this.onChangeRoatDirY = -1.0F;
                     this.onChangeTurnYaw = -90.0F;
                     break label66;
                  case downTOup_YP:
                     this.onChangeRoatDirX = -1.0F;
                     break label66;
                  case upTOdown_YN:
                     this.onChangeRoatDirX = 1.0F;
                     break label66;
               }
            case northTOsouth_ZP:
               switch (nextGravityDirection) {
                  case southTOnorth_ZN:
                     this.onChangeRoatDirX = -2.0F;
                  case northTOsouth_ZP:
                  default:
                     break label66;
                  case westTOeast_XP:
                     this.onChangeRoatDirY = -1.0F;
                     this.onChangeTurnYaw = -90.0F;
                     break label66;
                  case eastTOwest_XN:
                     this.onChangeRoatDirY = 1.0F;
                     this.onChangeTurnYaw = 90.0F;
                     break label66;
                  case downTOup_YP:
                     this.onChangeRoatDirX = 1.0F;
                     break label66;
                  case upTOdown_YN:
                     this.onChangeRoatDirX = -1.0F;
                     break label66;
               }
            case westTOeast_XP:
               switch (nextGravityDirection) {
                  case southTOnorth_ZN:
                     this.onChangeRoatDirY = -1.0F;
                     this.onChangeTurnYaw = -90.0F;
                     break label66;
                  case northTOsouth_ZP:
                     this.onChangeRoatDirY = 1.0F;
                     this.onChangeTurnYaw = 90.0F;
                  case westTOeast_XP:
                  default:
                     break label66;
                  case eastTOwest_XN:
                     this.onChangeRoatDirZ = -2.0F;
                     break label66;
                  case downTOup_YP:
                     this.onChangeRoatDirZ = -1.0F;
                     this.onChangeTurnYaw = -180.0F;
                     break label66;
                  case upTOdown_YN:
                     this.onChangeRoatDirZ = 1.0F;
                     break label66;
               }
            case eastTOwest_XN:
               switch (nextGravityDirection) {
                  case southTOnorth_ZN:
                     this.onChangeRoatDirY = 1.0F;
                     this.onChangeTurnYaw = 90.0F;
                     break label66;
                  case northTOsouth_ZP:
                     this.onChangeRoatDirY = -1.0F;
                     this.onChangeTurnYaw = -90.0F;
                     break label66;
                  case westTOeast_XP:
                     this.onChangeRoatDirZ = -2.0F;
                  case eastTOwest_XN:
                  default:
                     break label66;
                  case downTOup_YP:
                     this.onChangeRoatDirZ = 1.0F;
                     this.onChangeTurnYaw = -180.0F;
                     break label66;
                  case upTOdown_YN:
                     this.onChangeRoatDirZ = -1.0F;
                     break label66;
               }
            case downTOup_YP:
               switch (nextGravityDirection) {
                  case southTOnorth_ZN:
                     this.onChangeRoatDirX = 1.0F;
                     break label66;
                  case northTOsouth_ZP:
                     this.onChangeRoatDirX = -1.0F;
                     break label66;
                  case westTOeast_XP:
                     this.onChangeRoatDirZ = 1.0F;
                     this.onChangeTurnYaw = 180.0F;
                     break label66;
                  case eastTOwest_XN:
                     this.onChangeRoatDirZ = -1.0F;
                     this.onChangeTurnYaw = 180.0F;
                  case downTOup_YP:
                  default:
                     break label66;
                  case upTOdown_YN:
                     this.onChangeRoatDirX = -2.0F;
                     break label66;
               }
            case upTOdown_YN:
               switch (nextGravityDirection) {
                  case southTOnorth_ZN:
                     this.onChangeRoatDirX = -1.0F;
                     break;
                  case northTOsouth_ZP:
                     this.onChangeRoatDirX = 1.0F;
                     break;
                  case westTOeast_XP:
                     this.onChangeRoatDirZ = -1.0F;
                     break;
                  case eastTOwest_XN:
                     this.onChangeRoatDirZ = 1.0F;
                     break;
                  case downTOup_YP:
                     this.onChangeRoatDirX = -2.0F;
                  case upTOdown_YN:
               }
         }

         this.gravityDirectionNext = nextGravityDirection;
      }
   }

   public void updateAtractedStateAndGravityZero(Entity entity) {
      if (this.isAttracted && !this.isStillAttracted(entity)) {
         this.loseAttractedBy();
      }
   }

   private boolean isStillAttracted(Entity entity) {
      TileEntity tileEntity = entity.world.getTileEntity(new BlockPos(this.attractedPosX, this.attractedPosY, this.attractedPosZ));
      return tileEntity instanceof IAttractableTileEntity && ((IAttractableTileEntity)tileEntity).isStillInAttractedState(entity);
   }

   public void validateGravity(EntityPlayer entityPlayer, boolean isGravityZero) {
      if (!this.isAttracted
         && !isGravityZero
         && this.gravityDirection != GravityDirection.upTOdown_YN
         && !entityPlayer.isPlayerSleeping()
         && this.acceptExceptionalGravityTick > 0) {
         this.illegalGStateTickCount += 2;
      }

      this.illegalGStateTickCount--;
      if (this.illegalGStateTickCount < 0) {
         this.illegalGStateTickCount = 0;
      }
   }

   public static ExtendedPropertyGravity getExtendedPropertyGravity(Entity entity) {
      return entity instanceof IGravityHolder ? ((IGravityHolder)entity).starminer$getGravity() : null;
   }

   public boolean isZeroGravity(boolean isOutSpace) {
      return this.temporatyZeroGTick > 0 || !this.isAttracted && isOutSpace && this.normalGravityEffectRedistTick <= 0;
   }

   public static boolean isAttracted(Entity entity) {
      if (entity == null) {
         return false;
      } else {
         ExtendedPropertyGravity gp = getExtendedPropertyGravity(entity);
         return gp != null && gp.isAttracted;
      }
   }

   public static boolean isEntityAbnormalGravity(Entity entity) {
      if (entity == null) {
         return false;
      } else {
         ExtendedPropertyGravity gp = getExtendedPropertyGravity(entity);
         return gp != null && gp.gravityDirection != GravityDirection.upTOdown_YN;
      }
   }

   public static boolean isEntityNormalGravity(Entity entity) {
      if (entity == null) {
         return true;
      } else {
         ExtendedPropertyGravity gp = getExtendedPropertyGravity(entity);
         return gp == null || gp.gravityDirection == GravityDirection.upTOdown_YN;
      }
   }
}
