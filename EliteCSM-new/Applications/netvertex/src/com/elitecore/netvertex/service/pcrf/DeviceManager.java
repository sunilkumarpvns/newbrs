package com.elitecore.netvertex.service.pcrf;

import com.elitecore.core.commons.InitializationFailedException;
import com.elitecore.corenetvertex.pm.HibernateReader;
import com.elitecore.corenetvertex.sm.device.DeviceData;
import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.SessionFactory;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.elitecore.commons.logging.LogManager.getLogger;
import static com.elitecore.corenetvertex.pm.HibernateConfigurationUtil.closeQuietly;
/**
 * 
 * @author Jay Trivedi
 *
 */
public class DeviceManager {

	private static final String MODULE = "DEVICE-MNGR";
	private static final String DISPLAY_NAME = "DEVICE-MANAGER";

	private SessionFactory sessionFactory;
	private Map<String, TACDetail> tacDetailMap;

	public DeviceManager() {
		tacDetailMap = new HashMap<>();
	}

	public void init(SessionFactory sessionFactory) throws InitializationFailedException {
		this.sessionFactory = sessionFactory;
		try {
			read();
		} catch (HibernateException e) {
			throw new InitializationFailedException("Error while reading device manager. Reason: " + e.getMessage(), e);
		}
	}

	private void read() {
		getLogger().info(MODULE, "Initializing device manager");

		Map<String, TACDetail> mapTACDetails = new HashMap<>();
		Session session = null;
		try {
			session = sessionFactory.openSession();
			List<DeviceData> deviceDatas = HibernateReader.readAll(DeviceData.class, session);
			deviceDatas.forEach(deviceData -> {
				TACDetail tacDetail = createTACDetail(deviceData);
				mapTACDetails.put(tacDetail.getTac(), tacDetail);
			});

			tacDetailMap = mapTACDetails;
			if (tacDetailMap.size() == 0) {
				getLogger().info(MODULE, "No device(s) configured");
			} else {
				getLogger().info(MODULE, tacDetailMap.size() + " device(s) read");
			}
			getLogger().info(MODULE, "Device manager initialization completed");
		} finally {
			closeQuietly(session);
		}
	}

	public String getName() {
		return DISPLAY_NAME;
	}

	public TACDetail getTACDetail(String tac) {
		return tacDetailMap.get(tac);
	}

	private TACDetail createTACDetail(DeviceData deviceData) {
		String tac = deviceData.getTac() == null ? "0" : deviceData.getTac();
		String brand = deviceData.getBrand();
		String model = deviceData.getDeviceModel();
		String hwType = deviceData.getHardwareType();
		String os= deviceData.getOs();
		String year = deviceData.getYear();
		String additionalInfo = deviceData.getAdditionalInformation();
		return new TACDetail(tac, brand, model, hwType, os, year, additionalInfo);
	}
}
