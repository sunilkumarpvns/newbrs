package com.sterlite.voltdbloadgen.task;

public interface RequestBuildContext {
    void addToInitialQueue(RequestTask requestTask);
    void scheduleUpdate(RequestTask requestTask);
}
