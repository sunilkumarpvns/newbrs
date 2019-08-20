package com.elitecore.netvertex.core.prefix;

import com.elitecore.commons.collections.Trie;
import com.elitecore.netvertex.service.pcrf.prefix.PrefixRepository;
import com.elitecore.netvertex.service.pcrf.prefix.conf.PrefixConfiguration;
import org.mockito.Mockito;

import java.util.Arrays;
import java.util.List;
import java.util.UUID;

public class DummyPrefixRepository implements PrefixRepository {

    private Trie<PrefixConfiguration> prefixTree = new Trie<>();

    public static DummyPrefixRepository spy() {
        return Mockito.spy(new DummyPrefixRepository());
    }

    @Override
    public PrefixConfiguration getBestMatch(String param) {
        return prefixTree.longestPrefixKeyMatch(param);
    }

    @Override
    public List<PrefixConfiguration> getAnyMatch(String param) {
        return Arrays.asList(prefixTree.longestPrefixKeyMatch(param));
    }

    public PrefixConfiguration setPrefixConfiguration(String prefix) {
        PrefixConfiguration prefixConfiguration = new PrefixConfiguration();
        prefixConfiguration.setPrefix(prefix);
        prefixConfiguration.setOperator(UUID.randomUUID().toString());
        prefixConfiguration.setNetworkName(UUID.randomUUID().toString());
        prefixConfiguration.setCountry(UUID.randomUUID().toString());
        prefixTree.put(prefix, prefixConfiguration);
        return prefixConfiguration;
    }
}
