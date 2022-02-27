package ooo.foooooooooooo.wickedpaintings.network;

import net.minecraft.nbt.NbtCompound;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.listener.ClientPlayPacketListener;
import net.minecraft.network.packet.s2c.play.EntitySpawnS2CPacket;
import ooo.foooooooooooo.wickedpaintings.entity.WickedPaintingEntity;

public class WickedEntitySpawnPacket extends EntitySpawnS2CPacket {

    private final NbtCompound customData;

    public WickedEntitySpawnPacket(WickedPaintingEntity entity) {
        super(entity);
        var nbt = new NbtCompound();
        entity.writeCustomDataToNbt(nbt);
        this.customData = nbt;
    }

    public WickedEntitySpawnPacket(PacketByteBuf buf) {
        super(buf);
        this.customData = buf.readNbt();
    }

    public NbtCompound getCustomData() {
        return this.customData;
    }

    @Override
    public void write(PacketByteBuf buf) {
        super.write(buf);
        buf.writeNbt(this.customData);
    }

    @Override
    public void apply(ClientPlayPacketListener clientPlayPacketListener) {
        clientPlayPacketListener.onEntitySpawn(this);
    }
}