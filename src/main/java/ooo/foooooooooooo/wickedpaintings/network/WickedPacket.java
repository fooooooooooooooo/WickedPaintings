package ooo.foooooooooooo.wickedpaintings.network;

import net.minecraft.network.PacketByteBuf;
import net.minecraft.util.Identifier;

public class WickedPacket {
    public Identifier packetId;
    public PacketByteBuf buffer;

    public WickedPacket(Identifier packetId, PacketByteBuf buffer) {
        this.packetId = packetId;
        this.buffer = buffer;
    }
}
