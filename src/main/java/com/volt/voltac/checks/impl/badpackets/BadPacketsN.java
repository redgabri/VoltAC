package com.volt.voltac.checks.impl.badpackets;

import com.volt.voltac.checks.Check;
import com.volt.voltac.checks.CheckData;
import com.volt.voltac.checks.type.PacketCheck;
import com.volt.voltac.player.VoltPlayer;

@CheckData(name = "BadPacketsN")
public class BadPacketsN extends Check implements PacketCheck {
    public BadPacketsN(final VoltPlayer player) {
        super(player);
    }
}
