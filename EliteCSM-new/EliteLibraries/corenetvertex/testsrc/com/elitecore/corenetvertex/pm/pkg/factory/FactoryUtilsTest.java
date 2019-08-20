package com.elitecore.corenetvertex.pm.pkg.factory;

import de.bechte.junit.runners.context.HierarchicalContextRunner;
import org.apache.commons.lang3.RandomUtils;
import org.hamcrest.CoreMatchers;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.Random;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.*;

@RunWith(HierarchicalContextRunner.class)
public class FactoryUtilsTest {

    public class isValidPort{

        public class InvalidPortWhen {
            @Test
            public void portIsLessThan1025() {
                assertFalse(FactoryUtils.isValidPort(new Random().nextInt(1024)));
                assertFalse(FactoryUtils.isValidPort(-1));
                assertFalse(FactoryUtils.isValidPort(1024));
            }

            @Test
            public void portIsGreaterThan65535() {
                assertFalse(FactoryUtils.isValidPort(Integer.MAX_VALUE));
                assertFalse(FactoryUtils.isValidPort(65536));
                assertFalse(FactoryUtils.isValidPort(665033));
            }
        }

        public class ValidPortWhen {
            @Test
            public void portIsBetween1025To65535() {
                assertTrue(FactoryUtils.isValidPort(1025));
                assertTrue(FactoryUtils.isValidPort(65535));
                assertTrue(FactoryUtils.isValidPort(10000));
            }
        }


    }

    public class validateRange{

        private int defaultValue = 10;
        private int minValue = 2;
        private int maxValue = 100;

        public class selectDefaultValue {


            @Test
            public void originalValueIsNull() {
                int result = getRange(null);
                checkDefaultValue(result);
            }

            @Test
            public void originalValueIsLessThanMinValue() {
                int result = getRange(minValue - 1);
                checkDefaultValue(result);
            }

            @Test
            public void originalValueIsGreaterThanMaxValue() {
                int result = getRange(maxValue + 1);
                checkDefaultValue(result);
            }
            private void checkDefaultValue(int result) {
                assertThat(result, is(equalTo(defaultValue)));
            }

        }



        public class selectOriginalValue {
            @Test
            public void originalValueBetweenRange() {

                int originalValue = RandomUtils.nextInt(minValue, maxValue + 1);
                assertThat(getRange(originalValue), is(equalTo(originalValue)));

            }

            @Test
            public void originalValueEqualToMinValue() {

                assertThat(getRange(minValue), is(equalTo(minValue)));

            }


            @Test
            public void originalValueEqualToMaxValue() {

                assertThat(getRange(maxValue), is(equalTo(maxValue)));

            }
        }

        private int getRange(Integer originalValue) {
            return FactoryUtils.validateRange("Test", originalValue, defaultValue, minValue, maxValue);
        }




    }

}