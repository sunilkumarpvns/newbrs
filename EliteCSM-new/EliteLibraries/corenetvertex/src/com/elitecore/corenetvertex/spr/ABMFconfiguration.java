package com.elitecore.corenetvertex.spr;

import com.elitecore.corenetvertex.util.ToStringable;

public interface ABMFconfiguration extends ToStringable {
    public int getBatchSize();
    public int getBatchQueryTimeout();

    public int getQueryTimeout();
}
