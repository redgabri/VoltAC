package com.volt.voltac;

import ac.grim.grimac.manager.*;
import ac.grim.volt.manager.*;
import com.volt.voltac.manager.*;
import com.volt.voltac.manager.config.BaseConfigManager;
import com.volt.voltac.utils.anticheat.PlayerDataManager;
import lombok.Getter;
import org.bukkit.plugin.java.JavaPlugin;

@Getter
public enum VoltAPI {
    INSTANCE;

    private final BaseConfigManager configManager = new BaseConfigManager();
    private final AlertManagerImpl alertManager = new AlertManagerImpl();
    private final SpectateManager spectateManager = new SpectateManager();
    private final DiscordManager discordManager = new DiscordManager();
    private final PlayerDataManager playerDataManager = new PlayerDataManager();
    private final TickManager tickManager = new TickManager();
    private final VoltExternalAPI externalAPI = new VoltExternalAPI(this);
    private InitManager initManager;
    private JavaPlugin plugin;

    public void load(final JavaPlugin plugin) {
        this.plugin = plugin;
        initManager = new InitManager();
        initManager.load();
    }

    public void start(final JavaPlugin plugin) {
        this.plugin = plugin;
        initManager.start();
    }

    public void stop(final JavaPlugin plugin) {
        this.plugin = plugin;
        initManager.stop();
    }
}
