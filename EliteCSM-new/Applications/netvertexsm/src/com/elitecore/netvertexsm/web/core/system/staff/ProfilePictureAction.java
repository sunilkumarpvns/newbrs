package com.elitecore.netvertexsm.web.core.system.staff;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.elitecore.commons.base.Strings;
import com.elitecore.corenetvertex.sm.acl.StaffProfilePictureData;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionMapping;
import org.apache.struts.actions.DownloadAction;

import com.elitecore.netvertexsm.blmanager.core.system.staff.StaffBLManager;
import com.elitecore.netvertexsm.datamanager.core.system.staff.data.IStaffData;
import com.elitecore.netvertexsm.util.EliteUtility;

public class ProfilePictureAction extends DownloadAction {

	@Override
	protected StreamInfo getStreamInfo(ActionMapping mapping, ActionForm form,
			HttpServletRequest request, HttpServletResponse response)
			throws Exception {

		String staffId = request.getParameter("staffId");
		if(Strings.isNullOrEmpty(staffId)==true){
			return null;
		}
		StaffBLManager blManager = new StaffBLManager();
		StaffProfilePictureData staffProfilePicture = blManager.getStaffProfilePicture(Long.parseLong(staffId));
		if (staffProfilePicture == null || staffProfilePicture.getProfilePicture() == null) {
			String defaultImagePath = EliteUtility.getSMHome() + File.separator + "images" + File.separator + "defaultProfilePicture.jpg";
			File defaultProfilePic = new File(defaultImagePath);
			InputStream fis = new FileInputStream(defaultProfilePic);
			return new CustomStreamInfo(fis);
		}
		InputStream bis = new ByteArrayInputStream(staffProfilePicture.getProfilePicture());
		return new CustomStreamInfo(bis);
	}

	private class CustomStreamInfo implements StreamInfo {

		private final String contentType = "image/jpeg";
		private InputStream inputStream;

		public CustomStreamInfo(InputStream inputStream) {
			this.inputStream = inputStream;
		}

		public String getContentType() {
			return contentType;
		}

		public InputStream getInputStream() throws IOException {
			return inputStream;
		}
	}
}
