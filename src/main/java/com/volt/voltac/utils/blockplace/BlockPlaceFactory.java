package com.volt.voltac.utils.blockplace;

import com.volt.voltac.player.VoltPlayer;
import com.volt.voltac.utils.anticheat.update.BlockPlace;

public interface BlockPlaceFactory {
    void applyBlockPlaceToWorld(VoltPlayer player, BlockPlace place);
}
