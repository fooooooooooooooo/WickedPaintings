package ooo.foooooooooooo.wickedpaintings;

import ooo.foooooooooooo.wickedpaintings.config.IModConfig;
import ooo.foooooooooooo.wickedpaintings.entity.ModEntityTypes;
import ooo.foooooooooooo.wickedpaintings.network.ClientBoundPackets;

public class WickedPaintingsClient {
  public static IModConfig CONFIG;

  public WickedPaintingsClient(IModConfig config) {
    CONFIG = config;
  }

  public void init() {
    ClientBoundPackets.registerPackets();
    ModEntityTypes.registerRenderers();
  }
}
