package ooo.foooooooooooo.wickedpaintings;

import me.shedaniel.autoconfig.AutoConfig;
import me.shedaniel.autoconfig.serializer.JanksonConfigSerializer;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.itemgroup.v1.FabricItemGroup;
import net.fabricmc.loader.impl.util.log.Log;
import net.fabricmc.loader.impl.util.log.LogCategory;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import ooo.foooooooooooo.wickedpaintings.config.ModConfig;
import ooo.foooooooooooo.wickedpaintings.entity.ModEntityTypes;
import ooo.foooooooooooo.wickedpaintings.item.ModItems;
import ooo.foooooooooooo.wickedpaintings.network.ModNetworking;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class WickedPaintings implements ModInitializer {
  public static final String MOD_ID = "wicked_paintings";

  public static final Identifier GENERAL_GROUP_ID = new Identifier(MOD_ID, "general");
  public static final ItemGroup GENERAL_ITEM_GROUP = FabricItemGroup.builder(GENERAL_GROUP_ID)
    .displayName(Text.translatable("itemGroup.wicked_paintings.general"))
    .icon(() -> new ItemStack(ModItems.WICKED_PAINTING))
    .build();

  public static final Logger LOGGERS = LoggerFactory.getLogger(WickedPaintings.class);

  @Override
  public void onInitialize() {
    Log.info(LogCategory.LOG, "Loading Wicked Paintings");
    AutoConfig.register(ModConfig.class, JanksonConfigSerializer::new);

    ModEntityTypes.registerEntityTypes();
    ModItems.registerItems();
    ModNetworking.registerServerBoundPackets();
  }
}
