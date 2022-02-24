package ooo.foooooooooooo.wickedpaintings.client;

import net.minecraft.util.Identifier;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.awt.image.BufferedImage;
import java.util.Objects;

public class LoadedImage {
    public static final LoadedImage DEFAULT = new LoadedImage(ImageManager.DEFAULT_IMAGE_ID, "", null);
    private final String url;
    private final int width;
    private final int height;
    private final BufferedImage image;
    private final Identifier imageId;

    public LoadedImage(
            Identifier imageId,
            String url,
            @Nullable BufferedImage image
    ) {
        this.imageId = imageId;
        this.url = url;

        if (image == null) {
            this.width = 0;
            this.height = 0;
        } else {
            this.width = image.getWidth();
            this.height = image.getHeight();
        }

        this.image = image;
    }

    @NotNull
    public Identifier getTextureId() {
        if (this.imageId == null) {
            return ImageManager.DEFAULT_IMAGE_ID;
        }

        return this.imageId;
    }

    public String getUrl() {
        return this.url;
    }

    public int getWidth() {
        return this.width;
    }

    public int getHeight() {
        return this.height;
    }

    @Nullable
    public BufferedImage getImage() {
        return this.image;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == this) return true;
        if (obj == null || obj.getClass() != this.getClass()) return false;
        var that = (LoadedImage) obj;
        return Objects.equals(this.imageId, that.imageId) &&
                Objects.equals(this.url, that.url) &&
                this.width == that.width &&
                this.height == that.height &&
                this.image == that.image;
    }

    @Override
    public int hashCode() {
        return Objects.hash(imageId, url, width, height, image);
    }

    @Override
    public String toString() {
        return "LoadedImage[" +
                "identifier=" + imageId + ", " +
                "url=" + url + ", " +
                "width=" + width + ", " +
                "height=" + height + ", " +
                "image=" + image + "]";
    }
}
