package com.volt.voltac.checks.impl.badpackets;

import com.volt.voltac.checks.Check;
import com.volt.voltac.checks.CheckData;
import com.volt.voltac.checks.type.PacketCheck;
import com.volt.voltac.player.VoltPlayer;
import com.volt.voltac.utils.data.Pair;
import com.github.retrooper.packetevents.event.PacketReceiveEvent;
import com.github.retrooper.packetevents.event.PacketSendEvent;
import com.github.retrooper.packetevents.protocol.packettype.PacketType;
import com.github.retrooper.packetevents.wrapper.play.client.WrapperPlayClientKeepAlive;
import com.github.retrooper.packetevents.wrapper.play.server.WrapperPlayServerKeepAlive;

import java.util.LinkedList;
import java.util.Queue;

@CheckData(name = "BadPacketsO")
public class BadPacketsO extends Check implements PacketCheck {
    Queue<Pair<Long, Long>> keepaliveMap = new LinkedList<>();

    public BadPacketsO(VoltPlayer player) {
        super(player);
    }

    @Override
    public void onPacketSend(PacketSendEvent event) {
        if (event.getPacketType() == PacketType.Play.Server.KEEP_ALIVE) {
            WrapperPlayServerKeepAlive packet = new WrapperPlayServerKeepAlive(event);
            keepaliveMap.add(new Pair<>(packet.getId(), System.nanoTime()));
        }
    }

    @Override
    public void onPacketReceive(PacketReceiveEvent event) {
        if (event.getPacketType() == PacketType.Play.Client.KEEP_ALIVE) {
            WrapperPlayClientKeepAlive packet = new WrapperPlayClientKeepAlive(event);

            long id = packet.getId();
            boolean hasID = false;

            for (Pair<Long, Long> iterator : keepaliveMap) {
                if (iterator.getFirst() == id) {
                    hasID = true;
                    break;
                }
            }

            if (!hasID) {
                if (flagAndAlert("id=" + id) && shouldModifyPackets()) {
                    event.setCancelled(true);
                    player.onPacketCancel();
                }
            } else { // Found the ID, remove stuff until we get to it (to stop very slow memory leaks)
                Pair<Long, Long> data;
                do {
                    data = keepaliveMap.poll();
                    if (data == null) break;
                } while (data.getFirst() != id);
            }
        }
    }
}
