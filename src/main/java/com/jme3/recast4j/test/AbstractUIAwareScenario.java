package com.jme3.recast4j.test;

import com.jme3.recast4j.Detour.Crowd.Crowd;

public abstract class AbstractUIAwareScenario implements UIAwareScenario {
    protected String description;
    protected String name;

    public AbstractUIAwareScenario(String description, String name) {
        this.description = description;
        this.name = name;
    }

    @Override
    public String getDescription() {
        return description;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public abstract Crowd[] run();
}
