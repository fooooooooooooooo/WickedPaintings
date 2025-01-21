package ooo.foooooooooooo.wickedpaintings.platform.fabric;

import me.shedaniel.autoconfig.AutoConfig;
import me.shedaniel.autoconfig.serializer.JanksonConfigSerializer;
import net.fabricmc.api.ModInitializer;
import ooo.foooooooooooo.wickedpaintings.WickedPaintings;
import ooo.foooooooooooo.wickedpaintings.platform.fabric.config.ModConfigFabric;

public class WickedPaintingsFabric implements ModInitializer {
  @Override
  public void onInitialize() {
    WickedPaintings.init();

    AutoConfig.register(ModConfigFabric.class, JanksonConfigSerializer::new);
  }
}
