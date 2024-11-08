package com.volt.voltac.checks.impl.combat.autoclicker;

import ac.grim.grimac.api.config.ConfigManager;
import com.volt.voltac.checks.Check;
import com.volt.voltac.checks.CheckData;
import com.volt.voltac.checks.type.PacketCheck;
import com.volt.voltac.player.VoltPlayer;
import com.github.retrooper.packetevents.event.PacketReceiveEvent;
import com.github.retrooper.packetevents.protocol.packettype.PacketType;
import com.github.retrooper.packetevents.wrapper.play.client.WrapperPlayClientInteractEntity;

import java.util.ArrayDeque;
import java.util.Deque;

@CheckData(name = "AutoClickerC", configName = "AutoClicker", setback = 10)
public class AutoClickerC extends Check implements PacketCheck {
    private final Deque<Long> clickTimeDifferences = new ArrayDeque<>();
    private double maxClickTimeDeltaVariance = 40000; // minimum allowed variance in click times
    private int minClicksToTrack = 10; // minimum number of clicks to track

    public AutoClickerC(VoltPlayer player) {
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

                    // Calculate time differences between consecutive timestamps
                    long previousTime = -1;
                    double sumDifferences = 0.0;
                    double maxDifference = 0.0;
                    double minDifference = Double.MAX_VALUE;

                    for (long clickTime : clickTimeDifferences) {
                        if (previousTime != -1) {
                            long diff = clickTime - previousTime;
                            sumDifferences += diff;
                            maxDifference = Math.max(maxDifference, diff);
                            minDifference = Math.min(minDifference, diff);
                        }
                        previousTime = clickTime;
                    }

                    // Calculate the variance in click time differences
                    double mean = sumDifferences / (clickTimeDifferences.size() - 1);
                    double variance = 0.0;
                    for (long clickTime : clickTimeDifferences) {
                        if (previousTime != -1) {
                            long diff = clickTime - previousTime;
                            variance += Math.pow(diff - mean, 2);
                        }
                        previousTime = clickTime;
                    }
                    variance /= (clickTimeDifferences.size() - 1);

                    System.out.println(variance);

                    if (variance < maxClickTimeDeltaVariance) {
                        flagAndAlert("Highly consistent click timing pattern detected. (Variance: " + Math.round(variance / 1000) + " )" + (variance < 38500 ? "(lcoc)" : ""));
                    }
                }
            }
        }
    }

    @Override
    public void onReload(ConfigManager config) {
        this.maxClickTimeDeltaVariance = config.getDoubleElse("AutoClicker.max-click-time-delta-variance", 40000);
        this.minClicksToTrack = config.getIntElse("AutoClicker.min-clicks-to-track", 10);
    }
}