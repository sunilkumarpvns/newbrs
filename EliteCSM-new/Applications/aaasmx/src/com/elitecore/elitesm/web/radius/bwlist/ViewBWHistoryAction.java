package com.elitecore.elitesm.web.radius.bwlist;

import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;

import com.elitecore.commons.base.Strings;
import com.elitecore.elitesm.blmanager.history.HistoryBLManager;
import com.elitecore.elitesm.blmanager.radius.bwlist.BWListBLManager;
import com.elitecore.elitesm.datamanager.core.system.staff.data.IStaffData;
import com.elitecore.elitesm.datamanager.radius.bwlist.data.BWListData;
import com.elitecore.elitesm.util.constants.ConfigConstant;
import com.elitecore.elitesm.util.exception.EliteExceptionUtils;
import com.elitecore.elitesm.util.logger.Logger;
import com.elitecore.elitesm.web.core.base.BaseWebAction;
import com.elitecore.elitesm.web.core.system.login.forms.SystemLoginForm;
import com.elitecore.elitesm.web.history.DatabaseHistoryData;
import com.elitecore.elitesm.web.radius.bwlist.forms.CreateBWListForm;

public class ViewBWHistoryAction extends BaseWebAction {
	private static final String VIEW_FORWARD = "viewBWHistoryList";
	private static final String FAILURE_FORWARD = "failure";
	private static final String MODULE = "ViewBWHistoryAction";
	
	public ActionForward execute(ActionMapping mapping,ActionForm form,HttpServletRequest request,HttpServletResponse response) throws Exception{
			Logger.logTrace(MODULE,"Enter execute method of"+getClass().getName());
			try{
				CreateBWListForm bwListForm = (CreateBWListForm)form;
				BWListBLManager blManager = new BWListBLManager();
				String strBwID = request.getParameter("bwId");
				String bwId;
				IStaffData staffData = getStaffObject(((SystemLoginForm)request.getSession().getAttribute("radiusLoginForm")));
				
				if(strBwID != null){
					bwId = strBwID;
				}else{
					bwId=bwListForm.getBwId();
				}
				
				if(Strings.isNullOrBlank(bwId) == false){
					BWListData bwListData = blManager.getBWListData(bwId);
					
					HistoryBLManager historyBlManager= new HistoryBLManager();

					String strAuditUid = request.getParameter("auditUid");
					String strSytemAuditId=request.getParameter("systemAuditId");
					
					if(strSytemAuditId != null){
						request.setAttribute("systemAuditId", strSytemAuditId);
					}
					
					if(Strings.isNullOrEmpty(strAuditUid) == false){
						
						staffData.setAuditName(ConfigConstant.BLACKLIST_CANDIDATES_LABEL);
						staffData.setAuditId(bwListData.getAuditUid());
						
						List<DatabaseHistoryData> lstDatabaseDSHistoryDatas=historyBlManager.getHistoryData(strAuditUid);
						
						request.setAttribute("name", ConfigConstant.BLACKLIST_CANDIDATES_LABEL);
						request.setAttribute("lstDatabaseDSHistoryDatas", lstDatabaseDSHistoryDatas);
					}
					
					request.setAttribute("bwListData",bwListData);
				}
				return mapping.findForward(VIEW_FORWARD);
			}catch(Exception e){
				e.printStackTrace();
				Logger.logTrace(MODULE,e.getMessage());
				Object errorElements[] = EliteExceptionUtils.getFullStackTraceAsArray(e);
				request.setAttribute("errorDetails", errorElements);
			}
			return mapping.findForward(FAILURE_FORWARD);
		}
}
