package ooo.foooooooooooo.wickedpaintings.client;

import net.minecraft.client.texture.NativeImage;
import net.minecraft.util.Identifier;
import ooo.foooooooooooo.wickedpaintings.WickedPaintings;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.Objects;

public final class LoadedImage {
  private final Identifier identifier;
  private final String url;
  private final int width;
  private final int height;
  private final byte[] data;
  private final NativeImage image;

  public LoadedImage(
      Identifier identifier,
      String url,
      int width,
      int height,
      byte[] data
  ) {
    this.identifier = identifier;
    this.url = url;
    this.width = width;
    this.height = height;
    this.data = data;

    NativeImage image = null;

    try {
      image = NativeImage.read(ByteBuffer.wrap(data));
    } catch (IOException e) {
      WickedPaintings.LOGGER.error("Failed to load image: " + url, e);
    }

    this.image = image;
  }

  public Identifier getIdentifier() {
    return this.identifier;
  }

  public String getUrl() {
    return this.url;
  }

  public int getWidth() {
    return this.width;
  }

  public int getHeight() {
    return this.height;
  }

  public byte[] getData() {
    return this.data;
  }

  public Identifier identifier() {
    return identifier;
  }

  public String url() {
    return url;
  }

  public int width() {
    return width;
  }

  public int height() {
    return height;
  }

  public byte[] data() {
    return data;
  }

  @Override
  public boolean equals(Object obj) {
    if (obj == this) return true;
    if (obj == null || obj.getClass() != this.getClass()) return false;
    var that = (LoadedImage) obj;
    return Objects.equals(this.identifier, that.identifier) &&
        Objects.equals(this.url, that.url) &&
        this.width == that.width &&
        this.height == that.height &&
        Objects.equals(this.data, that.data);
  }

  @Override
  public int hashCode() {
    return Objects.hash(identifier, url, width, height, data);
  }

  @Override
  public String toString() {
    return "LoadedImage[" +
        "identifier=" + identifier + ", " +
        "url=" + url + ", " +
        "width=" + width + ", " +
        "height=" + height + ", " +
        "data=" + data + ']';
  }

  public NativeImage getImage() {
    return image;
  }
}
