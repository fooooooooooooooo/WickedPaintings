package ooo.foooooooooooo.wickedpaintings.entity;

import net.fabricmc.loader.impl.util.log.Log;
import net.fabricmc.loader.impl.util.log.LogCategory;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.decoration.painting.PaintingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.network.Packet;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.Direction;
import net.minecraft.world.GameRules;
import net.minecraft.world.World;
import ooo.foooooooooooo.wickedpaintings.item.ModItems;
import ooo.foooooooooooo.wickedpaintings.network.packet.WickedPaintingSpawnS2CPacket;
import org.jetbrains.annotations.Nullable;

public class WickedPaintingEntity extends PaintingEntity {
  public String url = "";

  public int width = 16;
  public int height = 16;
  public Identifier identifier;
  public byte[] data;

  public WickedPaintingEntity(EntityType<? extends PaintingEntity> entityType, World world) {
    super(entityType, world);
  }

  @Override
  public int getWidthPixels() {
    return this.width;
  }

  @Override
  public int getHeightPixels() {
    return this.height;
  }

  @Override
  public void writeCustomDataToNbt(NbtCompound nbt) {
    nbt.putString("Url", this.url);
    nbt.putByte("Facing", (byte) this.facing.getHorizontal());
    nbt.putInt("Width", this.width);
    nbt.putInt("Height", this.height);
    nbt.putString("Identifier", this.identifier.toString());
    nbt.putByteArray("Data", this.data);

    super.writeCustomDataToNbt(nbt);
  }

  @Override
  public void readCustomDataFromNbt(NbtCompound nbt) {
    this.url = nbt.getString("Url");
    this.facing = Direction.fromHorizontal(nbt.getByte("Facing"));
    this.width = nbt.getInt("Width");
    this.height = nbt.getInt("Height");
    this.identifier = Identifier.tryParse(nbt.getString("Identifier"));
    this.data = nbt.getByteArray("Data");

    super.readCustomDataFromNbt(nbt);
    this.setFacing(this.facing);
  }

  @Override
  public void onBreak(@Nullable Entity entity) {
    Log.info(LogCategory.LOG, "Painting broken");
    if (this.world.getGameRules().getBoolean(GameRules.DO_ENTITY_DROPS)) {
      this.playSound(SoundEvents.ENTITY_PAINTING_BREAK, 1.0F, 1.0F);
      if (entity instanceof PlayerEntity playerEntity) {
        if (playerEntity.getAbilities().creativeMode) {
          return;
        }
      }

      this.dropItem(ModItems.WICKED_PAINTING);
    }
  }

  @Override
  public void onPlace() {
    Log.info(LogCategory.LOG, "Painting placed");
    this.playSound(SoundEvents.ENTITY_PAINTING_PLACE, 1.0F, 1.0F);
  }

  @Override
  public Packet<?> createSpawnPacket() {
    Log.info(LogCategory.LOG, "Sending spawn packet");
    return WickedPaintingSpawnS2CPacket.createPacket(this);
  }

  @Override
  public ItemStack getPickBlockStack() {
    Log.info(LogCategory.LOG, "Painting picked");
    return new ItemStack(ModItems.WICKED_PAINTING);
  }

  @Override
  public boolean shouldRenderName() {
    return true;
  }

  public void readFromBuffer(PacketByteBuf buffer) {
    Log.info(LogCategory.LOG, "Reading painting from buffer");

    this.url = buffer.readString();
    this.setFacing(Direction.fromHorizontal(buffer.readByte()));
    this.width = buffer.readInt();
    this.height = buffer.readInt();
    this.identifier = Identifier.tryParse(buffer.readString());
    this.data = buffer.readByteArray();
  }

  public void writeToBuffer(PacketByteBuf buf) {
    Log.info(LogCategory.LOG, "Writing painting to buffer");

    buf.writeString(this.url);
    buf.writeByte(this.facing.getHorizontal());
    buf.writeInt(this.getWidthPixels());
    buf.writeInt(this.getHeightPixels());
    buf.writeString(this.identifier.toString());
    buf.writeByteArray(this.data);
  }
}
