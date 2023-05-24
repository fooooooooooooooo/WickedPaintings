package ooo.foooooooooooo.wickedpaintings.client.render;

import me.shedaniel.autoconfig.AutoConfig;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.render.*;
import net.minecraft.client.render.entity.EntityRenderer;
import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import ooo.foooooooooooo.wickedpaintings.client.ImageManager;
import ooo.foooooooooooo.wickedpaintings.config.ModConfig;
import ooo.foooooooooooo.wickedpaintings.entity.WickedPaintingEntity;
import org.joml.Matrix3f;
import org.joml.Matrix4f;
import org.joml.Quaternionf;

@Environment(EnvType.CLIENT)
public class WickedPaintingEntityRenderer extends EntityRenderer<WickedPaintingEntity> {
  // doesn't z fight at 500% entity distance
  private static final float WallOffset = -0.02f;
  private final ModConfig config;

  public WickedPaintingEntityRenderer(EntityRendererFactory.Context context) {
    super(context);

    this.config = AutoConfig.getConfigHolder(ModConfig.class).getConfig();
  }

  @Override
  public void render(
    WickedPaintingEntity entity,
    float _yaw,
    float tickDelta,
    MatrixStack matrices,
    VertexConsumerProvider consumerProvider,
    int light
  ) {
    if (!config.enabled) {
      return;
    }

    var pitch = entity.getPitch();
    var yaw = entity.getYaw();

    var width = entity.getRealWidth();
    var height = entity.getRealHeight();

    matrices.push();

    matrices.multiply(quaternionFromEulerAngles(180 - pitch, yaw, 180, true));

    var consumer = consumerProvider.getBuffer(RenderLayer.getEntityTranslucent(this.getTexture(entity)));

    this.drawTexture(matrices, consumer, entity, width, height);

    matrices.pop();
  }

  public static Quaternionf quaternionFromEulerAngles(float x, float y, float z, boolean degrees) {
    if (degrees) {
      x *= 0.017453292F;
      y *= 0.017453292F;
      z *= 0.017453292F;
    }

    var f = MathHelper.sin(0.5F * x);
    var g = MathHelper.cos(0.5F * x);
    var h = MathHelper.sin(0.5F * y);
    var i = MathHelper.cos(0.5F * y);
    var j = MathHelper.sin(0.5F * z);
    var k = MathHelper.cos(0.5F * z);

    // @formatter:off
    return new Quaternionf(
      f * i * k + g * h * j,
      g * h * k - f * i * j,
      f * h * k + g * i * j,
      g * i * k - f * h * j
    );
    // @formatter:on
  }

  private void drawTexture(
    MatrixStack matrices, VertexConsumer consumer, WickedPaintingEntity entity, int width, int height
  ) {
    var entry = matrices.peek();

    var m4f = entry.getPositionMatrix();
    var m3f = entry.getNormalMatrix();

    var offsetX = -width / 2f;
    var offsetY = -height / 2f;

    var scaleX = 1f / (float) width;
    var scaleY = 1f / (float) height;

    var lightX = entity.getBlockX();
    var lightY = entity.getBlockY();
    var lightZ = entity.getBlockZ();

    for (var x = 0; x < width; ++x) {
      for (var y = 0; y < height; ++y) {
        // without these +1s nothing renders
        var right = offsetX + x + 1;
        var left = offsetX + x;
        var bottom = offsetY + y + 1;
        var top = offsetY + y;

        var direction = entity.getHorizontalFacing();

        lightX = entity.getBlockX();
        lightY = MathHelper.floor(entity.getY() + (top + bottom) / 2.0F);
        lightZ = entity.getBlockZ();

        switch (direction) {
          case NORTH -> lightX = MathHelper.floor(entity.getX() + (right + left) / 2.0F);
          case WEST -> lightZ = MathHelper.floor(entity.getZ() - (right + left) / 2.0F);
          case SOUTH -> lightX = MathHelper.floor(entity.getX() - (right + left) / 2.0F);
          case EAST -> lightZ = MathHelper.floor(entity.getZ() + (right + left) / 2.0F);
        }

        var lightPos = new BlockPos(lightX, lightY, lightZ);
        var light = WorldRenderer.getLightmapCoordinates(entity.world, lightPos);

        var u0 = scaleX * (width - x);
        var u1 = scaleX * (width - x - 1);
        var v0 = scaleY * (height - y);
        var v1 = scaleY * (height - y - 1);

        // @formatter:off
        this.vertex(m4f, m3f, consumer, left,  bottom, u0, v1, light);
        this.vertex(m4f, m3f, consumer, right, bottom, u1, v1, light);
        this.vertex(m4f, m3f, consumer, right, top,    u1, v0, light);
        this.vertex(m4f, m3f, consumer, left,  top,    u0, v0, light);
        // @formatter:on
      }
    }
  }

  private void vertex(
    Matrix4f positionMatrix,
    Matrix3f normalMatrix,
    VertexConsumer consumer,
    float x,
    float y,
    float u,
    float v,
    int light
  ) {
    consumer
      .vertex(positionMatrix, x, y, -WallOffset)
      .color(255, 255, 255, 255)
      .texture(u, v)
      .overlay(OverlayTexture.DEFAULT_UV)
      .light(light)
      .normal(normalMatrix, 0f, 0f, -1f)
      .next();
  }

  @Override
  public Identifier getTexture(WickedPaintingEntity entity) {
    return ImageManager.loadImage(entity.getImageId(), entity.getUrl()).getTextureId();
  }
}
