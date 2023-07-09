package ooo.foooooooooooo.wickedpaintings.util;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.texture.NativeImage;
import net.minecraft.client.texture.NativeImageBackedTexture;
import net.minecraft.util.Identifier;
import org.lwjgl.BufferUtils;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.awt.image.DataBufferByte;
import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.net.URL;
import java.nio.ByteBuffer;

public class ImageUtils {
  public static ImageData downloadImage(String url) throws IOException {
    BufferedInputStream inputStream = new BufferedInputStream(new URL(url).openStream());
    BufferedImage bufferedImage = ImageIO.read(inputStream);

    var pixels = ((DataBufferByte) bufferedImage.getRaster().getDataBuffer()).getData();
    return new ImageData(pixels);
  }

  public static BufferedImage downloadBufImage(String url) throws IOException {
    BufferedInputStream inputStream = new BufferedInputStream(new URL(url).openStream());

    return ImageIO.read(inputStream);
  }

  public static void saveBufferedImageAsIdentifier(BufferedImage bufferedImage, Identifier identifier) {
    NativeImageBackedTexture texture;

    try {
      ByteArrayOutputStream stream = new ByteArrayOutputStream();
      ImageIO.write(bufferedImage, "png", stream);
      byte[] bytes = stream.toByteArray();

      ByteBuffer data = BufferUtils.createByteBuffer(bytes.length).put(bytes);
      data.flip();
      NativeImage img = NativeImage.read(data);
      texture = new NativeImageBackedTexture(img);
    } catch (Exception e) {
      texture = new NativeImageBackedTexture(new NativeImage(1, 1, false));
    }

    NativeImageBackedTexture finalTexture = texture;

    MinecraftClient.getInstance().execute(() -> MinecraftClient.getInstance().getTextureManager().registerTexture(identifier, finalTexture));
  }
}
