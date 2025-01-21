package ooo.foooooooooooo.wickedpaintings.client.screen;

import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraft.client.gui.widget.TextWidget;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import ooo.foooooooooooo.wickedpaintings.Constants;
import ooo.foooooooooooo.wickedpaintings.WickedPaintings;
import ooo.foooooooooooo.wickedpaintings.network.ServerBoundPackets;
import ooo.foooooooooooo.wickedpaintings.util.ImageUtils;

import java.net.MalformedURLException;
import java.net.URL;

public class WickedPaintingScreen extends Screen {
  protected static final Identifier WICKED_TEXTURE = WickedPaintings.id("textures/gui/gui.png");

  private static final Text TITLE = Text.translatable("gui.wicked_paintings.title");
  private static final Text URL_PLACEHOLDER = Text.translatable("gui.wicked_paintings.url_placeholder");
  private static final Text URL_LABEL_INVALID = Text.translatable("gui.wicked_paintings.url_label_invalid");
  private static final Text IMAGE_WIDTH = Text.translatable("gui.wicked_paintings.image_width");
  private static final Text IMAGE_HEIGHT = Text.translatable("gui.wicked_paintings.image_height");
  private static final Text APPLY_BUTTON = Text.translatable("gui.wicked_paintings.apply_button");
  private static final Text LOAD_BUTTON = Text.translatable("gui.wicked_paintings.load_button");
  private static final Text CANCEL_BUTTON = Text.translatable("gui.wicked_paintings.cancel_button");

  private static final int GUI_WIDTH = 453;
  private static final int GUI_HEIGHT = 273;

  private String url;
  private int imageWidthBlocks;
  private int imageHeightBlocks;

  private final int selectedSlot;

  private boolean closed = false;
  private boolean invalidUrl = false;
  private Identifier imageId;

  private ImageWidget imageWidget;

  private TextFieldWidget widthField;
  private TextFieldWidget heightField;

  private TextFieldWidget urlField;

  private ButtonWidget applyButton;
  private ButtonWidget loadButton;

  private TextWidget invalidUrlText;

  public WickedPaintingScreen(ItemStack stack, PlayerInventory inventory) {
    super(TITLE);

    this.selectedSlot = inventory.selectedSlot;

    var nbt = stack.getOrCreateNbt();

    this.imageWidthBlocks = nbt.getInt(Constants.WIDTH);
    this.imageHeightBlocks = nbt.getInt(Constants.HEIGHT);
    this.url = nbt.getString(Constants.URL);
    this.imageId = Constants.DEFAULT_TEX;
  }

  private void loadImage() {
    if (this.url == null || this.url.isEmpty() || this.client == null) return;

    WickedPaintings.LOGGERS.info("Loading image: `{}`", url);

    this.imageId = Constants.DEFAULT_TEX;

    ImageUtils.getOrLoadImageAsync(url).thenAccept(id -> {
      WickedPaintings.LOGGERS.info("Loaded image: `{}` id {}", url, id);

      if (this.closed) return;

      this.imageId = id;
      this.imageWidget.setTexture(this.imageId);
    });
  }

  private int left() {
    return (this.width - GUI_WIDTH) / 2;
  }

  private int top() {
    return (this.height - GUI_HEIGHT) / 2;
  }

  private int bottom() {
    return top() + GUI_HEIGHT;
  }

  @Override
  protected void init() {
    this.loadImage();

    var PADDING = 3;
    var EDGE_PADDING = PADDING * 2;

    var BUTTON_HEIGHT = 20;
    var INPUT_HEIGHT = 20;

    var BUTTON_PADDING = 10;
    var INPUT_PADDING = 4;

    var left = left() + EDGE_PADDING;
    var bottom = bottom() - EDGE_PADDING;
    var top = top() + EDGE_PADDING;
    var middle = this.width / 2;

    var buttonsY = bottom - BUTTON_HEIGHT;

    var third = GUI_WIDTH / 3;
    var middleLeft = left + (third / 2);

    var titleLabelWidth = this.textRenderer.getWidth(TITLE);
    this.addDrawable(new TextWidget(middleLeft - (titleLabelWidth / 2),
      top,
      titleLabelWidth,
      this.textRenderer.fontHeight,
      TITLE,
      this.textRenderer
    ));

    var widthLabelWidth = this.textRenderer.getWidth(IMAGE_WIDTH);
    var heightLabelWidth = this.textRenderer.getWidth(IMAGE_HEIGHT);

    var largestLabelWidth = Math.max(widthLabelWidth, heightLabelWidth);

    var numberInputWidth = this.textRenderer.getWidth("00_") + INPUT_PADDING * 2;

    var widthHeightInputX = left + largestLabelWidth + PADDING * 2;
    var widthY = top + 20;

    this.addDrawable(new TextWidget(left, widthY, widthLabelWidth, INPUT_HEIGHT, IMAGE_WIDTH, this.textRenderer));
    this.widthField = this.addDrawableChild(new TextFieldWidget(this.textRenderer,
      widthHeightInputX,
      widthY,
      numberInputWidth,
      INPUT_HEIGHT,
      IMAGE_WIDTH
    ));
    this.widthField.setMaxLength(2);
    this.widthField.setChangedListener(this::onWidthFieldChanged);

    this.addSelectableChild(this.widthField);

    var heightY = widthY + INPUT_HEIGHT + PADDING;

    this.addDrawable(new TextWidget(left, heightY, heightLabelWidth, INPUT_HEIGHT, IMAGE_HEIGHT, this.textRenderer));
    this.heightField = this.addDrawableChild(new TextFieldWidget(this.textRenderer,
      widthHeightInputX,
      heightY,
      numberInputWidth,
      INPUT_HEIGHT,
      IMAGE_HEIGHT
    ));
    this.heightField.setMaxLength(2);
    this.heightField.setChangedListener(this::onHeightFieldChanged);

    var loadWidth = this.textRenderer.getWidth(LOAD_BUTTON) + BUTTON_PADDING * 2;

    var urlY = bottom - BUTTON_HEIGHT - PADDING - INPUT_HEIGHT;
    var urlWidth = GUI_WIDTH - EDGE_PADDING - PADDING - loadWidth - EDGE_PADDING;

    this.urlField = this.addDrawableChild(new TextFieldWidget(this.textRenderer,
      left,
      urlY,
      urlWidth,
      INPUT_HEIGHT,
      URL_PLACEHOLDER
    ));
    this.urlField.setMaxLength(512);
    this.urlField.setPlaceholder(URL_PLACEHOLDER);
    this.urlField.setChangedListener(this::onUrlFieldChanged);

    this.loadButton = this.addDrawableChild(ButtonWidget
      .builder(LOAD_BUTTON, (press) -> onLoad())
      .dimensions(left + urlWidth + PADDING, urlY, loadWidth, BUTTON_HEIGHT)
      .build());

    var cancelWidth = this.textRenderer.getWidth(CANCEL_BUTTON) + BUTTON_PADDING * 2;

    this.addDrawableChild(ButtonWidget.builder(CANCEL_BUTTON, (press) -> {
      if (this.client != null) {
        this.client.setScreen(null);
      }
    }).dimensions(middle - PADDING - cancelWidth, buttonsY, cancelWidth, BUTTON_HEIGHT).build());

    var applyWidth = this.textRenderer.getWidth(APPLY_BUTTON) + BUTTON_PADDING * 2;

    this.applyButton = this.addDrawableChild(ButtonWidget
      .builder(APPLY_BUTTON, (press) -> onApply())
      .dimensions(middle + PADDING, buttonsY, applyWidth, BUTTON_HEIGHT)
      .build());

    var imageWidth = GUI_WIDTH - EDGE_PADDING - third - EDGE_PADDING;
    var imageHeight = GUI_HEIGHT - EDGE_PADDING - BUTTON_HEIGHT - PADDING - INPUT_HEIGHT - PADDING - EDGE_PADDING;

    this.imageWidget = this.addDrawable(new ImageWidget(left + third, top, imageWidth, imageHeight, this.imageId));


    var invalidUrlTextWidth = this.textRenderer.getWidth(URL_LABEL_INVALID);
    // don't addDrawable, render manually in render() when this.invalidUrl
    this.invalidUrlText = new TextWidget(left,
      urlY - PADDING - this.textRenderer.fontHeight,
      invalidUrlTextWidth,
      this.textRenderer.fontHeight,
      URL_LABEL_INVALID,
      this.textRenderer
    );
    this.invalidUrlText.setTextColor(0xFF6666);

    this.widthField.setText(String.valueOf(this.imageWidthBlocks));
    this.heightField.setText(String.valueOf(this.imageHeightBlocks));
    this.urlField.setText(this.url);
  }

  @Override
  public void tick() {
    super.tick();

    this.widthField.tick();
    this.heightField.tick();

    this.urlField.tick();

    this.applyButton.active = !this.invalidUrl;
    this.loadButton.active = !this.invalidUrl;
  }

  @Override
  public void renderBackground(DrawContext context) {
    super.renderBackground(context);

    context.drawNineSlicedTexture(WICKED_TEXTURE, left(), top(), GUI_WIDTH, GUI_HEIGHT, 8, 236, 34, 1, 1);
  }

  @Override
  public void render(DrawContext context, int mouseX, int mouseY, float delta) {
    this.renderBackground(context);

    if (this.invalidUrl) this.invalidUrlText.render(context, mouseX, mouseY, delta);

    super.render(context, mouseX, mouseY, delta);
  }

  public void onLoad() {
    if (this.invalidUrl) return;

    this.url = this.urlField.getText();
    this.imageWidthBlocks = parseField(this.widthField.getText(), Constants.MIN_WIDTH);
    this.imageHeightBlocks = parseField(this.heightField.getText(), Constants.MIN_HEIGHT);

    this.loadImage();
  }


  public void onApply() {
    if (this.client == null || this.invalidUrl) return;

    onLoad();

    ServerBoundPackets.sendWickedUpdate(this.selectedSlot, this.url, this.imageWidthBlocks, this.imageHeightBlocks);

    this.client.setScreen(null);
    this.closed = true;
  }

  private void onWidthFieldChanged(String text) {
    correctNumericField(text, this.widthField, Constants.MIN_WIDTH, Constants.MAX_WIDTH);
  }

  private void onHeightFieldChanged(String text) {
    correctNumericField(text, this.heightField, Constants.MIN_HEIGHT, Constants.MAX_HEIGHT);
  }

  private void correctNumericField(String newText, TextFieldWidget field, int minValue, int maxValue) {
    var text = newText.trim();
    if (text.isEmpty()) {
      if (!newText.equals(text)) {
        field.setText(text);
      }
      return;
    }

    if (!Character.isDigit(text.charAt(text.length() - 1))) {
      text = text.substring(0, text.length() - 1);
    }

    var value = Integer.parseInt(text);

    if (value < minValue) {
      value = minValue;
    } else if (value > maxValue) {
      value = maxValue;
    }

    text = String.valueOf(value);
    if (!newText.equals(text)) {
      field.setText(text);
    }
  }

  private void onUrlFieldChanged(String text) {
    try {
      new URL(text);
      this.invalidUrl = false;
    } catch (MalformedURLException e) {
      this.invalidUrl = true;
    }
  }

  public int parseField(String text, int defaultValue) {
    if (text.trim().isEmpty()) {
      return defaultValue;
    }

    try {
      return Integer.parseInt(text);
    } catch (NumberFormatException e) {
      return defaultValue;
    }
  }
}
