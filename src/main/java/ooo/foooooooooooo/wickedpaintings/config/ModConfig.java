package ooo.foooooooooooo.wickedpaintings.config;

import me.shedaniel.autoconfig.ConfigData;
import me.shedaniel.autoconfig.annotation.Config;
import ooo.foooooooooooo.wickedpaintings.mod.WickedPaintings;

@Config(name = WickedPaintings.MOD_ID)
public class ModConfig implements ConfigData {
  public ImageSendMode sendMode = ImageSendMode.CACHE;
}
