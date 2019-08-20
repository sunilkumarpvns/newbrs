/**                                                     
 * Copyright (C) Elitecore Technologies Ltd.            
 * FileName   HibernetToDBTranslaterFacatory.java                            
 * ModualName com.elitecore.netvertexsm.datamanager.core.exceptions                                    
 * Created on Jan 31, 2008
 * Last Modified on                                     
 * @author :  himanshudobaria
 */
package com.elitecore.netvertexsm.hibernate.core.exception;

import org.hibernate.DuplicateMappingException;
import org.hibernate.HibernateException;
import org.hibernate.JDBCException;
import org.hibernate.MappingException;
import org.hibernate.NonUniqueObjectException;
import org.hibernate.NonUniqueResultException;
import org.hibernate.PropertyNotFoundException;
import org.hibernate.QueryException;
import org.hibernate.StaleStateException;
import org.hibernate.TransactionException;
import org.hibernate.exception.ConstraintViolationException;
import org.hibernate.exception.GenericJDBCException;
import org.hibernate.exception.JDBCConnectionException;
import org.hibernate.exception.LockAcquisitionException;
import org.hibernate.exception.SQLGrammarException;
import org.hibernate.id.IdentifierGenerationException;

import com.elitecore.netvertexsm.datamanager.core.exceptions.IExceptionTranslater;
import com.elitecore.netvertexsm.datamanager.core.exceptions.datasource.BaseDatasourceException;
import com.elitecore.netvertexsm.datamanager.core.exceptions.datasource.DatasourceConnectionException;
import com.elitecore.netvertexsm.datamanager.core.exceptions.datasource.DatasourceConstraintViolationException;
import com.elitecore.netvertexsm.datamanager.core.exceptions.datasource.DatasourceDuplicateMappingException;
import com.elitecore.netvertexsm.datamanager.core.exceptions.datasource.DatasourceException;
import com.elitecore.netvertexsm.datamanager.core.exceptions.datasource.DatasourceGenericException;
import com.elitecore.netvertexsm.datamanager.core.exceptions.datasource.DatasourceIdentifierGenerationException;
import com.elitecore.netvertexsm.datamanager.core.exceptions.datasource.DatasourceLockException;
import com.elitecore.netvertexsm.datamanager.core.exceptions.datasource.DatasourceMappingException;
import com.elitecore.netvertexsm.datamanager.core.exceptions.datasource.DatasourceMultipleObjectWithSameIdentifier;
import com.elitecore.netvertexsm.datamanager.core.exceptions.datasource.DatasourceUncategorizedException;
import com.elitecore.netvertexsm.datamanager.core.exceptions.datasource.DatasourceNonUniqueResultException;
import com.elitecore.netvertexsm.datamanager.core.exceptions.datasource.DatasourcePropertyNotFoundException;
import com.elitecore.netvertexsm.datamanager.core.exceptions.datasource.DatasourceQueryException;
import com.elitecore.netvertexsm.datamanager.core.exceptions.datasource.DatasourceSQLGrammarException;
import com.elitecore.netvertexsm.datamanager.core.exceptions.datasource.DatasourceStaleStateException;
import com.elitecore.netvertexsm.datamanager.core.exceptions.datasource.DatasourceTransactionException;

/**
 * @author himanshudobaria
 * 
 */
public class Hibernate2DatasourceTranslaterFactory implements IExceptionTranslater {
    
    private static Hibernate2DatasourceTranslaterFactory hibernate2DatasourceTranslaterFactory = new Hibernate2DatasourceTranslaterFactory();
    
    private Hibernate2DatasourceTranslaterFactory() {}
    
    public static Hibernate2DatasourceTranslaterFactory getInstance( ) {
        return hibernate2DatasourceTranslaterFactory;
    }
    
    public BaseDatasourceException translateToDatasourceException( HibernateException e ) {
        
        // Thrown by IdentifierGenerator implementation class when ID generation fails.
        if (e instanceof IdentifierGenerationException)
            return new DatasourceIdentifierGenerationException(e.getMessage(), e);
        
        // Indicates that a transaction could not be begun, committed or rolled back.
        if (e instanceof TransactionException)
            return new DatasourceTransactionException(e.getMessage(), e);
        
        // Thrown when the query returned more than one result.
        if (e instanceof NonUniqueResultException)
            return new DatasourceNonUniqueResultException(e.getMessage(), e);
        
        // This occurs if the user tries to associate two different objects with a particular identifier.
        if (e instanceof NonUniqueObjectException)
            return new DatasourceMultipleObjectWithSameIdentifier(e.getMessage(), e);
        
        // JDBC Exception Starts here 
        if (e instanceof SQLGrammarException)
            return new DatasourceSQLGrammarException(e.getMessage(), e);
        
        if (e instanceof ConstraintViolationException)
            return new DatasourceConstraintViolationException(e.getMessage(), e);
        
        if (e instanceof JDBCConnectionException)
            return new DatasourceConnectionException(e.getMessage(), e);
        
        if (e instanceof GenericJDBCException)
            return new DatasourceGenericException(e.getMessage(), e);
        
        if (e instanceof LockAcquisitionException)
            return new DatasourceLockException(e.getMessage(), e);
        
        if (e instanceof JDBCException)
            return new DatasourceException(e.getMessage(), e);
        // Ends here
        
        //Hibernate Mapping Exception Starts Here
        if (e instanceof DuplicateMappingException)
            return new DatasourceDuplicateMappingException(e.getMessage(), e);
        
        if (e instanceof PropertyNotFoundException)
            return new DatasourcePropertyNotFoundException(e.getMessage(), e);
        
        if (e instanceof MappingException)
            return new DatasourceMappingException(e.getMessage(), e);
        // Ends Here
        
        // A problem occurred translating a Hibernate query to SQL due to invalid query syntax, etc
        if (e instanceof QueryException)
            return new DatasourceQueryException(e.getMessage(), e);
        
        // Thrown when a version number or timestamp check failed.
        if (e instanceof StaleStateException)
            return new DatasourceStaleStateException(e.getMessage(), e);
        
        return new DatasourceUncategorizedException(e.getMessage(), e);
    }
    
    public BaseDatasourceException translateToDatasourceExceptionWithSource( HibernateException e, String source) {
        
        // Thrown by IdentifierGenerator implementation class when ID generation fails.
        if (e instanceof IdentifierGenerationException)
            return new DatasourceIdentifierGenerationException(e.getMessage(), source, e);
        
        // Indicates that a transaction could not be begun, committed or rolled back.
        if (e instanceof TransactionException)
            return new DatasourceTransactionException(e.getMessage(), source, e);
        
        // Thrown when the query returned more than one result.
        if (e instanceof NonUniqueResultException)
            return new DatasourceNonUniqueResultException(e.getMessage(), source, e);
        
        // This occurs if the user tries to associate two different objects with a particular identifier.
        if (e instanceof NonUniqueObjectException)
            return new DatasourceMultipleObjectWithSameIdentifier(e.getMessage(), source, e);
        
        // JDBC Exception Starts here 
        if (e instanceof SQLGrammarException)
            return new DatasourceSQLGrammarException(e.getMessage(), source, e);
        
        if (e instanceof ConstraintViolationException)
            return new DatasourceConstraintViolationException(e.getMessage(), source, e);
        
        if (e instanceof JDBCConnectionException)
            return new DatasourceConnectionException(e.getMessage(), source, e);
        
        if (e instanceof GenericJDBCException)
            return new DatasourceGenericException(e.getMessage(), source, e);
        
        if (e instanceof LockAcquisitionException)
            return new DatasourceLockException(e.getMessage(), source, e);
        
        if (e instanceof JDBCException)
            return new DatasourceException(e.getMessage(), source, e);
        // Ends here
        
        //Hibernate Mapping Exception Starts Here
        if (e instanceof DuplicateMappingException)
            return new DatasourceDuplicateMappingException(e.getMessage(), source, e);
        
        if (e instanceof PropertyNotFoundException)
            return new DatasourcePropertyNotFoundException(e.getMessage(), source, e);
        
        if (e instanceof MappingException)
            return new DatasourceMappingException(e.getMessage(), source, e);
        // Ends Here
        
        // A problem occurred translating a Hibernate query to SQL due to invalid query syntax, etc
        if (e instanceof QueryException)
            return new DatasourceQueryException(e.getMessage(), source, e);
        
        // Thrown when a version number or timestamp check failed.
        if (e instanceof StaleStateException)
            return new DatasourceStaleStateException(e.getMessage(), source, e);
        
        return new DatasourceUncategorizedException(e.getMessage(), source, e);
    }
    
}
