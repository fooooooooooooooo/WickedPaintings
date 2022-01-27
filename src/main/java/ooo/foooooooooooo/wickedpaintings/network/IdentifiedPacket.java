package ooo.foooooooooooo.wickedpaintings.network;

import net.minecraft.network.PacketByteBuf;
import net.minecraft.util.Identifier;

public record IdentifiedPacket(Identifier channel, PacketByteBuf packetByteBuf) { }
