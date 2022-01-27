package ooo.foooooooooooo.wickedpaintings.network;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import ooo.foooooooooooo.wickedpaintings.image.ImageLoaderManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;


@Environment(EnvType.CLIENT)
public class ClientBoundPacketHandlers {
  private static final Logger LOGGER = LoggerFactory.getLogger(ClientBoundPacketHandlers.class);

  public static void init() {
    ModNetworking.registerClientBoundHandler(
        ClientBoundPackets.SYNC_IMAGES, (client, handler, packetBuffer, responseSender) -> {
          List<ImageLoaderManager.LoadedImage> images =
              new ExtendedPacketBuffer(packetBuffer).readCodec(ImageLoaderManager.CODEC);

          LOGGER.info("Received {} images from server", images.size());
//          client.execute(() -> ClientChunkManager.setLoadedChunks(images));
        });
  }
}
