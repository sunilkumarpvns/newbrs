package com.elitecore.aaa.radius.translators;

import com.elitecore.coreradius.commons.attributes.IRadiusAttribute;
import com.elitecore.coreradius.commons.packet.RadiusPacket;

/**
 * This provides decrypted value of attribute from radius packet which have encryption standard like-wise
 * cisco-password(9:249) attribute based on shared secret and packet authenticator.
 * @author chirag.i.prajapati
 */
public class RadiusAttributeDecryptedValueProvider extends RadiusAttributeValueProvider {

	private String sharedSecret;

	public RadiusAttributeDecryptedValueProvider(RadiusPacket packet, String sharedSecret) {
		super(packet);
		this.sharedSecret = sharedSecret;
	}
	
	@Override
	protected String getStringValue(IRadiusAttribute attribute) {
		return attribute.getStringValue(sharedSecret, getAuthenticator());
	}
}
