package com.volt.voltac.utils.data.packetentity;

import com.volt.voltac.player.VoltPlayer;
import com.github.retrooper.packetevents.protocol.entity.type.EntityType;

import java.util.UUID;

public class PacketEntitySizeable extends PacketEntity {
    public int size = 4; // To support entity metadata being sent after spawn, assume max size of vanilla slime

    public PacketEntitySizeable(VoltPlayer player, UUID uuid, EntityType type, double x, double y, double z) {
        super(player, uuid, type, x, y, z);
    }
}
