/**                                                     
 * Copyright (C) Elitecore Technologies Ltd.            
 * FileName   DownloadLicensePublickeyForm.java                             
 * ModualName                                     
 * Created on Oct 8, 2007
 * Last Modified on                                     
 * @author :  kaushikvira
 */    
package com.elitecore.elitesm.web.servermgr.server.forms;

import com.elitecore.elitesm.web.core.base.forms.BaseWebForm;


/**
 * @author kaushikvira
 *
 */
public class DownloadLicensePublickeyForm extends BaseWebForm {
    
    String netServerId;

    
    public String getNetServerId() {
        return netServerId;
    }

    
    public void setNetServerId( String netServerId ) {
        this.netServerId = netServerId;
    }

}
