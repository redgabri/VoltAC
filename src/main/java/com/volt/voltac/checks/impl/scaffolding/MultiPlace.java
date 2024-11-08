package com.volt.voltac.checks.impl.scaffolding;

import com.volt.voltac.checks.CheckData;
import com.volt.voltac.checks.type.BlockPlaceCheck;
import com.volt.voltac.player.VoltPlayer;
import com.volt.voltac.utils.anticheat.MessageUtil;
import com.volt.voltac.utils.anticheat.update.BlockPlace;
import com.volt.voltac.utils.anticheat.update.PredictionComplete;
import com.github.retrooper.packetevents.event.PacketReceiveEvent;
import com.github.retrooper.packetevents.protocol.player.ClientVersion;
import com.github.retrooper.packetevents.protocol.world.BlockFace;
import com.github.retrooper.packetevents.util.Vector3f;
import com.github.retrooper.packetevents.util.Vector3i;
import com.github.retrooper.packetevents.wrapper.play.client.WrapperPlayClientPlayerFlying;

import java.util.ArrayList;
import java.util.List;

@CheckData(name = "MultiPlace", experimental = true)
public class MultiPlace extends BlockPlaceCheck {
    public MultiPlace(VoltPlayer player) {
        super(player);
    }

    private final List<String> flags = new ArrayList<>();

    private boolean hasPlaced;
    private BlockFace lastFace;
    private Vector3f lastCursor;
    private Vector3i lastPos;

    @Override
    public void onBlockPlace(final BlockPlace place) {
        final BlockFace face = place.getDirection();
        final Vector3f cursor = place.getCursor();
        final Vector3i pos = place.getPlacedAgainstBlockLocation();

        if (hasPlaced && (face != lastFace || !cursor.equals(lastCursor) || !pos.equals(lastPos))) {
            final String verbose = "face=" + face + ", lastFace=" + lastFace
                    + ", cursor=" + MessageUtil.toUnlabledString(cursor) + ", lastCursor=" + MessageUtil.toUnlabledString(lastCursor)
                    + ", pos=" + MessageUtil.toUnlabledString(pos) + ", lastPos=" + MessageUtil.toUnlabledString(lastPos);
            if (player.getClientVersion().isOlderThanOrEquals(ClientVersion.V_1_8)) {
                if (flagAndAlert(verbose) && shouldModifyPackets() && shouldCancel()) {
                    place.resync();
                }
            } else {
                flags.add(verbose);
            }
        }

        lastFace = face;
        lastCursor = cursor;
        lastPos = pos;
        hasPlaced = true;
    }

    @Override
    public void onPacketReceive(PacketReceiveEvent event) {
        if (WrapperPlayClientPlayerFlying.isFlying(event.getPacketType()) && !player.packetStateData.lastPacketWasTeleport && !player.packetStateData.lastPacketWasOnePointSeventeenDuplicate) {
            hasPlaced = false;
        }
    }

    @Override
    public void onPredictionComplete(PredictionComplete predictionComplete) {
        if (player.getClientVersion().isNewerThan(ClientVersion.V_1_8) && !player.skippedTickInActualMovement && predictionComplete.isChecked()) {
            for (String verbose : flags) {
                flagAndAlert(verbose);
            }
        }

        flags.clear();
    }
}
