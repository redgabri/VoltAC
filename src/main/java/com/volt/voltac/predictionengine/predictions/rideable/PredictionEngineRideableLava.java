package com.volt.voltac.predictionengine.predictions.rideable;

import com.volt.voltac.player.VoltPlayer;
import com.volt.voltac.predictionengine.predictions.PredictionEngineLava;
import com.volt.voltac.utils.data.VectorData;
import org.bukkit.util.Vector;

import java.util.List;
import java.util.Set;

public class PredictionEngineRideableLava extends PredictionEngineLava {

    Vector movementVector;

    public PredictionEngineRideableLava(Vector movementVector) {
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
