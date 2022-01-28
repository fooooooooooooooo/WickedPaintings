package ooo.foooooooooooo.wickedpaintings.item;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.item.TooltipContext;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.DecorationItem;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemUsageContext;
import net.minecraft.nbt.NbtByteArray;
import net.minecraft.nbt.NbtOps;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.Identifier;
import net.minecraft.util.TypedActionResult;
import net.minecraft.world.World;
import ooo.foooooooooooo.wickedpaintings.client.WickedScreen;
import ooo.foooooooooooo.wickedpaintings.entity.ModEntityTypes;
import ooo.foooooooooooo.wickedpaintings.entity.WickedPaintingEntity;
import ooo.foooooooooooo.wickedpaintings.image.ImageLoaderManager;

import java.io.InputStream;
import java.net.URL;
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

    byte[] data = new byte[]{};

    var url = "https://i.imgur.com/removed.png";
    try {
      URL u = new URL(url);

      try (InputStream in = u.openStream()) {
        data = in.readAllBytes();
      }
    } catch (Exception e) {
      e.printStackTrace();
    }


    var id = ImageLoaderManager.LoadedImage.generateIdentifier(url);
    Identifier.CODEC
        .encodeStart(NbtOps.INSTANCE, id)
        .result()
        .ifPresent(identifier -> nbt.put("Identifier", identifier));

    nbt.putUuid("UUID", UUID.randomUUID());
    nbt.putString("Url", url);
    nbt.putInt("Width", 512);
    nbt.putInt("Height", 512);
    nbt.putByteArray("Data", data);

    MinecraftClient.getInstance().setScreen(new WickedScreen(stack));
  }

  @Override
  public void appendTooltip(ItemStack itemStack, World world, List<Text> tooltip, TooltipContext tooltipContext) {
    tooltip.add(new TranslatableText(getOrCreateTranslationKey() + ".tooltip"));
  }

  public ActionResult useOnBlock(ItemUsageContext context) {
    var blockPos = context.getBlockPos();
    var direction = context.getSide();
    var blockPos2 = blockPos.offset(direction);
    var playerEntity = context.getPlayer();
    var itemStack = context.getStack();

    if (playerEntity != null && !this.canPlaceOn(playerEntity, direction, itemStack, blockPos2)) {
      return ActionResult.FAIL;
    }

    World world = context.getWorld();
    var wickedEntity = new WickedPaintingEntity(ModEntityTypes.WICKED_PAINTING, world);

    var nbt = itemStack.getOrCreateNbt();

    nbt.put("Data", new NbtByteArray(new byte[]{
        // todo: pepelaugh
    }));

    wickedEntity.readCustomDataFromNbt(nbt);

    return ActionResult.CONSUME;
  }

}
