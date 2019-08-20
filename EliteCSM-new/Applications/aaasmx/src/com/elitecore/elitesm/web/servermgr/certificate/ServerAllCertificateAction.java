package com.elitecore.elitesm.web.servermgr.certificate;

import java.io.BufferedInputStream;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.security.InvalidKeyException;
import java.security.KeyFactory;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.SignatureException;
import java.security.cert.CRL;
import java.security.cert.CRLException;
import java.security.cert.Certificate;
import java.security.cert.CertificateException;
import java.security.cert.CertificateFactory;
import java.security.cert.X509CRLEntry;
import java.security.cert.X509Certificate;
import java.security.spec.PKCS8EncodedKeySpec;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.crypto.Cipher;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.apache.struts.action.ActionMessage;
import org.apache.struts.action.ActionMessages;
import org.apache.struts.upload.FormFile;
import org.hibernate.exception.ConstraintViolationException;

import sun.security.x509.X509CRLImpl;

import com.elitecore.commons.base.Strings;
import com.elitecore.core.commons.tls.constant.PrivateKeyAlgo;
import com.elitecore.elitesm.blmanager.core.base.GenericBLManager;
import com.elitecore.elitesm.blmanager.servermgr.certificate.CrlCertificateBLManager;
import com.elitecore.elitesm.blmanager.servermgr.certificate.ServerCertificateBLManager;
import com.elitecore.elitesm.blmanager.servermgr.certificate.TrustedCertificateBLManager;
import com.elitecore.elitesm.datamanager.DataManagerException;
import com.elitecore.elitesm.datamanager.core.exceptions.auhorization.ActionNotPermitedException;
import com.elitecore.elitesm.datamanager.core.system.staff.data.IStaffData;
import com.elitecore.elitesm.datamanager.core.util.PageList;
import com.elitecore.elitesm.datamanager.servermgr.certificate.data.CrlCertificateData;
import com.elitecore.elitesm.datamanager.servermgr.certificate.data.ServerCertificateData;
import com.elitecore.elitesm.datamanager.servermgr.certificate.data.TrustedCertificateData;
import com.elitecore.elitesm.util.constants.CertificateRemarks;
import com.elitecore.elitesm.util.constants.ConfigConstant;
import com.elitecore.elitesm.util.exception.CertificateValidationException;
import com.elitecore.elitesm.util.exception.EliteExceptionUtils;
import com.elitecore.elitesm.util.exception.InvalidPrivateKeyException;
import com.elitecore.elitesm.util.logger.Logger;
import com.elitecore.elitesm.web.core.base.BaseDispatchAction;
import com.elitecore.elitesm.web.core.system.login.forms.SystemLoginForm;
import com.elitecore.elitesm.web.servermgr.certificate.forms.ServerCertificateForm;
import com.elitecore.passwordutil.PasswordEncryption;

public class ServerAllCertificateAction extends BaseDispatchAction {

	private static final String MODULE = "SERVER_CERTI";
	
	//ACL for Server Certificate
	private static final String CREATE_ACTION_ALIAS = ConfigConstant.CREATE_SERVER_CERTIFICATE;
	private static final String DELETE_ACTION_ALIAS = ConfigConstant.DELETE_SERVER_CERTIFICATE;
	private static final String SEARCH_ACTION_ALIAS = ConfigConstant.SEARCH_SERVER_CERTIFICATE;
	
	//ACL for Trusted Certificate
	private static final String CREATE_TRUSTED_ACTION_ALIAS = ConfigConstant.CREATE_TRUSTED_CERTIFICATE;
	private static final String DELETE_TRUSTED_ACTION_ALIAS = ConfigConstant.DELETE_TRUSTED_CERTIFICATE;

	//ACL for CRL Certificate
	private static final String CREATE_CRL_ACTION_ALIAS = ConfigConstant.CREATE_CERTIFICATE_REVOCATION_LIST;
	private static final String DELETE_CRL_ACTION_ALIAS = ConfigConstant.DELETE_CERTIFICATE_REVOCATION_LIST;
	
	private static final String SUCCESS_FORWARD = "success";
	private static final String FAILURE_FORWARD = "failure";
	
	private static final String SEARCH_FORWARD = "searchAllServerCertificate";
	private static final String CREATE_FORWARD = "createServerCertificate";
	private static final String VIEW_FORWARD = "viewServerCertificate";
	private static final String SHOWALL_FORWARD = "showAllServerCertificate";
	
	private static final String TRUSTED_CREATE_FORWARD = "createTrustedCertificate";
	private static final String SHOWALLTRUSTED_FORWARD = "showAllTrustedCertificate";
	private static final String VIEWTRUSTED_FORWARD = "viewTrustedCertificate";
	
	private static final String CREATECRL_FORWARD = "createCrlCertificate";
	private static final String SHOWALLCRL_FORWARD = "showAllCrlCertificate";
	private static final String VIEWCRL_FORWARD = "viewCrlCertificate";
	
	
	public ActionForward initSearch(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
		Logger.logInfo(MODULE, "Entered initSearch method of " + getClass().getName());
		return searchFun(mapping, form, request, response);
	}

	/**
	 * returns all the data when search-string is null and return searched data when search-string is set. 
	 * @param mapping
	 * @param form
	 * @param request
	 * @param response
	 * @return
	 * @throws Exception
	 */
	private ActionForward searchFun(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {

		try {
			
			// BEGIN : SERVER CERTIFICATE 
			ServerCertificateData serverCertificateData = new ServerCertificateData();
			ServerCertificateForm serverCertificateForm = (ServerCertificateForm) form;
			String strCertificateName = serverCertificateForm.getServerCertificateName();

			if (strCertificateName != null) {
				serverCertificateData.setServerCertificateName(strCertificateName);
			} else {
				serverCertificateData.setServerCertificateName("");
			}

			IStaffData staffData = getStaffObject(((SystemLoginForm) request.getSession().getAttribute("radiusLoginForm")));

			ServerCertificateBLManager blManager = new ServerCertificateBLManager();
			PageList pageList = blManager.search(serverCertificateData, staffData);		

			List<ServerCertificateData> list=pageList.getListData();
			for(ServerCertificateData data : list){
				setServerCertificateDetail(data);
			}

			serverCertificateForm.setServerCertificateName(strCertificateName);
			serverCertificateForm.setPageNumber(pageList.getCurrentPage());
			serverCertificateForm.setCrlTotalPages(pageList.getTotalPages());
			serverCertificateForm.setTotalRecords(pageList.getTotalItems());
			serverCertificateForm.setListServerCertificate(pageList.getListData());
			serverCertificateForm.setServerCertificateList(pageList.getCollectionData());
			// END : SERVER CERTIFICATE 
			
			
			// START : TRUSTED CERTIFICATE 
			TrustedCertificateData trustedCertificateData=new TrustedCertificateData();
			String strTrustedCertificateName=serverCertificateForm.getServerCertificateName();

			if(strTrustedCertificateName!=null){
				trustedCertificateData.setTrustedCertificateName(strTrustedCertificateName);
			}else{
				trustedCertificateData.setTrustedCertificateName("");
			}

			TrustedCertificateBLManager trustedBLManager=new TrustedCertificateBLManager();

			pageList=trustedBLManager.search(trustedCertificateData, staffData);
			List<TrustedCertificateData> trustedList=pageList.getListData();
			for(TrustedCertificateData data : trustedList){
				setTrustedCertificateDetail(data);
			}

			serverCertificateForm.setTrustedCertificateName(strTrustedCertificateName);
			serverCertificateForm.setTrustedPageNumber(pageList.getCurrentPage());
			serverCertificateForm.setTrustedTotalPages(pageList.getTotalPages());
			serverCertificateForm.setTrustedtotalRecords(pageList.getTotalItems());
			serverCertificateForm.setListTrustedCertificate(pageList.getListData());
			serverCertificateForm.setTrustedCertificateList(pageList.getCollectionData());
			// END : TRUSTED CERTIFICATE 
			
			//START  : CRL
			CrlCertificateData crlCertificateData=new CrlCertificateData();
			String strCrlCertificateName=serverCertificateForm.getServerCertificateName();

			if(strCrlCertificateName!=null){
				crlCertificateData.setCrlCertificateName(strCrlCertificateName);
			}else{
				crlCertificateData.setCrlCertificateName("");
			}

			CrlCertificateBLManager crlBLManager=new CrlCertificateBLManager();

			pageList=crlBLManager.search(crlCertificateData, staffData);

			List<CrlCertificateData> crlList=pageList.getListData();

			for(CrlCertificateData data : crlList){
				if (data.getCrlCertificate() != null) {
					setCrlCertificateDetail(data);
				}
			}

			serverCertificateForm.setCrlCertificateName(strCrlCertificateName);
			serverCertificateForm.setCrlPageNumber(pageList.getCurrentPage());
			serverCertificateForm.setCrlTotalPages(pageList.getTotalPages());
			serverCertificateForm.setCrlTotalRecords(pageList.getTotalItems());
			serverCertificateForm.setListCrlCertificate(pageList.getListData());
			serverCertificateForm.setCrlCertificateList(pageList.getCollectionData());
			//END  : CRL
			
			request.setAttribute("serverCertificateForm", serverCertificateForm);
			return mapping.findForward(SEARCH_FORWARD);

		} catch (Exception exp) {
			Logger.logError(MODULE,"Error during Data Manager operation , reason : "+ exp.getMessage());
			Object errorElements[] = EliteExceptionUtils.getFullStackTraceAsArray(exp);
			request.setAttribute("errorDetails", errorElements);
			ActionMessage message = new ActionMessage("servermgr.servercertificate.search.failure");
			ActionMessages messages = new ActionMessages();
			messages.add("information", message);
			saveErrors(request, messages);
		}			

		return mapping.findForward(FAILURE_FORWARD);
	}

	public ActionForward search(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response)throws Exception {
		Logger.logTrace(MODULE, "Enter search method of "+ getClass().getName());
		try {
			checkActionPermission(request, SEARCH_ACTION_ALIAS);
			return searchFun(mapping, form, request, response);
		} catch (ActionNotPermitedException e) {
			Logger.logError(MODULE, "Error during Data Manager operation ");
			Object errorElements[] = EliteExceptionUtils.getFullStackTraceAsArray(e);
			request.setAttribute("errorDetails", errorElements);
			ActionMessage message = new ActionMessage("general.user.restricted");
			ActionMessages messages = new ActionMessages();
			messages.add("information", message);
			saveErrors(request, messages);
		}

		return mapping.findForward(FAILURE_FORWARD);
	}

	public ActionForward initCreate(ActionMapping mapping, ActionForm form,HttpServletRequest request, HttpServletResponse response)throws Exception {
		Logger.logInfo(MODULE, "Entered initCreate method of "+ getClass().getName());
		try {
			checkActionPermission(request, CREATE_ACTION_ALIAS);
			return mapping.findForward(CREATE_FORWARD);
		} catch (ActionNotPermitedException e) {
			Logger.logError(MODULE, "Error during Data Manager operation ");
			Object errorElements[] = EliteExceptionUtils.getFullStackTraceAsArray(e);
			request.setAttribute("errorDetails", errorElements);
			ActionMessage message = new ActionMessage("general.user.restricted");
			ActionMessages messages = new ActionMessages();
			messages.add("information", message);
			saveErrors(request, messages);
		} catch (Exception e) {
			Logger.logError(MODULE,"Error during Data Manager operation , reason : "+ e.getMessage());
			Object errorElements[] = EliteExceptionUtils.getFullStackTraceAsArray(e);
			request.setAttribute("errorDetails", errorElements);
			ActionMessage message = new ActionMessage("general.error");
			ActionMessages messages = new ActionMessages();
			messages.add("information", message);
			saveErrors(request, messages);
		}
		return mapping.findForward(FAILURE_FORWARD);
	}

	public ActionForward create(ActionMapping mapping, ActionForm form,HttpServletRequest request, HttpServletResponse response)throws Exception {
		Logger.logInfo(MODULE, "Entered create method of "+ getClass().getName());
		try {
			checkActionPermission(request, CREATE_ACTION_ALIAS);
			ServerCertificateForm serverCertificateForm = (ServerCertificateForm) form;
			ServerCertificateData serverCertificateData = new ServerCertificateData();

			//generate certificate
			Collection<? extends Certificate> certs=generateCertificate(serverCertificateForm.getPublicCert().getInputStream());

			//generate private key
			PrivateKey privateKey=generatePrivateKey(serverCertificateForm.getPrivateKey().getInputStream(), PrivateKeyAlgo.fromAlgoName(serverCertificateForm.getPrivateKeyAlgorithm()), serverCertificateForm.getPrivateKeyPassword());

			//validation
			Certificate publicCert=null;
			for(Certificate cert:certs){
				publicCert=cert;
				break;
			}
			checkForPrivateKeyAndPublicKey(publicCert,privateKey,PrivateKeyAlgo.fromAlgoName(serverCertificateForm.getPrivateKeyAlgorithm()));

			convertFormToBean(serverCertificateForm, serverCertificateData);

			/* Encrypt server password */
			String encryptedPassword = PasswordEncryption.getInstance().crypt(serverCertificateData.getPrivateKeyPassword(),PasswordEncryption.ELITE_PASSWORD_CRYPT);
			serverCertificateData.setPrivateKeyPassword(encryptedPassword);
			
			String currentUser = ((SystemLoginForm) request.getSession().getAttribute("radiusLoginForm")).getUserId();
			serverCertificateData.setCreatedByStaffId(currentUser);
			serverCertificateData.setModifiedByStaffId(currentUser);

			IStaffData staffData = getStaffObject(((SystemLoginForm) request.getSession().getAttribute("radiusLoginForm")));
			ServerCertificateBLManager blManager = new ServerCertificateBLManager();
			
			blManager.createServerCertificate(serverCertificateData, staffData);

			request.setAttribute("responseUrl","/serverAllCertificates.do?method=initSearch");
			ActionMessage message = new ActionMessage("servermgr.servercertificate.create");
			ActionMessages messages = new ActionMessages();
			messages.add("information", message);
			saveMessages(request, messages);
			return mapping.findForward(SUCCESS_FORWARD);

		}catch(InvalidPrivateKeyException e){			
			Logger.logError(MODULE,"Error during Data Manager operation , reason : "+ e.getMessage());
			Object errorElements[] = EliteExceptionUtils.getFullStackTraceAsArray(e);
			request.setAttribute("errorDetails", errorElements);
			ActionMessage message = new ActionMessage("servermgr.servercertificate.create.invalidkey");
			ActionMessages messages = new ActionMessages();
			messages.add("information", message);
			saveErrors(request, messages);		

		} catch (CertificateValidationException exp) {
			Logger.logError(MODULE,"Error during Data Manager operation , reason : "+ exp.getMessage());
			Object errorElements[] = EliteExceptionUtils.getFullStackTraceAsArray(exp);
			request.setAttribute("errorDetails", errorElements);
			ActionMessage message = new ActionMessage("servermgr.servercertificate.create.keycertificatemismatch");
			ActionMessages messages = new ActionMessages();
			messages.add("information", message);
			saveErrors(request, messages);

		} catch (CertificateException exp) {
			Logger.logError(MODULE,"Error during Data Manager operation , reason : "+ exp.getMessage());
			Object errorElements[] = EliteExceptionUtils.getFullStackTraceAsArray(exp);
			request.setAttribute("errorDetails", errorElements);
			ActionMessage message = new ActionMessage("servermgr.servercertificate.create.invalidcertificate");
			ActionMessages messages = new ActionMessages();
			messages.add("information", message);
			saveErrors(request, messages);

		}catch (ActionNotPermitedException e) {
			Logger.logError(MODULE, "Error :-" + e.getMessage());
			printPermitedActionAlias(request);
			ActionMessages messages = new ActionMessages();
			messages.add("information", new ActionMessage("general.user.restricted"));
			saveErrors(request, messages);
		} catch (Exception exp) {
			Logger.logError(MODULE,"Error during Data Manager operation , reason : "+ exp.getMessage());
			Object errorElements[] = EliteExceptionUtils.getFullStackTraceAsArray(exp);
			request.setAttribute("errorDetails", errorElements);
			ActionMessage message = new ActionMessage("servermgr.servercertificate.create.failure");
			ActionMessages messages = new ActionMessages();
			messages.add("information", message);
			saveErrors(request, messages);
		}
		return mapping.findForward(FAILURE_FORWARD);
	}

	public ActionForward deleteCertificate(ActionMapping mapping,ActionForm form, HttpServletRequest request,HttpServletResponse response) throws Exception {
		Logger.logInfo(MODULE, "Entered delete method of "+ getClass().getName());
		try {
			checkActionPermission(request, DELETE_ACTION_ALIAS);
			String[] strSelectedIds = request.getParameterValues("selectCertificate");
			IStaffData staffData = getStaffObject(((SystemLoginForm) request.getSession().getAttribute("radiusLoginForm")));
			ServerCertificateBLManager blManager = new ServerCertificateBLManager();
			blManager.deleteServerCertificateById(Arrays.asList(strSelectedIds), staffData);
			int strSelectedIdsLen = strSelectedIds.length;
			ServerCertificateForm serverCertificateForm = (ServerCertificateForm) form;
			long currentPageNumber = getCurrentPageNumberAfterDel(strSelectedIdsLen, serverCertificateForm.getPageNumber(),serverCertificateForm.getTotalPages(),serverCertificateForm.getTotalRecords());

			request.setAttribute("responseUrl","/serverAllCertificates.do?method=initSearch&pageNumber="+ currentPageNumber);
			ActionMessage message = new ActionMessage("servermgr.servercertificate.delete");
			ActionMessages messages1 = new ActionMessages();
			messages1.add("information", message);
			saveMessages(request, messages1);
			return mapping.findForward(SUCCESS_FORWARD);
		} catch (ActionNotPermitedException e) {
			Logger.logError(MODULE, "Error during Data Manager operation ");
			Object errorElements[] = EliteExceptionUtils.getFullStackTraceAsArray(e);
			request.setAttribute("errorDetails", errorElements);
			ActionMessage message = new ActionMessage("general.user.restricted");
			ActionMessages messages = new ActionMessages();
			messages.add("information", message);
			saveErrors(request, messages);
		} catch (Exception exp) {
			Logger.logError(MODULE,"Error during Data Manager operation , reason : "+ exp.getMessage());
			Object errorElements[] = EliteExceptionUtils.getFullStackTraceAsArray(exp);
			request.setAttribute("errorDetails", errorElements);
			ActionMessage message = new ActionMessage("servermgr.servercertificate.delete.failure");
			ActionMessages messages = new ActionMessages();
			messages.add("information", message);
			saveErrors(request, messages);
		}
		return mapping.findForward(FAILURE_FORWARD);
	}

	public ActionForward view(ActionMapping mapping, ActionForm form,HttpServletRequest request, HttpServletResponse response)throws Exception {
		Logger.logTrace(MODULE, "Enter view method of" + getClass().getName());
		try {
			ServerCertificateBLManager blManager = new ServerCertificateBLManager();

			String serverCertificateId = "";
			if (Strings.isNullOrBlank(request.getParameter("serverCertificateId")) == false) {
				serverCertificateId = request.getParameter("serverCertificateId");
			}
			ServerCertificateData serverCertificateData = blManager.view(serverCertificateId);
			if (serverCertificateData.getCertificate() != null) {
				setServerCertificateDetail(serverCertificateData);
			}
			if (serverCertificateData.getPrivateKey() != null) {
				setPrivateKeyDetail(serverCertificateData);
			}

			request.setAttribute("serverCertificateData", serverCertificateData);
		} catch (ActionNotPermitedException e) {
			Logger.logError(MODULE, "Restricted to do action.");
			Object errorElements[] = EliteExceptionUtils.getFullStackTraceAsArray(e);
			request.setAttribute("errorDetails", errorElements);
			ActionMessage message = new ActionMessage("general.user.restricted");
			ActionMessages messages = new ActionMessages();
			messages.add("information", message);
			saveErrors(request, messages);
			return mapping.findForward(FAILURE_FORWARD);
		} catch (DataManagerException managerExp) {
			Logger.logError(MODULE,"Error during Data Manager operation , reason : "+ managerExp.getMessage());
			Object errorElements[] = EliteExceptionUtils.getFullStackTraceAsArray(managerExp);
			request.setAttribute("errorDetails", errorElements);
			ActionMessage message = new ActionMessage("general.error");
			ActionMessages messages = new ActionMessages();
			messages.add("information", message);
			saveErrors(request, messages);
			return mapping.findForward(FAILURE_FORWARD);
		}
		return mapping.findForward(VIEW_FORWARD);
	}

	public ActionForward showAll(ActionMapping mapping, ActionForm form,HttpServletRequest request, HttpServletResponse response)throws Exception {
		Logger.logTrace(MODULE, "Enter showAll method of"+ getClass().getName());
		try {
			ServerCertificateForm serverCertificateForm = (ServerCertificateForm) form;
			GenericBLManager genericBLManager = new GenericBLManager();
			PageList pageList = genericBLManager.getAllRecords(ServerCertificateData.class, "serverCertificateName", true);
			serverCertificateForm.setPageNumber(pageList.getCurrentPage());
			serverCertificateForm.setPageNumber(pageList.getCurrentPage());
			serverCertificateForm.setCrlTotalPages(pageList.getTotalPages());
			serverCertificateForm.setTotalRecords(pageList.getTotalItems());
			serverCertificateForm.setListServerCertificate(pageList.getListData());

			List<ServerCertificateData> list=pageList.getListData();
			for(ServerCertificateData data : list){
				setServerCertificateDetail(data);
			}

			request.setAttribute("showAllServerCertificateForm",serverCertificateForm);
			return mapping.findForward(SHOWALL_FORWARD);

		} catch (ConstraintViolationException e) {
			Logger.logError(MODULE, "Returning error forward from "+ getClass().getName());
			Object errorElements[] = EliteExceptionUtils.getFullStackTraceAsArray(e);
			request.setAttribute("errorDetails", errorElements);
			ActionMessage message = new ActionMessage("servermgr.servercertificate.alreadyinuse");
			ActionMessages messages = new ActionMessages();
			messages.add("information", message);
			saveErrors(request, messages);
			return mapping.findForward(FAILURE_FORWARD);
		} catch (ActionNotPermitedException e) {
			Logger.logError(MODULE, "Error during Data Manager operation ");
			Object errorElements[] = EliteExceptionUtils.getFullStackTraceAsArray(e);
			request.setAttribute("errorDetails", errorElements);
			ActionMessages messages = new ActionMessages();
			ActionMessage message = new ActionMessage("general.user.restricted");
			messages.add("information", message);
			saveErrors(request, messages);
		} catch (Exception managerExp) {
			Logger.logError(MODULE,"Error during Data Manager operation , reason : "+ managerExp.getMessage());
			Object errorElements[] = EliteExceptionUtils.getFullStackTraceAsArray(managerExp);
			request.setAttribute("errorDetails", errorElements);
		}
		return mapping.findForward(FAILURE_FORWARD);
	}

	public ActionForward viewCertificate(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
		Logger.logTrace(MODULE, "Enter viewCertificate method of" + getClass().getName());
		try {
			ServerCertificateForm serverCertificateForm = (ServerCertificateForm) form;
			FormFile newFile = serverCertificateForm.getPublicCert();

			InputStream fis = newFile.getInputStream();
			CertificateFactory cf = CertificateFactory.getInstance("X509");
			ArrayList<Certificate> certList = (ArrayList<Certificate>) cf.generateCertificates(fis);
			ArrayList<X509Certificate> x509List = new ArrayList<X509Certificate>();

			for (Certificate cert : certList) {
				X509Certificate x509 = (X509Certificate) cert;
				x509.checkValidity();
				x509List.add((X509Certificate) cert);
			}
			return mapping.findForward(CREATE_FORWARD);
		} catch (Exception exp) {
			Logger.logError(MODULE,"Error during Data Manager operation , reason : "+ exp.getMessage());
			Object errorElements[] = EliteExceptionUtils.getFullStackTraceAsArray(exp);
			request.setAttribute("errorDetails", errorElements);
			ActionMessage message = new ActionMessage("general.error");
			ActionMessages messages = new ActionMessages();
			messages.add("information", message);
			saveErrors(request, messages);
		}
		return mapping.findForward(FAILURE_FORWARD);
	}

	/**
	 * Download Public certificate
	 * @param mapping
	 * @param form
	 * @param request
	 * @param response
	 * @return
	 * @throws Exception
	 */
	public ActionForward downloadFile(ActionMapping mapping, ActionForm form,HttpServletRequest request, HttpServletResponse response)throws Exception {
		Logger.logTrace(MODULE, "Enter downloadFile method of"+ getClass().getName());
		try {
			if(request.getParameter("serverCertificateId") != null){
				String serverCertificateId=request.getParameter("serverCertificateId");
				ServerCertificateBLManager blManager = new ServerCertificateBLManager();
				ServerCertificateData serverCertificateData = blManager.view(serverCertificateId);

				String certificateFileName=serverCertificateData.getCertificateFileName();
				if(certificateFileName.contains(" ")){
					certificateFileName=certificateFileName.replaceAll("\\s", "_");
				}
				response.setContentType("application/octet-stream");
				response.setHeader("Content-Disposition", "attachment;filename="+ serverCertificateData.getCertificateFileName());
				ServletOutputStream out = response.getOutputStream();
				out.write(serverCertificateData.getCertificate());
				out.flush();
				out.close();
				response.flushBuffer();				
			}
		} catch (Exception exp) {
			Logger.logError(MODULE,"Error during Data Manager operation , reason : "+ exp.getMessage());
			Object errorElements[] = EliteExceptionUtils.getFullStackTraceAsArray(exp);
			request.setAttribute("errorDetails", errorElements);
			ActionMessage message = new ActionMessage("servermgr.servercertificate.delete.failure");
			ActionMessages messages = new ActionMessages();
			messages.add("information", message);
			saveErrors(request, messages);
		}
		return null;
	}

	public ActionForward duplicateServerCertificate(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response)throws Exception{
		Logger.logTrace(MODULE, "Enter Duplicate Server Certificate method of "+ getClass().getName());
		try {
			if(request.getParameter("serverCertificateId") != null){
				checkActionPermission(request, CREATE_ACTION_ALIAS);
				ServerCertificateForm serverCertificateForm = (ServerCertificateForm) form;
				ServerCertificateBLManager blManager = new ServerCertificateBLManager();
				ServerCertificateData serverCertificateData = blManager.getServerCertificateById(serverCertificateForm.getServerCertificateId());
				String serverCertificateName=request.getParameter("serverCertificateName");
				
				String currentUser = ((SystemLoginForm) request.getSession().getAttribute("radiusLoginForm")).getUserId();
				serverCertificateData.setCreatedByStaffId(currentUser);
				serverCertificateData.setModifiedByStaffId(currentUser);
				serverCertificateData.setServerCertificateName(serverCertificateName);
				
				IStaffData staffData = getStaffObject(((SystemLoginForm) request.getSession().getAttribute("radiusLoginForm")));
				
				blManager.createServerCertificate(serverCertificateData, staffData);

				request.setAttribute("responseUrl","/serverAllCertificates.do?method=initSearch");
				ActionMessage message = new ActionMessage("servermgr.servercertificate.duplicate");
				ActionMessages messages = new ActionMessages();
				messages.add("information", message);
				saveMessages(request, messages);
				return mapping.findForward(SUCCESS_FORWARD);
			}
			} catch (Exception exp) {
				Logger.logError(MODULE,"Error during Data Manager operation , reason : "+ exp.getMessage());
				Object errorElements[] = EliteExceptionUtils.getFullStackTraceAsArray(exp);
				request.setAttribute("errorDetails", errorElements);
				ActionMessage message = new ActionMessage("servermgr.servercertificate.delete.failure");
				ActionMessages messages = new ActionMessages();
				messages.add("information", message);
				saveErrors(request, messages);
			}
		return mapping.findForward(FAILURE_FORWARD);
	}
	
	/**
	 * Convert ServerCertificateForm to ServerCertificateData to store the data in to database.
	 * @param form
	 * @param data
	 * @throws Exception
	 */
	private void convertFormToBean(ServerCertificateForm form, ServerCertificateData data) throws IOException {
		data.setServerCertificateId(form.getServerCertificateId());
		data.setServerCertificateName(form.getServerCertificateName());
		data.setPrivateKeyPassword(form.getPrivateKeyPassword());
		data.setPrivateKeyAlgorithm(form.getPrivateKeyAlgorithm());
		data.setCertificateFileName(form.getPublicCert().getFileName());
		data.setPrivateKeyFileName(form.getPrivateKey().getFileName());
		data.setCertificateFileName(form.getPublicCert().getFileName());
		data.setPrivateKey(form.getPrivateKey().getFileData());
		data.setCertificate(form.getPublicCert().getFileData());
	}

	/**
	 * checks that the given public key and private key are correspond to each other.
	 * 
	 * @param publicKey
	 * @param privateKey
	 * @return true if correspond
	 * @throws Exception
	 * @throws {@link NullPointerException}
	 */
	//FIXME NEED TO MOVE THIS METHOD IN UTILITY.
	public void checkForPrivateKeyAndPublicKey(Certificate publicKey,PrivateKey privateKey, PrivateKeyAlgo privateKeyAlgo)throws CertificateValidationException {
		try{
			if(publicKey==null || privateKey==null){
				throw new CertificateValidationException("Public Certificate and Private Key are mismatch", CertificateRemarks.CERTIFICATE_KEY_MISMATCH);
			}
			String plainText="elitecoreelitecoreel";

			byte[] hashValue = plainText.getBytes();
			String algorithm = PrivateKeyAlgo.DSA.name.equals(privateKeyAlgo.name)?"NONEwithDSA":"NONEwithRSA";
			byte[] encryptedData = sign(hashValue, algorithm, privateKey);
			if(!verify(encryptedData, hashValue, publicKey.getPublicKey(), algorithm)) {
				throw new CertificateValidationException("Public Certificate and Private Key are mismatch", CertificateRemarks.CERTIFICATE_KEY_MISMATCH);	
			}
			
		}catch(CertificateValidationException e){
			throw e;
		}catch(Exception e){
			throw new CertificateValidationException("Public Certificate and Private Key are mismatch", CertificateRemarks.CERTIFICATE_KEY_MISMATCH,e);
		}
	}

	private byte[] sign(byte[] plainBytes, String algorithm, PrivateKey serverPrivateKey){
		byte[] signatureBytes = null;
		try {
			java.security.Signature signature = java.security.Signature.getInstance(algorithm);
			signature.initSign(serverPrivateKey);
			signature.update(plainBytes);
			signatureBytes = signature.sign();
		} catch (InvalidKeyException e) {
			System.out.println();
		} catch (NoSuchAlgorithmException e) {
			System.out.println();
		} catch (SignatureException e) {
			System.out.println();
		}
		return signatureBytes;
	}
	
	private boolean verify(byte[] signedBytes, byte[] verifyBytes, PublicKey publicKey, String algorithm){
		try {
			java.security.Signature signature = java.security.Signature.getInstance(algorithm);
			signature.initVerify(publicKey);
			signature.update(verifyBytes);
			boolean v = signature.verify(signedBytes); 
			return v;
		} catch (InvalidKeyException e) {
			System.out.println();
		} catch (NoSuchAlgorithmException e) {
			System.out.println();
		} catch (SignatureException e) {
			System.out.println();
		}
		return false;
	}
	
	/**
	 * Generate Certificate and return its collection.
	 * @param certFormFile
	 * @return
	 * @throws Exception
	 */
	//FIXME NEED TO MOVE THIS METHOD IN UTILITY.
	public Collection<? extends Certificate> generateCertificate(InputStream fileInputStream) throws CertificateException {
		if(fileInputStream==null){
			throw new CertificateException("Invalid Public Certificate");
		}
		try{
			CertificateFactory cf = CertificateFactory.getInstance("X.509");

			Collection<? extends Certificate> certList = cf.generateCertificates(fileInputStream);
			if(certList == null || certList.isEmpty()){
				throw new CertificateException("Invalid Public Certificate");
			}
			return certList;
		}catch(CertificateException e){
			throw new CertificateException("Invalid Public Certificate");
		}
	}

	public Collection<? extends Certificate> generateTrustedCertificate(InputStream fileInputStream) throws CertificateException {
		if(fileInputStream==null){
			throw new CertificateException("Invalid Trusted Certificate");
		}
		CertificateFactory cf = CertificateFactory.getInstance("X.509");

		Collection<? extends Certificate> certList = cf.generateCertificates(fileInputStream);
		if(certList == null || certList.isEmpty()){
			throw new CertificateException("Invalid Trusted Certificate");
		}
		return certList;
	}
	
	/**
	 * Generate Private Key and return Private Key Object.
	 * @param keyPath
	 * @param privateKeyAlgo
	 * @param privateKeyPassword
	 * @return
	 * @throws Exception
	 */
	//FIXME NEED TO MOVE THIS METHOD IN UTILITY. CREATE OVERLOAD METHOD FOR THIS 1) WITHOUR PASSWORD 2) WITH PASSWORD
	public PrivateKey generatePrivateKey(InputStream keyPath,PrivateKeyAlgo privateKeyAlgo, String privateKeyPassword) throws InvalidPrivateKeyException {
		try {
			PrivateKey privateKey = null;
			PKCS8EncodedKeySpec keySpec;
			KeyFactory keyFactory;
			if (!"".equals(privateKeyPassword)) {
				BufferedInputStream inFile;

				inFile = new BufferedInputStream(keyPath);

				ByteArrayOutputStream outStream = new ByteArrayOutputStream();
				byte[] buffer = new byte[1024];
				int bytesRead = 0;

				while ((bytesRead = inFile.read(buffer)) != -1) {
					outStream.write(buffer, 0, bytesRead);
				}
				inFile.close();
				outStream.close();
				javax.crypto.EncryptedPrivateKeyInfo encPriKey = new javax.crypto.EncryptedPrivateKeyInfo(outStream.toByteArray());
				Cipher cipher = Cipher.getInstance(encPriKey.getAlgName());
				PBEKeySpec pbeKeySpec = new PBEKeySpec(privateKeyPassword.toCharArray());
				SecretKeyFactory secKeyFactory = SecretKeyFactory.getInstance(encPriKey.getAlgName());
				cipher.init(Cipher.DECRYPT_MODE,secKeyFactory.generateSecret(pbeKeySpec),encPriKey.getAlgParameters());
				keySpec = encPriKey.getKeySpec(cipher);

			} else {
				DataInputStream dis = new DataInputStream(keyPath);
				byte[] bytes = new byte[dis.available()];
				dis.readFully(bytes);
				ByteArrayInputStream bais = new ByteArrayInputStream(bytes);
				byte[] keyBytes = new byte[bais.available()];
				bais.read(keyBytes, 0, bais.available());
				bais.close();
				keySpec = new PKCS8EncodedKeySpec(keyBytes);
			}

			keyFactory = KeyFactory.getInstance(privateKeyAlgo.name);
			privateKey = keyFactory.generatePrivate(keySpec);
			return privateKey;

		} catch (Exception e) {
			throw new InvalidPrivateKeyException("Invalid PrivateKey", CertificateRemarks.INVALID_PRIVATE_KEY);
		}
	}


	/**
	 * Set the Public Certificate Details to show details on JSP Page.
	 * @param data
	 * @throws Exception
	 */
	public void setServerCertificateDetail(ServerCertificateData data) throws InvalidPrivateKeyException{
		try {
			byte[] fileData=data.getCertificate();
			if(fileData!=null){

				ByteArrayInputStream bais=new ByteArrayInputStream(fileData);
				CertificateFactory cf;

				cf = CertificateFactory.getInstance("X.509");

				Collection<? extends Certificate> certList=cf.generateCertificates(bais);		

				if(certList!=null){
					for(Certificate certificate : certList) {

						X509Certificate x509Cert=(X509Certificate)certificate;		

						//Version
						data.setVersion(x509Cert.getVersion()+"");	

						//Serial No
						if(x509Cert.getSerialNumber().toString()!=null){
							data.setSerialNo(x509Cert.getSerialNumber().toString());	
						}
						//Signature Algorithm
						if(x509Cert.getSigAlgName()!=null){
							data.setSignatureAlgo(x509Cert.getSigAlgName());	
						}
						//Issuer
						if(x509Cert.getIssuerX500Principal().toString()!=null){
							data.setIssuer(x509Cert.getIssuerX500Principal().toString());				
						}
						Map<String,String> oidMap=new HashMap<String,String>();
						oidMap.put("TE","2.5.4.15");

						//Subject
						if(x509Cert.getSubjectX500Principal().toString()!=null){
							data.setSubject(x509Cert.getSubjectX500Principal().toString());	
						}
						//x509Cert.getExtensionValue("");
						SimpleDateFormat simpleDate=new SimpleDateFormat("dd MMM yyyy kk:mm:ss ");

						//Valid Form
						if(x509Cert.getNotBefore().toString()!=null){
							data.setValidFrom(simpleDate.format(x509Cert.getNotBefore()).toString());	
						}
						//Valid To Date
						if(x509Cert.getNotAfter().toString()!=null){
							data.setValidTo(simpleDate.format(x509Cert.getNotAfter()).toString());	
						}
						//Public Key
						if(x509Cert.getPublicKey().toString()!=null){
							data.setPublicKey(x509Cert.getPublicKey().toString());	
						}
						//Basic Constraint
						data.setBasicConstraint(x509Cert.getBasicConstraints()+"");

						//Subject Unique ID			
						if(x509Cert.getSubjectUniqueID()!=null){
							data.setSubjectUniqueID(x509Cert.getSubjectUniqueID().toString());
						}

						//Key Usage
						boolean[] b1=x509Cert.getKeyUsage();
						if(b1!=null){
							int i=0;
							if(x509Cert.getKeyUsage()!=null){
								String keyUsage="";	
								for(Boolean b :x509Cert.getKeyUsage()){
									if(b==true){
										switch(i){
										case 0:keyUsage=keyUsage+" Digital Signature,"; break;
										case 1:keyUsage=keyUsage+" Non Repudiation,"; break;
										case 2:keyUsage=keyUsage+" Key Encipherment,"; break;
										case 3:keyUsage=keyUsage+" Data Encipherment,"; break;
										case 4:keyUsage=keyUsage+" Key Agreement,"; break;	
										case 5:keyUsage=keyUsage+" Key Certificate Sign,"; break;
										case 6:keyUsage=keyUsage+" CRL Sign,"; break;
										case 7:keyUsage=keyUsage+" Encipher Only,"; break;
										case 8:keyUsage=keyUsage+" Decipher Only,"; break;					
										}
									}	
									i++;
								}
								keyUsage=keyUsage.substring(0,keyUsage.length()-1);
								data.setKeyUsage(keyUsage);
							}
						}

						//Subject Alternative Name
						if(x509Cert.getSubjectAlternativeNames()!=null){
							String subjectAltName = "";
							for(List<?> x:x509Cert.getSubjectAlternativeNames()){
								String subName=x.toString();
								subjectAltName=subjectAltName+subName.substring(subName.indexOf(",")+1, subName.length()-1)+",";					
							}
							subjectAltName=subjectAltName.substring(0, subjectAltName.length()-1);
							data.setSubjectAltName(subjectAltName);
						}

						//Issuer Alternative Name
						if(x509Cert.getIssuerAlternativeNames()!=null){
							String issuerAltName = "";
							for(List<?> x:x509Cert.getIssuerAlternativeNames()){
								String issuerName=x.toString();
								issuerAltName=issuerAltName+issuerName.substring(issuerName.indexOf(",")+1, issuerName.length()-1)+",";					
							}
							issuerAltName=issuerAltName.substring(0, issuerAltName.length()-1);
							data.setIssuerAltName(issuerAltName);
						}
						break;
					}		
				}
			}
		}catch (Exception e) {
			throw new InvalidPrivateKeyException("Invalid Public Certificate", CertificateRemarks.INVALID_CERTIFICATE);
		}
	}

	/**
	 * Set the Private key Details to show details on JSP page.
	 * @param data
	 * @throws Exception
	 */
	public void setPrivateKeyDetail(ServerCertificateData data) throws InvalidPrivateKeyException{

		if(data==null){
			throw new InvalidPrivateKeyException("Invalid PrivateKey", CertificateRemarks.INVALID_PRIVATE_KEY);
		}
		byte[] keyData=data.getPrivateKey();
		if(keyData==null){
			throw new InvalidPrivateKeyException("Invalid PrivateKey", CertificateRemarks.INVALID_PRIVATE_KEY);
		}
		try {	

			PrivateKey privateKey;
			if(data.getPrivateKeyPassword()!=null && data.getPrivateKeyPassword().length() > 0){

				javax.crypto.EncryptedPrivateKeyInfo encPriKey;

				encPriKey = new javax.crypto.EncryptedPrivateKeyInfo(keyData);

				/* Decrypt  password */
				String decryptedPassword = PasswordEncryption.getInstance().decrypt(data.getPrivateKeyPassword(),PasswordEncryption.ELITE_PASSWORD_CRYPT);
				data.setPrivateKeyPassword(decryptedPassword);
				
				Cipher cipher = Cipher.getInstance(encPriKey.getAlgName());
				PBEKeySpec pbeKeySpec = new PBEKeySpec(data.getPrivateKeyPassword().toCharArray());
				SecretKeyFactory secKeyFactory = SecretKeyFactory.getInstance(encPriKey.getAlgName());
				cipher.init(Cipher.DECRYPT_MODE,secKeyFactory.generateSecret(pbeKeySpec),encPriKey.getAlgParameters());
				PKCS8EncodedKeySpec keySpec = encPriKey.getKeySpec(cipher);

				KeyFactory keyFactory = KeyFactory.getInstance(data.getPrivateKeyAlgorithm());
				privateKey = keyFactory.generatePrivate(keySpec);					

			}else{
				ByteArrayInputStream bais=new ByteArrayInputStream(keyData);
				byte[] keyByte=new byte[keyData.length];
				bais.read(keyByte,0,bais.available());
				bais.close();
				PKCS8EncodedKeySpec keySpec=new PKCS8EncodedKeySpec(keyByte);				
				KeyFactory kf=KeyFactory.getInstance(data.getPrivateKeyAlgorithm());
				privateKey=kf.generatePrivate(keySpec);

			}
			data.setPrivateKeyData(privateKey.toString());

		} catch (Exception e) {
			throw new InvalidPrivateKeyException("Invalid PrivateKey", CertificateRemarks.INVALID_PRIVATE_KEY);
		}
	}
	public void setTrustedCertificateDetail(TrustedCertificateData data) throws Exception{
		byte[] fileData=data.getTrustedCertificate();
		if(fileData!=null){		

			ByteArrayInputStream bais=new ByteArrayInputStream(fileData);
			CertificateFactory cf=CertificateFactory.getInstance("X.509");
			Collection<? extends Certificate> certList=cf.generateCertificates(bais);		

			if(certList!=null){
				for(Certificate certificate : certList) {
					X509Certificate x509Cert=(X509Certificate)certificate;		

					//Version
					data.setVersion(x509Cert.getVersion()+"");	

					//Serial No
					if(x509Cert.getSerialNumber().toString()!=null){
						data.setSerialNo(x509Cert.getSerialNumber().toString());	
					}
					//Signature Algorithm
					if(x509Cert.getSigAlgName()!=null){
						data.setSignatureAlgo(x509Cert.getSigAlgName());	
					}
					//Issuer
					if(x509Cert.getIssuerX500Principal().toString()!=null){
						data.setIssuer(x509Cert.getIssuerX500Principal().toString());				
					}
					Map<String,String> oidMap=new HashMap<String,String>();
					oidMap.put("TE","2.5.4.15");

					//Subject
					if(x509Cert.getSubjectX500Principal().toString()!=null){
						data.setSubject(x509Cert.getSubjectX500Principal().toString());	
					}
					SimpleDateFormat simpleDate=new SimpleDateFormat("dd MMM yyyy kk:mm:ss ");
					//Valid Form
					if(x509Cert.getNotBefore().toString()!=null){
						data.setValidFrom(simpleDate.format(x509Cert.getNotBefore()).toString());	
					}
					//Valid To Date
					if(x509Cert.getNotAfter().toString()!=null){
						data.setValidTo(simpleDate.format(x509Cert.getNotAfter()).toString());	
					}
					//Public Key
					if(x509Cert.getPublicKey().toString()!=null){
						data.setPublicKey(x509Cert.getPublicKey().toString());	
					}
					//Basic Constraint
					data.setBasicConstraint(x509Cert.getBasicConstraints()+"");

					//Subject Unique ID			
					if(x509Cert.getSubjectUniqueID()!=null){
						System.out.println("Subject Unique ID = "+x509Cert.getSubjectUniqueID());
						data.setSubjectUniqueID(x509Cert.getSubjectUniqueID().toString());
					}
					//Key Usage
					boolean[] b1=x509Cert.getKeyUsage();
					if(b1!=null){
						int i=0;
						if(x509Cert.getKeyUsage()!=null){
							String keyUsage="";	
							for(Boolean b :x509Cert.getKeyUsage()){
								if(b==true){
									switch(i){
									case 0:keyUsage=keyUsage+" Digital Signature,"; break;
									case 1:keyUsage=keyUsage+" Non Repudiation,"; break;
									case 2:keyUsage=keyUsage+" Key Encipherment,"; break;
									case 3:keyUsage=keyUsage+" Data Encipherment,"; break;
									case 4:keyUsage=keyUsage+" Key Agreement,"; break;	
									case 5:keyUsage=keyUsage+" Key Certificate Sign,"; break;
									case 6:keyUsage=keyUsage+" CRL Sign,"; break;
									case 7:keyUsage=keyUsage+" Encipher Only,"; break;
									case 8:keyUsage=keyUsage+" Decipher Only,"; break;					
									}
								}	
								i++;
							}
							keyUsage=keyUsage.substring(0,keyUsage.length()-1);
							data.setKeyUsage(keyUsage);
						}
					}

					//Subject Alternative Name
					if(x509Cert.getSubjectAlternativeNames()!=null){
						String subjectAltName = "";
						for(List<?> x:x509Cert.getSubjectAlternativeNames()){
							String subName=x.toString();
							subjectAltName=subjectAltName+subName.substring(subName.indexOf(",")+1, subName.length()-1)+",";					
						}
						subjectAltName=subjectAltName.substring(0, subjectAltName.length()-1);
						data.setSubjectAltName(subjectAltName);
					}

					//Issuer Alternative Name
					if(x509Cert.getIssuerAlternativeNames()!=null){
						String issuerAltName = "";
						for(List<?> x:x509Cert.getIssuerAlternativeNames()){
							String issuerName=x.toString();
							issuerAltName=issuerAltName+issuerName.substring(issuerName.indexOf(",")+1, issuerName.length()-1)+",";					
						}
						issuerAltName=issuerAltName.substring(0, issuerAltName.length()-1);
						data.setIssuerAltName(issuerAltName);
					}
					break;
				}		
			}
		}
	}
	public void setCrlCertificateDetail(CrlCertificateData data) throws Exception{
		Collection<? extends CRL> crlList=getCRLCertificateFromBytes(data.getCrlCertificate());
		if(crlList!=null){
			for(CRL crlCertis : crlList) {
				X509CRLImpl x509Crl=(X509CRLImpl) crlCertis;		

				if(x509Crl.getIssuerDN()!=null){
					data.setIssuer(x509Crl.getIssuerDN().toString());
				}					

				SimpleDateFormat simpleDate=new SimpleDateFormat("dd MMM yyyy kk:mm:ss ");

				if(x509Crl.getThisUpdate()!=null){
					data.setLastUpdate(simpleDate.format(x509Crl.getThisUpdate()).toString());	
				}

				if(x509Crl.getNextUpdate()!=null){
					data.setNextUpdate(simpleDate.format(x509Crl.getNextUpdate()).toString());	
				}

				if(x509Crl.getRevokedCertificates()!=null){
					Set<X509CRLEntry> x509CrlEntry=x509Crl.getRevokedCertificates();
					/*for(X509CRLEntry crlEntry : x509CrlEntry){

							System.out.println("Revocation Date = "+crlEntry.getRevocationDate());
							System.out.println("Serial Number = "+crlEntry.getSerialNumber());
						}*/
					data.setRevokedList(x509CrlEntry);
				}

				if(x509Crl.getCRLNumber()!=null){
					data.setSerialNo(x509Crl.getCRLNumber().toString());	
				}else{
					data.setSerialNo("-");
				}

				if(x509Crl.getSigAlgName()!=null){
					data.setSignatureAlgo(x509Crl.getSigAlgName());	
				}	
				break;
			}
		}
	}
	private Collection<? extends CRL> getCRLCertificateFromBytes(byte[] crlBytes)throws CRLException{
		if(crlBytes==null){
			throw new CRLException("Invalid CRL Certificate");
		}
		ByteArrayInputStream bais=new ByteArrayInputStream(crlBytes);
		try{
			CertificateFactory cf=CertificateFactory.getInstance("X.509");
			Collection<? extends CRL> crlList=cf.generateCRLs(bais);
			return crlList;
		}catch(CertificateException e){
			throw new CRLException("Invalid CRL Certificate");
		}catch(CRLException e){
			throw new CRLException("Invalid CRL Certificate");
		}		
	}
	
	//TRUSTED CERTIFICATE MODULES METHODS
	
	public ActionForward initCreateTrusted(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response)throws Exception{
		try{
			checkActionPermission(request, CREATE_TRUSTED_ACTION_ALIAS);
			Logger.logTrace(MODULE,"Enter init Trusted Create method of "+getClass().getName());
			ServerCertificateForm serverCertificateForm = (ServerCertificateForm) form;
			request.setAttribute("trustedCertificateForm", serverCertificateForm);
			return mapping.findForward(TRUSTED_CREATE_FORWARD);
		}catch (ActionNotPermitedException e) {
			Logger.logError(MODULE, "Error during Data Manager operation ");
			Object errorElements[] = EliteExceptionUtils.getFullStackTraceAsArray(e);
			request.setAttribute("errorDetails", errorElements);
			ActionMessage message = new ActionMessage("general.user.restricted");
			ActionMessages messages = new ActionMessages();
			messages.add("information", message);
			saveErrors(request, messages); 
		}catch(Exception e){
			Logger.logError(MODULE, "Error during Data Manager operation , reason : " + e.getMessage());
			Object errorElements[] = EliteExceptionUtils.getFullStackTraceAsArray(e);
			request.setAttribute("errorDetails", errorElements);
			ActionMessage message = new ActionMessage("general.error");                                                         
			ActionMessages messages = new ActionMessages();                                                                                 
			messages.add("information", message);
			saveErrors(request, messages);			   
		}
		return mapping.findForward(FAILURE_FORWARD);
	}
	public ActionForward createTrusted(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response)throws Exception{
		Logger.logTrace(MODULE,"Enter create method of "+getClass().getName());
		try{
			checkActionPermission(request, CREATE_TRUSTED_ACTION_ALIAS);

			ServerCertificateForm serverCertificateForm = (ServerCertificateForm) form;
			TrustedCertificateData trustedCertificateData=new TrustedCertificateData();

			String currentUser = ((SystemLoginForm)request.getSession().getAttribute("radiusLoginForm")).getUserId();

			//generate certificate
			Collection<? extends Certificate> certs=generateTrustedCertificate(serverCertificateForm.getTrustedCert().getInputStream());

			convertFormToBeanTrusted(serverCertificateForm, trustedCertificateData);
			trustedCertificateData.setCreatedByStaffId(currentUser);
			trustedCertificateData.setModifiedByStaffId(currentUser);

			IStaffData staffData = getStaffObject(((SystemLoginForm)request.getSession().getAttribute("radiusLoginForm")));
			TrustedCertificateBLManager blManager=new TrustedCertificateBLManager();
			blManager.createTrustedCertificate(trustedCertificateData, staffData);

			request.setAttribute("responseUrl","/serverAllCertificates.do?method=initSearch");  
			ActionMessage message = new ActionMessage("servermgr.trustedcertificate.create");
			ActionMessages messages = new ActionMessages();          
			messages.add("information", message);                    
			saveMessages(request,messages);      	
			return mapping.findForward(SUCCESS_FORWARD);
		} catch (CertificateException exp) {
			Logger.logError(MODULE,"Error during Data Manager operation , reason : "+ exp.getMessage());
			Object errorElements[] = EliteExceptionUtils.getFullStackTraceAsArray(exp);
			request.setAttribute("errorDetails", errorElements);
			ActionMessage message = new ActionMessage("servermgr.trustedcertificate.create.invalidcertificate");
			ActionMessages messages = new ActionMessages();
			messages.add("information", message);
			saveErrors(request, messages);
		}catch (ActionNotPermitedException e) {
			Logger.logError(MODULE, "Error during Data Manager operation ");
			Object errorElements[] = EliteExceptionUtils.getFullStackTraceAsArray(e);
			request.setAttribute("errorDetails", errorElements);
			ActionMessage message = new ActionMessage("general.user.restricted");
			ActionMessages messages = new ActionMessages();
			messages.add("information", message);
			saveErrors(request, messages); 
		}catch (Exception exp) {                                                                                                             
			Logger.logError(MODULE, "Error during Data Manager operation , reason : " + exp.getMessage());
			Object errorElements[] = EliteExceptionUtils.getFullStackTraceAsArray(exp);
			request.setAttribute("errorDetails", errorElements);            
			ActionMessage message = new ActionMessage("servermgr.trustedcertificate.create.failure");                                                       
			ActionMessages messages = new ActionMessages();                                                                               
			messages.add("information", message);                                                                                           
			saveErrors(request, messages);                                                                                                  
		}                                                                                                                                                                                                                                                                
		return mapping.findForward(FAILURE_FORWARD); 		
	}
	private void convertFormToBeanTrusted(ServerCertificateForm form, TrustedCertificateData data) throws IOException {
		data.setTrustedCertificateId(form.getTrustedCertificateId());
		data.setTrustedCertificateName(form.getTrustedCertificateName());
		data.setCertificateFileName(form.getTrustedCert().getFileName());
		data.setTrustedCertificate(form.getTrustedCert().getFileData());			
	}
	
	public ActionForward showAllTrusted(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response)throws Exception{
		Logger.logTrace(MODULE,"Enter showAll method of "+getClass().getName());
		try{
			ServerCertificateForm serverCertificateForm = (ServerCertificateForm) form;
			GenericBLManager genericBLManager=new GenericBLManager();
			PageList pageList = genericBLManager.getAllRecords(TrustedCertificateData.class,"trustedCertificateName",true);
			serverCertificateForm.setTrustedPageNumber(pageList.getCurrentPage());
			serverCertificateForm.setTrustedTotalPages(pageList.getTotalPages());
			serverCertificateForm.setTrustedtotalRecords(pageList.getTotalItems());
			serverCertificateForm.setListTrustedCertificate(pageList.getListData());

			List<TrustedCertificateData> list=pageList.getListData();
			for(TrustedCertificateData data : list){
				setTrustedServerCertificateDetail(data);
			}

			request.setAttribute("showAllTrustedCertificate", serverCertificateForm);
			return mapping.findForward(SHOWALLTRUSTED_FORWARD);
		}catch (ConstraintViolationException e) {
			Logger.logError(MODULE, "Returning error forward from " + getClass().getName());
			Object errorElements[] = EliteExceptionUtils.getFullStackTraceAsArray(e);
			request.setAttribute("errorDetails", errorElements);
			ActionMessage message = new ActionMessage("servermgr.trustedcertificate.alreadyinuse");
			ActionMessages messages = new ActionMessages();
			messages.add("information",message);
			saveErrors(request,messages);			
		}catch(Exception managerExp){
			Logger.logError(MODULE, "Error during Data Manager operation , reason : " + managerExp.getMessage());            
			Object errorElements[] = EliteExceptionUtils.getFullStackTraceAsArray(managerExp);
			request.setAttribute("errorDetails", errorElements);
		}
		return mapping.findForward(FAILURE_FORWARD);
	}
	private void setTrustedServerCertificateDetail(TrustedCertificateData data) throws Exception{
		byte[] fileData=data.getTrustedCertificate();
		if(fileData!=null){		

			ByteArrayInputStream bais=new ByteArrayInputStream(fileData);
			CertificateFactory cf=CertificateFactory.getInstance("X.509");
			Collection<? extends Certificate> certList=cf.generateCertificates(bais);		

			if(certList!=null){
				for(Certificate certificate : certList) {
					X509Certificate x509Cert=(X509Certificate)certificate;		

					//Version
					data.setVersion(x509Cert.getVersion()+"");	

					//Serial No
					if(x509Cert.getSerialNumber().toString()!=null){
						data.setSerialNo(x509Cert.getSerialNumber().toString());	
					}
					//Signature Algorithm
					if(x509Cert.getSigAlgName()!=null){
						data.setSignatureAlgo(x509Cert.getSigAlgName());	
					}
					//Issuer
					if(x509Cert.getIssuerX500Principal().toString()!=null){
						data.setIssuer(x509Cert.getIssuerX500Principal().toString());				
					}
					Map<String,String> oidMap=new HashMap<String,String>();
					oidMap.put("TE","2.5.4.15");

					//Subject
					if(x509Cert.getSubjectX500Principal().toString()!=null){
						data.setSubject(x509Cert.getSubjectX500Principal().toString());	
					}
					SimpleDateFormat simpleDate=new SimpleDateFormat("dd MMM yyyy kk:mm:ss ");
					//Valid Form
					if(x509Cert.getNotBefore().toString()!=null){
						data.setValidFrom(simpleDate.format(x509Cert.getNotBefore()).toString());	
					}
					//Valid To Date
					if(x509Cert.getNotAfter().toString()!=null){
						data.setValidTo(simpleDate.format(x509Cert.getNotAfter()).toString());	
					}
					//Public Key
					if(x509Cert.getPublicKey().toString()!=null){
						data.setPublicKey(x509Cert.getPublicKey().toString());	
					}
					//Basic Constraint
					data.setBasicConstraint(x509Cert.getBasicConstraints()+"");

					//Subject Unique ID			
					if(x509Cert.getSubjectUniqueID()!=null){
						System.out.println("Subject Unique ID = "+x509Cert.getSubjectUniqueID());
						data.setSubjectUniqueID(x509Cert.getSubjectUniqueID().toString());
					}
					//Key Usage
					boolean[] b1=x509Cert.getKeyUsage();
					if(b1!=null){
						int i=0;
						if(x509Cert.getKeyUsage()!=null){
							String keyUsage="";	
							for(Boolean b :x509Cert.getKeyUsage()){
								if(b==true){
									switch(i){
									case 0:keyUsage=keyUsage+" Digital Signature,"; break;
									case 1:keyUsage=keyUsage+" Non Repudiation,"; break;
									case 2:keyUsage=keyUsage+" Key Encipherment,"; break;
									case 3:keyUsage=keyUsage+" Data Encipherment,"; break;
									case 4:keyUsage=keyUsage+" Key Agreement,"; break;	
									case 5:keyUsage=keyUsage+" Key Certificate Sign,"; break;
									case 6:keyUsage=keyUsage+" CRL Sign,"; break;
									case 7:keyUsage=keyUsage+" Encipher Only,"; break;
									case 8:keyUsage=keyUsage+" Decipher Only,"; break;					
									}
								}	
								i++;
							}
							keyUsage=keyUsage.substring(0,keyUsage.length()-1);
							data.setKeyUsage(keyUsage);
						}
					}

					//Subject Alternative Name
					if(x509Cert.getSubjectAlternativeNames()!=null){
						String subjectAltName = "";
						for(List<?> x:x509Cert.getSubjectAlternativeNames()){
							String subName=x.toString();
							subjectAltName=subjectAltName+subName.substring(subName.indexOf(",")+1, subName.length()-1)+",";					
						}
						subjectAltName=subjectAltName.substring(0, subjectAltName.length()-1);
						data.setSubjectAltName(subjectAltName);
					}

					//Issuer Alternative Name
					if(x509Cert.getIssuerAlternativeNames()!=null){
						String issuerAltName = "";
						for(List<?> x:x509Cert.getIssuerAlternativeNames()){
							String issuerName=x.toString();
							issuerAltName=issuerAltName+issuerName.substring(issuerName.indexOf(",")+1, issuerName.length()-1)+",";					
						}
						issuerAltName=issuerAltName.substring(0, issuerAltName.length()-1);
						data.setIssuerAltName(issuerAltName);
					}
					break;
				}		
			}
		}
	}
	public ActionForward deleteTrustedCertificate(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response)throws Exception{
		Logger.logTrace(MODULE,"Enter Delete method of "+getClass().getName());
		try{
			checkActionPermission(request, DELETE_TRUSTED_ACTION_ALIAS);

			String[] strSelectedIds = request.getParameterValues("selectTrusted");

			IStaffData staffData = getStaffObject(((SystemLoginForm)request.getSession().getAttribute("radiusLoginForm")));

			TrustedCertificateBLManager blManager = new TrustedCertificateBLManager();
			blManager.deleteTrustedCertificateById(Arrays.asList(strSelectedIds), staffData);

			int strSelectedIdsLen = strSelectedIds.length;
			ServerCertificateForm serverCertificateForm = (ServerCertificateForm) form;
			long currentPageNumber = getCurrentPageNumberAfterDel(strSelectedIdsLen,serverCertificateForm.getTrustedPageNumber(),serverCertificateForm.getTrustedTotalPages(),serverCertificateForm.getTrustedtotalRecords());

			request.setAttribute("responseUrl", "/serverAllCertificates.do?method=initSearch&pageNumber="+currentPageNumber);    
			ActionMessage message = new ActionMessage("servermgr.trustedcertificate.delete");
			ActionMessages messages = new ActionMessages();          
			messages.add("information", message);                    
			saveMessages(request,messages);      	
			return mapping.findForward(SUCCESS_FORWARD);
		}catch (ActionNotPermitedException e) {
			Logger.logError(MODULE, "Error during Data Manager operation ");
			Object errorElements[] = EliteExceptionUtils.getFullStackTraceAsArray(e);
			request.setAttribute("errorDetails", errorElements);
			ActionMessage message = new ActionMessage("general.user.restricted");
			ActionMessages messages = new ActionMessages();
			messages.add("information", message);
			saveErrors(request, messages); 
		}catch (Exception exp) {                                                                                                             
			Logger.logError(MODULE, "Error during Data Manager operation , reason : " + exp.getMessage());
			Object errorElements[] = EliteExceptionUtils.getFullStackTraceAsArray(exp);
			request.setAttribute("errorDetails", errorElements);            
			ActionMessage message = new ActionMessage("servermgr.trustedcertificate.delete.failure");                                                       
			ActionMessages messages = new ActionMessages();                                                                               
			messages.add("information", message);                                                                                           
			saveErrors(request, messages);                                                                                                  
		}		
		return mapping.findForward(FAILURE_FORWARD);
	}
	public ActionForward viewTrustedCertificate(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response)throws Exception{
		Logger.logTrace(MODULE,"Enter View method of "+getClass().getName());
		try{			
			String trustedCertificateId="";
			String strTrustedCertificateId = request.getParameter("trustedCertificateId");
			if(Strings.isNullOrBlank(strTrustedCertificateId) == false){
				trustedCertificateId=strTrustedCertificateId;
			}

			TrustedCertificateBLManager blManager=new TrustedCertificateBLManager();
			TrustedCertificateData trustedCertificateData=blManager.view(trustedCertificateId);

			setTrustedServerCertificateDetail(trustedCertificateData);
			request.setAttribute("trustedCertificateData", trustedCertificateData);		
			return mapping.findForward(VIEWTRUSTED_FORWARD);		
		}catch (DataManagerException managerExp) {
			Logger.logError(MODULE, "Error during Data Manager operation , reason : " + managerExp.getMessage());
			Object errorElements[] = EliteExceptionUtils.getFullStackTraceAsArray(managerExp);
			request.setAttribute("errorDetails", errorElements);
			ActionMessage message = new ActionMessage("general.error");                                                         
			ActionMessages messages = new ActionMessages();                                                                                 
			messages.add("information", message);                                                                                           
			saveErrors(request, messages);                              
		}catch(Exception e){                                                                                                             
			Logger.logError(MODULE, "Error during Data Manager operation , reason : " + e.getMessage());
			Object errorElements[] = EliteExceptionUtils.getFullStackTraceAsArray(e);
			request.setAttribute("errorDetails", errorElements);            
			ActionMessage message = new ActionMessage("general.error");                                                       
			ActionMessages messages = new ActionMessages();                                                                               
			messages.add("information", message);                                                                                           
			saveErrors(request, messages);                                                                                                  
		}
		return mapping.findForward(FAILURE_FORWARD);
	}
	public ActionForward downloadTrustedFile(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response)throws Exception{
		Logger.logTrace(MODULE,"Enter downloadFile method of"+getClass().getName());
		try{
			String strTrustedCertificatId = request.getParameter("trustedCertificateId");
			if(Strings.isNullOrBlank(strTrustedCertificatId) == false){
				String trustedCertificateId=strTrustedCertificatId;
				TrustedCertificateBLManager blManager = new TrustedCertificateBLManager();
				TrustedCertificateData trustedCertificateData = blManager.view(trustedCertificateId);

				String certificateFileName=trustedCertificateData.getCertificateFileName();
				if(certificateFileName.contains(" ")){
					certificateFileName=certificateFileName.replaceAll("\\s", "_");
				}
				response.setContentType("application/octet-stream");
				response.setHeader("Content-Disposition", "attachment;filename="+ certificateFileName);
				ServletOutputStream out = response.getOutputStream();
				out.write(trustedCertificateData.getTrustedCertificate());
				out.flush();
				out.close();
				response.flushBuffer();				
			}

		}catch (Exception exp) {                                                                                                             
			Logger.logError(MODULE, "Error during Data Manager operation , reason : " + exp.getMessage());
			Object errorElements[] = EliteExceptionUtils.getFullStackTraceAsArray(exp);
			request.setAttribute("errorDetails", errorElements);            
			ActionMessage message = new ActionMessage("servermgr.servercertificate.delete.failure");                                                       
			ActionMessages messages = new ActionMessages();                                                                               
			messages.add("information", message);                                                                                           
			saveErrors(request, messages);                                                                                                  
		}
		return null;
	}
	public ActionForward duplicateTrustedCertificate(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response)throws Exception{
		Logger.logTrace(MODULE,"Enter Duplicate Trusted Certificate method of"+getClass().getName());
		try{
			if(request.getParameter("trustedCertificateId") != null){
				checkActionPermission(request, CREATE_TRUSTED_ACTION_ALIAS);
				ServerCertificateForm serverCertificateForm = (ServerCertificateForm) form;
				TrustedCertificateBLManager blManager=new TrustedCertificateBLManager();
				String trustedCertName=request.getParameter("trustedCertName");
				TrustedCertificateData trustedCertificateData = blManager.getTrustedCertificateById(serverCertificateForm.getTrustedCertificateId());
			
				String currentUser = ((SystemLoginForm) request.getSession().getAttribute("radiusLoginForm")).getUserId();
				trustedCertificateData.setCreatedByStaffId(currentUser);
				trustedCertificateData.setModifiedByStaffId(currentUser);
				trustedCertificateData.setTrustedCertificateName(trustedCertName);
				
				IStaffData staffData = getStaffObject(((SystemLoginForm) request.getSession().getAttribute("radiusLoginForm")));
				blManager.createTrustedCertificate(trustedCertificateData, staffData);

				request.setAttribute("responseUrl","/serverAllCertificates.do?method=initSearch");
				ActionMessage message = new ActionMessage("servermgr.trustedcertificate.duplicate");
				ActionMessages messages = new ActionMessages();
				messages.add("information", message);
				saveMessages(request, messages);
				return mapping.findForward(SUCCESS_FORWARD);
				
			}
		}catch (Exception exp) {
			Logger.logError(MODULE,"Error during Data Manager operation , reason : "+ exp.getMessage());
			Object errorElements[] = EliteExceptionUtils.getFullStackTraceAsArray(exp);
			request.setAttribute("errorDetails", errorElements);
			ActionMessage message = new ActionMessage("servermgr.servercertificate.delete.failure");
			ActionMessages messages = new ActionMessages();
			messages.add("information", message);
			saveErrors(request, messages);
		}
	return mapping.findForward(FAILURE_FORWARD);
	}
	
	public ActionForward initCreateCrl(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response)throws Exception{
		Logger.logTrace(MODULE,"Enter initCreate method of "+getClass().getName());
		try{
			checkActionPermission(request, CREATE_CRL_ACTION_ALIAS);
			ServerCertificateForm serverCertificateForm = (ServerCertificateForm) form;

			request.setAttribute("serverCertificateForm", serverCertificateForm);
			return mapping.findForward(CREATECRL_FORWARD);
		}catch (ActionNotPermitedException e) {
			Logger.logError(MODULE, "Error during Data Manager operation ");
			Object errorElements[] = EliteExceptionUtils.getFullStackTraceAsArray(e);
			request.setAttribute("errorDetails", errorElements);
			ActionMessage message = new ActionMessage("general.user.restricted");
			ActionMessages messages = new ActionMessages();
			messages.add("information", message);
			saveErrors(request, messages); 
		}catch(Exception e){
			Logger.logError(MODULE, "Error during Data Manager operation , reason : " + e.getMessage());
			Object errorElements[] = EliteExceptionUtils.getFullStackTraceAsArray(e);
			request.setAttribute("errorDetails", errorElements);
			ActionMessage message = new ActionMessage("general.error");                                                         
			ActionMessages messages = new ActionMessages();                                                                                 
			messages.add("information", message);
			saveErrors(request, messages);			   
		}		
		return mapping.findForward(FAILURE_FORWARD);
	}

	public ActionForward createCrl(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response)throws Exception{
		Logger.logTrace(MODULE,"Enter create method of "+getClass().getName());
		try{		
			checkActionPermission(request, CREATE_CRL_ACTION_ALIAS);

			ServerCertificateForm serverCertificateForm = (ServerCertificateForm) form;
			
			String currentUser = ((SystemLoginForm)request.getSession().getAttribute("radiusLoginForm")).getUserId();

			CrlCertificateData crlCertificateData=new CrlCertificateData();
			//generate CRL			
			generateCrlCertificate(serverCertificateForm.getCrlCert().getInputStream());

			convertFormToBeanCrl(serverCertificateForm,crlCertificateData);

			crlCertificateData.setCreatedByStaffId(currentUser);
			crlCertificateData.setModifiedByStaffId(currentUser);

			IStaffData staffData = getStaffObject(((SystemLoginForm)request.getSession().getAttribute("radiusLoginForm")));
			CrlCertificateBLManager blManager=new CrlCertificateBLManager();
			blManager.createCertificateRevocation(crlCertificateData, staffData);

			request.setAttribute("responseUrl", "/serverAllCertificates.do?method=initSearch");    
			ActionMessage message = new ActionMessage("servermgr.crlcertificate.create");
			ActionMessages messages = new ActionMessages();          
			messages.add("information", message);                    
			saveMessages(request,messages);      	
			return mapping.findForward(SUCCESS_FORWARD);
		}catch(CRLException e){
			Logger.logError(MODULE,"Error during Data Manager operation , reason : "+ e.getMessage());
			Object errorElements[] = EliteExceptionUtils.getFullStackTraceAsArray(e);
			request.setAttribute("errorDetails", errorElements);
			ActionMessage message = new ActionMessage("servermgr.crlcertificate.create.invalidcertificate");
			ActionMessages messages = new ActionMessages();
			messages.add("information", message);
			saveErrors(request, messages);
		}catch (ActionNotPermitedException e) {
			Logger.logError(MODULE, "Error during Data Manager operation ");
			Object errorElements[] = EliteExceptionUtils.getFullStackTraceAsArray(e);
			request.setAttribute("errorDetails", errorElements);
			ActionMessage message = new ActionMessage("general.user.restricted");
			ActionMessages messages = new ActionMessages();
			messages.add("information", message);
			saveErrors(request, messages); 
		}catch (Exception exp) {                                                                                                             
			Logger.logError(MODULE, "Error during Data Manager operation , reason : " + exp.getMessage());
			Object errorElements[] = EliteExceptionUtils.getFullStackTraceAsArray(exp);
			request.setAttribute("errorDetails", errorElements);            
			ActionMessage message = new ActionMessage("servermgr.crlcertificate.create.failure");                                                       
			ActionMessages messages = new ActionMessages();                                                                               
			messages.add("information", message);                                                                                           
			saveErrors(request, messages);                                                                                                  
		}                                                                                                                                                                                                                                                                
		return mapping.findForward(FAILURE_FORWARD); 		
	}
	public Collection<? extends CRL> generateCrlCertificate(InputStream crlInputStream)throws Exception {
		if(crlInputStream==null){
			throw new CRLException("Invalid CRL Certificate");
		}
		try{
			CertificateFactory cf=CertificateFactory.getInstance("X.509");
				Collection<? extends CRL> crlList=cf.generateCRLs(crlInputStream);
				if(crlList == null || crlList.isEmpty()){
					throw new CRLException("Invalid CRL Certificate");
				}
				return crlList;
		}finally{
			if(crlInputStream!=null){
				crlInputStream.close();
			}else{
				System.out.println("File InpustStream is null.");
			}
		}		
	}
	private CrlCertificateData convertFormToBeanCrl(ServerCertificateForm form,CrlCertificateData data) throws Exception{
		if(form.getCrlCert()!=null){
			//			byte[] crlArrayList=insertCRL(form.getCrlCert());
			//			CrlCertificateData data=new CrlCertificateData();
			//			int i=0;
			data.setCrlCertificateId(form.getCrlCertificateId());
			//			data.setCrlCertificateName(form.getCrlCertificateName()+(i+1));
			data.setCrlCertificateName(form.getCrlCertificateName());
			data.setCertificateFileName(form.getCrlCert().getFileName());
			data.setCrlCertificate(form.getCrlCert().getFileData());		
			return data;
		}
		return null;		
	}
	public ActionForward showAllCrl(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response)throws Exception{
		Logger.logTrace(MODULE,"Enter showAll method of "+getClass().getName());
		try{
			ServerCertificateForm serverCertificateForm = (ServerCertificateForm) form;
			GenericBLManager genericBLManager=new GenericBLManager();
			PageList pageList = genericBLManager.getAllRecords(CrlCertificateData.class,"crlCertificateName",true);
			serverCertificateForm.setCrlPageNumber(pageList.getCurrentPage());			
			serverCertificateForm.setCrlTotalPages(pageList.getTotalPages());
			serverCertificateForm.setCrlTotalRecords(pageList.getTotalItems());
			serverCertificateForm.setListCrlCertificate(pageList.getListData());

			List<CrlCertificateData> list=pageList.getListData();

			for(CrlCertificateData data : list){
				setCrlCertificateDetail(data);
			}

			request.setAttribute("showAllCrlCertificateForm", serverCertificateForm);
			return mapping.findForward(SHOWALLCRL_FORWARD);

		}catch (ConstraintViolationException e) {
			Logger.logError(MODULE, "Returning error forward from " + getClass().getName());
			Object errorElements[] = EliteExceptionUtils.getFullStackTraceAsArray(e);
			request.setAttribute("errorDetails", errorElements);
			ActionMessage message = new ActionMessage("servermgr.crlcertificate.alreadyinuse");
			ActionMessages messages = new ActionMessages();
			messages.add("information",message);
			saveErrors(request,messages);
		}catch(Exception managerExp){
			Logger.logError(MODULE, "Error during Data Manager operation , reason : " + managerExp.getMessage());            
			Object errorElements[] = EliteExceptionUtils.getFullStackTraceAsArray(managerExp);
			request.setAttribute("errorDetails", errorElements);
		}
		return mapping.findForward(FAILURE_FORWARD);
	}

	public ActionForward downloadCrlFile(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response)throws Exception{
		Logger.logTrace(MODULE,"Enter downloadFile method of"+getClass().getName());
		try{			
			if(request.getParameter("crlCertificateId")!=null){
				String crlCertificateId=request.getParameter("crlCertificateId");
				CrlCertificateBLManager blManager=new CrlCertificateBLManager();
				CrlCertificateData crlCertificateData=blManager.view(crlCertificateId);
				String certificateFileName=crlCertificateData.getCertificateFileName();

				if(certificateFileName.contains(" ")){
					certificateFileName=certificateFileName.replaceAll("\\s", "_");
				}
				response.setContentType("application/octet-stream");
				response.setHeader("Content-Disposition","attachment;filename="+certificateFileName);
				ServletOutputStream out = response.getOutputStream();
				out.write(crlCertificateData.getCrlCertificate());
				out.flush();
				out.close();
				response.flushBuffer();
			}		
		}catch (Exception exp) {                                                                                                             
			Logger.logError(MODULE, "Error during Data Manager operation , reason : " + exp.getMessage());
			Object errorElements[] = EliteExceptionUtils.getFullStackTraceAsArray(exp);
			request.setAttribute("errorDetails", errorElements);            
			ActionMessage message = new ActionMessage("servermgr.servercertificate.delete.failure");                                                       
			ActionMessages messages = new ActionMessages();                                                                               
			messages.add("information", message);                                                                                           
			saveErrors(request, messages);                                                                                                  
		}
		return null;
	}
	public ActionForward duplicateCRL(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response)throws Exception{
		Logger.logTrace(MODULE,"Enter downloadFile method of"+getClass().getName());
		try{			
			if(request.getParameter("crlCertificateId")!=null){
				checkActionPermission(request, CREATE_CRL_ACTION_ALIAS);
				ServerCertificateForm serverCertificateForm = (ServerCertificateForm) form;
				String crlName=request.getParameter("crlName");
				CrlCertificateBLManager blManager=new CrlCertificateBLManager();
				
				CrlCertificateData crlCertificateData = blManager.getCertificateRevocationById(serverCertificateForm.getCrlCertificateId());
				
				String currentUser = ((SystemLoginForm) request.getSession().getAttribute("radiusLoginForm")).getUserId();
				crlCertificateData.setCreatedByStaffId(currentUser);
				crlCertificateData.setModifiedByStaffId(currentUser);
				crlCertificateData.setCrlCertificateName(crlName);
				IStaffData staffData = getStaffObject(((SystemLoginForm) request.getSession().getAttribute("radiusLoginForm")));
				blManager.createCertificateRevocation(crlCertificateData, staffData);

				request.setAttribute("responseUrl","/serverAllCertificates.do?method=initSearch");
				ActionMessage message = new ActionMessage("servermgr.servercertificate.duplicate");
				ActionMessages messages = new ActionMessages();
				messages.add("information", message);
				saveMessages(request, messages);
				return mapping.findForward(SUCCESS_FORWARD);
			}		
		}catch (Exception exp) {                                                                                                             
			Logger.logError(MODULE, "Error during Data Manager operation , reason : " + exp.getMessage());
			Object errorElements[] = EliteExceptionUtils.getFullStackTraceAsArray(exp);
			request.setAttribute("errorDetails", errorElements);            
			ActionMessage message = new ActionMessage("servermgr.servercertificate.delete.failure");                                                       
			ActionMessages messages = new ActionMessages();                                                                               
			messages.add("information", message);                                                                                           
			saveErrors(request, messages);                                                                                                  
		}
		return mapping.findForward(FAILURE_FORWARD);
	}
	public ActionForward deleteCrlCertificate(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response)throws Exception{
		Logger.logTrace(MODULE,"Enter Delete method of "+getClass().getName());
		try{
			checkActionPermission(request, DELETE_CRL_ACTION_ALIAS);									 
			String[] strSelectedIds = request.getParameterValues("selectCRL");

			IStaffData staffData = getStaffObject(((SystemLoginForm)request.getSession().getAttribute("radiusLoginForm")));

			CrlCertificateBLManager blManager = new CrlCertificateBLManager();
			blManager.deleteCertificateRevocationById(Arrays.asList(strSelectedIds), staffData);

			int strSelectedIdsLen = strSelectedIds.length;
			ServerCertificateForm serverCertificateForm = (ServerCertificateForm) form;
			long currentPageNumber = getCurrentPageNumberAfterDel(strSelectedIdsLen,serverCertificateForm.getPageNumber(),serverCertificateForm.getTotalPages(),serverCertificateForm.getTotalRecords());

			request.setAttribute("responseUrl", "/serverAllCertificates.do?method=initSearch&pageNumber="+currentPageNumber);    
			ActionMessage message = new ActionMessage("servermgr.crlcertificate.delete");
			ActionMessages messages = new ActionMessages();          
			messages.add("information", message);                    
			saveMessages(request,messages);      	
			return mapping.findForward(SUCCESS_FORWARD);
		}catch (ActionNotPermitedException e) {
			Logger.logError(MODULE, "Error during Data Manager operation ");
			Object errorElements[] = EliteExceptionUtils.getFullStackTraceAsArray(e);
			request.setAttribute("errorDetails", errorElements);
			ActionMessage message = new ActionMessage("general.user.restricted");
			ActionMessages messages = new ActionMessages();
			messages.add("information", message);
			saveErrors(request, messages);  
		}catch (Exception exp) {                                                                                                             
			Logger.logError(MODULE, "Error during Data Manager operation , reason : " + exp.getMessage());
			Object errorElements[] = EliteExceptionUtils.getFullStackTraceAsArray(exp);
			request.setAttribute("errorDetails", errorElements);            
			ActionMessage message = new ActionMessage("servermgr.crlcertificate.delete.failure");                                                       
			ActionMessages messages = new ActionMessages();                                                                               
			messages.add("information", message);                                                                                           
			saveErrors(request, messages);                                                                                                  
		}		
		return mapping.findForward(FAILURE_FORWARD);
	}
	public ActionForward viewCrl(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response)throws Exception{
		Logger.logTrace(MODULE,"Enter view method of "+getClass().getName());
		try{			
			String crlCertificateId="";
			if(request.getParameter("crlCertificateId")!=null){
				crlCertificateId=request.getParameter("crlCertificateId");
			}

			CrlCertificateBLManager blManager=new CrlCertificateBLManager();
			CrlCertificateData crlCertificateData=blManager.view(crlCertificateId);
			if (crlCertificateData.getCrlCertificate() != null) {
				setCrlCertificateDetail(crlCertificateData);
			}

			request.setAttribute("crlCertificateData", crlCertificateData);		
			return mapping.findForward(VIEWCRL_FORWARD);		
		}catch(Exception e){
			Logger.logError(MODULE, "Error during Data Manager operation , reason : " + e.getMessage());
			Object errorElements[] = EliteExceptionUtils.getFullStackTraceAsArray(e);
			request.setAttribute("errorDetails", errorElements);
			ActionMessage message = new ActionMessage("general.error");                                                         
			ActionMessages messages = new ActionMessages();                                                                                 
			messages.add("information", message);
			saveErrors(request, messages);			   
		}		
		return mapping.findForward(VIEWCRL_FORWARD);	
	}
}
