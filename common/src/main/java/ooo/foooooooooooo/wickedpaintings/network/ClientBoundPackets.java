package ooo.foooooooooooo.wickedpaintings.network;

import dev.architectury.networking.NetworkManager;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.util.Identifier;
import ooo.foooooooooooo.wickedpaintings.WickedPaintings;

@Environment(EnvType.CLIENT)
public class ClientBoundPackets {
  public static final Identifier WICKED_SPAWN = WickedPaintings.id("wicked_spawn");

  public static void registerPackets() {
    NetworkManager.registerReceiver(NetworkManager.Side.S2C, WICKED_SPAWN, WickedEntitySpawnPacket::handle);
  }
}
