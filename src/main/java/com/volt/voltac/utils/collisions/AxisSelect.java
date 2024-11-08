package com.volt.voltac.utils.collisions;

import com.volt.voltac.utils.collisions.datatypes.SimpleCollisionBox;

public interface AxisSelect {
    SimpleCollisionBox modify(SimpleCollisionBox box);
}