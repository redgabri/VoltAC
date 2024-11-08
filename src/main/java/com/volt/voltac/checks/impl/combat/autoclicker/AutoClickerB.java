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
    private final Deque<Long> clickTime = new ArrayDeque<>();
    private final Deque<Double> variations = new ArrayDeque<>();
    private int flagIndex = 0;
    private int minClicksToTrack = 10;
    private double maxVarianceDifference = 7500;

    public AutoClickerB(VoltPlayer player) {
        super(player);
    }

    @Override
    public void onPacketReceive(PacketReceiveEvent event) {
        if (event.getPacketType() == PacketType.Play.Client.INTERACT_ENTITY) {
            WrapperPlayClientInteractEntity interactPacket = new WrapperPlayClientInteractEntity(event);

            if (interactPacket.getAction() == WrapperPlayClientInteractEntity.InteractAction.ATTACK) {
                long currentTime = System.currentTimeMillis();

                clickTime.add(currentTime);

                if (clickTime.size() >= minClicksToTrack) {
                    clickTime.removeFirst();

                    double variance = ClickUtils.getVariance(clickTime);
                    variations.add(variance);

                    if (variations.size() < 10) return;

                    boolean allVariationsSimilar = variations.stream()
                            .allMatch(v -> Math.abs(v - variance) <= maxVarianceDifference);

                    if (allVariationsSimilar) {
                        flagIndex++;
                        if (flagIndex >= 3) {
                            flagAndAlert("Highly consistent click timing pattern detected. (Variance: " + Math.round(variance / 1000) + ")");
                            flagIndex = 0;
                        }
                    } else {
                        flagIndex = 0;
                    }

                    variations.removeFirst();
                }
            }
        }
    }

    @Override
    public void onReload(ConfigManager config) {
        this.minClicksToTrack = config.getIntElse("AutoClicker.B.min-clicks-to-track", 10);
        this.maxVarianceDifference = config.getDoubleElse("AutoClicker.B.max-variance-difference", 7500);
    }
}
