package com.elitecore.nvsmx.pd.controller.ratecard;

import static com.elitecore.commons.logging.LogManager.getLogger;
import static com.opensymphony.xwork2.Action.SUCCESS;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import org.apache.commons.lang.math.NumberUtils;
import org.apache.struts2.convention.annotation.Namespace;
import org.apache.struts2.convention.annotation.ParentPackage;
import org.apache.struts2.convention.annotation.Result;
import org.apache.struts2.convention.annotation.Results;
import org.apache.struts2.interceptor.validation.SkipValidation;
import org.apache.struts2.rest.DefaultHttpHeaders;
import org.apache.struts2.rest.HttpHeaders;
import org.hibernate.criterion.Order;

import com.elitecore.commons.base.Collectionz;
import com.elitecore.commons.logging.LogManager;
import com.elitecore.corenetvertex.constants.CommonConstants;
import com.elitecore.corenetvertex.pd.pbss.ratecard.RateCardData;
import com.elitecore.corenetvertex.pd.pbss.ratecard.RateCardVersionDetail;
import com.elitecore.corenetvertex.pd.pbss.ratecard.RateCardVersionRelation;
import com.elitecore.corenetvertex.pd.pbss.ratecardgroup.RateCardGroupData;
import com.elitecore.corenetvertex.pd.pbss.rncpackage.RncPackageData;
import com.elitecore.corenetvertex.pkg.constants.ACLModules;
import com.elitecore.corenetvertex.spr.exceptions.ResultCode;
import com.elitecore.corenetvertex.util.GsonFactory;
import com.elitecore.nvsmx.commons.model.acl.GroupDAO;
import com.elitecore.nvsmx.sm.controller.RestGenericCTRL;
import com.elitecore.nvsmx.system.constants.NVSMXCommonConstants;
import com.elitecore.nvsmx.system.hibernate.CRUDOperationUtil;
import com.google.common.base.Strings;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.reflect.TypeToken;

/**
 * manage RateCard related information. Created by Saket on 17/12/17.
 */

@ParentPackage(value = "pd")
@Namespace("/pd/ratecard")
@Results({ @Result(name = SUCCESS, type = RestGenericCTRL.REDIRECT_ACTION, params = { NVSMXCommonConstants.ACTION_NAME,
		"rate-card" }),

})
public class RateCardCTRL extends RestGenericCTRL<RateCardData> {

	private static final long serialVersionUID = -6882470730134447721L;
	
	/*private List<RateFileFormatData> rateFileFormatDataList = Collectionz.newArrayList();*/


	/*public List<RateFileFormatData> getRateFileFormatDataList() {
		return rateFileFormatDataList;
	}

	public void setRateFileFormatDataList(List<RateFileFormatData> rateFileFormatDataList) {
		this.rateFileFormatDataList = rateFileFormatDataList;
	}
*/
	private String rateCardAsJson;
	private String rncPackageId;
	
	private RateCardData model = createModel();

	private static final Comparator<RateCardVersionRelation> byOrder = (RateCardVersionRelation o1,
			RateCardVersionRelation o2) -> o2.getOrderNumber() - o1.getOrderNumber();

	@Override
	public ACLModules getModule() {
		return ACLModules.RATECARD;
	}

	@Override
	public RateCardData createModel() {
		return new RateCardData();
	}

	public String getRncPackageId() {
		return rncPackageId;
	}

	public void setRncPackageId(String rncPackageId) {
		this.rncPackageId = rncPackageId;
	}

	@SkipValidation
	@Override
	public void prepareValuesForSubClass() throws Exception {
		/*setRateFileFormatDataList(CRUDOperationUtil.findAll(RateFileFormatData.class));*/
		String rncPackageID = getRequest().getParameter(NVSMXCommonConstants.RNC_PACKAGE_ID);
		if (Strings.isNullOrEmpty(rncPackageID) == false) {
			setRncPackageId(rncPackageID);
		}
	}

	@Override
	public HttpHeaders create() {
		if (LogManager.getLogger().isDebugLogLevel()) {
			LogManager.getLogger().debug(getLogModule(), "Method called create()");
		}

		RateCardData rateCardData = (RateCardData) getModel();

		RncPackageData rncPackageData = new RncPackageData();
		rncPackageData.setId(getRncPackageId());
		rateCardData.setRncPackageData(rncPackageData);

		rateCardData.getRateCardVersionRelation().removeAll(Collections.singleton(null));
		setRateCardDataToRateCardVersionRelation(rateCardData);
		setRateCardVersionRelationToRateCardVersionDetail(rateCardData);

		return super.create();
	}

	@SuppressWarnings("unchecked")
	@Override
	public String edit() {
		RateCardData rateCardData = (RateCardData) getModel();
		RateCardData resourceInDB = CRUDOperationUtil.get((Class<RateCardData>) rateCardData.getClass(),
				rateCardData.getId(), getAdditionalCriteria());

		Collections.sort(resourceInDB.getRateCardVersionRelation(), byOrder);

		Gson gson = GsonFactory.defaultInstance();
		JsonArray modelJson = gson
				.toJsonTree(resourceInDB.getRateCardVersionRelation(), new TypeToken<List<RateCardVersionRelation>>() {
				}.getType()).getAsJsonArray();
		setRateCardAsJson(modelJson.toString());
		return super.edit();
	}

	@Override
	public HttpHeaders update() {
		if (LogManager.getLogger().isDebugLogLevel()) {
			LogManager.getLogger().debug(getLogModule(), "Method called update()");
		}
		RateCardData rateCardData = (RateCardData) getModel();

		RncPackageData rncPackageData = new RncPackageData();
		rncPackageData.setId(getRncPackageId());
		rateCardData.setRncPackageData(rncPackageData);

		rateCardData.getRateCardVersionRelation().removeAll(Collections.singleton(null));
		setRateCardDataToRateCardVersionRelation(rateCardData);
		setRateCardVersionRelationToRateCardVersionDetail(rateCardData);
		return super.update();
	}

	@Override
    public HttpHeaders show() {
        RateCardData rateCardData = (RateCardData) getModel();
        try{
        
        RateCardData resourceInDB = CRUDOperationUtil.get((Class<RateCardData>) rateCardData.getClass(),
                rateCardData.getId(), getAdditionalCriteria());

        Collections.sort(resourceInDB.getRateCardVersionRelation(), byOrder);
         if (resourceInDB == null) {
             addActionError(getModule().getDisplayLabel()+" Not Found with id " + rateCardData.getId());
             return new DefaultHttpHeaders(ERROR).withStatus(ResultCode.NOT_FOUND.code);
         }
         if (resourceInDB.getGroups() != null) {
             String belongingsGroups = GroupDAO.getGroupNames(CommonConstants.COMMA_SPLITTER.split(resourceInDB.getGroups()));
             resourceInDB.setGroupNames(belongingsGroups);
         }
         setModel(resourceInDB);
     } catch (Exception e) {
         addActionError("Fail to view " + getModule().getDisplayLabel() + " for id ");
         getLogger().error(getLogModule(), "Error while viewing " + getModule().getDisplayLabel() + " for id " + rateCardData.getId() + " . Reason: " + e.getMessage());
         getLogger().trace(getLogModule(), e);
     }
     setActionChainUrl(getRedirectURL(METHOD_SHOW));
     return new DefaultHttpHeaders(NVSMXCommonConstants.REDIRECT_URL);
    }

	@Override
	public HttpHeaders destroy() {// delete
		
		
		
		if (LogManager.getLogger().isDebugLogLevel()) {
			LogManager.getLogger().debug(getLogModule(), "Method called delete()");
		}
		try{
			RateCardData rateCardDataModel = (RateCardData) getModel();
            List<RateCardData> rateCardDataList = CRUDOperationUtil.get(RateCardData.class, Order.asc("name"));
            RateCardData rateCardData = rateCardDataList.stream()
                                                .filter(rncPackageData -> rncPackageData.getRncPackageData() != null && rncPackageData.getRncPackageData().getId().equals(rateCardDataModel.getId()))
                                                .findAny()
                                                .orElse(null);
            if(rateCardData != null){
                    addActionError("Rate Card " + rateCardData.getName() +" is associated as Rate Card Group");
                    getLogger().error(getLogModule(),"Rate Card " + rateCardData.getName() +" is associated as  Rate Card Group");
                    return new DefaultHttpHeaders(SUCCESS).withStatus(ResultCode.INTERNAL_ERROR.code);
			
            }     
			super.destroy();
			setActionChainUrl(CommonConstants.FORWARD_SLASH + "pd/rncpackage/rnc-package/"
					+ (getRequest().getParameter(NVSMXCommonConstants.RNC_PACKAGE_ID)));
			return new DefaultHttpHeaders(REDIRECT_ACTION).withStatus(ResultCode.SUCCESS.code).disableCaching();
			}
			catch (Exception e) {
	            addActionError("Can not perform delete operation. Reason: It is child Entity test....... " + e.getMessage());
	            
	            getLogger().error(getLogModule(), "Error while " + getModule().getDisplayLabel() + " for id "+((RateCardGroupData)getModel()).getId()+" . Reason: " + e.getMessage());
	            getLogger().trace(getLogModule(), e);
	        }
	        return new DefaultHttpHeaders(SUCCESS).withStatus(ResultCode.INTERNAL_ERROR.code);
		
		/*super.destroy();
		setActionChainUrl(CommonConstants.FORWARD_SLASH + "pd/rncpackage/rnc-package/"
				+ (getRequest().getParameter(NVSMXCommonConstants.RNC_PACKAGE_ID)));
		return new DefaultHttpHeaders(REDIRECT_ACTION).withStatus(ResultCode.SUCCESS.code).disableCaching();*/
		
	}

	private void setRateCardVersionRelationToRateCardVersionDetail(RateCardData rateCardData) {
		if (Collectionz.isNullOrEmpty(rateCardData.getRateCardVersionRelation()) == false) {
			for (RateCardVersionRelation rateCardVersionRelData : rateCardData.getRateCardVersionRelation())
				if (rateCardVersionRelData != null
						&& Collectionz.isNullOrEmpty(rateCardVersionRelData.getRateCardVersionDetail()) == false) {
					int orderNumber = 1;
					rateCardVersionRelData.getRateCardVersionDetail().removeAll(Collections.singleton(null));
					for (RateCardVersionDetail rateCardVersionDetail : rateCardVersionRelData
							.getRateCardVersionDetail()) {
						rateCardVersionDetail.setOrderNumber(orderNumber++);
					}
				}
		}
		rateCardData.getRateCardVersionRelation()
				.forEach(rateCardVedrionRelationDatas -> rateCardVedrionRelationDatas.getRateCardVersionDetail()
						.forEach(rateCardVersionDetail -> rateCardVersionDetail
								.setRateCardVersionRelation(rateCardVedrionRelationDatas)));

	}

	private void setRateCardDataToRateCardVersionRelation(RateCardData rateCardData) {
		if (Collectionz.isNullOrEmpty(rateCardData.getRateCardVersionRelation()) == false) {
			int orderNum = 1;
			for (RateCardVersionRelation rateCardVersionRelation : rateCardData.getRateCardVersionRelation()) {
				if (rateCardVersionRelation != null) {
					rateCardVersionRelation.setOrderNumber(orderNum++);
				}
			}
		}
		rateCardData.getRateCardVersionRelation().forEach(rateCardDatas -> rateCardDatas.setRateCardData(rateCardData));
	}

	public String getRateCardAsJson() {
		return rateCardAsJson;
	}

	public void setRateCardAsJson(String rateCardAsJson) {
		this.rateCardAsJson = rateCardAsJson;
	}

	@Override
	public void validate() {
		RateCardData rateCardData = (RateCardData) getModel();
		
		if(!("*").equals(rateCardData.getId())){
			super.validate();
		}
		else{
		boolean isAlreadyExist = isDuplicateEntity("name", rateCardData.getResourceName(), getMethodName());
		if (isAlreadyExist) {
			addFieldError("name", getText("ratecard.duplicate.name"));
		}

		else {
			if (Collectionz.isNullOrEmpty(rateCardData.getRateCardVersionRelation()) == false) {
				rateCardData.getRateCardVersionRelation().removeAll(Collections.singleton(null));
				boolean rateCardVersionDetailCheck = false;
				for (RateCardVersionRelation rateCardVersionRelation : rateCardData.getRateCardVersionRelation()) {
					if (Collectionz.isNullOrEmpty(rateCardVersionRelation.getRateCardVersionDetail())) {
						rateCardVersionDetailCheck = true;
						break;
					}
				}
				if (!rateCardVersionDetailCheck) {
					for (RateCardVersionRelation rateCardVersionRelation : rateCardData.getRateCardVersionRelation()) {
						rateCardVersionRelation.getRateCardVersionDetail().removeAll(Collections.singleton(null));
						for (RateCardVersionDetail rateCardVersionDetail : rateCardVersionRelation
								.getRateCardVersionDetail()) {
							setErrorMessage(rateCardVersionDetail.getLabel1(), "label1");
							setErrorMessage(rateCardVersionDetail.getLabel2(), "label2");
							setErrorMessage(rateCardVersionDetail.getPulse1(), "pulse1");
							setErrorMessage(rateCardVersionDetail.getRate1(), "rate1");
							setErrorMessage(rateCardVersionDetail.getSlab1(), "slab1");
							setErrorMessage(rateCardVersionDetail.getTierRateType(), "tierRateType");
						}
					}
				} else {
					addActionError(getText("ratecard.version.detail.error"));
				}

			} else {
				addActionError(getText("ratecard.version.error"));
			}
		}
	}
	}

	public void setErrorMessage(String fieldValue, String fieldName) {

		if (Strings.isNullOrEmpty(fieldValue)) {
			addFieldError(fieldName, fieldName + " ratecard.version.value.required");
		}
	}

	public void setErrorMessage(Double fieldValue, String fieldName) {
		if (fieldValue == null) {
			addFieldError(fieldName, fieldName + " ratecard.version.value.required");
		} else {
			if (NumberUtils.isNumber(String.valueOf(fieldValue)) == false) {
				addFieldError(fieldName, fieldName + " ratecard.version.double");
			}
		}
	}

}
