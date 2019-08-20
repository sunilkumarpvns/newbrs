package com.elitecore.netvertex.service.pcrf.prefix;

import java.util.List;
import com.elitecore.netvertex.service.pcrf.prefix.conf.PrefixConfiguration;

public interface PrefixRepository {
    PrefixConfiguration getBestMatch(String param);
    List<PrefixConfiguration> getAnyMatch(String param);
}
