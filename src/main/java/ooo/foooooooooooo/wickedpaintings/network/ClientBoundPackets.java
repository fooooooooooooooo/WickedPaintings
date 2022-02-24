package ooo.foooooooooooo.wickedpaintings.network;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.fabricmc.fabric.api.networking.v1.PacketSender;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayNetworkHandler;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.Direction;
import ooo.foooooooooooo.wickedpaintings.WickedPaintings;
import ooo.foooooooooooo.wickedpaintings.entity.WickedPaintingEntity;

public class ClientBoundPackets {
    public static final Identifier WICKED_SPAWN = new Identifier(WickedPaintings.MOD_ID, "wicked_spawn");

    public static void registerPackets() {
        registerClientBoundHandler(WICKED_SPAWN, (a, b, c, d) -> {
            WickedPaintings.LOGGER.info("Received entity: " + c.readUuid());

            ClientBoundPackets.onWickedSpawn(a, b, c, d);
        });
    }

    @Environment(EnvType.CLIENT)
    public static void registerClientBoundHandler(Identifier identifier, ClientPlayNetworking.PlayChannelHandler handler) {
        ClientPlayNetworking.registerGlobalReceiver(identifier, handler);
    }

    public static void sendWickedSpawn(WickedPaintingEntity wickedEntity) {
        var buffer = PacketByteBufs.create();
        buffer.writeBlockPos(wickedEntity.getBlockPos());
        buffer.writeByte(wickedEntity.getDirection().getHorizontal());

        var nbt = new NbtCompound();
        wickedEntity.writeCustomDataToNbt(nbt);

        buffer.writeNbt(nbt);

        // write entity data
        buffer.writeVarInt(wickedEntity.getId());
        buffer.writeUuid(wickedEntity.getUuid());
        buffer.writeFloat(wickedEntity.getPitch());
        buffer.writeFloat(wickedEntity.getYaw());

        WickedPaintings.LOGGER.info("Sending entity: " + wickedEntity.getUuid());

        ClientPlayNetworking.send(WICKED_SPAWN, buffer);
    }

    private static void onWickedSpawn(MinecraftClient client, ClientPlayNetworkHandler handler, PacketByteBuf buffer, PacketSender sender) {
        client.execute(() -> {
            WickedPaintings.LOGGER.info("Received entity");
            var world = client.world;
            if (world == null) {
                return;
            }

            var pos = buffer.readBlockPos();
            var direction = buffer.readByte();

            var wickedEntity = new WickedPaintingEntity(world, pos, Direction.fromHorizontal(direction));
            wickedEntity.readCustomDataFromNbt(buffer.readNbt());

            // read entity data
            var entityId = buffer.readVarInt();
            var uuid = buffer.readUuid();
            var pitch = buffer.readFloat();
            var yaw = buffer.readFloat();

            wickedEntity.setId(entityId);
            wickedEntity.setUuid(uuid);
            wickedEntity.setPitch((pitch * 360) / 256.0F);
            wickedEntity.setYaw((yaw * 360) / 256.0F);

            wickedEntity.updateTrackedPosition(pos.getX(), pos.getY(), pos.getZ());
            wickedEntity.refreshPositionAfterTeleport(pos.getX(), pos.getY(), pos.getZ());

            WickedPaintings.LOGGER.info("Spawned entity: " + wickedEntity.getUuid());
            WickedPaintings.LOGGER.info("Entity ID: " + wickedEntity.getId());
            WickedPaintings.LOGGER.info("Entity pitch: " + wickedEntity.getPitch());
            WickedPaintings.LOGGER.info("Entity yaw: " + wickedEntity.getYaw());
            WickedPaintings.LOGGER.info("Entity pos: " + wickedEntity.getBlockPos());
            WickedPaintings.LOGGER.info("Entity direction: " + wickedEntity.getDirection());

            client.world.spawnEntity(wickedEntity);
        });
    }

}
