package com.elitecore.corenetvertex.sm.device;

import com.elitecore.commons.base.Strings;

import java.util.List;
import java.util.regex.Pattern;

/**
 * @author jaidiptrivedi
 */
public enum DeviceDataField {

    TAC(0) {
        @Override
        public boolean validate(String value, List<String> failReasons) {
            if (Strings.isNullOrBlank(value)) {
                failReasons.add("TAC value can't be Empty");
                return false;
            }
            if (value.length() != 8) {
                failReasons.add("Invalid TAC value");
                return false;
            }
            if (NUMERIC_REGEX.matcher(value).matches() == false) {
                failReasons.add("Invalid TAC value");
                return false;
            }
            return true;
        }

        @Override
        public void setValue(DeviceData deviceData, String value) {
            deviceData.setTac(value);
        }

    }, BRAND(1) {
        @Override
        public boolean validate(String value, List<String> failReasons) {
            if (Strings.isNullOrBlank(value) == false && value.length() > 512) {
                failReasons.add("Invalid brand value");
                return false;
            }
            return true;
        }

        @Override
        public void setValue(DeviceData deviceData, String value) {
            deviceData.setBrand(value);
        }
    }, MODEL(2) {
        @Override
        public boolean validate(String value, List<String> failReasons) {
            if (Strings.isNullOrBlank(value) == false && value.length() > 512) {
                failReasons.add("Invalid model value");
                return false;
            }
            return true;
        }

        @Override
        public void setValue(DeviceData deviceData, String value) {
            deviceData.setDeviceModel(value);
        }
    }, HW_TYPE(3) {
        @Override
        public boolean validate(String value, List<String> failReasons) {
            if (Strings.isNullOrBlank(value) == false && value.length() > 512) {
                failReasons.add("Invalid hardware type value");
                return false;
            }
            return true;
        }

        @Override
        public void setValue(DeviceData deviceData, String value) {
            deviceData.setHardwareType(value);
        }
    }, OS(4) {
        @Override
        public boolean validate(String value, List<String> failReasons) {
            if (Strings.isNullOrBlank(value) == false && value.length() > 512) {
                failReasons.add("Invalid os value");
                return false;
            }
            return true;
        }

        @Override
        public void setValue(DeviceData deviceData, String value) {
            deviceData.setOs(value);
        }
    }, YEAR(5) {
        @Override
        public boolean validate(String value, List<String> failReasons) {
            if (Strings.isNullOrBlank(value) == false && value.length() > 40) {
                failReasons.add("Invalid year value");
                return false;
            }
            return true;
        }

        @Override
        public void setValue(DeviceData deviceData, String value) {
            deviceData.setYear(value);
        }
    }, ADDITIONAL_INFO(6) {
        @Override
        public boolean validate(String value, List<String> failReasons) {
            if (Strings.isNullOrBlank(value) == false && value.length() > 200) {
                failReasons.add("Invalid additional information value");
                return false;
            }
            return true;
        }

        @Override
        public void setValue(DeviceData deviceData, String value) {
            deviceData.setAdditionalInformation(value);
        }
    };

    int index;
    private static final Pattern NUMERIC_REGEX = Pattern.compile("^[0-9]*$");

    DeviceDataField(int index) {
        this.index = index;
    }

    public abstract boolean validate(String value, List<String> failReasons);

    public abstract void setValue(DeviceData deviceData, String value);

    public int getIndex() {
        return index;
    }

}
