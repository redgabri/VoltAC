package com.volt.voltac.events.packets;

import ac.grim.grimac.api.config.ConfigManager;
import com.volt.voltac.checks.Check;
import com.volt.voltac.checks.type.PacketCheck;
import com.volt.voltac.player.VoltPlayer;
import com.github.retrooper.packetevents.event.PacketReceiveEvent;
import com.github.retrooper.packetevents.event.PacketSendEvent;
import com.github.retrooper.packetevents.protocol.packettype.PacketType;
import com.github.retrooper.packetevents.wrapper.play.client.WrapperPlayClientPlayerAbilities;
import com.github.retrooper.packetevents.wrapper.play.server.WrapperPlayServerPlayerAbilities;

// The client can send ability packets out of order due to Mojang's excellent netcode design.
// We must delay the second ability packet until the tick after the first is received
// Else the player will fly for a tick, and we won't know about it, which is bad.
public class PacketPlayerAbilities extends Check implements PacketCheck {

    public PacketPlayerAbilities(VoltPlayer player) {
        super(player);
    }

    boolean lastSentPlayerCanFly = false;

    @Override
    public void onPacketReceive(PacketReceiveEvent event) {
        if (event.getPacketType() == PacketType.Play.Client.PLAYER_ABILITIES) {
            WrapperPlayClientPlayerAbilities abilities = new WrapperPlayClientPlayerAbilities(event);
            player.isFlying = abilities.isFlying() && player.canFly;
        }
    }

    @Override
    public void onPacketSend(PacketSendEvent event) {
        if (event.getPacketType() == PacketType.Play.Server.PLAYER_ABILITIES) {
            WrapperPlayServerPlayerAbilities abilities = new WrapperPlayServerPlayerAbilities(event);
            player.sendTransaction();

            if (lastSentPlayerCanFly && !abilities.isFlightAllowed()) {
                int noFlying = player.lastTransactionSent.get();
                if (maxFlyingPing != -1) {
                    player.runNettyTaskInMs(() -> {
                        if (player.lastTransactionReceived.get() < noFlying) {
                            player.getSetbackTeleportUtil().executeViolationSetback();
                        }
                    }, maxFlyingPing);
                }
            }

            lastSentPlayerCanFly = abilities.isFlightAllowed();

            player.latencyUtils.addRealTimeTask(player.lastTransactionSent.get(), () -> {
                player.canFly = abilities.isFlightAllowed();
                player.isFlying = abilities.isFlying();
            });

        }
    }

    int maxFlyingPing = 1000;

    @Override
    public void onReload(ConfigManager config) {
        maxFlyingPing = config.getIntElse("max-ping-out-of-flying", 1000);
    }

}