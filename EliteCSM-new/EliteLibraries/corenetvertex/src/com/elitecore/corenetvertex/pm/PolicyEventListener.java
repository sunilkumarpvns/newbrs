package com.elitecore.corenetvertex.pm;

public interface PolicyEventListener<T> {

    void policyCreated(T pkg);
    void policyRemoved(T pkg);
}
