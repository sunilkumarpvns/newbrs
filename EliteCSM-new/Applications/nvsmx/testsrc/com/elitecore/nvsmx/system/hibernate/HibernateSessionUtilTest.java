package com.elitecore.nvsmx.system.hibernate;

import com.elitecore.commons.logging.ILogger;
import com.elitecore.commons.logging.LogManager;
import com.elitecore.commons.logging.NullLogger;
import com.elitecore.core.commons.InitializationFailedException;
import de.bechte.junit.runners.context.HierarchicalContextRunner;
import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import static com.elitecore.nvsmx.system.hibernate.HibernateSessionUtil.closeSession;
import static com.elitecore.nvsmx.system.hibernate.HibernateSessionUtil.commitTransaction;
import static com.elitecore.nvsmx.system.hibernate.HibernateSessionUtil.rollBackTransaction;
import static org.mockito.Mockito.atLeastOnce;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * Created by aditya on 6/27/17.
 */

@RunWith(HierarchicalContextRunner.class)
public class HibernateSessionUtilTest {

    @Mock private Session session;
    @Mock private Transaction transaction;

    @Before
    public void before() throws InitializationFailedException {
        MockitoAnnotations.initMocks(this);
        when(session.isOpen()).thenReturn(true);
        when(session.beginTransaction()).thenReturn(transaction);

    }


    public class CloseSession{

        @Test
        public void shouldSkipNullSession(){
            closeSession(null);
            verify(session,never()).close();
        }

        @Test
        public void shouldSkipAlreadyCloseSession() {
            when(session.isOpen()).thenReturn(false);
            closeSession(session);
            verify(session, never()).close();
        }

        @Test
        public void closeOpenSession() {
            when(session.isOpen()).thenReturn(true);
            closeSession(session);
            verify(session, atLeastOnce()).close();
        }

        @Test
        public void shouldLogTraceForHibernateException(){
            ILogger spyLogger = spy(new NullLogger());
            LogManager.setDefaultLogger(spyLogger);
            HibernateException hibernateException = mock(HibernateException.class);
            when(session.close()).thenThrow(hibernateException);
            closeSession(session);
            verify(spyLogger).trace(hibernateException);
        }

    }

    public class RollBackTransaction{
        @Test
        public void skipNullTransaction(){
            rollBackTransaction(null);
            verify(transaction,never()).rollback();
        }

        @Test
        public void skipInActiveTransaction(){
            when(transaction.isActive()).thenReturn(false);
            rollBackTransaction(transaction);
            verify(transaction,never()).rollback();

        }

        @Test
        public void skipAlreadyCommitedTransaction(){
            when(transaction.wasCommitted()).thenReturn(true);
            rollBackTransaction(transaction);
            verify(transaction, never()).rollback();
        }
        @Test
        public void skipAlreadyRollBackedTransaction(){
            when(transaction.wasRolledBack()).thenReturn(true);
            rollBackTransaction(transaction);
            verify(transaction, never()).rollback();
        }

        @Test
        public void rollBackActiveTransaction(){
            when(transaction.isActive()).thenReturn(true);
            rollBackTransaction(transaction);
            verify(transaction, atLeastOnce()).rollback();
        }

        @Test
        public void shouldLogTraceForHibernateException(){
            when(transaction.isActive()).thenReturn(true);
            ILogger spyLogger = spy(new NullLogger());
            LogManager.setDefaultLogger(spyLogger);
            HibernateException hibernateException = mock(HibernateException.class);
            doThrow(hibernateException).when(transaction).rollback();
            rollBackTransaction(transaction);
            verify(spyLogger).trace(hibernateException);
        }
    }


    public class CommitTransaction{
        @Test
        public void skipNullTransaction(){
            commitTransaction(null);
            verify(transaction,never()).rollback();
        }

        @Test
        public void skipInActiveTransaction(){
            when(transaction.isActive()).thenReturn(false);
            commitTransaction(transaction);
            verify(transaction,never()).commit();

        }

        @Test
        public void skipAlreadyCommitedTransaction(){
            when(transaction.wasCommitted()).thenReturn(true);
            commitTransaction(transaction);
            verify(transaction, never()).commit();
        }
        @Test
        public void skipAlreadyRollBackedTransaction(){
            when(transaction.wasRolledBack()).thenReturn(true);
            commitTransaction(transaction);
            verify(transaction, never()).rollback();
        }

        @Test
        public void commitActiveTransaction(){
            when(transaction.isActive()).thenReturn(true);
            commitTransaction(transaction);
            verify(transaction, atLeastOnce()).commit();
        }

        @Test
        public void shouldLogTraceForHibernateException(){
            when(transaction.isActive()).thenReturn(true);
            ILogger spyLogger = spy(new NullLogger());
            LogManager.setDefaultLogger(spyLogger);
            HibernateException hibernateException = mock(HibernateException.class);
            doThrow(hibernateException).when(transaction).commit();
            commitTransaction(transaction);
            verify(spyLogger).trace(hibernateException);
        }
    }


}