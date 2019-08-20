package com.elitecore.corenetvertex.sm.systemparameter;

import de.bechte.junit.runners.context.HierarchicalContextRunner;
import org.junit.Test;
import org.junit.runner.RunWith;

import static junit.framework.TestCase.assertFalse;
import static org.junit.Assert.assertTrue;

/**
 * @author jaidiptrivedi
 */
@RunWith(HierarchicalContextRunner.class)
public class SystemParameterTest {

    public class TotalRowTest {

        @Test
        public void validate_success_when_currect_value_is_provided() {
            assertTrue(SystemParameter.TOTAL_ROW.validate("10"));
        }

        @Test
        public void validate_fail_when_wrong_value_is_provided() {
            assertFalse(SystemParameter.TOTAL_ROW.validate("10000"));
        }

        @Test
        public void validate_fail_when_string_is_provided() {
            assertFalse(SystemParameter.TOTAL_ROW.validate("test"));
        }
    }

    public class UpdateActionTest {

        @Test
        public void validate_success_when_currect_value_is_provided() {
            assertTrue(SystemParameter.UPDATE_ACTION.validate("0"));
            assertTrue(SystemParameter.UPDATE_ACTION.validate("1"));
            assertTrue(SystemParameter.UPDATE_ACTION.validate("2"));
        }

        @Test
        public void validate_fail_when_wrong_value_is_provided() {
            assertFalse(SystemParameter.UPDATE_ACTION.validate("3"));
            assertFalse(SystemParameter.UPDATE_ACTION.validate("-1"));
        }

        @Test
        public void validate_fail_when_string_is_provided() {
            assertFalse(SystemParameter.UPDATE_ACTION.validate("test"));
        }
    }

    public class ShortDateFormatTest {
        @Test
        public void validate_success_when_currect_value_is_provided() {
            assertTrue(SystemParameter.SHORT_DATE_FORMAT.validate("MM/dd/yyyy"));
        }
    }

    public class DateFormatTest {
        @Test
        public void validate_success_when_currect_value_is_provided() {
            assertTrue(SystemParameter.DATE_FORMAT.validate("MM/dd/yyyy HH:mm:ss"));
        }
    }

    public class TaxTest {
        @Test
        public void fail_when_value_is_out_of_range() {
            assertFalse(SystemParameter.TAX.validate("100.01"));
        }

        @Test
        public void success_when_value_is_between_0_to_100() {
            assertTrue(SystemParameter.TAX.validate("99.99"));
            assertTrue(SystemParameter.TAX.validate("0.01"));
        }

        @Test
        public void fail_when_value_is_not_decimal_number() {
            assertFalse(SystemParameter.TAX.validate("100.45#1"));
        }

        @Test
        public void success_when_value_is_blank() {
            assertTrue(SystemParameter.TAX.validate(""));
        }

        @Test
        public void success_when_value_is_NULL() {
            assertTrue(SystemParameter.TAX.validate(null));
        }
    }
}
