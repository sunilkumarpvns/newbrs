package com.elitecore.nvsmx.remotecommunications;

import com.elitecore.commons.base.Collectionz;
import com.elitecore.core.commons.conf.impl.BaseConfigurationImpl;
import com.elitecore.core.serverx.ServerContext;
import com.elitecore.corenetvertex.sm.servergroup.ServerGroupData;
import com.elitecore.corenetvertex.util.SessionProvider;
import com.elitecore.nvsmx.system.hibernate.HibernateSessionUtil;
import org.hibernate.Session;
import org.hibernate.Transaction;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;

import static com.elitecore.commons.logging.LogManager.getLogger;

/**
 * Created by aditya on 5/25/17.
 */
public class NetvertexInstanceGroupConfiguration extends BaseConfigurationImpl {

    private static final String MODULE = "NET-INSTANCE-GROUP-CONF";
    private @Nonnull SessionProvider sessionProvider;
    private @Nullable List<ServerGroupData> serverGroupDataList;

    public NetvertexInstanceGroupConfiguration(@Nonnull SessionProvider sessionProvider,@Nonnull ServerContext serverContext) {
        super(serverContext);
        this.sessionProvider = sessionProvider;
    }


    @Override
    public void readConfiguration() {
        if (getLogger().isInfoLogLevel()) {
            getLogger().info(MODULE, "Read configuration for Server Group data started");
        }

        Session session = null;
        boolean isExternalTransaction = true;
        try {
            session = sessionProvider.getSession();
            Transaction transaction = session.getTransaction();
            if(transaction.isActive() == false) {
                isExternalTransaction = false;
                session.beginTransaction();
            }

            List<ServerGroupData> serverGroupList = session.createCriteria(ServerGroupData.class).list();
            if (serverGroupList.isEmpty() && getLogger().isInfoLogLevel()) {
                getLogger().info(MODULE, "No Server Group configured");

                this.serverGroupDataList = serverGroupList;
                return;
            }

            List<ServerGroupData> tempServerGroupDataList = new ArrayList<>();
            for (ServerGroupData serverGroupData : serverGroupList) {
                if (Collectionz.isNullOrEmpty(serverGroupData.getServerInstances())) {
                    if (getLogger().isWarnLogLevel()) {
                        getLogger().warn(MODULE, "Skipping Server Group: " + serverGroupData.getName() + ". Reason: no Server Instance configured in group");
                    }
                    continue;
                }

                if (getLogger().isInfoLogLevel()) {
                    getLogger().info(MODULE, serverGroupData.toString());
                }
                if (Collectionz.isNullOrEmpty(serverGroupData.getServerInstances()) == false) {
                    tempServerGroupDataList.add(serverGroupData);
                }
            }

            this.serverGroupDataList = tempServerGroupDataList;

            if (getLogger().isDebugLogLevel()) {
                getLogger().debug(MODULE, "Read configuration for Server Groups successfully completed");
            }
        } catch (Exception e) {
            getLogger().error(MODULE, "Error while reading ServerGroup. Reason: " + e.getMessage());
            getLogger().trace(MODULE, e);
        } finally {
            if(isExternalTransaction == false){
                HibernateSessionUtil.closeSession(session);
            }

        }

    }

    public @Nullable List<ServerGroupData> getServerInstanceGroupDatas() {
        return serverGroupDataList;
    }
}
