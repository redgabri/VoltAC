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

@CheckData(name = "AutoClickerB", configName = "AutoClicker", setback = 10)
public class AutoClickerB extends Check implements PacketCheck {
    private final Deque<Long> clickTimeDifferences = new ArrayDeque<>();
    private double minClickTimeDeltaVariance = 35555; // minimum allowed variance in click times
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

                    if (variance < minClickTimeDeltaVariance) {
                        flagAndAlert("Highly consistent click timing pattern detected. (Variance: " + Math.round(variance / 1000) + ")");
                    }
                }
            }
        }
    }

    @Override
    public void onReload(ConfigManager config) {
        this.minClickTimeDeltaVariance = config.getDoubleElse("AutoClicker.B.min-click-time-delta-variance", 35555);
        this.minClicksToTrack = config.getIntElse("AutoClicker.B.min-clicks-to-track", 10);
    }
}