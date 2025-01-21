package ooo.foooooooooooo.wickedpaintings.util;

import com.mojang.blaze3d.platform.TextureUtil;
import com.mojang.blaze3d.systems.RenderSystem;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.texture.NativeImage;
import net.minecraft.util.Identifier;
import net.minecraft.util.Pair;
import net.minecraft.util.Util;
import ooo.foooooooooooo.wickedpaintings.Constants;
import ooo.foooooooooooo.wickedpaintings.WickedPaintings;
import ooo.foooooooooooo.wickedpaintings.client.PaintingTexture;

import java.io.IOException;
import java.net.URL;
import java.util.HashMap;
import java.util.HashSet;
import java.util.concurrent.CompletableFuture;

@Environment(EnvType.CLIENT)
public class ImageUtils {
  public static final HashMap<Identifier, Pair<Integer, Integer>> DIMENSION_CACHE = new HashMap<>();
  private static final HashSet<Identifier> LOADING = new HashSet<>();
  private static final HashMap<String, Identifier> URL_TO_ID = new HashMap<>();

  private static void registerImage(Identifier id, NativeImage image, String url) {
    try (var texture = new PaintingTexture()) {
      TextureUtil.prepareImage(texture.getGlId(), image.getWidth(), image.getHeight());

      image.upload(0, 0, 0, true);

      MinecraftClient.getInstance().getTextureManager().registerTexture(id, texture);

      texture.onLoaded();

      URL_TO_ID.put(url, id);
    } catch (Exception e) {
      WickedPaintings.LOGGERS.warn("Failed to load image: `{}` id {}", url, id, e);
      URL_TO_ID.put(url, Constants.BLOCKED_TEX);
    } finally {
      LOADING.remove(id);
    }
  }

  public static CompletableFuture<Identifier> getOrLoadImageAsync(String url) {
    var id = URL_TO_ID.get(url);

    if (id != null) {
      return CompletableFuture.completedFuture(id);
    }

    if (url.trim().isEmpty()) {
      return CompletableFuture.completedFuture(Constants.DEFAULT_TEX);
    }

    var newId = DownloadUtil.urlToId(url);

    if (LOADING.contains(newId)) {
      URL_TO_ID.put(url, Constants.DEFAULT_TEX);

      return CompletableFuture.completedFuture(Constants.DEFAULT_TEX);
    }

    try {
      LOADING.add(newId);

      var parsedUrl = new URL(url);

      var future = CompletableFuture.supplyAsync(() -> DownloadUtil.downloadImage(parsedUrl),
        Util.getIoWorkerExecutor()
      );

      return future.thenApply(response -> {
        if (response.tooLarge()) {
          WickedPaintings.LOGGERS.warn("Image is too large: `{}` id {}", url, newId);

          URL_TO_ID.put(url, Constants.BLOCKED_TEX);
          LOADING.remove(newId);

          return Constants.BLOCKED_TEX;
        }

        try (var stream = response.image()) {
          if (stream == null) {
            URL_TO_ID.put(url, Constants.BLOCKED_TEX);
            LOADING.remove(newId);

            return Constants.BLOCKED_TEX;
          }

          var image = NativeImage.read(stream);

          DIMENSION_CACHE.put(newId, new Pair<>(image.getWidth(), image.getHeight()));

          MinecraftClient.getInstance().execute(() -> {
            if (RenderSystem.isOnRenderThread()) {
              registerImage(newId, image, url);
            } else {
              RenderSystem.recordRenderCall(() -> registerImage(newId, image, url));
            }
          });

          return newId;
        } catch (IOException e) {
          WickedPaintings.LOGGERS.warn("Failed to load image: `{}` id {}", url, newId, e);

          URL_TO_ID.put(url, Constants.BLOCKED_TEX);
          LOADING.remove(newId);

          return Constants.BLOCKED_TEX;
        }
      });
    } catch (IOException e) {
      WickedPaintings.LOGGERS.warn("Failed to load image: `{}` id {}", url, newId, e);

      URL_TO_ID.put(url, Constants.BLOCKED_TEX);
      LOADING.remove(newId);

      return CompletableFuture.completedFuture(Constants.BLOCKED_TEX);
    }
  }
}
