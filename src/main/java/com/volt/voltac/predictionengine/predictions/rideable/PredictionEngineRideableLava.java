package com.volt.voltac.predictionengine.predictions.rideable;

import com.volt.voltac.player.GrimPlayer;
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
    public void addJumpsToPossibilities(GrimPlayer player, Set<VectorData> existingVelocities) {
        PredictionEngineRideableUtils.handleJumps(player, existingVelocities);
    }

    @Override
    public List<VectorData> applyInputsToVelocityPossibilities(GrimPlayer player, Set<VectorData> possibleVectors, float speed) {
        return PredictionEngineRideableUtils.applyInputsToVelocityPossibilities(movementVector, player, possibleVectors, speed);
    }
}
