package com.elitecore.corenetvertex.pd.account;

import java.io.Serializable;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import javax.xml.bind.annotation.XmlTransient;

import org.codehaus.jackson.annotate.JsonIgnore;
import org.hibernate.annotations.Fetch;
import org.hibernate.annotations.FetchMode;

@Entity(name = "com.elitecore.corenetvertex.pd.account.AccountPrefixMasterRelationData")
@Table(name = "TBLM_ACC_PREFIX_MASTER_REL")
public class AccountPrefixMasterRelationData implements Serializable{
	private static final long serialVersionUID = 4156583982035289207L;
	
	private String id;
	private  PrefixListMasterData prefixListMasterData;
	private transient AccountData accountData;

	@Id
	@Column(name = "ID")
	@GeneratedValue(generator = "eliteSequenceGenerator")
	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	@OneToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "ACCOUNT_ID")
	@Fetch(FetchMode.JOIN)
	@XmlTransient
	@JsonIgnore
	public AccountData getAccountData() {
		return accountData;
	}

	public void setAccountData(AccountData accountData) {
		this.accountData = accountData;
	}

	@OneToOne(cascade = { CascadeType.ALL }, fetch = FetchType.LAZY, orphanRemoval = false)
	@JoinColumn(name = "PREFIX_LIST_MASTER_ID")
	@Fetch(FetchMode.JOIN)
	public PrefixListMasterData getPrefixListMasterData() {
		return prefixListMasterData;
	}

	public void setPrefixListMasterData(PrefixListMasterData prefixListMasterData) {
		this.prefixListMasterData = prefixListMasterData;
	}

}