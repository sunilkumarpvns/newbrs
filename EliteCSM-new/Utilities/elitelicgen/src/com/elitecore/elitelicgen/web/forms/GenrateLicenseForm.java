/*
 *  License Generation Module
 *
 *  Elitecore Technologies Ltd.
 *  904, Silicon Tower, Law Garden
 *  Ahmedabad, India - 380009
 *
 *  Created on Nov 5, 2007
 *  Created By kaushikvira
 */
package com.elitecore.elitelicgen.web.forms;

import java.util.ArrayList;
import java.util.List;

import com.elitecore.elitelicgen.base.forms.BaseWebForm;
import com.elitecore.license.base.LicenseData;

/**
 * @author kaushikvira
 *
 */
public class GenrateLicenseForm extends BaseWebForm {
    
    /**
     * 
     */
    private static final long serialVersionUID = 1L;
    private List<LicenseData> lstLicData       = new ArrayList<LicenseData>();
    
    
    public static long getSerialVersionUID( ) {
        return serialVersionUID;
    }

    public String getSelect( int index ) {
        return ((LicenseData) lstLicData.get(index)).getSelect();
    }
    
    public void setSelect( int index ,String select ) {
        while (lstLicData.size() - 1 < index)
            lstLicData.add(new LicenseData());
        ((LicenseData) lstLicData.get(index)).setSelect(select);
    }
    
    public List<LicenseData> getLstLicData( ) {
        return lstLicData;
    }
    
    public void setLstLicData( List<LicenseData> lstLicData ) {
        this.lstLicData = lstLicData;
    }
    
    public String getAdditionalKey( int index ) {
        return ((LicenseData) lstLicData.get(index)).getAdditionalKey();
    }
    
    public void setAdditionalKey( int index , String additionalKey ) {
        while (lstLicData.size() - 1 < index)
            lstLicData.add(new LicenseData());
        ((LicenseData) lstLicData.get(index)).setAdditionalKey(additionalKey);
    }
    
    public String getModule( int index ) {
        
        return ((LicenseData) lstLicData.get(index)).getModule();
    }
    
    public void setModule( int index , String module ) {
        
        while (lstLicData.size() - 1 < index)
            lstLicData.add(new LicenseData());
        ((LicenseData) lstLicData.get(index)).setModule(module);
    }
    
    public String getName( int index ) {
        return ((LicenseData) lstLicData.get(index)).getName();
    }
    
    public void setName( int index , String name ) {
        while (lstLicData.size() - 1 < index)
            lstLicData.add(new LicenseData());
        ((LicenseData) lstLicData.get(index)).setName(name);
    }
    
    public String getStatus( int index ) {
        return ((LicenseData) lstLicData.get(index)).getStatus();
    }
    
    public void setStatus( int index , String status ) { 
        while (lstLicData.size() - 1 < index)
            lstLicData.add(new LicenseData());
        ((LicenseData) lstLicData.get(index)).setStatus(status);
    }
    
    public String getType( int index ) {
        return ((LicenseData) lstLicData.get(index)).getType();
    }
    
    public void setType( int index , String type ) {
        while (lstLicData.size() - 1 < index)
            lstLicData.add(new LicenseData());
        ((LicenseData) lstLicData.get(index)).setType(type);
    }
    
    public String getStrValue( int index ) {
        return ((LicenseData) lstLicData.get(index)).getValue();
    }
    
    public void setStrValue( int index , String value ) {
        while (lstLicData.size() - 1 < index)
            lstLicData.add(new LicenseData());
        ((LicenseData) lstLicData.get(index)).setValue(value);
    }
    
    public String getVersion( int index ) {
        return ((LicenseData) lstLicData.get(index)).getVersion();
    }
    
    public void setVersion( int index , String version ) {
        while (lstLicData.size() - 1 < index)
            lstLicData.add(new LicenseData());
        ((LicenseData) lstLicData.get(index)).setVersion(version);
        
    }
    
    public void setDisplayName( int index ,String displayName ) {
        while (lstLicData.size() - 1 < index)
            lstLicData.add(new LicenseData());
        ((LicenseData) lstLicData.get(index)).setDisplayName(displayName);
        
    }
    
    public String getDisplayName( int index ) {
        return ((LicenseData) lstLicData.get(index)).getDisplayName();
    }
    
    public void setDescription( int index , String description ) {
    	while (lstLicData.size() - 1 < index)
    		lstLicData.add(new LicenseData());
    	((LicenseData) lstLicData.get(index)).setDescription(description);

    }

    public String getDescription( int index ) {
    	return ((LicenseData) lstLicData.get(index)).getDescription();
    }
     
    public void setValueType( int index , String valueType) {
    	while (lstLicData.size() - 1 < index)
    		lstLicData.add(new LicenseData());
    	((LicenseData) lstLicData.get(index)).setValueType(valueType);

    }

    public String getValueType( int index ) {
    	return ((LicenseData) lstLicData.get(index)).getValueType();
    }

    public void setOperator( int index , String operator) {
    	while (lstLicData.size() - 1 < index)
    		lstLicData.add(new LicenseData());
    	((LicenseData) lstLicData.get(index)).setOperator(operator);

    }

    public String getOperator( int index ) {
    	return ((LicenseData) lstLicData.get(index)).getOperator();
    }

    
    
}
 