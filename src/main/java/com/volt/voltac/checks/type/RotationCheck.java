package com.volt.voltac.checks.type;

import ac.grim.grimac.api.AbstractCheck;
import com.volt.voltac.utils.anticheat.update.RotationUpdate;

public interface RotationCheck extends AbstractCheck {

    default void process(final RotationUpdate rotationUpdate) {
    }
}
