package com.elitecore.corenetvertex.pm.pkg.factory;

import static com.elitecore.commons.base.Collectionz.isNullOrEmpty;
import static com.elitecore.commons.logging.LogManager.getLogger;
import static com.elitecore.commons.logging.LogManager.ignoreTrace;

import java.util.ArrayList;
import java.util.EnumMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import com.elitecore.corenetvertex.constants.IMSServiceAction;
import com.elitecore.corenetvertex.pkg.constants.PCCAttribute;
import com.elitecore.corenetvertex.pkg.ims.IMSPkgPCCAttributeData;
import com.elitecore.corenetvertex.pkg.ims.IMSPkgServiceData;
import com.elitecore.corenetvertex.pkg.ims.MediaTypeData;
import com.elitecore.corenetvertex.pm.pkg.PackageFactory;
import com.elitecore.corenetvertex.pm.pkg.imspackage.IMSServiceTable;
import com.elitecore.corenetvertex.pm.pkg.imspackage.MediaType;
import com.elitecore.corenetvertex.pm.pkg.imspackage.PCCAttributeTableEntry;
import com.elitecore.exprlib.parser.exception.InvalidExpressionException;
import com.elitecore.exprlib.parser.expression.LogicalExpression;

public class IMSServiceTableFactory {

	private static final String MODULE = "IMS-SERVICE-TBL-FCTRY";
	private PCCAttributeTableEntryFactory pccAttributeTableEntryFactory;
	private PackageFactory packageFactory;
	
	public IMSServiceTableFactory(PackageFactory packageFactory, PCCAttributeTableEntryFactory pccAttributeTableEntryFactory) {
		this.packageFactory = packageFactory;
		this.pccAttributeTableEntryFactory = pccAttributeTableEntryFactory;
	}

	public IMSServiceTable create(IMSPkgServiceData imsPkgServiceData, List<String> qosProfileFailReasons) {

        List<IMSPkgPCCAttributeData> imsPackagePccAttributesDatas = imsPkgServiceData.getImsPkgPCCAttributeDatas();

        Map<PCCAttribute, List<PCCAttributeTableEntry>> pccAttributeToTableEntries = new EnumMap<PCCAttribute, List<PCCAttributeTableEntry>>(PCCAttribute.class);
        if (isNullOrEmpty(imsPackagePccAttributesDatas) == false) {

            Iterator<IMSPkgPCCAttributeData> iterator = imsPackagePccAttributesDatas.iterator();

            while (iterator.hasNext()) {

                IMSPkgPCCAttributeData imsPkgPCCAttributeData = iterator.next();

                List<String> pccAttributeTableEntryFailReasons = new ArrayList<String>();
                PCCAttributeTableEntry pccAttributeTableEntry = pccAttributeTableEntryFactory.create(imsPkgPCCAttributeData, pccAttributeTableEntryFailReasons);

				/* DO NOT CHANGE THE SEQUENCE */
                if (pccAttributeTableEntryFailReasons.isEmpty() == false) {
                    qosProfileFailReasons.add("PCC attribute (" + imsPkgPCCAttributeData.getAttribute() + ") parsing fail. Cause by: "
                            + FactoryUtils.format(pccAttributeTableEntryFailReasons));
                } else if (pccAttributeTableEntry == null) {
                    if (getLogger().isWarnLogLevel()) {
                        getLogger().warn(MODULE, "Skipping PCC attribute(" + imsPkgPCCAttributeData.getAttribute()
                                + "). Reason: value not configured");
                    }
                    iterator.remove();
                } else {
                    List<PCCAttributeTableEntry> pccAttributeTableEntries = pccAttributeToTableEntries.get(pccAttributeTableEntry.getPCCAttribute());
                    if (pccAttributeTableEntries == null) {
                        pccAttributeTableEntries = new ArrayList<PCCAttributeTableEntry>();
                        pccAttributeToTableEntries.put(pccAttributeTableEntry.getPCCAttribute(), pccAttributeTableEntries);
                    }

                    pccAttributeTableEntries.add(pccAttributeTableEntry);

                }
            }
        }


        MediaTypeData mediaTypeData = imsPkgServiceData.getMediaTypeData();
		MediaType serviceType = packageFactory.createMediaType(mediaTypeData.getId(), mediaTypeData.getMediaIdentifier(), mediaTypeData.getName());

        LogicalExpression logicalExpression = null;

        if (imsPkgServiceData.getExpression() != null) {
            try {
                logicalExpression = com.elitecore.exprlib.compiler.Compiler.getDefaultCompiler().parseLogicalExpression(imsPkgServiceData.getExpression());
            } catch (InvalidExpressionException e) { 
            	ignoreTrace(e);
                qosProfileFailReasons.add("Invalid condition: " + imsPkgServiceData.getExpression());
            }
        }

        IMSServiceAction action = imsPkgServiceData.getAction();
        if (action == null) {
            qosProfileFailReasons.add("Invalid action: " + imsPkgServiceData.getAction());
        }

        if (qosProfileFailReasons.isEmpty() == false) {
            return null;
        }

        return packageFactory.createIMSServiceTable(imsPkgServiceData.getName()
                , serviceType
                , imsPkgServiceData.getAfApplicationId()
                , logicalExpression
                , imsPkgServiceData.getExpression()
                , action
                , pccAttributeToTableEntries);

    }

        
}
