package ooo.foooooooooooo.wickedpaintings.client;

import com.mojang.blaze3d.platform.TextureUtil;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.texture.NativeImage;
import net.minecraft.client.texture.ResourceTexture;
import net.minecraft.resource.ResourceManager;
import net.minecraft.util.Util;
import ooo.foooooooooooo.wickedpaintings.WickedPaintings;
import ooo.foooooooooooo.wickedpaintings.WickedPaintingsClient;
import org.jetbrains.annotations.Nullable;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.http.HttpClient;
import java.util.concurrent.CompletableFuture;

import static ooo.foooooooooooo.wickedpaintings.util.ImageUtils.DEFAULT_TEX;

public class PaintingTexture extends ResourceTexture {
  private static final HttpClient CLIENT = HttpClient.newBuilder().build();
  private static final String USER_AGENT = "WickedPaintings (Minecraft Mod)";

  public boolean tooLarge = false;

  private final URL url;
  private boolean loaded;
  private CompletableFuture<?> loader;

  public PaintingTexture(String url) throws MalformedURLException {
    super(DEFAULT_TEX);
    this.url = new URL(url);
  }

  private void upload(NativeImage image) {
    TextureUtil.prepareImage(this.getGlId(), image.getWidth(), image.getHeight());
    image.upload(0, 0, 0, true);
  }


  @Override
  public void load(ResourceManager manager) {
    MinecraftClient.getInstance().execute(() -> {
      if (!this.loaded) {
        try {
          super.load(manager);
        } catch (IOException e) {
          WickedPaintings.LOGGERS.warn("failed to load texture: {}", this.location, e);
        }

        this.loaded = true;
      }
    });

    if (this.loader == null) {
      this.loader = CompletableFuture.runAsync(this::download, Util.getDownloadWorkerExecutor());
    }
  }

  @Nullable
  private NativeImage loadTexture(InputStream stream) {
    NativeImage nativeImage = null;

    try {
      nativeImage = NativeImage.read(stream);
    } catch (Exception e) {
      WickedPaintings.LOGGERS.warn("error loading texture", e);
    }

    return nativeImage;
  }

  private void download() {
    try {
      var connection = (HttpURLConnection) this.url.openConnection();
      connection.setRequestMethod("GET");
      connection.setRequestProperty("User-Agent", USER_AGENT);
      connection.connect();

      var contentLength = connection.getContentLengthLong();

      if (WickedPaintingsClient.CONFIG.hasMaxSize() && contentLength > WickedPaintingsClient.CONFIG.maxSizeBytes()) {
        this.tooLarge = true;
        WickedPaintings.LOGGERS.warn("image too large: {} ({} bytes)", this.url, contentLength);
        return;
      }

      if (contentLength < 0) {
        WickedPaintings.LOGGERS.debug("image size unknown: {}", this.url);
      }

      var code = connection.getResponseCode();

      if (code / 100 != 2) {
        WickedPaintings.LOGGERS.warn("error downloading texture: {}: {}", code, connection.getResponseMessage());
        return;
      }

      NativeImage image = this.loadTexture(connection.getInputStream());

      MinecraftClient.getInstance().execute(() -> {
        if (image != null) {
          MinecraftClient.getInstance().execute(() -> {
            this.loaded = true;

            if (!RenderSystem.isOnRenderThread()) {
              RenderSystem.recordRenderCall(() -> this.upload(image));
            } else {
              this.upload(image);
            }
          });
        }
      });
    } catch (IOException e) {
      WickedPaintings.LOGGERS.error("texture download failed", e);
    }
  }
}
