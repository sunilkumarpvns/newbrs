package com.elitecore.nvsmx.system.menu;

import com.elitecore.commons.base.Collectionz;
import com.elitecore.commons.base.Strings;
import com.elitecore.nvsmx.sm.model.systemparameter.SystemParameterDAO;
import com.elitecore.nvsmx.system.ConfigurationProvider;

import java.util.ArrayList;
import java.util.List;

public class MenuBuilder{

	private MenuBuilder(){}
 	public static List<MenuItem> getPolicyDesignerMenuItem() {

		List<MenuItem> menuItems = new ArrayList<>();
		
		ContainerMenu sprContainer = new ContainerMenu(1,"Subscriber");
		sprContainer.addMenuList(new NonContainerMenu("Add Subscriber", "policydesigner/subscriber/Subscriber/initCreate"));
		sprContainer.addMenuList(new NonContainerMenu("Search Subscriber", "policydesigner/subscriber/Subscriber/search"));
		sprContainer.addMenuList(new NonContainerMenu("Search Session", "policydesigner/session/Session/search"));
		sprContainer.addMenuList(new NonContainerMenu("Test Subscriber", "policydesigner/subscriber/Subscriber/searchTestSubscriber"));
		sprContainer.addMenuList(new NonContainerMenu("Deleted Subscriber", "policydesigner/subscriber/Subscriber/searchDeletedSubscriber"));
		menuItems.add(sprContainer);
	
		ContainerMenu packageContainer = new ContainerMenu(2, "Product");
		packageContainer.addMenuList(new NonContainerMenu("Product Offer", "pd/productoffer/product-offer"));
		packageContainer.addMenuList(new NonContainerMenu("RnC Package", "pd/rncpackage/rnc-package"));
		packageContainer.addMenuList(new NonContainerMenu("Data Package", "policydesigner/pkg/Pkg/search"));
		packageContainer.addMenuList(new NonContainerMenu("IMS Package", "policydesigner/ims/IMSPkg/search"));
		packageContainer.addMenuList(new NonContainerMenu("Emergency Package", "policydesigner/emergency/EmergencyPkg/search"));
		packageContainer.addMenuList(new NonContainerMenu("Promotional Package", "promotional/policydesigner/pkg/Pkg/PromotionalPkg/search?pkgType=PROMOTIONAL"));
		packageContainer.addMenuList(new NonContainerMenu("Data Top-Up", "pd/datatopup/data-topup"));
		packageContainer.addMenuList(new NonContainerMenu("BoD Package", "pd/bodpackage/bod-package"));
		packageContainer.addMenuList(new NonContainerMenu("Global PCC Rule", "policydesigner/pccrule/PccRule/search"));
		packageContainer.addMenuList(new NonContainerMenu("Global Monetary Rate Card", "pd/globalratecard/global-rate-card"));
		packageContainer.addMenuList(new NonContainerMenu("Charging Rule Base Name", "policydesigner/chargingrulebasename/ChargingRuleBaseName/search"));
		packageContainer.addMenuList(new NonContainerMenu("Service", "pd/service/service"));
		packageContainer.addMenuList(new NonContainerMenu("Revenue Detail", "pd/revenuedetail/revenue-detail"));
		packageContainer.addMenuList(new NonContainerMenu("Data Service Type", "policydesigner/dataservicetype/DataServiceType/search"));
		packageContainer.addMenuList(new NonContainerMenu("Rating Group", "policydesigner/ratinggroup/RatingGroup/search"));
		packageContainer.addMenuList(new NonContainerMenu("Data Slice Configuration", "pd/sliceconfig/slice-config/*"));
		packageContainer.addMenuList(new NonContainerMenu("Monetary Recharge Plan", "pd/monetaryrechargeplan/monetary-recharge-plan"));
		packageContainer.addMenuList(new NonContainerMenu("Reload Policy", "policydesigner/util/Generic/showProgressBar?redirectUrl=ajax/pkgReload/reload"));

		menuItems.add(packageContainer);
		
		menuItems.add(new NonContainerMenu("Notification Template", "policydesigner/notification/NotificationTemplate/search"));
		
		
		return menuItems;

	}


	public static List<MenuItem> getServerManagerMenuItems() {
		List<MenuItem> menuItems = new ArrayList<>();
		ContainerMenu servicePolicyContainer = new ContainerMenu(1,"Service Policy");
		servicePolicyContainer.addMenuList(new NonContainerMenu("PCC Service Policy","sm/pccservicepolicy/pcc-service-policy"));
		menuItems.add(servicePolicyContainer);

		ContainerMenu serverManangerContainer = new ContainerMenu(2,"Server");
		serverManangerContainer.addMenuList(new NonContainerMenu("Server Group", "sm/servergroup/server-group"));
		serverManangerContainer.addMenuList(new NonContainerMenu("Server Profile", "sm/serverprofile/server-profile/*"));
		serverManangerContainer.addMenuList(new NonContainerMenu("Session", "sm/sessionconfiguration/session-configuration/*"));
		serverManangerContainer.addMenuList(new NonContainerMenu("Driver", "sm/driver/csv-driver"));
		serverManangerContainer.addMenuList(new NonContainerMenu("Alert", "sm/alert/file-alert"));
		serverManangerContainer.addMenuList(new NonContainerMenu("Notification Agents", "sm/notificationagents/email-agent"));
		menuItems.add(serverManangerContainer);

		ContainerMenu sprContainer = new ContainerMenu(2, "SPR");
		sprContainer.addMenuList(new NonContainerMenu("Sp Interface", "sm/spinterface/db-sp-interface"));
		sprContainer.addMenuList(new NonContainerMenu("Subscriber Profile", "sm/spr/spr"));
		sprContainer.addMenuList(new NonContainerMenu("DDF", "sm/ddf/ddf/*"));


		menuItems.add(sprContainer);

		ContainerMenu gatewayContainer = new ContainerMenu(3, "Gateway");
		gatewayContainer.addMenuList(new NonContainerMenu("Gateway Profile", "sm/gatewayprofile/diameter-gateway-profile"));
		gatewayContainer.addMenuList(new NonContainerMenu("Gateway", "sm/gateway/diameter-gateway"));
		gatewayContainer.addMenuList(new NonContainerMenu("Packet Mapping", "sm/packetmapping/diameter-packet-mapping"));
		gatewayContainer.addMenuList(new NonContainerMenu("PCC Rule Mapping", "sm/pccrulemapping/diameter-pcc-rule-mapping"));

		menuItems.add(gatewayContainer);
		
		ContainerMenu networkContainer = new ContainerMenu(4, "Network");
		networkContainer.addMenuList(new NonContainerMenu("Network Management", "sm/network/network"));
		networkContainer.addMenuList(new NonContainerMenu("MCC-MNC Group", "sm/mccmncgroup/mcc-mnc-group"));
		networkContainer.addMenuList(new NonContainerMenu("Prefix", "sm/prefix/prefix"));
		networkContainer.addMenuList(new NonContainerMenu("LRN", "sm/lrn/lrn"));
		networkContainer.addMenuList(new NonContainerMenu("Routing Table", "sm/routingtable/routing-table"));
		networkContainer.addMenuList(new NonContainerMenu("Geography", "sm/geography/geography"));
		networkContainer.addMenuList(new NonContainerMenu("Region", "sm/region/region"));
		networkContainer.addMenuList(new NonContainerMenu("City", "sm/city/city"));
		networkContainer.addMenuList(new NonContainerMenu("Area", "sm/area/area"));
		menuItems.add(networkContainer);

		menuItems.add(new NonContainerMenu("Device", "sm/device/device"));

		ContainerMenu externalSystemContainer = new ContainerMenu(6, "External System");
		externalSystemContainer.addMenuList(new NonContainerMenu("DB Data Source", "sm/database/database"));
		externalSystemContainer.addMenuList(new NonContainerMenu("LDAP Data Source", "sm/ldap/ldap"));

		menuItems.add(externalSystemContainer);

		ContainerMenu staffManagementContainer = new ContainerMenu(7, "Staff");
		staffManagementContainer.addMenuList(new NonContainerMenu("Staff", "sm/staff/staff"));
		staffManagementContainer.addMenuList(new NonContainerMenu("Group", "sm/group/group"));
		staffManagementContainer.addMenuList(new NonContainerMenu("Role", "sm/role/role"));
		staffManagementContainer.addMenuList(new NonContainerMenu("LDAP Authentication", "sm/ldapauthconf/ldap-auth-configuration/*"));
		staffManagementContainer.addMenuList(new NonContainerMenu("Password Policy", "sm/passwordpolicyconfig/password-policy-config/*"));

		menuItems.add(staffManagementContainer);

		return menuItems;
	}

	
	public static List<MenuItem> getCommonMenuItems() {
		List<MenuItem> menuItems = new ArrayList<>();
		if(SystemParameterDAO.isSSOEnable()) {
			NonContainerMenu ssoLinkMenu = new NonContainerMenu(ConfigurationProvider.getInstance().getSsoTitle(), ConfigurationProvider.getInstance().getSSOUrl());
			ssoLinkMenu.setLinkType(LinkType.EXTERNAL);
			menuItems.add(ssoLinkMenu);
		}
		menuItems.add(new NonContainerMenu("View Profile", "commons/staff/Staff/view"));
		menuItems.add(new NonContainerMenu("Change Password", "commons/login/ChangePassword/redirectToChangePasswordPage"));
		menuItems.add(new NonContainerMenu("Staff Audit", "commons/audit/Audit/search"));
		menuItems.add(new NonContainerMenu("System Parameter", "sm/systemparameter/system-parameter/show"));
		menuItems.add(new NonContainerMenu("License", "sm/license/license/show"));
		menuItems.add(new NonContainerMenu("Sign Out", "commons/login/Login/logout"));
		return menuItems;
	}

	public static List<MenuItem> getMenuItems(String menuType){

		List<MenuItem> menuItems = Collectionz.newArrayList();

		if(Strings.isNullOrBlank(menuType) == false){
				return getPolicyDesignerMenuItem();
		}
		return menuItems;
	}

}
