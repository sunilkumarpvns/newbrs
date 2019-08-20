package com.elitecore.netvertex.service.offlinernc.account;

import com.elitecore.commons.collections.Trie;
import com.elitecore.netvertex.service.offlinernc.guiding.Lob;
import com.elitecore.netvertex.service.offlinernc.partner.PartnerGroup;
import com.elitecore.netvertex.service.offlinernc.prefix.conf.PrefixConfiguration;
import com.elitecore.netvertex.service.offlinernc.productspec.ProductSpec;

public class Account {

	private String name;
	private String accountCurrency;
	private String timeZone;
	private Lob lob;
	private PartnerGroup partnerGroup;
	private ProductSpec productSpec;
	private Trie<PrefixConfiguration> prefixTrie;

	public Account(String name,
				   String accountCurrency,
				   String timeZone, PartnerGroup partnerGroup, Lob lob,
				   ProductSpec productSpec,
				   Trie<PrefixConfiguration> prefixTrie) {
		this.name = name;
		this.accountCurrency = accountCurrency;
		this.timeZone = timeZone;
		this.lob = lob;
		this.partnerGroup = partnerGroup;
		this.productSpec = productSpec;
		this.prefixTrie = prefixTrie;

	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public Lob getLob() {
		return lob;
	}

	public void setLob(Lob lob) {
		this.lob = lob;
	}

	public ProductSpec getProductSpec() {
		return productSpec;
	}

	public void setProductSpec(ProductSpec productSpec) {
		this.productSpec = productSpec;
	}
	
	public PrefixConfiguration selectPrefix(String value) {
		if (prefixTrie == null) {
			return null;
		}
		return prefixTrie.longestPrefixKeyMatch(value);
	}

	@Override
	public String toString() {
		return "name=" + name + ", accountCurrency=" + accountCurrency
				+ ", timeZone=" + timeZone + ", lob=" + lob.getName() + ", partnerGroup="
				+ partnerGroup.getName();
	}
}
