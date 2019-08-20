package com.elitecore.client.eap.methods;

import java.util.ArrayList;
import java.util.List;

import javax.naming.AuthenticationException;

import com.elitecore.client.configuration.PeapConfiguration;
import com.elitecore.client.eap.EapMethodAuthenticator;
import com.elitecore.client.util.constants.CommunicationStates;
import com.elitecore.commons.logging.LogManager;
import com.elitecore.coreeap.dictionary.EAPTypeDictionary;
import com.elitecore.coreeap.packet.types.EAPType;
import com.elitecore.coreeap.packet.types.gtc.GtcEAPType;
import com.elitecore.coreeap.util.constants.EapTypeConstants;

/**
 * 
 * @author Kuldeep Panchal
 * @author Malav Desai
 *
 */
public class GTCAuthenticator implements EapMethodAuthenticator {
	
	private String MODULE = "GTC_Authenticator";
	private CommunicationStates state;
	private final String innerIdentity;
	private final String innerPasswd;
	private List<EAPType> eapTypes;

	public GTCAuthenticator(PeapConfiguration configuration) {
		state = CommunicationStates.CONTINUE;
		innerIdentity = configuration.getInnerIdentity();
		innerPasswd = configuration.getInnerPasswd();
		eapTypes = new ArrayList<EAPType>(3);
	}

	@Override
	public void reset() {
		state = CommunicationStates.CONTINUE;
		eapTypes = new ArrayList<EAPType>(3);
	}

	@Override
	public CommunicationStates process(byte[] inTlsData) throws AuthenticationException {
		LogManager.getLogger().info(MODULE, "Current State: " + state);
		switch (state) {
		case CONTINUE:
			actionOnRecivedData(inTlsData);
			break;
		case COMPLETED_PROCESSING:
			break;

		default:
			LogManager.getLogger().error(MODULE, "Not in proper state. Something in fishy");
			throw new AuthenticationException(MODULE + " not in proper state");
		}
		return state;
	}

	private void actionOnRecivedData(byte[] inTlsData) {
		GtcEAPType gtcEAPType = (GtcEAPType) EAPTypeDictionary.getInstance().createEAPType(EapTypeConstants.GTC.typeId);
		gtcEAPType.setData(innerPasswd.getBytes());
		eapTypes.add(gtcEAPType);
	}

	@Override
	public byte[] getOutData() {
		return eapTypes.remove(0).toBytes();
	}
}
