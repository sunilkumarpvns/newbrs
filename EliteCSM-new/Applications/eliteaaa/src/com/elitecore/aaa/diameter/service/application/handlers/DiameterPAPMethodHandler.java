package com.elitecore.aaa.diameter.service.application.handlers;

import com.elitecore.aaa.core.authprotocol.BaseAuthMethodHandler;
import com.elitecore.aaa.core.authprotocol.PAP;
import com.elitecore.aaa.core.authprotocol.exception.AuthenticationFailedException;
import com.elitecore.aaa.core.authprotocol.exception.InvalidPasswordException;
import com.elitecore.aaa.core.authprotocol.exception.UserNotFoundException;
import com.elitecore.aaa.core.data.AccountData;
import com.elitecore.aaa.core.subscriber.IAccountInfoProvider;
import com.elitecore.aaa.diameter.service.application.ApplicationRequest;
import com.elitecore.aaa.diameter.service.application.ApplicationResponse;
import com.elitecore.aaa.diameter.service.application.handlers.conf.DiameterAuthenticationHandlerData;
import com.elitecore.aaa.diameter.subscriber.DiameterSubscriberProfileRepository;
import com.elitecore.commons.logging.LogLevel;
import com.elitecore.commons.logging.LogManager;
import com.elitecore.core.commons.InitializationFailedException;
import com.elitecore.core.servicex.ServiceContext;
import com.elitecore.diameterapi.diameter.common.util.DiameterUtility;
import com.elitecore.diameterapi.diameter.common.util.constant.DiameterAVPConstants;
import com.elitecore.diameterapi.diameter.common.util.constant.DiameterErrorMessageConstants;
import com.elitecore.diameterapi.diameter.common.util.constant.ResultCode;
import com.elitecore.passwordutil.EncryptionFailedException;
import com.elitecore.passwordutil.NoSuchEncryptionException;

public class DiameterPAPMethodHandler extends BaseAuthMethodHandler implements
		DiameterAuthMethodHandler {
	private final static String MODULE = "NAS-AUTH-PAP-HND";
	private boolean bTrimPassword;
	private DiameterAuthenticationHandlerData data;
	
	public DiameterPAPMethodHandler(ServiceContext context, DiameterAuthenticationHandlerData data) {
		super(context);
		this.data = data;
		bTrimPassword = data.getSubscriberProfileRepositoryDetails().getUpdateIdentity().getIsTrimPassword();
	}

	@Override
	public boolean isEligible(ApplicationRequest request) {
		boolean isEligible = false;
		isEligible = request.getAVP(DiameterAVPConstants.USER_PASSWORD) != null;
		if(isEligible){
			if(LogManager.getLogger().isLogLevel(LogLevel.DEBUG))
				LogManager.getLogger().debug(MODULE, "Request eligible for PAP authentication.");
		}else{
			if(LogManager.getLogger().isLogLevel(LogLevel.DEBUG))
				LogManager.getLogger().debug(MODULE, "Request is not eligible for PAP authentication.");
		}
			
		return isEligible;
	}

	@Override
	public void init() throws InitializationFailedException {
		LogManager.getLogger().info(MODULE, "Initialized NAS PAP Method handler for policy " + data.getPolicyName());

	}

	@Override
	public void handleRequest(ApplicationRequest request, ApplicationResponse response, IAccountInfoProvider accountInfoProvider)
		throws AuthenticationFailedException {
		LogManager.getLogger().info(MODULE, "Processing request using PAP Method.");
		AccountData accountData = accountInfoProvider.getAccountData(request,response); 
		if (accountData == null) {
			accountData = ((DiameterSubscriberProfileRepository)accountInfoProvider).getAnonymousAccountData(request);
			if (accountData == null) {
				throw new UserNotFoundException();
			}
		}
		String passwordCheck = accountData.getPasswordCheck();
		if(passwordCheck == null || !passwordCheck.equalsIgnoreCase("NO")){
			String decodedPassword = request.getAVP(DiameterAVPConstants.USER_PASSWORD).getStringValue();			
			if(bTrimPassword)
				decodedPassword = decodedPassword.trim();
			int encryptionType = 1;
			if(accountData.getEncryptionType() != null){
				try{
					encryptionType = Integer.parseInt(accountData.getEncryptionType());
				}catch(NumberFormatException nfe){
					throw new AuthenticationFailedException(DiameterErrorMessageConstants.AUTHENTICATION_FAILED_DUE_TO_INVALID_ENCRYPTION_TYPE,nfe);
				}
			}
			try{
				PAP.handlePAPAuthentication(decodedPassword,accountData.getPassword(),encryptionType);
			}catch(InvalidPasswordException ipe){
				if(LogManager.getLogger().isLogLevel(LogLevel.TRACE)){										
					LogManager.getLogger().trace(MODULE, "Invalid User-Password  received.");					
				}
				throw new InvalidPasswordException(DiameterErrorMessageConstants.AUTHENTICATION_FAILED_DUE_INVALID_PASSWORD,ipe);
			} catch (NoSuchEncryptionException e) {
				throw new AuthenticationFailedException(DiameterErrorMessageConstants.AUTHENTICATION_FAILED_DUE_TO_UNSUPPORTED_ENCRYPTION,e);
			} catch (EncryptionFailedException e) {
				throw new AuthenticationFailedException(DiameterErrorMessageConstants.AUTHENTICATION_FAILED_DUE_TO_EXCEPTION_IN_ENCRYPTION,e);
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
	}

}
