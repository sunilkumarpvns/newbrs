/**                                                     
 * Copyright (C) Elitecore Technologies Ltd.            
 * FileName   IExceptionTranslater.java                            
 * ModualName com.elitecore.elitesm.datamanager.core.exceptions                                    
 * Created on Jan 31, 2008
 * Last Modified on                                     
 * @author :  himanshudobaria
 */
package com.elitecore.elitesm.datamanager.core.exceptions;

import org.hibernate.HibernateException;

import com.elitecore.elitesm.datamanager.core.exceptions.datasource.BaseDatasourceException;

/**
 * @author himanshudobaria
 *
 */
public interface IExceptionTranslater {
	
	public BaseDatasourceException translateToDatasourceException(HibernateException e);

}
