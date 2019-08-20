package com.elitecore.elitesm.ws.rest;

import javax.ws.rs.Consumes;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import com.elitecore.elitesm.ws.rest.accesspolicy.AccessPolicyController;
import com.elitecore.elitesm.ws.rest.ccservicepolicy.CCServicePolicyController;
import com.elitecore.elitesm.ws.rest.chargingservicepolicy.ChargingServicePolicyController;
import com.elitecore.elitesm.ws.rest.concurrentloginpolicy.ConcurrentLoginPolicyController;
import com.elitecore.elitesm.ws.rest.datasource.DatabaseDataSourceController;
import com.elitecore.elitesm.ws.rest.defaultconfiguration.DefaultConfigurationController;
import com.elitecore.elitesm.ws.rest.diameter.authorizationpolicies.AuthorizationPolicyController;
import com.elitecore.elitesm.ws.rest.diameterconcurrency.DiameterConcurrencyController;
import com.elitecore.elitesm.ws.rest.diameterpeergroup.DiameterPeerGroupController;
import com.elitecore.elitesm.ws.rest.diameterpeerprofiles.DiameterPeerProfilesController;
import com.elitecore.elitesm.ws.rest.diameterpeers.DiameterPeersController;
import com.elitecore.elitesm.ws.rest.diameterpolicygroup.DiameterPolicyGroupController;
import com.elitecore.elitesm.ws.rest.diameterroutingtable.DiameterRoutingTableController;
import com.elitecore.elitesm.ws.rest.digestconfiguration.DigestConfigurationController;
import com.elitecore.elitesm.ws.rest.dynauthservicepolicy.DynAuthServicePolicyController;
import com.elitecore.elitesm.ws.rest.eapservicepolicy.EapServicePolicyController;
import com.elitecore.elitesm.ws.rest.externalsystem.ExternalSystemController;
import com.elitecore.elitesm.ws.rest.gracepolicy.GracePolicyController;
import com.elitecore.elitesm.ws.rest.ippool.IPPoolController;
import com.elitecore.elitesm.ws.rest.ldapdatasource.LDAPDataSourceController;
import com.elitecore.elitesm.ws.rest.license.LicenseController;
import com.elitecore.elitesm.ws.rest.nasservicepolicy.NasServicePolicyController;
import com.elitecore.elitesm.ws.rest.plugins.DiameterUniversalPluginController;
import com.elitecore.elitesm.ws.rest.plugins.GroovyPluginController;
import com.elitecore.elitesm.ws.rest.plugins.TransactionLoggerController;
import com.elitecore.elitesm.ws.rest.plugins.UniversalPluginController;
import com.elitecore.elitesm.ws.rest.prioritytable.PriorityTableController;
import com.elitecore.elitesm.ws.rest.radius.clientprofile.TrustedClientProfileController;
import com.elitecore.elitesm.ws.rest.radius.correlatedradius.CorrelatedRadiusController;
import com.elitecore.elitesm.ws.rest.radius.radiuspolicy.RadiusPolicyController;
import com.elitecore.elitesm.ws.rest.radiusesigroup.RadiusESIGroupController;
import com.elitecore.elitesm.ws.rest.radiuspolicygroup.RadiusPolicyGroupController;
import com.elitecore.elitesm.ws.rest.radiusservicepolicy.RadiusServicePolicyController;
import com.elitecore.elitesm.ws.rest.script.ScriptController;
import com.elitecore.elitesm.ws.rest.serverconfig.alertconfiguration.AlertConfigurationController;
import com.elitecore.elitesm.ws.rest.serverconfig.copypacketconfig.CopyPacketConfigController;
import com.elitecore.elitesm.ws.rest.serverconfig.diametersessionmanager.DiameterSessionManagerController;
import com.elitecore.elitesm.ws.rest.serverconfig.driver.DriverController;
import com.elitecore.elitesm.ws.rest.serverconfig.eapconfig.EAPConfigurationController;
import com.elitecore.elitesm.ws.rest.serverconfig.server.CSMServerSynchronizeFromController;
import com.elitecore.elitesm.ws.rest.serverconfig.server.CSMServerSynchronizeToController;
import com.elitecore.elitesm.ws.rest.serverconfig.server.EliteCLIController;
import com.elitecore.elitesm.ws.rest.serverconfig.server.RMServerSynchronizeFromController;
import com.elitecore.elitesm.ws.rest.serverconfig.server.RMServerSynchronizeToController;
import com.elitecore.elitesm.ws.rest.serverconfig.server.ServerController;
import com.elitecore.elitesm.ws.rest.serverconfig.server.configurations.elitecsmserver.EliteCSMServerController;
import com.elitecore.elitesm.ws.rest.serverconfig.server.configurations.resourcemanager.RMServerController;
import com.elitecore.elitesm.ws.rest.serverconfig.server.services.EliteCSMServicesController;
import com.elitecore.elitesm.ws.rest.serverconfig.server.services.RMServicesController;
import com.elitecore.elitesm.ws.rest.serverconfig.sessionmanager.SessionManagerController;
import com.elitecore.elitesm.ws.rest.serverconfig.translationmapping.TranslationMappingConfigController;
import com.elitecore.elitesm.ws.rest.sslcertificates.SSLCertificatesController;
import com.elitecore.elitesm.ws.rest.system.databaseproperties.DatabasePropertiesController;
import com.elitecore.elitesm.ws.rest.tgppservicepolicy.TgppServicePolicyController;

@Path("/{version}")
@Consumes({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML })
@Produces({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML })
public class RestController {

	@Path("/datasource")
	public DatabaseDataSourceController getDatabaseDataSourceController() {
		return new DatabaseDataSourceController();
	}

	@Path("/server")
	public ServerController getServerController() {
		return new ServerController();
	}

	@Path("/server/elitecsmserver/configuration")
	public EliteCSMServerController getEliteCSMServerController(){
		return new EliteCSMServerController();
	}

	@Path("/server/elitecsmserver/synchronizeto")
	public CSMServerSynchronizeToController getSysnchronizationToController(){
		return new CSMServerSynchronizeToController();
	}

	@Path("/server/elitecsmserver/synchronizefrom")
	public CSMServerSynchronizeFromController getSysnchronizationFromController(){
		return new CSMServerSynchronizeFromController();
	}

	@Path("/server/resourcemanager/configuration")
	public RMServerController getRMMangerController(){
		return new RMServerController();
	}

	@Path("/server/elitecsmserver/cli")
	public EliteCLIController getEliteCliController(){
		return new EliteCLIController();
	}
	
	@Path("/radiuspolicy")
	public RadiusPolicyController getRadiusPolicyController() {
		return new RadiusPolicyController();
	}

	@Path("/authorizationpolicy")
	public AuthorizationPolicyController getAuthorizationPolicyController() {
		return new AuthorizationPolicyController();
	}

	@Path("/accesspolicy")
	public AccessPolicyController getAccessPolicyController(){
		return new AccessPolicyController();
	}

	@Path("/gracepolicy")
	public GracePolicyController getGracePolicyController(){
		return new GracePolicyController();
	}

	@Path("/sslcertificates")
	public SSLCertificatesController getSSLCertificatesController(){
		return new SSLCertificatesController();
	}
	
	@Path("/concurrentloginpolicy")
	public ConcurrentLoginPolicyController getConcurrentLoginPolicyController(){
		return new ConcurrentLoginPolicyController();
	}

	@Path("/driver")
	public DriverController getDriverController() {
		return new DriverController();
	}

	@Path("/clientprofile")
	public TrustedClientProfileController getTrustedClientProfile() {
		return new TrustedClientProfileController();
	}

	@Path("/diameterpeerprofiles")
	public DiameterPeerProfilesController getDiameterPeerProfilesController(){
		return new DiameterPeerProfilesController();
	}

	@Path("/digestconfiguration")
	public DigestConfigurationController getDigestConfigurationController() {
		return new DigestConfigurationController();
	}

	@Path("/diameterpeers")
	public DiameterPeersController getDiameterPeersController(){
		return new DiameterPeersController();
	}

	@Path("/diameterpeergroup")
	public DiameterPeerGroupController getDiameterPeerGroupController() {
		return new DiameterPeerGroupController();
	}

	@Path("/diameterpolicygroup")
	public DiameterPolicyGroupController getDiameterPolicyGroupController() {
		return new DiameterPolicyGroupController();
	}

	@Path("/radiuspolicygroup")
	public RadiusPolicyGroupController getRadiusPolicyGroupController() {
		return new RadiusPolicyGroupController();
	}

	@Path("/radiusesigroup")
	public RadiusESIGroupController getRadiusESIGroupController(){
		return new RadiusESIGroupController();
	}

	@Path("/prioritytable")
	public PriorityTableController getPriorityTableController(){
		return new PriorityTableController();
	}

	@Path("/diameterroutingtable")
	public DiameterRoutingTableController getDiameterRoutingTableController(){
		return new DiameterRoutingTableController();
	}

	@Path("/ldapdatasource")
	public LDAPDataSourceController getLDAPDataSourceController() {
		return new LDAPDataSourceController();
	}

	@Path("/translationmapping")
	public TranslationMappingConfigController getTransalationMappingController() {
		return new TranslationMappingConfigController();
	}

	@Path("/esi")
	public ExternalSystemController getExternalSystemInterfaceController() {
		return new ExternalSystemController();
	}

	@Path("/diameterconcurrency")
	public DiameterConcurrencyController getDiameterConcurrencyController(){
		return new DiameterConcurrencyController();
	}

	@Path("/eapconfig")
	public EAPConfigurationController getEAPConfigurationController(){
		return new EAPConfigurationController();
	}

	@Path("/ippool")
	public IPPoolController getIPPoolController(){
		return new IPPoolController();
	}

	@Path("/ccservicepolicy")
	public CCServicePolicyController getCCServicePolicyController() {
		return new CCServicePolicyController();
	}

	@Path("/tgppservicepolicy")
	public TgppServicePolicyController getTgppServicePolicyController() {
		return new TgppServicePolicyController();
	}

	@Path("/radiusservicepolicy")
	public RadiusServicePolicyController getRadiusServicePolicyController() {
		return new RadiusServicePolicyController();
	}

	@Path("/dynauthservicepolicy")
	public DynAuthServicePolicyController getDynauthServicePolicyController() {
		return new DynAuthServicePolicyController();
	}

	@Path("/chargingservicepolicy")
	public ChargingServicePolicyController getChargingServicePolicyController() {
		return new ChargingServicePolicyController();
	}
	
	@Path("/sessionmanager")
	public SessionManagerController getSessionManagerController(){
		return new SessionManagerController();
	}

	@Path("/copypacket")
	public CopyPacketConfigController getCopyPacketConfigController() {
		return new CopyPacketConfigController();
	}

	@Path("/universalplugin")
	public UniversalPluginController getUniversalPluginController(){
		return new UniversalPluginController();
	}

	@Path("/transactionloggerplugin")
	public TransactionLoggerController getTransactionLoggerController(){
		return new TransactionLoggerController();
	}

	@Path("/groovyplugin")
	public GroovyPluginController getGroovyPluginController(){
		return new GroovyPluginController();
	}
	
	@Path("/server/elitecsmserver/services")
	public EliteCSMServicesController getEliteCSMServicesController(){
		return new EliteCSMServicesController();
	}
	
	@Path("server/resourcemanager/services")
	public RMServicesController getRMServicesController(){
		return new RMServicesController();
	}

	@Path("/server/elitecsmserver/license")
	public LicenseController getLicenseController() {
		return new LicenseController();
	}

	@Path("/nasservicepolicy")
	public NasServicePolicyController getNasServicePolicyController() {
		return new NasServicePolicyController();
	}

	@Path("/alertconfiguration")
	public AlertConfigurationController getAlertConfigurationController() {
		return new AlertConfigurationController();
	}

	@Path("/eapservicepolicy")
	public EapServicePolicyController getEapServicePolicyController() {
		return new EapServicePolicyController();
	}
	
	@Path("/universalplugin/diameter")
	public DiameterUniversalPluginController getDiameterUniversalPluginController(){
		return new DiameterUniversalPluginController();
	}
	
	@Path("/diametersessionmanager")
	public DiameterSessionManagerController getDiameterSessionManagerController() {
		return new DiameterSessionManagerController();
	}
	
	@Path("/defaultconfiguration")
	public DefaultConfigurationController getAutoConfigurationController() {
		return new DefaultConfigurationController();
	}
	
	@Path("/server/elitermserver/synchronizeto")
	public RMServerSynchronizeToController getRMSysnchronizationToController(){
		return new RMServerSynchronizeToController();
	}

	@Path("/server/elitermserver/synchronizefrom")
	public RMServerSynchronizeFromController getRMSysnchronizationFromController(){
		return new RMServerSynchronizeFromController();
	}
	
	@Path("/subscriber")
	public RestWSSubscriberProvider getRestWSSubscriberProvider(){
		return new RestWSSubscriberProvider();
	}
	
	@Path("/session")
	public RestWSSessionsProvider getSessionProvider(){
		return new RestWSSessionsProvider();
	}
	
	@Path("/script")
	public ScriptController getScriptController(){
		return new ScriptController();
	}
	
	@Path("/databaseproperties")
	public DatabasePropertiesController getDatabaseProperties(){
		return new DatabasePropertiesController();
	}

	@Path("/correlatedradius")
	public CorrelatedRadiusController getCorrelatedRadiusController () {
		return  new CorrelatedRadiusController();
	}
}