package ooo.foooooooooooo.wickedpaintings.network;


import net.minecraft.nbt.NbtCompound;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;

import java.util.Arrays;
import java.util.Objects;

public enum ObjectBufferUtils {

  STRING(String.class, (string, buffer) -> {
    buffer.writeInt(string.length());
    buffer.writeString(string);
  }, buffer -> {
    return buffer.readString(buffer.readInt());
  }),

  INT(Integer.class, (value, buffer) -> {
    buffer.writeInt(value);
  }, PacketByteBuf::readInt),

  LONG(Long.class, (pos, buffer) -> {
    buffer.writeLong(pos);
  }, ExtendedPacketBuffer::readLong),

  DOUBLE(Double.class, (pos, buffer) -> {
    buffer.writeDouble(pos);
  }, ExtendedPacketBuffer::readDouble),

  FLOAT(Float.class, (pos, buffer) -> {
    buffer.writeFloat(pos);
  }, ExtendedPacketBuffer::readFloat),

  BOOLEAN(Boolean.class, (value, buffer) -> {
    buffer.writeBoolean(value);
  }, ExtendedPacketBuffer::readBoolean),

  BLOCK_POS(BlockPos.class, (pos, buffer) -> {
    buffer.writeBlockPos(pos);
  }, PacketByteBuf::readBlockPos),

  ID(Identifier.class, (id, buffer) -> {
    String string = id.toString();
    buffer.writeInt(string.length());
    buffer.writeString(string);
  }, buffer -> {
    return new Identifier(buffer.readString(buffer.readInt()));
  }),

  COMPOUND_TAG(NbtCompound.class, (value, buffer) -> {
    buffer.writeNbt(value);
  }, PacketByteBuf::readNbt);

  Class clazz;
  ObjectWriter writer;
  ObjectReader reader;

  <T> ObjectBufferUtils(Class<T> clazz, ObjectWriter<T> writer, ObjectReader<T> reader) {
    this.clazz = clazz;
    this.writer = writer;
    this.reader = reader;
  }

  public static void writeObject(Object object, ExtendedPacketBuffer buffer) {
    ObjectBufferUtils utils = Arrays.stream(values()).filter(objectBufferUtils -> objectBufferUtils.clazz == object.getClass()).findFirst().orElse(null);
    Objects.requireNonNull(utils, "No support found for " + object.getClass());
    buffer.writeInt(utils.ordinal());
    utils.writer.write(object, buffer);
  }

  public static Object readObject(ExtendedPacketBuffer buffer) {
    ObjectBufferUtils utils = values()[buffer.readInt()];
    Objects.requireNonNull(utils, "Could not find reader");
    return utils.reader.read(buffer);
  }

  private interface ObjectWriter<T> {
    void write(T object, ExtendedPacketBuffer buffer);
  }

  private interface ObjectReader<T> {
    T read(ExtendedPacketBuffer buffer);
  }

}
