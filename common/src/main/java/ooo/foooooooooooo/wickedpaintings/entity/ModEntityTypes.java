package ooo.foooooooooooo.wickedpaintings.entity;

import dev.architectury.registry.client.level.entity.EntityRendererRegistry;
import dev.architectury.registry.registries.DeferredRegister;
import dev.architectury.registry.registries.RegistrySupplier;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.SpawnGroup;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.util.Identifier;
import ooo.foooooooooooo.wickedpaintings.WickedPaintings;
import ooo.foooooooooooo.wickedpaintings.client.render.WickedPaintingEntityRenderer;

public class ModEntityTypes {
  private static final DeferredRegister<EntityType<?>> ENTITY_TYPES = DeferredRegister.create(WickedPaintings.MOD_ID,
    RegistryKeys.ENTITY_TYPE
  );

  private static final Identifier WICKED_PAINTING_ID = WickedPaintings.id("wicked_painting");
  public static final RegistrySupplier<EntityType<WickedPaintingEntity>> WICKED_PAINTING =
    ENTITY_TYPES.register(WICKED_PAINTING_ID,
    () -> {

      EntityType.Builder<WickedPaintingEntity> builder = EntityType.Builder.create(WickedPaintingEntity::new,
        SpawnGroup.MISC
      );
      return builder
        .setDimensions(0.5F, 0.5F)
        .maxTrackingRange(10)
        .trackingTickInterval(Integer.MAX_VALUE)
        .build(WICKED_PAINTING_ID.toString());
    }
  );

  public static void registerEntityTypes() {
    ENTITY_TYPES.register();
  }

  @Environment(EnvType.CLIENT)
  public static void registerRenderers() {
    EntityRendererRegistry.register(WICKED_PAINTING, WickedPaintingEntityRenderer::new);
  }
}
