package ooo.foooooooooooo.wickedpaintings.client.screen;

import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.Drawable;
import net.minecraft.client.gui.widget.ClickableWidget;
import net.minecraft.client.gui.widget.Widget;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.util.Identifier;
import ooo.foooooooooooo.wickedpaintings.WickedPaintings;
import ooo.foooooooooooo.wickedpaintings.util.ImageUtils;

import java.util.function.Consumer;

public class ImageWidget implements Drawable, Widget {
  private Identifier texture;

  private int x;
  private int y;

  private final int width;
  private final int height;

  private int scaledTextureWidth = 0;
  private int scaledTextureHeight = 0;

  private int x1;
  private int y1;
  private int x2;
  private int y2;

  private static final int BORDER_SIZE = 1;

  public ImageWidget(
    int x, int y, int width, int height, Identifier texture
  ) {
    this.x = x;
    this.y = y;
    this.width = width;
    this.height = height;
    this.texture = texture;
    this.loadDimensions();
  }

  public void setTexture(Identifier texture) {
    this.texture = texture;
    this.loadDimensions();
  }

  void loadDimensions() {
    var cachedDimensions = ImageUtils.DIMENSION_CACHE.get(this.texture);

    var textureWidth = 0;
    var textureHeight = 0;

    if (cachedDimensions != null) {
      textureWidth = cachedDimensions.getLeft();
      textureHeight = cachedDimensions.getRight();
    } else {
      WickedPaintings.LOGGERS.warn("ImageWidget texture dimensions are not cached");
      textureWidth = 1;
      textureHeight = 1;
    }

    // fit x1, y1, x2, y2 to the aspect ratio of the image, filling the available space from x, y, width, height
    if (textureWidth > 0 && textureHeight > 0) {
      var viewportWidth = this.width - (BORDER_SIZE * 2);
      var viewportHeight = this.height - (BORDER_SIZE * 2);

      if (viewportWidth == 0 || viewportHeight == 0) {
        WickedPaintings.LOGGERS.warn("ImageWidget viewport dimensions are invalid");
        return;
      }

      var scale = Math.min(
        (float) viewportWidth / (float) textureWidth,
        (float) viewportHeight / (float) textureHeight
      );

      this.scaledTextureWidth = (int) (textureWidth * scale);
      this.scaledTextureHeight = (int) (textureHeight * scale);

      var viewportLeft = this.x + BORDER_SIZE;
      var viewportTop = this.y + BORDER_SIZE;

      this.x1 = viewportLeft + (viewportWidth - this.scaledTextureWidth) / 2;
      this.y1 = viewportTop + (viewportHeight - this.scaledTextureHeight) / 2;
      this.x2 = this.x1 + this.scaledTextureWidth;
      this.y2 = this.y1 + this.scaledTextureHeight;

    } else {
      WickedPaintings.LOGGERS.warn("ImageWidget texture dimensions are invalid");
    }
  }

  @Override
  public void render(DrawContext context, int mouseX, int mouseY, float delta) {
    if (this.scaledTextureWidth == 0 && this.scaledTextureHeight == 0) {
      var cachedDimensions = ImageUtils.DIMENSION_CACHE.get(this.texture);
      if (cachedDimensions != null) {
        this.loadDimensions();
      }
    }

    context.drawNineSlicedTexture(
      WickedPaintingScreen.WICKED_TEXTURE,
      this.x,
      this.y,
      this.width,
      this.height,
      8,
      18,
      18,
      1,
      36
    );

    context.drawTexture(
      this.texture,
      this.x1,
      this.y1,
      0,
      0,
      this.x2 - this.x1,
      this.y2 - this.y1,
      this.scaledTextureWidth,
      this.scaledTextureHeight
    );
  }

  @Override
  public void setX(int x) {
    this.x = x;
  }

  @Override
  public void setY(int y) {
    this.y = y;
  }

  @Override
  public int getX() {
    return this.x;
  }

  @Override
  public int getY() {
    return this.y;
  }

  @Override
  public int getWidth() {
    return this.width;
  }

  @Override
  public int getHeight() {
    return this.height;
  }

  @Override
  public void forEachChild(Consumer<ClickableWidget> consumer) {
    // no children
  }
}
