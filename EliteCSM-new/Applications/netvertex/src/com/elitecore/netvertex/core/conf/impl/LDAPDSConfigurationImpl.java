package com.elitecore.netvertex.core.conf.impl;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.annotation.Nullable;
import com.elitecore.commons.logging.LogManager;
import com.elitecore.core.commons.configuration.LoadConfigurationException;
import com.elitecore.core.serverx.ServerContext;
import com.elitecore.core.util.url.InvalidURLException;
import com.elitecore.core.util.url.URLData;
import com.elitecore.core.util.url.URLParser;
import com.elitecore.corenetvertex.core.conf.FailReason;
import com.elitecore.corenetvertex.core.ldap.LDAPDataSource;
import com.elitecore.corenetvertex.ldap.LdapData;
import com.elitecore.corenetvertex.pm.HibernateReader;
import com.elitecore.netvertex.core.conf.LDAPDSConfiguration;
import com.elitecore.netvertex.core.conf.impl.base.BaseConfigurationImpl;
import com.elitecore.netvertex.core.util.ConfigLogger;
import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.SessionFactory;


import static com.elitecore.corenetvertex.pm.HibernateConfigurationUtil.closeQuietly;


/**
 * author Harsh Patel
 *
 */
public class LDAPDSConfigurationImpl extends BaseConfigurationImpl implements LDAPDSConfiguration {

	private static final String MODULE = "LDAP-DSCNF";

	private final SessionFactory sessionFactory;

	private Map<String, LDAPDataSource> datasourceMap;
	private Map<String, LDAPDataSource> datasourceNameMap;

	public LDAPDSConfigurationImpl(ServerContext serverContext, SessionFactory sessionFactory) {
		super(serverContext);
		this.sessionFactory = sessionFactory;
		datasourceMap=new HashMap<>();
		datasourceNameMap = new HashMap<>();
	}

	@Override
	public LDAPDataSource getDatasourceByName(String dsName) {
		return datasourceNameMap.get(dsName);
	}

	@Override
	public Map<String, LDAPDataSource> getDatasourceMap() {
		return datasourceMap;
	}
	@Override
	public void reloadConfiguration() throws LoadConfigurationException {
		LogManager.getLogger().info(MODULE,"Reload configuration for LDAP datasource started");

		Map<String, LDAPDataSource> tempDatasourceMap = new HashMap<String, LDAPDataSource>(this.datasourceMap);
		Map<String, LDAPDataSource> tempDatasourceNameMap = new HashMap<String, LDAPDataSource>(this.datasourceNameMap);

		Session session = null;

		try{
			session = sessionFactory.openSession();
			List<LdapData> ldapDatas = HibernateReader.readAll(LdapData.class, session);

			for(LdapData ldapData : ldapDatas) {
				LDAPDataSource ldapDataSource = createLDAPDs(ldapData);
				tempDatasourceMap.put(ldapData.getId(), ldapDataSource);
				tempDatasourceNameMap.put(ldapData.getName(), ldapDataSource);
			}

			this.datasourceMap = tempDatasourceMap;
			this.datasourceNameMap = tempDatasourceNameMap;

			ConfigLogger.getInstance().info(MODULE, toString());
			LogManager.getLogger().info(MODULE,"Reload configuration for LDAP datasource successfully completed");
		} catch (HibernateException ex) {
			throw new LoadConfigurationException("Error while reloading DB datasource. Reason: " + ex.getMessage(), ex);
		} catch(Exception ex) {
			throw new LoadConfigurationException("Error while reloading LDAP datasource configuration. Reason: " + ex.getMessage(), ex);
		} finally {
			closeQuietly(session);
		}
	}

	@Nullable
	private URLData parseURLData(String ipAddress) {
		URLData urlData = null;
		try{
            urlData = URLParser.parse(ipAddress);
        }catch(InvalidURLException ex){
            LogManager.getLogger().trace(MODULE, ex);
        }
		return urlData;
	}

	@Override
	public void readConfiguration() throws LoadConfigurationException {
		reloadConfiguration();
	}
	@Override
	public String toString() {
		StringWriter stringBuffer = new StringWriter();
		PrintWriter out = new PrintWriter(stringBuffer);
		out.println();
		out.println();
		out.println(" -- LDAP Datasource Configuration -- ");
		if (datasourceMap == null || datasourceMap.isEmpty()) {
			out.println("      No configuration found");
		} else {
			for(LDAPDataSource dataSource:datasourceMap.values()){
				out.println(dataSource);
				out.println();
			}
		}
		out.println();
		out.close();
		return stringBuffer.toString();
	}

	@Override
	public LDAPDataSource getDatasourceById(String dsID) {
		return datasourceMap.get(dsID);
	}


	@Override
	public Map<String, LDAPDataSource> getDatasourceNameMap() {
		return datasourceNameMap;
	}

	private LDAPDataSource createLDAPDs(LdapData ldapData){
        FailReason failReason = new FailReason("LDAP DS");
		return LDAPDataSourceImpl.create(ldapData, failReason);
	}

}
