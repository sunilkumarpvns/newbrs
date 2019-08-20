package com.elitecore.corenetvertex.sm.databasesetup;

import java.io.Serializable;

/**
 * Used to manage Database set up configuration from server manager
 * @author dhyani.raval
 */
public class DatabaseSetUp implements Serializable {

    private static final long serialVersionUID = 1L;
    private String url;
    private String userName;
    private String password;
    private Boolean createDatabaseSchema;
    private String dbAdmin;
    private String dbPassword;
    private String dbfFile;
    private StringBuilder sqlFileNames;

    public DatabaseSetUp () {
        sqlFileNames = new StringBuilder();
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public Boolean getCreateDatabaseSchema() {
        return createDatabaseSchema;
    }

    public void setCreateDatabaseSchema(Boolean createDatabaseSchema) {
        this.createDatabaseSchema = createDatabaseSchema;
    }

    public String getDbAdmin() {
        return dbAdmin;
    }

    public void setDbAdmin(String dbAdmin) {
        this.dbAdmin = dbAdmin;
    }

    public String getDbPassword() {
        return dbPassword;
    }

    public void setDbPassword(String dbPassword) {
        this.dbPassword = dbPassword;
    }

    public String getDbfFile() {
        return dbfFile;
    }

    public void setDbfFile(String dbfFile) {
        this.dbfFile = dbfFile;
    }

    public StringBuilder getSqlFileNames() {
        return sqlFileNames;
    }

    public void setSqlFileNames(StringBuilder sqlFileNames) {
        this.sqlFileNames = sqlFileNames;
    }
}
