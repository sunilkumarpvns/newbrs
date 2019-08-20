package com.elitecore.elitesm.web.core.system.profilemanagement;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.elitecore.elitesm.blmanager.core.system.profilemanagement.ProfileBLManager;
import com.elitecore.elitesm.datamanager.DataManagerException;
import com.elitecore.elitesm.datamanager.core.exceptions.datavalidation.InvalidArrguementsException;
import com.elitecore.elitesm.datamanager.core.exceptions.datavalidation.InvalidHttpSessionException;
import com.elitecore.elitesm.datamanager.core.system.profilemanagement.data.ActionData;
import com.elitecore.elitesm.datamanager.core.system.profilemanagement.data.BISModuleData;
import com.elitecore.elitesm.datamanager.core.system.profilemanagement.data.IActionData;
import com.elitecore.elitesm.datamanager.core.system.profilemanagement.data.IBISModelData;
import com.elitecore.elitesm.datamanager.core.system.profilemanagement.data.IBISModelModuleRelData;
import com.elitecore.elitesm.datamanager.core.system.profilemanagement.data.IBISModuleData;
import com.elitecore.elitesm.datamanager.core.system.profilemanagement.data.IBISModuleSubBISModuleRelData;
import com.elitecore.elitesm.datamanager.core.system.profilemanagement.data.ISubBISModuleActionRelData;
import com.elitecore.elitesm.datamanager.core.system.profilemanagement.data.ISubBISModuleData;
import com.elitecore.elitesm.datamanager.core.system.profilemanagement.data.SubBISModuleData;
import com.elitecore.elitesm.util.EliteAssert;
import com.elitecore.elitesm.util.constants.BaseConstant;
import com.elitecore.elitesm.util.logger.Logger;
import com.elitecore.elitesm.web.core.system.login.forms.SystemLoginForm;


public class ProfileManager {
	private static final String MODULE = "PROFILE MANAGER ACTION CLASS ";
    private static HashMap licenseMap = new HashMap();
	private static HashMap hashMap = new HashMap();
	private static HashMap profileMap;
	
	public static HashMap getProfileMap() {
		return profileMap;
	}

	public static void setActionMap(HashMap hashMap){
		profileMap= hashMap;
	}
	public static boolean getModelStatus(HttpServletRequest request,String businessModelAlias){
		SystemLoginForm radiusLoginForm = (SystemLoginForm)request.getSession().getAttribute("radiusLoginForm");
		HashMap modelMap = null;
		if(radiusLoginForm.getSystemUserName().equalsIgnoreCase(BaseConstant.ADMIN_USER_NAME))
		{
			modelMap = (HashMap)profileMap.get("modelMap");
		}else{
			modelMap = (HashMap)request.getSession().getAttribute("modelMap");
		}
		for(Iterator w=modelMap.keySet().iterator();w.hasNext();){
			String modelAlias = (String)w.next();
			if(businessModelAlias.equalsIgnoreCase(modelAlias)){
				HashMap modelObjectMap = (HashMap)modelMap.get(modelAlias);
				String modelStatus = (String)modelObjectMap.get("businessModelStatus");
				if(modelStatus.equalsIgnoreCase("E")){
					return true;
				}else{
					return false;
				}
			}
		}
		return false;
	}
	
	public static boolean getModuleStatus(HttpServletRequest request,String businessModelAlias,String businessModuleAlias){
		SystemLoginForm radiusLoginForm = (SystemLoginForm)request.getSession().getAttribute("radiusLoginForm");
		HashMap modelMap = null;
		if(radiusLoginForm.getSystemUserName().equalsIgnoreCase(BaseConstant.ADMIN_USER_NAME))
		{
			modelMap = (HashMap)profileMap.get("modelMap");
		}else{
			modelMap = (HashMap)request.getSession().getAttribute("modelMap");
		}

		for(Iterator w=modelMap.keySet().iterator();w.hasNext();){
			String modelAlias = (String)w.next();
			if(businessModelAlias.equalsIgnoreCase(modelAlias)){
				HashMap modelObjectMap = (HashMap)modelMap.get(modelAlias);
				String modelStatus = (String)modelObjectMap.get("businessModelStatus");
				if(modelStatus.equalsIgnoreCase("E")){
					HashMap moduleMap = (HashMap)modelObjectMap.get("moduleMap");
					for(Iterator w1=moduleMap.keySet().iterator();w1.hasNext();){
						String moduleAlias = (String)w1.next();
						if(businessModuleAlias.equalsIgnoreCase(moduleAlias)){
							HashMap moduleObjectMap = (HashMap)moduleMap.get(moduleAlias);
							String moduleStatus = (String)moduleObjectMap.get("businessModuleStatus");
							if(moduleStatus.equalsIgnoreCase("E")){
								return true;
							}else{
								return false;
							}
						}
					}
					return false;
				}else{
					return false;
				}
			}
		}
		return false;
	}
	
	public static boolean getSubModuleStatus(HttpServletRequest request,String businessModelAlias,String businessModuleAlias,String subBusinessModuleAlias) {
		SystemLoginForm radiusLoginForm = (SystemLoginForm)request.getSession().getAttribute("radiusLoginForm");
		HashMap modelMap = null;
		if(radiusLoginForm.getSystemUserName().equalsIgnoreCase(BaseConstant.ADMIN_USER_NAME))
		{
			modelMap = (HashMap)profileMap.get("modelMap");
		}else{
			modelMap = (HashMap)request.getSession().getAttribute("modelMap");
		}

		for(Iterator w=modelMap.keySet().iterator();w.hasNext();){
			String modelAlias = (String)w.next();
			if(businessModelAlias.equalsIgnoreCase(modelAlias)){
				HashMap modelObejctMap = (HashMap)modelMap.get(modelAlias);
				String modelStatus = (String)modelObejctMap.get("businessModelStatus");
				if(modelStatus.equalsIgnoreCase("E")){
					HashMap moduleMap = (HashMap)modelObejctMap.get("moduleMap");
					for(Iterator w1=moduleMap.keySet().iterator();w1.hasNext();){
						String moduleAlias = (String)w1.next();
						if(businessModuleAlias.equalsIgnoreCase(moduleAlias)){
							HashMap moduleObjectMap = (HashMap)moduleMap.get(moduleAlias);
							String moduleStatus = (String)moduleObjectMap.get("businessModuleStatus");
							if(moduleStatus.equalsIgnoreCase("E")){
								HashMap subModuleMap = (HashMap)moduleObjectMap.get("subModuleMap");
								for(Iterator w2=subModuleMap.keySet().iterator();w2.hasNext();){
									String subModuleAlias = (String)w2.next();
									if(subBusinessModuleAlias.equalsIgnoreCase(subModuleAlias)){
										HashMap subModuleObjectMap = (HashMap)subModuleMap.get(subModuleAlias);
										String subModuleStatus = (String)subModuleObjectMap.get("subBusinessModuleStatus");
										if(subModuleStatus.equalsIgnoreCase("E")){
											return true;
										}else{
											return false;
										}
									}
								}
							}else{
								return false;
							}
						}
					}
				}else{
					return false;
				}
			}
		}
		return false;
	}
	
        public static void generateLicense(String licenseKey){
            try {
                ProfileBLManager profileBLManager = new ProfileBLManager();
                HashMap modelMap = new HashMap();
                
                IBISModelData bisModelData = profileBLManager.getBISModelAlias(licenseKey);
                
                if(bisModelData != null) {

                    IBISModuleData businessModuleData = new BISModuleData();
                    List businessModelModuleRelList = new ArrayList();
                    HashMap modelObjectMap = new HashMap();
                    modelObjectMap.put("businessModelId", bisModelData.getBusinessModelId());
                    modelObjectMap.put("businessModelName", bisModelData.getName());
                    modelObjectMap.put("businessModelStatus", bisModelData.getStatus());
                    modelObjectMap.put("businessModelAlias", bisModelData.getAlias());
                
                    businessModelModuleRelList = profileBLManager.getBISModelModuleRelList(modelObjectMap.get("businessModelId").toString());
                    HashMap moduleMap = new HashMap();
            
                    for ( int j = 0; j < businessModelModuleRelList.size(); j++ ) {
                        HashMap moduleObjectMap = new HashMap();
                        List businessModuleSubBISModuleRelList = new ArrayList();
                        ISubBISModuleData subBusinessModuleData = new SubBISModuleData();
                        businessModuleData = profileBLManager.getBISModule(((IBISModelModuleRelData) businessModelModuleRelList.get(j)).getBusinessModuleId());
                        moduleObjectMap.put("businessModuleId", businessModuleData.getBusinessModuleId());
                        moduleObjectMap.put("businessModuleName", businessModuleData.getName());
                        moduleObjectMap.put("businessModuleStatus", businessModuleData.getStatus());
                        moduleObjectMap.put("businessModuleAlias", businessModuleData.getAlias());
                    
                        businessModuleSubBISModuleRelList = profileBLManager.getBISModuleSubBISModuleRelList(moduleObjectMap.get("businessModuleId").toString());
                        HashMap subModuleMap = new HashMap();
                
                        for ( int k = 0; k < businessModuleSubBISModuleRelList.size(); k++ ) {
                            HashMap subModuleObjectMap = new HashMap();
                            List subBusinessModuleActionRelList = new ArrayList();
                            IActionData actionData = new ActionData();
                            subBusinessModuleData = profileBLManager.getSubBISModule(((IBISModuleSubBISModuleRelData) businessModuleSubBISModuleRelList.get(k)).getSubBusinessModuleId());
                            subModuleObjectMap.put("subBusinessModuleId", subBusinessModuleData.getSubBusinessModuleId());
                            subModuleObjectMap.put("subBusinessModuleName", subBusinessModuleData.getName());
                            subModuleObjectMap.put("subBusinessModuleStatus", subBusinessModuleData.getStatus());
                            subModuleObjectMap.put("subBusinessModuleAlias", subBusinessModuleData.getAlias());
                            
                            subBusinessModuleActionRelList = profileBLManager.getSubBISModuleActionRelList(subModuleObjectMap.get("subBusinessModuleId").toString());
                            HashMap actionMap = new HashMap();
                        
                            for ( int l = 0; l < subBusinessModuleActionRelList.size(); l++ ) {
                                HashMap actionObjectMap = new HashMap();
                                actionData = profileBLManager.getActionData(((ISubBISModuleActionRelData) subBusinessModuleActionRelList.get(l)).getActionId());
                                actionObjectMap.put("actionId", actionData.getActionId());
                                actionObjectMap.put("actionName", actionData.getName());
                                actionObjectMap.put("actionStatus", actionData.getStatus());
                                actionObjectMap.put("actionAlias", actionData.getAlias());
                                actionMap.put(((ISubBISModuleActionRelData) subBusinessModuleActionRelList.get(l)).getActionData().getAlias(), actionObjectMap);
                            }
                        
                            subModuleObjectMap.put("actionMap", actionMap);
                            subModuleMap.put(subBusinessModuleData.getAlias(), subModuleObjectMap);
                        }
                        moduleObjectMap.put("subModuleMap", subModuleMap);
                        moduleMap.put(businessModuleData.getAlias(), moduleObjectMap);
                    }
                    modelObjectMap.put("moduleMap", moduleMap);
                    modelMap.put(bisModelData.getAlias(), modelObjectMap);
                }
                licenseMap.put(licenseKey, modelMap);
        }
        catch (DataManagerException dExp) {
            Logger.logError(MODULE, "Error during get license map, reason : " + dExp.getMessage());
            Logger.logTrace(MODULE, dExp);
        }
        catch (Exception exp) {
            Logger.logError(MODULE, "Error during get license map, reason : " + exp.getMessage());
            Logger.logTrace(MODULE, exp);
        }
    }
    
        
        public static boolean getSubMoudleActionStatus(HttpServletRequest request,HttpServletResponse response,String subModuleActionAlias) throws InvalidArrguementsException{
            try {
                EliteAssert.notNull(request,"request must be specified.");
                EliteAssert.notNull(response,"response must be specified.");
                EliteAssert.notNull(subModuleActionAlias,"subModuleActionAlias must be specified.");

                EliteAssert.valiedWebSession(request);

                SystemLoginForm radiusLoginForm = (SystemLoginForm) request.getSession(false).getAttribute("radiusLoginForm");
                EliteAssert.notNull(radiusLoginForm,"radiusLoginForm must be specified in session.");

                Set<String> actionAliasSet = (Set<String>) request.getSession(false).getAttribute("__action_Alias_Set_");
                EliteAssert.notNull(actionAliasSet,"__action_Alias_Set_ must be specified in session.");

                if(actionAliasSet.contains(subModuleActionAlias))
                    return true;

            }
            catch(InvalidArrguementsException e){
                Logger.logTrace(MODULE, e);
                return false;
            }
            catch(InvalidHttpSessionException e){
                Logger.logTrace(MODULE, e);
                return false;
            }
            catch (Exception exp) {
                Logger.logTrace(MODULE, exp);
                return false;
            }
            return false;
        }

            
            
            
        
        
        
}
