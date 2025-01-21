package ooo.foooooooooooo.wickedpaintings.config;

public interface IModConfig {
  boolean enabled();

  int maxSizeMb();

  // todo: implement this again
  boolean debug();

  default long maxSizeBytes() {
    return this.maxSizeMb() * 1024L * 1024L;
  }

  default boolean hasMaxSize() {
    return this.maxSizeMb() > 0;
  }
}
