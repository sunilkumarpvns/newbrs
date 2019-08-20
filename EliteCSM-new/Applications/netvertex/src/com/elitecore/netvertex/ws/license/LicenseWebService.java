package com.elitecore.netvertex.ws.license;

import com.elitecore.commons.logging.LogManager;
import com.elitecore.license.base.LicenseManager;
import com.elitecore.license.base.LicenseObserver;

import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

/**
 * @author Kartik Prajapati
 */

@Path("/license")
public class LicenseWebService {

    private static final String MODULE = "LICENSE-WEB-SERVICE";

    private LicenseManager licenseManager;
    private LicenseObserver licenseRequestFutureDeregisterer;

    public LicenseWebService(LicenseManager licenseManager, LicenseObserver licenseObserver){
        this.licenseManager = licenseManager;
        this.licenseRequestFutureDeregisterer = licenseObserver;
    }

    @POST
    @Path("/deregister")
    @Produces({ MediaType.TEXT_PLAIN })
    public String deregisterLicense() {

        if (LogManager.getLogger().isDebugLogLevel()) {
            LogManager.getLogger().debug(MODULE, "Calling deregisterLicense.");
        }

        licenseManager.deregisterLicense();
        licenseRequestFutureDeregisterer.setStatus();

        return "License deregistered successfully.";
    }
}
