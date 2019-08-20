package com.elitecore.elitesm.datamanager.rm.ippool.data;

import java.io.StringWriter;
import java.sql.Timestamp;
import java.util.HashSet;
import java.util.Set;
import java.util.regex.Pattern;

import javax.validation.ConstraintValidatorContext;
import javax.validation.Valid;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;

import net.sf.json.JSONObject;

import org.hibernate.validator.constraints.NotEmpty;

import com.elitecore.commons.base.Collectionz;
import com.elitecore.commons.base.Differentiable;
import com.elitecore.core.commons.util.StringUtility;
import com.elitecore.coreradius.commons.util.RadiusUtility.TabbedPrintWriter;
import com.elitecore.elitesm.datamanager.core.base.data.BaseData;
import com.elitecore.elitesm.datamanager.core.system.staff.data.IStaffData;
import com.elitecore.elitesm.util.constants.RestValidationMessages;
import com.elitecore.elitesm.ws.rest.adapter.StatusAdapter;
import com.elitecore.elitesm.ws.rest.util.RestUtitlity;
import com.elitecore.elitesm.ws.rest.validator.ValidObject;
import com.elitecore.elitesm.ws.rest.validator.Validator;

@ValidObject
@XmlRootElement(name="ip-pool")
public class IPPoolData extends BaseData implements IIPPoolData, Differentiable, Validator{

	private static final Pattern PATTERN = Pattern.compile(RestValidationMessages.IPV4_REGEX);

	private String ipPoolId;
	
	@NotEmpty(message = "IP Pool name must be specified")
	@javax.validation.constraints.Pattern(regexp = RestValidationMessages.NAME_REGEX, message = RestValidationMessages.NAME_INVALID)
	private String name;
	private String description;
	
	@NotEmpty(message = "Plugin Status must be specified")
	@javax.validation.constraints.Pattern(regexp = "^$|CST01|CST02", message = "Invalid value of Status parameter. It can ACTIVE or INACTIVE.")
	private String commonStatusId;
	private String additionalAttributes;
	private Timestamp statusChangedDate;
	private String createdByStaffId;
	private String lastModifiedByStaffId;
	private Timestamp createDate;
	private Timestamp lastModifiedDate;
	private String nasIPAddress;
	private String ruleSet;
	private String systemGenerated;
	private String editable;

	//for validation handling purpose only
	private boolean checkValidate;

	@Valid
	private Set<IPPoolDetailData> ipPoolDetail;

	private IStaffData lastModifiedByStaff;
	private IStaffData createdByStaff;
	private String auditUId;
	private Set<IPPoolDetailData> ipPoolDetailSet;

	public IPPoolData() {
		description = RestUtitlity.getDefaultDescription();
	}

	@XmlTransient
	public String getIpPoolId() {
		return ipPoolId;
	}
	public void setIpPoolId(String ipPoolId) {
		this.ipPoolId = ipPoolId;
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
	public Timestamp getCreateDate() {
		return createDate;
	}
	public void setCreateDate(Timestamp createDate) {
		this.createDate = createDate;
	}

	@XmlTransient
	public String getCreatedByStaffId() {
		return createdByStaffId;
	}
	public void setCreatedByStaffId(String createdByStaffId) {
		this.createdByStaffId = createdByStaffId;
	}

	@XmlElement(name="description")
	public String getDescription() {
		return description;
	}
	public void setDescription(String description) {
		this.description = description;
	}

	@XmlTransient
	public String getEditable() {
		return editable;
	}
	public void setEditable(String editable) {
		this.editable = editable;
	}

	@XmlTransient
	public String getLastModifiedByStaffId() {
		return lastModifiedByStaffId;
	}
	public void setLastModifiedByStaffId(String lastModifiedByStaffId) {
		this.lastModifiedByStaffId = lastModifiedByStaffId;
	}

	@XmlTransient
	public Timestamp getLastModifiedDate() {
		return lastModifiedDate;
	}
	public void setLastModifiedDate(Timestamp lastModifiedDate) {
		this.lastModifiedDate = lastModifiedDate;
	}

	@XmlElement(name="nas-ip-address")
	public String getNasIPAddress() {
		return nasIPAddress;
	}
	public void setNasIPAddress(String nasIPAddress) {
		this.nasIPAddress = nasIPAddress;
	}

	@XmlElement(name="ruleset")
	public String getRuleSet() {
		return ruleSet;
	}
	public void setRuleSet(String ruleSet) {
		this.ruleSet = ruleSet;
	}

	@XmlElement(name="name")
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}

	@XmlTransient
	public Timestamp getStatusChangedDate() {
		return statusChangedDate;
	}
	public void setStatusChangedDate(Timestamp statusChangedDate) {
		this.statusChangedDate = statusChangedDate;
	}

	@XmlTransient
	public String getSystemGenerated() {
		return systemGenerated;
	}
	public void setSystemGenerated(String systemGenerated) {
		this.systemGenerated = systemGenerated;
	}

	@XmlTransient
	public IStaffData getLastModifiedByStaff() {
		return lastModifiedByStaff;
	}
	public void setLastModifiedByStaff(IStaffData lastModifiedByStaff) {
		this.lastModifiedByStaff = lastModifiedByStaff;
	}

	@XmlTransient
	public IStaffData getCreatedByStaff() {
		return createdByStaff;
	}
	public void setCreatedByStaff(IStaffData createdByStaff) {
		this.createdByStaff = createdByStaff;
	}

	@XmlElementWrapper(name="ip-pool-details")
	@XmlElement(name="ip-pool-data")
	public Set<IPPoolDetailData> getIpPoolDetail() {
		return ipPoolDetail;
	}
	public void setIpPoolDetail(Set<IPPoolDetailData> ipPoolDetail) {
		this.ipPoolDetail = ipPoolDetail;
	}
	
	@XmlElement(name = "additional-attributes")
	public String getAdditionalAttributes() {
		return additionalAttributes;
	}
	public void setAdditionalAttributes(String additionalAttributes) {
		this.additionalAttributes = additionalAttributes;
	}

	@XmlTransient
	public boolean isCheckValidate() {
		return checkValidate;
	}

	public void setCheckValidate(boolean checkValidate) {
		this.checkValidate = checkValidate;
	}

	@Override
	public String toString() {
		StringWriter out = new StringWriter();
		TabbedPrintWriter writer = new TabbedPrintWriter(out);
		writer.print(StringUtility.fillChar("", 30, '-'));
		writer.print(this.getClass().getName());
		writer.println(StringUtility.fillChar("", 30, '-'));
		writer.incrementIndentation();

		writer.println("Ip PoolId :" + ipPoolId);
		writer.println("Name :" + name);
		writer.println("Description :" + description);
		writer.println("Common Status Id :" + commonStatusId);
		writer.println("Additional Attributes :" + additionalAttributes);
		writer.println("Status Changed Date :" + statusChangedDate);
		writer.println("CreatedBy Staff Id :" + createdByStaffId);
		writer.println("Last ModifiedBy StaffId :" + lastModifiedByStaffId);
		writer.println("Create Date :" + createDate);
		writer.println("Last Modified Date :" + lastModifiedDate);
		writer.println("Nas IP Address :" + nasIPAddress);
		writer.println("RuleSet :" + ruleSet);
		writer.println("System Generated :" + systemGenerated);
		writer.println("Editable :" + editable);
		writer.println("Ip Pool Detail :" + ipPoolDetail);
		writer.println("Last ModifiedBy Staff :" + lastModifiedByStaff);
		writer.println("CreatedBy Staff :" + createdByStaff);

		writer.decrementIndentation();
		writer.println(StringUtility.fillChar("", 80, '-'));
		writer.close();
		return out.toString();
	}
	@Override
	public JSONObject toJson() {
		JSONObject object = new JSONObject();
		object.put("IP Pool Name", name);
		object.put("Description", description);
		object.put("NAS IP Address", nasIPAddress);
		object.put("Additional Attributes", additionalAttributes);

		if (Collectionz.isNullOrEmpty(ipPoolDetailSet) == false) {
			JSONObject ipRange = new JSONObject();
			for (IPPoolDetailData element : ipPoolDetailSet) {
				if (ipRange.containsKey(element.getIpAddressRangeId()) == false) {
					ipRange.put(element.getIpAddressRangeId(), element.getIpAddressRange());
				}
			}
			object.put("IP Pool Details", ipRange);
		}
		return object;
	}

	@XmlTransient
	public String getAuditUId() {
		return auditUId;
	}
	public void setAuditUId(String auditUId) {
		this.auditUId = auditUId;
	}

	@XmlTransient
	public Set<IPPoolDetailData> getIpPoolDetailSet() {
		return ipPoolDetailSet;
	}
	public void setIpPoolDetailSet(Set<IPPoolDetailData> ipPoolDetailSet) {
		this.ipPoolDetailSet = ipPoolDetailSet;
	}

	@Override
	public boolean validate(ConstraintValidatorContext context) {
		boolean isValid = true;
		if(Collectionz.isNullOrEmpty(ipPoolDetail) == false && checkValidate ==false){
			
			for (IPPoolDetailData ipPoolDetailData : ipPoolDetail) {
				if(ipPoolDetailData.getIpAddressRangeId() == "".trim()){
					RestUtitlity.setValidationMessage(context,"Please Specify IP Address Range Id");
					isValid = false;
				}
				if(ipPoolDetailData.getIpAddressRange() == "".trim()){
					RestUtitlity.setValidationMessage(context,"Please Specify IP Address Range");
					isValid = false;
				}
			}
			
			Set<IPPoolDetailData> ipPoolData = ipPoolDetail;
			Set<String> ipAddresRangeValue = new HashSet<String>();
			String[] ipAddressRangeArray = new String[ipPoolDetail.size()];
			int count = 0;

			for (IPPoolDetailData ipPoolDetailData : ipPoolData) {
				ipAddressRangeArray[count] = ipPoolDetailData.getIpAddressRange();
				count++;
			}

			int ipAddressRangeSize = ipAddresRangeValue.size();
			int[][] ipAddressValues = new int[ipAddressRangeSize][];

			for(int i=0;i<ipAddressRangeSize;i++){
				ipAddressValues[i] = new int[ipAddressRangeSize];
			}
			for(int index=0 ; index < ipAddressRangeSize; index++){
				if(ipAddressRangeArray[index].indexOf("-") != -1){
					if(ipAddressRangeArray[index].indexOf("-") != -1){
						String [] rangeValue= ipAddressRangeArray[index].split("-");
						if(isValidIP(rangeValue[0]) && rangeValue.length>1){
							if(org.apache.commons.lang.StringUtils.isNumeric(rangeValue[1])){
								if(!(Integer.parseInt(rangeValue[1]) <= 65536 && Integer.parseInt(rangeValue[1]) > 0)){
									RestUtitlity.setValidationMessage(context,"Invalid IP Address Range. Please provide total no of range between 0 to 65536.\n e.g. 10.106.1.25-25,10.106.1.25-65535");
									isValid = false;
								}
								int ipLongValue = ipToNumber(rangeValue[0]);
								ipAddressValues[index][0] = ipLongValue;
								ipAddressValues[index][1] = (int) (ipLongValue + Float.parseFloat(rangeValue[1]));
							}else if(isValidIP(rangeValue[1])){
								int dotIndex = 0;
								for(int i=0; i<rangeValue[0].length(); i++){
									if(rangeValue[0].charAt(i) != rangeValue[1].charAt(i)){
										RestUtitlity.setValidationMessage(context,"Network address of IP Address Range is mismatch.Please enter valid Class B network address\n e.g. IP Address Range 1.1.1.1-1.1.2.50(N/W Address : 1.1)");
										isValid = false;
									}
									if(rangeValue[0].charAt(i) == '.'){
										dotIndex++;
									}
									if(dotIndex >= 2)
										break;
								}
								ipAddressValues[index][0] = ipToNumber(rangeValue[0]);
								ipAddressValues[index][1] = ipToNumber(rangeValue[1]);
							}else {
								RestUtitlity.setValidationMessage(context,"Invalid IP Address Range: " + ipAddressRangeArray[index]);
								isValid = false;
							}
						}else {
							RestUtitlity.setValidationMessage(context,"Invalid IP Address Range: " + ipAddressRangeArray[index]);
							isValid = false;
						}
					}else if(isValidIP(ipAddressRangeArray[index])){
						ipAddressValues[index][0] = ipToNumber(ipAddressRangeArray[index]);
						ipAddressValues[index][1] = ipToNumber(ipAddressRangeArray[index]);
					}else {
						RestUtitlity.setValidationMessage(context,"Invalid IP Address Range: " + ipAddressRangeArray[index]);
						isValid = false;
					}

				}
			}

			if(isValid){
				for(int i =0 ; i<ipAddressValues.length; i++){
					for(int j=i+1; j<ipAddressValues.length; j++){
						if((ipAddressValues[j][0] >= ipAddressValues[i][0] && ipAddressValues[j][0] <= ipAddressValues[i][1]) ||
								(ipAddressValues[j][1] >= ipAddressValues[i][0] && ipAddressValues[j][1] <= ipAddressValues[i][1])) {
							RestUtitlity.setValidationMessage(context,"Range '"+ipAddressRangeArray[j]+"' overlaps  range '"+ipAddressRangeArray[i]+"'.Please Correct it.");
							isValid = false;
						}
					}
				}
			}
		}
		return isValid;
	}

	private int ipToNumber(String ipAddress) {
		byte[] bytes = ipAddress.getBytes();
		int val = 0;
		for (int i = 0; i < bytes.length; i++) {
			val <<= 8;
			val |= bytes[i] & 0xff;
		}
		return val;
	}

	private boolean isValidIP(String ip) {
		return PATTERN.matcher(ip).matches();
	}
}
