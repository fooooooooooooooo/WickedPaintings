package ooo.foooooooooooo.wickedpaintings.config;

import me.shedaniel.autoconfig.ConfigData;
import me.shedaniel.autoconfig.annotation.Config;
import ooo.foooooooooooo.wickedpaintings.common.Constants;

@Config(name = Constants.MOD_ID)
public class ModConfig implements ConfigData {
  public boolean enabled = true;
  public int maxSizeMb = 128;

  // todo: implement this again
  public boolean debug = false;

  public long maxSizeBytes() {
    return this.maxSizeMb * 1024L * 1024L;
  }

  public boolean hasMaxSize() {
    return this.maxSizeMb > 0;
  }
}
