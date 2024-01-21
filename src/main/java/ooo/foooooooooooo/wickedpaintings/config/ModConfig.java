package ooo.foooooooooooo.wickedpaintings.config;

import me.shedaniel.autoconfig.ConfigData;
import me.shedaniel.autoconfig.annotation.Config;
import ooo.foooooooooooo.wickedpaintings.WickedPaintings;

@Config(name = WickedPaintings.MOD_ID)
public class ModConfig implements ConfigData {
  public boolean enabled = true;
  public int maxSizeMb = 128;

  public long maxSizeBytes() {
    return maxSizeMb * 1024L * 1024L;
  }

  public boolean hasMaxSize() {
    return maxSizeMb > 0;
  }
}
