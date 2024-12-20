package ooo.foooooooooooo.wickedpaintings.common;

import net.minecraft.util.Identifier;
import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;

import java.io.IOException;
import java.io.InputStream;
import java.net.URISyntaxException;
import java.net.URL;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.Locale;

import static ooo.foooooooooooo.wickedpaintings.common.Constants.IMAGES_NAMESPACE;

public class DownloadUtil {
  private static final HttpClient client = HttpClient.newHttpClient();
  private static final String USER_AGENT = "WickedPaintings (Minecraft Mod)";

  public record DownloadedImage(@Nullable InputStream image, boolean tooLarge) {}

  public static DownloadedImage downloadImage(
    URL url, boolean hasMaxSize, long maxSizeBytes, Logger logger
  ) {
    try {
      var request = HttpRequest.newBuilder(url.toURI()).header("User-Agent", USER_AGENT).build();
      var response = client.send(request, HttpResponse.BodyHandlers.ofInputStream());

      var contentLength = response.headers().firstValueAsLong("Content-Length").orElse(-1L);

      if (hasMaxSize && contentLength > maxSizeBytes) {
        logger.warn("image too large: {} ({} bytes)", url, contentLength);
        return new DownloadedImage(null, true);
      }

      if (contentLength < 0) {
        logger.debug("image size unknown: {}", url);
      }

      var code = response.statusCode();

      if (code < 200 || code >= 300) {
        try (var in = response.body()) {
          logger.warn("error downloading image: {}: {}", code, new String(in.readAllBytes()));
        }
        return new DownloadedImage(null, false);
      }

      return new DownloadedImage(response.body(), false);
    } catch (IOException | URISyntaxException e) {
      logger.error("texture download failed", e);
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
