package com.elitecore.nvsmx.sm.model.systemparameter;

import com.elitecore.commons.base.Collectionz;
import com.elitecore.commons.base.Strings;
import com.elitecore.commons.logging.LogManager;
import com.elitecore.core.commons.InitializationFailedException;
import com.elitecore.corenetvertex.constants.CommonConstants;
import com.elitecore.corenetvertex.constants.DeploymentMode;
import com.elitecore.corenetvertex.sm.systemparameter.*;
import com.elitecore.nvsmx.system.ConfigurationProvider;
import com.elitecore.nvsmx.system.exception.HibernateDataException;
import com.elitecore.nvsmx.system.exception.SessionFactoryNotFoundException;
import com.elitecore.nvsmx.system.hibernate.CRUDOperationUtil;
import com.elitecore.nvsmx.system.hibernate.HibernateSessionFactory;
import com.elitecore.nvsmx.system.hibernate.HibernateSessionUtil;
import com.elitecore.nvsmx.ws.util.UpdateActions;
import org.apache.commons.lang.BooleanUtils;
import org.apache.commons.lang.StringUtils;
import com.elitecore.corenetvertex.sm.routing.network.OperatorData;
import com.elitecore.corenetvertex.sm.routing.network.CountryData;
import org.hibernate.Criteria;
import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.Transaction;

import java.util.*;
import java.util.stream.Collectors;

import static com.elitecore.commons.logging.LogManager.getLogger;
import static org.apache.commons.lang.math.NumberUtils.createLong;

/**
 * @author jaidiptrivedi
 */
public class SystemParameterDAO {

    public static final String MODULE = SystemParameterDAO.class.getSimpleName();
    public static final String UPDATE_ACTION_FOR_WS = "UPDATE_ACTION";

    private static Map<String, SystemParameterData> systemParameterMap = new HashMap<String, SystemParameterData>();

    private static Map<String, String> systemParameterToValue = new HashMap<>();

    private static List<SystemParameterData> orderedSystemParameterData = Collectionz.newArrayList();
    private static List<SystemParameterData> orderedPackageParameterData = Collectionz.newArrayList();

    private static List<SystemParameterData> orderedOfflineRnCFileSystemParameterData = Collectionz.newArrayList();
    private static List<SystemParameterData> orderedOfflineRnCRatingSystemParameterData = Collectionz.newArrayList();

    public static void init() throws InitializationFailedException {

        try {

            Map<String, String> systemParameterMapFromDB = readSystemParameters();
            createSystemParameterMap(systemParameterMapFromDB);
            ConfigurationProvider.getInstance().reInit();

        } catch (Exception e) {
            throw new InitializationFailedException(e.getMessage(), e);
        }
    }

    private static void createSystemParameterMap(Map<String, String> systemParameterMapFromDB) {

        for (SystemParameter systemParameter : SystemParameter.values()) {
            String key = systemParameter.name();
            String value = systemParameter.getValue();

            if (systemParameterMapFromDB.containsKey(key)) {
                value = systemParameterMapFromDB.get(key);
            }

            //When system start this map will be null so no need to skip this part
            if(systemParameterToValue.containsKey(key)) {
                String val = systemParameterToValue.get(key);
                if (systemParameter.isRefreshAllowed() == false && StringUtils.isNotEmpty(val)) {
                    value = val;
                }
            }
            if (getLogger().isDebugLogLevel()) {
                getLogger().debug(MODULE, "Initializing system parameter: " + systemParameter.getName() + " with value: " + value);
            }
            systemParameterToValue.put(key, value);
        }

        for (PackageParameter packageParameter : PackageParameter.values()) {
            String key = packageParameter.name();
            String value = packageParameter.getValue();

            if (systemParameterMapFromDB.containsKey(key)) {
                value = systemParameterMapFromDB.get(key);
            }

            systemParameterToValue.put(key, value);
        }

        for (OfflineRnCFileSystemParameter fileParameter : OfflineRnCFileSystemParameter.values()) {
            String key = fileParameter.name();
            String value = fileParameter.getValue();

            if (systemParameterMapFromDB.containsKey(key)) {
                value = systemParameterMapFromDB.get(key);
            }

            systemParameterToValue.put(key, value);
        }

        for (OfflineRnCRatingSystemParameter ratingParameter : OfflineRnCRatingSystemParameter.values()) {
            String key = ratingParameter.name();
            String value = ratingParameter.getValue();

            if (systemParameterMapFromDB.containsKey(key)) {
                value = systemParameterMapFromDB.get(key);
            }

            systemParameterToValue.put(key, value);
        }

    }

    private static void createSystemParameterList(Map<String, String> systemParameterMapFromDB) {

        for (SystemParameter systemParameter : SystemParameter.values()) {

            String key = systemParameter.name();
            String value = systemParameter.getValue();

            if (systemParameterMapFromDB.containsKey(key)) {
                value = systemParameterMapFromDB.get(key);
            }

            orderedSystemParameterData.add(new SystemParameterData(systemParameter.getName(), systemParameter.getDescription(), key, value));
        }

        for (PackageParameter packageParameter : PackageParameter.values()) {

            String key = packageParameter.name();
            String value = packageParameter.getValue();
            if (systemParameterMapFromDB.containsKey(key)) {
                value = systemParameterMapFromDB.get(key);
            }

            orderedPackageParameterData.add(new SystemParameterData(packageParameter.getName(), packageParameter.getDescription(), key, value));
        }

        for (OfflineRnCFileSystemParameter fileParameter : OfflineRnCFileSystemParameter.values()) {
            String key = fileParameter.name();
            String value = fileParameter.getValue();

            if (systemParameterMapFromDB.containsKey(key)) {
                value = systemParameterMapFromDB.get(key);
            }

            orderedOfflineRnCFileSystemParameterData.add(new SystemParameterData(fileParameter.getName(), fileParameter.getDescription(), key, value));
        }

        for (OfflineRnCRatingSystemParameter ratingParameter : OfflineRnCRatingSystemParameter.values()) {
            String key = ratingParameter.name();
            String value = ratingParameter.getValue();

            if (systemParameterMapFromDB.containsKey(key)) {
                value = systemParameterMapFromDB.get(key);
            }

            orderedOfflineRnCRatingSystemParameterData.add(new SystemParameterData(ratingParameter.getName(), ratingParameter.getDescription(), key, value));
        }

    }

    public static void initializeSystemParameterList() throws InitializationFailedException {
        try {
            Map<String, String> systemParameterMapFromDB = readSystemParameters(HibernateSessionFactory.getSession());
            createSystemParameterList(systemParameterMapFromDB);
        } catch (Exception e) {
            throw new InitializationFailedException(e.getMessage(), e);
        }

    }


    /**
     * get all the system parameters from the database
     *
     * @return List<SystemParameterData>
     * @throws HibernateDataException
     */
    @SuppressWarnings("unchecked")
    public static Map<String, String> readSystemParameters() throws HibernateException, SessionFactoryNotFoundException {

        Session session = null;
        try {
            session = HibernateSessionFactory.getNewSession();
            session.beginTransaction();
            Criteria criteria = session.createCriteria(SystemParameterData.class);
            List<SystemParameterData> systemParameterDatas = criteria.list();

            return systemParameterDatas.stream().collect(Collectors.toMap(SystemParameterData::getAlias, SystemParameterData::getValue));

        } finally {
            HibernateSessionUtil.closeSession(session);
        }

    }

    private static Map<String, String> readSystemParameters(Session session) {

        Criteria criteria = session.createCriteria(SystemParameterData.class);
        List<SystemParameterData> systemParameterDatas = criteria.list();

        return systemParameterDatas.stream().collect(Collectors.toMap(SystemParameterData::getAlias, SystemParameterData::getValue));
    }

    /**
     * Used to fetch the value of system parameter
     *
     * @param alias
     * @return String
     */
    public static String get(String alias) {
        if (systemParameterToValue.containsKey(alias)) {
            return systemParameterToValue.get(alias);
        }
        return CommonConstants.DOUBLE_QUOTES;
    }

    public static String getCurrency() {
        return systemParameterToValue.get(SystemParameter.CURRENCY.name());
    }

    public static String getCountry() {
        return systemParameterToValue.get(SystemParameter.COUNTRY.name());
    }

    public static String getOperator() {
        return systemParameterToValue.get(SystemParameter.OPERATOR.name());
    }

    public static String getTax() {
        return systemParameterToValue.get(SystemParameter.TAX.name());
    }

    public static boolean  isSSOEnable() {
        return BooleanUtils.toBoolean(systemParameterToValue.get(SystemParameter.SSO_AUTHENTICATION.name()));
    }

    public static boolean  isMultiCurrencyEnable() {
        return BooleanUtils.toBoolean(systemParameterToValue.get(SystemParameter.MULTI_CURRENCY_SUPPORT.name()));
    }

    public static void refresh() throws InitializationFailedException {

        try {

            Map<String, String> systemParameterMapFromDB = readSystemParameters(HibernateSessionFactory.getSession());
            orderedSystemParameterData.clear();
            orderedPackageParameterData.clear();
            orderedOfflineRnCFileSystemParameterData.clear();
            orderedOfflineRnCRatingSystemParameterData.clear();
            createSystemParameterList(systemParameterMapFromDB);
            createSystemParameterMap(systemParameterMapFromDB);
            ConfigurationProvider.getInstance().reInit();
        } catch (Exception e) {
            throw new InitializationFailedException(e.getMessage(), e);
        }
    }

    public static DeploymentMode getDeploymentMode(){
        return DeploymentMode.fromName(systemParameterToValue.get(SystemParameter.DEPLOYMENT_MODE.name()));
    }

    public static void reloadMap() throws InitializationFailedException {
        init();
    }

    public static void reloadList() throws InitializationFailedException {
        orderedSystemParameterData.clear();
        orderedPackageParameterData.clear();
        orderedOfflineRnCFileSystemParameterData.clear();
        orderedOfflineRnCRatingSystemParameterData.clear();
        initializeSystemParameterList();
    }

    public static UpdateActions getAutoReauthEnable() {
        try {
            String value = systemParameterToValue.get(UPDATE_ACTION_FOR_WS);
            if (Strings.isNullOrBlank(value)) {
                return UpdateActions.NO_ACTION;
            }

            UpdateActions updateAction = UpdateActions.fromValue(Integer.parseInt(value));
            if (updateAction == null) {
                LogManager.getLogger().warn(MODULE, "Invalid value Configured in System Parameter for 'Update Action for WS' parameter. Taking 0(NO_ACTION) value for 'Update Action for WS'");
                return UpdateActions.NO_ACTION;
            } else {
                return updateAction;
            }
        } catch (Exception ex) {
            LogManager.getLogger().error(MODULE, "Error while taking system parameter 'Update Action for WS' value. Reason:  " + ex.getMessage()
                    + ". Taking 0(NO_ACTION) value for 'Update Action for WS' system Parameter. ");
            LogManager.getLogger().trace(MODULE, ex);
            return UpdateActions.NO_ACTION;
        }
    }

    public static Collection<SystemParameterData> getSystemParameters() {
        return systemParameterMap.values();
    }

    public static List<SystemParameterData> getOrderedPackageParameterData() {
        return orderedPackageParameterData;
    }

    public static void setOrderedPackageParameterData(List<SystemParameterData> orderedPackageParameterData) {
        SystemParameterDAO.orderedPackageParameterData = orderedPackageParameterData;
    }

    public static List<SystemParameterData> getOrderedSystemParameterData() {
        return orderedSystemParameterData;
    }

    public static Map<String, String> getSystemParameterToValue() {
        return systemParameterToValue;
    }

    public static void setSystemParameterToValue(Map<String, String> systemParameterMapNew) {
        SystemParameterDAO.systemParameterToValue = systemParameterMapNew;
    }

    public static Map<String, SystemParameterData> getSystemParameterMap() {
        return systemParameterMap;
    }

	public static List<SystemParameterData> getOrderedOfflineRnCFileSystemParameterData() {
		return orderedOfflineRnCFileSystemParameterData;
	}

	public static void setOrderedOfflineRnCFileSystemParameterData(
			List<SystemParameterData> orderedOfflineRnCFileSystemParameterData) {
		SystemParameterDAO.orderedOfflineRnCFileSystemParameterData = orderedOfflineRnCFileSystemParameterData;
	}

	public static List<SystemParameterData> getOrderedOfflineRnCRatingSystemParameterData() {
		return orderedOfflineRnCRatingSystemParameterData;
	}

	public static void setOrderedOfflineRnCRatingSystemParameterData(
			List<SystemParameterData> orderedOfflineRnCRatingSystemParameterData) {
		SystemParameterDAO.orderedOfflineRnCRatingSystemParameterData = orderedOfflineRnCRatingSystemParameterData;
	}

    public static void syncCurrency() {
        SystemParameterData currencyParameterData = new SystemParameterData(SystemParameter.CURRENCY.getName(),
                SystemParameter.CURRENCY.getDescription(), SystemParameter.CURRENCY.name(), getCurrency());
        Session session = HibernateSessionFactory.getNewSession();
        Transaction transaction = session.beginTransaction();
        session.merge(currencyParameterData);
        HibernateSessionUtil.commitTransaction(transaction);
        HibernateSessionUtil.closeSession(session);
    }

    public static boolean isMandatorySystemParamaterConfigured() {
        return (Strings.isNullOrBlank(getCountry()) == false && Strings.isNullOrBlank(getOperator()) == false && Strings.isNullOrBlank(getCurrency()) == false);
    }

    public static String getCountryName(String id) {
       /* Long longId = null;
        try {
            longId = createLong(id);
        } catch (Exception e) {
            LogManager.getLogger().error(MODULE, "Unable to parse id:" + id);
        }
        if (Objects.isNull(longId)) {
            return null;
        }*/
        CountryData countryData = CRUDOperationUtil.get(CountryData.class, id);
        if (Objects.nonNull(countryData)) {
            return countryData.getName();
        }
        return null;
    }

    public static String getOperatorName(String id){
        /*Long longId = null;
        try {
            longId = createLong(id);
        } catch (Exception e) {
            LogManager.getLogger().error(MODULE, "Unable to parse id:" + id);
        }
        if (Objects.isNull(longId)) {
            return null;
        }*/
        OperatorData operatorData = CRUDOperationUtil.get(OperatorData.class, id);
        if(Objects.nonNull(operatorData)){
            return operatorData.getName();
        }
        return null;
    }
}
