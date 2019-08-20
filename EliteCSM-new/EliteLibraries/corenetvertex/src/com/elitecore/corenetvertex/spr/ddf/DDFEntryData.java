package com.elitecore.corenetvertex.spr.ddf;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import javax.persistence.Transient;

import com.elitecore.corenetvertex.spr.data.SubscriberRepositoryData;


/** 
 * DDFSPREntry indicates one entry for DDF table
 * 
 * @author Chetan.Sankhala
 */

@Entity
@Table(name="TBLM_DDF_SPR_REL")
public class DDFEntryData {
	
	private String id;
	private String identityPattern;
	private SubscriberRepositoryData subscriberRepositoryData;
	private Integer orderNo;
	
	private String sprId;
	
	transient private DDFTableData ddfTableData;
	
	public DDFEntryData() { }
	
	public DDFEntryData(String identityPattern, String sprId, int orderNo) {
		this.identityPattern = identityPattern;
		this.sprId = sprId;
		this.orderNo = orderNo;
	}

	@Id
	@Column(name="ID")
	@GeneratedValue(generator="eliteSequenceGenerator")
	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	@Column(name="IDENTITY_PATTERN")
	public String getIdentityPattern() {
		return identityPattern;
	}

	public void setIdentityPattern(String identityPattern) {
		this.identityPattern = identityPattern;
	}

	@OneToOne(fetch=FetchType.EAGER)
	@JoinColumn(name="SPR_ID")
	public SubscriberRepositoryData getSubscriberRepositoryData() {
		return subscriberRepositoryData;
	}

	public void setSubscriberRepositoryData(SubscriberRepositoryData subscriberRepositoryData) {
		this.subscriberRepositoryData = subscriberRepositoryData;
	}

	@Column(name="ORDER_NO")
	public Integer getOrderNo() {
		return orderNo;
	}

	public void setOrderNo(Integer orderNo) {
		this.orderNo = orderNo;
	}
	
	@ManyToOne(fetch=FetchType.EAGER)
	@JoinColumn(name="DDF_TABLE_ID")
	public DDFTableData getDdfTableData() {
		return ddfTableData;
	}

	public void setDdfTableData(DDFTableData ddfTableData) {
		this.ddfTableData = ddfTableData;
	}
	
	@Transient
	public String getSprId() {
		
		if (sprId != null) {
			return sprId;
		}

		if (subscriberRepositoryData != null) {
			return subscriberRepositoryData.getId();
		}
		
		return null;
	}
	
	@Override
	public String toString() {
		return orderNo + ") Identity Pattern:" + identityPattern + " SPR: " + getSprId();
	}
}
