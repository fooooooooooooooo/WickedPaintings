package ooo.foooooooooooo.wickedpaintings.image;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtElement;
import net.minecraft.nbt.NbtOps;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.Identifier;
import net.minecraft.world.PersistentState;
import net.minecraft.world.World;
import ooo.foooooooooooo.wickedpaintings.network.ClientBoundPackets;
import ooo.foooooooooooo.wickedpaintings.network.ModNetworking;
import org.apache.commons.codec.binary.Base32;

import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

public class ImageLoaderManager extends PersistentState {
  private static final String NBT_KEY = "image_state";

  public static Codec<List<LoadedImage>> CODEC = Codec.list(LoadedImage.CODEC);

  private final List<LoadedImage> loadedImages = new ArrayList<>();

  private static ImageLoaderManager fromTag(NbtCompound nbtCompound) {
    ImageLoaderManager imageStateManager = new ImageLoaderManager();
    imageStateManager.loadedImages.clear();

    List<LoadedImage> images = CODEC
        .parse(NbtOps.INSTANCE, nbtCompound.getList("loaded_images", NbtElement.COMPOUND_TYPE))
        .result()
        .orElse(Collections.emptyList());

    imageStateManager.loadedImages.addAll(images);
    return imageStateManager;
  }

  public ImageLoaderManager get(World world) {
    ServerWorld serverWorld = (ServerWorld) world;
    return serverWorld
        .getPersistentStateManager()
        .getOrCreate(ImageLoaderManager::fromTag, ImageLoaderManager::new, NBT_KEY);
  }

  public LoadedImage addImage(String url, int width, int height, byte[] imageData) {
    for (LoadedImage loadedImage : loadedImages) {
      if (loadedImage.getUrl().equals(url)) {
        return loadedImage;
      }
    }

    var identifier = LoadedImage.generateIdentifier(url);

    var image = new LoadedImage(identifier, url, width, height, imageData);

    loadedImages.add(image);
    return image;
  }

  public LoadedImage getImage(Identifier identifier) {
    return loadedImages
        .stream()
        .filter(loadedImage -> loadedImage.getIdentifier().equals(identifier))
        .findFirst()
        .orElse(null);
  }

  public LoadedImage getImage(String url) {
    return loadedImages
        .stream()
        .filter(loadedImage -> loadedImage.getUrl().equals(url))
        .findFirst()
        .orElse(null);
  }

  @Override
  public NbtCompound writeNbt(NbtCompound nbt) {
    CODEC
        .encodeStart(NbtOps.INSTANCE, loadedImages)
        .result()
        .ifPresent(tag -> nbt.put("loaded_chunks", tag));

    return nbt;
  }

  public void syncImageToClient(ServerPlayerEntity serverPlayerEntity, Identifier identifier) {
    syncToClient(serverPlayerEntity, loadedImages
        .stream()
        .filter(loadedImage -> loadedImage.getIdentifier().equals(identifier))
        .collect(Collectors.toList())
    );
  }

  public void syncAllToClient(ServerPlayerEntity serverPlayerEntity) {
    syncToClient(serverPlayerEntity, loadedImages);
  }

  public void clearClient(ServerPlayerEntity serverPlayerEntity) {
    syncToClient(serverPlayerEntity, Collections.emptyList());
  }

  public void syncToClient(ServerPlayerEntity serverPlayerEntity, List<LoadedImage> images) {
    ModNetworking.sendToPlayer(ClientBoundPackets.createPacketSyncLoadedImages(images), serverPlayerEntity);
  }


  public static class LoadedImage {
    public static Codec<LoadedImage> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            Identifier.CODEC.fieldOf("identifier").forGetter(LoadedImage::getIdentifier),
            Codec.STRING.fieldOf("url").forGetter(LoadedImage::getUrl),
            Codec.INT.fieldOf("width").forGetter(LoadedImage::getWidth),
            Codec.INT.fieldOf("height").forGetter(LoadedImage::getHeight),
            Codec.BYTE_BUFFER.fieldOf("data").forGetter(LoadedImage::getByteBuffer)
        )
        .apply(instance, LoadedImage::new));

    private final Identifier identifier;
    private final String url;
    private final int width;
    private final int height;
    private final byte[] data;

    public LoadedImage(Identifier identifier, String url, int width, int height, byte[] data) {
      this.identifier = identifier;
      this.url = url;
      this.width = width;
      this.height = height;
      this.data = data;
    }

    public LoadedImage(Identifier identifier, String url, int width, int height, ByteBuffer data) {
      this.identifier = identifier;
      this.url = url;
      this.width = width;
      this.height = height;
      this.data = data.array();
    }

    public static Identifier generateIdentifier(String url) {
      var bytes = new Base32().encode(url.getBytes(StandardCharsets.UTF_8));

      var encoded = new String(bytes, StandardCharsets.UTF_8)
          .replaceAll("=", "").toLowerCase();

      return new Identifier("wicked_images", encoded);
    }

    public Identifier getIdentifier() {
      return this.identifier;
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

    public byte[] getData() {
      return this.data;
    }

    public ByteBuffer getByteBuffer() {
      return ByteBuffer.wrap(data);
    }
  }
}
