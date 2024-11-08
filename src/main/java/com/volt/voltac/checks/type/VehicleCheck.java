package com.volt.voltac.checks.type;

import ac.grim.grimac.api.AbstractCheck;
import com.volt.voltac.utils.anticheat.update.VehiclePositionUpdate;

public interface VehicleCheck extends AbstractCheck {

    void process(final VehiclePositionUpdate vehicleUpdate);
}
