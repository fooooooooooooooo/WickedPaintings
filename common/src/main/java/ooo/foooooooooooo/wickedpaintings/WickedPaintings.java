package ooo.foooooooooooo.wickedpaintings;

import net.minecraft.util.Identifier;
import ooo.foooooooooooo.wickedpaintings.entity.ModEntityTypes;
import ooo.foooooooooooo.wickedpaintings.item.ModItems;
import ooo.foooooooooooo.wickedpaintings.network.ServerBoundPackets;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class WickedPaintings {
  public static final String MOD_ID = "wicked_paintings";

  public static final Logger LOGGERS = LoggerFactory.getLogger(WickedPaintings.class);

  public static Identifier id(String path) {
    return new Identifier(MOD_ID, path);
  }

  public static void init() {
    LOGGERS.info("Loading Wicked Paintings");

    ModEntityTypes.registerEntityTypes();
    ModItems.registerItems();
    ServerBoundPackets.registerPackets();
  }
}
