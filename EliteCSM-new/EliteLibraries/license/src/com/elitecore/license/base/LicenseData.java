/*
 *  License Module
 *
 *  Elitecore Technologies Ltd.
 *  904, Silicon Tower, Law Garden
 *  Ahmedabad, India - 380009
 *
 *  Created on 12th September 2007
 *  Created By Vaseem Lahori
 */

package com.elitecore.license.base;

import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import com.elitecore.license.base.commons.LicenseConstants;

public class LicenseData implements Serializable {
    
    /**
     * 
     */
    private static final long serialVersionUID = 1L;
    private String            name;
    private String            module;
    private String            type;
    private String            value;
    private String            licenseKey;
    private String            additionalKey;
    private String            version;
    private String            status;
    private String            select;
    private String            description;
    private List<LicenseData> licenseData;
    private String            displayName;
    private boolean           flag;
    private int               index;
    private String            state ;
    private String 			  valueType;
    private String 			  operator; 
    
    public LicenseData() {
	}
    
    public LicenseData(LicenseData licenseData) {
    	this.name = licenseData.name;
    	this.module = licenseData.module;
    	this.type = licenseData.type;
    	this.value = licenseData.value;
    	this.licenseKey = licenseData.licenseKey;
    	this.additionalKey = licenseData.additionalKey;
    	this.version = licenseData.version;
    	this.status = licenseData.status;
    	this.select = licenseData.select;
    	this.description = licenseData.description;
    	this.displayName = licenseData.displayName;
    	this.flag = licenseData.flag;
    	this.index = licenseData.index;
    	this.state = licenseData.state;
    	this.valueType = licenseData.valueType;
    	this.operator = licenseData.operator;
	}
    
    public String getDisplayName( ) {
        return displayName;
    }
    
    public void setDisplayName( String diplayName ) {
        this.displayName = diplayName;
    }
    
    public int getIndex( ) {
        return index;
    }
    
    public void setIndex( int index ) {
        this.index = index;
    }
    
    public static long getSerialVersionUID( ) {
        return serialVersionUID;
    }
    
    public boolean isFlag( ) {
        return flag;
    }
    
    public void setFlag( boolean flag ) {
        this.flag = flag;
    }
    
    public String getVersion( ) {
        return version;
    }
    
    public String getAdditionalKey( ) {
        return additionalKey;
    }
    
    public void setAdditionalKey( String additionalKey ) {
        this.additionalKey = additionalKey;
    }
    
    public void setVersion( String version ) {
        this.version = version;
    }
    
    public List<LicenseData> getLicenseData( ) {
        return licenseData;
    }
    
    public void setLicenseData( List<LicenseData> licenseData ) {
        this.licenseData = licenseData;
    }
    
    public String getDescription( ) {
        return description;
    }
    
    public void setDescription( String description ) {
        this.description = description;
    }
    
    public String getLicenseKey( ) {
        return licenseKey;
    }
    
    public void setLicenseKey( String licenseKey ) {
        this.licenseKey = licenseKey;
    }
    
    public String getModule( ) {
        return module;
    }
    
    public void setModule( String module ) {
        this.module = module;
    }
    
    public String getName( ) {
        return name;
    }
    
    public void setName( String name ) {
        this.name = name;
    }
    
    public String getStatus( ) {
        return status;
    }
    
    public void setStatus( String status ) {
        this.status = status;
    }
    
    public String getType( ) {
        return type;
    }
    
    public void setType( String type ) {
        this.type = type;
    }
    
    public String getValue( ) {
        return value;
    }
    
    public void setValue( String value ) {
        this.value = value;
    }
    
    @Override
    public String toString( ) {
        return index +":"+ displayName + ":" + name + ":" + module + ":" + type + ":" + value + ":" + version + ":" + status + ":" + additionalKey + ":" + description + ":" + valueType + ":" + operator;
    }
    
    public String getSelect( ) {
        return select;
    }
    
    public void setSelect( String select ) {
        this.select = select;
    }
    
    public String getState( ) {
        return state;
    }
    
    public void setState( String state ) {
        this.state = state;
    }
    
    public void setValueType(String valueType){
    	this.valueType=valueType;
    	}
    	
    public String getValueType(){
    	return this.valueType;
    }
    
    public void setOperator(String operator){
    	this.operator=operator;
    }
    
    public String getOperator(){
    	return this.operator;
    }
    
    @Override
    public boolean equals(Object obj) {

    	if(obj instanceof LicenseData){

    		if(this.valueType.equalsIgnoreCase("TEXT")){
    			return compareValue(((LicenseData)obj).getValue());

    		}else if(this.valueType.equalsIgnoreCase("NUMBER")){
    			return compareValue(Long.parseLong(((LicenseData)obj).getValue()));

    		}else if(this.valueType.equalsIgnoreCase("DATE")){
    			try {
    				return compareValue(new Date(Long.parseLong(((LicenseData)obj).getValue())));
    			} catch (Exception e) {
    				return false;
    			}
    		}
    	}
    	return false;
    } 

    private boolean compareValue(String value){
    	if(this.operator.equals(">")){
    		return  value.length() > this.value.length(); 
    	}else if(this.operator.equals("<")){
    		return value.length() < this.value.length();
    	}else if(this.operator.equals(">=")){
    		return value.length() >= this.value.length();
    	}else if(this.operator.equals("<=")){ 
    		return value.length() <= this.value.length();
    	}else if(this.operator.equals("=")){
    		return this.value.equalsIgnoreCase(value);
    	}else if(this.operator.equals("!=")){
    		return (!this.value.equalsIgnoreCase(value));
    	}else if(this.operator.equals("contains")){
    		return (this.value.contains(value));
    	}else			
    		return false;

    }

    private boolean compareValue(long value){
    	long objValue;
    	try{
    		objValue= Long.parseLong(this.value);
    	}catch(Exception e){
    		return false;
    	}

    	if(this.operator.equals(">")){
    		return value > objValue ; 
    	}else if(this.operator.equals("<")){
    		return value < objValue;
    	}else if(this.operator.equals(">=")){
    		return  value >= objValue ;
    	}else if(this.operator.equals("<=")){ 
    		return value <= objValue;
    	}else if(this.operator.equals("=")){
    		return value==objValue; 
    	}else if(this.operator.equals("!=")){
    		return value!=objValue;
    	}else
    		return false;
    }

    private boolean compareValue(Date value){
    	Date objValue;
    	SimpleDateFormat dateFormat = new SimpleDateFormat(LicenseConstants.DATE_FORMAT);
    	try{
    		objValue = dateFormat.parse(this.value);
    	}catch(Exception e){
    		return false;
    	}
    	if(this.operator.equals(">")){
    		return value.after(objValue); 
    	}else if(this.operator.equals(">=")){
    		Calendar cal = Calendar.getInstance();
    		cal.setTime(objValue);
    		cal.add( Calendar.DATE, -1 );
    		return value.after(cal.getTime());
    	}else if(this.operator.equals("<")){
    		return value.before(objValue);
    	}else if(this.operator.equals("<=")){
    		Calendar cal = Calendar.getInstance();
    		cal.setTime(objValue);
    		cal.add( Calendar.DATE, 1 );
    		return value.before(cal.getTime());
    	}else if(this.operator.equals("=")){
    		return value.equals(objValue);
    	}else if(this.operator.equals("!=")){
    		return (!value.equals(objValue));
    	}else
    		return false;
    }
}

