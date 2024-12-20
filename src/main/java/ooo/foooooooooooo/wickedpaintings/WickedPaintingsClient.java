package ooo.foooooooooooo.wickedpaintings;

import me.shedaniel.autoconfig.AutoConfig;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gui.screen.ingame.HandledScreens;
import ooo.foooooooooooo.wickedpaintings.client.screen.WickedPaintingScreen;
import ooo.foooooooooooo.wickedpaintings.config.ModConfig;
import ooo.foooooooooooo.wickedpaintings.entity.ModEntityTypes;
import ooo.foooooooooooo.wickedpaintings.network.ModNetworking;

@Environment(EnvType.CLIENT)
public class WickedPaintingsClient implements ClientModInitializer {
  public static final ModConfig CONFIG = AutoConfig.getConfigHolder(ModConfig.class).getConfig();

  @Override
  public void onInitializeClient() {
    HandledScreens.register(WickedPaintings.WICKED_SCREEN_HANDLER_TYPE, WickedPaintingScreen::new);
    ModEntityTypes.registerRenderers();
    ModNetworking.registerClientBoundPackets();
  }
}
