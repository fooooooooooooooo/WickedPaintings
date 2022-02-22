package ooo.foooooooooooo.wickedpaintings.network;

import net.minecraft.util.Identifier;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.registry.Registry;
import ooo.foooooooooooo.wickedpaintings.WickedPaintings;
import ooo.foooooooooooo.wickedpaintings.entity.WickedPaintingEntity;

public class ClientBoundPackets {
  public static final Identifier WICKED_PAINTING_SPAWN = new Identifier(WickedPaintings.MOD_ID, "wicked_painting_spawn");

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
