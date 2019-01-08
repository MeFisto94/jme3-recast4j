package com.jme3.recast4j.Detour.Crowd;

import com.jme3.bullet.control.BetterCharacterControl;
import com.jme3.math.Vector3f;
import com.jme3.recast4j.Detour.BetterDefaultQueryFilter;
import com.jme3.recast4j.Detour.DetourUtils;
import com.jme3.scene.Spatial;
import org.recast4j.detour.DefaultQueryFilter;
import org.recast4j.detour.NavMesh;
import org.recast4j.detour.QueryFilter;
import org.recast4j.detour.crowd.CrowdAgent;
import org.recast4j.detour.crowd.CrowdAgentParams;
import org.recast4j.detour.crowd.debug.CrowdAgentDebugInfo;

import java.util.HashMap;
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

    public Crowd(MovementApplicationType applicationType, int maxAgents, float maxAgentRadius, NavMesh nav) {
        this(applicationType, maxAgents, maxAgentRadius, nav, i -> (i == 0 ? new BetterDefaultQueryFilter() : new DefaultQueryFilter()));
    }

    public Crowd(MovementApplicationType applicationType, int maxAgents, float maxAgentRadius, NavMesh nav, IntFunction<QueryFilter> queryFilterFactory) {
        super(maxAgents, maxAgentRadius, nav, queryFilterFactory);
        this.applicationType = applicationType;
        spatialMap = new Spatial[maxAgents];
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

    public void removeAgent(CrowdAgent agent) {
        if (agent.idx != -1) {
            removeAgent(agent.idx);
        }
    }

    public boolean requestMoveToTarget(Vector3f to, long polyRef) {
        if (polyRef == 0 || to == null) {
            throw new IllegalArgumentException("Invalid Target (" + to + ", " + polyRef + ")");
        }

        // Unfortunately ag.setTarget is not an exposed API, maybe we'll write a dispatcher class if that bugs me too much
        return getActiveAgents().stream()
            .map(ca -> requestMoveTarget(ca.idx, polyRef, DetourUtils.toFloatArray(to)))
            .allMatch(b -> b); // if all were successful, return true, else return false.
    }

    /**
     * This method is called by the CrowdManager to move the agents on the screen.
     */
    protected void applyMovements() {
        getActiveAgents().stream().forEach(ca -> applyMovement(ca, DetourUtils.createVector3f(ca.npos),
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
                spatialMap[crowdAgent.idx].setLocalTranslation(newPos);
                break;

            case BETTER_CHARACTER_CONTROL:
                BetterCharacterControl bcc = spatialMap[crowdAgent.idx].getControl(BetterCharacterControl.class);
                bcc.setWalkDirection(velocity);
                bcc.setViewDirection(velocity.normalize());
                break;

            default:
                throw new IllegalArgumentException("Unknown Application Type");
        }
    }
}
