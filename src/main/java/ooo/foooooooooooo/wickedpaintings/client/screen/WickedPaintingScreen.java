package ooo.foooooooooooo.wickedpaintings.client.screen;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Hand;

public class WickedPaintingScreen extends WickedScreen {
  public WickedPaintingScreen(Hand hand, PlayerEntity player, ItemStack stack) {
    super(new WickedGuiDescription(hand, player, stack));
  }
}
