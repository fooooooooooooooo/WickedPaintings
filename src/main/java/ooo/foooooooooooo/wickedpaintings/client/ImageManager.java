package ooo.foooooooooooo.wickedpaintings.client;

import net.minecraft.util.Identifier;
import ooo.foooooooooooo.wickedpaintings.WickedPaintings;
import ooo.foooooooooooo.wickedpaintings.util.ImageUtils;
import org.jetbrains.annotations.NotNull;

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class ImageManager {
    public static final String IMAGES_NAMESPACE = "wicked_images";
    public static final Identifier DEFAULT_IMAGE_ID = new Identifier(IMAGES_NAMESPACE, "default");
    private static final ImageManager instance = new ImageManager();
    private final List<LoadedImage> loadedImages = new ArrayList<>();

    @NotNull
    public static LoadedImage loadImage(String url) {
        var identifier = generateIdentifier(url);

        return loadImage(identifier, url);
    }

    @NotNull
    public static LoadedImage loadImage(Identifier id, String url) {
        var validatedUrl = "";

        try {
            new URL(url);
            validatedUrl = url;
        } catch (Exception e) {
            validatedUrl = null;
        }

        if (validatedUrl == null) return LoadedImage.DEFAULT;

        for (LoadedImage loadedImage : instance.loadedImages) {
            if (loadedImage.getUrl().equals(url)) {
                return loadedImage;
            }
        }

        BufferedImage image;
        try {
            image = ImageUtils.downloadBufImage(url);
        } catch (IOException e) {
            WickedPaintings.LOGGERS.error("Failed to download image: " + url + ", Exception: " + e.getMessage());
            return LoadedImage.DEFAULT;
        }

        try {
            ImageUtils.saveBufferedImageAsIdentifier(image, id);
        } catch (IOException e) {
            WickedPaintings.LOGGERS.error("Failed to save image as texture: " + url + ", Exception: " + e.getMessage());
        }

        var loadedImage = new LoadedImage(id, url, image);

        instance.loadedImages.add(loadedImage);

        return loadedImage;
    }

    public static Identifier generateIdentifier(String url) {
        var hash = url.hashCode();
        var encoded = Integer.toHexString(hash);

        return new Identifier(IMAGES_NAMESPACE, encoded.toLowerCase(Locale.ROOT));
    }
}
