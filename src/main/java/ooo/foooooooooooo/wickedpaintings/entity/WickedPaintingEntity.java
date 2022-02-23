package ooo.foooooooooooo.wickedpaintings.entity;

import net.fabricmc.loader.impl.util.log.Log;
import net.fabricmc.loader.impl.util.log.LogCategory;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.decoration.AbstractDecorationEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.network.Packet;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.packet.s2c.play.EntitySpawnS2CPacket;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.GameRules;
import net.minecraft.world.World;
import ooo.foooooooooooo.wickedpaintings.NbtConstants;
import ooo.foooooooooooo.wickedpaintings.item.ModItems;
import ooo.foooooooooooo.wickedpaintings.network.WickedSpawnPacket;
import org.jetbrains.annotations.Nullable;

public class WickedPaintingEntity extends AbstractDecorationEntity {
    private String url = "https://cdn.discordapp.com/avatars/538012458712039424/c3523bd2e4c84c4e94c3d82a13cfa8ef.png";
    private int width = 16;
    private int height = 16;
    private Identifier imageId;

    public WickedPaintingEntity(EntityType<? extends AbstractDecorationEntity> entityType, World world) {
        super(entityType, world);
    }

    public WickedPaintingEntity(EntityType<? extends AbstractDecorationEntity> entityType, World world, BlockPos position, Direction direction) {
        super(entityType, world, position);
        this.facing = direction;
    }

    @Override
    public int getWidthPixels() {
        return this.width;
    }

    @Override
    public int getHeightPixels() {
        return this.height;
    }

    public String getUrl() {
        return this.url;
    }

    public Identifier getImageId() {
        return imageId;
    }

    @Override
    public void writeCustomDataToNbt(NbtCompound nbt) {
        nbt.putString(NbtConstants.URL, this.url);
        nbt.putByte("Facing", (byte) this.facing.getHorizontal());
        nbt.putInt(NbtConstants.WIDTH, this.width);
        nbt.putInt(NbtConstants.HEIGHT, this.height);
        nbt.putString(NbtConstants.IMAGE_ID, this.imageId.toString());
    }

    @Override
    public void readCustomDataFromNbt(NbtCompound nbt) {
        this.url = nbt.getString(NbtConstants.URL);
        this.facing = Direction.fromHorizontal(nbt.getByte("Facing"));
        this.width = nbt.getInt(NbtConstants.WIDTH);
        this.height = nbt.getInt(NbtConstants.HEIGHT);
        this.imageId = Identifier.tryParse(nbt.getString(NbtConstants.IMAGE_ID));

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

            var itemStack = new ItemStack(ModItems.WICKED_PAINTING);

            var nbt = itemStack.getOrCreateNbt();

            this.writeCustomDataToNbt(nbt);

            this.dropStack(itemStack);
        }
    }

    @Override
    public void onPlace() {
        Log.info(LogCategory.LOG, "Painting placed");
        this.playSound(SoundEvents.ENTITY_PAINTING_PLACE, 1.0F, 1.0F);
    }

    @Override
    public Packet<?> createSpawnPacket() {
        return new WickedSpawnPacket(this, this.url, this.width, this.height, this.imageId);
    }

    @Override
    public void onSpawnPacket(EntitySpawnS2CPacket p) {
        var packet = (WickedSpawnPacket) p;

        int i = packet.getId();
        double d = packet.getX();
        double e = packet.getY();
        double f = packet.getZ();
        this.updateTrackedPosition(d, e, f);
        this.refreshPositionAfterTeleport(d, e, f);
        this.setPitch((float) (packet.getPitch() * 360) / 256.0F);
        this.setYaw((float) (packet.getYaw() * 360) / 256.0F);
        this.setId(i);
        this.setUuid(packet.getUuid());

        this.url = packet.getUrl();
        this.width = packet.getWidth();
        this.height = packet.getHeight();
        this.imageId = packet.getImageId();
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
        Log.info(LogCategory.LOG, "bytes: " + buffer.readableBytes());

        this.url = buffer.readString();
        this.setFacing(Direction.fromHorizontal(buffer.readByte()));
        this.width = buffer.readInt();
        this.height = buffer.readInt();
        this.imageId = Identifier.tryParse(buffer.readString());
    }

    public void writeToBuffer(PacketByteBuf buf) {
        Log.info(LogCategory.LOG, "Writing painting to buffer");

        buf.writeString(this.url);
        buf.writeByte(this.facing.getHorizontal());
        buf.writeInt(this.getWidthPixels());
        buf.writeInt(this.getHeightPixels());
        buf.writeString(this.imageId.toString());
    }
}
