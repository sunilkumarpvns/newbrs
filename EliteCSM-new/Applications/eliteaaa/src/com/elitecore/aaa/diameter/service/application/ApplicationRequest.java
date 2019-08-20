package com.elitecore.aaa.diameter.service.application;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.Nullable;

import com.elitecore.aaa.core.data.AccountData;
import com.elitecore.aaa.radius.service.handlers.RadiusRequestExecutor;
import com.elitecore.core.serverx.servicepolicy.BaseServicePolicy;
import com.elitecore.core.servicex.base.BaseServiceRequest;
import com.elitecore.diameterapi.diameter.common.packet.DiameterRequest;
import com.elitecore.diameterapi.diameter.common.packet.avps.IDiameterAVP;

public class ApplicationRequest extends BaseServiceRequest {
	
	public static final String EC_VSA_ADDED = "VSA_ADDED";
	private BaseServicePolicy<ApplicationRequest> applicationPolicy;
	private DiameterRequest request;		
	private AccountData accountData = null;
	private RadiusRequestExecutor<ApplicationRequest, ApplicationResponse> executor;
	private RadiusRequestExecutor<ApplicationRequest, ApplicationResponse> postResponseExecutor;
	
	public ApplicationRequest(DiameterRequest request) {			
		super();
		this.request = request;
	}
	public List<IDiameterAVP> getAvps(){
		return this.request.getAVPList();
	}
	public DiameterRequest getDiameterRequest(){
		return this.request;	
	}

	@Override
	public Object getParameter(String str) {				
		return request.getParameter(str);
	}
	@Override
	public void setParameter(String key, Object parameterValue) {
		request.setParameter(key,parameterValue);	
	}
	
	
	public IDiameterAVP getAVP(String strAvpCode){
		return this.request.getAVP(strAvpCode);			
	}
	
	public IDiameterAVP getAVP(String strAvpCode,boolean bIncludeInfoAttr){
		return this.request.getAVP(strAvpCode,bIncludeInfoAttr);			
	}

	public List<IDiameterAVP> getAVPs(long vendorId,int avpId){
		return this.request.getVendorSpeficAvps(vendorId, avpId);			
	}
	
	public void setApplicationPolicy(BaseServicePolicy<ApplicationRequest> applicationPolicy) {
		this.applicationPolicy = applicationPolicy;
	}
	
	public BaseServicePolicy<ApplicationRequest> getApplicationPolicy() {
		return applicationPolicy;
	}
	
	@Override 
	public String toString(){
		return request.toString();
	}
	
	public ArrayList<IDiameterAVP> getAVPList(String strAVPCode) {
		return request.getAVPList(strAVPCode);
	}
	
	public ArrayList<IDiameterAVP> getAVPList(String strAVPCode,boolean bIncludeInfoAvp){
		return request.getAVPList(strAVPCode,bIncludeInfoAvp);
	}
	
	public void addInfoAvp(IDiameterAVP avp){
		this.request.addInfoAvp(avp);
	}
	
	public IDiameterAVP getInfoAvp(String avpId){
		return this.request.getInfoAVP(avpId);
	}
	public AccountData getAccountData() {
		return accountData;
	}
	public void setAccountData(AccountData accountData) {
		this.accountData = accountData;
	}
	public RadiusRequestExecutor<ApplicationRequest, ApplicationResponse> getExecutor() {
		return executor;
	}
	public void setExecutor(RadiusRequestExecutor<ApplicationRequest, ApplicationResponse> executor) {
		this.executor = executor;
		
	}

	public void setPostResponseExecutor(
			@Nullable RadiusRequestExecutor<ApplicationRequest, ApplicationResponse> postResponseExecutor) {
		this.postResponseExecutor = postResponseExecutor;
	}
	
	public @Nullable RadiusRequestExecutor<ApplicationRequest, ApplicationResponse> getPostResponseExecutor() {
		return postResponseExecutor;
	}
	
}