package com.volt.voltac.checks.impl.crash;

import com.volt.voltac.checks.Check;
import com.volt.voltac.checks.CheckData;
import com.volt.voltac.checks.type.PacketCheck;
import com.volt.voltac.player.VoltPlayer;
import com.github.retrooper.packetevents.event.PacketReceiveEvent;
import com.github.retrooper.packetevents.protocol.world.Location;
import com.github.retrooper.packetevents.wrapper.play.client.WrapperPlayClientPlayerFlying;

@CheckData(name = "CrashC")
public class CrashC extends Check implements PacketCheck {
    public CrashC(VoltPlayer playerData) {
        super(playerData);
    }

    @Override
    public void onPacketReceive(PacketReceiveEvent event) {
        if (WrapperPlayClientPlayerFlying.isFlying(event.getPacketType())) {
            WrapperPlayClientPlayerFlying flying = new WrapperPlayClientPlayerFlying(event);
            if (flying.hasPositionChanged()) {
                Location pos = flying.getLocation();
                if (Double.isNaN(pos.getX()) || Double.isNaN(pos.getY()) || Double.isNaN(pos.getZ())
                        || Double.isInfinite(pos.getX()) || Double.isInfinite(pos.getY()) || Double.isInfinite(pos.getZ()) ||
                        Float.isNaN(pos.getYaw()) || Float.isNaN(pos.getPitch()) ||
                        Float.isInfinite(pos.getYaw()) || Float.isInfinite(pos.getPitch())) {
                    flagAndAlert("xyzYP: " + pos.getX() + ", " + pos.getY() + ", " + pos.getZ() + ", " + pos.getYaw() + ", " + pos.getPitch());
                    player.getSetbackTeleportUtil().executeViolationSetback();
                    event.setCancelled(true);
                    player.onPacketCancel();
                }
            }
        }
    }
}
