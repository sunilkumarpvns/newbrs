package com.elitecore.nvsmx.pd.controller.lob;

import static com.opensymphony.xwork2.Action.SUCCESS;

import org.apache.struts2.convention.annotation.Namespace;
import org.apache.struts2.convention.annotation.ParentPackage;
import org.apache.struts2.convention.annotation.Result;
import org.apache.struts2.convention.annotation.Results;

import com.elitecore.corenetvertex.pd.lob.LobData;
import com.elitecore.corenetvertex.pkg.constants.ACLModules;
import com.elitecore.nvsmx.sm.controller.RestGenericCTRL;

/**
 * manage LOB related information.
 * Created by Saket on 4/12/17.
 */

@ParentPackage(value = "pd")
@Namespace("/pd/lob")
@Results({
        @Result(name= SUCCESS, type="redirectAction",params = {"actionName","lob"}),

})
public class LobCTRL extends RestGenericCTRL<LobData> {

	private static final long serialVersionUID = -6882470730134447721L;

	@Override
    public ACLModules getModule() {
        return ACLModules.LOB;
    }

    @Override
    public LobData createModel() {
        return new LobData();
    }
}
