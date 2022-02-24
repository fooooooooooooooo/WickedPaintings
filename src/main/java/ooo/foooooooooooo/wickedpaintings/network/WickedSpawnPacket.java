package ooo.foooooooooooo.wickedpaintings.network;

import net.minecraft.entity.Entity;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.packet.s2c.play.EntitySpawnS2CPacket;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.Direction;
import net.minecraft.util.registry.Registry;

public class WickedSpawnPacket extends EntitySpawnS2CPacket {
    private final Direction facing;
    private final String url;
    private final int width;
    private final int height;
    private final Identifier imageId;

    public WickedSpawnPacket(Entity entity, String url, int width, int height, Identifier imageId) {
        super(entity);

        this.facing = entity.getHorizontalFacing();

        this.url = url;

        this.width = width;
        this.height = height;

        this.imageId = imageId;
    }

    @Override
    public void write(PacketByteBuf buf) {
        buf.writeVarInt(this.getId());
        buf.writeUuid(this.getUuid());
        buf.writeVarInt(Registry.ENTITY_TYPE.getRawId(this.getEntityTypeId()));
        buf.writeDouble(this.getX());
        buf.writeDouble(this.getY());
        buf.writeDouble(this.getZ());
        buf.writeByte(this.getPitch());
        buf.writeByte(this.getYaw());
        buf.writeInt(this.getEntityData());

        buf.writeShort((int) this.getVelocityX());
        buf.writeShort((int) this.getVelocityY());
        buf.writeShort((int) this.getVelocityZ());

        buf.writeByte(this.getFacing().getHorizontal());

        buf.writeString(this.url);

        buf.writeInt(this.width);
        buf.writeInt(this.height);

        buf.writeIdentifier(this.imageId);
    }

    public String getUrl() {
        return this.url;
    }

    public int getWidth() {
        return width;
    }

    public int getHeight() {
        return height;
    }

    public Identifier getImageId() {
        return imageId;
    }

    public Direction getFacing() {
        return this.facing;
    }
}
