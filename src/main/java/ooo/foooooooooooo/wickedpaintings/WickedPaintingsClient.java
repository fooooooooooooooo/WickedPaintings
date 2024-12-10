package ooo.foooooooooooo.wickedpaintings;

import io.github.cottonmc.cotton.gui.client.CottonInventoryScreen;
import me.shedaniel.autoconfig.AutoConfig;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gui.screen.ingame.HandledScreens;
import ooo.foooooooooooo.wickedpaintings.client.screen.WickedGuiDescription;
import ooo.foooooooooooo.wickedpaintings.config.ModConfig;
import ooo.foooooooooooo.wickedpaintings.entity.ModEntityTypes;
import ooo.foooooooooooo.wickedpaintings.network.ModNetworking;

@Environment(EnvType.CLIENT)
public class WickedPaintingsClient implements ClientModInitializer {
  public static final ModConfig CONFIG = AutoConfig.getConfigHolder(ModConfig.class).getConfig();

  @Override
  public void onInitializeClient() {
    //noinspection RedundantTypeArguments fails to build if you remove these types
    HandledScreens.<WickedGuiDescription, CottonInventoryScreen<WickedGuiDescription>>register(WickedPaintings.WICKED_SCREEN_HANDLER_TYPE,
      CottonInventoryScreen::new
    );
    ModEntityTypes.registerRenderers();
    ModNetworking.registerClientBoundPackets();
  }
}
