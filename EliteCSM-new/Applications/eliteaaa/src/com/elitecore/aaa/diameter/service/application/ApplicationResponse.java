package com.elitecore.aaa.diameter.service.application;

import com.elitecore.core.servicex.base.BaseServiceResponse;
import com.elitecore.diameterapi.diameter.common.packet.DiameterAnswer;
import com.elitecore.diameterapi.diameter.common.packet.DiameterRequest;
import com.elitecore.diameterapi.diameter.common.packet.avps.IDiameterAVP;

public class ApplicationResponse extends BaseServiceResponse {		
	private DiameterAnswer answer;
	private boolean isFurtherProcessingRequired;
	public ApplicationResponse(DiameterRequest request) {			
		super();
		this.isFurtherProcessingRequired = true;
		this.answer = new DiameterAnswer(request);
	}
	public void setProxiableBit(){
		answer.setProxiableBit();			
	}
	public DiameterAnswer getDiameterAnswer(){
		return this.answer;	
	}
	public void setCommandCode(int code){
		this.answer.setCommandCode(code);			
	}
	@Override
	public Object getParameter(String str) {				
		return answer.getParameter(str);
	}
	@Override
	public void setParameter(String key, Object parameterValue) {
		answer.setParameter(key,parameterValue);	
	}		
	
	public IDiameterAVP getAVP(String strAvpCode){
		return this.answer.getAVP(strAvpCode);			
	}		
	
	public IDiameterAVP getAVP(String strAvpCode,boolean bIncludeInfoAttr){
		return this.answer.getAVP(strAvpCode,bIncludeInfoAttr);			
	}
	
	public void addAVP(IDiameterAVP  avp){
		this.answer.addAvp(avp);			
	}
	public void addInfoAvp(IDiameterAVP avp){
		this.answer.addInfoAvp(avp);
	}
	
	@Override
	public boolean isFurtherProcessingRequired() {

		return isFurtherProcessingRequired;
	}
	@Override
	public void setFurtherProcessingRequired(boolean bIsFurtherProcessingRequired) {
		isFurtherProcessingRequired = bIsFurtherProcessingRequired;			
	}
	@Override
	public String toString(){
		return answer.toString();
	}
		
}