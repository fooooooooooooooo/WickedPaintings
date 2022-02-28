package ooo.foooooooooooo.wickedpaintings;

import ooo.foooooooooooo.wickedpaintings.util.ImageData;
import ooo.foooooooooooo.wickedpaintings.util.ImageUtils;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.io.IOException;

import static org.junit.jupiter.api.Assertions.fail;

public class ImageUtilsTests {

    @Test
    public void downloadImage_Returns_Data() {
        ImageData data;

        try {
            data = ImageUtils.downloadImage("https://cdn.discordapp.com/attachments/902081288645804042/945856899956478012/unknown.png");
        } catch (IOException e) {
            fail("Failed to get pixels: " + e);
            return;
        }

        var pixels = data.pixels();

        Assertions.assertNotNull(pixels);

        var emptyPixelArray = new byte[pixels.length];
        Assertions.assertNotEquals(pixels, emptyPixelArray);
    }
}
