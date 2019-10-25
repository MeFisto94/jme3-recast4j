package com.jme3.recast4j.test;

/**
 * As opposed to {@link com.jme3.recast4j.test.Scenario }, this Scenario will appear in jme3-recast4j-demo as well.
 * It might have to provide more API to work with the UI.
 */
public interface UIAwareScenario extends Scenario {
    String getDescription();
}
