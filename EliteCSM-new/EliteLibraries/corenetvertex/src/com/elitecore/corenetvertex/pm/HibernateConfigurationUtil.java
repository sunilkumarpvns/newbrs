package com.elitecore.corenetvertex.pm;

import com.elitecore.corenetvertex.pkg.chargingrulebasename.ChargingRuleDataServiceTypeData;
import com.elitecore.corenetvertex.pkg.dataservicetype.DataServiceTypeData;
import com.elitecore.corenetvertex.pkg.ratinggroup.RatingGroupData;
import org.hibernate.Session;
import org.hibernate.cfg.Configuration;

import static com.elitecore.commons.logging.LogManager.getLogger;

public class HibernateConfigurationUtil {

	private static final String MODULE = "HIBERNATE-CONF-UTILL";

	public static void setConfigurationClasses(Configuration configuration) {
		configuration.addPackage("com.elitecore.corenetvertex.pkg");
		configuration.addAnnotatedClass(com.elitecore.corenetvertex.pkg.PkgData.class);
		configuration.addAnnotatedClass(com.elitecore.corenetvertex.pkg.PkgGroupOrderData.class);
		configuration.addAnnotatedClass(com.elitecore.corenetvertex.pkg.quota.QuotaProfileData.class);
		configuration.addAnnotatedClass(com.elitecore.corenetvertex.pkg.qos.QosProfileData.class);
		configuration.addAnnotatedClass(com.elitecore.corenetvertex.pkg.pccrule.PCCRuleData.class);
		configuration.addAnnotatedClass(com.elitecore.corenetvertex.pkg.qos.QosProfileDetailData.class);
		configuration.addAnnotatedClass(com.elitecore.corenetvertex.pkg.qos.TimePeriodData.class);
		configuration.addAnnotatedClass(com.elitecore.corenetvertex.pkg.qos.DeviceProfileData.class);
		configuration.addAnnotatedClass(com.elitecore.corenetvertex.pkg.quota.QuotaProfileDetailData.class);
		configuration.addAnnotatedClass(RatingGroupData.class);
		configuration.addAnnotatedClass(com.elitecore.corenetvertex.pkg.dataservicetype.ServiceDataFlowData.class);
		configuration.addAnnotatedClass(DataServiceTypeData.class);
		configuration.addAnnotatedClass(com.elitecore.corenetvertex.pkg.dataservicetype.DefaultServiceDataFlowData.class);
		configuration.addAnnotatedClass(com.elitecore.corenetvertex.pkg.notification.UsageNotificationData.class);
		configuration.addAnnotatedClass(com.elitecore.corenetvertex.pkg.notification.QuotaNotificationData.class);
		configuration.addAnnotatedClass(com.elitecore.corenetvertex.pkg.notification.NotificationTemplateData.class);
		configuration.addAnnotatedClass(com.elitecore.corenetvertex.pkg.ims.IMSPkgData.class);
		configuration.addAnnotatedClass(com.elitecore.corenetvertex.pkg.ims.IMSPkgServiceData.class);
		configuration.addAnnotatedClass(com.elitecore.corenetvertex.pkg.ims.IMSPkgPCCAttributeData.class);
		configuration.addAnnotatedClass(com.elitecore.corenetvertex.pkg.ims.MediaTypeData.class);
		configuration.addAnnotatedClass(com.elitecore.corenetvertex.pkg.syquota.SyQuotaProfileData.class);
		configuration.addAnnotatedClass(com.elitecore.corenetvertex.pkg.syquota.SyQuotaProfileDetailData.class);
		configuration.addAnnotatedClass(com.elitecore.corenetvertex.pkg.chargingrulebasename.ChargingRuleBaseNameData.class);
		configuration.addAnnotatedClass(ChargingRuleDataServiceTypeData.class);
		configuration.addAnnotatedClass(com.elitecore.corenetvertex.pkg.rnc.RncProfileData.class);
		configuration.addAnnotatedClass(com.elitecore.corenetvertex.pkg.rnc.RncProfileDetailData.class);

		/*DATA SOURCE */
		configuration.addAnnotatedClass(com.elitecore.corenetvertex.database.DatabaseData.class);
		configuration.addAnnotatedClass(com.elitecore.corenetvertex.ldap.LdapData.class);
		configuration.addAnnotatedClass(com.elitecore.corenetvertex.ldap.LdapBaseDn.class);

		/*SESSION */
		configuration.addAnnotatedClass(com.elitecore.corenetvertex.sm.session.SessionConfigurationData.class);
		configuration.addAnnotatedClass(com.elitecore.corenetvertex.sm.session.SessionConfigurationFieldMappingData.class);

		/*ACL */
		configuration.addAnnotatedClass(com.elitecore.corenetvertex.sm.acl.StaffData.class);
		configuration.addAnnotatedClass(com.elitecore.corenetvertex.sm.acl.StaffProfilePictureData.class);
		configuration.addAnnotatedClass(com.elitecore.corenetvertex.sm.acl.GroupData.class);
		configuration.addAnnotatedClass(com.elitecore.corenetvertex.sm.acl.StaffGroupRoleRelData.class);
		configuration.addAnnotatedClass(com.elitecore.corenetvertex.sm.acl.RoleModuleActionData.class);
		configuration.addAnnotatedClass(com.elitecore.corenetvertex.sm.acl.RoleData.class);
		configuration.addAnnotatedClass(com.elitecore.corenetvertex.sm.audit.AuditData.class);

		/*GATEWAY*/
		configuration.addAnnotatedClass(com.elitecore.corenetvertex.sm.gateway.AttributeMappingData.class);
		configuration.addAnnotatedClass(com.elitecore.corenetvertex.sm.gateway.DiameterGatewayData.class);
		configuration.addAnnotatedClass(com.elitecore.corenetvertex.sm.gateway.DiameterGatewayProfileData.class);
		configuration.addAnnotatedClass(com.elitecore.corenetvertex.sm.gateway.DiameterGwProfilePacketMapData.class);
		configuration.addAnnotatedClass(com.elitecore.corenetvertex.sm.gateway.DiameterGwProfilePCCRuleMappingData.class);
		configuration.addAnnotatedClass(com.elitecore.corenetvertex.sm.gateway.GroovyScriptData.class);
		configuration.addAnnotatedClass(com.elitecore.corenetvertex.sm.gateway.PacketMappingData.class);
		configuration.addAnnotatedClass(com.elitecore.corenetvertex.sm.gateway.PCCRuleMappingData.class);
		configuration.addAnnotatedClass(com.elitecore.corenetvertex.sm.gateway.RadiusGatewayData.class);
		configuration.addAnnotatedClass(com.elitecore.corenetvertex.sm.gateway.RadiusGatewayProfileData.class);
		configuration.addAnnotatedClass(com.elitecore.corenetvertex.sm.gateway.RadiusGwProfilePacketMapData.class);
		configuration.addAnnotatedClass(com.elitecore.corenetvertex.sm.gateway.RadiusGwProfilePCCRuleMappingData.class);
		configuration.addAnnotatedClass(com.elitecore.corenetvertex.sm.gateway.VendorData.class);
		configuration.addAnnotatedClass(com.elitecore.corenetvertex.sm.gateway.ServiceGuidingData.class);


		configuration.addAnnotatedClass(com.elitecore.corenetvertex.sm.spinterface.SpInterfaceData.class);
		configuration.addAnnotatedClass(com.elitecore.corenetvertex.sm.spinterface.DbSpInterfaceData.class);
		configuration.addAnnotatedClass(com.elitecore.corenetvertex.sm.spinterface.LdapSpInterfaceData.class);
		configuration.addAnnotatedClass(com.elitecore.corenetvertex.sm.spinterface.SpInterfaceFieldMappingData.class);
		configuration.addAnnotatedClass(com.elitecore.corenetvertex.sm.spr.SprData.class);
		configuration.addAnnotatedClass(com.elitecore.corenetvertex.sm.ddf.DdfData.class);
		configuration.addAnnotatedClass(com.elitecore.corenetvertex.sm.ddf.DdfSprRelData.class);

		configuration.addAnnotatedClass(com.elitecore.corenetvertex.sm.serverinstance.ServerInstanceData.class);
		configuration.addAnnotatedClass(com.elitecore.corenetvertex.sm.servergroup.ServerGroupData.class);
		configuration.addAnnotatedClass(com.elitecore.corenetvertex.sm.servergroup.ServerGroupServerInstanceRelData.class);
		configuration.addAnnotatedClass(com.elitecore.corenetvertex.sm.serverprofile.ServerProfileData.class);
		configuration.addAnnotatedClass(com.elitecore.corenetvertex.sm.notificationagents.EmailAgentData.class);
		configuration.addAnnotatedClass(com.elitecore.corenetvertex.sm.notificationagents.SMSAgentData.class);
		configuration.addAnnotatedClass(com.elitecore.corenetvertex.sm.serverinstance.ServerInstanceGroovyScriptData.class);

		/*Driver Management*/
		configuration.addAnnotatedClass(com.elitecore.corenetvertex.sm.driver.DriverData.class);
		configuration.addAnnotatedClass(com.elitecore.corenetvertex.sm.driver.csv.CsvDriverData.class);
		configuration.addAnnotatedClass(com.elitecore.corenetvertex.sm.driver.csv.CsvDriverFieldMappingData.class);
		configuration.addAnnotatedClass(com.elitecore.corenetvertex.sm.driver.csv.CsvDriverStripMappingData.class);
		configuration.addAnnotatedClass(com.elitecore.corenetvertex.sm.driver.dbcdr.DbCdrDriverData.class);
		configuration.addAnnotatedClass(com.elitecore.corenetvertex.sm.driver.dbcdr.DbCdrDriverFieldMappingData.class);

		/*Alert Management*/
		configuration.addAnnotatedClass(com.elitecore.corenetvertex.sm.alerts.AlertListenerData.class);
		configuration.addAnnotatedClass(com.elitecore.corenetvertex.sm.alerts.AlertListenerRelData.class);

		/*Location Configuration*/
		configuration.addAnnotatedClass(com.elitecore.corenetvertex.sm.location.region.RegionData.class);
		configuration.addAnnotatedClass(com.elitecore.corenetvertex.sm.location.city.CityData.class);
		configuration.addAnnotatedClass(com.elitecore.corenetvertex.sm.location.area.AreaData.class);
		configuration.addAnnotatedClass(com.elitecore.corenetvertex.sm.location.area.LacInformationData.class);
		configuration.addAnnotatedClass(com.elitecore.corenetvertex.sm.location.geography.GeographyData.class);

        /*Network Configuration*/
		configuration.addAnnotatedClass(com.elitecore.corenetvertex.sm.routing.network.CountryData.class);
		configuration.addAnnotatedClass(com.elitecore.corenetvertex.sm.routing.network.BrandData.class);
		configuration.addAnnotatedClass(com.elitecore.corenetvertex.sm.routing.network.OperatorData.class);
		configuration.addAnnotatedClass(com.elitecore.corenetvertex.sm.routing.network.NetworkData.class);


		configuration.addAnnotatedClass(com.elitecore.corenetvertex.sm.device.DeviceData.class);

		/*Routing Table*/
		configuration.addAnnotatedClass(com.elitecore.corenetvertex.sm.routing.mccmncroutingtable.RoutingTableData.class);
		configuration.addAnnotatedClass(com.elitecore.corenetvertex.sm.routing.mccmncgroup.MccMncGroupData.class);
		configuration.addAnnotatedClass(com.elitecore.corenetvertex.sm.routing.mccmncroutingtable.RoutingTableGatewayRelData.class);

		/*PD Instance Configuration*/
		configuration.addAnnotatedClass(com.elitecore.corenetvertex.systeminformation.PDContextInformation.class);
		
		

		configuration.addAnnotatedClass(com.elitecore.corenetvertex.sm.pccservicepolicy.PccServicePolicyData.class);
		
		/*File Location*/
		configuration.addAnnotatedClass(com.elitecore.corenetvertex.sm.filelocation.FileLocationData.class);
		configuration.addAnnotatedClass(com.elitecore.corenetvertex.sm.filelocation.ColumnMappingData.class);
		configuration.addAnnotatedClass(com.elitecore.corenetvertex.sm.filelocation.FileOutputConfigurationData.class);
		
		/* Partner RnC Configuration */
		configuration.addAnnotatedClass(com.elitecore.corenetvertex.pd.lob.LobData.class);
		configuration.addAnnotatedClass(com.elitecore.corenetvertex.pd.service.ServiceData.class);
		configuration.addAnnotatedClass(com.elitecore.corenetvertex.pd.partner.PartnerData.class);
		configuration.addAnnotatedClass(com.elitecore.corenetvertex.pd.account.AccountData.class);
		configuration.addAnnotatedClass(com.elitecore.corenetvertex.pd.prefixes.PrefixesData.class);
		configuration.addAnnotatedClass(com.elitecore.corenetvertex.pd.account.AccountPrefixMasterRelationData.class);
		configuration.addAnnotatedClass(com.elitecore.corenetvertex.pd.account.PrefixListMasterData.class);

		/* Offline RnC*/
		configuration.addAnnotatedClass(com.elitecore.corenetvertex.sm.serverprofile.OfflineRncServerProfileData.class);
		configuration.addAnnotatedClass(com.elitecore.corenetvertex.sm.dictonary.VendorInformation.class);
		configuration.addAnnotatedClass(com.elitecore.corenetvertex.sm.dictonary.AttributeData.class);
		configuration.addAnnotatedClass(com.elitecore.corenetvertex.sm.dictonary.AttributeSupportedValueData.class);
		configuration.addAnnotatedClass(com.elitecore.corenetvertex.sm.dictonary.AttributeValueData.class);


		/* File Mapping */
		configuration.addAnnotatedClass(com.elitecore.corenetvertex.sm.filemapping.FileMappingData.class);
		configuration.addAnnotatedClass(com.elitecore.corenetvertex.sm.filemapping.FileMappingDetail.class);
		
		/*Currency*/
		configuration.addAnnotatedClass(com.elitecore.corenetvertex.pd.currency.CurrencyData.class);
		
		/*PartnerGroup*/
		configuration.addAnnotatedClass(com.elitecore.corenetvertex.pd.partnergroup.PartnerGroupData.class);
		
		/* Guiding*/
		configuration.addAnnotatedClass(com.elitecore.corenetvertex.pd.guiding.GuidingData.class);
		
		/* Calendar */
		configuration.addAnnotatedClass(com.elitecore.corenetvertex.pd.calender.CalenderData.class);
		configuration.addAnnotatedClass(com.elitecore.corenetvertex.pd.calender.CalenderDetails.class);
		
		/* Rate Card*/
		configuration.addAnnotatedClass(com.elitecore.corenetvertex.pd.pbss.ratecard.RateCardData.class);
		configuration.addAnnotatedClass(com.elitecore.corenetvertex.pd.pbss.ratecard.RateCardVersionRelation.class);
		configuration.addAnnotatedClass(com.elitecore.corenetvertex.pd.pbss.ratecard.RateCardVersionDetail.class);

		/*Monetary  Rate Card*/
		configuration.addAnnotatedClass(com.elitecore.corenetvertex.pd.rncpackage.RncPackageData.class);
		configuration.addAnnotatedClass(com.elitecore.corenetvertex.pd.ratecard.RateCardData.class);
		configuration.addAnnotatedClass(com.elitecore.corenetvertex.pd.ratecardgroup.RateCardGroupData.class);
		configuration.addAnnotatedClass(com.elitecore.corenetvertex.pd.ratecardgroup.TimeSlotRelationData.class);
		configuration.addAnnotatedClass(com.elitecore.corenetvertex.pd.rncpackage.notification.RncNotificationData.class);

		/*Non Monetary Rate Card*/
		configuration.addAnnotatedClass(com.elitecore.corenetvertex.pd.ratecard.NonMonetaryRateCardData.class);

		configuration.addAnnotatedClass(com.elitecore.corenetvertex.pd.ratecard.MonetaryRateCardData.class);
		configuration.addAnnotatedClass(com.elitecore.corenetvertex.pd.ratecard.MonetaryRateCardVersion.class);
		configuration.addAnnotatedClass(com.elitecore.corenetvertex.pd.ratecard.MonetaryRateCardVersionDetail.class);
		/*Rate Card Group*/
		configuration.addAnnotatedClass(com.elitecore.corenetvertex.pd.pbss.ratecardgroup.RateCardGroupData.class);
		configuration.addAnnotatedClass(com.elitecore.corenetvertex.pd.pbss.ratecardgroup.TimeSlotRelationData.class);

		/*RnC Package*/
		configuration.addAnnotatedClass(com.elitecore.corenetvertex.pd.pbss.rncpackage.RncPackageData.class);

		/*Product Specification Data*/
		configuration.addAnnotatedClass(com.elitecore.corenetvertex.pd.productoffer.ProductSpecData.class);
		configuration.addAnnotatedClass(com.elitecore.corenetvertex.pd.productoffer.ProductSpecServicePkgRelData.class);

		/*Product Offer Data*/
		configuration.addAnnotatedClass(com.elitecore.corenetvertex.pd.productoffer.ProductOfferData.class);
		configuration.addAnnotatedClass(com.elitecore.corenetvertex.pd.productoffer.ProductOfferServicePkgRelData.class);
		configuration.addAnnotatedClass(com.elitecore.corenetvertex.pd.productoffer.ProductOfferAutoSubscriptionRelData.class);

		/* Prefix Data */
		configuration.addAnnotatedClass(com.elitecore.corenetvertex.pd.prefix.PrefixData.class);
		configuration.addAnnotatedClass(com.elitecore.corenetvertex.pd.prefix.PrefixDataExt.class);

		/* LRN Data */
		configuration.addAnnotatedClass(com.elitecore.corenetvertex.pd.lrn.LrnData.class);

		/*System Parameter Data */
		configuration.addAnnotatedClass(com.elitecore.corenetvertex.sm.systemparameter.SystemParameterData.class);

		/*notification data*/
		configuration.addAnnotatedClass(com.elitecore.corenetvertex.pd.topup.DataTopUpData.class);
		configuration.addAnnotatedClass(com.elitecore.corenetvertex.pd.notification.TopUpNotificationData.class);
		configuration.addAnnotatedClass(com.elitecore.corenetvertex.pkg.ratecard.DataRateCardData.class);
		configuration.addAnnotatedClass(com.elitecore.corenetvertex.pkg.ratecard.DataRateCardVersionRelationData.class);
		configuration.addAnnotatedClass(com.elitecore.corenetvertex.pkg.ratecard.DataRateCardVersionDetailData.class);

		configuration.addAnnotatedClass(com.elitecore.corenetvertex.pd.ratecard.MonetaryRateCardData.class);

		/*Slice Config Data*/
		configuration.addAnnotatedClass(com.elitecore.corenetvertex.pd.sliceconfig.SliceConfigData.class);

		/* Monetary Recharge Plan*/
		configuration.addAnnotatedClass(com.elitecore.corenetvertex.pd.monetaryrechargeplan.MonetaryRechargePlanData.class);

		/*BoD Package*/
		configuration.addAnnotatedClass(com.elitecore.corenetvertex.pd.bod.BoDData.class);
		configuration.addAnnotatedClass(com.elitecore.corenetvertex.pd.bod.BoDQosMultiplierData.class);
		configuration.addAnnotatedClass(com.elitecore.corenetvertex.pd.bod.BoDServiceMultiplierData.class);

		/*Revenue Detail*/
		configuration.addAnnotatedClass(com.elitecore.corenetvertex.pd.revenuedetail.RevenueDetailData.class);

	}
	
	public static void closeQuietly(Session session) {

		if (session == null) {
			return;
		}

		if (session.isOpen() == false) {
			if (getLogger().isDebugLogLevel()) {
				getLogger().debug(MODULE, "Previous session is already closed.");
			}

			return;
		}


		try {
			session.close();
		} catch (Exception ex) {
			getLogger().error(MODULE, "Error while closing previous session. Reason:" + ex.getMessage());
			getLogger().trace(MODULE, ex);
		}

	}
}
