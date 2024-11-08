package com.volt.voltac.checks.impl.scaffolding;

import com.volt.voltac.checks.CheckData;
import com.volt.voltac.checks.type.BlockPlaceCheck;
import com.volt.voltac.player.VoltPlayer;
import com.volt.voltac.utils.anticheat.update.BlockPlace;
import com.volt.voltac.utils.nmsutil.Materials;
import com.github.retrooper.packetevents.protocol.world.states.type.StateTypes;
import com.github.retrooper.packetevents.util.Vector3f;

@CheckData(name = "FabricatedPlace")
public class FabricatedPlace extends BlockPlaceCheck {
    public FabricatedPlace(VoltPlayer player) {
        super(player);
    }

    @Override
    public void onBlockPlace(final BlockPlace place) {
        Vector3f cursor = place.getCursor();
        if (cursor == null) return;

        double allowed = Materials.isShapeExceedsCube(place.getPlacedAgainstMaterial()) || place.getPlacedAgainstMaterial() == StateTypes.LECTERN ? 1.5 : 1;
        double minAllowed = 1 - allowed;

        if (cursor.getX() < minAllowed || cursor.getY() < minAllowed || cursor.getZ() < minAllowed || cursor.getX() > allowed || cursor.getY() > allowed || cursor.getZ() > allowed) {
            if (flagAndAlert() && shouldModifyPackets() && shouldCancel()) {
                place.resync();
            }
        }
    }
}
