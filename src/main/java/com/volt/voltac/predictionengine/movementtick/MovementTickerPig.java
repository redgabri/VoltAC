package com.volt.voltac.predictionengine.movementtick;

import com.volt.voltac.player.VoltPlayer;
import com.volt.voltac.utils.data.packetentity.PacketEntityRideable;
import com.github.retrooper.packetevents.protocol.attribute.Attributes;
import org.bukkit.util.Vector;

public class MovementTickerPig extends MovementTickerRideable {
    public MovementTickerPig(VoltPlayer player) {
        super(player);
        movementInput = new Vector(0, 0, 1);
    }

    @Override
    public float getSteeringSpeed() { // Vanilla multiples by 0.225f
        PacketEntityRideable pig = (PacketEntityRideable) player.compensatedEntities.getSelf().getRiding();
        return (float) pig.getAttributeValue(Attributes.GENERIC_MOVEMENT_SPEED) * 0.225f;
    }
}
