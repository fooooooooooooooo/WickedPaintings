package ooo.foooooooooooo.wickedpaintings;

import me.shedaniel.autoconfig.AutoConfig;
import me.shedaniel.autoconfig.serializer.JanksonConfigSerializer;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.itemgroup.v1.FabricItemGroup;
import net.fabricmc.fabric.api.screenhandler.v1.ExtendedScreenHandlerType;
import net.fabricmc.loader.impl.util.log.Log;
import net.fabricmc.loader.impl.util.log.LogCategory;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.inventory.StackReference;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.screen.ScreenHandlerType;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import ooo.foooooooooooo.wickedpaintings.client.screen.WickedGuiDescription;
import ooo.foooooooooooo.wickedpaintings.config.ModConfig;
import ooo.foooooooooooo.wickedpaintings.entity.ModEntityTypes;
import ooo.foooooooooooo.wickedpaintings.item.ModItems;
import ooo.foooooooooooo.wickedpaintings.network.ModNetworking;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class WickedPaintings implements ModInitializer {
  public static final String MOD_ID = "wicked_paintings";
  public static final RegistryKey<ItemGroup> ITEM_GROUP = RegistryKey.of(RegistryKeys.ITEM_GROUP, new Identifier(MOD_ID, "general"));

  public static final Logger LOGGERS = LoggerFactory.getLogger(WickedPaintings.class);
  public static ScreenHandlerType<WickedGuiDescription> WICKED_SCREEN_HANDLER_TYPE;

  @Override
  public void onInitialize() {
    Log.info(LogCategory.LOG, "Loading Wicked Paintings");
    AutoConfig.register(ModConfig.class, JanksonConfigSerializer::new);

    var itemGroup = FabricItemGroup
      .builder()
      .displayName(Text.translatable("itemGroup.wicked_paintings.general"))
      .icon(() -> new ItemStack(ModItems.WICKED_PAINTING))
      .build();

    Registry.register(Registries.ITEM_GROUP, ITEM_GROUP, itemGroup);

    WICKED_SCREEN_HANDLER_TYPE = new ExtendedScreenHandlerType<>((syncId, inventory, buf) -> {
      var equipmentSlot = buf.readEnumConstant(EquipmentSlot.class);
      var handStack = StackReference.of(inventory.player, equipmentSlot);
      return new WickedGuiDescription(syncId, inventory, handStack);
    });

    Registry.register(Registries.SCREEN_HANDLER, new Identifier(MOD_ID, "wicked_gui"), WICKED_SCREEN_HANDLER_TYPE);

    ModEntityTypes.registerEntityTypes();
    ModItems.registerItems();
    ModNetworking.registerServerBoundPackets();
  }
}
