package com.volt.voltac.manager.init.start;

import com.volt.voltac.manager.init.Initable;

public class ViaBackwardsManager implements Initable {
    @Override
    public void start() {
        System.setProperty("com.viaversion.handlePingsAsInvAcknowledgements", "true");
    }
}
