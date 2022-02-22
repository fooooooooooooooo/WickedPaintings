package ooo.foooooooooooo.wickedpaintings.network;

import net.minecraft.network.PacketByteBuf;
import net.minecraft.util.Identifier;

public class IdentifiedPacket {
  public final PacketByteBuf buffer;
  public final Identifier identifier;

  public IdentifiedPacket(Identifier identifier, PacketByteBuf buffer) {
    this.identifier = identifier;
    this.buffer = buffer;
  }
}
