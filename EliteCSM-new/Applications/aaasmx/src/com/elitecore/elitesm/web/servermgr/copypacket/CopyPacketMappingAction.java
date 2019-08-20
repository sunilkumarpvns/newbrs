package com.elitecore.elitesm.web.servermgr.copypacket;

import com.elitecore.elitesm.web.core.base.BaseDispatchAction;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;

import com.elitecore.elitesm.util.logger.Logger;

public class CopyPacketMappingAction extends BaseDispatchAction{
	
	public ActionForward searchCopyPacket(ActionMapping mapping,ActionForm form, HttpServletRequest request,HttpServletResponse response) throws Exception {
		Logger.logError("COPYPACKET****", "search METHOD**************");
		return mapping.findForward("SEARCH");
	}
	
	public ActionForward viewCopyPacketBasicDeatil(ActionMapping mapping,ActionForm form, HttpServletRequest request,HttpServletResponse response) throws Exception {
		Logger.logError("COPYPACKET****", "VIEW METHOD**************");
		return mapping.findForward("VIEWBASIC");
	}
	
	public ActionForward viewCopyPacketMappingConfig(ActionMapping mapping,ActionForm form, HttpServletRequest request,HttpServletResponse response) throws Exception {
		Logger.logError("COPYPACKET****", "VIEW Mapping Config**************");
		return mapping.findForward("VIEWMAPPINGCONFIG");
	}
	
}
