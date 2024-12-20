package ooo.foooooooooooo.wickedpaintings.entity;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.decoration.AbstractDecorationEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.network.listener.ClientPlayPacketListener;
import net.minecraft.network.packet.Packet;
import net.minecraft.network.packet.s2c.play.EntitySpawnS2CPacket;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.GameRules;
import net.minecraft.world.World;
import ooo.foooooooooooo.wickedpaintings.common.Constants;
import ooo.foooooooooooo.wickedpaintings.item.ModItems;
import ooo.foooooooooooo.wickedpaintings.network.WickedEntitySpawnPacket;
import org.jetbrains.annotations.Nullable;

import java.util.Objects;


public class WickedPaintingEntity extends AbstractDecorationEntity {
  private String url = "";
  private int width = 16;
  private int height = 16;
  private Identifier imageId = Constants.DEFAULT_TEX;

  public WickedPaintingEntity(EntityType<? extends AbstractDecorationEntity> entityType, World world) {
    super(entityType, world);
  }

  public WickedPaintingEntity(World world, BlockPos position, Direction direction) {
    super(ModEntityTypes.WICKED_PAINTING, world, position);
    super.setFacing(direction);
  }

  public int getRealWidth() {
    return this.width;
  }

  public int getRealHeight() {
    return this.height;
  }

  public String getUrl() {
    return this.url;
  }

  public Identifier getImageId() {
    return imageId;
  }
  public void setImageId(Identifier imageId) {
    this.imageId = imageId;
  }

  @Override
  public NbtCompound writeNbt(NbtCompound nbt) {
    super.writeNbt(nbt);
    this.writeCustomDataToNbt(nbt);

    return nbt;
  }

  @Override
  public void writeCustomDataToNbt(NbtCompound nbt) {
    nbt.putInt(Constants.FACING, this.facing.getHorizontal());

    nbt.putString(Constants.URL, this.url);

    nbt.putInt(Constants.WIDTH, this.width);
    nbt.putInt(Constants.HEIGHT, this.height);

    nbt.putString(Constants.IMAGE_ID, this.imageId.toString());

    super.writeCustomDataToNbt(nbt);
  }

  @Override
  public void readCustomDataFromNbt(NbtCompound nbt) {
    this.setFacing(Direction.fromHorizontal(nbt.getByte(Constants.FACING)));

    this.url = nbt.getString(Constants.URL);
    this.url = Objects.requireNonNullElse(this.url, "");

    this.width = nbt.getInt(Constants.WIDTH);
    this.height = nbt.getInt(Constants.HEIGHT);

    this.imageId = Identifier.tryParse(nbt.getString(Constants.IMAGE_ID));
    this.imageId = Objects.requireNonNullElse(this.imageId, Constants.DEFAULT_TEX);

    super.readCustomDataFromNbt(nbt);
    this.updateAttachmentPosition();
  }

  @Override
  public int getWidthPixels() {
    return this.width * 16;
  }

  @Override
  public int getHeightPixels() {
    return this.height * 16;
  }

  @Override
  public void onBreak(@Nullable Entity entity) {
    if (this.getWorld().getGameRules().getBoolean(GameRules.DO_ENTITY_DROPS)) {
      this.playSound(SoundEvents.ENTITY_PAINTING_BREAK, 1.0F, 1.0F);

      if (entity instanceof PlayerEntity playerEntity) {
        if (playerEntity.getAbilities().creativeMode) {
          return;
        }
      }

      var itemStack = new ItemStack(ModItems.WICKED_PAINTING);
      var nbt = itemStack.getOrCreateNbt();

      this.writeCustomDataToNbt(nbt);
      this.dropStack(itemStack);
    }
  }

  @Override
  public void onPlace() {
    this.playSound(SoundEvents.ENTITY_PAINTING_PLACE, 1.0F, 1.0F);
  }

  @Override
  public void readNbt(NbtCompound nbt) {
    super.readNbt(nbt);
    this.readCustomDataFromNbt(nbt);
  }

  @SuppressWarnings("unchecked")
  @Override
  public Packet<ClientPlayPacketListener> createSpawnPacket() {
    // why
    return (Packet<ClientPlayPacketListener>) WickedEntitySpawnPacket.createPacket(this);
  }

  @Override
  public void onSpawnPacket(EntitySpawnS2CPacket packet) {
    super.onSpawnPacket(packet);

    if (packet instanceof WickedEntitySpawnPacket wickedPacket) {
      this.readCustomDataFromNbt(wickedPacket.getCustomData());
    }
  }

  @Override
  public ItemStack getPickBlockStack() {
    var itemStack = new ItemStack(ModItems.WICKED_PAINTING);

    this.writeCustomDataToNbt(itemStack.getOrCreateNbt());

    return itemStack;
  }
}
