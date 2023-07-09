package ooo.foooooooooooo.wickedpaintings.client.screen;

import io.github.cottonmc.cotton.gui.ItemSyncedGuiDescription;
import io.github.cottonmc.cotton.gui.widget.*;
import io.github.cottonmc.cotton.gui.widget.data.HorizontalAlignment;
import io.github.cottonmc.cotton.gui.widget.data.Insets;
import io.github.cottonmc.cotton.gui.widget.data.VerticalAlignment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.resource.language.I18n;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.StackReference;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import ooo.foooooooooooo.wickedpaintings.NbtConstants;
import ooo.foooooooooooo.wickedpaintings.WickedPaintings;
import ooo.foooooooooooo.wickedpaintings.client.ImageManager;
import ooo.foooooooooooo.wickedpaintings.network.ServerBoundPackets;

public class WickedGuiDescription extends ItemSyncedGuiDescription {
  private final ImageWidget imageWidget;
  private final int selectedSlot;
  private boolean invalidUrl = false;
  private Identifier imageId = new Identifier("wicked_image", "default");

  @SuppressWarnings("CommentedOutCode")
  public WickedGuiDescription(int syncId, PlayerInventory playerInventory, StackReference stackRef) {
    super(WickedPaintings.WICKED_SCREEN_HANDLER_TYPE, syncId, playerInventory, stackRef);

    selectedSlot = playerInventory.selectedSlot;
    var stack = stackRef.get();

    var root = new WGridPanel(9);

    var nbt = stack.getOrCreateNbt();

    var url = nbt.getString(NbtConstants.URL);
    var imageId = Identifier.tryParse(nbt.getString(NbtConstants.IMAGE_ID));

    setRootPanel(root);

    var guiWidth = 36;

    root.setInsets(Insets.ROOT_PANEL);

    var widthLabel = new WLabel(Text.translatable("gui.wicked_paintings.image_width"));
    widthLabel.setVerticalAlignment(VerticalAlignment.CENTER);
    root.add(widthLabel, 0, 4, 10, 2);

    var widthField = new WTextField(Text.translatable("gui.wicked_paintings.image_width_placeholder"));
    widthField.setTextPredicate((text) -> text.matches("[0-9]+") && text.length() <= 2);
    widthField.setText(String.valueOf(nbt.getInt(NbtConstants.WIDTH)));
    root.add(widthField, 9, 4, 4, 2);

    var heightLabel = new WLabel(Text.translatable("gui.wicked_paintings.image_height"));
    heightLabel.setVerticalAlignment(VerticalAlignment.CENTER);
    root.add(heightLabel, 0, 7, 10, 2);

    var heightField = new WTextField(Text.translatable("gui.wicked_paintings.image_height_placeholder"));
    heightField.setTextPredicate((text) -> text.matches("[0-9]+") && text.length() <= 2);
    heightField.setText(String.valueOf(nbt.getInt(NbtConstants.HEIGHT)));
    root.add(heightField, 9, 7, 4, 2);

    var imageWidget = new ImageWidget(imageId);
    imageWidget.setTexture(imageId);
    imageWidget.setSize(90, 90);
    this.imageWidget = imageWidget;
    root.add(imageWidget, guiWidth - 10, 1, 10, 10);

    var title = new WLabel(Text.literal("WICKED"), 0x0);
    title.setHorizontalAlignment(HorizontalAlignment.CENTER);
    root.add(title, 0, 0, guiWidth, 1);

    var urlField = new WTextField(Text.translatable("gui.wicked_paintings.url_placeholder"));
    urlField.setMaxLength(512);
    urlField.setText(url);
    root.add(urlField, 0, 12, guiWidth, 2);

    // var debugLabel = new WDynamicLabel(() -> this.imageId.toString());
    // root.add(debugLabel, 0, 10, 0, 1);

    var urlLabel = new WDynamicLabel(() -> invalidUrl ? I18n.translate("gui.wicked_paintings.url_label_invalid") : "");
    urlLabel.setColor(0xFF0F0F, 0xFF1F1F);
    root.add(urlLabel, 0, 14, 0, 1);

    var loadButton = new WButton(Text.translatable("gui.wicked_paintings.load_button"));
    loadButton.setOnClick(() -> onLoad(urlField.getText()));
    root.add(loadButton, guiWidth - 9, 16, 4, 2);

    var applyButton = new WButton(Text.translatable("gui.wicked_paintings.apply_button"));
    applyButton.setOnClick(() -> onApply(urlField.getText(), parseField(widthField.getText(), 1), parseField(heightField.getText(), 1)));

    root.add(applyButton, guiWidth - 4, 16, 4, 2);

    root.validate(this);
  }

  public void onLoad(String url) {
    var image = ImageManager.loadImage(url);

    imageId = image.getTextureId();
    setTexture(imageId);

    invalidUrl = image.getImage() == null;
  }

  public void onApply(String url, int width, int height) {
    if (url.isEmpty()) {
      invalidUrl = true;
      return;
    }

    onLoad(url);

    // int slot = this.hand == Hand.MAIN_HAND ? this.player.getInventory().selectedSlot : 40;

    ServerBoundPackets.sendWickedUpdate(selectedSlot, url, imageId, width, height);

    MinecraftClient.getInstance().setScreen(null);
  }

  public int parseField(String text, int defaultValue) {
    try {
      return Integer.parseInt(text);
    } catch (NumberFormatException e) {
      return defaultValue;
    }
  }

  public void setTexture(Identifier texture) {
    this.imageWidget.setTexture(texture);
  }
}
