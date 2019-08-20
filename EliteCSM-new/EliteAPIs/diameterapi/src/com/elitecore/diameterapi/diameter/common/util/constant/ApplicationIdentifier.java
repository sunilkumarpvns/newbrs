package com.elitecore.diameterapi.diameter.common.util.constant;

import static com.elitecore.commons.base.Preconditions.checkNotNull;

import java.util.HashMap;
import java.util.Map;

import javax.annotation.Nonnull;

import com.elitecore.diameterapi.core.common.util.constant.ApplicationEnum;
import com.elitecore.diameterapi.mibs.constants.ServiceTypes;

public enum ApplicationIdentifier implements ApplicationEnum {
	
	BASE(0, 0, ServiceTypes.BOTH, Application.BASE),
	NASREQ(1, 0, ServiceTypes.BOTH, Application.NASREQ),	
	MOBILE_IPV4(2, 0, ServiceTypes.BOTH, Application.MOBILE_IPV4),
	BASEACCOUNTING(3, 0, ServiceTypes.ACCT,  Application.BASEACCOUNTING),
	RELAY(0xFFFFFFFFL, 0,ServiceTypes.BOTH, Application. BASE),
	CC(4, 0, ServiceTypes.AUTH,  Application.CC),	
	EAP(5, 0,ServiceTypes.AUTH,  Application.EAP),
	SIP(6, 0,ServiceTypes.BOTH, Application.SIP),
	TGPP_CX_PX(16777216, 10415,ServiceTypes.BOTH, Application.TGPP_CX_PX),
	TGPP_SH_PH(16777217, 10415,ServiceTypes.BOTH, Application.TGPP_SH_PH),
	TGPP_RE(16777218, 10415,ServiceTypes.BOTH, Application.TGPP_RE),
	TGPP_WX(16777219, 10415,ServiceTypes.BOTH, Application.TGPP_WX),
	TGPP_ZN(16777220, 10415,ServiceTypes.BOTH, Application.TGPP_ZN),
	TGPP_ZH(16777221, 10415,ServiceTypes.BOTH, Application.TGPP_ZH),
	TGPP_GQ(16777222, 10415,ServiceTypes.BOTH, Application.TGPP_GQ),
	TGPP_GMB(16777223, 10415,ServiceTypes.BOTH, Application.TGPP_GMB),
	TGPP_GX_29_210_15(16777224, 10415,ServiceTypes.BOTH, Application.TGPP_GX_29_210_15),
	TGPP_GX_OVER_GY(16777225, 10415,ServiceTypes.BOTH, Application.TGPP_GX_OVER_GY),
	TGPP_MM10(16777226, 10415,ServiceTypes.BOTH, Application.TGPP_MM10),
	TGPP_RX_29_211_17(16777229, 10415,ServiceTypes.BOTH, Application.TGPP_RX_29_211_17),
	TGPP_PR(16777230, 10415,ServiceTypes.BOTH, Application.TGPP_PR),
	TGPP_RX_29_214_18(16777236, 10415,ServiceTypes.BOTH, Application.TGPP_RX_29_214_18),
	TGPP_GX_29_212_18(16777238, 10415,ServiceTypes.BOTH, Application.TGPP_GX_29_212_18),
	TGPP_STA(16777250, 10415,ServiceTypes.BOTH, Application.TGPP_STA),
	TGPP_S6A(16777251, 10415,ServiceTypes.BOTH, Application.TGPP_S6A),
	TGPP_S13_S13(16777252, 10415,ServiceTypes.BOTH, Application.TGPP_S13_S13),
	TGPP_SLG(16777255, 10415,ServiceTypes.BOTH, Application.TGPP_SLG),
	TGPP_SWM(16777264, 10415,ServiceTypes.BOTH, Application.TGPP_SWM),
	TGPP_SWX(16777265, 10415,ServiceTypes.AUTH, Application.TGPP_SWX),
	TGPP_GXX(16777266, 10415,ServiceTypes.BOTH, Application.TGPP_GXX),
	TGPP_S9(16777267, 10415,ServiceTypes.BOTH, Application.TGPP_S9),
	TGPP_ZPN(16777268, 10415,ServiceTypes.BOTH, Application.TGPP_ZPN),
	TGPP_S6B(16777272, 10415,ServiceTypes.BOTH, Application.TGPP_S6B),
	TGPP_SLH(16777291, 10415,ServiceTypes.BOTH, Application.TGPP_SLH),
	TGPP_SGMB(16777292, 10415,ServiceTypes.BOTH, Application.TGPP_SGMB),
	TGPP_SY(16777302, 10415,ServiceTypes.BOTH, Application. TGPP_SY);
	
	public final long applicationId;
	public final long vendorId;
	public final Application application;
	public final ServiceTypes serviceType;
	
	private static final Map<Long, ApplicationIdentifier> map;
	public static final ApplicationIdentifier[] VALUES = values();
	
	static {
		map = new HashMap<Long, ApplicationIdentifier>();
		for (ApplicationIdentifier type : VALUES) {
			map.put(type.applicationId, type);
			}
		}
	
	ApplicationIdentifier(long applicationId, 
			long vendorId, 
			@Nonnull ServiceTypes appType, 
			@Nonnull Application application) {
		this.applicationId = applicationId;
		this.vendorId = vendorId;
		this.serviceType = checkNotNull(appType, "serviceType is null");
		this.application = checkNotNull(application, "application is null");
	}
	
	public static ApplicationIdentifier fromApplicationIdentifiers(long applicationId) {
		return map.get(applicationId);
	}
	
	@Override
	public long getVendorId() {
		return this.vendorId;
	}

	@Override
	public Application getApplication() {
		return this.application;
	}

	@Override
	public long getApplicationId() {
		return applicationId;
	}

	@Override
	public ServiceTypes getApplicationType() {
		return serviceType;
	}

	public static String getDisplayName(long applicationId) {
		
		ApplicationIdentifier applicationIdentifier = fromApplicationIdentifiers(applicationId);
		if(applicationIdentifier == null)
			return String.valueOf(applicationId);
		
		return applicationIdentifier.getApplication().getDisplayName();
	}
	
	@Override
	public String toString() {
		return new StringBuilder()
		.append(getVendorId())
		.append(":")
		.append(getApplicationId())
		.append(" [").append(getApplication().getDisplayName()).append("]").toString();
	}
}
