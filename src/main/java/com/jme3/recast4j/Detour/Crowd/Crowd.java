package com.jme3.recast4j.Detour.Crowd;

import com.jme3.bullet.control.BetterCharacterControl;
import com.jme3.math.Quaternion;
import com.jme3.math.Vector3f;
import com.jme3.recast4j.Detour.BetterDefaultQueryFilter;
import com.jme3.recast4j.Detour.DetourUtils;
import com.jme3.scene.Spatial;
import org.recast4j.detour.*;
import org.recast4j.detour.crowd.CrowdAgent;
import org.recast4j.detour.crowd.CrowdAgentParams;
import org.recast4j.detour.crowd.debug.CrowdAgentDebugInfo;

import java.lang.reflect.Field;
import java.util.function.IntFunction;

/**
 * TODO: Javadoc
 * Important to know: Filters have to be done using the queryFilterFactory. If none is specified, the first filter
 * (index 0) will be the {@link BetterDefaultQueryFilter} and all others will be filled with {@link DefaultQueryFilter}s
 */
public class Crowd extends org.recast4j.detour.crowd.Crowd {
    protected boolean debug;
    protected CrowdAgentDebugInfo debugInfo;
    protected MovementApplicationType applicationType;
    protected ApplyFunction applyFunction;
    protected Spatial[] spatialMap;
    protected TargetProximityDetector proximityDetector;
    protected FormationHandler formationHandler;
    protected NavMeshQuery m_navquery;
    protected boolean[] formationInProgress;

    public Crowd(MovementApplicationType applicationType, int maxAgents, float maxAgentRadius, NavMesh nav)
            throws NoSuchFieldException, IllegalAccessException {
        this(applicationType, maxAgents, maxAgentRadius, nav,
                i -> (i == 0 ? new BetterDefaultQueryFilter() : new DefaultQueryFilter())
        );
    }

    public Crowd(MovementApplicationType applicationType, int maxAgents, float maxAgentRadius, NavMesh nav,
                 IntFunction<QueryFilter> queryFilterFactory) throws NoSuchFieldException, IllegalAccessException {
        super(maxAgents, maxAgentRadius, nav, queryFilterFactory);
        this.applicationType = applicationType;
        spatialMap = new Spatial[maxAgents];
        proximityDetector = new SimpleTargetProximityDetector(1f);
        formationHandler = new CircleFormationHandler(maxAgents, this, 1f);
        formationInProgress = new boolean[maxAgents];

        Field f = getClass().getSuperclass().getDeclaredField("m_navquery");
        f.setAccessible(true);
        m_navquery = (NavMeshQuery)f.get(this);
    }

    public void update(float deltaTime) {
        if (debug) {
            debugInfo = new CrowdAgentDebugInfo(); // Clear.
            update(deltaTime, debugInfo);
        } else {
            update(deltaTime, null);
        }
    }

    @Override
    public CrowdAgent getAgent(int idx) {
        CrowdAgent ca = super.getAgent(idx);
        if (ca == null) {
            throw new IndexOutOfBoundsException("Invalid Index");
        }

        return ca;
    }

    public CrowdAgent createAgent(Vector3f pos, CrowdAgentParams params) {
        int idx = addAgent(DetourUtils.toFloatArray(pos), params);
        if (idx == -1) {
            throw new IndexOutOfBoundsException("This crowd doesn't have a free slot anymore.");
        }
        return super.getAgent(idx);
    }

    /**
     * Call this method to update the internal data storage of spatials.
     * This is required for some {@link MovementApplicationType}s.
     * @param agent The Agent
     * @param spatial The Agent's Spatial
     */
    public void setSpatialForAgent(CrowdAgent agent, Spatial spatial) {
        spatialMap[agent.idx] = spatial;
    }

    /**
     * Remove the Agent from this Crowd (Convenience Wrapper around {@link #removeAgent(int)})
     * @param agent The Agent to remove from the crowd
     */
    public void removeAgent(CrowdAgent agent) {
        if (agent.idx != -1) {
            removeAgent(agent.idx);
        }
    }

    /**
     * Makes the whole Crowd move to a target. Know that you can also move individual agents.
     * @param to The Move Target
     * @param polyRef The Polygon to which the target belongs
     * @return Whether all agents could be scheduled to approach the target
     */
    public boolean requestMoveToTarget(Vector3f to, long polyRef) {
        if (polyRef == 0 || to == null) {
            throw new IllegalArgumentException("Invalid Target (" + to + ", " + polyRef + ")");
        }

        formationHandler.setTargetPosition(to);

        // Unfortunately ag.setTarget is not an exposed API, maybe we'll write a dispatcher class if that bugs me too much
        // Why? That way we could throw Exceptions when the index is wrong (IndexOutOfBoundsEx)
        return getActiveAgents().stream()
            .allMatch(ca -> requestMoveTarget(ca.idx, polyRef, DetourUtils.toFloatArray(to)));
        // if all were successful, return true, else return false.
    }

    /**
     * This method is called by the CrowdManager to move the agents on the screen.
     */
    protected void applyMovements() {
        getActiveAgents().forEach(ca -> applyMovement(ca, DetourUtils.createVector3f(ca.npos),
                DetourUtils.createVector3f(ca.vel)));
    }

    protected void applyMovement(CrowdAgent crowdAgent, Vector3f newPos, Vector3f velocity) {
        switch (applicationType) {
            case NONE:
                break;

            case CUSTOM:
                applyFunction.applyMovement(crowdAgent, newPos, velocity);
                break;

            case DIRECT:
                // Debug Code to handle "approaching behavior"
                System.out.println("" + Boolean.toString(crowdAgent.targetRef != 0) + " speed: " + velocity.length() + " newPos: " + newPos + " velocity: " + velocity);
                if (velocity.length() > 0.1f) {
                    Quaternion rotation = new Quaternion();
                    rotation.lookAt(velocity.normalize(), Vector3f.UNIT_Y);
                    spatialMap[crowdAgent.idx].setLocalTranslation(newPos);
                    spatialMap[crowdAgent.idx].setLocalRotation(rotation);
                }
                break;

            case BETTER_CHARACTER_CONTROL:
                BetterCharacterControl bcc = spatialMap[crowdAgent.idx].getControl(BetterCharacterControl.class);
                bcc.setWalkDirection(velocity);
                bcc.setViewDirection(velocity.normalize());
                break;

            default:
                throw new IllegalArgumentException("Unknown Application Type");
        }

        // alternative: crowdAgent.targetPos != formationHandler.getTargetPosition(). But better not rely on these impls
        // and what if formation's targetPosition might change?
        if (!formationInProgress[crowdAgent.idx]) {
            //@TODO: Instead of targetRef, expose targetState [which means create a WrapperClass soon]
            if (crowdAgent.targetRef != 0 && proximityDetector.isInTargetProximity(crowdAgent, newPos,
                    DetourUtils.createVector3f(crowdAgent.targetPos))) {
                // Handle Crowd Agent in proximity.
                resetMoveTarget(crowdAgent.idx); // Make him stop moving.
                formationHandler.moveIntoFormation(crowdAgent);
                formationInProgress[crowdAgent.idx] = true;
            }
        } else {
            /*
             @TODO: crowdAgent.targetPos... Unreliable, crowd should track them, together in one variable
             with formationInProgress. Maybe also as part of the formationHandler!
            */
            if (SimpleTargetProximityDetector.euclideanDistanceSquared(newPos,
                    DetourUtils.createVector3f(crowdAgent.targetPos)) < 0.1f * 0.1f) {
                formationInProgress[crowdAgent.idx] = false;
                resetMoveTarget(crowdAgent.idx);
                System.out.println("Reached Target");
            }
        }
    }

    public void setApplicationType(MovementApplicationType applicationType) {
        this.applicationType = applicationType;
    }

    public void setCustomApplyFunction(ApplyFunction applyFunction) {
        this.applyFunction = applyFunction;
    }

    /**
     * Moves a specified Agent to a Location.<br />
     * This code implicitly searches for the correct polygon with a constant tolerance, in most cases you should prefer
     * to determine the poly ref manually with domain specific knowledge.
     * @see #requestMoveToTarget(CrowdAgent, long, Vector3f)
     * @param crowdAgent the agent to move
     * @param to where the agent shall move to
     * @return whether this operation was successful
     */
    public boolean requestMoveToTarget(CrowdAgent crowdAgent, Vector3f to) {
        FindNearestPolyResult res = m_navquery.findNearestPoly(DetourUtils.toFloatArray(to), new float[]{0.5f, 0.5f, 0.5f}, new BetterDefaultQueryFilter());
        if (res.getNearestRef() != -1) {
            return requestMoveToTarget(crowdAgent, res.getNearestRef(), to);
        } else {
            return false;
        }
    }

    /**
     * Moves a specified Agent to a Location.
     * @param crowdAgent the agent to move
     * @param polyRef The Polygon where the position resides
     * @param to where the agent shall move to
     * @return whether this operation was successful
     */
    public boolean requestMoveToTarget(CrowdAgent crowdAgent, long polyRef, Vector3f to) {
        return requestMoveTarget(crowdAgent.idx, polyRef, DetourUtils.toFloatArray(to));
    }
}
