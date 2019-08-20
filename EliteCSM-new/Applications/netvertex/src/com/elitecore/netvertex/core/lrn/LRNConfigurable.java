package com.elitecore.netvertex.core.lrn;

import com.elitecore.core.commons.configuration.LoadConfigurationException;
import com.elitecore.corenetvertex.pd.lrn.LrnData;
import com.elitecore.corenetvertex.pm.HibernateReader;
import com.elitecore.corenetvertex.util.IndentingToStringBuilder;
import com.elitecore.netvertex.core.locationmanagement.LocationRepository;
import com.elitecore.netvertex.core.lrn.data.LRNConfiguration;
import com.elitecore.netvertex.core.lrn.data.LRNConfigurationRepository;
import com.elitecore.netvertex.core.util.ConfigLogger;
import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.SessionFactory;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.elitecore.commons.logging.LogManager.getLogger;
import static com.elitecore.corenetvertex.pm.HibernateConfigurationUtil.closeQuietly;

public class LRNConfigurable implements LRNConfigurationRepository {
    private static final String MODULE = "LRN-CONFGRBL";
    private SessionFactory sessionFactory;
    private LocationRepository locationRepository;
    private Map<String,LRNConfiguration> lrnConfigurationMap;

    public LRNConfigurable(SessionFactory sessionFactory, LocationRepository locationRepository) {
        this.sessionFactory = sessionFactory;
        this.locationRepository = locationRepository;
        lrnConfigurationMap = new HashMap<>();
    }
    public void read() throws LoadConfigurationException {

        Session session = null;

        try {
            session = sessionFactory.openSession();
            List<LrnData> lrnList = HibernateReader.readAll(LrnData.class, session);
            Map<String,LRNConfiguration> tempLrnConfigurationMap = new HashMap<>();
            for (LrnData lrnData: lrnList) {
                tempLrnConfigurationMap.put(lrnData.getLrn(),new LRNConfiguration(lrnData.getLrn(),locationRepository.getNetworkInformationById(lrnData.getNetworkId())));
            }
            this.lrnConfigurationMap = tempLrnConfigurationMap;

            ConfigLogger.getInstance().info(MODULE, this.toString());
            getLogger().info(MODULE, "Reading of LRN configuration completed");
        } catch (HibernateException e) {
            throw new LoadConfigurationException("Error while reading LRN configuration. Reason: " + e.getMessage(), e);
        } finally {
            closeQuietly(session);
        }
    }

    public void reload() throws LoadConfigurationException {
        read();
    }

    @Override
    public LRNConfiguration getLRNConfigurationByLRN(String lrn){
        return lrnConfigurationMap.get(lrn);
    }

    @Override
    public String toString() {
        IndentingToStringBuilder builder = new IndentingToStringBuilder();
        builder.newline();
        builder.appendHeading(" -- LRN Configurations -- ");

        if (lrnConfigurationMap.isEmpty()) {
            builder.appendValue("No configuration found");
        } else {

            for (LRNConfiguration lrn : lrnConfigurationMap.values()) {
                builder.appendHeading(" -- start of LRN configuration -- ");
                builder.appendValue(lrn.toString());
                builder.appendHeading(" -- end of LRN configuration -- ");
                builder.newline();
            }
        }
        return builder.toString();
    }
}
