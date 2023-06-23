package ooo.foooooooooooo.wickedpaintings.util;

import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.texture.NativeImage;
import net.minecraft.client.texture.NativeImageBackedTexture;
import net.minecraft.util.Identifier;
import ooo.foooooooooooo.wickedpaintings.WickedPaintings;

import org.lwjgl.BufferUtils;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.awt.image.DataBufferByte;
import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URL;
import java.nio.ByteBuffer;
import java.nio.file.Files;
import java.nio.file.Path;

public class ImageUtils {
  public static ImageData downloadImage(String url) throws IOException {
    BufferedInputStream inputStream = new BufferedInputStream(new URL(url).openStream());
    BufferedImage bufferedImage = ImageIO.read(inputStream);

    var pixels = ((DataBufferByte) bufferedImage.getRaster().getDataBuffer()).getData();
    return new ImageData(pixels);
  }

  public static void downloadImageFile(String url, FileOutputStream fileOutputStream) throws IOException {
    BufferedInputStream inputStream = new BufferedInputStream(new URL(url).openStream());

    byte dataBuffer[] = new byte[1024];
    int bytesRead;
    while ((bytesRead = inputStream.read(dataBuffer, 0, 1024)) != -1) {
        fileOutputStream.write(dataBuffer, 0, bytesRead);
    }
  }

  public static BufferedImage downloadBufImage(String url) throws IOException {

    String shreded_url = url.substring(url.lastIndexOf('/')+1);

    Path cache_derectory_String = FabricLoader.getInstance().getGameDir().resolve("wicked_paintings_cache");
    String cache_string_string = cache_derectory_String.resolve(shreded_url).toString();

    File cache_derectory = new File(cache_derectory_String.toString());

    if(!cache_derectory.exists()){
      Files.createDirectories(cache_derectory_String);
    }

    //here is where we cache the image
    File cached_file = new File(cache_string_string);
    if(!cached_file.exists()){
      try {
        FileOutputStream imageFile = new FileOutputStream(cache_derectory_String.resolve(shreded_url).toString());
        ImageUtils.downloadImageFile(url,imageFile);
        imageFile.close();
        url = "file://"+cache_string_string;
        WickedPaintings.LOGGERS.info("Successfully wrote to the file.");
      } catch (IOException e) {
        WickedPaintings.LOGGERS.error("An error occurred.");
        e.printStackTrace();
      }
    }else{
      url = "file://"+cache_string_string;
    }
    
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

    MinecraftClient
      .getInstance()
      .execute(() -> MinecraftClient.getInstance().getTextureManager().registerTexture(identifier, finalTexture));
  }
}

