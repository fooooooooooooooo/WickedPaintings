package ooo.foooooooooooo.wickedpaintings.client.render;

import com.mojang.blaze3d.systems.RenderSystem;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gui.DrawableHelper;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.EntityRenderer;
import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.Identifier;
import ooo.foooooooooooo.wickedpaintings.client.ImageLoaderManager;
import ooo.foooooooooooo.wickedpaintings.entity.WickedPaintingEntity;

@Environment(EnvType.CLIENT)
public class WickedPaintingEntityRenderer extends EntityRenderer<WickedPaintingEntity> {
  public WickedPaintingEntityRenderer(EntityRendererFactory.Context context) {
    super(context);
  }

  @Override
  public void render(WickedPaintingEntity entity, float yaw, float tickDelta, MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light) {
    var image = ImageLoaderManager.loadImage(entity.getIdentifier(), entity.getUrl());

    RenderSystem.setShaderTexture(0, image.getIdentifier());
    DrawableHelper.drawTexture(matrices, 0, 0, 0, 0f, 0f, 1, 1, 1, 1);
  }

  @Override
  public Identifier getTexture(WickedPaintingEntity entity) {
    return ImageLoaderManager.loadImage(entity.getIdentifier(), entity.getUrl()).getIdentifier();
  }
}
