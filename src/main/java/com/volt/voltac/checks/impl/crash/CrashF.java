package com.volt.voltac.checks.impl.crash;

import com.volt.voltac.checks.Check;
import com.volt.voltac.checks.CheckData;
import com.volt.voltac.checks.type.PacketCheck;
import com.volt.voltac.player.GrimPlayer;
import com.github.retrooper.packetevents.event.PacketReceiveEvent;
import com.github.retrooper.packetevents.protocol.packettype.PacketType;
import com.github.retrooper.packetevents.wrapper.play.client.WrapperPlayClientClickWindow;

@CheckData(name = "CrashF")
public class CrashF extends Check implements PacketCheck {

    public CrashF(GrimPlayer playerData) {
        super(playerData);
    }

    @Override
    public void onPacketReceive(final PacketReceiveEvent event) {
        if (event.getPacketType() == PacketType.Play.Client.CLICK_WINDOW) {
            WrapperPlayClientClickWindow click = new WrapperPlayClientClickWindow(event);
            int clickType = click.getWindowClickType().ordinal();
            int button = click.getButton();
            int windowId = click.getWindowId();
            int slot = click.getSlot();

            if ((clickType == 1 || clickType == 2) && windowId >= 0 && button < 0) {
                if (flagAndAlert("clickType=" + clickType + " button=" + button)) {
                    event.setCancelled(true);
                    player.onPacketCancel();
                }
            }

            else if (windowId >= 0 && clickType == 2 && slot < 0) {
                if (flagAndAlert("clickType=" + clickType + " button=" + button + " slot=" + slot)) {
                    event.setCancelled(true);
                    player.onPacketCancel();
                }
            }

        }
    }

}
