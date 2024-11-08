package com.volt.voltac.commands;

import com.volt.voltac.VoltAPI;
import com.volt.voltac.utils.anticheat.MessageUtil;
import co.aikar.commands.BaseCommand;
import co.aikar.commands.annotation.CommandAlias;
import co.aikar.commands.annotation.CommandPermission;
import co.aikar.commands.annotation.Subcommand;
import org.bukkit.command.CommandSender;

@CommandAlias("volt|voltac")
public class VoltReload extends BaseCommand {
    @Subcommand("reload")
    @CommandPermission("volt.reload")
    public void onReload(CommandSender sender) {
        //reload config
        sender.sendMessage(MessageUtil.format("%prefix% &7Reloading config..."));
        VoltAPI.INSTANCE.getExternalAPI().reloadAsync().exceptionally(throwable -> false)
                .thenAccept(bool -> {
                    if (bool) {
                        sender.sendMessage(MessageUtil.format("%prefix% &fConfig has been reloaded."));
                    } else {
                        sender.sendMessage(MessageUtil.format("%prefix% &cFailed to reload config."));
                    }
                });
    }

}
