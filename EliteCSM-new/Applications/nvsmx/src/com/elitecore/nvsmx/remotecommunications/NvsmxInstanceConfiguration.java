package com.elitecore.nvsmx.remotecommunications;

import com.elitecore.commons.base.Collectionz;
import com.elitecore.core.commons.conf.impl.BaseConfigurationImpl;
import com.elitecore.core.serverx.ServerContext;
import com.elitecore.corenetvertex.systeminformation.PDContextInformation;
import com.elitecore.corenetvertex.util.SessionProvider;
import org.hibernate.Session;
import org.hibernate.Transaction;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.List;
import java.util.concurrent.TimeUnit;

import static com.elitecore.commons.logging.LogManager.getLogger;
import static com.elitecore.nvsmx.system.hibernate.HibernateSessionUtil.closeSession;
import static com.elitecore.nvsmx.system.hibernate.HibernateSessionUtil.commitTransaction;
import static com.elitecore.nvsmx.system.hibernate.HibernateSessionUtil.rollBackTransaction;

/**
 * Created by aditya on 5/25/17.
 */
public class NvsmxInstanceConfiguration extends BaseConfigurationImpl {

    private static final String MODULE = "NVSMX-INSTANCE-CONF";
    private static final long INTERVAL_OF_7_DAYS = TimeUnit.DAYS.toMillis(7);
    @Nonnull
    private SessionProvider sessionProvider;
    @Nullable
    private List<PDContextInformation> pdContextInformations;

    public NvsmxInstanceConfiguration(@Nonnull SessionProvider sessionProvider, @Nonnull ServerContext serverContext) {
        super(serverContext);
        this.sessionProvider = sessionProvider;
    }

    @Override
    public void readConfiguration() {
        if (getLogger().isInfoLogLevel()) {
            getLogger().info(MODULE, "Read configuration for PD context started");
        }
        List<PDContextInformation> allPDContexts = getAllPDContexts();

        if (Collectionz.isNullOrEmpty(allPDContexts)) {
            return;
        }

        this.pdContextInformations = flushOldPdContexts(allPDContexts);
        if (getLogger().isDebugLogLevel()) {
            getLogger().debug(MODULE, "Read configuration for PD context successfully completed");
        }
    }

    public @Nullable List<PDContextInformation> getPdContextInformations() {
        return pdContextInformations;
    }

    private @Nullable List<PDContextInformation> getAllPDContexts() {
        Session session = null;
        boolean isExternalTransaction = true;
        try {
            session = sessionProvider.getSession();
            Transaction transaction = session.getTransaction();
            if(transaction.isActive() == false) {
                isExternalTransaction = false;
                session.beginTransaction();
            }
            return session.createCriteria(PDContextInformation.class).list();
        } catch (Exception e) {
            getLogger().error(MODULE, "Error while reading PD context. Reason: " + e.getMessage());
            getLogger().trace(e);
        } finally {
            if(isExternalTransaction == false) {
                closeSession(session);
            }
        }
        return null;

    }

    private @Nonnull List<PDContextInformation> flushOldPdContexts(List<PDContextInformation> allPDContexts) {

        Long currentTimeInMillis = System.currentTimeMillis();
        List<PDContextInformation> filteredPdContext = Collectionz.newArrayList();

        for (PDContextInformation pdContext : allPDContexts) {
            if (getLogger().isInfoLogLevel()) {
                getLogger().info(MODULE, pdContext.toString());
            }

            if (isInActiveForSevenDays(currentTimeInMillis, pdContext) == false) {
                filteredPdContext.add(pdContext);
                continue;
            }
            if (getLogger().isWarnLogLevel()) {
                getLogger().warn(MODULE, "Deleting pd context: " + pdContext.getName() + ". Reason: Instance is in InActive state for more than 7 days and last update time is: "+ pdContext.getLastUpdateTime());
            }
            deletePdContext(pdContext);
        }
        return filteredPdContext;
    }

    private void deletePdContext(PDContextInformation pdContext) {
        Session session = null;
        Transaction transaction = null;
        boolean isExternalTransaction = true;
        try {
            session = sessionProvider.getSession();
            transaction = session.getTransaction();
            if(transaction.isActive() == false) {
                isExternalTransaction = false;
                session.beginTransaction();
            }
            session.delete(pdContext);
        } catch (Exception e) {
            rollBackTransaction(transaction);
            getLogger().error(MODULE, "Error while deleting PDContext Information. Reason: " + e.getMessage());
            getLogger().trace(MODULE, e);
        } finally {
            if(isExternalTransaction == false) {
                closeSession(session);
            }
        }
    }

    private boolean isInActiveForSevenDays(Long currentTimeInMillis, PDContextInformation pdContext) {
        if(pdContext.getLastUpdateTime() == null){
            return false;
        }
        return INTERVAL_OF_7_DAYS <= (currentTimeInMillis - pdContext.getLastUpdateTime().getTime());
    }
}
