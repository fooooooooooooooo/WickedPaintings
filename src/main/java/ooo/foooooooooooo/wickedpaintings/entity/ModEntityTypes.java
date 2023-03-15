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
import ooo.foooooooooooo.wickedpaintings.WickedPaintings;
import ooo.foooooooooooo.wickedpaintings.client.render.WickedPaintingEntityRenderer;

public class ModEntityTypes {
  public static final EntityType<WickedPaintingEntity> WICKED_PAINTING = FabricEntityTypeBuilder
    .create(SpawnGroup.MISC,
      (EntityType<WickedPaintingEntity> entityType, World world) -> new WickedPaintingEntity(entityType, world))
    .dimensions(EntityDimensions.fixed(0.5F, 0.5F))
    .trackRangeBlocks(10)
    .trackedUpdateRate(2147483647)
    .build();

  public static void registerEntityTypes() {
    register(new Identifier(WickedPaintings.MOD_ID, "wicked_painting"), WICKED_PAINTING);
  }

  @Environment(EnvType.CLIENT)
  public static void registerRenderers() {
    EntityRendererRegistry.register(WICKED_PAINTING, WickedPaintingEntityRenderer::new);
  }

  private static <T extends Entity> void register(Identifier id, EntityType<T> type) {
    Registry.register(Registries.ENTITY_TYPE, id, type);
  }
}
