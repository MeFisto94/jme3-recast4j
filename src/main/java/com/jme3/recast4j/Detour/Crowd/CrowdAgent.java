package com.jme3.recast4j.Detour.Crowd;

/**
 * This is the jme3-recast4j Wrapper around recast4j's CrowdAgent to provide additional behavior.<br />
 * Ghost: This Crowd Agent wont participate in moving but will block other agents like a "ghost".<br />
 * Use this for player characters and others.
 */
public class CrowdAgent extends org.recast4j.detour.crowd.CrowdAgent {
    protected boolean isGhost = false;

    public CrowdAgent(int idx) {
        super(idx);
    }

    public boolean isGhost() {
        return isGhost;
    }

    public void setGhost(boolean ghost) {
        isGhost = ghost;
    }
}
