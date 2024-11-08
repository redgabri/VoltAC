package com.volt.voltac.manager.tick.impl;

import com.volt.voltac.GrimAPI;
import com.volt.voltac.manager.tick.Tickable;
import com.volt.voltac.player.GrimPlayer;

public class ResetTick implements Tickable {
    @Override
    public void tick() {
        for (GrimPlayer player : GrimAPI.INSTANCE.getPlayerDataManager().getEntries()) {
            player.checkManager.getEntityReplication().tickStartTick();
        }
    }
}
