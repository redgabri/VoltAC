package com.volt.voltac.checks.impl.movement;

import com.volt.voltac.checks.Check;
import com.volt.voltac.checks.type.PositionCheck;
import com.volt.voltac.player.VoltPlayer;
import com.volt.voltac.utils.anticheat.update.PositionUpdate;

public class PredictionRunner extends Check implements PositionCheck {
    public PredictionRunner(VoltPlayer playerData) {
        super(playerData);
    }

    @Override
    public void onPositionUpdate(final PositionUpdate positionUpdate) {
        if (!player.compensatedEntities.getSelf().inVehicle()) {
            player.movementCheckRunner.processAndCheckMovementPacket(positionUpdate);
        }
    }
}
