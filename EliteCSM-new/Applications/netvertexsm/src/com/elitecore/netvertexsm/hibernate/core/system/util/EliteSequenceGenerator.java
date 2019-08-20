package com.elitecore.netvertexsm.hibernate.core.system.util;

import java.io.Serializable;
import java.util.Properties;

import org.hibernate.HibernateException;
import org.hibernate.MappingException;
import org.hibernate.dialect.Dialect;
import org.hibernate.engine.spi.SessionImplementor;
import org.hibernate.id.PersistentIdentifierGenerator;
import org.hibernate.id.SequenceIdentityGenerator;
import org.hibernate.type.Type;


public class EliteSequenceGenerator extends SequenceIdentityGenerator {
    
    
    private static final String seqPrefix = "SEQ_";
    
    public EliteSequenceGenerator(){
    	
    }
    
    @Override
    public void configure(Type type, Properties params, Dialect dialect) throws MappingException {
        
        String tableName = params.getProperty(PersistentIdentifierGenerator.TABLE);
        if(tableName != null) {
            String seqName = seqPrefix + tableName.replaceFirst("TBL","");
            params.setProperty(SEQUENCE, seqName);               
        }
        super.configure(type, params, dialect);
    }
    
    @Override
    public Serializable generate(SessionImplementor session, Object obj) throws HibernateException {
        return super.generate(session, obj);
    }
}