package ooo.foooooooooooo.wickedpaintings.platform.fabric.config;

import me.shedaniel.autoconfig.ConfigData;
import me.shedaniel.autoconfig.annotation.Config;
import ooo.foooooooooooo.wickedpaintings.Constants;
import ooo.foooooooooooo.wickedpaintings.WickedPaintings;
import ooo.foooooooooooo.wickedpaintings.config.IModConfig;

@Config(name = WickedPaintings.MOD_ID)
public class ModConfigFabric implements ConfigData, IModConfig {
  public boolean enabled = Constants.DEFAULT_ENABLED;
  public int maxSizeMb = Constants.DEFAULT_MAX_SIZE_MB;
  public boolean debug = Constants.DEFAULT_DEBUG;

  @Override
  public boolean enabled() {
    return this.enabled;
  }

  @Override
  public int maxSizeMb() {
    return this.maxSizeMb;
  }

  @Override
  public boolean debug() {
    return this.debug;
  }
}
