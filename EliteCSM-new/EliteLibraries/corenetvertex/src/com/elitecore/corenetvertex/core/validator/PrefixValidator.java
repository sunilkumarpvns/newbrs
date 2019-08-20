package com.elitecore.corenetvertex.core.validator;

import com.elitecore.commons.base.Strings;
import com.elitecore.commons.logging.LogManager;
import com.elitecore.corenetvertex.pd.prefix.PrefixDataExt;
import com.elitecore.corenetvertex.sm.ResourceData;
import com.elitecore.corenetvertex.sm.routing.network.CountryData;
import com.elitecore.corenetvertex.sm.routing.network.NetworkData;
import com.elitecore.corenetvertex.sm.routing.network.OperatorData;
import com.elitecore.corenetvertex.util.SessionProvider;
import com.elitecore.corenetvertex.util.commons.ImportExportCRUDOperationUtil;
import org.apache.commons.lang.StringUtils;

import java.util.ArrayList;
import java.util.List;

public class PrefixValidator implements Validator<PrefixDataExt, Object, ResourceData> {
    private static final String MODULE = "PREFIX-VALIDATOR";
    private static final String PREFIX = "Prefix ";
    private static final String NOT_EXISTS = " does not exist in System";

    @Override
    public List<String> validate(PrefixDataExt prefixDataImported, Object parentObject, ResourceData pkgData, String action, SessionProvider session) {
        List<String> subReasons = new ArrayList<>();
        try {
            if (Strings.isNullOrBlank(prefixDataImported.getId()) && Strings.isNullOrBlank(prefixDataImported.getPrefix())) {
                subReasons.add("Prefix is mandatory");
            }
            Boolean isPrefixContainsDigit = StringUtils.isNumeric(prefixDataImported.getPrefix());
            if (isPrefixContainsDigit == false) {
                LogManager.getLogger().error(MODULE, PREFIX + prefixDataImported.getPrefix() + " should contains digits");
                subReasons.add(PREFIX + prefixDataImported.getPrefix() + " should contains digits without precision ");
            }
            if (action.equalsIgnoreCase("fail")) {
                List<PrefixDataExt> prefixData = ImportExportCRUDOperationUtil.getAll(PrefixDataExt.class, "prefix", prefixDataImported.getPrefix(), session);
                if ((!(prefixData.isEmpty())) && prefixData.get(0).getPrefix().equals(prefixDataImported.getPrefix())) {
                    subReasons.add(PREFIX + prefixDataImported.getPrefix() + " is already exists in System");
                }
            }
            isCountryExist(prefixDataImported, session, subReasons);
            isOperatorExist(prefixDataImported, session, subReasons);
            isNetworkExist(prefixDataImported, session, subReasons);
        } catch (Exception e) {
            LogManager.getLogger().error(MODULE, "Failed to validate prefix with " + BasicValidations.printIdAndName(prefixDataImported.getId(), prefixDataImported.getPrefix()));
            LogManager.getLogger().trace(MODULE, e);
            subReasons.add("Failed to validate Prefix with " + BasicValidations.printIdAndName(prefixDataImported.getId(), prefixDataImported.getPrefix()));
        }
        return subReasons;
    }

    private void isCountryExist(PrefixDataExt prefixDataImported, SessionProvider session, List<String> subReasons) throws Exception {
        if (Strings.isNullOrBlank(prefixDataImported.getCountryData().getId())) {
            List<CountryData> existingPrefixCountry = ImportExportCRUDOperationUtil.getAll(CountryData.class, "name", prefixDataImported.getCountryData().getName(), session);
            if (existingPrefixCountry.isEmpty()) {
                subReasons.add("Country: " + prefixDataImported.getCountryData().getName() + NOT_EXISTS);
            }
        }
    }

    private void isOperatorExist(PrefixDataExt prefixDataImported, SessionProvider session, List<String> subReasons) throws Exception {
        if (Strings.isNullOrBlank(prefixDataImported.getOperatorData().getId())) {
            List<OperatorData> existingPrefixOperator = ImportExportCRUDOperationUtil.getAll(OperatorData.class, "name", prefixDataImported.getOperatorData().getName(), session);
            if (existingPrefixOperator.isEmpty()) {
                subReasons.add("Operator: " + prefixDataImported.getOperatorData().getName() + NOT_EXISTS);
            }
        }
    }

    private void isNetworkExist(PrefixDataExt prefixDataImported, SessionProvider session, List<String> subReasons) throws Exception {
        if (prefixDataImported != null) {
            if (Strings.isNullOrBlank(prefixDataImported.getNetworkData().getId())) {
                List<NetworkData> existingPrefixNetwork = ImportExportCRUDOperationUtil.getByName(NetworkData.class, prefixDataImported.getNetworkData().getName(), session);
                if (existingPrefixNetwork.isEmpty()) {
                    subReasons.add("Network: " + prefixDataImported.getNetworkData().getName() + NOT_EXISTS);
                }
            }
        } else {
            subReasons.add("Network:  does not exist in System");
        }
    }
}
