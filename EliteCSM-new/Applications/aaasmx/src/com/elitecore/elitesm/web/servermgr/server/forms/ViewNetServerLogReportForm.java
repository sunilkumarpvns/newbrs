/**                                                     
 * Copyright (C) Elitecore Technologies Ltd.            
 * FileName   EliteRadiusLogReportControllerMBean.java                            
 * ModualName com.elitecore.radius.util.mbean                                      
 * Created on Jan 7, 2008
 * Last Modified on                                     
 * @author :  kaushikvira
 */
package com.elitecore.elitesm.web.servermgr.server.forms;

import com.elitecore.elitesm.web.core.base.forms.BaseWebForm;

/**
 * @author kaushikvira
 *
 */
public class ViewNetServerLogReportForm extends BaseWebForm {
    
    private static final long serialVersionUID = -4905307954669655251L;
    
    private String            netServerId;
    private String            action;
    private String            strTriBandNumber;
    private String            strDate;
    private String            radioChoice;
    
    public String getRadioChoice( ) {
        return radioChoice;
    }
    
    public void setRadioChoice( String radioChoice ) {
        this.radioChoice = radioChoice;
    }
    
    public String getAction( ) {
        return action;
    }
    
    public void setAction( String action ) {
        this.action = action;
    }
    
    public String getNetServerId( ) {
        return netServerId;
    }
    
    public void setNetServerId( String netServerId ) {
        this.netServerId = netServerId;
    }
    
    public static long getSerialVersionUID( ) {
        return serialVersionUID;
    }
    
    public String getStrDate( ) {
        return strDate;
    }
    
    public void setStrDate( String strDate ) {
        this.strDate = strDate;
    }
    
    public String getStrTriBandNumber( ) {
        return strTriBandNumber;
    }
    
    public void setStrTriBandNumber( String strTriBandNumber ) {
        this.strTriBandNumber = strTriBandNumber;
    }
    
}
