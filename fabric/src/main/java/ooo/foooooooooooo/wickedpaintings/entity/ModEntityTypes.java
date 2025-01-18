package ooo.foooooooooooo.wickedpaintings.entity;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.rendering.v1.EntityRendererRegistry;
import net.fabricmc.fabric.api.object.builder.v1.entity.FabricEntityTypeBuilder;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityDimensions;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.SpawnGroup;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.util.Identifier;
import net.minecraft.world.World;
import ooo.foooooooooooo.wickedpaintings.client.render.WickedPaintingEntityRenderer;
import ooo.foooooooooooo.wickedpaintings.common.Constants;

public class ModEntityTypes {
  public static final EntityType<WickedPaintingEntity> WICKED_PAINTING = FabricEntityTypeBuilder
    .create(
      SpawnGroup.MISC,
      (EntityType<WickedPaintingEntity> entityType, World world) -> new WickedPaintingEntity(entityType, world)
    )
    .dimensions(EntityDimensions.fixed(0.5F, 0.5F))
    .trackRangeBlocks(64)
    .trackedUpdateRate(Integer.MAX_VALUE)
    .build();

  public static void registerEntityTypes() {
    register(new Identifier(Constants.MOD_ID, "wicked_painting"), WICKED_PAINTING);
  }

  private static <T extends Entity> void register(Identifier id, EntityType<T> type) {
    Registry.register(Registries.ENTITY_TYPE, id, type);
  }

  @Environment(EnvType.CLIENT)
  public static void registerRenderers() {
    EntityRendererRegistry.register(WICKED_PAINTING, WickedPaintingEntityRenderer::new);
  }
}
