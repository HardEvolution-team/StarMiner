package com.electrifiedded.starminer.basics.client;

import com.electrifiedded.starminer.api.GravityDirection;
import java.nio.FloatBuffer;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.lwjgl.BufferUtils;

@SideOnly(Side.CLIENT)
public final class GravityGL {
   private static final FloatBuffer BUF = BufferUtils.createFloatBuffer(16);

   private GravityGL() {
   }

   public static void rotateAboutBlockCenter(GravityDirection dir) {
      if (dir == null || dir == GravityDirection.upTOdown_YN) {
         return;
      }
      double[] m = dir.matrixRotationD;
      GlStateManager.translate(0.5F, 0.5F, 0.5F);
      BUF.clear();
      for (int c = 0; c < 4; c++) {
         for (int r = 0; r < 4; r++) {
            float v;
            if (c < 3 && r < 3) {
               v = (float)m[3 * c + r];
            } else {
               v = c == r ? 1.0F : 0.0F;
            }
            BUF.put(v);
         }
      }
      BUF.flip();
      GlStateManager.multMatrix(BUF);
      GlStateManager.translate(-0.5F, -0.5F, -0.5F);
   }
}
