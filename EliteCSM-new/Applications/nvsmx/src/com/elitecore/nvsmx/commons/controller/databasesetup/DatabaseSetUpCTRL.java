package com.elitecore.nvsmx.commons.controller.databasesetup;

import com.elitecore.commons.base.DBUtility;
import com.elitecore.commons.io.Files;
import com.elitecore.core.commons.util.db.DBVendors;
import com.elitecore.corenetvertex.annotation.ActionChain;
import com.elitecore.corenetvertex.sm.databasesetup.DatabaseSetUp;
import com.elitecore.nvsmx.system.constants.InputConfigConstants;
import com.elitecore.nvsmx.system.constants.NVSMXCommonConstants;
import com.elitecore.nvsmx.system.constants.Results;
import com.elitecore.nvsmx.system.db.NVSMXDBConnectionManager;
import com.elitecore.nvsmx.system.listeners.DefaultNVSMXContext;
import com.elitecore.nvsmx.system.util.DataInitializer;
import com.elitecore.nvsmx.system.util.PasswordUtility;
import com.elitecore.nvsmx.system.util.SqlFileExecutor;
import com.opensymphony.xwork2.ActionSupport;
import com.opensymphony.xwork2.ModelDriven;
import com.opensymphony.xwork2.Validateable;
import com.opensymphony.xwork2.interceptor.annotations.InputConfig;
import org.apache.struts2.interceptor.ServletRequestAware;
import org.apache.struts2.interceptor.validation.SkipValidation;

import javax.servlet.http.HttpServletRequest;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.StringReader;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.Properties;

import static com.elitecore.commons.logging.LogManager.getLogger;

/**
 * Used to set up the database with schema, sql files and properties file
 * @author dhyani.raval
 */
public class DatabaseSetUpCTRL extends ActionSupport implements ModelDriven<DatabaseSetUp>, Validateable, ServletRequestAware{

    private static final String MODULE = "DatabaseSetUpCTRL";

    private static final String ORACLE_FILE_LOCATION = DefaultNVSMXContext.getContext().getServerHome()+"setup/database/fullsetup/";
    private static final String POSTGRES_FILE_LOCATION = DefaultNVSMXContext.getContext().getServerHome()+"setup/database/postgres/";

    private static final String ORACLE_GRANTED_ROLE_QUERY = "select * from dba_role_privs where granted_role='DBA'";
    private static final String POSTGRES_GRANTED_ROLE_QUERY = "select current_user";

    private static final String ORACLE_ALL_TABLE_EXIST_QUERY = "select * from tab";
    private static final String POSTGRES_ALL_TABLE_EXIST_QUERY = "select * from information_schema.tables where table_schema=";
    private static final String PG_AGENT_EXIST_QUERY = "select exists (select * from pg_catalog.pg_namespace where nspname = 'pgagent')";

    private static final String ORACLE = "oracle";
    private static final String POSTGRESQL = "postgresql";
    private static final String SUCCESS_SETUP ="ServerManagerStartup";
    private static final String DATABASE_SETUP = "DatabaseSetUp";
    private static final String IS_SQL_EXECUTED = "isSqlExecuted";

    private DatabaseSetUp databaseSetUp = new DatabaseSetUp();
    private String actionChainUrl;
    private HttpServletRequest request;

    @Override
    public DatabaseSetUp getModel() {
        return databaseSetUp;
    }

    public void setModel(DatabaseSetUp databaseSetUp) {
        this.databaseSetUp = databaseSetUp;
    }

    @SkipValidation
    public String initConfigureDatabaseSetUp() {
        if(getLogger().isDebugLogLevel()){
            getLogger().debug(MODULE, "Method called initConfigureDatabaseSetUp()");
        }
        setActionChainUrl(DATABASE_SETUP);//HERE ACTION CHAIN URL METHOD IS USED TO IDENTIFY JSP
        addActionError(getText("database.configuration.invalid"));
        return Results.DBSETUP.getValue();
    }

    @InputConfig(resultName = InputConfigConstants.DBSETUP)
    public String configureDatabaseSetUp() {

        if(getLogger().isDebugLogLevel()){
            getLogger().debug(MODULE, "Method called configureDatabaseSetUp()");
        }

        Statement statement = null;
        DatabaseSetUp databaseSetUp = getModel();
        String connectionUrl = databaseSetUp.getUrl();
        String userName = databaseSetUp.getUserName();
        String password = databaseSetUp.getPassword();
        String driver ;
        try {

            File file = new File(DefaultNVSMXContext.getContext().getServerHome() + File.separator + NVSMXCommonConstants.DATABASE_CONFIG_FILE_LOCATION);
            driver = DBVendors.fromUrl(connectionUrl).driverClassName;

            if(databaseSetUp.getCreateDatabaseSchema()) {

                String dbAdmin = databaseSetUp.getDbAdmin();
                String dbPassword = databaseSetUp.getDbPassword();
                String dbfFile = databaseSetUp.getDbfFile();


                //Login to Database Administrator
                try (Connection connection = NVSMXDBConnectionManager.getInstance().getDBConnection(connectionUrl, dbAdmin, dbPassword, driver)) {
                    statement = connection.createStatement();

                    // check whether entered db admin user has administrator permission for creating new user or not
                    statement.executeQuery(getGrantQueryFromConnectionURL(connectionUrl));

                    File schemaFile = getFileLocation(connectionUrl, NVSMXCommonConstants.SCHEMA_FILE_NAME);
                    byte[] schemaFileContent = Files.readFully(schemaFile);

                    String schemaSqlFilesStatement = new String(schemaFileContent, "UTF-8");
                    schemaSqlFilesStatement = replaceSchemaStatement(schemaSqlFilesStatement, userName, password, dbfFile, connectionUrl);

                    SqlFileExecutor sqlFileExecutor = new SqlFileExecutor(connection, true, true);
                    if (connectionUrl.contains(POSTGRESQL)) {
                        schemaSqlFilesStatement =  executePgAgent(connectionUrl, statement, schemaSqlFilesStatement);
                    }
                    sqlFileExecutor.runScript(new StringReader(schemaSqlFilesStatement));

                    if (getLogger().isInfoLogLevel()) {
                        getLogger().info(MODULE, schemaFile.getName() + " executed successfully");
                    }
                }

                //LOGIN WITH CREATES USER AND PASSWORD
                try(Connection connection = NVSMXDBConnectionManager.getInstance().getDBConnection(connectionUrl,userName,password, driver)) {

                    //EXECUTE SQL FILES
                    executeSqlFile(getFileLocation(connectionUrl, NVSMXCommonConstants.NETVERTEX_SQL_FILE_NAME),connection);
                    databaseSetUp.getSqlFileNames().append(NVSMXCommonConstants.NETVERTEX_SQL_FILE_NAME).append(",");
                    executeSqlFile(getFileLocation(connectionUrl, NVSMXCommonConstants.NETWORK_INFORMATION_SQL_FILE_NAME),connection);
                    databaseSetUp.getSqlFileNames().append(NVSMXCommonConstants.NETWORK_INFORMATION_SQL_FILE_NAME);

                    //UPDATE DATABASE.PROPERTIES WITH NEWLY CREATED USERNAME, PASSWORD, AND URL
                    updateProperties(file,connectionUrl,userName,password,driver);
                    addActionMessage("Database setup is completed");
                    setActionChainUrl(SUCCESS_SETUP); //HERE ACTION CHAIN URL METHOD IS USED TO IDENTIFY JSP
                    request.setAttribute(IS_SQL_EXECUTED,true);
                }

            } else {

                ResultSet resultSet = null;
                //LOGIN TO CREATED USER
                try(Connection connection =  NVSMXDBConnectionManager.getInstance().getDBConnection(connectionUrl, userName, password, driver)) {
                    statement = connection.createStatement();

                    //CHECK WHETHER SQL FILES ARE EXECUTED OR NOT
                    resultSet = statement.executeQuery(getTableExistQueryFromConnectionURL(connectionUrl,userName));

                    if(resultSet.next() == false) {
                        executeSqlFile(getFileLocation(connectionUrl, NVSMXCommonConstants.NETVERTEX_SQL_FILE_NAME), connection);
                        databaseSetUp.getSqlFileNames().append(NVSMXCommonConstants.NETVERTEX_SQL_FILE_NAME).append(",");
                        executeSqlFile(getFileLocation(connectionUrl, NVSMXCommonConstants.NETWORK_INFORMATION_SQL_FILE_NAME), connection);
                        databaseSetUp.getSqlFileNames().append(NVSMXCommonConstants.NETWORK_INFORMATION_SQL_FILE_NAME);
                        request.setAttribute(IS_SQL_EXECUTED,true);
                    } else {
                        request.setAttribute(IS_SQL_EXECUTED,false);
                    }

                    //UPDATE DATABASE.PROPERTIES WITH CONFIGURED CREATED USERNAME, PASSWORD, AND URL
                    updateProperties(file,connectionUrl,userName,password,driver);

                    addActionMessage("Database Configured Successfully");
                    setActionChainUrl(SUCCESS_SETUP);

                } finally {
                    DBUtility.closeQuietly(resultSet);
                }

            }
        } catch (Exception e) {
            getLogger().error(MODULE,"Error while configuring database. Reason: "+e.getMessage());
            getLogger().trace(MODULE,e);
            addActionError(e.getMessage());
            setActionChainUrl(DATABASE_SETUP); //HERE ACTION CHAIN URL METHOD IS USED TO IDENTIFY JSP
        } finally {
            DBUtility.closeQuietly(statement);
        }
        return Results.DBSETUP.getValue();
    }

    /**
     * Check whether Pg Agent exist or not.
     * if not exist then append pg agent file statements to schema statements otherwise only schema statement
     * @param connectionUrl
     * @param statement
     * @param schemaSqlFilesStatement
     * @return String
     * @throws Exception
     */
    private String executePgAgent(String connectionUrl, Statement statement, String schemaSqlFilesStatement) throws Exception {

        //CHECK WHETHER PG AGENT EXIST OR NOT
        try(ResultSet resultSet = statement.executeQuery(PG_AGENT_EXIST_QUERY)) {
                while (resultSet.next()) {

                    String result = resultSet.getString("exists");
                    if("f".equalsIgnoreCase(result)){
                        File pgAgentSchemaFile = getFileLocation(connectionUrl, NVSMXCommonConstants.PGAGENT_SQL_FILE_NAME);
                        byte[] schemaFileContentPgAgent = Files.readFully(pgAgentSchemaFile);
                        String pgAgentSchemaFileStatement = new String(schemaFileContentPgAgent,"UTF-8");

                        //APPENDING PG AGENT FILE STATEMENTS TO SCHEMA FILE STATEMENTS. APPENDING BEFORE BECAUSE IT SHOULD BE EXECUTE BEFORE THE SCHEMA
                        schemaSqlFilesStatement = pgAgentSchemaFileStatement.concat(schemaSqlFilesStatement);
                    }
                }
        }
        return  schemaSqlFilesStatement;
    }

    /**
     * Used to fetch granted query for defined database
     * Granted role query is used to check , user has administrator rights or not
     * @param connectionURL
     * @return String
     */
    private String getGrantQueryFromConnectionURL(String connectionURL) {
        String query = "";
        if (connectionURL.contains(ORACLE)) {
            query = ORACLE_GRANTED_ROLE_QUERY;
        } else if (connectionURL.contains(POSTGRESQL)) {
            query = POSTGRES_GRANTED_ROLE_QUERY;
        }
        return query;
    }

    /**
     * Used to fetch granted query for defined database
     * Table exist query is used to check SQL file is executed or not
     * @param connectionURL
     * @return
     */
    private String getTableExistQueryFromConnectionURL(String connectionURL, String userName) {
        String query = "";
        if (connectionURL.contains(ORACLE)) {
            query = ORACLE_ALL_TABLE_EXIST_QUERY;
        } else if (connectionURL.contains(POSTGRESQL)) {
            query = POSTGRES_ALL_TABLE_EXIST_QUERY + "'"+userName+"';";
        }
        return query;
    }

    /**
     * Used to fetch SQL file based of defined database
     * @param connectionURL
     * @param fileName
     * @return File
     * @throws Exception
     */
    private File getFileLocation(String connectionURL, String fileName) throws Exception {
        if (connectionURL.contains(ORACLE)) {
            boolean exists = new File(ORACLE_FILE_LOCATION, fileName).exists();
            if(exists) {
                return new File(ORACLE_FILE_LOCATION + fileName);
            }
        } else if (connectionURL.contains(POSTGRESQL)) {
            boolean exists = new File(POSTGRES_FILE_LOCATION, fileName).exists();
            if(exists) {
                return new File(POSTGRES_FILE_LOCATION + fileName);
            }
        }
        throw new Exception(fileName + " does not exist");
    }

    private void executeSqlFile(File file, Connection connection) throws Exception {

        try {

            if(getLogger().isDebugLogLevel()) {
                getLogger().debug(MODULE, "Executing "+file.getName()+" file" );
            }

            SqlFileExecutor sqlFileExecutor = new SqlFileExecutor(connection,true,true);
            sqlFileExecutor.runScript(new BufferedReader(new FileReader(file)));

            if(getLogger().isInfoLogLevel()) {
                getLogger().info(MODULE,file.getName()+" executed successfully");
            }


        } catch (Exception e) {
            getLogger().error(MODULE, "Error executing SQL file " + file.getName() + ". Reason: " + e.getMessage());
            getLogger().trace(MODULE, e);
            throw e;
        }
    }

    /**
     * Used to update database properties
     * @param file
     * @param connectionUrl
     * @param userName
     * @param password
     * @param driver
     * @throws Exception
     */
    private void updateProperties(File file, String connectionUrl, String userName, String password, String driver) throws Exception {
        if(getLogger().isDebugLogLevel()) {
            getLogger().debug(MODULE,"Updating "+file.getName());

        }
        Properties databaseProperties = new Properties();
        databaseProperties.load(new FileInputStream(file));
        databaseProperties.put("driverClassName",driver);
        databaseProperties.put("url",connectionUrl);
        databaseProperties.put("username",userName);
        databaseProperties.put("password", PasswordUtility.getEncryptedPassword(password));
        databaseProperties.put("encryptedPassword", "true");
        databaseProperties.store(new FileOutputStream(file), "");
        if(getLogger().isInfoLogLevel()) {
            getLogger().info(MODULE,file.getName() + " updated successfully");

        }

        DataInitializer.getInstance().init();
    }

    /**
     * Used to modify the schema file based on the provided parameters
     * @param schemaSqlFilesStatement
     * @param dbUsername
     * @param dbUserPassword
     * @param dbfFileLocation
     * @param connectionUrl
     * @return
     */
    private String replaceSchemaStatement(String schemaSqlFilesStatement, String dbUsername, String dbUserPassword, String dbfFileLocation, String connectionUrl) {

        if( connectionUrl.contains(ORACLE)){

            schemaSqlFilesStatement = schemaSqlFilesStatement.replaceAll("(?i)netvertex", dbUsername);
            schemaSqlFilesStatement = schemaSqlFilesStatement.replaceFirst("IDENTIFIED BY "+dbUsername,"IDENTIFIED BY "+dbUserPassword);
            schemaSqlFilesStatement = replaceDBFFileLocation(schemaSqlFilesStatement, dbfFileLocation, "DATAFILE '" , "' size 100M AUTOEXTEND ON;");

        }else if(connectionUrl.contains(POSTGRESQL)){

            schemaSqlFilesStatement = schemaSqlFilesStatement.replaceAll("(?i)netvertex", dbUsername);
            schemaSqlFilesStatement = schemaSqlFilesStatement.replaceFirst("LOGIN PASSWORD '"+dbUsername+"'", "LOGIN PASSWORD '"+dbUserPassword+"'");
            schemaSqlFilesStatement = replaceDBFFileLocation(schemaSqlFilesStatement, dbfFileLocation, "LOCATION '" , "';");
            schemaSqlFilesStatement = replaceDBFFileLocation(schemaSqlFilesStatement, dbfFileLocation, "-p ", "/';");
        }

        return schemaSqlFilesStatement;
    }

    /**
     * Used to modify the DBF file with provided parameters
     * @param schemaSqlFilesStatement
     * @param dbfFileLocation
     * @param firstDelimeter
     * @param lastDelimeter
     * @return
     */
    private String replaceDBFFileLocation(String schemaSqlFilesStatement, String dbfFileLocation, String firstDelimeter, String lastDelimeter) {
        int p1 = schemaSqlFilesStatement.indexOf(firstDelimeter);
        int p2 = schemaSqlFilesStatement.indexOf(lastDelimeter, p1);
        String replacement = dbfFileLocation;
        if (p1 >= 0 && p2 > p1) {
            return schemaSqlFilesStatement.substring(0, p1 + firstDelimeter.length()) + replacement + schemaSqlFilesStatement.substring(p2);
        }
        return schemaSqlFilesStatement;
    }


    public String getActionChainUrl() {
        return actionChainUrl;
    }

    @ActionChain(name = "actionChainUrlMethod")
    public void setActionChainUrl(String actionChainUrl) {
        this.actionChainUrl = actionChainUrl;
    }

    @Override
    public void setServletRequest(HttpServletRequest request) {
        this.request = request;
    }
}
