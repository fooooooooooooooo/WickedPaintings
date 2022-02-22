package ooo.foooooooooooo.wickedpaintings.client;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.texture.NativeImageBackedTexture;
import net.minecraft.util.Identifier;
import ooo.foooooooooooo.wickedpaintings.WickedPaintings;
import org.apache.commons.codec.binary.Base32;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.BufferedInputStream;
import java.io.IOException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

public class ImageLoaderManager {
  private final List<LoadedImage> loadedImages = new ArrayList<>();

  private static final ImageLoaderManager instance = new ImageLoaderManager();

  public static LoadedImage loadImage(Identifier id, String url) {
    for (LoadedImage loadedImage : instance.loadedImages) {
      if (loadedImage.getUrl().equals(url) || loadedImage.getIdentifier().equals(id)) {
        return loadedImage;
      }
    }

    var identifier = generateIdentifier(url);

    byte[] imageData = new byte[0];

    try {
      imageData = getImageData(url);
    } catch (IOException e) {
      WickedPaintings.LOGGER.error("Failed to load image: " + url + "\nException: " + e.getMessage());
    }

    var width = 0;
    var height = 0;

    var image = new LoadedImage(identifier, url, width, height, imageData);

    instance.loadedImages.add(image);

    var texture = new NativeImageBackedTexture(image.getImage());

    MinecraftClient.getInstance().getTextureManager().registerTexture(identifier, texture);

    return image;
  }

  private static byte[] getImageData(String url) throws IOException {
    BufferedInputStream inputStream = new BufferedInputStream(new URL(url).openStream());
    BufferedImage bufferedImage = ImageIO.read(inputStream);

    return new byte[bufferedImage.getWidth() * bufferedImage.getHeight() * 4];
  }

  public static LoadedImage getImage(Identifier identifier) {
    return instance.loadedImages
        .stream()
        .filter(loadedImage -> loadedImage.getIdentifier().equals(identifier))
        .findFirst()
        .orElse(null);
  }

  public static LoadedImage getImage(String url) {

    return instance.loadedImages
        .stream()
        .filter(loadedImage -> loadedImage.getUrl().equals(url))
        .findFirst()
        .orElse(null);
  }

  public static Identifier generateIdentifier(String url) {
    var bytes = new Base32().encode(url.getBytes(StandardCharsets.UTF_8));

    var encoded = new String(bytes, StandardCharsets.UTF_8)
        .replaceAll("=", "").toLowerCase();

    return new Identifier("wicked_images", encoded);
  }
}
