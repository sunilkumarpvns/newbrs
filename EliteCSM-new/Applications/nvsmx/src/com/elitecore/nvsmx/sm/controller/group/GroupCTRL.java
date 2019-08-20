package com.elitecore.nvsmx.sm.controller.group;

import com.elitecore.corenetvertex.pkg.constants.ACLModules;
import com.elitecore.corenetvertex.sm.acl.GroupData;
import com.elitecore.nvsmx.sm.controller.DestroyNotSupportedCTRL;
import org.apache.struts2.convention.annotation.Namespace;
import org.apache.struts2.convention.annotation.ParentPackage;
import org.apache.struts2.convention.annotation.Result;
import org.apache.struts2.convention.annotation.Results;

import static com.opensymphony.xwork2.Action.SUCCESS;

/**
 * Created by aditya on 8/2/17.
 */

@ParentPackage(value = "sm")
@Namespace("/sm/group")
@Results({
        @Result(name= SUCCESS, type="redirectAction",params = {"actionName","group"}),
})
public class GroupCTRL extends DestroyNotSupportedCTRL<GroupData> {

    @Override
    public ACLModules getModule() {
        return ACLModules.GROUP;
    }

    @Override
    public GroupData createModel() {
        return new GroupData();
    }


}


