package com.volt.voltac.checks.impl.badpackets;

import com.volt.voltac.checks.Check;
import com.volt.voltac.checks.CheckData;
import com.volt.voltac.checks.type.PacketCheck;
import com.volt.voltac.player.VoltPlayer;
import com.github.retrooper.packetevents.event.PacketReceiveEvent;
import com.github.retrooper.packetevents.protocol.packettype.PacketType;
import com.github.retrooper.packetevents.wrapper.play.client.WrapperPlayClientEntityAction;

@CheckData(name = "BadPacketsF")
public class BadPacketsF extends Check implements PacketCheck {
    public boolean lastSprinting;
    public boolean exemptNext = true; // Support 1.14+ clients starting on either true or false sprinting, we don't know

    public BadPacketsF(VoltPlayer player) {
        super(player);
    }

    @Override
    public void onPacketReceive(PacketReceiveEvent event) {
        if (event.getPacketType() == PacketType.Play.Client.ENTITY_ACTION) {
            WrapperPlayClientEntityAction packet = new WrapperPlayClientEntityAction(event);

            if (packet.getAction() == WrapperPlayClientEntityAction.Action.START_SPRINTING) {
                if (lastSprinting) {
                    if (exemptNext) {
                        exemptNext = false;
                        return;
                    }
                    if (flagAndAlert("state=true") && shouldModifyPackets()) {
                        event.setCancelled(true);
                        player.onPacketCancel();
                    }
                }

                lastSprinting = true;
            } else if (packet.getAction() == WrapperPlayClientEntityAction.Action.STOP_SPRINTING) {
                if (!lastSprinting) {
                    if (exemptNext) {
                        exemptNext = false;
                        return;
                    }
                    if (flagAndAlert("state=false") && shouldModifyPackets()) {
                        event.setCancelled(true);
                        player.onPacketCancel();
                    }
                }

                lastSprinting = false;
            }
        }
    }
}
