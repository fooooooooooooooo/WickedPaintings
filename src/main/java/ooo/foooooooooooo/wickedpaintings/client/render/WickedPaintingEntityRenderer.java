package ooo.foooooooooooo.wickedpaintings.client.render;

import net.minecraft.client.render.entity.EntityRenderer;
import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.util.Identifier;
import ooo.foooooooooooo.wickedpaintings.entity.WickedPaintingEntity;

public class WickedPaintingEntityRenderer extends EntityRenderer<WickedPaintingEntity> {
  protected WickedPaintingEntityRenderer(EntityRendererFactory.Context ctx) {
    super(ctx);
  }

  @Override
  public Identifier getTexture(WickedPaintingEntity entity) {
    return null;
  }
}
