package ooo.foooooooooooo.wickedpaintings.network;

import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.Identifier;

import java.util.Collection;
import java.util.Collections;
import java.util.function.Consumer;

public class ModNetworking {
  public static IdentifiedPacket createClientBoundPacket(Identifier identifier,
                                                         Consumer<PacketByteBuf> packetBufferConsumer) {
    var buf = PacketByteBufs.create();
    packetBufferConsumer.accept(new PacketByteBuf(buf));
    return new IdentifiedPacket(identifier, buf);
  }

  public static void registerClientBoundHandler(Identifier identifier, ClientPlayNetworking.PlayChannelHandler handler) {
    ClientPlayNetworking.registerGlobalReceiver(identifier, handler);
  }

  public static void sendToPlayer(IdentifiedPacket packet, ServerPlayerEntity serverPlayerEntity) {
    send(packet, Collections.singletonList(serverPlayerEntity));
  }

  public static void send(IdentifiedPacket packet, Collection<ServerPlayerEntity> players) {
    for (ServerPlayerEntity player : players) {
      ServerPlayNetworking.send(player, packet.identifier, packet.buffer);
    }
  }
}
