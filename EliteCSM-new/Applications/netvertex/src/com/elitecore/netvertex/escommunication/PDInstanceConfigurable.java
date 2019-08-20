package com.elitecore.netvertex.escommunication;

import com.elitecore.commons.base.Collectionz;
import com.elitecore.core.commons.configuration.LoadConfigurationException;
import com.elitecore.core.serverx.ServerContext;
import com.elitecore.corenetvertex.pm.HibernateReader;
import com.elitecore.corenetvertex.systeminformation.PDContextInformation;
import com.elitecore.netvertex.core.conf.impl.base.BaseConfigurationImpl;
import com.elitecore.netvertex.escommunication.data.PDInstanceConfiguration;
import org.hibernate.Session;
import org.hibernate.SessionFactory;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.List;

import static com.elitecore.commons.logging.LogManager.getLogger;
import static com.elitecore.corenetvertex.pm.HibernateConfigurationUtil.closeQuietly;

public class PDInstanceConfigurable extends BaseConfigurationImpl {

    private static final String MODULE = "PD INSTANCE CONFIGURABLE";
    private SessionFactory sessionFactory;

    private List<PDInstanceConfiguration> pdInstanceConfigurationList;

    public PDInstanceConfigurable(ServerContext serverContext, SessionFactory sessionFactory) {
        super(serverContext);
        this.sessionFactory = sessionFactory;
        pdInstanceConfigurationList = new ArrayList<>();
    }

    @Override
    public void readConfiguration() throws LoadConfigurationException {

        Session session = null;
        try {
            getLogger().info(MODULE, "Reading from hibernate started");

            session = sessionFactory.openSession();

            List<PDContextInformation> pdContextInformations = HibernateReader.readAll(PDContextInformation.class, session);

            pdInstanceConfigurationList.clear();

            for (PDContextInformation pdContextInformation : pdContextInformations) {

                PDInstanceConfiguration pdInstanceConfiguration = new PDInstanceConfiguration();

                pdInstanceConfiguration.setId(pdContextInformation.getId());
                pdInstanceConfiguration.setIpAddresses(pdContextInformation.getIpAddresses());
                pdInstanceConfiguration.setPort(pdContextInformation.getPort());
                pdInstanceConfiguration.setContextPath(pdContextInformation.getContextPath());
                pdInstanceConfiguration.setDeploymentPath(pdContextInformation.getDeploymentPath());
                pdInstanceConfiguration.setHostName(pdContextInformation.getHostName());
                pdInstanceConfiguration.setStatus(pdContextInformation.getStatus());
                pdInstanceConfiguration.setLastUpdateTime(pdContextInformation.getLastUpdateTime());
                pdContextInformation.getName();

                pdInstanceConfigurationList.add(pdInstanceConfiguration);

            }
        } catch (Exception ex) {
            throw new LoadConfigurationException("Error while reloading PD Instance Configuration. Reason: " + ex.getMessage(), ex);
        } finally {
            closeQuietly(session);
        }
    }

    public List<PDInstanceConfiguration> getPDInstanceConfigurations() {
        return pdInstanceConfigurationList;
    }

    @Override
    public String toString() {
        StringWriter stringBuffer = new StringWriter();
        PrintWriter out = new PrintWriter(stringBuffer);
        out.println();
        out.println();
        out.println(" -- PD Instance Configuration -- ");
        if (Collectionz.isNullOrEmpty(pdInstanceConfigurationList)) {
            out.println("      No configuration found");
        } else {
            for(PDInstanceConfiguration pdInstanceConfiguration:pdInstanceConfigurationList){
                out.println(pdInstanceConfiguration);
                out.println();
            }
        }
        out.close();
        return stringBuffer.toString();
    }

}
