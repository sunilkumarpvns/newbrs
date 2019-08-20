package com.elitecore.netvertex.service.offlinernc.util;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

import java.math.BigDecimal;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import de.bechte.junit.runners.context.HierarchicalContextRunner;

@RunWith(HierarchicalContextRunner.class)
public class BigDecimalFormatterTest {
	
	private BigDecimalFormatter formatter;

	public class RoundingModeTruncate {
		
		@Before
		public void setUp() {
			formatter = new BigDecimalFormatter(3, RoundingModeTypes.TRUNCATE);
		}
		
		@Test
		public void scaling_AddsTrailingZerosGivenScaleMoreThanDecimalPlaces() {
			assertThat(formatter.scale(new BigDecimal(1500.0)), is(equalTo(new BigDecimal("1500.000"))));
		}
		
		@Test
		public void formatting_AddsTrailingZerosGivenScaleMoreThanDecimalPlaces() {
			assertThat(formatter.format(BigDecimal.valueOf(1500.0)), is("1500.000"));
		}
		
		@Test
		public void scaling_TruncatesExtraneousDecimalPlacesWithoutRounding() {
			assertThat(formatter.scale(new BigDecimal(1500.8965)), is(equalTo(BigDecimal.valueOf(1500.896))));
		}
		
		@Test
		public void formatting_TruncatesExtraneousDecimalPlacesWithoutRounding() {
			assertThat(formatter.format(BigDecimal.valueOf(1500.8965)), is("1500.896"));
		}
		
	}
	
	public class RoundingModeUpper {
		
		@Before
		public void setUp() {
			formatter = new BigDecimalFormatter(3, RoundingModeTypes.UPPER);
		}
		
		@Test
		public void scaling_AddsTrailingZerosGivenScaleMoreThanDecimalPlaces() {
			assertThat(formatter.scale(new BigDecimal(1500.0)), is(equalTo(new BigDecimal("1500.000"))));
		}
		
		@Test
		public void formatting_AddsTrailingZerosGivenScaleMoreThanDecimalPlaces() {
			assertThat(formatter.format(new BigDecimal(1500.0)), is(equalTo("1500.000")));
		}
		
		public class ScalingTruncatesExtraneousDecimalDigitsAndSetsTheLastDigit {
			
			public class AsItIs {
				
				@Test
				public void givenFirstDigitExceedingTheScaleIsLessThanFive() {
					assertThat(formatter.scale(new BigDecimal("0.0054")), is(equalTo(new BigDecimal("0.005"))));
				}
				
				@Test
				public void givenFirstDigitExceedingTheScaleIsFiveAndLastDigitIsEven() {
					assertThat(formatter.scale(new BigDecimal("1500.8965")), is(equalTo(new BigDecimal("1500.896"))));
					assertThat(formatter.scale(new BigDecimal("0.0045")), is(equalTo(new BigDecimal("0.004"))));
				}
				
			}

			public class NextDigit {
				
				@Test
				public void givenFirstDigitExceedingTheScaleIsGreaterThanFive() {
					assertThat(formatter.scale(new BigDecimal("1500.897756")), is(equalTo(new BigDecimal("1500.898"))));
					assertThat(formatter.scale(new BigDecimal("215.76982")), is(equalTo(new BigDecimal("215.770"))));
				}
				
				@Test
				public void givenFirstDigitExceedingTheScaleIsFiveAndLastDigitIsOdd() {
					assertThat(formatter.scale(new BigDecimal("1500.8975")), is(equalTo(new BigDecimal("1500.898"))));
				}
			}
		}
		
		
		public class FormatReturnsStringAfterScaling {

			@Test
			public void AfterScalingDecimalValue() {
				assertThat(formatter.format(new BigDecimal("0.00547")), is(equalTo("0.005")));
				assertThat(formatter.format(new BigDecimal("156789.123456")), is("156789.123"));
				assertThat(formatter.format(new BigDecimal(12.5687)), is("12.569"));
				assertThat(formatter.format(new BigDecimal("1500.8975")), is("1500.898"));
				assertThat(formatter.format(new BigDecimal("1500.8965")), is("1500.896"));
				assertThat(formatter.format(new BigDecimal("0.0045")), is("0.004"));
			}
		}
	}
}
