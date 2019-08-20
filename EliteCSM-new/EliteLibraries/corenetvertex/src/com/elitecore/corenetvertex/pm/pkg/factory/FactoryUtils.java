package com.elitecore.corenetvertex.pm.pkg.factory;

import java.util.List;

import com.elitecore.commons.base.Collectionz;
import com.elitecore.commons.logging.LogManager;
import com.elitecore.corenetvertex.constants.CommonConstants;
import com.elitecore.corenetvertex.constants.DataUnit;
import com.elitecore.corenetvertex.constants.QoSUnit;
import com.elitecore.corenetvertex.constants.TimeUnit;
import com.elitecore.corenetvertex.pkg.PkgData;
import com.elitecore.corenetvertex.pkg.PkgGroupOrderData;
import com.elitecore.corenetvertex.pkg.PkgType;
import com.elitecore.corenetvertex.pm.pkg.datapackage.conf.GroupManageOrder;

public class FactoryUtils {
	
	private static final int MIN_PORT = 1025;
	private static final int MAX_PORT = 65535;
	private static final String MODULE = "FCTRY-UTIL";

	public static String format(List<String> failReasons) {

		StringBuilder builder = new StringBuilder();

		for (int index = failReasons.size() - 1; index > 0; index--) {
			builder.append(failReasons.get(index));
			builder.append(CommonConstants.COMMA);
		}

		builder.append(failReasons.get(0));

		return builder.toString();
	}

	public static long getDataInBytes(Long dataQuantity, String dataUnit) {

		if (dataQuantity == null) {
			return 0;
		}

		DataUnit usageUnit = DataUnit.valueOf(dataUnit);

		if (usageUnit != null) {
			return usageUnit.toBytes(dataQuantity);
		}

		return dataQuantity;
	}

	public static long getTimeInSeconds(Long time, String timeUnitStr) {

		if (time == null) {
			return 0;
		}

		TimeUnit timeUnit = TimeUnit.valueOf(timeUnitStr);

		if (timeUnit != null) {
			return timeUnit.toSeconds(time);
		}

		return time;
	}

	public static long getQoSInBps(long qos, String qosUnit) {

		if (qos == 0) {
			return qos;
		}

		QoSUnit unit = QoSUnit.fromVal(qosUnit);
		long bps;
		if (unit != null) {
			bps = unit.toBps(qos);
		} else {
			bps = qos;
		}

		/*
		 * while calculating bits per seconds, it may possible that bps cross
		 * the max value long value. In that case it value will be less than
		 * zero
		 */
		if (bps > CommonConstants.UNSIGNED32_MAX_VALUE) {
			bps = CommonConstants.UNSIGNED32_MAX_VALUE;
		}

		return bps;
	}

	public static List<GroupManageOrder> createGroupManageOrders(
			List<PkgGroupOrderData> pkgGroupWiseOrders) {

		List<GroupManageOrder> groupOrderConfs = Collectionz.newArrayList();

		if (Collectionz.isNullOrEmpty(pkgGroupWiseOrders)) {
			return groupOrderConfs;
		}

		for (PkgGroupOrderData data : pkgGroupWiseOrders) {
			groupOrderConfs.add(new GroupManageOrder(data.getId(), data
					.getGroupId(), PkgType.valueOf(data.getType()), data
					.getOrderNumber(), data.getPkgData().getId()));
		}

		return groupOrderConfs;
	}

	public static void setDefaultValues(PkgData pkgData) {
		if (pkgData.isExclusiveAddOn() == null) {
			pkgData.setExclusiveAddOn(false);
		}

		if (pkgData.isMultipleSubscription() == null) {
			pkgData.setMultipleSubscription(true);
		}

		if (pkgData.isReplaceableByAddOn() == null) {
			pkgData.setReplaceableByAddOn(true);
		}
	}

	/**
	 * This method generates String from List of Strings, Appends "\n" after new
	 * String
	 *
	 * @param failReasons
	 * @return String
	 */
	public static String getCompleteFailReason(List<String> failReasons) {
		StringBuilder failStringBuilder = new StringBuilder();
		failStringBuilder.append("Caused by: [");
		int index = 1;

		for (int i = 0; i < failReasons.size() - 1; i++) {
			failStringBuilder.append(index++).append(".")
					.append(failReasons.get(i)).append(",");
		}
		failStringBuilder.append(index).append(".")
				.append(failReasons.get(failReasons.size() - 1)).append("]");

		return failStringBuilder.toString();
	}

	public static boolean isValidPort(int port) {
		return port >= MIN_PORT && port <= MAX_PORT;
	}
	
	public static int validateRange(String parameterName, Integer originalVal, int defaultValue, int minValue, int maxValue) {
		
		if(originalVal == null) {
			LogManager.getLogger().warn(MODULE, "Considering default value " + defaultValue + " for " + parameterName + ". Reason: Configuration value not specified" );
			return defaultValue;
		}
		int resultValue = originalVal;

		if(resultValue < minValue || resultValue > maxValue){
			LogManager.getLogger().warn(MODULE, "Considering default value " + defaultValue + " for " + parameterName + ". Reason: Invalid configured value " + originalVal);
			resultValue = defaultValue ;
		}
		return resultValue;

	}
}
