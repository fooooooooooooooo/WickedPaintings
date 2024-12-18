package ooo.foooooooooooo.wickedpaintings.client.screen;

import io.github.cottonmc.cotton.gui.ItemSyncedGuiDescription;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.StackReference;
import net.minecraft.item.ItemStack;
import net.minecraft.network.PacketByteBuf;
import ooo.foooooooooooo.wickedpaintings.WickedPaintings;

public class WickedGuiDescription extends ItemSyncedGuiDescription {
    public WickedGuiDescription(int syncId, PlayerInventory inventory, PacketByteBuf buffer) {
        this(syncId, inventory, StackReference.of(inventory.player, buffer.readEnumConstant(EquipmentSlot.class)));
    }

    public WickedGuiDescription(int syncId, PlayerInventory playerInventory, StackReference stackRef) {
        super(WickedPaintings.WICKED_SCREEN_HANDLER_TYPE, syncId, playerInventory, stackRef);
    }

    public ItemStack getStack() {
        return ownerStack;
    }
}
