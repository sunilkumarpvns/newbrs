package com.elitecore.aaa.diameter.service.application.handlers;

import com.elitecore.aaa.core.authprotocol.BaseAuthMethodHandler;
import com.elitecore.aaa.core.authprotocol.CHAP;
import com.elitecore.aaa.core.authprotocol.exception.AuthenticationFailedException;
import com.elitecore.aaa.core.authprotocol.exception.InvalidPasswordException;
import com.elitecore.aaa.core.authprotocol.exception.UserNotFoundException;
import com.elitecore.aaa.core.data.AccountData;
import com.elitecore.aaa.core.subscriber.IAccountInfoProvider;
import com.elitecore.aaa.diameter.service.DiameterServiceContext;
import com.elitecore.aaa.diameter.service.application.ApplicationRequest;
import com.elitecore.aaa.diameter.service.application.ApplicationResponse;
import com.elitecore.aaa.diameter.service.application.handlers.conf.DiameterAuthenticationHandlerData;
import com.elitecore.aaa.diameter.subscriber.DiameterSubscriberProfileRepository;
import com.elitecore.commons.logging.LogLevel;
import com.elitecore.commons.logging.LogManager;
import com.elitecore.core.commons.InitializationFailedException;
import com.elitecore.diameterapi.diameter.common.packet.avps.IDiameterAVP;
import com.elitecore.diameterapi.diameter.common.util.DiameterDictionary;
import com.elitecore.diameterapi.diameter.common.util.DiameterUtility;
import com.elitecore.diameterapi.diameter.common.util.constant.DiameterAVPConstants;
import com.elitecore.diameterapi.diameter.common.util.constant.DiameterErrorMessageConstants;
import com.elitecore.diameterapi.diameter.common.util.constant.ResultCode;
import com.elitecore.passwordutil.DecryptionFailedException;
import com.elitecore.passwordutil.NoSuchEncryptionException;

public class DiameterCHAPMethodHandler extends BaseAuthMethodHandler implements DiameterAuthMethodHandler{
	private static final String MODULE = "NAS-AUTH-CHAP-HND"; 
	private static final String CHAP_IDENT = DiameterAVPConstants.CHAP_AUTH + "." + DiameterAVPConstants.CHAP_IDENT;
	private static final String CHAP_RESPONSE = DiameterAVPConstants.CHAP_AUTH + "." + DiameterAVPConstants.CHAP_RESPONSE;
	private DiameterAuthenticationHandlerData data;

	public DiameterCHAPMethodHandler(DiameterServiceContext context, DiameterAuthenticationHandlerData data) {
		super(context);
		this.data = data;
	}

	@Override
	public void init() throws InitializationFailedException {
		LogManager.getLogger().info(MODULE, "Initialized NAS CHAP Method handler for policy " + data.getPolicyName());
		
	}

	@Override
	public boolean isEligible(ApplicationRequest request) {
		boolean isEligible = false;
		isEligible = request.getAVP(DiameterAVPConstants.CHAP_AUTH) != null &&		
					 request.getAVP(DiameterAVPConstants.CHAP_CHALLENGE) != null && 
					 request.getAVP(CHAP_IDENT) != null;
		
		if(isEligible){
			if(LogManager.getLogger().isLogLevel(LogLevel.DEBUG))
				LogManager.getLogger().debug(MODULE, "Request eligible for CHAP authentication.");
		}else{
			if(LogManager.getLogger().isLogLevel(LogLevel.DEBUG))
				LogManager.getLogger().debug(MODULE, "Request is not eligible for CHAP authentication.");
		}
		return isEligible;		
	}
	
	@Override
	public void handleRequest(ApplicationRequest request, ApplicationResponse response,
			IAccountInfoProvider accountInfoProvider)
			throws AuthenticationFailedException {
		
		LogManager.getLogger().info(MODULE, "Processing request using CHAP Method.");
		AccountData accountData = accountInfoProvider.getAccountData(request,response); 
		if (accountData == null) {
			accountData = ((DiameterSubscriberProfileRepository)accountInfoProvider).getAnonymousAccountData(request);
			if (accountData == null) {
				throw new UserNotFoundException();
			}
		}
		
		String passwordCheck = accountData.getPasswordCheck();
		
		if(passwordCheck == null || !passwordCheck.equalsIgnoreCase("NO") || !passwordCheck.equalsIgnoreCase("FALSE")){
			
			IDiameterAVP chapIdent = request.getAVP(CHAP_IDENT);
			IDiameterAVP chapResponse = request.getAVP(CHAP_RESPONSE);			
			IDiameterAVP chapChallenegeAvp = request.getAVP(DiameterAVPConstants.CHAP_CHALLENGE);
			
			if(chapResponse == null){
				// Encrypted Password is not send by client hence Invalid Password
				throw new AuthenticationFailedException(DiameterErrorMessageConstants.AUTHENTICATION_FAILED_DUE_INVALID_PASSWORD);
			}
			
			if (chapIdent.getValueBytes().length == 0) {
				if (LogManager.getLogger().isWarnLogLevel()) {
					LogManager.getLogger().warn(MODULE, "Invalid AVP reason: CHAP-Ident attribute does not "
							+ "contain any value for Session-Id: "+ request.getAVP(DiameterAVPConstants.SESSION_ID).getStringValue());
				}
				DiameterUtility.addFailedAVP(response.getDiameterAnswer(), 
						DiameterDictionary.getInstance().getAttribute(DiameterAVPConstants.CHAP_IDENT));
				
				throw new InvalidPasswordException(DiameterErrorMessageConstants.AUTHENTICATION_FAILED_DUE_TO_INVALID_AVP);
			}
			
			byte[] chapPswdAttributeBytes = chapResponse.getValueBytes();	
			byte[] requestAuthenticator   = chapChallenegeAvp.getValueBytes();
			byte[] chapIdentBytes = chapIdent.getValueBytes();
			byte chapId = chapIdentBytes[chapIdentBytes.length - 1];
			int encryptionType = 0;
			
			if(accountData.getEncryptionType() != null){
				try {
					encryptionType  =Integer.parseInt(accountData.getEncryptionType());
				} catch (NumberFormatException e) {
					throw new AuthenticationFailedException(DiameterErrorMessageConstants.AUTHENTICATION_FAILED_DUE_TO_INVALID_ENCRYPTION_TYPE,e);
				}									
			}
			
			try{
				CHAP.handleCHAPAuthentication(accountData.getPassword(), chapPswdAttributeBytes, encryptionType, requestAuthenticator,chapId);
			}catch (NoSuchEncryptionException e) {
				throw new AuthenticationFailedException(DiameterErrorMessageConstants.AUTHENTICATION_FAILED_DUE_TO_UNSUPPORTED_ENCRYPTION,e);
			} catch (com.elitecore.passwordutil.DecryptionNotSupportedException e) {
				throw new AuthenticationFailedException(DiameterErrorMessageConstants.AUTHENTICATION_FAILED_DUE_TO_IMPROPER_PASSWORD_ENCRYPTION_FORMAT,e);
			} catch (InvalidPasswordException ipe) {
				throw new InvalidPasswordException(DiameterErrorMessageConstants.AUTHENTICATION_FAILED_DUE_INVALID_PASSWORD, ipe);
			} catch (DecryptionFailedException e) {
				throw new AuthenticationFailedException(DiameterErrorMessageConstants.AUTHENTICATION_FAILED_DUE_TO_EXCEPTION_IN_DECRYPTION,e);
			}
			
		}else{
			if(LogManager.getLogger().isLogLevel(LogLevel.DEBUG))
				LogManager.getLogger().debug(MODULE, "Password check flag is set to: "+passwordCheck+", skipping the password check.");
		}
		request.setAccountData(accountData);
		
		DiameterUtility.addOrReplaceAvp(DiameterAVPConstants.RESULT_CODE, response.getDiameterAnswer(), ResultCode.DIAMETER_SUCCESS.code + "");
		LogManager.getLogger().debug(MODULE, "Authentication success,result code: "+ ResultCode.DIAMETER_SUCCESS);
		
	}

	@Override
	public void reInit() throws InitializationFailedException {
		//As it does not cache any configuration so no need to reinitialize
	}

}
