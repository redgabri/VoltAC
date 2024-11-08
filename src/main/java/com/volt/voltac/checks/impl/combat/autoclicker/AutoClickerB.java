package com.volt.voltac.checks.impl.combat.autoclicker;

import ac.grim.grimac.api.config.ConfigManager;
import com.volt.voltac.checks.Check;
import com.volt.voltac.checks.CheckData;
import com.volt.voltac.checks.type.PacketCheck;
import com.volt.voltac.player.VoltPlayer;
import com.github.retrooper.packetevents.event.PacketReceiveEvent;
import com.github.retrooper.packetevents.protocol.packettype.PacketType;
import com.github.retrooper.packetevents.wrapper.play.client.WrapperPlayClientInteractEntity;
import com.volt.voltac.utils.anticheat.click.ClickUtils;

import java.util.ArrayDeque;
import java.util.Deque;
import java.util.LinkedList;
import java.util.Queue;


@CheckData(name = "AutoClickerB", configName = "AutoClicker", setback = 10, experimental = true)
public class AutoClickerB extends Check implements PacketCheck {
    private final Deque<Long> clickTimeDifferences = new ArrayDeque<>();
    private final double MAX_CLICK_DIFFERENCE = 100_000_000; //This is used so this doesn't flag when a player stop clicking for some time
    private double maxClickTimeDeltaVariance = 100000; // maximum allowed variance in click times
    private int minClicksToTrack = 10; // minimum number of clicks to track

    public AutoClickerB(VoltPlayer player) {
        super(player);
    }

    @Override
    public void onPacketReceive(PacketReceiveEvent event) {
        if (event.getPacketType() == PacketType.Play.Client.INTERACT_ENTITY) {
            WrapperPlayClientInteractEntity interactPacket = new WrapperPlayClientInteractEntity(event);

            if (interactPacket.getAction() == WrapperPlayClientInteractEntity.InteractAction.ATTACK) {
                long currentTime = System.currentTimeMillis();

                clickTimeDifferences.add(currentTime);

                if (clickTimeDifferences.size() >= minClicksToTrack) {
                    // Remove the oldest timestamp to keep deque within the minClicksToTrack size
                    clickTimeDifferences.removeFirst();

                    double variance = ClickUtils.getVariance(clickTimeDifferences);

                    if (variance > maxClickTimeDeltaVariance) {
                        flagAndAlert("Too high variance. (Variance: " + Math.round(variance / 1000) + ")");
                    }
                }
            }
        }
    }

    @Override
    public void onReload(ConfigManager config) {
        this.maxClickTimeDeltaVariance = config.getDoubleElse("AutoClicker.B.max-click-time-delta-variance", 100000);
        this.minClicksToTrack = config.getIntElse("AutoClicker.B.min-clicks-to-track", 10);
    }
}