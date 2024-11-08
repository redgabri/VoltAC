package com.volt.voltac.manager.init.start;

import com.volt.voltac.VoltAPI;
import com.volt.voltac.commands.*;
import com.volt.voltac.manager.init.Initable;
import co.aikar.commands.PaperCommandManager;

public class CommandRegister implements Initable {
    @Override
    public void start() {
        // This does not make Volt require paper
        // It only enables new features such as asynchronous tab completion on paper
        PaperCommandManager commandManager = new PaperCommandManager(VoltAPI.INSTANCE.getPlugin());

        commandManager.registerCommand(new VoltPerf());
        commandManager.registerCommand(new VoltDebug());
        commandManager.registerCommand(new VoltAlerts());
        commandManager.registerCommand(new VoltProfile());
        commandManager.registerCommand(new VoltSendAlert());
        commandManager.registerCommand(new VoltHelp());
        commandManager.registerCommand(new VoltReload());
        commandManager.registerCommand(new VoltSpectate());
        commandManager.registerCommand(new VoltStopSpectating());
        commandManager.registerCommand(new VoltLog());
        commandManager.registerCommand(new VoltVerbose());
    }
}
