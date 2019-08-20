package com.elitecore.netvertex.gateway.diameter.utility;

import com.elitecore.commons.base.Maps;
import com.elitecore.commons.io.IndentingPrintWriter;
import com.elitecore.commons.logging.LogManager;
import com.elitecore.diameterapi.diameter.common.packet.avps.IDiameterAVP;
import com.elitecore.diameterapi.diameter.common.packet.avps.grouped.AvpGrouped;
import com.elitecore.diameterapi.diameter.common.util.constant.DiameterAVPConstants;

import java.io.StringWriter;
import java.util.Map;

import static com.elitecore.commons.logging.LogManager.getLogger;

/**
 * ResultCodeEntry stores
 * - Default DIAMETER result code AVP
 * - Application Id wise DIAMETER result code AVP
 *
 *
 * @Auther Chetan.Sankhala
 */
public class ResultCodeEntry {
    private IDiameterAVP defaultResultCodeAVP;
    private Map<Integer, IDiameterAVP> appWiseResultCodes;

    ResultCodeEntry(IDiameterAVP defaultResultCodeAVP, Map<Integer, IDiameterAVP> appWiseResultCodes) {
        this.defaultResultCodeAVP = defaultResultCodeAVP;
        this.appWiseResultCodes = appWiseResultCodes;
    }

    ResultCodeEntry(IDiameterAVP defaultResultCodeAVP) {
        this(defaultResultCodeAVP, Maps.newHashMap());
    }

    /**
     * First checks for application specific result code,
     * if not found then returns default result code
     * @param appId
     * @return
     */
    IDiameterAVP getResultCodeAVP(int appId) {
        IDiameterAVP appResultCodeAVP = this.appWiseResultCodes.get(appId);
        if (appResultCodeAVP != null) {
            return clone(appResultCodeAVP);
        }

        return clone(defaultResultCodeAVP);
    }

    @Override
    public String toString() {
        StringWriter stringWriter = new StringWriter();
        IndentingPrintWriter printWriter = new IndentingPrintWriter(stringWriter);
        toString(printWriter);
        printWriter.close();
        return stringWriter.toString();
    }

    public void toString(IndentingPrintWriter writer) {

        writer.println("DefaultResultCode = " + defaultResultCodeAVP.getStringValue());

        writer.println("ApplicationWiseEntry:");
        if (Maps.isNullOrEmpty(appWiseResultCodes)) {
            writer.incrementIndentation();
            writer.println("No Application Entry");
            writer.decrementIndentation();
        } else {
            writer.incrementIndentation();
            appWiseResultCodes.forEach((applicationId, diameterAVP) -> {
                writer.println("Application Id = " + applicationId);
                resultCodeToString(writer, diameterAVP);

            });
            writer.decrementIndentation();
        }

    }

    private void resultCodeToString(IndentingPrintWriter writer, IDiameterAVP diameterAVP) {
        if (diameterAVP instanceof AvpGrouped) {
            writer.println("VendorId = "+ (((AvpGrouped) diameterAVP).getSubAttribute(DiameterAVPConstants.VENDOR_ID).getStringValue()));
            writer.println("Experimental ResultCode = "+ (((AvpGrouped) diameterAVP).getSubAttribute(DiameterAVPConstants.EXPERIMENTAL_RESULT_CODE).getStringValue()));
        } else {
            writer.println("Result Code= " + diameterAVP.getStringValue());
        }
    }

    public IDiameterAVP clone(IDiameterAVP diameterAVP) {
        try {
            return (IDiameterAVP) diameterAVP.clone();
        } catch (CloneNotSupportedException e) {
            getLogger().trace(e);
        }
        return null;
    }
}
