package com.jme3.recast4j.Detour.Crowd.Impl;

import com.jme3.app.Application;
import com.jme3.app.state.BaseAppState;
import com.jme3.recast4j.Detour.Crowd.CrowdManager;

/**
 * CrowdManagerAppstate is a wrapper around the "CrowdManager" class to provide an easier interfacing with the jMonkeyEngine.
 * Use this as starting point for your entity-based approaches.
 */
public class CrowdManagerAppstate extends BaseAppState {
    CrowdManager crowdManager;

    public CrowdManagerAppstate(CrowdManager crowdManager) {
        this.crowdManager = crowdManager;
    }

    public CrowdManager getCrowdManager() {
        return crowdManager;
    }

    public void setCrowdManager(CrowdManager crowdManager) {
        this.crowdManager = crowdManager;
    }

    @Override
    protected void initialize(Application application) {

    }

    @Override
    protected void cleanup(Application application) {

    }

    @Override
    protected void onEnable() {

    }

    @Override
    protected void onDisable() {
    }

    @Override
    public void update(float tpf) {
        super.update(tpf);
        crowdManager.update(tpf);
    }
}
