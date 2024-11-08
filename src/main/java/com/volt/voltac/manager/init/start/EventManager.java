package com.volt.voltac.manager.init.start;

import com.volt.voltac.VoltAPI;
import com.volt.voltac.events.bukkit.PistonEvent;
import com.volt.voltac.manager.init.Initable;
import com.volt.voltac.utils.anticheat.LogUtil;
import org.bukkit.Bukkit;

public class EventManager implements Initable {
    public void start() {
        LogUtil.info("Registering singular bukkit event... (PistonEvent)");

        Bukkit.getPluginManager().registerEvents(new PistonEvent(), VoltAPI.INSTANCE.getPlugin());
    }
}
