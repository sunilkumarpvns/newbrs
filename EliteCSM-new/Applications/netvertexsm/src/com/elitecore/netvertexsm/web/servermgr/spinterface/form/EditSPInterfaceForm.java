
package com.elitecore.netvertexsm.web.servermgr.spinterface.form;
import org.apache.struts.action.ActionForm;

public class EditSPInterfaceForm extends ActionForm {

	private static final long serialVersionUID = 1L;
	private Long driverInstanceId;
	public Long getDriverInstanceId() {
		return driverInstanceId;
	}
	public void setDriverInstanceId(Long driverInstanceId) {
		this.driverInstanceId = driverInstanceId;
	}
	
}
