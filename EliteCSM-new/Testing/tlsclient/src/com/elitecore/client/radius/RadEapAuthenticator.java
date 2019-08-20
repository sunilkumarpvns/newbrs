package com.elitecore.client.radius;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.Random;

import javax.naming.AuthenticationException;

import com.elitecore.client.configuration.EapConfiguration;
import com.elitecore.client.eap.EapAuthenticator;
import com.elitecore.commons.logging.LogManager;
import com.elitecore.core.commons.InitializationFailedException;
import com.elitecore.coreeap.packet.EAPPacket;
import com.elitecore.coreeap.packet.InvalidEAPPacketException;
import com.elitecore.coreeap.util.constants.EapTypeConstants;
import com.elitecore.coreradius.commons.attributes.IRadiusAttribute;
import com.elitecore.coreradius.commons.packet.RadiusPacket;
import com.elitecore.coreradius.commons.util.Dictionary;
import com.elitecore.coreradius.commons.util.RadiusUtility;
import com.elitecore.coreradius.commons.util.constants.RadiusAttributeConstants;
import com.elitecore.coreradius.commons.util.constants.RadiusConstants;

/**
 * RadEapHandler creates EAPPacket from the received RasiusPacket and pass the generated eapPacket to EAPHandler
 * for further processing.
 * 
 * @author Kuldeep Panchal
 * @author Malav Desai
 *
 */
public class RadEapAuthenticator extends EapAuthenticator {
	private static final String MODULE = "RAD_EAP_AUTHENTICATOR";
	private static final int AVP_SIZE = 253;
	private IRadiusAttribute stateAvp;
	
	//TODO repair before commit; whether to keep calling station id functionality or not?  
	private final String CALLING_STATION_ID = String.valueOf(new Random().nextInt());
//	private final String CALLING_STATION_ID = "Elitecore";
//	private final String CALLING_STATION_ID = "AB001234";
	
	public RadEapAuthenticator(EapConfiguration configuration) throws InitializationFailedException {
		super(EapTypeConstants.getEapTypeConstants(configuration.getEapMethod()), configuration, configuration.getIdentity());
	}
	/**
	 * Form EAPPacket from the radiusPacket received and submit it to the EAPHandler
	 * @param receivedRadiusPacket
	 * @return responseRadiusPacket
	 * @throws AuthenticationException
	 */
	public RadiusPacket process(RadiusPacket receivedRadiusPacket) throws AuthenticationException {
		LogManager.getLogger().info(MODULE, "Processing in RadEapHandler");
		ArrayList<IRadiusAttribute> eapMessageList = (ArrayList<IRadiusAttribute>)receivedRadiusPacket.getRadiusAttributes(RadiusAttributeConstants.EAP_MESSAGE);
		byte[] eapPacketBytes = getBytesFromRadiusAtrCollection(eapMessageList);
		EAPPacket receivedEapPacket= null;
		try {
			receivedEapPacket = new EAPPacket(eapPacketBytes);
			EAPPacket sendEapPacket = process(receivedEapPacket);
			RadiusPacket responseRadiusPacket = generateRadiusPacket(sendEapPacket);
			return responseRadiusPacket;
		} catch (InvalidEAPPacketException e) {
			LogManager.getLogger().trace(e);
			throw new AuthenticationException("Cannot parse EAPPacket, Reason: " + e.getMessage());
		}
	}
	
	/**
	 * Generate the RadiusPacket to send by setting its attributes' values. 
	 * This method also takes care of the size of AVP. 
	 * That is making multiple AVP of 253 size if size of AVP is more
	 * @param eapPacket
	 * @param username
	 * @return radiusPacket
	 */
	public RadiusPacket generateRadiusPacket(EAPPacket eapPacket) {
		RadiusPacket radiusPacket = new RadiusPacket();
        IRadiusAttribute radiusAttributeUser = Dictionary.getInstance().getAttribute(RadiusAttributeConstants.USER_NAME);
        radiusAttributeUser.setStringValue(getConfiguration().getUsername());
        radiusPacket.addAttribute(radiusAttributeUser);
        if(stateAvp != null){
        	radiusPacket.addAttribute(stateAvp);
        }
        
        //Add the required attributes
        addRequiredAttributes(radiusPacket);
        
        //Add eap packet in radius packet
        addEapAttributes(radiusPacket, eapPacket);
        
        radiusPacket.setPacketType(RadiusConstants.ACCESS_REQUEST_MESSAGE);
        
        radiusPacket.setIdentifier(RadiusUtility.generateIdentifier());
        
        //add request authenticator and message authenticator
        addRequestAndMsgAuthenticatorAttributes(radiusPacket);
        
        radiusPacket.refreshPacketHeader();
        
        return radiusPacket;
	}
	
	private void addRequiredAttributes(RadiusPacket radiusPacket) {
		System.out.println("Using value of calling station ID as : " + CALLING_STATION_ID);
		//Adding the calling station ID attribute
		IRadiusAttribute callingStationIDAttribute = Dictionary.getInstance().getAttribute(RadiusAttributeConstants.CALLING_STATION_ID);
		callingStationIDAttribute.setStringValue(CALLING_STATION_ID);
		radiusPacket.addAttribute(callingStationIDAttribute);
		
		radiusPacket.refreshPacketHeader();
	}
	
	private void addEapAttributes(RadiusPacket radiusPacket, EAPPacket eapPacket){
		int noOfEapMsg = eapPacket.getLength() / AVP_SIZE + 1;
		byte[] eapPacketBytes = eapPacket.getBytes();
		byte[] eapMsgBytes = null;
		for(int i = 0; i < noOfEapMsg -1; i++) {
			eapMsgBytes = new byte[AVP_SIZE];
			System.arraycopy(eapPacketBytes, i * AVP_SIZE, eapMsgBytes, 0, AVP_SIZE);
			IRadiusAttribute radiusAttributeEapMsg = Dictionary.getInstance().getAttribute(RadiusAttributeConstants.EAP_MESSAGE);
			radiusAttributeEapMsg.setValueBytes(eapMsgBytes);
			radiusPacket.addAttribute(radiusAttributeEapMsg);
		}
		int len = eapPacketBytes.length - ((noOfEapMsg - 1) * AVP_SIZE);
		eapMsgBytes = new byte[len];
		System.arraycopy(eapPacketBytes, (noOfEapMsg -1) * AVP_SIZE, eapMsgBytes, 0, len);
		IRadiusAttribute radiusAttributeEapMsg = Dictionary.getInstance().getAttribute(RadiusAttributeConstants.EAP_MESSAGE);
		radiusAttributeEapMsg.setValueBytes(eapMsgBytes);
		radiusPacket.addAttribute(radiusAttributeEapMsg);
	} 
	
	private void addRequestAndMsgAuthenticatorAttributes(RadiusPacket radiusPacket){
		
        byte[] requestAuthenticatorForAuthentication = RadiusUtility.generateRFC2865RequestAuthenticator();
        radiusPacket.setAuthenticator(requestAuthenticatorForAuthentication);
         
        IRadiusAttribute msgAuthenticatorAttribute = Dictionary.getInstance().getAttribute(RadiusAttributeConstants.MESSAGE_AUTHENTICATOR);				
		radiusPacket.addAttribute(msgAuthenticatorAttribute);
		radiusPacket.refreshPacketHeader();
		msgAuthenticatorAttribute.setValueBytes(RadiusUtility.generateMessageAuthenticator(radiusPacket.getBytes(), requestAuthenticatorForAuthentication, getConfiguration().getSecret()));
	}
	
	private byte[] getBytesFromRadiusAtrCollection(ArrayList<IRadiusAttribute> radiusAttributeCollection) { 
		int length = 0;
		Collection<byte[]> byteArrayCollection = new ArrayList<byte[]>();
		Iterator<IRadiusAttribute> attrItr = radiusAttributeCollection.iterator();
		while(attrItr.hasNext()){
			IRadiusAttribute temp = attrItr.next();
			byteArrayCollection.add(temp.getValueBytes());
			length+=temp.getValueBytes().length;
		}
		
		byte[] resultBytes = new byte[length];
		Iterator<byte[]> byteItr = byteArrayCollection.iterator();
		
		int count = 0;
		while(byteItr.hasNext()){
			byte[] tempBytes = byteItr.next();
			System.arraycopy(tempBytes, 0, resultBytes, count, tempBytes.length);
			count+=tempBytes.length;
		}
		return resultBytes;
	}
	/**
	 * checks whether the request is eligible for EAP Authentication
	 * @param receivedRadiusPacket
	 * @return true if eligible otherwise false
	 */
	public boolean isEligible(RadiusPacket receivedRadiusPacket) {
		return (receivedRadiusPacket.getRadiusAttribute(RadiusAttributeConstants.MESSAGE_AUTHENTICATOR) != null
				&& receivedRadiusPacket.getRadiusAttributes(RadiusAttributeConstants.EAP_MESSAGE) != null);
	}
	public RadiusPacket getIdentityRadiusPacket() {
		EAPPacket identityPacket = getIdentityEapPacket();
		RadiusPacket identityRadiusPacket = generateRadiusPacket(identityPacket);
		return identityRadiusPacket;
	}
	public void setStateAvp(IRadiusAttribute stateAvp) {
		this.stateAvp = stateAvp;
	}
}
