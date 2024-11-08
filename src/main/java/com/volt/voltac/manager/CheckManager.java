package com.volt.voltac.manager;

import ac.grim.grimac.api.AbstractCheck;
import com.volt.voltac.checks.impl.aim.AimDuplicateLook;
import com.volt.voltac.checks.impl.aim.AimModulo360;
import com.volt.voltac.checks.impl.aim.processor.AimProcessor;
import com.volt.voltac.checks.impl.badpackets.*;
import com.volt.voltac.checks.impl.combat.Reach;
import com.volt.voltac.checks.impl.crash.*;
import com.volt.voltac.checks.impl.exploit.ExploitA;
import com.volt.voltac.checks.impl.exploit.ExploitB;
import com.volt.voltac.checks.impl.groundspoof.NoFallA;
import com.volt.voltac.checks.impl.misc.ClientBrand;
import com.volt.voltac.checks.impl.misc.FastBreak;
import com.volt.voltac.checks.impl.misc.GhostBlockMitigation;
import com.volt.voltac.checks.impl.misc.TransactionOrder;
import com.volt.voltac.checks.impl.movement.*;
import com.volt.voltac.checks.impl.post.PostCheck;
import com.volt.voltac.checks.impl.prediction.DebugHandler;
import com.volt.voltac.checks.impl.prediction.NoFallB;
import com.volt.voltac.checks.impl.prediction.OffsetHandler;
import com.volt.voltac.checks.impl.prediction.Phase;
import com.volt.voltac.checks.impl.scaffolding.*;
import com.volt.voltac.checks.impl.velocity.ExplosionHandler;
import com.volt.voltac.checks.impl.velocity.KnockbackHandler;
import com.volt.voltac.checks.type.*;
import com.volt.voltac.events.packets.PacketChangeGameState;
import com.volt.voltac.events.packets.PacketEntityReplication;
import com.volt.voltac.events.packets.PacketPlayerAbilities;
import com.volt.voltac.events.packets.PacketWorldBorder;
import com.volt.voltac.manager.init.start.SuperDebug;
import com.volt.voltac.player.VoltPlayer;
import com.volt.voltac.predictionengine.GhostBlockDetector;
import com.volt.voltac.predictionengine.SneakingEstimator;
import com.volt.voltac.utils.anticheat.update.*;
import com.volt.voltac.utils.latency.CompensatedCooldown;
import com.volt.voltac.utils.latency.CompensatedFireworks;
import com.volt.voltac.utils.latency.CompensatedInventory;
import com.volt.voltac.utils.team.TeamHandler;
import com.github.retrooper.packetevents.event.PacketReceiveEvent;
import com.github.retrooper.packetevents.event.PacketSendEvent;
import com.google.common.collect.ClassToInstanceMap;
import com.google.common.collect.ImmutableClassToInstanceMap;
import org.bukkit.Bukkit;
import org.bukkit.permissions.Permission;
import org.bukkit.permissions.PermissionDefault;

public class CheckManager {
    private static boolean inited;

    ClassToInstanceMap<PacketCheck> packetChecks;
    ClassToInstanceMap<PositionCheck> positionCheck;
    ClassToInstanceMap<RotationCheck> rotationCheck;
    ClassToInstanceMap<VehicleCheck> vehicleCheck;
    ClassToInstanceMap<PacketCheck> prePredictionChecks;

    ClassToInstanceMap<BlockPlaceCheck> blockPlaceCheck;
    ClassToInstanceMap<PostPredictionCheck> postPredictionCheck;

    public ClassToInstanceMap<AbstractCheck> allChecks;

    public CheckManager(VoltPlayer player) {
        // Include post checks in the packet check too
        packetChecks = new ImmutableClassToInstanceMap.Builder<PacketCheck>()
                .put(Reach.class, new Reach(player))
                .put(PacketEntityReplication.class, new PacketEntityReplication(player))
                .put(PacketChangeGameState.class, new PacketChangeGameState(player))
                .put(CompensatedInventory.class, new CompensatedInventory(player))
                .put(PacketPlayerAbilities.class, new PacketPlayerAbilities(player))
                .put(PacketWorldBorder.class, new PacketWorldBorder(player))
                .put(ActionManager.class, player.actionManager)
                .put(TeamHandler.class, new TeamHandler(player))
                .put(ClientBrand.class, new ClientBrand(player))
                .put(NoFallA.class, new NoFallA(player))
                .put(BadPacketsO.class, new BadPacketsO(player))
                .put(BadPacketsA.class, new BadPacketsA(player))
                .put(BadPacketsB.class, new BadPacketsB(player))
                .put(BadPacketsC.class, new BadPacketsC(player))
                .put(BadPacketsD.class, new BadPacketsD(player))
                .put(BadPacketsE.class, new BadPacketsE(player))
                .put(BadPacketsF.class, new BadPacketsF(player))
                .put(BadPacketsG.class, new BadPacketsG(player))
                .put(BadPacketsH.class, new BadPacketsH(player))
                .put(BadPacketsI.class, new BadPacketsI(player))
                .put(BadPacketsJ.class, new BadPacketsJ(player))
                .put(BadPacketsK.class, new BadPacketsK(player))
                .put(BadPacketsL.class, new BadPacketsL(player))
                .put(BadPacketsM.class, new BadPacketsM(player))
                .put(BadPacketsN.class, new BadPacketsN(player))
                .put(BadPacketsP.class, new BadPacketsP(player))
                .put(BadPacketsQ.class, new BadPacketsQ(player))
                .put(BadPacketsR.class, new BadPacketsR(player))
                .put(BadPacketsS.class, new BadPacketsS(player))
                .put(BadPacketsT.class, new BadPacketsT(player))
                .put(BadPacketsU.class, new BadPacketsU(player))
                .put(BadPacketsV.class, new BadPacketsV(player))
                .put(BadPacketsW.class, new BadPacketsW(player))
                .put(BadPacketsX.class, new BadPacketsX(player))
                .put(BadPacketsY.class, new BadPacketsY(player))
                .put(BadPacketsZ.class, new BadPacketsZ(player))
                .put(FastBreak.class, new FastBreak(player))
                .put(TransactionOrder.class, new TransactionOrder(player))
                .put(NoSlowB.class, new NoSlowB(player))
                .put(SetbackBlocker.class, new SetbackBlocker(player)) // Must be last class otherwise we can't check while blocking packets
                .build();
        positionCheck = new ImmutableClassToInstanceMap.Builder<PositionCheck>()
                .put(PredictionRunner.class, new PredictionRunner(player))
                .put(CompensatedCooldown.class, new CompensatedCooldown(player))
                .build();
        rotationCheck = new ImmutableClassToInstanceMap.Builder<RotationCheck>()
                .put(AimProcessor.class, new AimProcessor(player))
                .put(AimModulo360.class, new AimModulo360(player))
                .put(AimDuplicateLook.class, new AimDuplicateLook(player))
//                .put(Baritone.class, new Baritone(player))
                .build();
        vehicleCheck = new ImmutableClassToInstanceMap.Builder<VehicleCheck>()
                .put(VehiclePredictionRunner.class, new VehiclePredictionRunner(player))
                .build();

        postPredictionCheck = new ImmutableClassToInstanceMap.Builder<PostPredictionCheck>()
                .put(NegativeTimerCheck.class, new NegativeTimerCheck(player))
                .put(ExplosionHandler.class, new ExplosionHandler(player))
                .put(KnockbackHandler.class, new KnockbackHandler(player))
                .put(GhostBlockDetector.class, new GhostBlockDetector(player))
                .put(Phase.class, new Phase(player))
                .put(PostCheck.class, new PostCheck(player))
                .put(NoFallB.class, new NoFallB(player))
                .put(OffsetHandler.class, new OffsetHandler(player))
                .put(SuperDebug.class, new SuperDebug(player))
                .put(DebugHandler.class, new DebugHandler(player))
                .put(EntityControl.class, new EntityControl(player))
                .put(NoSlowA.class, new NoSlowA(player))
                .put(NoSlowC.class, new NoSlowC(player))
                .put(NoSlowD.class, new NoSlowD(player))
                .put(NoSlowE.class, new NoSlowE(player))
                .put(SetbackTeleportUtil.class, new SetbackTeleportUtil(player)) // Avoid teleporting to new position, update safe pos last
                .put(CompensatedFireworks.class, player.compensatedFireworks)
                .put(SneakingEstimator.class, new SneakingEstimator(player))
                .put(LastInstanceManager.class, player.lastInstanceManager)
                .build();

        blockPlaceCheck = new ImmutableClassToInstanceMap.Builder<BlockPlaceCheck>()
                .put(InvalidPlaceA.class, new InvalidPlaceA(player))
                .put(InvalidPlaceB.class, new InvalidPlaceB(player))
                .put(AirLiquidPlace.class, new AirLiquidPlace(player))
                .put(MultiPlace.class, new MultiPlace(player))
                .put(FarPlace.class, new FarPlace(player))
                .put(FabricatedPlace.class, new FabricatedPlace(player))
                .put(PositionPlace.class, new PositionPlace(player))
                .put(RotationPlace.class, new RotationPlace(player))
                .put(DuplicateRotPlace.class, new DuplicateRotPlace(player))
                .put(GhostBlockMitigation.class, new GhostBlockMitigation(player))
                .build();

        prePredictionChecks = new ImmutableClassToInstanceMap.Builder<PacketCheck>()
                .put(TimerCheck.class, new TimerCheck(player))
                .put(CrashA.class, new CrashA(player))
                .put(CrashB.class, new CrashB(player))
                .put(CrashC.class, new CrashC(player))
                .put(CrashD.class, new CrashD(player))
                .put(CrashE.class, new CrashE(player))
                .put(CrashF.class, new CrashF(player))
                .put(CrashG.class, new CrashG(player))
                .put(CrashH.class, new CrashH(player))
                .put(ExploitA.class, new ExploitA(player))
                .put(ExploitB.class, new ExploitB(player))
                .put(VehicleTimer.class, new VehicleTimer(player))
                .build();

        allChecks = new ImmutableClassToInstanceMap.Builder<AbstractCheck>()
                .putAll(packetChecks)
                .putAll(positionCheck)
                .putAll(rotationCheck)
                .putAll(vehicleCheck)
                .putAll(postPredictionCheck)
                .putAll(blockPlaceCheck)
                .putAll(prePredictionChecks)
                .build();

        init();
    }

    @SuppressWarnings("unchecked")
    public <T extends PositionCheck> T getPositionCheck(Class<T> check) {
        return (T) positionCheck.get(check);
    }

    @SuppressWarnings("unchecked")
    public <T extends RotationCheck> T getRotationCheck(Class<T> check) {
        return (T) rotationCheck.get(check);
    }

    @SuppressWarnings("unchecked")
    public <T extends VehicleCheck> T getVehicleCheck(Class<T> check) {
        return (T) vehicleCheck.get(check);
    }

    public void onPrePredictionReceivePacket(final PacketReceiveEvent packet) {
        for (PacketCheck check : prePredictionChecks.values()) {
            check.onPacketReceive(packet);
        }
    }

    public void onPacketReceive(final PacketReceiveEvent packet) {
        for (PacketCheck check : packetChecks.values()) {
            check.onPacketReceive(packet);
        }
        for (PostPredictionCheck check : postPredictionCheck.values()) {
            check.onPacketReceive(packet);
        }
        for (BlockPlaceCheck check : blockPlaceCheck.values()) {
            check.onPacketReceive(packet);
        }
    }

    public void onPacketSend(final PacketSendEvent packet) {
        for (PacketCheck check : prePredictionChecks.values()) {
            check.onPacketSend(packet);
        }
        for (PacketCheck check : packetChecks.values()) {
            check.onPacketSend(packet);
        }
        for (PostPredictionCheck check : postPredictionCheck.values()) {
            check.onPacketSend(packet);
        }
        for (BlockPlaceCheck check : blockPlaceCheck.values()) {
            check.onPacketSend(packet);
        }
    }

    public void onPositionUpdate(final PositionUpdate position) {
        for (PositionCheck check : positionCheck.values()) {
            check.onPositionUpdate(position);
        }
    }

    public void onRotationUpdate(final RotationUpdate rotation) {
        for (RotationCheck check : rotationCheck.values()) {
            check.process(rotation);
        }
        for (BlockPlaceCheck check : blockPlaceCheck.values()) {
            check.process(rotation);
        }
    }

    public void onVehiclePositionUpdate(final VehiclePositionUpdate update) {
        for (VehicleCheck check : vehicleCheck.values()) {
            check.process(update);
        }
    }

    public void onPredictionFinish(final PredictionComplete complete) {
        for (PostPredictionCheck check : postPredictionCheck.values()) {
            check.onPredictionComplete(complete);
        }
        for (BlockPlaceCheck check : blockPlaceCheck.values()) {
            check.onPredictionComplete(complete);
        }
    }

    public void onBlockPlace(final BlockPlace place) {
        for (BlockPlaceCheck check : blockPlaceCheck.values()) {
            check.onBlockPlace(place);
        }
    }

    public void onPostFlyingBlockPlace(final BlockPlace place) {
        for (BlockPlaceCheck check : blockPlaceCheck.values()) {
            check.onPostFlyingBlockPlace(place);
        }
    }

    public ExplosionHandler getExplosionHandler() {
        return getPostPredictionCheck(ExplosionHandler.class);
    }

    @SuppressWarnings("unchecked")
    public <T extends PacketCheck> T getPacketCheck(Class<T> check) {
        return (T) packetChecks.get(check);
    }

    @SuppressWarnings("unchecked")
    public <T extends PacketCheck> T getPrePredictionCheck(Class<T> check) {
        return (T) prePredictionChecks.get(check);
    }

    private PacketEntityReplication packetEntityReplication = null;

    public PacketEntityReplication getEntityReplication() {
        if (packetEntityReplication == null) packetEntityReplication = getPacketCheck(PacketEntityReplication.class);
        return packetEntityReplication;
    }

    public NoFallA getNoFall() {
        return getPacketCheck(NoFallA.class);
    }

    private CompensatedInventory inventory = null;

    public CompensatedInventory getInventory() {
        if (inventory == null) inventory = getPacketCheck(CompensatedInventory.class);
        return inventory;
    }

    public KnockbackHandler getKnockbackHandler() {
        return getPostPredictionCheck(KnockbackHandler.class);
    }

    public CompensatedCooldown getCompensatedCooldown() {
        return getPositionCheck(CompensatedCooldown.class);
    }

    public NoSlowA getNoSlow() {
        return getPostPredictionCheck(NoSlowA.class);
    }

    public SetbackTeleportUtil getSetbackUtil() {
        return getPostPredictionCheck(SetbackTeleportUtil.class);
    }

    public DebugHandler getDebugHandler() {
        return getPostPredictionCheck(DebugHandler.class);
    }

    public OffsetHandler getOffsetHandler() {
        return getPostPredictionCheck(OffsetHandler.class);
    }

    @SuppressWarnings("unchecked")
    public <T extends PostPredictionCheck> T getPostPredictionCheck(Class<T> check) {
        return (T) postPredictionCheck.get(check);
    }

    private void init() {
        if (inited) return;
        for (AbstractCheck check : allChecks.values()) {
            if (check.getCheckName() != null) {
                String permissionName = "grim.exempt." + check.getCheckName().toLowerCase();
                Permission permission = Bukkit.getPluginManager().getPermission(permissionName);

                if (permission == null) {
                    Bukkit.getPluginManager().addPermission(new Permission(permissionName, PermissionDefault.FALSE));
                } else {
                    permission.setDefault(PermissionDefault.FALSE);
                }
            }
        }

        inited = true;
    }
}
