package com.volt.voltac.utils.blockplace;

import com.volt.voltac.player.GrimPlayer;
import com.volt.voltac.utils.anticheat.update.BlockPlace;

public interface BlockPlaceFactory {
    void applyBlockPlaceToWorld(GrimPlayer player, BlockPlace place);
}
