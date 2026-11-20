package com.electrifiedded.starminer.core;

import java.io.File;
import com.electrifiedded.starminer.core.common.CoreProxy;
import com.electrifiedded.starminer.core.common.SMCommonEventHandler;
import com.electrifiedded.starminer.core.packet.SMCorePacketSender;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.fml.common.FMLCommonHandler;

public final class SMCore {
   public static CoreProxy proxy = new CoreProxy();

   private SMCore() {
   }

   public static void preInit(File configDir) {
      try {
         proxy = FMLCommonHandler.instance().getSide().isClient()
            ? (CoreProxy)Class.forName("com.electrifiedded.starminer.core.common.CoreProxyClient").getDeclaredConstructor().newInstance()
            : new CoreProxy();
      } catch (ReflectiveOperationException e) {
         throw new RuntimeException(e);
      }

      try {
         Configuration config = new Configuration(new File(configDir, "Starminer.cfg"));
         config.load();
         config.getCategory(CoreConfig.CONFIG_CAT_SERVERSIDE);
         config.addCustomCategoryComment(CoreConfig.CONFIG_CAT_SERVERSIDE, "Affects server side only.");
         CoreConfig.skipIllegalStanceCheck = config.get(CoreConfig.CONFIG_CAT_SERVERSIDE, "skipIllegalStanceCheck", true).getBoolean(true);
         CoreConfig.illegalGStateTickToCheck = config.get(CoreConfig.CONFIG_CAT_SERVERSIDE, "unsynchronizedTickToCheck", 100).getInt(100);
         CoreConfig.showUnsynchronizedWarning = config.get(CoreConfig.CONFIG_CAT_SERVERSIDE, "showUnsynchronizedWarning", true).getBoolean(true);
         CoreConfig.unsynchronizedWarnToKick = config.get(CoreConfig.CONFIG_CAT_SERVERSIDE, "unsynchronizedWarnToKick", 0).getInt(0);
         if (config.hasChanged()) {
            config.save();
         }
      } catch (Exception e) {
         e.printStackTrace();
      }

      SMCorePacketSender.init();
   }

   public static void init() {
      MinecraftForge.EVENT_BUS.register(new SMCommonEventHandler());
   }
}
