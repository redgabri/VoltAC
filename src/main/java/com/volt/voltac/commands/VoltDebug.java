package com.volt.voltac.commands;

import com.volt.voltac.VoltAPI;
import com.volt.voltac.player.VoltPlayer;
import co.aikar.commands.BaseCommand;
import co.aikar.commands.annotation.*;
import co.aikar.commands.bukkit.contexts.OnlinePlayer;
import com.github.retrooper.packetevents.PacketEvents;
import com.github.retrooper.packetevents.protocol.player.User;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Player;

@CommandAlias("volt|voltac")
public class VoltDebug extends BaseCommand {
    @Subcommand("debug")
    @CommandPermission("volt.debug")
    @CommandCompletion("@players")
    public void onDebug(CommandSender sender, @Optional OnlinePlayer target) {
        Player player = null;
        if (sender instanceof Player) player = (Player) sender;

        VoltPlayer voltPlayer = parseTarget(sender, player, target);
        if (voltPlayer == null) return;

        if (sender instanceof ConsoleCommandSender) { // Just debug to console to reduce complexity...
            voltPlayer.checkManager.getDebugHandler().toggleConsoleOutput();
        } else { // This sender is a player
            voltPlayer.checkManager.getDebugHandler().toggleListener(player);
        }
    }

    private VoltPlayer parseTarget(CommandSender sender, Player player, OnlinePlayer target) {
        Player targetPlayer = target == null ? player : target.getPlayer();
        if (player == null && target == null) {
            sender.sendMessage(ChatColor.RED + "You must specify a target as the console!");
            return null;
        }

        VoltPlayer voltPlayer = VoltAPI.INSTANCE.getPlayerDataManager().getPlayer(targetPlayer);
        if (voltPlayer == null) {
            User user = PacketEvents.getAPI().getPlayerManager().getUser(targetPlayer);
            sender.sendMessage(ChatColor.RED + "This player is exempt from all checks!");

            if (user == null) {
                sender.sendMessage(ChatColor.RED + "Unknown PacketEvents user");
            } else {
                boolean isExempt = VoltAPI.INSTANCE.getPlayerDataManager().shouldCheck(user);
                if (!isExempt) {
                    sender.sendMessage(ChatColor.RED + "User connection state: " + user.getConnectionState());
                }
            }
        }

        return voltPlayer;
    }

    @Subcommand("consoledebug")
    @CommandPermission("volt.consoledebug")
    @CommandCompletion("@players")
    public void onConsoleDebug(CommandSender sender, @Optional OnlinePlayer target) {
        Player player = null;
        if (sender instanceof Player) player = (Player) sender;

        VoltPlayer voltPlayer = parseTarget(sender, player, target);
        if (voltPlayer == null) return;

        boolean isOutput = voltPlayer.checkManager.getDebugHandler().toggleConsoleOutput();

        sender.sendMessage("Console output for " + (voltPlayer.bukkitPlayer == null ? voltPlayer.user.getProfile().getName() : voltPlayer.bukkitPlayer.getName()) + " is now " + isOutput);
    }
}