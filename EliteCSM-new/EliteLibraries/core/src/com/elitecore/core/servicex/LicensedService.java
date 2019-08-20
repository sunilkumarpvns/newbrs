package com.elitecore.core.servicex;

import static com.elitecore.commons.base.Preconditions.checkNotNull;

import java.util.Date;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import com.elitecore.core.commons.InitializationFailedException;
import com.elitecore.core.commons.util.constants.LifeCycleState;
import com.elitecore.core.commons.util.constants.ServiceRemarks;
import com.elitecore.core.serverx.ServerContext;

/**
 * A decorator around {@link EliteService}, which validates the license availability before starting 
 * the decorated service.
 *  
 * @author narendra.pathai
 *
 */
public class LicensedService implements EliteService {

	private @Nonnull ServerContext serverContext;
	private @Nonnull EliteService service;
	private @Nonnull String licenseKey;
	private boolean isLicenseValid;
	private @Nullable ServiceRemarks serviceRemark;

	public LicensedService(@Nonnull ServerContext serverContext, @Nonnull EliteService service, 
			@Nonnull String licenseKey) {
		this.serverContext = checkNotNull(serverContext, "serverContext is null");
		this.service = checkNotNull(service, "service is null");
		this.licenseKey = checkNotNull(licenseKey, "licenseKey is null");
	}
	
	@Override
	public boolean start() {
		return service.start();
	}

	/**
	 * Initializes the decorated service only if the license for underlying service is acquired.
	 * @throws ServiceInitializationException if license for underlying service is not acquired. 
	 * @return this
	 */
	@Override
	public EliteService init() throws ServiceInitializationException {
		validateLicense();
		return service.init();
	}

	private void validateLicense() throws ServiceInitializationException {
		this.isLicenseValid = isLicenseValid();
		if (isLicenseValid == false) {
			serviceRemark = ServiceRemarks.INVALID_LICENSE;
			throw new ServiceInitializationException(getServiceIdentifier() + " will not start, "
						+ "Reason: License for " + getLicenseKey() + " is not acquired or invalid.", 
					ServiceRemarks.INVALID_LICENSE);
		}
	}
	
	@Override
	public ServiceDescription getDescription() {
		if (isLicenseValid == false) {
			return new ServiceDescription(service.getServiceIdentifier(), LifeCycleState.NOT_STARTED.message, 
					null, null, ServiceRemarks.INVALID_LICENSE.remark);
		}
		return service.getDescription();
	}

	@Override
	public String getServiceIdentifier() {
		return service.getServiceIdentifier();
	}

	@Override
	public String getServiceName() {
		return service.getServiceName();
	}
	
	private String getLicenseKey() {
		return licenseKey;
	}
	
	private boolean isLicenseValid() {
		if (!serverContext.isLicenseValid(getLicenseKey(), String.valueOf(System.currentTimeMillis()))) {
			return false;
		}
		
		return true;
	}

	/**
	 * @return true if license validation for decorated service fails else returns the result of 
	 * calling {@link EliteService#stop()} on decorated service. 
	 */
	@Override
	public boolean stop() {
		if (isLicenseValid == false) {
			return true;
		}
		return service.stop();
	}

	@Override
	public String getStatus() {
		return service.getStatus();
	}

	@Override
	public String getRemarks() {
		return serviceRemark != null ? serviceRemark.remark : service.getRemarks();
	}

	@Override
	public Date getStartDate() {
		return service.getStartDate();
	}

	@Override
	public void reInit() throws InitializationFailedException {
		service.reInit();
	}

	@Override
	public void doFinalShutdown() {
		service.doFinalShutdown();
	}

	@Override
	public void cleanupResources() {
		service.cleanupResources();
	}

	@Override
	public void setRemark(ServiceRemarks remark) {
		service.setRemark(remark);
	}
}
