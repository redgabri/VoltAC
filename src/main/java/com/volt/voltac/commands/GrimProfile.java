package com.volt.voltac.commands;

import com.volt.voltac.VoltAPI;
import com.volt.voltac.player.GrimPlayer;
import com.volt.voltac.utils.anticheat.MessageUtil;
import com.volt.voltac.utils.anticheat.MultiLibUtil;
import co.aikar.commands.BaseCommand;
import co.aikar.commands.annotation.CommandAlias;
import co.aikar.commands.annotation.CommandCompletion;
import co.aikar.commands.annotation.CommandPermission;
import co.aikar.commands.annotation.Subcommand;
import co.aikar.commands.bukkit.contexts.OnlinePlayer;
import com.github.retrooper.packetevents.PacketEvents;
import com.github.retrooper.packetevents.manager.server.ServerVersion;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

@CommandAlias("grim|grimac")
public class GrimProfile extends BaseCommand {
    @Subcommand("profile")
    @CommandPermission("grim.profile")
    @CommandCompletion("@players")
    public void onConsoleDebug(CommandSender sender, OnlinePlayer target) {
        Player player = null;
        if (sender instanceof Player) player = (Player) sender;

        // Short circuit due to minimum java requirements for MultiLib
        if (PacketEvents.getAPI().getServerManager().getVersion().isNewerThanOrEquals(ServerVersion.V_1_18) && MultiLibUtil.isExternalPlayer(target.getPlayer())) {
            String alertString = VoltAPI.INSTANCE.getConfigManager().getConfig().getStringElse("player-not-this-server", "%prefix% &cThis player isn't on this server!");
            sender.sendMessage(MessageUtil.format(alertString));
            return;
        }

        GrimPlayer grimPlayer = VoltAPI.INSTANCE.getPlayerDataManager().getPlayer(target.getPlayer());
        if (grimPlayer == null) {
            String message = VoltAPI.INSTANCE.getConfigManager().getConfig().getStringElse("player-not-found", "%prefix% &cPlayer is exempt or offline!");
            sender.sendMessage(MessageUtil.format(message));
            return;
        }

        for (String message : VoltAPI.INSTANCE.getConfigManager().getConfig().getStringList("profile")) {
            message = VoltAPI.INSTANCE.getExternalAPI().replaceVariables(grimPlayer, message, true);
            sender.sendMessage(message);
        }
    }
}
