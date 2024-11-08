package com.volt.voltac.checks.type;

import ac.grim.grimac.api.AbstractCheck;
import com.volt.voltac.utils.anticheat.update.PositionUpdate;

public interface PositionCheck extends AbstractCheck {

    default void onPositionUpdate(final PositionUpdate positionUpdate) {
    }
}
