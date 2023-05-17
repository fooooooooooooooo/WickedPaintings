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
    VertexConsumerProvider vertexConsumerProvider,
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

    var vertexConsumer = vertexConsumerProvider.getBuffer(RenderLayer.getEntitySolid(this.getTexture(entity)));

    this.drawTexture(matrices, vertexConsumer, entity, width, height);

    matrices.pop();
  }

  private void drawTexture(
    MatrixStack matrices, VertexConsumer vertexConsumer, WickedPaintingEntity entity, int width, int height
  ) {
    MatrixStack.Entry entry = matrices.peek();

    Matrix4f matrix4f = entry.getPositionMatrix();
    Matrix3f matrix3f = entry.getNormalMatrix();

    float offsetX = (float) -width / 2.0F;
    float offsetY = (float) -height / 2.0F;

    var blockScaleX = 1F / (float) width;
    var blockScaleY = 1F / (float) height;

    var blockPos = entity.getBlockPos();

    var blockX = blockPos.getX();
    var blockY = blockPos.getY();
    var blockZ = blockPos.getZ();

    var lightingX = blockX;
    var lightingY = blockY;
    var lightingZ = blockZ;

    for (int x = 0; x < width; ++x) {
      for (int y = 0; y < height; ++y) {
        var right = offsetX + (float) (x + 1);
        var left = offsetX + (float) x;
        var bottom = offsetY + (float) (y + 1);
        var top = offsetY + (float) y;

        Direction direction = entity.getHorizontalFacing();

        lightingY = MathHelper.floor(blockY + (double) (bottom - 1));

        if (direction == Direction.NORTH) {
          lightingX = MathHelper.floor(blockX + left);
        }

        if (direction == Direction.WEST) {
          lightingZ = MathHelper.floor(blockZ - right);
        }

        if (direction == Direction.SOUTH) {
          lightingX = MathHelper.floor(blockX - right);
        }

        if (direction == Direction.EAST) {
          lightingZ = MathHelper.floor(blockZ + left);
        }

        var lightmapCoordinates = WorldRenderer.getLightmapCoordinates(entity.world,
          new BlockPos(lightingX, lightingY, lightingZ));

        var scaledU1 = blockScaleX * (width - x - 1);
        var scaledU0 = blockScaleX * (width - x);
        var scaledV1 = blockScaleY * (height - y - 1);
        var scaledV0 = blockScaleY * (height - y);

        // @formatter:off
        this.vertex(matrix4f, matrix3f, vertexConsumer, left,  bottom, scaledU0, scaledV1, -WallOffset, 0, 0, -1, lightmapCoordinates);
        this.vertex(matrix4f, matrix3f, vertexConsumer, right, bottom, scaledU1, scaledV1, -WallOffset, 0, 0, -1, lightmapCoordinates);
        this.vertex(matrix4f, matrix3f, vertexConsumer, right, top,    scaledU1, scaledV0, -WallOffset, 0, 0, -1, lightmapCoordinates);
        this.vertex(matrix4f, matrix3f, vertexConsumer, left,  top,    scaledU0, scaledV0, -WallOffset, 0, 0, -1, lightmapCoordinates);
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
    VertexConsumer vertexConsumer,
    float x,
    float y,
    float u,
    float v,
    float z,
    int normalX,
    int normalY,
    int normalZ,
    int light
  ) {
    vertexConsumer
      .vertex(positionMatrix, x, y, z)
      .color(255, 255, 255, 255)
      .texture(u, v)
      .overlay(OverlayTexture.DEFAULT_UV)
      .light(light)
      .normal(normalMatrix, (float) normalX, (float) normalY, (float) normalZ)
      .next();
  }
}
