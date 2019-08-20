package com.elitecore.netvertex.service.offlinernc.productspec;

import com.elitecore.commons.base.SimpleDateFormatThreadLocal;
import com.elitecore.commons.base.TimeRange;
import com.elitecore.commons.logging.LogManager;
import com.elitecore.corenetvertex.constants.OfflineRnCKeyConstants;
import com.elitecore.netvertex.core.conf.impl.SystemParameterConfiguration;
import com.elitecore.netvertex.service.offlinernc.core.OfflineRnCErrorCodes;
import com.elitecore.netvertex.service.offlinernc.core.OfflineRnCErrorMessages;
import com.elitecore.netvertex.service.offlinernc.core.OfflineRnCException;
import com.elitecore.netvertex.service.offlinernc.core.RnCRequest;
import com.elitecore.netvertex.service.offlinernc.rncpackage.RnCPackage;

import java.sql.Timestamp;
import java.text.ParseException;
import java.util.List;

import static com.elitecore.commons.logging.LogManager.getLogger;
import static com.elitecore.netvertex.service.offlinernc.util.RnCPreConditions.checkKeyNotNull;

public class ProductSpec {

	private static final String MODULE = "PRODUCT-SPEC";

	private String name;
	private List<ProductSpecService> productSpecServices;
	private TimeRange timeRange;
	private SystemParameterConfiguration systemParameterConfiguration;

	public ProductSpec(String name, List<ProductSpecService> productSpecServices, TimeRange timeRange, SystemParameterConfiguration systemParameterConfiguration) {
		this.name = name;
		this.productSpecServices = productSpecServices;
		this.timeRange = timeRange;
		this.systemParameterConfiguration = systemParameterConfiguration;
	}

	public RnCPackage selectPackage(RnCRequest request) throws OfflineRnCException {
		String service = checkKeyNotNull(request, OfflineRnCKeyConstants.SERVICE_NAME);

		request.getTraceWriter().println();
		request.getTraceWriter().println();
		request.getTraceWriter().print("[ " + MODULE + " ]");
		request.getTraceWriter().incrementIndentation();
		request.getTraceWriter().println();


		for (ProductSpecService productSpecService : productSpecServices) {
			if (productSpecService.getAlias().equals(service)) {
				if (LogManager.getLogger().isInfoLogLevel()) {
					request.getTraceWriter().println();
					request.getTraceWriter().print("Selected RnC Package: " + productSpecService.getRncPackage().getName()
							+ " from ProductSpec: " + getName());
					request.getTraceWriter().decrementIndentation();
				}
				return productSpecService.getRncPackage();
			}
		}
		request.getTraceWriter().print("No RnC Package Found for service: " + service + " in ProductSpec: " + name);
		request.getTraceWriter().decrementIndentation();
		throw new OfflineRnCException(OfflineRnCErrorCodes.PACKAGE_NOT_FOUND, OfflineRnCErrorMessages.PACKAGE_NOT_FOUND);
	}

	public boolean isEligible(RnCRequest request) throws OfflineRnCException, ParseException {

		SimpleDateFormatThreadLocal simpleDateFormatThreadLocal = SimpleDateFormatThreadLocal.create(systemParameterConfiguration.getEdrDateTimeStampFormat());
		String ratingKeyValue = checkKeyNotNull(request, systemParameterConfiguration.getRateSelectionWhenDateChange());

		if (timeRange.contains(new Timestamp(simpleDateFormatThreadLocal.get().parse(ratingKeyValue).getTime()))) {
			return true;
		} else {
			if (getLogger().isInfoLogLevel()) {
				request.getTraceWriter().println();
				request.getTraceWriter().println();
				request.getTraceWriter().print("[ " + MODULE + " ]");
				request.getTraceWriter().incrementIndentation();
				request.getTraceWriter().println();
				request.getTraceWriter().println();
				request.getTraceWriter().print("Product Specification : " + name + " is not eligible. Reason: Rating Key "
						+ systemParameterConfiguration.getRateSelectionWhenDateChange() + " is outside range.");
				request.getTraceWriter().decrementIndentation();
			}
			return false;
		}
	}

	public String getName() {
		return name;
	}
}
