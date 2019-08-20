package com.elitecore.netvertex.service.offlinernc.productspec;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.elitecore.commons.base.SimpleDateFormatThreadLocal;
import com.elitecore.commons.base.TimeRange;
import com.elitecore.commons.logging.LogManager;
import com.elitecore.core.commons.InitializationFailedException;
import com.elitecore.corenetvertex.pd.productoffer.ProductSpecData;
import com.elitecore.corenetvertex.pd.productoffer.ProductSpecServicePkgRelData;
import com.elitecore.netvertex.core.conf.impl.SystemParameterConfiguration;
import com.elitecore.netvertex.service.offlinernc.rncpackage.RnCPackage;
import com.elitecore.netvertex.service.offlinernc.rncpackage.RnCPackageFactory;

public class ProductSpecFactory {

	private static final String MODULE = "PRODUCT-SPEC-FACTORY";

	private RnCPackageFactory rncPackageFactory;
	private Map<String, ProductSpec> productSpecNameToProductSpec;
	private SystemParameterConfiguration systemParameterConfiguration;

	public ProductSpecFactory(RnCPackageFactory rncPackageFactory, SystemParameterConfiguration systemParameterConfiguration) {
		this.rncPackageFactory = rncPackageFactory ;
		productSpecNameToProductSpec = new HashMap<>();
		this.systemParameterConfiguration = systemParameterConfiguration;
	}

	public ProductSpec create(ProductSpecData productSpecData) throws InitializationFailedException {
		ProductSpec productSpec = productSpecNameToProductSpec.get(productSpecData.getName());
		if (productSpec == null) {
			productSpec = createProductSpec(productSpecData);
			productSpecNameToProductSpec.put(productSpecData.getName(), productSpec);
		}

		return productSpec;
	}

	private ProductSpec createProductSpec(ProductSpecData productSpecData) throws InitializationFailedException {
		TimeRange timeRange = null;
		SimpleDateFormatThreadLocal simpleDateFormatThreadLocal = SimpleDateFormatThreadLocal.create(systemParameterConfiguration.getEdrDateTimeStampFormat());
		try {
			if (productSpecData.getAvailabilityStartDate() != null && productSpecData.getAvailabilityEndDate() != null) {
				timeRange = TimeRange.closed(systemParameterConfiguration.getEdrDateTimeStampFormat(), simpleDateFormatThreadLocal.get().format(productSpecData.getAvailabilityStartDate())
						, simpleDateFormatThreadLocal.get().format(productSpecData.getAvailabilityEndDate()));
			} else if (productSpecData.getAvailabilityStartDate() != null && productSpecData.getAvailabilityEndDate() == null) {
				timeRange = TimeRange.closed(systemParameterConfiguration.getEdrDateTimeStampFormat(), simpleDateFormatThreadLocal.get().format(productSpecData.getAvailabilityStartDate()));
			} else {
				timeRange = TimeRange.open();
			}
		} catch (ParseException e) {
			LogManager.getLogger().warn(MODULE, "Unable to parse date in " + systemParameterConfiguration.getEdrDateTimeStampFormat() +" format");
		}

		List<ProductSpecService> productSpecServices = new ArrayList<>();
		for (ProductSpecServicePkgRelData productOfferServicePkgRelData : productSpecData.getProductOfferServicePkgRelDataList()) {
			productSpecServices.add(createProductSpecService(productOfferServicePkgRelData, productSpecData.getSubscriptionCurrency()));
		}

		return new ProductSpec(productSpecData.getName(), productSpecServices, timeRange, systemParameterConfiguration);
	}

	private ProductSpecService createProductSpecService(ProductSpecServicePkgRelData productOfferServicePkgRelData, String currency) throws InitializationFailedException {
		RnCPackage rncPackage = rncPackageFactory.create(productOfferServicePkgRelData.getRncPackageData(), currency);

		return new ProductSpecService(productOfferServicePkgRelData.getServiceData().getName(),
				productOfferServicePkgRelData.getServiceData().getId(),
				rncPackage);
	}

}
