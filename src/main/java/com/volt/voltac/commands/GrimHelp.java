package com.volt.voltac.commands;

import com.volt.voltac.VoltAPI;
import com.volt.voltac.utils.anticheat.MessageUtil;
import co.aikar.commands.BaseCommand;
import co.aikar.commands.annotation.CommandAlias;
import co.aikar.commands.annotation.CommandPermission;
import co.aikar.commands.annotation.Default;
import co.aikar.commands.annotation.Subcommand;
import org.bukkit.command.CommandSender;

@CommandAlias("grim|grimac")
public class GrimHelp extends BaseCommand {
    @Default
    @Subcommand("help")
    @CommandPermission("grim.help")
    public void onHelp(CommandSender sender) {
        for (String string : VoltAPI.INSTANCE.getConfigManager().getConfig().getStringList("help")) {
            string = MessageUtil.format(string);
            sender.sendMessage(string);
        }
    }
}
