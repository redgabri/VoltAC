package com.volt.voltac.manager.init.start;

import com.volt.voltac.GrimAPI;
import com.volt.voltac.manager.init.Initable;

public class BStats implements Initable {
    @Override
    public void start() {
        int pluginId = 12820; // <-- Replace with the id of your plugin!
        try {
            new io.github.retrooper.packetevents.bstats.Metrics(GrimAPI.INSTANCE.getPlugin(), pluginId);
        } catch (Exception ignored) {
        }
    }
}
