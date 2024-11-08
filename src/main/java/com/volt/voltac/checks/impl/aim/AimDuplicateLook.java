package com.volt.voltac.checks.impl.aim;

import com.volt.voltac.checks.Check;
import com.volt.voltac.checks.CheckData;
import com.volt.voltac.checks.type.RotationCheck;
import com.volt.voltac.player.VoltPlayer;
import com.volt.voltac.utils.anticheat.update.RotationUpdate;

@CheckData(name = "AimDuplicateLook")
public class AimDuplicateLook extends Check implements RotationCheck {
    public AimDuplicateLook(VoltPlayer playerData) {
        super(playerData);
    }

    boolean exempt = false;

    @Override
    public void process(final RotationUpdate rotationUpdate) {
        if (player.packetStateData.lastPacketWasTeleport || player.packetStateData.lastPacketWasOnePointSeventeenDuplicate || player.compensatedEntities.getSelf().getRiding() != null) {
            exempt = true;
            return;
        }

        if (exempt) { // Exempt for a tick on teleport
            exempt = false;
            return;
        }

        if (rotationUpdate.getFrom().equals(rotationUpdate.getTo())) {
            flagAndAlert();
        }
    }
}
