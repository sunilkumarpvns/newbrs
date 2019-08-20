package com.elitecore.elitesm.web.core.system.accessgroup;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;

import com.elitecore.elitesm.blmanager.core.system.profilemanagement.ProfileBLManager;
import com.elitecore.elitesm.datamanager.DataManagerException;
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
import com.elitecore.elitesm.util.logger.Logger;
import com.elitecore.elitesm.web.core.base.BaseWebAction;
import com.elitecore.elitesm.web.core.system.accessgroup.forms.CreateAccessGroupForm;
import com.elitecore.elitesm.web.core.system.login.forms.SystemLoginForm;

public class InitCreateAccessGroupAction extends BaseWebAction{
	private static final String SUCCESS_FORWARD = "initCreateAccessGroup";
	private static final String FAILURE_FORWARD = "failuire";
	private static final String MODULE = "Create Access Group Action";
	
	public ActionForward execute(ActionMapping mapping,ActionForm form,HttpServletRequest request,HttpServletResponse response) throws DataManagerException{
		Logger.logTrace(MODULE,"Enter execute method of "+getClass().getName());
		
		try {
			ProfileBLManager profileBLManager = new ProfileBLManager();
			CreateAccessGroupForm createAccessGroupForm = (CreateAccessGroupForm)form;
			String userName = ((SystemLoginForm)request.getSession().getAttribute("radiusLoginForm")).getSystemUserName();
			createAccessGroupForm.setDescription(getDefaultDescription(userName));
			List businessModelList = profileBLManager.getBusinessModelList();
			
			createAccessGroupForm.setListBusinessModel(businessModelList);
			HashMap profileMap = new HashMap();
			profileMap.put("name",profileMap);
			HashMap modelMap = new HashMap();
			for(int i=0;i<businessModelList.size();i++){
				IBISModelData bisModelData = (IBISModelData)businessModelList.get(i);
				IBISModuleData businessModuleData = new BISModuleData();
				List businessModelModuleRelList = new ArrayList();
				HashMap modelObjectMap = new HashMap();
				modelObjectMap.put("businessModelId",bisModelData.getBusinessModelId());
				modelObjectMap.put("businessModelName",bisModelData.getName());
				modelObjectMap.put("businessModelStatus",bisModelData.getStatus());
				businessModelModuleRelList = profileBLManager.getBISModelModuleRelList(modelObjectMap.get("businessModelId").toString());
				HashMap moduleMap = new HashMap();
				
				for(int j=0;j<businessModelModuleRelList.size();j++){
					HashMap moduleObjectMap = new HashMap();
					List businessModuleSubBISModuleRelList = new ArrayList();
					ISubBISModuleData subBusinessModuleData = new SubBISModuleData();
					businessModuleData = profileBLManager.getBISModule(((IBISModelModuleRelData)businessModelModuleRelList.get(j)).getBusinessModuleId());
					moduleObjectMap.put("businessModuleId",businessModuleData.getBusinessModuleId());
					moduleObjectMap.put("businessModuleName",businessModuleData.getName());
					moduleObjectMap.put("businessModuleStatus",businessModuleData.getStatus());
					businessModuleSubBISModuleRelList = profileBLManager.getBISModuleSubBISModuleRelList(moduleObjectMap.get("businessModuleId").toString());
					HashMap subModuleMap = new HashMap();
					
					for(int k=0;k<businessModuleSubBISModuleRelList.size();k++){
						HashMap subModuleObjectMap = new HashMap();
						List subBusinessModuleActionRelList = new ArrayList();
						IActionData actionData = new ActionData();
						subBusinessModuleData = profileBLManager.getSubBISModule(((IBISModuleSubBISModuleRelData)businessModuleSubBISModuleRelList.get(k)).getSubBusinessModuleId());
						subModuleObjectMap.put("subBusinessModuleId",subBusinessModuleData.getSubBusinessModuleId());
						subModuleObjectMap.put("subBusinessModuleName",subBusinessModuleData.getName());
						subModuleObjectMap.put("subBusinessModuleStatus",subBusinessModuleData.getStatus());
						subBusinessModuleActionRelList = profileBLManager.getSubBISModuleActionRelList(subModuleObjectMap.get("subBusinessModuleId").toString());
						HashMap actionMap = new HashMap();
						
						for(int l=0;l<subBusinessModuleActionRelList.size();l++){
							HashMap actionObjectMap = new HashMap();
							actionData = profileBLManager.getActionData(((ISubBISModuleActionRelData)subBusinessModuleActionRelList.get(l)).getActionId());
							actionObjectMap.put("actionId",actionData.getActionId());
							actionObjectMap.put("actionName",actionData.getName());
							actionObjectMap.put("actionStatus",actionData.getStatus());
							actionMap.put(((ISubBISModuleActionRelData)subBusinessModuleActionRelList.get(l)).getActionId(),actionObjectMap);
						}
						subModuleObjectMap.put("actionMap",actionMap);
						subModuleMap.put(((IBISModuleSubBISModuleRelData)businessModuleSubBISModuleRelList.get(k)).getSubBusinessModuleId(),subModuleObjectMap);
					}
					moduleObjectMap.put("subModuleMap",subModuleMap);
					moduleMap.put(((IBISModelModuleRelData)businessModelModuleRelList.get(j)).getBusinessModuleId(),moduleObjectMap);
				}
				modelObjectMap.put("moduleMap",moduleMap);
				modelMap.put(((IBISModelData)businessModelList.get(i)).getBusinessModelId(),modelObjectMap);
			}
			profileMap.put("modelMap",modelMap);
			request.setAttribute("profileMap",profileMap);
			return mapping.findForward(SUCCESS_FORWARD);
		} catch (DataManagerException hExp) {
			hExp.printStackTrace();
			Logger.logError(MODULE,"Error during data Manager operation, reason : "+hExp.getMessage());
		}
		return mapping.findForward(SUCCESS_FORWARD);
	}
}
