package com.elitecore.netvertexsm.web.sendpacket.radius;

import com.elitecore.commons.logging.LogManager;
import com.elitecore.netvertexsm.util.logger.Logger;
import com.elitecore.netvertexsm.web.core.base.BaseWebAction;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class InitEditRadiusSendPacketAction extends BaseWebAction{
	private static final String FORWARD = "initEditRadiusSendPacket";
	private static final String FAILURE_FORWARD = "failure";
	private static final String MODULE = InitEditRadiusSendPacketAction.class.getSimpleName();

	public ActionForward execute(ActionMapping mapping,ActionForm form,HttpServletRequest request,HttpServletResponse response) throws Exception{
		if(LogManager.getLogger().isInfoLogLevel()){
			Logger.logInfo(MODULE,"Enter execute method of " + getClass().getName());
		}

		return mapping.findForward(FAILURE_FORWARD);
	}

}
