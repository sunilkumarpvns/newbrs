package com.elitecore.corenetvertex.pd.account;

import java.io.Serializable;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.Transient;

import org.hibernate.annotations.Fetch;
import org.hibernate.annotations.FetchMode;
import org.hibernate.annotations.Where;

import com.elitecore.commons.base.Collectionz;
import com.elitecore.corenetvertex.pd.prefixes.PrefixesData;
import com.elitecore.corenetvertex.sm.ResourceData;

@Entity(name = "com.elitecore.corenetvertex.pd.account.PrefixListMasterData")
@Table(name = "TBLM_PREFIX_LIST_MASTER")
public class PrefixListMasterData extends ResourceData implements Serializable {

	private static final long serialVersionUID = -4637852691111023446L;

	private List<PrefixesData> prefixesList;

	public PrefixListMasterData() {
		prefixesList = Collectionz.newArrayList();
	}

	@OneToMany(cascade = { CascadeType.ALL }, fetch = FetchType.LAZY, mappedBy = "prefixMaster", orphanRemoval = false)
	@Fetch(FetchMode.SUBSELECT)
	@Where(clause = "STATUS != 'DELETED'")
	public List<PrefixesData> getPrefixesList() {
		return prefixesList;
	}

	public void setPrefixesList(List<PrefixesData> prefixesList) {
		this.prefixesList = prefixesList;
	}

	@Override
	@Column(name = "STATUS")
	public String getStatus() {
		return super.getStatus();
	}

	@Transient
	@Override
	public String getResourceName() {
		return null;
	}
}