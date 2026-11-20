package com.electrifiedded.starminer.core;

import java.util.Map;
import net.minecraftforge.fml.relauncher.IFMLLoadingPlugin;

@IFMLLoadingPlugin.Name("StarMinerCore")
@IFMLLoadingPlugin.MCVersion("1.12.2")
public class SMPlugin implements IFMLLoadingPlugin {
   @Override
   public String[] getASMTransformerClass() {
      return new String[0];
   }

   @Override
   public String getModContainerClass() {
      return null;
   }

   @Override
   public String getSetupClass() {
      return null;
   }

   @Override
   public void injectData(Map<String, Object> data) {
   }

   @Override
   public String getAccessTransformerClass() {
      return null;
   }
}
