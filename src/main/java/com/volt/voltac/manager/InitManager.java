package com.volt.voltac.manager;

import com.volt.voltac.GrimAPI;
import com.volt.voltac.manager.init.Initable;
import com.volt.voltac.manager.init.load.PacketEventsInit;
import ac.grim.grimac.manager.init.start.*;
import ac.grim.volt.manager.init.start.*;
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
                .add(() -> GrimAPI.INSTANCE.getExternalAPI().load())
                .build();

        initializersOnStart = ImmutableList.<Initable>builder()
                .add(GrimAPI.INSTANCE.getExternalAPI())
                .add(new ExemptOnlinePlayers())
                .add(new EventManager())
                .add(new PacketManager())
                .add(new ViaBackwardsManager())
                .add(new TickRunner())
                .add(new TickEndEvent())
                .add(new CommandRegister())
                .add(new BStats())
                .add(new PacketLimiter())
                .add(GrimAPI.INSTANCE.getDiscordManager())
                .add(GrimAPI.INSTANCE.getSpectateManager())
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