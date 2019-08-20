package com.elitecore.nvsmx.pd.controller.ratecardgroupdata;

import com.elitecore.commons.base.Collectionz;
import com.elitecore.corenetvertex.constants.*;
import com.elitecore.corenetvertex.pd.ratecard.RateCardScope;
import com.elitecore.corenetvertex.pd.ratecard.RateCardData;
import com.elitecore.corenetvertex.pd.ratecardgroup.RateCardGroupData;
import com.elitecore.corenetvertex.pd.ratecardgroup.TimeSlotRelationData;
import com.elitecore.corenetvertex.pd.rncpackage.RncPackageData;
import com.elitecore.corenetvertex.pkg.PkgMode;
import com.elitecore.corenetvertex.pkg.constants.ACLModules;
import com.elitecore.corenetvertex.spr.exceptions.ResultCode;
import com.elitecore.corenetvertex.util.GsonFactory;
import com.elitecore.nvsmx.sm.controller.RestGenericCTRL;
import com.elitecore.nvsmx.system.constants.Attributes;
import com.elitecore.nvsmx.system.constants.NVSMXCommonConstants;
import com.elitecore.nvsmx.system.hibernate.CRUDOperationUtil;
import com.google.common.base.Strings;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import org.apache.struts2.convention.annotation.Namespace;
import org.apache.struts2.convention.annotation.ParentPackage;
import org.apache.struts2.convention.annotation.Result;
import org.apache.struts2.convention.annotation.Results;
import org.apache.struts2.interceptor.validation.SkipValidation;
import org.apache.struts2.rest.DefaultHttpHeaders;
import org.apache.struts2.rest.HttpHeaders;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import static com.elitecore.commons.logging.LogManager.getLogger;
import static com.elitecore.corenetvertex.pm.pkg.ResourceDataPredicates.createStaffBelongingPredicate;
import static com.elitecore.nvsmx.policydesigner.controller.util.ProductOfferUtility.doesBelongToGroup;
import static com.opensymphony.xwork2.Action.SUCCESS;
import static org.codehaus.groovy.runtime.DefaultGroovyMethods.collect;

@ParentPackage(value = "pd")
@Namespace("/pd/ratecardgroup")
@Results({ @Result(name = SUCCESS, type = RestGenericCTRL.REDIRECT_ACTION, params = { NVSMXCommonConstants.ACTION_NAME,
		"rate-card-group" }),

})
public class RateCardGroupCTRL extends RestGenericCTRL<RateCardGroupData> {

	private static final long serialVersionUID = -1762482196596650473L;
	private static final String PEAKRATECARDID = "peakRateRateCardId";
	private static final String OFFPEAKRATECARDID = "offPeakRateRateCardId";

	private List<RateCardGroupData> list;
	private List<RateCardData> monetaryRateCardDataList = Collectionz.newArrayList();
	private List<RateCardData> globalMonetaryRateCardDataList = Collectionz.newArrayList();
	private List<RateCardData> nonMonetaryRateCardDataList = Collectionz.newArrayList();

	private List<TimeSlotRelationData> peakRateTimeSlotList = Collectionz.newArrayList();
	private List<TimeSlotRelationData> offPeakTimeSlotList = Collectionz.newArrayList();

	private String peakRateAsJson;
	private String offPeakRateAsJson;

	private String rateCardGroupAsJson;
	private String rncPackageId;

	public String getRncPackageId() {
		return rncPackageId;
	}

	public void setRncPackageId(String rncPackageId) {
		this.rncPackageId = rncPackageId;
	}

	@Override
	public ACLModules getModule() {
		return ACLModules.RATECARDGROUP;
	}

	@Override
	public RateCardGroupData createModel() {
		return new RateCardGroupData();
	}

    @SkipValidation
    @Override
    public void prepareValuesForSubClass() throws Exception {

        String rncPackageID = getRequest().getParameter(NVSMXCommonConstants.RNC_PACKAGE_ID);
		RncPackageData data = CRUDOperationUtil.get(RncPackageData.class, rncPackageID);
        if (Strings.isNullOrEmpty(rncPackageID) == false) {
            setRncPackageId(rncPackageID);
            List<RateCardData> rateCards = CRUDOperationUtil.findAll(RateCardData.class, "name");

            List<RateCardData> globalRateCards = new ArrayList<>(rateCards);

			rateCards = rateCards.stream().filter(c -> (c.getScope().equalsIgnoreCase(RateCardScope.LOCAL.name()) && data.getCurrency().equalsIgnoreCase(c.getCurrency()))).collect(Collectors.toList());
			globalRateCards = globalRateCards.stream().filter(c -> c.getScope().equalsIgnoreCase(RateCardScope.GLOBAL.name()) && data.getCurrency().equalsIgnoreCase(c.getCurrency())).collect(Collectors.toList());

			//Staff global rate card
			Collectionz.filter(globalRateCards, createStaffBelongingPredicate(getStaffBelongingGroups()));

			//Pick global rate card where group is common
			globalRateCards = globalRateCards.stream().filter(c -> doesBelongToGroup(CommonConstants.COMMA_SPLITTER.split(c.getGroups()), CommonConstants.COMMA_SPLITTER.split(data.getGroups()))).collect(Collectors.toList());

			for (RateCardData globalRateCard : globalRateCards) {
				if (globalRateCard.getScope().equalsIgnoreCase(RateCardScope.GLOBAL.name())) {
					if(globalRateCard.getChargingType().equalsIgnoreCase(data.getChargingType())) {
						globalMonetaryRateCardDataList.add(globalRateCard);
					}
				}
			}
            for (RateCardData rateCardData : rateCards) {
				if ((rncPackageID.equals(rateCardData.getRncPackageData().getId()))) {
					if (RateCardType.MONETARY.name().equalsIgnoreCase(rateCardData.getType())) {
						monetaryRateCardDataList.add(rateCardData);
					} else if (RateCardType.NON_MONETARY.name().equalsIgnoreCase(rateCardData.getType())) {
						nonMonetaryRateCardDataList.add(rateCardData);
					}
				}
            }
        }
    }

	private List<String> getStaffBelongingGroups() {
		String groups = (String) getRequest().getSession().getAttribute(Attributes.STAFF_BELONGING_GROUP_IDS);
		if (groups == null) {
			groups = "";
		}
		return CommonConstants.COMMA_SPLITTER.split(groups);
	}

	@Override
	public HttpHeaders create() {
		if (getLogger().isDebugLogLevel()) {
			getLogger().debug(getLogModule(), "Method called create()");
		}
		int orderNo = 1;
		RateCardGroupData rateCardGroupData = (RateCardGroupData) getModel();
		RncPackageData rncPackageData = rateCardGroupData.getRncPackageData();
		List<RateCardGroupData> rateCardGroups = rncPackageData.getRateCardGroupData();

		if (Collectionz.isNullOrEmpty(rateCardGroups) == false) {
			orderNo = rateCardGroups.size() + 1;
		}

		rateCardGroupData.setOrderNo(orderNo);
		setOrderNoForTimeSlotRelation(rateCardGroupData.getTimeSlotRelationData());

		if(Collectionz.isNullOrEmpty(rateCardGroupData.getTimeSlotRelationData()) == false){
			rateCardGroupData.getTimeSlotRelationData().forEach(timeSlotRelationData -> timeSlotRelationData.setRateCardGroupData(rateCardGroupData));
		}
		setModel(rateCardGroupData);

		HttpHeaders headers = super.create();
		if(hasActionErrors()){
			return new DefaultHttpHeaders(SUCCESS).withStatus(ResultCode.INTERNAL_ERROR.code).disableCaching();
		}
		setActionChainUrl(getRedirectToParentURL(rateCardGroupData.getRncPkgId()));
		return headers;
	}

	private void setOrderNoForTimeSlotRelation(List<TimeSlotRelationData> timeSlotRelationDataList) {
		int i = 1;
		for(TimeSlotRelationData timeSlotRelationData : timeSlotRelationDataList) {
			timeSlotRelationData.setOrderNo(i);
			i++;
		}
	}

	@Override
	public HttpHeaders update() {
		if (getLogger().isDebugLogLevel()) {
			getLogger().debug(getLogModule(), "Method called update()");
		}
		RateCardGroupData rateCardGroupData = (RateCardGroupData) getModel();

		RateCardGroupData rateCardGroupFromDB = CRUDOperationUtil.get(RateCardGroupData.class,
				rateCardGroupData.getId());
		rateCardGroupData.setOrderNo(rateCardGroupFromDB.getOrderNo());
		setOrderNoForTimeSlotRelation(rateCardGroupData.getTimeSlotRelationData());

        if(Collectionz.isNullOrEmpty(rateCardGroupData.getTimeSlotRelationData()) == false){
            rateCardGroupData.getTimeSlotRelationData().forEach(timeSlotRelationData -> timeSlotRelationData.setRateCardGroupData(rateCardGroupData));
        }
		setModel(rateCardGroupData);

		HttpHeaders headers = super.update();
		if(hasActionErrors()){
			return new DefaultHttpHeaders(SUCCESS).withStatus(ResultCode.INTERNAL_ERROR.code).disableCaching();
		}
		setActionChainUrl(getRedirectToParentURL(rateCardGroupData.getRncPkgId()));
		return  headers;
	}


	public String getPeakRateDataAsJson() {
		Gson gson = GsonFactory.defaultInstance();
		RateCardGroupData rateCardGroupData = (RateCardGroupData) getModel();

		if (Collectionz.isNullOrEmpty(rateCardGroupData.getTimeSlotRelationData()) == false) {
			for (TimeSlotRelationData relationData : rateCardGroupData.getTimeSlotRelationData()) {
				if (RateCardGroupTimeSlotConstant.PEAK_DAY_RATE.getValue().equals(relationData.getType())) {
					peakRateTimeSlotList.add(relationData);
				}
			}
		}

		return gson.toJsonTree(getPeakRateTimeSlotList(), new TypeToken<List<TimeSlotRelationData>>() {
		}.getType()).getAsJsonArray().toString();
	}

	public String getRateCardGroupAsJson() {
		return rateCardGroupAsJson;
	}

	public void setRateCardGroupAsJson(String rateCardGroupAsJson) {
		this.rateCardGroupAsJson = rateCardGroupAsJson;
	}

	@Override
	public HttpHeaders destroy() {// delete
		super.destroy();
		setActionChainUrl(CommonConstants.FORWARD_SLASH + NVSMXCommonConstants.RNC_PACKAGE_URL
				+ (getRequest().getParameter(NVSMXCommonConstants.RNC_PACKAGE_ID)));
		return new DefaultHttpHeaders(REDIRECT_ACTION).withStatus(ResultCode.SUCCESS.code).disableCaching();
	}


	@Override
	public boolean prepareAndValidateDestroy(RateCardGroupData rateCardGroupData) {
		if(PkgMode.LIVE.name().equalsIgnoreCase(rateCardGroupData.getRncPackageData().getMode()) || PkgMode.LIVE2.name().equalsIgnoreCase(rateCardGroupData.getRncPackageData().getMode())){
			addActionError("Rate Card Group Configured with Live or Live2 RnC package can not be deleted");
			return false;
		}
		return true;
	}

	@Override
	public void validate() {
		RateCardGroupData rateCardGroupData = (RateCardGroupData) getModel();
		String rncPkgId = getRncPackageId();
		boolean isAlreadyExist = CRUDOperationUtil.isDuplicateNameWithInParent(RateCardGroupData.class,getMethodName(),rateCardGroupData.getId(),rateCardGroupData.getResourceName(),rncPkgId,"rncPackageData");
		RncPackageData rncPackageData = null;
		String chargingType = null;

		if (isAlreadyExist) {
			addFieldError("name", getText("name.already.exist",new String[]{rateCardGroupData.getName()}));
			return;
		}
		if(Strings.isNullOrEmpty(rncPkgId)){
			rncPkgId = rateCardGroupData.getRncPkgId();
		}
		if(Strings.isNullOrEmpty(rncPkgId)){
			addFieldError("rncPkgId",getText("error.valueRequired"));
		}else{
			rncPackageData = CRUDOperationUtil.get(RncPackageData.class, rncPkgId);
			if(rncPackageData == null){
				addFieldError("rncPkgId",getText("Rnc Package does not exist"));
			}else if(PkgMode.LIVE.name().equalsIgnoreCase(rncPackageData.getMode()) || PkgMode.LIVE2.name().equalsIgnoreCase(rncPackageData.getMode())){
				addActionError("Live or Live2 Package does not allow create / update operation on Rate Card Group");
			} else {
				chargingType = rncPackageData.getChargingType();
				rateCardGroupData.setRncPackageData(rncPackageData);
			}
		}

		String peakRateRateCardId = rateCardGroupData.getPeakRateRateCardId();
		if(Strings.isNullOrEmpty(peakRateRateCardId)){
			addFieldError(PEAKRATECARDID,getText("error.valueRequired"));
		} else {
			RateCardData peakRateCardData = CRUDOperationUtil.get(RateCardData.class, peakRateRateCardId);
			if (peakRateCardData == null) {
				addFieldError(PEAKRATECARDID, getText("error.peakRateCard.not.exist"));
			} else if (peakRateCardData.getScope().equalsIgnoreCase(RateCardScope.LOCAL.name()) && peakRateCardData.getRncPackageData().getId().equals(rncPkgId) == false) {
				addFieldError(PEAKRATECARDID, getText("error.peakRateCard.not.belong.RnCPackage"));
			} else if (peakRateCardData.getScope().equalsIgnoreCase(RateCardScope.GLOBAL.name())) {
				if (doesBelongToGroup(CommonConstants.COMMA_SPLITTER.split(peakRateCardData.getGroups()), CommonConstants.COMMA_SPLITTER.split(rateCardGroupData.getRncPackageData().getGroups()))
						&& doesBelongToGroup(CommonConstants.COMMA_SPLITTER.split(peakRateCardData.getGroups()), getStaffBelongingGroups())) {
					if (Objects.isNull(rncPackageData) == false && peakRateCardData.getChargingType().equalsIgnoreCase(chargingType)) {
						rateCardGroupData.setPeakRateRateCard(peakRateCardData);
					} else {
						addActionError("You can only associate peak rate card belongs to " + chargingType + " Charging Type");
					}
				} else {
					addActionError("Peak Rate Card Not Belongs to Same Group");
				}
			} else{
				rateCardGroupData.setPeakRateRateCard(peakRateCardData);
			}
		}

		String offPeakRateRateCardId = rateCardGroupData.getOffPeakRateRateCardId();
		if(Strings.isNullOrEmpty(offPeakRateRateCardId) == false){
			RateCardData offPeakRateCardData = CRUDOperationUtil.get(RateCardData.class, offPeakRateRateCardId);
			if (offPeakRateCardData == null) {
				addFieldError(OFFPEAKRATECARDID, getText("error.offPeakRateCard.not.exist"));
			} else if (offPeakRateCardData.getScope().equalsIgnoreCase(RateCardScope.LOCAL.name()) && offPeakRateCardData.getRncPackageData().getId().equals(rncPkgId) == false) {
				addFieldError(OFFPEAKRATECARDID, getText("error.offPeakRateCard.not.belong.RnCPackage"));
			} else if (offPeakRateCardData.getScope().equalsIgnoreCase(RateCardScope.GLOBAL.name())) {
				if (doesBelongToGroup(CommonConstants.COMMA_SPLITTER.split(offPeakRateCardData.getGroups()), CommonConstants.COMMA_SPLITTER.split(rateCardGroupData.getRncPackageData().getGroups()))
						&& doesBelongToGroup(CommonConstants.COMMA_SPLITTER.split(offPeakRateCardData.getGroups()), getStaffBelongingGroups())) {
					if (Objects.isNull(rncPackageData) == false && offPeakRateCardData.getChargingType().equalsIgnoreCase(chargingType)) {
						rateCardGroupData.setOffPeakRateRateCard(offPeakRateCardData);
					} else {
						addActionError("You can only associate off peak rate card belongs to " + chargingType + " Charging Type");
					}
				} else {
					addActionError("Off Peak Rate Card Not Belongs to Same Group");
				}
			} else{
				rateCardGroupData.setOffPeakRateRateCard(offPeakRateCardData);
			}
		}else{
		    rateCardGroupData.setOffPeakRateRateCard(null);
        }

		if(Strings.isNullOrEmpty(offPeakRateRateCardId)){
		    rateCardGroupData.setTimeSlotRelationData(new ArrayList<>());
        }else{
			if((Collectionz.isNullOrEmpty(rateCardGroupData.getTimeSlotRelationData()))){
				addFieldError(OFFPEAKRATECARDID,getText("error.rate.card.group.timeslotmapping"));
			}

			int dayOfWeekMaxLength = 10;
			int timePeriodMaxLength = 50;

			List<TimeSlotRelationData> timeSlotRelationDatas = rateCardGroupData.getTimeSlotRelationData();
			Collectionz.filter(timeSlotRelationDatas, (TimeSlotRelationData timeSlotRelationData) -> {
				if(Objects.isNull(timeSlotRelationData)) {
					return false;
				}

				if((Strings.isNullOrEmpty(timeSlotRelationData.getDayOfWeek()) && Strings.isNullOrEmpty(timeSlotRelationData.getTimePeriod()))){
				    return false;
                }

				if(Strings.isNullOrEmpty(timeSlotRelationData.getDayOfWeek()) == false && timeSlotRelationData.getDayOfWeek().length()>dayOfWeekMaxLength) {
                    addFieldError(OFFPEAKRATECARDID,getText("error.field.max.length",new String[]{timeSlotRelationData.getDayOfWeek(),Integer.toString(dayOfWeekMaxLength)}));
				}

                if (Strings.isNullOrEmpty(timeSlotRelationData.getTimePeriod()) == false && timeSlotRelationData.getTimePeriod().length() > timePeriodMaxLength) {
                    addFieldError(OFFPEAKRATECARDID, getText("error.field.max.length", new String[]{ timeSlotRelationData.getTimePeriod(), Integer.toString(timePeriodMaxLength)}));
                }

				return true;
			});

			if((Collectionz.isNullOrEmpty(rateCardGroupData.getTimeSlotRelationData()))){
				addFieldError(OFFPEAKRATECARDID,getText("error.rate.card.group.timeslotmapping"));
			}

		}

	}

	public String getAdvanceConditionAsJson() {
		Gson gson = GsonFactory.defaultInstance();
		List<PCRFKeyConstants> pcrfKeyConstants = PCRFKeyConstants.values(PCRFKeyType.RULE);
		String[] autoSuggestion = new String[pcrfKeyConstants.size()];
		short index = 0;
		for (PCRFKeyConstants keyConstants : pcrfKeyConstants) {
			autoSuggestion[index] = keyConstants.getVal();
			index++;
		}
		return gson.toJson(autoSuggestion);
	}

	public void setOffPeakRateAsJson(String offPeakRateAsJson) {
		this.offPeakRateAsJson = offPeakRateAsJson;
	}

	public String getOffPeakRateAsJson() {
		Gson gson = GsonFactory.defaultInstance();
		RateCardGroupData rateCardGroupData = (RateCardGroupData) getModel();
		return gson.toJsonTree(rateCardGroupData.getTimeSlotRelationData(), new TypeToken<List<RateCardGroupData>>() {
		}.getType()).getAsJsonArray().toString();

	}

	private void setPeakRateAsJsonWrappers(List<TimeSlotRelationData> peakRateList) {
		Gson gson = GsonFactory.defaultInstance();
		setPeakRateAsJson(gson.toJsonTree(peakRateList).getAsJsonArray().toString());
	}

	private void setOffPeakRateAsJsonWrappers(List<TimeSlotRelationData> offPeakRateList) {
		Gson gson = GsonFactory.defaultInstance();
		setOffPeakRateAsJson(gson.toJsonTree(offPeakRateList).getAsJsonArray().toString());
	}

	public String getPeakRateAsJson() {
		return peakRateAsJson;
	}

	public void setPeakRateAsJson(String peakRateAsJson) {
		this.peakRateAsJson = peakRateAsJson;
	}

	public List<RateCardData> getMonetaryRateCardDataList() {
		return monetaryRateCardDataList;
	}

	public void setMonetaryRateCardDataList(List<RateCardData> monetaryRateCardDatList) {
		this.monetaryRateCardDataList = monetaryRateCardDatList;
	}

	public List<RateCardData> getGlobalMonetaryRateCardDataList() {
		return globalMonetaryRateCardDataList;
	}

	public void setGlobalMonetaryRateCardDataList(List<RateCardData> globalMonetaryRateCardDataList) {
		this.globalMonetaryRateCardDataList = globalMonetaryRateCardDataList;
	}

	public List<RateCardData> getNonMonetaryRateCardDataList() {
		return nonMonetaryRateCardDataList;
	}

	public void setNonMonetaryRateCardDataList(List<RateCardData> nonMonetaryRateCardDataList) {
		this.nonMonetaryRateCardDataList = nonMonetaryRateCardDataList;
	}

	@Override
	public List<RateCardGroupData> getList() {
		return list;
	}

	public void setList(List<RateCardGroupData> list) {
		this.list = list;
	}

	public List<TimeSlotRelationData> getPeakRateTimeSlotList() {
		return peakRateTimeSlotList;
	}

	public void setPeakRateTimeSlotList(List<TimeSlotRelationData> peakRateTimeSlotList) {
		this.peakRateTimeSlotList = peakRateTimeSlotList;
	}

	public List<TimeSlotRelationData> getOffPeakTimeSlotList() {
		return offPeakTimeSlotList;
	}

	public void setOffPeakTimeSlotList(List<TimeSlotRelationData> offPeakTimeSlotList) {
		this.offPeakTimeSlotList = offPeakTimeSlotList;
	}
}
