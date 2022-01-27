package ooo.foooooooooooo.wickedpaintings.network;

import net.minecraft.util.Identifier;
import ooo.foooooooooooo.wickedpaintings.image.ImageLoaderManager;
import ooo.foooooooooooo.wickedpaintings.mod.WickedPaintings;

import java.util.List;

public class ClientBoundPackets {
  public static final Identifier SYNC_IMAGES = new Identifier(WickedPaintings.MOD_ID, "sync_images");

  public static IdentifiedPacket createPacketSyncLoadedImages(List<ImageLoaderManager.LoadedImage> images) {
    return ModNetworking.createClientBoundPacket(SYNC_IMAGES, extendedPacketBuffer -> {
      extendedPacketBuffer.writeCodec(ImageLoaderManager.CODEC, images);
    });
  }
}
