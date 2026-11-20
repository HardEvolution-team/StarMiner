package com.electrifiedded.starminer.core.ai.path;

import javax.annotation.Nullable;
import com.electrifiedded.starminer.core.entity.GravityLiving;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLiving;
import net.minecraft.pathfinding.Path;
import net.minecraft.pathfinding.PathFinder;
import net.minecraft.pathfinding.PathNavigate;
import net.minecraft.pathfinding.PathPoint;
import net.minecraft.pathfinding.WalkNodeProcessor;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

public class GPathNavigate extends PathNavigate {
   private Entity targetEntity;
   private Vec3d targetPos;
   private Path dummyPath;
   private int stuckTicks;
   private double lastDist = Double.MAX_VALUE;

   public GPathNavigate(EntityLiving entityLiving, World world) {
      super(entityLiving, world);
   }

   @Override
   protected PathFinder getPathFinder() {
      return new PathFinder(new WalkNodeProcessor());
   }

   @Override
   protected Vec3d getEntityPosition() {
      return new Vec3d(this.entity.posX, this.entity.posY, this.entity.posZ);
   }

   @Override
   protected boolean canNavigate() {
      return this.entity.onGround || this.entity.isInWater() || this.entity.isInLava();
   }

   @Override
   protected boolean isDirectPathBetweenPoints(Vec3d posVec31, Vec3d posVec32, int sizeX, int sizeY, int sizeZ) {
      return true;
   }

   private Path makePath(double x, double y, double z) {
      return new Path(new PathPoint[]{new PathPoint(MathHelper.floor(x), MathHelper.floor(y), MathHelper.floor(z))});
   }

   @Nullable
   @Override
   public Path getPathToPos(BlockPos pos) {
      return this.makePath(pos.getX() + 0.5, pos.getY(), pos.getZ() + 0.5);
   }

   @Nullable
   @Override
   public Path getPathToEntityLiving(Entity entityIn) {
      Path p = this.makePath(entityIn.posX, entityIn.posY, entityIn.posZ);
      this.pendingEntity = entityIn;
      this.pendingPath = p;
      return p;
   }

   private Entity pendingEntity;
   private Path pendingPath;

   @Override
   public boolean tryMoveToXYZ(double x, double y, double z, double speedIn) {
      this.targetEntity = null;
      this.targetPos = new Vec3d(x, y, z);
      this.speed = speedIn;
      this.dummyPath = this.makePath(x, y, z);
      this.resetProgress();
      return true;
   }

   @Override
   public boolean tryMoveToEntityLiving(Entity entityIn, double speedIn) {
      this.targetEntity = entityIn;
      this.targetPos = null;
      this.speed = speedIn;
      this.dummyPath = this.makePath(entityIn.posX, entityIn.posY, entityIn.posZ);
      this.resetProgress();
      return true;
   }

   @Override
   public boolean setPath(@Nullable Path pathentityIn, double speedIn) {
      if (pathentityIn == null || pathentityIn.getCurrentPathLength() == 0) {
         this.clearPath();
         return false;
      }
      if (pathentityIn == this.pendingPath && this.pendingEntity != null) {
         return this.tryMoveToEntityLiving(this.pendingEntity, speedIn);
      }
      PathPoint end = pathentityIn.getFinalPathPoint();
      boolean r = this.tryMoveToXYZ(end.x + 0.5, end.y, end.z + 0.5, speedIn);
      this.dummyPath = pathentityIn;
      return r;
   }

   private void resetProgress() {
      this.stuckTicks = 0;
      this.lastDist = Double.MAX_VALUE;
   }

   @Nullable
   @Override
   public Path getPath() {
      return this.noPath() ? null : this.dummyPath;
   }

   @Override
   public void onUpdateNavigation() {
      ++this.totalTicks;
      if (this.noPath()) return;
      Vec3d goal;
      if (this.targetEntity != null) {
         if (!this.targetEntity.isEntityAlive() || this.targetEntity.world != this.entity.world) {
            this.clearPath();
            return;
         }
         goal = new Vec3d(this.targetEntity.posX, this.targetEntity.posY, this.targetEntity.posZ);
      } else {
         goal = this.targetPos;
      }

      double dx = goal.x - this.entity.posX;
      double dy = goal.y - this.entity.posY;
      double dz = goal.z - this.entity.posZ;
      com.electrifiedded.starminer.api.GravityDirection g = GravityLiving.getDir(this.entity);
      double lx = g.toLocalX(dx, dy, dz);
      double lz = g.toLocalZ(dx, dy, dz);
      double horiz = lx * lx + lz * lz;
      double reach = Math.max(0.6, this.entity.width * 0.5 + 0.5);
      if (horiz < reach * reach) {
         this.clearPath();
         return;
      }

      if (horiz < this.lastDist - 0.01) {
         this.lastDist = horiz;
         this.stuckTicks = 0;
      } else if (++this.stuckTicks > 100) {
         this.clearPath();
         return;
      }

      this.entity.getMoveHelper().setMoveTo(goal.x, goal.y, goal.z, this.speed);
   }

   @Override
   public boolean noPath() {
      return this.targetEntity == null && this.targetPos == null;
   }

   @Override
   public void clearPath() {
      this.targetEntity = null;
      this.targetPos = null;
      this.dummyPath = null;
   }
}
