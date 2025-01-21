package ooo.foooooooooooo.wickedpaintings.network;


import dev.architectury.networking.NetworkManager;
import io.netty.buffer.Unpooled;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.util.Identifier;
import ooo.foooooooooooo.wickedpaintings.Constants;
import ooo.foooooooooooo.wickedpaintings.WickedPaintings;

public class ServerBoundPackets {
  public static final Identifier WICKED_UPDATE = WickedPaintings.id("wicked_update");

  public static void registerPackets() {
    NetworkManager.registerReceiver(NetworkManager.Side.C2S, WICKED_UPDATE, ServerBoundPackets::onWickedUpdate);
  }

  private static void onWickedUpdate(PacketByteBuf buf, NetworkManager.PacketContext context) {
    var slot = buf.readInt();
    var url = buf.readString();
    var width = buf.readInt();
    var height = buf.readInt();

    var painting = context.getPlayer().getInventory().getStack(slot);

    var nbt = painting.getOrCreateNbt();

    nbt.putString(Constants.URL, url);
    nbt.putInt(Constants.WIDTH, width);
    nbt.putInt(Constants.HEIGHT, height);
  }

  public static void sendWickedUpdate(int slot, String url, int width, int height) {
    var buf = new PacketByteBuf(Unpooled.buffer());

    buf.writeInt(slot);
    buf.writeString(url);
    buf.writeInt(width);
    buf.writeInt(height);

    NetworkManager.sendToServer(WICKED_UPDATE, buf);
  }
}
