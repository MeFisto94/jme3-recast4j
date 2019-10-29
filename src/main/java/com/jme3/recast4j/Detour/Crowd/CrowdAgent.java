package com.jme3.recast4j.Detour.Crowd;

/**
 * This is the jme3-recast4j Wrapper around recast4j's CrowdAgent to provide additional behavior.<br />
 * Ghost: This Crowd Agent wont participate in moving but will block other agents like a "ghost".<br />
 * Use this for player characters and others.
 */
public class CrowdAgent extends org.recast4j.detour.crowd.CrowdAgent {
    protected boolean isGhost = false;
    protected Crowd crowd;

    public CrowdAgent(int idx, Crowd crowd) {
        super(idx);
        this.crowd = crowd;
    }

    public boolean isMoving() {
        return active &&
                (state == CrowdAgentState.DT_CROWDAGENT_STATE_OFFMESH ||
                 state == CrowdAgentState.DT_CROWDAGENT_STATE_WALKING);
    }

    public boolean hasNoInvalidTarget() {
        return active && targetState != MoveRequestState.DT_CROWDAGENT_TARGET_FAILED;
    }

    public boolean hasValidTarget() {
        return active && targetState == CrowdAgent.MoveRequestState.DT_CROWDAGENT_TARGET_VALID;
    }

    public boolean hasNoTarget() {
        return active && targetState == CrowdAgent.MoveRequestState.DT_CROWDAGENT_TARGET_NONE;
    }

    public boolean isGhost() {
        return isGhost;
    }

    public void setGhost(boolean ghost) {
        isGhost = ghost;
    }

    public Crowd getCrowd() {
        return crowd;
    }
}
