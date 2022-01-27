package ooo.foooooooooooo.wickedpaintings.client;

import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.render.Tessellator;
import net.minecraft.client.render.VertexFormat;
import net.minecraft.client.render.VertexFormats;
import net.minecraft.client.texture.NativeImage;
import net.minecraft.client.texture.NativeImageBackedTexture;
import net.minecraft.client.util.ScreenshotRecorder;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.item.ItemStack;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;

public class WickedScreen extends Screen {
  private final String imageUrl;
  private final int imageWidth;
  private final int imageHeight;
  private final Identifier imageIdentifier;
  private final byte[] imageBytes;
  private final ItemStack stack;

  public WickedScreen(ItemStack itemStack) {
    super(Text.of("Wicked screen"));
    this.stack = itemStack;

    var nbt = itemStack.getOrCreateNbt();

    imageUrl = nbt.getString("Url");
    imageWidth = nbt.getInt("Width");
    imageHeight = nbt.getInt("Height");
    imageIdentifier = Identifier.tryParse(nbt.getString("Identifier"));
    imageBytes = nbt.getByteArray("Data");
  }


  public void render(MatrixStack matrixStack, int mouseX, int mouseY, float delta) {
    this.renderBackground(matrixStack);

//    RenderSystem.setShaderColor(1F, 1F, 1F, 1F);
    if (this.client == null) return;

    if (imageIdentifier != null) {
      var textureManager = this.client.getTextureManager();
      if (textureManager.getOrDefault(imageIdentifier, null) != null) {
        textureManager.bindTexture(imageIdentifier);
      } else {
        NativeImage nativeImage;
        try {

          nativeImage = ScreenshotRecorder.takeScreenshot(client.getFramebuffer());

//                    nativeImage = NativeImage.read((new ByteArrayInputStream(imageBytes)));
          NativeImageBackedTexture nativeImageBackedTexture = new NativeImageBackedTexture(nativeImage);

          Identifier identifier = textureManager.registerDynamicTexture(
              "wicked_image",
              nativeImageBackedTexture
          );

          textureManager.bindTexture(identifier);
        } catch (Exception e) {
          e.printStackTrace();
        }
      }

      drawImage(matrixStack, width, height, 100);
    }
  }

  private void drawImage(MatrixStack matrixStack, int width, int height, float zLevel) {
    matrixStack.push();

    float imageWidth = 12F;
    float imageHeight = 8F;

    var tessellator = Tessellator.getInstance();
    var buffer = tessellator.getBuffer();
    buffer.begin(VertexFormat.DrawMode.QUADS, VertexFormats.POSITION_TEXTURE);

    float scale = 0.95F;

    float widthScaled = (float) width * scale;
    float heightScaled = (float) height * scale;

    float rs = widthScaled / heightScaled;
    float ri = imageWidth / imageHeight;

    float newWidth;
    float newHeight;

    if (rs > ri) {
      newWidth = imageWidth * heightScaled / imageHeight;
      newHeight = heightScaled;
    } else {
      newWidth = widthScaled;
      newHeight = imageHeight * widthScaled / imageWidth;
    }

    float top = (heightScaled - newHeight) / 2F;
    float left = (widthScaled - newWidth) / 2F;

    left += ((1F - scale) * widthScaled) / 2F;
    top += ((1F - scale) * heightScaled) / 2F;

    buffer.vertex(left, top, zLevel).texture(0F, 0F).next();
    buffer.vertex(left, top + newHeight, zLevel).texture(0F, 1F).next();
    buffer.vertex(left + newWidth, top + newHeight, zLevel).texture(1F, 1F).next();
    buffer.vertex(left + newWidth, top, zLevel).texture(1F, 0F).next();

    tessellator.draw();

    matrixStack.pop();
  }

}
