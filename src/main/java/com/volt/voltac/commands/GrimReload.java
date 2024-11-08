package com.volt.voltac.commands;

import com.volt.voltac.GrimAPI;
import com.volt.voltac.utils.anticheat.MessageUtil;
import co.aikar.commands.BaseCommand;
import co.aikar.commands.annotation.CommandAlias;
import co.aikar.commands.annotation.CommandPermission;
import co.aikar.commands.annotation.Subcommand;
import org.bukkit.command.CommandSender;

@CommandAlias("grim|grimac")
public class GrimReload extends BaseCommand {
    @Subcommand("reload")
    @CommandPermission("grim.reload")
    public void onReload(CommandSender sender) {
        //reload config
        sender.sendMessage(MessageUtil.format("%prefix% &7Reloading config..."));
        GrimAPI.INSTANCE.getExternalAPI().reloadAsync().exceptionally(throwable -> false)
                .thenAccept(bool -> {
                    if (bool) {
                        sender.sendMessage(MessageUtil.format("%prefix% &fConfig has been reloaded."));
                    } else {
                        sender.sendMessage(MessageUtil.format("%prefix% &cFailed to reload config."));
                    }
                });
    }

}
