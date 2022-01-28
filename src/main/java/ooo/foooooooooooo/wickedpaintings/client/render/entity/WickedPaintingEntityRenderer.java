package ooo.foooooooooooo.wickedpaintings.client.render.entity;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.*;
import net.minecraft.client.render.entity.EntityRenderer;
import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.client.texture.Sprite;
import net.minecraft.client.texture.SpriteAtlasTexture;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.*;
import ooo.foooooooooooo.wickedpaintings.entity.WickedPaintingEntity;
import ooo.foooooooooooo.wickedpaintings.mod.WickedPaintings;

@Environment(EnvType.CLIENT)
public class WickedPaintingEntityRenderer extends EntityRenderer<WickedPaintingEntity> {
  protected WickedPaintingEntityRenderer(EntityRendererFactory.Context context) {
    super(context);
  }

  public void render(WickedPaintingEntity wickedPaintingEntity, float f, float g, MatrixStack matrixStack, VertexConsumerProvider consumerProvider, int i) {
    matrixStack.push();
    matrixStack.multiply(Vec3f.POSITIVE_Y.getDegreesQuaternion(180.0F - f));

    float h = 0.0625F;
    matrixStack.scale(0.0625F, 0.0625F, 0.0625F);

    var vertexConsumer = consumerProvider.getBuffer(RenderLayer.getEntitySolid(this.getTexture(wickedPaintingEntity)));
    var paintingManager = MinecraftClient.getInstance().getPaintingManager();

    var texture = MinecraftClient.getInstance().getTextureManager().getTexture(
        new Identifier(WickedPaintings.MOD_ID, "textures/atlas/images"));

    var sprite = ((SpriteAtlasTexture) texture).getSprite(wickedPaintingEntity.identifier);

    this.renderPainting(
        matrixStack,
        vertexConsumer,
        wickedPaintingEntity,
        wickedPaintingEntity.getWidthPixels(),
        wickedPaintingEntity.getHeightPixels(),
        sprite,
        paintingManager.getBackSprite()
    );

    matrixStack.pop();
    super.render(wickedPaintingEntity, f, g, matrixStack, consumerProvider, i);
  }

  public Identifier getTexture(WickedPaintingEntity wickedPaintingEntity) {
    return MinecraftClient.getInstance().getPaintingManager().getBackSprite().getAtlas().getId();
  }

  private void renderPainting(MatrixStack matrices, VertexConsumer vertexConsumer, WickedPaintingEntity entity,
                              int width, int height, Sprite sprite, Sprite backSprite) {
    MatrixStack.Entry entry = matrices.peek();
    Matrix4f matrix4f = entry.getPositionMatrix();
    Matrix3f matrix3f = entry.getNormalMatrix();
    float f = (float) (-width) / 2.0F;
    float g = (float) (-height) / 2.0F;
    float h = 0.5F;
    float i = backSprite.getMinU();
    float j = backSprite.getMaxU();
    float k = backSprite.getMinV();
    float l = backSprite.getMaxV();
    float m = backSprite.getMinU();
    float n = backSprite.getMaxU();
    float o = backSprite.getMinV();
    float p = backSprite.getFrameV(1.0D);
    float q = backSprite.getMinU();
    float r = backSprite.getFrameU(1.0D);
    float s = backSprite.getMinV();
    float t = backSprite.getMaxV();
    int u = width / 16;
    int v = height / 16;
    double d = 16.0D / (double) u;
    double e = 16.0D / (double) v;

    for (int w = 0; w < u; ++w) {
      for (int x = 0; x < v; ++x) {
        float y = f + (float) ((w + 1) * 16);
        float z = f + (float) (w * 16);
        float aa = g + (float) ((x + 1) * 16);
        float ab = g + (float) (x * 16);
        int ac = entity.getBlockX();
        int ad = MathHelper.floor(entity.getY() + (double) ((aa + ab) / 2.0F / 16.0F));
        int ae = entity.getBlockZ();
        Direction direction = entity.getHorizontalFacing();

        if (direction == Direction.NORTH) {
          ac = MathHelper.floor(entity.getX() + (double) ((y + z) / 2.0F / 16.0F));
        }

        if (direction == Direction.WEST) {
          ae = MathHelper.floor(entity.getZ() - (double) ((y + z) / 2.0F / 16.0F));
        }

        if (direction == Direction.SOUTH) {
          ac = MathHelper.floor(entity.getX() - (double) ((y + z) / 2.0F / 16.0F));
        }

        if (direction == Direction.EAST) {
          ae = MathHelper.floor(entity.getZ() + (double) ((y + z) / 2.0F / 16.0F));
        }

        int af = WorldRenderer.getLightmapCoordinates(entity.world, new BlockPos(ac, ad, ae));

        float ag = sprite.getFrameU(d * (double) (u - w));
        float ah = sprite.getFrameU(d * (double) (u - (w + 1)));

        float ai = sprite.getFrameV(e * (double) (v - x));
        float aj = sprite.getFrameV(e * (double) (v - (x + 1)));

        this.vertex(matrix4f, matrix3f, vertexConsumer, y, ab, ah, ai, -0.5F, 0, 0, -1, af);
        this.vertex(matrix4f, matrix3f, vertexConsumer, z, ab, ag, ai, -0.5F, 0, 0, -1, af);
        this.vertex(matrix4f, matrix3f, vertexConsumer, z, aa, ag, aj, -0.5F, 0, 0, -1, af);
        this.vertex(matrix4f, matrix3f, vertexConsumer, y, aa, ah, aj, -0.5F, 0, 0, -1, af);
        this.vertex(matrix4f, matrix3f, vertexConsumer, y, aa, j, k, 0.5F, 0, 0, 1, af);
        this.vertex(matrix4f, matrix3f, vertexConsumer, z, aa, i, k, 0.5F, 0, 0, 1, af);
        this.vertex(matrix4f, matrix3f, vertexConsumer, z, ab, i, l, 0.5F, 0, 0, 1, af);
        this.vertex(matrix4f, matrix3f, vertexConsumer, y, ab, j, l, 0.5F, 0, 0, 1, af);
        this.vertex(matrix4f, matrix3f, vertexConsumer, y, aa, m, o, -0.5F, 0, 1, 0, af);
        this.vertex(matrix4f, matrix3f, vertexConsumer, z, aa, n, o, -0.5F, 0, 1, 0, af);
        this.vertex(matrix4f, matrix3f, vertexConsumer, z, aa, n, p, 0.5F, 0, 1, 0, af);
        this.vertex(matrix4f, matrix3f, vertexConsumer, y, aa, m, p, 0.5F, 0, 1, 0, af);
        this.vertex(matrix4f, matrix3f, vertexConsumer, y, ab, m, o, 0.5F, 0, -1, 0, af);
        this.vertex(matrix4f, matrix3f, vertexConsumer, z, ab, n, o, 0.5F, 0, -1, 0, af);
        this.vertex(matrix4f, matrix3f, vertexConsumer, z, ab, n, p, -0.5F, 0, -1, 0, af);
        this.vertex(matrix4f, matrix3f, vertexConsumer, y, ab, m, p, -0.5F, 0, -1, 0, af);
        this.vertex(matrix4f, matrix3f, vertexConsumer, y, aa, r, s, 0.5F, -1, 0, 0, af);
        this.vertex(matrix4f, matrix3f, vertexConsumer, y, ab, r, t, 0.5F, -1, 0, 0, af);
        this.vertex(matrix4f, matrix3f, vertexConsumer, y, ab, q, t, -0.5F, -1, 0, 0, af);
        this.vertex(matrix4f, matrix3f, vertexConsumer, y, aa, q, s, -0.5F, -1, 0, 0, af);
        this.vertex(matrix4f, matrix3f, vertexConsumer, z, aa, r, s, -0.5F, 1, 0, 0, af);
        this.vertex(matrix4f, matrix3f, vertexConsumer, z, ab, r, t, -0.5F, 1, 0, 0, af);
        this.vertex(matrix4f, matrix3f, vertexConsumer, z, ab, q, t, 0.5F, 1, 0, 0, af);
        this.vertex(matrix4f, matrix3f, vertexConsumer, z, aa, q, s, 0.5F, 1, 0, 0, af);
      }
    }

  }

  private void vertex(Matrix4f positionMatrix, Matrix3f normalMatrix, VertexConsumer vertexConsumer, float x, float y, float u, float v, float z, int normalX, int normalY, int normalZ, int light) {
    vertexConsumer.vertex(positionMatrix, x, y, z).color(255, 255, 255, 255).texture(u, v).overlay(OverlayTexture.DEFAULT_UV).light(light).normal(normalMatrix, (float) normalX, (float) normalY, (float) normalZ).next();
  }
}
