package com.volt.voltac.predictionengine;

import com.volt.voltac.checks.Check;
import com.volt.voltac.checks.type.PostPredictionCheck;
import com.volt.voltac.player.GrimPlayer;
import com.volt.voltac.utils.anticheat.update.PredictionComplete;
import com.volt.voltac.utils.collisions.datatypes.SimpleCollisionBox;
import com.volt.voltac.utils.data.packetentity.PacketEntity;
import com.github.retrooper.packetevents.protocol.player.ClientVersion;

public class GhostBlockDetector extends Check implements PostPredictionCheck {

    public GhostBlockDetector(GrimPlayer player) {
        super(player);
    }

    // Must process data first to get rid of false positives from ghost blocks
    public void onPredictionComplete(final PredictionComplete predictionComplete) {
        // If the offset is low, there probably isn't ghost blocks
        // However, if we would flag nofall, check for ghost blocks
        if (predictionComplete.getOffset() < 0.001 && (player.clientClaimsLastOnGround == player.onGround || player.compensatedEntities.getSelf().inVehicle()))
            return;

        // This is meant for stuff like buggy blocks and mechanics on old clients
        // It was once for ghost blocks, although I've removed it for ghost blocks
        boolean shouldResync = isGhostBlock(player);

        if (shouldResync) {
            // I once used a buffer for this, but it should be very accurate now.
            if (player.clientClaimsLastOnGround != player.onGround) {
                // Rethink this.  Is there a better way to force the player's ground for the next tick?
                // No packet for it, so I think this is sadly the best way.
                player.onGround = player.clientClaimsLastOnGround;
            }

            predictionComplete.setOffset(0);
            player.getSetbackTeleportUtil().executeForceResync();
        }
    }

    public static boolean isGhostBlock(GrimPlayer player) {
        // Player is on glitchy block (1.8 client on anvil/wooden chest)
        if (player.uncertaintyHandler.isOrWasNearGlitchyBlock) {
            return true;
        }

        // Boats are moved client sided by 1.7/1.8 players, and have a mind of their own
        // Simply setback, don't ban, if a player gets a violation by a boat.
        // Note that we allow setting back to the ground for this one, to try and mitigate
        // the effect that this buggy behavior has on players
        if (player.getClientVersion().isOlderThan(ClientVersion.V_1_9)) {
            SimpleCollisionBox largeExpandedBB = player.boundingBox.copy().expand(12, 0.5, 12);

            for (PacketEntity entity : player.compensatedEntities.entityMap.values()) {
                if (entity.isBoat()) {
                    if (entity.getPossibleCollisionBoxes().isIntersected(largeExpandedBB)) {
                        return true;
                    }
                }
            }
        }

        return false;
    }
}
