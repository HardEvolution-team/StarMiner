package com.electrifiedded.starminer.mixin;

import java.util.List;
import java.util.Set;
import org.objectweb.asm.tree.ClassNode;
import org.spongepowered.asm.mixin.extensibility.IMixinConfigPlugin;
import org.spongepowered.asm.mixin.extensibility.IMixinInfo;

public class StarMinerMixinPlugin implements IMixinConfigPlugin {
   private static boolean has(String cls) {
      ClassLoader cl = StarMinerMixinPlugin.class.getClassLoader();
      return cl.getResource(cls.replace('.', '/') + ".class") != null;
   }

   @Override public boolean shouldApplyMixin(String target, String mixin) {
      if (mixin.endsWith(".MixinRenderGlobalTerrain")) {
         return !has("org.taumc.celeritas.mixin.core.terrain.RenderGlobalMixin")
            && !has("me.jellysquid.mods.sodium.client.SodiumClientMod");
      }
      return true;
   }
   @Override public void onLoad(String pkg) {}
   @Override public String getRefMapperConfig() { return null; }
   @Override public void acceptTargets(Set<String> mine, Set<String> others) {}
   @Override public List<String> getMixins() { return null; }
   @Override public void preApply(String t, ClassNode c, String m, IMixinInfo i) {}
   @Override public void postApply(String t, ClassNode c, String m, IMixinInfo i) {}
}
