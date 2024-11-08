package com.volt.voltac.commands;

import com.volt.voltac.VoltAPI;
import com.volt.voltac.player.GrimPlayer;
import com.volt.voltac.utils.anticheat.MessageUtil;
import com.volt.voltac.utils.anticheat.MultiLibUtil;
import co.aikar.commands.BaseCommand;
import co.aikar.commands.annotation.*;
import co.aikar.commands.bukkit.contexts.OnlinePlayer;
import com.github.retrooper.packetevents.PacketEvents;
import com.github.retrooper.packetevents.manager.server.ServerVersion;
import io.github.retrooper.packetevents.adventure.serializer.legacy.LegacyComponentSerializer;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.event.ClickEvent;
import net.kyori.adventure.text.event.HoverEvent;
import org.bukkit.GameMode;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

@CommandAlias("grim|grimac")
public class GrimSpectate extends BaseCommand {
    @Subcommand("spectate")
    @CommandPermission("grim.spectate")
    @CommandCompletion("@players")
    public void onSpectate(CommandSender sender, @Optional OnlinePlayer target) {
        if (!(sender instanceof Player)) return;
        Player player = (Player) sender;

        if (target != null && target.getPlayer().getUniqueId().equals(player.getUniqueId())) {
            String message = VoltAPI.INSTANCE.getConfigManager().getConfig().getStringElse("cannot-run-on-self", "%prefix% &cYou cannot use this command on yourself!");
            sender.sendMessage(MessageUtil.format(message));
            return;
        }

        if (target == null || (PacketEvents.getAPI().getServerManager().getVersion().isNewerThanOrEquals(ServerVersion.V_1_18) && MultiLibUtil.isExternalPlayer(target.getPlayer()))) {
            String message = VoltAPI.INSTANCE.getConfigManager().getConfig().getStringElse("player-not-this-server", "%prefix% &cThis player isn't on this server!");
            sender.sendMessage(MessageUtil.format(message));
            return;
        }
        //hide player from tab list
        if (VoltAPI.INSTANCE.getSpectateManager().enable(player)) {
            GrimPlayer grimPlayer = VoltAPI.INSTANCE.getPlayerDataManager().getPlayer(player);
            if (grimPlayer != null) {
                String message = VoltAPI.INSTANCE.getConfigManager().getConfig().getStringElse("spectate-return", "\n%prefix% &fClick here to return to previous location\n");
                grimPlayer.user.sendMessage(
                        LegacyComponentSerializer.legacy('&')
                                .deserialize(MessageUtil.formatWithNoColor(message))
                                .clickEvent(ClickEvent.clickEvent(ClickEvent.Action.RUN_COMMAND, "/grim stopspectating"))
                                .hoverEvent(HoverEvent.showText(Component.text("/grim stopspectating")))
                );
            }
        }

        player.setGameMode(GameMode.SPECTATOR);
        player.teleport(target.getPlayer());
    }


}
