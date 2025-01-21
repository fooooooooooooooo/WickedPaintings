package ooo.foooooooooooo.wickedpaintings.platform.fabric.client.config;

import com.terraformersmc.modmenu.api.ConfigScreenFactory;
import com.terraformersmc.modmenu.api.ModMenuApi;
import me.shedaniel.autoconfig.AutoConfig;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import ooo.foooooooooooo.wickedpaintings.platform.fabric.config.ModConfigFabric;

@Environment(EnvType.CLIENT)
public class ModConfigModMenuIntegration implements ModMenuApi {
  @Override
  public ConfigScreenFactory<?> getModConfigScreenFactory() {
    return parent -> AutoConfig.getConfigScreen(ModConfigFabric.class, parent).get();
  }
}
