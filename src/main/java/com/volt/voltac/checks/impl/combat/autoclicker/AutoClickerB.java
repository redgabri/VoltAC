package com.volt.voltac.checks.impl.combat.autoclicker;

import ac.grim.grimac.api.config.ConfigManager;
import com.volt.voltac.checks.Check;
import com.volt.voltac.checks.CheckData;
import com.volt.voltac.checks.type.PacketCheck;
import com.volt.voltac.player.VoltPlayer;
import com.github.retrooper.packetevents.event.PacketReceiveEvent;
import com.github.retrooper.packetevents.protocol.packettype.PacketType;
import com.github.retrooper.packetevents.wrapper.play.client.WrapperPlayClientInteractEntity;

import java.util.LinkedList;
import java.util.Queue;

//Experimental check
@CheckData(name = "AutoClickerB", configName = "AutoClicker", setback = 10, experimental = true)
public class AutoClickerB extends Check implements PacketCheck {
    private final Queue<Long> clickTimes = new LinkedList<>();
    private int flagCount = 0;
    private double maxClickTimeDelta = 50; // maximum allowed variance in click times (in milliseconds)

    public AutoClickerB(VoltPlayer player) {
        super(player);
    }

    @Override
    public void onPacketReceive(PacketReceiveEvent event) {
        if (event.getPacketType() == PacketType.Play.Client.INTERACT_ENTITY) {
            WrapperPlayClientInteractEntity interactPacket = new WrapperPlayClientInteractEntity(event);

            // Check if the player is attacking an entity
            if (interactPacket.getAction() == WrapperPlayClientInteractEntity.InteractAction.ATTACK) {
                long currentTime = System.currentTimeMillis();
                clickTimes.add(currentTime);

                // Remove old click times from the queue
                while (!clickTimes.isEmpty() && currentTime - clickTimes.peek() > 1000) {
                    clickTimes.poll();
                }

                // Only check the click pattern if the player is clicking more than 7 CPS
                if (clickTimes.size() > 7) {
                    // Check the variance in click times
                    if (clickTimes.size() >= 2) {
                        long timeDelta = clickTimes.toArray(new Long[0])[clickTimes.size() - 1] - clickTimes.toArray(new Long[0])[clickTimes.size() - 2];
                        if (Math.abs(timeDelta - clickTimes.toArray(new Long[0])[clickTimes.size() - 2] + clickTimes.toArray(new Long[0])[clickTimes.size() - 3]) < maxClickTimeDelta) {
                            flagCount++;
                            if (flagCount >= 5) {
                                flagAndAlert("Suspicious click timing pattern detected.");
                                flagCount = 0;
                            }
                        } else {
                            flagCount = 0;
                        }
                    }
                }
            }
        }
    }

    @Override
    public void onReload(ConfigManager config) {
        this.maxClickTimeDelta = config.getDoubleElse("AutoClicker.max-click-time-delta", 50);
    }
}