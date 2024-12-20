package ooo.foooooooooooo.wickedpaintings.common;

import net.minecraft.util.Identifier;

public final class Constants {
  public static final String MOD_ID = "wicked_paintings";

  // Images
  public static final String IMAGES_NAMESPACE = "wicked_image";
  public static final Identifier DEFAULT_TEX = Identifier.of(Constants.MOD_ID,
    "textures/block/wicked_painting/default.png"
  );
  public static final Identifier BLOCKED_TEX = Identifier.of(Constants.MOD_ID,
    "textures/block/wicked_painting/blocked.png"
  );

  // Limits
  // width/height in blocks
  public static final int MIN_WIDTH = 1;
  public static final int MIN_HEIGHT = 1;
  public static final int MAX_WIDTH = 32;
  public static final int MAX_HEIGHT = 32;

  // NBT keys
  public static final String FACING = "Facing";
  public static final String URL = "Url";
  public static final String WIDTH = "Width";
  public static final String HEIGHT = "Height";
  public static final String IMAGE_ID = "Identifier";
  public static final String ATTACHMENT_POS_X = "TileX";
  public static final String ATTACHMENT_POS_Y = "TileY";
  public static final String ATTACHMENT_POS_Z = "TileZ";
}
