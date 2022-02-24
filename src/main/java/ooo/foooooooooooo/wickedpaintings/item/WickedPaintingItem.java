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
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.TypedActionResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;
import net.minecraft.world.event.GameEvent;
import ooo.foooooooooooo.wickedpaintings.NbtConstants;
import ooo.foooooooooooo.wickedpaintings.client.ImageManager;
import ooo.foooooooooooo.wickedpaintings.client.screen.WickedPaintingScreen;
import ooo.foooooooooooo.wickedpaintings.entity.ModEntityTypes;
import ooo.foooooooooooo.wickedpaintings.entity.WickedPaintingEntity;
import ooo.foooooooooooo.wickedpaintings.network.ClientBoundPackets;

import java.util.List;

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
        var player = MinecraftClient.getInstance().player;

        if (player != null) {
            MinecraftClient.getInstance().setScreen(new WickedPaintingScreen(player.getActiveHand(), player, stack));
        }
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

            var wickedEntity = new WickedPaintingEntity(world, blockPos2, direction);
            var nbt = itemStack.getOrCreateNbt();

            if (nbt.getString(NbtConstants.URL) == null || nbt.getString(NbtConstants.URL).isEmpty()) {
                nbt.putString(NbtConstants.URL, "https://cdn.discordapp.com/attachments/902081288645804042/946165664345886800/FMS-3LjWQAY1cq9.png");
                nbt.putString(NbtConstants.IMAGE_ID, ImageManager.DEFAULT_IMAGE_ID.toString());
            }

            var width = Math.max(1, Math.min(32, nbt.getInt(NbtConstants.WIDTH)));
            nbt.putInt(NbtConstants.WIDTH, width);

            var height = Math.max(1, Math.min(32, nbt.getInt(NbtConstants.HEIGHT)));
            nbt.putInt(NbtConstants.HEIGHT, height);

            nbt.putInt(NbtConstants.FACING, direction.getHorizontal());

            nbt.putInt(NbtConstants.ATTACHMENT_POS_X, blockPos2.getX());
            nbt.putInt(NbtConstants.ATTACHMENT_POS_Y, blockPos2.getY());
            nbt.putInt(NbtConstants.ATTACHMENT_POS_Z, blockPos2.getZ());

            wickedEntity.readCustomDataFromNbt(nbt);

            wickedEntity.setPos(blockPos2.getX(), blockPos2.getY(), blockPos2.getZ());

            EntityType.loadFromEntityNbt(world, playerEntity, wickedEntity, nbt);

            if (wickedEntity.canStayAttached()) {
                if (!world.isClient) {
                    wickedEntity.onPlace();

                    world.emitGameEvent(playerEntity, GameEvent.ENTITY_PLACE, blockPos);
                    world.spawnEntity(wickedEntity);
                    ClientBoundPackets.sendWickedSpawn(wickedEntity);
                }

                itemStack.decrement(1);
                return ActionResult.success(world.isClient);
            } else {
                return ActionResult.CONSUME;
            }
        }
    }
}
