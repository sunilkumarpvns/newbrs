package com.elitecore.nvsmx.sm.controller;

import com.elitecore.commons.logging.LogManager;
import com.elitecore.corenetvertex.sm.ResourceData;
import com.elitecore.nvsmx.system.constants.NVSMXCommonConstants;
import org.apache.struts2.rest.DefaultHttpHeaders;
import org.apache.struts2.rest.HttpHeaders;

import javax.servlet.http.HttpServletResponse;

import static com.elitecore.commons.logging.LogManager.getLogger;

public abstract class DestroyNotSupportedCTRL<T extends ResourceData> extends RestGenericCTRL<T> {

    @Override
    public final HttpHeaders destroy() {
        if(LogManager.getLogger().isDebugLogLevel()){
            LogManager.getLogger().debug(getLogModule(),"Method called destroy()");
        }
        getLogger().error(getLogModule(),"Delete operation Not Supported");
        setActionChainUrl(getRedirectURL(METHOD_SHOW));
        addActionError(getText("method.not.allowed"));
        getResponse().addHeader(ALLOWED_METHOD_HEADER,NON_DESTROYABLE_RESOURCE_ALLOWED_METHOD);
        return new DefaultHttpHeaders(NVSMXCommonConstants.REDIRECT_URL).withStatus(HttpServletResponse.SC_METHOD_NOT_ALLOWED);

    }
}
