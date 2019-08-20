package com.elitecore.aaa.diameter.service.application.handlers;

import java.util.List;

import javax.annotation.Nonnull;

import com.elitecore.aaa.core.authprotocol.BaseAuthMethodHandler;
import com.elitecore.aaa.core.authprotocol.EAPHandler;
import com.elitecore.aaa.core.authprotocol.LDAP;
import com.elitecore.aaa.core.authprotocol.exception.AuthenticationFailedException;
import com.elitecore.aaa.core.authprotocol.exception.UserNotFoundException;
import com.elitecore.aaa.core.conf.EAPConfigurationData;
import com.elitecore.aaa.core.conf.impl.EAPConfigurationDataImpl;
import com.elitecore.aaa.core.constant.DriverTypes;
import com.elitecore.aaa.core.data.AccountData;
import com.elitecore.aaa.core.drivers.DriverConfiguration;
import com.elitecore.aaa.core.server.AAAServerContext;
import com.elitecore.aaa.core.subscriber.IAccountInfoProvider;
import com.elitecore.aaa.diameter.service.DiameterServiceContext;
import com.elitecore.aaa.diameter.service.application.ApplicationRequest;
import com.elitecore.aaa.diameter.service.application.ApplicationResponse;
import com.elitecore.aaa.diameter.util.DiameterProcessHelper;
import com.elitecore.aaa.util.EliteUtility;
import com.elitecore.aaa.util.constants.AAAServerConstants;
import com.elitecore.commons.logging.LogLevel;
import com.elitecore.commons.logging.LogManager;
import com.elitecore.core.commons.InitializationFailedException;
import com.elitecore.coreeap.data.AAAEapRespData;
import com.elitecore.coreeap.data.AccountInfoProviderException;
import com.elitecore.coreeap.data.ICustomerAccountInfo;
import com.elitecore.coreeap.data.ICustomerAccountInfoProvider;
import com.elitecore.coreeap.fsm.eap.IEapStateMachine;
import com.elitecore.coreeap.packet.EAPException;
import com.elitecore.coreeap.packet.EAPPacket;
import com.elitecore.coreeap.packet.InvalidEAPPacketException;
import com.elitecore.coreeap.util.constants.EapTypeConstants;
import com.elitecore.coreeap.util.constants.fsm.events.EapEvents;
import com.elitecore.diameterapi.diameter.common.packet.avps.IDiameterAVP;
import com.elitecore.diameterapi.diameter.common.util.DiameterDictionary;
import com.elitecore.diameterapi.diameter.common.util.DiameterUtility;
import com.elitecore.diameterapi.diameter.common.util.constant.DiameterAVPConstants;
import com.elitecore.diameterapi.diameter.common.util.constant.DiameterErrorMessageConstants;
import com.elitecore.diameterapi.diameter.common.util.constant.ResultCode;
import com.elitecore.passwordutil.DecryptionFailedException;
import com.elitecore.passwordutil.DecryptionNotSupportedException;
import com.elitecore.passwordutil.NoSuchEncryptionException;
import com.elitecore.passwordutil.PasswordEncryption;

public class DiameterEapHandler extends BaseAuthMethodHandler  implements DiameterAuthMethodHandler {

	public static final String MODULE = "DIAMETER_EAP_HANDLER";

	private EAPHandler eapHandler;
	private String eapConfId;
	private DiameterServiceContext serviceContext;
	private EAPConfigurationData eapConfiguration;
	
	//FIXME introducing the test mode flag in diameter too, but currently it is always false
	private boolean isTestMode = false;

	private final IAccountInfoProvider accountInfoProvider;

	public DiameterEapHandler(DiameterServiceContext serviceContext,
			String eapConfId,
			@Nonnull IAccountInfoProvider accountInfoProvider) {
		super(serviceContext);
		this.serviceContext = serviceContext;
		this.eapConfId = eapConfId;
		this.accountInfoProvider = accountInfoProvider;

	}

	@Override
	public void init() throws InitializationFailedException {
		this.eapConfiguration = ((AAAServerContext)serviceContext.getServerContext()).getServerConfiguration().getEAPConfigurations().getEAPConfigurationDataForID(eapConfId);
		if(eapConfiguration == null)
			eapConfiguration = new EAPConfigurationDataImpl(serviceContext.getServerContext(),eapConfId);
		eapHandler = new EAPHandler(serviceContext,eapConfiguration,DiameterAVPConstants.EAP_PAYLOAD_INT,isTestMode);
		eapHandler.init();

	}

	@Override
	public void handleRequest(final ApplicationRequest request,final ApplicationResponse response, 
			final IAccountInfoProvider accountInfoProvider) throws AuthenticationFailedException {

		byte[] eapPacketBytes = request.getAVP(DiameterAVPConstants.EAP_PAYLOAD).getValueBytes();
		String sessionId = request.getAVP(DiameterAVPConstants.SESSION_ID).getStringValue();
		
		ICustomerAccountInfoProvider customerAccountInfoProvider = new ICustomerAccountInfoProvider(){
			private static final String MODULE = "IAccountInfoProvider";
			private DriverConfiguration driverConfiguration;
			
			public ICustomerAccountInfo getCustomerAccountInfo() throws AccountInfoProviderException{
				AccountData accountData =accountInfoProvider.getAccountData(request,response);
				return getCustomerProfile(accountData);
			}
			public ICustomerAccountInfo getCustomerAccountInfo(String strUserIdentity) throws AccountInfoProviderException{
				AccountData accountData =accountInfoProvider.getAccountData(request,response,strUserIdentity);
				return (ICustomerAccountInfo)getCustomerProfile(accountData);
			}
			
			@Override
			public ICustomerAccountInfo getCustomerAccountInfo(String strUserIdentity, EapTypeConstants constant)
					throws AccountInfoProviderException {
				IDiameterAVP infoAvp = request.getInfoAvp(DiameterAVPConstants.ELITE_EAP_METHOD);
				if (infoAvp == null) {
					infoAvp = DiameterDictionary.getInstance().getAttribute(DiameterAVPConstants.ELITE_EAP_METHOD);
					request.addInfoAvp(infoAvp);
				}
				infoAvp.setInteger(constant.typeId);
				return getCustomerAccountInfo(strUserIdentity);
			}
			
			private AccountData getCustomerProfile(AccountData accountData){
				
				if(accountData == null)
					return null;
				String decodedPassword = null;
				int encryptionType = 0;
				try {
					try{
						if(accountData.getEncryptionType() != null)
							encryptionType = Integer.parseInt(accountData.getEncryptionType());
					}catch (NumberFormatException e) {
						if(LogManager.getLogger().isLogLevel(LogLevel.DEBUG))
							LogManager.getLogger().debug(MODULE, "Invalid Password Encryption Type Format for user : "+accountData.getUserName());
					}
					decodedPassword = PasswordEncryption.getInstance().decrypt(accountData.getPassword(), encryptionType);
				} catch (NoSuchEncryptionException e) {
					throw new AccountInfoProviderException("Authentication failed due to Unsupported Password Encryption Type.",e,AccountInfoProviderException.PROFILE_OPERATION_EXCEPTION);
				} catch (DecryptionNotSupportedException e) {
					throw new AccountInfoProviderException("Authentication failed due to improper password encryption format.",e,AccountInfoProviderException.PROFILE_OPERATION_EXCEPTION);
				} catch (DecryptionFailedException e) {
					throw new AccountInfoProviderException("Authentication failed due to error in decryption.",e,AccountInfoProviderException.PROFILE_OPERATION_EXCEPTION);
				} 
				if(decodedPassword == null){
					if(LogManager.getLogger().isLogLevel(LogLevel.DEBUG))
						LogManager.getLogger().debug(MODULE, "Exception got during getting decoded password for user : "+accountData.getUserName()+", null account data returned");
					return null;
				}
				accountData.setPassword(decodedPassword);
				return accountData;
			}

			@Override
			public List<String> getClientUserIdentityAttributeList() {
				return null;
			}
			
			@Override
			public boolean doLdapBindAuthentication(String decodedPassword,ICustomerAccountInfo accountData) {
				if(driverConfiguration == null){
					driverConfiguration = ((AAAServerContext)getServiceContext().getServerContext()).getServerConfiguration().getDriverConfigurationProvider().getDriverConfiguration((String)request.getParameter(AAAServerConstants.DRIVER_INSTANCE_ID));
				}
				
				try{
					LDAP.verifyPassword(driverConfiguration, (String)request.getParameter(AAAServerConstants.DN), decodedPassword);
					return true;
				}catch (Exception e) {
					LogManager.getLogger().trace(MODULE, e);
					return false;
				}
			}
			
			@Override
			public boolean checkLDAPBindAuthenticationRequired(ICustomerAccountInfo accountData) {
				driverConfiguration = ((AAAServerContext)getServiceContext().getServerContext())
				.getServerConfiguration().getDriverConfigurationProvider()
				.getDriverConfiguration((String)request.getParameter(AAAServerConstants.DRIVER_INSTANCE_ID));
				
				return accountData.getPasswordCheck().equalsIgnoreCase("bind") 
						&& driverConfiguration.getDriverType() == DriverTypes.DIAMETER_LDAP_DRIVER;
			}
			
		};
		AAAEapRespData eapRespData = null;
		EapEvents event = null;

		IDiameterAVP framedMTU = request.getAVP(DiameterAVPConstants.FRAMED_MTU);
		int configValue = eapHandler.getEapContext().getFragmentSize();

		if(framedMTU != null){
			int framedMTUValue = Integer.parseInt(framedMTU.getStringValue());
			if(configValue > 0){
				if((framedMTUValue-4) < configValue){							
					configValue = framedMTUValue - 4;
				}
			}else if(configValue == 0){
				configValue = framedMTUValue - 4;
			}
		}

		try {
			try{
				eapRespData = new AAAEapRespData(eapPacketBytes);
				eapRespData.setFragmentedSize(configValue);
				
				IDiameterAVP callingStationId = request.getAVP(DiameterAVPConstants.CALLING_STATION_ID);
				if(callingStationId != null){
					String strCallingStationId = callingStationId.getStringValue();
					eapRespData.setOUI(strCallingStationId);
				} else {
					if (LogManager.getLogger().isDebugLogLevel()) {
						LogManager.getLogger().debug(MODULE, "Calling-Station-id(" + DiameterAVPConstants.CALLING_STATION_ID + " not found in request. So, OUI is not added");
					}
				}
				
				event =  (EapEvents) eapHandler.handleRequest(eapRespData, sessionId, customerAccountInfoProvider);

				if (event == EapEvents.EapSuccessEvent) {
					AccountData accountData =  (AccountData) eapHandler.getCustomerAccountInfo(sessionId);
					if (accountData == null) {
						throw new UserNotFoundException();
					}
					
					request.setParameter("EAP_SESSION", ((AAAServerContext)getServiceContext().getServerContext()).getEapSessionManager()
							.getEAPStateMachine(sessionId));

					request.setParameter("EAP_SESSION_ID", sessionId);

					request.setParameter("EAP_IDENTITY", eapHandler.getIdentity(sessionId));
					request.setAccountData(accountData);
					request.setParameter(AAAServerConstants.MSK_REVALIDATION_TIME, eapConfiguration.getMskRevalidationTime());
					byte[] eapKeyData = null;
					if (eapRespData.getAAAEapKeyData() != null) {
						eapKeyData = eapRespData.getAAAEapKeyData();
						request.setParameter("EAP_KEY_DATA", eapKeyData);
					} else {
						if (LogManager.getLogger().isLogLevel(LogLevel.DEBUG)) {
							LogManager.getLogger().debug(MODULE, "AAA EAP key data  for Session-Id : " + sessionId + " is null");
						}
					}
					
					// username
					IEapStateMachine sessionState = ((AAAServerContext)getServiceContext().getServerContext())
							.getEapSessionManager().getEAPStateMachine(sessionId);
					if (sessionState != null ) {
						String userIdentityValue = sessionState.getUserIdentity();
						if (userIdentityValue != null) {
							request.setParameter(AAAServerConstants.CUI_KEY, userIdentityValue);
						}
					}
					
					EliteUtility.addSubscriberProfileDiameterVSA(request,accountData);
				}
				buildDiameterResponse(request, response, eapRespData, event);

			} catch (EAPException e) {
				LogManager.getLogger().trace(MODULE, e);
				if (LogManager.getLogger().isLogLevel(LogLevel.INFO)) {
					LogManager.getLogger().info(MODULE, "Packet Dropped. Reason: " + e.getMessage());
				}
				response.markForDropRequest();
				response.setFurtherProcessingRequired(false);

			} catch (AccountInfoProviderException e) {
				if (e.getCode() == AccountInfoProviderException.USER_NOT_FOUND) {
					throw new UserNotFoundException();
				} else {
					throw new AuthenticationFailedException("EAP Failure",e); 
				}
			}
		} catch (AuthenticationFailedException ex) {
			if (LogManager.getLogger().isInfoLogLevel()) {
				LogManager.getLogger().info(MODULE, "Authentication failed, result code: DIAMETER_USER_UNKNOWN. Reason: " + ex.getMessage());
			}
			
			updateOrAddResultCodeAVPInto(response);
			
			IEapStateMachine eapStateMachine = ((AAAServerContext)getServiceContext().getServerContext()).getEapSessionManager().getEAPStateMachine(sessionId);
			if (eapStateMachine != null) {
				EAPPacket eapFailurePacket;
				try {
					eapFailurePacket = eapStateMachine.buildFailure(eapStateMachine.getCurrentIdentifier());
					IDiameterAVP eapPayload = DiameterDictionary.getInstance().getAttribute(DiameterAVPConstants.EAP_PAYLOAD);
					if (eapPayload != null) {
						eapPayload.setValueBytes(eapFailurePacket.getBytes());
						response.addAVP(eapPayload);
					}
				} catch (EAPException e1) {
					if (LogManager.getLogger().isLogLevel(LogLevel.WARN)) {
						LogManager.getLogger().warn(MODULE, "Failed to add EAP-Payload Avp to DiameterAnswer, Reason: "+e1.getMessage());
					}
				}
			}

			response.setFurtherProcessingRequired(false);
		}
		
	}

	private void updateOrAddResultCodeAVPInto(ApplicationResponse response) {
		IDiameterAVP resultCodeAvp = response.getAVP(DiameterAVPConstants.RESULT_CODE);
		if (resultCodeAvp == null) {
			resultCodeAvp = DiameterDictionary.getInstance().getAttribute(DiameterAVPConstants.RESULT_CODE);
			resultCodeAvp.setInteger(ResultCode.DIAMETER_USER_UNKNOWN.code);
			response.addAVP(resultCodeAvp);
		} else {
			resultCodeAvp.setInteger(ResultCode.DIAMETER_USER_UNKNOWN.code);
		}
	}

	
	private void buildDiameterResponse(ApplicationRequest request,
			ApplicationResponse response,AAAEapRespData eapRespData, EapEvents event ){

		if(eapRespData.getEapReqPacketBytes() != null){
			IDiameterAVP eapPayLoadAVP = DiameterDictionary.getInstance().getAttribute(DiameterAVPConstants.EAP_PAYLOAD);
			eapPayLoadAVP.setValueBytes(eapRespData.getEapReqPacketBytes());
			response.addAVP(eapPayLoadAVP);
		}

		if(event == EapEvents.EapSuccessEvent)
			DiameterUtility.addOrReplaceAvp(DiameterAVPConstants.RESULT_CODE, response.getDiameterAnswer(), ResultCode.DIAMETER_SUCCESS.code + "");
		else if(event == EapEvents.EapFailureEvent){
			DiameterProcessHelper.rejectResponse(response, ResultCode.DIAMETER_AUTHENTICATION_REJECTED, DiameterErrorMessageConstants.EAP_FAILURE);
		}else if(event == EapEvents.EapException){
			DiameterUtility.addOrReplaceAvp(DiameterAVPConstants.RESULT_CODE, response.getDiameterAnswer(), ResultCode.DIAMETER_UNABLE_TO_COMPLY.code + "");
			response.setFurtherProcessingRequired(false);
		}else{
			DiameterUtility.addOrReplaceAvp(DiameterAVPConstants.RESULT_CODE, response.getDiameterAnswer(), ResultCode.DIAMETER_MULTI_ROUND_AUTH.code + "");
			response.setFurtherProcessingRequired(false);
		}	

		//adding the Elitecore EAP VSA
		addElitecoreEAPInfoAVP(response);

		if (response.isFurtherProcessingRequired() == false) {
			return;
		}

		IDiameterAVP sessionTimeoutAVP = DiameterDictionary.getInstance().getAttribute(DiameterAVPConstants.SESSION_TIMEOUT);
		sessionTimeoutAVP.setInteger(eapConfiguration.getSessionTimeout());
		response.addAVP(sessionTimeoutAVP);
		byte[] keyData = eapRespData.getAAAEapKeyData();
		if(keyData!= null){				
			byte[] msk = new byte[64];
			System.arraycopy(keyData, 0, msk, 0, msk.length);				
			IDiameterAVP mskAvp = DiameterDictionary.getInstance().getAttribute(DiameterAVPConstants.EAP_MASTER_SESSION_KEY);
			mskAvp.setValueBytes(msk);
			response.addAVP(mskAvp);
		}			

	}

	
	private void addElitecoreEAPInfoAVP(ApplicationResponse eapApplicationResponse){
		
		IDiameterAVP eapPayloadAVP = eapApplicationResponse.getAVP(DiameterAVPConstants.EAP_PAYLOAD);
		if(eapPayloadAVP != null){
			try {
				EAPPacket eapPacket = new EAPPacket(eapPayloadAVP.getValueBytes());
				IDiameterAVP ecEAPCodeAVP = DiameterDictionary.getInstance().getKnownAttribute(DiameterAVPConstants.ELITE_EAP_CODE);
				if(ecEAPCodeAVP != null){
					ecEAPCodeAVP.setStringValue(String.valueOf(eapPacket.getCode()));
					eapApplicationResponse.addInfoAvp(ecEAPCodeAVP);
				}

				IDiameterAVP ecEAPMethodAVP = DiameterDictionary.getInstance().getKnownAttribute(DiameterAVPConstants.ELITE_EAP_METHOD);
				if(ecEAPMethodAVP != null){
					ecEAPMethodAVP.setStringValue(String.valueOf(eapPacket.getEAPType().getType()));
					eapApplicationResponse.addInfoAvp(ecEAPMethodAVP);
				}
			} catch (InvalidEAPPacketException e) {
				if (LogManager.getLogger().isLogLevel(LogLevel.WARN))
					LogManager.getLogger().warn(MODULE, "Invalid EAP Payload AVP, so ELITE-EAP-AVP and ELITE-EAP-METHOD will not be added to Diameter Request.");
			}
		}
	}

	@Override
	public void reInit() throws InitializationFailedException {
		EAPConfigurationData eapConfiguration = ((AAAServerContext)serviceContext.getServerContext()).getServerConfiguration().getEAPConfigurations().getEAPConfigurationDataForID(eapConfId);
		if(eapConfiguration != null){
			this.eapConfiguration = eapConfiguration;
			this.eapHandler.reInit(eapConfiguration);
		}
	}

	@Override
	public boolean isEligible(ApplicationRequest request) {
		return request.getAVP(DiameterAVPConstants.EAP_PAYLOAD) != null;
	}

}
