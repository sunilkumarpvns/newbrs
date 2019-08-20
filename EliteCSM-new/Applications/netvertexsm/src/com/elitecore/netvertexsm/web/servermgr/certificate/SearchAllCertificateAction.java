package com.elitecore.netvertexsm.web.servermgr.certificate;

import java.io.BufferedInputStream;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.security.KeyFactory;
import java.security.PrivateKey;
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

import org.apache.commons.collections.map.HashedMap;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.apache.struts.action.ActionMessage;
import org.apache.struts.action.ActionMessages;
import org.apache.struts.action.ActionRedirect;
import org.apache.struts.upload.FormFile;
import org.hibernate.exception.ConstraintViolationException;

import sun.security.x509.X509CRLImpl;

import com.elitecore.core.commons.tls.constant.PrivateKeyAlgo;
import com.elitecore.netvertexsm.blmanager.core.base.GenericBLManager;
import com.elitecore.netvertexsm.blmanager.servermgr.certificate.CrlCertificateBLManager;
import com.elitecore.netvertexsm.blmanager.servermgr.certificate.ServerCertificateBLManager;
import com.elitecore.netvertexsm.blmanager.servermgr.certificate.TrustedCertificateBLManager;
import com.elitecore.netvertexsm.datamanager.DataManagerException;
import com.elitecore.netvertexsm.datamanager.core.exceptions.auhorization.ActionNotPermitedException;
import com.elitecore.netvertexsm.datamanager.core.system.staff.data.IStaffData;
import com.elitecore.netvertexsm.datamanager.core.util.PageList;
import com.elitecore.netvertexsm.datamanager.servermgr.certificate.data.CrlCertificateData;
import com.elitecore.netvertexsm.datamanager.servermgr.certificate.data.ServerCertificateData;
import com.elitecore.netvertexsm.datamanager.servermgr.certificate.data.TrustedCertificateData;
import com.elitecore.netvertexsm.util.constants.CertificateRemarks;
import com.elitecore.netvertexsm.util.constants.ConfigConstant;
import com.elitecore.netvertexsm.util.exception.CertificateValidationException;
import com.elitecore.netvertexsm.util.exception.EliteExceptionUtils;
import com.elitecore.netvertexsm.util.exception.InvalidPrivateKeyException;
import com.elitecore.netvertexsm.web.core.base.BaseWebDispatchAction;
import com.elitecore.netvertexsm.web.core.system.cache.ConfigManager;
import com.elitecore.netvertexsm.web.core.system.login.forms.SystemLoginForm;
import com.elitecore.netvertexsm.web.servermgr.certificate.form.CrlCertificateForm;
import com.elitecore.netvertexsm.web.servermgr.certificate.form.ServerCertificateForm;
import com.elitecore.netvertexsm.web.servermgr.certificate.form.TrustedCertificateForm;
import com.elitecore.netvertexsm.ws.logger.Logger;

public class SearchAllCertificateAction extends BaseWebDispatchAction {

	private static final String MODULE = "SERVER_CERTIFICATE";
	
	private static final String CREATE_ACTION_ALIAS = ConfigConstant.CREATE_SERVER_CERTIFICATE;
	private static final String UPDATE_ACTION_ALIAS = ConfigConstant.UPDATE_SERVER_CERTIFICATE;
	private static final String DELETE_ACTION_ALIAS = ConfigConstant.DELETE_SERVER_CERTIFICATE;
	private static final String SEARCH_ACTION_ALIAS = ConfigConstant.SEARCH_SERVER_CERTIFICATE;
	
	private static final String SUCCESS_FORWARD = "success";
	private static final String FAILURE_FORWARD = "failure";
	
	private static final String SEARCH_FORWARD 	= "searchAllCertificate";
	private static final String CREATE_FORWARD 	= "createServerCertificate";
	private static final String UPDATE_FORWARD 	= "updateServerCertificate";
	private static final String VIEW_FORWARD 	= "viewServerCertificate";
	private static final String SHOWALL_FORWARD = "showAllServerCertificate";

	public ActionForward initSearch(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
		Logger.logInfo(MODULE, "Entered initSearch method of " + getClass().getName());
		return doSearch(mapping, form, request, response);
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
	private ActionForward doSearch(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
		Logger.logInfo(MODULE, "Entered doSearch method of " + getClass().getName());
		try {
			
			// BEGIN : SERVER CERTIFICATE 
			ServerCertificateData serverCertificateData = new ServerCertificateData();
			ServerCertificateForm serverCertificateForm = (ServerCertificateForm) form;
			String strCertificateName = serverCertificateForm.getServerCertificateName();
			String ajaxServerCertificateName = request.getParameter("ajaxServerCertificateName");
			Logger.logDebug(MODULE,"strCertificateName : "+strCertificateName);
			Logger.logDebug(MODULE,"ajaxServerCertificateName : "+ajaxServerCertificateName);
			String  requestServerCertificateName = (String)request.getAttribute("ajaxServerCertificateName"); 
			if (strCertificateName != null) {
				serverCertificateData.setServerCertificateName(strCertificateName);
			} else if(ajaxServerCertificateName!=null){
				serverCertificateData.setServerCertificateName(ajaxServerCertificateName);
				request.setAttribute("ajaxServerCertificateName", ajaxServerCertificateName);
			}else if(requestServerCertificateName!=null){
				serverCertificateData.setServerCertificateName(requestServerCertificateName);
				request.setAttribute("ajaxServerCertificateName", ajaxServerCertificateName);
			}else{
				serverCertificateData.setServerCertificateName("");
			}

			IStaffData staffData = getStaffObject(((SystemLoginForm) request.getSession().getAttribute("radiusLoginForm")));
			Integer pageSize = Integer.parseInt(ConfigManager.get("TOTAL_ROW"));
			int requiredPageNo = 1;
			Map infoMap = new HashedMap();
			infoMap.put("pageNo", requiredPageNo);
			infoMap.put("pageSize", pageSize);
			infoMap.put("actionAlias", SEARCH_ACTION_ALIAS);
			infoMap.put("staffData", staffData);

			ServerCertificateBLManager blManager = new ServerCertificateBLManager();
			PageList pageList = blManager.search(serverCertificateData, infoMap);		

			List<ServerCertificateData> list=pageList.getListData();
			Logger.logDebug(MODULE, "Server Certificate List : "+list.size());
			for(ServerCertificateData data : list){
				setServerCertificateDetail(data);
			}

			serverCertificateForm.setServerCertificateName(strCertificateName);
			serverCertificateForm.setPageNumber(pageList.getCurrentPage());
			serverCertificateForm.setTotalPages(pageList.getTotalPages());
			serverCertificateForm.setTotalRecords(pageList.getTotalItems());
			serverCertificateForm.setListServerCertificate(pageList.getListData());
			serverCertificateForm.setServerCertificateList(pageList.getCollectionData());
			request.setAttribute("searchServerCertificateForm",serverCertificateForm);
			
			// END : SERVER CERTIFICATE
			
			// BEGIN : CRL CERTIFICATE
			CrlCertificateData crlCertificateData = new CrlCertificateData();
			CrlCertificateForm crlCertificateForm = new CrlCertificateForm();
			crlCertificateForm.setCrlCertificateName(serverCertificateData.getServerCertificateName());
			pageSize=Integer.parseInt(ConfigManager.get("TOTAL_ROW"));
			if(request.getParameter("pageNo") != null){
				requiredPageNo = Integer.parseInt(String.valueOf(request.getParameter("pageNo")));
			}else{
				requiredPageNo = new Long(crlCertificateForm.getPageNumber()).intValue();
			}
			if(requiredPageNo == 0)
				requiredPageNo =1;

			String strCrlCertificateName=crlCertificateForm.getCrlCertificateName();

			if(strCrlCertificateName!=null){
				crlCertificateData.setCrlCertificateName(strCrlCertificateName);
			}else{
				crlCertificateData.setCrlCertificateName("");
			}

			//			IStaffData staffData = getStaffObject(((SystemLoginForm)request.getSession().getAttribute("radiusLoginForm")));

			infoMap= new HashedMap();
			infoMap.put("pageNo", requiredPageNo);
			infoMap.put("pageSize", pageSize);
			//			infoMap.put("actionAlias", SEARCH_ACTION_ALIAS);
			//			infoMap.put("staffData", staffData);

			CrlCertificateBLManager crlBlManager = new CrlCertificateBLManager();

			pageList=crlBlManager.search(crlCertificateData, infoMap);

			List<CrlCertificateData> crlList=pageList.getListData();
			Logger.logDebug(MODULE, "crlList : "+crlList.size());
			for(CrlCertificateData data : crlList){
				setCrlCertificateDetail(data);
			}

			crlCertificateForm.setCrlCertificateName(strCrlCertificateName);
			crlCertificateForm.setPageNumber(pageList.getCurrentPage());
			crlCertificateForm.setTotalPages(pageList.getTotalPages());
			crlCertificateForm.setTotalRecords(pageList.getTotalItems());
			crlCertificateForm.setListCrlCertificate(pageList.getListData());
			crlCertificateForm.setCrlCertificateList(pageList.getCollectionData());

			request.setAttribute("crlCertificateForm", crlCertificateForm);
			// END :  CRL CERTIFICATE
			
			// BEGIN : TRUSTED CERTIFICATE

			TrustedCertificateForm trustedCertificateForm=new TrustedCertificateForm();
			trustedCertificateForm.setTrustedCertificateName(serverCertificateData.getServerCertificateName());

			pageSize=Integer.parseInt(ConfigManager.get("TOTAL_ROW"));
			if(request.getParameter("pageNo") != null){
				requiredPageNo = Integer.parseInt(String.valueOf(request.getParameter("pageNo")));
			}else{
				requiredPageNo = new Long(trustedCertificateForm.getPageNumber()).intValue();
			}
			if(requiredPageNo == 0)
				requiredPageNo =1;

			TrustedCertificateData trustedCertificateData=new TrustedCertificateData();
			String strTrustedCertificateName=trustedCertificateForm.getTrustedCertificateName();

			if(strTrustedCertificateName!=null){
				trustedCertificateData.setTrustedCertificateName(strTrustedCertificateName);
			}else{
				trustedCertificateData.setTrustedCertificateName("");
			}

			staffData = getStaffObject(((SystemLoginForm)request.getSession().getAttribute("radiusLoginForm")));

			infoMap= new HashedMap();
			infoMap.put("pageNo", requiredPageNo);
			infoMap.put("pageSize", pageSize);

			TrustedCertificateBLManager trustedBlManager = new TrustedCertificateBLManager();

			pageList=trustedBlManager.search(trustedCertificateData, infoMap);
			List<TrustedCertificateData> trustedist=pageList.getListData();
			Logger.logDebug(MODULE, "Trustedist : "+trustedist.size());
			for(TrustedCertificateData data : trustedist){
				setServerCertificateDetail(data);
			}

			trustedCertificateForm.setTrustedCertificateName(strTrustedCertificateName);
			trustedCertificateForm.setPageNumber(pageList.getCurrentPage());
			trustedCertificateForm.setTotalPages(pageList.getTotalPages());
			trustedCertificateForm.setTotalRecords(pageList.getTotalItems());
			trustedCertificateForm.setListTrustedCertificate(pageList.getListData());
			trustedCertificateForm.setTrustedCertificateList(pageList.getCollectionData());

			request.setAttribute("trustedCertificateForm", trustedCertificateForm);

			return mapping.findForward(SEARCH_FORWARD);

		} catch (Exception exp) {
			Logger.logError(MODULE,"Error during Data Manager operation , reason : "+ exp.getMessage());
			//			Logger.logTrace(MODULE, exp);
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
			return doSearch(mapping, form, request, response);
		} catch (ActionNotPermitedException e) {
			Logger.logError(MODULE, "Error during Data Manager operation ");
			//			Logger.logTrace(MODULE, e);
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
			ServerCertificateForm serverCertificateForm = (ServerCertificateForm) form;

			if (request.getParameter("name") != null&& !request.getParameter("name").trim().equalsIgnoreCase("")) {
				serverCertificateForm.setServerCertificateName(request.getParameter("name"));
			}
			return mapping.findForward(CREATE_FORWARD);
		} catch (ActionNotPermitedException e) {
			Logger.logError(MODULE, "Error during Data Manager operation ");
			//			Logger.logTrace(MODULE, e);
			Object errorElements[] = EliteExceptionUtils.getFullStackTraceAsArray(e);
			request.setAttribute("errorDetails", errorElements);
			ActionMessage message = new ActionMessage("general.user.restricted");
			ActionMessages messages = new ActionMessages();
			messages.add("information", message);
			saveErrors(request, messages);
		} catch (Exception e) {
			Logger.logError(MODULE,"Error during Data Manager operation , reason : "+ e.getMessage());
			//			Logger.logTrace(MODULE, e);
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

			String currentUser = ((SystemLoginForm) request.getSession().getAttribute("radiusLoginForm")).getUserId();
			serverCertificateData.setCreatedByStaffId(Long.parseLong(currentUser));
			serverCertificateData.setModifiedByStaffId(Long.parseLong(currentUser));

			IStaffData staffData = getStaffObject(((SystemLoginForm) request.getSession().getAttribute("radiusLoginForm")));
			ServerCertificateBLManager blManager = new ServerCertificateBLManager();
			blManager.create(serverCertificateData, staffData,CREATE_ACTION_ALIAS);
			
			request.setAttribute("responseUrl","/serverCertificate.do?method=initSearch");
			ActionMessage message = new ActionMessage("servermgr.servercertificate.create");
			ActionMessages messages = new ActionMessages();
			messages.add("information", message);
			saveMessages(request, messages);
			return mapping.findForward(SUCCESS_FORWARD);

		}catch(InvalidPrivateKeyException e){			
			Logger.logError(MODULE,"Error during Data Manager operation , reason : "+ e.getMessage());
			//Logger.logTrace(MODULE, e);
			Object errorElements[] = EliteExceptionUtils.getFullStackTraceAsArray(e);
			request.setAttribute("errorDetails", errorElements);
			ActionMessage message = new ActionMessage("servermgr.servercertificate.create.invalidkey");
			ActionMessages messages = new ActionMessages();
			messages.add("information", message);
			saveErrors(request, messages);		

		} catch (CertificateValidationException exp) {
			Logger.logError(MODULE,"Error during Data Manager operation , reason : "+ exp.getMessage());
			//Logger.logTrace(MODULE, exp);
			Object errorElements[] = EliteExceptionUtils.getFullStackTraceAsArray(exp);
			request.setAttribute("errorDetails", errorElements);
			ActionMessage message = new ActionMessage("servermgr.servercertificate.create.keycertificatemismatch");
			ActionMessages messages = new ActionMessages();
			messages.add("information", message);
			saveErrors(request, messages);

		} catch (CertificateException exp) {
			Logger.logError(MODULE,"Error during Data Manager operation , reason : "+ exp.getMessage());
			//Logger.logTrace(MODULE, exp);
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
			//Logger.logTrace(MODULE, exp);
			Object errorElements[] = EliteExceptionUtils.getFullStackTraceAsArray(exp);
			request.setAttribute("errorDetails", errorElements);
			ActionMessage message = new ActionMessage("servermgr.servercertificate.create.failure");
			ActionMessages messages = new ActionMessages();
			messages.add("information", message);
			saveErrors(request, messages);
		}
		return mapping.findForward(FAILURE_FORWARD);
	}

	public ActionForward initUpdate(ActionMapping mapping, ActionForm form,HttpServletRequest request, HttpServletResponse response)throws Exception {
		Logger.logInfo(MODULE, "Entered iniUpdate method of "+ getClass().getName());
		try {
			checkActionPermission(request, UPDATE_ACTION_ALIAS);
			ServerCertificateForm serverCertificateForm = (ServerCertificateForm) form;

			ServerCertificateBLManager blManager = new ServerCertificateBLManager();
			ServerCertificateData serverCertificateData = blManager.initUpdate(serverCertificateForm.getServerCertificateId());
			convertBeanToForm(serverCertificateForm, serverCertificateData);

			setServerCertificateDetail(serverCertificateData);
			request.setAttribute("serverCertificateData", serverCertificateData);
			request.setAttribute("serverCertificateForm", serverCertificateForm);
			return mapping.findForward(UPDATE_FORWARD);
		} catch (ActionNotPermitedException e) {
			Logger.logError(MODULE, "Error during Data Manager operation ");
			//			Logger.logTrace(MODULE, e);
			Object errorElements[] = EliteExceptionUtils
					.getFullStackTraceAsArray(e);
			request.setAttribute("errorDetails", errorElements);
			ActionMessages messages = new ActionMessages();
			ActionMessage message = new ActionMessage("general.user.restricted");
			messages.add("information", message);
			saveErrors(request, messages);
		} catch (Exception authExp) {
			Logger.logError(MODULE,"Error during Data Manager operation , reason :"+ authExp.getMessage());
			//			Logger.logTrace(MODULE, authExp);
			ActionMessages messages = new ActionMessages();
			ActionMessage message = new ActionMessage("general.error");
			messages.add("information", message);
			saveErrors(request, messages);
		}
		return mapping.findForward(FAILURE_FORWARD);
	}

	public ActionForward update(ActionMapping mapping, ActionForm form,HttpServletRequest request, HttpServletResponse response)throws Exception {
		Logger.logInfo(MODULE, "Entered update method of "+ getClass().getName());
		try {
			checkActionPermission(request, UPDATE_ACTION_ALIAS);

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

			String currentUser = ((SystemLoginForm) request.getSession().getAttribute("radiusLoginForm")).getUserId();
			serverCertificateData.setModifiedByStaffId(Long.parseLong(currentUser));
			serverCertificateData.setModifiedDate(getCurrentTimeStemp());

			IStaffData staffData = getStaffObject(((SystemLoginForm) request.getSession().getAttribute("radiusLoginForm")));
			ServerCertificateBLManager blManager = new ServerCertificateBLManager();
			blManager.update(serverCertificateData, staffData,UPDATE_ACTION_ALIAS);			

			request.setAttribute("serverCertificateData", serverCertificateData);
			request.setAttribute("serverCertificateForm", serverCertificateForm);
			request.setAttribute("responseUrl","/serverCertificate.do?method=initSearch");
			ActionMessage message = new ActionMessage("servermgr.servercertificate.update");
			ActionMessages messages = new ActionMessages();
			messages.add("information", message);
			saveMessages(request, messages);
			Logger.logInfo(MODULE, "Returning success forward from "+ getClass().getName());
			return mapping.findForward(SUCCESS_FORWARD);
		}catch(InvalidPrivateKeyException e){			
			Logger.logError(MODULE,"Error during Data Manager operation , reason : "+ e.getMessage());
			//Logger.logTrace(MODULE, e);
			Object errorElements[] = EliteExceptionUtils.getFullStackTraceAsArray(e);
			request.setAttribute("errorDetails", errorElements);
			ActionMessage message = new ActionMessage("servermgr.servercertificate.create.invalidkey");
			ActionMessages messages = new ActionMessages();
			messages.add("information", message);
			saveErrors(request, messages);		

		} catch (CertificateValidationException exp) {
			Logger.logError(MODULE,"Error during Data Manager operation , reason : "+ exp.getMessage());
			//Logger.logTrace(MODULE, exp);
			Object errorElements[] = EliteExceptionUtils.getFullStackTraceAsArray(exp);
			request.setAttribute("errorDetails", errorElements);
			ActionMessage message = new ActionMessage("servermgr.servercertificate.create.keycertificatemismatch");
			ActionMessages messages = new ActionMessages();
			messages.add("information", message);
			saveErrors(request, messages);

		} catch (CertificateException exp) {
			Logger.logError(MODULE,"Error during Data Manager operation , reason : "+ exp.getMessage());
			//Logger.logTrace(MODULE, exp);
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
			//Logger.logTrace(MODULE, exp);
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
			String[] strSelectedIds = request.getParameterValues("select");
			List<Long> listSelectedIDs = new ArrayList<Long>();
			if (strSelectedIds != null) {
				for (int i = 0; i < strSelectedIds.length; i++) {
					listSelectedIDs.add(Long.parseLong(strSelectedIds[i]));
				}
			}
			IStaffData staffData = getStaffObject(((SystemLoginForm) request.getSession().getAttribute("radiusLoginForm")));
			ServerCertificateBLManager blManager = new ServerCertificateBLManager();
			blManager.delete(listSelectedIDs, staffData, DELETE_ACTION_ALIAS);
			int strSelectedIdsLen = strSelectedIds.length;
			ServerCertificateForm serverCertificateForm = (ServerCertificateForm) form;
			long currentPageNumber = getCurrentPageNumberAfterDel(strSelectedIdsLen, serverCertificateForm.getPageNumber(),serverCertificateForm.getTotalPages(),serverCertificateForm.getTotalRecords());

			request.setAttribute("responseUrl","/serverCertificate.do?method=initSearch&pageNumber="+ currentPageNumber);
			ActionMessage message = new ActionMessage("servermgr.servercertificate.delete");
			ActionMessages messages1 = new ActionMessages();
			messages1.add("information", message);
			saveMessages(request, messages1);
			return mapping.findForward(SUCCESS_FORWARD);
		} catch (ActionNotPermitedException e) {
			Logger.logError(MODULE, "Error during Data Manager operation ");
			//			Logger.logTrace(MODULE, e);
			Object errorElements[] = EliteExceptionUtils.getFullStackTraceAsArray(e);
			request.setAttribute("errorDetails", errorElements);
			ActionMessage message = new ActionMessage("general.user.restricted");
			ActionMessages messages = new ActionMessages();
			messages.add("information", message);
			saveErrors(request, messages);
		} catch (Exception exp) {
			Logger.logError(MODULE,"Error during Data Manager operation , reason : "+ exp.getMessage());
			//			Logger.logTrace(MODULE, exp);
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

			Long serverCertificateId = 0L;
			if (request.getParameter("serverCertificateId") != null) {
				serverCertificateId = Long.parseLong(request.getParameter("serverCertificateId"));
			}
			ServerCertificateData serverCertificateData = blManager.view(serverCertificateId);
			setServerCertificateDetail(serverCertificateData);
			setPrivateKeyDetail(serverCertificateData);

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
			serverCertificateForm.setTotalPages(pageList.getTotalPages());
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
			//			Logger.logTrace(MODULE, e);
			Object errorElements[] = EliteExceptionUtils.getFullStackTraceAsArray(e);
			request.setAttribute("errorDetails", errorElements);
			ActionMessage message = new ActionMessage("servermgr.servercertificate.alreadyinuse");
			ActionMessages messages = new ActionMessages();
			messages.add("information", message);
			saveErrors(request, messages);
			return mapping.findForward(FAILURE_FORWARD);
		} catch (ActionNotPermitedException e) {
			Logger.logError(MODULE, "Error during Data Manager operation ");
			//			Logger.logTrace(MODULE, e);
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
			//			Logger.logTrace(MODULE, exp);
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
				Long serverCertificateId=Long.parseLong(request.getParameter("serverCertificateId"));
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
			//			Logger.logTrace(MODULE, exp);
			Object errorElements[] = EliteExceptionUtils.getFullStackTraceAsArray(exp);
			request.setAttribute("errorDetails", errorElements);
			ActionMessage message = new ActionMessage("servermgr.servercertificate.delete.failure");
			ActionMessages messages = new ActionMessages();
			messages.add("information", message);
			saveErrors(request, messages);
		}
		return null;
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
	 * Convert ServerCertificateData to ServerCertificateForm to show details on JSP page.
	 * @param form
	 * @param data
	 * @throws FileNotFoundException
	 * @throws IOException
	 */
	private void convertBeanToForm(ServerCertificateForm form,ServerCertificateData data) throws FileNotFoundException,IOException {
		form.setServerCertificateId(data.getServerCertificateId());
		form.setServerCertificateName(data.getServerCertificateName());
		form.setPrivateKeyAlgorithm(data.getPrivateKeyAlgorithm());		
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
	private void checkForPrivateKeyAndPublicKey(Certificate publicKey,PrivateKey privateKey, PrivateKeyAlgo privateKeyAlgo)throws CertificateValidationException {
		try{
			if(publicKey==null || privateKey==null){
				throw new CertificateValidationException("Public Certificate and Private Key are mismatch", CertificateRemarks.CERTIFICATE_KEY_MISMATCH);
			}
			String plainText="elitecore";

			byte[] plainTextByte=plainText.getBytes();
			Cipher  cipherInstance = Cipher.getInstance(privateKeyAlgo.name);
			cipherInstance.init(Cipher.ENCRYPT_MODE, publicKey);
			byte[] encryptedData = cipherInstance.doFinal(plainTextByte);

			cipherInstance.init(Cipher.DECRYPT_MODE, privateKey);
			byte [] decryptedTextByte = cipherInstance.doFinal(encryptedData);

			String decrytedText=new String(decryptedTextByte);

			if(!plainText.equals(decrytedText)){
				throw new CertificateValidationException("Public Certificate and Private Key are mismatch", CertificateRemarks.CERTIFICATE_KEY_MISMATCH);	
			}

		}catch(CertificateValidationException e){
			throw e;
		}catch(Exception e){
			throw new CertificateValidationException("Public Certificate and Private Key are mismatch", CertificateRemarks.CERTIFICATE_KEY_MISMATCH,e);
		}
	}

	/**
	 * Generate Certificate and return its collection.
	 * @param certFormFile
	 * @return
	 * @throws Exception
	 */
	//FIXME NEED TO MOVE THIS METHOD IN UTILITY.
	private Collection<? extends Certificate> generateCertificate(InputStream fileInputStream) throws CertificateException {
		if(fileInputStream==null){
			throw new CertificateException("Invalid Public Certificate, fileInputStream is null");
		}
		try{
			CertificateFactory cf = CertificateFactory.getInstance("X.509");

			Collection<? extends Certificate> certList = cf.generateCertificates(fileInputStream);
			if(certList == null || certList.isEmpty()){
				throw new CertificateException("Invalid Public Certificate, certList is Empty");
			}
			return certList;
		}catch(CertificateException e){
			throw new CertificateException("Invalid Public Certificate, "+e.getMessage());
		}
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
	private PrivateKey generatePrivateKey(InputStream keyPath,PrivateKeyAlgo privateKeyAlgo, String privateKeyPassword) throws InvalidPrivateKeyException {
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
	private void setServerCertificateDetail(ServerCertificateData data) throws InvalidPrivateKeyException{
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
	private void setPrivateKeyDetail(ServerCertificateData data) throws InvalidPrivateKeyException{

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
	
	private void setCrlCertificateDetail(CrlCertificateData data) throws Exception{
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
				}

				if(x509Crl.getSigAlgName()!=null){
					data.setSignatureAlgo(x509Crl.getSigAlgName());	
				}	
				break;
			}
		}
	}

	private void setServerCertificateDetail(TrustedCertificateData data) throws Exception{
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
}
