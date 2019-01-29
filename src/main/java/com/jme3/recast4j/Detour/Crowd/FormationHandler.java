package com.jme3.recast4j.Detour.Crowd;

import com.jme3.math.Vector3f;
import org.recast4j.detour.crowd.CrowdAgent;

public interface FormationHandler {
    void setTargetPosition(Vector3f targetPosition);

    /**
     * This is called when an Agent is close enough to it's target and should start building a formation at the
     * previously set target.<br />
     *
     * @param crowdAgent The Agent whose target shall be set
     * @return the formation position
     */
    Vector3f moveIntoFormation(CrowdAgent crowdAgent);
}
