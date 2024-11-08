package com.volt.voltac.commands;

import com.volt.voltac.VoltAPI;
import co.aikar.commands.BaseCommand;
import co.aikar.commands.annotation.CommandAlias;
import co.aikar.commands.annotation.CommandPermission;
import co.aikar.commands.annotation.Subcommand;
import org.bukkit.entity.Player;

@CommandAlias("grim|grimac")
public class GrimVerbose extends BaseCommand {
    @Subcommand("verbose")
    @CommandPermission("grim.verbose")
    public void onVerbose(Player player) {
        VoltAPI.INSTANCE.getAlertManager().toggleVerbose(player);
    }
}
