package com.elitecore.elitesm.web.core.system.license.forms;

import java.util.List;

import org.apache.struts.upload.FormFile;

import com.elitecore.elitesm.web.core.base.forms.BaseWebForm;
import com.elitecore.license.base.LicenseData;
import com.elitecore.license.nfv.PresentableLicenseData;

public class LicenseForm extends BaseWebForm {

	private static final long serialVersionUID = 1L;
	private FormFile upload;
	private List<String> lstLicenseFile = new java.util.ArrayList<String>();
	private List<LicenseData> licenseData;
	private String errorCode;
	private List<ServerInstanceStatus> smLicenceData;
	private List<PresentableLicenseData> presentableLicenseDatas;
	private String instanceName;
	private String status;
	private String licenseName;
	private Long id;
	
	public List<String> getLstLicenseFile() {
		return lstLicenseFile;
	}

	public void setLstLicenseFile(List<String> lstLicenseFile) {
		this.lstLicenseFile = lstLicenseFile;
	}

	public FormFile getUpload() {
		return upload;
	}

	public void setUpload(FormFile upload) {
		this.upload = upload;
	}

	public List<LicenseData> getLicenseData() {
		return licenseData;
	}

	public void setLicenseData(List<LicenseData> licenseData) {
		this.licenseData = licenseData;
	}

	public String getErrorCode() {
		return errorCode;
	}

	public void setErrorCode(String errorCode) {
		this.errorCode = errorCode;
	}

	public List<ServerInstanceStatus> getSmLicenceData() {
		return smLicenceData;
	}

	public void setSmLicenceData(List<ServerInstanceStatus> smLicenceData) {
		this.smLicenceData = smLicenceData;
	}

	public String getInstanceName() {
		return instanceName;
	}

	public void setInstanceName(String instanceName) {
		this.instanceName = instanceName;
	}

	public String getLicenseName() {
		return licenseName;
	}

	public void setLicenseName(String licenseName) {
		this.licenseName = licenseName;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public List<PresentableLicenseData> getPresentableLicenseDatas() {
		return presentableLicenseDatas;
	}

	public void setPresentableLicenseDatas(List<PresentableLicenseData> presentableLicenseDatas) {
		this.presentableLicenseDatas = presentableLicenseDatas;
	}

	
}