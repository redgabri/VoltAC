package com.volt.voltac.checks.impl.movement;

import com.volt.voltac.checks.Check;
import com.volt.voltac.checks.CheckData;
import com.volt.voltac.checks.type.PostPredictionCheck;
import com.volt.voltac.player.VoltPlayer;

@CheckData(name = "Entity control", configName = "EntityControl")
public class EntityControl extends Check implements PostPredictionCheck {
    public EntityControl(VoltPlayer player) {
        super(player);
    }

    public void rewardPlayer() {
        reward();
    }
}
