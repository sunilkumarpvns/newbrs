package com.elitecore.netvertexsm.web.servermgr.certificate;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.security.cert.CRL;
import java.security.cert.CRLException;
import java.security.cert.CertificateException;
import java.security.cert.CertificateFactory;
import java.security.cert.X509CRLEntry;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.collections.map.HashedMap;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.apache.struts.action.ActionMessage;
import org.apache.struts.action.ActionMessages;
import org.hibernate.exception.ConstraintViolationException;

import sun.security.x509.X509CRLImpl;

import com.elitecore.netvertexsm.blmanager.core.base.GenericBLManager;
import com.elitecore.netvertexsm.blmanager.servermgr.certificate.CrlCertificateBLManager;
import com.elitecore.netvertexsm.datamanager.core.exceptions.auhorization.ActionNotPermitedException;
import com.elitecore.netvertexsm.datamanager.core.exceptions.constraintviolation.DuplicateInstanceNameFoundException;
import com.elitecore.netvertexsm.datamanager.core.system.staff.data.IStaffData;
import com.elitecore.netvertexsm.datamanager.core.util.PageList;
import com.elitecore.netvertexsm.datamanager.servermgr.certificate.data.CrlCertificateData;
import com.elitecore.netvertexsm.util.constants.ConfigConstant;
import com.elitecore.netvertexsm.util.exception.EliteExceptionUtils;
import com.elitecore.netvertexsm.web.core.base.BaseWebDispatchAction;
import com.elitecore.netvertexsm.web.core.system.cache.ConfigManager;
import com.elitecore.netvertexsm.web.core.system.login.forms.SystemLoginForm;
import com.elitecore.netvertexsm.web.servermgr.certificate.form.CrlCertificateForm;
import com.elitecore.netvertexsm.ws.logger.Logger;

public class CrlCertificateAction extends BaseWebDispatchAction{

	private static final String MODULE = CrlCertificateAction.class.getSimpleName();
	
	private static final String SUCCESS_FORWARD	= "success";
	private static final String FAILURE_FORWARD	= "failure";
	private static final String SEARCH_FORWARD 	= "searchCrlCertificate";
	private static final String CREATE_FORWARD 	= "createCrlCertificate";
	private static final String UPDATE_FORWARD 	= "updateCrlCertificate";
	private static final String SHOWALL_FORWARD = "showAllCrlCertificate";
	private static final String VIEW_FORWARD 	= "viewCrlCertificate";
	
	private static final String CREATE_ACTION_ALIAS = ConfigConstant.CREATE_CERTIFICATE_REVOCATION_LIST;
	private static final String UPDATE_ACTION_ALIAS = ConfigConstant.UPDATE_CERTIFICATE_REVOCATION_LIST;
	private static final String DELETE_ACTION_ALIAS = ConfigConstant.DELETE_CERTIFICATE_REVOCATION_LIST;
	private static final String SEARCH_ACTION_ALIAS = ConfigConstant.SEARCH_CERTIFICATE_REVOCATION_LIST;

	public ActionForward initSearch(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response)throws Exception{
		Logger.logTrace(MODULE,"Enter initSearch method of "+getClass().getName());
		return searchFun(mapping, form, request, response);		
	}

	private ActionForward searchFun(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception{
		try{
			CrlCertificateData crlCertificateData = new CrlCertificateData();
			CrlCertificateForm crlCertificateForm = (CrlCertificateForm)form;

			Integer pageSize=Integer.parseInt(ConfigManager.get("TOTAL_ROW"));
			int requiredPageNo;
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

			Map infoMap= new HashedMap();
			infoMap.put("pageNo", requiredPageNo);
			infoMap.put("pageSize", pageSize);
			//			infoMap.put("actionAlias", SEARCH_ACTION_ALIAS);
			//			infoMap.put("staffData", staffData);

			CrlCertificateBLManager blManager=new CrlCertificateBLManager();

			PageList pageList=blManager.search(crlCertificateData, infoMap);

			List<CrlCertificateData> list=pageList.getListData();

			for(CrlCertificateData data : list){
				setCrlCertificateDetail(data);
			}

			crlCertificateForm.setCrlCertificateName(strCrlCertificateName);
			crlCertificateForm.setPageNumber(pageList.getCurrentPage());
			crlCertificateForm.setTotalPages(pageList.getTotalPages());
			crlCertificateForm.setTotalRecords(pageList.getTotalItems());
			crlCertificateForm.setListCrlCertificate(pageList.getListData());
			crlCertificateForm.setCrlCertificateList(pageList.getCollectionData());

			request.setAttribute("crlCertificateForm", crlCertificateForm);
			return mapping.findForward(SEARCH_FORWARD);		
		}catch(Exception managerExp){
			Logger.logError(MODULE, "Error during Data Manager operation , reason : " + managerExp.getMessage());
			Object errorElements[] = EliteExceptionUtils.getFullStackTraceAsArray(managerExp);
			request.setAttribute("errorDetails", errorElements);
			ActionMessage message = new ActionMessage("servermgr.crlcertificate.search.failure");                                                         
			ActionMessages messages = new ActionMessages();                                                                                 
			messages.add("information", message);
			saveErrors(request, messages);			   
		}		       
		return mapping.findForward(FAILURE_FORWARD);
	}

	public ActionForward search(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response)throws Exception{
		Logger.logTrace(MODULE,"Enter Search method of "+getClass().getName());
		try{
			checkActionPermission(request, SEARCH_ACTION_ALIAS);
			return searchFun(mapping, form, request, response);			
		}catch (ActionNotPermitedException e) {
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

	public ActionForward initCreate(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response)throws Exception{
		Logger.logTrace(MODULE,"Enter initCreate method of "+getClass().getName());
		try{
			checkActionPermission(request, CREATE_ACTION_ALIAS);
			CrlCertificateForm crlCertificateForm=(CrlCertificateForm) form;

			if(request.getParameter("name")!=null && !request.getParameter("name").trim().equalsIgnoreCase("")){
				crlCertificateForm.setCrlCertificateName(request.getParameter("name"));
			}

			request.setAttribute("crlCertificateForm", crlCertificateForm);
			return mapping.findForward(CREATE_FORWARD);
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

	public ActionForward create(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response)throws Exception{
		Logger.logTrace(MODULE,"Enter create method of "+getClass().getName());
		try{		
			checkActionPermission(request, CREATE_ACTION_ALIAS);

			CrlCertificateForm crlCertificateForm=(CrlCertificateForm)form;

			String currentUser = ((SystemLoginForm)request.getSession().getAttribute("radiusLoginForm")).getUserId();

			CrlCertificateData crlCertificateData=new CrlCertificateData();
			//generate CRL			
			generateCrlCertificate(crlCertificateForm.getCrlCert().getInputStream());

			convertFormToBean(crlCertificateForm,crlCertificateData);

			/*for(CrlCertificateData data : crlCertificateDatas){*/
			crlCertificateData.setCreatedByStaffId(Long.parseLong(currentUser));
			crlCertificateData.setModifiedByStaffId(Long.parseLong(currentUser));
			/*}		*/

			IStaffData staffData = getStaffObject(((SystemLoginForm)request.getSession().getAttribute("radiusLoginForm")));
			CrlCertificateBLManager blManager=new CrlCertificateBLManager();
			blManager.create(crlCertificateData,staffData,CREATE_ACTION_ALIAS);			

			request.setAttribute("responseUrl", "/searchAllCertificate.do?method=initSearch");    
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

	public ActionForward initUpdate(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response)throws Exception{
		Logger.logTrace(MODULE, "Enter initUpdate method of " + getClass().getName());
		try {
			checkActionPermission(request, UPDATE_ACTION_ALIAS);
			CrlCertificateForm crlCertificateForm=(CrlCertificateForm)form;
			CrlCertificateBLManager blManager=new CrlCertificateBLManager();

			CrlCertificateData crlCertificateData=blManager.initUpdate(crlCertificateForm.getCrlCertificateId());
			convertBeanToForm(crlCertificateForm,crlCertificateData);
			setCrlCertificateDetail(crlCertificateData);
			request.setAttribute("crlCertificateData",crlCertificateData);
			request.setAttribute("crlCertificateForm",crlCertificateForm);			
			return mapping.findForward(UPDATE_FORWARD);			
		}catch (ActionNotPermitedException e) {
			Logger.logError(MODULE, "Error during Data Manager operation ");
			Object errorElements[] = EliteExceptionUtils.getFullStackTraceAsArray(e);
			request.setAttribute("errorDetails", errorElements);
			ActionMessage message = new ActionMessage("general.user.restricted");
			ActionMessages messages = new ActionMessages();
			messages.add("information", message);
			saveErrors(request, messages);  
		}catch (Exception authExp) {                                                                                           
			Logger.logError(MODULE, "Error during Data Manager operation , reason :" + authExp.getMessage());                              
			ActionMessage message = new ActionMessage("general.error");
			ActionMessages messages = new ActionMessages();
			messages.add("information", message);
			saveErrors(request, messages);
		}   		 
		return mapping.findForward(FAILURE_FORWARD);			
	}

	public ActionForward update(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response)throws Exception{
		Logger.logTrace(MODULE, "Entered update method of " + getClass().getName());
		try {
			checkActionPermission(request, UPDATE_ACTION_ALIAS);

			CrlCertificateForm crlCertificateForm = (CrlCertificateForm)form;			

			String currentUser = ((SystemLoginForm)request.getSession().getAttribute("radiusLoginForm")).getUserId();

			//generate CRL			
			generateCrlCertificate(crlCertificateForm.getCrlCert().getInputStream());


			CrlCertificateData crlCertificateData=new CrlCertificateData();
			convertFormToBean(crlCertificateForm,crlCertificateData);

			crlCertificateData.setModifiedByStaffId(Long.parseLong(currentUser));
			crlCertificateData.setModifiedDate(getCurrentTimeStemp());

			IStaffData staffData = getStaffObject(((SystemLoginForm)request.getSession().getAttribute("radiusLoginForm")));
			CrlCertificateBLManager blManager = new CrlCertificateBLManager();
			blManager.update(crlCertificateData,staffData,UPDATE_ACTION_ALIAS);

			request.setAttribute("crlCertificateForm",crlCertificateForm);
			request.setAttribute("responseUrl", "/searchAllCertificate.do?method=initSearch");    
			ActionMessage message = new ActionMessage("servermgr.crlcertificate.update");
			ActionMessages messages = new ActionMessages();          
			messages.add("information", message);                    
			saveMessages(request,messages);         				   
			Logger.logInfo(MODULE, "Returning success forward from " + getClass().getName()); 

			return mapping.findForward(SUCCESS_FORWARD);

		}catch(CRLException e){
			Logger.logError(MODULE,"Error during Data Manager operation , reason : "+ e.getMessage());
			Object errorElements[] = EliteExceptionUtils.getFullStackTraceAsArray(e);
			request.setAttribute("errorDetails", errorElements);
			ActionMessage message = new ActionMessage("servermgr.crlcertificate.create.invalidcertificate");
			ActionMessages messages = new ActionMessages();
			messages.add("information", message);
			saveErrors(request, messages);
		}catch (DuplicateInstanceNameFoundException dpfExp) {
			Logger.logError(MODULE, "Returning error forward from " + getClass().getName());
			Object errorElements[] = EliteExceptionUtils.getFullStackTraceAsArray(dpfExp);
			request.setAttribute("errorDetails", errorElements);
			ActionMessage message = new ActionMessage("servermgr.crlcertificate.update.duplicate");
			ActionMessages messages = new ActionMessages();
			messages.add("information",message);
			saveErrors(request,messages);		
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

	public ActionForward view(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response)throws Exception{
		Logger.logTrace(MODULE,"Enter view method of "+getClass().getName());
		try{			
			Long crlCertificateId=0L;
			if(request.getParameter("crlCertificateId")!=null){
				crlCertificateId=Long.parseLong(request.getParameter("crlCertificateId"));
			}

			CrlCertificateBLManager blManager=new CrlCertificateBLManager();
			CrlCertificateData crlCertificateData=blManager.view(crlCertificateId);
			setCrlCertificateDetail(crlCertificateData);

			request.setAttribute("crlCertificateData", crlCertificateData);		
			return mapping.findForward(VIEW_FORWARD);		
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

	public ActionForward deleteCertificate(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response)throws Exception{
		Logger.logTrace(MODULE,"Enter Delete method of "+getClass().getName());
		try{
			checkActionPermission(request, DELETE_ACTION_ALIAS);									 
			String[] strSelectedIds = request.getParameterValues("selectCrl");
			List<Long> listSelectedIDs = new ArrayList<Long>();
			if(strSelectedIds != null){
				for(int i=0;i<strSelectedIds.length;i++){
					listSelectedIDs.add(Long.parseLong(strSelectedIds[i]));
				}
			}

			IStaffData staffData = getStaffObject(((SystemLoginForm)request.getSession().getAttribute("radiusLoginForm")));

			CrlCertificateBLManager blManager = new CrlCertificateBLManager();
			blManager.delete(listSelectedIDs,staffData,DELETE_ACTION_ALIAS);

			int strSelectedIdsLen = strSelectedIds.length;
			CrlCertificateForm crlCertificateForm = (CrlCertificateForm)form;
			long currentPageNumber = getCurrentPageNumberAfterDel(strSelectedIdsLen,crlCertificateForm.getPageNumber(),crlCertificateForm.getTotalPages(),crlCertificateForm.getTotalRecords());

			request.setAttribute("responseUrl", "/searchAllCertificate.do?method=initSearch&pageNumber="+currentPageNumber);    
			ActionMessage message = new ActionMessage("servermgr.crlcertificate.delete");
			ActionMessages messages = new ActionMessages();          
			messages.add("information", message);                    
			saveMessages(request,messages);      	
			return mapping.findForward(SUCCESS_FORWARD);
		}catch (ActionNotPermitedException e) {
			Logger.logError(MODULE, "Error during Data Manager operation ");
			//			Logger.logTrace(MODULE, e);
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

	public ActionForward showAll(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response)throws Exception{
		Logger.logTrace(MODULE,"Enter showAll method of "+getClass().getName());
		try{
			CrlCertificateForm crlCertificateForm=(CrlCertificateForm)form;
			GenericBLManager genericBLManager=new GenericBLManager();
			PageList pageList = genericBLManager.getAllRecords(CrlCertificateData.class,"crlCertificateName",true);
			crlCertificateForm.setPageNumber(pageList.getCurrentPage());			
			crlCertificateForm.setPageNumber(pageList.getCurrentPage());
			crlCertificateForm.setTotalPages(pageList.getTotalPages());
			crlCertificateForm.setTotalRecords(pageList.getTotalItems());
			crlCertificateForm.setListCrlCertificate(pageList.getListData());

			List<CrlCertificateData> list=pageList.getListData();

			for(CrlCertificateData data : list){
				setCrlCertificateDetail(data);
			}

			request.setAttribute("showAllCrlCertificateForm", crlCertificateForm);
			return mapping.findForward(SHOWALL_FORWARD);

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

	public ActionForward downloadFile(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response)throws Exception{
		Logger.logTrace(MODULE,"Enter downloadFile method of"+getClass().getName());
		try{			
			if(request.getParameter("crlCertificateId")!=null){
				Long crlCertificateId=Long.parseLong(request.getParameter("crlCertificateId"));
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

	private CrlCertificateData convertFormToBean(CrlCertificateForm form,CrlCertificateData data) throws Exception{
		if(form.getCrlCert()!=null){
			//			byte[] crlArrayList=insertCRL(form.getCrlCert());
			//			CrlCertificateData data=new CrlCertificateData();
			//			int i=0;
			data.setCrlCertificateId(form.getCrlCertificateId());
			//			data.setCrlCertificateName(form.getCrlCertificateName()+(i+1));
			data.setCrlCertificateName(form.getCrlCertificateName());
			data.setCertificateFileName(form.getCrlCert().getFileName());
			data.setCrlCertificate(form.getCrlCert().getFileData());
			data.setCreateDate(getCurrentTimeStemp());
			return data;
		}
		return null;		
	}

	private void convertBeanToForm(CrlCertificateForm form, CrlCertificateData data) throws Exception{
		form.setCrlCertificateId(data.getCrlCertificateId());
		form.setCrlCertificateName(data.getCrlCertificateName());		
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
	private Collection<? extends CRL> generateCrlCertificate(InputStream crlInputStream)throws Exception {
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
}
