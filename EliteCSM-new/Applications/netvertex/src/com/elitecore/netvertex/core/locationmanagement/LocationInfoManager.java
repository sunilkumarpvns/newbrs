package com.elitecore.netvertex.core.locationmanagement;

import com.elitecore.commons.logging.LogLevel;
import com.elitecore.commons.logging.LogManager;
import com.elitecore.core.commons.InitializationFailedException;
import com.elitecore.netvertex.core.locationmanagement.data.CGIConfiguration;
import com.elitecore.netvertex.core.locationmanagement.data.LacConfiguration;
import com.elitecore.netvertex.core.locationmanagement.data.LocationInformationConfiguration;
import com.elitecore.netvertex.core.locationmanagement.data.MccConfiguration;
import com.elitecore.netvertex.core.locationmanagement.data.NetworkConfiguration;
import com.elitecore.netvertex.core.locationmanagement.data.RAIConfiguration;
import com.elitecore.netvertex.core.locationmanagement.data.SAIConfiguration;
import org.hibernate.SessionFactory;

import javax.annotation.Nullable;

import static com.elitecore.commons.logging.LogManager.getLogger;

public class LocationInfoManager implements LocationRepository {
    private static final String MODULE = "LOCN-MGR";
    private final SessionFactory sessionFactory;
    private LocationConfigurable locationConfigurable;

    public LocationInfoManager(SessionFactory sessionfactory) {
        sessionFactory = sessionfactory;
        this.locationConfigurable = new LocationConfigurable(sessionFactory);
    }

    public void init() throws InitializationFailedException {
        LogManager.getLogger().info(MODULE, "Initializing location manager");
        try {
            locationConfigurable.readConfiguration();
        } catch (Exception e) {
            throw new InitializationFailedException("Error while initializing location Manager. Reason: " + e.getMessage(), e);
        }
        LogManager.getLogger().debug(MODULE, "Initializing location manager completed");
    }

    // to fetch location information by cgi
    @Override
    public LocationInformationConfiguration getLocationInformationByCGI(String mcc, String mnc, String lac, String ci) {
        if (LogManager.getLogger().isLogLevel(LogLevel.DEBUG)) {
            LogManager.getLogger().debug(MODULE, "Fetching location information for MCC: " + mcc + " MNC: " + mnc + " LAC: " + lac + " CI: " + ci);
        }
        LocationInformationConfiguration locationInformationConfiguration = getLocationInformationByLai(mcc, mnc, lac);
        if (locationInformationConfiguration == null) {
            if (LogManager.getLogger().isLogLevel(LogLevel.DEBUG)) {
                LogManager.getLogger().debug(MODULE, "Location information can't be found. Reason :No country information found for MCC:" + mcc);
            }
            return null;
        }
        if (locationInformationConfiguration.getLocationId() != null) {
            return locationInformationConfiguration;
        }


        LacConfiguration lacConfiguration = getLacConfiguration(mcc, mnc, lac);
        if (lacConfiguration == null) {
            if (LogManager.getLogger().isLogLevel(LogLevel.DEBUG)) {
                LogManager.getLogger().debug(MODULE, "No location can be found for LAC: " + lac + " Reason: LAC DATA not found. Returning location information: " + locationInformationConfiguration.toString());
            }
            return locationInformationConfiguration;
        }


        if (ci == null) {
            if (LogManager.getLogger().isLogLevel(LogLevel.DEBUG)) {
                LogManager.getLogger().debug(MODULE, "No location can be found from CGIConfiguration Reason: ci is null. Returning location information: " + locationInformationConfiguration.toString());
            }
            return locationInformationConfiguration;
        }

        try {
            CGIConfiguration cgiConfiguration = lacConfiguration.getCgi(Long.parseLong(ci));
            if (cgiConfiguration != null) {
                if (LogManager.getLogger().isLogLevel(LogLevel.DEBUG)) {
                    LogManager.getLogger().debug(MODULE, "Location information successfully fetched for CI: " + ci + ". Returning location information :" + cgiConfiguration.getLocationInformationConfiguration().toString());
                }
                return cgiConfiguration.getLocationInformationConfiguration();
            } else {
                if (LogManager.getLogger().isLogLevel(LogLevel.DEBUG)) {
                    LogManager.getLogger().debug(MODULE, "No location information is fetched for CI. Reason: CGIConfiguration data not found");
                }
            }
        } catch (NumberFormatException e) {
            if (LogManager.getLogger().isLogLevel(LogLevel.ERROR))
                LogManager.getLogger().error(MODULE, "CGIConfiguration data not found for ci: " + ci + ". Reason: " + e.getMessage());
            LogManager.getLogger().trace(e);
        }

        if (LogManager.getLogger().isLogLevel(LogLevel.DEBUG)) {
            LogManager.getLogger().debug(MODULE, "Returning location information: " + locationInformationConfiguration.toString());
        }
        return locationInformationConfiguration;

    }

    // to fetch location information by RAIConfiguration
    @Override
    public LocationInformationConfiguration getLocationInformationByRAI(String mcc, String mnc, String lac, String rac) {
        if (LogManager.getLogger().isLogLevel(LogLevel.DEBUG)) {
            LogManager.getLogger().debug(MODULE, "Fetching location information for MCC: " + mcc + " MNC: " + mnc + " LAC: " + lac + " RAC: " + rac);
        }
        LocationInformationConfiguration locationInformationConfiguration = getLocationInformationByLai(mcc, mnc, lac);
        if (locationInformationConfiguration == null) {
            if (LogManager.getLogger().isLogLevel(LogLevel.DEBUG)) {
                LogManager.getLogger().debug(MODULE, "Location information can't be found. Reason :No country information found for MCC:" + mcc);
            }

            return null;
        }
        if (locationInformationConfiguration.getLocationId() == null) {
            LacConfiguration lacConfiguration = getLacConfiguration(mcc, mnc, lac);
            if (lacConfiguration == null) {
                if (LogManager.getLogger().isLogLevel(LogLevel.DEBUG)) {
                    LogManager.getLogger().debug(MODULE, "No location can be found for LAC: " + lac + " Reason: LAC DATA not found. Returning location information: " + locationInformationConfiguration.toString());
                }
                return locationInformationConfiguration;
            }
            if (rac == null) {
                if (LogManager.getLogger().isLogLevel(LogLevel.DEBUG)) {
                    LogManager.getLogger().debug(MODULE, "No location can be found for  Reason: rac is null. Returning location information: " + locationInformationConfiguration.toString());
                }
                return locationInformationConfiguration;
            }
            try {
                RAIConfiguration raiConfiguration = lacConfiguration.getRai(Long.parseLong(rac));
                if (raiConfiguration != null) {
                    if (LogManager.getLogger().isLogLevel(LogLevel.DEBUG)) {
                        LogManager.getLogger().debug(MODULE, "Location information successfully fetched for RAC: " + rac + ". Returning location information " + locationInformationConfiguration.toString());
                    }
                    return raiConfiguration.getLocationInformationConfiguration();
                } else {
                    if (LogManager.getLogger().isLogLevel(LogLevel.DEBUG)) {
                        LogManager.getLogger().debug(MODULE, "No location information is fetched for RAC. Reason. RAIConfiguration data not found");
                    }
                }
            } catch (NumberFormatException e) {
                if (LogManager.getLogger().isLogLevel(LogLevel.ERROR))
                    LogManager.getLogger().error(MODULE, "RAIConfiguration data not found. Reason" + e.getMessage());
                LogManager.getLogger().trace(e);
            }
        }
        if (LogManager.getLogger().isLogLevel(LogLevel.DEBUG)) {
            LogManager.getLogger().debug(MODULE, "Returning location information: " + locationInformationConfiguration.toString());
        }
        return locationInformationConfiguration;
    }

    @Override
    public LocationInformationConfiguration getLocationInformationByAreaName(String areaName) {
        return this.locationConfigurable.getLocationInformationbyAreaName(areaName);
    }

    // to fetch location information by SAIConfiguration
    @Override
    public LocationInformationConfiguration getLocationInformationBySAI(String mcc, String mnc, String lac, String sac) {

        if (LogManager.getLogger().isLogLevel(LogLevel.DEBUG)) {
            LogManager.getLogger().debug(MODULE, "Fetching location information for MCC: " + mcc + " MNC: " + mnc + " LAC: " + lac + " SAC: " + sac);
        }
        LocationInformationConfiguration locationInformationConfiguration = getLocationInformationByLai(mcc, mnc, lac);
        if (locationInformationConfiguration == null) {
            if (LogManager.getLogger().isLogLevel(LogLevel.DEBUG)) {
                LogManager.getLogger().debug(MODULE, "No locationInformationConfiguration found for MCC: " + mcc + " MNC: " + mnc + " LAC: " + lac + " SAC: " + sac);
            }
            return null;
        }
        if (locationInformationConfiguration.getLocationId() == null) {
            LacConfiguration lacConfiguration = getLacConfiguration(mcc, mnc, lac);
            if (lacConfiguration == null) {
                if (LogManager.getLogger().isLogLevel(LogLevel.DEBUG)) {
                    LogManager.getLogger().debug(MODULE, "No location can be found for LAC: " + lac + " Reason: LAC DATA not found. Returning location information: " + locationInformationConfiguration.toString());
                }
                return locationInformationConfiguration;
            }
            if (sac == null) {
                if (LogManager.getLogger().isLogLevel(LogLevel.DEBUG)) {
                    LogManager.getLogger().debug(MODULE, "No location can be found for  Reason: sac is null. Returning Location information: " + locationInformationConfiguration.toString());
                }
                return locationInformationConfiguration;
            }
            try {
                SAIConfiguration saiConfiguration = lacConfiguration.getSai(Long.parseLong(sac));
                if (saiConfiguration != null) {
                    if (LogManager.getLogger().isLogLevel(LogLevel.DEBUG)) {
                        LogManager.getLogger().debug(MODULE, "Location Information successfully fetched for SAC: " + sac + "Returning Location Information :" + locationInformationConfiguration.toString());
                    }
                    locationInformationConfiguration = saiConfiguration.getLocationInformationConfiguration();
                } else {
                    if (LogManager.getLogger().isLogLevel(LogLevel.DEBUG)) {
                        LogManager.getLogger().debug(MODULE, "No location information is fetched for SAC. Reason. SAIConfiguration data not found. Returning location information: " + locationInformationConfiguration.toString());
                    }
                }
            } catch (NumberFormatException e) {
                if (LogManager.getLogger().isLogLevel(LogLevel.ERROR))
                    LogManager.getLogger().error(MODULE, "SAIConfiguration data not found. Reason: " + e.getMessage());
                LogManager.getLogger().trace(e);
            }
        }
        if (LogManager.getLogger().isLogLevel(LogLevel.DEBUG)) {
            LogManager.getLogger().debug(MODULE, "Returning location information: " + locationInformationConfiguration.toString());
        }
        return locationInformationConfiguration;
    }

    private LocationInformationConfiguration getLocationInformationByLai(String mcc, String mnc, String lac) {
        MccConfiguration mccConfigurationData = getMCCConfigurationByMCCCode(mcc);
        if (mccConfigurationData == null) {
            return null;
        }
        LocationInformationConfiguration locationInformationConfiguration = new LocationInformationConfiguration(mccConfigurationData.getCountry());
        NetworkConfiguration networkConfiguration = getNetworkConfiguration(mnc, mccConfigurationData);
        if (networkConfiguration == null) {
            if (LogManager.getLogger().isLogLevel(LogLevel.DEBUG)) {
                LogManager.getLogger().debug(MODULE, "Only country information can be fetched. Reason: Network data not found");
            }
            return locationInformationConfiguration;
        }
        //to check for location information set in network data is it is one return that value else iterate further
        if (networkConfiguration.getLocationInformationConfigurationSet().size() == 1) {
            for (LocationInformationConfiguration location : networkConfiguration.getLocationInformationConfigurationSet()) {
                if (LogManager.getLogger().isLogLevel(LogLevel.DEBUG)) {
                    LogManager.getLogger().debug(MODULE, "Location Information successfully fetched for MNC: " + mnc);
                }
                return location;
            }
        }
        LacConfiguration lacConfiguration = getLacByNetworkConfiguration(lac, networkConfiguration);
        if (lacConfiguration != null && lacConfiguration.getLocationInformationConfigurationSet().size() == 1) {
            //to check for locationInformationConfiguration set in lacdata if it is one return that value else iterate further
            for (LocationInformationConfiguration location : lacConfiguration.getLocationInformationConfigurationSet()) {
                if (LogManager.getLogger().isLogLevel(LogLevel.DEBUG)) {
                    LogManager.getLogger().debug(MODULE, "Location Information successfully fetched for Lac: " + lac);
                }
                return location;
            }
        }
        return locationInformationConfiguration;
    }

    public MccConfiguration getMCCConfigurationByMCCCode(String mcc) {
        try {
            return this.locationConfigurable.getCountryInformationByMCCCode(Integer.parseInt(mcc));
        } catch (Exception e) {
            if (LogManager.getLogger().isLogLevel(LogLevel.ERROR))
                LogManager.getLogger().error(MODULE, "MCC Data not found for MCC : " + mcc + "Reason: " + e.getMessage());
            LogManager.getLogger().trace(e);
            return null;
        }
    }


    public @Nullable NetworkConfiguration getNetworkInformationByMCCMNC(String mccMnc) {
        NetworkConfiguration networkConfiguration = this.locationConfigurable.getNetworkByMccMnc(mccMnc);
        if (networkConfiguration == null) {
            if (getLogger().isDebugLogLevel()) {
                getLogger().debug(MODULE, "Network Information can't be found.Reason no mcc mcc configuration found with MCC-MNC" + mccMnc);
            }
        }
        return networkConfiguration;
    }

    @Override
    public NetworkConfiguration getNetworkInformationById(String id) {
        return this.locationConfigurable.getNetworkConfigurationById(id);
    }

    private NetworkConfiguration getNetworkConfiguration(String mnc, MccConfiguration mccConfigurationData) {
        if (mnc == null) {
            return null;
        }
        try {
            return mccConfigurationData.getNetworkData(Integer.parseInt(mnc));
        } catch (Exception e) {
            if (LogManager.getLogger().isLogLevel(LogLevel.ERROR))
                LogManager.getLogger().error(MODULE, "Network Data not found for MNC : " + mnc + " Reason: " + e.getMessage());
            LogManager.getLogger().trace(e);
            return null;
        }
    }

    private LacConfiguration getLacConfiguration(String mcc, String mnc, String lac) {
        LacConfiguration lacConfiguration = null;
        MccConfiguration mccConfigurationData = getMCCConfigurationByMCCCode(mcc);
        if (mccConfigurationData != null) {
            NetworkConfiguration networkConfiguration = getNetworkConfiguration(mnc, mccConfigurationData);
            if (networkConfiguration != null) {
                return getLacByNetworkConfiguration(lac, networkConfiguration);
            }
        }
        return lacConfiguration;
    }

    private LacConfiguration getLacByNetworkConfiguration(String lac, NetworkConfiguration networkConfiguration) {
        if (lac == null) {
            return null;
        }
        try {
            return networkConfiguration.getLacData(Long.parseLong(lac));
        } catch (Exception e) {
            if (LogManager.getLogger().isLogLevel(LogLevel.DEBUG))
                LogManager.getLogger().debug(MODULE, "LAC data not found for LAC:" + lac + " Reason. " + e.getMessage());
            LogManager.getLogger().trace(e);
            return null;
        }
    }
}
