package com.volt.voltac.commands;

import com.volt.voltac.VoltAPI;
import com.volt.voltac.utils.anticheat.LogUtil;
import com.volt.voltac.utils.anticheat.MessageUtil;
import co.aikar.commands.BaseCommand;
import co.aikar.commands.annotation.CommandAlias;
import co.aikar.commands.annotation.CommandPermission;
import co.aikar.commands.annotation.Subcommand;
import org.bukkit.entity.Player;

@CommandAlias("volt|voltac")
public class VoltSendAlert extends BaseCommand {
    @Subcommand("sendalert")
    @CommandPermission("volt.sendalert")
    public void sendAlert(String string) {
        string = MessageUtil.format(string);

        for (Player bukkitPlayer : VoltAPI.INSTANCE.getAlertManager().getEnabledAlerts()) {
            bukkitPlayer.sendMessage(string);
        }

        if (VoltAPI.INSTANCE.getConfigManager().isPrintAlertsToConsole()) {
            LogUtil.console(string); // Print alert to console
        }
    }
}
