package com.volt.voltac.manager.tick.impl;

import com.volt.voltac.VoltAPI;
import com.volt.voltac.manager.tick.Tickable;
import com.volt.voltac.player.VoltPlayer;

public class ClientVersionSetter implements Tickable {
    @Override
    public void tick() {
        for (VoltPlayer player : VoltAPI.INSTANCE.getPlayerDataManager().getEntries()) {
            player.pollData();
        }
    }
}
