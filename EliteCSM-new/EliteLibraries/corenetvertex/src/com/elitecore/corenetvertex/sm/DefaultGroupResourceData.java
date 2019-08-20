package com.elitecore.corenetvertex.sm;

import com.elitecore.corenetvertex.constants.CommonConstants;

import javax.persistence.Column;
import javax.persistence.MappedSuperclass;

/**
 * Used to manage non-mvno entity groups.
 * @author dhyani.raval
 */

@MappedSuperclass
public abstract class DefaultGroupResourceData extends ResourceData {

    @Override
    @Column(name="GROUPS")
    public  String getGroups() {
        return CommonConstants.DEFAULT_GROUP_ID;
    }

    @Override
    public void setGroups(String groupNames) {
        super.setGroups(CommonConstants.DEFAULT_GROUP_ID);
    }
}
