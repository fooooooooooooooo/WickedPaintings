package ooo.foooooooooooo.wickedpaintings.network;

import dev.architectury.networking.NetworkManager;
import io.netty.buffer.Unpooled;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.packet.s2c.play.EntitySpawnS2CPacket;
import net.minecraft.util.math.Direction;
import ooo.foooooooooooo.wickedpaintings.entity.WickedPaintingEntity;

public class WickedEntitySpawnPacket extends EntitySpawnS2CPacket {

  private final NbtCompound customData;
  private final Direction facing;

  public WickedEntitySpawnPacket(WickedPaintingEntity entity) {
    super(entity, entity.getId());

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

  public PacketByteBuf toPacketByteBuf() {
    var buf = new PacketByteBuf(Unpooled.buffer());

    super.write(buf);
    buf.writeNbt(this.customData);
    buf.writeByte(this.facing.getHorizontal());

    return buf;
  }

  @Environment(EnvType.CLIENT)
  public static void handle(PacketByteBuf buf, NetworkManager.PacketContext context) {
    var packet = new WickedEntitySpawnPacket(buf);

    MinecraftClient.getInstance().execute(() -> context.getPlayer().onSpawnPacket(packet));
  }

  public NbtCompound getCustomData() {
    return this.customData;
  }
}
