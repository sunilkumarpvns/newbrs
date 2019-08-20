package com.elitecore.aaa.radius.service.auth.handlers;


import com.elitecore.aaa.core.authprotocol.BaseAuthMethodHandler;
import com.elitecore.aaa.core.authprotocol.CHAP;
import com.elitecore.aaa.core.authprotocol.exception.AuthenticationFailedException;
import com.elitecore.aaa.core.authprotocol.exception.InvalidPasswordException;
import com.elitecore.aaa.core.authprotocol.exception.UserNotFoundException;
import com.elitecore.aaa.core.constant.AuthMethods;
import com.elitecore.aaa.core.data.AccountData;
import com.elitecore.aaa.core.subscriber.IAccountInfoProvider;
import com.elitecore.aaa.radius.service.RadServiceResponse;
import com.elitecore.aaa.radius.service.auth.RadAuthRequest;
import com.elitecore.aaa.radius.service.auth.RadAuthResponse;
import com.elitecore.aaa.radius.service.auth.RadAuthServiceContext;
import com.elitecore.aaa.radius.service.auth.constant.AuthReplyMessageConstant;
import com.elitecore.commons.logging.LogLevel;
import com.elitecore.commons.logging.LogManager;
import com.elitecore.core.commons.InitializationFailedException;
import com.elitecore.coreradius.commons.util.constants.RadiusAttributeConstants;
import com.elitecore.coreradius.commons.util.constants.RadiusConstants;
import com.elitecore.passwordutil.DecryptionFailedException;
import com.elitecore.passwordutil.DecryptionNotSupportedException;
import com.elitecore.passwordutil.NoSuchEncryptionException;

public class RadCHAPHandler extends BaseAuthMethodHandler implements IRadAuthMethodHandler{
	
	private static final String MODULE = "RAD-CHAP-HND";	
	
	public RadCHAPHandler(RadAuthServiceContext serviceContext){
		super(serviceContext);
	}
	
	public void handleRequest(RadAuthRequest request,RadAuthResponse response, IAccountInfoProvider accountInfoProvider) throws AuthenticationFailedException {	
		AccountData accountData = accountInfoProvider.getAccountData(request,response); 
		if(accountData==null)
			throw new UserNotFoundException();
		
		String passwordCheck = accountData.getPasswordCheck();
		
		if(passwordCheck == null || !passwordCheck.equalsIgnoreCase("NO")){
			byte[] chapPswdAttributeBytes = request.getRadiusAttribute(RadiusAttributeConstants.CHAP_PASSWORD).getValueBytes();			
			byte chapId = chapPswdAttributeBytes[0];
			byte[] requestAuthenticator = request.getAuthenticator();
			byte[] chapPasswordBytes = new byte[chapPswdAttributeBytes.length - 1];
			System.arraycopy(chapPswdAttributeBytes, 1, chapPasswordBytes, 0, chapPswdAttributeBytes.length - 1);
			if(request.getRadiusAttribute(RadiusAttributeConstants.CHAP_CHALLENGE) != null){
				byte[] chapChallengeBytes = request.getRadiusAttribute(RadiusAttributeConstants.CHAP_CHALLENGE).getValueBytes();
				requestAuthenticator = chapChallengeBytes;
			}else{
				if(LogManager.getLogger().isLogLevel(LogLevel.DEBUG))
					LogManager.getLogger().debug(MODULE, "CHAP-Challenge attribute not found. Considering the Request-Authenticator as CHAP-Challenge for validating the CHAP-Password.");
			}
			int encryptionType = 0;
			if(accountData.getEncryptionType() != null){
				try {
					encryptionType  =Integer.parseInt(accountData.getEncryptionType());
				} catch (NumberFormatException e) {
					throw new AuthenticationFailedException("Authentication failed due to invalid encryption type.",e);
				}									
			}
			try{
				CHAP.handleCHAPAuthentication(accountData.getPassword(), chapPasswordBytes, encryptionType, requestAuthenticator,chapId);
			}catch (NoSuchEncryptionException e) {
				throw new AuthenticationFailedException("Authentication failed due to unsupported encryption.",e);
			} catch (DecryptionNotSupportedException e) {
				throw new AuthenticationFailedException("Authentication failed due to improper password encryption format.",e);
			} catch (InvalidPasswordException ipe) {
				throw new InvalidPasswordException("Authentication Failed due to Invalid Password");
			} catch (DecryptionFailedException e) {
				throw new AuthenticationFailedException("Authentication failed due to exception in decryption",e);
			}
			}else{
				if(LogManager.getLogger().isLogLevel(LogLevel.DEBUG))
					LogManager.getLogger().debug(MODULE, "Password check flag is set to: "+passwordCheck+", skipping the password check.");
			}			
			((RadServiceResponse)response).setResponseMessage(AuthReplyMessageConstant.AUTHENTICATION_SUCCESS);		
			((RadServiceResponse)response).setPacketType(RadiusConstants.ACCESS_ACCEPT_MESSAGE);
	}
	
	public boolean isEligible(RadAuthRequest request){
		boolean isEligible = request.getRadiusAttribute(RadiusAttributeConstants.CHAP_PASSWORD) != null;
		if(isEligible){
			if(LogManager.getLogger().isLogLevel(LogLevel.DEBUG))
				LogManager.getLogger().debug(MODULE, "Request eligible for CHAP authentication.");
		}
		return isEligible;
	}

	@Override
	public void init() throws InitializationFailedException {
		//no configuration to read for CHAP...
		
	}
	@Override
	public String toString() {
		return "    		CHAP Authentication";
	}

	@Override
	public void reInit() throws InitializationFailedException {
		//As this class does not store any configuration so no need for reinitializing
	}

	@Override
	public int getMethodType() {
		return AuthMethods.CHAP;
	}

}
