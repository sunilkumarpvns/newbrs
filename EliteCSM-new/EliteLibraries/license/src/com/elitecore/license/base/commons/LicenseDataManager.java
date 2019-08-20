package com.elitecore.license.base.commons;

import java.util.List;

import com.elitecore.license.base.LicenseData;
import com.elitecore.license.configuration.LicenseConfigurationManager;

/**
 * @author Pankit Shah
 */
public class LicenseDataManager {
 
	public static LicenseData getLicenseData( ) {
        
        /*Radius*/
    //	LicenseConfigurationManager.getInstance().init();
        List<LicenseData> lstAAAModualData = LicenseConfigurationManager.getInstance().getAAAServiceList();
       
        LicenseData licAAAModualData_Module = new LicenseData();
        licAAAModualData_Module.setName(LicenseNameConstants.AAA_MODULE);
        licAAAModualData_Module.setDisplayName(LicenseNameConstants.AAA_MODULE_DISPLAY);
        licAAAModualData_Module.setType(LicenseTypeConstants.MODULE);
        licAAAModualData_Module.setDescription("system_aaa_module");
        licAAAModualData_Module.setModule(LicenseModuleConstants.SYSTEM_AAA);
        licAAAModualData_Module.setValueType("TEXT");
        licAAAModualData_Module.setOperator("=");
        /*adding list to modual*/
        licAAAModualData_Module.setLicenseData(lstAAAModualData);
        
        
        List<LicenseData> lstNVModuleData = LicenseConfigurationManager.getInstance().getNVServiceList();
        
        LicenseData licNVModuleData_Module = new LicenseData();
        licNVModuleData_Module.setName(LicenseNameConstants.NV_MODULE);
        licNVModuleData_Module.setDisplayName(LicenseNameConstants.NV_MODULE_DISPLAY);
        licNVModuleData_Module.setType(LicenseTypeConstants.MODULE);
        licNVModuleData_Module.setDescription("system_nv_module");
        licNVModuleData_Module.setModule(LicenseModuleConstants.SYSTEM_NV);
        licNVModuleData_Module.setValueType("TEXT");
        licNVModuleData_Module.setOperator("=");
        licNVModuleData_Module.setLicenseData(lstNVModuleData);
        
        /*Resource Manager*/
/*        List<LicenseData> lstRMModualData = LicenseConfigurationManager.getInstance().getRMLicenseList();
        
        LicenseData licRMModualData_Module = new LicenseData();
        licRMModualData_Module.setName(LicenseNameConstants.RESOURCEMANAGER_MODULE);
        licRMModualData_Module.setDisplayName(LicenseNameConstants.RESOURCEMANAGER_MODULE_DISPLAY);
        licRMModualData_Module.setType(LicenseTypeConstants.MODULE);
        licRMModualData_Module.setDescription("resourcemanager_module");
        licRMModualData_Module.setModule(LicenseModuleConstants.SYSTEM_RESOURCEMANAGER);
        licRMModualData_Module.setValueType("TEXT");
        licRMModualData_Module.setOperator("=");
        adding list to modual 
        licRMModualData_Module.setLicenseData(lstRMModualData);      
        
         DIAMETER                 
        LicenseData licDiameterModuleData_Module = new LicenseData();
        licDiameterModuleData_Module.setName(LicenseNameConstants.DIAMETER_MODULE);
        licDiameterModuleData_Module.setDisplayName(LicenseNameConstants.DIAMETER_MODULE_DISPLAY);
        licDiameterModuleData_Module.setType(LicenseTypeConstants.MODULE);
        licDiameterModuleData_Module.setDescription("diameter_module");
        licDiameterModuleData_Module.setModule(LicenseModuleConstants.SYSTEM_DIAMETER);
        licDiameterModuleData_Module.setValueType("TEXT");
        licDiameterModuleData_Module.setOperator("=");
        
        List<LicenseData> lstDiameterModuleData = LicenseConfigurationManager.getInstance().getDiameterLicenseList(); 
        licDiameterModuleData_Module.setLicenseData(lstDiameterModuleData);
         DIAMETER END 
*/        
        List<LicenseData> lstSystemModuelData = LicenseConfigurationManager.getInstance().getSystemLicenseList();

       // lstSystemModuelData.add(licRMModualData_Module);
        lstSystemModuelData.add(licAAAModualData_Module);
        lstSystemModuelData.add(licNVModuleData_Module);
       // lstSystemModuelData.add(licDiameterModuleData_Module);
        
        LicenseData licSystemModualData = new LicenseData();
        licSystemModualData.setName(LicenseNameConstants.SYSTEM_MODULE);
        licSystemModualData.setDisplayName(LicenseNameConstants.SYSTEM_MODULE_DISPLAY);
        licSystemModualData.setType(LicenseTypeConstants.MODULE);
        licSystemModualData.setDescription("system_module");
        licSystemModualData.setModule(LicenseModuleConstants.SYSTEM);
        
        /*Adding list into modual*/
        
        licSystemModualData.setLicenseData(lstSystemModuelData);
        return licSystemModualData;
    }
}
