package com.elitecore.netvertex.service.offlinernc.prefix;

import com.elitecore.commons.collections.Trie;
import com.elitecore.corenetvertex.pd.account.PrefixListMasterData;
import com.elitecore.corenetvertex.pd.prefixes.PrefixesData;
import com.elitecore.netvertex.service.offlinernc.prefix.conf.PrefixConfiguration;

public class PrefixTrieFactory {

	public Trie<PrefixConfiguration> create(PrefixListMasterData prefixListMasterData) {
		Trie<PrefixConfiguration> prefixTrie = new Trie<>();
		
		for (PrefixesData prefixData : prefixListMasterData.getPrefixesList()) {
			PrefixConfiguration prefixConfiguration = new PrefixConfiguration(prefixData.getPrefix(), prefixData.getName(),
					prefixData.getCountryCode(), prefixData.getAreaCode());
			prefixTrie.put(prefixData.getPrefix(), prefixConfiguration);
		}
		
		return prefixTrie;
	}

}
