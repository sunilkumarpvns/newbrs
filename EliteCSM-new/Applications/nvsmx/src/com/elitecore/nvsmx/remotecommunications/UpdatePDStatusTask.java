package com.elitecore.nvsmx.remotecommunications;

import com.elitecore.core.serverx.internal.tasks.AsyncTaskContext;
import com.elitecore.core.serverx.internal.tasks.base.BaseIntervalBasedTask;
import com.elitecore.corenetvertex.systeminformation.PDContextInformation;
import com.elitecore.corenetvertex.util.SessionProvider;
import com.elitecore.nvsmx.remotecommunications.data.EndPointStatus;
import org.hibernate.Session;
import org.hibernate.Transaction;

import java.sql.Timestamp;
import java.util.concurrent.TimeUnit;

import static com.elitecore.commons.logging.LogManager.getLogger;
import static com.elitecore.nvsmx.system.hibernate.HibernateSessionUtil.closeSession;
import static com.elitecore.nvsmx.system.hibernate.HibernateSessionUtil.commitTransaction;
import static com.elitecore.nvsmx.system.hibernate.HibernateSessionUtil.rollBackTransaction;


/**
 * Created by aditya on 6/23/17.
 */
public class UpdatePDStatusTask extends BaseIntervalBasedTask {

    private static final String MODULE = "UPDATE-PD-CONTEXT-TASK";
    private SessionProvider sessionProvider;
    private PDContextInformation localPdContext;


    public UpdatePDStatusTask(SessionProvider sessionProvider, PDContextInformation localPdContext) {
        this.sessionProvider = sessionProvider;
        this.localPdContext = localPdContext;
    }


    @Override
    public long getInitialDelay() {
        return 1;
    }

    @Override
    public long getInterval() {
        return 1;
    }

    @Override
    public boolean isFixedDelay() {
        return true;
    }

    @Override
    public TimeUnit getTimeUnit() {
        return TimeUnit.HOURS;
    }


    @Override
    public void execute(AsyncTaskContext context) {
        if (getLogger().isInfoLogLevel()) {
            getLogger().info(MODULE, "Updating pd context: " + localPdContext.getName());
        }
        Session session = null;
        Transaction transaction = null;
        try {
            session = sessionProvider.getSession();
            transaction = session.beginTransaction();
            localPdContext.setLastUpdateTime(new Timestamp(System.currentTimeMillis()));
            localPdContext.setStatus(EndPointStatus.STARTED.getVal());
            session.update(localPdContext);
            commitTransaction(transaction);
        } catch (Exception e) {
            rollBackTransaction(transaction);
            getLogger().error(MODULE, "Error While updating PD Context Information. Reason: " + e.getMessage());
            getLogger().trace(MODULE, e);
        } finally {
            closeSession(session);
        }
    }

}