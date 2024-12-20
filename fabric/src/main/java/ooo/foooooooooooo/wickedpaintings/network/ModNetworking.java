package ooo.foooooooooooo.wickedpaintings.network;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;

public class ModNetworking {
  @Environment(EnvType.CLIENT)
  public static void registerClientBoundPackets() {
    ClientPlayNetworking.registerGlobalReceiver(
      Packets.WICKED_SPAWN,
      (client, handler, buffer, sender) -> WickedEntitySpawnPacket.handle(client, handler, buffer)
    );
  }

  public static void registerServerBoundPackets() {
    ServerBoundPackets.registerPackets();
  }
}
