package ooo.foooooooooooo.wickedpaintings.item;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.item.TooltipContext;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.DecorationItem;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemUsageContext;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtOps;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.Identifier;
import net.minecraft.util.TypedActionResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;
import net.minecraft.world.event.GameEvent;
import ooo.foooooooooooo.wickedpaintings.NbtConstants;
import ooo.foooooooooooo.wickedpaintings.WickedPaintings;
import ooo.foooooooooooo.wickedpaintings.client.ImageManager;
import ooo.foooooooooooo.wickedpaintings.client.WickedScreen;
import ooo.foooooooooooo.wickedpaintings.entity.ModEntityTypes;
import ooo.foooooooooooo.wickedpaintings.entity.WickedPaintingEntity;

import java.util.List;
import java.util.UUID;

public class WickedPaintingItem extends DecorationItem {
    public WickedPaintingItem(Settings settings) {
        super(ModEntityTypes.WICKED_PAINTING, settings);
    }

    public TypedActionResult<ItemStack> use(World world, PlayerEntity player, Hand hand) {
        ItemStack stack = player.getStackInHand(hand);
        if (world.isClient()) {
            openClientGui(stack);
        }

        return TypedActionResult.success(player.getStackInHand(hand));
    }

    @Environment(EnvType.CLIENT)
    private void openClientGui(ItemStack stack) {
        var nbt = stack.getOrCreateNbt();

        String url = "https://i.imgur.com/removed.png";

        var id = ImageManager.generateIdentifier(url);
        Identifier.CODEC
                .encodeStart(NbtOps.INSTANCE, id)
                .result()
                .ifPresent(identifier -> nbt.put(NbtConstants.IMAGE_ID, identifier));

        nbt.putUuid(NbtConstants.UUID, UUID.randomUUID());
        nbt.putString(NbtConstants.URL, url);
        nbt.putInt(NbtConstants.WIDTH, 512);
        nbt.putInt(NbtConstants.HEIGHT, 512);

        MinecraftClient.getInstance().setScreen(new WickedScreen(stack));
    }

    @Override
    public void appendTooltip(ItemStack itemStack, World world, List<Text> tooltip, TooltipContext tooltipContext) {
        tooltip.add(new TranslatableText(getOrCreateTranslationKey() + ".tooltip"));
    }

    @Override
    public ActionResult useOnBlock(ItemUsageContext context) {
        BlockPos blockPos = context.getBlockPos();
        Direction direction = context.getSide();
        BlockPos blockPos2 = blockPos.offset(direction);
        PlayerEntity playerEntity = context.getPlayer();
        ItemStack itemStack = context.getStack();

        if (playerEntity != null && !this.canPlaceOn(playerEntity, direction, itemStack, blockPos2)) {
            return ActionResult.FAIL;
        } else {
            World world = context.getWorld();

            var wickedEntity = new WickedPaintingEntity(ModEntityTypes.WICKED_PAINTING, world, blockPos2, direction);
            var nbt = itemStack.getNbt();
            if (nbt == null) nbt = new NbtCompound();

            nbt.remove("Facing");
            nbt.putInt("TileX", blockPos2.getX());
            nbt.putInt("TileY", blockPos2.getY());
            nbt.putInt("TileZ", blockPos2.getZ());

            wickedEntity.readCustomDataFromNbt(nbt);

            NbtCompound nbtCompound = itemStack.getNbt();
            if (nbtCompound != null) {
                EntityType.loadFromEntityNbt(world, playerEntity, wickedEntity, nbtCompound);
            }

            if ((wickedEntity).canStayAttached()) {
                if (!world.isClient) {
                    (wickedEntity).onPlace();
                    world.emitGameEvent(playerEntity, GameEvent.ENTITY_PLACE, blockPos);
                    world.spawnEntity(wickedEntity);
                    WickedPaintings.LOGGER.info("Placed painting at " + wickedEntity.getPos());
                }

                itemStack.decrement(1);
                return ActionResult.success(world.isClient);
            } else {
                return ActionResult.CONSUME;
            }
        }
    }

}
