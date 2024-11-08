package com.volt.voltac.checks.impl.misc;

import com.volt.voltac.checks.Check;
import com.volt.voltac.checks.CheckData;
import com.volt.voltac.checks.type.PacketCheck;
import com.volt.voltac.player.VoltPlayer;

@CheckData(name = "TransactionOrder", experimental = false)
public class TransactionOrder extends Check implements PacketCheck {

    public TransactionOrder(VoltPlayer player) {
        super(player);
    }

}