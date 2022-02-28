package ooo.foooooooooooo.wickedpaintings.item;

import net.minecraft.item.Item;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;
import ooo.foooooooooooo.wickedpaintings.WickedPaintings;

public class ModItems {
    public static final Item WICKED_PAINTING = new WickedPaintingItem(defaultSettings());

    public static Item.Settings defaultSettings() {
        return new Item.Settings().group(WickedPaintings.DEFAULT_GROUP);
    }

    public static void registerItems() {
        register(WICKED_PAINTING, "wicked_painting");
    }

    public static void register(Item item, String name) {
        Registry.register(Registry.ITEM, new Identifier(WickedPaintings.MOD_ID, name), item);
    }
}
