package ooo.foooooooooooo.wickedpaintings;

import me.shedaniel.autoconfig.AutoConfig;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import ooo.foooooooooooo.wickedpaintings.config.ModConfig;
import ooo.foooooooooooo.wickedpaintings.entity.ModEntityTypes;
import ooo.foooooooooooo.wickedpaintings.network.ModNetworking;

@Environment(EnvType.CLIENT)
public class WickedPaintingsClient implements ClientModInitializer {
  public static final ModConfig CONFIG = AutoConfig.getConfigHolder(ModConfig.class).getConfig();

  @Override
  public void onInitializeClient() {
    ModEntityTypes.registerRenderers();
    ModNetworking.registerClientBoundPackets();
  }
}
