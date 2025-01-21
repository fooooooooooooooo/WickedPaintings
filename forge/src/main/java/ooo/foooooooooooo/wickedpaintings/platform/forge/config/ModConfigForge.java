package ooo.foooooooooooo.wickedpaintings.platform.forge.config;

import net.minecraftforge.common.ForgeConfigSpec;
import ooo.foooooooooooo.wickedpaintings.Constants;
import ooo.foooooooooooo.wickedpaintings.config.IModConfig;

public class ModConfigForge implements IModConfig {
  public static ForgeConfigSpec SPEC;

  public final ForgeConfigSpec.BooleanValue enabled;
  public final ForgeConfigSpec.IntValue maxSizeMb;
  public final ForgeConfigSpec.BooleanValue debug;

  public ModConfigForge() {
    ForgeConfigSpec.Builder builder = new ForgeConfigSpec.Builder();

    enabled = builder.comment("Enable rendering").define("enabled", Constants.DEFAULT_ENABLED);

    maxSizeMb = builder
      .comment("Maximum size of the image cache in MB")
      .defineInRange("maxSizeMb", Constants.DEFAULT_MAX_SIZE_MB, Constants.MAX_SIZE_MB_MIN, Constants.MAX_SIZE_MB_MAX);

    debug = builder.comment("Enable debug mode").define("debug", Constants.DEFAULT_DEBUG);

    SPEC = builder.build();
  }

  @Override
  public boolean enabled() {
    return this.enabled.get();
  }

  @Override
  public int maxSizeMb() {
    return this.maxSizeMb.get();
  }

  @Override
  public boolean debug() {
    return this.debug.get();
  }
}
