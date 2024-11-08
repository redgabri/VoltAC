package com.volt.voltac.checks.impl.prediction;

import com.volt.voltac.checks.Check;
import com.volt.voltac.checks.CheckData;
import com.volt.voltac.checks.type.PostPredictionCheck;
import com.volt.voltac.player.GrimPlayer;
import com.volt.voltac.utils.anticheat.update.PredictionComplete;
import com.github.retrooper.packetevents.PacketEvents;
import com.github.retrooper.packetevents.manager.server.ServerVersion;
import com.github.retrooper.packetevents.protocol.player.GameMode;

@CheckData(name = "GroundSpoof", configName = "GroundSpoof", setback = 10, decay = 0.01)
public class NoFallB extends Check implements PostPredictionCheck {

    public NoFallB(GrimPlayer player) {
        super(player);
    }

    @Override
    public void onPredictionComplete(final PredictionComplete predictionComplete) {
        // Exemptions
        // Don't check players in spectator
        if (PacketEvents.getAPI().getServerManager().getVersion().isNewerThanOrEquals(ServerVersion.V_1_8) && player.gamemode == GameMode.SPECTATOR)
            return;
        // And don't check this long list of ground exemptions
        if (player.exemptOnGround() || !predictionComplete.isChecked()) return;
        // Don't check if the player was on a ghost block
        if (player.getSetbackTeleportUtil().blockOffsets) return;
        // Viaversion sends wrong ground status... (doesn't matter but is annoying)
        if (player.packetStateData.lastPacketWasTeleport) return;

        boolean invalid = player.clientClaimsLastOnGround != player.onGround;

        if (invalid) {
            if (flagWithSetback()) {
                alert("claimed " + player.clientClaimsLastOnGround);
            }
            player.checkManager.getNoFall().flipPlayerGroundStatus = true;
        }
    }
}
