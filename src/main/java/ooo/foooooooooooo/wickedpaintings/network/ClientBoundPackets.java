package ooo.foooooooooooo.wickedpaintings.network;

import net.minecraft.util.Identifier;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.registry.Registry;
import ooo.foooooooooooo.wickedpaintings.entity.WickedPaintingEntity;
import ooo.foooooooooooo.wickedpaintings.image.ImageLoaderManager;
import ooo.foooooooooooo.wickedpaintings.mod.WickedPaintings;

import java.util.List;

public class ClientBoundPackets {
  public static final Identifier SYNC_IMAGES = new Identifier(WickedPaintings.MOD_ID, "sync_images");
  public static final Identifier WICKED_PAINTING_SPAWN = new Identifier(WickedPaintings.MOD_ID, "wicked_painting_spawn");

  public static IdentifiedPacket createPacketSyncLoadedImages(List<ImageLoaderManager.LoadedImage> images) {
    return ModNetworking.createClientBoundPacket(SYNC_IMAGES, extendedPacketBuffer -> {
      extendedPacketBuffer.writeCodec(ImageLoaderManager.CODEC, images);
    });
  }

  public static IdentifiedPacket createPacketWickedPaintingSpawn(WickedPaintingEntity entity) {
    return ModNetworking.createClientBoundPacket(WICKED_PAINTING_SPAWN, buffer -> {
      buffer.writeVarInt(Registry.ENTITY_TYPE.getRawId(entity.getType()));
      buffer.writeUuid(entity.getUuid());
      buffer.writeVarInt(entity.getId());
      buffer.writeDouble(entity.getX());
      buffer.writeDouble(entity.getY());
      buffer.writeDouble(entity.getZ());
      buffer.writeByte(MathHelper.floor(entity.getPitch() * 256.0F / 360.0F));
      buffer.writeByte(MathHelper.floor(entity.getYaw() * 256.0F / 360.0F));
      entity.writeToBuffer(buffer);
    });
  }

}
