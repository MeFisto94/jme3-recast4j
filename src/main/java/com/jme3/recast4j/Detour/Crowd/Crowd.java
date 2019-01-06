package com.jme3.recast4j.Detour.Crowd;

import com.jme3.math.Vector3f;
import com.jme3.recast4j.Detour.DetourUtils;
import org.recast4j.detour.NavMesh;
import org.recast4j.detour.QueryFilter;
import org.recast4j.detour.crowd.CrowdAgent;
import org.recast4j.detour.crowd.CrowdAgentParams;
import org.recast4j.detour.crowd.debug.CrowdAgentDebugInfo;

import java.util.function.IntFunction;

public class Crowd extends org.recast4j.detour.crowd.Crowd {
    protected boolean debug;
    protected CrowdAgentDebugInfo debugInfo;

    public Crowd(int maxAgents, float maxAgentRadius, NavMesh nav) {
        super(maxAgents, maxAgentRadius, nav);
    }

    public Crowd(int maxAgents, float maxAgentRadius, NavMesh nav, IntFunction<QueryFilter> queryFilterFactory) {
        super(maxAgents, maxAgentRadius, nav, queryFilterFactory);
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

    public void removeAgent(CrowdAgent agent) {
        if (agent.idx != -1) {
            removeAgent(agent.idx);
        }
    }
}
