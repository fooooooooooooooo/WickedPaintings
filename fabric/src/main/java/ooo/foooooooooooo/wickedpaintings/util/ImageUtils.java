package ooo.foooooooooooo.wickedpaintings.util;

import com.mojang.blaze3d.platform.TextureUtil;
import com.mojang.blaze3d.systems.RenderSystem;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.texture.NativeImage;
import net.minecraft.client.texture.TextureManager;
import net.minecraft.util.Identifier;
import net.minecraft.util.Pair;
import net.minecraft.util.Util;
import ooo.foooooooooooo.wickedpaintings.WickedPaintings;
import ooo.foooooooooooo.wickedpaintings.WickedPaintingsClient;
import ooo.foooooooooooo.wickedpaintings.client.PaintingTexture;
import ooo.foooooooooooo.wickedpaintings.common.Constants;
import ooo.foooooooooooo.wickedpaintings.common.DownloadUtil;

import java.io.IOException;
import java.net.URL;
import java.util.HashMap;
import java.util.HashSet;
import java.util.concurrent.CompletableFuture;

public class ImageUtils {
  public static final HashMap<Identifier, Pair<Integer, Integer>> DIMENSION_CACHE = new HashMap<>();
  private static final HashSet<Identifier> LOADING = new HashSet<>();

  private static void upload(int glId, NativeImage image) {
    TextureUtil.prepareImage(glId, image.getWidth(), image.getHeight());
    image.upload(0, 0, 0, true);
  }

  private static void registerImage(TextureManager manager, Identifier id, NativeImage image, String url) {
    try (var texture = new PaintingTexture()) {
      upload(texture.getGlId(), image);
      manager.registerTexture(id, texture);

      texture.onLoaded();
    } catch (Exception e) {
      WickedPaintings.LOGGERS.warn("Failed to load image: `{}` id {}", url, id, e);
    }
  }

  @Environment(EnvType.CLIENT)
  public static CompletableFuture<Identifier> getOrLoadImageAsync(String url) {
    var id = DownloadUtil.urlToId(url);
    var manager = MinecraftClient.getInstance().getTextureManager();
    var existingTexture = manager.getOrDefault(id, null);

    if (existingTexture == null) {
      if (LOADING.contains(id)) {
        return CompletableFuture.completedFuture(Constants.DEFAULT_TEX);
      }

      try {
        LOADING.add(id);

        var parsedUrl = new URL(url);

        var future = CompletableFuture.supplyAsync(() -> DownloadUtil.downloadImage(
          parsedUrl,
          WickedPaintingsClient.CONFIG.hasMaxSize(),
          WickedPaintingsClient.CONFIG.maxSizeBytes(),
          WickedPaintings.LOGGERS
        ), Util.getIoWorkerExecutor());

        return future.thenApply(response -> {

          if (response.tooLarge()) {
            WickedPaintings.LOGGERS.warn("Image is too large: `{}` id {}", url, id);
            return Constants.BLOCKED_TEX;
          }

          try (var stream = response.image()) {
            if (stream == null) return Constants.BLOCKED_TEX;

            var image = NativeImage.read(stream);

            DIMENSION_CACHE.put(id, new Pair<>(image.getWidth(), image.getHeight()));

            MinecraftClient.getInstance().execute(() -> {
              if (RenderSystem.isOnRenderThread()) {
                registerImage(manager, id, image, url);
              } else {
                RenderSystem.recordRenderCall(() -> registerImage(manager, id, image, url));
              }
            });

            return id;
          } catch (IOException e) {
            WickedPaintings.LOGGERS.warn("Failed to load image: `{}` id {}", url, id, e);
            return Constants.BLOCKED_TEX;
          }
        });
      } catch (IOException e) {
        WickedPaintings.LOGGERS.warn("Failed to load image: `{}` id {}", url, id, e);
        return CompletableFuture.completedFuture(Constants.BLOCKED_TEX);
      } finally {
        LOADING.remove(id);
      }
    }

    return CompletableFuture.completedFuture(id);
  }
}
