package com.volt.voltac.manager.init.start;

import com.volt.voltac.VoltAPI;
import com.volt.voltac.manager.init.Initable;
import com.volt.voltac.utils.anticheat.LogUtil;
import io.github.retrooper.packetevents.util.folia.FoliaScheduler;
import org.bukkit.Bukkit;

public class TickRunner implements Initable {
    @Override
    public void start() {
        LogUtil.info("Registering tick schedulers...");

        if (FoliaScheduler.isFolia()) {
            FoliaScheduler.getAsyncScheduler().runAtFixedRate(VoltAPI.INSTANCE.getPlugin(), (dummy) -> {
                VoltAPI.INSTANCE.getTickManager().tickSync();
                VoltAPI.INSTANCE.getTickManager().tickAsync();
            }, 1, 1);
        } else {
            Bukkit.getScheduler().runTaskTimer(VoltAPI.INSTANCE.getPlugin(), () -> VoltAPI.INSTANCE.getTickManager().tickSync(), 0, 1);
            Bukkit.getScheduler().runTaskTimerAsynchronously(VoltAPI.INSTANCE.getPlugin(), () -> VoltAPI.INSTANCE.getTickManager().tickAsync(), 0, 1);
        }
    }
}
