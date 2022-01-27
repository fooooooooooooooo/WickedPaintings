package ooo.foooooooooooo.wickedpaintings.network;

import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import io.netty.buffer.ByteBuf;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtElement;
import net.minecraft.nbt.NbtList;
import net.minecraft.nbt.NbtOps;
import net.minecraft.network.PacketByteBuf;

public class ExtendedPacketBuffer extends PacketByteBuf {
  public ExtendedPacketBuffer(ByteBuf wrapped) {
    super(wrapped);
  }

  protected void writeObject(Object object) {
    ObjectBufferUtils.writeObject(object, this);
  }

  protected Object readObject() {
    return ObjectBufferUtils.readObject(this);
  }

  // Supports reading and writing list codec's
  public <T> void writeCodec(Codec<T> codec, T object) {
    DataResult<NbtElement> dataResult = codec.encodeStart(NbtOps.INSTANCE, object);
    if (dataResult.error().isPresent()) {
      throw new RuntimeException("Failed to encode: " + dataResult.error().get().message() + " " + object);
    } else {
      NbtElement tag = dataResult.result().get();
      if (tag instanceof NbtCompound) {
        writeByte(0);
        writeNbt((NbtCompound) tag);
      } else if (tag instanceof NbtList) {
        writeByte(1);
        NbtCompound compoundTag = new NbtCompound();
        compoundTag.put("tag", tag);
        writeNbt(compoundTag);
      } else {
        throw new RuntimeException("Failed to write: " + tag);
      }
    }
  }

  public <T> T readCodec(Codec<T> codec) {
    byte type = readByte();
    NbtElement tag = null;

    if (type == 0) {
      tag = readNbt();
    } else if (type == 1) {
      tag = readNbt().get("tag");
    } else {
      throw new RuntimeException("Failed to read codec");
    }

    DataResult<T> dataResult = codec.parse(NbtOps.INSTANCE, tag);

    if (dataResult.error().isPresent()) {
      throw new RuntimeException("Failed to decode: " + dataResult.error().get().message() + " " + tag);
    } else {
      return dataResult.result().get();
    }
  }
}
