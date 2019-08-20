package com.elitecore.nvsmx.system.interceptor;

import com.elitecore.nvsmx.system.constants.Results;
import com.elitecore.nvsmx.system.hibernate.HibernateSessionFactory;
import com.opensymphony.xwork2.ActionInvocation;
import com.opensymphony.xwork2.ValidationAware;
import com.opensymphony.xwork2.interceptor.MethodFilterInterceptor;
import com.opensymphony.xwork2.interceptor.PreResultListener;
import org.hibernate.Session;
import org.hibernate.Transaction;

import static com.elitecore.commons.logging.LogManager.getLogger;
import static com.elitecore.nvsmx.system.hibernate.HibernateSessionUtil.closeSession;
import static com.elitecore.nvsmx.system.hibernate.HibernateSessionUtil.commitTransaction;
import static com.elitecore.nvsmx.system.hibernate.HibernateSessionUtil.rollBackTransaction;

/**
 * @author kirpalsinh.raj
 * This Interceptor class is used to begin/commit/rollback Hibernate session transaction as per the result returned from the action classes.
 */

public class TransactionInterceptor extends MethodFilterInterceptor {

    private static final long serialVersionUID = 1L;
    private static final String MODULE = "TRANSACTION-INTRCPTR";
    private static final String LOGIN_ACTION_NAME = "commons/login/Login/login";
    private  String actionName = null;
    private boolean localTransaction = false;

    @Override
    public String doIntercept(ActionInvocation parentActionInvocation) throws Exception {
        Transaction transaction = null;
        Session session = null;
        actionName = parentActionInvocation.getProxy().getActionName();

        /**
         * For login action even though hibernate session factory is not initialized properly still need to invoke action
         * because in case of database failure it will authenticate with file system
         */
        if (actionName.equals(LOGIN_ACTION_NAME) && HibernateSessionFactory.isInitialized() == false) {
            return parentActionInvocation.invoke();
        }
        try {
            session = HibernateSessionFactory.getSession();
            if (session.getTransaction().isActive() == false) {
                if (getLogger().isDebugLogLevel()) {
                    getLogger().debug(MODULE, "Initializing transaction as InActive transaction found from session ");
                }
                transaction = session.beginTransaction();
                localTransaction = true;
            }

            parentActionInvocation.addPreResultListener(new PreResultListener() {
                @Override
                public void beforeResult(ActionInvocation childActionInvocation, String strResult) {

                    if (getLogger().isDebugLogLevel()) {
                        getLogger().debug(MODULE, "Received Result: " + strResult);
                    }
                    Session session = HibernateSessionFactory.getSession();
                    Transaction transaction = session.getTransaction();
                    actionName = childActionInvocation.getProxy().getActionName();
                    Results result = Results.fromValue(strResult);
                      if (result != null && result.isCommittable()) {
                        Object action = childActionInvocation.getAction();
                        if (action instanceof ValidationAware) {
                            if (((ValidationAware) action).hasActionErrors()) {
                                rollBackTransaction(transaction);
                            } else {
                                commitTransaction(transaction);
                            }
                        } else {
                            commitTransaction(transaction);
                        }

                    } else if (Results.ERROR.equals(result) || Results.REDIRECT_ERROR.equals(result)) {
                        rollBackTransaction(transaction);
                    }
                }
            });

            return parentActionInvocation.invoke();
        } catch (Exception ex) {
            getLogger().error(MODULE, "Transaction Failed for action " + actionName + ". Reason: " + ex.getMessage());
            getLogger().trace(MODULE, ex);
            rollBackTransaction(transaction);
            return parentActionInvocation.invoke();

        } finally {
            if (localTransaction) {
                if (getLogger().isDebugLogLevel()) {
                    getLogger().debug(MODULE, "Closing session ");
                }
                closeSession(session);
            }
        }
    }

}
