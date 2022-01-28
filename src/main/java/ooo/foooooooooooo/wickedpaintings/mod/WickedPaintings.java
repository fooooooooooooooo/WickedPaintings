package ooo.foooooooooooo.wickedpaintings.mod;

import me.shedaniel.autoconfig.AutoConfig;
import me.shedaniel.autoconfig.serializer.JanksonConfigSerializer;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.client.itemgroup.FabricItemGroupBuilder;
import net.fabricmc.loader.impl.util.log.Log;
import net.fabricmc.loader.impl.util.log.LogCategory;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.util.Identifier;
import ooo.foooooooooooo.wickedpaintings.config.ModConfig;
import ooo.foooooooooooo.wickedpaintings.entity.ModEntityTypes;
import ooo.foooooooooooo.wickedpaintings.item.ModItems;

public class WickedPaintings implements ModInitializer {
  public static final String MOD_ID = "wicked_paintings";
  public static final ItemGroup DEFAULT_GROUP = FabricItemGroupBuilder.build(
      new Identifier(MOD_ID, "general"),
      () -> new ItemStack(Items.PAINTING)
  );

  @Override
  public void onInitialize() {
    Log.info(LogCategory.LOG, "Initializing Wicked Paintings");
    AutoConfig.register(ModConfig.class, JanksonConfigSerializer::new);
    ModEntityTypes.registerEntityTypes();
    ModItems.registerItems();
  }
}
