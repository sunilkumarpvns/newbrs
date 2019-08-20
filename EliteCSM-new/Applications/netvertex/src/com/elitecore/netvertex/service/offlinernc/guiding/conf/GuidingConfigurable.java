package com.elitecore.netvertex.service.offlinernc.guiding.conf;

import static com.elitecore.commons.logging.LogManager.getLogger;
import static com.elitecore.corenetvertex.pm.HibernateConfigurationUtil.closeQuietly;

import java.util.ArrayList;
import java.util.List;

import org.hibernate.Session;
import org.hibernate.SessionFactory;

import com.elitecore.core.commons.configuration.LoadConfigurationException;
import com.elitecore.core.serverx.ServerContext;
import com.elitecore.corenetvertex.pd.guiding.GuidingData;
import com.elitecore.corenetvertex.pm.HibernateReader;
import com.elitecore.corenetvertex.util.IndentingToStringBuilder;
import com.elitecore.netvertex.core.conf.impl.base.BaseConfigurationImpl;
import com.elitecore.netvertex.core.util.ConfigLogger;
import com.elitecore.netvertex.service.offlinernc.guiding.Lob;
import com.elitecore.netvertex.service.offlinernc.guiding.Service;

public class GuidingConfigurable extends BaseConfigurationImpl {

	private static final String MODULE = "GUIDING-CNFGRBL";
	
	private List<GuidingConfiguration> guidingConfigurations;
	private final SessionFactory sessionFactory;
	
	public GuidingConfigurable(ServerContext serverContext, SessionFactory sessionFactory) {
		super(serverContext);
		this.sessionFactory = sessionFactory;
		this.guidingConfigurations = new ArrayList<>();
	}

	@Override
	public void readConfiguration() throws LoadConfigurationException {

		Session session = null;

		try {
			session = sessionFactory.openSession();
			List<GuidingData> guidingDatas = HibernateReader.readAll(GuidingData.class, session);
			for (GuidingData guidingData : guidingDatas) {
				GuidingConfiguration guidingConfiguration = new GuidingConfiguration();
				guidingConfiguration.setAccountIdentifierType(guidingData.getAccountIdentifierType());
				guidingConfiguration.setAccountIdentifier(guidingData.getAccountIdentifierValue());
				guidingConfiguration.setLob(new Lob(guidingData.getLobData().getName(), guidingData.getLobData().getAlias()));
				guidingConfiguration.setEndDate(guidingData.getEndDate());
				guidingConfiguration.setStartDate(guidingData.getStartDate());
				guidingConfiguration.setPartnerName(guidingData.getAccountData().getPartnerData().getPartnerLegalName());
				guidingConfiguration.setAccountId(guidingData.getAccountData().getName());
				guidingConfiguration.setTrafficType(guidingData.getTrafficType());
				guidingConfiguration.setService(new Service(guidingData.getServiceData().getName(), guidingData.getServiceData().getId()));
				guidingConfigurations.add(guidingConfiguration);
			}

			ConfigLogger.getInstance().info(MODULE, this.toString());
			getLogger().info(MODULE, "Read Guiding configuration completed");
		} finally {
			closeQuietly(session);
		}
	}
	
	
	public List<GuidingConfiguration> getGuidingConfigurations(){
		return guidingConfigurations;
	}
	
	@Override
	public String toString() {
        IndentingToStringBuilder builder = new IndentingToStringBuilder();
        builder.newline();
        builder.appendHeading(" -- Partner Guiding Configuration -- ");

        if (guidingConfigurations.isEmpty()) {
            builder.appendValue("No configuration found");
        } else {
        	
            for (GuidingConfiguration guidingConfiguration : guidingConfigurations) {
                builder.appendHeading(" -- start of guiding configuration -- ");
                builder.appendValue(guidingConfiguration.toString());
                builder.appendHeading(" -- end of guiding configuration -- ");
                builder.newline();
            }
        }
        return builder.toString();
	}
		
}
