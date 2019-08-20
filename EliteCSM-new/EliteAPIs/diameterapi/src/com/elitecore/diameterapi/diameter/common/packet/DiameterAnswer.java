/**
 * 
 */
package com.elitecore.diameterapi.diameter.common.packet;

import com.elitecore.diameterapi.diameter.common.packet.avps.IDiameterAVP;
import com.elitecore.diameterapi.diameter.common.util.DiameterDictionary;
import com.elitecore.diameterapi.diameter.common.util.DiameterUtility;
import com.elitecore.diameterapi.diameter.common.util.Parameter;
import com.elitecore.diameterapi.diameter.common.util.constant.CommandCode;
import com.elitecore.diameterapi.diameter.common.util.constant.DiameterAVPConstants;
import com.elitecore.diameterapi.diameter.common.util.constant.ResultCode;
import com.elitecore.diameterapi.diameter.common.util.constant.ResultCodeCategory;

/**
 * @author pulin
 *
 */
public class DiameterAnswer extends DiameterPacket {
	private long requestReceivedTime = 0;
	private String answeringHost;
	
	public DiameterAnswer(DiameterRequest diameterRequest, ResultCode resultCode) {
		setHeaderFrom(diameterRequest);
		
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
				cloneAndAdd(diameterRequest.getAVP(DiameterAVPConstants.ACCOUNTING_RECORD_NUMBER));
				cloneAndAdd(diameterRequest.getAVP(DiameterAVPConstants.ACCOUNTING_SUB_SESSION_ID));
				
			}else if(diameterRequest.getCommandCode() == CommandCode.SPENDING_LIMIT.code){
				cloneAndAdd(diameterRequest.getAVP(DiameterAVPConstants.TGPP_SL_REQUEST_TYPE));
			
				// Adding EAP and NAS Auth Application related AVPs.
			} else if (diameterRequest.getCommandCode() == CommandCode.DIAMETER_EAP.code ||
					diameterRequest.getCommandCode() == CommandCode.AUTHENTICATION_AUTHORIZATION.code) {
				cloneAndAdd(diameterRequest.getAVP(DiameterAVPConstants.AUTH_REQUEST_TYPE));
			}
		}
		
		setRcvdOnStream(diameterRequest.getRcvdOnStream());
		
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

	public void setHeaderFrom(DiameterRequest diameterRequest) {
		setRequestReceivedTime(diameterRequest.creationTimeMillis());
		setCommandCode(diameterRequest.getCommandCode());
		setApplicationID(diameterRequest.getApplicationID());
		setHop_by_hopIdentifier(diameterRequest.getHop_by_hopIdentifier());
		setEnd_to_endIdentifier(diameterRequest.getEnd_to_endIdentifier());
		if(diameterRequest.isProxiable()) {
			setProxiableBit();
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
		
	}

	@Override
	public DiameterRequest getAsDiameterRequest() {
		return null;
	}

	@Override
	public DiameterAnswer getAsDiameterAnswer() {
		return this;
	}

	public long getRequestReceivedTime() {
		return requestReceivedTime;
	}

	public void setRequestReceivedTime(long requestReceivedTime) {
		this.requestReceivedTime = requestReceivedTime;
	}
	
	public String getAnsweringHost() {
		return answeringHost;
	}

	//TODO set this entity
	public void setAnsweringHost(String answeringHost) {
		this.answeringHost = answeringHost;
	}

	/**
	 * @return true if the answer is of failure category, false otherwise. Returns false in case
	 * when result code avp is absent.
	 */
	public boolean isFailureCategory() {
		IDiameterAVP resultCode = this.getAVP(DiameterAVPConstants.RESULT_CODE);
		if (resultCode == null) {
			return false;
		}
		return ResultCodeCategory.getResultCodeCategory(resultCode.getInteger()).isFailureCategory;
	}
}
