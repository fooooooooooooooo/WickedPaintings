package ooo.foooooooooooo.wickedpaintings.client;

import net.minecraft.util.Identifier;
import ooo.foooooooooooo.wickedpaintings.WickedPaintings;
import ooo.foooooooooooo.wickedpaintings.util.ImageUtils;
import org.apache.commons.codec.binary.Base32;
import org.jetbrains.annotations.Nullable;

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

public class ImageManager {
    private final List<LoadedImage> loadedImages = new ArrayList<>();

    private static final ImageManager instance = new ImageManager();

    @Nullable
    public static LoadedImage loadImage(Identifier id, String url) {
        var validatedUrl = "";

        try {
            new URL(url);
            validatedUrl = url;
        } catch (Exception e) {
            validatedUrl = null;
        }

        if (validatedUrl == null) return null;

        if (id == null) {
            id = generateIdentifier(url);
        }

        for (LoadedImage loadedImage : instance.loadedImages) {
            if (loadedImage.getUrl().equals(url)) {
                return loadedImage;
            }
        }

        BufferedImage image;
        try {
            image = ImageUtils.downloadBufImage(url);
        } catch (IOException e) {
            WickedPaintings.LOGGER.error("Failed to load image: " + url + "\n  Exception: " + e.getMessage());
            return null;
        }

        try {
            ImageUtils.saveBufferedImageAsIdentifier(image, id);
        } catch (IOException e) {
            WickedPaintings.LOGGER.error("Failed to save image as texture: " + url + "\n  Exception: " + e.getMessage());
        }

        var loadedImage = new LoadedImage(id, url, image);

        instance.loadedImages.add(loadedImage);

        return loadedImage;

    }

    public static Identifier generateIdentifier(String url) {
        var bytes = new Base32().encode(url.getBytes(StandardCharsets.UTF_8));

        var encoded = new String(bytes, StandardCharsets.UTF_8)
                .replaceAll("=", "").toLowerCase();

        return new Identifier("wicked_images", encoded);
    }
}
