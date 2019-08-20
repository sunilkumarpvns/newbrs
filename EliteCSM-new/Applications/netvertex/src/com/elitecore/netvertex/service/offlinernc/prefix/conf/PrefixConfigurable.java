package com.elitecore.netvertex.service.offlinernc.prefix.conf;

import static com.elitecore.commons.logging.LogManager.getLogger;
import static com.elitecore.corenetvertex.pm.HibernateConfigurationUtil.closeQuietly;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.hibernate.Session;
import org.hibernate.SessionFactory;

import com.elitecore.core.commons.configuration.LoadConfigurationException;
import com.elitecore.core.serverx.ServerContext;
import com.elitecore.corenetvertex.pd.prefixes.PrefixesData;
import com.elitecore.corenetvertex.pm.HibernateReader;
import com.elitecore.corenetvertex.util.IndentingToStringBuilder;
import com.elitecore.netvertex.core.conf.impl.base.BaseConfigurationImpl;
import com.elitecore.netvertex.core.util.ConfigLogger;

public class PrefixConfigurable extends BaseConfigurationImpl {
	
	private static final String DEFAULT_PREFIX_LIST_MASTER = "DEFAULT_PREFIX_LIST_MASTER_1";

	private static final String MODULE = "PREFIX-CNFGRBL";
	
	private List<PrefixConfiguration> prefixConfigurations;
	private final SessionFactory sessionFactory;
	
	
	public  PrefixConfigurable(ServerContext serverContext, SessionFactory sessionFactory) {
		super(serverContext);
		this.sessionFactory=sessionFactory;
		prefixConfigurations = new ArrayList<>();
	}

	@Override
	public void readConfiguration() throws LoadConfigurationException {


		Session session = null;

		try {
			session = sessionFactory.openSession();
			List<PrefixesData> prefixesDatas = HibernateReader.readAll(PrefixesData.class, session);
			this.prefixConfigurations = prefixesDatas.stream()
				.filter(prefixesData -> DEFAULT_PREFIX_LIST_MASTER.equals(prefixesData.getPrefixMaster().getId()))
				.map(each -> {
					PrefixConfiguration prefixConfiguration = new PrefixConfiguration();
					prefixConfiguration.setPrefixCode(each.getPrefix());
					prefixConfiguration.setCountryCode(each.getCountryCode());
					prefixConfiguration.setAreaCode(each.getAreaCode());
					prefixConfiguration.setPrefixName(each.getName());
					return prefixConfiguration;
				})
				.collect(Collectors.toList());
			
			ConfigLogger.getInstance().info(MODULE, this.toString());
			getLogger().info(MODULE, "Read Guiding configuration completed");
		} finally {
			closeQuietly(session);
		}
	}
	
	
	public List<PrefixConfiguration> getPrefixConfigurations(){
		return prefixConfigurations;
	}
	
	@Override
	public String toString() {
        IndentingToStringBuilder builder = new IndentingToStringBuilder();
        builder.newline();
        builder.appendHeading(" -- Prefix Configuration -- ");

        if (prefixConfigurations.isEmpty()) {
            builder.appendValue("No configuration found");
        } else {
        	
            for (PrefixConfiguration prefixConfiguration : prefixConfigurations) {
                builder.appendHeading(" -- start of prefix configuration -- ");
                builder.appendValue(prefixConfiguration.toString());
                builder.appendHeading(" -- end of prefix configuration -- ");
                builder.newline();
            }
        }
        return builder.toString();
	}
}
