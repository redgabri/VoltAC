package com.volt.voltac.manager.init.start;

import com.volt.voltac.VoltAPI;
import com.volt.voltac.manager.init.Initable;
import com.github.retrooper.packetevents.PacketEvents;
import com.github.retrooper.packetevents.protocol.player.User;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

public class ExemptOnlinePlayers implements Initable {
    @Override
    public void start() {
        for (Player player : Bukkit.getOnlinePlayers()) {
            User user = PacketEvents.getAPI().getPlayerManager().getUser(player);
            VoltAPI.INSTANCE.getPlayerDataManager().exemptUsers.add(user);
        }
    }
}
