package com.elitecore.core.commons.utilx;

import com.elitecore.core.commons.utilx.db.TransactionException;
import de.bechte.junit.runners.context.HierarchicalContextRunner;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.sql.Connection;
import java.sql.SQLException;

import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.verify;

@RunWith(HierarchicalContextRunner.class)
public class DBTransactionSupportTest {
    @Mock
    private Connection connection;
    @Mock
    private DummyDBTransactionSupport dbTransactionSupport;

    @Rule
    public ExpectedException expectedException = ExpectedException.none();

    @Before
    public void setup() {
        MockitoAnnotations.initMocks(this);
        dbTransactionSupport = new DummyDBTransactionSupport();
    }

    public class Statement {
        @Test
        public void Throws_transactionException_when_connection_is_null() throws Exception {
            dbTransactionSupport = new DummyDBTransactionSupport();
            expectedException.expect(TransactionException.class);
            expectedException.expectMessage("Error in creating statement. Reason: Connection is null");
            dbTransactionSupport.statement(null);
        }

        @Test
        public void Create_statement_from_connection() throws Exception {
            dbTransactionSupport.statement(connection);
            verify(connection).createStatement();
        }

        @Test
        public void Mark_dead_called_when_sql_exception_is_DB_down_Exception() throws Exception{
            dbTransactionSupport.setSQLExMarkDBDown();
            doThrow(SQLException.class).when(connection).createStatement();
            try {
                dbTransactionSupport.statement(connection);
                Assert.fail("Exception should thrown");
            } catch (TransactionException ex) {}

            Assert.assertTrue(dbTransactionSupport.markDeadCalled);
        }

        @Test
        public void Do_not_call_mark_dead_when_sql_exception_is_DB_down_Exception() throws Exception{
            doThrow(SQLException.class).when(connection).createStatement();
            try {
                dbTransactionSupport.statement(connection);
                Assert.fail("Exception should thrown");
            } catch (TransactionException ex) {}

            Assert.assertFalse(dbTransactionSupport.markDeadCalled);
        }

        @Test
        public void Throw_trnasactionException_when_sql_exception_generated() throws Exception {
            doThrow(SQLException.class).when(connection).createStatement();
            expectedException.expect(TransactionException.class);
            dbTransactionSupport.statement(connection);

        }
    }

    public class PreparedStatement {
        private String query = "Hello";

        @Test
        public void Throws_transactionException_when_connection_is_null() throws Exception {
            dbTransactionSupport = new DummyDBTransactionSupport();
            expectedException.expect(TransactionException.class);
            expectedException.expectMessage("Error in creating prepared statement. Reason: Connection is null");
            dbTransactionSupport.prepareStatement(null, query);
        }

        @Test
        public void Create_preparedStatement_from_connection() throws Exception {
            dbTransactionSupport.prepareStatement(connection, query);
            verify(connection).prepareStatement(query);
        }

        @Test
        public void Mark_dead_called_when_sql_exception_is_DB_down_Exception() throws Exception{
            dbTransactionSupport.setSQLExMarkDBDown();
            doThrow(SQLException.class).when(connection).prepareStatement(query);
            try {
                dbTransactionSupport.prepareStatement(connection, query);
            } catch (TransactionException ex) {}

            Assert.assertTrue(dbTransactionSupport.markDeadCalled);
        }

        @Test
        public void Do_not_call_mark_dead_when_sql_exception_is_DB_down_Exception() throws Exception{
            doThrow(SQLException.class).when(connection).prepareStatement(query);
            try {
                dbTransactionSupport.prepareStatement(connection, query);
            } catch (TransactionException ex) {}

            Assert.assertFalse(dbTransactionSupport.markDeadCalled);
        }

        @Test
        public void Throw_transactionException_when_sql_exception_generated() throws Exception {
            doThrow(SQLException.class).when(connection).prepareStatement(query);
            expectedException.expect(TransactionException.class);
            dbTransactionSupport.prepareStatement(connection, query);

        }
    }

    public class PrepareStatementWithGeneratedKeys {
        private String query = "Hello";

        @Test
        public void Throws_transactionException_when_connection_is_null() throws Exception {
            dbTransactionSupport = new DummyDBTransactionSupport();
            expectedException.expect(TransactionException.class);
            expectedException.expectMessage("Error in creating prepared statement. Reason: Connection is null");
            dbTransactionSupport.prepareStatement(null, query, new String[1]);
        }

        @Test
        public void Create_preparedStatement_from_connection() throws Exception {
            dbTransactionSupport.prepareStatement(connection, query, new String[1]);
            verify(connection).prepareStatement(query, new String[1]);
        }

        @Test
        public void Mark_dead_called_when_sql_exception_is_DB_down_Exception() throws Exception{
            dbTransactionSupport.setSQLExMarkDBDown();
            doThrow(SQLException.class).when(connection).prepareStatement(query, new String[1]);
            try {
                dbTransactionSupport.prepareStatement(connection, query, new String[1]);
            } catch (TransactionException ex) {}

            Assert.assertTrue(dbTransactionSupport.markDeadCalled);
        }

        @Test
        public void Do_not_call_mark_dead_when_sql_exception_is_DB_down_Exception() throws Exception{
            doThrow(SQLException.class).when(connection).prepareStatement(query, new String[1]);
            try {
                dbTransactionSupport.prepareStatement(connection, query, new String[1]);
            } catch (TransactionException ex) {}

            Assert.assertFalse(dbTransactionSupport.markDeadCalled);
        }

        @Test
        public void Throw_transaction_exception_when_sql_exception_generated() throws Exception {
            doThrow(SQLException.class).when(connection).prepareStatement(query, new String[1]);
            expectedException.expect(TransactionException.class);
            dbTransactionSupport.prepareStatement(connection, query, new String[1]);

        }
    }

    public class EndConnection {
        @Test
        public void Skip_to_end_connection_when_connection_object_is_null() throws Exception{
            dbTransactionSupport.endConnection(null);
            Assert.assertFalse(connection.isClosed());
        }

        @Test
        public void Mark_dead_when_sql_exception_is_db_down_exception() throws Exception{
            dbTransactionSupport.setSQLExMarkDBDown();
            doThrow(SQLException.class).when(connection).close();
            try {
                dbTransactionSupport.endConnection(connection);
            } catch (TransactionException ex) {}

            Assert.assertTrue(dbTransactionSupport.markDeadCalled);
        }

        @Test
        public void Do_not_call_mark_dead_when_sql_exception_is_db_down_exception() throws Exception{
            doThrow(SQLException.class).when(connection).setReadOnly(false);
            try {
                dbTransactionSupport.endConnection(connection);
            } catch (TransactionException ex) {}

            Assert.assertFalse(dbTransactionSupport.markDeadCalled);
        }

        @Test
        public void Throw_transaction_exception_when_sql_exception_generated() throws Exception {
            doThrow(SQLException.class).when(connection).close();
            expectedException.expect(TransactionException.class);
            dbTransactionSupport.endConnection(connection);

        }
    }


    private class DummyDBTransactionSupport extends DBTransactionSupport {
        private boolean markDeadCalled = false;
        private boolean markDBDown = false;

        public DummyDBTransactionSupport() {
        }

        @Override
        public void begin() throws TransactionException {
            throw new UnsupportedOperationException("Operation is not supported");
        }

        @Override
        public void end() throws TransactionException {
            throw new UnsupportedOperationException("Operation is not supported");
        }

        @Override
        public java.sql.PreparedStatement prepareStatement(String query) throws TransactionException {
            throw new UnsupportedOperationException("Operation is not supported");
        }

        @Override
        public java.sql.PreparedStatement prepareStatement(String query, String[] generatedKeys) throws TransactionException {
            throw new UnsupportedOperationException("Operation is not supported");
        }

        @Override
        public boolean isBegin() {
            throw new UnsupportedOperationException("Operation is not supported");
        }

        @Override
        public void setTimeout() {
            throw new UnsupportedOperationException("Operation is not supported");
        }

        @Override
        public void markDead() {
            markDeadCalled = true;
        }



        @Override
        public java.sql.Statement statement() throws TransactionException {
            throw new UnsupportedOperationException("Operation is unsupported");
        }

        @Override
        public boolean isDBDownSQLException(SQLException ex) {
            return markDBDown;
        }

        public void setSQLExMarkDBDown() {
            this.markDBDown = true;
        }
    }
}