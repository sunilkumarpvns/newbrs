package com.elitecore.corenetvertex.util;

import de.bechte.junit.runners.context.HierarchicalContextRunner;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.anyObject;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.*;

@RunWith(HierarchicalContextRunner.class)
public class IndentingToStringBuilderTest {

    private IndentingToStringBuilder builder;
    @Mock
    private ToStringIndentingStyle style;

    @Before
    public void setUp() {
        this.builder = new IndentingToStringBuilder();
        this.style = mock(ToStringIndentingStyle.class);
    }

    @Test
    public void test_IndentionShouldReturnZeroInitially() {
        assertEquals(0, builder.getCurrentIndentation().get());
    }

    @Test
    public void test_IndentationShouldBeIncrementedOnIncrementIndentationCall() {
        builder.incrementIndentation();
        assertEquals(1, builder.getCurrentIndentation().get());
        builder.incrementIndentation();
        assertEquals(2, builder.getCurrentIndentation().get());
    }

    @Test
    public void test_IndentationShouldBeDecrementedOnDecrementIndentationCall() {
        builder.incrementIndentation();
        builder.decrementIndentation();
        assertEquals(0, builder.getCurrentIndentation().get());
    }

    @Test(expected = IllegalStateException.class)
    public void test_decrementIndentationShouldThrowIllegalStateExceptionWhenCallOnZeroIndentation() {
        builder.decrementIndentation();
    }

    @Test
    public void test_newLineShouldAddNewEndLineInToString() {
        builder.newline();
        assertEquals("\n", builder.toString());
    }

    public class appendValue {

        @Before
        public void setUp() {
            builder.pushStyle(style);
        }

        @Test
        public void ShouldCallStyleAppendWithFieldNameNullAndFullDetaulTrue() {
            Object value = (Object) "value";
            builder.appendValue(value);
            verify(style, times(1)).append(anyObject(), eq(null), eq(value), eq(true));
        }
    }

    public class appendHeader {

        @Before
        public void setUp() {
            builder.pushStyle(style);
        }

        @Test
        public void ShouldCallStyleAppend_WithFieldNameNullAndFullDetailTrue() {
            String heading = "heading";
            builder.appendHeading(heading);
            verify(style, times(1)).append(anyObject(), eq(null), eq(heading), eq(true));
        }
    }

    public class append {

        @Before
        public void setUp() {
            builder.pushStyle(style);
        }

        @Test
        public void ShouldCallStyleAppendWithPassedFieldValueAndFullDetailTrue() {
            String fieldName = "field";
            Object value = (Object) "value";
            builder.append(fieldName, value);
            verify(style, times(1)).append(anyObject(), eq(fieldName), eq(value), eq(true));
        }
    }

    public class PushStyle {

        @Test
        public void ShouldAddPassedStyleAsCurrentStypeAndAllAppendOperationShouldCallThat() {
            builder.pushStyle(style);
            String fieldName = "field";
            Object value = (Object) "";
            builder.append(fieldName, value);
            verify(style, times(1)).append(anyObject(), eq(fieldName), eq(value), eq(true));
        }
    }

    public class PopStyle {

        @Test
        public void ShouldRemovePassedStyleAsCurrentStype() {
            builder.pushStyle(style);
            builder.popStyle();

            String fieldName = "field";
            Object value = (Object) "";
            builder.append(fieldName, value);

            verify(style, times(0)).append(anyObject(), eq(fieldName), eq(value), eq(true));
        }
    }


    public class AppendField {

        @Before
        public void setUp() {
            builder.pushStyle(style);
        }

        @Test
        public void ShouldCallStyleAppendWithPassedFieldAndEmptyValueAndFullDetailTrue() {
            String fieldName = "field";
            Object expectedValue = (Object) "";
            builder.appendField(fieldName);
            verify(style, times(1)).append(anyObject(), eq(fieldName), eq(expectedValue), eq(true));
        }
    }

    public class AppendChildList {

        private IndentingToStringBuilder spyBuilder;

        @Before
        public void setUp() {
            spyBuilder = spy(builder);
        }

        @Test
        public void ShouldCallAppendValueWithAllElement() {
            String fieldName = "field";
            String child2 = "child1";
            String child1 = "child2";
            List<String> childs = Arrays.asList(child1, child2);

            spyBuilder.appendChild(fieldName, childs);

            verify(spyBuilder, times(1)).appendValue(child1);
            verify(spyBuilder, times(1)).appendValue(child2);
            verify(spyBuilder, times(1)).appendField(fieldName);
            verify(spyBuilder, times(1)).incrementIndentation();
            verify(spyBuilder, times(1)).decrementIndentation();
        }

        public class ShouldCallAppendValueWithNullWhen {

            @Test
            public void ListOfStringIsNull() {
                String fieldName = "field";
                List<String> childs = null;
                spyBuilder.appendChild(fieldName, childs);
                verify(spyBuilder, times(1)).appendField(fieldName);
                verify(spyBuilder, times(1)).appendValue(null);
                verify(spyBuilder, times(1)).incrementIndentation();
                verify(spyBuilder, times(1)).decrementIndentation();
            }

            @Test
            public void ListOfStringIsEmpty() {
                String fieldName = "field";
                List<String> childs = Collections.emptyList();
                spyBuilder.appendChild(fieldName, childs);
                verify(spyBuilder, times(1)).appendField(fieldName);
                verify(spyBuilder, times(1)).appendValue(null);
                verify(spyBuilder, times(1)).incrementIndentation();
                verify(spyBuilder, times(1)).decrementIndentation();
            }
        }
    }

    public class AppendChildObject {

        private IndentingToStringBuilder spyBuilder;

        @Before
        public void setUp() {
            spyBuilder = spy(builder);
        }

        public class ShouldCallChildObjectOfToStringWhen {
            private String fieldName = "field";

            @Test
            public void SingleObjectIsPassed() {
                DummyToStringable childObject = new DummyToStringable();

                spyBuilder.appendChildObject(fieldName, childObject);

                assertCommonsCalls();
                childObject.checkToStringCalled();
            }

            @Test
            public void ListOfObjectIsPassed() {
                String fieldName = "field";
                DummyToStringable childObject1 = new DummyToStringable();
                DummyToStringable childObject2 = new DummyToStringable();
                List<DummyToStringable> childObjects = Arrays.asList(childObject1, childObject2);

                spyBuilder.appendChildObject(fieldName, childObjects);

                assertCommonsCalls();
                childObject1.checkToStringCalled();
                childObject2.checkToStringCalled();
            }

            public void assertCommonsCalls() {
                verify(spyBuilder, times(1)).appendField(fieldName);
                verify(spyBuilder, times(0)).appendValue(null);
                verify(spyBuilder, times(1)).incrementIndentation();
                verify(spyBuilder, times(1)).decrementIndentation();

            }

        }

        public class ShouldCallAppendValueWithValueNullWhen {

            @Test
            public void ChildObjectIsNull() {
                String fieldName = "field";
                ToStringable childObject = null;
                spyBuilder.appendChildObject(fieldName, childObject);
                verify(spyBuilder, times(1)).appendField(fieldName);
                verify(spyBuilder, times(1)).appendValue(null);
                verify(spyBuilder, times(1)).incrementIndentation();
                verify(spyBuilder, times(1)).decrementIndentation();
            }

            @Test
            public void ChildObjectsIsNull() {
                String fieldName = "field";
                List<ToStringable> childObjects = null;
                spyBuilder.appendChildObject(fieldName, childObjects);
                verify(spyBuilder, times(1)).appendField(fieldName);
                verify(spyBuilder, times(1)).appendValue(null);
                verify(spyBuilder, times(1)).incrementIndentation();
                verify(spyBuilder, times(1)).decrementIndentation();
            }

            @Test
            public void ChildObjectsIsEmpty() {
                String fieldName = "field";
                List<ToStringable> childObjects = Collections.emptyList();
                spyBuilder.appendChildObject(fieldName, childObjects);
                verify(spyBuilder, times(1)).appendField(fieldName);
                verify(spyBuilder, times(1)).appendValue(null);
                verify(spyBuilder, times(1)).incrementIndentation();
                verify(spyBuilder, times(1)).decrementIndentation();
            }
        }
    }

    private class DummyToStringable implements ToStringable {

        private boolean isToStringCalled = false;

        @Override
        public void toString(IndentingToStringBuilder builder) {
            isToStringCalled = true;
        }

        public void checkToStringCalled() {
            assertTrue(isToStringCalled);
        }
    }
}