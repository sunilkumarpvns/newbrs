/**                                                     
 * Copyright (C) Elitecore Technologies Ltd.            
 * FileName   NtradData.java                 		
 * ModualName radius    			      		
 * Created on 2 April, 2008
 * Last Modified on                                     
 * @author :  SMCodeGen
 */
package com.elitecore.elitesm.datamanager.radius.radtest.data;

import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

import com.elitecore.commons.base.Differentiable;
import com.elitecore.elitesm.datamanager.core.base.data.BaseData;

public class RadiusTestData extends BaseData implements IRadiusTestData,Differentiable {
    
    private String ntradId;
    private String name;
    private String adminHost;
    private int    adminPort;
    private int   reTimeOut;
    private int    retries;
    private String scecretKey;
    private String userName;
    private String userPassword;
    private String isChap;
    private int   requestType;
    private Set<RadiusTestParamData>    radParamRel = new LinkedHashSet<RadiusTestParamData>();
    private List paramList;
    private String hostAddress;
    
    public String getHostAddress() {
		return hostAddress;
	}


	public void setHostAddress(String hostAddress) {
		this.hostAddress = hostAddress;
	}


	public List getParamList( ) {
        return paramList;
    }

    
    public void setParamList( List paramList ) {
        this.paramList = paramList;
    }

    public Set getRadParamRel( ) {
        return radParamRel;
    }
    
    public void setRadParamRel( Set radParamRel ) {
        this.radParamRel = radParamRel;
    }
    
    
    
    public String getNtradId() {
		return ntradId;
	}


	public void setNtradId(String ntradId) {
		this.ntradId = ntradId;
	}


	public String getName( ) {
        return name;
    }
    
    public void setName( String name ) {
        this.name = name;
    }
    
    public String getAdminHost( ) {
        return adminHost;
    }
    
    public void setAdminHost( String adminHost ) {
        this.adminHost = adminHost;
    }
    
    public int getAdminPort( ) {
        return adminPort;
    }
    
    public void setAdminPort( int adminPort ) {
        this.adminPort = adminPort;
    }
    
    public int getReTimeOut( ) {
        return reTimeOut;
    }
    
    public void setReTimeOut( int reTimeOut ) {
        this.reTimeOut = reTimeOut;
    }
    
    public int getRetries( ) {
        return retries;
    }
    
    public void setRetries( int retries ) {
        this.retries = retries;
    }
    
    public String getScecretKey( ) {
        return scecretKey;
    }
    
    public void setScecretKey( String scecretKey ) {
        this.scecretKey = scecretKey;
    }
    
    public String getUserName( ) {
        return userName;
    }
    
    public void setUserName( String userName ) {
        this.userName = userName;
    }
    
    public String getUserPassword( ) {
        return userPassword;
    }
    
    public void setUserPassword( String userPassword ) {
        this.userPassword = userPassword;
    }
    
    public String getIsChap( ) {
        return isChap;
    }
    
    public void setIsChap( String isChap ) {
        this.isChap = isChap;
    }
    
    public int getRequestType( ) {
        return requestType;
    }
    
    public void setRequestType( int requestType ) {
        this.requestType = requestType;
    }


	@Override
	public JSONObject toJson() {
		JSONObject object = new JSONObject();
		object.put("Packet Name", name);
		object.put("Radius Server/Port", adminHost + ":" + adminPort);
		object.put("Replay Timeout (sec)", reTimeOut);
		object.put("Radius Secret Key", scecretKey);
		object.put("User Name", userName);
		object.put("Password", userPassword);
		object.put("Request Type", requestType);
		object.put("Client Ip/Port", hostAddress);
		if(radParamRel!=null){
			JSONArray array = new JSONArray();
			for (RadiusTestParamData element : radParamRel) {
				array.add(element.toJson());
			}
			object.put("Parameter", array);
		}
		return object;
	}
    
}
