package com.elitecore.netvertexsm.web.core.util;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.hibernate.Criteria;
import org.hibernate.HibernateException;
import org.hibernate.criterion.ProjectionList;
import org.hibernate.criterion.Projections;

import com.elitecore.commons.logging.LogManager;
import com.elitecore.corenetvertex.constants.PCRFKeyConstants;
import com.elitecore.corenetvertex.constants.PCRFKeyType;
import com.elitecore.netvertexsm.blmanager.core.system.util.DataManagerSessionFactory;
import com.elitecore.netvertexsm.datamanager.DataManagerException;
import com.elitecore.netvertexsm.datamanager.core.exceptions.constraintviolation.DuplicateInstanceNameFoundException;
import com.elitecore.netvertexsm.datamanager.core.system.util.IDataManagerSession;
import com.elitecore.netvertexsm.datamanager.locationconfig.area.data.AreaData;
import com.elitecore.netvertexsm.hibernate.core.system.util.HibernateDataSession;
import com.elitecore.netvertexsm.util.constants.CrestelKeyRequestConstants;
import com.elitecore.netvertexsm.util.constants.CrestelKeyResponseConstants;
import com.elitecore.netvertexsm.util.constants.InstanceTypeConstants;

/**
 * Servlet implementation class ListRetrivalServlet
 */
public class ListRetrivalServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
	private static final String MODULE = "LIST-RETV-SERV";
	private String[] mappingTypeArray;   
	/**
	 * @see HttpServlet#HttpServlet()
	 */
	public ListRetrivalServlet() {
		super();
		// TODO Auto-generated constructor stub
	}

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		doPost(request, response);
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		String instanceType = request.getParameter("instanceType");
		String propertyName = request.getParameter("propertyName");
		mappingTypeArray = request.getParameterValues("mappingTypeArray[]");

		if(propertyName == null){
			propertyName = "*";
		}
		response.setContentType("text/plain");
		PrintWriter out = response.getWriter();
		try{
			out.println(verifyName(Integer.parseInt(instanceType), propertyName));
		}catch(DuplicateInstanceNameFoundException e){
			LogManager.getLogger().error(MODULE, e.getMessage());
			out.println("false");
		}catch(DataManagerException e){
			out.print(e.getMessage());
			LogManager.getLogger().error(MODULE, e.getMessage());
		}
		out.close();
	}

	private List<Object> verifyName(int instanceType,String propertyName) throws DuplicateInstanceNameFoundException,DataManagerException{

		try{	
			switch (instanceType) {
			case InstanceTypeConstants.POLICY_KEY:
				return getKeyVal();
			case InstanceTypeConstants.CRESTEL_REQUEST_KEY:
				return getCrestelRequestKeyVal();
			case InstanceTypeConstants.CRESTEL_RESPONSE_KEY:
				return getCrestelResponseKeyVal();
			case InstanceTypeConstants.AREA_MASTER:
				return getList(AreaData.class,propertyName);				
			default:
				return new ArrayList<Object>();
			}
		}catch(HibernateException hExp){
			throw new DataManagerException(hExp.getMessage(), hExp);
		}catch(DuplicateInstanceNameFoundException e) {
			throw new DuplicateInstanceNameFoundException(e.getMessage(), e);
		}catch(DataManagerException e) {
			throw new DataManagerException(e.getMessage(), e);
		}catch(Exception e) {
			throw new DataManagerException(e.getMessage(), e);
		}
	}


	private <T> List<Object> getList(Class<?> instanceClass, String propertyName) throws DataManagerException{
		List<Object> list =null;
		Criteria criteria =null;
		IDataManagerSession session = null;
		try{
			
			session = DataManagerSessionFactory.getInstance().getDataManagerSession();
			criteria  = ((HibernateDataSession)session).getSession().createCriteria(instanceClass);
			if(!propertyName.equals("*")){
				ProjectionList proList = Projections.projectionList();
				proList.add(Projections.distinct(Projections.property(propertyName)));
				criteria.setProjection(proList);				
			}			
			list = criteria.list();
			
		}catch(HibernateException hExp){
			throw new DataManagerException(hExp.getMessage(), hExp);
		}catch(Exception e) {
			throw new DataManagerException(e.getMessage(), e);
		}finally{
			if(session!=null)
				session.close();
		}
		return list;
	}

	private List<Object> getKeyVal() {
		Set<Object> keySet = new HashSet<Object>();
		int mapValue=0;
		for (int i = 0; i < mappingTypeArray.length; i++) {
			try{
				mapValue = Integer.parseInt(mappingTypeArray[i]);
			}catch (NumberFormatException e) {
				return new ArrayList<Object>(keySet);
			}
			if(mapValue == PCRFKeyType.REQUEST.val){
				for(PCRFKeyConstants val : PCRFKeyConstants.values(PCRFKeyType.REQUEST)){
					keySet.add(val.getVal());
				}
			}else if(mapValue == PCRFKeyType.RESPONSE.val){ 
				for(PCRFKeyConstants val : PCRFKeyConstants.values(PCRFKeyType.RESPONSE)){
					keySet.add(val.getVal());
				}
			}else if(mapValue == PCRFKeyType.PCC_RULE.val){ 
				for(PCRFKeyConstants val : PCRFKeyConstants.values(PCRFKeyType.PCC_RULE)){
					keySet.add(val.getVal());
				}
			}else if(mapValue == PCRFKeyType.RULE.val){ 
				for(PCRFKeyConstants val : PCRFKeyConstants.values(PCRFKeyType.RULE)){
					keySet.add(val.getVal());
				}
			}					
		}			
		return new ArrayList<Object>(keySet);
	}

	private List<Object> getCrestelRequestKeyVal() {
		List<Object> keyList = new ArrayList<Object>();
		for(CrestelKeyRequestConstants val : CrestelKeyRequestConstants.values()) 
			keyList.add(val.getVal());
		return keyList;
	}
	private List<Object> getCrestelResponseKeyVal() {
		List<Object> keyList = new ArrayList<Object>();
		for(CrestelKeyResponseConstants val : CrestelKeyResponseConstants.values()) 
			keyList.add(val.getVal());
		return keyList;
	}
}