package com.elitecore.corenetvertex.pd.rncpackage;

import com.elitecore.commons.base.Collectionz;
import com.elitecore.commons.base.Strings;
import com.elitecore.corenetvertex.constants.FieldValueConstants;
import com.elitecore.corenetvertex.constants.RateCardType;
import com.elitecore.corenetvertex.pd.ratecard.RateCardData;
import com.elitecore.corenetvertex.pd.ratecardgroup.RateCardGroupData;
import com.elitecore.corenetvertex.pd.rncpackage.notification.RncNotificationData;
import com.elitecore.corenetvertex.pkg.PkgMode;
import com.elitecore.corenetvertex.pkg.PkgType;
import com.elitecore.corenetvertex.pkg.RnCPkgType;
import com.elitecore.corenetvertex.sm.Replicable;
import com.elitecore.corenetvertex.sm.ResourceData;
import com.google.gson.JsonObject;
import org.codehaus.jackson.annotate.JsonIgnore;
import org.hibernate.annotations.Fetch;
import org.hibernate.annotations.FetchMode;
import org.hibernate.annotations.OrderBy;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.Transient;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

@Entity(name = "com.elitecore.corenetvertex.pd.rncpackage.RncPackageData")
@Table(name = "TBLM_RNC_PACKAGE")
public class RncPackageData extends ResourceData implements Serializable,Replicable {

	private static final long serialVersionUID = 1710049796365131632L;
	private String name;
	private String description;
	private String tag;
	private String type;
	private String mode;
	private String chargingType;
	private String currency;

	private transient List<RateCardData> rateCardData;
	private transient List<RateCardGroupData> rateCardGroupData;
	private transient List<RncNotificationData> rncNotifications;

	public RncPackageData(){
		rateCardData = Collectionz.newArrayList();
		rateCardGroupData = Collectionz.newArrayList();
		rncNotifications = Collectionz.newArrayList();
	}


	@Column(name = "NAME")
	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	@Column(name = "DESCRIPTION")
	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}
	
	@Column(name = "TAG")
	public String getTag() {
		return tag;
	}

	public void setTag(String tag) {
		this.tag = tag;
	}

	@Column(name = "TYPE")
	public String getType() {
		return type;
	}

	public void setType(String type) {
		if(Strings.isNullOrBlank(type)){
			this.type = PkgType.BASE.name();
		} else {
			this.type = type;
		}
	}

	@Column(name = "PACKAGE_MODE")
	public String getMode() {
		return mode;
	}

	public void setMode(String mode) {
		this.mode = mode;
	}

	@OneToMany(cascade = { CascadeType.ALL }, fetch = FetchType.LAZY, mappedBy = "rncPackageData", orphanRemoval = true)
	@Fetch(FetchMode.SUBSELECT)
	@OrderBy(clause="name ASC")
	public List<RateCardData> getRateCardData() {
		return rateCardData;
	}

	public void setRateCardData(List<RateCardData> rateCardData) {
		this.rateCardData = rateCardData;
	}

	@OneToMany(cascade = { CascadeType.ALL }, fetch = FetchType.LAZY, mappedBy = "rncPackageData", orphanRemoval = true)
	@Fetch(FetchMode.SUBSELECT)
	@OrderBy(clause="ORDER_NO asc")
	public List<RateCardGroupData> getRateCardGroupData() {
		return rateCardGroupData;
	}

	public void setRateCardGroupData(List<RateCardGroupData> rateCardGroupData) {
		this.rateCardGroupData = rateCardGroupData;
	}

	@Override
	@Column(name = "STATUS")
	public String getStatus() {
		return super.getStatus();
	}

	@JsonIgnore
	@Transient
	@Override
	public String getResourceName() {
		return getName();
	}

	@JsonIgnore
	@Transient
	@Override
	public void setResourceName(String name) {
		setName(name);
	}

	@Override
	public RncPackageData copyModel() {
		RncPackageData newData = new RncPackageData();
		newData.setCreatedByStaff(this.getCreatedByStaff());
		newData.setCreatedDate(this.getCreatedDate());
		newData.name=this.name;
		newData.description=this.description;
		newData.tag=this.getTag();
		newData.type=this.getType();
		newData.chargingType = this.getChargingType();
		newData.currency = this.getCurrency();
		newData.setStatus(this.getStatus());
		newData.setMode(PkgMode.DESIGN.name());
	    newData.setGroups(this.getGroups());
		newData.setGroupNames(this.getGroupNames());
		List<RateCardData> newRateCardData = new ArrayList<>();
		for(RateCardData rateCard:this.getRateCardData()){
			RateCardData rateCardCopy = rateCard.copyModel();
			rateCardCopy.setId(null);
			rateCardCopy.setRncPackageData(newData);
			rateCardCopy.setCreatedDateAndStaff(this.getCreatedByStaff());
			newRateCardData.add(rateCardCopy);
		}

		newData.rateCardData = newRateCardData;
		List<RateCardGroupData> newRateCardGroupData=new ArrayList<>();
		for(RateCardGroupData rcg:this.getRateCardGroupData()){
			RateCardGroupData rcgCopy=rcg.copyModel(newData);
			rcgCopy.setId(null);
			rcgCopy.setRncPackageData(newData);
			newRateCardGroupData.add(rcgCopy);
		}
		newData.rateCardGroupData=newRateCardGroupData;
		List<RncNotificationData> notificationDataList = new ArrayList<>();
		for(RncNotificationData rncNotificationData : rncNotifications){
			RncNotificationData rncNotificationDataCopy = rncNotificationData.copyModel(newData);
			rncNotificationDataCopy.setId(null);
			rncNotificationDataCopy.setRncPackageData(newData);
			notificationDataList.add(rncNotificationDataCopy);
		}
		newData.rncNotifications = notificationDataList;
		return newData;
	}

	@Override
	@Column(name="GROUPS")
	public String getGroups() {
		return super.getGroups();
	}

	@Transient
	@Override
	public String getHierarchy() {
		return getId() + "<br>" + name;
	}


	@Override
	public JsonObject toJson() {

		JsonObject jsonObject = new JsonObject();
		jsonObject.addProperty(FieldValueConstants.NAME, name);
		jsonObject.addProperty(FieldValueConstants.DESCRIPTION, description);
		jsonObject.addProperty(FieldValueConstants.TYPE, RnCPkgType.fromName(type).getVal());
		jsonObject.addProperty(FieldValueConstants.STATUS, getStatus());
		jsonObject.addProperty(FieldValueConstants.MODE, mode);
		jsonObject.addProperty("Charging Type", chargingType);
		jsonObject.addProperty(FieldValueConstants.CURRENCY, currency);
		jsonObject.addProperty(FieldValueConstants.GROUPS, getGroupNames());
		if (Collectionz.isNullOrEmpty(rateCardData) == false) {
			JsonObject monetaryRateCardJson = new JsonObject();
			JsonObject nonMonetaryRateCardJson = new JsonObject();
			for (RateCardData rateCard : rateCardData) {
				if(RateCardType.MONETARY.name().equalsIgnoreCase(rateCard.getType())) {
					monetaryRateCardJson.add(rateCard.getName(), rateCard.toJson());
				}else if(RateCardType.NON_MONETARY.name().equalsIgnoreCase(rateCard.getType())) {
					nonMonetaryRateCardJson.add(rateCard.getName(),rateCard.toJson());
				}
			}
			jsonObject.add(FieldValueConstants.MONETARY_RATE_CARD, monetaryRateCardJson);
			jsonObject.add(FieldValueConstants.NON_MONETARY_RATE_CARD, nonMonetaryRateCardJson);
		}

		if(Collectionz.isNullOrEmpty(rateCardGroupData) == false){
			JsonObject rateCardGroupJson = new JsonObject();
			for (RateCardGroupData rateCardGroup : rateCardGroupData) {
				rateCardGroupJson.add(rateCardGroup.getName(), rateCardGroup.toJson());
			}
			jsonObject.add(FieldValueConstants.RATE_CARD_GROUP, rateCardGroupJson);
		}

		if(Collectionz.isNullOrEmpty(rncNotifications) == false){
			JsonObject rncNotificationJson = new JsonObject();
			for (RncNotificationData rncNotificationData : rncNotifications) {
				rncNotificationJson.add(rncNotificationData.getRateCardData().getName(),rncNotificationData.toJson());
			}
			jsonObject.add(FieldValueConstants.RNC_NOTIFICATION, rncNotificationJson);
		}

		return jsonObject;
	}

	@OneToMany(cascade = { CascadeType.ALL }, fetch = FetchType.LAZY, mappedBy = "rncPackageData", orphanRemoval = true)
	@Fetch(FetchMode.SUBSELECT)
	public List<RncNotificationData> getRncNotifications() {
		return rncNotifications;
	}

	public void setRncNotifications(List<RncNotificationData> rncNotifications) {
		this.rncNotifications = rncNotifications;
	}

	@Column(name="CHARGING_TYPE")
	public String getChargingType() {
		return chargingType;
	}

	public void setChargingType(String chargingType) {
		this.chargingType = chargingType;
	}

	@Column(name="CURRENCY")
	public String getCurrency() {
		return currency;
	}

	public void setCurrency(String currency) {
		this.currency = currency;
	}
}
