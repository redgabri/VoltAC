package com.volt.voltac.utils.collisions.datatypes;

import com.volt.voltac.player.VoltPlayer;
import com.github.retrooper.packetevents.protocol.player.ClientVersion;
import com.github.retrooper.packetevents.protocol.world.states.WrappedBlockState;
import com.github.retrooper.packetevents.protocol.world.states.type.StateType;

public interface HitBoxFactory {
    CollisionBox fetch(VoltPlayer player, StateType heldItem, ClientVersion version, WrappedBlockState block, int x, int y, int z);
}
