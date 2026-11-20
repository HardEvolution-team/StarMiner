package com.electrifiedded.starminer.basics.block.bed;

import java.util.ArrayList;
import java.util.List;
import com.electrifiedded.archaiclib.v1710.api.block.ArchaicRenderPart;
import com.electrifiedded.archaiclib.v1710.api.client.IIcon;
import com.electrifiedded.starminer.basics.block.SMDir;
import net.minecraft.util.EnumFacing;

public final class StarBedModel {
   private StarBedModel() {
   }

   public static final int KEY_NONE = 63;

   public static int key(int grav, int conn) {
      if (grav < 0 || grav > 5 || conn < 0 || conn > 5) {
         return KEY_NONE;
      }

      return grav | conn << 3;
   }

   public static List<ArchaicRenderPart> build(
      int key, boolean head, IIcon top, IIcon end, IIcon side, IIcon star, IIcon planks
   ) {
      List<ArchaicRenderPart> parts = new ArrayList<>();
      if (key == KEY_NONE) {
         return parts;
      }

      int grav = key & 7;
      int conn = key >> 3 & 7;
      EnumFacing ground = SMDir.ATTACH[grav];
      double[] uv = {-ground.getXOffset(), -ground.getYOffset(), -ground.getZOffset()};
      int[] c = StarBedUtil.CHECKNEIGHBOR_LIST[conn];
      double[] hv = {c[0], c[1], c[2]};
      if (uv[0] * hv[0] + uv[1] * hv[1] + uv[2] * hv[2] != 0.0) {
         return parts;
      }

      double[] lv = cross(uv, hv);
      double h = StarBedUtil.HEIGHT_D;

      parts.add(plane(planks, neg(uv), 0.1875, uv, hv, lv).color(0.5F, 0.5F, 0.5F));
      parts.add(plane(top, uv, h, uv, hv, lv));
      if (!head && star != null) {
         parts.add(plane(star, uv, h + 0.02, uv, hv, lv));
      }

      for (int s = 0; s < 2; s++) {
         double[] n = s == 0 ? neg(lv) : lv;
         parts.add(sidePlane(side, n, s, uv, hv, lv, false, 0.8F));
      }

      double[] n = head ? hv : neg(hv);
      parts.add(sidePlane(end, n, head ? 1 : 0, uv, hv, lv, true, 0.8F));
      return parts;
   }

   private static ArchaicRenderPart plane(IIcon icon, double[] normal, double u, double[] uv, double[] hv, double[] lv) {
      double[][] corners = {{0, 0}, {1, 0}, {1, 1}, {0, 1}};
      double[][] v = new double[4][];
      for (int i = 0; i < 4; i++) {
         double a = corners[i][0];
         double l = corners[i][1];
         double[] p = world(uv, hv, lv, u, a, l);
         v[i] = new double[]{p[0], p[1], p[2], 16.0 * a, 16.0 * l};
      }

      return quad(icon, normal, v);
   }

   private static ArchaicRenderPart sidePlane(IIcon icon, double[] normal, int at, double[] uv, double[] hv, double[] lv, boolean endPlane, float shade) {
      double[][] corners = {{0, 0}, {0, 1}, {1, 1}, {1, 0}};
      double[][] v = new double[4][];
      for (int i = 0; i < 4; i++) {
         double u = corners[i][0];
         double t = corners[i][1];
         double[] p;
         double tu;
         if (endPlane) {
            p = world(uv, hv, lv, u, at, t);
            tu = at == 1 ? 16.0 * (1.0 - t) : 16.0 * t;
         } else {
            p = world(uv, hv, lv, u, t, at);
            tu = 16.0 * t;
         }

         v[i] = new double[]{p[0], p[1], p[2], tu, 16.0 * (1.0 - u)};
      }

      return quad(icon, normal, v).color(shade, shade, shade);
   }

   private static ArchaicRenderPart quad(IIcon icon, double[] normal, double[][] v) {
      double[] e1 = {v[1][0] - v[0][0], v[1][1] - v[0][1], v[1][2] - v[0][2]};
      double[] e2 = {v[2][0] - v[0][0], v[2][1] - v[0][1], v[2][2] - v[0][2]};
      double[] n = cross(e1, e2);
      if (n[0] * normal[0] + n[1] * normal[1] + n[2] * normal[2] < 0.0) {
         return ArchaicRenderPart.quad(icon, v[0], v[3], v[2], v[1]);
      }

      return ArchaicRenderPart.quad(icon, v[0], v[1], v[2], v[3]);
   }

   private static double[] world(double[] uv, double[] hv, double[] lv, double u, double a, double l) {
      double[] r = new double[3];
      for (int i = 0; i < 3; i++) {
         r[i] = 0.5 + (u - 0.5) * uv[i] + (a - 0.5) * hv[i] + (l - 0.5) * lv[i];
      }

      return r;
   }

   private static double[] cross(double[] a, double[] b) {
      return new double[]{a[1] * b[2] - a[2] * b[1], a[2] * b[0] - a[0] * b[2], a[0] * b[1] - a[1] * b[0]};
   }

   private static double[] neg(double[] a) {
      return new double[]{-a[0], -a[1], -a[2]};
   }
}
