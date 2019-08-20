/**                                                     
 * Copyright (C) Elitecore Technologies Ltd.            
 * FileName   INtradData.java                 		
 * ModualName radius    			      		
 * Created on 2 April, 2008
 * Last Modified on                                     
 * @author :  SMCodeGen
 */
package com.elitecore.elitesm.datamanager.radius.radtest.data;

import java.util.List;
import java.util.Set;

public interface IRadiusTestData {

	public String getNtradId();

	public void setNtradId(String ntradId);

	public String getName( );

	public void setName( String name );

	public String getAdminHost( );

	public void setAdminHost( String adminHost );

	public int getAdminPort( );

	public void setAdminPort( int adminPort );

	public int getReTimeOut( );

	public void setReTimeOut( int reTimeOut );

	public int getRetries( );

	public void setRetries( int retries );

	public String getScecretKey( );

	public void setScecretKey( String scecretKey );

	public String getUserName( );

	public void setUserName( String userName );

	public String getUserPassword( );

	public void setUserPassword( String userPassword );

	public String getIsChap( );

	public void setIsChap( String isChap );

	public int getRequestType( );

	public void setRequestType( int requestType );

	public Set getRadParamRel( );

	public void setRadParamRel( Set radParamRel );

	public List getParamList( );

	public void setParamList( List paramList );

	public String getHostAddress();

	public void setHostAddress(String hostAddress);


}
