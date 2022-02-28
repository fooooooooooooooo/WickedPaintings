package ooo.foooooooooooo.wickedpaintings.client.render;

import com.mojang.blaze3d.systems.RenderSystem;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.render.*;
import net.minecraft.client.render.entity.EntityRenderer;
import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.Quaternion;
import ooo.foooooooooooo.wickedpaintings.client.ImageManager;
import ooo.foooooooooooo.wickedpaintings.entity.WickedPaintingEntity;
import org.lwjgl.opengl.GL11;

@Environment(EnvType.CLIENT)
public class WickedPaintingEntityRenderer extends EntityRenderer<WickedPaintingEntity> {
    public WickedPaintingEntityRenderer(EntityRendererFactory.Context context) {
        super(context);
    }

    @Override
    public void render(WickedPaintingEntity entity, float yaw, float tickDelta, MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light) {
        var image = ImageManager.loadImage(entity.getImageId(), entity.getUrl());

        var imageId = image.getTextureId();

        float entityPitch = entity.getPitch();
        float entityYaw = entity.getYaw();

        var w = entity.getRealWidth();
        var h = entity.getRealHeight();

        matrices.push();

        matrices.multiply(new Quaternion(180 - entityPitch, entityYaw, 0, true));

        var xTrans = -w / 2f;
        var yTrans = -h / 2f;

        matrices.translate(xTrans, yTrans, 0);

        RenderSystem.setShaderTexture(0, imageId);

        this.drawTexture(matrices, 0, 0, 0, 0f, 0f, w, h, w, h);

        matrices.pop();
    }

    private void drawTexture(MatrixStack matrices, int x, int y, int z, float u, float v, int width, int height, int textureWidth, int textureHeight) {
        var matrix = matrices.peek().getPositionMatrix();

        var x1 = x + width;
        var y1 = y + height;

        var u0 = (u + 0.0F) / (float) textureWidth;
        var u1 = (u + (float) width) / (float) textureWidth;

        var v0 = (v + 0.0F) / (float) textureHeight;
        var v1 = (v + (float) height) / (float) textureHeight;

        RenderSystem.enableDepthTest();
        RenderSystem.depthFunc(GL11.GL_LEQUAL);
        RenderSystem.enableCull();
        RenderSystem.setShader(GameRenderer::getPositionTexShader);
        BufferBuilder bufferBuilder = Tessellator.getInstance().getBuffer();

        bufferBuilder.begin(VertexFormat.DrawMode.QUADS, VertexFormats.POSITION_TEXTURE);
        bufferBuilder.vertex(matrix, (float) x, (float) y1, (float) z).texture(u0, v1).next();
        bufferBuilder.vertex(matrix, (float) x1, (float) y1, (float) z).texture(u1, v1).next();
        bufferBuilder.vertex(matrix, (float) x1, (float) y, (float) z).texture(u1, v0).next();
        bufferBuilder.vertex(matrix, (float) x, (float) y, (float) z).texture(u0, v0).next();
        bufferBuilder.end();

        BufferRenderer.draw(bufferBuilder);
    }

    @Override
    public Identifier getTexture(WickedPaintingEntity entity) {
        return ImageManager.loadImage(entity.getImageId(), entity.getUrl()).getTextureId();
    }
}
