package com.elitecore.elitesm.web.servicepolicy.radiusservicepolicy;



import java.util.ArrayList;
import java.util.List;

public class ProfileLookupDriver {
	
	private String handlerName;
	private String realmPattern;
	private String stripIdentity;
	private String separator;
	private String selectCase;	
	private String trimUserIdentity;	
	private String trimPassword;
	private String anonymousProfileIdentity;
	private List<String> selecteddriverIds = new java.util.ArrayList<String>();
	private List<String> selectedCacheDriverIds = new java.util.ArrayList<String>();
	private List<String> selectedAdditionalDriverIds = new java.util.ArrayList<String>();
	private List<PrimaryDriverRelData> primaryDriverRelDataList=new ArrayList<PrimaryDriverRelData>();
	private List<SecondaryDriverRelData> secondaryDriverRelDataList = new ArrayList<SecondaryDriverRelData>();
	private List<AdditionalDriverRelData> additionalDriverRelDataList=new ArrayList<AdditionalDriverRelData>();
	private String driverScript;
	private int orderNumber;
	private String isAdditional;
	private String isHandlerEnabled = "false";
	
	public String getRealmPattern() {
		return realmPattern;
	}
	public void setRealmPattern(String realmPattern) {
		this.realmPattern = realmPattern;
	}
	public String getStripIdentity() {
		return stripIdentity;
	}
	public void setStripIdentity(String stripIdentity) {
		this.stripIdentity = stripIdentity;
	}
	public String getSeparator() {
		return separator;
	}
	public void setSeparator(String separator) {
		this.separator = separator;
	}
	public String getSelectCase() {
		return selectCase;
	}
	public void setSelectCase(String selectCase) {
		this.selectCase = selectCase;
	}
	public String getTrimUserIdentity() {
		return trimUserIdentity;
	}
	public void setTrimUserIdentity(String trimUserIdentity) {
		this.trimUserIdentity = trimUserIdentity;
	}
	public String getTrimPassword() {
		return trimPassword;
	}
	public void setTrimPassword(String trimPassword) {
		this.trimPassword = trimPassword;
	}
	public String getAnonymousProfileIdentity() {
		return anonymousProfileIdentity;
	}
	public void setAnonymousProfileIdentity(String anonymousProfileIdentity) {
		this.anonymousProfileIdentity = anonymousProfileIdentity;
	}
	public String getDriverScript() {
		return driverScript;
	}
	public void setDriverScript(String driverScript) {
		this.driverScript = driverScript;
	}
	public int getOrderNumber() {
		return orderNumber;
	}
	public void setOrderNumber(int orderNumber) {
		this.orderNumber = orderNumber;
	}
	public String getIsAdditional() {
		return isAdditional;
	}
	public void setIsAdditional(String isAdditional) {
		this.isAdditional = isAdditional;
	}
	public List<String> getSelecteddriverIds() {
		return selecteddriverIds;
	}
	public void setSelecteddriverIds(List<String> selecteddriverIds) {
		this.selecteddriverIds = selecteddriverIds;
		doAssignPrimaryDriver(selecteddriverIds);
	}
	public List<String> getSelectedCacheDriverIds() {
		return selectedCacheDriverIds;
	}
	public void setSelectedCacheDriverIds(List<String> selectedCacheDriverIds) {
		this.selectedCacheDriverIds = selectedCacheDriverIds;
		doAssignSelectedCacheDriver(selectedCacheDriverIds);
	}
	public List<String> getSelectedAdditionalDriverIds() {
		return selectedAdditionalDriverIds;
	}
	public void setSelectedAdditionalDriverIds(
			List<String> selectedAdditionalDriverIds) {
		this.selectedAdditionalDriverIds = selectedAdditionalDriverIds;
		doAssignAdditionalDrivers(selectedAdditionalDriverIds);
	}
	
	public List<PrimaryDriverRelData> getPrimaryDriverRelDataList() {
		return primaryDriverRelDataList;
	}
	public void setPrimaryDriverRelDataList(List<PrimaryDriverRelData> primaryDriverRelDataList) {
		this.primaryDriverRelDataList = primaryDriverRelDataList;
	}
	public List<SecondaryDriverRelData> getSecondaryDriverRelDataList() {
		return secondaryDriverRelDataList;
	}
	public void setSecondaryDriverRelDataList(
			List<SecondaryDriverRelData> secondaryDriverRelDataList) {
		this.secondaryDriverRelDataList = secondaryDriverRelDataList;
	}
	public List<AdditionalDriverRelData> getAdditionalDriverRelDataList() {
		return additionalDriverRelDataList;
	}
	public void setAdditionalDriverRelDataList(
			List<AdditionalDriverRelData> additionalDriverRelDataList) {
		this.additionalDriverRelDataList = additionalDriverRelDataList;
	}
	
	private void doAssignAdditionalDrivers(List<String> additionalDriverRelDataListData) {
		List<AdditionalDriverRelData> additionalDriverRelDataList=new ArrayList<AdditionalDriverRelData>();
		if(additionalDriverRelDataListData != null && additionalDriverRelDataListData.size() > 0){
			for(String selectedAdditionalDriver:additionalDriverRelDataListData){
				String selectedDrivers=selectedAdditionalDriver;
				AdditionalDriverRelData additionalDriverRelData=new AdditionalDriverRelData();
				additionalDriverRelData.setDriverInstanceId(selectedDrivers);
				additionalDriverRelDataList.add(additionalDriverRelData);
			}
		}
		setAdditionalDriverRelDataList(additionalDriverRelDataList);
		
	}
	private void doAssignPrimaryDriver(List<String> selecteddriverIds) {
		List<PrimaryDriverRelData> primaryDriverRelDataList=new ArrayList<PrimaryDriverRelData>();
		if(selecteddriverIds != null && selecteddriverIds.size() > 0){
			for(String selectedDriver:selecteddriverIds){
				
				String[] selectedDrivers = selectedDriver.split("(\\()|(\\))");
			
				PrimaryDriverRelData primaryDriverRelData=new PrimaryDriverRelData();
				primaryDriverRelData.setDriverInstanceId(selectedDrivers[0]);
				primaryDriverRelData.setWeightage(Integer.parseInt(selectedDrivers[1]));
				
				primaryDriverRelDataList.add(primaryDriverRelData);
			}
		}
		setPrimaryDriverRelDataList(primaryDriverRelDataList);
	}

	private void doAssignSelectedCacheDriver(List<String> selectedCacheDriverIdsData) {
		List<SecondaryDriverRelData> secondaryDriverRelDataList=new ArrayList<SecondaryDriverRelData>();
		if(selectedCacheDriverIdsData != null && selectedCacheDriverIdsData.size() > 0){
			for(String selectedCacheDriver : selectedCacheDriverIdsData){
				
				String[] selectedCacheDrivers=selectedCacheDriver.split("\\(");
				
				SecondaryDriverRelData secondaryDriverRelData=new SecondaryDriverRelData();
				secondaryDriverRelData.setSecondaryDriverInstId(selectedCacheDrivers[0]);
				
				String secondaryDriverName = selectedCacheDrivers[1].substring(0, selectedCacheDrivers[1].length()-1);
				
				if( secondaryDriverName.equalsIgnoreCase("--None--")){
					secondaryDriverRelData.setCacheDriverInstId("0");
				}else{
					secondaryDriverRelData.setCacheDriverInstId(secondaryDriverName);
				}
				
				secondaryDriverRelDataList.add(secondaryDriverRelData);
			}
		}
		setSecondaryDriverRelDataList(secondaryDriverRelDataList);
	}
	
	public String getIsHandlerEnabled() {
		return isHandlerEnabled;
	}
	public void setIsHandlerEnabled(String isHandlerEnabled) {
		this.isHandlerEnabled = isHandlerEnabled;
	}
	public String getHandlerName() {
		return handlerName;
	}
	public void setHandlerName(String handlerName) {
		this.handlerName = handlerName;
	}
	
	@Override
	public String toString() {
		return "ProfileLookupDriver [realmPattern=" + realmPattern
				+ ", stripIdentity=" + stripIdentity + ", separator="
				+ separator + ", selectCase=" + selectCase
				+ ", trimUserIdentity=" + trimUserIdentity + ", trimPassword="
				+ trimPassword + ", anonymousProfileIdentity="
				+ anonymousProfileIdentity + ", selecteddriverIds="
				+ selecteddriverIds + ", selectedCacheDriverIds="
				+ selectedCacheDriverIds + ", selectedAdditionalDriverIds="
				+ selectedAdditionalDriverIds + ", primaryDriverRelDataList="
				+ primaryDriverRelDataList + ", secondaryDriverRelDataList="
				+ secondaryDriverRelDataList + ", additionalDriverRelDataList="
				+ additionalDriverRelDataList + ", driverScript="
				+ driverScript + ", orderNumber=" + orderNumber
				+ ", isAdditional=" + isAdditional + ", isHandlerEnabled="
				+ isHandlerEnabled + "]";
	}
}
