package com.elitecore.corenetvertex.pd.account;

import java.io.Serializable;
import java.sql.Timestamp;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import javax.persistence.Transient;
import javax.xml.bind.annotation.XmlTransient;

import org.codehaus.jackson.annotate.JsonIgnore;
import org.hibernate.annotations.Fetch;
import org.hibernate.annotations.FetchMode;

import com.elitecore.commons.base.Strings;
import com.elitecore.corenetvertex.constants.FieldValueConstants;
import com.elitecore.corenetvertex.pd.lob.LobData;
import com.elitecore.corenetvertex.pd.partner.PartnerData;
import com.elitecore.corenetvertex.pd.partnergroup.PartnerGroupData;
import com.elitecore.corenetvertex.pd.productoffer.ProductSpecData;
import com.elitecore.corenetvertex.sm.DefaultGroupResourceData;
import com.google.gson.JsonObject;

@Entity(name = "com.elitecore.corenetvertex.sm.account.AccountData")
@Table(name = "TBLM_ACCOUNT")
public class AccountData extends DefaultGroupResourceData implements Serializable {

	private static final long serialVersionUID = 4613917399784973205L;

	private String name;
	private String accountManager;
	private String accountCurrency;
	private Timestamp creationDate;
	private String timeZone;
	private String partnerId;
	private LobData lob;
	private PartnerGroupData partnerGroup;
	private transient ProductSpecData productSpecification;

	private transient PartnerData partnerData;

	private AccountPrefixMasterRelationData accountPrefixMasterRelation;
	
	@OneToOne(cascade = { CascadeType.ALL }, fetch = FetchType.EAGER, mappedBy = "accountData", orphanRemoval = false)
	public AccountPrefixMasterRelationData getAccountPrefixMasterRelation() {
		return accountPrefixMasterRelation;
	}

	public void setAccountPrefixMasterRelation(AccountPrefixMasterRelationData accountPrefixMasterRelation) {
		this.accountPrefixMasterRelation = accountPrefixMasterRelation;
	}
	
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "PARTNER_ID")
	@Fetch(FetchMode.JOIN)
	@XmlTransient
	@JsonIgnore
	public PartnerData getPartnerData() {
		return partnerData;
	}

	public void setPartnerData(PartnerData partnerData) {
		this.partnerData = partnerData;
	}

	@Transient
	public String getPartnerId() {
		if (partnerData != null) {
			this.partnerId = this.partnerData.getId();
		}
		return partnerId;
	}

	public void setPartnerId(String partnerId) {

		if (Strings.isNullOrBlank(partnerId) == false) {
			PartnerData partner = new PartnerData();
			partner.setId(partnerId);
			this.partnerData = partner;
		}
		this.partnerId = partnerId;
	}

	@Column(name = "NAME")
	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	@Column(name = "ACCT_MANAGER")
	public String getAccountManager() {
		return accountManager;
	}

	public void setAccountManager(String accountManager) {
		this.accountManager = accountManager;
	}

	@Column(name = "ACCT_CURRENCY")
	public String getAccountCurrency() {
		return accountCurrency;
	}

	public void setAccountCurrency(String accountCurrency) {
		this.accountCurrency = accountCurrency;
	}

	@Column(name = "CREATION_DATE")
	public Timestamp getCreationDate() {
		return creationDate;
	}

	public void setCreationDate(Timestamp creationDate) {
		this.creationDate = creationDate;
	}

	@Column(name = "TIMEZONE")
	public String getTimeZone() {
		return timeZone;
	}

	public void setTimeZone(String timeZone) {
		this.timeZone = timeZone;
	}

	@OneToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "LOB_ID")
	@Fetch(FetchMode.JOIN)
	public LobData getLob() {
		return lob;
	}

	public void setLob(LobData lob) {
		this.lob = lob;
	}

	@OneToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "PARTNER_GROUP_ID")
	@Fetch(FetchMode.JOIN)
	public PartnerGroupData getPartnerGroup() {
		return partnerGroup;
	}

	public void setPartnerGroup(PartnerGroupData partnerGroup) {
		this.partnerGroup = partnerGroup;
	}

	@OneToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "PRODUCT_SPEC_ID")
	@Fetch(FetchMode.JOIN)
	public ProductSpecData getProductSpecification() {
		return productSpecification;
	}

	public void setProductSpecification(ProductSpecData productSpecification) {
		this.productSpecification = productSpecification;
	}

	@Transient
	public String getProductSpecificationId() {
		if (this.getProductSpecification() != null) {
			return getProductSpecification().getId();
		}
		return null;
	}

	public void setProductSpecificationId(String productSpecificationId) {
		if (Strings.isNullOrBlank(productSpecificationId) == false) {
			ProductSpecData productSpecificationObj = new ProductSpecData();
			productSpecificationObj.setId(productSpecificationId);
			this.productSpecification = productSpecificationObj;
		}
	}

	@Transient
	public String getLobId() {
		if (this.getLob() != null) {
			return getLob().getId();
		}
		return null;
	}

	public void setLobId(String lobId) {
		if (Strings.isNullOrBlank(lobId) == false) {
			LobData lobIdj = new LobData();
			lobIdj.setId(lobId);
			this.lob = lobIdj;
		}
	}

	@Transient
	public String getPartnerGroupId() {
		if (this.getPartnerGroup() != null) {
			return getPartnerGroup().getId();
		}
		return null;
	}

	public void setPartnerGroupId(String partnerGroupId) {
		if (Strings.isNullOrBlank(partnerGroupId) == false) {
			PartnerGroupData partnerGroupObj = new PartnerGroupData();
			partnerGroupObj.setId(partnerGroupId);
			this.partnerGroup = partnerGroupObj;
		}
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

	@Override
	public JsonObject toJson() {

		JsonObject jsonObject = new JsonObject();
		jsonObject.addProperty(FieldValueConstants.NAME, name);
		jsonObject.addProperty(FieldValueConstants.ACCT_MANAGER, accountManager);
		jsonObject.addProperty(FieldValueConstants.ACCT_CURRENCY, accountCurrency);
		jsonObject.addProperty(FieldValueConstants.CREATION_DATE, String.valueOf(creationDate));
		jsonObject.addProperty(FieldValueConstants.TIME_ZONE, timeZone);
		if (getLob() != null) {
			jsonObject.addProperty(FieldValueConstants.LOB, lob.getName());
		}
		if (getPartnerGroup() != null) {
			jsonObject.addProperty(FieldValueConstants.PARTNERGROUP_ID, partnerGroup.getName());
		}
		if (getProductSpecification() != null) {
			jsonObject.addProperty(FieldValueConstants.PRODUCT_SPEC_ID, productSpecification.getName());
		}
		jsonObject.addProperty(FieldValueConstants.STATUS, getStatus());
		jsonObject.addProperty(FieldValueConstants.GROUPS, getGroups());

		return jsonObject;
	}

}