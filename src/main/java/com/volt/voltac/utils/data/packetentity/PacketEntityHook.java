package com.volt.voltac.utils.data.packetentity;

import com.volt.voltac.player.VoltPlayer;
import com.github.retrooper.packetevents.protocol.entity.type.EntityType;

import java.util.UUID;

public class PacketEntityHook extends PacketEntity{
    public int owner;
    public int attached = -1;

    public PacketEntityHook(VoltPlayer player, UUID uuid, EntityType type, double x, double y, double z, int owner) {
        super(player, uuid, type, x, y, z);
        this.owner = owner;
    }
}
