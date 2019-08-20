package com.elitecore.netvertex.service.pcrf.prefix.conf;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import com.elitecore.commons.collections.Trie;
import com.elitecore.core.commons.configuration.LoadConfigurationException;
import com.elitecore.corenetvertex.pd.prefix.PrefixData;
import com.elitecore.corenetvertex.pm.HibernateReader;
import com.elitecore.corenetvertex.util.IndentingToStringBuilder;
import com.elitecore.netvertex.core.util.ConfigLogger;
import com.elitecore.netvertex.service.pcrf.prefix.PrefixRepository;
import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.SessionFactory;


import static com.elitecore.commons.logging.LogManager.getLogger;
import static com.elitecore.corenetvertex.pm.HibernateConfigurationUtil.closeQuietly;

public class PrefixConfigurable implements PrefixRepository {
    private static final String MODULE = "PCC-PREFIX-CNFGRBL";

    private SessionFactory sessionFactory;
    private List<PrefixConfiguration> prefixConfigurations;
    private Trie<PrefixConfiguration> prefixTree;

    public PrefixConfigurable(SessionFactory sessionFactory) {
        this.sessionFactory = sessionFactory;
        prefixConfigurations = new ArrayList<>();
    }

    public void read() throws LoadConfigurationException {

        Session session = null;
        try {
            session = sessionFactory.openSession();
            List<PrefixData> prefixesDatas = HibernateReader.readAll(PrefixData.class, session);
            List<PrefixConfiguration> tempPrefixConfigurations = prefixesDatas.stream()
                    .map(each -> {
                        PrefixConfiguration prefixConfiguration = new PrefixConfiguration();
                        prefixConfiguration.setPrefix(each.getPrefix());
                        prefixConfiguration.setCountry(each.getCountryData().getName());
                        prefixConfiguration.setOperator(each.getOperatorData().getName());

                        if (each.getNetworkData() != null) {
                            prefixConfiguration.setNetworkName(each.getNetworkData().getName());
                        }
                        return prefixConfiguration;
                    })
                    .collect(Collectors.toList());

            Trie<PrefixConfiguration> tempPrefixTree = new Trie<>();
            for (PrefixConfiguration prefixConfiguration : tempPrefixConfigurations) {
                tempPrefixTree.put(prefixConfiguration.getPrefix(), prefixConfiguration);
            }

            this.prefixConfigurations = tempPrefixConfigurations;
            this.prefixTree = tempPrefixTree;
            ConfigLogger.getInstance().info(MODULE, this.toString());
            getLogger().info(MODULE, "Read Guiding configuration completed");
        } catch (HibernateException e) {
            throw new LoadConfigurationException("Error while reading device manager. Reason: " + e.getMessage(), e);
        } finally {
            closeQuietly(session);
        }
    }

    public void reload() throws LoadConfigurationException {
        read();
    }

    public List<PrefixConfiguration> getPrefixConfigurations(){
        return prefixConfigurations;
    }

    @Override
    public String toString() {
        IndentingToStringBuilder builder = new IndentingToStringBuilder();
        builder.newline();
        builder.appendHeading(" -- PCC Prefix Configuration -- ");

        if (prefixConfigurations.isEmpty()) {
            builder.appendValue("No configuration found");
        } else {

            for (PrefixConfiguration prefixConfiguration : prefixConfigurations) {
                builder.appendHeading(" -- start of PCC prefix configuration -- ");
                builder.appendValue(prefixConfiguration.toString());
                builder.appendHeading(" -- end of PCC prefix configuration -- ");
                builder.newline();
            }
        }
        return builder.toString();
    }

    @Override
    public PrefixConfiguration getBestMatch(String param) {
        return prefixTree.longestPrefixKeyMatch(param);
    }

    @Override
    public List<PrefixConfiguration> getAnyMatch(String param) {
        List<PrefixConfiguration> tempAnyMatchPrefixConf = new ArrayList<>();

        int index = 0;

        for (int nextIndex = index + 1; nextIndex <= param.length(); nextIndex++) {
            PrefixConfiguration tempPrefixConfiguration = prefixTree.longestPrefixKeyMatch(param.substring(index, nextIndex));

            if (tempPrefixConfiguration != null) {
                if (tempAnyMatchPrefixConf.contains(tempPrefixConfiguration) == false) {
                    tempAnyMatchPrefixConf.add(tempPrefixConfiguration);
                }
            }
        }

        return tempAnyMatchPrefixConf;
    }
}
