package ooo.foooooooooooo.wickedpaintings.entity;

import net.fabricmc.fabric.api.object.builder.v1.entity.FabricEntityTypeBuilder;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityDimensions;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.SpawnGroup;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;
import ooo.foooooooooooo.wickedpaintings.mod.WickedPaintings;

public class ModEntityTypes {
  public static final EntityType<WickedPaintingEntity> WICKED_PAINTING =
      FabricEntityTypeBuilder
          .create(SpawnGroup.MISC, WickedPaintingEntity::new)
          .dimensions(EntityDimensions.fixed(0.5F, 0.5F))
          .trackRangeBlocks(10)
          .trackedUpdateRate(2147483647)
          .build();

  public static void registerEntityTypes() {
    register(new Identifier(WickedPaintings.MOD_ID, "wicked_painting"), WICKED_PAINTING);
  }

  private static <T extends Entity> void register(Identifier id, EntityType<T> type) {
    Registry.register(Registry.ENTITY_TYPE, id, type);
  }
}
