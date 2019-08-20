package com.elitecore.netvertexsm.web.servermgr.certificate;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.security.cert.Certificate;
import java.security.cert.CertificateException;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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

import com.elitecore.netvertexsm.blmanager.core.base.GenericBLManager;
import com.elitecore.netvertexsm.blmanager.servermgr.certificate.TrustedCertificateBLManager;
import com.elitecore.netvertexsm.datamanager.DataManagerException;
import com.elitecore.netvertexsm.datamanager.core.exceptions.auhorization.ActionNotPermitedException;
import com.elitecore.netvertexsm.datamanager.core.exceptions.constraintviolation.DuplicateInstanceNameFoundException;
import com.elitecore.netvertexsm.datamanager.core.system.staff.data.IStaffData;
import com.elitecore.netvertexsm.datamanager.core.util.PageList;
import com.elitecore.netvertexsm.datamanager.servermgr.certificate.data.TrustedCertificateData;
import com.elitecore.netvertexsm.util.constants.ConfigConstant;
import com.elitecore.netvertexsm.util.exception.EliteExceptionUtils;
import com.elitecore.netvertexsm.web.core.base.BaseWebDispatchAction;
import com.elitecore.netvertexsm.web.core.system.cache.ConfigManager;
import com.elitecore.netvertexsm.web.core.system.login.forms.SystemLoginForm;
import com.elitecore.netvertexsm.web.servermgr.certificate.form.TrustedCertificateForm;
import com.elitecore.netvertexsm.ws.logger.Logger;

public class TrustedCertificateAction extends BaseWebDispatchAction{

	private static final String CREATE_ACTION_ALIAS = ConfigConstant.CREATE_TRUSTED_CERTIFICATE;
	private static final String UPDATE_ACTION_ALIAS = ConfigConstant.UPDATE_TRUSTED_CERTIFICATE;
	private static final String DELETE_ACTION_ALIAS = ConfigConstant.DELETE_TRUSTED_CERTIFICATE;
	private static final String SEARCH_ACTION_ALIAS = ConfigConstant.SEARCH_TRUSTED_CERTIFICATE;
	private static final String MODULE="TRUSTED-CERTI";
	private static final String SUCCESS_FORWARD = "success";
	private static final String FAILURE_FORWARD = "failure";	
	private static final String SEARCH_FORWARD = "searchTrustedCertificate";
	private static final String CREATE_FORWARD = "createTrustedCertificate";
	private static final String UPDATE_FORWARD = "updateTrustedCertificate";
	private static final String VIEW_FORWARD = "viewTrustedCertificate";
	private static final String SHOWALL_FORWARD = "showAllTrustedCertificate";

	public ActionForward initSearch(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response)throws Exception{
		Logger.logTrace(MODULE,"Enter initSearch method of "+getClass().getName());
		return searchFun(mapping, form, request, response);
	}	

	private ActionForward searchFun(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response){
		try{
			TrustedCertificateForm trustedCertificateForm=(TrustedCertificateForm)form;

			Integer pageSize=Integer.parseInt(ConfigManager.get("TOTAL_ROW"));
			int requiredPageNo;
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

			IStaffData staffData = getStaffObject(((SystemLoginForm)request.getSession().getAttribute("radiusLoginForm")));

			Map infoMap= new HashedMap();
			infoMap.put("pageNo", requiredPageNo);
			infoMap.put("pageSize", pageSize);

			TrustedCertificateBLManager blManager=new TrustedCertificateBLManager();

			PageList pageList=blManager.search(trustedCertificateData, infoMap);
			List<TrustedCertificateData> list=pageList.getListData();
			for(TrustedCertificateData data : list){
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
		}catch(Exception managerExp){
			managerExp.printStackTrace();
			Logger.logError(MODULE, "Error during Data Manager operation , reason : " + managerExp.getMessage());
			Object errorElements[] = EliteExceptionUtils.getFullStackTraceAsArray(managerExp);
			request.setAttribute("errorDetails", errorElements);
			ActionMessage message = new ActionMessage("servermgr.trustedcertificate.search.failure");                                                         
			ActionMessages messages = new ActionMessages();                                                                                 
			messages.add("information", message);
			saveErrors(request, messages);			   
		}		       
		return mapping.findForward(FAILURE_FORWARD);
	}

	public ActionForward search(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response)throws Exception{
		Logger.logTrace(MODULE,"Enter search method of "+getClass().getName());
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
		try{
			checkActionPermission(request, CREATE_ACTION_ALIAS);
			Logger.logTrace(MODULE,"Enter initCreate method of "+getClass().getName());
			TrustedCertificateForm trustedCertificateForm=new TrustedCertificateForm();
			if(request.getParameter("name")!=null && !request.getParameter("name").trim().equalsIgnoreCase("")){
				trustedCertificateForm.setTrustedCertificateName(request.getParameter("name"));
			}
			request.setAttribute("trustedCertificateForm", trustedCertificateForm);
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

			TrustedCertificateForm trustedCertificateForm=(TrustedCertificateForm)form;
			TrustedCertificateData trustedCertificateData=new TrustedCertificateData();

			String currentUser = ((SystemLoginForm)request.getSession().getAttribute("radiusLoginForm")).getUserId();

			//generate certificate
			Collection<? extends Certificate> certs=generateCertificate(trustedCertificateForm.getTrustedCert().getInputStream());

			convertFormToBean(trustedCertificateForm, trustedCertificateData);
			trustedCertificateData.setCreatedByStaffId(Long.parseLong(currentUser));
			trustedCertificateData.setModifiedByStaffId(Long.parseLong(currentUser));
			
			IStaffData staffData = getStaffObject(((SystemLoginForm)request.getSession().getAttribute("radiusLoginForm")));
			TrustedCertificateBLManager blManager=new TrustedCertificateBLManager();
			blManager.create(trustedCertificateData,staffData,CREATE_ACTION_ALIAS);

			request.setAttribute("responseUrl", "/searchAllCertificate.do?method=initSearch");    
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

	public ActionForward initUpdate(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response)throws Exception{

		Logger.logTrace(MODULE, "Enter initUpdate method of " + getClass().getName());
		try {
			checkActionPermission(request, UPDATE_ACTION_ALIAS);
			TrustedCertificateForm trustedCertificateForm=(TrustedCertificateForm)form;
			TrustedCertificateBLManager blManager=new TrustedCertificateBLManager();

			TrustedCertificateData trustedCertificateData=blManager.initUpdate(trustedCertificateForm.getTrustedCertificateId());
			convertBeanToForm(trustedCertificateForm,trustedCertificateData);
			setServerCertificateDetail(trustedCertificateData);
			request.setAttribute("trustedCertificateData",trustedCertificateData);
			request.setAttribute("trustedCertificateForm",trustedCertificateForm);			
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

			TrustedCertificateForm trustedCertificateForm = (TrustedCertificateForm)form;			
			TrustedCertificateData trustedCertificateData = new TrustedCertificateData();

			//generate certificate
			Collection<? extends Certificate> certs=generateCertificate(trustedCertificateForm.getTrustedCert().getInputStream());

			convertFormToBean(trustedCertificateForm,trustedCertificateData);

			String currentUser = ((SystemLoginForm)request.getSession().getAttribute("radiusLoginForm")).getUserId();
			trustedCertificateData.setModifiedByStaffId(Long.parseLong(currentUser));
			trustedCertificateData.setModifiedDate(getCurrentTimeStemp());

			IStaffData staffData = getStaffObject(((SystemLoginForm)request.getSession().getAttribute("radiusLoginForm")));
			TrustedCertificateBLManager blManager = new TrustedCertificateBLManager();
			blManager.update(trustedCertificateData,staffData,UPDATE_ACTION_ALIAS);

			request.setAttribute("trustedCertificateData",trustedCertificateData);
			request.setAttribute("trustedCertificateForm",trustedCertificateForm);
			request.setAttribute("responseUrl", "/searchAllCertificate.do?method=initSearch");    
			ActionMessage message = new ActionMessage("servermgr.trustedcertificate.update");
			ActionMessages messages = new ActionMessages();          
			messages.add("information", message);                    
			saveMessages(request,messages);         				   
			Logger.logInfo(MODULE, "Returning success forward from " + getClass().getName()); 

			return mapping.findForward(SUCCESS_FORWARD);
		}catch(CertificateException e){
			Logger.logError(MODULE,"Error during Data Manager operation , reason : "+ e.getMessage());
			Object errorElements[] = EliteExceptionUtils.getFullStackTraceAsArray(e);
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
		}catch (DuplicateInstanceNameFoundException dpfExp) {
			Logger.logError(MODULE, "Returning error forward from " + getClass().getName());
			Object errorElements[] = EliteExceptionUtils.getFullStackTraceAsArray(dpfExp);
			request.setAttribute("errorDetails", errorElements);
			ActionMessage message = new ActionMessage("servermgr.trustedcertificate.update.duplicate");
			ActionMessages messages = new ActionMessages();
			messages.add("information",message);
			saveErrors(request,messages);		
		}catch (Exception authExp) {                                                                                           
			Logger.logError(MODULE, "Error during Data Manager operation , reason :" + authExp.getMessage());                              
			ActionMessage message = new ActionMessage("servermgr.trustedcertificate.update.failure");
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

			String[] strSelectedIds = request.getParameterValues("selectTrusted");
			List<Long> listSelectedIDs = new ArrayList<Long>();
			if(strSelectedIds != null){
				for(int i=0;i<strSelectedIds.length;i++){
					listSelectedIDs.add(Long.parseLong(strSelectedIds[i]));
				}
			}

			IStaffData staffData = getStaffObject(((SystemLoginForm)request.getSession().getAttribute("radiusLoginForm")));

			TrustedCertificateBLManager blManager = new TrustedCertificateBLManager();
			blManager.delete(listSelectedIDs,staffData,DELETE_ACTION_ALIAS);

			int strSelectedIdsLen = strSelectedIds.length;
			TrustedCertificateForm trustedCertificateForm = (TrustedCertificateForm)form;
			long currentPageNumber = getCurrentPageNumberAfterDel(strSelectedIdsLen,trustedCertificateForm.getPageNumber(),trustedCertificateForm.getTotalPages(),trustedCertificateForm.getTotalRecords());

			request.setAttribute("responseUrl", "/searchAllCertificate.do?method=initSearch&pageNumber="+currentPageNumber);    
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

	public ActionForward view(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response)throws Exception{
		Logger.logTrace(MODULE,"Enter View method of "+getClass().getName());
		try{			
			Long trustedCertificateId=0L;
			if(request.getParameter("trustedCertificateId")!=null){
				trustedCertificateId=Long.parseLong(request.getParameter("trustedCertificateId"));
			}

			TrustedCertificateBLManager blManager=new TrustedCertificateBLManager();
			TrustedCertificateData trustedCertificateData=blManager.view(trustedCertificateId);

			setServerCertificateDetail(trustedCertificateData);
			request.setAttribute("trustedCertificateData", trustedCertificateData);		
			return mapping.findForward(VIEW_FORWARD);		
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

	public ActionForward showAll(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response)throws Exception{
		Logger.logTrace(MODULE,"Enter showAll method of "+getClass().getName());
		try{
			TrustedCertificateForm trustedCertificateForm=(TrustedCertificateForm)form;
			GenericBLManager genericBLManager=new GenericBLManager();
			PageList pageList = genericBLManager.getAllRecords(TrustedCertificateData.class,"trustedCertificateName",true);
			trustedCertificateForm.setPageNumber(pageList.getCurrentPage());			
			trustedCertificateForm.setPageNumber(pageList.getCurrentPage());
			trustedCertificateForm.setTotalPages(pageList.getTotalPages());
			trustedCertificateForm.setTotalRecords(pageList.getTotalItems());
			trustedCertificateForm.setListTrustedCertificate(pageList.getListData());

			List<TrustedCertificateData> list=pageList.getListData();
			for(TrustedCertificateData data : list){
				setServerCertificateDetail(data);
			}

			request.setAttribute("showAllTrustedCertificateForm", trustedCertificateForm);
			return mapping.findForward(SHOWALL_FORWARD);
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

	public ActionForward downloadFile(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response)throws Exception{
		Logger.logTrace(MODULE,"Enter downloadFile method of"+getClass().getName());
		try{
			if(request.getParameter("trustedCertificateId") != null){
				Long trustedCertificateId=Long.parseLong(request.getParameter("trustedCertificateId"));
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

	private void convertBeanToForm( TrustedCertificateForm form, TrustedCertificateData data) throws Exception{
		form.setTrustedCertificateId(data.getTrustedCertificateId());
		form.setTrustedCertificateName(data.getTrustedCertificateName());
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
	private void convertFormToBean(TrustedCertificateForm form, TrustedCertificateData data) throws IOException {
		data.setTrustedCertificateId(form.getTrustedCertificateId());
		data.setTrustedCertificateName(form.getTrustedCertificateName());
		data.setCertificateFileName(form.getTrustedCert().getFileName());
		data.setTrustedCertificate(form.getTrustedCert().getFileData());	
		data.setCreateDate(getCurrentTimeStemp());
	}
	
	private Collection<? extends Certificate> generateCertificate(InputStream fileInputStream) throws CertificateException {
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
}
