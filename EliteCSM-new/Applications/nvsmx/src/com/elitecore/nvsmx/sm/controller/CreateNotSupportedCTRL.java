package com.elitecore.nvsmx.sm.controller;

import com.elitecore.commons.logging.LogManager;
import com.elitecore.corenetvertex.sm.ResourceData;
import com.elitecore.nvsmx.system.constants.NVSMXCommonConstants;
import org.apache.struts2.interceptor.validation.SkipValidation;
import org.apache.struts2.rest.DefaultHttpHeaders;
import org.apache.struts2.rest.HttpHeaders;

import javax.servlet.http.HttpServletResponse;

import static com.elitecore.commons.logging.LogManager.getLogger;

public abstract class CreateNotSupportedCTRL<T extends ResourceData> extends DestroyNotSupportedCTRL<T> {

    @Override
    @SkipValidation
    public final HttpHeaders create() {
        if(LogManager.getLogger().isDebugLogLevel()){
            LogManager.getLogger().debug(getLogModule(),"Method called create()");
        }
        getLogger().error(getLogModule(),"create operation Not Supported");
        setActionChainUrl(getRedirectURL(METHOD_SHOW));
        addActionError(getText("method.not.allowed"));
        getResponse().addHeader(ALLOWED_METHOD_HEADER,NON_CREATEABLE_RESOURCE_ALLOWED_METHOD);
        return new DefaultHttpHeaders(NVSMXCommonConstants.REDIRECT_URL).withStatus(HttpServletResponse.SC_METHOD_NOT_ALLOWED);
    }

}
