package ooo.foooooooooooo.wickedpaintings.network;

import net.minecraft.network.PacketByteBuf;
import net.minecraft.util.Identifier;

public record WickedPacket(Identifier packetId, PacketByteBuf buffer) { }
