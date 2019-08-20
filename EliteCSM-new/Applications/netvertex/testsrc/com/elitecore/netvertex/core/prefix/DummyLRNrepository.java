package com.elitecore.netvertex.core.prefix;

import com.elitecore.netvertex.core.locationmanagement.data.Country;
import com.elitecore.netvertex.core.locationmanagement.data.NetworkConfiguration;
import com.elitecore.netvertex.core.lrn.data.LRNConfiguration;
import com.elitecore.netvertex.core.lrn.data.LRNConfigurationRepository;
import org.mockito.Mockito;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class DummyLRNrepository implements LRNConfigurationRepository{
    private Map<String,LRNConfiguration> lrnConfigurationMap = new HashMap<>();

    public static DummyLRNrepository spy() {return Mockito.spy(new DummyLRNrepository());}

    @Override
    public LRNConfiguration getLRNConfigurationByLRN(String lrn) {
        return lrnConfigurationMap.get(lrn);
    }

    public LRNConfiguration setLRNconfiguration(String lrn) {
        Country country = new Country(UUID.randomUUID().toString(),"Test","TE");
        NetworkConfiguration networkConfiguration = new NetworkConfiguration("101", 405,
                UUID.randomUUID().toString(), UUID.randomUUID().toString(),
                null, null, country);
        LRNConfiguration lrnConfiguration = new LRNConfiguration(lrn, networkConfiguration);

        lrnConfigurationMap.put(lrn, lrnConfiguration);
        return lrnConfiguration;
    }
}
