package com.elitecore.elitesm.datamanager.rm.concurrentloginpolicy.data;

import java.io.StringWriter;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

import javax.validation.ConstraintValidatorContext;
import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;
import javax.xml.bind.annotation.XmlType;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

import org.hibernate.validator.constraints.Length;
import org.hibernate.validator.constraints.NotEmpty;

import com.elitecore.commons.base.Collectionz;
import com.elitecore.commons.base.Differentiable;
import com.elitecore.commons.base.Strings;
import com.elitecore.core.commons.util.StringUtility;
import com.elitecore.coreradius.commons.util.RadiusUtility.TabbedPrintWriter;
import com.elitecore.elitesm.blmanager.radius.dictionary.RadiusDictionaryBLManager;
import com.elitecore.elitesm.datamanager.DataManagerException;
import com.elitecore.elitesm.datamanager.core.base.data.BaseData;
import com.elitecore.elitesm.datamanager.core.base.data.CommonStatusData;
import com.elitecore.elitesm.datamanager.radius.dictionary.data.DictionaryData;
import com.elitecore.elitesm.datamanager.radius.dictionary.data.DictionaryParameterDetailData;
import com.elitecore.elitesm.datamanager.radius.system.standardmaster.data.StandardMasterData;
import com.elitecore.elitesm.util.ConcurrentLoginPolicyUtility;
import com.elitecore.elitesm.util.constants.BaseConstant;
import com.elitecore.elitesm.util.constants.ConcurrentLoginPolicyConstant;
import com.elitecore.elitesm.util.constants.RestValidationMessages;
import com.elitecore.elitesm.ws.rest.adapter.StatusAdapter;
import com.elitecore.elitesm.ws.rest.adapter.concurrentloginpolicy.PolicyModeAdapter;
import com.elitecore.elitesm.ws.rest.adapter.concurrentloginpolicy.PolicyTypeAdapter;
import com.elitecore.elitesm.ws.rest.util.RestUtitlity;
import com.elitecore.elitesm.ws.rest.validator.ValidObject;
import com.elitecore.elitesm.ws.rest.validator.Validator;

@XmlRootElement(name = "concurrent-login-policy")
@ValidObject
@XmlType(propOrder = { "name", "commonStatusId", "description", "concurrentLoginPolicyTypeId", "loginLimit", "login", "concurrentLoginPolicyModeId", "attribute", "lstConcurrentLoginPolicyDetails" })
public class ConcurrentLoginPolicyData extends BaseData implements IConcurrentLoginPolicyData, Differentiable, Validator {
	
	private static final String UNLIMITED = "Unlimited";

	private static final String LIMITED = "Limited";

	private static final int UNLIMITED_VALUE = -1;
	
	private String concurrentLoginId;
	private String serviceParameterId;
	
	private Integer login;
	
	@NotNull(message = "Login Limit must be specified.")
	@Pattern(regexp = "(?i)(Limited|Unlimited)", message = "Invalid value of Login Limit. Value could be 'Limited' or 'Unlimited'.")
	private String loginLimit;

	@NotEmpty(message = "Concurrent Login Policy Name must be specified.")
	@Pattern(regexp = RestValidationMessages.NAME_REGEX, message = RestValidationMessages.NAME_INVALID)
	@Length(max = 50, message = "Length of Concurrent Login Policy Name must not greater than 50.")
	private String name;
	
	private String systemGenerated;
	
	@Length(max = 255, message = "Length of Description must not greater than 255.")
	private String description;
	
	@NotEmpty(message = "Status must be specified. Value could be 'ACTIVE' or 'INACTIVE'.")
	@Pattern(regexp = BaseConstant.SHOW_STATUS_ID + "|" + BaseConstant.HIDE_STATUS_ID, message = "Invalid value of Status. Value could be 'ACTIVE' or 'INACTIVE'.")
	private String commonStatusId;
	
	private Timestamp createDate;
	private String createdByStaffId;
	private Timestamp lastModifiedDate;
	private String lastModifiedByStaffId;
	private Timestamp statusChangeDate;
	
	@NotEmpty(message = "Policy Type must be specified.")
	@Pattern(regexp = ConcurrentLoginPolicyConstant.POLICY_TYPE_INDIVIDUAL_ID + "|" + ConcurrentLoginPolicyConstant.POLICY_TYPE_GROUP_ID, message = "Invalid value of Policy Type. Value could be 'Group' or 'Individual'.")
	private String concurrentLoginPolicyTypeId;
	
	@NotEmpty(message = "Policy Mode must be specified.")
	@Pattern(regexp = ConcurrentLoginPolicyConstant.POLICY_MODE_GENERAL_ID + "|" + ConcurrentLoginPolicyConstant.POLICY_MODE_SERVICE_WISE_ID, message = "Invalid value of Policy Mode. Value could be 'General' or 'Service Wise'.")
	private String concurrentLoginPolicyModeId;
	
	private CommonStatusData commonStatus;
	private StandardMasterData policyType;
	
	private List<ConcurrentLoginPolicyDetailData> lstConcurrentLoginPolicyDetail;
	
	@Valid
	private List<ConcurrentLoginPolicyDetailData> lstConcurrentLoginPolicyDetails;

	private String attribute;
	
	private String auditUId;
	
	public ConcurrentLoginPolicyData() {
		description = RestUtitlity.getDefaultDescription();
	}
	
	@XmlElement(name = "status")
	@XmlJavaTypeAdapter(StatusAdapter.class)
	public String getCommonStatusId() {
		return commonStatusId;
	}

	public void setCommonStatusId(String commonStatusId) {
		this.commonStatusId = commonStatusId;
	}

	@XmlTransient
	public String getConcurrentLoginId() {
		return concurrentLoginId;
	}

	public void setConcurrentLoginId(String concurrentLoginId) {
		this.concurrentLoginId = concurrentLoginId;
	}

	@XmlElement(name = "policy-mode")
	@XmlJavaTypeAdapter(PolicyModeAdapter.class)
	public String getConcurrentLoginPolicyModeId() {
		return concurrentLoginPolicyModeId;
	}

	public void setConcurrentLoginPolicyModeId(String concurrentLoginPolicyModeId) {
		this.concurrentLoginPolicyModeId = concurrentLoginPolicyModeId;
	}
	
	@XmlElement(name = "policy-type")
	@XmlJavaTypeAdapter(PolicyTypeAdapter.class)
	public String getConcurrentLoginPolicyTypeId() {
		return concurrentLoginPolicyTypeId;
	}

	public void setConcurrentLoginPolicyTypeId(String concurrentLoginPolicyTypeId) {
		this.concurrentLoginPolicyTypeId = concurrentLoginPolicyTypeId;
	}

	@XmlElement(name = "description")
	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	@XmlTransient
	public String getCreatedByStaffId() {
		return createdByStaffId;
	}

	public void setCreatedByStaffId(String createdByStaffId) {
		this.createdByStaffId = createdByStaffId;
	}

	@XmlTransient
	public String getLastModifiedByStaffId() {
		return lastModifiedByStaffId;
	}

	public void setLastModifiedByStaffId(String lastModifiedByStaffId) {
		this.lastModifiedByStaffId = lastModifiedByStaffId;
	}

	@XmlElement(name = "max-concurrent-login")
	public Integer getLogin() {
		return login;
	}

	public void setLogin(Integer login) {
		this.login = login;
	}
	
	@XmlElement(name = "login-limit")
	public String getLoginLimit() {
		return loginLimit;
	}
	
	public void setLoginLimit(String loginLimit) {
		this.loginLimit = loginLimit;
	}

	@XmlElement(name = "name")
	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	@XmlTransient
	public String getServiceParameterId() {
		return serviceParameterId;
	}

	public void setServiceParameterId(String serviceParameterId) {
		this.serviceParameterId = serviceParameterId;
	}

	@XmlTransient
	public String getSystemGenerated() {
		return systemGenerated;
	}

	public void setSystemGenerated(String systemGenerated) {
		this.systemGenerated = systemGenerated;
	}

	@XmlTransient
	public CommonStatusData getCommonStatus() {
		return commonStatus;
	}

	public void setCommonStatus(CommonStatusData commonStatus) {
		this.commonStatus = commonStatus;
	}

	@XmlTransient
	public StandardMasterData getPolicyType() {
		return policyType;
	}

	public void setPolicyType(StandardMasterData policyType) {
		this.policyType = policyType;
	}

	@XmlTransient
	public List getConcurrentLoginPolicyDetail() {
		return lstConcurrentLoginPolicyDetail;
	}

	public void setConcurrentLoginPolicyDetail(List lstConcurrentLoginPolicyDetail) {
		this.lstConcurrentLoginPolicyDetail = lstConcurrentLoginPolicyDetail;
	}
	
	@XmlElementWrapper(name = "attribute-details")
	@XmlElement(name = "attribute-detail", type = ConcurrentLoginPolicyDetailData.class)
	public List<ConcurrentLoginPolicyDetailData> getLstConcurrentLoginPolicyDetails() {
		return lstConcurrentLoginPolicyDetails;
	}

	public void setLstConcurrentLoginPolicyDetails(List<ConcurrentLoginPolicyDetailData> lstConcurrentLoginPolicyDetails) {
		this.lstConcurrentLoginPolicyDetails = lstConcurrentLoginPolicyDetails;
	}

	@XmlTransient
	public Timestamp getCreateDate() {
		return createDate;
	}

	public void setCreateDate(Timestamp createDate) {
		this.createDate = createDate;
	}

	@XmlTransient
	public Timestamp getLastModifiedDate() {
		return lastModifiedDate;
	}

	public void setLastModifiedDate(Timestamp lastModifiedDate) {
		this.lastModifiedDate = lastModifiedDate;
	}

	@XmlTransient
	public Timestamp getStatusChangeDate() {
		return statusChangeDate;
	}

	public void setStatusChangeDate(Timestamp statusChangeDate) {
		this.statusChangeDate = statusChangeDate;
	}

	@XmlElement(name = "attribute")
	public String getAttribute() {
		return attribute;
	}

	public void setAttribute(String attribute) {
		this.attribute = attribute;
	}

	@XmlTransient
	public String getAuditUId() {
		return auditUId;
	}
	
	public void setAuditUId(String auditUId) {
		this.auditUId = auditUId;
	}
	
	@Override
	public String toString() {
		StringWriter out = new StringWriter();
		TabbedPrintWriter writer = new TabbedPrintWriter(out);
		writer.print(StringUtility.fillChar("", 30, '-'));
		writer.print(this.getClass().getName());
		writer.println(StringUtility.fillChar("", 30, '-'));
		writer.incrementIndentation();

		writer.println("Concurrent LoginId :" + concurrentLoginId);
		writer.println("Service ParameterId :" + serviceParameterId);
		writer.println("Login :" + login);
		writer.println("Name :" + name);
		writer.println("System Generated :" + systemGenerated);
		writer.println("Description :" + description);
		writer.println("CreateDate :" + createDate);
		writer.println("Created ByStaffId :" + createdByStaffId);
		writer.println("Last ModifiedDate :" + lastModifiedDate);
		writer.println("Last Modified ByStaffId :" + lastModifiedByStaffId);
		writer.println("Status ChangeDate :" + statusChangeDate);
		writer.println("Concurrent Login Policy TypeId :" + concurrentLoginPolicyTypeId);
		writer.println("Concurrent Login Policy ModeId :" + concurrentLoginPolicyModeId);
		writer.println("CommonStatus :" + commonStatus);
		writer.println("PolicyType :" + policyType);
		writer.println("Lst Concurrent Login Policy Detail :" + lstConcurrentLoginPolicyDetail);
		writer.println("Attribute :" + attribute);

		writer.decrementIndentation();
		writer.println(StringUtility.fillChar("", 80, '-'));
		writer.close();
		return out.toString();
	}

	@Override
	public JSONObject toJson() {
		JSONObject object = new JSONObject();
		object.put("Name", name);
		object.put("Description", description);

		if (commonStatusId != null) {
			object.put("Active", (commonStatusId.equals(BaseConstant.SHOW_STATUS_ID)) ? "Active" : "Inactive");
		}

		object.put("Policy Type", getPolicyType(concurrentLoginPolicyTypeId));
		object.put("Max. Concurrent Login", login != null && login < 0 ? UNLIMITED : (LIMITED + "-" + login));
		object.put("Policy Mode", getPolicyMode(concurrentLoginPolicyModeId));
		object.put("Attribute", attribute);
		if (lstConcurrentLoginPolicyDetail != null) {
			JSONArray array = new JSONArray();
			for (ConcurrentLoginPolicyDetailData element : lstConcurrentLoginPolicyDetail) {
				array.add(element.toJson());
			}
			object.put("Attribute Detail", array);
		}
		return object;
	}

	private String getPolicyMode(String concurrentLoginPolicyModeIdValue) {
		String policyTypeVal = "";
		if ("SMS0150".equals(concurrentLoginPolicyModeIdValue)) {
			policyTypeVal = "ServiceWise";
		} else if ("SMS0149".equals(concurrentLoginPolicyModeIdValue)) {
			policyTypeVal = "General";
		}
		return policyTypeVal;
	}

	private String getPolicyType(String concurrentLoginPolicyTypeIdVal) {
		String policyTypeVal = "";
		if ("SMS0138".equals(concurrentLoginPolicyTypeIdVal)) {
			policyTypeVal = "Individual";
		} else if ("SMS0139".equals(concurrentLoginPolicyTypeIdVal)) {
			policyTypeVal = "Group";
		}
		return policyTypeVal;
	}

	@Override
	public boolean validate(ConstraintValidatorContext context) {
		
		Boolean isValid = true;
		
		if (Strings.isNullOrBlank(attribute) == false) {
			if (isValidAttributeName() == false) {
				isValid = false;
				RestUtitlity.setValidationMessage(context, "Invalid value of Attribute.");
			} else if (isValidAttributeDetailName(context)) {
				if (isDuplicateAttributeDetailName()) {
					isValid = false;
					RestUtitlity.setValidationMessage(context, "Duplication of Attribute Detail is not allowed.");
				} else if (login != null && login >-1  && validateInnerAndOuterLevelLoginLimit(context)) {
					isValid = false;
				}
			} else {
				isValid = false;
			}
		}
		
		if (Strings.isNullOrBlank(loginLimit) == false) {
			if (LIMITED.equalsIgnoreCase(loginLimit) && login == null) {
				isValid = false;
				RestUtitlity.setValidationMessage(context, "Max Concurrent Login must be specified and it must be zero or positive Integer.");
			} else if (LIMITED.equalsIgnoreCase(loginLimit) && login < 0) {
				isValid = false;
				RestUtitlity.setValidationMessage(context, "Invalid value of Max Concurrent Login. It must be zero or positive Integer.");
			}
		}
		
		if (Collectionz.isNullOrEmpty(lstConcurrentLoginPolicyDetails) == false) {
			for (ConcurrentLoginPolicyDetailData concurrentLoginPolicyDetailData : lstConcurrentLoginPolicyDetails) {
				if (Strings.isNullOrBlank(concurrentLoginPolicyDetailData.getLoginLimit()) == false) {
					if (LIMITED.equalsIgnoreCase(concurrentLoginPolicyDetailData.getLoginLimit()) && concurrentLoginPolicyDetailData.getLogin() == null) {
						isValid = false;
						RestUtitlity.setValidationMessage(context, "Max Concurrent Login of Attribute Detail must be specified and it must be zero or positive Integer.");
					} else if (LIMITED.equalsIgnoreCase(concurrentLoginPolicyDetailData.getLoginLimit()) && concurrentLoginPolicyDetailData.getLogin() < 0) {
						isValid = false;
						RestUtitlity.setValidationMessage(context, "Invalid value of Max Concurrent Login of Attribute Detail. It must be zero or positive Integer.");
					}
				}
			}
		}
		
		if (concurrentLoginPolicyModeId.equals(ConcurrentLoginPolicyConstant.POLICY_MODE_SERVICE_WISE_ID) && Strings.isNullOrBlank(attribute) == true) {
			isValid = false;
			RestUtitlity.setValidationMessage(context, "Attribute must be specified.");
		}
		
		if (concurrentLoginPolicyModeId.equals(ConcurrentLoginPolicyConstant.POLICY_MODE_GENERAL_ID)) {
			if (Strings.isNullOrBlank(attribute) == false) {
				isValid = false;
				RestUtitlity.setValidationMessage(context, "Attribute field is not required when Policy Mode is General.");
			}
			if (Collectionz.isNullOrEmpty(lstConcurrentLoginPolicyDetails) == false) {
				isValid = false;
				RestUtitlity.setValidationMessage(context, "Concurrent Login Policy Detail is not required when Policy Mode is General.");
				
			}
		}
		
		return isValid;
	}


	private boolean isValidAttributeName() {
		RadiusDictionaryBLManager dictionaryBLManager = new RadiusDictionaryBLManager();
		try {
			DictionaryData dictionaryData = dictionaryBLManager.getDictionaryDataByVendor(0);
			List<DictionaryParameterDetailData> dictionaryParameterDetailList = dictionaryData.getDictionaryParameterDetailList();
			for (DictionaryParameterDetailData dictionaryParameterDetail : dictionaryParameterDetailList) {
				if (dictionaryParameterDetail.getName().equals(attribute.trim())) {
					return true;
				}
			}
			return false;
		} catch (DataManagerException e) {
			e.printStackTrace();
			return false;
		}
	}
	
	private boolean isValidAttributeDetailName(ConstraintValidatorContext context) {
		if (Collectionz.isNullOrEmpty(lstConcurrentLoginPolicyDetails) == false) {
			List<ConcurrentLoginPolicyDetailData> concurrentLoginPolicyDetails = lstConcurrentLoginPolicyDetails;
			boolean isValidAttribute = true;
			List<String> invalidAttributes = new ArrayList<String>();

			for (ConcurrentLoginPolicyDetailData concurrentLoginPolicyDetail : concurrentLoginPolicyDetails) {
				try {
					String AttributeValueName = ConcurrentLoginPolicyUtility.getAttributeIdFromAttributeName(attribute.trim(), concurrentLoginPolicyDetail.getAttributeValue());
					if (Strings.isNullOrBlank(concurrentLoginPolicyDetail.getAttributeValue()) == false && AttributeValueName == null ) {
						isValidAttribute = false;
						invalidAttributes.add(concurrentLoginPolicyDetail.getAttributeValue());
					}
				} catch (DataManagerException e) {
					e.printStackTrace();
					RestUtitlity.setValidationMessage(context, "Invalid value of Attribute Value.");
				}
			}
			if (isValidAttribute == false) {
				RestUtitlity.setValidationMessage(context, "Invalid Attribute value(s) of Attribute Detail "+invalidAttributes+".");
				return false;
			}
		}
		return true;
	}
	
	private boolean isDuplicateAttributeDetailName() {
		if (Collectionz.isNullOrEmpty(lstConcurrentLoginPolicyDetails) == false) {
			List<ConcurrentLoginPolicyDetailData> concurrentLoginPolicyDetails = lstConcurrentLoginPolicyDetails;
			List<String> attributeDetailNames = new ArrayList<String>();

			for (ConcurrentLoginPolicyDetailData concurrentLoginPolicyDetail : concurrentLoginPolicyDetails) {
				if (attributeDetailNames.contains(concurrentLoginPolicyDetail.getAttributeValue().trim())) {
					return true;
				}
				attributeDetailNames.add(concurrentLoginPolicyDetail.getAttributeValue().trim());
			}
		}
		return false;
	}	
	
	private boolean validateInnerAndOuterLevelLoginLimit(ConstraintValidatorContext context) {
		boolean isGreaterMaxLogin = false;
		List<String> invalidMaxLoginAttributes = new ArrayList<String>();
		if (Collectionz.isNullOrEmpty(lstConcurrentLoginPolicyDetails) == false) {
			if (login == UNLIMITED_VALUE || (loginLimit != null && UNLIMITED.equalsIgnoreCase(loginLimit))) {
				return false;
			}
			for (ConcurrentLoginPolicyDetailData concurrentLoginPolicyDetail : lstConcurrentLoginPolicyDetails) {
				if (UNLIMITED.equalsIgnoreCase(concurrentLoginPolicyDetail.getLoginLimit()) || (concurrentLoginPolicyDetail.getLogin() != null && (concurrentLoginPolicyDetail.getLogin() > login))) {
					isGreaterMaxLogin = true;
					invalidMaxLoginAttributes.add(concurrentLoginPolicyDetail.getAttributeValue());
				}
			}
			if (isGreaterMaxLogin) {
				RestUtitlity.setValidationMessage(context, "Max Login Of Attribute Detail " + invalidMaxLoginAttributes + " is greater than Max Login Of basic details [ "+login+" ], which is not allowed.");
				return true;
			}
		}
		return false;
	}
	
}
