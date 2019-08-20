package com.elitecore.nvsmx.system.hibernate;

import com.elitecore.commons.logging.LogManager;
import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.Transaction;

import javax.annotation.Nullable;

/**
 * This class contains utility method relate hibernate Sessions
 * Created by aditya on 6/24/17.
 */
public class HibernateSessionUtil {


    private static final String MODULE = "HIBERNATE-SESSION-UTIL";

    private HibernateSessionUtil() {
    }

    public static void closeSession(@Nullable Session session){
        if (LogManager.getLogger().isDebugLogLevel()) {
            LogManager.getLogger().debug(MODULE, "Closing hibernate session");
        }
        try {
            if (session == null) {
                return;

            }
            if (session.isOpen()) {
                session.close();
            }
        } catch (HibernateException ex) {
            LogManager.getLogger().error(MODULE, "Error while closing session. Reason: " + ex.getMessage());
            LogManager.getLogger().trace(ex);
        }
    }


    public static void rollBackTransaction(@Nullable Transaction transaction){
        if (LogManager.getLogger().isDebugLogLevel()) {
            LogManager.getLogger().debug(MODULE, "Rolling back transaction");
        }
        try {
            if (transaction == null) {
                return;
            }
            if (transaction.wasCommitted()) {
                return;
            }
            if (transaction.wasRolledBack()) {
                return;
            }
            if (transaction.isActive() ) {
                transaction.rollback();
            }
        } catch (HibernateException ex) {
            LogManager.getLogger().error(MODULE, "Error while rolling back transaction. Reason: " + ex.getMessage());
            LogManager.getLogger().trace(ex);
        }
    }

    public static void commitTransaction(@Nullable Transaction transaction){
        if (LogManager.getLogger().isDebugLogLevel()) {
            LogManager.getLogger().debug(MODULE, "Committing transaction");
        }
        try {
            if (transaction == null) {
                return;
            }
            if (transaction.wasCommitted()) {
                return;
            }
            if (transaction.wasRolledBack()) {
                return;
            }
            if (transaction.isActive() ) {
                transaction.commit();
            }
        } catch (HibernateException ex) {
            LogManager.getLogger().error(MODULE, "Error while committing transaction. Reason: " + ex.getMessage());
            LogManager.getLogger().trace(ex);
        }
    }

    public static void syncSession(Session session) {
        session.flush();
        session.clear();

    }

}
