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

package com.elitecore.license.base.commons;

public class LicenseConstants {

    public static final String ENTITY_NAME                 = "LICENSE";
    
    public static final int    LICENSE_KEY_TOKEN_COUNT     = 10;
    public static final String LICENSE_SEPRATOR            = "!";
    public static final String PUBLIC_KEY_SEPRATOR         = ":";
    public static final String LICENSE_KEY_SEPRATOR        = "|";
    
    public static final int    LICENSE_NAME_INDEX          = 1;
    public static final int    LICENSE_MODULE_INDEX        = 2;
    public static final int    LICENSE_TYPE_INDEX          = 3;
    public static final int    LICENSE_VALUE_INDEX         = 4;
    public static final int    LICENSE_VERSION_INDEX       = 5;
    public static final int    LICENSE_STATUS_INDEX        = 6;
    public static final int    LICENSE_ADDITIONALKEY_INDEX = 7;
    public static final int    LICENSE_DISPLAYNAME_INDEX   = 8;
    public static final int    LICENSE_VALUETYPE_INDEX   	 = 9;
    public static final int    LICENSE_OPERATOR_INDEX   	 = 10;
    
    public static final String LICENSE_NAME_LABEL 	       = "name";
    public static final String LICENSE_MODULE_LABEL 	   = "module";
    public static final String LICENSE_TYPE_LABEL 		   = "type";
    public static final String LICENSE_VALUE_LABEL 		   = "value";
    public static final String LICENSE_VERSION_LABEL 	   = "version";
    public static final String LICENSE_STATUS_LABEL		   = "status";
    public static final String LICENSE_ADDITIONALKEY_LABEL = "additional key";
    public static final String LICENSE_DISPLAYNAME_LABEL   = "display name";
    public static final String LICENSE_VALUETYPE_LABEL     = "valuet ype";
    public static final String LICENSE_OPERATOR_LABEL 	   = "operator";
    
    public static final int    PUBLIC_KEY_TOKEN_COUNT      = 4;
    
    public static final String LICENSE_DIRECTORY           = "license";
    public static final String PUBLIC_KEY_FILE_EXT         = "pubkey";
    public static final String LICENSE_FILE_NAME           = "local_node";
    public static final String NFV_LICENSE_FILE_NAME       = "nfv_node";
    public static final String LICESE_FILE_EXT             = ".lic";
    public static final String DEFAULT_ADDITIONAL_KEY      = "elitecore";
    public static final String DEFAULT_STATUS              = "elitecore";
    public static final String DEFAULT_ADDITIONAL_KEY_STERLITE      = "sterlite";
    public static final String DATE_FORMAT                 = "dd/MM/yyyy";
    public static final String INTEGRATION_USER = "integration_user";
    public static final String INTEGRATION_SECRET = "S3CR37";
    
    private LicenseConstants(){

    }
}
