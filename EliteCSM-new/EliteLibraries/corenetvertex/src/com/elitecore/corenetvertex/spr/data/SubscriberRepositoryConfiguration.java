package com.elitecore.corenetvertex.spr.data;

import com.elitecore.corenetvertex.constants.CommonConstants;
import com.elitecore.corenetvertex.constants.SpInterfaceType;
import com.elitecore.corenetvertex.core.conf.FailReason;
import com.elitecore.corenetvertex.core.db.DBDataSource;
import com.elitecore.corenetvertex.sm.spr.SprData;
import com.elitecore.corenetvertex.spr.ABMFconfiguration;
import com.elitecore.corenetvertex.spr.ABMFconfigurationImpl;
import com.elitecore.corenetvertex.spr.SPInterfaceConfiguration;
import com.elitecore.corenetvertex.spr.UMconfiguration;
import com.elitecore.corenetvertex.spr.UMconfigurationImpl;
import com.elitecore.corenetvertex.spr.data.impl.DBSPInterfaceConfigurationImpl;
import com.elitecore.corenetvertex.spr.data.impl.LDAPSPInterfaceConfigurationImpl;
import com.elitecore.corenetvertex.util.DataSourceProvider;
import com.elitecore.corenetvertex.util.IndentingToStringBuilder;
import com.elitecore.corenetvertex.util.ToStringable;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

/**
 * 
 * @author Jay Trivedi
 * 
 */
public class SubscriberRepositoryConfiguration implements ToStringable {

	private String sprId;
	private String sprName;
	private DBDataSource dbDataSource;
	private SPInterfaceConfiguration spInterfaceDetail;
	private UMconfiguration umConfiguration;
	private SPRFields alternateIdField;
	private List<String> groupsIds;
	private ABMFconfiguration abmFconfiguration;

	public SubscriberRepositoryConfiguration(String sprId,
                                             String sprName,
                                             DBDataSource dataSource,
                                             SPInterfaceConfiguration spInterfaceDetail,
											 UMconfiguration umConfiguration,
											 SPRFields alternateIdField,
											 List<String> groupIds,
											 ABMFconfiguration abmFconfiguration) {

		this(sprId, sprName, dataSource, umConfiguration, alternateIdField, groupIds, abmFconfiguration);
		this.spInterfaceDetail = spInterfaceDetail;
	}

	public SubscriberRepositoryConfiguration(String sprId,
											 String sprName,
											 DBDataSource dataSource,
											 UMconfiguration umConfiguration,
											 SPRFields alternateIdField,
											 List<String> groupsList,
											 ABMFconfiguration abmFconfiguration) {
		this.sprId = sprId;
		this.sprName = sprName;
		this.dbDataSource = dataSource;
		this.umConfiguration = umConfiguration;
		this.alternateIdField = alternateIdField;
		this.groupsIds = groupsList;
		this.abmFconfiguration = abmFconfiguration;
	}

	public String getSprId() {
		return sprId;
	}

	public String getSprName() {
		return sprName;
	}

	public DBDataSource getDbDataSource() {
		return dbDataSource;
	}

	public SPRFields getAlternateIdField() {
		return alternateIdField;
	}

	public void setAlternateIdField(SPRFields alternateIdField) {
		this.alternateIdField = alternateIdField;
	}

	public SPInterfaceConfiguration getSpInterfaceConfiguration() {
		return spInterfaceDetail;
	}

	public void setSpInterfaceConfiguration(SPInterfaceConfiguration spInterfaceDetail) {
		this.spInterfaceDetail = spInterfaceDetail;
	}
	
	public void setUmConfiguration(UMconfiguration umConfiguration) {
		this.umConfiguration = umConfiguration;
	}
	
	public UMconfiguration getUmConfiguration() {
		return umConfiguration;
	}
	
	public List<String> getGroupIds() {
		return groupsIds;
	}

	public void setGroupIds(List<String> groupsList) {
		this.groupsIds = groupsList;
	}

	@Override
	public String toString() {
		IndentingToStringBuilder builder = new IndentingToStringBuilder();
		builder.appendHeading(" -- Subscriber Profile Repository -- ");
		toString(builder);
		return builder.toString();
	}

	public ABMFconfiguration getAbmFconfiguration() {
		return abmFconfiguration;
	}

	public static SubscriberRepositoryConfiguration create(SprData defaultSprData, DataSourceProvider dataSourceProvider, FailReason failReason) {


		FailReason dsFailReason = new FailReason(defaultSprData.getName());
		DBDataSource dbDataSource = dataSourceProvider.getDBDataSource(defaultSprData.getDatabaseData(), failReason);
		failReason.addChildModuleFailReasonIfNotEmpty(dsFailReason);

		int batchSize = defaultSprData.getBatchSize()==null?CommonConstants.DEFAULT_BATCH_SIZE:defaultSprData.getBatchSize();
		ABMFconfiguration abmFconfiguration = new ABMFconfigurationImpl(batchSize, CommonConstants.DEFAULT_BATCH_QUERY_TIMEOUT, CommonConstants.QUERY_TIMEOUT_DEFAULT);
		UMconfiguration uMconfiguration = new UMconfigurationImpl(batchSize, CommonConstants.DEFAULT_BATCH_QUERY_TIMEOUT);
		SPRFields sprFields = SPRFields.fromSPRFields(defaultSprData.getAlternateIdField());

		List<String> groups = Collections.emptyList();
		if(Objects.isNull(defaultSprData.getGroups()) == false) {
			groups = Arrays.asList(defaultSprData.getGroups().split(","));
		}

		if(defaultSprData.getSpInterfaceData() == null) {

			return new SubscriberRepositoryConfiguration(defaultSprData.getId(),
					defaultSprData.getName(),
					dbDataSource,
					uMconfiguration,
					sprFields,
					groups,
					abmFconfiguration);

		} else {

			SPInterfaceConfiguration spInterfaceConfiguration;
			if(defaultSprData.getSpInterfaceData().getSpInterfaceType().equalsIgnoreCase(SpInterfaceType.DB_SP_INTERFACE.name())) {

				FailReason dbSPInterfaceFailReasaon = new FailReason("DB SP Interface");
				spInterfaceConfiguration = DBSPInterfaceConfigurationImpl.create(defaultSprData.getSpInterfaceData(), dataSourceProvider, dbSPInterfaceFailReasaon);

				if(dbSPInterfaceFailReasaon.isEmpty() == false) {
					failReason.addChildModuleFailReasonIfNotEmpty(dbSPInterfaceFailReasaon);
				}

			} else {

				FailReason ldapSPInterfaceFailReasaon = new FailReason("LDAP SP Interface");
				spInterfaceConfiguration = LDAPSPInterfaceConfigurationImpl.create(defaultSprData.getSpInterfaceData(), dataSourceProvider, ldapSPInterfaceFailReasaon);

				if(ldapSPInterfaceFailReasaon.isEmpty() == false) {
					failReason.addChildModuleFailReasonIfNotEmpty(ldapSPInterfaceFailReasaon);
				}
			}

			if(failReason.isEmpty() == false) {
				return null;
			}


			return new SubscriberRepositoryConfiguration(defaultSprData.getId(),
					defaultSprData.getName(),
					dbDataSource,
					spInterfaceConfiguration,
					uMconfiguration,
					sprFields,
					groups,
					abmFconfiguration);
		}

	}

	@Override
	public void toString(IndentingToStringBuilder builder) {
		builder.append("Id", sprId)
		.append("Name", sprName)
		.appendChild("Group Ids", groupsIds)
		.append("DataSource", dbDataSource.getDataSourceName())
		.appendChildObject("SP Interface", spInterfaceDetail)
		.append("AlternateId", alternateIdField)
		.appendChildObject("UM Configuration", umConfiguration)
		.appendChildObject("ABMF Configuration", abmFconfiguration);

	}
}
