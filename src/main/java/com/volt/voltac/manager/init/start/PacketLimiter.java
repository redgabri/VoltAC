package com.volt.voltac.manager.init.start;

import com.volt.voltac.GrimAPI;
import com.volt.voltac.manager.init.Initable;
import com.volt.voltac.player.GrimPlayer;
import io.github.retrooper.packetevents.util.folia.FoliaScheduler;

public class PacketLimiter implements Initable {
    @Override
    public void start() {
        FoliaScheduler.getAsyncScheduler().runAtFixedRate(GrimAPI.INSTANCE.getPlugin(), (dummy) -> {
            for (GrimPlayer player : GrimAPI.INSTANCE.getPlayerDataManager().getEntries()) {
                // Avoid concurrent reading on an integer as it's results are unknown
                player.cancelledPackets.set(0);
            }
        }, 1, 20);
    }
}
