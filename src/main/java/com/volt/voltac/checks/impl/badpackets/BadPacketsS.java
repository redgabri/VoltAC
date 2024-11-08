package com.volt.voltac.checks.impl.badpackets;

import com.volt.voltac.checks.Check;
import com.volt.voltac.checks.CheckData;
import com.volt.voltac.checks.type.PacketCheck;
import com.volt.voltac.player.VoltPlayer;

@CheckData(name = "BadPacketsS")
public class BadPacketsS extends Check implements PacketCheck {
    public BadPacketsS(VoltPlayer player) {
        super(player);
    }

}
