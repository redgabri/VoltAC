package com.volt.voltac.predictionengine.predictions.rideable;

import com.volt.voltac.player.VoltPlayer;
import com.volt.voltac.predictionengine.predictions.PredictionEngineWater;
import com.volt.voltac.utils.data.VectorData;
import org.bukkit.util.Vector;

import java.util.List;
import java.util.Set;

public class PredictionEngineRideableWater extends PredictionEngineWater {

    Vector movementVector;

    public PredictionEngineRideableWater(Vector movementVector) {
        this.movementVector = movementVector;
    }

    @Override
    public void addJumpsToPossibilities(VoltPlayer player, Set<VectorData> existingVelocities) {
        PredictionEngineRideableUtils.handleJumps(player, existingVelocities);
    }

    @Override
    public List<VectorData> applyInputsToVelocityPossibilities(VoltPlayer player, Set<VectorData> possibleVectors, float speed) {
        return PredictionEngineRideableUtils.applyInputsToVelocityPossibilities(movementVector, player, possibleVectors, speed);
    }
}
