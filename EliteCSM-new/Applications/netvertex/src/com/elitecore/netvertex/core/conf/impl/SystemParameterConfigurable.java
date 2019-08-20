package com.elitecore.netvertex.core.conf.impl;

import java.util.List;
import com.elitecore.core.commons.configuration.LoadConfigurationException;
import com.elitecore.core.serverx.ServerContext;
import com.elitecore.corenetvertex.constants.DeploymentMode;
import com.elitecore.corenetvertex.pm.HibernateConfigurationUtil;
import com.elitecore.corenetvertex.pm.HibernateReader;
import com.elitecore.corenetvertex.sm.routing.network.CountryData;
import com.elitecore.corenetvertex.sm.routing.network.OperatorData;
import com.elitecore.corenetvertex.sm.systemparameter.SystemParameter;
import com.elitecore.corenetvertex.sm.systemparameter.SystemParameterData;
import com.elitecore.netvertex.core.conf.impl.base.BaseConfigurationImpl;
import com.elitecore.netvertex.core.util.ConfigLogger;
import org.hibernate.Session;
import org.hibernate.SessionFactory;


import static com.elitecore.commons.logging.LogManager.getLogger;

public class SystemParameterConfigurable extends BaseConfigurationImpl {

    private static final String MODULE = "SYS-PARAM-CNFGRBLE";
	
	private SessionFactory sessionFactory;
	private SystemParameterConfiguration systemParameterConfiguration;

    public SystemParameterConfigurable(ServerContext serverContext, SessionFactory sessionFactory) {
		super(serverContext);
		this.sessionFactory = sessionFactory;
		this.systemParameterConfiguration = new SystemParameterConfiguration();
	}

    @Override
    public void readConfiguration() throws LoadConfigurationException {

        getLogger().info(MODULE,"Read configuration for system parameter started");

        Session session = null;
        try {
            session = sessionFactory.openSession();
            List<SystemParameterData> systemParameters = HibernateReader.readAll(SystemParameterData.class, session);
            for (SystemParameterData systemParameterData : systemParameters) {

                SystemParameter systemParameter = SystemParameter.fromName(systemParameterData.getAlias());
                if (systemParameter == null) {
                    continue;
                }

                if (SystemParameter.CURRENCY == systemParameter) {
                    systemParameterConfiguration.setSystemCurrency(systemParameterData.getValue());
                } else if (SystemParameter.COUNTRY == systemParameter) {
                    CountryData countryData = HibernateReader.get(CountryData.class, session, systemParameterData.getValue());
                    if (countryData != null) {
                        systemParameterConfiguration.setCountry(countryData.getName());
                    }
                } else if (SystemParameter.OPERATOR == systemParameter) {
                    OperatorData operatorData = HibernateReader.get(OperatorData.class, session, systemParameterData.getValue());
                    if (operatorData != null) {
                        systemParameterConfiguration.setOperator(operatorData.getName());
                    }
                } else if (SystemParameter.DEPLOYMENT_MODE == systemParameter) {
					DeploymentMode deploymentMode = DeploymentMode.fromName(systemParameterData.getValue());
					if (deploymentMode == null) {
						if (getLogger().isInfoLogLevel()) {
							getLogger().info(MODULE, "Taking default deployment mode: "
									+ DeploymentMode.PCC + ". Reason: Invalid deployment mode configured: " + systemParameterData.getValue());
						}
						deploymentMode = DeploymentMode.PCC;
					}
					systemParameterConfiguration.setDeploymentMode(deploymentMode);
				} else if (SystemParameter.TAX == systemParameter) {
                    systemParameterConfiguration.setTax(systemParameterData.getValue());
                }
            }

            ConfigLogger.getInstance().info(MODULE, this.systemParameterConfiguration.toString());
        } catch (Exception ex) {
            throw new LoadConfigurationException("Error while reading system parameter. Reason: " + ex.getMessage(), ex);
        } finally {
            HibernateConfigurationUtil.closeQuietly(session);
        }

        getLogger().info(MODULE,"Read configuration for system parameter completed");
    }

	public SystemParameterConfiguration getSystemParameterConfiguration() {
		return systemParameterConfiguration;
	}

}
