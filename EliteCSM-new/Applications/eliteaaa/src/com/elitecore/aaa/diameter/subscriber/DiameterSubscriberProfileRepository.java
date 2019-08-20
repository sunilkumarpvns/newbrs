package com.elitecore.aaa.diameter.subscriber;

import java.util.List;

import com.elitecore.aaa.core.data.AccountData;
import com.elitecore.aaa.core.drivers.IEliteAuthDriver;
import com.elitecore.aaa.core.subscriber.SubscriberProfileRepository;
import com.elitecore.aaa.diameter.service.DiameterServiceContext;
import com.elitecore.aaa.diameter.service.application.ApplicationRequest;
import com.elitecore.aaa.diameter.service.application.ApplicationResponse;
import com.elitecore.aaa.util.EliteUtility;
import com.elitecore.aaa.util.constants.AAAServerConstants;
import com.elitecore.aaa.util.mbean.SubscriberProfileController;
import com.elitecore.commons.base.Strings;
import com.elitecore.commons.logging.LogManager;
import com.elitecore.core.commons.InitializationFailedException;
import com.elitecore.core.commons.utilx.mbean.MBeanConstants;
import com.elitecore.diameterapi.diameter.common.packet.DiameterRequest;

public class DiameterSubscriberProfileRepository extends SubscriberProfileRepository<ApplicationRequest, ApplicationResponse> {

	private static final String MODULE = "DIA-SUBSCRIBR-PRF-REPO";
	private final DiameterServiceContext serviceContext;
	private final DiameterSubscriberProfileRepositoryDetails data;
	private AccountData anonymousAccountData;
	private final String anonymousIdentity;

	public DiameterSubscriberProfileRepository(DiameterServiceContext serviceContext, 
			DiameterSubscriberProfileRepositoryDetails data) {
		super(serviceContext.getServerContext(), data);
		this.serviceContext = serviceContext;
		this.anonymousIdentity = data.getAnonymousProfileIdentity();
		this.data = data;
	}
	
	@Override
	public void init() throws InitializationFailedException {
		super.init();
		cacheAnonymousAccountData();
		initializeMBean();
	}

	private void initializeMBean() {
		DiameterSubscriberProfileController mbean = new DiameterSubscriberProfileController(MBeanConstants.POLICY_PROFILE + data.getPolicyName());
		serviceContext.getServerContext().registerMBean(mbean);
	}
	
	@Override
	protected void addSubscriberProfileVSA(ApplicationRequest serviceRequest, AccountData accountData) {
		EliteUtility.addSubscriberProfileDiameterVSA(serviceRequest, accountData);
	}

	@Override
	protected IEliteAuthDriver getDriver(String driverInstanceId) {
		return (IEliteAuthDriver) serviceContext.getDriver(driverInstanceId);
	}

	@Override
	protected AccountData getAccountDataFromRequest(ApplicationRequest request) {
		return request.getAccountData();
	}

	@Override
	protected void setAccountDataIntoRequest(ApplicationRequest request, AccountData accountData) {
		request.setAccountData(accountData);
	}

	public DiameterSubscriberProfileRepositoryDetails getData() {
		return data;
	}
	
	@Override
	protected List<String> getUserIdentities(ApplicationRequest request) {
		if (data.getPolicyData() == null) {
			// This means that the SPR is being utilized in NAS or EAP policy. So return from spr configuration 
			return data.getUserIdentities();
		}
		
		// This means that the handler is being utilized in TGPP server policy. So return from policy basic configuration.
		return data.getPolicyData().getUserIdentities();
	}
	
	class DiameterSubscriberProfileController extends SubscriberProfileController {

		public DiameterSubscriberProfileController(String name) {
			super(name);
		}

		@Override
		public AccountData getSubscriberProfile(String identity) {
			ApplicationRequest dummyRequest = new ApplicationRequest(new DiameterRequest());
			return getAccountData(dummyRequest, new ApplicationResponse(dummyRequest.getDiameterRequest()), identity);
		}
		
	}

	@Override
	protected String getModuleName() {
		return MODULE;
	}

	private void cacheAnonymousAccountData() {
		if (Strings.isNullOrBlank(anonymousIdentity)) {
			return;
		}
		anonymousAccountData = getAccountData(new AnonymousRequest(), new AnonymousResponse(), anonymousIdentity);
		if (anonymousAccountData != null) {
			if (LogManager.getLogger().isInfoLogLevel()){
				LogManager.getLogger().info(MODULE, "Anonymous User Profile in " 
						+ "Policy ["+ data.getPolicyName() +"] : "
						+ anonymousAccountData);
			}
		} else {
			if (LogManager.getLogger().isInfoLogLevel()) {
				LogManager.getLogger().info(MODULE, "Anonymous User Profile [" 
						+ anonymousIdentity 
						+ "] not found in Policy : " + data.getPolicyName()
						+ " So, Anonymous functionality is disabled.");
			}
		}
	}
	
	public AccountData getAnonymousAccountData(ApplicationRequest request) {
		if (anonymousAccountData != null) {
			request.setAccountData(anonymousAccountData);
			request.setParameter(AAAServerConstants.CUI_KEY, anonymousIdentity);
			request.setParameter(AAAServerConstants.UNSTRIPPED_CUI, anonymousIdentity);
			if (LogManager.getLogger().isInfoLogLevel()) {
				LogManager.getLogger().info(MODULE, "Anonymous User Profile for Identity: " + anonymousIdentity + anonymousAccountData);
			}
			return this.anonymousAccountData;
		}
		return null;
	}
	
	private class AnonymousRequest extends ApplicationRequest {
		public AnonymousRequest() {
			super(new DiameterRequest());
		}
	}
	
	private class AnonymousResponse extends ApplicationResponse {
		public AnonymousResponse() {
			super(new DiameterRequest());
		}
	}
}
