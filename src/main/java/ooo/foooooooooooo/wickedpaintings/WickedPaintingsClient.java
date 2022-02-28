package ooo.foooooooooooo.wickedpaintings;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import ooo.foooooooooooo.wickedpaintings.entity.ModEntityTypes;
import ooo.foooooooooooo.wickedpaintings.network.ModNetworking;

@Environment(EnvType.CLIENT)
public class WickedPaintingsClient implements ClientModInitializer {

    @Override
    public void onInitializeClient() {
        ModEntityTypes.registerRenderers();
        ModNetworking.registerClientBoundPackets();
    }
}
