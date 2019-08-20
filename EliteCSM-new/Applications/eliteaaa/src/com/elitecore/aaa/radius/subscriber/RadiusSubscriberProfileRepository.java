package com.elitecore.aaa.radius.subscriber;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.List;

import javax.management.MXBean;

import com.elitecore.aaa.core.data.AccountData;
import com.elitecore.aaa.core.drivers.IEliteAuthDriver;
import com.elitecore.aaa.core.subscriber.SubscriberProfileRepository;
import com.elitecore.aaa.radius.data.RadClientData;
import com.elitecore.aaa.radius.service.RadServiceContext;
import com.elitecore.aaa.radius.service.RadServiceRequest;
import com.elitecore.aaa.radius.service.RadServiceResponse;
import com.elitecore.aaa.radius.service.base.RadServiceRequestImpl;
import com.elitecore.aaa.radius.service.base.RadServiceResponseImpl;
import com.elitecore.aaa.radius.subscriber.conf.RadiusSubscriberProfileRepositoryDetails;
import com.elitecore.aaa.util.EliteUtility;
import com.elitecore.aaa.util.constants.AAAServerConstants;
import com.elitecore.aaa.util.mbean.SubscriberProfileController;
import com.elitecore.commons.base.Collectionz;
import com.elitecore.commons.base.Strings;
import com.elitecore.commons.logging.LogManager;
import com.elitecore.core.commons.InitializationFailedException;
import com.elitecore.core.commons.utilx.mbean.MBeanConstants;
import com.elitecore.core.util.url.SocketDetail;
import com.elitecore.coreradius.commons.packet.RadiusPacket;

/**
 * 
 * @author narendra.pathai
 *
 */
public class RadiusSubscriberProfileRepository extends SubscriberProfileRepository<RadServiceRequest, RadServiceResponse> {

	private static final String MODULE = "RAD-SUBSCRIBR-PRF-REPO";
	private final RadServiceContext<?, ?> serviceContext;
	private final RadiusSubscriberProfileRepositoryDetails repositoryDetails;
	private AccountData anonymousAccountData;

	public RadiusSubscriberProfileRepository(RadServiceContext<?, ?> serviceContext, RadiusSubscriberProfileRepositoryDetails data) {
		super(serviceContext.getServerContext(), data);
		this.serviceContext = serviceContext;
		repositoryDetails = data;
	}
	
	@Override
	public void init() throws InitializationFailedException {
		super.init();
		cacheAnnonymousAccountData();
		initializeMBean();
	}

	private void initializeMBean() {
		RadiusSubscriberProfileController mbean = new RadiusSubscriberProfileController(MBeanConstants.POLICY_PROFILE + repositoryDetails.getPolicyName());
		serviceContext.getServerContext().registerMBean(mbean);
	}

	private void cacheAnnonymousAccountData() {
		if (Strings.isNullOrBlank(repositoryDetails.getAnonymousProfileIdentity())) {
			return;
		}
		try {
			anonymousAccountData = getAccountData(new AnonymousRequest(), new AnonymousResponse(), repositoryDetails.getAnonymousProfileIdentity());
			if (anonymousAccountData != null) {
				if (LogManager.getLogger().isInfoLogLevel()){
					LogManager.getLogger().info(MODULE, "Anonymous User Profile in " 
							+ "Policy ["+ repositoryDetails.getPolicyName() +"] : "
							+ anonymousAccountData);
				}
			} else {
				if (LogManager.getLogger().isInfoLogLevel()) {
					LogManager.getLogger().info(MODULE, "Anonyous User Profile [" 
							+ repositoryDetails.getAnonymousProfileIdentity() 
							+ "] not found in Policy : " + repositoryDetails.getPolicyName()
							+ " So, Anonymous functionality is disabled.");
				}
			}
			
		} catch (UnknownHostException e) {
			if (LogManager.getLogger().isDebugLogLevel()) {
				LogManager.getLogger().debug(MODULE,"Problem while Creating " 
						+ "AnonymousAuthRequest, Reason : " + e.getMessage());
			}
		}
	}
	
	public AccountData getAnonymousAccountData(RadServiceRequest request) {
		if (anonymousAccountData != null) {
			request.setAccountData(anonymousAccountData);
			request.setParameter(AAAServerConstants.CUI_KEY, repositoryDetails.getAnonymousProfileIdentity());
			request.setParameter(AAAServerConstants.UNSTRIPPED_CUI, repositoryDetails.getAnonymousProfileIdentity());
			return this.anonymousAccountData;
		}
		return null;
	}
	
	@Override
	protected void addSubscriberProfileVSA(RadServiceRequest serviceRequest, AccountData accountData) {
		EliteUtility.addSubscriberProfileRadiusVSA(serviceRequest, accountData);
	}

	@Override
	protected IEliteAuthDriver getDriver(String driverInstanceId) {
		return (IEliteAuthDriver) serviceContext.getDriver(driverInstanceId);
	}

	@Override
	protected AccountData getAccountDataFromRequest(RadServiceRequest request) {
		return request.getAccountData();
	}

	@Override
	protected void setAccountDataIntoRequest(RadServiceRequest request, AccountData accountData) {
		request.setAccountData(accountData);
	}

	@Override
	public List<String> getUserIdentities(RadServiceRequest request) {
		List<String> userIdentities = null;
		RadClientData clientData = serviceContext.getServerContext().getServerConfiguration().getRadClientConfiguration().getClientData(((RadServiceRequest)request).getClientIp());
		userIdentities = clientData.getUserIdentities();
		if(Collectionz.isNullOrEmpty(userIdentities)){
			userIdentities = repositoryDetails.getUserIdentities();
		}
		return userIdentities;
	}
	
	public AccountData getAccountDataForIdentity(String userIdentity) {
		try {
			return getAccountData(new AnonymousRequest(), new AnonymousResponse(), userIdentity);
		} catch (UnknownHostException e) {
			LogManager.getLogger().trace("Error in fetching profile for identity: " + userIdentity + "Reason: ", e);
		}
		return null;
	}
	
	private class AnonymousRequest extends RadServiceRequestImpl {
		public AnonymousRequest() throws UnknownHostException {
			super(new byte[1], InetAddress.getLocalHost(), 1, new SocketDetail(InetAddress.getLocalHost().getHostAddress(), 1));
		}
		
		@Override
		public RadServiceRequest clone() {
			return (RadServiceRequest) super.clone();
		}
	}
	
	private class AnonymousResponse extends RadServiceResponseImpl {

		public AnonymousResponse() {
			super(new byte[1], 1);
		}

		@Override
		public RadiusPacket generatePacket() {
			return new RadiusPacket();
		}
		
		@Override
		public RadServiceResponse clone() {
			return super.clone();
		}
	}
	
	@MXBean
	private class RadiusSubscriberProfileController extends SubscriberProfileController {

		public RadiusSubscriberProfileController(String name) {
			super(name);
		}

		@Override
		public AccountData getSubscriberProfile(String identity) {
			return getAccountDataForIdentity(identity);
		}
		
	}

	@Override
	protected String getModuleName() {
		return MODULE;
	}
}
