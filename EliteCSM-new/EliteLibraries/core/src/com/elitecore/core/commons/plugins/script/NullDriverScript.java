package com.elitecore.core.commons.plugins.script;

import com.elitecore.core.serverx.manager.scripts.ScriptContext;
import com.elitecore.core.servicex.ServiceRequest;
import com.elitecore.core.servicex.ServiceResponse;

/**
 * A null object pattern placeholder script that performs no action.
 * @author narendra.pathai
 */
public class NullDriverScript extends DriverScript {

    public NullDriverScript(ScriptContext scriptContext) {
        super(scriptContext);
    }

    @Override
    public String getName() {
        return "NULL-DRIVER-SCRIPT";
    }

    @Override
    protected void pre(ServiceRequest serviceRequest, ServiceResponse serviceResponse) {

    }

    @Override
    protected void post(ServiceRequest serviceRequest, ServiceResponse serviceResponse) {

    }
}
