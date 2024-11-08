package com.volt.voltac.predictionengine.movementtick;

import com.volt.voltac.player.VoltPlayer;
import com.volt.voltac.predictionengine.predictions.PredictionEngineLava;
import com.volt.voltac.predictionengine.predictions.PredictionEngineNormal;
import com.volt.voltac.predictionengine.predictions.PredictionEngineWater;
import com.volt.voltac.predictionengine.predictions.PredictionEngineWaterLegacy;
import com.volt.voltac.utils.nmsutil.BlockProperties;
import com.github.retrooper.packetevents.protocol.player.ClientVersion;

public class MovementTickerPlayer extends MovementTicker {
    public MovementTickerPlayer(VoltPlayer player) {
        super(player);
    }

    @Override
    public void doWaterMove(float swimSpeed, boolean isFalling, float swimFriction) {
        if (player.getClientVersion().isNewerThanOrEquals(ClientVersion.V_1_13)) {
            new PredictionEngineWater().guessBestMovement(swimSpeed, player, isFalling, player.gravity, swimFriction, player.lastY);
        } else {
            new PredictionEngineWaterLegacy().guessBestMovement(swimSpeed, player, player.gravity, swimFriction, player.lastY);
        }
    }

    @Override
    public void doLavaMove() {
        new PredictionEngineLava().guessBestMovement(0.02F, player);
    }

    @Override
    public void doNormalMove(float blockFriction) {
        new PredictionEngineNormal().guessBestMovement(BlockProperties.getFrictionInfluencedSpeed(blockFriction, player), player);
    }
}
