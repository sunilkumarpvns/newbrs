package com.elitecore.core.commons.utilx.db;

import com.elitecore.core.commons.util.db.DataSourceException;
import de.bechte.junit.runners.context.HierarchicalContextRunner;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.unitils.reflectionassert.ReflectionAssert;

import javax.annotation.Nullable;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;
import static org.mockito.Matchers.anyBoolean;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.verify;

@RunWith(HierarchicalContextRunner.class)
public class ReadOnlyTransactionTest {
    private DummyReadOnlyTransaction readOnlyTransaction;
    @Mock private Connection connection;

    @Rule
    public ExpectedException expectedException = ExpectedException.none();

    @Before
    public void setup() {
        MockitoAnnotations.initMocks(this);
        readOnlyTransaction = new DummyReadOnlyTransaction(connection);

    }


    public class BeginTransaction {

        @Before
        public void setup() throws TransactionException {
        }

        @Test
        public void TransactionException_is_thrown_when_connection_is_null() throws Exception{
            readOnlyTransaction = new DummyReadOnlyTransaction(null);
            expectedException.expect(TransactionException.class);
            expectedException.expectMessage("Error in getting connection");
            try {
                readOnlyTransaction.begin();
            } catch (TransactionException ex) {
                Assert.assertEquals(TransactionErrorCode.CONNECTION_NOT_FOUND, ex.getErrorCode());
                throw ex;
            }
        }

        @Test
        public void Mark_Connection_as_read_only() throws Exception {
            readOnlyTransaction.begin();
            verify(connection).setReadOnly(true);
        }

        @Test
        public void Set_auto_commit_false() throws Exception {
            readOnlyTransaction.begin();
            verify(connection).setAutoCommit(false);
        }

        @Test
        public void Mark_dead_called_when_sql_exception_is_DB_down_Exception() throws Exception{
            readOnlyTransaction.setSQLExMarkDBDown();
            doThrow(SQLException.class).when(connection).setAutoCommit(anyBoolean());
            try {
                readOnlyTransaction.begin();
                fail("Exception should be thrown");
            } catch (TransactionException ex) {}

            Assert.assertTrue(readOnlyTransaction.markDeadCalled);


        }

        @Test
        public void Do_not_call_mark_dead_when_sql_exception_is_not_DB_down_Exception() throws Exception{
            doThrow(SQLException.class).when(connection).setAutoCommit(anyBoolean());
            try {
                readOnlyTransaction.begin();
                fail("Exception should thrown");
            } catch (TransactionException ex) {}

            Assert.assertFalse(readOnlyTransaction.markDeadCalled);

        }

        @Test
        public void TransactionException_is_thrown_when_sql_exception_generated() throws Exception {
            doThrow(SQLException.class).when(connection).setAutoCommit(anyBoolean());
            expectedException.expect(TransactionException.class);
            readOnlyTransaction.begin();


        }

        @Test
        public void Does_not_perform_any_operation_when_called_more_than_once() throws Exception{
            readOnlyTransaction.begin();
            doThrow(SQLException.class).when(connection).setAutoCommit(anyBoolean());
            readOnlyTransaction.begin();

        }

    }

    public class EndTransaction {



        @Test
        public void Throw_transaction_exception_when_called_before_begin() throws Exception{
            expectedException.expect(TransactionException.class);
            expectedException.expectMessage("Transaction end called before begin");
            readOnlyTransaction.end();
        }

        @Test
        public void Call_MarkDead_when_sql_exception_is_db_down_exception() throws Exception{
            readOnlyTransaction.setSQLExMarkDBDown();
            doThrow(SQLException.class).when(connection).setReadOnly(false);
            readOnlyTransaction.begin();
            try {
                readOnlyTransaction.end();
                fail("Exception should thrown");
            } catch (TransactionException ex) {}

            Assert.assertTrue(readOnlyTransaction.markDeadCalled);
        }

        @Test
        public void Does_not_call_mark_dead_when_sql_exception_is_db_down_exception() throws Exception{
            doThrow(SQLException.class).when(connection).setReadOnly(false);
            readOnlyTransaction.begin();
            try {
                readOnlyTransaction.end();
                fail("Exception should thrown");
            } catch (TransactionException ex) {}

            Assert.assertFalse(readOnlyTransaction.markDeadCalled);
        }

        @Test
        public void Reset_connection_read_only_flag() throws Exception{
            readOnlyTransaction.begin();
            readOnlyTransaction.end();
            verify(connection).setReadOnly(false);
        }

        @Test
        public void TransactionException_is_thrown_when_sql_exception_is_thown_while_resetting_read_only_flag() throws Exception {
            doThrow(SQLException.class).when(connection).setReadOnly(anyBoolean());
            expectedException.expect(TransactionException.class);
            readOnlyTransaction.end();
        }

        @Test
        public void skip_to_close_connection_when_connection_object_is_null() throws Exception{
            readOnlyTransaction = new DummyReadOnlyTransaction(null);
            try {
                readOnlyTransaction.begin();
                connection.setReadOnly(true);
            } catch (TransactionException ex) {}
            readOnlyTransaction.end();
        }
    }

    @Test
    public void test_preparestatement_with_GeneratedKeys_call_parent_preparestatement() throws Exception {
        String[] generatedKeys = new String[1];
        String query = "anyQuery";
        readOnlyTransaction.begin();
        readOnlyTransaction.prepareStatement(query, generatedKeys);

        readOnlyTransaction.checkPrepareStatemetntCalledWithArgs(Arrays.asList(connection, query, generatedKeys));
    }

    @Test
    public void test_preparestatement_call_parentPrepareStatement() throws Exception {
        String query = "anyQuery";
        readOnlyTransaction.begin();
        readOnlyTransaction.prepareStatement(query);
        readOnlyTransaction.checkPrepareStatemetntCalledWithArgsOnlyQuery(Arrays.asList(connection, query));
    }

    @Test
    public void test_statement_call_parent_statement() throws Exception {
        readOnlyTransaction.begin();
        readOnlyTransaction.statement();
        readOnlyTransaction.checkStatementCalled();
    }

    private class DummyReadOnlyTransaction extends ReadOnlyTransaction {

        private boolean markDeadCalled = false;
        private boolean markDBDown = false;
        private Connection connection;
        private boolean prepareStatementCalled;
        private boolean statementCalled;
        private List<Object> prepareStatementArguments;

        public DummyReadOnlyTransaction(Connection connection) {
            this.connection = connection;
        }

        @Override
        protected Connection getConnection() throws DataSourceException {
            return connection;
        }

        @Override
        public void markDead() {
            markDeadCalled = true;
        }

        @Override
        public boolean isDBDownSQLException(SQLException ex) {
            return markDBDown;
        }

        public void setSQLExMarkDBDown() {
            this.markDBDown = true;
        }

        @Override
        protected PreparedStatement prepareStatement(@Nullable Connection connection, String query, String[] generatedKeys) throws TransactionException {
            prepareStatementCalled = true;
            prepareStatementArguments = new ArrayList<Object>();
            prepareStatementArguments.add(connection);
            prepareStatementArguments.add(query);
            prepareStatementArguments.add(generatedKeys);
            return super.prepareStatement(connection, query, generatedKeys);
        }

        public void checkPrepareStatemetntCalledWithArgs(List<Object> arguments) {
            assertTrue(prepareStatementCalled);
            ReflectionAssert.assertLenientEquals(arguments, prepareStatementArguments);
        }

        @Override
        protected PreparedStatement prepareStatement(@Nullable Connection connection, String query) throws TransactionException {
            prepareStatementCalled = true;
            prepareStatementArguments = new ArrayList<Object>();
            prepareStatementArguments.add(connection);
            prepareStatementArguments.add(query);
            return super.prepareStatement(connection, query);
        }

        public void checkPrepareStatemetntCalledWithArgsOnlyQuery(List<Object> arguments) {
            assertTrue(prepareStatementCalled);
            ReflectionAssert.assertLenientEquals(arguments, prepareStatementArguments);

        }

        @Override
        protected Statement statement(@Nullable Connection connection) throws TransactionException {
            statementCalled = true;
            return super.statement(connection);
        }

        public void checkStatementCalled() {
            assertTrue(statementCalled);
        }


    }
}
