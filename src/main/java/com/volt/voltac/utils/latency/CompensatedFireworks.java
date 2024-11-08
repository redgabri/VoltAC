package com.volt.voltac.utils.latency;

import com.volt.voltac.checks.Check;
import com.volt.voltac.checks.type.PostPredictionCheck;
import com.volt.voltac.player.VoltPlayer;
import com.volt.voltac.utils.anticheat.update.PredictionComplete;

import java.util.HashSet;
import java.util.Set;

public class CompensatedFireworks extends Check implements PostPredictionCheck {

    // As this is sync to one player, this does not have to be concurrent
    private final Set<Integer> activeFireworks = new HashSet<>();
    private final Set<Integer> fireworksToRemoveNextTick = new HashSet<>();

    public CompensatedFireworks(VoltPlayer player) {
        super(player);
    }

    @Override
    public void onPredictionComplete(final PredictionComplete predictionComplete) {
        // Remove all the fireworks that were removed in the last tick
        // Remember to remove with an int not an Integer
        activeFireworks.removeAll(fireworksToRemoveNextTick);
        fireworksToRemoveNextTick.clear();
    }

    public boolean hasFirework(int entityId) {
        return activeFireworks.contains(entityId);
    }

    public void addNewFirework(int entityID) {
        activeFireworks.add(entityID);
    }

    public void removeFirework(int entityID) {
        fireworksToRemoveNextTick.add(entityID);
    }

    public int getMaxFireworksAppliedPossible() {
        return activeFireworks.size();
    }
}
