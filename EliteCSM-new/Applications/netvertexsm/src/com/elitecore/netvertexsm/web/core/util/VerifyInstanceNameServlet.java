package com.elitecore.netvertexsm.web.core.util;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.hibernate.Criteria;
import org.hibernate.HibernateException;
import org.hibernate.criterion.ProjectionList;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;

import com.elitecore.corenetvertex.constants.CommunicationProtocol;
import com.elitecore.corenetvertex.sm.acl.GroupData;
import com.elitecore.netvertexsm.blmanager.core.system.util.DataManagerSessionFactory;
import com.elitecore.netvertexsm.datamanager.DataManagerException;
import com.elitecore.netvertexsm.datamanager.customizedmenu.CustomizedMenuData;
import com.elitecore.netvertexsm.datamanager.RoutingTable.mccmncgroup.data.MCCMNCGroupData;
import com.elitecore.netvertexsm.datamanager.RoutingTable.mccmncroutingtable.data.RoutingTableData;
import com.elitecore.netvertexsm.datamanager.RoutingTable.network.data.NetworkData;
import com.elitecore.netvertexsm.datamanager.bitemplate.data.BITemplateData;
import com.elitecore.netvertexsm.datamanager.core.exceptions.constraintviolation.DuplicateInstanceNameFoundException;
import com.elitecore.netvertexsm.datamanager.core.system.accessgroup.data.RoleData;
import com.elitecore.netvertexsm.datamanager.core.system.util.IDataManagerSession;
import com.elitecore.netvertexsm.datamanager.datasource.database.data.DatabaseDSData;
import com.elitecore.netvertexsm.datamanager.datasource.ldap.data.LDAPDatasourceData;
import com.elitecore.netvertexsm.datamanager.gateway.attrmapping.data.PacketMappingData;
import com.elitecore.netvertexsm.datamanager.gateway.gateway.data.DiameterGatewayData;
import com.elitecore.netvertexsm.datamanager.gateway.gateway.data.GatewayData;
import com.elitecore.netvertexsm.datamanager.gateway.pccrulemapping.data.RuleMappingData;
import com.elitecore.netvertexsm.datamanager.gateway.profile.data.GatewayProfileData;
import com.elitecore.netvertexsm.datamanager.locationconfig.area.data.AreaData;
import com.elitecore.netvertexsm.datamanager.locationconfig.city.data.CityData;
import com.elitecore.netvertexsm.datamanager.locationconfig.region.data.RegionData;
import com.elitecore.netvertexsm.datamanager.servergroup.data.ServerInstanceGroupData;
import com.elitecore.netvertexsm.datamanager.servermgr.alert.data.AlertListenerData;
import com.elitecore.netvertexsm.datamanager.servermgr.certificate.data.ServerCertificateData;
import com.elitecore.netvertexsm.datamanager.servermgr.data.NetServerInstanceData;
import com.elitecore.netvertexsm.datamanager.servermgr.drivers.data.DriverInstanceData;
import com.elitecore.netvertexsm.datamanager.servermgr.spr.data.SPRData;
import com.elitecore.netvertexsm.datamanager.servicepolicy.pcrfservicepolicy.data.PCRFServicePolicyData;
import com.elitecore.netvertexsm.hibernate.core.system.util.HibernateDataSession;
import com.elitecore.netvertexsm.util.constants.InstanceTypeConstants;
import com.elitecore.netvertexsm.util.logger.Logger;
/**
 * Servlet implementation class VerifyInstanceNameServlet
 */
public class VerifyInstanceNameServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
	private static final String MODULE="VERIFY_INSTANCE_NAME_SERVLET";
	private String parentId = null; 
	/**
	 * @see HttpServlet#HttpServlet()
	 */
	public VerifyInstanceNameServlet() {
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
		Logger.logInfo(MODULE,"Enter in Servlet " + getClass().getName());
		String instanceType = request.getParameter("instanceType");
		String searchName 	= request.getParameter("searchName");
		String mode 		= request.getParameter("mode");
		String id 			= request.getParameter("id");
		parentId 	= request.getParameter("parentId");
		//String isMonitoringKey = request.getParameter("isMonitoringKey");
		response.setContentType("text/plain");
		PrintWriter out = response.getWriter();
		try{
			if(mode==null){
				mode="";
			}
			
			
			boolean flag = verifyName(Integer.parseInt(instanceType),searchName,mode,id);
			if(flag){
				out.println("true");
			}else {
				out.println("false");
			}
			
		}catch(DuplicateInstanceNameFoundException e){
			Logger.logError(MODULE, e.getMessage());
			out.println("false");
		}catch(DataManagerException e){
			out.print(e.getMessage());
			Logger.logError(MODULE, e.getMessage());
		}
		
		out.close();
	}

	private boolean verifyName(int instanceType,String searchName,String mode, String id) throws DuplicateInstanceNameFoundException,DataManagerException{

		try{
			List<Object[]> list =null;
			switch (instanceType) {
			case InstanceTypeConstants.PCRF_SERVICE_POLICY:
				list = checkForName(mode, PCRFServicePolicyData.class, "pcrfPolicyId", id, "name", searchName);
				break;
			case InstanceTypeConstants.DRIVER:
				list = checkForName(mode, DriverInstanceData.class, "driverInstanceId", id, "name", searchName);
				break;
			case InstanceTypeConstants.GATEWAY:
				list = checkForName(mode, GatewayData.class, "gatewayId", id, "gatewayName", searchName);
				break;
			case InstanceTypeConstants.PROFILE:
				list = checkForName(mode, GatewayProfileData.class, "profileId", id, "profileName", searchName);
				break;
			case InstanceTypeConstants.LDAP_DATASOURCE:
				list = checkForName(mode, LDAPDatasourceData.class, "ldapDsId", id, "name", searchName);
				break;
			case InstanceTypeConstants.DATABASE_DATASOURCE:
				list = checkForName(mode, DatabaseDSData.class, "databaseId", id, "name", searchName);
				break;
			case InstanceTypeConstants.ALERT_CONFIGURATION:
				list = checkForName(mode, AlertListenerData.class, "listenerId", id, "name", searchName);
				break;
			case InstanceTypeConstants.PACKET_MAPPING:
				list = checkForName(mode, PacketMappingData.class, "packetMapId", id, "name", searchName);
				break;
			case InstanceTypeConstants.BI_TEMPLATE:
				list = checkForName(mode, BITemplateData.class, "id", id, "name", searchName);
				break;
			case InstanceTypeConstants.MCCMNC_GROUP:
				list = checkForName(mode, MCCMNCGroupData.class, "mccmncGroupId", id, "name", searchName);
				break;
			case InstanceTypeConstants.CUSTOMIZED_MENU:
				list = checkForName(mode, CustomizedMenuData.class, "customizedMenuId", id, "title", searchName);
				break;
			case InstanceTypeConstants.ROUTING_TABLE:
				list = checkForName(mode, RoutingTableData.class, "routingTableId", id, "name", searchName);
				break;
			case InstanceTypeConstants.DIAMETER_GATEWAY:
				list = checkForName(mode, DiameterGatewayData.class, "gatewayId", id, "hostId", searchName);
				break;				
			case InstanceTypeConstants.NETWORK_MANAGEMENT:
				list = checkForName(mode, NetworkData.class, "networkID", id, "networkName", searchName);
				break;
			case InstanceTypeConstants.AREA_MASTER:
				list = checkForName(mode, AreaData.class, "areaId", id, "area", searchName,"cityId",parentId);
				break;
			case InstanceTypeConstants.REGION:
				list = checkForName(mode, RegionData.class, "regionId", id, "regionName", searchName,"countryId",parentId);
				break;
			case InstanceTypeConstants.CITY:
				list = checkForName(mode, CityData.class, "cityId", id, "cityName", searchName,"regionId",parentId);
				break;
			case InstanceTypeConstants.SERVER_CERTIFICATE : 
				list = checkForName(mode, ServerCertificateData.class, "serverCertificateId", id, "serverCertificateName", searchName);
				break;
			case InstanceTypeConstants.PCCRULE_MAPPING: 
				list = checkForName(mode, RuleMappingData.class, "ruleMappingId", id, "name", searchName);
				break;
				
			case InstanceTypeConstants.ACCESS_GROUP: 
				list = checkForName(mode, RoleData.class, "roleId", id, "name", searchName);
				
			break;
			case InstanceTypeConstants.RADIUS_GATEWAY:
				list = checkForGatewayAddress(mode, GatewayData.class, "gatewayId", id, "connectionUrl", searchName,  "commProtocol", CommunicationProtocol.RADIUS.id);
				break;		
			case InstanceTypeConstants.SPR: 
				list = checkForSPRName(mode, SPRData.class, "id", id, "sprName", searchName);
			break;	
			case InstanceTypeConstants.GROUP_MANAGEMENT: 
				list = checkForGroupName(mode, GroupData.class, "id", id, "name", searchName);
			break;
			case InstanceTypeConstants.SERVER_GROUP_MANAGEMENT: 
				list = checkForGroupName(mode, ServerInstanceGroupData.class, "id", id, "name", searchName);
			break;
			case InstanceTypeConstants.SERVER: 
				list = checkForName(mode, NetServerInstanceData.class, "netServerId", id, "name", searchName);
			break;
			case InstanceTypeConstants.DIAMETER_GATEWAY_CONNECTION_URL:
				list = checkForGatewayAddress(mode, GatewayData.class, "gatewayId", id, "connectionUrl", searchName,  "commProtocol", CommunicationProtocol.DIAMETER.id);
				break;
			
/*			case InstanceTypeConstants.TRUSTED_CERTIFICATE : 
				list = checkForName(mode, TrustedCertificateData.class, "trustedCertificateId", id, "trustedCertificateName", searchName);
				break;
			case InstanceTypeConstants.CRL_CERTIFICATE : 
				list = checkForName(mode, CrlCertificateData.class, "crlCertificateId", id, "crlCertificateName", searchName);
*/				//break;				
			default:
			break;
			}

			if(list!=null && !list.isEmpty()){
				throw new DuplicateInstanceNameFoundException("Name Is Duplicated.");
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
		return true;
	}
	
	private List<Object[]> checkForName(String mode, Class<?> instanceClass, String idProperty, String id, String namePropery, String searchName) throws DataManagerException{
		List<Object[]> list =null;
		Criteria criteria =null;
		IDataManagerSession session = null;
		try{
			session = DataManagerSessionFactory.getInstance().getDataManagerSession();
			criteria  = ((HibernateDataSession)session).getSession().createCriteria(instanceClass);
			ProjectionList proList = Projections.projectionList();
			proList.add(Projections.property(namePropery));
			criteria.setProjection(proList);
			if(mode.equalsIgnoreCase("update")){
				criteria.add(Restrictions.ne(idProperty, Long.parseLong(id)));
			}
			list = criteria.add(Restrictions.eq(namePropery,searchName)).list();
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
	
	private List<Object[]> checkForSPRName(String mode, Class<?> instanceClass, String idProperty, String id, String namePropery, String searchName) throws DataManagerException{
		List<Object[]> list =null;
		Criteria criteria =null;
		IDataManagerSession session = null;
		try{
			session = DataManagerSessionFactory.getInstance().getDataManagerSession();
			criteria  = ((HibernateDataSession)session).getSession().createCriteria(instanceClass);
			ProjectionList proList = Projections.projectionList();
			proList.add(Projections.property(namePropery));
			criteria.setProjection(proList);
			if(mode.equalsIgnoreCase("update")){
				criteria.add(Restrictions.ne(idProperty, id));
			}
			list = criteria.add(Restrictions.eq(namePropery,searchName)).list();
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
		
	/**
	 * @param mode
	 * @param instanceClass
	 * @param idProperty
	 * @param id
	 * @param namePropery
	 * @param searchName
	 * @param idParentProperty
	 * @param parentId
	 * @return List
	 * @throws DataManagerException
	 * @description This method will be use for instance having own id and parentId to check for the duplicate name
	 */
	private List<Object[]> checkForName(String mode, Class<?> instanceClass, String idProperty, String id, String namePropery, String searchName,String idParentProperty,String parentId) throws DataManagerException{
		List<Object[]> list =null;
		Criteria criteria =null;
		IDataManagerSession session = null;
		try{
			session = DataManagerSessionFactory.getInstance().getDataManagerSession();
			criteria  = ((HibernateDataSession)session).getSession().createCriteria(instanceClass);
			ProjectionList proList = Projections.projectionList();
			proList.add(Projections.property(namePropery));
			criteria.setProjection(proList);
			if(mode.equalsIgnoreCase("update")){
				criteria.add(Restrictions.ne(idProperty, Long.parseLong(id)));
			}
			criteria.add(Restrictions.eq(idParentProperty, Long.parseLong(parentId)));
			criteria.add(Restrictions.ilike(namePropery, searchName));			
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
	
	
	private List<Object[]> checkForName(String mode, Class<?> instanceClass, String idProperty, String id, String nameProperty, Integer propertyValue) throws DataManagerException{
		List<Object[]> list =null;
		Criteria criteria =null;
		IDataManagerSession session = null;
		try{
			session = DataManagerSessionFactory.getInstance().getDataManagerSession();
			criteria  = ((HibernateDataSession)session).getSession().createCriteria(instanceClass);
			ProjectionList proList = Projections.projectionList();
			proList.add(Projections.property(nameProperty));
			criteria.setProjection(proList);
			if(mode.equalsIgnoreCase("update")){
				criteria.add(Restrictions.ne(idProperty, Long.parseLong(id)));
			}
			list = criteria.add(Restrictions.eq(nameProperty,propertyValue)).list();
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
	
	private List<Object[]> checkForGatewayAddress(String mode, Class<?> instanceClass, String idProperty, String id, String namePropery, String searchName,String commProtocol,String commProtocolType) throws DataManagerException{
		List<Object[]> list =null;
		Criteria criteria =null;
		IDataManagerSession session = null;
		try{
			session = DataManagerSessionFactory.getInstance().getDataManagerSession();
			criteria  = ((HibernateDataSession)session).getSession().createCriteria(instanceClass);
			ProjectionList proList = Projections.projectionList();
			proList.add(Projections.property(namePropery));
			criteria.setProjection(proList);
			
			if(mode.equalsIgnoreCase("update")){
				criteria.add(Restrictions.ne(idProperty, Long.parseLong(id)));
			}
			criteria.add(Restrictions.eq(commProtocol,commProtocolType));
			list = criteria.add(Restrictions.eq(namePropery,searchName).ignoreCase()).list();
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
	
	/**
	 * this method will check for valid group name based on string id
	 * @param mode
	 * @param instanceClass
	 * @param idProperty
	 * @param id
	 * @param namePropery
	 * @param searchName
	 * @return
	 * @throws DataManagerException
	 */
	private List<Object[]> checkForGroupName(String mode, Class<?> instanceClass, String idProperty, String id, String namePropery, String searchName) throws DataManagerException{
		List<Object[]> list =null;
		Criteria criteria =null;
		IDataManagerSession session = null;
		try{
			session = DataManagerSessionFactory.getInstance().getDataManagerSession();
			criteria  = ((HibernateDataSession)session).getSession().createCriteria(instanceClass);
			ProjectionList proList = Projections.projectionList();
			proList.add(Projections.property(namePropery));
			criteria.setProjection(proList);
			if(mode.equalsIgnoreCase("update")){
				criteria.add(Restrictions.ne(idProperty,id));
			}
			list = criteria.add(Restrictions.eq(namePropery,searchName)).list();
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
	
	
}
