package com.volt.voltac;

import org.bukkit.plugin.java.JavaPlugin;

public final class VoltAC extends JavaPlugin {

    @Override
    public void onLoad() {
        VoltAPI.INSTANCE.load(this);
    }

    @Override
    public void onDisable() {
        VoltAPI.INSTANCE.stop(this);
    }

    @Override
    public void onEnable() {
        VoltAPI.INSTANCE.start(this);
    }
}
