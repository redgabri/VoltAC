package com.volt.voltac.utils.data.packetentity;

import com.volt.voltac.player.GrimPlayer;
import com.volt.voltac.utils.data.attribute.ValuedAttribute;
import com.github.retrooper.packetevents.protocol.attribute.Attributes;
import com.github.retrooper.packetevents.protocol.entity.type.EntityType;

import java.util.UUID;

public class PacketEntityRideable extends PacketEntity {

    public boolean hasSaddle = false;
    public int boostTimeMax = 0;
    public int currentBoostTime = 0;

    public PacketEntityRideable(GrimPlayer player, UUID uuid, EntityType type, double x, double y, double z) {
        super(player, uuid, type, x, y, z);
        setAttribute(Attributes.GENERIC_STEP_HEIGHT, 1.0f);
        trackAttribute(ValuedAttribute.ranged(Attributes.GENERIC_MOVEMENT_SPEED, 0.1f, 0, 1024));
    }
}
