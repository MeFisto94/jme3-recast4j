package com.jme3.recast4j.Detour.Crowd;

import java.util.ArrayList;
import java.util.stream.Stream;

public class CrowdManager {
    CrowdUpdateType updateType;
    ArrayList<Crowd> crowdList;

    public CrowdManager() {
        updateType = CrowdUpdateType.SEQUENTIAL;
        crowdList = new ArrayList<>();
    }

    public void setUpdateType(CrowdUpdateType updateType) {
        this.updateType = updateType;
    }

    public void addCrowd(Crowd c) {
        crowdList.add(c);
    }

    public void removeCrowd(Crowd c) {
        crowdList.remove(c);
    }

    public Crowd getCrowd(int idx) {
        return crowdList.get(idx);
    }

    public int getNumberOfCrowds() {
        return crowdList.size();
    }

    public void update(float timePassed) {
        Stream<Crowd> stream;

        switch (updateType) {
            case SEQUENTIAL:
                stream = crowdList.stream();
                break;

            case PARALLEL:
                stream = crowdList.parallelStream();
                break;

            default:
                throw new IllegalArgumentException("Unknown Update Type");
        }

        stream.forEach(c -> c.update(timePassed));
    }

}
