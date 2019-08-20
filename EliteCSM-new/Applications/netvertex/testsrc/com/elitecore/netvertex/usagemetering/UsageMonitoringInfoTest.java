package com.elitecore.netvertex.usagemetering;

import de.bechte.junit.runners.context.HierarchicalContextRunner;
import org.junit.Assume;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.Random;

import static org.junit.Assert.*;

@RunWith(HierarchicalContextRunner.class)
public class UsageMonitoringInfoTest {


    public class HashCode_should_be {

        private UsageMonitoringInfo usageMonitoringInfo;
        private UsageMonitoringInfo equalUMInfo;

        @Before
        public void setUp() {
            usageMonitoringInfo = new UsageMonitoringInfo();
            usageMonitoringInfo.setMonitoringKey("1");

            equalUMInfo = new UsageMonitoringInfo();
            equalUMInfo.setMonitoringKey("1");

        }

        @Test
        public void consistence() {

            int hashCode = usageMonitoringInfo.hashCode();
            for(int index = 0; index < new Random().nextInt(20); index++) {
                assertEquals("It is consistent", hashCode, usageMonitoringInfo.hashCode());
            }
        }

        @Test
        public void equalsThenHashCodeMushMatch() {
            Assume.assumeTrue(usageMonitoringInfo.equals(equalUMInfo));

            assertEquals(usageMonitoringInfo.hashCode(), equalUMInfo.hashCode());
        }
    }

    public class Equal_ShouldBe {
        private UsageMonitoringInfo usageMonitoringInfo;
        private UsageMonitoringInfo equalUMInfo;
        private UsageMonitoringInfo equalUMInfo2;
        private UsageMonitoringInfo notEqualUMInfo;

        @Before
        public void setUp() {
            usageMonitoringInfo = new UsageMonitoringInfo();
            usageMonitoringInfo.setMonitoringKey("1");

            equalUMInfo = new UsageMonitoringInfo();
            equalUMInfo.setMonitoringKey("1");

            equalUMInfo2 = new UsageMonitoringInfo();
            equalUMInfo2.setMonitoringKey("1");

            notEqualUMInfo = new UsageMonitoringInfo();
            notEqualUMInfo.setMonitoringKey("2");

        }


        @Test
        public void reflexive() {
            UsageMonitoringInfo usageMonitoringInfo = new UsageMonitoringInfo();

            assertTrue("It is reflexive: for any non-null reference value x, x.equals(x) should return true", usageMonitoringInfo.equals(usageMonitoringInfo));
        }

        public class Symmetric {

            @Test
            public void forNotEquals() {
                Assume.assumeFalse("It is symmetric: for any non-null reference values x and y, x.equals(y) should return true if and only if y.equals(x) returns true",
                        usageMonitoringInfo.equals(notEqualUMInfo));

                assertFalse("It is symmetric: for any non-null reference values x and y, x.equals(y) should return true if and only if y.equals(x) returns true",
                        notEqualUMInfo.equals(usageMonitoringInfo));
            }

            @Test
            public void forEquals() {
                    Assume.assumeTrue("It is symmetric: for any non-null reference values x and y, x.equals(y) should return true if and only if y.equals(x) returns true",
                            usageMonitoringInfo.equals(equalUMInfo));
                assertTrue("It is symmetric: for any non-null reference values x and y, x.equals(y) should return true if and only if y.equals(x) returns true",
                        equalUMInfo.equals(usageMonitoringInfo));
            }
        }

        public class Transitive {

            @Test
            public void forEquals() {
                Assume.assumeTrue("It is transitive: for any non-null reference values x, y, and z, if x.equals(y) returns true and y.equals(z) returns true, then x.equals(z) should return true.",
                        usageMonitoringInfo.equals(equalUMInfo));

                Assume.assumeTrue("It is transitive: for any non-null reference values x, y, and z, if x.equals(y) returns true and y.equals(z) returns true, then x.equals(z) should return true.",
                        equalUMInfo.equals(equalUMInfo2));

                assertTrue("It is transitive: for any non-null reference values x, y, and z, if x.equals(y) returns true and y.equals(z) returns true, then x.equals(z) should return true.",
                        usageMonitoringInfo.equals(equalUMInfo2));
            }
        }

        public class consistent {

            @Test
            public void forNotEquals() {

                for(int index = 0; index < new Random().nextInt(20); index++) {
                    assertFalse("It is consistent: for any non-null reference values x and y, multiple invocations of x.equals(y) consistently return true or consistently return false, provided no information used in equals comparisons on the objects is modified.",
                            usageMonitoringInfo.equals(notEqualUMInfo));
                }
            }

            @Test
            public void forEquals() {

                for(int index = 0; index < new Random().nextInt(20); index++) {
                    assertTrue("It is consistent: for any non-null reference values x and y, multiple invocations of x.equals(y) consistently return true or consistently return false, provided no information used in equals comparisons on the objects is modified.",
                            usageMonitoringInfo.equals(equalUMInfo));
                }
            }
        }

        @Test
        public void doesNotEqualsWithNull() {
            UsageMonitoringInfo usageMonitoringInfo = new UsageMonitoringInfo();

            assertFalse("For any non-null reference value x, x.equals(null) should return false.", usageMonitoringInfo.equals(null));
        }



    }



}