package ooo.foooooooooooo.wickedpaintings.platform.fabric;

import me.shedaniel.autoconfig.AutoConfig;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import ooo.foooooooooooo.wickedpaintings.WickedPaintingsClient;
import ooo.foooooooooooo.wickedpaintings.platform.fabric.config.ModConfigFabric;

@Environment(EnvType.CLIENT)
public class WickedPaintingsFabricClient implements ClientModInitializer {
  public ModConfigFabric config;

  @Override
  public void onInitializeClient() {
    this.config = AutoConfig.getConfigHolder(ModConfigFabric.class).getConfig();

    new WickedPaintingsClient(this.config).init();
  }
}
