package com.electrifiedded.starminer.core.entity;

import java.util.ArrayList;
import java.util.List;
import com.electrifiedded.starminer.api.GravityDirection;
import com.electrifiedded.starminer.api.IZeroGravityWorldProvider;
import com.electrifiedded.starminer.core.CoreConfig;
import com.electrifiedded.starminer.core.SMCore;
import com.electrifiedded.starminer.core.ai.AbstractGravityAIHelper;
import com.electrifiedded.starminer.core.packet.SMCorePacketSender;
import com.electrifiedded.starminer.mixin.EntityAccessor;
import com.electrifiedded.starminer.mixin.EntityLivingAccessor;
import com.electrifiedded.starminer.mixin.EntityLivingBaseAccessor;
import net.minecraft.block.Block;
import net.minecraft.block.BlockFence;
import net.minecraft.block.BlockFenceGate;
import net.minecraft.block.BlockSlime;
import net.minecraft.block.BlockWall;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.crash.CrashReport;
import net.minecraft.crash.CrashReportCategory;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.MoverType;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.init.MobEffects;
import net.minecraft.init.SoundEvents;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.util.ReportedException;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.minecraftforge.common.ForgeHooks;

public final class GravityLiving {
   public static DataParameter<Byte> GRAVITY_DW;

   private GravityLiving() {
   }

   public static DataParameter<Byte> setGravityKey(DataParameter<Byte> key) {
      GRAVITY_DW = key;
      return key;
   }

   public static boolean isGravitized(Entity entity) {
      return entity instanceof EntityLiving || entity instanceof EntityPlayer;
   }

   public static ExtendedPropertyGravity gravity(Entity entity) {
      return ExtendedPropertyGravity.getExtendedPropertyGravity(entity);
   }

   public static boolean isAbnormal(Entity entity) {
      if (!isGravitized(entity)) return false;
      ExtendedPropertyGravity gp = gravity(entity);
      return gp != null && gp.gravityDirection != GravityDirection.upTOdown_YN;
   }

   public static boolean isOutSpace(Entity entity) {
      return entity.world != null && entity.world.provider instanceof IZeroGravityWorldProvider;
   }


   /** Overlap tolerance for gravity-frame collisions: rotating the box around its pivot is not
    *  bit-exact, so a box can end up 1 ulp inside the ground. Vanilla's strict overlap test then
    *  treats every ground block as a wall (player freezes) and lets gravity sink the box. */
   private static final double EPS = 1.0E-7;

   static double offX(AxisAlignedBB b, AxisAlignedBB e, double off) {
      if (b.maxY - EPS <= e.minY || b.minY + EPS >= e.maxY || b.maxZ - EPS <= e.minZ || b.minZ + EPS >= e.maxZ) return off;
      if (off > 0.0 && b.minX + EPS >= e.maxX) {
         double d = b.minX - e.maxX;
         if (d < off) off = d;
      } else if (off < 0.0 && b.maxX - EPS <= e.minX) {
         double d = b.maxX - e.minX;
         if (d > off) off = d;
      }
      return off;
   }

   static double offY(AxisAlignedBB b, AxisAlignedBB e, double off) {
      if (b.maxX - EPS <= e.minX || b.minX + EPS >= e.maxX || b.maxZ - EPS <= e.minZ || b.minZ + EPS >= e.maxZ) return off;
      if (off > 0.0 && b.minY + EPS >= e.maxY) {
         double d = b.minY - e.maxY;
         if (d < off) off = d;
      } else if (off < 0.0 && b.maxY - EPS <= e.minY) {
         double d = b.maxY - e.minY;
         if (d > off) off = d;
      }
      return off;
   }

   static double offZ(AxisAlignedBB b, AxisAlignedBB e, double off) {
      if (b.maxX - EPS <= e.minX || b.minX + EPS >= e.maxX || b.maxY - EPS <= e.minY || b.minY + EPS >= e.maxY) return off;
      if (off > 0.0 && b.minZ + EPS >= e.maxZ) {
         double d = b.minZ - e.maxZ;
         if (d < off) off = d;
      } else if (off < 0.0 && b.maxZ - EPS <= e.minZ) {
         double d = b.maxZ - e.minZ;
         if (d > off) off = d;
      }
      return off;
   }

   public static void setPositionG(Entity e, GravityDirection g, double x, double y, double z) {
      e.posX = x;
      e.posY = y;
      e.posZ = z;
      AxisAlignedBB cur = e.getEntityBoundingBox();
      if (cur != null && boxMatches(cur, g, e.width, e.height)) {
         // vanilla often writes posX/Y/Z first and then calls setPosition, so compare with the
         // position the current box stands for, not with the old pos fields
         Vec3d d = downVec(g);
         double wh0 = e.width / 2.0F;
         Vec3d feet = feetCenter(cur, g);
         double dx = x - (feet.x - d.x * wh0);
         double dy = y - (feet.y - d.y * wh0 - wh0);
         double dz = z - (feet.z - d.z * wh0);
         // keep the box move() produced (exact ground contact) instead of re-rotating it every call
         if (Math.abs(dx) > 1.0E-6 || Math.abs(dy) > 1.0E-6 || Math.abs(dz) > 1.0E-6) {
            e.setEntityBoundingBox(cur.offset(dx, dy, dz));
         }
         return;
      }
      double wh = e.width / 2.0F;
      AxisAlignedBB upright = new AxisAlignedBB(x - wh, y, z - wh, x + wh, y + e.height, z + wh);
      e.setEntityBoundingBox(g.rotateAABBAt(upright, x, y + wh, z));
   }

   private static boolean boxMatches(AxisAlignedBB bb, GravityDirection g, float width, float height) {
      AxisAlignedBB l = g.toLocal(bb);
      return Math.abs((l.maxY - l.minY) - height) < 1.0E-4
         && Math.abs((l.maxX - l.minX) - width) < 1.0E-4
         && Math.abs((l.maxZ - l.minZ) - width) < 1.0E-4;
   }

   public static AxisAlignedBB resizedBox(Entity e, GravityDirection g, float width, float height) {
      AxisAlignedBB local = g.toLocal(e.getEntityBoundingBox());
      double cx = (local.minX + local.maxX) / 2.0;
      double cz = (local.minZ + local.maxZ) / 2.0;
      double hw = width / 2.0;
      return g.toWorld(new AxisAlignedBB(cx - hw, local.minY, cz - hw, cx + hw, local.minY + height, cz + hw));
   }

   public static Vec3d downVec(GravityDirection g) {
      return new Vec3d(g.toWorldX(0, -1, 0), g.toWorldY(0, -1, 0), g.toWorldZ(0, -1, 0));
   }

   private static double extentAlong(AxisAlignedBB bb, Vec3d axis) {
      return Math.abs(axis.x) * (bb.maxX - bb.minX) + Math.abs(axis.y) * (bb.maxY - bb.minY) + Math.abs(axis.z) * (bb.maxZ - bb.minZ);
   }

   public static Vec3d feetCenter(AxisAlignedBB bb, GravityDirection g) {
      Vec3d d = downVec(g);
      double half = extentAlong(bb, d) / 2.0;
      return new Vec3d((bb.minX + bb.maxX) / 2.0 + d.x * half, (bb.minY + bb.maxY) / 2.0 + d.y * half, (bb.minZ + bb.maxZ) / 2.0 + d.z * half);
   }

   public static void resetPositionToBBG(Entity e, GravityDirection g) {
      AxisAlignedBB bb = e.getEntityBoundingBox();
      Vec3d d = downVec(g);
      double wh = e.width / 2.0F;
      Vec3d feet = feetCenter(bb, g);
      e.posX = feet.x - d.x * wh;
      e.posY = feet.y - d.y * wh - wh;
      e.posZ = feet.z - d.z * wh;
   }

   public static Vec3d getEyePosition(Entity e, GravityDirection g, float partial) {
      double x = e.prevPosX + (e.posX - e.prevPosX) * partial;
      double y = e.prevPosY + (e.posY - e.prevPosY) * partial;
      double z = e.prevPosZ + (e.posZ - e.prevPosZ) * partial;
      return eyeAt(e, g, x, y, z);
   }

   public static Vec3d eyeAt(Entity e, GravityDirection g, double x, double y, double z) {
      double wh = e.width / 2.0F;
      double up = e.getEyeHeight() - wh;
      return new Vec3d(x + g.toWorldX(0, up, 0), y + wh + g.toWorldY(0, up, 0), z + g.toWorldZ(0, up, 0));
   }

   public static Vec3d eyeFromBB(Entity entity) {
      GravityDirection g = getDir(entity);
      AxisAlignedBB bb = entity.getEntityBoundingBox();
      if (g == GravityDirection.upTOdown_YN) {
         return new Vec3d(entity.posX, entity.posY + entity.getEyeHeight(), entity.posZ);
      }
      Vec3d feet = feetCenter(bb, g);
      Vec3d d = downVec(g);
      double eye = entity.getEyeHeight();
      return new Vec3d(feet.x - d.x * eye, feet.y - d.y * eye, feet.z - d.z * eye);
   }

   public static GravityDirection getDir(Entity e) {
      ExtendedPropertyGravity gp = isGravitized(e) ? gravity(e) : null;
      return gp == null ? GravityDirection.upTOdown_YN : gp.gravityDirection;
   }


   private static Vec3d localMotion(Entity e, GravityDirection g) {
      return new Vec3d(g.toLocalX(e.motionX, e.motionY, e.motionZ), g.toLocalY(e.motionX, e.motionY, e.motionZ), g.toLocalZ(e.motionX, e.motionY, e.motionZ));
   }

   private static void setLocalMotion(Entity e, GravityDirection g, double x, double y, double z) {
      e.motionX = g.toWorldX(x, y, z);
      e.motionY = g.toWorldY(x, y, z);
      e.motionZ = g.toWorldZ(x, y, z);
   }

   private static List<AxisAlignedBB> localBoxes(Entity e, GravityDirection g, AxisAlignedBB localArea) {
      List<AxisAlignedBB> world = e.world.getCollisionBoxes(e, g.toWorld(localArea));
      List<AxisAlignedBB> out = new ArrayList<>(world.size());
      for (AxisAlignedBB bb : world) {
         out.add(g.toLocal(bb));
      }
      return out;
   }

   private static boolean emptyLocal(Entity e, GravityDirection g, AxisAlignedBB localBB) {
      return e.world.getCollisionBoxes(e, g.toWorld(localBB)).isEmpty();
   }


   public static void move(Entity e, ExtendedPropertyGravity gp, MoverType type, double x, double y, double z) {
      GravityDirection g = gp.gravityDirection;
      EntityAccessor acc = (EntityAccessor)e;
      World world = e.world;
      if (e.noClip) {
         e.setEntityBoundingBox(e.getEntityBoundingBox().offset(x, y, z));
         resetPositionToBBG(e, g);
         return;
      }

      world.profiler.startSection("move");
      double startX = e.posX;
      double startY = e.posY;
      double startZ = e.posZ;
      double lx = g.toLocalX(x, y, z);
      double ly = g.toLocalY(x, y, z);
      double lz = g.toLocalZ(x, y, z);
      if (acc.starminer$isInWeb()) {
         acc.starminer$setInWeb(false);
         lx *= 0.25;
         ly *= 0.05F;
         lz *= 0.25;
         e.motionX = 0.0;
         e.motionY = 0.0;
         e.motionZ = 0.0;
      }

      double d2 = lx;
      double d3 = ly;
      double d4 = lz;
      AxisAlignedBB bb = g.toLocal(e.getEntityBoundingBox());
      double step = e.stepHeight;
      if ((type == MoverType.SELF || type == MoverType.PLAYER) && e.onGround && e.isSneaking() && e instanceof EntityPlayer) {
         for (; lx != 0.0 && emptyLocal(e, g, bb.offset(lx, -step, 0.0)); d2 = lx) {
            lx = shrinkTowardZero(lx);
         }

         for (; lz != 0.0 && emptyLocal(e, g, bb.offset(0.0, -step, lz)); d4 = lz) {
            lz = shrinkTowardZero(lz);
         }

         for (; lx != 0.0 && lz != 0.0 && emptyLocal(e, g, bb.offset(lx, -step, lz)); d4 = lz) {
            lx = shrinkTowardZero(lx);
            d2 = lx;
            lz = shrinkTowardZero(lz);
         }
      }

      List<AxisAlignedBB> list1 = localBoxes(e, g, bb.expand(lx, ly, lz));
      AxisAlignedBB startBB = bb;
      if (ly != 0.0) {
         for (AxisAlignedBB b : list1) {
            ly = offY(b, bb, ly);
         }

         bb = bb.offset(0.0, ly, 0.0);
      }

      if (lx != 0.0) {
         for (AxisAlignedBB b : list1) {
            lx = offX(b, bb, lx);
         }

         if (lx != 0.0) {
            bb = bb.offset(lx, 0.0, 0.0);
         }
      }

      if (lz != 0.0) {
         for (AxisAlignedBB b : list1) {
            lz = offZ(b, bb, lz);
         }

         if (lz != 0.0) {
            bb = bb.offset(0.0, 0.0, lz);
         }
      }

      boolean flag = e.onGround || d3 != ly && d3 < 0.0;
      if (step > 0.0F && flag && (d2 != lx || d4 != lz)) {
         double d14 = lx;
         double d6 = ly;
         double d7 = lz;
         AxisAlignedBB bb1 = bb;
         bb = startBB;
         ly = step;
         List<AxisAlignedBB> list = localBoxes(e, g, bb.expand(d2, ly, d4));
         AxisAlignedBB bb2 = bb;
         AxisAlignedBB bb3 = bb2.expand(d2, 0.0, d4);
         double d8 = ly;
         for (AxisAlignedBB b : list) {
            d8 = offY(b, bb3, d8);
         }

         bb2 = bb2.offset(0.0, d8, 0.0);
         double d18 = d2;
         for (AxisAlignedBB b : list) {
            d18 = offX(b, bb2, d18);
         }

         bb2 = bb2.offset(d18, 0.0, 0.0);
         double d19 = d4;
         for (AxisAlignedBB b : list) {
            d19 = offZ(b, bb2, d19);
         }

         bb2 = bb2.offset(0.0, 0.0, d19);
         AxisAlignedBB bb4 = bb;
         double d20 = ly;
         for (AxisAlignedBB b : list) {
            d20 = offY(b, bb4, d20);
         }

         AxisAlignedBB bb5 = bb4.offset(0.0, d20, 0.0);
         double d21 = d2;
         for (AxisAlignedBB b : list) {
            d21 = offX(b, bb5, d21);
         }

         AxisAlignedBB bb6 = bb5.offset(d21, 0.0, 0.0);
         double d22 = d4;
         for (AxisAlignedBB b : list) {
            d22 = offZ(b, bb6, d22);
         }

         AxisAlignedBB bb7 = bb6.offset(0.0, 0.0, d22);
         double d23 = d18 * d18 + d19 * d19;
         double d9 = d21 * d21 + d22 * d22;
         if (d23 > d9) {
            lx = d18;
            lz = d19;
            ly = -d8;
            bb = bb2;
         } else {
            lx = d21;
            lz = d22;
            ly = -d20;
            bb = bb7;
         }

         for (AxisAlignedBB b : list) {
            ly = offY(b, bb, ly);
         }

         bb = bb.offset(0.0, ly, 0.0);
         if (d14 * d14 + d7 * d7 >= lx * lx + lz * lz) {
            lx = d14;
            ly = d6;
            lz = d7;
            bb = bb1;
         }
      }

      world.profiler.endSection();
      world.profiler.startSection("rest");
      e.setEntityBoundingBox(g.toWorld(bb));
      resetPositionToBBG(e, g);
      gp.collidedGravityHorizontally = d2 != lx || d4 != lz;
      e.collidedHorizontally = gp.collidedGravityHorizontally;
      e.collidedVertically = d3 != ly;
      e.onGround = e.collidedVertically && d3 < 0.0;
      e.collided = e.collidedHorizontally || e.collidedVertically;

      Vec3d down = downVec(g);
      Vec3d feet = feetCenter(e.getEntityBoundingBox(), g);
      BlockPos blockpos = new BlockPos(feet.x + down.x * 0.2, feet.y + down.y * 0.2, feet.z + down.z * 0.2);
      IBlockState state = world.getBlockState(blockpos);
      if (state.getMaterial() == Material.AIR) {
         BlockPos below = blockpos.offset(g.getDownFacing());
         IBlockState state1 = world.getBlockState(below);
         Block block1 = state1.getBlock();
         if (block1 instanceof BlockFence || block1 instanceof BlockWall || block1 instanceof BlockFenceGate) {
            state = state1;
            blockpos = below;
         }
      }

      acc.starminer$updateFallState(ly, e.onGround, state, blockpos);
      Vec3d ml = localMotion(e, g);
      double mlx = ml.x;
      double mly = ml.y;
      double mlz = ml.z;
      if (d2 != lx) {
         mlx = 0.0;
      }

      if (d4 != lz) {
         mlz = 0.0;
      }

      Block block = state.getBlock();
      if (d3 != ly) {
         if (block instanceof BlockSlime && !e.isSneaking() && mly < 0.0) {
            mly = -mly;
         } else {
            mly = 0.0;
         }
      }

      setLocalMotion(e, g, mlx, mly, mlz);
      if (acc.starminer$canTriggerWalking() && (!e.onGround || !e.isSneaking() || !(e instanceof EntityPlayer)) && !e.isRiding()) {
         double wx = e.posX - startX;
         double wy = e.posY - startY;
         double wz = e.posZ - startZ;
         double d15 = g.toLocalX(wx, wy, wz);
         double d16 = g.toLocalY(wx, wy, wz);
         double d17 = g.toLocalZ(wx, wy, wz);
         if (block != Blocks.LADDER) {
            d16 = 0.0;
         }

         if (e.onGround) {
            block.onEntityWalk(world, blockpos, e);
         }

         e.distanceWalkedModified = (float)(e.distanceWalkedModified + MathHelper.sqrt(d15 * d15 + d17 * d17) * 0.6);
         e.distanceWalkedOnStepModified = (float)(e.distanceWalkedOnStepModified + MathHelper.sqrt(d15 * d15 + d16 * d16 + d17 * d17) * 0.6);
         if (e.distanceWalkedOnStepModified > acc.starminer$getNextStepDistance() && state.getMaterial() != Material.AIR) {
            acc.starminer$setNextStepDistance((int)e.distanceWalkedOnStepModified + 1);
            if (e.isInWater()) {
               float f1 = MathHelper.sqrt(e.motionX * e.motionX * 0.2F + e.motionY * e.motionY + e.motionZ * e.motionZ * 0.2F) * 0.35F;
               if (f1 > 1.0F) {
                  f1 = 1.0F;
               }

               e.playSound(acc.starminer$getSwimSound(), f1, 1.0F + (e.world.rand.nextFloat() - e.world.rand.nextFloat()) * 0.4F);
            } else {
               acc.starminer$playStepSound(blockpos, block);
            }
         } else if (e.distanceWalkedOnStepModified > acc.starminer$getNextFlap() && acc.starminer$makeFlySound() && state.getMaterial() == Material.AIR) {
            acc.starminer$setNextFlap(acc.starminer$playFlySound(e.distanceWalkedOnStepModified));
         }
      }

      try {
         acc.starminer$doBlockCollisions();
      } catch (Throwable throwable) {
         CrashReport crashreport = CrashReport.makeCrashReport(throwable, "Checking entity block collision");
         CrashReportCategory crashreportcategory = crashreport.makeCategory("Entity being checked for collision");
         e.addEntityCrashInfo(crashreportcategory);
         throw new ReportedException(crashreport);
      }

      boolean wet = e.isWet();
      if (world.isFlammableWithin(e.getEntityBoundingBox().shrink(0.001))) {
         acc.starminer$dealFireDamage(1);
         if (!wet) {
            int fire = acc.starminer$getFire() + 1;
            acc.starminer$setFire(fire);
            if (fire == 0) {
               e.setFire(8);
            }
         }
      } else if (acc.starminer$getFire() <= 0) {
         acc.starminer$setFire(-acc.starminer$getFireImmuneTicks());
      }

      if (wet && e.isBurning()) {
         e.playSound(SoundEvents.ENTITY_GENERIC_EXTINGUISH_FIRE, 0.7F, 1.6F + (world.rand.nextFloat() - world.rand.nextFloat()) * 0.4F);
         acc.starminer$setFire(-acc.starminer$getFireImmuneTicks());
      }

      world.profiler.endSection();
   }

   private static double shrinkTowardZero(double v) {
      if (v < 0.05 && v >= -0.05) {
         return 0.0;
      }
      return v > 0.0 ? v - 0.05 : v + 0.05;
   }


   public static void travel(EntityLivingBase e, ExtendedPropertyGravity gp, float strafe, float vertical, float forward) {
      GravityDirection g = gp.gravityDirection;
      boolean zero = gp.isZeroGravity(isOutSpace(e));
      boolean flying = e instanceof EntityPlayer && ((EntityPlayer)e).capabilities.isFlying;
      double savedLocalY = localMotion(e, g).y;
      if (zero && !flying) {
         strafe = 0.0F;
         vertical = 0.0F;
         forward = 0.0F;
      }

      if (e.isServerWorld() || e.canPassengerSteer()) {
         if (!e.isInWater() || flying) {
            if (!e.isInLava() || flying) {
               float f6 = 0.91F;
               BlockPos under = blockUnderFeet(e, g);
               if (e.onGround) {
                  IBlockState underState = e.world.getBlockState(under);
                  f6 = underState.getBlock().getSlipperiness(underState, e.world, under, e) * 0.91F;
               }

               float f7 = 0.16277136F / (f6 * f6 * f6);
               float f8 = e.onGround ? e.getAIMoveSpeed() * f7 : e.jumpMovementFactor;
               moveRelative(e, g, strafe, vertical, forward, f8);
               f6 = 0.91F;
               if (e.onGround) {
                  under = blockUnderFeet(e, g);
                  IBlockState underState = e.world.getBlockState(under);
                  f6 = underState.getBlock().getSlipperiness(underState, e.world, under, e) * 0.91F;
               }

               Vec3d ml = localMotion(e, g);
               double mlx = ml.x;
               double mly = ml.y;
               double mlz = ml.z;
               if (!zero && e.isOnLadder()) {
                  mlx = MathHelper.clamp(mlx, -0.15F, 0.15F);
                  mlz = MathHelper.clamp(mlz, -0.15F, 0.15F);
                  e.fallDistance = 0.0F;
                  if (mly < -0.15) {
                     mly = -0.15;
                  }

                  if (e.isSneaking() && e instanceof EntityPlayer && mly < 0.0) {
                     mly = 0.0;
                  }
               }

               setLocalMotion(e, g, mlx, mly, mlz);
               e.move(MoverType.SELF, e.motionX, e.motionY, e.motionZ);
               ml = localMotion(e, g);
               mlx = ml.x;
               mly = ml.y;
               mlz = ml.z;
               if (!zero) {
                  if (gp.collidedGravityHorizontally && e.isOnLadder()) {
                     mly = 0.2;
                  }

                  if (e.isPotionActive(MobEffects.LEVITATION)) {
                     mly += (0.05 * (e.getActivePotionEffect(MobEffects.LEVITATION).getAmplifier() + 1) - mly) * 0.2;
                  } else if (!e.world.isRemote || e.world.isBlockLoaded(new BlockPos(e.posX, 0.0, e.posZ)) && e.world.getChunk(new BlockPos(e.posX, 0.0, e.posZ)).isLoaded()) {
                     if (!e.hasNoGravity()) {
                        mly -= 0.08;
                     }
                  } else if (e.posY > 0.0) {
                     mly -= 0.1;
                  } else {
                     mly = 0.0;
                  }
               }

               if (!zero || flying) {
                  mly *= zero ? 0.8 : 0.98F;
                  mlx *= f6;
                  mlz *= f6;
               }

               setLocalMotion(e, g, mlx, mly, mlz);
            } else {
               double startLocalY = g.toLocalY(e.posX, e.posY, e.posZ);
               moveRelative(e, g, strafe, vertical, forward, 0.02F);
               e.move(MoverType.SELF, e.motionX, e.motionY, e.motionZ);
               e.motionX *= 0.5;
               e.motionY *= 0.5;
               e.motionZ *= 0.5;
               liquidTail(e, g, zero, startLocalY);
            }
         } else {
            double startLocalY = g.toLocalY(e.posX, e.posY, e.posZ);
            float f1 = ((EntityLivingBaseAccessor)e).starminer$getWaterSlowDown();
            float f2 = 0.02F;
            float f3 = EnchantmentHelper.getDepthStriderModifier(e);
            if (f3 > 3.0F) {
               f3 = 3.0F;
            }

            if (!e.onGround) {
               f3 *= 0.5F;
            }

            if (f3 > 0.0F) {
               f1 += (0.54600006F - f1) * f3 / 3.0F;
               f2 += (e.getAIMoveSpeed() - f2) * f3 / 3.0F;
            }

            moveRelative(e, g, strafe, vertical, forward, f2);
            e.move(MoverType.SELF, e.motionX, e.motionY, e.motionZ);
            Vec3d ml = localMotion(e, g);
            setLocalMotion(e, g, ml.x * f1, ml.y * 0.8F, ml.z * f1);
            liquidTail(e, g, zero, startLocalY);
         }
      }

      e.prevLimbSwingAmount = e.limbSwingAmount;
      double d5 = e.posX - e.prevPosX;
      double d6 = e.posY - e.prevPosY;
      double d7 = e.posZ - e.prevPosZ;
      float f10 = MathHelper.sqrt(d5 * d5 + d6 * d6 + d7 * d7) * 4.0F;
      if (f10 > 1.0F) {
         f10 = 1.0F;
      }

      e.limbSwingAmount = e.limbSwingAmount + (f10 - e.limbSwingAmount) * 0.4F;
      e.limbSwing = e.limbSwing + e.limbSwingAmount;
      if (!zero && flying && !e.isRiding()) {
         Vec3d ml = localMotion(e, g);
         setLocalMotion(e, g, ml.x, savedLocalY * 0.6, ml.z);
      }
   }

   private static void liquidTail(EntityLivingBase e, GravityDirection g, boolean zero, double startLocalY) {
      Vec3d ml = localMotion(e, g);
      double mly = ml.y;
      if (!zero && !e.hasNoGravity()) {
         mly -= 0.02;
      }

      setLocalMotion(e, g, ml.x, mly, ml.z);
      double movedUp = g.toLocalY(e.posX, e.posY, e.posZ) - startLocalY;
      double ox = ml.x;
      double oy = mly + 0.6F - movedUp;
      double oz = ml.z;
      if (e.collidedHorizontally && e.isOffsetPositionInLiquid(g.toWorldX(ox, oy, oz), g.toWorldY(ox, oy, oz), g.toWorldZ(ox, oy, oz))) {
         setLocalMotion(e, g, ml.x, 0.3F, ml.z);
      }
   }

   private static BlockPos blockUnderFeet(Entity e, GravityDirection g) {
      Vec3d feet = feetCenter(e.getEntityBoundingBox(), g);
      Vec3d d = downVec(g);
      return new BlockPos(feet.x + d.x, feet.y + d.y, feet.z + d.z);
   }

   public static void moveRelative(EntityLivingBase e, GravityDirection g, float strafe, float up, float forward, float friction) {
      float f = strafe * strafe + up * up + forward * forward;
      if (f >= 1.0E-4F) {
         f = MathHelper.sqrt(f);
         if (f < 1.0F) {
            f = 1.0F;
         }

         f = friction / f;
         strafe *= f;
         up *= f;
         forward *= f;
         if (e.isInWater() || e.isInLava()) {
            float swim = (float)e.getEntityAttribute(EntityLivingBase.SWIM_SPEED).getAttributeValue();
            strafe *= swim;
            up *= swim;
            forward *= swim;
         }

         float f1 = MathHelper.sin(e.rotationYaw * (float)(Math.PI / 180.0));
         float f2 = MathHelper.cos(e.rotationYaw * (float)(Math.PI / 180.0));
         double lx = strafe * f2 - forward * f1;
         double lz = forward * f2 + strafe * f1;
         e.motionX += g.toWorldX(lx, up, lz);
         e.motionY += g.toWorldY(lx, up, lz);
         e.motionZ += g.toWorldZ(lx, up, lz);
      }
   }

   public static void addLocalUp(Entity e, GravityDirection g, double amount) {
      e.motionX += g.toWorldX(0, amount, 0);
      e.motionY += g.toWorldY(0, amount, 0);
      e.motionZ += g.toWorldZ(0, amount, 0);
   }

   public static void jump(EntityLivingBase e, GravityDirection g) {
      Vec3d ml = localMotion(e, g);
      double mlx = ml.x;
      double mly = ((EntityLivingBaseAccessor)e).starminer$getJumpUpwardsMotion();
      double mlz = ml.z;
      if (e.isPotionActive(MobEffects.JUMP_BOOST)) {
         mly += (e.getActivePotionEffect(MobEffects.JUMP_BOOST).getAmplifier() + 1) * 0.1F;
      }

      if (e.isSprinting()) {
         float f = e.rotationYaw * (float)(Math.PI / 180.0);
         mlx -= MathHelper.sin(f) * 0.2F;
         mlz += MathHelper.cos(f) * 0.2F;
      }

      setLocalMotion(e, g, mlx, mly, mlz);
      e.isAirBorne = true;
      ForgeHooks.onLivingJump(e);
   }

   public static boolean isOnLadder(EntityLivingBase e, GravityDirection g) {
      if (e instanceof EntityPlayer && ((EntityPlayer)e).isSpectator()) {
         return false;
      }
      Vec3d feet = feetCenter(e.getEntityBoundingBox(), g);
      Vec3d d = downVec(g);
      BlockPos pos = new BlockPos(feet.x - d.x * 0.01, feet.y - d.y * 0.01, feet.z - d.z * 0.01);
      IBlockState state = e.world.getBlockState(pos);
      return state.getBlock().isLadder(state, e.world, pos, e);
   }

   public static boolean isInsideOpaqueBlock(Entity e, GravityDirection g) {
      Vec3d eye = eyeFromBB(e);
      BlockPos pos = new BlockPos(eye);
      return e.world.getBlockState(pos).causesSuffocation();
   }

   public static boolean canEntityBeSeen(EntityLivingBase self, Entity target) {
      return self.world.rayTraceBlocks(eyeFromBB(self), eyeFromBB(target), false, true, false) == null;
   }

   public static void applyEntityCollision(Entity self, Entity target, GravityDirection g) {
      if (self.isRidingSameEntity(target) || target.noClip || self.noClip) {
         return;
      }
      double wx = target.posX - self.posX;
      double wy = target.posY - self.posY;
      double wz = target.posZ - self.posZ;
      double d0 = g.toLocalX(wx, wy, wz);
      double d1 = g.toLocalZ(wx, wy, wz);
      double d2 = MathHelper.absMax(d0, d1);
      if (d2 >= 0.01F) {
         d2 = MathHelper.sqrt(d2);
         d0 /= d2;
         d1 /= d2;
         double d3 = 1.0 / d2;
         if (d3 > 1.0) {
            d3 = 1.0;
         }

         d0 *= d3;
         d1 *= d3;
         d0 *= 0.05F;
         d1 *= 0.05F;
         d0 *= 1.0F - self.entityCollisionReduction;
         d1 *= 1.0F - self.entityCollisionReduction;
         double px = g.toWorldX(d0, 0, d1);
         double py = g.toWorldY(d0, 0, d1);
         double pz = g.toWorldZ(d0, 0, d1);
         if (!self.isBeingRidden()) {
            self.addVelocity(-px, -py, -pz);
         }

         if (!target.isBeingRidden()) {
            target.addVelocity(px, py, pz);
         }
      }
   }

   public static void onSwing(EntityLivingBase e) {
      if (!e.isSneaking() && e instanceof EntityPlayer && ExtendedPropertyGravity.isEntityZeroGravity(e) && e.getHeldItemMainhand().isEmpty()) {
         Vec3d look = e.getLookVec();
         double speed = 0.02;
         double min = -0.25;
         double max = 0.25;
         if (look.x > 0.0 && min <= e.motionX || look.x < 0.0 && max >= e.motionX) {
            e.motionX -= look.x * speed;
         }

         if (look.y > 0.0 && min <= e.motionY || look.y < 0.0 && max >= e.motionY) {
            e.motionY -= look.y * speed;
         }

         if (look.z > 0.0 && min <= e.motionZ || look.z < 0.0 && max >= e.motionZ) {
            e.motionZ -= look.z * speed;
         }
      }
   }


   public static void beforeSetSize(Entity e, ExtendedPropertyGravity gp) {
      GravityDirection g = gp.gravityDirection;
      Vec3d feet = feetCenter(e.getEntityBoundingBox(), g);
      Vec3d d = downVec(g);
      gp.savedFootPos = feet.x * d.x + feet.y * d.y + feet.z * d.z;
   }

   public static void afterSetSize(Entity e, ExtendedPropertyGravity gp) {
      if (Double.isNaN(gp.savedFootPos)) return;
      GravityDirection g = gp.gravityDirection;
      setPositionG(e, g, e.posX, e.posY, e.posZ);
      Vec3d d = downVec(g);
      Vec3d feet = feetCenter(e.getEntityBoundingBox(), g);
      double now = feet.x * d.x + feet.y * d.y + feet.z * d.z;
      double shift = gp.savedFootPos - now;
      gp.savedFootPos = Double.NaN;
      if (shift != 0.0) {
         e.setEntityBoundingBox(e.getEntityBoundingBox().offset(d.x * shift, d.y * shift, d.z * shift));
         resetPositionToBBG(e, g);
      }
   }


   public static void fixOutFromOpaqueBlock(Entity e, GravityDirection g) {
      if (!isGravitized(e)) return;
      double h = e.height;
      AxisAlignedBB bb = g.toLocal(e.getEntityBoundingBox());
      List<AxisAlignedBB> list = localBoxes(e, g, bb);
      if (list.isEmpty()) return;
      AxisAlignedBB shifted = bb.offset(0.0, -h, 0.0);
      double dummyMove = h;
      for (AxisAlignedBB b : list) {
         dummyMove = offY(b, shifted, dummyMove);
      }

      if (dummyMove != h) {
         double back = h - dummyMove;
         Vec3d d = downVec(g);
         e.setPosition(e.posX + d.x * back, e.posY + d.y * back, e.posZ + d.z * back);
      }
   }

   private static void changeGravityToNext(EntityLivingBase e, ExtendedPropertyGravity gravity) {
      gravity.gravityDirection = gravity.gravityDirectionNext;
      e.rotationYaw = e.rotationYaw + gravity.onChangeTurnYaw;
      e.prevRotationYaw = e.prevRotationYaw + gravity.onChangeTurnYaw;
      e.renderYawOffset = e.renderYawOffset + gravity.onChangeTurnYaw;
      e.prevRenderYawOffset = e.prevRenderYawOffset + gravity.onChangeTurnYaw;
      e.rotationYawHead = e.rotationYaw;
      e.prevRotationYawHead = e.prevRotationYaw;
      e.fallDistance = 0.0F;
      e.setPosition(e.posX, e.posY, e.posZ);
   }

   public static void preSetSpawnGravity(EntityLivingBase e, GravityDirection gdir, int attractedX, int attractedY, int attractedZ) {
      ExtendedPropertyGravity gravityThis = gravity(e);
      gravityThis.gravityDirectionNext = gravityThis.gravityDirection = gdir;
      gravityThis.isAttracted = true;
      gravityThis.attractedPosX = attractedX;
      gravityThis.attractedPosY = attractedY;
      gravityThis.attractedPosZ = attractedZ;
      e.setPosition(e.posX, e.posY, e.posZ);
      hideInEntityGDirToDataManager(e, gravityThis.gravityDirection);
   }

   public static void hideInEntityGDirToDataManager(EntityLivingBase e, GravityDirection gDir) {
      if (GRAVITY_DW != null && !e.world.isRemote) {
         byte v = (byte)(gDir.ordinal() + 1);
         if (e.getDataManager().get(GRAVITY_DW) != v) {
            e.getDataManager().set(GRAVITY_DW, v);
         }
      }
   }

   public static void onDataManagerChange(EntityLivingBase e, DataParameter<?> key) {
      if (key != GRAVITY_DW || !e.world.isRemote) return;
      ExtendedPropertyGravity gravity = gravity(e);
      if (gravity == null || gravity.hasInitClientGravity) return;
      int hidden = e.getDataManager().get(GRAVITY_DW) - 1;
      if (hidden >= 0 && hidden < GravityDirection.values().length) {
         gravity.gravityDirectionNext = gravity.gravityDirection = GravityDirection.values()[hidden];
         if (!isOutSpace(e)) {
            gravity.normalGravityEffectRedistTick = 60;
         }

         e.setPosition(e.posX, e.posY, e.posZ);
         gravity.hasInitClientGravity = true;
      }
   }

   public static void onEntityUpdatePre(EntityLivingBase e) {
      ExtendedPropertyGravity gravity = gravity(e);
      boolean isGravityZero = gravity.isZeroGravity(isOutSpace(e));
      if (e instanceof EntityPlayer) {
         if (e.world.isRemote) {
            if (!SMCore.proxy.isOtherPlayer((EntityPlayer)e)) {
               gravity.keepGravityDirTick = Math.max(0, gravity.keepGravityDirTick - 1);
               gravity.temporatyZeroGTick = Math.max(0, gravity.temporatyZeroGTick - 1);
               boolean sendGravityStatePacket = false;
               if (e.isRiding()) {
                  gravity.changeGravityDirection(GravityDirection.upTOdown_YN);
                  gravity.turnRate = 1.0F;
                  changeGravityToNext(e, gravity);
                  sendGravityStatePacket = true;
               } else {
                  gravity.prevTurnRate = gravity.turnRate;
                  if (gravity.turnRate < 1.0F) {
                     gravity.turnRate = gravity.turnRate + gravity.turnSpeed;
                     gravity.turnSpeed *= 1.7F;
                     if (gravity.turnRate >= 1.0F) {
                        changeGravityToNext(e, gravity);
                        sendGravityStatePacket = true;
                        fixOutFromOpaqueBlock(e, gravity.gravityDirection);
                     }
                  }

                  if (!isGravityZero) {
                     gravity.updateGravityDirectionState(e);
                  }
               }

               if (sendGravityStatePacket || ++gravity.gravityUpdateTick % CoreConfig.gravityUpdateFreq == 0) {
                  SMCorePacketSender.sendGravityStatePacketToServer(e, gravity.gravityDirection);
                  gravity.gravityUpdateTick = 0;
               }

               gravity.normalGravityEffectRedistTick = Math.max(0, gravity.normalGravityEffectRedistTick - 1);
            }
         } else {
            gravity.hasInitServerPlayer = true;
            gravity.attractUpdateTickCount = Math.max(0, gravity.attractUpdateTickCount - 1);
            if (e.isRiding()) {
               if (gravity.isAttracted) {
                  gravity.loseAttractedBy();
               }
               gravity.gravityDirection = GravityDirection.upTOdown_YN;
            } else {
               gravity.updateAtractedStateAndGravityZero(e);
            }

            gravity.validateGravity((EntityPlayer)e, isGravityZero);
            if (gravity.illegalGStateTickCount > CoreConfig.illegalGStateTickToCheck) {
               if (!((EntityPlayer)e).isPlayerSleeping()) {
                  gravity.unsynchronizedWarnCount++;
                  try {
                     SMCore.proxy.warnOrKickIllegalGravity((EntityPlayer)e, gravity);
                  } catch (Exception ex) {
                     ex.printStackTrace();
                  }
               }

               gravity.illegalGStateTickCount = 0;
            }

            gravity.redistInOpaqueBlockDamageTick = Math.max(0, gravity.redistInOpaqueBlockDamageTick - 1);
            gravity.acceptExceptionalGravityTick = Math.max(0, gravity.acceptExceptionalGravityTick - 1);
         }
      } else {
         gravity.turnRate = 1.0F;
         gravity.attractUpdateTickCount = Math.max(0, gravity.attractUpdateTickCount - 1);
         gravity.keepGravityDirTick = Math.max(0, gravity.keepGravityDirTick - 1);
         gravity.temporatyZeroGTick = Math.max(0, gravity.temporatyZeroGTick - 1);
         if (!e.world.isRemote) {
            if ((gravity.isAttracted || gravity.gravityDirection != GravityDirection.upTOdown_YN) && e.isBeingRidden()) {
               e.removePassengers();
            }

            gravity.updateAtractedStateAndGravityZero(e);
         }

         if (!isGravityZero) {
            gravity.changeGravityImmidiate = false;
            gravity.updateGravityDirectionState(e);
         }

         if (gravity.gravityDirectionNext != gravity.gravityDirection) {
            changeGravityToNext(e, gravity);
            gravity.normalGravityEffectRedistTick = 20;
            fixOutFromOpaqueBlock(e, gravity.gravityDirection);
         }

         gravity.normalGravityEffectRedistTick = Math.max(0, gravity.normalGravityEffectRedistTick - 1);
         gravity.redistInOpaqueBlockDamageTick = Math.max(0, gravity.redistInOpaqueBlockDamageTick - 1);
         gravity.acceptExceptionalGravityTick = Math.max(0, gravity.acceptExceptionalGravityTick - 1);
      }
   }

   public static void onEntityUpdatePost(EntityLivingBase e) {
      ExtendedPropertyGravity gravity = gravity(e);
      hideInEntityGDirToDataManager(e, gravity.gravityDirection);
      if (e instanceof EntityPlayer && !e.world.isRemote && !e.isDead && e.posY < -128.0) {
         e.onKillCommand();
      }

      gravity.changeGravityImmidiate = false;
   }


   public static boolean updateAITasksForGravity(EntityLiving living) {
      ExtendedPropertyGravity gravity = gravity(living);
      if (gravity.isAttracted && ensureGAIHelper(living, gravity)) {
         AbstractGravityAIHelper helper = gravity.gAIHelper;
         AbstractGravityAIHelper.injectGAIHelper(helper, living);
         ((EntityLivingBaseAccessor)living).starminer$setIdleTime(((EntityLivingBaseAccessor)living).starminer$getIdleTime() + 1);
         ((EntityLivingAccessor)living).starminer$despawnEntity();
         helper.gSenses.clearSensingCache();
         helper.gTargetTasks.onUpdateTasks();
         helper.gTasks.onUpdateTasks();
         helper.gNavigator.onUpdateNavigation();
         ((EntityLivingAccessor)living).starminer$updateAITasks();
         helper.gMoveHelper.onUpdateMoveHelper();
         helper.gLookHelper.onUpdateLook();
         helper.gJumpHelper.doJump();
         return true;
      } else {
         if (gravity.gAIHelper != null) {
            AbstractGravityAIHelper.resetOrgAIHelper(gravity.gAIHelper, living);
         }

         return false;
      }
   }

   private static boolean ensureGAIHelper(EntityLiving entityLiving, ExtendedPropertyGravity gravity) {
      if (gravity.noGAIHelper) {
         return false;
      } else if (gravity.gAIHelper == null) {
         Class<? extends AbstractGravityAIHelper> clazz = AbstractGravityAIHelper.gAiConstructMap.get(entityLiving.getClass());
         if (clazz == null) {
            clazz = AbstractGravityAIHelper.DefaultGravityAIHelper.class;
         }

         try {
            gravity.gAIHelper = clazz.getDeclaredConstructor().newInstance();
            gravity.gAIHelper.initAI(entityLiving);
            return true;
         } catch (Exception ex) {
            ex.printStackTrace();
            gravity.noGAIHelper = true;
            gravity.gAIHelper = null;
            return false;
         }
      } else {
         return true;
      }
   }
}
