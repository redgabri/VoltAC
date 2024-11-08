package com.volt.voltac.checks.impl.scaffolding;

import com.volt.voltac.checks.CheckData;
import com.volt.voltac.checks.type.BlockPlaceCheck;
import com.volt.voltac.player.GrimPlayer;
import com.volt.voltac.utils.anticheat.update.BlockPlace;
import com.github.retrooper.packetevents.util.Vector3f;

@CheckData(name = "InvalidPlaceA")
public class InvalidPlaceA extends BlockPlaceCheck {
    public InvalidPlaceA(GrimPlayer player) {
        super(player);
    }

    @Override
    public void onBlockPlace(final BlockPlace place) {
        Vector3f cursor = place.getCursor();
        if (cursor == null) return;
        if (!Float.isFinite(cursor.getX()) || !Float.isFinite(cursor.getY()) || !Float.isFinite(cursor.getZ())) {
            if (flagAndAlert() && shouldModifyPackets() && shouldCancel()) {
                place.resync();
            }
        }
    }
}
