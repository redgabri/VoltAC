package com.volt.voltac.utils.inventory.slot;

import com.volt.voltac.player.VoltPlayer;
import com.volt.voltac.utils.inventory.InventoryStorage;
import com.github.retrooper.packetevents.protocol.item.ItemStack;

public class ResultSlot extends Slot {

    public ResultSlot(InventoryStorage container, int slot) {
        super(container, slot);
    }

    @Override
    public boolean mayPlace(ItemStack p_40178_) {
        return false;
    }

    @Override
    public void onTake(VoltPlayer player, ItemStack p_150639_) {
        // Resync the player's inventory
    }
}