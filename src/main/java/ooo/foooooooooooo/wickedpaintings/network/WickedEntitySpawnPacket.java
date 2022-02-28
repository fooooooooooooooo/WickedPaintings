package ooo.foooooooooooo.wickedpaintings.network;

import io.netty.buffer.Unpooled;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayNetworkHandler;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.network.Packet;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.packet.s2c.play.EntitySpawnS2CPacket;
import net.minecraft.util.math.Direction;
import ooo.foooooooooooo.wickedpaintings.entity.WickedPaintingEntity;

public class WickedEntitySpawnPacket extends EntitySpawnS2CPacket {

    private final NbtCompound customData;
    private final Direction facing;

    public WickedEntitySpawnPacket(WickedPaintingEntity entity, int id) {
        super(entity, id);

        var nbt = new NbtCompound();
        entity.writeCustomDataToNbt(nbt);

        this.customData = nbt;
        this.facing = entity.getHorizontalFacing();
    }

    public WickedEntitySpawnPacket(PacketByteBuf buf) {
        super(buf);
        this.customData = buf.readNbt();
        this.facing = Direction.fromHorizontal(buf.readByte());
    }

    public static Packet<?> createPacket(WickedPaintingEntity entity) {
        var passedData = new PacketByteBuf(Unpooled.buffer());
        new WickedEntitySpawnPacket(entity, entity.getId()).write(passedData);
        return ServerPlayNetworking.createS2CPacket(Packets.WICKED_SPAWN, passedData);
    }

    @Environment(EnvType.CLIENT)
    public static void handle(MinecraftClient client, ClientPlayNetworkHandler handler, PacketByteBuf buffer) {
        var packet = new WickedEntitySpawnPacket(buffer);

        client.execute(() -> handler.onEntitySpawn(packet));
    }

    public NbtCompound getCustomData() {
        return this.customData;
    }

    public void write(PacketByteBuf buf) {
        super.write(buf);
        buf.writeNbt(this.customData);
        buf.writeByte(this.facing.getHorizontal());
    }
}