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
import net.minecraft.util.math.Direction;
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

  public static Quaternionf quaternionFromEulerAngles(float x, float y, float z, boolean degrees) {
    if (degrees) {
      x *= 0.017453292F;
      y *= 0.017453292F;
      z *= 0.017453292F;
    }

    float f = MathHelper.sin(0.5F * x);
    float g = MathHelper.cos(0.5F * x);
    float h = MathHelper.sin(0.5F * y);
    float i = MathHelper.cos(0.5F * y);
    float j = MathHelper.sin(0.5F * z);
    float k = MathHelper.cos(0.5F * z);

    // @formatter:off
    return new Quaternionf(
      f * i * k + g * h * j,
      g * h * k - f * i * j,
      f * h * k + g * i * j,
      g * i * k - f * h * j
    );
    // @formatter:on
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

    float pitch = entity.getPitch();
    float yaw = entity.getYaw();

    var width = entity.getRealWidth();
    var height = entity.getRealHeight();

    matrices.push();

    matrices.multiply(quaternionFromEulerAngles(180 - pitch, yaw, 180, true));

    var consumer = consumerProvider.getBuffer(RenderLayer.getEntitySolid(this.getTexture(entity)));

    this.drawTexture(matrices, consumer, entity, width, height);

    matrices.pop();
  }

  private void drawTexture(
    MatrixStack matrices, VertexConsumer consumer, WickedPaintingEntity entity, int width, int height
  ) {
    MatrixStack.Entry entry = matrices.peek();

    Matrix4f m4f = entry.getPositionMatrix();
    Matrix3f m3f = entry.getNormalMatrix();

    var offsetX = (-width / 2f);
    var offsetY = (-height / 2f);

    var scaleX = 1f / (float) width;
    var scaleY = 1f / (float) height;

    var blockPos = entity.getBlockPos();

    var blockX = blockPos.getX();
    var blockY = blockPos.getY();
    var blockZ = blockPos.getZ();

    var lightX = (float) blockX;
    var lightY = (float) blockY;
    var lightZ = (float) blockZ;

    var correction = width % 2 == 0 ? 0 : 1;

    for (int x = 0; x < width; ++x) {
      for (int y = 0; y < height; ++y) {
        // without these +1s nothing renders
        var right = offsetX + x + 1;
        var left = offsetX + x;
        var bottom = offsetY + y + 1;
        var top = offsetY + y;

        Direction direction = entity.getHorizontalFacing();

        lightY = blockY + (bottom - 1);

        switch (direction) {
          case NORTH -> lightX = blockX + left + correction;
          case WEST -> lightZ = blockZ - right + correction;
          case SOUTH -> lightX = blockX - right + correction;
          case EAST -> lightZ = blockZ + left + correction;
        }

        var lightPos = new BlockPos((int) lightX, (int) lightY, (int) lightZ);
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

  @Override
  public Identifier getTexture(WickedPaintingEntity entity) {
    return ImageManager.loadImage(entity.getImageId(), entity.getUrl()).getTextureId();
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
}
