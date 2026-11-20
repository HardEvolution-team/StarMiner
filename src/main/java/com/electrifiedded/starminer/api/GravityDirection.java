package com.electrifiedded.starminer.api;

import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.Vec3d;

public enum GravityDirection {
   upTOdown_YN(
      1.0F,
      0.0F,
      0.0F,
      1.0F,
      0.0F,
      0.0F,
      0.0F,
      0.0F,
      0.0F,
      0.0F,
      0.0F,
      0.0F,
      0.0F,
      GravityConst.matirxRoatUpToDownI,
      GravityConst.matirxRoatUpToDownD,
      GravityConst.forgeSideRotUpToDown
   ),
   downTOup_YP(
      1.0F,
      0.0F,
      0.0F,
      -1.0F,
      0.0F,
      -1.0F,
      0.0F,
      0.0F,
      1.0F,
      0.0F,
      0.0F,
      -1.0F,
      0.0F,
      GravityConst.matirxRoatDownTOupI,
      GravityConst.matirxRoatDownTOupD,
      GravityConst.forgeSideRotDownTOup
   ),
   eastTOwest_XN(
      0.0F,
      -1.0F,
      1.0F,
      0.0F,
      0.0F,
      0.0F,
      0.5F,
      -1.0F,
      1.0F,
      0.0F,
      1.0F,
      0.0F,
      0.0F,
      GravityConst.matirxRoatEastTOwestI,
      GravityConst.matirxRoatEastTOwestD,
      GravityConst.forgeSideRotEastTOwest
   ),
   westTOeast_XP(
      0.0F,
      1.0F,
      -1.0F,
      0.0F,
      0.0F,
      0.0F,
      -0.5F,
      1.0F,
      1.0F,
      0.0F,
      -1.0F,
      0.0F,
      0.0F,
      GravityConst.matirxRoatWestTOeastI,
      GravityConst.matirxRoatWestTOeastD,
      GravityConst.forgeSideRotWestTOeast
   ),
   northTOsouth_ZP(
      1.0F,
      0.0F,
      0.0F,
      0.0F,
      -1.0F,
      0.5F,
      0.0F,
      0.0F,
      1.0F,
      1.0F,
      0.0F,
      0.0F,
      -1.0F,
      GravityConst.matirxRoatNorthTOsouthI,
      GravityConst.matirxRoatNorthTOsouthD,
      GravityConst.forgeSideRotNorthTOsouth
   ),
   southTOnorth_ZN(
      1.0F,
      0.0F,
      0.0F,
      0.0F,
      1.0F,
      -0.5F,
      0.0F,
      0.0F,
      1.0F,
      -1.0F,
      0.0F,
      0.0F,
      1.0F,
      GravityConst.matirxRoatSouthTOnorthI,
      GravityConst.matirxRoatSouthTOnorthD,
      GravityConst.forgeSideRotSouthTOnorth
   );

   public float pitchRotDirX;
   public float pitchRotDirY;
   public float yawRotDirX;
   public float yawRotDirY;
   public float yawRotDirZ;
   public float rotX;
   public float rotZ;
   public float shiftEyeX;
   public float shiftEyeY;
   public float shiftEyeZ;
   public float shiftSneakX;
   public float shiftSneakY;
   public float shiftSneakZ;
   public int[] matrixRotationI;
   public double[] matrixRotationD;
   public int[] forgeSideRot;
   public int collideCheckExpandX;
   public int collideCheckExpandY;
   public int collideCheckExpandZ;

   private GravityDirection(
      float argPitchRotDirX,
      float argPitchRotDirY,
      float argYawRotDirX,
      float argYawRotDirY,
      float argYawRotDirZ,
      float argRotX,
      float argRotZ,
      float argShiftEyeX,
      float argShiftEyeY,
      float argShiftEyeZ,
      float argShiftSneakX,
      float argShiftSneakY,
      float argShiftSneakZ,
      int[] argMatrixRotationI,
      double[] argMatrixRotationD,
      int[] argForgeSideRot
   ) {
      this.pitchRotDirX = argPitchRotDirX;
      this.pitchRotDirY = argPitchRotDirY;
      this.yawRotDirX = argYawRotDirX;
      this.yawRotDirY = argYawRotDirY;
      this.yawRotDirZ = argYawRotDirZ;
      this.rotX = argRotX;
      this.rotZ = argRotZ;
      this.shiftEyeX = argShiftEyeX;
      this.shiftEyeY = argShiftEyeY;
      this.shiftEyeZ = argShiftEyeZ;
      this.shiftSneakX = argShiftSneakX;
      this.shiftSneakY = argShiftSneakY;
      this.shiftSneakZ = argShiftSneakZ;
      this.matrixRotationI = argMatrixRotationI;
      this.matrixRotationD = argMatrixRotationD;
      this.forgeSideRot = argForgeSideRot;
      this.collideCheckExpandX = -this.matrixRotationI[3];
      this.collideCheckExpandY = -this.matrixRotationI[4];
      this.collideCheckExpandZ = -this.matrixRotationI[5];
   }

   public static GravityDirection turnWayForNormal(GravityDirection gDir) {
      switch (gDir) {
         case downTOup_YP:
            return downTOup_YP;
         case eastTOwest_XN:
            return westTOeast_XP;
         case westTOeast_XP:
            return eastTOwest_XN;
         case northTOsouth_ZP:
            return southTOnorth_ZN;
         case southTOnorth_ZN:
            return northTOsouth_ZP;
         default:
            return upTOdown_YN;
      }
   }

   public Vec3d rotateVec3(Vec3d vec3) {
      return toWorld(vec3);
   }

   public Vec3d rotateVec3At(Vec3d vec3, double centerX, double centerY, double centerZ) {
      return rotateVec3At(this, vec3, centerX, centerY, centerZ);
   }

   public static final Vec3d rotateVec3(GravityDirection dir, Vec3d vec3) {
      return dir.toWorld(vec3);
   }

   public static final Vec3d rotateVec3At(GravityDirection dir, Vec3d vec3, double centerX, double centerY, double centerZ) {
      double x = vec3.x - centerX, y = vec3.y - centerY, z = vec3.z - centerZ;
      return new Vec3d(dir.toWorldX(x, y, z) + centerX, dir.toWorldY(x, y, z) + centerY, dir.toWorldZ(x, y, z) + centerZ);
   }

   public double toWorldX(double x, double y, double z) { return x * matrixRotationD[0] + y * matrixRotationD[3] + z * matrixRotationD[6]; }
   public double toWorldY(double x, double y, double z) { return x * matrixRotationD[1] + y * matrixRotationD[4] + z * matrixRotationD[7]; }
   public double toWorldZ(double x, double y, double z) { return x * matrixRotationD[2] + y * matrixRotationD[5] + z * matrixRotationD[8]; }
   public double toLocalX(double x, double y, double z) { return x * matrixRotationD[0] + y * matrixRotationD[1] + z * matrixRotationD[2]; }
   public double toLocalY(double x, double y, double z) { return x * matrixRotationD[3] + y * matrixRotationD[4] + z * matrixRotationD[5]; }
   public double toLocalZ(double x, double y, double z) { return x * matrixRotationD[6] + y * matrixRotationD[7] + z * matrixRotationD[8]; }

   public Vec3d toWorld(Vec3d v) { return new Vec3d(toWorldX(v.x, v.y, v.z), toWorldY(v.x, v.y, v.z), toWorldZ(v.x, v.y, v.z)); }
   public Vec3d toLocal(Vec3d v) { return new Vec3d(toLocalX(v.x, v.y, v.z), toLocalY(v.x, v.y, v.z), toLocalZ(v.x, v.y, v.z)); }

   public AxisAlignedBB toLocal(AxisAlignedBB bb) {
      if (this == upTOdown_YN) return bb;
      return new AxisAlignedBB(
         toLocalX(bb.minX, bb.minY, bb.minZ), toLocalY(bb.minX, bb.minY, bb.minZ), toLocalZ(bb.minX, bb.minY, bb.minZ),
         toLocalX(bb.maxX, bb.maxY, bb.maxZ), toLocalY(bb.maxX, bb.maxY, bb.maxZ), toLocalZ(bb.maxX, bb.maxY, bb.maxZ));
   }

   public AxisAlignedBB toWorld(AxisAlignedBB bb) {
      if (this == upTOdown_YN) return bb;
      return new AxisAlignedBB(
         toWorldX(bb.minX, bb.minY, bb.minZ), toWorldY(bb.minX, bb.minY, bb.minZ), toWorldZ(bb.minX, bb.minY, bb.minZ),
         toWorldX(bb.maxX, bb.maxY, bb.maxZ), toWorldY(bb.maxX, bb.maxY, bb.maxZ), toWorldZ(bb.maxX, bb.maxY, bb.maxZ));
   }

   public EnumFacing getDownFacing() {
      switch (this) {
         case downTOup_YP: return EnumFacing.UP;
         case eastTOwest_XN: return EnumFacing.WEST;
         case westTOeast_XP: return EnumFacing.EAST;
         case northTOsouth_ZP: return EnumFacing.SOUTH;
         case southTOnorth_ZN: return EnumFacing.NORTH;
         default: return EnumFacing.DOWN;
      }
   }

   public static GravityDirection fromDownFacing(EnumFacing f) {
      switch (f) {
         case UP: return downTOup_YP;
         case WEST: return eastTOwest_XN;
         case EAST: return westTOeast_XP;
         case SOUTH: return northTOsouth_ZP;
         case NORTH: return southTOnorth_ZN;
         default: return upTOdown_YN;
      }
   }

   public double[] rotateXYZAt(double[] retVal, double argX, double argY, double argZ, double centerX, double centerY, double centerZ) {
      return rotateXYZAt(this, retVal, argX, argY, argZ, centerX, centerY, centerZ);
   }

   public static final double[] rotateXYZAt(
      GravityDirection dir, double[] retVal, double argX, double argY, double argZ, double centerX, double centerY, double centerZ
   ) {
      double x = argX - centerX;
      double y = argY - centerY;
      double z = argZ - centerZ;
      retVal[0] = dir.toWorldX(x, y, z) + centerX;
      retVal[1] = dir.toWorldY(x, y, z) + centerY;
      retVal[2] = dir.toWorldZ(x, y, z) + centerZ;
      return retVal;
   }

   public float[] rotateXYZAt(float[] retVal, float argX, float argY, float argZ, float centerX, float centerY, float centerZ) {
      return rotateXYZAt(this, retVal, argX, argY, argZ, centerX, centerY, centerZ);
   }

   public static final float[] rotateXYZAt(
      GravityDirection dir, float[] retVal, float argX, float argY, float argZ, float centerX, float centerY, float centerZ
   ) {
      float x = argX - centerX;
      float y = argY - centerY;
      float z = argZ - centerZ;
      retVal[0] = (float)dir.toWorldX(x, y, z) + centerX;
      retVal[1] = (float)dir.toWorldY(x, y, z) + centerY;
      retVal[2] = (float)dir.toWorldZ(x, y, z) + centerZ;
      return retVal;
   }

   public int[] rotateXYZAt(int[] retVal, int argX, int argY, int argZ, int centerX, int centerY, int centerZ) {
      return rotateXYZAt(this, retVal, argX, argY, argZ, centerX, centerY, centerZ);
   }

   public static final int[] rotateXYZAt(GravityDirection dir, int[] retVal, int argX, int argY, int argZ, int centerX, int centerY, int centerZ) {
      int x = argX - centerX;
      int y = argY - centerY;
      int z = argZ - centerZ;
      retVal[0] = x * dir.matrixRotationI[0] + y * dir.matrixRotationI[3] + z * dir.matrixRotationI[6] + centerX;
      retVal[1] = x * dir.matrixRotationI[1] + y * dir.matrixRotationI[4] + z * dir.matrixRotationI[7] + centerY;
      retVal[2] = x * dir.matrixRotationI[2] + y * dir.matrixRotationI[5] + z * dir.matrixRotationI[8] + centerZ;
      return retVal;
   }

   public AxisAlignedBB rotateAABBAt(AxisAlignedBB aabb, int x, int y, int z) {
      return rotateAABBAt(this, aabb, x + 0.5, y + 0.5, z + 0.5);
   }

   public static final AxisAlignedBB rotateAABBAt(GravityDirection dir, AxisAlignedBB aabb, int x, int y, int z) {
      return rotateAABBAt(dir, aabb, x + 0.5, y + 0.5, z + 0.5);
   }

   public AxisAlignedBB rotateAABBAt(AxisAlignedBB aabb, double roatCenterX, double roatCenterY, double roatCenterZ) {
      return rotateAABBAt(this, aabb, roatCenterX, roatCenterY, roatCenterZ);
   }

   public static final AxisAlignedBB rotateAABBAt(GravityDirection dir, AxisAlignedBB aabb, double cx, double cy, double cz) {
      double ax = aabb.minX - cx, ay = aabb.minY - cy, az = aabb.minZ - cz;
      double bx = aabb.maxX - cx, by = aabb.maxY - cy, bz = aabb.maxZ - cz;
      return new AxisAlignedBB(
         dir.toWorldX(ax, ay, az) + cx, dir.toWorldY(ax, ay, az) + cy, dir.toWorldZ(ax, ay, az) + cz,
         dir.toWorldX(bx, by, bz) + cx, dir.toWorldY(bx, by, bz) + cy, dir.toWorldZ(bx, by, bz) + cz);
   }
}
