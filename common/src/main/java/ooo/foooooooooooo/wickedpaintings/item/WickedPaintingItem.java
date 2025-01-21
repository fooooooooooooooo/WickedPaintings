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
import net.minecraft.text.Text;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.TypedActionResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;
import net.minecraft.world.event.GameEvent;
import ooo.foooooooooooo.wickedpaintings.Constants;
import ooo.foooooooooooo.wickedpaintings.client.screen.WickedPaintingScreen;
import ooo.foooooooooooo.wickedpaintings.entity.WickedPaintingEntity;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class WickedPaintingItem extends DecorationItem {
  public WickedPaintingItem(Settings settings) {
    // override useOnBlock and appendTooltip, which are the only methods that use the entityType parameter from super
    // constructor so it's safe to null
    super(null, settings);
  }

  private static @NotNull NbtCompound getNbt(ItemStack stack, Direction direction, BlockPos pos) {
    var nbt = stack.getOrCreateNbt();

    if (nbt.getString(Constants.URL) == null || nbt.getString(Constants.URL).isEmpty()) {
      nbt.putString(Constants.URL, "");
    }

    // Clamp width and height to between 1 and 32

    var width = Math.max(1, Math.min(32, nbt.getInt(Constants.WIDTH)));
    nbt.putInt(Constants.WIDTH, width);

    var height = Math.max(1, Math.min(32, nbt.getInt(Constants.HEIGHT)));
    nbt.putInt(Constants.HEIGHT, height);

    nbt.putInt(Constants.FACING, direction.getHorizontal());

    nbt.putInt(Constants.ATTACHMENT_POS_X, pos.getX());
    nbt.putInt(Constants.ATTACHMENT_POS_Y, pos.getY());
    nbt.putInt(Constants.ATTACHMENT_POS_Z, pos.getZ());

    return nbt;
  }

  @Override
  public TypedActionResult<ItemStack> use(World world, PlayerEntity player, Hand hand) {
    if (world.isClient) {
      openScreen(player, hand);
    }

    return TypedActionResult.success(player.getStackInHand(hand));
  }

  private void openScreen(PlayerEntity player, Hand hand) {
    MinecraftClient
      .getInstance()
      .setScreen(new WickedPaintingScreen(player.getStackInHand(hand), player.getInventory()));
  }

  @Override
  public ActionResult useOnBlock(ItemUsageContext context) {
    var offsetPos = context.getBlockPos().offset(context.getSide());

    if (context.getPlayer() != null && !this.canPlaceOn(context.getPlayer(),
      context.getSide(),
      context.getStack(),
      offsetPos
    )) {
      return ActionResult.FAIL;
    } else {
      var world = context.getWorld();

      var wickedEntity = new WickedPaintingEntity(world, offsetPos, context.getSide());
      var nbt = getNbt(context.getStack(), context.getSide(), offsetPos);

      wickedEntity.readCustomDataFromNbt(nbt);
      wickedEntity.setPos(offsetPos.getX(), offsetPos.getY(), offsetPos.getZ());

      EntityType.loadFromEntityNbt(world, context.getPlayer(), wickedEntity, nbt);

      if (wickedEntity.canStayAttached()) {
        if (!world.isClient) {
          wickedEntity.onPlace();

          world.emitGameEvent(context.getPlayer(), GameEvent.ENTITY_PLACE, context.getBlockPos());
          world.spawnEntity(wickedEntity);
        }

        context.getStack().decrement(1);
        return ActionResult.success(world.isClient);
      } else {
        return ActionResult.CONSUME;
      }
    }
  }

  @Environment(EnvType.CLIENT)
  @Override
  public void appendTooltip(ItemStack itemStack, World world, List<Text> tooltip, TooltipContext tooltipContext) {
    tooltip.add(Text.translatable(getOrCreateTranslationKey() + ".tooltip"));
  }
}
