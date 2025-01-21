package ooo.foooooooooooo.wickedpaintings.client;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.texture.ResourceTexture;
import net.minecraft.resource.ResourceManager;
import ooo.foooooooooooo.wickedpaintings.Constants;
import ooo.foooooooooooo.wickedpaintings.WickedPaintings;

import java.io.IOException;

public class PaintingTexture extends ResourceTexture {
  private boolean loaded;

  public PaintingTexture() {
    super(Constants.DEFAULT_TEX);
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
  }

  public void onLoaded() {
    this.loaded = true;
  }
}
