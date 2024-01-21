package ooo.foooooooooooo.wickedpaintings.util;

import net.minecraft.client.MinecraftClient;
import net.minecraft.util.Identifier;
import ooo.foooooooooooo.wickedpaintings.WickedPaintings;
import ooo.foooooooooooo.wickedpaintings.client.PaintingTexture;

import java.net.MalformedURLException;
import java.util.Locale;

public class ImageUtils {
  public static final String IMAGES_NAMESPACE = "wicked_image";
  public static final Identifier DEFAULT_TEX = new Identifier(WickedPaintings.MOD_ID, "textures/block/wicked_painting/default.png");
  public static final Identifier BLOCKED_TEX = new Identifier(WickedPaintings.MOD_ID, "textures/block/wicked_painting/blocked.png");

  public static Identifier getOrLoadImage(Identifier id, String url) {
    var manager = MinecraftClient.getInstance().getTextureManager();
    var texture = manager.getOrDefault(id, null);

    if (texture == null) {
      try {
        var painting = new PaintingTexture(url);

        if (painting.tooLarge) {
          return BLOCKED_TEX;
        }

        manager.registerTexture(id, painting);
      } catch (MalformedURLException e) {
        return DEFAULT_TEX;
      }
    }

    return id;
  }

  public static Identifier generateImageId(String url) {
    var hash = url.hashCode();
    var encoded = Integer.toHexString(hash);

    return new Identifier(IMAGES_NAMESPACE, encoded.toLowerCase(Locale.ROOT));
  }
}
