/**
 * 
 */
package com.elitecore.test.dependecy.diameter.packet;

import com.elitecore.test.dependecy.diameter.DiameterAVPConstants;
import com.elitecore.test.dependecy.diameter.DiameterDictionary;
import com.elitecore.test.dependecy.diameter.DiameterUtility;
import com.elitecore.test.dependecy.diameter.Parameter;
import com.elitecore.test.dependecy.diameter.packet.avps.IDiameterAVP;

import java.util.HashMap;
import java.util.Map;

/**
 * @author pulin
 *
 */
public class DiameterAnswer extends DiameterPacket {
	private Map<String, Object> parameterMap;
	private long requestReceivedTime = 0;
	public DiameterAnswer(DiameterRequest diameterRequest, ResultCode resultCode) {
		setRequestReceivedTime(diameterRequest.creationTimeMillis());
		parameterMap = new HashMap<String, Object>();
		setCommandCode(diameterRequest.getCommandCode());
		setApplicationID(diameterRequest.getApplicationID());
		setHop_by_hopIdentifier(diameterRequest.getHop_by_hopIdentifier());
		setEnd_to_endIdentifier(diameterRequest.getEnd_to_endIdentifier());
		if(diameterRequest.isProxiable()) {
			setProxiableBit();
		}
		
		if(!DiameterUtility.isBaseProtocolPacket(getCommandCode())) {
			
			cloneAndAdd(diameterRequest.getAVP(DiameterAVPConstants.AUTH_APPLICATION_ID));

			cloneAndAdd(diameterRequest.getAVP(DiameterAVPConstants.ACCT_APPLICATION_ID));

			cloneAndAdd(diameterRequest.getAVP(DiameterAVPConstants.VENDOR_SPECIFIC_APPLICATION_ID));

			cloneAndAdd(diameterRequest.getAVP(DiameterAVPConstants.SESSION_ID));
			
			// Adding Credit-Control application related avps
			if(diameterRequest.getCommandCode() == CommandCode.CREDIT_CONTROL.code) {
				cloneAndAdd(diameterRequest.getAVP(DiameterAVPConstants.CC_REQUEST_TYPE));
				
				cloneAndAdd(diameterRequest.getAVP(DiameterAVPConstants.CC_REQUEST_NUMBER));
				
			}else if(diameterRequest.getCommandCode() == CommandCode.ACCOUNTING.code){
				cloneAndAdd(diameterRequest.getAVP(DiameterAVPConstants.ACCOUNTING_RECORD_TYPE));
				
			}else if(diameterRequest.getCommandCode() == CommandCode.SPENDING_LIMIT.code){
				cloneAndAdd(diameterRequest.getAVP(DiameterAVPConstants.TGPP_SL_REQUEST_TYPE));
			}
		}
		
		// Adding Origin Host as must for all types of Diameter Message
		IDiameterAVP originHost = (DiameterDictionary.getInstance().getAttribute(DiameterAVPConstants.ORIGIN_HOST));
		originHost.setStringValue(Parameter.getInstance().getOwnDiameterIdentity());
		addAvp(originHost);

		// Adding Origin Host as must for all types of Diameter Message
		IDiameterAVP originRealm = (DiameterDictionary.getInstance().getAttribute(DiameterAVPConstants.ORIGIN_REALM));
		originRealm.setStringValue(Parameter.getInstance().getOwnDiameterRealm());
		addAvp(originRealm);



		if(resultCode != null) {
			IDiameterAVP resultCodeAVP = DiameterDictionary.getInstance().getAttribute(DiameterAVPConstants.RESULT_CODE);
			resultCodeAVP.setInteger(resultCode.getCode());
			addAvp(resultCodeAVP);
			
			if(resultCode != ResultCode.DIAMETER_SUCCESS) {
				setErrorBit();
			}
		}
	}

	private void cloneAndAdd(IDiameterAVP diameterAvp) {
		if(diameterAvp == null) {
			return;
		}
		try {
			addAvp((IDiameterAVP) diameterAvp.clone());
		} catch (CloneNotSupportedException e) {
		}
	}

	public DiameterAnswer(DiameterRequest diameterRequest) {
		this(diameterRequest, null);
	}
	
	public DiameterAnswer() {
		parameterMap = new HashMap<String, Object>();
	}

	public Object getParameter(String str) {			
		return parameterMap.get(str);
	}

	public void setParameter(String key, Object parameterValue) {			
		parameterMap.put(key, parameterValue);
	}

	public long getRequestReceivedTime() {
		return requestReceivedTime;
	}

	public void setRequestReceivedTime(long requestReceivedTime) {
		this.requestReceivedTime = requestReceivedTime;
	}

}
