package ooo.foooooooooooo.wickedpaintings.platform.forge;

import dev.architectury.platform.forge.EventBuses;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.config.ModConfig;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.fml.loading.FMLEnvironment;
import ooo.foooooooooooo.wickedpaintings.WickedPaintings;
import ooo.foooooooooooo.wickedpaintings.WickedPaintingsClient;
import ooo.foooooooooooo.wickedpaintings.platform.forge.config.ModConfigForge;

@Mod(WickedPaintings.MOD_ID)
public class WickedPaintingsForge {
  ModConfigForge config;

  public WickedPaintingsForge() {
    EventBuses.registerModEventBus(WickedPaintings.MOD_ID, FMLJavaModLoadingContext.get().getModEventBus());

    if (FMLEnvironment.dist.isClient()) {
      this.config = new ModConfigForge();

      ModLoadingContext
        .get()
        .registerConfig(ModConfig.Type.COMMON, ModConfigForge.SPEC, WickedPaintings.MOD_ID + ".toml");

      new WickedPaintingsClient(this.config).init();
    }

    WickedPaintings.init();
  }
}
