package com.elitecore.corenetvertex.sm;


/***
 * This interface define that any class which implements this interface must define
 * the copyModel method to replicate existing class
 * The class from which the replicate operation start should implement this method
 */
public interface Replicable {
    default <T extends ResourceData> T copyModel() {
        throw new UnsupportedOperationException("Copy operation not supported");
    }
}

