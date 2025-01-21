package ooo.foooooooooooo.wickedpaintings.item;

import dev.architectury.registry.registries.DeferredRegister;
import dev.architectury.registry.registries.RegistrySupplier;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroups;
import net.minecraft.registry.RegistryKeys;
import ooo.foooooooooooo.wickedpaintings.WickedPaintings;

public class ModItems {
  @SuppressWarnings("UnstableApiUsage")
  private static final Item.Settings DEFAULT_SETTINGS = new Item.Settings().arch$tab(ItemGroups.FUNCTIONAL);

  public static final DeferredRegister<Item> ITEMS = DeferredRegister.create(WickedPaintings.MOD_ID, RegistryKeys.ITEM);

  public static final RegistrySupplier<Item> WICKED_PAINTING = ITEMS.register(WickedPaintings.id("wicked_painting"),
    () -> new WickedPaintingItem(DEFAULT_SETTINGS)
  );

  public static void registerItems() {
    ITEMS.register();
  }
}
