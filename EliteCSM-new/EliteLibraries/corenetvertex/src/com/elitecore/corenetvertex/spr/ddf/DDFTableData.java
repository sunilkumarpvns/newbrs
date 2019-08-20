package com.elitecore.corenetvertex.spr.ddf;

import java.io.Serializable;
import java.sql.Timestamp;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.OrderBy;
import javax.persistence.Table;
import javax.persistence.Transient;

import org.hibernate.annotations.Fetch;
import org.hibernate.annotations.FetchMode;

import com.elitecore.commons.base.Collectionz;
import com.elitecore.corenetvertex.spr.data.SubscriberRepositoryData;
import com.elitecore.corenetvertex.util.ToStringBuilder;
import com.elitecore.corenetvertex.util.ToStringStyle;

/**
 * 
 * @author Chetan.Sankhala
 */

@Entity
@Table(name="TBLM_DDF_TABLE")
public class DDFTableData implements Serializable {
	
	private static final long serialVersionUID = 1L;
	
	private String id;
	private SubscriberRepositoryData defaultSPR;
	private List<DDFEntryData> ddfEntries; //NOSONAR
	private Timestamp createdDate;
	private Timestamp modifiedDate;
	private Long createdByStaffId;
	private Long modifiedByStaffId;
	private String clientIp;
	private String stripPrefixes;
	private String defaultSprId;
	
	public DDFTableData() { }

	public DDFTableData(String defaultSprId, String stripPrefixes, List<DDFEntryData> ddfEntries) {
		this.defaultSprId = defaultSprId;
		this.ddfEntries = ddfEntries;
		this.stripPrefixes = stripPrefixes;
	}

	@Id
	@Column(name="ID")
	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}
	
	@OneToOne(fetch=FetchType.EAGER)
	@JoinColumn(name="DEFAULT_SPR_ID")
	public SubscriberRepositoryData getDefaultSPR() {
		return defaultSPR;
	}

	public void setDefaultSPR(SubscriberRepositoryData defaultSPR) {
		this.defaultSPR = defaultSPR;
	}

	@OneToMany(fetch = FetchType.EAGER, mappedBy = "ddfTableData")
	@Fetch(FetchMode.SUBSELECT)
	@OrderBy("orderNo ASC")
	public List<DDFEntryData> getDdfEntries() {
		return ddfEntries;
	}

	public void setDdfEntries(List<DDFEntryData> ddfEntries) {
		this.ddfEntries = ddfEntries;
	}

	@Column(name="CREATEDDATE")
	public Timestamp getCreatedDate() {
		return createdDate;
	}

	public void setCreatedDate(Timestamp createdDate) {
		this.createdDate = createdDate;
	}

	@Column(name="MODIFIEDDATE")
	public Timestamp getModifiedDate() {
		return modifiedDate;
	}

	public void setModifiedDate(Timestamp modifiedDate) {
		this.modifiedDate = modifiedDate;
	}

	@Column(name="CREATEDBYSTAFFID")
	public Long getCreatedByStaffId() {
		return createdByStaffId;
	}

	public void setCreatedByStaffId(Long createdByStaffId) {
		this.createdByStaffId = createdByStaffId;
	}

	@Column(name="MODIFIEDBYSTAFFID")
	public Long getModifiedByStaffId() {
		return modifiedByStaffId;
	}

	public void setModifiedByStaffId(Long modifiedByStaffId) {
		this.modifiedByStaffId = modifiedByStaffId;
	}

	@Column(name="CLIENTIP")
	public String getClientIp() {
		return clientIp;
	}

	@Column(name="STRIP_PREFIXES")
	public String getStripPrefixes() {
		return stripPrefixes;
	}

	public void setStripPrefixes(String stripPrefixes) {
		this.stripPrefixes = stripPrefixes;
	}

	public void setClientIp(String clientIp) {
		this.clientIp = clientIp;
	}
	
	@Transient
	public String getDefaultSprId() {
		
		if (defaultSprId != null) {
			return defaultSprId;
		}
		
		if (defaultSPR != null) {
			return defaultSPR.getId();
		}
		
		return null;
	}
	
	@Override
	public String toString() {
		ToStringBuilder toStringBuilder = new ToStringBuilder(this, ToStringStyle.CUSTOM_TO_STRING_STYLE)
		.append(" -- DDF Table --")
		.append("Default SPR", getDefaultSprId())
		.append("SPR Entries: ");		
		
		if (Collectionz.isNullOrEmpty(ddfEntries) == false) {
			for (int index = 0; index < ddfEntries.size(); index++) {
				toStringBuilder.append(ddfEntries.get(index));
			}
		} else {
			toStringBuilder.append("No SPR configured in DDF");
		}
		
		return toStringBuilder.toString();
	}
}
