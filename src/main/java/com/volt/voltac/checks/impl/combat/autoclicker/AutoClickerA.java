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

//Check CPS cap
@CheckData(name = "AutoClickerA", configName = "AutoClicker", setback = 10)
public class AutoClickerA extends Check implements PacketCheck {
    private final Queue<Long> clickTimes = new LinkedList<>();
    private double maxCps = 30;

    public AutoClickerA(VoltPlayer player) {
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

                // Check if the player is clicking too fast
                if (clickTimes.size() > maxCps) {
                    flagAndAlert(clickTimes.size()+" cps");
                }
            }
        }
    }

    @Override
    public void onReload(ConfigManager config) {
        this.maxCps = config.getDoubleElse("AutoClicker.A.max-cps", 30);
    }
}