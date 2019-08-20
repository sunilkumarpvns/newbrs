package com.elitecore.nvsmx.commons.controller.staff;

import com.elitecore.corenetvertex.sm.acl.StaffProfilePictureData;
import com.elitecore.nvsmx.commons.model.staff.StaffDAO;
import com.elitecore.nvsmx.system.ConfigurationProvider;
import com.elitecore.nvsmx.system.constants.Results;
import com.opensymphony.xwork2.ActionSupport;
import org.apache.struts2.interceptor.ServletRequestAware;

import javax.servlet.http.HttpServletRequest;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;

public class ProfilePictureCTRL extends ActionSupport implements ServletRequestAware{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private static final String DEFAULT_IMAGE_PATH = File.separator + "images" + File.separator + "defaultProfilePicture.jpg";
	private HttpServletRequest request;
	private InputStream inputStream;
	
	public InputStream getInputStream() {
		return inputStream;
	}

	@Override
	public void setServletRequest(HttpServletRequest request) {
		this.request = request;
		
	}

	@Override
	public String execute() throws Exception {
		String staffId = request.getParameter("staffId");
		if(staffId == null){
			setDefaultpicture();

		}
			StaffProfilePictureData staffProfilePicture =  StaffDAO.getStaffProfilePictureBy(staffId);
		//To do need to get profile picture from StaffProfilePictureData
		if(staffProfilePicture == null || staffProfilePicture.getProfilePicture() == null){
			setDefaultpicture();
		} else {
			inputStream = new ByteArrayInputStream( staffProfilePicture.getProfilePicture() );
		}
		 
		return Results.PROFILE_PICTURE.getValue();
	}

	private void setDefaultpicture() throws FileNotFoundException {
		String systemInfo = ConfigurationProvider.getInstance().getDeploymentPath();
		File defaultProfilePic = new File(systemInfo + DEFAULT_IMAGE_PATH);
		FileInputStream fis = new FileInputStream(defaultProfilePic);
		inputStream = fis;
	}
}