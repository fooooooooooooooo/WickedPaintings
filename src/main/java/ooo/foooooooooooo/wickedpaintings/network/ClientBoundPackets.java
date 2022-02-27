package ooo.foooooooooooo.wickedpaintings.network;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.fabricmc.fabric.api.networking.v1.PacketSender;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayNetworkHandler;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.Direction;
import ooo.foooooooooooo.wickedpaintings.WickedPaintings;
import ooo.foooooooooooo.wickedpaintings.entity.WickedPaintingEntity;

public class ClientBoundPackets {
    public static final Identifier WICKED_SPAWN = new Identifier(WickedPaintings.MOD_ID, "wicked_spawn");

    public static void registerPackets() {
        registerClientBoundHandler(WICKED_SPAWN, (a, b, c, d) -> {
            WickedPaintings.LOGGERS.info("Received entity: " + c.readUuid());

            ClientBoundPackets.onWickedSpawn(a, b, c, d);
        });
    }

    @Environment(EnvType.CLIENT)
    public static void registerClientBoundHandler(Identifier identifier, ClientPlayNetworking.PlayChannelHandler handler) {
        ClientPlayNetworking.registerGlobalReceiver(identifier, handler);
    }

    public static void sendWickedSpawn(ServerPlayerEntity player, WickedPaintingEntity wickedEntity) {
        var buffer = PacketByteBufs.create();
        buffer.writeBlockPos(wickedEntity.getBlockPos());
        buffer.writeByte(wickedEntity.getDirection().getHorizontal());

        // write entity data
        buffer.writeVarInt(wickedEntity.getId());
        buffer.writeUuid(wickedEntity.getUuid());
        buffer.writeFloat(wickedEntity.getPitch());
        buffer.writeFloat(wickedEntity.getYaw());

        var nbt = new NbtCompound();
        wickedEntity.writeCustomDataToNbt(nbt);

        WickedPaintings.LOGGERS.info("Sending NBT to client: " + nbt);

        buffer.writeNbt(nbt);



        WickedPaintings.LOGGERS.info("Sending entity: " + wickedEntity.getUuid());

        ServerPlayNetworking.send(player, WICKED_SPAWN, buffer);
    }

    private static void onWickedSpawn(MinecraftClient client, ClientPlayNetworkHandler handler, PacketByteBuf buffer, PacketSender sender) {
        System.out.println("Received entity: " + buffer.readUuid());
        WickedPaintings.LOGGERS.info("Received entity");
        var world = client.world;
        if (world == null) {
            return;
        }

        var pos = buffer.readBlockPos();
        var direction = buffer.readByte();


        // read entity data
        var entityId = buffer.readVarInt();
        var uuid = buffer.readUuid();
        var pitch = buffer.readFloat();
        var yaw = buffer.readFloat();

        var wickedEntity = new WickedPaintingEntity(world, pos, Direction.fromHorizontal(direction));

        var nbt = buffer.readNbt();

        wickedEntity.readCustomDataFromNbt(nbt);

        wickedEntity.setId(entityId);
        wickedEntity.setUuid(uuid);
        wickedEntity.setPitch((pitch * 360) / 256.0F);
        wickedEntity.setYaw((yaw * 360) / 256.0F);

        wickedEntity.updateTrackedPosition(pos.getX(), pos.getY(), pos.getZ());
        wickedEntity.refreshPositionAfterTeleport(pos.getX(), pos.getY(), pos.getZ());

        WickedPaintings.LOGGERS.info("Spawned entity: " + wickedEntity.getUuid());
        WickedPaintings.LOGGERS.info("Entity ID: " + wickedEntity.getId());
        WickedPaintings.LOGGERS.info("Entity pitch: " + wickedEntity.getPitch());
        WickedPaintings.LOGGERS.info("Entity yaw: " + wickedEntity.getYaw());
        WickedPaintings.LOGGERS.info("Entity pos: " + wickedEntity.getBlockPos());
        WickedPaintings.LOGGERS.info("Entity direction: " + wickedEntity.getDirection());

        client.execute(() -> {
            client.world.spawnEntity(wickedEntity);
        });
    }

}
