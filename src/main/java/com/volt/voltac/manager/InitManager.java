package com.volt.voltac.manager;

import com.volt.voltac.VoltAPI;
import com.volt.voltac.manager.init.Initable;
import com.volt.voltac.manager.init.load.PacketEventsInit;
import com.volt.voltac.manager.init.start.*;
import com.volt.voltac.manager.init.stop.TerminatePacketEvents;
import com.google.common.collect.ImmutableList;
import lombok.Getter;

public class InitManager {

    private final ImmutableList<Initable> initializersOnLoad;
    private final ImmutableList<Initable> initializersOnStart;
    private final ImmutableList<Initable> initializersOnStop;

    @Getter private boolean loaded = false;
    @Getter private boolean started = false;
    @Getter private boolean stopped = false;

    public InitManager() {
        initializersOnLoad = ImmutableList.<Initable>builder()
                .add(new PacketEventsInit())
                .add(() -> VoltAPI.INSTANCE.getExternalAPI().load())
                .build();

        initializersOnStart = ImmutableList.<Initable>builder()
                .add(VoltAPI.INSTANCE.getExternalAPI())
                .add(new ExemptOnlinePlayers())
                .add(new EventManager())
                .add(new PacketManager())
                .add(new ViaBackwardsManager())
                .add(new TickRunner())
                .add(new TickEndEvent())
                .add(new CommandRegister())
                .add(new BStats())
                .add(new PacketLimiter())
                .add(VoltAPI.INSTANCE.getDiscordManager())
                .add(VoltAPI.INSTANCE.getSpectateManager())
                .add(new JavaVersion())
                .add(new ViaVersion())
                .build();

        initializersOnStop = ImmutableList.<Initable>builder()
                .add(new TerminatePacketEvents())
                .build();
    }

    public void load() {
        for (Initable initable : initializersOnLoad) handle(initable);
        loaded = true;
    }

    public void start() {
        for (Initable initable : initializersOnStart) handle(initable);
        started = true;
    }

    public void stop() {
        for (Initable initable : initializersOnStop) handle(initable);
        stopped = true;
    }

    private void handle(Initable initable) {
        try {
            initable.start();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}