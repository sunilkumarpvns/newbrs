package com.elitecore.corenetvertex.util;

import de.bechte.junit.runners.context.HierarchicalContextRunner;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.concurrent.atomic.AtomicInteger;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertSame;

@RunWith(HierarchicalContextRunner.class)
public class ToStringIndentingStyleTest {

    private DummyToStringIndentingStyle style;
    private AtomicInteger level;

    @Before
    public void setUp() {
        this.level = new AtomicInteger(0);
        this.style = new DummyToStringIndentingStyle(level);
    }


    public class DefaultFormateParameters {
        /*
        this.setFieldSeparatorAtStart(true);
        this.setContentEnd(System.lineSeparator());
         */
        @Test
        public void isUseClassNameShouldGiveFalse() throws Exception {
            style.assertUseClassName(false);
        }

        @Test
        public void isUseIdentityHashCodeGiveFalse() throws Exception {
            style.assertUseIdentityHashCode(false);
        }

        @Test
        public void getNullTextShouldBeNA() throws Exception {
            style.assertNullText("N/A");
        }

        @Test
        public void isUseFieldNamesGiveFalse() throws Exception {
            style.assertUseFieldNames(true);
        }

        @Test
        public void getFieldNameValueSeperatorShouldGiveEqualToSign() throws Exception {
            style.assertFieldNameValueSeparator(" = ");
        }

        @Test
        public void getContentStartShouldGiveEmptyString() throws Exception {
            style.assertGetContentStart("");
        }

        @Test
        public void getFieldSeperatorShouldGiveSystemNewLine() throws Exception {
            style.assertGetFieldSeperator(System.lineSeparator());
        }

        @Test
        public void getFieldSeparatorAtStartShouldGiveTrue() throws Exception {
            style.getFieldSeparatorAtStart(true);
        }

        @Test
        public void getContentEndShouldGiveSystemNewLine() throws Exception {
            style.assertGetContentEnd(System.lineSeparator());
        }

    }

    private class DummyToStringIndentingStyle extends ToStringIndentingStyle {

        public DummyToStringIndentingStyle(AtomicInteger level) {
            super(level);
        }

        public void assertUseClassName(boolean expectedFlag) {
            assertSame(expectedFlag, super.isUseClassName());
        }

        public void assertUseIdentityHashCode(boolean expectedFlag) {
            assertSame(expectedFlag, super.isUseIdentityHashCode());
        }

        public void assertNullText(String nullText) {
            assertEquals(nullText, this.getNullText());
        }

        public void assertUseFieldNames(boolean expectedFlag) {
            assertSame(expectedFlag, super.isUseFieldNames());
        }

        public void assertFieldNameValueSeparator(String expectedText) {
            assertEquals(expectedText, super.getFieldNameValueSeparator());
        }

        public void assertGetContentStart(String expectedText) {
            assertEquals(expectedText, super.getContentStart());
        }

        public void assertGetFieldSeperator(String expectedText) {
            assertEquals(expectedText, super.getFieldSeparator());
        }

        public void getFieldSeparatorAtStart(boolean expectedFlag) {
            assertSame(expectedFlag, super.isFieldSeparatorAtStart());
        }

        public void assertGetContentEnd(String expectedText) {
            assertEquals(expectedText, super.getContentEnd());
        }
    }

    public class appendShouldAddAsManyTabsAsValueOfLevel{
        private StringBuffer stringBuffer;

        @Before
        public void setUp() {
            this.stringBuffer = new StringBuffer();
        }
        @Test
        public void zeroIndentationLevel() {
            level.set(0);
            style.append(stringBuffer, "ABC", "DEF", true);
            assertEquals("ABC = DEF\n", stringBuffer.toString());
        }

        @Test
        public void oneIndentationLevel() {
            level.set(1);
            style.append(stringBuffer, "ABC", "DEF", true);
            assertEquals("\tABC = DEF\n", stringBuffer.toString());
        }

        @Test
        public void twoIndentationLevel() {
            level.set(2);
            style.append(stringBuffer, "ABC", "DEF", true);
            assertEquals("\t\tABC = DEF\n", stringBuffer.toString());
        }
    }

}