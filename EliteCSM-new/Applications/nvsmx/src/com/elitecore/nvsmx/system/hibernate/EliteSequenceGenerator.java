package com.elitecore.nvsmx.system.hibernate;

import java.io.Serializable;
import java.util.UUID;

import org.hibernate.HibernateException;
import org.hibernate.engine.spi.SessionImplementor;
import org.hibernate.id.IdentifierGenerator;

import com.elitecore.commons.logging.LogManager;

/**
 * Unique Id Generator for whole nvsmx
 * 
 * @author aditya.shrivastava
 *
 */
public class EliteSequenceGenerator implements  IdentifierGenerator {

 private static final String MODULE="ELITE-SEQ-GEN";
  public EliteSequenceGenerator(){
    	
    }
	@Override
	public Serializable generate(SessionImplementor session, Object object) throws HibernateException {
		LogManager.getLogger().debug(MODULE,"Method called generate");
		    return UUID.randomUUID().toString();
	}
    
    
}
