package ooo.foooooooooooo.wickedpaintings.network;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.networking.v1.PacketSender;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayNetworkHandler;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;
import ooo.foooooooooooo.wickedpaintings.entity.WickedPaintingEntity;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Arrays;
import java.util.UUID;

@Environment(EnvType.CLIENT)
public class ClientBoundPacketHandlers {
  private static final Logger logger = LoggerFactory.getLogger(ClientBoundPacketHandlers.class);

  public static void registerPacketHandlers() {
    register(ClientBoundPackets.WICKED_PAINTING_SPAWN, ClientBoundPacketHandlers::onWickedPaintingSpawn);
  }

  private static void register(Identifier identifier, ClientPlayNetworking.PlayChannelHandler handler) {
    ModNetworking.registerClientBoundHandler(identifier, handler);
  }

  private static void onWickedPaintingSpawn(
      MinecraftClient client,
      ClientPlayNetworkHandler handler,
      PacketByteBuf buffer,
      PacketSender sender
  ) {
    EntityType<?> type = Registry.ENTITY_TYPE.get(buffer.readVarInt());
    UUID entityUUID = buffer.readUuid();
    int entityID = buffer.readVarInt();
    double x = buffer.readDouble();
    double y = buffer.readDouble();
    double z = buffer.readDouble();
    float pitch = (buffer.readByte() * 360) / 256.0F;
    float yaw = (buffer.readByte() * 360) / 256.0F;

    ClientWorld world = MinecraftClient.getInstance().world;

    Entity entity = type.create(world);

    var arr = buffer.array();

    client.execute(() -> {
      if (world != null && entity != null) {
        logger.info("got here1");
        logger.info("buffer: " + Arrays.toString(arr));
        logger.info("got here2");

        entity.setPosition(x, y, z);
        entity.updateTrackedPosition(x, y, z);
        entity.setPitch(pitch);
        entity.getYaw(yaw);
        entity.setId(entityID);
        entity.setUuid(entityUUID);

        logger.info("spawned entity: " + entity.getClass().getName());
        if (entity instanceof WickedPaintingEntity e) {
          logger.info("got here3");

          logger.info("found WickedPaintingEntity: " + e.getClass().getName());
          logger.info("entityID: " + entityID);
          logger.info("entityUUID: " + entityUUID);

          e.readFromBuffer(buffer);

          logger.info("identifier: " + e.identifier);
          logger.info("url: " + e.url);
          world.addEntity(entityID, e);
        } else {
          world.addEntity(entityID, entity);
        }
      }
    });
  }


}
