package ooo.foooooooooooo.wickedpaintings.client.screen;

import io.github.cottonmc.cotton.gui.client.ScreenDrawing;
import io.github.cottonmc.cotton.gui.widget.WWidget;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.Identifier;

public class ImageWidget extends WWidget {
    private Identifier texture;

    public ImageWidget(Identifier texture) {
        this.texture = texture;
    }

    @Override
    public void paint(MatrixStack matrices, int x, int y, int mouseX, int mouseY) {
        ScreenDrawing.texturedRect(matrices, x, y, width, height, texture, 0xFFFFFFFF);
    }

    public void setTexture(Identifier texture) {
        this.texture = texture;
    }
}
