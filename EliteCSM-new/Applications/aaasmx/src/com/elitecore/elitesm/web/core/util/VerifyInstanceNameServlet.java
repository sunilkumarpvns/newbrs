package com.elitecore.elitesm.web.core.util;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.elitecore.elitesm.datamanager.radius.correlatedradius.data.CorrelatedRadiusData;
import org.hibernate.Criteria;
import org.hibernate.HibernateException;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.criterion.ProjectionList;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;

import com.elitecore.commons.base.Collectionz;
import com.elitecore.elitesm.blmanager.core.system.util.DataManagerSessionFactory;
import com.elitecore.elitesm.datamanager.DataManagerException;
import com.elitecore.elitesm.datamanager.core.exceptions.constraintviolation.DuplicateInstanceNameFoundException;
import com.elitecore.elitesm.datamanager.core.system.staff.data.StaffData;
import com.elitecore.elitesm.datamanager.core.system.util.IDataManagerSession;
import com.elitecore.elitesm.datamanager.dashboard.data.DashboardData;
import com.elitecore.elitesm.datamanager.datasource.database.data.DatabaseDSData;
import com.elitecore.elitesm.datamanager.datasource.ldap.data.LDAPDatasourceData;
import com.elitecore.elitesm.datamanager.diameter.diameterconcurrency.data.DiameterConcurrencyData;
import com.elitecore.elitesm.datamanager.diameter.diameterpeer.data.DiameterPeerData;
import com.elitecore.elitesm.datamanager.diameter.diameterpeergroup.data.DiameterPeerGroup;
import com.elitecore.elitesm.datamanager.diameter.diameterpeerprofile.data.DiameterPeerProfileData;
import com.elitecore.elitesm.datamanager.diameter.diameterpolicy.data.DiameterPolicyData;
import com.elitecore.elitesm.datamanager.diameter.diameterpolicygroup.data.DiameterPolicyGroup;
import com.elitecore.elitesm.datamanager.diameter.imsibasedroutingtable.data.IMSIBasedRoutingTableData;
import com.elitecore.elitesm.datamanager.diameter.msisdnbasedroutingtable.data.MSISDNBasedRoutingTableData;
import com.elitecore.elitesm.datamanager.diameter.routingconf.data.DiameterRoutingConfData;
import com.elitecore.elitesm.datamanager.diameter.routingconf.data.DiameterRoutingTableData;
import com.elitecore.elitesm.datamanager.diameter.sessionmanager.data.DiameterSessionManagerData;
import com.elitecore.elitesm.datamanager.digestconf.data.DigestConfigInstanceData;
import com.elitecore.elitesm.datamanager.externalsystem.data.ExternalSystemInterfaceInstanceData;
import com.elitecore.elitesm.datamanager.radius.clientprofile.data.RadiusClientProfileData;
import com.elitecore.elitesm.datamanager.radius.policies.accesspolicy.data.AccessPolicyData;
import com.elitecore.elitesm.datamanager.radius.policies.radiuspolicy.data.RadiusPolicyData;
import com.elitecore.elitesm.datamanager.radius.radiusesigroup.data.RadiusESIGroupData;
import com.elitecore.elitesm.datamanager.radius.radiuspolicygroup.data.RadiusPolicyGroup;
import com.elitecore.elitesm.datamanager.radius.radtest.data.RadiusTestData;
import com.elitecore.elitesm.datamanager.rm.concurrentloginpolicy.data.ConcurrentLoginPolicyData;
import com.elitecore.elitesm.datamanager.rm.ippool.data.IPPoolData;
import com.elitecore.elitesm.datamanager.servermgr.alert.data.AlertListenerData;
import com.elitecore.elitesm.datamanager.servermgr.certificate.data.CrlCertificateData;
import com.elitecore.elitesm.datamanager.servermgr.certificate.data.ServerCertificateData;
import com.elitecore.elitesm.datamanager.servermgr.certificate.data.TrustedCertificateData;
import com.elitecore.elitesm.datamanager.servermgr.copypacket.data.CopyPacketTranslationConfData;
import com.elitecore.elitesm.datamanager.servermgr.drivers.data.DriverInstanceData;
import com.elitecore.elitesm.datamanager.servermgr.eap.data.EAPConfigData;
import com.elitecore.elitesm.datamanager.servermgr.gracepolicy.data.GracepolicyData;
import com.elitecore.elitesm.datamanager.servermgr.plugins.data.PluginInstData;
import com.elitecore.elitesm.datamanager.servermgr.script.data.ScriptInstanceData;
import com.elitecore.elitesm.datamanager.servermgr.transmapconf.data.TranslationMappingConfData;
import com.elitecore.elitesm.datamanager.servicepolicy.acct.data.AcctPolicyInstData;
import com.elitecore.elitesm.datamanager.servicepolicy.auth.data.AuthPolicyInstData;
import com.elitecore.elitesm.datamanager.servicepolicy.diameter.creditcontrolpolicy.data.CreditControlPolicyData;
import com.elitecore.elitesm.datamanager.servicepolicy.diameter.eappolicy.data.EAPPolicyData;
import com.elitecore.elitesm.datamanager.servicepolicy.diameter.naspolicy.data.NASPolicyInstData;
import com.elitecore.elitesm.datamanager.servicepolicy.diameter.tgppaaapolicy.data.TGPPAAAPolicyData;
import com.elitecore.elitesm.datamanager.servicepolicy.dynauth.data.DynAuthPolicyInstData;
import com.elitecore.elitesm.datamanager.servicepolicy.radiusservicepolicy.data.RadServicePolicyData;
import com.elitecore.elitesm.datamanager.servicepolicy.rm.cgpolicy.data.CGPolicyData;
import com.elitecore.elitesm.datamanager.sessionmanager.data.SessionManagerInstanceData;
import com.elitecore.elitesm.hibernate.core.system.util.HibernateDataSession;
import com.elitecore.elitesm.util.constants.InstanceTypeConstants;
import com.elitecore.elitesm.util.logger.Logger;
/**
 * Servlet implementation class VerifyInstanceNameServlet
 */
public class VerifyInstanceNameServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
	private static final String MODULE="VeryfyInstanceNameServlet";

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
		String instanceType = request.getParameter("instanceType");
		String searchName = request.getParameter("searchName");
		String mode = request.getParameter("mode");
		String id = request.getParameter("id");

		response.setContentType("text/plain");
		PrintWriter out = response.getWriter();
		
		Pattern p = Pattern.compile("^[a-zA-Z0-9-_.]*$"); //fixed typo
		boolean regExpFlag = false;
		Matcher matcher= p.matcher(searchName);
		if(matcher!=null){
			regExpFlag = matcher.matches();
		}
		
		if(!regExpFlag) {
			out.println("invalid");
			out.close();
			return;
		}
		
		try{
			if(mode==null){
				mode="";
			}
			boolean flag =verifyName(Integer.parseInt(instanceType),searchName,mode,id);
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
			case InstanceTypeConstants.AUTH_POLICY:
				list = checkForName(mode, AuthPolicyInstData.class, "authPolicyId", id, "name", searchName, false);
				break;
			case InstanceTypeConstants.ACCT_POLICY:
				list = checkForName(mode, AcctPolicyInstData.class, "acctPolicyId", id, "name", searchName, false);
				break;
			case InstanceTypeConstants.DYNA_AUTH_POLICY:
				list = checkForName(mode, DynAuthPolicyInstData.class, "dynAuthPolicyId", id, "name", searchName, false);
				break;
			case InstanceTypeConstants.NAS_POLICY:
				list = checkForName(mode, NASPolicyInstData.class, "nasPolicyId", id, "name", searchName, false);
				break;
			case InstanceTypeConstants.CREDIT_CONTROL_POLICY:
				list = checkForName(mode, CreditControlPolicyData.class, "policyId", id, "name", searchName, false);
				break;
			case InstanceTypeConstants.SESSION_MANAGER:
				list = checkForName(mode, SessionManagerInstanceData.class, "smInstanceId", id, "name", searchName, false);
				break;
			case InstanceTypeConstants.DRIVER:
				list = checkForName(mode, DriverInstanceData.class, "driverInstanceId", id, "name", searchName, false);
				break;
			case InstanceTypeConstants.DIGEST_CONFIG:
				list = checkForName(mode, DigestConfigInstanceData.class, "digestConfId", id, "name", searchName, false);
				break;
			case InstanceTypeConstants.ALERT_LISTENER:
				list = checkForName(mode, AlertListenerData.class, "listenerId", id, "name", searchName, false);
				break;
			case InstanceTypeConstants.EAP_CONFIG:
				list = checkForName(mode, EAPConfigData.class, "eapId", id, "name", searchName, false);
				break;
			case InstanceTypeConstants.RADIUS_POLICY:
				list = checkForName(mode, RadiusPolicyData.class, "radiusPolicyId", id, "name", searchName, true);
				break;
			case InstanceTypeConstants.ACCESS_POLICY:
				list = checkForName(mode, AccessPolicyData.class, "accessPolicyId", id, "name", searchName, true);
				break;
			case InstanceTypeConstants.TRUSTED_CLIENT_PROFILE:
				list = checkForName(mode, RadiusClientProfileData.class, "profileId", id, "profileName", searchName, false);
				break;
			case InstanceTypeConstants.IPPOOL:
				list = checkForName(mode, IPPoolData.class, "ipPoolId", id, "name", searchName, false);
				break;
			case InstanceTypeConstants.CONCURRENT_LOGIN_POLICY:
				list = checkForName(mode, ConcurrentLoginPolicyData.class, "concurrentLoginId", id, "name", searchName, true);
				break;
			case InstanceTypeConstants.LDAP_DATASOURCE:
				list = checkForName(mode, LDAPDatasourceData.class, "ldapDsId", id, "name", searchName, false);
				break;
			case InstanceTypeConstants.DATABASE_DATASOURCE:
				list = checkForName(mode, DatabaseDSData.class, "databaseId", id, "name", searchName, false);
				break;
			case InstanceTypeConstants.EXTENDED_RADIUS_SYSTEM:
				list = checkForName(mode, ExternalSystemInterfaceInstanceData.class, "esiInstanceId", id, "name", searchName, false);
				break;
			case InstanceTypeConstants.STAFF:
				list = checkForName(mode, StaffData.class, "staffId", id, "username", searchName, false);
				break;
			case InstanceTypeConstants.ACCESS_GROUP:
				list = checkForName(mode, AccessPolicyData.class, "accessPolicyId", id, "name", searchName, false);
				break;
			case InstanceTypeConstants.TRANSLATION_MAPPING_CONFIG:
				list = checkForName(mode, TranslationMappingConfData.class, "translationMapConfigId", id, "name", searchName, false);
				break;
			case InstanceTypeConstants.DIAMETER_EAP_POLICY:
				list = checkForName(mode, EAPPolicyData.class, "eapPolicyId", id, "name", searchName, false);
				break;
			case InstanceTypeConstants.DIAMETER_POLICY:
				list = checkForName(mode, DiameterPolicyData.class, "diameterPolicyId", id, "name", searchName, true);
				break;
			case InstanceTypeConstants.CHARGING_POLICY:
				list = checkForName(mode, CGPolicyData.class, "policyId", id, "name", searchName, false);
				break;
			case InstanceTypeConstants.GRACE_POLICY:
				list = checkForName(mode, GracepolicyData.class, "gracePolicyId", id, "name", searchName, true);
				break;
			case InstanceTypeConstants.DIAMETER_PEER_PROFILE:
				list = checkForName(mode, DiameterPeerProfileData.class, "peerProfileId", id, "profileName", searchName, false);
				break;	
			case InstanceTypeConstants.ROUTING_CONFIG:
				list = checkForName(mode, DiameterRoutingConfData.class, "routingConfigId", id, "name", searchName, false);
				break;
			case InstanceTypeConstants.DIAMETER_ROUTING_TABLE:
				list = checkForName(mode, DiameterRoutingTableData.class, "routingTableId", id, "routingTableName", searchName, false);
				break;	
			case InstanceTypeConstants.RADIUS_TEST_PACKET:
				list = checkForName(mode, RadiusTestData.class, "ntradId", id, "name", searchName, false);
				break;
			case InstanceTypeConstants.DIAMETER_PEER:
				list = checkForName(mode, DiameterPeerData.class, "peerUUID", id, "name", searchName, false);
				break;
			case InstanceTypeConstants.DIAMETER_HOST_IDENTITY:
				list = checkForName(mode, DiameterPeerData.class, "peerUUID", id, "hostIdentity", searchName, false);
				break;				
			case InstanceTypeConstants.SERVER_CERTIFICATE : 
				list = checkForName(mode, ServerCertificateData.class, "serverCertificateId", id, "serverCertificateName", searchName, false);
				break;
			case InstanceTypeConstants.TRUSTED_CERTIFICATE : 
				list = checkForName(mode, TrustedCertificateData.class, "trustedCertificateId", id, "trustedCertificateName", searchName, false);
				break;
			case InstanceTypeConstants.CRL_CERTIFICATE : 
				list = checkForName(mode, CrlCertificateData.class, "crlCertificateId", id, "crlCertificateName", searchName, false);
				break;
			case InstanceTypeConstants.DASHBOARD : 
				list = checkForName(mode, DashboardData.class, "dashboardId", id, "dashboardName", searchName, false);
				break;
			case InstanceTypeConstants.COPY_PACKET_TRANSLATION_MAPPING_CONFIG : 
				list = checkForName(mode, CopyPacketTranslationConfData.class, "copyPacketTransConfId", id, "name", searchName, false);
				break;
			case InstanceTypeConstants.DIAMETER_SESSION_MANAGER : 
				list = checkForName(mode, DiameterSessionManagerData.class, "sessionManagerId", id, "name", searchName, false);
				break;
			case InstanceTypeConstants.RADIUS_SERVICE_POLICY : 
				list = checkForName(mode, RadServicePolicyData.class, "radiusPolicyId", id, "name", searchName, false);
				break;
			case InstanceTypeConstants.IMSI_BASED_ROUTING_TABLE :
				list = checkForName(mode, IMSIBasedRoutingTableData.class, "routingTableId", id, "routingTableName", searchName, false);
				break;
			case InstanceTypeConstants.MSISDN_BASED_ROUTING_TABLE :
				list = checkForName(mode, MSISDNBasedRoutingTableData.class, "routingTableId", id, "routingTableName", searchName, false);
				break;
			case InstanceTypeConstants.RADIUS_POLICY_GROUP :
				list = checkForName(mode, RadiusPolicyGroup.class, "policyId", id, "policyName", searchName, false);
				break;
			case InstanceTypeConstants.DIAMETER_POLICY_GROUP:
				list = checkForName(mode, DiameterPolicyGroup.class, "policyId", id, "policyName", searchName, false);
				break;
			case InstanceTypeConstants.DIAMETER_CONCURRENCY:
				list = checkForName(mode, DiameterConcurrencyData.class, "diaConConfigId", id, "name", searchName, false);
				break;
			case InstanceTypeConstants.PLUGIN:
				list = checkForName(mode, PluginInstData.class, "pluginInstanceId", id, "name", searchName, false);
				break;
			case InstanceTypeConstants.DIAMETER_PEER_GROUP:
				list = checkForName(mode, DiameterPeerGroup.class, "peerGroupId", id, "peerGroupName", searchName, false);
				break;
			case InstanceTypeConstants.TGPP_AAA_POLICY:
				list = checkForName(mode, TGPPAAAPolicyData.class, "tgppAAAPolicyId", id, "name", searchName, false);
				break;
			case InstanceTypeConstants.RADIUS_ESI_GROUP:
				list = checkForName(mode, RadiusESIGroupData.class, "id", id, "name", searchName, false);
				break;
			case InstanceTypeConstants.SCRIPT:
				list = checkForName(mode, ScriptInstanceData.class, "scriptId", id, "name", searchName, false);
				break;
			case InstanceTypeConstants.CORRELATED_RADIUS:
				list = checkForName(mode, CorrelatedRadiusData.class, "id", id, "name", searchName, false);
				break;
			default:
				break;
			}

			if(list !=null && !list.isEmpty()){
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
	private List<Object[]> checkForName(String mode, Class<?> instanceClass, String idProperty, String id, String namePropery, String searchName, boolean caseSensitivity) throws DataManagerException{
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
			} else if(caseSensitivity){
				Session hbmSession = ((HibernateDataSession)session).getSession();
				list = checkForDuplicateName(hbmSession,instanceClass,searchName);
			}
			
			if(Collectionz.isNullOrEmpty(list) == false){
				return list;
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
	
	private List<Object[]> checkForDuplicateName(Session hbmSession, Class<?> instanceClass, String searchName) {
		List<Object[]> list =null;
		
		Query query= hbmSession.createQuery("from "+ instanceClass.getName() +" where lower(name) = '" + searchName.toLowerCase() +"'");  
		return list = query.list();
	}
}
