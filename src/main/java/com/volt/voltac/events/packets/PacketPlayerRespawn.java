package com.volt.voltac.events.packets;

import com.volt.voltac.VoltAPI;
import com.volt.voltac.checks.impl.badpackets.BadPacketsE;
import com.volt.voltac.checks.impl.badpackets.BadPacketsF;
import com.volt.voltac.player.VoltPlayer;
import com.volt.voltac.utils.data.TrackerData;
import com.volt.voltac.utils.data.packetentity.PacketEntitySelf;
import com.volt.voltac.utils.enums.Pose;
import com.github.retrooper.packetevents.PacketEvents;
import com.github.retrooper.packetevents.event.PacketListenerAbstract;
import com.github.retrooper.packetevents.event.PacketListenerPriority;
import com.github.retrooper.packetevents.event.PacketSendEvent;
import com.github.retrooper.packetevents.manager.server.ServerVersion;
import com.github.retrooper.packetevents.protocol.entity.type.EntityTypes;
import com.github.retrooper.packetevents.protocol.packettype.PacketType;
import com.github.retrooper.packetevents.protocol.player.ClientVersion;
import com.github.retrooper.packetevents.util.Vector3d;
import com.github.retrooper.packetevents.wrapper.play.server.WrapperPlayServerJoinGame;
import com.github.retrooper.packetevents.wrapper.play.server.WrapperPlayServerRespawn;
import com.github.retrooper.packetevents.wrapper.play.server.WrapperPlayServerUpdateHealth;
import org.bukkit.util.Vector;

import java.util.List;
import java.util.Objects;

/**
 * PlayerRespawnS2CPacket info (1.20.2+):
 * If the world is different (check via registry key), world is recreated (all entities etc destroyed).
 * <p>
 * Client player is ALWAYS recreated
 * <p>
 * If the packet has the `KEEP_TRACKED_DATA` flag:
 * Sneaking and Sprinting fields are kept on the new client player.
 * <p>
 * If the packet has the `KEEP_ATTRIBUTES` flag:
 * Attributes are kept.
 * <p>
 * New client player is initialised:
 * Pose is set to standing.
 * Velocity is set to zero.
 * Pitch is set to 0.
 * Yaw is set to -180.
 */
// TODO update for 1.20.2-
public class PacketPlayerRespawn extends PacketListenerAbstract {

    public PacketPlayerRespawn() {
        super(PacketListenerPriority.HIGH);
    }

    private static final byte KEEP_ATTRIBUTES = 1;
    private static final byte KEEP_TRACKED_DATA = 2;
    private static final byte KEEP_ALL = 3;

    private boolean hasFlag(WrapperPlayServerRespawn respawn, byte flag) {
        // This packet was added in 1.16
        // On versions older than 1.15, via does not keep all data.
        // https://github.com/ViaVersion/ViaVersion/blob/master/common/src/main/java/com/viaversion/viaversion/protocols/v1_15_2to1_16/rewriter/EntityPacketRewriter1_16.java#L124
        if (PacketEvents.getAPI().getServerManager().getVersion().isOlderThan(ServerVersion.V_1_15)) {
            return false;
        }
        return (respawn.getKeptData() & flag) != 0;
    }

    @Override
    public void onPacketSend(PacketSendEvent event) {
        if (event.getPacketType() == PacketType.Play.Server.UPDATE_HEALTH) {
            WrapperPlayServerUpdateHealth health = new WrapperPlayServerUpdateHealth(event);

            VoltPlayer player = VoltAPI.INSTANCE.getPlayerDataManager().getPlayer(event.getUser());
            if (player == null) return;
            //
            if (player.packetStateData.lastFood == health.getFood()
                    && player.packetStateData.lastHealth == health.getHealth()
                    && player.packetStateData.lastSaturation == health.getFoodSaturation()
                    && PacketEvents.getAPI().getServerManager().getVersion().isOlderThan(ServerVersion.V_1_9)) return;

            player.packetStateData.lastFood = health.getFood();
            player.packetStateData.lastHealth = health.getHealth();
            player.packetStateData.lastSaturation = health.getFoodSaturation();

            player.sendTransaction();

            if (health.getFood() == 20) { // Split so transaction before packet
                player.latencyUtils.addRealTimeTask(player.lastTransactionReceived.get(), () -> player.food = 20);
            } else { // Split so transaction after packet
                player.latencyUtils.addRealTimeTask(player.lastTransactionReceived.get() + 1, () -> player.food = health.getFood());
            }

            if (health.getHealth() <= 0) {
                player.latencyUtils.addRealTimeTask(player.lastTransactionSent.get(), () -> player.compensatedEntities.getSelf().isDead = true);
            } else {
                player.latencyUtils.addRealTimeTask(player.lastTransactionSent.get() + 1, () -> player.compensatedEntities.getSelf().isDead = false);
            }

            event.getTasksAfterSend().add(player::sendTransaction);
        }

        if (event.getPacketType() == PacketType.Play.Server.JOIN_GAME) {
            VoltPlayer player = VoltAPI.INSTANCE.getPlayerDataManager().getPlayer(event.getUser());
            if (player == null) return;

            WrapperPlayServerJoinGame joinGame = new WrapperPlayServerJoinGame(event);
            player.gamemode = joinGame.getGameMode();
            player.entityID = joinGame.getEntityId();
            player.dimensionType = joinGame.getDimensionType();

            if (PacketEvents.getAPI().getServerManager().getVersion().isOlderThan(ServerVersion.V_1_17)) return;
            player.compensatedWorld.setDimension(joinGame.getDimensionType(), event.getUser());
        }

        if (event.getPacketType() == PacketType.Play.Server.RESPAWN) {
            WrapperPlayServerRespawn respawn = new WrapperPlayServerRespawn(event);

            VoltPlayer player = VoltAPI.INSTANCE.getPlayerDataManager().getPlayer(event.getUser());
            if (player == null) return;

            List<Runnable> tasks = event.getTasksAfterSend();
            tasks.add(player::sendTransaction);

            // Force the player to accept a teleport before respawning
            // (We won't process movements until they accept a teleport, we won't let movements though either)
            // Also invalidate previous positions
            player.getSetbackTeleportUtil().hasAcceptedSpawnTeleport = false;
            player.getSetbackTeleportUtil().lastKnownGoodPosition = null;

            // clear server entity positions when the world changes
            if (isWorldChange(player, respawn)) {
                player.compensatedEntities.serverPositionsMap.clear();
            }

            player.latencyUtils.addRealTimeTask(player.lastTransactionSent.get() + 1, () -> {
                player.isSneaking = false;
                player.lastOnGround = false;
                player.onGround = false;
                player.isInBed = false;
                player.packetStateData.setSlowedByUsingItem(false);
                player.packetStateData.packetPlayerOnGround = false; // If somewhere else pulls last ground to fix other issues
                player.packetStateData.lastClaimedPosition = new Vector3d();
                player.filterMojangStupidityOnMojangStupidity = new Vector3d();

                final boolean keepTrackedData = this.hasFlag(respawn, KEEP_TRACKED_DATA);

                if (!keepTrackedData) {
                    player.powderSnowFrozenTicks = 0;
                    player.compensatedEntities.getSelf().hasGravity = true;
                    player.playerEntityHasGravity = true;
                }

                if (player.getClientVersion().isNewerThanOrEquals(ClientVersion.V_1_19_4)) {
                    if (!keepTrackedData) {
                        player.isSprinting = false;
                    }
                } else {
                    player.lastSprintingForSpeed = false;
                }

                player.checkManager.getPacketCheck(BadPacketsE.class).handleRespawn(); // Reminder ticks reset

                // compensate for immediate respawn gamerule
                if (player.getClientVersion().isNewerThanOrEquals(ClientVersion.V_1_15)) {
                    player.checkManager.getPacketCheck(BadPacketsF.class).exemptNext = true;
                }

                // EVERYTHING gets reset on a cross dimensional teleport, clear chunks and entities!
                if (isWorldChange(player, respawn)) {
                    player.compensatedEntities.entityMap.clear();
                    player.compensatedWorld.activePistons.clear();
                    player.compensatedWorld.openShulkerBoxes.clear();
                    player.compensatedWorld.chunks.clear();
                    player.compensatedWorld.isRaining = false;
                }
                player.dimensionType = respawn.getDimensionType();

                player.compensatedEntities.serverPlayerVehicle = null; // All entities get removed on respawn
                player.compensatedEntities.playerEntity = new PacketEntitySelf(player, player.compensatedEntities.playerEntity);
                player.compensatedEntities.selfTrackedEntity = new TrackerData(0, 0, 0, 0, 0, EntityTypes.PLAYER, player.lastTransactionSent.get());

                if (player.getClientVersion().isOlderThan(ClientVersion.V_1_14)) { // 1.14+ players send a packet for this, listen for it instead
                    player.isSprinting = false;
                    player.checkManager.getPacketCheck(BadPacketsF.class).lastSprinting = false; // Pre 1.14 clients set this to false when creating new entity
                    // TODO: What the fuck viaversion, why do you throw out keep all metadata?
                    // The server doesn't even use it... what do we do?
                    player.compensatedEntities.hasSprintingAttributeEnabled = false;
                }
                player.pose = Pose.STANDING;
                player.clientVelocity = new Vector();
                player.gamemode = respawn.getGameMode();
                if (PacketEvents.getAPI().getServerManager().getVersion().isNewerThanOrEquals(ServerVersion.V_1_17)) {
                    player.compensatedWorld.setDimension(respawn.getDimensionType(), event.getUser());
                }

                if (player.getClientVersion().isNewerThanOrEquals(ClientVersion.V_1_16) && !this.hasFlag(respawn, KEEP_ATTRIBUTES)) {
                    // Reset attributes if not kept
                    player.compensatedEntities.getSelf().resetAttributes();
                    player.compensatedEntities.hasSprintingAttributeEnabled = false;
                }
            });
        }
    }

    private boolean isWorldChange(VoltPlayer player, WrapperPlayServerRespawn respawn) {
        ClientVersion version = PacketEvents.getAPI().getServerManager().getVersion().toClientVersion();
        return respawn.getDimensionType().getId(version) != player.dimensionType.getId(version)
                || !Objects.equals(respawn.getDimensionType().getName(), player.dimensionType.getName());
    }
}