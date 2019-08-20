package com.elitecore.core.commons.utilx.db;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Statement;

public interface DBTransaction {
    void begin() throws TransactionException;
    void end() throws TransactionException;
    PreparedStatement prepareStatement(String query) throws TransactionException;
    PreparedStatement prepareStatement(String query, String[] generatedKeys)throws TransactionException;
    boolean isBegin();
    void setTimeout();
    void markDead();
    Statement statement() throws TransactionException;
    boolean isDBDownSQLException(SQLException ex);
}
