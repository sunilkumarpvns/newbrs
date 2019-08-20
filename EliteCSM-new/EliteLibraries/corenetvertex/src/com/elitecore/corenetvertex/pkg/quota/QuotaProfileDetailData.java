package com.elitecore.corenetvertex.pkg.quota;

import com.elitecore.corenetvertex.annotation.Import;
import com.elitecore.corenetvertex.core.validator.DataServiceTypeValidator;
import com.elitecore.corenetvertex.pkg.dataservicetype.DataServiceTypeData;
import com.elitecore.corenetvertex.util.ToStringBuilder;
import com.elitecore.corenetvertex.util.ToStringStyle;
import com.elitecore.corenetvertex.util.commons.jaxb.adapter.LongToStringAdapter;
import com.elitecore.corenetvertex.util.commons.jaxb.adapter.StringToIntegerAdapter;
import com.google.gson.JsonObject;
import org.apache.commons.lang.SystemUtils;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Transient;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlTransient;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;
import java.io.Serializable;
import java.util.List;

/**
 * @author aditya
 * 
 */
@Entity
@Table(name = "TBLM_QUOTA_PROFILE_DETAIL")
@XmlAccessorType(XmlAccessType.NONE)
public class QuotaProfileDetailData implements Serializable, Cloneable {
	
	private static final long serialVersionUID = 1L;

	private static final ToStringStyle QUOTA_PROFILE_DETAIL_TO_STRING_STYLE = new QuotaProfileDetailDataToString();

	private transient String id;
	private Integer fupLevel;
	private transient DataServiceTypeData dataServiceTypeData;
	private String serviceId;
	private String serviceName;
	private String aggregationKey;
	private transient QuotaProfileData quotaProfile;
	private Long total;
	private String totalUnit;
	private Long download;
	private String downloadUnit;
	private Long upload;
	private String uploadUnit;
	private Long time;
	private String timeUnit;
	private List<String> failReasons;

	@Transient
	@XmlTransient
	public List<String> getFailReasons() {
		return failReasons;
	}

	public void setFailReasons(List<String> failReasons) {
		this.failReasons = failReasons;
	}

	@Id
	@Column(name = "ID")
	@GeneratedValue(generator = "eliteSequenceGenerator")
	@XmlElement(name = "id")
	public String getId() {
		return id;
	}

	public void setId(String quotaProfileDetailId) {
		this.id = quotaProfileDetailId;
	}

	@Column(name = "FUP_LEVEL")
	@XmlElement(name = "fup-level")
	@XmlJavaTypeAdapter(StringToIntegerAdapter.class)
	public Integer getFupLevel() {
		return fupLevel;
	}

	public void setFupLevel(Integer fupLevel) {
		this.fupLevel = fupLevel;
	}

	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(name = "QUOTA_PROFILE_ID")
	@XmlTransient
	public QuotaProfileData getQuotaProfile() {
		return quotaProfile;
	}

	public void setQuotaProfile(QuotaProfileData quotaProfile) {
		this.quotaProfile = quotaProfile;
	}

	@Column(name = "AGGREGATION_KEY")
	@XmlElement(name = "aggregation-key")
	public String getAggregationKey() {
		return aggregationKey;
	}

	public void setAggregationKey(String aggregationKey) {
		this.aggregationKey = aggregationKey;
	}

	@Column(name = "TOTAL")
	@XmlElement(name = "total")
	@XmlJavaTypeAdapter(LongToStringAdapter.class)
	public Long getTotal() {
		return total;
	}

	public void setTotal(Long total) {
		this.total = total;
	}

	@Column(name = "TOTAL_UNIT")
	@XmlElement(name = "total-unit")
	public String getTotalUnit() {
		return totalUnit;
	}

	public void setTotalUnit(String totalUnit) {
		this.totalUnit = totalUnit;
	}

	@Column(name = "DOWNLOAD")
	@XmlJavaTypeAdapter(LongToStringAdapter.class)
	public Long getDownload() {
		return download;
	}

	public void setDownload(Long download) {
		this.download = download;
	}

	@Column(name = "DOWNLOAD_UNIT")
	@XmlElement(name = "download-unit")
	public String getDownloadUnit() {
		return downloadUnit;
	}

	public void setDownloadUnit(String downloadUnit) {
		this.downloadUnit = downloadUnit;
	}

	@Column(name = "UPLOAD")
	@XmlJavaTypeAdapter(LongToStringAdapter.class)
	public Long getUpload() {
		return upload;
	}

	public void setUpload(Long upload) {
		this.upload = upload;
	}

	@Column(name = "UPLOAD_UNIT")
	@XmlElement(name = "upload-unit")
	public String getUploadUnit() {
		return uploadUnit;
	}

	public void setUploadUnit(String uploadUnit) {
		this.uploadUnit = uploadUnit;
	}

	@Column(name = "TIME")
	@XmlJavaTypeAdapter(LongToStringAdapter.class)
	public Long getTime() {
		return time;
	}

	public void setTime(Long time) {
		this.time = time;
	}

	@Column(name = "TIME_UNIT")
	@XmlElement(name = "time-unit")
	public String getTimeUnit() {
		return timeUnit;
	}

	public void setTimeUnit(String timeUnit) {
		this.timeUnit = timeUnit;
	}

	@ManyToOne(fetch=FetchType.EAGER)
	@JoinColumn(name="DATA_SERVICE_TYPE_ID")
	@XmlElement(name = "dataServiceType")
	@Import(required = true, validatorClass = DataServiceTypeValidator.class)
	public DataServiceTypeData getDataServiceTypeData() {
		return dataServiceTypeData;
	}
	public void setDataServiceTypeData(DataServiceTypeData dataServiceTypeData) {
		this.dataServiceTypeData = dataServiceTypeData;
	}
	
	@Transient
	@XmlElement(name="serviceId")
	public String getServiceId() {
			return serviceId;
	}
	public void setServiceId(String serviceId) {
		this.serviceId = serviceId;
	}

	public String toString() {
	
		return new ToStringBuilder(this, QUOTA_PROFILE_DETAIL_TO_STRING_STYLE)
			.append("FUP Level", fupLevel)
			.append("Service Name", dataServiceTypeData.getName())
			.append("Aggregation Key", aggregationKey)
			.append("Total Quota", total + " " + totalUnit)
			.append("Upload Quota", upload + " " + uploadUnit)
			.append("Download Quota", download + " " + downloadUnit)
			.append("Time", time + " " + timeUnit)
			.toString();
	}

	public QuotaProfileDetailData copyModel() {
		QuotaProfileDetailData newData = new QuotaProfileDetailData();
		newData.aggregationKey = this.aggregationKey;
		newData.dataServiceTypeData = this.dataServiceTypeData;
		newData.download = this.download;
		newData.downloadUnit = this.downloadUnit;
		newData.failReasons = this.failReasons;
		newData.fupLevel = this.fupLevel;
		newData.serviceId = this.serviceId;
		newData.time = this.time;
		newData.timeUnit = this.timeUnit;
		newData.total = this.total;
		newData.totalUnit = this.totalUnit;
		newData.upload = this.upload;
		newData.uploadUnit = this.uploadUnit;
		return  newData;
	}

	private static final class QuotaProfileDetailDataToString extends ToStringStyle.CustomToStringStyle {
		
		private static final long serialVersionUID = 1L;

		QuotaProfileDetailDataToString() {
			super();
			this.setContentStart(SystemUtils.LINE_SEPARATOR);
			this.setFieldSeparator(SystemUtils.LINE_SEPARATOR + getSpaces(6) + getTabs(2));
		}
	}
	
	public void addFailReason(String string) {
		this.failReasons.add(string);
	}

	@Transient
	public String getHierarchyTree() {
		return quotaProfile.getHierarchy() + "<br>" + (fupLevel > 0 ? ("FUP" + fupLevel) : "HSQ");
	}
	
	public JsonObject toJson(){
		JsonObject jsonObject = new JsonObject();
		jsonObject.addProperty("Aggregation Key", aggregationKey);
		jsonObject.addProperty("Total", total);
		jsonObject.addProperty("Total Unit", totalUnit);
		jsonObject.addProperty("Upload", upload);
		jsonObject.addProperty("Upload Unit", uploadUnit);
		jsonObject.addProperty("Download", download);
		jsonObject.addProperty("Download Unit", downloadUnit);
		jsonObject.addProperty("Time", time);
		jsonObject.addProperty("Time Unit", timeUnit);
		JsonObject serviceTypeJson = new JsonObject();
		serviceTypeJson.add(dataServiceTypeData.getName(), jsonObject);
		return serviceTypeJson;
	}
	
	public QuotaProfileDetailData deepClone() throws CloneNotSupportedException {
		QuotaProfileDetailData newData = (QuotaProfileDetailData) this.clone();
		
		newData.id = id;
		newData.dataServiceTypeData = dataServiceTypeData == null ? null : dataServiceTypeData.deepClone();
		newData.quotaProfile = quotaProfile;
		return newData;
	}

	@Transient
	public String getServiceName() {
		return serviceName;
	}

	public void setServiceName(String serviceName) {
		this.serviceName = serviceName;
	}
}