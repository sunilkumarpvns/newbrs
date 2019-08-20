package com.elitecore.netvertex.pm;

import java.util.List;
import java.util.Map;
import com.elitecore.corenetvertex.constants.PCRFKeyConstants;
import com.elitecore.corenetvertex.constants.PkgStatus;
import com.elitecore.corenetvertex.constants.PolicyStatus;
import com.elitecore.corenetvertex.pkg.PkgMode;
import com.elitecore.corenetvertex.pm.constants.FlowStatus;
import com.elitecore.corenetvertex.pm.pkg.imspackage.IMSServiceTable;
import com.elitecore.netvertex.gateway.diameter.af.MediaComponent;
import com.elitecore.netvertex.service.pcrf.PCRFRequest;
import com.elitecore.netvertex.service.pcrf.PCRFResponse;
import com.elitecore.netvertex.service.pcrf.util.MediaComponentAwareValueProvider;


import static com.elitecore.commons.logging.LogManager.getLogger;

public class IMSPackage extends com.elitecore.corenetvertex.pm.pkg.imspackage.IMSPackage{

	private static final long serialVersionUID = 1L;
	private static final String MODULE = "IMS-PKG";
	
	public IMSPackage(String id, String name, Map<Long, List<IMSServiceTable>> serviceIdentifierToServiceTables,
                      PkgMode packageMode, PkgStatus availabilityStatus, Double price, List<String> groupIds) {
		super(id, name, serviceIdentifierToServiceTables, packageMode, availabilityStatus, price, groupIds);
	}

	public IMSPackage(String id, String name, PkgMode packageMode, PkgStatus availabilityStatus,
                      PolicyStatus policyStatus, String failReason, String partialFailReason, Double price, List<String> groupIds) {
		super(id, name, packageMode, availabilityStatus, policyStatus, failReason, partialFailReason, price, groupIds);
	}

	public void apply(PCRFRequest pcrfRequest, PCRFResponse pcrfResponse, List<MediaComponent> mediaComponents) {

		if (getLogger().isInfoLogLevel()) {
			getLogger().info(MODULE, "Applying IMS package: " + getName() + " to subscriber: "
					+ pcrfRequest.getAttribute(PCRFKeyConstants.SUB_SUBSCRIBER_IDENTITY.val));
		}

		int mediaComponentIndex = 0;
		while (mediaComponentIndex < mediaComponents.size()) {

			if(getLogger().isDebugLogLevel())
				getLogger().debug(MODULE, "Processing IMS service: " + getName());

			MediaComponent mediaComponent = mediaComponents.get(mediaComponentIndex);

			if (mediaComponent.getFlowStatus() == FlowStatus.REMOVED) {
				if (getLogger().isDebugLogLevel()) {
					getLogger().debug(MODULE, "Skipping processing for service: " + mediaComponent.getMediaType()
							+ ". Reason: Flow-Status is removed(4)");
				}
				mediaComponentIndex++;
				continue;
			}

			List<IMSServiceTable> serviceTables = getServiceIdentfierToServiceTables().get(mediaComponent.getMediaIdentifier());

			if (serviceTables == null) {
				if (getLogger().isDebugLogLevel()) {
					getLogger().debug(MODULE, "Skipping processing for service: " + mediaComponent.getMediaType()
							+ ". Reason: Configuration not found in package");
				}
				mediaComponentIndex++;
				continue;
			}

			boolean isStatisfied = false;
			MediaComponentAwareValueProvider valueProvider = new MediaComponentAwareValueProvider(mediaComponent,pcrfRequest, pcrfResponse);

			for (int i = 0; i < serviceTables.size(); i++) {
				if (((com.elitecore.netvertex.pm.IMSServiceTable) serviceTables.get(i)).apply(mediaComponent, pcrfResponse,
						valueProvider)) {
					isStatisfied = true;
					break;
				}
			}

			if(isStatisfied) {
				mediaComponents.remove(mediaComponentIndex);
				mediaComponentIndex--;
			}

			mediaComponentIndex++;
		}
	}

}
