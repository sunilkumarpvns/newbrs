package com.elitecore.aaa.radius.service.auth.handlers;

import com.elitecore.aaa.core.authprotocol.BaseAuthMethodHandler;
import com.elitecore.aaa.core.authprotocol.LDAP;
import com.elitecore.aaa.core.authprotocol.PAP;
import com.elitecore.aaa.core.authprotocol.exception.AuthenticationFailedException;
import com.elitecore.aaa.core.authprotocol.exception.InvalidPasswordException;
import com.elitecore.aaa.core.authprotocol.exception.UserNotFoundException;
import com.elitecore.aaa.core.constant.AuthMethods;
import com.elitecore.aaa.core.constant.DriverTypes;
import com.elitecore.aaa.core.data.AccountData;
import com.elitecore.aaa.core.drivers.DriverConfiguration;
import com.elitecore.aaa.core.server.AAAServerContext;
import com.elitecore.aaa.core.subscriber.IAccountInfoProvider;
import com.elitecore.aaa.radius.service.auth.RadAuthRequest;
import com.elitecore.aaa.radius.service.auth.RadAuthResponse;
import com.elitecore.aaa.radius.service.auth.RadAuthServiceContext;
import com.elitecore.aaa.radius.service.auth.constant.AuthReplyMessageConstant;
import com.elitecore.aaa.radius.service.auth.policy.handlers.conf.AuthenticationHandlerData;
import com.elitecore.aaa.util.constants.AAAServerConstants;
import com.elitecore.commons.logging.LogLevel;
import com.elitecore.commons.logging.LogManager;
import com.elitecore.core.commons.InitializationFailedException;
import com.elitecore.coreradius.commons.util.RadiusUtility;
import com.elitecore.coreradius.commons.util.constants.RadiusAttributeConstants;
import com.elitecore.coreradius.commons.util.constants.RadiusConstants;
import com.elitecore.passwordutil.EncryptionFailedException;
import com.elitecore.passwordutil.NoSuchEncryptionException;

public class RadPAPHandler extends BaseAuthMethodHandler implements IRadAuthMethodHandler{
		
	public static final String MODULE = "RAD_PAP_HANDLER";
	private boolean bTrimPassword;
	
	public RadPAPHandler(RadAuthServiceContext serviceContext, AuthenticationHandlerData data){
		super(serviceContext);
		bTrimPassword = data.getUserProfileRepoDetails().getUpdateIdentity().getIsTrimPassword();
	}

	public void handleRequest(RadAuthRequest radiusServiceRequest,RadAuthResponse radiusServiceResponse, IAccountInfoProvider accountInfoProvider) throws AuthenticationFailedException {
								
		AccountData accountData  =  accountInfoProvider.getAccountData(radiusServiceRequest,radiusServiceResponse); 			
		
		if(accountData == null)
			throw new UserNotFoundException();
		
		String passwordCheck = accountData.getPasswordCheck();
		if(passwordCheck == null || !passwordCheck.equalsIgnoreCase("NO")){
			byte[] userPasswordBytes = radiusServiceRequest.getRadiusAttribute(RadiusAttributeConstants.USER_PASSWORD).getValueBytes();			
			String decodedPassword = RadiusUtility.decryptPasswordRFC2865(userPasswordBytes,radiusServiceRequest.getAuthenticator(),radiusServiceResponse.getClientData().getSharedSecret(radiusServiceRequest.getPacketType()));
			if(bTrimPassword)
				decodedPassword = decodedPassword.trim();
			int encryptionType = 0;
			if(accountData.getEncryptionType() != null){
				try{
					encryptionType = Integer.parseInt(accountData.getEncryptionType());
				}catch(NumberFormatException nfe){
					throw new AuthenticationFailedException("Authentication failed due to invalid encryption type.",nfe);
				}
			}
			
			DriverConfiguration driverConfiguration = ((AAAServerContext)getServiceContext().getServerContext()).getServerConfiguration().getDriverConfigurationProvider().getDriverConfiguration((String)radiusServiceRequest.getParameter(AAAServerConstants.DRIVER_INSTANCE_ID));
			if (passwordCheck.equalsIgnoreCase("bind") && driverConfiguration.getDriverType() == DriverTypes.RAD_LDAP_AUTH_DRIVER) {
				try{
					LDAP.verifyPassword(driverConfiguration, (String)radiusServiceRequest.getParameter(AAAServerConstants.DN), decodedPassword);
				}catch (InvalidPasswordException e) {
					throw e;
				}
			}else{
				try{
					PAP.handlePAPAuthentication(decodedPassword,accountData.getPassword(),encryptionType);
				}catch(InvalidPasswordException ipe){
					if(LogManager.getLogger().isLogLevel(LogLevel.TRACE)){
						if(accountData.getPassword()!=null){
							byte[] encodedPassword = RadiusUtility.encryptPasswordRFC2865(accountData.getPassword(),radiusServiceRequest.getAuthenticator(),radiusServiceResponse.getClientData().getSharedSecret(radiusServiceRequest.getPacketType()));					
							LogManager.getLogger().trace(MODULE, "Received User-Password : "+ RadiusUtility.bytesToHex(userPasswordBytes) + ", expected User-Password :" + RadiusUtility.bytesToHex(encodedPassword));
						}else{
							if(LogManager.getLogger().isLogLevel(LogLevel.INFO))
								LogManager.getLogger().info(MODULE, "User-Password not found in subscriber profile of user-identity : "+accountData.getUserIdentity());
						}
					}
					throw new InvalidPasswordException("Authentication Failed due to Invalid Password",ipe);
				} catch (NoSuchEncryptionException e) {
					throw new AuthenticationFailedException("Authentication failed due to unsupported encryption.",e);
				} catch (EncryptionFailedException e) {
					throw new AuthenticationFailedException("Authentication failed due to exception in encryption.",e);
				}
			}
		}else{
			if(LogManager.getLogger().isLogLevel(LogLevel.DEBUG))
				LogManager.getLogger().debug(MODULE, "Password check flag is set to: "+passwordCheck+", skipping the password check.");
		}
		radiusServiceResponse.setFurtherProcessingRequired(true);
		radiusServiceResponse.setResponseMessage(AuthReplyMessageConstant.AUTHENTICATION_SUCCESS);
		radiusServiceResponse.setPacketType(RadiusConstants.ACCESS_ACCEPT_MESSAGE);
	}	

	public boolean isEligible (RadAuthRequest request){	
		boolean isEligible = (request.getRadiusAttribute(RadiusAttributeConstants.USER_PASSWORD) != null);
		if(isEligible){
			if(LogManager.getLogger().isLogLevel(LogLevel.DEBUG))
				LogManager.getLogger().debug(MODULE, "Request eligible for PAP authentication.");
		}
		return isEligible;		
	}

	@Override
	public void init() throws InitializationFailedException {
		//No configuration to read here for PAP 
	}
	
	@Override
	public String toString() {
		return "    		PAP Authentication";
	}

	@Override
	public void reInit() throws InitializationFailedException {
//		bTrimPassword = ((RadAuthServiceContext)getServiceContext()).getAuthConfiguration().getAuthServicePolicyConfiguraion(authPolicyId).getIsTrimPassword();
	}
	@Override
	public int getMethodType() {
		return AuthMethods.PAP;
	}
	

}
