package com.elitecore.nvsmx.pd.controller.guiding;

import static com.opensymphony.xwork2.Action.SUCCESS;

import java.sql.Timestamp;
import java.util.List;

import org.apache.struts2.convention.annotation.Namespace;
import org.apache.struts2.convention.annotation.ParentPackage;
import org.apache.struts2.convention.annotation.Result;
import org.apache.struts2.convention.annotation.Results;
import org.apache.struts2.interceptor.validation.SkipValidation;
import com.elitecore.corenetvertex.constants.CommonConstants;
import com.elitecore.corenetvertex.constants.TrafficType;
import com.elitecore.commons.base.Collectionz;
import com.elitecore.corenetvertex.pd.account.AccountData;
import com.elitecore.corenetvertex.pd.guiding.GuidingData;
import com.elitecore.corenetvertex.pd.lob.LobData;
import com.elitecore.corenetvertex.pd.service.ServiceData;
import com.elitecore.corenetvertex.pkg.constants.ACLModules;
import com.elitecore.corenetvertex.util.GsonFactory;
import com.elitecore.nvsmx.sm.controller.RestGenericCTRL;
import com.elitecore.nvsmx.system.hibernate.CRUDOperationUtil;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.reflect.TypeToken;

/**
 * Created by ajay pandey on 23/12/17.
 */


@ParentPackage(value = "pd")
@Namespace("/pd/guiding")
@Results({
        @Result(name= SUCCESS, type="redirectAction",params = {"actionName","guiding"}),
})
public class GuidingCTRL extends  RestGenericCTRL<GuidingData> {

	
		private static final long serialVersionUID = -6882470730134447721L;
		
	    private List<LobData> lobDataList = Collectionz.newArrayList();
	    private List<ServiceData> serviceDataList = Collectionz.newArrayList();
	    private List<AccountData> accountDataList = Collectionz.newArrayList();
	    private List<String> trafficTypeList= Collectionz.newArrayList();
	    

		@Override
	    public ACLModules getModule() {
	        return ACLModules.GUIDING;
	    }

	    @Override
	    public GuidingData createModel() {
	        return new GuidingData();
	    }
	    
	    @Override
	    public void validate() {
	    	GuidingData guidingData = (GuidingData) getModel();
	    	boolean isAlreadyExist = isDuplicateEntity("guidingName", guidingData.getResourceName(), getMethodName());
	        if(isAlreadyExist){
	            addFieldError("guidingName", getText("guiding.name.duplicate"));
	        }
	        else{
	        	LobData lobData = CRUDOperationUtil.get(LobData.class, guidingData.getLobId());
				ServiceData serviceData = CRUDOperationUtil.get(ServiceData.class, guidingData.getServiceId());
				AccountData accountData = CRUDOperationUtil.get(AccountData.class, guidingData.getAccountId());
				
				if (lobData == null) {
					addFieldError("lobId", getText("guiding.lob.invalid"));
				}
				
				if(serviceData == null){
					addFieldError("serviceId", getText("guiding.service.invalid"));
				}
				
				if(accountData == null){
					addFieldError("accountId", getText("guiding.account.invalid"));
				}
				
				Timestamp endDate = guidingData.getEndDate();
				if(endDate!=null){
					long startDateTime = guidingData.getStartDate().getTime();
					if((endDate.getTime() - startDateTime) < CommonConstants.TEN_MINUTES){
						addFieldError("endDate", getText("guiding.enddate.mustbe.greaterthan.startdate"));
					}
				}
	        }
	    }
	    
	    @SkipValidation
	    @Override
	    public void prepareValuesForSubClass() throws Exception {
	    	setLobDataList(CRUDOperationUtil.findAll(LobData.class));
	    	setServiceDataList(CRUDOperationUtil.findAll(ServiceData.class));
	    	setAccountDataList(CRUDOperationUtil.findAll(AccountData.class));
	    }

		public List<LobData> getLobDataList() {
			return lobDataList;
		}

		public void setLobDataList(List<LobData> lobDataList) {
			this.lobDataList = lobDataList;
		}

		public List<ServiceData> getServiceDataList() {
			return serviceDataList;
		}

		public void setServiceDataList(List<ServiceData> serviceDataList) {
			this.serviceDataList = serviceDataList;
		}

		public List<AccountData> getAccountDataList() {
			return accountDataList;
		}

		public void setAccountDataList(List<AccountData> accountDataList) {
			this.accountDataList = accountDataList;
		}
		
		public List<String> getTrafficTypeList() {
			return trafficTypeList;
		}

		public void setTrafficTypeList(List<String> trafficTypeList) {
			this.trafficTypeList = trafficTypeList;
		}
	    
		
		public String getTrafficTypeListAsJson() {
	        Gson gson = GsonFactory.defaultInstance();
	        TrafficType[] trafficType = TrafficType.values();
			 for(TrafficType traType: trafficType)
			 {
				 trafficTypeList.add(traType.getVal());
			 }
	        JsonArray modelJson = gson.toJsonTree(trafficTypeList,new TypeToken<List<String>>() {}.getType()).getAsJsonArray();
	        return modelJson.toString();
	    }
		
	}


