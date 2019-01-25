package com.jme3.recast4j.Detour.Crowd;

import com.jme3.math.FastMath;
import com.jme3.math.Quaternion;
import com.jme3.math.Vector3f;
import org.recast4j.detour.crowd.CrowdAgent;

/**
 * The Circle Formation Handler moves CrowdAgents on a circle around the target in evenly spaced portions.<br />
 * It does not prefer "back facing" angles, so when you have 2 agents one would be in front and one in back of the target
 * (where "in front" is hard to tell, it means the world global z axis actually).<br />
 * Note that this code does not like dynamic changes of the crowdSize, because other agents would have to be re-arranged.
 */
public class CircleFormationHandler implements FormationHandler {
    protected int numAgents;
    protected Crowd crowd;
    protected Vector3f target;
    protected float radius;
    protected int filledSlotIdx;
    protected float angle;

    public CircleFormationHandler(int maxAgents, Crowd crowd, float radius) {
        this.numAgents = maxAgents;
        this.crowd = crowd;
        this.radius = radius;
    }

    @Override
    public void setTargetPosition(Vector3f targetPosition) {
        this.target = targetPosition;
        rebuildFormation();
    }

    protected void rebuildFormation() {
        // This only works when no one is yet part of the formation!
        filledSlotIdx = 0;
        angle = FastMath.TWO_PI / numAgents;
    }

    @Override
    public void moveIntoFormation(CrowdAgent crowdAgent) {
        //@TODO: Assumption here is, that crowdAgent is not yet part of our formation
        filledSlotIdx++;
        Quaternion q = new Quaternion();
        q.fromAngleAxis(angle * filledSlotIdx, Vector3f.UNIT_Y);
        boolean b = crowd.requestMoveToTarget(crowdAgent,
            target.add(
                // Offset Vector: Rotate and scale to the radius
                q.mult(Vector3f.UNIT_Z).mult(radius)
            )
        );

        if (!b) {
            // @TODO: Consider throwing an exception
        }
    }
}
