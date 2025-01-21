package ooo.foooooooooooo.wickedpaintings.util;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.util.Identifier;
import ooo.foooooooooooo.wickedpaintings.WickedPaintings;
import ooo.foooooooooooo.wickedpaintings.WickedPaintingsClient;
import org.jetbrains.annotations.Nullable;

import java.io.IOException;
import java.io.InputStream;
import java.net.URISyntaxException;
import java.net.URL;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.Locale;

import static ooo.foooooooooooo.wickedpaintings.Constants.IMAGES_NAMESPACE;

public class DownloadUtil {
  private static final HttpClient client = HttpClient.newHttpClient();
  private static final String USER_AGENT = "WickedPaintings (Minecraft Mod)";

  public record DownloadedImage(@Nullable InputStream image, boolean tooLarge) {}

  @Environment(EnvType.CLIENT)
  public static DownloadedImage downloadImage(
    URL url
  ) {
    WickedPaintings.LOGGERS.info("Downloading image: {}", url);

    try {
      var request = HttpRequest.newBuilder(url.toURI()).header("User-Agent", USER_AGENT).build();
      var response = client.send(request, HttpResponse.BodyHandlers.ofInputStream());

      var contentLength = response.headers().firstValueAsLong("Content-Length").orElse(-1L);

      if (WickedPaintingsClient.CONFIG.hasMaxSize() && contentLength > WickedPaintingsClient.CONFIG.maxSizeBytes()) {
        WickedPaintings.LOGGERS.warn("image too large: {} ({} bytes)", url, contentLength);
        return new DownloadedImage(null, true);
      }

      if (contentLength < 0) {
        WickedPaintings.LOGGERS.debug("image size unknown: {}", url);
      }

      var code = response.statusCode();

      if (code < 200 || code >= 300) {
        try (var in = response.body()) {
          WickedPaintings.LOGGERS.warn("error downloading image: {}: {}", code, new String(in.readAllBytes()));
        }

        return new DownloadedImage(null, false);
      }

      return new DownloadedImage(response.body(), false);
    } catch (IOException | URISyntaxException e) {
      WickedPaintings.LOGGERS.error("texture download failed", e);
    } catch (InterruptedException e) {
      Thread.currentThread().interrupt();
    }

    return new DownloadedImage(null, false);
  }

  public static Identifier urlToId(String url) {
    var hash = url.hashCode();
    var encoded = Integer.toHexString(hash);

    return Identifier.of(IMAGES_NAMESPACE, encoded.toLowerCase(Locale.ROOT));
  }
}
