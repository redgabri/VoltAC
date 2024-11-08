package com.volt.voltac.utils.collisions.datatypes;

import com.volt.voltac.player.VoltPlayer;
import com.github.retrooper.packetevents.protocol.player.ClientVersion;
import com.github.retrooper.packetevents.protocol.world.states.WrappedBlockState;

public interface CollisionFactory {
    CollisionBox fetch(VoltPlayer player, ClientVersion version, WrappedBlockState block, int x, int y, int z);
}