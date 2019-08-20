package com.elitecore.ssp.web.promotional.forms;

import java.util.List;
import java.util.Map;

import com.elitecore.netvertexsm.ws.cxfws.ssp.subscription.AddOnPackage;
import com.elitecore.netvertexsm.ws.cxfws.ssp.subscription.AddOnSubRequestData;
import com.elitecore.netvertexsm.ws.cxfws.ssp.subscription.AddOnSubscriptionData;
import com.elitecore.ssp.web.core.base.forms.BaseWebForm;

public class PromotionalForm extends BaseWebForm{
	
	private List<AddOnSubscriptionData> subscriptionHistoriesList;

	private static final long serialVersionUID = 1L;
	
	private AddOnPackage promotionalData ;
	private List<AddOnPackage> activatedPromotionalData ;
	private List<AddOnPackage> availableAddons ;
	private List<AddOnSubRequestData> pendingPromotionalReqData;
	//private List<AddOnSubscriptionData> subscriptionHistories;
	private List<AddOnSubscriptionData> acticvePromotions ;
	private String errorMessage;
	private AddOnSubRequestData pendingPromotionalSubReqData;
	private Map<Long,AddOnPackage> addOnPackageMapById;
	private List<AddOnSubRequestData> addOnSubRequestDatas;
	
	public List<AddOnSubRequestData> getAddOnSubRequestDatas() {
		return addOnSubRequestDatas;
	}
	public void setAddOnSubRequestDatas(
			List<AddOnSubRequestData> addOnSubRequestDatas) {
		this.addOnSubRequestDatas = addOnSubRequestDatas;
	}
	public AddOnSubRequestData getPendingPromotionalSubReqData() {
		return pendingPromotionalSubReqData;
	}
	public void setPendingPromotionalSubReqData(
			AddOnSubRequestData pendingPromotionalSubReqData) {
		this.pendingPromotionalSubReqData = pendingPromotionalSubReqData;
	}
	public List<AddOnSubRequestData> getPendingPromotionalReqData() {
		return pendingPromotionalReqData;
	}
	public void setPendingPromotionalReqData(List<AddOnSubRequestData> pendingPromotionalReqData) {
		this.pendingPromotionalReqData = pendingPromotionalReqData;
	}
	public String getErrorMessage() {
		return errorMessage;
	}
	public void setErrorMessage(String errorMessage) {
		this.errorMessage = errorMessage;
	}
	public List<AddOnSubscriptionData> getActicvePromotions() {
		return acticvePromotions;
	}
	public void setActicvePromotions(List<AddOnSubscriptionData> acticvePromotions) {
		this.acticvePromotions = acticvePromotions;
	}
	/*public List<AddOnSubscriptionData> getSubscriptionHistories() {
		return subscriptionHistories;
	}
	public void setSubscriptionHistories(
			List<AddOnSubscriptionData> subscriptionHistories) {
		this.subscriptionHistories = subscriptionHistories;
	}*/
	public AddOnPackage getPromotionalData() {
		return promotionalData;
	}
	public void setPromotionalData(AddOnPackage promotionalData) {
		this.promotionalData = promotionalData;
	}
	public List<AddOnPackage> getActivatedPromotionalData() {
		return activatedPromotionalData;
	}
	public void setActivatedPromotionalData(
			List<AddOnPackage> activatedPromotionalData) {
		this.activatedPromotionalData = activatedPromotionalData;
	}
	public List<AddOnPackage> getAvailableAddons() {
		return availableAddons;
	}
	public void setAvailableAddons(List<AddOnPackage> availableAddons) {
		this.availableAddons = availableAddons;
	}
	
	public List<AddOnSubscriptionData> getSubscriptionHistoriesList() {
		return subscriptionHistoriesList;
	}
	public void setSubscriptionHistoriesList(
			List<AddOnSubscriptionData> subscriptionHistoriesList) {
		this.subscriptionHistoriesList = subscriptionHistoriesList;
	}
	/**
	 * @return the addOnPackageMapById
	 */
	public Map<Long, AddOnPackage> getAddOnPackageMapById() {
		return addOnPackageMapById;
	}
	/**
	 * @param addOnPackageMapById the addOnPackageMapById to set
	 */
	public void setAddOnPackageMapById(Map<Long, AddOnPackage> addOnPackageMapById) {
		this.addOnPackageMapById = addOnPackageMapById;
	}
	
	public AddOnPackage getAddOnPackageById(Long addOnPackageId){
		return addOnPackageMapById.get(addOnPackageId);
	}
	
}