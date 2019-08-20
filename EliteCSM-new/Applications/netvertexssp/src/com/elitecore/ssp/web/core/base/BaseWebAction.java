package com.elitecore.ssp.web.core.base;

import java.rmi.RemoteException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.apache.struts.action.Action;

import com.elitecore.commons.base.Collectionz;
import com.elitecore.commons.base.Predicate;
import com.elitecore.netvertexsm.ws.cxfws.ssp.subscription.AddOnPackage;
import com.elitecore.netvertexsm.ws.cxfws.ssp.subscription.AddOnSubRequestData;
import com.elitecore.netvertexsm.ws.cxfws.ssp.subscription.AddOnSubscriptionData;
import com.elitecore.netvertexsm.ws.cxfws.ssp.subscription.BoDPackage;
import com.elitecore.netvertexsm.ws.cxfws.ssp.subscription.BoDSubscriptionData;
import com.elitecore.ssp.subscriber.SubscriberProfile;
import com.elitecore.ssp.util.EliteAssert;
import com.elitecore.ssp.util.constants.ApprovalStates;
import com.elitecore.ssp.util.constants.BaseConstant;
import com.elitecore.ssp.util.constants.SessionAttributeKeyConstant;
import com.elitecore.ssp.util.exception.DataManagerException;
import com.elitecore.ssp.util.logger.Logger;
import com.elitecore.ssp.web.login.forms.LoginForm;
import com.elitecore.ssp.webservice.WebServiceManager;
import com.elitecore.ssp.webservice.promotional.PromotionalDataManager;



public abstract class BaseWebAction extends Action {
    
    protected static final String INVALID_ACCESS_FORWARD = "failure";
    protected static final String MODULE                 = "BaseWebAction";
    protected static final String SUCCESS = "success";
    protected static final String FAILURE = "failure";
    protected static final String POPUPSUCCESS = "popupsuccess";
    protected static final String POPUPFAILURE = "popupfailure";
    private static final String SEARCH_ALL_ADDONS="all"; 
    
    protected boolean checkPermission(HttpServletRequest request) {
		return false;
	}
    
    protected boolean checkAccess( HttpServletRequest request , String subModuleActionAlias ) throws DataManagerException {
        String userName = null;
        try{
            EliteAssert.notNull(request,"request must be specified.");
            EliteAssert.notNull(subModuleActionAlias,"subModuleActionAlias must be specified.");
            EliteAssert.valiedWebSession(request);
            LoginForm sspLoginForm = (LoginForm) request.getSession(false).getAttribute("sspLoginForm");
            EliteAssert.notNull(sspLoginForm,"sspLoginForm must be specified in session.");
            userName = sspLoginForm.getUserName();

            if (userName != null ) {
                return true;
            }
            
        }catch(Exception e){
            throw new DataManagerException("[ "+ subModuleActionAlias +" ] is not permitted to [ "+ userName +" ]");
        }
        return false;

    }
    
    public AddOnSubscriptionData getActivePromotionData(Long addOnPackageId, HttpServletRequest request){
    	AddOnSubscriptionData promotionalData=null;
    	List<AddOnSubscriptionData> totalOfferedPromotions = (List<AddOnSubscriptionData>)request.getSession().getAttribute(SessionAttributeKeyConstant.ACTIVE_PROMOTIONAL_OFFERS);
    	Logger.logDebug(MODULE, "Total Active Promotional Offers: " + totalOfferedPromotions);
		if(totalOfferedPromotions!=null){
			for (int i = 0; i < totalOfferedPromotions.size(); i++) {
				if(totalOfferedPromotions.get(i)!=null && totalOfferedPromotions.get(i).getAddOnPackageID() == addOnPackageId.longValue()){
					return totalOfferedPromotions.get(i);
				}
			}
		}else{
			Logger.logDebug(MODULE, "No Promotional Offer found for addonId: " + addOnPackageId);
		}
		return promotionalData;
    }
    
    public AddOnPackage getAvailablePromotionData(Long addOnPackageId, HttpServletRequest request){
    	AddOnPackage promotionalData=null;
    	List<AddOnPackage> totalOfferedPromotions = (List<AddOnPackage>)request.getSession().getAttribute(SessionAttributeKeyConstant.PROMOTIONAL_OFFERS);
    	Logger.logDebug(MODULE, "Total Available Promotional Offers: " + totalOfferedPromotions);
		if(totalOfferedPromotions!=null){
			for (int i = 0; i < totalOfferedPromotions.size(); i++) {
				if(totalOfferedPromotions.get(i)!=null && totalOfferedPromotions.get(i).getAddOnPackageID() == addOnPackageId.longValue()){
					return totalOfferedPromotions.get(i);
				}
			}
		}else{
			Logger.logDebug(MODULE, "No Promotional Offer found for addonId: " + addOnPackageId);
		}
		return promotionalData;
    }
    
    public BoDSubscriptionData getBodSubscriptionData(Long bodId, HttpServletRequest request ){
    	BoDSubscriptionData bodSubscriptionData=null;
    	List<BoDSubscriptionData> totalBodSubscriptionData = (List<BoDSubscriptionData>)request.getSession().getAttribute(SessionAttributeKeyConstant.ACTIVE_BOD_SUBSCRIPTIONS);
		if(totalBodSubscriptionData!=null){
			for (int i = 0; i < totalBodSubscriptionData.size(); i++) {
				if(totalBodSubscriptionData.get(i)!=null && totalBodSubscriptionData.get(i).getBodSubscriptionID() == bodId.longValue()){
					return totalBodSubscriptionData.get(i);
				}
			}
		}
		return bodSubscriptionData;
    }
    
    public BoDPackage getBodPackageData(Long bodPackageId, HttpServletRequest request){
    	BoDPackage bodPackageData=null;
    	List<BoDPackage> totalBoDPackageData = (List<BoDPackage>)request.getSession().getAttribute(SessionAttributeKeyConstant.BOD_OFFERS);
		if(totalBoDPackageData!=null){
			for (int i = 0; i < totalBoDPackageData.size(); i++) {
				if(totalBoDPackageData.get(i)!=null && totalBoDPackageData.get(i).getBodPackageID().equals(bodPackageId.longValue())){
					return totalBoDPackageData.get(i);
				}
			}
		}
		return bodPackageData;
    }
    
    public Map<Long, AddOnPackage> convertAddOnListToMap(List<AddOnPackage>  addOnList){
    	Map<Long,AddOnPackage> addOnMap=new HashMap<Long, AddOnPackage>();
    	for (AddOnPackage addOnPackage : addOnList) {
			addOnMap.put(addOnPackage.getAddOnPackageID(),addOnPackage);
    	}
    	return addOnMap;
   }
    
    protected void setSelectedLink(HttpServletRequest request,String link,String module) {
		String selectedLink = request.getParameter(SessionAttributeKeyConstant.SELECTED_LINK);
		Logger.logTrace(module, " selectedLink  : "+ selectedLink );		
		request.getSession().setAttribute(SessionAttributeKeyConstant.SELECTED_LINK,link);
	}
    
    protected String getSelectPortal(HttpServletRequest request){
    	return (String)request.getSession().getServletContext().getAttribute("selectedPortal");
    }
    
    protected List<AddOnPackage> getAvailableAddOns(PromotionalDataManager promotinalDataManager,SubscriberProfile childObj) throws RemoteException,
    DataManagerException {
    	List<AddOnPackage> availableAddons;
    	if(WebServiceManager.getInstance().getParameterValue(BaseConstant.AVAILABLE_ADDON_SEARCH_CRITERIA).equals(SEARCH_ALL_ADDONS)){
    		availableAddons = promotinalDataManager.getPromotions(null);			
    	}else{
    		availableAddons = promotinalDataManager.getPromotions(childObj);
    	}
    	return availableAddons;
    }
   
	/**
	 * @param pendingPromotionalReqDataList
	 */
	protected void filterApprovalPendingAddOns(List<AddOnSubRequestData> pendingPromotionalReqDataList) {
		if(Collectionz.isNullOrEmpty(pendingPromotionalReqDataList)==false){
			Collectionz.filter(pendingPromotionalReqDataList, new Predicate<AddOnSubRequestData>() {
				@Override
				public boolean apply(AddOnSubRequestData addOnSubRequestData) {
					if(addOnSubRequestData.getStatus().equals(ApprovalStates.APPROVAL_PENDING.getStringVal())){
						return true;
					}
					return false;
				}
			});
		}
	}

	protected void filterSusbcriptionHistory(List<AddOnSubscriptionData> subscriptionHistory,final String selectedStatus){
		 Collectionz.filter(subscriptionHistory, new Predicate<AddOnSubscriptionData>() {
			@Override
			public boolean apply(AddOnSubscriptionData addOnSubscriptionData) {
				return selectedStatus.equalsIgnoreCase(addOnSubscriptionData.getSubscriptionStatusValue());
			}
		});
	}
	
	protected List<AddOnSubscriptionData> getSubscriptionHistoryFromPendingAddOns(List<AddOnSubRequestData> addOnSubrequestList,Map<Long, AddOnPackage> addOnMap){
		    List<AddOnSubscriptionData> addOnSubscriptionDatalist=null;
		    if(Collectionz.isNullOrEmpty(addOnSubrequestList)){
		    	return addOnSubscriptionDatalist;
		    } 
		    addOnSubscriptionDatalist=Collectionz.newArrayList();
		    for(AddOnSubRequestData addOnSubRequestData:addOnSubrequestList){
		    AddOnSubscriptionData addOnSubscriptionData=new AddOnSubscriptionData();
		        addOnSubscriptionData.setAddOnPackageID(addOnSubRequestData.getAddOnID());
		        addOnSubscriptionData.setSubscriptionStatusName(ApprovalStates.fromValue(Integer.parseInt(addOnSubRequestData.getStatus())).getValue());
		        addOnSubscriptionData.setSubscriptionStatusValue(addOnSubRequestData.getStatus());
		        addOnSubscriptionData.setRejectReason(addOnSubRequestData.getRejectReason());
		        addOnSubscriptionData.setSubscriptionTime(addOnSubRequestData.getCreateTime());
		        addOnSubscriptionData.setSubscriptionEndTime(addOnSubscriptionData.getLastUpdateTime());
		        AddOnPackage addOnPackage=addOnMap.get(addOnSubRequestData.getAddOnID());
		        if(addOnPackage!=null){
		        	addOnSubscriptionData.setAddOnPackageName(addOnPackage.getAddOnPackageName());
		        }
		        addOnSubscriptionDatalist.add(addOnSubscriptionData);
		    }
	     return addOnSubscriptionDatalist;	
	}
	
}