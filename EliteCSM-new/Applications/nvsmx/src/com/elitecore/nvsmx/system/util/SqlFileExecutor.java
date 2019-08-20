package com.elitecore.nvsmx.system.util;

import com.elitecore.commons.base.DBUtility;

import java.io.IOException;
import java.io.LineNumberReader;
import java.io.Reader;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import static com.elitecore.commons.logging.LogManager.getLogger;

/**
 * Used to execute the SQL File.
 * @author dhyani.raval
 */
public class SqlFileExecutor {

    public static final String MODULE = "SqlFileExecutor";
    public static final String DEFAULT_DELIMITER = ";";
    public static final String PL_SQL_BLOCK_END_DELIMITER = "/";
    public static final String POSTGRES_PL_SQL_BLOCK_END_DELIMITER = "$$;";

    private final boolean autoCommit;
    private final boolean stopOnError;
    private final Connection connection;
    private String delimiter = SqlFileExecutor.DEFAULT_DELIMITER;

	/* To Store any 'SELECT' queries output */
    private List<Table> tableList;
	
	/* To Store any SQL Queries output except 'SELECT' SQL */
    private List<String> sqlOutput;

    public SqlFileExecutor(final Connection connection, final boolean autoCommit, final boolean stopOnError) {
        if (connection == null) {
            throw new RuntimeException("SqlFileExecutor requires an SQL Connection");
        }
		
        this.connection = connection;
        this.autoCommit = autoCommit;
        this.stopOnError = stopOnError;

        tableList = new ArrayList();
        sqlOutput = new ArrayList();
    }

    public void runScript(final Reader reader) throws SQLException, IOException {
        final boolean originalAutoCommit = this.connection.getAutoCommit();
        try {
            if (originalAutoCommit != this.autoCommit) {
                this.connection.setAutoCommit(this.autoCommit);
            }
            this.runScript(this.connection, reader);
        } catch (Exception e) {
            this.connection.rollback();
            throw e;
        } finally {
            this.connection.setAutoCommit(originalAutoCommit);
        }
    }

    private void runScript(final Connection conn, final Reader reader) throws SQLException, IOException {
        StringBuilder command = null;

            final LineNumberReader lineReader = new LineNumberReader(reader);
            String line = null;
            while ((line = lineReader.readLine()) != null) {
                if (command == null) {
                    command = new StringBuilder();
                }

                String trimmedLine = line.trim();


                boolean isPlSqlBlock = command.toString().contains("BEGIN") || command.toString().contains("DECLARE") || command.toString().contains("CREATE OR REPLACE");

				// Interpret SQL Comment & Some statement that are not executable
                if (trimmedLine.startsWith("--")
                        || trimmedLine.startsWith("//")
                        || trimmedLine.startsWith("#")
                        || trimmedLine.toLowerCase().startsWith("rem inserting into")
                        || trimmedLine.toLowerCase().startsWith("set define off")) {

                    // do nothing...
                } else if ((trimmedLine.endsWith(this.delimiter) && isPlSqlBlock == false ) || trimmedLine.endsWith(PL_SQL_BLOCK_END_DELIMITER) || trimmedLine.endsWith(POSTGRES_PL_SQL_BLOCK_END_DELIMITER)) { // Line is end of statement
                    
                    // Append
                    if (trimmedLine.endsWith(this.delimiter)) {
                        command.append(line.substring(0, line.lastIndexOf(this.delimiter)));
                        command.append(" ");
                    } else if (trimmedLine.endsWith(PL_SQL_BLOCK_END_DELIMITER)) {
                        command.append(line.substring(0, line.lastIndexOf(PL_SQL_BLOCK_END_DELIMITER)));
                        command.append(" ");

                    }

                    Statement stmt = null;
                    ResultSet rs = null;
                    try {
                        stmt = conn.createStatement();
                        boolean hasResults = false;

                        if (this.stopOnError) {
                            hasResults = stmt.execute(command.toString());
                        } else {
                            stmt.execute(command.toString());
                        }

                        rs = stmt.getResultSet();
                        if (hasResults && rs != null) {

                            List<String> headerRow = new ArrayList();

                            // Print & Store result column names
                            final ResultSetMetaData md = rs.getMetaData();
                            final int cols = md.getColumnCount();
                            for (int i = 0; i < cols; i++) {
                                final String name = md.getColumnLabel(i + 1);


                                headerRow.add(name);
                            }

                            Table table = new Table();
                            table.setHeaderRow(headerRow);

                            // Print & Store result rows
                            List<List<String>> toupleList = new ArrayList();
                            List<String> touple = new ArrayList();
                            int index = 1;
                            while (rs.next() && index <= cols) {
                                touple.add(rs.getString(index));
                                index++;
                            }
                            toupleList.add(touple);
                            table.setToupleList(toupleList);
                            this.tableList.add(table);
                        } else {
                            sqlOutput.add(stmt.getUpdateCount() + " row(s) affected.");

                            if(getLogger().isDebugLogLevel()){
                                getLogger().debug(MODULE,command.toString());
                                getLogger().debug(MODULE,stmt.getUpdateCount() + " row(s) affected.");
                            }
                        }
                        command = null;
                    } catch (final SQLException e) {
                        getLogger().error(MODULE, "Error executing SQL Command: \"" + command + "\". Reason: "+e.getMessage() );
                        getLogger().trace(MODULE, e );
                        throw e;
                    } finally {
                        DBUtility.closeQuietly(rs);
                        DBUtility.closeQuietly(stmt);
                    }
                } else { // Line is middle of a statement

                    // Append
                    command.append(line);
                    command.append(" ");
                }
            }
            if (!this.autoCommit) {
                conn.commit();
            }
    }

    /**
     * @return the tableList
     */
    public List<Table> getTableList() {
        return tableList;
    }

    /**
     * @param tableList the tableList to set
     */
    public void setTableList(List<Table> tableList) {
        this.tableList = tableList;
    }

    /**
     * @return the sqlOutput
     */
    public List<String> getSqlOutput() {
        return sqlOutput;
    }

    /**
     * @param sqlOutput the sqlOutput to set
     */
    public void setSqlOutput(List<String> sqlOutput) {
        this.sqlOutput = sqlOutput;
    }
}
