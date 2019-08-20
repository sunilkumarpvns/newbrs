package com.elitecore.netvertex.core.driver.spr;

import java.util.List;
import javax.annotation.Nullable;
import com.elitecore.core.commons.configuration.LoadConfigurationException;
import com.elitecore.core.serverx.ServerContext;
import com.elitecore.corenetvertex.pm.HibernateConfigurationUtil;
import com.elitecore.corenetvertex.pm.HibernateReader;
import com.elitecore.corenetvertex.sm.ddf.DdfData;
import com.elitecore.corenetvertex.spr.data.DDFConfiguration;
import com.elitecore.corenetvertex.util.DataSourceProvider;
import com.elitecore.netvertex.core.conf.impl.base.BaseConfigurationImpl;
import com.elitecore.netvertex.core.util.ConfigLogger;
import org.hibernate.Session;
import org.hibernate.SessionFactory;


import static com.elitecore.commons.logging.LogManager.getLogger;

/**
 * 
 * @author Chetan.Sankhala
 *
 */
public class DDFConfigurable extends BaseConfigurationImpl {

	private static final String MODULE = "DDF-CNF";
	
	private DDFConfiguration ddfConfiguration;
	private final SessionFactory sessionFactory;
    private final DataSourceProvider dataSourceProvider;


    public DDFConfigurable(ServerContext serverContext, SessionFactory sessionFactory, DataSourceProvider dataSourceProvider) {
		super(serverContext);
		this.sessionFactory = sessionFactory;
        this.dataSourceProvider = dataSourceProvider;
    }

	@Override
	public void readConfiguration() throws LoadConfigurationException {
		getLogger().info(MODULE, "Read configuration for DDF started");

		readDDFConfiguration();

		getLogger().info(MODULE, "Read configuration for DDF completed");

	}

	private void readDDFConfiguration() {
		
		Session session = null;
		try {
			
			session = sessionFactory.openSession();
			List<DdfData> ddfTableDatas = HibernateReader.readAll(DdfData.class, session);
			
			if (ddfTableDatas.isEmpty() == false) {
				DdfData ddfTableData = ddfTableDatas.get(0);

				ddfConfiguration = DDFConfiguration.create(ddfTableData, dataSourceProvider);

				ConfigLogger.getInstance().info(MODULE, this.ddfConfiguration.toString());
			} else {
				getLogger().warn(MODULE, "DDF not configured");
			}
			
		} finally {
			HibernateConfigurationUtil.closeQuietly(session);
		}
		
	}
	
	public @Nullable
	DDFConfiguration getDdfTableData() {
			return ddfConfiguration;
	}
	
	public void reloadConfiguration(){
		getLogger().info(MODULE, "Reload configuration for DDF is started");
		readDDFConfiguration();
		getLogger().info(MODULE, "Reload configuration for DDF is completed");
	}	
}
