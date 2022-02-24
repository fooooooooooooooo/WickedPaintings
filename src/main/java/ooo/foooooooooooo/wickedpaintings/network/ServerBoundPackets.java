package ooo.foooooooooooo.wickedpaintings.network;

import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.fabricmc.fabric.api.networking.v1.PacketSender;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayNetworkHandler;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.Identifier;
import ooo.foooooooooooo.wickedpaintings.NbtConstants;
import ooo.foooooooooooo.wickedpaintings.WickedPaintings;

public class ServerBoundPackets {
    public static final Identifier WICKED_UPDATE = new Identifier(WickedPaintings.MOD_ID, "wicked_update");

    public static void registerPackets() {
        registerServerBoundHandler(WICKED_UPDATE, ServerBoundPackets::onWickedUpdate);
    }

    public static void registerServerBoundHandler(Identifier identifier, ServerPlayNetworking.PlayChannelHandler handler) {
        ServerPlayNetworking.registerGlobalReceiver(identifier, handler);
    }

    public static void sendPacketToServer(WickedPacket packet) {
        ClientPlayNetworking.send(packet.packetId, packet.buffer);
    }

    public static void sendWickedUpdate(int slot, String url, Identifier imageId, int width, int height) {
        var packet = new WickedPacket(WICKED_UPDATE, PacketByteBufs.create());

        packet.buffer.writeInt(slot);
        packet.buffer.writeString(url);
        packet.buffer.writeIdentifier(imageId);
        packet.buffer.writeInt(width);
        packet.buffer.writeInt(height);

        sendPacketToServer(packet);
    }

    private static void onWickedUpdate(MinecraftServer server, ServerPlayerEntity player, ServerPlayNetworkHandler handler, PacketByteBuf buffer, PacketSender sender) {
        var slot = buffer.readInt();
        var url = buffer.readString();
        var imageId = buffer.readIdentifier();
        var width = buffer.readInt();
        var height = buffer.readInt();

        var painting = player.getInventory().getStack(slot);
        var nbt = painting.getOrCreateNbt();

        nbt.putString(NbtConstants.URL, url);
        nbt.putString(NbtConstants.IMAGE_ID, imageId.toString());
        nbt.putInt(NbtConstants.WIDTH, width);
        nbt.putInt(NbtConstants.HEIGHT, height);
    }
}
