package com.elitecore.nvsmx.policydesigner.model.pkg.qos;

import java.io.Serializable;
import java.util.List;

import com.elitecore.corenetvertex.pkg.qos.QosProfileDetailData;
import com.elitecore.corenetvertex.pkg.quota.QuotaProfileData;
import com.elitecore.corenetvertex.pkg.rnc.RncProfileData;
import com.elitecore.corenetvertex.pkg.syquota.SyQuotaProfileData;
import com.elitecore.nvsmx.system.constants.NVSMXCommonConstants;

/**
 * @author kirpalsinh.raj
 * This wrapper class is used to display Qos Profile Detail in JSP using the DataTable.
 * The inner class 'QosProfileDetailWrapperBuilder' prepares the objects of the 'QosProfileDetailWrapper' from the QosProfile  with the detail of HSQ (fup level 0)
 **/

public class QosProfileDetailWrapper implements Serializable{
    
    
    private static final long serialVersionUID = 1L;
    
	private String id;
    private String name;
    private String qci;
	private String aambrdl;
	private String aambrul;
	private String mbrdl;
	private String mbrul;
	private String action;
	private String quotaProfileName;
	private Integer orderNumber;
	
	
	
	public QosProfileDetailWrapper(String id, String name, Integer orderNumber){
	    this.id = id;
	    this.name = name;
	    this.orderNumber = orderNumber;
	}
	
	public static class QosProfileDetailWrapperBuilder{
	    QosProfileDetailWrapper qosDetailProfileWrapper;
	    
	    public QosProfileDetailWrapperBuilder(String id, String name, Integer orderNumber){
		qosDetailProfileWrapper = new QosProfileDetailWrapper(id, name, orderNumber);
	    }
	    
	    public QosProfileDetailWrapper build(){
		return qosDetailProfileWrapper;
	    }
	    
	    public QosProfileDetailWrapperBuilder withQosProfileDetails(List<QosProfileDetailData> detailList){
		setDetailData(detailList);
		return this;
	    }

	    //to set quota profile name
	    public QosProfileDetailWrapperBuilder withQuotaProfile(QuotaProfileData quotaProfile){
	    	qosDetailProfileWrapper.quotaProfileName = (quotaProfile == null) ? "" : quotaProfile.getName();
	    	return this;
	    }
	    
	    public QosProfileDetailWrapperBuilder withSyQuotaProfile(SyQuotaProfileData syQuotaProfileData){
	    	qosDetailProfileWrapper.quotaProfileName = (syQuotaProfileData == null) ? "" : syQuotaProfileData.getName();
	    	return this;
	    }
		public QosProfileDetailWrapperBuilder withRncProfile(RncProfileData rncProfileData){
			qosDetailProfileWrapper.quotaProfileName = (rncProfileData == null) ? "" : rncProfileData.getName();
			return this;
		}
	    
	    private void setDetailData(List<QosProfileDetailData> detailList){
			final Integer HSQ = 0;
			final String ONE_SPACE = " ";
			final String EMPTRY_STRING = "";

			for(QosProfileDetailData detail : detailList){		    
			    if( detail.getFupLevel() ==  HSQ ){
			     qosDetailProfileWrapper.aambrul = detail.getAambrul() == null ?EMPTRY_STRING:(String.valueOf(detail.getAambrul()) + ONE_SPACE +detail.getAambrulUnit()+ NVSMXCommonConstants.UPLOAD_STRING);
			     qosDetailProfileWrapper.aambrdl = detail.getAambrdl() == null ?EMPTRY_STRING:(String.valueOf(detail.getAambrdl())+ ONE_SPACE + detail.getAambrdlUnit()+ NVSMXCommonConstants.DOWNLOAD_STRING);
			     qosDetailProfileWrapper.mbrul   = detail.getMbrul() == null ?EMPTRY_STRING:(String.valueOf(detail.getMbrul()) + ONE_SPACE + detail.getMbrulUnit()+ NVSMXCommonConstants.UPLOAD_STRING);
	             qosDetailProfileWrapper.mbrdl   = detail.getMbrdl() ==null ?EMPTRY_STRING:(String.valueOf(detail.getMbrdl()) + ONE_SPACE + detail.getMbrdlUnit()+ NVSMXCommonConstants.DOWNLOAD_STRING);
	             qosDetailProfileWrapper.qci	= String.valueOf(detail.getQci());
	            }
			}
	    }
	}
	
	

	public String getId() {
	    return id;
	}

	public String getName() {
	    return name;
	}

	public String getAambrdl() {
	    return aambrdl;
	}

	public String getAambrul() {
	    return aambrul;
	}

	public String getMbrdl() {
	    return mbrdl;
	}

	public String getMbrul() {
	    return mbrul;
	}

	public String getAction() {
	    return action;
	}

	public String getQci() {
	    return qci;
	}

	public void setId(String id) {
	    this.id = id;
	}

	public void setName(String name) {
	    this.name = name;
	}

	public void setQci(String qci) {
	    this.qci = qci;
	}

	public void setAambrdl(String aambrdl) {
	    this.aambrdl = aambrdl;
	}

	public void setAambrul(String aambrul) {
	    this.aambrul = aambrul;
	}

	public void setMbrdl(String mbrdl) {
	    this.mbrdl = mbrdl;
	}

	public void setMbrul(String mbrul) {
	    this.mbrul = mbrul;
	}

	public void setAction(String action) {
	    this.action = action;
	}

	public String getQuotaProfileName() {
		return quotaProfileName;
	}

	public void setQuotaProfileName(String quotaProfileName) {
		this.quotaProfileName = quotaProfileName;
		
	}

	public Integer getOrderNumber() {
		return orderNumber;
	}

	public void setOrderNumber(Integer orderNumber) {
		this.orderNumber = orderNumber;
	}

}
