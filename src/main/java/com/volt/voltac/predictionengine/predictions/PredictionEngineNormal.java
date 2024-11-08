package com.volt.voltac.predictionengine.predictions;

import com.volt.voltac.player.VoltPlayer;
import com.volt.voltac.utils.collisions.datatypes.SimpleCollisionBox;
import com.volt.voltac.utils.data.VectorData;
import com.volt.voltac.utils.math.GrimMath;
import com.volt.voltac.utils.nmsutil.Collisions;
import com.volt.voltac.utils.nmsutil.JumpPower;
import com.github.retrooper.packetevents.protocol.item.ItemStack;
import com.github.retrooper.packetevents.protocol.item.type.ItemTypes;
import com.github.retrooper.packetevents.protocol.player.ClientVersion;
import com.github.retrooper.packetevents.protocol.potion.PotionTypes;
import com.github.retrooper.packetevents.protocol.world.states.type.StateTypes;
import org.bukkit.util.Vector;

import java.util.HashSet;
import java.util.OptionalInt;
import java.util.Set;

public class PredictionEngineNormal extends PredictionEngine {

    public static void staticVectorEndOfTick(VoltPlayer player, Vector vector) {
        double adjustedY = vector.getY();
        final OptionalInt levitation = player.compensatedEntities.getPotionLevelForPlayer(PotionTypes.LEVITATION);
        if (levitation.isPresent()) {
            adjustedY += (0.05 * (levitation.getAsInt() + 1) - vector.getY()) * 0.2;
            // Reset fall distance with levitation
            player.fallDistance = 0;
        } else if (player.hasGravity) {
            adjustedY -= player.gravity;
        }

        vector.setX(vector.getX() * player.friction);
        vector.setY(adjustedY * 0.98F);
        vector.setZ(vector.getZ() * player.friction);
    }

    @Override
    public void addJumpsToPossibilities(VoltPlayer player, Set<VectorData> existingVelocities) {
        for (VectorData vector : new HashSet<>(existingVelocities)) {
            Vector jump = vector.vector.clone();

            if (!player.isFlying) {
                // Negative jump boost does not allow the player to leave the ground
                // Negative jump boost doesn't seem to work in water/lava
                // If the player didn't try to jump
                // And 0.03 didn't affect onGround status
                // The player cannot jump
                final OptionalInt jumpBoost = player.compensatedEntities.getPotionLevelForPlayer(PotionTypes.JUMP_BOOST);
                if (((!jumpBoost.isPresent() || jumpBoost.getAsInt() >= 0) && player.onGround) || !player.lastOnGround)
                    return;

                JumpPower.jumpFromGround(player, jump);
            } else {
                jump.add(new Vector(0, player.flySpeed * 3, 0));
                if (!player.wasFlying) {
                    Vector edgeCaseJump = jump.clone();
                    JumpPower.jumpFromGround(player, edgeCaseJump);
                    existingVelocities.add(vector.returnNewModified(edgeCaseJump, VectorData.VectorType.Jump));
                }
            }

            existingVelocities.add(vector.returnNewModified(jump, VectorData.VectorType.Jump));
        }
    }

    @Override
    public void endOfTick(VoltPlayer player, double delta) {
        super.endOfTick(player, delta);

        boolean walkingOnPowderSnow = false;

        if (!player.compensatedEntities.getSelf().inVehicle() && player.getClientVersion().isNewerThanOrEquals(ClientVersion.V_1_17) &&
                player.compensatedWorld.getStateTypeAt(player.x, player.y, player.z) == StateTypes.POWDER_SNOW) {
            ItemStack boots = player.getInventory().getBoots();
            walkingOnPowderSnow = boots != null && boots.getType() == ItemTypes.LEATHER_BOOTS;
        }

        player.isClimbing = Collisions.onClimbable(player, player.x, player.y, player.z);

        // Force 1.13.2 and below players to have something to collide with horizontally to climb
        if (player.lastWasClimbing == 0 && (player.pointThreeEstimator.isNearClimbable() || player.isClimbing) && (player.getClientVersion().isNewerThanOrEquals(ClientVersion.V_1_14)
                || !Collisions.isEmpty(player, player.boundingBox.copy().expand(
                player.clientVelocity.getX(), 0, player.clientVelocity.getZ()).expand(0.5, -SimpleCollisionBox.COLLISION_EPSILON, 0.5))) || walkingOnPowderSnow) {
            Vector ladderVelocity = player.clientVelocity.clone().setY(0.2);
            staticVectorEndOfTick(player, ladderVelocity);
            player.lastWasClimbing = ladderVelocity.getY();
        }

        for (VectorData vector : player.getPossibleVelocitiesMinusKnockback()) {
            staticVectorEndOfTick(player, vector.vector);
        }
    }

    @Override
    public Vector handleOnClimbable(Vector vector, VoltPlayer player) {
        if (player.isClimbing) {
            // Reset fall distance when climbing
            player.fallDistance = 0;

            vector.setX(GrimMath.clamp(vector.getX(), -0.15F, 0.15F));
            vector.setZ(GrimMath.clamp(vector.getZ(), -0.15F, 0.15F));
            vector.setY(Math.max(vector.getY(), -0.15F));

            // Yes, this uses shifting not crouching
            if (vector.getY() < 0.0 && !(player.compensatedWorld.getStateTypeAt(player.lastX, player.lastY, player.lastZ) == StateTypes.SCAFFOLDING) && player.isSneaking && !player.isFlying) {
                vector.setY(0.0);
            }
        }

        return vector;
    }
}