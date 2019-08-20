package com.elitecore.corenetvertex.sm.device;

import de.bechte.junit.runners.context.HierarchicalContextRunner;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.*;

/**
 * @author jaidiptrivedi
 */
@RunWith(HierarchicalContextRunner.class)
public class DeviceDataFieldTest { //NOSONAR

    DeviceData validDeviceData = new DeviceData();

    @Before
    public void setUp() throws Exception {
        validDeviceData.setTac("11194804");
        validDeviceData.setBrand("Samsung");
        validDeviceData.setAdditionalInformation("Additional Info");
        validDeviceData.setYear("2015");
        validDeviceData.setOs("Android");
        validDeviceData.setHardwareType("Hardware Type");
        validDeviceData.setDeviceModel("J7");
    }

    public class TACTest {
        List<String> failReasons = new ArrayList<>();

        @Test
        public void validate_success_when_currect_value_is_provided() {
            assertTrue(DeviceDataField.TAC.validate(validDeviceData.getTac().toString(), failReasons));
            assertTrue("failReasons should be empty", failReasons.isEmpty());
        }

        @Test
        public void validate_fail_when_invalid_length_of_tac_is_provided() {
            assertFalse(DeviceDataField.TAC.validate("123", failReasons));
            assertFalse("failReasons should have value", failReasons.isEmpty());
        }

        @Test
        public void validate_fail_when_empty_tac_is_provided() {
            assertFalse(DeviceDataField.TAC.validate("", failReasons));
            assertFalse("failReasons should have value for empty tac", failReasons.isEmpty());
        }

        @Test
        public void validate_fail_when_string_value_is_provided() {
            assertFalse(DeviceDataField.TAC.validate("A2345678", failReasons));
            assertFalse("failReasons should have value for non numeric tac", failReasons.isEmpty());
        }

    }

    public class BrandTest {
        List<String> failReasons = new ArrayList<>();

        @Test
        public void validate_success_when_currect_value_is_provided() {
            assertTrue(DeviceDataField.BRAND.validate(validDeviceData.getBrand(), failReasons));
            assertTrue("failReasons should be empty", failReasons.isEmpty());
        }
    }

    public class ModelTest {
        List<String> failReasons = new ArrayList<>();

        @Test
        public void validate_success_when_currect_value_is_provided() {
            assertTrue(DeviceDataField.MODEL.validate(validDeviceData.getDeviceModel(), failReasons));
            assertTrue("failReasons should be empty", failReasons.isEmpty());
        }
    }

    public class AdditionalInfoTest {
        List<String> failReasons = new ArrayList<>();

        @Test
        public void validate_success_when_currect_value_is_provided() {
            assertTrue(DeviceDataField.ADDITIONAL_INFO.validate(validDeviceData.getAdditionalInformation(), failReasons));
            assertTrue("failReasons should be empty", failReasons.isEmpty());
        }
    }

    public class OsTest {
        List<String> failReasons = new ArrayList<>();

        @Test
        public void validate_success_when_currect_value_is_provided() {
            assertTrue(DeviceDataField.OS.validate(validDeviceData.getOs(), failReasons));
            assertTrue("failReasons should be empty", failReasons.isEmpty());
        }
    }

    public class HWTypeTest {
        List<String> failReasons = new ArrayList<>();

        @Test
        public void validate_success_when_currect_value_is_provided() {
            assertTrue(DeviceDataField.HW_TYPE.validate(validDeviceData.getHardwareType(), failReasons));
            assertTrue("failReasons should be empty", failReasons.isEmpty());
        }
    }

    public class YearTest {
        List<String> failReasons = new ArrayList<>();

        @Test
        public void validate_success_when_currect_value_is_provided() {
            assertTrue(DeviceDataField.YEAR.validate(validDeviceData.getYear().toString(), failReasons));
            assertTrue("failReasons should be empty", failReasons.isEmpty());
        }

    }

}
