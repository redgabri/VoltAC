package com.volt.voltac.manager.init.start;

import com.volt.voltac.VoltAPI;
import com.volt.voltac.manager.init.Initable;
import com.volt.voltac.player.VoltPlayer;
import io.github.retrooper.packetevents.util.folia.FoliaScheduler;

public class PacketLimiter implements Initable {
    @Override
    public void start() {
        FoliaScheduler.getAsyncScheduler().runAtFixedRate(VoltAPI.INSTANCE.getPlugin(), (dummy) -> {
            for (VoltPlayer player : VoltAPI.INSTANCE.getPlayerDataManager().getEntries()) {
                // Avoid concurrent reading on an integer as it's results are unknown
                player.cancelledPackets.set(0);
            }
        }, 1, 20);
    }
}
