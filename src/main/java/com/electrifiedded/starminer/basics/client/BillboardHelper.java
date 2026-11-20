package com.electrifiedded.starminer.basics.client;

import net.minecraft.client.Minecraft;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public final class BillboardHelper {
   public static float rotationX;
   public static float rotationZ;
   public static float rotationYZ;
   public static float rotationXY;
   public static float rotationXZ;
   private static float lastPartial = Float.NaN;
   private static long lastFrame = -1L;

   private BillboardHelper() {
   }

   public static void compute(float partialTick) {
      Minecraft mc = Minecraft.getMinecraft();
      long frame = mc.getFrameTimer().getIndex();
      if (frame == lastFrame && partialTick == lastPartial) {
         return;
      }

      lastFrame = frame;
      lastPartial = partialTick;
      Entity view = mc.getRenderViewEntity();
      if (view == null) {
         return;
      }

      int i = mc.gameSettings.thirdPersonView == 2 ? 1 : 0;
      Vec3d lookVec = view.getLook(partialTick);
      float xz = MathHelper.sqrt(lookVec.x * lookVec.x + lookVec.z * lookVec.z);
      float ry = (float)(-90.0 + Math.atan2(lookVec.z, lookVec.x) * 180.0 / Math.PI);
      float rp = -((float)(Math.atan2(lookVec.y, xz) * 180.0 / Math.PI));
      rotationX = MathHelper.cos(ry * (float)Math.PI / 180.0F) * (1 - i * 2);
      rotationZ = MathHelper.sin(ry * (float)Math.PI / 180.0F) * (1 - i * 2);
      rotationYZ = -rotationZ * MathHelper.sin(rp * (float)Math.PI / 180.0F) * (1 - i * 2);
      rotationXY = rotationX * MathHelper.sin(rp * (float)Math.PI / 180.0F) * (1 - i * 2);
      rotationXZ = MathHelper.cos(rp * (float)Math.PI / 180.0F);
   }
}
