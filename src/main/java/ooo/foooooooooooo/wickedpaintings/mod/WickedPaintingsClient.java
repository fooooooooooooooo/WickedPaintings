package ooo.foooooooooooo.wickedpaintings.mod;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import ooo.foooooooooooo.wickedpaintings.network.ClientBoundPacketHandlers;

@Environment(EnvType.CLIENT)
public class WickedPaintingsClient implements ClientModInitializer {
  @Override
  public void onInitializeClient() {
    ClientBoundPacketHandlers.init();
  }
}
