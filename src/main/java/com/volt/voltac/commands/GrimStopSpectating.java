package com.volt.voltac.commands;

import com.volt.voltac.VoltAPI;
import com.volt.voltac.utils.anticheat.MessageUtil;
import co.aikar.commands.BaseCommand;
import co.aikar.commands.annotation.CommandAlias;
import co.aikar.commands.annotation.CommandCompletion;
import co.aikar.commands.annotation.CommandPermission;
import co.aikar.commands.annotation.Subcommand;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

@CommandAlias("grim|grimac")
public class GrimStopSpectating extends BaseCommand {
    @Subcommand("stopspectating")
    @CommandPermission("grim.spectate")
    @CommandCompletion("here")
    public void onStopSpectate(CommandSender sender, String[] args) {
        String string = args.length > 0 ? args[0] : null;
        if (!(sender instanceof Player)) return;
        Player player = (Player) sender;
        if (VoltAPI.INSTANCE.getSpectateManager().isSpectating(player.getUniqueId())) {
            boolean teleportBack = string == null || !string.equalsIgnoreCase("here");
            VoltAPI.INSTANCE.getSpectateManager().disable(player, teleportBack);
        } else {
            String message = VoltAPI.INSTANCE.getConfigManager().getConfig().getStringElse("cannot-spectate-return", "%prefix% &cYou can only do this after spectating a player.");
            sender.sendMessage(MessageUtil.format(message));
        }
    }
}

