package com.elitecore.netvertexsm.hibernate.core.system.util;


import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.Transaction;

import com.elitecore.netvertexsm.datamanager.DataManagerException;
import com.elitecore.netvertexsm.datamanager.core.exceptions.constraintviolation.ConstraintViolationException;
import com.elitecore.netvertexsm.datamanager.core.system.util.IDataManagerSession;

public class HibernateDataSession implements IDataManagerSession {
    
    private Session session;
    private Transaction tx; 
    
    public HibernateDataSession () {
    		session = HibernateSessionFactory.createSession();
     }
    
    public void beginTransaction() throws DataManagerException {
      	try{
	    	if(session != null){
	    		tx = session.beginTransaction();
	    	}
    	}catch(HibernateException exp){
    		throw new DataManagerException(exp.getMessage(), exp);    		
    	}
    }

    public void commit() throws DataManagerException, ConstraintViolationException {
    	try{
	    	if(tx != null){
	    		tx.commit();
	    	}
    	}catch(org.hibernate.exception.ConstraintViolationException cve){
    		rollback();
    		throw new ConstraintViolationException(cve.getMessage(), cve);
    	}catch(HibernateException exp){
    		rollback();    		
    		throw new DataManagerException(exp.getMessage(), exp);
    	}
    }

    public void rollback() throws DataManagerException, ConstraintViolationException {
            try{    	
	    	if(tx != null){
	    		tx.rollback();
	    	}
		}catch(org.hibernate.exception.ConstraintViolationException cve){
		    throw new ConstraintViolationException(cve.getMessage(), cve);
		}catch(HibernateException exp){
		    throw new DataManagerException(exp.getMessage(), exp);
		}
    	
    }

    public void close() throws DataManagerException {
        try{
            if(session != null && session.isOpen()){
                HibernateSessionFactory.closeSession(session);
            }
        }catch(HibernateException exp){
            throw new DataManagerException(exp.getMessage(), exp);
        }
    }
    
    public final Session getSession(){
         return session;
    }
    
    @Override
    public String toString() {
    	return session.toString();
    }
}
