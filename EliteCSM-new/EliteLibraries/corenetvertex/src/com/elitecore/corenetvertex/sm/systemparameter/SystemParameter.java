package com.elitecore.corenetvertex.sm.systemparameter;

import com.elitecore.commons.base.Strings;
import com.elitecore.corenetvertex.constants.DeploymentMode;

import java.math.BigDecimal;
import java.util.Currency;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.regex.Pattern;

/**
 * This enum contains default value for system parameters. If value doesn't exists in db, then this value will be used.
 *
 * @author jaidiptrivedi
 */
public enum SystemParameter {

    DATE_FORMAT("Date Format", "MM/dd/yyyy HH:mm:ss", "dd : Day, MM : Month, yy : Year, hh : Hour, mm : Minutes, ss : Seconds", true) {
        @Override
        public boolean validate(String value) {
            return value.length() < 255;
        }
    },

    SHORT_DATE_FORMAT("Short Date Format", "MM/dd/yyyy", "dd : Day, MM : Month, yy : Year", true) {
        @Override
        public boolean validate(String value) {
            return value.length() < 255;
        }
    },

    TOTAL_ROW("Datasource Max Row", "50", "Total Number of Row Display in Search", true) {
        @Override
        public boolean validate(String value) {
            return NUMERIC_REGEX.matcher(value).matches() && Integer.parseInt(value) < 1000;
        }
    },

    UPDATE_ACTION("Update Action For WS", "0", "Update Action on Subscription or Subscriber policy change. Possible options: 0(No_Action), 1(ReAuth_Session), 2(Disconnect Session)", true) {
        @Override
        public boolean validate(String value) {
            if (NUMERIC_REGEX.matcher(value).matches() == false) {
                return false;
            }
            int updateAction = Integer.parseInt(value);
            return updateAction >= 0 && updateAction <= 2;
        }
    },
    COUNTRY("Country", "", "This parameter defines the name of the country where the operator operates. " +
            "Based on the country, Operator name and Currency can be specified in the system. Country helps to identify roaming and international calls.\n", true) {
        @Override
        public boolean validate(String value) {
            return value.length() < 255;
        }
    },
    CURRENCY("Currency", "", "This parameter defines the currency of the country where the operator operates. Value configured in this parameter will be used  " +
            "for rating and charging purpose.", true) {
        @Override
        public boolean validate(String value) {
            for (Currency currency : Currency.getAvailableCurrencies()) {
                if (currency.getCurrencyCode().equalsIgnoreCase(value.trim())) {
                    return true;
                }
            }
            return false;
        }
    },
    MULTI_CURRENCY_SUPPORT("Multi Currency Support", "false", "This parameter defines whether to enable/disable Multi Currency Support for Server Manager.<br>Note: <ul><li>If Multi Currency Support is Enable, it can't be revert back to Disable; but vice versa is possible.</li></ul>", true) {
        @Override
        public boolean validate(String value) {
            return "true".equals(value) || "false".equals(value);
        }
    },
    OPERATOR("Operator", "", "This parameter defines the operator within the selected country. It will help to identify on-net/off-net calls.", true) {
        @Override
        public boolean validate(String value) {
            return value.length() < 255;
        }
    },
    DEPLOYMENT_MODE("Deployment Mode", DeploymentMode.PCC.name(), "Deployment mode is used to manage policy where PCC is used for Quota metering and QoS metering, OCS is used for Quota metering and PCRF is used for QoS metering. Note: Server Manager restart in required to apply this configuration", false){
        @Override
        public boolean validate(String value) { return Objects.isNull(DeploymentMode.fromName(value)) == false; }
    },
    TAX("Tax(%)", "", "This parameter defines the value of the tax that should be applied on monetary recharge price. ", true) {
        @Override
        public boolean validate(String value) {

            if (Strings.isNullOrBlank(value)) {
                return true;
            }

            if(TAX_REGEX.matcher(value).matches() == false) {
                return false;
            }

            BigDecimal tax = new BigDecimal(value);

            return tax.compareTo(new BigDecimal(0)) >= 0 && tax.compareTo(new BigDecimal(100)) <= 0;
        }
    },
    SSO_AUTHENTICATION("SSO Authentication", "false", "This parameter defines wheter to enable/disable staff SSO Authentication for Server Manager.<br>Note: <ul><li>Server Manager must be restart to make SSO authentication configuration effective , during restart API call will not work.</li>" +
            "<li>If SSO Authentication is Enable, it can't be revert back to Disable; but vice versa is possible.</li>" +
            "<li>Changing from Disable to Enable requires keyCloak configuration; that is to be provided at WEB-INF/keyCloak.json file.Please refer sample configuration file in installation guide</li></ul>", false) {
        @Override
        public boolean validate(String value) {
            return value.length() < 255;
        }
    };


    private String name;
    private String value;
    private String description;
    private boolean refreshAllowed;
    private static final Pattern NUMERIC_REGEX = Pattern.compile("^[0-9]*$");
    private static final Pattern TAX_REGEX = Pattern.compile("^(([0-9]{0,3}(\\.\\d{0,2})?))$");
    private static Map<String, SystemParameter> nameMap;

    static {
        nameMap = new HashMap<>();
        for (SystemParameter systemParameter : values()) {
            nameMap.put(systemParameter.name(), systemParameter);
        }
    }

    SystemParameter(String name, String value, String description, boolean refreshAllowed) {
        this.name = name;
        this.value = value;
        this.description = description;
        this.refreshAllowed = refreshAllowed;
    }

    public String getName() {
        return name;
    }

    public String getValue() {
        return value;
    }


    public String getDescription() {
        return description;
    }

    public boolean isRefreshAllowed() {
        return refreshAllowed;
    }

    public abstract boolean validate(String value);

    public static SystemParameter fromName(String name) {
        return nameMap.get(name);
    }

}
