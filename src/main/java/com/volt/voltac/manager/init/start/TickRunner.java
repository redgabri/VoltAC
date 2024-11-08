package com.volt.voltac.manager.init.start;

import com.volt.voltac.GrimAPI;
import com.volt.voltac.manager.init.Initable;
import com.volt.voltac.utils.anticheat.LogUtil;
import io.github.retrooper.packetevents.util.folia.FoliaScheduler;
import org.bukkit.Bukkit;

public class TickRunner implements Initable {
    @Override
    public void start() {
        LogUtil.info("Registering tick schedulers...");

        if (FoliaScheduler.isFolia()) {
            FoliaScheduler.getAsyncScheduler().runAtFixedRate(GrimAPI.INSTANCE.getPlugin(), (dummy) -> {
                GrimAPI.INSTANCE.getTickManager().tickSync();
                GrimAPI.INSTANCE.getTickManager().tickAsync();
            }, 1, 1);
        } else {
            Bukkit.getScheduler().runTaskTimer(GrimAPI.INSTANCE.getPlugin(), () -> GrimAPI.INSTANCE.getTickManager().tickSync(), 0, 1);
            Bukkit.getScheduler().runTaskTimerAsynchronously(GrimAPI.INSTANCE.getPlugin(), () -> GrimAPI.INSTANCE.getTickManager().tickAsync(), 0, 1);
        }
    }
}
