package ooo.foooooooooooo.wickedpaintings.network;

import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.Identifier;
import ooo.foooooooooooo.wickedpaintings.common.Constants;

public class ServerBoundPackets {
  public static final Identifier WICKED_UPDATE = new Identifier(Constants.MOD_ID, "wicked_update");

  public static void registerPackets() {
    register(WICKED_UPDATE, (server, player, handler, buffer, sender) -> onWickedUpdate(player, buffer));
  }

  public static void register(Identifier identifier, ServerPlayNetworking.PlayChannelHandler handler) {
    ServerPlayNetworking.registerGlobalReceiver(identifier, handler);
  }

  private static void onWickedUpdate(ServerPlayerEntity player, PacketByteBuf buffer) {
    var slot = buffer.readInt();
    var url = buffer.readString();
    var imageId = buffer.readIdentifier();
    var width = buffer.readInt();
    var height = buffer.readInt();

    var painting = player.getInventory().getStack(slot);
    var nbt = painting.getOrCreateNbt();

    nbt.putString(Constants.URL, url);
    nbt.putString(Constants.IMAGE_ID, imageId.toString());
    nbt.putInt(Constants.WIDTH, width);
    nbt.putInt(Constants.HEIGHT, height);
  }

  public static void sendWickedUpdate(int slot, String url, Identifier imageId, int width, int height) {
    var packet = new WickedPacket(WICKED_UPDATE, PacketByteBufs.create());

    var buffer = packet.buffer();

    buffer.writeInt(slot);
    buffer.writeString(url);
    buffer.writeIdentifier(imageId);
    buffer.writeInt(width);
    buffer.writeInt(height);

    sendPacketToServer(packet);
  }

  public static void sendPacketToServer(WickedPacket packet) {
    ClientPlayNetworking.send(packet.packetId(), packet.buffer());
  }
}
