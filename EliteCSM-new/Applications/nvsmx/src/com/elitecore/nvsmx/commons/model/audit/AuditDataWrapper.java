package com.elitecore.nvsmx.commons.model.audit;

import java.io.Serializable;

import com.elitecore.corenetvertex.sm.audit.AuditData;
import com.elitecore.nvsmx.system.util.NVSMXUtil;

/**
 * Customized the AuditData
 * @author Dhyani.Raval
 *
 */
public class AuditDataWrapper implements Serializable {

	private static final long serialVersionUID = 1L;
	private String id;
	private String resourceName;
	private String message;
	private String staffUserName;
	private String clientIp;
	private String auditDate;

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getResourceName() {
		return resourceName;
	}

	public void setResourceName(String resourceName) {
		this.resourceName = resourceName;
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

	public String getStaffUserName() {
		return staffUserName;
	}

	public void setStaffUserName(String staffUserName) {
		this.staffUserName = staffUserName;
	}

	public String getClientIp() {
		return clientIp;
	}

	public void setClientIp(String clientIp) {
		this.clientIp = clientIp;
	}

	public String getAuditDate() {
		return auditDate;
	}

	public void setAuditDate(String auditDate) {
		this.auditDate = auditDate;
	}

	public static class AuditDataWrapperBuilder {
		private AuditDataWrapper auditDataWrapper;

		public AuditDataWrapperBuilder() {
			auditDataWrapper = new AuditDataWrapper();
		}

		public AuditDataWrapper build() {
			return auditDataWrapper;
		}

		public AuditDataWrapperBuilder withAuditDatas(AuditData auditData) {
			auditDataWrapper.id = auditData.getId();
			auditDataWrapper.auditDate = NVSMXUtil.simpleDateFormatPool.get().format(auditData.getAuditDate());
			auditDataWrapper.clientIp = auditData.getClientIp();
			auditDataWrapper.resourceName = auditData.getAuditableResourceName();//.getAuditableResource().getName();
			auditDataWrapper.message = auditData.getMessage();
			auditDataWrapper.staffUserName = auditData.getStaffData().getUserName();
			return this;
		}
	}
	
}
