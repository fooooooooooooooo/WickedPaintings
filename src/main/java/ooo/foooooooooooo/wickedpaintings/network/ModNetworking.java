package ooo.foooooooooooo.wickedpaintings.network;

import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;

public class ModNetworking {
    public static void registerPackets() {
        ClientPlayNetworking.registerGlobalReceiver(Packets.WICKED_SPAWN, WickedEntitySpawnPacket::handle);
    }
}
