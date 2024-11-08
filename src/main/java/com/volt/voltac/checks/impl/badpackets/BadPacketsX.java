package com.volt.voltac.checks.impl.badpackets;

import com.volt.voltac.checks.Check;
import com.volt.voltac.checks.CheckData;
import com.volt.voltac.checks.type.PacketCheck;
import com.volt.voltac.player.VoltPlayer;
import com.github.retrooper.packetevents.event.PacketReceiveEvent;
import com.github.retrooper.packetevents.protocol.item.type.ItemTypes;
import com.github.retrooper.packetevents.protocol.player.ClientVersion;
import com.github.retrooper.packetevents.protocol.player.DiggingAction;
import com.github.retrooper.packetevents.protocol.world.states.type.StateType;
import com.github.retrooper.packetevents.protocol.world.states.type.StateTypes;
import com.github.retrooper.packetevents.wrapper.play.client.WrapperPlayClientPlayerDigging;

@CheckData(name = "BadPacketsX", experimental = true)
public class BadPacketsX extends Check implements PacketCheck {
    public BadPacketsX(VoltPlayer player) {
        super(player);
    }

    public final boolean noFireHitbox = player.getClientVersion().isOlderThanOrEquals(ClientVersion.V_1_15_2);

    public final void handle(PacketReceiveEvent event, WrapperPlayClientPlayerDigging dig, StateType block) {
        if (dig.getAction() != DiggingAction.START_DIGGING && dig.getAction() != DiggingAction.FINISHED_DIGGING)
            return;

        // the block does not have a hitbox
        boolean invalid = (block == StateTypes.LIGHT && !(player.getInventory().getHeldItem().is(ItemTypes.LIGHT) || player.getInventory().getOffHand().is(ItemTypes.LIGHT)))
                || block.isAir()
                || block == StateTypes.WATER
                || block == StateTypes.LAVA
                || block == StateTypes.BUBBLE_COLUMN
                || block == StateTypes.MOVING_PISTON
                || (block == StateTypes.FIRE && noFireHitbox)
                // or the client claims to have broken an unbreakable block
                || block.getHardness() == -1.0f && dig.getAction() == DiggingAction.FINISHED_DIGGING;

        if (invalid && flagAndAlert("block=" + block.getName() + ", type=" + dig.getAction()) && shouldModifyPackets()) {
            event.setCancelled(true);
            player.onPacketCancel();
        }
    }
}