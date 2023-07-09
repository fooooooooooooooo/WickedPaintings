package ooo.foooooooooooo.wickedpaintings.item;

import net.fabricmc.fabric.api.screenhandler.v1.ExtendedScreenHandlerFactory;
import net.minecraft.client.item.TooltipContext;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.StackReference;
import net.minecraft.item.DecorationItem;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemUsageContext;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.screen.NamedScreenHandlerFactory;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.TypedActionResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;
import net.minecraft.world.event.GameEvent;
import ooo.foooooooooooo.wickedpaintings.NbtConstants;
import ooo.foooooooooooo.wickedpaintings.client.ImageManager;
import ooo.foooooooooooo.wickedpaintings.client.screen.WickedGuiDescription;
import ooo.foooooooooooo.wickedpaintings.entity.ModEntityTypes;
import ooo.foooooooooooo.wickedpaintings.entity.WickedPaintingEntity;

import java.util.List;

public class WickedPaintingItem extends DecorationItem {
  public WickedPaintingItem(Settings settings) {
    super(ModEntityTypes.WICKED_PAINTING, settings);
  }

  public TypedActionResult<ItemStack> use(World world, PlayerEntity player, Hand hand) {
    player.openHandledScreen(createScreenHandlerFactory(player, hand));
    return TypedActionResult.success(player.getStackInHand(hand));
  }

  private NamedScreenHandlerFactory createScreenHandlerFactory(PlayerEntity player, Hand hand) {
    EquipmentSlot slot = switch (hand) {
      case MAIN_HAND -> EquipmentSlot.MAINHAND;
      case OFF_HAND -> EquipmentSlot.OFFHAND;
    };

    ItemStack stack = player.getStackInHand(hand);

    return new ExtendedScreenHandlerFactory() {
      @Override
      public Text getDisplayName() {
        return stack.getName();
      }

      @Override
      public ScreenHandler createMenu(int syncId, PlayerInventory playerInventory, PlayerEntity player) {
        return new WickedGuiDescription(syncId, playerInventory, StackReference.of(player, slot));
      }

      @Override
      public void writeScreenOpeningData(ServerPlayerEntity player, PacketByteBuf buf) {
        buf.writeEnumConstant(slot);
      }
    };
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

      // Clamp width and height to between 1 and 32

      var width = Math.max(1, Math.min(32, nbt.getInt(NbtConstants.WIDTH)));
      nbt.putInt(NbtConstants.WIDTH, width);

      var height = Math.max(1, Math.min(32, nbt.getInt(NbtConstants.HEIGHT)));
      nbt.putInt(NbtConstants.HEIGHT, height);

      nbt.putInt(NbtConstants.FACING, direction.getHorizontal());

      // Don't know why this is necessary, but it is
      nbt.putInt(NbtConstants.ATTACHMENT_POS_X, blockPos2.getX());
      nbt.putInt(NbtConstants.ATTACHMENT_POS_Y, blockPos2.getY());
      nbt.putInt(NbtConstants.ATTACHMENT_POS_Z, blockPos2.getZ());

      wickedEntity.readCustomDataFromNbt(nbt);

      // Also don't know why this is necessary, but it is
      wickedEntity.setPos(blockPos2.getX(), blockPos2.getY(), blockPos2.getZ());

      EntityType.loadFromEntityNbt(world, playerEntity, wickedEntity, nbt);

      if (wickedEntity.canStayAttached()) {
        if (!world.isClient) {
          wickedEntity.onPlace();

          world.emitGameEvent(playerEntity, GameEvent.ENTITY_PLACE, blockPos);
          world.spawnEntity(wickedEntity);
        }

        itemStack.decrement(1);
        return ActionResult.success(world.isClient);
      } else {
        return ActionResult.CONSUME;
      }
    }
  }

  @Override
  public void appendTooltip(ItemStack itemStack, World world, List<Text> tooltip, TooltipContext tooltipContext) {
    tooltip.add(Text.translatable(getOrCreateTranslationKey() + ".tooltip"));
  }
}
