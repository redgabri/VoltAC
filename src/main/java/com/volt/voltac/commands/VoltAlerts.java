package com.volt.voltac.commands;

import com.volt.voltac.VoltAPI;
import co.aikar.commands.BaseCommand;
import co.aikar.commands.annotation.CommandAlias;
import co.aikar.commands.annotation.CommandPermission;
import co.aikar.commands.annotation.Subcommand;
import org.bukkit.entity.Player;

@CommandAlias("volt|voltac")
public class VoltAlerts extends BaseCommand {
    @Subcommand("alerts")
    @CommandPermission("volt.alerts")
    public void onAlerts(Player player) {
        VoltAPI.INSTANCE.getAlertManager().toggleAlerts(player);
    }
}
