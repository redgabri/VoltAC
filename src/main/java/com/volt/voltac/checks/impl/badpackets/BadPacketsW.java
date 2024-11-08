package com.volt.voltac.checks.impl.badpackets;

import com.volt.voltac.checks.Check;
import com.volt.voltac.checks.CheckData;
import com.volt.voltac.checks.type.PacketCheck;
import com.volt.voltac.player.VoltPlayer;

@CheckData(name = "BadPacketsW", experimental = true)
public class BadPacketsW extends Check implements PacketCheck {
    public BadPacketsW(VoltPlayer player) {
        super(player);
    }
}
