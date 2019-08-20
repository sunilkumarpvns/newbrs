package com.elitecore.elitesm.web.core.system.license;


import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.io.FileUtils;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.apache.struts.action.ActionMessage;
import org.apache.struts.action.ActionMessages;
import org.apache.struts.upload.FormFile;

import com.elitecore.commons.base.Maps;
import com.elitecore.commons.base.ObjectDiffer;
import com.elitecore.commons.base.Strings;
import com.elitecore.commons.io.Closeables;
import com.elitecore.elitesm.Version;
import com.elitecore.elitesm.blmanager.core.system.license.SMLicenseBLManager;
import com.elitecore.elitesm.datamanager.DataManagerException;
import com.elitecore.elitesm.datamanager.core.exceptions.auhorization.ActionNotPermitedException;
import com.elitecore.elitesm.datamanager.core.system.staff.data.IStaffData;
import com.elitecore.elitesm.util.EliteUtility;
import com.elitecore.elitesm.util.constants.ConfigConstant;
import com.elitecore.elitesm.util.exception.EliteExceptionUtils;
import com.elitecore.elitesm.util.logger.Logger;
import com.elitecore.elitesm.web.core.base.BaseDispatchAction;
import com.elitecore.elitesm.web.core.system.license.forms.LicenseForm;
import com.elitecore.elitesm.web.core.system.login.forms.SystemLoginForm;
import com.elitecore.license.base.LicenseData;
import com.elitecore.license.base.LicenseManager;
import com.elitecore.license.base.MultiLicenseManager;
import com.elitecore.license.base.commons.LicenseConstants;
import com.elitecore.license.base.exception.InvalidLicenseKeyException;
import com.elitecore.license.publickey.ElitePublickeyGenerator;
import com.elitecore.license.util.SystemUtil;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
/**
 * 
 * @author Tejas.P.Shah
 *
 */
public class ViewLicenceAction  extends BaseDispatchAction {
	
	private static final String LICENSE_FILE = "License File";
	private static final String VIEW_FORWARD = "viewNetserverLicense";
	private static final String UDPATE_SERVER_LICENSE = "updateServerLicense";
	private static final String SUCCESS_FORWARD = "success";	
	private static final String FAILURE_FORWARD ="failure";
	private static final String SUCCESS = "success";
	private static final String VIEW_LICENSE = ConfigConstant.VIEW_LICENSE;
	private static final String DOWNLOAD_KEY = ConfigConstant.DOWNLOAD_KEY;
	private static final String DOWNLOAD_LICENSE = ConfigConstant.DOWNLOAD_LICENSE;
	private static final String UPGRADE_LICENSE = ConfigConstant.UPGRADE_LICENSE;
	private static final String DELETE_LICENSE = ConfigConstant.DELETE_LICENSE;
	
	public ActionForward getLicenceData(ActionMapping mapping, ActionForm form,HttpServletRequest request, HttpServletResponse response) throws Exception {
			
			try {
				checkActionPermission(request, VIEW_LICENSE);
				List<String> fileList = new ArrayList<String>();
				List<LicenseData> lstLicenseData = new ArrayList<LicenseData>();
				LicenseForm licenseForm = (LicenseForm) form;
				String path= EliteUtility.getSMHome() +File.separator+ ConfigConstant.LICENCE_LOCATION;
				File[] listOfFiles = null;
				File folder = new File(path);
				
				
				if(!folder.exists()) {
					boolean isLicenseFolderCreated = false ;
					
					isLicenseFolderCreated = folder.mkdir();
					
					if( isLicenseFolderCreated == false) {
						throw new SecurityException("Failed to create license folder in: " + EliteUtility.getSMHome() + File.separator +" , Reason: Permission denied for creating directory");
					}
					
					listOfFiles = folder.listFiles();
				}else {
					listOfFiles = folder.listFiles();
				}
				
				File tempFile = null;
				if(listOfFiles != null){
					int length = listOfFiles.length;
					for (int i = 0; i < length; i++) {
						if (listOfFiles[i].isFile() && listOfFiles[i].getName().contains(".lic")) {
							fileList.add(listOfFiles[i].getName());
							licenseForm.setLicenseName(listOfFiles[i].getName());
							tempFile = listOfFiles[i].getAbsoluteFile();
						}
					}
				}
				
				if (tempFile != null) {
					String fileData = null;
					fileData = FileUtils.readFileToString(tempFile);
					Map<String, LicenseData> licenseDataMap = null;
					licenseDataMap = SystemUtil.getLicenseInformationMap(fileData);
					
					if (Maps.isNullOrEmpty(licenseDataMap) == false) {
						for (Iterator<String> iterator = licenseDataMap.keySet().iterator(); iterator.hasNext();) {
							String key = iterator.next();
							LicenseData licenseData = licenseDataMap.get(key);
							lstLicenseData.add(licenseData);
						}
					}
					
					licenseForm.setLstLicenseFile(fileList);
					licenseForm.setLicenseData(lstLicenseData);
					
				}
				licenseForm.setLstLicenseFile(fileList);
				licenseForm.setLicenseData(lstLicenseData);
				request.setAttribute("licenseForm", licenseForm);
				if(request.getParameter("param") != null) {
					return mapping.findForward(UDPATE_SERVER_LICENSE);
				} else {
					return mapping.findForward(VIEW_FORWARD);
				}
			} catch(ActionNotPermitedException e){
	            Logger.logError(MODULE,"Error :-" + e.getMessage());
	            printPermitedActionAlias(request);
	            ActionMessages messages = new ActionMessages();
	            messages.add("information", new ActionMessage("general.user.restricted"));
	            saveErrors(request, messages);
	            return mapping.findForward(INVALID_ACCESS_FORWARD);
	        } catch (InvalidLicenseKeyException ile) {
				Logger.logError(MODULE,"Error during license fetching, reason: "+ile.getMessage());
				Logger.logTrace(MODULE,ile);
				Object errorElements[] = EliteExceptionUtils.getFullStackTraceAsArray(ile);
				request.setAttribute("errorDetails", errorElements);
				ActionMessage message = new ActionMessage("license.invalid.key");
				ActionMessages messages = new ActionMessages();
				messages.add("information",message);
				saveErrors(request,messages);
				return mapping.findForward(FAILURE_FORWARD);
			} catch (Exception hExp) {
				Logger.logError(MODULE,"Error during license fetching, reason : "+hExp.getMessage());
				Logger.logTrace(MODULE,hExp);
				Object errorElements[] = EliteExceptionUtils.getFullStackTraceAsArray(hExp);
				request.setAttribute("errorDetails", errorElements);
				ActionMessage message = new ActionMessage("license.view.failure");
				ActionMessages messages = new ActionMessages();
				messages.add("information",message);
				saveErrors(request,messages);
				return mapping.findForward(FAILURE_FORWARD);
			}
	}

	public	ActionForward getExistedLicenseData(ActionMapping mapping,ActionForm form,HttpServletRequest request,HttpServletResponse response) throws Exception {
		getLicenceData(mapping,form,request,response);
		return mapping.findForward(UDPATE_SERVER_LICENSE);
	}
	
	public ActionForward fetchLicense(ActionMapping mapping,ActionForm form,HttpServletRequest request,HttpServletResponse response)  {
		
		try {
			LicenseForm licenseForm = (LicenseForm) form;
			String fileName = request.getParameter("fileName");
			String path= EliteUtility.getSMHome() + File.separator +ConfigConstant.LICENCE_LOCATION + File.separator + fileName;
			File file = new File(path);

			byte[] data = FileUtils.readFileToByteArray(file);
			String fileData = new String(data);
			
			List<LicenseData> lstLicenseData = new ArrayList<LicenseData>();
			Map<String,LicenseData> licenseDataMap = SystemUtil.getLicenseInformationMap(fileData);
			
			if(licenseDataMap!=null){
			
				for (Iterator<String> iterator = licenseDataMap.keySet().iterator(); iterator.hasNext();) {
					String key = iterator.next();
					LicenseData licenseData = licenseDataMap.get(key);
					lstLicenseData.add(licenseData);
				}
				
			}
			List<String> fileList = new ArrayList<String>();
 			licenseForm.setLstLicenseFile(fileList);
			licenseForm.setLicenseData(lstLicenseData);
			licenseForm.setErrorCode("0");
			
			String module = request.getParameter("module");
			if(module != null) {
				request.setAttribute("module", module);
			}
			request.setAttribute("licenseForm", licenseForm);
			request.setAttribute("showData", new String("showData"));
			
			return mapping.findForward(VIEW_FORWARD);
		} catch (Exception e) {
			Logger.logError(MODULE,"Error during license fetching, reason : "+e.getMessage());
			Logger.logTrace(MODULE,e);
			Object errorElements[] = EliteExceptionUtils.getFullStackTraceAsArray(e);
			request.setAttribute("errorDetails", errorElements);
			ActionMessage message = new ActionMessage("license.view.failure");
			ActionMessages messages = new ActionMessages();
			messages.add("information",message);
			saveErrors(request,messages);
			return mapping.findForward(FAILURE_FORWARD);
		}
		
	}
	public ActionForward createLicence(ActionMapping mapping,ActionForm form,HttpServletRequest request,HttpServletResponse response) throws IOException, ActionNotPermitedException {
		
		FileOutputStream outputStream = null;
			try {
				checkActionPermission(request, UPGRADE_LICENSE);
				
				
				String SM_HOME = System.getenv("SM_HOME");
				FormFile file = null;
				List<String> fileList = new ArrayList<String>();
				LicenseForm uploadForm = (LicenseForm) form;
				Logger.logTrace(MODULE, "Entered execute method of " + getClass().getName());
				file = uploadForm.getUpload();
				String path = getServlet().getServletContext().getRealPath("");
				
				
				path = EliteUtility.getSMHome() + File.separator +ConfigConstant.LICENCE_LOCATION;
				File folder = new File(path);
				File[] listOfFiles = folder.listFiles();
				
				int length = listOfFiles.length;
				for (int i = 0; i < length; i++) {
					if (listOfFiles[i].isFile() && listOfFiles[i].getName().contains(".lic")) {
						fileList.add(listOfFiles[i].getName());
					}
				}
				
				byte[] buffer = file.getFileData();
				String fileData = new String(buffer);
				
				List<LicenseData> lstLicenseData = new ArrayList<LicenseData>();
				LicenseManager manager = new MultiLicenseManager();
				manager.add(fileData, Version.getMajorVersion());
				
				Map<String,LicenseData> licenseDataMap = SystemUtil.getLicenseInformationMap(fileData);
				if(Maps.isNullOrEmpty(licenseDataMap) == false){
					
					for (Iterator<String> iterator = licenseDataMap.keySet().iterator(); iterator.hasNext();) {
						String key = iterator.next();
						LicenseData licenseData = licenseDataMap.get(key);
						lstLicenseData.add(licenseData);
					}
					
				}
				if (Strings.isNullOrBlank(SM_HOME) == false) {
					
					String webRootDirectoryPath = SM_HOME + File.separator + "webroot" + File.separator ;
					
					File webRootDirectory = new File(webRootDirectoryPath);
					
					if(webRootDirectory.exists() && webRootDirectory.isDirectory()) {
							
						String licenseFolderPath = webRootDirectoryPath + ConfigConstant.LICENCE_LOCATION;
							
						createLicenseFolderAndWriteLicenseFileInThat(file, webRootDirectoryPath, licenseFolderPath);
							
					} else {
							
						String licenseFolderPath = SM_HOME + File.separator + ConfigConstant.LICENCE_LOCATION;
							
						createLicenseFolderAndWriteLicenseFileInThat(file, SM_HOME, licenseFolderPath);
					 }
					
				}
				
				outputStream = new FileOutputStream(new File(path+File.separator + ConfigConstant.LICENCE_FILE_NAME));
				outputStream.write(file.getFileData());
				
				uploadForm.setLstLicenseFile(fileList);
				uploadForm.setLicenseData(lstLicenseData);
				uploadForm.setErrorCode("0");
				
				request.setAttribute("lstLicenseFiles", fileList);
				request.setAttribute("uploadForm", uploadForm);
				request.setAttribute("responseUrl", "/viewLicenceAction?method=getExistedLicenseData");
				
				IStaffData staffData = getStaffObject(((SystemLoginForm)request.getSession().getAttribute("radiusLoginForm")));
				
				SMLicenseBLManager licenseBLManager = new SMLicenseBLManager();
				staffData.setAuditId(licenseBLManager.getAuditId(ConfigConstant.LICENSE));
				JSONArray jsonArray = getHistoryDifference("create");
				licenseBLManager.doAudit(jsonArray.toString(), staffData);
				
				ActionMessage message = new ActionMessage("license.create.success");
				ActionMessages messages = new ActionMessages();
				messages.add("information",message);
				saveMessages(request, messages);
				return mapping.findForward(SUCCESS_FORWARD);
				
			} catch (ActionNotPermitedException e) {
				Logger.logWarn(MODULE, "Invalid License Data found  " + getClass().getName());
				Logger.logTrace(MODULE, e);
				Object errorElements[] = EliteExceptionUtils.getFullStackTraceAsArray(e);
				request.setAttribute("errorDetails", errorElements);
				ActionMessages messages = new ActionMessages();
				messages.add("information", new ActionMessage("general.user.restricted"));
				saveErrors(request,messages);
				return mapping.findForward(FAILURE_FORWARD);
			} catch (Exception hExp) {
				Logger.logError(MODULE,"Error during license creating, reason : "+hExp.getMessage());
				Logger.logTrace(MODULE,hExp);
				Object errorElements[] = EliteExceptionUtils.getFullStackTraceAsArray(hExp);
				request.setAttribute("errorDetails", errorElements);
				ActionMessage message = new ActionMessage("license.create.failure");
				ActionMessages messages = new ActionMessages();
				messages.add("information",message);
				saveErrors(request,messages);
				return mapping.findForward(FAILURE_FORWARD);
			} finally {
				if (outputStream != null) {
					outputStream.close();
				}
			}	
	}

	private void createLicenseFolderAndWriteLicenseFileInThat(FormFile file, String parentFolderOfLicense,
			String licenseFolderPath) throws IOException {
		
		FileOutputStream licenseFileWriter = null;
		boolean isLicenseFolderCreated = false ;
		
		try {
			
			File licenseFolder = new File(licenseFolderPath);
			
			if(! licenseFolder.exists()) {
				
				isLicenseFolderCreated = licenseFolder.mkdir();
				
				if( ! isLicenseFolderCreated) {
					throw new SecurityException("Failed to create license folder in: " + parentFolderOfLicense +" , Reason: Permission denied for creating directory");
				}
			}
			licenseFileWriter = new FileOutputStream(new File( licenseFolderPath + File.separator + ConfigConstant.LICENCE_FILE_NAME));
			licenseFileWriter.write(file.getFileData());
			licenseFileWriter.flush();
		} finally {
			Closeables.closeQuietly(licenseFileWriter);
		}
		
	}

	public ActionForward downloadPubKey(ActionMapping mapping,ActionForm form,HttpServletRequest request,HttpServletResponse response) throws IOException, ActionNotPermitedException {
		FileOutputStream outputStream = null;
		FileInputStream fileInputStream =  null;
		ServletOutputStream out = null;
			Logger.logTrace(MODULE, "Entered execute method of " + getClass().getName());
			try {
				checkActionPermission(request, DOWNLOAD_KEY);
				int i;   
				String path = EliteUtility.getSMHome();
				String publicKey = request.getContextPath() + ConfigConstant.PUB_KEY;
				publicKey = publicKey.substring(1);
				String realpath = path + File.separator + ConfigConstant.LICENCE_LOCATION + File.separator  + publicKey;
				outputStream = new FileOutputStream(new File(realpath));
				FileUtils.writeStringToFile(new File(realpath), 
						new ElitePublickeyGenerator().generatePublicKey(EliteUtility.getSMHome(), LicenseConstants.DEFAULT_ADDITIONAL_KEY));
				path = EliteUtility.getSMHome() + File.separator +ConfigConstant.LICENCE_LOCATION;
				
				response.setContentType("application/octet-stream");
				response.setHeader("Content-Disposition","attachment;filename="+publicKey);
				
				out = response.getOutputStream();
				fileInputStream = new FileInputStream(realpath);
				
				while ((i=fileInputStream.read()) != -1) {  
					out.write(i);   
				}   
				
			} catch (ActionNotPermitedException e) {
				Logger.logWarn(MODULE, "Invalid License Data found  " + getClass().getName());
				Logger.logTrace(MODULE, e);
				Object errorElements[] = EliteExceptionUtils.getFullStackTraceAsArray(e);
				request.setAttribute("errorDetails", errorElements);
				ActionMessages messages = new ActionMessages();
				messages.add("information", new ActionMessage("general.user.restricted"));
				saveErrors(request,messages);
				return mapping.findForward(FAILURE_FORWARD);
			} catch(Exception e) {
				Logger.logError(MODULE,"Error during license key fetching, reason : "+e.getMessage());
				Logger.logTrace(MODULE,e);
				Object errorElements[] = EliteExceptionUtils.getFullStackTraceAsArray(e);
				request.setAttribute("errorDetails", errorElements);
				ActionMessage message = new ActionMessage("license.download.failure");
				ActionMessages messages = new ActionMessages();
				messages.add("information",message);
				saveErrors(request,messages);
				return mapping.findForward(FAILURE_FORWARD);
			} finally {
				if(fileInputStream != null) {
					fileInputStream.close();
				} if(out != null) {
					out.close();
				}
				if (outputStream != null) {
					outputStream.close();
				}
			}
			return null;	
	}
	
	public ActionForward downloadLicense(ActionMapping mapping,ActionForm form,HttpServletRequest request,HttpServletResponse response) throws IOException, ActionNotPermitedException {
		FileInputStream fileInputStream =  null;
		ServletOutputStream out = null;
			Logger.logTrace(MODULE, "Entered execute method of " + getClass().getName());
			try {
				checkActionPermission(request, DOWNLOAD_LICENSE);
				int i;   
				String path = EliteUtility.getSMHome();
				String fileName = request.getParameter("filename");
				String realpath = path + File.separator + ConfigConstant.LICENCE_LOCATION + File.separator +fileName;
				response.setContentType("application/octet-stream");
				response.setHeader("Content-Disposition","attachment;filename=" + fileName);
				out = response.getOutputStream();
				fileInputStream = new FileInputStream(realpath);  
				while ((i=fileInputStream.read()) != -1) {  
					out.write(i);   
				}   
			} catch (ActionNotPermitedException e) {
				Logger.logWarn(MODULE, "Invalid License Data found  " + getClass().getName());
				Logger.logTrace(MODULE, e);
				Object errorElements[] = EliteExceptionUtils.getFullStackTraceAsArray(e);
				request.setAttribute("errorDetails", errorElements);
				ActionMessages messages = new ActionMessages();
				messages.add("information", new ActionMessage("general.user.restricted"));
				saveErrors(request,messages);
				return mapping.findForward(FAILURE_FORWARD);
			} catch(Exception e) {
				Logger.logError(MODULE,"Error during license downloading, reason : "+e.getMessage());
				Logger.logTrace(MODULE,e);
				Object errorElements[] = EliteExceptionUtils.getFullStackTraceAsArray(e);
				request.setAttribute("errorDetails", errorElements);
				ActionMessage message = new ActionMessage("license.download.failure");
				ActionMessages messages = new ActionMessages();
				messages.add("information",message);
				saveErrors(request,messages);
				return mapping.findForward(FAILURE_FORWARD);
			} finally {
				if(fileInputStream != null) {
					fileInputStream.close();
				} if(out != null) {
					out.close();
				}
			}
			return null;	
	}
	public	ActionForward deleteLicenseFile(ActionMapping mapping,ActionForm form,HttpServletRequest request,HttpServletResponse response) throws ActionNotPermitedException {
		String fileName = request.getParameter("fileName");
			Logger.logTrace(MODULE, "Entered execute method of " + getClass().getName());
			try {
				checkActionPermission(request, DELETE_LICENSE);
				String path= request.getServletContext().getRealPath(ConfigConstant.LICENCE_LOCATION);
				File file = new File(path+ File.separator +fileName);
				if(file.exists()) {
					file.delete();
				}
				String SERVER_HOME = System.getenv("SM_HOME");
				if (Strings.isNullOrBlank(SERVER_HOME) == false) {
					String physicalFolder = SERVER_HOME +"/webroot" +File.separator +ConfigConstant.LICENCE_LOCATION;
					File physicalfile = new File(physicalFolder+ File.separator +fileName);
					if(physicalfile.exists()) {
						physicalfile.delete();
					}
					
				}	
				request.setAttribute("responseUrl", "/viewLicenceAction.do?method=getExistedLicenseData");
				
				IStaffData staffData = getStaffObject(((SystemLoginForm)request.getSession().getAttribute("radiusLoginForm")));
				
				SMLicenseBLManager licenseBLManager = new SMLicenseBLManager();
				staffData.setAuditId(licenseBLManager.getAuditId(ConfigConstant.LICENSE));
				
				JSONArray jsonArray = getHistoryDifference("delete");
				licenseBLManager.doAudit(jsonArray.toString(), staffData);
				
				ActionMessage message = new ActionMessage("license.delete.success");
				ActionMessages messages = new ActionMessages();
				messages.add("information",message);
				saveMessages(request, messages);
				return mapping.findForward(SUCCESS);
			} catch (ActionNotPermitedException e) {
				Logger.logWarn(MODULE, "Invalid License Data found  " + getClass().getName());
				Logger.logTrace(MODULE, e);
				Object []  errorElements= EliteExceptionUtils.getFullStackTraceAsArray(e);
				request.setAttribute("errorDetails", errorElements);
				ActionMessages messages = new ActionMessages();
				messages.add("information", new ActionMessage("general.user.restricted"));
				saveErrors(request,messages);
				return mapping.findForward(FAILURE_FORWARD);
			} catch(Exception e) {
				Logger.logError(MODULE,"Error during license deleting, reason : "+e.getMessage());
				Logger.logTrace(MODULE,e);
				Object errorElements[] = EliteExceptionUtils.getFullStackTraceAsArray(e);
				request.setAttribute("errorDetails", errorElements);
				ActionMessage message = new ActionMessage("license.delete.failure");
				ActionMessages messages = new ActionMessages();
				messages.add("information",message);
				saveErrors(request,messages);
				return mapping.findForward(FAILURE_FORWARD);
			}
	}
	
	private JSONArray getHistoryDifference (String action) throws DataManagerException {
		
		JSONObject oldJsonObject = new JSONObject();
		JSONObject newJsonObject = new JSONObject();
		if("delete".equals(action)){
			oldJsonObject.put(LICENSE_FILE, ConfigConstant.LICENCE_FILE_NAME);
			newJsonObject.put(LICENSE_FILE, ConfigConstant.HYPHEN);
		} else if ("create".equals(action)) {
			oldJsonObject.put(LICENSE_FILE, ConfigConstant.HYPHEN);
			newJsonObject.put(LICENSE_FILE, ConfigConstant.LICENCE_FILE_NAME);
		}
		return  ObjectDiffer.diff(oldJsonObject, newJsonObject);
	}
}