package ooo.foooooooooooo.wickedpaintings.client.render;

import com.mojang.blaze3d.systems.RenderSystem;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.render.*;
import net.minecraft.client.render.entity.EntityRenderer;
import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Quaternion;
import ooo.foooooooooooo.wickedpaintings.WickedPaintings;
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

        if (image == null) {
            image = ImageManager.loadImage(new Identifier("wicked_images", "cumata"), "https://cdn.discordapp.com/attachments/902081288645804042/945872405581164544/lumuta.png");
        }

        if (image == null) {
            WickedPaintings.LOGGER.error("Failed to load default image");
            return;
        }

        var imageId = image.getTextureId();

        float pitch = entity.getPitch();
        float y = entity.getYaw();

        matrices.push();

        matrices.multiply(new Quaternion(180 - pitch, y, 0, true));
//        matrices.translate(-0.5d, -0.5, -0.5d / 16d);
        matrices.translate(-0.5d, -0.5, 0);


        RenderSystem.setShaderTexture(0, imageId);

        this.drawTexture(matrices, 0, 0, 0, 0f, 0f, 1, 1, 1, 1);

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
        var image = ImageManager.loadImage(entity.getImageId(), entity.getUrl());
        if (image == null) return new Identifier("wicked_image", "fucked_up");
        return image.getTextureId();
    }
}
