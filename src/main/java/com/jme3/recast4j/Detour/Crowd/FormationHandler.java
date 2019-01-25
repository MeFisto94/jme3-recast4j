package com.jme3.recast4j.Detour.Crowd;

import com.jme3.math.Vector3f;

public interface FormationHandler {
    void setTargetPosition(Vector3f targetPosition);
    void moveIntoFormation(CrowdAgent crowdAgent);
}
